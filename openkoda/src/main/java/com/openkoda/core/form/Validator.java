package com.openkoda.core.form;

import java.util.function.Function;

import static org.apache.commons.lang.StringUtils.isBlank;

public class Validator {
    public static Function<String,String> notBlank(){
        return v -> isBlank(v) ? "not.empty" : null;
    }
}
