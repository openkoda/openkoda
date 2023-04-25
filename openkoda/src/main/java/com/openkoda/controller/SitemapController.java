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

package com.openkoda.controller;

import com.openkoda.core.controller.generic.AbstractController;
import com.openkoda.core.service.SitemapIndex;
import com.openkoda.model.FrontendResource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import java.util.Collection;

@Controller
/**
 * <p> SitemapController is handling requests for xml files with sitemaps. </p>
 *
 * @author dwojcik
 * 
 */
public class SitemapController extends AbstractController {

    @RequestMapping(value = _GENERAL_SITEMAP + _XML_EXTENSION, headers = _XML_HEADER, produces = MediaType.APPLICATION_XML_VALUE)
    public Object getGeneralSitemap() {
        debug("[getGeneralSitemap]");
        return new ModelAndView(XML  + _GENERAL_SITEMAP + _XML_EXTENSION, baseUrl.name, services.url.getBaseUrl());
    }

    @RequestMapping(value = _SITEMAP_INDEX + _XML_EXTENSION, headers = _XML_HEADER, produces = MediaType.APPLICATION_XML_VALUE)
    public Object getSitemapIndex() {
        debug("[getSiteMapIndex]");
        return new ModelAndView(XML  + _SITEMAP_INDEX + _XML_EXTENSION, baseUrl.name, services.url.getBaseUrl());
    }

    @RequestMapping(value = _SITEMAP + _XML_EXTENSION, headers = _XML_HEADER, produces = MediaType.APPLICATION_XML_VALUE)
    public Object getSitemap() {
        debug("[getSitemap]");

        RedirectView redirectView = new RedirectView();
        redirectView.setExposeModelAttributes(false);
        redirectView.setUrl(_SITEMAP_INDEX + _XML_EXTENSION);
        return redirectView;
    }

    @RequestMapping(value = _PAGES_SITEMAP + _XML_EXTENSION, headers = _XML_HEADER, produces = MediaType.APPLICATION_XML_VALUE)
    @ResponseBody
    public SitemapIndex getPagesSitemap() {
        debug("[getPagesSitemap]");
        Collection<FrontendResource> entries = repositories.unsecure.frontendResource.getEntriesToSitemap();
        return new SitemapIndex(entries, services.url.getBaseUrl());
    }
}
