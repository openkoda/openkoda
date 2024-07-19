package com.openkoda.service.autocomplete;

import com.openkoda.core.form.FormFieldDefinitionBuilder;
import com.openkoda.core.form.FormFieldDefinitionBuilderStart;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class FormAutocompleteService extends GenericAutocompleteService{

    public FormAutocompleteResponse getResponse() {
        FormAutocompleteResponse response = new FormAutocompleteResponse();
        response.setBuilderStart(getBuilderStart());
        response.setBuilder(getBuilder());
        return response;
    }

    private Map<String, String> getBuilderStart(){
        return getSuggestionsAndDocumentation(getExposedMethods(FormFieldDefinitionBuilderStart.class.getName()), null);
    }

    private Map<String, String> getBuilder(){
        return getSuggestionsAndDocumentation(getExposedMethods(FormFieldDefinitionBuilder.class.getName()), null);
    }
}
