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
import com.openkoda.core.job.SearchIndexUpdaterJob;
import com.openkoda.model.FrontendResource;
import com.openkoda.service.export.converter.ResourceLoadingException;
import com.openkoda.service.export.converter.YamlToEntityConverterFactory;
import jakarta.inject.Inject;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.yaml.snakeyaml.Yaml;

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
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import static com.openkoda.service.export.FolderPathConstants.*;

@Service
public class YamlImportService implements LoggingComponent {

    @Autowired
    private YamlToEntityConverterFactory yamlToEntityConverterFactory;

    @Inject
    SearchIndexUpdaterJob searchIndexUpdaterJob;

    public void loadResourcesFromFiles() {
        debug("[loadResourcesFromFiles]");

        Set<String> yamlFiles = getAllYamlFiles();

        for (String yamlFile : yamlFiles) {
            loadYamlFile(yamlFile);
        }

        searchIndexUpdaterJob.updateSearchIndexes();
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

}
