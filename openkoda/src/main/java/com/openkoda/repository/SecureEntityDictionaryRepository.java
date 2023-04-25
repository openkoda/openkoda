/*
MIT License

Copyright (c) 2016-2022, Codedose CDX Sp. z o.o. Sp. K. <stratoflow.com>

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
documentation files (the "Software"), to deal in the Software without restriction, including without limitation
the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software,
and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice
shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR
A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS
OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR
IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*/

package com.openkoda.repository;

import com.openkoda.controller.ComponentProvider;
import com.openkoda.core.flow.Tuple;
import com.openkoda.core.form.AbstractForm;
import com.openkoda.core.helper.JsonHelper;
import com.openkoda.core.helper.PrivilegeHelper;
import com.openkoda.core.security.HasSecurityRules;
import com.openkoda.core.tracker.DebugLogsDecoratorWithRequestId;
import com.openkoda.dto.file.FileDto;
import com.openkoda.form.rule.LogicalOperator;
import com.openkoda.form.rule.Operator;
import com.openkoda.model.ControllerEndpoint;
import com.openkoda.model.FrontendResource;
import com.openkoda.model.OptionWithLabel;
import com.openkoda.model.common.ModelConstants;
import com.openkoda.model.common.SearchableEntity;
import com.openkoda.model.common.SearchableOrganizationRelatedEntity;
import com.openkoda.model.common.SearchableRepositoryMetadata;
import com.openkoda.model.file.File;
import com.openkoda.model.file.TimestampedEntityWithFiles;
import com.openkoda.service.user.RoleService;
import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.openkoda.model.file.File.toFileDto;

@Repository("secureEntityDictionaryRepository")
public class SecureEntityDictionaryRepository extends ComponentProvider implements HasSecurityRules, ModelConstants {

    @Value("#{'${role.types:" +
            RoleService.ROLE_TYPE_ORG + "," +
            RoleService.ROLE_TYPE_GLOBAL + "," +
            RoleService.ROLE_TYPE_GLOBAL_ORG + "}'.split(',')}")
    private List<String> roleTypes;

    @Value("#{'${language.options:en}'.split(',')}")
    private List<String> languagesList;

    @PersistenceContext
    private EntityManager em;



    private Map<String, Object> commonDictionaries = new HashMap<>();
    private Map<String, String> languages = new LinkedHashMap<>();
    public static HashMap<String, String> countries;
    private static Map<String, Map<Object, String>> moduleDictionaries = new HashMap<>();



    static {
        LinkedHashMap<String, String> tempMap = new LinkedHashMap<>();

        for (String iso : Locale.getISOCountries()) {
            Locale l = new Locale("", iso);
            tempMap.put(iso, l.getDisplayCountry(Locale.ENGLISH));
        }

        // sort countries by name
        countries = tempMap.entrySet().stream()
                .sorted(Map.Entry.comparingByValue())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
    }


    public Map dictionary(String entityKey) {
        SearchableRepositoryMetadata gsa = SearchableRepositories.getSearchableRepositoryMetadata(entityKey);
        return dictionary(gsa);//, ID, null, null);
    }
    public Map dictionary(Class entityClass) {
        SearchableRepositoryMetadata gsa = SearchableRepositories.getSearchableRepositoryMetadata(entityClass);
        return dictionary(gsa);//, ID, null, null);
    }
    public <T extends SearchableEntity> Map dictionary(SearchableRepositoryMetadata gsa) {
        if (gsa == null) {
            warn("SearchableRepository for entity key not found");
            return Collections.emptyMap();
        }

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> q = cb.createQuery(Long.class).distinct(true);
        Root root = q.from(gsa.entityClass());
        q.select(root.get(ID));
        q.where(toSecurePredicate(null, null, root, q, cb));
        List<Long> result = em.createQuery(q).getResultList();
        String tableName = SearchableRepositories.discoverTableName(gsa.entityClass());
        Query query = em.createNativeQuery("select id, " + gsa.descriptionFormula() + " from " +  tableName + " where id in (:ids) order by " + gsa.descriptionFormula());
        query.setParameter("ids", result);
        List<Object[]> r = query.getResultList();

        Map<Object, String> res = new LinkedHashMap<>(r.size());
        for (Object[] t : r) {
            res.put(t[0], t[1]+"");
        }
        return res;

    }

    public <T extends SearchableEntity> Map dictionary(Class<T> entityClass, String labelField) {
        return dictionary(entityClass, ID, labelField, labelField);
    }

    public <T extends SearchableEntity> Map dictionary(Class<T> entityClass, String keyField, String labelField) {
        return dictionary(entityClass, keyField, labelField, labelField);
    }

    public <T extends SearchableEntity> Map dictionary(String entityKey, String keyField, String labelField) {
        Class<SearchableOrganizationRelatedEntity> entityClass = (Class<SearchableOrganizationRelatedEntity>) SearchableRepositories.getSearchableRepositoryMetadata(entityKey).entityClass();
        if (entityClass == null) {
            warn("SearchableRepository for entity key {} not found", entityKey);
            return Collections.emptyMap();
        }
        return dictionary(entityClass, keyField, labelField, labelField);
    }


    public <T extends SearchableEntity> Map dictionary(Class<T> entityClass, String keyField, String labelField, String sortField) {
        CriteriaQuery<Tuple> q = getTupleCriteriaQuery(entityClass, keyField, labelField, sortField).distinct(true);
        List<Tuple> result = em.createQuery(q).getResultList();
        return toLinkedMap(result);
     }

    public <T extends SearchableEntity> Map dictionary(Class<T> entityClass, String label, Function<Tuple, ? extends Stream<Tuple>> postQueryTupleMapper) {
        CriteriaQuery<Tuple> q = getTupleCriteriaQuery(entityClass, ID, label, label).distinct(true);
        List<Tuple> result = em.createQuery(q).getResultList();
        if (postQueryTupleMapper != null) {
            result = result.stream().flatMap(postQueryTupleMapper).distinct().collect(Collectors.toList());
        }
        return toLinkedMap(result);
    }

    private <T extends SearchableEntity> CriteriaQuery<Tuple> getTupleCriteriaQuery(Class<T> entityClass, String keyField, String labelField, String sortField) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Tuple> q = cb.createQuery(Tuple.class);
        Root<T> root = q.from(entityClass);
        q.select(cb.construct(Tuple.class, root.get(keyField), root.get(labelField)));
        q.where(toSecurePredicate(null, null, root, q, cb));
        if (sortField != null) {
            q.orderBy(cb.asc(root.get(sortField)));
        }
        return q;
    }

    @PostConstruct void init() {
        for (String lang : languagesList) {
            Locale locale = Locale.forLanguageTag(lang);
            languages.put(lang, locale.getDisplayLanguage(locale));
        }
        AbstractForm.setSecureEntityDictionaryRepositoryOnce(this);
    }

    public SecureEntityDictionaryRepository getInstance() {
        return this;
    }

    public Map<String, String> getLoggersDictionary() {
        Map<String, String> d = new LinkedHashMap<>(DebugLogsDecoratorWithRequestId.availableLoggers.size());
        for(Class c : DebugLogsDecoratorWithRequestId.availableLoggers) {
            d.put(c.getName(), c.getSimpleName());
        }
        return d;
    }


    public String getCommonDictionaries() throws JSONException {
        commonDictionaries.clear();
        commonDictionaries.put("booleanValues", toJsonString(Map.of("true", "YES", "false", "NO")));
        commonDictionaries.put("privileges", PrivilegeHelper.allEnumsAsPrivilegeBaseJsonString());
        commonDictionaries.put("frontendResourceType", enumsToJsonString(FrontendResource.Type.values()));
        commonDictionaries.put("consumers", mapObjectArraysToJsonString(services.eventListener.getConsumersArray()));
        commonDictionaries.put("organizationRoles", listTupleToJsonString(repositories.unsecure.organizationRole.findAllAsTupleWithLabelName()));
        commonDictionaries.put("globalRoles", listTupleToJsonString(repositories.unsecure.globalRole.findAllAsTupleWithLabelName()));
        commonDictionaries.put("languages", toJsonString(languages));
        commonDictionaries.put("events", mapObjectToJsonString(services.eventListener.getEvents()));
        commonDictionaries.put("roleTypes", listStringToJsonString(roleTypes));
        commonDictionaries.put("httpMethod", enumsToJsonString(ControllerEndpoint.HttpMethod.values()));
        commonDictionaries.put("responseType", enumsToJsonString(ControllerEndpoint.ResponseType.values()));
        commonDictionaries.put("countries", toJsonString(countries));
        commonDictionaries.put("operators", enumsLabelToJsonString(Operator.values()));
        commonDictionaries.put("logicalOperators", enumsLabelToJsonString(LogicalOperator.values()));
        commonDictionaries.put("globalOrganizationRoles", listTupleToJsonString(repositories.unsecure.globalOrganizationRole.findAllAsTuple()));
        commonDictionaries.putAll(moduleDictionaries);

        return JsonHelper.to(commonDictionaries);
    }

    public String getOrganizationDictionaries(Long organizationId) {
        Map<String, Map<Object, String>> result = new HashMap<>(3);
        return JsonHelper.to(result);
    }
    public Set<String> getCommonDictionariesNames() {
        return commonDictionaries.keySet();
    }
    public static Map<Object, String> toLinkedMap(List<Tuple> allByOrganizationId) {
        Map<Object, String> result = new LinkedHashMap<>(allByOrganizationId.size());
        for (Tuple t : allByOrganizationId) {
            result.put(t.v(Object.class, 0), t.v(String.class, 1));
        }
        return result;
    }

    public <E extends Enum<E>> Map<Object, String> enumDictionary(E[] enumValues) {
        return enumsToMap(enumValues);
    }

    public static <E extends Enum<E>> Map<Object, String> enumsToMap(E[] enumValues) {
        return Arrays.stream(enumValues).collect(Collectors.toMap(Function.identity(), E::name));
    }
    public static <E extends Enum<E>> Map<Object, String> enumsToMapWithLabels(E[] enumValues) {
        return Arrays.stream(enumValues).collect(Collectors.toMap(Function.identity(), e -> ((OptionWithLabel) e).getLabel()));
    }


    private <E extends Enum<E>> String enumsToJsonString(E[] enumClass) throws JSONException {
        JSONArray results = new JSONArray();
        JSONObject result;
        for (E e : enumClass) {
            result = new JSONObject();
            result.put("k", e.name());
            result.put("v", e.name());
            results.put(result);
        }
        return results.toString();
    }

    private <E extends Enum<E>> String enumsLabelToJsonString(E[] enumClass) throws JSONException {
        JSONArray results = new JSONArray();
        JSONObject result;
        for (E e : enumClass) {
            result = new JSONObject();
            result.put("k", e.name());
            result.put("v", ((OptionWithLabel) e).getLabel());
            results.put(result);
        }
        return results.toString();
    }

    private Object mapObjectArraysToJsonString(Map<Object, Object[]> objArray) throws JSONException {
        JSONArray results = new JSONArray();
        JSONObject result;
        for (var entry : objArray.entrySet()) {
            result = new JSONObject();
            result.put("k", entry.getKey());
            JSONArray values = new JSONArray();
            for (Object o : entry.getValue()) {
                values.put(o);
            }
            result.put("v", values);
            results.put(result);
        }
        return results.toString();
    }

    private String mapObjectToJsonString(Map<Object, String> map) throws JSONException {
        JSONArray results = new JSONArray();
        JSONObject result;
        for (var entry : map.entrySet()) {
            result = new JSONObject();
            result.put("k", entry.getKey());
            result.put("v", entry.getValue());
            results.put(result);
        }
        return results.toString();
    }

    private String toJsonString(Map<String, String> map) throws JSONException {
        JSONArray results = new JSONArray();
        JSONObject result;
        for (var entry : map.entrySet()) {
            result = new JSONObject();
            result.put("k", entry.getKey());
            result.put("v", entry.getValue());
            results.put(result);
        }
        return results.toString();
    }

    private String listTupleToJsonString(List<Tuple> allByOrganizationId) throws JSONException {
        JSONArray results = new JSONArray();
        JSONObject result;
        for (Tuple t : allByOrganizationId) {
            result = new JSONObject();
            String label = messages.get(t.v(String.class, 1));
            result.put("k", t.v(String.class, 0));
            result.put("v", StringUtils.isNotEmpty(label) ? label : t.v(String.class, 0));
            results.put(result);
        }
        return results.toString();
    }

    private String listStringToJsonString(List<String> allByOrganizationId) throws JSONException {
        JSONArray results = new JSONArray();
        JSONObject result;
        for (String s : allByOrganizationId) {
            result = new JSONObject();
            result.put("k", s);
            result.put("v", s);
            results.put(result);
        }
        return results.toString();
    }

    private Map<Object, String> toLinkedMapWithCustomLabels(List<Tuple> allByOrganizationId) {
        Map<Object, String> result = new LinkedHashMap<>(allByOrganizationId.size());
        for (Tuple t : allByOrganizationId) {
            String label = messages.get(t.v(String.class, 1));
            result.put(t.v(String.class, 0), StringUtils.isNotEmpty(label) ? label : t.v(String.class, 0));
        }
        return result;
    }
    public static void addModuleDictionary(Map<Object,String> dictionary, String dictName){
        moduleDictionaries.put(dictName, dictionary);
    }
    public static Map<Object, String> collectionToLinkedMap(Collection<Object> values) {
        Map<Object, String> result = new LinkedHashMap<>(values.size());
        for (Object t : values) {
            result.put(t, t.toString());
        }
        return result;
    }

    public final Map<Long, FileDto> getFileDtos(TimestampedEntityWithFiles entity)  {
        if (entity == null || entity.getFiles() == null) {
            return Collections.emptyMap();
        }
        Map<Long, FileDto> result = new LinkedHashMap<>();
        Long i = 0l;
        for (File a : entity.getFiles()) {
            if (a != null) {
                result.put(i, toFileDto(a));
                i++;
            }
        }
        return result;
    }

}
