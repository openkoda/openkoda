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

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.openkoda.model.Organization;
import com.openkoda.model.PrivilegeBase;
import com.openkoda.repository.SecureEntityDictionaryRepository;
import reactor.util.function.Tuple2;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

import static com.openkoda.core.form.FieldType.*;
import static com.openkoda.core.form.FrontendMappingFieldDefinition.createFormFieldDefinition;
import static com.openkoda.core.form.FrontendMappingFieldDefinition.createNonDtoFormFieldDefinition;

public class FormFieldDefinitionBuilderStart {
    private static final String DATALIST_PREFIX = "__datalist_";
    protected final List<FrontendMappingFieldDefinition> fields = new ArrayList<>();
    protected final List<Tuple2<FrontendMappingFieldDefinition, Function<?, String>>> fieldValidators = new ArrayList<>();
    protected final List<Function<? extends Form, Map<String, String>>>  formValidators = new ArrayList<>();
    protected final String formName;
    protected final PrivilegeBase defaultReadPrivilege;
    protected final PrivilegeBase defaultWritePrivilege;
    protected static String RECAPTCHA = "ReCaptcha";

    @JsonIgnore
    protected FrontendMappingFieldDefinition lastField;

    public FormFieldDefinitionBuilderStart (
            String formName,
            PrivilegeBase defaultReadPrivilege,
            PrivilegeBase defaultWritePrivilege) {
        this.formName = formName;
        this.defaultReadPrivilege = defaultReadPrivilege;
        this.defaultWritePrivilege = defaultWritePrivilege;
    }

    public FormFieldDefinitionBuilder<Object> datalist(String datalistId, BiFunction<DtoAndEntity, SecureEntityDictionaryRepository, Object> datalistSupplier) {
        fields.add(lastField = createFormFieldDefinition(formName, DATALIST_PREFIX + datalistId, datalistId, datalist, datalistSupplier, defaultReadPrivilege, defaultWritePrivilege));
        return (FormFieldDefinitionBuilder<Object>)this;
    }

    public FormFieldDefinitionBuilder<Object> datalist(String datalistId, Function<SecureEntityDictionaryRepository, Object> datalistSupplier) {
        fields.add(lastField = createFormFieldDefinition(formName, DATALIST_PREFIX + datalistId, datalistId, datalist, datalistSupplier, defaultReadPrivilege, defaultWritePrivilege));
        return (FormFieldDefinitionBuilder<Object>)this;
    }

    public FormFieldDefinitionBuilder<String> text(String fieldName) {
        fields.add(lastField = createFormFieldDefinition(formName, fieldName, text, defaultReadPrivilege, defaultWritePrivilege));
        return (FormFieldDefinitionBuilder<String>)this;
    }

    public FormFieldDefinitionBuilder<String> textarea(String fieldName) {
        fields.add(lastField = createFormFieldDefinition(formName, fieldName, textarea, defaultReadPrivilege, defaultWritePrivilege));
        return (FormFieldDefinitionBuilder<String>)this;
    }

    public FormFieldDefinitionBuilder<Boolean> checkbox(String fieldName) {
        fields.add(lastField = createFormFieldDefinition(formName, fieldName, checkbox, defaultReadPrivilege, defaultWritePrivilege));
        return (FormFieldDefinitionBuilder<Boolean>)this;
    }

    public FormFieldDefinitionBuilder<LocalDateTime> datetime(String fieldName) {
        fields.add(lastField = createFormFieldDefinition(formName, fieldName, datetime, defaultReadPrivilege, defaultWritePrivilege));
        return (FormFieldDefinitionBuilder<LocalDateTime>)this;
    }

    public FormFieldDefinitionBuilder<LocalDate> date(String fieldName) {
        fields.add(lastField = createFormFieldDefinition(formName, fieldName, date, defaultReadPrivilege, defaultWritePrivilege));
        return (FormFieldDefinitionBuilder<LocalDate>)this;
    }

    public FormFieldDefinitionBuilder<Number> number(String fieldName) {
        fields.add(lastField = createFormFieldDefinition(formName, fieldName, number, defaultReadPrivilege, defaultWritePrivilege));
        return (FormFieldDefinitionBuilder<Number>)this;
    }

    public FormFieldDefinitionBuilder<String> dropdown(String fieldName, String datalistId) {
        fields.add(lastField = createFormFieldDefinition(formName, fieldName, datalistId, dropdown, defaultReadPrivilege, defaultWritePrivilege));
        return (FormFieldDefinitionBuilder<String>)this;
    }


//    public FormFieldDefinitionBuilder<Object> oneToMany(String fieldName, String urlToAddObjectToList, String datalistId, Function<AbstractForm, Object> datalistSupplier, String componentFragmentName) {
//        fields.add( lastField = createFormFieldDefinition(formName, fieldName, one_to_many, defaultReadPrivilege, defaultWritePrivilege, urlToAddObjectToList, datalistId, datalistSupplier, componentFragmentName));
//        return (FormFieldDefinitionBuilder<Object>)this;
//    }

//    public FormFieldDefinitionBuilder<Object> oneToMany(String fieldName, String urlToAddObjectToList, Function<Object, Object> valueSupplier) {
//        fields.add(lastField = createFormFieldDefinition(formName, fieldName, one_to_many, defaultReadPrivilege, defaultWritePrivilege, urlToAddObjectToList, valueSupplier, "forms::default-entity-tile"));
//        return (FormFieldDefinitionBuilder<Object>)this;
//    }

    public FormFieldDefinitionBuilder<String> dropdown(String fieldName, String datalistId, boolean allowNull) {
        fields.add(lastField = createFormFieldDefinition(formName, fieldName, datalistId, allowNull, dropdown, defaultReadPrivilege, defaultWritePrivilege));
        return (FormFieldDefinitionBuilder<String>)this;
    }

    public FormFieldDefinitionBuilder<String> dropdown(String fieldName, BiFunction<DtoAndEntity, SecureEntityDictionaryRepository, Object> datalistSupplier) {
        fields.add(lastField = createFormFieldDefinition(formName, fieldName, null, dropdown, datalistSupplier, defaultReadPrivilege, defaultWritePrivilege));
        return (FormFieldDefinitionBuilder<String>)this;
    }

    public FormFieldDefinitionBuilder<String> dropdown(String fieldName, String datalistId, Boolean allowNull) {
        fields.add(lastField = createFormFieldDefinition(formName, fieldName, datalistId, allowNull, dropdown, defaultReadPrivilege, defaultWritePrivilege));
        return (FormFieldDefinitionBuilder<String>)this;
    }

    public FormFieldDefinitionBuilder<String> dropdownWithDisable(String fieldName, String datalistId) {
        fields.add(lastField = createFormFieldDefinition(formName, fieldName, datalistId, dropdown_with_disable, defaultReadPrivilege, defaultWritePrivilege));
        return (FormFieldDefinitionBuilder<String>)this;
    }

    public FormFieldDefinitionBuilder<String> dropdownWithDisable(String fieldName, BiFunction<DtoAndEntity, SecureEntityDictionaryRepository, Object> datalistSupplier) {
        fields.add(lastField = createFormFieldDefinition(formName, fieldName, null, dropdown_with_disable, datalistSupplier, defaultReadPrivilege, defaultWritePrivilege));
        return (FormFieldDefinitionBuilder<String>)this;
    }

    public FormFieldDefinitionBuilder<String> sectionWithDropdown(String fieldName, String datalistId) {
        fields.add(lastField = createFormFieldDefinition(formName, fieldName, datalistId, section_with_dropdown, defaultReadPrivilege, defaultWritePrivilege));
        return (FormFieldDefinitionBuilder<String>)this;
    }

    public FormFieldDefinitionBuilder<String> sectionWithDropdown(String fieldName, BiFunction<DtoAndEntity, SecureEntityDictionaryRepository, Object> datalistSupplier) {
        fields.add(lastField = createFormFieldDefinition(formName, fieldName, null, section_with_dropdown, null, datalistSupplier, defaultReadPrivilege, defaultWritePrivilege));
        return (FormFieldDefinitionBuilder<String>)this;
    }

    public FormFieldDefinitionBuilder<String> dropdownNonDto(String fieldName, String datalistId) {
        fields.add(lastField = createNonDtoFormFieldDefinition(formName, fieldName, datalistId, dropdown, defaultReadPrivilege, defaultWritePrivilege));
        return (FormFieldDefinitionBuilder<String>)this;
    }

    public FormFieldDefinitionBuilder<String> checkboxList(String fieldName, BiFunction<DtoAndEntity, SecureEntityDictionaryRepository, Object> datalistSupplier) {
        fields.add(lastField = createFormFieldDefinition(formName, fieldName, fieldName, checkbox_list, datalistSupplier, defaultReadPrivilege, defaultWritePrivilege));
        return (FormFieldDefinitionBuilder<String>)this;
    }

    public FormFieldDefinitionBuilder<String> checkboxList(String fieldName, String datalistId) {
        fields.add(lastField = createFormFieldDefinition(formName, fieldName, datalistId, checkbox_list, defaultReadPrivilege, defaultWritePrivilege));
        return (FormFieldDefinitionBuilder<String>)this;
    }

    public FormFieldDefinitionBuilder<Long> organizationSelect(String fieldName) {
        datalist("organizations", d -> d.dictionary(Organization.class));
        fields.add(lastField = createFormFieldDefinition(formName, fieldName, "organizations", true, organization_select, defaultReadPrivilege, defaultWritePrivilege));
        return (FormFieldDefinitionBuilder<Long>)this;
    }
    public FormFieldDefinitionBuilder<Object> radioList(String fieldName, String datalistId) {
        fields.add(lastField = createFormFieldDefinition(formName, fieldName, datalistId, radio_list, defaultReadPrivilege, defaultWritePrivilege));
        return (FormFieldDefinitionBuilder<Object>)this;
    }

    public FormFieldDefinitionBuilder<Object> radioListNoLabel(String fieldName, BiFunction<DtoAndEntity, SecureEntityDictionaryRepository, Object> datalistSupplier) {
        fields.add(lastField = createFormFieldDefinition(formName, fieldName, null, radio_list_no_label, null, datalistSupplier, defaultReadPrivilege, defaultWritePrivilege));
        return (FormFieldDefinitionBuilder<Object>)this;
    }

    public FormFieldDefinitionBuilder<Object> radioListNoLabel(String fieldName, String dataListId) {
        fields.add(lastField = createFormFieldDefinition(formName, fieldName, dataListId, radio_list_no_label, defaultReadPrivilege, defaultWritePrivilege));
        return (FormFieldDefinitionBuilder<Object>)this;
    }

    public FormFieldDefinitionBuilder<Object> customFieldType(String fieldName, Function<Object, FieldType> fieldTypeFunction) {
        fields.add(lastField = createFormFieldDefinition(formName, fieldName, fieldTypeFunction, defaultReadPrivilege, defaultWritePrivilege));
        return (FormFieldDefinitionBuilder<Object>)this;
    }

    public FormFieldDefinitionBuilder<Object> divider(String fieldName) {
        fields.add(lastField = createFormFieldDefinition(formName, fieldName, divider, defaultReadPrivilege, defaultWritePrivilege));
        return (FormFieldDefinitionBuilder<Object>)this;
    }

    public FormFieldDefinitionBuilder<String> codeCss(String fieldName) {
        fields.add(lastField = createFormFieldDefinition(formName, fieldName, code_css, defaultReadPrivilege, defaultWritePrivilege));
        return (FormFieldDefinitionBuilder<String>)this;
    }

    public FormFieldDefinitionBuilder<String> codeHtml(String fieldName) {
        fields.add(lastField = createFormFieldDefinition(formName, fieldName, code_html, defaultReadPrivilege, defaultWritePrivilege));
        return (FormFieldDefinitionBuilder<String>)this;
    }

    public FormFieldDefinitionBuilder<String> codeJs(String fieldName) {
        fields.add(lastField = createFormFieldDefinition(formName, fieldName, code_js, defaultReadPrivilege, defaultWritePrivilege));
        return (FormFieldDefinitionBuilder<String>)this;
    }

    public FormFieldDefinitionBuilder<String> codeWithWebendpointAutocomplete(String fieldName) {
        fields.add(lastField = createFormFieldDefinition(formName, fieldName, code_with_webendpoint_autocomplete, defaultReadPrivilege, defaultWritePrivilege));
        return (FormFieldDefinitionBuilder<String>)this;
    }
    public FormFieldDefinitionBuilder<String> codeWithFormAutocomplete(String fieldName) {
        fields.add(lastField = createFormFieldDefinition(formName, fieldName, code_with_form_autocomplete, defaultReadPrivilege, defaultWritePrivilege));
        return (FormFieldDefinitionBuilder<String>)this;
    }

    public FormFieldDefinitionBuilder<String> hidden(String fieldName) {
        fields.add(lastField = createFormFieldDefinition(formName, fieldName, hidden, defaultReadPrivilege, defaultWritePrivilege));
        return (FormFieldDefinitionBuilder<String>)this;
    }

    public FormFieldDefinitionBuilder<Object> switchValues(String fieldName) {
        fields.add(lastField = createFormFieldDefinition(formName, fieldName, switch_values, defaultReadPrivilege, defaultWritePrivilege));
        return (FormFieldDefinitionBuilder<Object>)this;
    }

    public FormFieldDefinitionBuilder<Object> switchValuesWithWarning(String fieldName) {
        fields.add(lastField = createFormFieldDefinition(formName, fieldName, switch_values_with_warning, defaultReadPrivilege, defaultWritePrivilege));
        return (FormFieldDefinitionBuilder<Object>)this;
    }

    public FormFieldDefinitionBuilder<Object> sectionWithCheckboxWithWarning(String fieldName) {
        fields.add(lastField = createFormFieldDefinition(formName, fieldName, section_with_checkbox_with_warning, defaultReadPrivilege, defaultWritePrivilege));
        return (FormFieldDefinitionBuilder<Object>)this;
    }

    public FormFieldDefinitionBuilder<Object> sectionWithCheckbox(String fieldName) {
        fields.add(lastField = createFormFieldDefinition(formName, fieldName, section_with_checkbox, defaultReadPrivilege, defaultWritePrivilege));
        return (FormFieldDefinitionBuilder<Object>)this;
    }

    public FormFieldDefinitionBuilder<Object> sectionWithSwitch(String fieldName) {
        fields.add(lastField = createFormFieldDefinition(formName, fieldName, section_with_switch, defaultReadPrivilege, defaultWritePrivilege));
        return (FormFieldDefinitionBuilder<Object>)this;
    }

    public FormFieldDefinitionBuilder<Object> sectionWithSwitchContent(String fieldName) {
        fields.add(lastField = createFormFieldDefinition(formName, fieldName, section_with_switch_content, defaultReadPrivilege, defaultWritePrivilege));
        return (FormFieldDefinitionBuilder<Object>)this;
    }

    public FormFieldDefinitionBuilder<String> password(String fieldName) {
        fields.add(lastField = createFormFieldDefinition(formName, fieldName, password, defaultReadPrivilege, defaultWritePrivilege));
        return (FormFieldDefinitionBuilder<String>)this;
    }

    public FormFieldDefinitionBuilder<Object> submitToNewTab(String fieldName, String url) {
        fields.add(lastField = createFormFieldDefinition(formName, fieldName, submit_to_new_tab, defaultReadPrivilege, defaultWritePrivilege, url));
        return (FormFieldDefinitionBuilder<Object>)this;
    }

    public FormFieldDefinitionBuilder<Object> map(String fieldName) {
        fields.add(lastField = createFormFieldDefinition(formName, fieldName, map, defaultReadPrivilege, defaultWritePrivilege));
        return (FormFieldDefinitionBuilder<Object>) this;
    }


    public FormFieldDefinitionBuilder<Object> imagesLibrary(String fieldName, BiFunction<DtoAndEntity, SecureEntityDictionaryRepository, Object> datalistSupplier) {
        fields.add(lastField = createFormFieldDefinition(formName, fieldName, DATALIST_PREFIX + fieldName, files_library, datalistSupplier, defaultReadPrivilege, defaultWritePrivilege, "image/png,image/jpeg", filesConverter));
        return (FormFieldDefinitionBuilder<Object>)this;
    }

    public FormFieldDefinitionBuilder<Object> imageLibrary(String fieldName, BiFunction<DtoAndEntity, SecureEntityDictionaryRepository, Object> datalistSupplier) {
        fields.add(lastField = createFormFieldDefinition(formName, fieldName, DATALIST_PREFIX + fieldName, file_library, datalistSupplier, defaultReadPrivilege, defaultWritePrivilege, "image/png,image/jpeg", filesConverter));
        return (FormFieldDefinitionBuilder<Object>)this;
    }

    public FormFieldDefinitionBuilder<Object> files(String fieldName, BiFunction<DtoAndEntity, SecureEntityDictionaryRepository, Object> datalistSupplier, String mimeType) {
        fields.add(lastField = createFormFieldDefinition(formName, fieldName, DATALIST_PREFIX + fieldName, files, datalistSupplier, defaultReadPrivilege, defaultWritePrivilege, mimeType, filesConverter));
        return (FormFieldDefinitionBuilder<Object>)this;
    }

    public FormFieldDefinitionBuilder<Object> image(String fieldName) {
        fields.add(lastField = createFormFieldDefinition(formName, fieldName, image, "", defaultReadPrivilege, defaultWritePrivilege, "image/png,image/jpeg", filesConverter));
        return (FormFieldDefinitionBuilder<Object>)this;
    }

    public FormFieldDefinitionBuilder<String> imageUrl(String fieldName) {
        fields.add(lastField = createFormFieldDefinition(formName, fieldName, image_url, defaultReadPrivilege, defaultWritePrivilege));
        return (FormFieldDefinitionBuilder<String>)this;
    }

    public FormFieldDefinitionBuilder<String> colorPicker(String fieldName) {
        fields.add(lastField = createFormFieldDefinition(formName, fieldName, color_picker, defaultReadPrivilege, defaultWritePrivilege));
        return (FormFieldDefinitionBuilder<String>)this;
    }

    public FormFieldDefinitionBuilder<Object> timePicker(String fieldName) {
        fields.add(lastField = createFormFieldDefinition(formName, fieldName, time, defaultReadPrivilege, defaultWritePrivilege));
        return (FormFieldDefinitionBuilder<Object>)this;
    }

    public FormFieldDefinitionBuilder<Object> ruleThen(String fieldName, BiFunction<DtoAndEntity, SecureEntityDictionaryRepository, Object> datalistSupplier, String url) {
        fields.add(lastField = createFormFieldDefinition(formName, fieldName,null, url, rule_then, datalistSupplier, defaultReadPrivilege, defaultWritePrivilege));
        return (FormFieldDefinitionBuilder<Object>)this;
    }

    public FormFieldDefinitionBuilder<Object> recaptcha() {
        fields.add(lastField = createFormFieldDefinition(formName, RECAPTCHA, recaptcha, defaultReadPrivilege, defaultWritePrivilege));
        return (FormFieldDefinitionBuilder<Object>)this;
    }

    //TODO: move to some better place
    Function filesConverter = new Function() {
        @Override
        public Object apply(Object o) {
            List<Long> result = new ArrayList<>();
            if (o == null) {return result;}
            if (o instanceof String) {
                result.add(Long.valueOf((String) o));
                return result;
            }
            if (o.getClass().isArray()) {
                String[] oa = (String[])o;
                for (String s : oa) {
                    result.add(Long.valueOf(s));
                }
                return result;
            }
            return result;
        }
    };
}