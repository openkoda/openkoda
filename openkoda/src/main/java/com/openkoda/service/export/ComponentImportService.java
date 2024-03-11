/*
MIT License

Copyright (c) 2016-2023, Openkoda CDX Sp. z o.o. Sp. K. <openkoda.com>

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
documentation files (the "Software"), to deal in the Software without restriction, including without limitation
the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software,
and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice
shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR
A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS
OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR
IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*/

package com.openkoda.service.export;

import com.openkoda.App;
import com.openkoda.controller.ComponentProvider;
import com.openkoda.core.form.FrontendMappingDefinition;
import com.openkoda.core.job.SearchIndexUpdaterJob;
import com.openkoda.core.multitenancy.QueryExecutor;
import com.openkoda.model.OpenkodaModule;
import com.openkoda.model.component.Form;
import com.openkoda.model.component.FrontendResource;
import com.openkoda.model.component.event.EventListenerEntry;
import com.openkoda.service.dynamicentity.DynamicEntityRegistrationService;
import com.openkoda.service.export.converter.ResourceLoadingException;
import com.openkoda.service.export.converter.YamlToEntityConverterFactory;
import com.openkoda.service.export.dto.ComponentDto;
import com.openkoda.service.export.dto.FormConversionDto;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.yaml.snakeyaml.Yaml;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.JarURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static com.openkoda.service.export.FolderPathConstants.*;
import static java.util.stream.Collectors.toMap;

@Service
public class ComponentImportService extends ComponentProvider {

    @Autowired
    private YamlToEntityConverterFactory yamlToEntityConverterFactory;

    @Inject
    SearchIndexUpdaterJob searchIndexUpdaterJob;
    @Inject
    QueryExecutor queryExecutor;

    @Autowired
    private EntityManager entityManager;

    public void loadResourcesFromFiles() {
        debug("[loadResourcesFromFiles]");

        Set<String> yamlFiles = getAllYamlFiles();

        for (String yamlFile : yamlFiles) {
            loadYamlFile(yamlFile);
        }
        if(new ClassPathResource("/migration/upgrade.sql").exists()) {
            queryExecutor.runQueryFromResourceInTransaction("/migration/upgrade.sql");
        }
        searchIndexUpdaterJob.updateSearchIndexes();
    }

    public String loadResourcesFromZip(MultipartFile file) {
        debug("[loadResourcesFromZip] {}", file.getName());
        Map <String, Object> configsFromZip = new HashMap<>();
        Map <String, String> componentResourcesFromZip = new HashMap<>();
        StringBuilder importNote = new StringBuilder(String.format("IMPORT %s \r\n", file.getName()));
        try {
            ZipInputStream zis = new ZipInputStream(new ByteArrayInputStream(file.getBytes()));
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
            List<String> discoveredModules = configsFromZip.values().stream().map(o -> ((ComponentDto) o).getModule()).distinct().toList();
            unregisterComponents(discoveredModules, importNote);
            deleteComponents(discoveredModules, importNote);

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
            searchIndexUpdaterJob.updateSearchIndexes();
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

    public Object loadResourceFromFile(String basePath, FrontendResource.AccessLevel accessLevel, Long organizationId, String name) {
        debug("[loadResourceFromFile] {} {} {}", name, accessLevel, organizationId);

        String aLevel = accessLevel != null ? accessLevel.getPath() : "";
        String yamlFile = COMPONENTS_ + basePath + aLevel + SUBDIR_ORGANIZATION_PREFIX + organizationId + "/" + name + ".yaml";
        Resource resource = new ClassPathResource(yamlFile);
        if (resource.exists()) {
//            resource exists in /components/basePath/accessLevel/org_orgId/ directory
//            let's load yaml configuration
            return loadYamlFile(yamlFile);
        } else {
//            no yaml for given organization ID try to load common organization file
            yamlFile = COMPONENTS_ + basePath + aLevel + name + ".yaml";
            resource = new ClassPathResource(yamlFile);
            if (resource.exists()) {
                return loadYamlFile(yamlFile);
            }
        }
        return null;
    }

    private Object loadYamlFile(String yamlFile) {
        InputStream inputStream = loadResource(yamlFile);
        if(inputStream == null){
            inputStream = loadResource(yamlFile);
        }
        if (inputStream != null) {
            debug("[YamlLoaderService] Processing file: " + yamlFile);
            return yamlToEntityConverterFactory.processYamlDto(new Yaml().load(inputStream), yamlFile);
        }
        return null;
    }

    private Set<String> getAllYamlFiles() {
        Set<String> yamlFiles = new HashSet<>();
        List<String> allFilePaths = new ArrayList<>(BASE_FILE_PATHS);
        List<String> subdirFilePaths = new ArrayList<>(SUBDIR_FILE_PATHS);
        for (String folderPath : allFilePaths) {
            for (String subdirPath : subdirFilePaths) {
                getYamlFilesFromDir(folderPath, subdirPath, yamlFiles);
            }
            getYamlFilesFromDir(folderPath, "", yamlFiles);
        }
        return yamlFiles;
    }

    private void getYamlFilesFromDir(String folderPath, String subdirPath, Set<String> yamlFiles) {
        try {
            Enumeration<URL> resources = getClass().getClassLoader().getResources(folderPath + subdirPath);
            while (resources.hasMoreElements()) {
                URL url = resources.nextElement();
                if (url.getProtocol().equals("jar")) {
                    getFromJar(yamlFiles, folderPath, url);
                } else if (url.getProtocol().equals("file")) {
                    getFromFile(yamlFiles, url, folderPath, subdirPath);
                }
            }
        } catch (IOException e) {
            throw new ResourceLoadingException("Error accessing resource under path: " + folderPath);
        }
    }

    private void getFromFile(Set<String> yamlFiles, URL url, String basePath, String subdirPath) {
        try {
            Path resourceFolderPath = Paths.get(url.toURI());
            try (DirectoryStream<Path> stream = Files.newDirectoryStream(resourceFolderPath)) {
                for (Path path : stream) {
                    if (Files.isDirectory(path)) {
                        try (DirectoryStream<Path> orgAssignedStream = Files.newDirectoryStream(path)) {
                            for (Path orgAssignedPath : orgAssignedStream) {
                                if(orgAssignedPath.toString().endsWith(".yaml")) {
                                    String p = orgAssignedPath.toString().replace("\\", "/");
                                    yamlFiles.add(p.substring(StringUtils.indexOf(p, basePath)));
                                }
                            }
                        }
                    } else if(path.toString().endsWith(".yaml")) {
                        yamlFiles.add(basePath + subdirPath + path.getFileName().toString());
                    }
                }
            } catch (IOException e) {
                throw new ResourceLoadingException("YAML files not found under the path: " + resourceFolderPath);
            }
        } catch (URISyntaxException e) {
            throw new ResourceLoadingException("Incorrect folder path syntax: " + e.getMessage());
        }
    }

    private void getFromJar(Set<String> yamlFiles, String folderPath, URL url) {
        try {
            URLConnection urlConnection = url.openConnection();
            if (urlConnection instanceof JarURLConnection) {
                List<String> fileNames = getMatchingJarEntries((JarURLConnection) urlConnection, folderPath);
                yamlFiles.addAll(fileNames);
            }
        } catch (IOException e) {
            throw new ResourceLoadingException("Error loading resources from path " + folderPath);
        }
    }

    private List<String> getMatchingJarEntries(JarURLConnection jarConnection, String folderPath) throws IOException {
        List<String> matchingEntries = new ArrayList<>();
        try (JarFile jarFile = jarConnection.getJarFile()) {
            Enumeration<JarEntry> entries = jarFile.entries();
            while (entries.hasMoreElements()) {
                JarEntry entry = entries.nextElement();
                String entryName = entry.getName();
                if (entryName.startsWith(folderPath) && entryName.endsWith(".yaml")) {
                    matchingEntries.add(entryName);
                }
            }
        }
        return matchingEntries;
    }

    private InputStream loadResource(String path) {
        return this.getClass().getClassLoader().getResourceAsStream(path);
    }

    private List<Object> processComponentsFromZip(Map<String, Object> configsFromZip, Map<String, String> componentResourcesFromZip, StringBuilder importNote) {
        return configsFromZip.entrySet().stream().map(entry -> {
            importNote.append(String.format("PROCESS component %s from file %s\r\n", entry.getValue().getClass(), entry.getKey()));
            return yamlToEntityConverterFactory.processYamlDto( entry.getValue(), entry.getKey(), componentResourcesFromZip);
        }).collect(Collectors.toList());
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

    private void executeDatabaseUpdate(String updateQuery) {
        try {
            repositories.unsecure.entityUnrelatedQueries.runUpdateQuery(updateQuery);
        } catch (SQLException e) {
            error("[executeDatabaseUpdate]", e);
        }
    }
}
