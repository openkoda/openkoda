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

package com.openkoda.uicomponent.editor;

import com.openkoda.dto.CanonicalObject;
import com.openkoda.dto.OrganizationRelatedObject;
import com.openkoda.model.ControllerEndpoint;

public class UIComponentControllerEndpointDto implements CanonicalObject, OrganizationRelatedObject {

    public Long organizationId;
    public String subPath;
    public String code;
    public String httpHeaders;
    public ControllerEndpoint.HttpMethod httpMethod;
    public String modelAttributes;
    public ControllerEndpoint.ResponseType responseType;
    public Long frontendResourceId;

    public UIComponentControllerEndpointDto() { }

    public UIComponentControllerEndpointDto(Long organizationId, Long frontendResourceId) {
        this.organizationId = organizationId;
        this.frontendResourceId = frontendResourceId;
    }

    public void setOrganizationId(Long organizationId) {
        this.organizationId = organizationId;
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

    public ControllerEndpoint.HttpMethod getHttpMethod() {
        return httpMethod;
    }

    public void setHttpMethod(ControllerEndpoint.HttpMethod httpMethod) {
        this.httpMethod = httpMethod;
    }

    public String getModelAttributes() {
        return modelAttributes;
    }

    public void setModelAttributes(String modelAttributes) {
        this.modelAttributes = modelAttributes;
    }

    public ControllerEndpoint.ResponseType getResponseType() {
        return responseType;
    }

    public void setResponseType(ControllerEndpoint.ResponseType responseType) {
        this.responseType = responseType;
    }

    public Long getFrontendResourceId() {
        return frontendResourceId;
    }

    public void setFrontendResourceId(Long frontendResourceId) {
        this.frontendResourceId = frontendResourceId;
    }

    @Override
    public String notificationMessage() {
        return null;
    }

    @Override
    public Long getOrganizationId() {
        return null;
    }

}
