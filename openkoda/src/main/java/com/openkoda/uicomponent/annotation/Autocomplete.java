package com.openkoda.uicomponent.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface Autocomplete {

    String doc() default "";
}
