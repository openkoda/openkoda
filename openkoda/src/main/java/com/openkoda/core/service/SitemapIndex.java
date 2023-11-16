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

package com.openkoda.core.service;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import com.openkoda.core.tracker.LoggingComponentWithRequestId;
import com.openkoda.model.FrontendResource;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@JacksonXmlRootElement(localName = "urlset")
public class SitemapIndex implements Serializable, LoggingComponentWithRequestId {

    public SitemapIndex(Collection<FrontendResource> entries, String baseUrl) {
        debug("[SitemapIndex]");
        this.entries = new ArrayList<>();
        for (FrontendResource entry : entries) {
            SitemapEntry sitemapEntry = new SitemapEntry(entry, baseUrl);
            this.entries.add(sitemapEntry);
        }
    }

    @JacksonXmlProperty(localName = "url")
    @JacksonXmlElementWrapper(useWrapping = false)
    private List<SitemapEntry> entries;

    @JacksonXmlProperty(isAttribute = true)
    private String xmlns = "http://www.sitemaps.org/schemas/sitemap/0.9";

    public List<SitemapEntry> getEntries() {
        debug("[getEntries]");
        return entries;
    }
}
