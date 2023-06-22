package com.openkoda.service.autocomplete;

import com.openkoda.controller.ComponentProvider;
import com.openkoda.uicomponent.live.LiveComponentProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.HashMap;
import java.util.Map;

import static java.util.Arrays.stream;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toMap;

@Service
@PropertySource("classpath:autocomplete.properties")
public class AutocompleteService extends ComponentProvider {

    @Autowired
    private Environment env;

    public Map<String, String> getSuggestionsAndDocumentation(){
        return stream(LiveComponentProvider.class.getDeclaredFields())
           .map(f -> getSuggestionsAndDocumentation(f.getType().getName(), f.getName()))
           .flatMap(map -> map.entrySet().stream())
           .collect(toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    private Map<String,String> getSuggestionsAndDocumentation(String className, String variableName){
        try {
            return stream(Class.forName(className).getDeclaredMethods())
                    .collect(toMap(m -> getSuggestion(variableName, m), m -> getDocumentation(variableName, m)));
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return new HashMap<>();
    }
    private String getSuggestion(String variableName, Method method){
        return variableName + "." + method.getName() + "(" + getParameters(method) + ")";
    }
    private String getDocumentation(String variableName, Method method){
        String fullKey = getDocFullKey(variableName, method);
        String content = env.getProperty(fullKey);
        if(content == null){
            String shortKey = getDocShortKey(variableName, method);
            content = env.getProperty(shortKey);
        }
        return content != null ? content : fullKey;
    }
    private String getDocShortKey(String variableName, Method method) {
        return variableName + "." + method.getName();
    }
    private String getParameterTypes(Method method) {
        return stream(method.getParameterTypes())
                .map(c -> c.getName().replace(c.getPackageName() + ".", ""))
                .collect(joining(","));
    }
    private String getDocFullKey(String variableName, Method method) {
        return variableName + "." + method.getName() + "(" + getParameterTypes(method) + ")";
    }
    private String getParameters(Method method){
        return stream(method.getParameters())
                .map(Parameter::getName)
                .collect(joining(","));
    }
}
