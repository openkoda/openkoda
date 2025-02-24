package com.openkoda.core.helper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;

import static java.lang.String.join;
import static java.util.Arrays.stream;
import static java.util.stream.Collectors.joining;

@Component
public class MethodParameterHelper {

    @Autowired
    private ReflectionHelper reflectionHelper;

    public String getParameterNames(Method method) {
        return stream(method.getParameters())
                .map(Parameter::getName)
                .collect(joining(","));
    }

    public String getParameterTypes(Method method) {
        return stream(method.getGenericParameterTypes())
                .map(reflectionHelper::getShortName)
                .collect(joining(","));
    }

    public String getParameterNamesAndTypes(Method method) {
        Parameter[] names = method.getParameters();
        Type[] types = method.getGenericParameterTypes();
        String[] namesAndTypes = new String[names.length];
        for (int i = 0; i < names.length; i++) {
            namesAndTypes[i] = reflectionHelper.getShortName(types[i]) + " " + names[i].getName();
        }
        return join(", ", namesAndTypes);
    }
}
