/*
MIT License

Copyright (c) 2016-2022, Codedose CDX Sp. z o.o. Sp. K. <stratoflow.com>

Permission is hereby granted, free of charge, to any person obtaining a copy of this software
 and associated documentation files (the "Software"), to deal in the Software without restriction, 
including without limitation the rights to use, copy, modify, merge, publish, distribute, 
sublicense, and/or sell copies of the Software, and to permit persons to whom the Software 
is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice 
shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, 
EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES 
OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE 
AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS 
OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES 
OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, 
TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION 
WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*/

package com.openkoda.uicomponent.editor;

import com.openkoda.uicomponent.dto.UIComponentControllerEndpointList;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import static com.openkoda.controller.common.URLConstants._HTML_ORGANIZATION_ORGANIZATIONID;
import static com.openkoda.controller.common.URLConstants._WEBENDPOINTS;
import static com.openkoda.core.controller.generic.AbstractController._HTML;


@RestController
@RequestMapping({_HTML_ORGANIZATION_ORGANIZATIONID + _WEBENDPOINTS, _HTML + _WEBENDPOINTS})
public class UIComponentControllerHtml extends AbstractUIComponentController {


    @PreAuthorize(CHECK_CAN_READ_FRONTEND_RESOURCES)
    @GetMapping({_ID_SETTINGS})
    public Object settings(
            @PathVariable(value = ORGANIZATIONID, required = false) Long organizationId,
            @PathVariable(value = ID, required = false) Long frontendResourceId) {
        debug("[settings] FrontendResourceId: {}", frontendResourceId);
        return findUIComponent(organizationId, frontendResourceId)
                .mav(WEBENDPOINTS + "-" + SETTINGS);
    }

    @PreAuthorize(CHECK_CAN_ACCESS_GLOBAL_SETTINGS)
    @GetMapping({_NEW_SETTINGS})
    public Object create(@PathVariable(value = ORGANIZATIONID, required = false) Long organizationId) {
        debug("[newSettings] FrontendResource");
        return newUIComponentForms(organizationId)
                .mav(WEBENDPOINTS + "-" + SETTINGS);
    }

    @PreAuthorize(CHECK_CAN_MANAGE_FRONTEND_RESOURCES)
    @PostMapping({_ID_SETTINGS})
    @ResponseBody
    public Object update(
            @PathVariable(value = ORGANIZATIONID, required = false) Long organizationId,
            UIComponentControllerEndpointList componentControllerEndpointList,
            @Valid UIComponentFrontendResourceForm frontendResourceForm,
            @PathVariable(value = ID, required = false) Long frontendResourceId,
            BindingResult br) {
        debug("[update] FrontendResourceId: {}", frontendResourceId);
        return updateUIComponent(organizationId, frontendResourceId, componentControllerEndpointList, frontendResourceForm, br)
                .mav(a -> true, a -> a.get(message));
    }

    @PreAuthorize(CHECK_CAN_MANAGE_FRONTEND_RESOURCES)
    @PostMapping({_ID_REMOVE})
    @ResponseBody
    public Object remove(
            @PathVariable(value = ID, required = true) Long frontendResourceId) {
        debug("[remove] FrontendResourceId: {}", frontendResourceId);
        return removeUIComponent(frontendResourceId)
                .mav(a -> true, a -> a.get(message));
    }

    @PreAuthorize(CHECK_CAN_MANAGE_FRONTEND_RESOURCES)
    @PostMapping({"/{uiComponentId}/controllerEndpoint" + _ID_REMOVE})
    @ResponseBody
    public Object removeControllerEndpoint(
            @PathVariable(value = "uiComponentId", required = true) Long uiComponentId,
            @PathVariable(value = ID, required = true) Long controllerEndpointId) {
        debug("[remove] UiComponentId: {}", controllerEndpointId);
        return removeControllerEndpointById(controllerEndpointId)
                .mav(a -> true, a -> a.get(message));
    }

    @PostMapping({_NEW_SETTINGS})
    @ResponseBody
    @PreAuthorize(CHECK_CAN_ACCESS_GLOBAL_SETTINGS)
    public Object saveNew(
            @PathVariable(value = ORGANIZATIONID, required = false) Long organizationId,
            UIComponentControllerEndpointList componentControllerEndpointList,
            @Valid UIComponentFrontendResourceForm frontendResourceForm,
            BindingResult br) {
        debug("[saveNew] FrontendResourceId");
        return saveNewUIComponent(organizationId, componentControllerEndpointList, frontendResourceForm, br)
                .mav(a -> a.get(uiComponentUrl), a -> a.get(message));
    }

    @PreAuthorize(CHECK_CAN_READ_FRONTEND_RESOURCES)
    @GetMapping(_ALL)
    public Object all(@Qualifier("obj") Pageable pageable) {
        debug("[all]");
        return allUIComponents(pageable)
                .mav(WEBENDPOINTS + "-" + ALL);
    }
}
