package com.openkoda.service.autocomplete;

import java.util.List;
import java.util.Map;

public class WebendpointAutocompleteResponse {

    Map<String,String> servicesSuggestions;
    String[] modelKeys;
    String[] importSuggestions;
    Map<String, List<String>> serverJsSuggestions;

    public Map<String, String> getServicesSuggestions() {
        return servicesSuggestions;
    }

    public void setServicesSuggestions(Map<String, String> servicesSuggestions) {
        this.servicesSuggestions = servicesSuggestions;
    }

    public String[] getModelKeys() {
        return modelKeys;
    }

    public void setModelKeys(String[] modelKeys) {
        this.modelKeys = modelKeys;
    }

    public String[] getImportSuggestions() {
        return importSuggestions;
    }

    public void setImportSuggestions(String[] importSuggestions) {
        this.importSuggestions = importSuggestions;
    }

    public Map<String, List<String>> getServerJsSuggestions() {
        return serverJsSuggestions;
    }

    public void setServerJsSuggestions(Map<String, List<String>> serverJsSuggestions) {
        this.serverJsSuggestions = serverJsSuggestions;
    }
}
