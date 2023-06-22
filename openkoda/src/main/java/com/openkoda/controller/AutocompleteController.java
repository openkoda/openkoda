package com.openkoda.controller;

import com.openkoda.core.security.HasSecurityRules;
import com.openkoda.service.autocomplete.AutocompleteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;


@RestController
@RequestMapping("/autocomplete")
public class AutocompleteController implements HasSecurityRules {

    @Autowired
    private AutocompleteService autocompleteService;

    @GetMapping(value="/service-data", produces= MediaType.APPLICATION_JSON_VALUE)
    public Map<String, String> getSuggestionsAndDocumentations() throws ClassNotFoundException {
        return autocompleteService.getSuggestionsAndDocumentation();
    }


}
