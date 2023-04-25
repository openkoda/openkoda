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

package com.openkoda.form;

import com.openkoda.controller.common.PageAttributes;
import com.openkoda.core.form.FrontendMappingDefinition;

import java.util.HashMap;
import java.util.Map;

public class TestModel {

    public static Map<String, FrontendMappingDefinition> forms = new HashMap<>();
    static {
        forms.put(PageAttributes.frontendResourceForm.name, FrontendMappingDefinitions.frontendResourceForm);
        forms.put(PageAttributes.organizationRelatedForm.name,FrontendMappingDefinitions.organizationForm);
        forms.put(PageAttributes.organizationForm.name, FrontendMappingDefinitions.organizationForm);
        forms.put(PageAttributes.schedulerForm.name, FrontendMappingDefinitions.schedulerForm);
        forms.put(PageAttributes.roleForm.name, FrontendMappingDefinitions.roleForm);
        forms.put(PageAttributes.eventListenerForm.name, FrontendMappingDefinitions.eventListenerForm);
        forms.put(PageAttributes.inviteUserForm.name, FrontendMappingDefinitions.inviteForm);
        forms.put(PageAttributes.eventListenerForm.name, FrontendMappingDefinitions.eventListenerForm);
        forms.put(PageAttributes.editUserForm.name, FrontendMappingDefinitions.editUserForm);
    }


}
