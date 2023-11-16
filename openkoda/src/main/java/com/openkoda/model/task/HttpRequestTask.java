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

package com.openkoda.model.task;

import com.openkoda.core.helper.JsonHelper;
import com.openkoda.model.common.AuditableEntityOrganizationRelated;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Transient;
import org.apache.commons.lang3.StringUtils;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.*;

/**
 * Http request object to store information about the post request that will be sent to an external webhook
 *
 * @author Martyna Litkowska (mlitkowska@stratoflow.com)
 * @since 2019-07-01
 */
@Entity
@DiscriminatorValue("httprequest")
public class HttpRequestTask extends Task implements AuditableEntityOrganizationRelated {

    final static List<String> contentProperties = Arrays.asList("json");

    @Column(nullable = false)
    private String requestUrl;

    @Column(length = 65535)
    private String json;

    @Column(length = 65535)
    private String headersJson;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private Date dateSent;

    @Transient
    private Map<String, String> headersMap;

    public HttpRequestTask() {
    }

    public HttpRequestTask(String requestUrl, String json) {
        this.requestUrl = requestUrl;
        this.json = json;
    }

    public HttpRequestTask(String requestUrl, String json, String headersJson) {
        this.requestUrl = requestUrl;
        this.json = json;
        this.headersJson = headersJson;
    }

    public HttpRequestTask(String requestUrl, String json, Long organizationId) {
        this.requestUrl = requestUrl;
        this.json = json;
        this.setOrganizationId(organizationId);
    }

    public String getRequestUrl() {
        return requestUrl;
    }

    public void setRequestUrl(String requestUrl) {
        this.requestUrl = requestUrl;
    }

    public String getJson() {
        return json;
    }

    public void setJson(String json) {
        this.json = json;
    }

    public Date getDateSent() {
        return dateSent;
    }

    public void setDateSent(Date dateSent) {
        this.dateSent = dateSent;
    }

    @Override
    public String toAuditString() {
        return "ID: " + this.getId();
    }

    @Override
    public Collection<String> contentProperties() {
        return contentProperties;
    }

    public String getHeadersJson() {
        return headersJson;
    }

    public void setHeadersJson(String headersJson) {
        this.headersJson = headersJson;
    }

    public Map<String, String> getHeadersMap() {
        if ( headersMap == null ) {
            headersMap = JsonHelper.from(StringUtils.defaultString(headersJson, "{}"), Map.class);
        }
        return headersMap;
    }
}
