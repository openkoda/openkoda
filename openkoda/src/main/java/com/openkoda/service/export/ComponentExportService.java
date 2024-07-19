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

import com.openkoda.core.flow.LoggingComponent;
import com.openkoda.core.service.system.DatabaseValidationService;
import com.openkoda.model.DynamicPrivilege;
import com.openkoda.model.PrivilegeBase;
import com.openkoda.model.common.ComponentEntity;
import com.openkoda.model.component.Form;
import com.openkoda.service.export.converter.EntityToYamlConverterFactory;
import com.openkoda.service.export.util.ZipUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.zip.ZipOutputStream;

import static com.openkoda.service.export.FolderPathConstants.*;

@Service
public class ComponentExportService implements LoggingComponent {

    @Autowired
    private EntityToYamlConverterFactory entityToYamlConverterFactory;

    @Autowired
    ZipUtils zipUtils;

    @Autowired
    DatabaseValidationService databaseValidationService;

    @Value("${components.export.syncWithFilesystem:false}")
    private boolean syncWithFilesystem;

    public ByteArrayOutputStream exportToZip(List<?> entities){
        debug("[exportEntityList]");

        ByteArrayOutputStream zipByteArrayOutputStream = new ByteArrayOutputStream();
        List dependencies = new ArrayList<>();
        Set<String> zipEntries = new HashSet<>();
        try (ZipOutputStream zipOut = new ZipOutputStream(zipByteArrayOutputStream)) {
            for (Object entity : entities) {
                debug("[exportToZip] Adding {}", entity.toString());
                entityToYamlConverterFactory.exportToZip(entity, zipOut, zipEntries);
                if(entity instanceof Form) {
                    Form entityForm = (Form)entity;
                    PrivilegeBase priv = entityForm.getReadPrivilege();
                    if(priv instanceof DynamicPrivilege) {
                        dependencies.add(priv);
                    }
                }
            }
            
            List<String> dbScriptLines = null;
            if (dependencies != null) {
                addEntityDependencies(zipOut, dependencies, dbScriptLines = new ArrayList<>(), zipEntries);
            }
            
            additionalExportFiles(zipOut, dbScriptLines);
            debug("All YAML files added to ZIP successfully.");
        } catch (IOException e) {
            error("[exportEntityList]", e);
        } catch (Exception ee) {
            error("[exportEntityList]", ee);
            throw ee;
        }

        return zipByteArrayOutputStream;
    }
    private void addEntityDependencies(ZipOutputStream zipOut, List dependencies, List<String> dbUpgradeEntries, Set<String> zipEntries) {
        // TODO Auto-generated method stub
        debug("[addEntityDependencies] Adding entity dependencies {}", dependencies);
        dependencies.stream().distinct().forEach( d -> {
            entityToYamlConverterFactory.exportToZip(d, zipOut, dbUpgradeEntries, zipEntries);
        }); 
    }
    public List<ComponentEntity> exportToFileIfRequired(List<ComponentEntity> entities){
        if(syncWithFilesystem && entities != null){
            return entities.stream().map(entityToYamlConverterFactory::exportToFile).toList();
        }
        return null;
    }
    public ComponentEntity exportToFileIfRequired(ComponentEntity entity){
        if(syncWithFilesystem){
            entityToYamlConverterFactory.exportToFile(entity);
        }
        return entity;
    }
    public List<ComponentEntity> removeExportedFilesIfRequired(List<ComponentEntity> entities){
        if(syncWithFilesystem && entities != null){
            return entities.stream().map(entityToYamlConverterFactory::removeExportedFiles).toList();
        }
        return null;
    }
    public ComponentEntity removeExportedFilesIfRequired(ComponentEntity entity){
        if(syncWithFilesystem){
            entityToYamlConverterFactory.removeExportedFiles(entity);
        }
        return entity;
    }
    private void additionalExportFiles(ZipOutputStream zos, List<String> dbScriptLines) {
        debug("[additionalExportFiles]");
        try {
            URL additionalFilesFolder = getClass().getClassLoader().getResource(COMPONENTS_ADDITIONAL_FILES_);
            if (!additionalFilesFolder.toString().startsWith("jar:file:")) {
                try (DirectoryStream<Path> stream = Files
                        .newDirectoryStream(Paths.get(additionalFilesFolder.toURI()))) {
                    for (Path path : stream) {
                        if (!Files.isDirectory(path)) {
                            String fileName = path.getFileName().toString();
                            zipUtils.addURLFileToZip(path.toUri().toURL(),
                                    (fileName.contains("application") ? EXPORT_PATH : "") + fileName, zos);
                        }
                    }
                }
            } else {
                List<String> nestedResources = jarResources(additionalFilesFolder);
                for (String string : nestedResources) {
                    URL nestedUrl = getClass().getClassLoader().getResource(string);
                    String fileName = new File(string).getName();
                    zipUtils.addURLFileToZip(nestedUrl,
                            (fileName.contains("application") ? EXPORT_PATH : "") + fileName, zos);
                }
            }
//          add migration script if exists
            StringBuilder dbUpdateScriptContent = new StringBuilder(databaseValidationService.getUpdateScript(false));
            if(StringUtils.isNotEmpty(dbUpdateScriptContent)) {
//                TODO: update sql script name generation so it contains version info
                if(dbScriptLines != null) {
                    dbScriptLines.forEach( l -> dbUpdateScriptContent.append(System.getProperty("line.separator")).append(l));
                }
                zipUtils.addToZipFile(dbUpdateScriptContent.toString(), EXPORT_MIGRATION_PATH_ + "upgrade.sql", zos);
            }
        } catch (IOException | URISyntaxException e) {
            error("[additionalExportFiles]", e);
        }
    }

    /**
     * lists a content of a folder within a JAR file
     * 
     * @param jarFile
     * @return list of files/direcoties within folder in a jar
     */
    // TODO : list it recirsively
    private List<String> jarResources(URL jarFile) {
        // at this point argument URL is useful but compatible when trying to list
        // actual nested folder within a JAR
        String[] jarPathParts = jarFile.getPath().replace("file:", "").split("!", 2);

        final String jarFilePath = jarPathParts[0];
        // Jar entries do not start with / so we need to drop it from the original URL
        final String subfolder = jarPathParts[1].replaceFirst("/", "").replace("!/", "/");
        List<String> folderContent = new ArrayList<>();
        try (JarFile jar = new JarFile(new File(jarFilePath))) {
            Enumeration<JarEntry> e = jar.entries();
            while (e.hasMoreElements()) {
                JarEntry jarEntry = e.nextElement();
                // match only file inside the folder but not the folder itself
                if (!jarEntry.getName().equals(subfolder) && jarEntry.getName().startsWith(subfolder)) {
                    // folderContent.add(jarEntry)
                    folderContent.add(jarEntry.getName());
                }
            }
        } catch (IOException e) {
            error("[jarResources] error while listing content of jar [{}]", jarFile.toString());
        }

        return folderContent;
    }

}
