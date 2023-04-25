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

package com.openkoda.core.controller.frontendresource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import static com.openkoda.controller.common.URLConstants._FRONTENDRESOURCE;
import static com.openkoda.controller.common.URLConstants._HTML_ORGANIZATION_ORGANIZATIONID;
import static com.openkoda.core.controller.generic.AbstractController._HTML;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;


/**
 * The controller for server-side generated html actions considering any {@link com.openkoda.model.FrontendResource} management or CRUD operations.
 * See also {@link AbstractFrontendResourceController}
 */
@RestController
@RequestMapping({_HTML_ORGANIZATION_ORGANIZATIONID + _FRONTENDRESOURCE, _HTML + _FRONTENDRESOURCE})
public class FrontendResourceControllerHtml extends AbstractFrontendResourceController {


    /**
     * The default resource name of a content for {@link com.openkoda.model.FrontendResource} with type {@link FrontendResource.Type.PAGE}
     */
    @Value("${default.frontendResourcePage.template.name:frontend-resource-template}")
    String defaultFrontendResourcePageTemplate;

    /**
     * Triggers update of the {@link com.openkoda.model.FrontendResource} and prepares the result response.
     * See also {@link AbstractFrontendResourceController}
     *
     * @param content a {@link java.lang.String} object.
     * @param id      a {@link java.lang.Long} object.
     * @return a {@link java.lang.Object} object.
     */
    @PostMapping(_ID)
    @ResponseBody
    @PreAuthorize(CHECK_CAN_MANAGE_FRONTEND_RESOURCES)
    public Object update(
            @PathVariable(value = ORGANIZATIONID, required = false) Long organizationId,
            @RequestParam String content,
            @PathVariable(ID) Long id) {
        debug("[update] FrontendResourceId: {}", id);
        return updateFrontendResource(content, id)
                .mav(a -> true, a -> a.get(message));
    }


    /**
     * Triggers the publish action of the {@link com.openkoda.model.FrontendResource} and prepares the result response.
     * See also {@link AbstractFrontendResourceController}
     *
     * @param organizationId
     * @param frontendResourceId
     * @return java.lang.Object
     */
    @PostMapping(_ID + _PUBLISH)
    @PreAuthorize(CHECK_CAN_MANAGE_FRONTEND_RESOURCES)
    public Object publish(
            @PathVariable(value = ORGANIZATIONID, required = false) Long organizationId,
            @PathVariable(ID) Long frontendResourceId) {
        debug("[publish] FrontendResourceId: {}", frontendResourceId);
        return publishFrontendResource(frontendResourceId)
                .mav(a -> true);
    }

    /**
     * Triggers the publish action of all {@link com.openkoda.model.FrontendResource} in the database and prepares the result response.
     * See also {@link AbstractFrontendResourceController}
     *
     * @return java.lang.Object
     */
    @PostMapping(_ALL + _PUBLISH)
    @PreAuthorize(CHECK_CAN_MANAGE_FRONTEND_RESOURCES)
    public Object publishAll() {
        debug("[publishAll]");
        return publishAllFrontendResource()
                .mav(a -> true);
    }


    /**
     * @deprecated
     * This method is no longer used due to changes in frontend resource flow
     * Use {@link #reloadToDraft(Long)} instead.
     */
    @Deprecated
    @PostMapping(_ID + _RELOAD)
    @PreAuthorize(CHECK_CAN_MANAGE_FRONTEND_RESOURCES)
    public Object reload(@PathVariable(ID) Long frontendResourceId) {
        debug("[reload] FrontendResourceId: {}", frontendResourceId);
        return reloadFrontendResource(frontendResourceId)
                .mav(a -> true);
    }

    /**
     * Reloads draft content of all {@link com.openkoda.model.FrontendResource} .
     * See also {@link AbstractFrontendResourceController}
     *
     * @param frontendResourceId
     * @return java.lang.Object
     */
    @RequestMapping(value = _ID + _RELOAD_TO_DRAFT, method = POST)
    @PreAuthorize(CHECK_CAN_MANAGE_FRONTEND_RESOURCES)
    public Object reloadToDraft(@PathVariable(ID) Long frontendResourceId) {
        debug("[reload] FrontendResourceId: {}", frontendResourceId);
        return reloadFrontendResourceToDraft(frontendResourceId)
                .mav(a -> true);
    }

    /**
     * Downloads all {@link com.openkoda.model.FrontendResource} from the database packed in a .ZIP file.
     * See also {@link com.openkoda.core.service.ZipService}, {@link com.openkoda.repository.FrontendResourceRepository}
     *
     * @return byte[]
     */
    @RequestMapping(value = _ZIP, method = GET, produces = "application/zip")
    @ResponseBody
    @PreAuthorize(CHECK_CAN_READ_FRONTEND_RESOURCES)
    public byte[] getAllZipped() {
        debug("[getAllZipped]");
        return services.zipService.zipFrontendResources(repositories.unsecure.frontendResource.findAll()).toByteArray();
    }

}
