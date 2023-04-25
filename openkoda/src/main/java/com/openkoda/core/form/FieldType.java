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

/**
 * Provides list of available form fields
 * All of these have their representation in the HTML form generator
 *
 * @author Arkadiusz Drysch (adrysch@stratoflow.com)
 *
 */
public enum FieldType {

    //Direct single value:
    text, password, hidden,

    //date and time
    date, datetime, time,

    //Direct single boolean value:
    checkbox, checkbox_with_warning, switch_values, switch_values_with_warning,

    //Simple textarea
    textarea,

    //Code edition:
    code_html, code_css, code_js, code_with_autocomplete,

    //Special for organization selection:
    organization_select,

    datalist(false),
    //One value from dictionary:
    dropdown, dropdown_with_disable, radio_list, radio_list_no_label,

    //Many values from dictionary:
    checkbox_list,

    //Visual divider:
    divider,
    section_with_checkbox, section_with_checkbox_with_warning, section_with_switch, section_with_switch_content(false),

    //Buttons
    button,
    submit_to_new_tab(false),
    //

    number, map,

    document,
    rule_then, rule_then_else,

    image_url,

    //Files
    files_library, file_library,
    images_library, image_library,
    image,
    files,

    //One to Many component
    one_to_many,
    //
    color_picker,

    section_with_dropdown,

    recaptcha
    ;

    public boolean hasValue() {
        return hasValue;
    }
    public String getName() {
        return name();
    }

    FieldType(boolean hasValue) {
        this.hasValue = hasValue;
    }

    FieldType() {
        hasValue = true;
    }

    private boolean hasValue;

}
