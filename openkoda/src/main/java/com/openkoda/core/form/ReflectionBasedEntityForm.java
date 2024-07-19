/*
MIT License

Copyright (c) 2016-2023, Openkoda CDX Sp. z o.o. Sp. K. <openkoda.com>

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

package com.openkoda.core.form;

import com.openkoda.controller.HtmlCRUDControllerConfigurationMap;
import com.openkoda.core.flow.LoggingComponent;
import com.openkoda.core.helper.PrivilegeHelper;
import com.openkoda.core.security.OrganizationUser;
import com.openkoda.core.security.UserProvider;
import com.openkoda.model.common.SearchableOrganizationRelatedEntity;
import com.openkoda.model.component.FrontendResource;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.util.ReflectionUtils;
import org.springframework.validation.BindingResult;
import reactor.util.function.Tuple3;
import reactor.util.function.Tuples;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.openkoda.model.Privilege.readOrgData;
/**
 *
 *
 * @author Arkadiusz Drysch (adrysch@stratoflow.com)
 *
 */
public class ReflectionBasedEntityForm extends AbstractOrganizationRelatedEntityForm<OrganizationRelatedMap, SearchableOrganizationRelatedEntity> {

    private static final String NO_ACCESS = "[no access]";
    public static final Map<Class, Function> converters = new HashMap<>();

    static {
        converters.put(BigDecimal.class, a -> (a == null || StringUtils.isBlank(a+"")) ? null : new BigDecimal(a + ""));
        converters.put(LocalDateTime.class, a -> (a == null || StringUtils.isBlank(a+"")) ? null : LocalDateTime.parse(a + ""));
        converters.put(LocalDate.class, a -> (a == null || StringUtils.isBlank(a+"")) ? null : LocalDate.parse(a + ""));
        converters.put(Long.class, a -> (a == null || StringUtils.isBlank(a+"")) ? null : Long.valueOf(a+""));
        converters.put(long.class, a -> (a == null || StringUtils.isBlank(a+"")) ? 0L : Long.parseLong(a+""));
        converters.put(Integer.class, a -> (a == null || StringUtils.isBlank(a+"")) ? null : Integer.valueOf(a+""));
        converters.put(int.class, a -> (a == null || StringUtils.isBlank(a+"")) ? 0 : Integer.parseInt(a+""));
        converters.put(Boolean.class, a -> (a == null || StringUtils.isBlank(a+"")) ? null : Boolean.valueOf(a+""));
        converters.put(boolean.class, a -> (a == null || StringUtils.isBlank(a+"")) ? false : Boolean.parseBoolean(a+""));
    }

    public ReflectionBasedEntityForm(FrontendMappingDefinition frontendMappingDefinition) {
        super(frontendMappingDefinition);
    }

    public ReflectionBasedEntityForm(FrontendMappingDefinition frontendMappingDefinition, Long organizationId, SearchableOrganizationRelatedEntity entity) {
        super(organizationId, new OrganizationRelatedMap(), entity, frontendMappingDefinition);
        populateSuppliedValuesFrom();
    }

    private void populateSuppliedValuesFrom() {
        for (FrontendMappingFieldDefinition f : frontendMappingDefinition.getFields()) {
            if (f.valueSupplier != null) {
                dto.put(f.getPlainName(), f.valueSupplier.apply(this));
            }
        }
    }

    @Override
    public <F extends Form> F validate(BindingResult br) {
        return null;
    }

    @Override
    protected ReflectionBasedEntityForm populateFrom(SearchableOrganizationRelatedEntity entity) {
        try {
            Map<String, Object> propertiesMap = PropertyUtils.describe(entity);
            dto.putAll(propertiesMap);
            for (FrontendMappingFieldDefinition f : frontendMappingDefinition.getFields()) {
                if (f.valueSupplier != null) {
                    dto.put(f.getPlainName(), f.valueSupplier.apply(this));
                } else {
                    Object entityValue = propertiesMap.get(f.getPlainName());
                    if (f.entityToDtoValueConverter != null) {
                        dto.put(f.getPlainName(), f.entityToDtoValueConverter.apply(entityValue));
                    } else {
                        dto.put(f.getPlainName(), entityValue);
                    }
                }
            }
            return this;
        } catch (IllegalAccessException |InvocationTargetException | NoSuchMethodException e) {
            throw new RuntimeException(String.format("Can't describe entity %s", entity), e);
        }
    }

    @Override
    protected SearchableOrganizationRelatedEntity populateTo(SearchableOrganizationRelatedEntity entity) {
       if (singleFieldToUpdate != null) {
           FrontendMappingFieldDefinition f = frontendMappingDefinition.findField(singleFieldToUpdate);
           setEntityValue(entity, f);
       } else {
           for (FrontendMappingFieldDefinition f : frontendMappingDefinition.getFields()) {
               setEntityValue(entity, f);
           }
       }
       return entity;
    }

    private void setEntityValue(SearchableOrganizationRelatedEntity entity, FrontendMappingFieldDefinition f)  {
        try {
            if (not(f.getFieldType(this).hasValue())) {
                return;
            }
            Function converter = getConverter(entity, f);
            PropertyUtils.setProperty(entity, f.getPlainName(), getSafeValue(BeanUtils.getProperty(entity, f.getPlainName()), f.getPlainName(), converter));
//        BeanUtils.setProperty(entity, fieldName, getSafeValue(BeanUtils.getProperty(entity, fieldName), fieldName, converter));
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new RuntimeException(String.format("Can't write field %s", f.getPlainName()), e);
        }
    }

    @Override
    protected Object convertDtoValue(FrontendMappingFieldDefinition ffd, Object dtoValue) {
        return entity == null ? dtoValue : getConverter(entity, ffd).apply(dtoValue);
    }

    private Function getConverter(SearchableOrganizationRelatedEntity entity, FrontendMappingFieldDefinition f) {
        Function converter = f.dtoToEntityValueConverter;
        if (converter == null) {
            Field field = ReflectionUtils.findField(entity.getClass(), f.getPlainName());
            if (field.getType().isEnum()) {
                converter = a -> (a == null || StringUtils.isBlank(a + "")) ? null : Enum.valueOf(((Class<? extends Enum>) field.getType()), (String) a);
            } else if ("java.util.List<java.lang.Long>".equals(field.getGenericType().getTypeName())) {
                converter = a -> {
                    if (a == null) { return null; }
                    if (String.class.equals(a.getClass())) {
                        return new ArrayList<Long>(List.of(Long.parseLong((String)a)));
                    }
                    if (String[].class.equals(a.getClass())) {
                        List<Long> collect = Arrays.stream((String[]) a)
                                .map(Long::parseLong)
                                .collect(Collectors.toList());
                        return collect;
                    }
                    return a;
                };
            } else if ("java.lang.String".equals(field.getGenericType().getTypeName())) {
                converter = a -> {
//                for multiselect which sends data as String[]
                    if (a instanceof String []) { return String.join(",", (String[]) a); }
                    return a;
                };
            } else {
                converter = converters.getOrDefault(field.getType(), Function.identity());
            }
        }
        return converter;
    }

    @Override
    public boolean isMapDto() {
        return true;
    }

    public static List<FrontendMappingFieldDefinition> getFieldsHeaders(FrontendMappingDefinition fd, String[] fieldNames, Map<String, Boolean> fieldColumnVisibility, Long organizationId) {
        if (fieldNames == null) {return Collections.emptyList();}
        List<FrontendMappingFieldDefinition> result = new ArrayList<>(fieldNames.length);
        for (int k = 0; k < fieldNames.length; k++) {
            if(organizationId != null && Boolean.FALSE.equals(fieldColumnVisibility.get(fieldNames[k]))) {
                continue;
            }

            FrontendMappingFieldDefinition field = fd.findField(fieldNames[k]);
            if(field == null) {
                field = FrontendMappingFieldDefinition.createFormFieldDefinition(fd.name, fieldNames[k], FieldType.text);
            }
            
            boolean canRead = organizationId == null || field.readPrivilege == null || PrivilegeHelper.getInstance().canReadGlobalOrOrgField(field, organizationId);
            if(canRead || fieldColumnVisibility.get(fieldNames[k])) {
                result.add(field);
            }
        }
        return result;
    }

    public static List<List<Object>> calculateFieldsValuesWithReadPrivileges(FrontendMappingDefinition fd, List<? extends SearchableOrganizationRelatedEntity> entities, String[] fieldNames
            , Map<String, Boolean> fieldColumnVisibility, Long organizationId) {
        List<List<Object>> result = new ArrayList<>(entities.size());
        Map<String, Map> dictionaries = new HashMap<>();
        for (FrontendMappingFieldDefinition field : fd.fields) {
            if (field.datalistSupplier != null && !field.formBasedDatalistSupplier) {
                dictionaries.put(field.datalistId, (Map) field.datalistSupplier.apply(null, dictionaryRepository));
            }
        }

        OrganizationUser user = UserProvider.getFromContext().get();
        for(String fieldName : fieldNames) {
            fieldColumnVisibility.put(fieldName, ((organizationId == null && user.isSuperUser()) || entities.size() == 0) ? Boolean.TRUE : Boolean.FALSE);
        }

        for (SearchableOrganizationRelatedEntity se: entities) {
            List<Object> accessibleFields = calculateFieldValuesWithReadPrivileges(fd, se, fieldNames, dictionaries, fieldColumnVisibility, (organizationId == null && user.isSuperUser()));
            if(accessibleFields != null && accessibleFields.size() > 0) {
                result.add(accessibleFields);
            }
        }

        // hide columns with all denied/hidden fields
        if(organizationId != null) {
            for (int k = fieldNames.length - 1; k >= 0; k--) {
                if(!fieldColumnVisibility.get(fieldNames[k])) {
                    for (List<Object> row : result) {
                        if(row.size() >= k + 1) {
                            row.remove(k);
                        }
                    }
                }
            }
        }

        return result;
    }

    public static List<Object> calculateFieldValuesWithReadPrivileges(FrontendMappingDefinition fd, SearchableOrganizationRelatedEntity entity, String[] fieldNames, Map<String, Map> dictionaries,
            Map<String, Boolean> fieldColumnVisibility, boolean canReadAll) {
        if (fieldNames == null) {
            return Collections.emptyList();
        }

        List<Object> result = new ArrayList<>(fieldNames.length);
        try {
            int i = 0;
            for (int k = 0; k < fieldNames.length; k++) {
                try {
                    boolean isReferenceFieldProperty = fieldNames[k].contains(".");
                    String referencedEntityKey = isReferenceFieldProperty ? StringUtils.substringBefore(fieldNames[k], ".") : fieldNames[k];
                    FrontendMappingFieldDefinition f = isReferenceFieldProperty ?
                            Arrays.stream(fd.fields)
                                    .filter(def -> def.referencedEntityKey != null && def.referencedEntityKey.equals(referencedEntityKey)).findFirst().orElse(null)
                            : fd.findField(fieldNames[k]);
                    if(f == null) {
    //                    allow display of data which have no column representation for users with readOrgData privilege,
//                    most likely these are columns like createdOn, updatedOn, organizationId, etc.
//                    all entity specific columns should have their field representation with access limitation
                        f = FrontendMappingFieldDefinition.createFormFieldDefinition(fd.name, fieldNames[k], FieldType.text, readOrgData, readOrgData);
                    }

                    boolean canRead = f.readPrivilege == null || canReadAll || PrivilegeHelper.getInstance().canReadField(f, entity);
                    fieldColumnVisibility.put(fieldNames[k], fieldColumnVisibility.get(fieldNames[k]) || canRead);

                    if(isReferenceFieldProperty) {
                        result.add(canRead && PropertyUtils.getProperty(entity, referencedEntityKey) != null ? PropertyUtils.getProperty(entity, fieldNames[k]) : NO_ACCESS);
                    } else {
                        result.add(canRead ? PropertyUtils.getProperty(entity, fieldNames[k]) : NO_ACCESS);
                    }
                    if (!isReferenceFieldProperty && f.datalistId != null && dictionaries.containsKey(f.datalistId)) {
                        result.set(i, canRead ? dictionaries.get(f.datalistId).get(result.get(i)) : NO_ACCESS);
                    }

                } catch (NoSuchMethodException exc) {
                    LoggingComponent.debugLogger.warn("Could not read entity property", exc);
                    result.add("");
                }

                i++;
            }
        } catch (Exception e) {
            LoggingComponent.debugLogger.warn("Could not read entity property", e);
        }
        return result;
    }

    public static List<Map<String, Object>> calculateFieldsValuesWithReadPrivilegesAsMap(FrontendMappingDefinition fd, Page<SearchableOrganizationRelatedEntity> entities, String[] fieldNames) {
        List<Map<String, Object>> result = new ArrayList<>(entities.getNumberOfElements());
        for (SearchableOrganizationRelatedEntity se: entities) {
            result.add(calculateFieldValuesWithReadPrivilegesAsMap(fd, se, fieldNames));
        }
        return result;
    }

    public static Map<String, Object> calculateFieldValuesWithReadPrivilegesAsMap(FrontendMappingDefinition fd, SearchableOrganizationRelatedEntity entity, String[] fieldNames) {
        HashMap<String, Object> result = new HashMap<>();
        if (fieldNames == null) {return result;}
        try {
            for (String fn :  fieldNames) {
                FrontendMappingFieldDefinition f = fd.findField(fn);
                boolean canRead = PrivilegeHelper.getInstance().canReadField(f, entity);
                result.put(fn, canRead ? PropertyUtils.getProperty(entity, fn) : "");
            }
        } catch (Exception e) {
            LoggingComponent.debugLogger.warn("Could not read entity property", e);
        }
        return result;
    }

    public boolean prepareDto(Map<String,String> params, SearchableOrganizationRelatedEntity entity) {
        nullNonWriteableDtoFields(entity);
        castToString();
        setParamsToDto(params);
        return true;
    }

    public static List<FrontendMappingFieldDefinition> getFilterFields(FrontendMappingDefinition fd, String[] fieldNames) {
        if (fieldNames == null) {
            return Collections.emptyList();
        }
        List<FrontendMappingFieldDefinition> result = new ArrayList<>(fieldNames.length);
        for (int k = 0; k < fieldNames.length; k++) {
            FrontendMappingFieldDefinition field = fd.findField(fieldNames[k]);
            if(field.getType().equals(FieldType.dropdown) || field.getType().equals(FieldType.many_to_one)) {
                FrontendMappingFieldDefinition dictionaryField = fd.findField(field.datalistId);
                if(dictionaryField != null) {
                    result.add(dictionaryField);
                }
            }
            result.add(field);
        }
        return result;
    }

    public static Collection<Object> getFilterFieldsNames(FrontendMappingDefinition fd) {
        return fd.getFieldsNamesByType(List.of(FieldType.text, FieldType.checkbox, FieldType.dropdown, FieldType.number, FieldType.date, FieldType.datetime, FieldType.many_to_one));
    }

    public static Collection<Object> getTableColumnsNames(FrontendMappingDefinition fd) {
        List<Object> result = new ArrayList<>();
        List<FrontendMappingFieldDefinition> manyToOneFields = fd.getFieldsByType(List.of(FieldType.many_to_one));
        for(FrontendMappingFieldDefinition field : manyToOneFields) {
            CRUDControllerConfiguration controllerConfig = HtmlCRUDControllerConfigurationMap.getControllers().get(field.referencedEntityKey);
            if(controllerConfig != null) {
                result.addAll(Arrays.stream(controllerConfig.getFrontendMappingDefinition().getNamesOfValuedTypeFields())
                        .map(atr -> field.getName().replace("Id", "") + "." + atr).toList());
            }
        }
        result.addAll(Arrays.asList(fd.getNamesOfValuedTypeFields()));
        return result;
    }

    public static List<Tuple3<String, FrontendMappingFieldDefinition, String>> getFilterTypesAndValues(FrontendMappingDefinition fd, Map<String,String> filters) {
        return filters.entrySet().stream()
                .map(entry -> Tuples.of(entry.getKey(), fd.findField(StringUtils.substringBefore(entry.getKey(), "_")), entry.getValue()))
                .collect(Collectors.toList());
    }

    /**Fields for which a user has no write permission must be null in the dto (otherwise {@link AbstractForm#getSafeValue} throws exception in {@link this#setEntityValue} method)
     * @param entity
     */
    private void nullNonWriteableDtoFields(SearchableOrganizationRelatedEntity entity){
        prepareFieldsReadWritePrivileges(entity);
        dto.forEach((fieldName, fieldValue) -> {
            FrontendMappingFieldDefinition field = frontendMappingDefinition.findField(fieldName);
            if(field != null && !readWriteForField.get(field).getT2()){
                dto.put(fieldName, null);
            }
        });
    }

    /** Sets parameters sent in a request to a dto. Skips params which don't map on a field in the form definition.
     * Doesn't skip fields for which a user has no write permission - in that case an exception will be raised
     * @param params
     */
    private void setParamsToDto(Map<String,String> params){
        if (params != null && !params.isEmpty()) {
            params.forEach((fieldName, fieldValue) -> {
                FrontendMappingFieldDefinition field = frontendMappingDefinition.findField(fieldName);
                //if valueSupplier is not null it means a field's value is calculated form others fields so shouldn't be updated directly
                if (field != null && field.valueSupplier == null) {
                    dto.put(fieldName, fieldValue);
                }
            });
        }
    }

    /**
     * Explicitly casts dto values to string with toString() method.
     * That's necessary for field validators {@link FormFieldDefinitionBuilder#validate} - in case a dto value can't be cast to String as ((String) value) it throws exception.
     * See for instance "type" field in frontendResourceForm - this value is an enum {@link FrontendResource}.Type
     */
    private void castToString(){
        dto.forEach((fieldName, fieldValue) -> {
            dto.put(fieldName, fieldValue != null ? fieldValue.toString() : null);
        });
    }
}