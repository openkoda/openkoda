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

import com.openkoda.core.helper.PrivilegeHelper;
import com.openkoda.model.common.LongIdEntity;
import com.openkoda.repository.SecureEntityDictionaryRepository;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.ReflectionUtils;
import org.springframework.validation.BindingResult;
import reactor.util.function.Tuples;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

/**
 * This class is an extension to a basic {@link Form}.
 * It assigns the DTO object to the form and provides mapping for the DTO fields.
 *
 * @author Arkadiusz Drysch (adrysch@stratoflow.com)
 */
public abstract class AbstractForm<D> extends Form implements DtoAndEntity<D, LongIdEntity> {

    /**
     * {@link SecureEntityDictionaryRepository}
     * Repository which provides dictionaries
     * (in other words lists of enums, strings and objects available for form HTML generation)
     */
    protected static SecureEntityDictionaryRepository dictionaryRepository = null;

    /**
     * DTO object of the form
     */
    public D dto;

    /**
     * Form field name to update.
     * Use in case the form should post only a single field instead of all dto object fields.
     */
    protected String singleFieldToUpdate;

    /**
     * Field mapping for all forms registered in the application and values retrieval for each form field.
     * The key is the concatenation of form name and DTO class name.
     * The value is another map containing all field names in a particular form as keys and functions to retrieve values for those fields as map values.
     */
    private static Map<String, Map<String, Function>> fieldMapping = new HashMap<>();

    private static Set<String> dirtyFrontendMapping = new HashSet<>();


    public AbstractForm(D dto, FrontendMappingDefinition frontendMappingDefinition) {
        super(frontendMappingDefinition);
        detectFieldMapping(dto);
        this.dto = dto;
    }

    public AbstractForm(FrontendMappingDefinition frontendMappingDefinition) {
        this(null, frontendMappingDefinition);
    }

    /**
     * Retrieves field value from fieldMapping and validates the value provided in the dto for this field.
     *
     * @param ffd {@link FrontendMappingFieldDefinition}
     * @param fieldValidator {@link Function} validating field's value
     * @param br {@link BindingResult}
     */
    public void validateField(FrontendMappingFieldDefinition ffd, Function<Object, String> fieldValidator, BindingResult br) {
        Object dtoValue = getField(ffd.getPlainName());
        dtoValue = convertDtoValue(ffd, dtoValue);
        String errorCode = fieldValidator.apply(dtoValue);
        if (StringUtils.isNotBlank(errorCode)) {
            br.rejectValue(ffd.getName(isMapDto()), errorCode);
        }
    }



    protected Object convertDtoValue(FrontendMappingFieldDefinition ffd, Object dtoValue) {
        return dtoValue;
    }

    public String extractFieldName(String nameWithDto) {
        if (isMapDto()) {
            return nameWithDto.replace("dto[", "").replace("]", "");
        } else {
            return nameWithDto.replace("dto.", "");
        }
    }

    /**
     * Retrieves field value from fieldMapping and validates the value provided in the dto for this field.
     *
     * @param ffd {@link FormFieldDefinition}
     * @param fieldValidator {@link Function} validating field's value
     */
    public boolean validateField(FrontendMappingFieldDefinition ffd, Function<Object, String> fieldValidator) {
        Object dtoValue = getField(ffd.getPlainName());
        dtoValue = convertDtoValue(ffd, dtoValue);
        String errorCode = fieldValidator.apply(dtoValue);
        return !StringUtils.isNotBlank(errorCode);

    }

    /**
     * Initializes form's dictionary repositories
     *
     * @param dictionaryRepository {@link SecureEntityDictionaryRepository}
     */
    public static void setSecureEntityDictionaryRepositoryOnce(SecureEntityDictionaryRepository dictionaryRepository) {
        if (AbstractForm.dictionaryRepository != null) {
            System.out.println("WARN: SecureRepositories already initialized");
            return;
        }
        AbstractForm.dictionaryRepository = dictionaryRepository;
    }

    public static void markDirty(String frontendMappingDefinitionName) {
        AbstractForm.dirtyFrontendMapping.add(frontendMappingDefinitionName);
    }

    /**
     * Returns dictionaries available to generate the form
     *
     * @return {@link SecureEntityDictionaryRepository}
     */
    public SecureEntityDictionaryRepository getDictionaryRepository() {
        return dictionaryRepository;
    }

    /**
     * Builds a map of field mappings {@link AbstractForm#fieldMapping}
     * It uses form name, class name and property descriptors
     *
     * @param dto DTO object assigned to a form
     */
    protected void detectFieldMapping(D dto) {
        if(dto == null) { return; }
        Class c = dto.getClass();
        Map<String, Function> m = fieldMapping.get(frontendMappingDefinition.name + "#" + c.getCanonicalName());
        if(m != null && !AbstractForm.dirtyFrontendMapping.contains(frontendMappingDefinition.name)) { return; }

        //this should be done only once per form class
        m = new HashMap<>(frontendMappingDefinition.fields.length);
        fieldMapping.put(frontendMappingDefinition.name + "#" + c.getCanonicalName(), m);
        PropertyDescriptor[] pds = PropertyUtils.getPropertyDescriptors(c);
        Map<String, PropertyDescriptor> pdMap = new HashMap<>(pds.length);
        for (PropertyDescriptor pd: pds) {
            pdMap.put(pd.getName(), pd);
        }
        for (FrontendMappingFieldDefinition ffd : frontendMappingDefinition.fields) {
            String ffdName = ffd.getPlainName();
            if (ffdName == null) {
                continue;
            }
            if (dto instanceof OrganizationRelatedMap) {
                m.put(ffdName, a -> ((OrganizationRelatedMap) a).get(ffdName));
            } else {
                PropertyDescriptor pd = pdMap.get(ffdName);
                if (pd != null) {
                    Method readMethod = pd.getReadMethod();
                    m.put(ffdName,
                        a -> {
                            try {
                                return readMethod.invoke(a);
                            } catch (Exception e) {
                                throw new RuntimeException(e);
                            }
                        }
                    );
                    continue;
                }
                Field f = ReflectionUtils.findField(c, ffdName);
                if (f != null) {
                    m.put(ffdName,
                            a -> ReflectionUtils.getField(f, a)
                    );
                    continue;
                }
                warn("No field reader for form {} field {}", frontendMappingDefinition.name, ffdName);
            }
        }
        AbstractForm.dirtyFrontendMapping.remove(frontendMappingDefinition.name);
    }

    @Override
    public D getDto() {
        return dto;
    }

    public void setDto(D dto) {
        this.dto = dto;
        detectFieldMapping(dto);
    }

    @Override
    public void process() {
        for (FrontendMappingFieldDefinition f : frontendMappingDefinition.fields) {
            readWriteForField.put(f,
                    Tuples.of(
                            PrivilegeHelper.getInstance().canReadGlobalField(f),
                            PrivilegeHelper.getInstance().canWriteGlobalField(f)));
        }
    }

    public String getSingleFieldToUpdate() {
        return singleFieldToUpdate;
    }

    public void setSingleFieldToUpdate(String singleFieldToUpdate) {
        this.singleFieldToUpdate = singleFieldToUpdate;
    }

    public boolean isMapDto() {
        return false;
    }

    /**
     * See {@link AbstractForm#getSafeValue(Object, String, Function)}
     *
     * @param entityValue value of the entity property
     * @param fieldName name of the form field
     * @return field value
     */
    final protected <T> T getSafeValue(T entityValue, String fieldName) {
        return getSafeValue(entityValue, fieldName, Function.identity());
    }

    /**
     * Retrieves safe value for the particular form field
     * It considers write privileges for field as well as null values
     *
     * @param entityValue value of the entity property
     * @param fieldName name of the form field
     * @param f function to run for the dto value
     * @return field value
     */
    final protected <F, T> T getSafeValue(T entityValue, String fieldName, Function<F,T> f) {
        //if singleFieldToUpdate contains field name, do not update any other field
        if (singleFieldToUpdate != null && !fieldName.equals(singleFieldToUpdate)) return entityValue;
        FrontendMappingFieldDefinition field = frontendMappingDefinition.findField(fieldName);
        Boolean canWrite = readWriteForField.get(field).getT2();
        F dtoValue = (F)getField(fieldName);
        if (canWrite) return f.apply(dtoValue);
        //here user can't update the field
        if (dtoValue == null) return entityValue;
        //here user is hacker
        throw new RuntimeException(String.format("Can't write field %s", fieldName));
    }

    /**
     * Retrieves safe value for the particular form field
     * It considers write privileges for field as well as null values
     *
     * @param entityValue value of the entity property
     * @param fieldName name of the form field
     * @param dtoValue value of the dto property
     * @param f function to run for the dto value
     * @return field value
     */
    final protected <F, T> T getSafeValue(T entityValue, String fieldName, F dtoValue, Function<F,T> f) {
        //if singleFieldToUpdate contains field name, do not update any other field
        if (singleFieldToUpdate != null && !(fieldName.equals(singleFieldToUpdate) || fieldName.contains(singleFieldToUpdate))) return entityValue;
        FrontendMappingFieldDefinition field = frontendMappingDefinition.findField(fieldName);
        Boolean canWrite = readWriteForField.get(field).getT2();
        if (canWrite) return f.apply(dtoValue);
        //here user can't update the field
        if (dtoValue == null) return entityValue;
        //here user is hacker
        throw new RuntimeException(String.format("Can't write field %s", fieldName));
    }

    protected Function <String, String> nullOnEmpty = ((String s) -> !s.isEmpty() ? s : null);
    protected Function <String, String> nullIfBlank = ((String s) -> StringUtils.defaultIfBlank(s, null));
    protected Function <String, String> emptyOnNull = ((String s) -> StringUtils.defaultString(s));
    protected Function <String, String> emptyIfBlank = ((String s) -> StringUtils.defaultIfBlank(s, ""));

    private Object getField(String fieldName) {
        //FIXME: refactor name generation
        return fieldMapping.get(frontendMappingDefinition.name + "#" + dto.getClass().getCanonicalName()).get(fieldName).apply(dto);
    }
}
