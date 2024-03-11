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

/**
 * Provides list of available form fields
 * All of these have their representation in the HTML form generator
 *
 * @author Arkadiusz Drysch (adrysch@stratoflow.com)
 *
 */
public enum FieldType {

    //Direct single value:
    text(FieldDbType.VARCHAR_255),
    password(FieldDbType.VARCHAR_255),
    hidden(FieldDbType.BIGINT),

    //date and time
    date(FieldDbType.DATE),
    datetime(FieldDbType.TIMESTAMP_W_TZ),
    time(FieldDbType.TIME_W_TZ),

    //Direct single boolean value:
    checkbox(FieldDbType.BOOLEAN),
    checkbox_with_warning(FieldDbType.BOOLEAN),
    switch_values(FieldDbType.BOOLEAN),
    switch_values_with_warning(FieldDbType.BOOLEAN),

    //Simple textarea
    textarea(FieldDbType.VARCHAR_1000),

    //Code edition:
    code_html(FieldDbType.VARCHAR_262144),
    code_css(FieldDbType.VARCHAR_262144),
    code_js(FieldDbType.VARCHAR_262144),
    code_with_webendpoint_autocomplete(FieldDbType.VARCHAR_262144),
    code_with_form_autocomplete(FieldDbType.VARCHAR_262144),

    //foreign key reference
    many_to_one(FieldDbType.BIGINT),
    //Special for organization selection:
    organization_select(FieldDbType.BIGINT),
    module_select(FieldDbType.VARCHAR_255),

    datalist(false),
    //One value from dictionary:
    dropdown(FieldDbType.VARCHAR_255),
    dropdown_with_disable,
    radio_list,
    radio_list_no_label,
    dropdown_with_entities,

    //Many values from dictionary:
    checkbox_list,
    checkbox_list_grouped,

    //Visual divider:
    divider,
    section_with_checkbox,
    section_with_checkbox_with_warning,
    section_with_switch,
    section_with_switch_content(false),

    //Buttons
    button,
    submit_to_new_tab(false),
    //

    number(FieldDbType.NUMERIC),
    map,

    document,
    rule_then, rule_then_else,

    image_url(FieldDbType.VARCHAR_255),

    //Files
    files_library, file_library,
    images_library, image_library,
    image,
    files(FieldDbType.VARCHAR_255),

    //One to Many component
    one_to_many,
    //
    color_picker(FieldDbType.VARCHAR_255),

    section_with_dropdown,

    recaptcha,
    div
    ;

    public boolean hasValue() {
        return hasValue;
    }
    public String getName() {
        return name();
    }

    public FieldDbType getDbType() {
        return dbType;
    }


    FieldType(boolean hasValue, FieldDbType dbType) {
        this.hasValue = hasValue;
        this.dbType = dbType;
    }

    FieldType(FieldDbType dbType) {
        this.hasValue=true;
        this.dbType = dbType;
    }

    FieldType(boolean hasValue) {
        this.hasValue = hasValue;
        this.dbType = null;
    }

    FieldType() {
        this.hasValue = true;
        this.dbType = null;
    }

    private boolean hasValue;
    private FieldDbType dbType;

}
