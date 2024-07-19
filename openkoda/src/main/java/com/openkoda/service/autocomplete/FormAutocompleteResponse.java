package com.openkoda.service.autocomplete;

import java.util.Map;

public class FormAutocompleteResponse extends GenericAutocompleteService {

    private Map<String,String> builderStart;
    private Map<String,String> builder;

    public Map<String, String> getBuilderStart() {
        return builderStart;
    }

    public void setBuilderStart(Map<String, String> builderStart) {
        this.builderStart = builderStart;
    }

    public Map<String, String> getBuilder() {
        return builder;
    }

    public void setBuilder(Map<String, String> builder) {
        this.builder = builder;
    }
}
