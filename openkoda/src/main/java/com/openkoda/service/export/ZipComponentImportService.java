package com.openkoda.service.export;

import com.openkoda.App;
import com.openkoda.core.form.FrontendMappingDefinition;
import com.openkoda.core.job.SearchIndexUpdaterJob;
import com.openkoda.core.multitenancy.MultitenancyService;
import com.openkoda.model.OpenkodaModule;
import com.openkoda.model.component.Form;
import com.openkoda.model.component.event.EventListenerEntry;
import com.openkoda.service.dynamicentity.DynamicEntityRegistrationService;
import com.openkoda.service.export.dto.ComponentDto;
import com.openkoda.service.export.dto.FormConversionDto;
import jakarta.inject.Inject;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.yaml.snakeyaml.Yaml;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static com.openkoda.service.export.FolderPathConstants.*;
import static java.util.stream.Collectors.toMap;
import static org.springframework.transaction.annotation.Propagation.REQUIRES_NEW;

@Service
public class ZipComponentImportService extends YamlComponentImportService {

    @Inject
    SearchIndexUpdaterJob searchIndexUpdaterJob;
    @Inject
    MultitenancyService multitenancyService;
    public String loadResourcesFromZip(MultipartFile zipFile, boolean delete) {
        debug("[loadResourcesFromZip] {}", zipFile.getName());
        Map<String, Object> configsFromZip = new HashMap<>();
        Map <String, String> componentResourcesFromZip = new HashMap<>();
        StringBuilder importNote = new StringBuilder(String.format("IMPORT %s \r\n", zipFile.getName()));
        try {
            ZipInputStream zis = new ZipInputStream(new ByteArrayInputStream(zipFile.getBytes()));
            ZipEntry entry;
            int bufferSize = 1024;
            byte[] buffer = new byte[bufferSize];
            while ((entry = zis.getNextEntry()) != null) {
                if(!entry.isDirectory()) {
                    StringBuilder content = new StringBuilder();
                    int read;
                    while ((read = zis.read(buffer, 0, bufferSize)) >= 0) {
                        content.append(new String(buffer, 0, read));
                    }
                    if (entry.getName().endsWith(".yaml")) {
                        configsFromZip.put(entry.getName(), new Yaml().load(content.toString()));
                    } else if (entry.getName().contains(EXPORT_CODE_PATH_) || entry.getName().contains(EXPORT_RESOURCES_PATH_)) {
                        componentResourcesFromZip.put(entry.getName().split(EXPORT_PATH)[1], content.toString());
                    }
                }
            }
            if(delete) {
                List<String> discoveredModules = configsFromZip.values().stream().map(o -> ((ComponentDto) o).getModule()).distinct().toList();
                unregisterComponents(discoveredModules, importNote);
                deleteComponents(discoveredModules, importNote);
            }

            StringBuilder validationLog = new StringBuilder("VALIDATION\r\n");
            StringBuilder updateQuery = new StringBuilder();
            Map<String, FrontendMappingDefinition> frontendMappingDefinitions = getFrontendMappingDefinitions(configsFromZip, componentResourcesFromZip);
            Map<String,String> formTableNames = getFormTableNames(configsFromZip);
            if (!frontendMappingDefinitions.entrySet().stream()
                    .allMatch(mappingDefinition -> services.databaseValidation.validateColumnTypes(formTableNames.get(mappingDefinition.getKey()), mappingDefinition.getValue().getFieldNameDbTypeMap(), validationLog, updateQuery))) {
                error("[loadResourcesFromZip] Validation error");
                return importNote.append(validationLog).toString();
            }

            List<Object> processedComponents = processComponentsFromZip(configsFromZip, componentResourcesFromZip, importNote);
            try {
                searchIndexUpdaterJob.updateSearchIndexes();
            } catch (Exception e) {
                warn("Error updating search engine during the import");
            }

//            assumption: that if no update query generated then we don't need to reload entities and restart the app
            if(!updateQuery.isEmpty()) {
                executeDatabaseUpdate(updateQuery.toString());
//            load entity classes and restart spring context
                debug("[loadResourcesFromZip] Context restart required");
                List<Form> createdForms = processedComponents.stream().filter(o -> o instanceof Form).map(o -> (Form) o).toList();

                DynamicEntityRegistrationService.generateDynamicEntityDescriptors(createdForms, frontendMappingDefinitions, System.currentTimeMillis());
                importNote.append("RESTARTING ...");
                App.restart();
            } else {
//                no conflicts requiring db changes, safe to register forms
                services.form.loadAllFormsFromDb(true);
            }

        } catch (IOException e) {
            error("[loadResourcesFromZip]", e);
            importNote.append("ERROR ");
            importNote.append(e.getMessage());
            return importNote.toString();
        }
        return importNote.toString();
    }

    private Map<String, String> getFormTableNames(Map<String, Object> configsFromZip) {
        return configsFromZip.values().stream()
                .filter(o -> o instanceof FormConversionDto)
                .map(o -> (FormConversionDto) o)
                .collect(toMap(FormConversionDto::getName, FormConversionDto::getTableName));
    }

    private Map<String, FrontendMappingDefinition> getFrontendMappingDefinitions(Map<String, Object> configsFromZip, Map<String, String> componentResourcesFromZip) {
        return configsFromZip.values().stream()
                .filter(o -> o instanceof FormConversionDto)
                .map(o -> (FormConversionDto) o)
                .collect(toMap(FormConversionDto::getName,
                        dto -> services.form.getFrontendMappingDefinition(dto.getName(), dto.getReadPrivilege(), dto.getWritePrivilege(), componentResourcesFromZip.get(dto.getCode()))));
    }


    private void deleteComponents(List<String> discoveredModules, StringBuilder importNote) {
        debug("[deleteComponents]");
        for (String module : discoveredModules) {
            OpenkodaModule openkodaModule = repositories.unsecure.openkodaModule.findByName(module);
            if (openkodaModule != null) {
                importNote.append(String.format("DELETE existing components for module %s \r\n", module));
                repositories.unsecure.controllerEndpoint.deleteByModule(openkodaModule);
                repositories.unsecure.frontendResource.deleteByModule(openkodaModule);
                repositories.unsecure.form.deleteByModule(openkodaModule);
                repositories.unsecure.eventListener.deleteByModule(openkodaModule);
                repositories.unsecure.scheduler.deleteByModule(openkodaModule);
                repositories.unsecure.serverJs.deleteByModule(openkodaModule);
            } else {
                openkodaModule = new OpenkodaModule(module);
                importNote.append(String.format("CREATE module %s \r\n", module));
            }
            repositories.unsecure.openkodaModule.save(openkodaModule);
        }
    }

    private void unregisterComponents(List<String> discoveredModules, StringBuilder importNote) {
        debug("[unregisterComponents]");
        for (String module : discoveredModules) {
            OpenkodaModule openkodaModule = repositories.unsecure.openkodaModule.findByName(module);
            if (openkodaModule != null) {
                importNote.append(String.format("UNREGISTER components for module %s \r\n", module));
                repositories.unsecure.form.findByModule(openkodaModule).forEach(componentEntity -> services.form.removeClusterAware(componentEntity.getId()));
                repositories.unsecure.eventListener.findByModule(openkodaModule).forEach(componentEntity -> services.eventListener.unregisterEventListenerClusterAware((EventListenerEntry) componentEntity));
                repositories.unsecure.scheduler.findByModule(openkodaModule).forEach(componentEntity -> services.scheduler.removeClusterAware(componentEntity.getId()));
            }
        }
    }

    @Transactional(propagation = REQUIRES_NEW)
    public void executeDatabaseUpdate(String updateQuery) {
        try {
            repositories.unsecure.nativeQueries.runUpdateQuery(updateQuery);
            if(MultitenancyService.isMultitenancy()) {
                multitenancyService.runEntityManagerForAllTenantsInTransaction(1000, (em, orgId) -> {
                    em.createNativeQuery(updateQuery).executeUpdate();
                    return true;
                });
            }
        } catch (SQLException e) {
            error("[executeDatabaseUpdate]", e);
        }
    }
    private List<Object> processComponentsFromZip(Map<String, Object> configsFromZip, Map<String, String> componentResourcesFromZip, StringBuilder importNote) {
        return configsFromZip.entrySet().stream().map(entry -> {
            importNote.append(String.format("PROCESS component %s from file %s\r\n", entry.getValue().getClass(), entry.getKey()));
            return yamlToEntityConverterFactory.processYamlDto( entry.getValue(), entry.getKey(), componentResourcesFromZip);
        }).collect(Collectors.toList());
    }

}
