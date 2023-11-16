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

package com.openkoda.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.openkoda.model.common.OpenkodaEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.Formula;

import java.util.*;
import java.util.stream.Stream;

@Entity
@Table(
        name = "controller_endpoint",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"frontend_resource_id", "sub_path", "http_method", "organization_id"})
        }
)
public class ControllerEndpoint extends OpenkodaEntity {

    final static List<String> contentProperties = Arrays.asList("code");

    public enum ResponseType {
        HTML, MODEL_AS_JSON, FILE, STREAM
    }

    public enum HttpMethod {
        GET, POST
    }

    @Column(name = "sub_path")
    private String subPath;

    @Column(length = 65536 * 4)
    private String code;

    @Column(length = 1000)
    private String httpHeaders;

    @Column(name = "http_method")
    private HttpMethod httpMethod;

    @Column(length = 1000)
    private String modelAttributes;

    @Enumerated(EnumType.STRING)
    @NotNull
    private ResponseType responseType = ResponseType.HTML;

    @JsonIgnore
    @ManyToOne(optional = true, fetch = FetchType.LAZY)
    @JoinColumn(nullable = true, insertable = false, updatable = false, name = FRONTEND_RESOURCE_ID)
    private FrontendResource frontendResource;
    @Column(nullable = true, name = FRONTEND_RESOURCE_ID)
    private Long frontendResourceId;

    public ControllerEndpoint() {
        super(null);
    }

    public ControllerEndpoint(Long organizationId) {
        super(null);
    }

    public ControllerEndpoint(Long frontendResourceId, Long organizationId) {
        super(organizationId);
        this.frontendResourceId = frontendResourceId;
    }

    public String getSubPath() {
        return subPath;
    }

    public void setSubPath(String subPath) {
        this.subPath = subPath;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getHttpHeaders() {
        return httpHeaders;
    }

    public void setHttpHeaders(String httpHeaders) {
        this.httpHeaders = httpHeaders;
    }

    public HttpMethod getHttpMethod() {
        return httpMethod;
    }

    public void setHttpMethod(HttpMethod httpMethod) {
        this.httpMethod = httpMethod;
    }
    public void setHttpMethod(String httpMethod) {
        this.httpMethod = HttpMethod.valueOf(httpMethod);
    }

    public String getModelAttributes() {
        return modelAttributes;
    }

    public void setModelAttributes(String modelAttributes) {
        this.modelAttributes = modelAttributes;
    }

    public ResponseType getResponseType() {
        return responseType;
    }

    public void setResponseType(ResponseType responseType) {
        this.responseType = responseType;
    }
    public void setResponseType(String responseType) {
        this.responseType = ResponseType.valueOf(responseType);
    }

    public FrontendResource getFrontendResource() {
        return frontendResource;
    }

    public void setFrontendResource(FrontendResource frontendResource) {
        this.frontendResource = frontendResource;
    }

    public Long getFrontendResourceId() {
        return frontendResourceId;
    }

    public void setFrontendResourceId(Long frontendResourceId) {
        this.frontendResourceId = frontendResourceId;
    }

    public Map<String, String> getHttpHeadersMap() {
        Map<String, String> httpHeadersMap = new HashMap();
        if(StringUtils.isNotBlank(this.httpHeaders)) {
            for (String httpHeader : Arrays.asList(this.httpHeaders.split("\n"))) {
                String[] headerParts = httpHeader.split(":");
                httpHeadersMap.put(headerParts[0].trim(), headerParts[1].trim());
            }
        }
        return httpHeadersMap;
    }

    public String[] getPageAttributesNames() {
        return StringUtils.isNotBlank(this.modelAttributes) ? Stream.of(this.modelAttributes.split(","))
                .map(s -> s.trim()).toArray(String[]::new) : null;
    }

    @Formula("(NULL)")
    protected String requiredReadPrivilege;

    @Formula("(NULL)")
    protected String requiredWritePrivilege;

    @Override
    public String getRequiredReadPrivilege() {
        return requiredReadPrivilege;
    }

    @Override
    public String getRequiredWritePrivilege() {
        return requiredWritePrivilege;
    }

    @Override
    public Collection<String> contentProperties() {
        return contentProperties;
    }

}
