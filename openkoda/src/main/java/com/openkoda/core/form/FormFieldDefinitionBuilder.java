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

package com.openkoda.core.form;

import com.openkoda.core.helper.PrivilegeHelper;
import com.openkoda.core.security.OrganizationUser;
import com.openkoda.model.PrivilegeBase;
import com.openkoda.model.common.LongIdEntity;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

import static com.openkoda.core.form.FrontendMappingFieldDefinition.createFormFieldDefinition;
import static com.openkoda.core.helper.PrivilegeHelper.valueOfString;

public class FormFieldDefinitionBuilder<V> extends FormFieldDefinitionBuilderStart {


    public FrontendMappingFieldDefinition[] getFieldsAsArray() {
        return fields.toArray(new FrontendMappingFieldDefinition[fields.size()]);
    }

    public Tuple2<FrontendMappingFieldDefinition, Function<?, String>>[] getFieldValidatorsAsArray() {
        return fieldValidators.toArray(new Tuple2[fieldValidators.size()]);
    }

    public Function<? extends Form, Map<String, String>>[]  getFormValidatorsAsArray() {
        return formValidators.toArray(new Function[formValidators.size()]);
    }

    public FormFieldDefinitionBuilder(String formName, PrivilegeBase defaultReadPrivilege, PrivilegeBase defaultWritePrivilege) {
        super(formName, defaultReadPrivilege, defaultWritePrivilege);
    }

    public FormFieldDefinitionBuilder<V> additionalCss(String additionalCss) {
        fields.set(fields.size() - 1, lastField = createFormFieldDefinition(formName, lastField, additionalCss));
        return this;
    }
    public FormFieldDefinitionBuilder<V> additionalPrivileges(PrivilegeBase readPrivilege, PrivilegeBase writePrivilege) {
        fields.set(fields.size() - 1, lastField = createFormFieldDefinition(formName, lastField, readPrivilege, writePrivilege));
        return this;
    }
    public FormFieldDefinitionBuilder<V> additionalPrivileges(String readPrivilege, String writePrivilege) {
        fields.set(fields.size() - 1, lastField = createFormFieldDefinition(formName, lastField, (PrivilegeBase) valueOfString(readPrivilege), (PrivilegeBase) valueOfString(writePrivilege)));
        return this;
    }
    public FormFieldDefinitionBuilder<V> additionalPrivileges(BiFunction<OrganizationUser, LongIdEntity, Boolean> canReadCheck, BiFunction<OrganizationUser, LongIdEntity, Boolean> canWriteCheck) {
        fields.set(fields.size() - 1, lastField = createFormFieldDefinition(formName, lastField, canReadCheck, canWriteCheck));
        return this;
    }

    public FormFieldDefinitionBuilder<V> additionalAction(String actionLabelKey, String actionUrl, PrivilegeBase additionalActionPrivilege) {
        fields.set(fields.size() - 1, lastField = createFormFieldDefinition(formName, lastField, actionLabelKey, actionUrl, additionalActionPrivilege));
        return this;
    }

    public FormFieldDefinitionBuilder<V> additionalAction(String actionLabelKey, String actionUrl, String privilegeNameAsString) {
        fields.set(fields.size() - 1, lastField = createFormFieldDefinition(formName, lastField, actionLabelKey, actionUrl, (PrivilegeBase) PrivilegeHelper.valueOfString(privilegeNameAsString)));
        return this;
    }

    public FormFieldDefinitionBuilder<V> valueSupplier(Function<AbstractForm, Object> valueSupplier) {
        fields.set(fields.size() - 1, lastField = createFormFieldDefinition(formName, lastField, valueSupplier));
        return this;
    }

    public <V2> FormFieldDefinitionBuilder<V2> valueConverters(Function<V, V2> toEntityValue, Function<Object, Object> toDtoValue) {
        fields.set(fields.size() - 1, lastField = createFormFieldDefinition(formName, lastField, toEntityValue, toDtoValue));
        return (FormFieldDefinitionBuilder<V2>) this;
    }

    public FormFieldDefinitionBuilder<V> validate(Function<V, String> validatorReturningErrorCode) {
        fieldValidators.add(Tuples.of(lastField, validatorReturningErrorCode));
        return this;
    }

    public FormFieldDefinitionBuilder<V> validateForm(Function<? extends Form, Map<String, String>> validatorReturningRejectedFieldToErrorCodeMap) {
        formValidators.add(validatorReturningRejectedFieldToErrorCodeMap);
        return this;
    }

    public <VT> FormFieldDefinitionBuilder<VT> valueType(Class<VT> c) {
        return (FormFieldDefinitionBuilder<VT>)this;
    }



}