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

import com.openkoda.core.controller.generic.AbstractController;
import com.openkoda.core.flow.Flow;
import com.openkoda.core.flow.PageModelMap;
import com.openkoda.core.security.HasSecurityRules;
import com.openkoda.model.ControllerEndpoint;
import com.openkoda.model.FrontendResource;
import com.openkoda.uicomponent.JsFlowRunner;
import com.openkoda.uicomponent.dto.UIComponentControllerEndpointList;
import jakarta.inject.Inject;
import org.apache.commons.collections.ListUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Pageable;
import org.springframework.validation.BindingResult;

import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.stream.IntStream;


public class AbstractUIComponentController extends AbstractController implements HasSecurityRules {

    @Inject
    private JsFlowRunner jsFlowRunner;

    protected PageModelMap findUIComponent(Long organizationId, long frontendResourceId) {
        debug("[findFrontendResource] FrontendResourceId: {}", frontendResourceId);
        return Flow.init()
                .thenSet(frontendResourceEntity, a -> repositories.unsecure.frontendResource.findOne(frontendResourceId))
                .thenSet(uiComponentFrontendResourceForm, a -> new UIComponentFrontendResourceForm(organizationId, a.model.get(frontendResourceEntity)))
                .thenSet(controllerEndpoints, a -> repositories.unsecure.controllerEndpoint.findByFrontendResourceId(frontendResourceId))
                .thenSet(uiComponentControllerEndpointFormList, a -> new UIComponentControllerEndpointList(a.model.get(controllerEndpoints).stream()
                        .map(ce -> {
                            UIComponentControllerEndpointForm controllerEndpointForm = new UIComponentControllerEndpointForm(organizationId, ce);
                            controllerEndpointForm.process();
                            return controllerEndpointForm;
                        })
                        .collect(Collectors.toList())))
                .then(a -> a.model.get(uiComponentControllerEndpointFormList).populateAdditionalEmptyForm())
                .thenSet(uiComponentPreviewUrl, a -> services.url.getUiComponentPreviewUrl(organizationId, a.model.get(frontendResourceEntity).getUrlPath(), a.model.get(frontendResourceEntity).isPublic()))
                .execute();
    }

    protected PageModelMap updateUIComponent(Long organizationId,
                                             long frontendResourceId,
                                             UIComponentControllerEndpointList controllerEndpointList,
                                             UIComponentFrontendResourceForm frontendResourceForm,
                                             BindingResult br) {
        debug("[updateUIComponent] FrontendResourceId: {}", frontendResourceId);
        return Flow.init()
                .thenSet(frontendResourceEntity, a -> repositories.unsecure.frontendResource.findOne(frontendResourceId))
                .then(a -> ((UIComponentFrontendResourceDto) frontendResourceForm.dto).getUrlPath() != null ?
                        services.validation.validateAndPopulateToEntity(frontendResourceForm, br, a.result) : null)
                .then(a -> a.result != null ? repositories.unsecure.frontendResource.save(a.result) : null)
                .thenSet(controllerEndpoints, a -> repositories.unsecure.controllerEndpoint.findByFrontendResourceId(frontendResourceId))
                .then(a -> {
                    if(!controllerEndpointList.getUiComponentControllerEndpointFormList().isEmpty())  {
                        return IntStream.range(0, a.model.get(controllerEndpoints).size())
                        .mapToObj(i -> services.validation.validateAndPopulateToEntity(
                                controllerEndpointList.getUiComponentControllerEndpointFormList().get(i),
                                br,
                                a.model.get(controllerEndpoints).get(i)))
                        .collect(Collectors.toList());
                    }
                    return new ArrayList();
                })
                .then(a -> ListUtils.union(a.result, IntStream.range(a.model.get(controllerEndpoints).size(), controllerEndpointList.getUiComponentControllerEndpointFormList().size())
                        .mapToObj(i -> controllerEndpointList.getUiComponentControllerEndpointFormList().get(i))
                        .filter(ceForm -> StringUtils.isNotBlank(((UIComponentControllerEndpointDto)ceForm.dto).getCode()))
                        .map(ceForm -> services.validation.validateAndPopulateToEntity(
                                ceForm,
                                br,
                                new ControllerEndpoint(frontendResourceId, organizationId)))
                        .collect(Collectors.toList()))
                )
                .then(a -> a.result != null ? repositories.unsecure.controllerEndpoint.saveAll(a.result) : null)
                .execute();
    }

    protected PageModelMap newUIComponentForms(Long organizationId) {
        debug("[newUIComponentForms] FrontendResourceId");
        return Flow.init()
                .thenSet(controllerEndpoints, a -> new ArrayList<>())
                .thenSet(uiComponentFrontendResourceForm, a -> new UIComponentFrontendResourceForm(organizationId, null))
                .thenSet(uiComponentControllerEndpointFormList, a -> new UIComponentControllerEndpointList().populateAdditionalEmptyForm())
                .execute();
    }

    protected PageModelMap saveNewUIComponent(Long organizationId,
                                              UIComponentControllerEndpointList controllerEndpointList,
                                              UIComponentFrontendResourceForm frontendResourceForm,
                                              BindingResult br) {
        debug("[saveNewUIComponent] FrontendResourceId");
        return Flow.init()
                .then(a -> ((UIComponentFrontendResourceDto) frontendResourceForm.dto).getContent() != null ?
                        services.validation.validateAndPopulateToEntity(frontendResourceForm, br, new FrontendResource(organizationId)) : null)
                .thenSet(frontendResourceEntity, a -> a.result != null ? repositories.unsecure.frontendResource.save(a.result) : null)
                .then(a -> IntStream.range(0, controllerEndpointList.getUiComponentControllerEndpointFormList().size())
                                .mapToObj(i -> controllerEndpointList.getUiComponentControllerEndpointFormList().get(i))
                                .filter(ceForm -> StringUtils.isNotBlank(((UIComponentControllerEndpointDto)ceForm.dto).getCode()))
                                .map(ceForm -> services.validation.validateAndPopulateToEntity(
                                        ceForm,
                                        br,
                                        new ControllerEndpoint(a.result.getId(), organizationId)))
                                .collect(Collectors.toList()))
                .then(a -> a.result != null && !a.result.isEmpty() ? repositories.unsecure.controllerEndpoint.saveAll(a.result) : null)
                .thenSet(uiComponentUrl, a -> services.url.getUiComponentSettingsUrl(organizationId, a.model.get(frontendResourceEntity).getId()))
                .execute();
    }

    protected PageModelMap removeUIComponent(Long frontendResourceId) {
        return Flow.init()
                .then(a -> repositories.unsecure.controllerEndpoint.findByFrontendResourceId(frontendResourceId))
                .then(a -> {
                        repositories.unsecure.controllerEndpoint.deleteInBatch(a.result);
                        return true;
                    }
                )
                .then(a -> repositories.unsecure.frontendResource.deleteOne(frontendResourceId))
                .execute();
    }

    protected PageModelMap removeControllerEndpointById(Long controllerEndpointId) {
        return Flow.init()
                .then(a -> repositories.unsecure.controllerEndpoint.deleteOne(controllerEndpointId))
                .execute();
    }

    protected PageModelMap allUIComponents(Pageable pageable) {
        debug("[allUIComponents]");
        return Flow.init()
                .thenSet(frontendResourcePage, a -> repositories.unsecure.frontendResource.findByType(FrontendResource.Type.UI_COMPONENT, pageable))
                .execute();
    }


}
