package com.openkoda.service.openai;

import com.openkoda.controller.ComponentProvider;
import com.openkoda.core.helper.ReflectionHelper;
import com.openkoda.model.common.OpenkodaEntity;
import com.openkoda.model.common.SearchableEntity;
import com.openkoda.repository.SearchableRepositories;
import com.openkoda.service.autocomplete.WebendpointAutocompleteService;
import com.openkoda.uicomponent.annotation.AiHint;
import com.openkoda.uicomponent.annotation.Autocomplete;
import com.openkoda.uicomponent.live.LiveComponentProvider;
import jakarta.inject.Inject;
import jakarta.persistence.Table;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.Formula;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static com.openkoda.core.helper.NameHelper.toColumnName;
import static java.util.Arrays.stream;
import static java.util.stream.Collectors.joining;
import static org.apache.commons.lang3.StringUtils.capitalize;

@Service
public class ChatGPTPromptService extends ComponentProvider {

    @Value("${chat.gpt.promptFileName.excludedFields:}")
    String[] excludedFields;
    private static final String templatePath = "/gpt/";

    private static final String defaultPrompt = "promptFileName.txt";

    @Inject
    ReflectionHelper reflectionHelper;
    @Inject
    WebendpointAutocompleteService webendpointAutocompleteService;

    public String getPromptFromFileForEntities(String promptFileName, String... entityKeys) {
        debug("[getWebEndpointPrompt] {}", entityKeys);
        if(entityKeys == null) {
            entityKeys = new String[0];
        }
        String promptTemplate="";
        String promptPath = templatePath + StringUtils.defaultString(promptFileName, defaultPrompt);
        try (InputStream resourceIO = this.getClass().getResourceAsStream(promptPath)) {
            debug("[getWebEndpointPrompt] read promptFileName {}", promptPath);
            if (resourceIO != null) {
                promptTemplate = IOUtils.toString(resourceIO);
            }
        } catch (IOException e) {
            error("Could not load ChatGPT promptFileName template", e);
        }

        return promptTemplate.replace("${CONTEXT_SPECIFICATION}", getContextSpecification())
                .replace("${DATA_MODEL}", getDataModels(entityKeys));

    }
    private String getContextSpecification(){
        debug("[getContextSpecification]");
        return stream(LiveComponentProvider.class.getDeclaredFields())
                .map(f -> getSuggestions(getExposedMethods(f.getType().getName()), f.getName()))
                .flatMap(Collection::stream)
                .sorted()
                .collect(joining("\n"));
    }
    private Method[] getExposedMethods(String className){
        return stream(reflectionHelper.getDeclaredMethods(className))
                .filter(f -> f.isAnnotationPresent(Autocomplete.class))
                .toArray(Method[]::new);
    }
    private List<String> getSuggestions(Method[] methods, String variableName){
        debug("[getSuggestions-1]");
        return stream(methods)
                .map(m->"context.services." + getSuggestion(variableName,m))
                .collect(Collectors.toList());
    }
    private String getSuggestion(String variableName, Method method){
        debug("[getSuggestions-2]");
        return (variableName != null ? variableName + "." : "") + reflectionHelper.getNameWithParamNamesAndTypes(method);
    }
    public String getDataModels(String ... entityKeys){
        debug("[getDataModels] {}", entityKeys);
        return Arrays.stream(entityKeys).map(this::getDataModel).collect(joining("\n"));
    }
    public String getDataSchemas(String ... entityKeys){
        debug("[getDataSchemas] {}", entityKeys);
        return Arrays.stream(entityKeys).map(this::getDataSchema).collect(joining("\n"));
    }
    private String getDataModel(String entityKey){
        debug("[getDataModel] {}", entityKey);
        Class<SearchableEntity> entityClass = SearchableRepositories.getSearchableRepositoryEntityClass(entityKey);
        return getPrefix(entityClass) + ":\n" +
                getDetectedMethodEntries(entityClass) + "\n" +
                getManualMethodEntries(entityClass) + "\n" +
                getAllHints(entityClass) + "\n" +
                getRelated(entityClass) + "\n";
    }
    private String getDataSchema(String entityKey){
        debug("[getDataSchema] {}", entityKey);
        Class<SearchableEntity> entityClass = SearchableRepositories.getSearchableRepositoryEntityClass(entityKey);
        Table[] annotationsByType = entityClass.getAnnotationsByType(Table.class);
        String tableName = annotationsByType.length > 0 ? annotationsByType[0].name() : entityKey;
        return tableName + " (" +
                getEligibleFields(entityClass).stream().map(field -> toColumnName(field.getName())).collect(joining(",")) + ")\n" +
                getAllHints(entityClass) + "\n";
    }
    private String getDetectedMethodEntries(Class<SearchableEntity> entityClass){
        debug("[getDetectedMethodEntries]");
        return detectEligibleMethods(entityClass)
                .stream()
                .map(m -> reflectionHelper.getNameWithParamNamesAndTypesAndReturnType(m, getPrefix(entityClass) + "."))
                .collect(joining("\n"));
    }
    private String getManualMethodEntries(Class<SearchableEntity> entityClass){
        debug("[getManualMethodEntries]");
        List<String> result = new ArrayList<>();
        if(OpenkodaEntity.class.isAssignableFrom(entityClass)){
            result.add("String " + getPrefix(entityClass) + ".getProperty(String key)");
            result.add("void " + getPrefix(entityClass) + ".setProperty(String key, String value)");
            result.add("LocalDateTime " + getPrefix(entityClass) + ".getCreatedOn()");
            result.add("LocalDateTime " + getPrefix(entityClass) + ".getUpdatedOn()");
            result.add("Long " + getPrefix(entityClass) + ".getOrganizationId()");
        }
        return String.join("\n", result);
    }
    private List<Method> detectEligibleMethods(Class<SearchableEntity> entityClass){
        debug("[detectEligibleMethods]");
        List<Method> methods = new ArrayList<>();
        for(Field f : getEligibleFields(entityClass)){
            if(reflectionHelper.isSimpleType(f)){
                addGetterIfExists(f,entityClass, methods);
                if(f.getAnnotation(Formula.class) == null){
                    addSetterIfExists(f, entityClass, methods);
                }
            } else {
                addGetterIfExists(f, entityClass, methods);
            }
        }
        return methods;
    }
    private List<Field> getEligibleFields(Class<SearchableEntity> entityClass){
        debug("[getEligibleFields]");
        return stream(entityClass.getDeclaredFields())
                .filter(this::isEligible)
                .collect(Collectors.toList());
    }
    private boolean isEligible(Field field){
        return !Arrays.asList(excludedFields).contains(field.getName())
                && !Modifier.isStatic(field.getModifiers());
    }
    private void addSetterIfExists(Field field, Class<SearchableEntity> entityClass, List<Method> methods){
        try {
            Method m = entityClass.getMethod("set" + capitalize(field.getName()), field.getType());
            methods.add(m);
        } catch (NoSuchMethodException e) {
            //do nothing
        }
    }
    private void addGetterIfExists(Field field, Class<SearchableEntity> entityClass, List<Method> methods){
        try {
            String prefix = reflectionHelper.isBoolean(field) ? "is" : "get";
            Method m = entityClass.getMethod(prefix + capitalize(field.getName()), null);
            methods.add(m);
        } catch (NoSuchMethodException e) {
            //do nothing
        }
    }
    private String getPrefix(Class<SearchableEntity> entityClass){
        return reflectionHelper.getShortName(entityClass).toLowerCase()
                //remove timestamp from entity name
                .split("_")[0];
    }

    private String getAllHints(Class<SearchableEntity> entityClass) {
        debug("[getAllHints]");
        List<String> hints = new ArrayList<>();
        for (Field field : entityClass.getDeclaredFields()) {
            AiHint hint = field.getDeclaredAnnotation(AiHint.class);
            if(hint != null) {
                hints.add("hint for " + getPrefix(entityClass) + ": " + hint.value());
            }

        }
        return String.join("\n", hints);
    }

    private String getRelated(Class<SearchableEntity> entityClass) {
        debug("[getRelated]");
        List<String> result = new ArrayList<>();
        for (Field field : getRelatedFields(entityClass)) {
            result.add("Know that " + getPrefix(entityClass) + " is assigned to object with repository name " + field.getName());
        }
        return String.join("\n", result);
    }

    private List<Field> getRelatedFields(Class<SearchableEntity> entityClass) {
        debug("[getRelatedFields]");
        return stream(entityClass.getDeclaredFields())
                .filter(field -> stream(field.getDeclaredAnnotations())
                        .anyMatch(annotation -> StringUtils.indexOfAny(annotation.annotationType().getName(), new String[]{"ManyToOne", "ManyToMany", "OneToOne"}) > -1))
                .collect(Collectors.toList());
    }
}
