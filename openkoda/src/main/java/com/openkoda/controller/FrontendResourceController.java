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

package com.openkoda.controller;

import com.openkoda.core.controller.frontendresource.AbstractFrontendResourceController;
import com.openkoda.core.form.AbstractOrganizationRelatedEntityForm;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

import static com.openkoda.controller.common.URLConstants.*;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@Controller
/**
 * <p>FrontendResourceController has just one purpose - to display pages that are stored in FrontendResource repository.</p>
 * <p>The idea and assumption is that all url's under root path made from letters and dash should be loaded
 * from database as FrontendResource</p>
 *
 * @author Arkadiusz Drysch (adrysch@stratoflow.com)
 * 
 */
@RequestMapping({"/", _HTML + _WEBENDPOINTS, _HTML_ORGANIZATION_ORGANIZATIONID + _WEBENDPOINTS})
public class FrontendResourceController extends AbstractFrontendResourceController {

    @Value("${default.pages.homeview:home}")
    String homeViewName;

    @RequestMapping(
            value = {
                    "/",
                    "/{frontendResourcePath:" + FRONTENDRESOURCEREGEX + "$}",
                    "/{frontendResourcePath:" + EXCLUDE_SWAGGER_UI_REGEX + URL_WITH_DASH_REGEX + "$}/{subPath:" + FRONTENDRESOURCEREGEX + "$}"
            },
            method = {GET, POST})
    public Object openFrontendResourcePage(
            @PathVariable(value = ORGANIZATIONID, required = false) Long organizationId,
            @PathVariable(value = "frontendResourcePath", required = false) String frontendResourcePath,
            @PathVariable(value = "subPath", required = false) String subPath,
            @RequestParam(value = "draft", required = false, defaultValue = "false") Boolean draft,
            @RequestParam Map<String,String> requestParams,
            @Valid AbstractOrganizationRelatedEntityForm form,
            HttpServletRequest request
    ) {
        debug("[openFrontendResourcePage] path {}/{}", frontendResourcePath, subPath);
        request.getSession(true);
        String finalPath = frontendResourcePath == null ? homeViewName : frontendResourcePath;
        boolean addRegisterForm = REGISTER.equals(frontendResourcePath);
        boolean isPublic = !request.getRequestURI().contains(_HTML);
        if(subPath == null) {
            subPath = "";
        }
        return invokeFrontendResourceEntry(organizationId, finalPath, subPath, request, addRegisterForm, isPublic, draft, requestParams, form);
    }
}
