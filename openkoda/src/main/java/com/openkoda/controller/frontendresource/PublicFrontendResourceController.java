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

package com.openkoda.controller.frontendresource;

import com.openkoda.core.controller.frontendresource.AbstractFrontendResourceController;
import com.openkoda.core.form.AbstractOrganizationRelatedEntityForm;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@Controller
/**
 * <p>PublicFrontendResourceController has just one purpose - to display PUBLIC pages that are stored in FrontendResource repository.</p>
 * <p>The idea and assumption is that all url's under root path made from letters and dash should be loaded
 * from database as FrontendResource</p>
 *
 * @author Arkadiusz Drysch (adrysch@stratoflow.com)
 *
 */
@RequestMapping({"/"})
public class PublicFrontendResourceController extends AbstractFrontendResourceController {

    @Value("${default.pages.homeview:home}")
    String homeViewName;

    @RequestMapping(
            value = {
                    "/",
                    "/{frontendResourcePath:" + FRONTENDRESOURCEREGEX + "$}",
                    "/{frontendResourcePath:" + EXCLUDE_SWAGGER_UI_REGEX + URL_WITH_DASH_REGEX + "$}/{subPath:" + FRONTENDRESOURCEREGEX + "$}"
            },
            method = {GET, POST})
    @Transactional
    public Object openFrontendResourcePage(
            @PathVariable(value = "frontendResourcePath", required = false) String frontendResourcePath,
            @PathVariable(value = "subPath", required = false) String subPath,
            @RequestParam(value = "draft", required = false, defaultValue = "false") Boolean draft,
            @RequestParam Map<String,String> requestParams,
            @Valid AbstractOrganizationRelatedEntityForm form,
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        debug("[openFrontendResourcePage] path {}/{}", frontendResourcePath, subPath);
        request.getSession(true);
        String finalPath = frontendResourcePath == null ? homeViewName : frontendResourcePath;
        if(subPath == null) {
            subPath = "";
        }
        return invokeFrontendResourceEntry(null, finalPath, null, subPath, request, response, draft, requestParams, form);
    }
}
