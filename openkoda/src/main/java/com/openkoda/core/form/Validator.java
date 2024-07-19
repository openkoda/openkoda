package com.openkoda.core.form;

import java.util.function.Function;

import static org.apache.commons.lang.StringUtils.isBlank;

public class Validator {
    public static Function<String,String> notBlank(){
        return v -> isBlank(v) ? "not.empty" : null;
    }
    public static Function<String,String> notCamelCase(){
        return v -> isBlank(v) || !v.matches("([a-z]+[a-zA-Z0-9]*)+") ? "not.matching.camelCase" : null;
    }
}
