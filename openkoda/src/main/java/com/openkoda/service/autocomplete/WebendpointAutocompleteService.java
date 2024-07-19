package com.openkoda.service.autocomplete;


import com.openkoda.model.component.ServerJs;
import com.openkoda.uicomponent.live.LiveComponentProvider;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toMap;

@Service
public class WebendpointAutocompleteService extends GenericAutocompleteService {


    public WebendpointAutocompleteResponse getResponse() {
        WebendpointAutocompleteResponse response = new WebendpointAutocompleteResponse();
        response.setServicesSuggestions(getSuggestionsAndDocumentation());
        response.setModelKeys(new String[]{"organizationEntityId","userEntityId"});
        response.setImportSuggestions(getImportSuggestions());
        response.setServerJsSuggestions(getServerJsSuggestions());
        return response;
    }
    private Map<String, String> getSuggestionsAndDocumentation(){
        return stream(LiveComponentProvider.class.getDeclaredFields())
                .map(f -> getSuggestionsAndDocumentation(getExposedMethods(f.getType().getName()), f.getName()))
                .flatMap(map -> map.entrySet().stream())
                .collect(toMap(Map.Entry::getKey, Map.Entry::getValue));
    }
    private String[] getImportSuggestions() {
        return repositories.secure.serverJs.findAll().stream().map(ServerJs::getName).toArray(String[]::new);
    }
    private Map<String, List<String>> getServerJsSuggestions(){
        return repositories.secure.serverJs.findAll().stream().collect(Collectors.toMap(ServerJs::getName, s -> services.jsParser.getFunctions(s.getCode())));
    }
}
