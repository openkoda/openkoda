package com.openkoda.core.helper;

import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Component("resources")
public class ResourcesHelper {

    public static String getResourceAsStringOrEmpty(String path){
        try {
            return IOUtils.toString(ResourcesHelper.class.getResourceAsStream(path), StandardCharsets.UTF_8.name());
        } catch (IOException e) {
            return "";
        }
    }
}
