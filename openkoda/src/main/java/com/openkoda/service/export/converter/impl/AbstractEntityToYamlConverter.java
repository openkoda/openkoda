package com.openkoda.service.export.converter.impl;

import com.openkoda.core.flow.LoggingComponent;
import com.openkoda.service.export.converter.EntityToYamlConverter;
import com.openkoda.service.export.util.ZipUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;
import java.util.zip.ZipOutputStream;

import static com.openkoda.service.export.FolderPathConstants.EXPORT_PATH;
import static java.nio.file.Files.*;
import static java.nio.file.Paths.get;

public abstract class AbstractEntityToYamlConverter<T,D> implements EntityToYamlConverter<T, D>, LoggingComponent {

    @Autowired
    ZipUtils zipUtils;

    public D addToZip(T entity, ZipOutputStream zipOut, Set<String> zipEntries){
        final String pathToContentFile = getPathToContentFile(entity);
        if(pathToContentFile != null && !zipEntries.contains(pathToContentFile)) {
            String content = getContent(entity);
            if(content != null) {
                zipUtils.addToZipFile(content, pathToContentFile, zipOut);
                zipEntries.add(pathToContentFile);
            }
        }
        D dto = getConversionDto(entity);
        final String pathToComponentFile = getPathToYamlComponentFile(entity);
        if(pathToComponentFile != null && !zipEntries.contains(pathToComponentFile)) {
            zipUtils.addToZipFile(dtoToYamlString(dto), pathToComponentFile, zipOut);
            zipEntries.add(pathToComponentFile);
        }
        return dto;
    }
    @Override
    public T saveToFile(T entity) {
        if(getPathToContentFile(entity) != null) {
            saveToFile(getPathToContentFile(entity), getContent(entity));
        }
        if(getPathToYamlComponentFile(entity) != null) {
            saveToFile(getPathToYamlComponentFile(entity), dtoToYamlString(getConversionDto(entity)));
        }
        return entity;
    }

    @Override
    public T removeExportedFiles(T entity) {
        if(getPathToContentFile(entity) != null) {
            removeFileIfExists(getPathToContentFile(entity));
        }
        if(getPathToYamlComponentFile(entity) != null) {
           removeFileIfExists(getPathToYamlComponentFile(entity));
        }
        return entity;
    }

    private void saveToFile(String pathToFile, String content){
        try {
            Path fullPath = get(pathToFile);
            if(!exists(fullPath)) {
                Path folderPath = fullPath.getParent();
                if(!exists(folderPath)){
                    Files.createDirectories(folderPath);
                }
                createFile(fullPath);
            }
            if(content != null) {
                Files.write(fullPath, content.getBytes());
            }
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }
    private void removeFileIfExists(String pathToFile){
        Path fullPath = get(pathToFile);
        try {
            if(exists(fullPath)) {
                delete(fullPath);
                if(Files.list(fullPath.getParent()).toList().isEmpty()){
                    delete(fullPath.getParent());
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public String getResourcePathToContentFile(T entity){
        return getPathToContentFile(entity).replace(EXPORT_PATH, "");
    }
    public abstract String getPathToContentFile(T entity);
    public abstract String getContent(T entity);
    public abstract String getPathToYamlComponentFile(T entity);
    public abstract D getConversionDto(T entity);

    String dtoToYamlString(Object object) {
        DumperOptions options = new DumperOptions();
        options.setDefaultScalarStyle(DumperOptions.ScalarStyle.DOUBLE_QUOTED);
        options.setPrettyFlow(true);
        Yaml yaml = new Yaml(options);

        return yaml.dump(object);
    }
    String getYamlDefaultFilePath(String filePath, String entityName, Long organizationId){
        debug("[getYamlDefaultFilePath]");
        return organizationId == null ? String.format("%s%s.yaml",filePath, entityName) : String.format("%s%s_%s.yaml", filePath, entityName, organizationId);
    }
}
