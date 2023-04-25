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

package com.openkoda.uicomponent.dto;

import com.openkoda.core.helper.ResourcesHelper;
import com.openkoda.uicomponent.editor.UIComponentControllerEndpointDto;
import com.openkoda.uicomponent.editor.UIComponentControllerEndpointForm;

import java.util.ArrayList;
import java.util.List;

public class UIComponentControllerEndpointList {
    List<UIComponentControllerEndpointForm> uiComponentControllerEndpointFormList;

    public UIComponentControllerEndpointList() {
        this.uiComponentControllerEndpointFormList = new ArrayList<>();
    }

    public UIComponentControllerEndpointList(List<UIComponentControllerEndpointForm> uiComponentControllerEndpointFormList) {
        this.uiComponentControllerEndpointFormList = uiComponentControllerEndpointFormList;
    }

    public List<UIComponentControllerEndpointForm> getUiComponentControllerEndpointFormList() {
        return uiComponentControllerEndpointFormList;
    }

    public void setUiComponentControllerEndpointFormList(List<UIComponentControllerEndpointForm> uiComponentControllerEndpointFormList) {
        this.uiComponentControllerEndpointFormList = uiComponentControllerEndpointFormList;
    }

    public UIComponentControllerEndpointList populateAdditionalEmptyForm() {
        UIComponentControllerEndpointForm<UIComponentControllerEndpointDto> controllerEndpointForm = new UIComponentControllerEndpointForm<>();
        if(uiComponentControllerEndpointFormList.isEmpty()) {
//            set default code for initial endpoint
            controllerEndpointForm.dto.code = ResourcesHelper.getResourceAsStringOrEmpty("/default-code/default-code.js");
        }
        controllerEndpointForm.process();
        uiComponentControllerEndpointFormList.add(controllerEndpointForm);
        return this;
    }
}
