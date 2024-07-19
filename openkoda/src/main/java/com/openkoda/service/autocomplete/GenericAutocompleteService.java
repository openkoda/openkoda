package com.openkoda.service.autocomplete;

import com.openkoda.controller.ComponentProvider;
import com.openkoda.core.helper.ReflectionHelper;
import com.openkoda.uicomponent.annotation.Autocomplete;
import org.springframework.beans.factory.annotation.Autowired;

import java.lang.reflect.Method;
import java.util.Map;

import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toMap;

class GenericAutocompleteService extends ComponentProvider {
    @Autowired
    private ReflectionHelper helper;

    Map<String,String> getSuggestionsAndDocumentation(Method[] methods, String variableName){
        return stream(methods)
                .collect(toMap(m -> getSuggestion(variableName, m), this::getDocumentation, (m1, m2) -> !m1.equals("") ? m1 : m2 ));
    }
    String getSuggestion(String variableName, Method method){
        return (variableName != null ? variableName + "." : "") + helper.getNameWithParamNames(method);
    }
    String getDocumentation(Method method){
        return method.getAnnotation(Autocomplete.class).doc();
    }
    Method[] getExposedMethods(String className){
        return stream(helper.getDeclaredMethods(className))
                .filter(f -> f.isAnnotationPresent(Autocomplete.class))
                .toArray(Method[]::new);
    }
}
