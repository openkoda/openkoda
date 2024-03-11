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

import com.openkoda.core.flow.Flow;
import com.openkoda.core.security.HasSecurityRules;
import com.openkoda.model.component.FrontendResource;
import com.openkoda.repository.specifications.FrontendResourceSpecifications;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.openkoda.controller.common.URLConstants.*;

@RestController
@RequestMapping({_HTML})
public class ComponentsController extends ComponentProvider implements HasSecurityRules {


    @PreAuthorize(CHECK_CAN_MANAGE_ORG_DATA)
    @ResponseBody
    @GetMapping(value = _ORGANIZATION + _EXPORT_YAML + _ALL, produces = "application/zip")
    public byte[] exportAllResourceToZippedYamls(){
        debug("[exportAllResourceToZippedYamls]");
        List<Object> allResources = new ArrayList<>();
        allResources.addAll(repositories.secure.serverJs.findAll());
        allResources.addAll(repositories.secure.frontendResource.findAll());
        allResources.addAll(repositories.secure.eventListener.findAll());
        allResources.addAll(repositories.secure.scheduler.findAll());
        allResources.addAll(repositories.secure.form.findAll());

        return services.componentExport.exportToZip(allResources).toByteArray();
    }

    @PreAuthorize(CHECK_CAN_MANAGE_ORG_DATA)
    @ResponseBody
    @GetMapping(value = _ORGANIZATION + _ORGANIZATIONID + _EXPORT_YAML + _ALL, produces = "application/zip")
    public byte[] exportAllForOrg(@PathVariable long organizationId){
        debug("[exportAllResourceToZippedYamls]");
        List<Object> allResources = new ArrayList<>();
        allResources.addAll(repositories.secure.serverJs.search(organizationId));
        allResources.addAll(repositories.secure.frontendResource.search(organizationId));
        allResources.addAll(repositories.secure.eventListener.search(organizationId));
        allResources.addAll(repositories.secure.scheduler.search(organizationId));
        allResources.addAll(repositories.secure.form.findAll());

        return services.componentExport.exportToZip(allResources).toByteArray();
    }

    @GetMapping(value = _FRONTENDRESOURCE + _EXPORT_YAML, produces = "application/zip")
    @ResponseBody
    @PreAuthorize(CHECK_CAN_MANAGE_BACKEND)
    public byte[] exportAllFrontendResourcesToZippedYamls(){
        debug("[exportAllFrontendResourcesToZippedYamls]");
        return services.componentExport.exportToZip(repositories.secure.frontendResource.findAll()).toByteArray();
    }

    @GetMapping(value =  _UI_COMPONENT + _EXPORT_YAML, produces = "application/zip")
    @ResponseBody
    @PreAuthorize(CHECK_CAN_MANAGE_BACKEND)
    public byte[] exportAllUiComponentsToZippedYamls(){
        debug("[exportAllFrontendResourcesToZippedYamls]");
        return services.componentExport.exportToZip(repositories.secure.frontendResource.search(FrontendResourceSpecifications.searchByResourceType(FrontendResource.ResourceType.UI_COMPONENT))).toByteArray();
    }

    @GetMapping(value = _FRONTENDRESOURCE + _ID + _EXPORT_YAML, produces = "application/zip")
    @ResponseBody
    @PreAuthorize(CHECK_CAN_MANAGE_BACKEND)
    public byte[] exportFrontendResourceToZippedYaml(@PathVariable(ID) long frontendResourceId){
        debug("[exportFrontendResourceToZippedYaml]");
        return services.componentExport.exportToZip(Arrays.asList(repositories.secure.frontendResource.findOne(frontendResourceId))).toByteArray();
    }


    @PreAuthorize(CHECK_CAN_MANAGE_BACKEND)
    @ResponseBody
    @GetMapping(value = _SERVERJS + _EXPORT_YAML, produces = "application/zip")
    public byte[] exportAllServerJsToZippedYamls(){
        debug("[exportAllServerJsToZippedYamls]");
        return services.componentExport.exportToZip(repositories.secure.serverJs.findAll()).toByteArray();
    }

    @PreAuthorize(CHECK_CAN_MANAGE_BACKEND)
    @ResponseBody
    @GetMapping(value = _SERVERJS + _ID + _EXPORT_YAML, produces = "application/zip")
    public byte[] exportServerJsToZippedYaml(@PathVariable(ID) long serverJsId){
        debug("[exportServerJsToZippedYaml]");
        return services.componentExport.exportToZip(Arrays.asList(repositories.secure.serverJs.findOne(serverJsId))).toByteArray();
    }

    @PreAuthorize(CHECK_CAN_MANAGE_BACKEND)
    @GetMapping(value = _FORM + _EXPORT_YAML, produces = "application/zip")
    @ResponseBody
    public byte[] exportAllFormsToZippedYamls(){
        debug("[exportAllFormsToZippedYamls]");
        return services.componentExport.exportToZip(repositories.secure.form.findAll()).toByteArray();
    }

    @PreAuthorize(CHECK_CAN_MANAGE_BACKEND)
    @GetMapping(value = _FORM + _ID + _EXPORT_YAML, produces = "application/zip")
    @ResponseBody
    public byte[] exportFormToZippedYaml(@PathVariable(ID) long formId){
        debug("[exportFormToZippedYaml]");
        return services.componentExport.exportToZip(Arrays.asList(repositories.secure.form.findOne(formId))).toByteArray();
    }

    @GetMapping(value = _SCHEDULER + _EXPORT_YAML, produces = "application/zip")
    @ResponseBody
    @PreAuthorize(CHECK_CAN_MANAGE_BACKEND)
    public byte[] exportAllSchedulersToZippedYamls(){
        debug("[exportAllSchedulersToZippedYamls]");
        return services.componentExport.exportToZip(repositories.secure.scheduler.findAll()).toByteArray();
    }

    @GetMapping(value = _SCHEDULER + _ID + _EXPORT_YAML, produces = "application/zip")
    @ResponseBody
    @PreAuthorize(CHECK_CAN_MANAGE_BACKEND)
    public byte[] exportSchedulerToZippedYaml(@PathVariable(ID) long schedulerId){
        debug("[exportSchedulerToZippedYaml]");
        return services.componentExport.exportToZip(Arrays.asList(repositories.secure.scheduler.findOne(schedulerId))).toByteArray();
    }

    @PreAuthorize(CHECK_CAN_MANAGE_BACKEND)
    @ResponseBody
    @GetMapping(value = _EVENTLISTENER + _EXPORT_YAML, produces = "application/zip")
    public byte[] exportAllEventListenersToZippedYamls(){
        debug("[exportAllEventListenersToZippedYamls]");
        return services.componentExport.exportToZip(repositories.secure.eventListener.findAll()).toByteArray();
    }

    @PreAuthorize(CHECK_CAN_MANAGE_BACKEND)
    @ResponseBody
    @GetMapping(value = _EVENTLISTENER + _ID + _EXPORT_YAML, produces = "application/zip")
    public byte[] exportEventListenerToZippedYaml(@PathVariable(ID) long eventListenerId){
        debug("[exportEventListenerToZippedYaml]");
        return services.componentExport.exportToZip(Arrays.asList(repositories.secure.eventListener.findOne(eventListenerId))).toByteArray();
    }

    @PreAuthorize(CHECK_CAN_MANAGE_BACKEND)
    @Transactional
    @RequestMapping(value = _COMPONENT + _IMPORT + _ZIP, method = RequestMethod.POST)
    public Object importComponentsZip(@RequestParam("file") MultipartFile file) {
        debug("[importComponentsZip]");
        return Flow.init()
                .thenSet(importLog ,a -> services.componentImport.loadResourcesFromZip(file))
                .execute()
                .mav("components");
    }
}
