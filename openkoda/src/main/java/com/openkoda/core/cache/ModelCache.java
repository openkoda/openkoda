package com.openkoda.core.cache;

import java.util.HashMap;
import java.util.Map;

public class ModelCache {
    private Map<String, Object> model = new HashMap<>();
    
    public Map<String, Object> getModel() {
        return model;
    }
    
    public void setModel(Map<String, Object> model) {
        this.model = model;
    }
}