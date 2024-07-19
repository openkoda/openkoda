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
import com.openkoda.model.OpenkodaModule;
import com.openkoda.model.Organization;
import com.openkoda.model.PrivilegeBase;
import com.openkoda.repository.SecureEntityDictionaryRepository;
import com.openkoda.uicomponent.annotation.Autocomplete;
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
    public static final String DATALIST_PREFIX = "__datalist_";
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
    @Autocomplete(doc = """
            Create list of values which can be later used to populate e.g. dropdowns. (Presentation layer impact only). Examples:
            <br/>Simple data list with fixed values:<br/>
            <code>
            .datalist("weekendDays", d => d.toLinkedMap(["Saturday","Sunday"]))
            .dropdown("nonWorking", "weekendDays")
            </code>
            <br/>Simple data list with fixed values:<br/>
            <code>
            .datalist("workingDays", a.services.data.getRepository("weekDays","ALL"))
            .dropdown("working", "workingDays")
            </code>
            """)
    public FormFieldDefinitionBuilder<Object> datalist(String datalistId, BiFunction<DtoAndEntity, SecureEntityDictionaryRepository, Object> datalistSupplier) {
        fields.add(lastField = createFormFieldDefinition(formName, DATALIST_PREFIX + datalistId, datalistId, datalist, datalistSupplier, defaultReadPrivilege, defaultWritePrivilege));
        return (FormFieldDefinitionBuilder<Object>)this;
    }
    @Autocomplete(doc = """
            Create list of values which can be later used to populate e.g. dropdowns. (Presentation layer impact only). Examples:
            <br/>Simple data list with fixed values:<br/>
            <code>
            .datalist("weekendDays", d => d.toLinkedMap(["Saturday","Sunday"]))
            .dropdown("nonWorking", "weekendDays")
            </code>
            <br/>Simple data list with fixed values:<br/>
            <code>
            .datalist("workingDays", a.services.data.getRepository("weekDays","ALL"))
            .dropdown("working", "workingDays")
            <code>
            """)
    public FormFieldDefinitionBuilder<Object> datalist(String datalistId, Function<SecureEntityDictionaryRepository, Object> datalistSupplier) {
        fields.add(lastField = createFormFieldDefinition(formName, DATALIST_PREFIX + datalistId, datalistId, datalist, datalistSupplier, defaultReadPrivilege, defaultWritePrivilege));
        return (FormFieldDefinitionBuilder<Object>)this;
    }
    @Autocomplete(doc = """
            Create string column in the database and add simple text input to the form. Examples:
            <br/>Simple text input both in the form and in the table:<br/>
            <code>
            .text("firstName")
            </code>
            <br/>Simple text calculated from sql formula:<br/>
            <code>
            .checkbox("username").sqlFormula("select first_name ||' '|| last_name from customer")
            </code>
            """)
    public FormFieldDefinitionBuilder<String> text(String fieldName) {
        fields.add(lastField = createFormFieldDefinition(formName, fieldName, text, defaultReadPrivilege, defaultWritePrivilege));
        return (FormFieldDefinitionBuilder<String>)this;
    }
    @Autocomplete(doc = """
            Create string column in the database and add textarea input to the form. Examples:
            <br/>Simple textarea input both in the form and in the table:<br/>
            <code>
            .textarea("notes")
            </code>
            """)
    public FormFieldDefinitionBuilder<String> textarea(String fieldName) {
        fields.add(lastField = createFormFieldDefinition(formName, fieldName, textarea, defaultReadPrivilege, defaultWritePrivilege));
        return (FormFieldDefinitionBuilder<String>)this;
    }
    @Autocomplete(doc = """
            Create boolean column in the database and add checkbox input to the form. Examples:
            <br/>Simple checkbox both in the form and in the table:<br/>
            <code>
            .checkbox("flag")
            </code>
            <br/>Simple checkbox calculated from sql formula:<br/>
            <code>
            .checkbox("flag").sqlFormula("true")
            </code>
            """)
    public FormFieldDefinitionBuilder<Boolean> checkbox(String fieldName) {
        fields.add(lastField = createFormFieldDefinition(formName, fieldName, checkbox, defaultReadPrivilege, defaultWritePrivilege));
        return (FormFieldDefinitionBuilder<Boolean>)this;
    }
    @Autocomplete(doc = """
            Create date with time column in the database and add date time picker input to the form. Examples:
            <br/>Simple datetime both in the form and in the table:<br/>
            <code>
            .datetime("busArrivalDateTime")
            </code>
            <br/>Simple datetime calculated from sql formula:<br/>
            <code>
            .datetime("currentTime").sqlFormula("select CURRENT_TIME")
            </code>
            """)
    public FormFieldDefinitionBuilder<LocalDateTime> datetime(String fieldName) {
        fields.add(lastField = createFormFieldDefinition(formName, fieldName, datetime, defaultReadPrivilege, defaultWritePrivilege));
        return (FormFieldDefinitionBuilder<LocalDateTime>)this;
    }
    @Autocomplete(doc = """
            Create date column in the database and add date picker input to the form. Examples:
            <br/>Simple date both in the form and in the table:<br/>
            <code>
            .date("busArrivalDate")
            </code>
            <br/>Simple date calculated from sql formula:<br/>
            <code>
            .date("currentDate").sqlFormula("select CURRENT_DATE")
            </code>
            """)
    public FormFieldDefinitionBuilder<LocalDate> date(String fieldName) {
        fields.add(lastField = createFormFieldDefinition(formName, fieldName, date, defaultReadPrivilege, defaultWritePrivilege));
        return (FormFieldDefinitionBuilder<LocalDate>)this;
    }
    @Autocomplete(doc = """
            Create numeric column in the database and add numeric input to the form. Examples:
            <br/>Simple numeric both in the form and in the table:<br/>
            <code>
            .number(455)
            </code>
            <br/>Simple numeric calculated from sql formula:<br/>
            <code>
            .number("quantity").sqlFormula("select count(*) from products")
            </code>
            """)
    public FormFieldDefinitionBuilder<Number> number(String fieldName) {
        fields.add(lastField = createFormFieldDefinition(formName, fieldName, number, defaultReadPrivilege, defaultWritePrivilege));
        return (FormFieldDefinitionBuilder<Number>)this;
    }
    @Autocomplete(doc = "Create non null string column in the database and select input with required value on presentation layer." +
            "This action may be preceded by appropriate data list creation. See also 'datalist'")
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
    @Autocomplete(doc = "Create nullable string column in the database and select input with optional value on presentation layer." +
            "This action may be preceded by appropriate data list creation. See also 'datalist'")
    public FormFieldDefinitionBuilder<String> dropdown(String fieldName, String datalistId, boolean allowNull) {
        fields.add(lastField = createFormFieldDefinition(formName, fieldName, datalistId, allowNull, dropdown, defaultReadPrivilege, defaultWritePrivilege));
        return (FormFieldDefinitionBuilder<String>)this;
    }
    @Autocomplete(doc = "Create non null string column in the database and select input with required value on presentation layer.")
    public FormFieldDefinitionBuilder<String> dropdown(String fieldName, BiFunction<DtoAndEntity, SecureEntityDictionaryRepository, Object> datalistSupplier) {
        fields.add(lastField = createFormFieldDefinition(formName, fieldName, null, dropdown, datalistSupplier, defaultReadPrivilege, defaultWritePrivilege));
        return (FormFieldDefinitionBuilder<String>)this;
    }
    @Autocomplete(doc = "Create nullable string column in the database and select input with optional value on presentation layer." +
            "This action may be preceded by appropriate data list creation. See also 'datalist'")
    public FormFieldDefinitionBuilder<String> dropdown(String fieldName, String datalistId, Boolean allowNull) {
        fields.add(lastField = createFormFieldDefinition(formName, fieldName, datalistId, allowNull, dropdown, defaultReadPrivilege, defaultWritePrivilege));
        return (FormFieldDefinitionBuilder<String>)this;
    }
    @Autocomplete(doc = "Create non null string column in the database and select input with required value by default disabled on presentation layer." +
            "This action may be preceded by appropriate data list creation. See also 'datalist'")
    public FormFieldDefinitionBuilder<String> dropdownWithDisable(String fieldName, String datalistId) {
        fields.add(lastField = createFormFieldDefinition(formName, fieldName, datalistId, dropdown_with_disable, defaultReadPrivilege, defaultWritePrivilege));
        return (FormFieldDefinitionBuilder<String>)this;
    }
    @Autocomplete(doc = "Create non null string column in the database and select input with required value by default disabled on presentation layer.")
    public FormFieldDefinitionBuilder<String> dropdownWithDisable(String fieldName, BiFunction<DtoAndEntity, SecureEntityDictionaryRepository, Object> datalistSupplier) {
        fields.add(lastField = createFormFieldDefinition(formName, fieldName, null, dropdown_with_disable, datalistSupplier, defaultReadPrivilege, defaultWritePrivilege));
        return (FormFieldDefinitionBuilder<String>)this;
    }
//    Dropdown element for section. Section fields to show/hide are selected by the matching css class, same as set for section_with_dropdown.
    @Autocomplete(doc = "Create non null string column in the database and select input with optional value on presentation layer." +
            "This action may be preceded by appropriate data list creation. See also 'datalist'" +
            "Section fields to show or hide on the basis of dropdown value are selected by their matching additional css class")
    public FormFieldDefinitionBuilder<String> sectionWithDropdown(String fieldName, String datalistId) {
        fields.add(lastField = createFormFieldDefinition(formName, fieldName, datalistId, true, section_with_dropdown, defaultReadPrivilege, defaultWritePrivilege));
        return (FormFieldDefinitionBuilder<String>)this;
    }
    @Autocomplete(doc = "Create non null string column in the database and select input with optional value on presentation layer." +
            "Section fields to show or hide on the basis of dropdown value are selected by their matching additional css class")
    public FormFieldDefinitionBuilder<String> sectionWithDropdown(String fieldName, BiFunction<DtoAndEntity, SecureEntityDictionaryRepository, Object> datalistSupplier) {
        fields.add(lastField = createFormFieldDefinition(formName, fieldName, null, true, section_with_dropdown, null, datalistSupplier, defaultReadPrivilege, defaultWritePrivilege, null));
        return (FormFieldDefinitionBuilder<String>)this;
    }
    @Autocomplete(doc = "Create non null string column in the database and select input on presentation layer. Use when there is no dto available for this form object." +
            "This action may be preceded by appropriate data list creation. See also 'datalist'")
    public FormFieldDefinitionBuilder<String> dropdownNonDto(String fieldName, String datalistId) {
        fields.add(lastField = createNonDtoFormFieldDefinition(formName, fieldName, datalistId, dropdown, defaultReadPrivilege, defaultWritePrivilege));
        return (FormFieldDefinitionBuilder<String>)this;
    }
    @Autocomplete(doc = "Create checkbox list element. (only on presentation layer) " +
            "Provide list values as a second argument (BiFunction datalistSupplier). ")
    public FormFieldDefinitionBuilder<String> checkboxList(String fieldName, BiFunction<DtoAndEntity, SecureEntityDictionaryRepository, Object> datalistSupplier) {
        fields.add(lastField = createFormFieldDefinition(formName, fieldName, fieldName, checkbox_list, datalistSupplier, defaultReadPrivilege, defaultWritePrivilege));
        return (FormFieldDefinitionBuilder<String>)this;
    }
    @Autocomplete(doc = "Create checkbox list element. (only on presentation layer) " +
            "This action may be preceded by appropriate data list creation. See also 'datalist'")
    public FormFieldDefinitionBuilder<String> checkboxList(String fieldName, String datalistId) {
        fields.add(lastField = createFormFieldDefinition(formName, fieldName, datalistId, checkbox_list, defaultReadPrivilege, defaultWritePrivilege));
        return (FormFieldDefinitionBuilder<String>)this;
    }

    @Autocomplete(doc = "Create checkbox list element. Checkboxes are grouped into columns by their 'category' property. (only on presentation layer) " +
            "This action may be preceded by appropriate data list creation. See also 'datalist'")
    public FormFieldDefinitionBuilder<String> checkboxListGrouped(String fieldName, String datalistId) {
        fields.add(lastField = createFormFieldDefinition(formName, fieldName, datalistId, checkbox_list_grouped, defaultReadPrivilege, defaultWritePrivilege));
        return (FormFieldDefinitionBuilder<String>)this;
    }

    @Autocomplete(doc = "Create non null string column in the database and multiselect dropdown on presentation layer. " +
            "Selected values are stored in the database as a comma-separated string." +
            "This action may be preceded by appropriate data list creation. See also 'datalist'")
    public FormFieldDefinitionBuilder<String> multiselect(String fieldName, String datalistId) {
        fields.add(lastField = createFormFieldDefinition(formName, fieldName, datalistId, multiselect, defaultReadPrivilege, defaultWritePrivilege));
        return (FormFieldDefinitionBuilder<String>)this;
    }

    @Autocomplete(doc = "Create nullable numeric column in the database and a dropdown element on the presentation layer." + 
            "Values available in the dropdown are loaded from the database as the referenced entity key table records. ")
    public FormFieldDefinitionBuilder<Long> manyToOne(String fieldName, String referencedEntityKey) {
        String datalistId = fieldName + "_" + referencedEntityKey;
        datalist(datalistId, d -> d.dictionary(referencedEntityKey));
        fields.add(lastField = createFormFieldDefinition(formName, fieldName, datalistId, referencedEntityKey,true, many_to_one, defaultReadPrivilege, defaultWritePrivilege));
        return (FormFieldDefinitionBuilder<Long>)this;
    }

    @Autocomplete(doc = "Create nullable numeric reference column in the database and select input populated with organization IDs on presentation layer.")
    public FormFieldDefinitionBuilder<Long> organizationSelect(String fieldName) {
        datalist("organizations", d -> d.dictionary(Organization.class));
        fields.add(lastField = createFormFieldDefinition(formName, fieldName, "organizations", true, organization_select, defaultReadPrivilege, defaultWritePrivilege));
        return (FormFieldDefinitionBuilder<Long>)this;
    }
    @Autocomplete(doc = "Create nullable numeric reference column in the database and select input populated with Openkoda Modules IDs on presentation layer.")
    public FormFieldDefinitionBuilder<Long> moduleSelect(String fieldName) {
        datalist("modules", d -> d.dictionary(OpenkodaModule.class));
        fields.add(lastField = createFormFieldDefinition(formName, fieldName, "modules", true, module_select, defaultReadPrivilege, defaultWritePrivilege));
        return (FormFieldDefinitionBuilder<Long>)this;
    }
    @Autocomplete(doc = "Create radio elements list (only on presentation layer)." +
            "This action may be preceded by appropriate data list creation. See also 'datalist'")
    public FormFieldDefinitionBuilder<Object> radioList(String fieldName, String datalistId) {
        fields.add(lastField = createFormFieldDefinition(formName, fieldName, datalistId, radio_list, defaultReadPrivilege, defaultWritePrivilege));
        return (FormFieldDefinitionBuilder<Object>)this;
    }
    @Autocomplete(doc = "Create radio elements list without the preceding default label element (only on presentation layer)." +
            "Provide list values as a second argument (BiFunction datalistSupplier). ")
    public FormFieldDefinitionBuilder<Object> radioListNoLabel(String fieldName, BiFunction<DtoAndEntity, SecureEntityDictionaryRepository, Object> datalistSupplier) {
        fields.add(lastField = createFormFieldDefinition(formName, fieldName, null, radio_list_no_label, null, datalistSupplier, defaultReadPrivilege, defaultWritePrivilege));
        return (FormFieldDefinitionBuilder<Object>)this;
    }
    @Autocomplete(doc = "Create radio elements list without the preceding default label element (only on presentation layer)." +
            "This action may be preceded by appropriate data list creation. See also 'datalist'")
    public FormFieldDefinitionBuilder<Object> radioListNoLabel(String fieldName, String dataListId) {
        fields.add(lastField = createFormFieldDefinition(formName, fieldName, dataListId, radio_list_no_label, defaultReadPrivilege, defaultWritePrivilege));
        return (FormFieldDefinitionBuilder<Object>)this;
    }
    @Autocomplete(doc = "Create custom field, providing its type with Function<Object,FieldType> as a second argument (only on presentation layer).")
    public FormFieldDefinitionBuilder<Object> customFieldType(String fieldName, Function<Object, FieldType> fieldTypeFunction) {
        fields.add(lastField = createFormFieldDefinition(formName, fieldName, fieldTypeFunction, defaultReadPrivilege, defaultWritePrivilege));
        return (FormFieldDefinitionBuilder<Object>)this;
    }
    @Autocomplete(doc = "Create divider element only on presentation layer.")
    public FormFieldDefinitionBuilder<Object> divider(String fieldName) {
        fields.add(lastField = createFormFieldDefinition(formName, fieldName, divider, defaultReadPrivilege, defaultWritePrivilege));
        return (FormFieldDefinitionBuilder<Object>)this;
    }
    @Autocomplete(doc = "Create non null long string column in the database and a CSS code editor element on presentation layer.")
    public FormFieldDefinitionBuilder<String> codeCss(String fieldName) {
        fields.add(lastField = createFormFieldDefinition(formName, fieldName, code_css, defaultReadPrivilege, defaultWritePrivilege));
        return (FormFieldDefinitionBuilder<String>)this;
    }
    @Autocomplete(doc = "Create non null long string column in the database and a HTML code editor element on presentation layer.")
    public FormFieldDefinitionBuilder<String> codeHtml(String fieldName) {
        fields.add(lastField = createFormFieldDefinition(formName, fieldName, code_html, defaultReadPrivilege, defaultWritePrivilege));
        return (FormFieldDefinitionBuilder<String>)this;
    }
    @Autocomplete(doc = "Create non null long string column in the database and a JS code editor element on presentation layer.")
    public FormFieldDefinitionBuilder<String> codeJs(String fieldName) {
        fields.add(lastField = createFormFieldDefinition(formName, fieldName, code_js, defaultReadPrivilege, defaultWritePrivilege));
        return (FormFieldDefinitionBuilder<String>)this;
    }
    @Autocomplete(doc = "Create non null long string column in the database and a code editor element with WebEndpoint specific autocomplete functionality on presentation layer.")
    public FormFieldDefinitionBuilder<String> codeWithWebendpointAutocomplete(String fieldName) {
        fields.add(lastField = createFormFieldDefinition(formName, fieldName, code_with_webendpoint_autocomplete, defaultReadPrivilege, defaultWritePrivilege));
        return (FormFieldDefinitionBuilder<String>)this;
    }
    @Autocomplete(doc = "Create non null long string column in the database and a code editor element with autocomplete functionality on presentation layer.")
    public FormFieldDefinitionBuilder<String> codeWithFormAutocomplete(String fieldName) {
        fields.add(lastField = createFormFieldDefinition(formName, fieldName, code_with_form_autocomplete, defaultReadPrivilege, defaultWritePrivilege));
        return (FormFieldDefinitionBuilder<String>)this;
    }
    @Autocomplete(doc = "Create non null string column in the database and an input of type hidden on presentation layer.")
    public FormFieldDefinitionBuilder<String> hidden(String fieldName) {
        fields.add(lastField = createFormFieldDefinition(formName, fieldName, hidden, defaultReadPrivilege, defaultWritePrivilege));
        return (FormFieldDefinitionBuilder<String>)this;
    }
    @Autocomplete(doc = "Create non null boolean column in the database and switch element on presentation layer.")
    public FormFieldDefinitionBuilder<Object> switchValues(String fieldName) {
        fields.add(lastField = createFormFieldDefinition(formName, fieldName, switch_values, defaultReadPrivilege, defaultWritePrivilege));
        return (FormFieldDefinitionBuilder<Object>)this;
    }
    @Autocomplete(doc = "Create switch element which switches visibility of section elements marked by this fieldName as a css class (only on presentation layer)." +
            "Switching on will trigger the warning in a form of a JS alert.")
    public FormFieldDefinitionBuilder<Object> switchValuesWithWarning(String fieldName) {
        fields.add(lastField = createFormFieldDefinition(formName, fieldName, switch_values_with_warning, defaultReadPrivilege, defaultWritePrivilege));
        return (FormFieldDefinitionBuilder<Object>)this;
    }
    @Autocomplete(doc = "Create checkbox element which switches visibility of section elements marked by this fieldName as a css class (only on presentation layer)." +
            "Selecting the checkbox will trigger the warning in a form of a JS alert.")
    public FormFieldDefinitionBuilder<Object> sectionWithCheckboxWithWarning(String fieldName) {
        fields.add(lastField = createFormFieldDefinition(formName, fieldName, section_with_checkbox_with_warning, defaultReadPrivilege, defaultWritePrivilege));
        return (FormFieldDefinitionBuilder<Object>)this;
    }
    @Autocomplete(doc = "Create link element which switches visibility of section elements marked by this fieldName as a css class (only on presentation layer).")
    public FormFieldDefinitionBuilder<Object> sectionWithLink(String fieldName) {
        fields.add(lastField = createFormFieldDefinition(formName, fieldName, section_with_link, defaultReadPrivilege, defaultWritePrivilege));
        return (FormFieldDefinitionBuilder<Object>)this;
    }
    @Autocomplete(doc = "Create checkbox element which switches visibility of section elements marked by this fieldName as a css class (only on presentation layer).")
    public FormFieldDefinitionBuilder<Object> sectionWithCheckbox(String fieldName) {
        fields.add(lastField = createFormFieldDefinition(formName, fieldName, section_with_checkbox, defaultReadPrivilege, defaultWritePrivilege));
        return (FormFieldDefinitionBuilder<Object>)this;
    }
    @Autocomplete(doc = "Create switch element which switches visibility of section elements marked by this fieldName as a css class (only on presentation layer).")
    public FormFieldDefinitionBuilder<Object> sectionWithSwitch(String fieldName) {
        fields.add(lastField = createFormFieldDefinition(formName, fieldName, section_with_switch, defaultReadPrivilege, defaultWritePrivilege));
        return (FormFieldDefinitionBuilder<Object>)this;
    }
    @Autocomplete(doc = "Create switch element which shows/hides other elements marked by additional css class (only on presentation layer).")
    public FormFieldDefinitionBuilder<Object> sectionWithSwitchContent(String fieldName) {
        fields.add(lastField = createFormFieldDefinition(formName, fieldName, section_with_switch_content, defaultReadPrivilege, defaultWritePrivilege));
        return (FormFieldDefinitionBuilder<Object>)this;
    }
    @Autocomplete(doc = "Create non null string column in the database and password input element on presentation layer.")
    public FormFieldDefinitionBuilder<String> password(String fieldName) {
        fields.add(lastField = createFormFieldDefinition(formName, fieldName, password, defaultReadPrivilege, defaultWritePrivilege));
        return (FormFieldDefinitionBuilder<String>)this;
    }
    @Autocomplete(doc = "Create submit to new tab button only on presentation layer.")
    public FormFieldDefinitionBuilder<Object> submitToNewTab(String fieldName, String url) {
        fields.add(lastField = createFormFieldDefinition(formName, fieldName, submit_to_new_tab, defaultReadPrivilege, defaultWritePrivilege, url));
        return (FormFieldDefinitionBuilder<Object>)this;
    }
    @Autocomplete(doc = "Create non null string column in the database and map element on presentation layer.")
    public FormFieldDefinitionBuilder<Object> map(String fieldName) {
        fields.add(lastField = createFormFieldDefinition(formName, fieldName, map, defaultReadPrivilege, defaultWritePrivilege));
        return (FormFieldDefinitionBuilder<Object>) this;
    }

    @Autocomplete(doc = "Create images library element only on presentation layer.")
    public FormFieldDefinitionBuilder<Object> imagesLibrary(String fieldName, BiFunction<DtoAndEntity, SecureEntityDictionaryRepository, Object> datalistSupplier) {
        fields.add(lastField = createFormFieldDefinition(formName, fieldName, fieldName, files_library, datalistSupplier, defaultReadPrivilege, defaultWritePrivilege, "image/png,image/jpeg", filesConverter));
        return (FormFieldDefinitionBuilder<Object>)this;
    }
    @Autocomplete(doc = "Create image library element only on presentation layer.")
    public FormFieldDefinitionBuilder<Object> imageLibrary(String fieldName, BiFunction<DtoAndEntity, SecureEntityDictionaryRepository, Object> datalistSupplier) {
        fields.add(lastField = createFormFieldDefinition(formName, fieldName, fieldName, file_library, datalistSupplier, defaultReadPrivilege, defaultWritePrivilege, "image/png,image/jpeg", filesConverter));
        return (FormFieldDefinitionBuilder<Object>)this;
    }
    @Autocomplete(doc = "Create non null string column in the database to store comma-separated list of file IDs and a file gallery with upload section on presentation layer.")
    public FormFieldDefinitionBuilder<Object> files(String fieldName, BiFunction<DtoAndEntity, SecureEntityDictionaryRepository, Object> datalistSupplier, String mimeType) {
        fields.add(lastField = createFormFieldDefinition(formName, fieldName, fieldName, files, datalistSupplier, defaultReadPrivilege, defaultWritePrivilege, mimeType, filesConverter));
        return (FormFieldDefinitionBuilder<Object>)this;
    }
    @Autocomplete(doc = "Create single image selector element (only on presentation layer).")
    public FormFieldDefinitionBuilder<Object> image(String fieldName) {
        fields.add(lastField = createFormFieldDefinition(formName, fieldName, image, "", defaultReadPrivilege, defaultWritePrivilege, "image/png,image/jpeg", filesConverter));
        return (FormFieldDefinitionBuilder<Object>)this;
    }
    @Autocomplete(doc = "Create non null string column in the database and simple text input element on presentation layer.")
    public FormFieldDefinitionBuilder<String> imageUrl(String fieldName) {
        fields.add(lastField = createFormFieldDefinition(formName, fieldName, image_url, defaultReadPrivilege, defaultWritePrivilege));
        return (FormFieldDefinitionBuilder<String>)this;
    }
    @Autocomplete(doc = "Create non null string column in the database and color picker element on presentation layer.")
    public FormFieldDefinitionBuilder<String> colorPicker(String fieldName) {
        fields.add(lastField = createFormFieldDefinition(formName, fieldName, color_picker, defaultReadPrivilege, defaultWritePrivilege));
        return (FormFieldDefinitionBuilder<String>)this;
    }
    @Autocomplete(doc = "Create non null timestamp with timezone column in the database and time picker element on presentation layer.")
    public FormFieldDefinitionBuilder<Object> timePicker(String fieldName) {
        fields.add(lastField = createFormFieldDefinition(formName, fieldName, time, defaultReadPrivilege, defaultWritePrivilege));
        return (FormFieldDefinitionBuilder<Object>)this;
    }
    @Autocomplete(doc = "Create 'then' rule configuration element (presentation layer only).")
    public FormFieldDefinitionBuilder<Object> ruleThen(String fieldName, BiFunction<DtoAndEntity, SecureEntityDictionaryRepository, Object> datalistSupplier, String url) {
        fields.add(lastField = createFormFieldDefinition(formName, fieldName,null, url, rule_then, datalistSupplier, defaultReadPrivilege, defaultWritePrivilege));
        return (FormFieldDefinitionBuilder<Object>)this;
    }
    @Autocomplete(doc = "Create recaptcha element only on presentation layer.")
    public FormFieldDefinitionBuilder<Object> recaptcha() {
        fields.add(lastField = createFormFieldDefinition(formName, RECAPTCHA, recaptcha, defaultReadPrivilege, defaultWritePrivilege));
        return (FormFieldDefinitionBuilder<Object>)this;
    }
    @Autocomplete(doc = "Create div only on presentation layer.")
    public FormFieldDefinitionBuilder<Object> div(String fieldName) {
        fields.add(lastField = createFormFieldDefinition(formName, fieldName, div,  defaultReadPrivilege, defaultWritePrivilege));
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