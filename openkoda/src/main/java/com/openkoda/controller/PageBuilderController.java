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

package com.openkoda.controller;

import com.openkoda.core.controller.generic.AbstractController;
import com.openkoda.core.flow.Flow;
import com.openkoda.core.flow.Tuple;
import com.openkoda.core.form.CRUDControllerConfiguration;
import com.openkoda.core.security.HasSecurityRules;
import com.openkoda.form.PageBuilderForm;
import com.openkoda.model.component.FrontendResource;
import com.openkoda.model.file.File;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.openkoda.controller.common.URLConstants.*;

@Controller
@RequestMapping({_HTML_ORGANIZATION_ORGANIZATIONID + _PAGEBUILDER, _HTML + _PAGEBUILDER})
public class PageBuilderController extends AbstractController implements HasSecurityRules {

    @Inject
    HtmlCRUDControllerConfigurationMap crudControllerConfigurationMap;

    public record EmbeddableComponents(List<Tuple> frontendResources, List<Tuple> uiComponents, Set<Map.Entry<String, CRUDControllerConfiguration>> tables, List<File> images){};

    @GetMapping({_NEW_SETTINGS})
    @ResponseBody
    @PreAuthorize(CHECK_CAN_ACCESS_GLOBAL_SETTINGS)
    public Object newPage(@PathVariable(value = ORGANIZATIONID, required = false) Long organizationId) {
        return Flow.init()
            .thenSet(embeddableComponents, a -> new EmbeddableComponents(
                    repositories.unsecure.frontendResource.findAllEmbeddableResources(),
                    repositories.unsecure.frontendResource.findAllEmbeddableUiComponents(),
                    crudControllerConfigurationMap.getExposed(),
                    repositories.unsecure.file.findByContentTypeStartsWith("image/")
            ))
            .thenSet(pageBuilderForm, a -> new PageBuilderForm(organizationId, null))
            .execute().mav("page/builder");
    }


    @GetMapping({_ID_SETTINGS})
    @ResponseBody
    @PreAuthorize(CHECK_CAN_ACCESS_GLOBAL_SETTINGS)
    public Object edit(@PathVariable(value = ORGANIZATIONID, required = false) Long organizationId,
                       @PathVariable("id") Long id) {
        return Flow.init()
            .thenSet(embeddableComponents, a -> new EmbeddableComponents(
                    repositories.unsecure.frontendResource.findAllEmbeddableResources(),
                    repositories.unsecure.frontendResource.findAllEmbeddableUiComponents(),
                    crudControllerConfigurationMap.getExposed(),
                    repositories.unsecure.file.findByContentTypeStartsWith("image/")
            ))
            .thenSet(frontendResourceEntity, a -> repositories.secure.frontendResource.findOne(id))
            .thenSet(pageBuilderForm, a -> new PageBuilderForm(organizationId, a.result))
            .execute().mav("page/builder");
    }
    @GetMapping(_ID + "/view")
    public Object view(@PathVariable(value = ORGANIZATIONID, required = false) Long organizationId,
                       @PathVariable("id") Long id) {
        return Flow.init()
                .thenSet(frontendResourceEntity, a -> repositories.unsecure.frontendResource.findDashboardDefinition(id))
            .execute().mav("page/view");
    }

    @PostMapping({_NEW_SETTINGS})
    @ResponseBody
    @PreAuthorize(CHECK_CAN_ACCESS_GLOBAL_SETTINGS)
    public Object saveNew(@PathVariable(value = ORGANIZATIONID, required = false) Long organizationId,
                          @Valid PageBuilderForm form,
                          BindingResult br) {
        FrontendResource fr = Flow.init()
                .then(a -> new FrontendResource(organizationId))
                .then(a -> services.validation.validateAndPopulateToEntity(form, br, a.result))
                .thenSet(frontendResourceEntity, a -> repositories.secure.frontendResource.save(a.result))
                .thenSet(pageBuilderForm, a -> new PageBuilderForm(organizationId, a.result))
                .execute().get(frontendResourceEntity);
        return new RedirectView(_HTML + _PAGEBUILDER + "/" + fr.getId() + _SETTINGS);
    }
    @PostMapping({_ID_SETTINGS})
    @ResponseBody
    @PreAuthorize(CHECK_CAN_ACCESS_GLOBAL_SETTINGS)
    public Object save(@PathVariable(value = ORGANIZATIONID, required = false) Long organizationId,
                       @Valid PageBuilderForm form,
                       @PathVariable(value = ID, required = false) Long frontendResourceId,
                       BindingResult br) {
        Flow.init()
                .thenSet(frontendResourceEntity, a -> repositories.unsecure.frontendResource.findDashboardDefinition(frontendResourceId))
                .then(a -> services.validation.validateAndPopulateToEntity(form, br, a.result))
                .thenSet(frontendResourceEntity, a -> repositories.secure.frontendResource.save(a.result))
                .thenSet(pageBuilderForm, a -> new PageBuilderForm(organizationId, a.result))
            .execute();
        return new RedirectView(_HTML + _PAGEBUILDER + "/" + frontendResourceId + _SETTINGS);

    }
}
