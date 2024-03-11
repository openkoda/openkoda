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

import com.openkoda.model.PrivilegeBase;
import org.apache.commons.lang3.ArrayUtils;
import reactor.util.function.Tuple2;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class FrontendMappingDefinition {

    public final FrontendMappingFieldDefinition[] fields;
    public final String name;
    public final String formLabel;

    public final Tuple2<FrontendMappingFieldDefinition, Function<Object, String>>[] fieldValidators;
    public final Function<? extends Form, Map<String, String>>[]  formValidators;

    public static String siteKey;

    /**
     * <p>Constructor for FrontendMappingDefinition.</p>
     *
     * @param name a {@link java.lang.String} dto.
     * @param fields an array of {@link FrontendMappingFieldDefinition} objects.
     */
    public FrontendMappingDefinition(String name, FrontendMappingFieldDefinition[] fields, Tuple2<FrontendMappingFieldDefinition, Function<Object, String>>[] fieldValidators, Function<? extends AbstractForm, Map<String, String>>[] formValidators) {
        this.name = name;
        this.fields = fields;
        this.fieldValidators = fieldValidators;
        this.formValidators = formValidators;
        this.formLabel = name + ".label";
    }

    public static FrontendMappingDefinition createFrontendMappingDefinition(
            String formName,
            PrivilegeBase defaultReadPrivilege,
            PrivilegeBase defaultWritePrivilege,
            Function<FormFieldDefinitionBuilderStart, FormFieldDefinitionBuilder> builder) {
        FormFieldDefinitionBuilder ffdb = builder.apply(new FormFieldDefinitionBuilder(formName, defaultReadPrivilege, defaultWritePrivilege));
        return new FrontendMappingDefinition(formName, ffdb.getFieldsAsArray(), ffdb.getFieldValidatorsAsArray(), ffdb.getFormValidatorsAsArray());
    }

    public static FrontendMappingDefinition createFrontendMappingDefinition(
            String formName,
            PrivilegeBase defaultReadPrivilege,
            PrivilegeBase defaultWritePrivilege,
            FrontendMappingFieldDefinition[] baseFormFields,
            Function<FormFieldDefinitionBuilderStart, FormFieldDefinitionBuilder> builder) {
        FormFieldDefinitionBuilder ffdb = builder.apply(new FormFieldDefinitionBuilder(formName, defaultReadPrivilege, defaultWritePrivilege));
        return new FrontendMappingDefinition(formName, ArrayUtils.addAll(baseFormFields, ffdb.getFieldsAsArray()), ffdb.getFieldValidatorsAsArray(), ffdb.getFormValidatorsAsArray());
    }

    /**
     * <p>Getter for the field <code>fields</code>.</p>
     *
     * @return an array of {@link FrontendMappingFieldDefinition} objects.
     */
    public FrontendMappingFieldDefinition[] getFields() {
        return fields;
    }

    public FrontendMappingFieldDefinition[] getDbTypeFields(){
        return Arrays.stream(getFields())
                .filter(s -> s.getType() != null && s.getType().getDbType() != null)
                .toArray(FrontendMappingFieldDefinition[]::new);
    }


    public Map<String, FrontendMappingFieldDefinition> getFieldNameDbTypeMap(){
        return Arrays.stream(getFields())
                .filter(s -> s.getType() != null && s.getType().getDbType() != null)
                .collect(Collectors.toMap(FrontendMappingFieldDefinition::getName, field -> field));
    }

    public String[] getNamesOfValuedTypeFields(){
        return Arrays.stream(getFields())
                .filter(s -> s.getType() != null && s.getType().hasValue())
                .map(FrontendMappingFieldDefinition::getName)
                .toArray(String[]::new);
    }

    public FrontendMappingFieldDefinition findField(String fieldName) {
        for (FrontendMappingFieldDefinition f : fields) {
            if (f.getPlainName().equals(fieldName)) {
                return f;
            }
        }
        return null;
    }

    public String getMappingKey() {
        return name.toLowerCase();
    }
}
