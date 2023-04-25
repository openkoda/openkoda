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

import com.openkoda.core.security.OrganizationUser;
import com.openkoda.model.PrivilegeBase;
import com.openkoda.model.common.LongIdEntity;

import java.util.function.BiFunction;
import java.util.function.Function;

import static com.openkoda.core.form.FieldType.*;

/**
 * <p>FormFieldDefinition class.</p>
 *
 * @author Arkadiusz Drysch (adrysch@stratoflow.com)
 * 
 */
public class FrontendMappingFieldDefinition {
    private final String name;
    public final FieldType type;
    public final PrivilegeBase readPrivilege;
    public final PrivilegeBase writePrivilege;
    public final BiFunction<OrganizationUser, LongIdEntity, Boolean> canReadCheck;
    public final BiFunction<OrganizationUser, LongIdEntity, Boolean> canWriteCheck;
    public final Function<AbstractForm, Object> datalistSupplier;
    public final Function<AbstractForm, Object> valueSupplier;
    public final Function<Object, FieldType> fieldTypeFunction;
    private final static Function<Object, FieldType> nullFunction = a -> null;
    private final static Function<Object, Object> nullSupplier = null;
    public final String key;
    public final String labelKey;
    public final String placeholderKey;
    public final String warningKey;
    public final String alertKey;
    public final String descriptionKey;
    public final String datalistId;
    public final String additionalCss;
    public final boolean allowNull;
    public final String url;
    public final String htmlFragmentName;
    public final String contentType;
    
    public final Function dtoToEntityValueConverter;
    public final Function entityToDtoValueConverter;

    public boolean isText(Form form) {
        return getFieldType(form) == text;
    }

    public boolean isPassword(Form form) {
        return getFieldType(form) == password;
    }

    public boolean isHidden(Form form) {
        return getFieldType(form) == hidden;
    }

    public boolean isMap(Form form) { return getFieldType(form) == map; }

    public boolean isFileUpload(Form form) {
        FieldType t = getFieldType(form);
        return t == file_library || t == files_library || t == files || t == image;}

    public boolean isColorPicker(Form form) { return getFieldType(form) == color_picker; }

    public boolean isTimePicker(Form form) { return getFieldType(form) == time; }

    public boolean isDocumentEditor(Form form) { return getFieldType(form) == document; }

    public boolean isCodeEditor(Form form) {
        FieldType t = getFieldType(form);
        return t == code_js || t == code_html || t == code_css || t == code_with_autocomplete;
    }
    public boolean isCodeEditorWithAutocomplete(Form form) {
        FieldType t = getFieldType(form);
        return t == code_with_autocomplete;
    }
    public boolean isReCaptcha(Form form) { return getFieldType(form) == recaptcha; }

    public FieldType getFieldType(Form form) {
        return type != null ? type : fieldTypeFunction.apply(form);
    }

    public FieldType getType() {
        return type;
    }

    public static FrontendMappingFieldDefinition createFormFieldDefinition(String formName, String name, Function<Object, FieldType> fieldTypeFunction) {
        return new FrontendMappingFieldDefinition(formName, name, null, null, null, null, null, null, null, fieldTypeFunction,
                null, null, false, null, null, null, null, null);
    }

    public static FrontendMappingFieldDefinition createFormFieldDefinition(String formName, String name, Function<Object, FieldType> fieldTypeFunction, PrivilegeBase requiredReadPrivilege, PrivilegeBase requiredWritePrivilege) {
        return new FrontendMappingFieldDefinition(formName, name, null, null, null, requiredReadPrivilege, requiredWritePrivilege, null, null, fieldTypeFunction, null, null, false, null,  null, null, null, null);
    }

    public static FrontendMappingFieldDefinition createFormFieldDefinition(String formName, String name, Function<AbstractForm, Object> valueSupplier, FieldType type) {
        return new FrontendMappingFieldDefinition(formName, name, type, null, null, null, null, valueSupplier, null, null, null, null, false, null,  null, null, null, null);
    }

    public static FrontendMappingFieldDefinition createFormFieldDefinition(String formName, String name, FieldType type) {
        return new FrontendMappingFieldDefinition(formName, name, type, null, null, null, null, null, null, null, null, null, false, null,  null, null, null, null);
    }

    public static FrontendMappingFieldDefinition createFormFieldDefinition(String formName, String name, String datalistId, FieldType type) {
        return new FrontendMappingFieldDefinition(formName, name, type, null, null, null, null, null, null, null, datalistId, null, false, null,  null, null, null, null);
    }

    public static FrontendMappingFieldDefinition createFormFieldDefinition(String formName, String name, String datalistId, FieldType type, PrivilegeBase readPrivilege, PrivilegeBase writePrivilege) {
        return new FrontendMappingFieldDefinition(formName, name, type, null, null, readPrivilege, writePrivilege, null, null, null, datalistId, null, false, null,  null, null, null, null);
    }

    public static FrontendMappingFieldDefinition createFormFieldDefinition(String formName, String name, String datalistId, FieldType type, String additionalCss, PrivilegeBase readPrivilege, PrivilegeBase writePrivilege) {
        return new FrontendMappingFieldDefinition(formName, name, type, null, null, readPrivilege, writePrivilege, null, null, null, datalistId, additionalCss, false, null,  null, null, null, null);
    }

    public static FrontendMappingFieldDefinition createNonDtoFormFieldDefinition(String formName, String name, String datalistId, FieldType type, PrivilegeBase readPrivilege, PrivilegeBase writePrivilege) {
        return new FrontendMappingFieldDefinition(formName, name, type, null, null, readPrivilege, writePrivilege, null, null, null, datalistId, null, false, null,  null, null, null, null);
    }

    public static FrontendMappingFieldDefinition createFormFieldDefinition(String formName, String name, String datalistId, FieldType type, BiFunction<OrganizationUser, LongIdEntity, Boolean> canReadCheck, BiFunction<OrganizationUser, LongIdEntity, Boolean> canWriteCheck) {
        return new FrontendMappingFieldDefinition(formName, name, type, canReadCheck, canWriteCheck, null, null, null, null, null, datalistId, null, false, null,  null, null, null, null);
    }
    public static FrontendMappingFieldDefinition createFormFieldDefinition(String formName, String name, String datalistId, boolean allowNull, FieldType type, BiFunction<OrganizationUser, LongIdEntity, Boolean> canReadCheck, BiFunction<OrganizationUser, LongIdEntity, Boolean> canWriteCheck) {
        return new FrontendMappingFieldDefinition(formName, name, type, canReadCheck, canWriteCheck, null, null, null, null, null, datalistId, null, allowNull, null,  null, null, null, null);
    }

    public static FrontendMappingFieldDefinition createFormFieldDefinition(String formName, String name, String datalistId, boolean allowNull, FieldType type, PrivilegeBase readPrivilege, PrivilegeBase writePrivilege) {
        return new FrontendMappingFieldDefinition(formName, name, type, null, null, readPrivilege, writePrivilege, null, null, null, datalistId, null, allowNull, null,  null, null, null, null);
    }

    public static FrontendMappingFieldDefinition createFormFieldDefinition(String formName, String name, String datalistId, FieldType type, Function<AbstractForm, Object> datalistSupplier) {
        return new FrontendMappingFieldDefinition(formName, name, type, null, null, null, null, null, datalistSupplier, null, datalistId, null, false, null,  null, null, null, null);
    }

    public static FrontendMappingFieldDefinition createFormFieldDefinition(String formName, String name, String datalistId,
                                                                           FieldType type, Function<AbstractForm, Object> datalistSupplier, PrivilegeBase requiredReadPrivilege, PrivilegeBase requiredWritePrivilege) {
        return new FrontendMappingFieldDefinition(formName, name, type, null, null, requiredReadPrivilege,
                requiredWritePrivilege, null, datalistSupplier, null, datalistId, null, false, null,  null, null, null, null);
    }

    public static FrontendMappingFieldDefinition createFormFieldDefinition(String formName, String name, String datalistId,
                                                                           FieldType type, Function<AbstractForm, Object> datalistSupplier, PrivilegeBase requiredReadPrivilege, PrivilegeBase requiredWritePrivilege, String contentType, Function dtoToEntityValueConverter) {
        return new FrontendMappingFieldDefinition(formName, name, type, null, null, requiredReadPrivilege,
                requiredWritePrivilege, null, datalistSupplier, null, datalistId, null, false, null,  null, contentType, dtoToEntityValueConverter, null);
    }

    public static FrontendMappingFieldDefinition createFormFieldDefinition(String formName, String name, String datalistId, String url,
                                                                           FieldType type, Function<AbstractForm, Object> datalistSupplier, PrivilegeBase requiredReadPrivilege, PrivilegeBase requiredWritePrivilege) {
        return new FrontendMappingFieldDefinition(formName, name, type, null, null, requiredReadPrivilege,
                requiredWritePrivilege, null, datalistSupplier, null, datalistId, null, false, url,  null, null, null, null);
    }

    public static FrontendMappingFieldDefinition createFormFieldDefinition(String formName, String name, String datalistId, FieldType type, String additionalCss,
                                                                           Function<AbstractForm, Object> datalistSupplier, PrivilegeBase requiredReadPrivilege, PrivilegeBase requiredWritePrivilege) {
        return new FrontendMappingFieldDefinition(formName, name, type, null, null, requiredReadPrivilege,
                requiredWritePrivilege, null, datalistSupplier, null, datalistId, additionalCss, false, null,  null, null, null, null);
    }

    public static FrontendMappingFieldDefinition createFormFieldDefinition(String formName, String name, String datalistId, Boolean allowNull, FieldType type, String additionalCss,
                                                                           Function<AbstractForm, Object> datalistSupplier, PrivilegeBase requiredReadPrivilege, PrivilegeBase requiredWritePrivilege, Function dtoToEntityValueConverter) {
        return new FrontendMappingFieldDefinition(formName, name, type, null, null, requiredReadPrivilege,
                requiredWritePrivilege, null, datalistSupplier, null, datalistId, additionalCss, allowNull, null,  null, null, dtoToEntityValueConverter, null);
    }

    public static FrontendMappingFieldDefinition createFormFieldDefinition(String formName, String name, FieldType type,
                                                                           PrivilegeBase requiredReadPrivilege, PrivilegeBase requiredWritePrivilege, Function<AbstractForm, Object> valueSupplier) {
        return new FrontendMappingFieldDefinition(formName, name, type, null, null, requiredReadPrivilege,
                requiredWritePrivilege, valueSupplier, null, null, null, null, false, null,  null, null, null, null);
    }

    public static FrontendMappingFieldDefinition createFormFieldDefinition(String formName, String name, FieldType type,
                                                                           PrivilegeBase requiredReadPrivilege, PrivilegeBase requiredWritePrivilege, Function<AbstractForm, Object> valueSupplier, boolean hasDto) {
        return new FrontendMappingFieldDefinition(formName, name, type, null, null, requiredReadPrivilege,
                requiredWritePrivilege, valueSupplier, null, null, null, null, false, null,  null, null, null, null);
    }

    public static FrontendMappingFieldDefinition createFormFieldDefinition(String formName, String name, FieldType type, PrivilegeBase requiredReadPrivilege, PrivilegeBase requiredWritePrivilege) {
        return new FrontendMappingFieldDefinition(formName, name, type, null, null, requiredReadPrivilege, requiredWritePrivilege, null, null, null, null, null, false, null,  null, null, null, null);
    }

    public static FrontendMappingFieldDefinition createFormFieldDefinition(String formName, String name, FieldType type, String additionalCss, PrivilegeBase requiredReadPrivilege, PrivilegeBase requiredWritePrivilege) {
        return new FrontendMappingFieldDefinition(formName, name, type, null, null, requiredReadPrivilege, requiredWritePrivilege, null, null, null, null, additionalCss, false, null,  null, null, null, null);
    }

    public static FrontendMappingFieldDefinition createFormFieldDefinition(String formName, String name, FieldType type, String additionalCss, PrivilegeBase requiredReadPrivilege, PrivilegeBase requiredWritePrivilege, String contentType, Function dtoToEntityValueConverter) {
        return new FrontendMappingFieldDefinition(formName, name, type, null, null, requiredReadPrivilege, requiredWritePrivilege, null, null, null, null, additionalCss, false, null,  null, contentType, dtoToEntityValueConverter, null);
    }

    public static FrontendMappingFieldDefinition createFormFieldDefinition(String formName, String name, FieldType type, String additionalCss, PrivilegeBase requiredReadPrivilege, BiFunction<OrganizationUser, LongIdEntity, Boolean> canWriteCheck) {
        return new FrontendMappingFieldDefinition(formName, name, type, null, canWriteCheck, requiredReadPrivilege, null, null, null, null, null, additionalCss, false, null,  null, null, null, null);
    }

    public static FrontendMappingFieldDefinition createFormFieldDefinition(String formName, String name, FieldType type, BiFunction<OrganizationUser, LongIdEntity, Boolean> canReadCheck, BiFunction<OrganizationUser, LongIdEntity, Boolean> canWriteCheck) {
        return new FrontendMappingFieldDefinition(formName, name, type, canReadCheck, canWriteCheck, null, null, null, null, null, null, null, false, null,  null, null, null, null);
    }

    public static FrontendMappingFieldDefinition createFormFieldDefinition(String formName, String name, FieldType type, PrivilegeBase requiredReadPrivilege, PrivilegeBase requiredWritePrivilege, String url) {
        return new FrontendMappingFieldDefinition(formName, name, type, null, null, requiredReadPrivilege, requiredWritePrivilege, null, null, null, null, null, false, url,  null, null, null, null);
    }

    public static FrontendMappingFieldDefinition createFormFieldDefinition(String formName, String name, FieldType type, PrivilegeBase requiredReadPrivilege, PrivilegeBase requiredWritePrivilege, String url, Function<AbstractForm, Object> valueSupplier, String htmlFragmentName) {
        return new FrontendMappingFieldDefinition(formName, name, type, null, null, requiredReadPrivilege, requiredWritePrivilege, valueSupplier, null, null, null, null, false, url,  htmlFragmentName, null, null, null);
    }

    public static FrontendMappingFieldDefinition createFormFieldDefinition(String formName, String name, FieldType type, BiFunction<OrganizationUser, LongIdEntity, Boolean> canReadCheck, BiFunction<OrganizationUser, LongIdEntity, Boolean> canWriteCheck, String url) {
        return new FrontendMappingFieldDefinition(formName, name, type, canReadCheck, canWriteCheck, null, null,null, null, null, null, null, false, url,  null, null, null, null);
    }

    public static FrontendMappingFieldDefinition createFormFieldDefinition(String formName, FrontendMappingFieldDefinition f, String additionalCss) {
        return new FrontendMappingFieldDefinition(formName, f.name, f.type, f.canReadCheck, f.canWriteCheck, f.readPrivilege, f.writePrivilege, f.valueSupplier, f.datalistSupplier, f.fieldTypeFunction, f.datalistId, additionalCss, f.allowNull, f.url, f.htmlFragmentName, f.contentType, f.dtoToEntityValueConverter, f.entityToDtoValueConverter);
    }

    public static FrontendMappingFieldDefinition createFormFieldDefinition(String formName, FrontendMappingFieldDefinition f, PrivilegeBase readPrivilege, PrivilegeBase writePrivilege) {
        return new FrontendMappingFieldDefinition(formName, f.name, f.type, f.canReadCheck, f.canWriteCheck, readPrivilege, writePrivilege, f.valueSupplier, f.datalistSupplier, f.fieldTypeFunction, f.datalistId, f.additionalCss, f.allowNull, f.url, f.htmlFragmentName, f.contentType, f.dtoToEntityValueConverter, f.entityToDtoValueConverter);
    }

    public static FrontendMappingFieldDefinition createFormFieldDefinition(String formName, FrontendMappingFieldDefinition f, BiFunction<OrganizationUser, LongIdEntity, Boolean> canReadCheck, BiFunction<OrganizationUser, LongIdEntity, Boolean> canWriteCheck) {
        return new FrontendMappingFieldDefinition(formName, f.name, f.type, canReadCheck, canWriteCheck, f.readPrivilege, f.writePrivilege, f.valueSupplier, f.datalistSupplier, f.fieldTypeFunction, f.datalistId, f.additionalCss, f.allowNull, f.url,  f.htmlFragmentName, f.contentType, f.dtoToEntityValueConverter, f.entityToDtoValueConverter);
    }

    public static FrontendMappingFieldDefinition createFormFieldDefinition(String formName, FrontendMappingFieldDefinition f, Function<AbstractForm, Object> valueSupplier) {
        return new FrontendMappingFieldDefinition(formName, f.name, f.type, f.canReadCheck, f.canWriteCheck, f.readPrivilege, f.writePrivilege, valueSupplier, f.datalistSupplier, f.fieldTypeFunction, f.datalistId, f.additionalCss, f.allowNull, f.url,  f.htmlFragmentName, f.contentType, f.dtoToEntityValueConverter, f.entityToDtoValueConverter);
    }

    public static FrontendMappingFieldDefinition createFormFieldDefinition(String formName, FrontendMappingFieldDefinition f, Function dtoToEntityValueConverter, Function entityToDtoValueConverter) {
        return new FrontendMappingFieldDefinition(formName, f.name, f.type, f.canReadCheck, f.canWriteCheck, f.readPrivilege, f.writePrivilege, f.valueSupplier, f.datalistSupplier, f.fieldTypeFunction, f.datalistId, f.additionalCss, f.allowNull, f.url,  f.htmlFragmentName, f.contentType, dtoToEntityValueConverter, entityToDtoValueConverter);
    }

    public String getName() {
        return name;
    }

    public String getPlainName() {
        return name;
    }

    public String getName(boolean dtoIsMap) {
        return  (dtoIsMap ? "dto[" + name + "]" : "dto." + name );
    }

    protected FrontendMappingFieldDefinition(
            String formName,
            String name,
            FieldType type,
            BiFunction<OrganizationUser, LongIdEntity, Boolean> canReadCheck,
            BiFunction<OrganizationUser, LongIdEntity, Boolean> canWriteCheck,
            PrivilegeBase readPrivilege,
            PrivilegeBase writePrivilege,
            Function<AbstractForm, Object> valueSupplier,
            Function<AbstractForm, Object> datalistSupplier,
            Function<Object, FieldType> fieldTypeFunction,
            String datalistId,
            String additionalCss,
            boolean allowNull,
            String url,
            String htmlFragmentName,
            String contentType,
            Function<?, ?> dtoToEntityValueConverter,
            Function<?, ?> entityToDtoValueConverter) {
        this.name = name;
        this.type = type;
        this.readPrivilege = readPrivilege;
        this.writePrivilege = writePrivilege;
        this.canReadCheck = canReadCheck;
        this.canWriteCheck = canWriteCheck;
        this.valueSupplier = valueSupplier;
        this.datalistSupplier = datalistSupplier;
        this.fieldTypeFunction = fieldTypeFunction;
        this.key = formName + "." + name;
        this.labelKey = formName + "." + name + ".label";
        this.placeholderKey = formName + "." + name + ".placeholder";
        this.warningKey = formName + "." + name + ".warning";
        this.alertKey = formName + "." + name + ".alert";
        this.descriptionKey = formName + "." + name + ".description";
        this.datalistId = datalistId;
        this.additionalCss = additionalCss;
        this.allowNull = allowNull;
        this.url = url;
        this.htmlFragmentName = htmlFragmentName;
        this.contentType = contentType;
        this.dtoToEntityValueConverter = dtoToEntityValueConverter;
        this.entityToDtoValueConverter = entityToDtoValueConverter;
    }

}
