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

package com.openkoda.service.export.dto;

import com.openkoda.model.component.FrontendResource;

import java.util.List;

public class FrontendResourceConversionDto extends ComponentDto{

    private boolean includeInSitemap;
    private FrontendResource.AccessLevel accessLevel;
    private String name;
    private String requiredPrivilege;
    private String type;
    private String resourceType;
    private String content;
    private List<ControllerEndpointConversionDto> controllerEndpoints;
    public boolean embeddable;

    public boolean getIncludeInSitemap() {
        return includeInSitemap;
    }

    public void setIncludeInSitemap(boolean includeInSitemap) {
        this.includeInSitemap = includeInSitemap;
    }

    public FrontendResource.AccessLevel getAccessLevel() {
        return accessLevel;
    }

    public void setAccessLevel(FrontendResource.AccessLevel accessLevel) {
        this.accessLevel = accessLevel;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRequiredPrivilege() {
        return requiredPrivilege;
    }

    public void setRequiredPrivilege(String requiredPrivilege) {
        this.requiredPrivilege = requiredPrivilege;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getResourceType() {
        return resourceType;
    }

    public void setResourceType(String resourceType) {
        this.resourceType = resourceType;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public List<ControllerEndpointConversionDto> getControllerEndpoints() {
        return controllerEndpoints;
    }

    public void setControllerEndpoints(List<ControllerEndpointConversionDto> controllerEndpoints) {
        this.controllerEndpoints = controllerEndpoints;
    }

    public boolean isEmbeddable() {
        return embeddable;
    }

    public boolean getEmbeddable() {
        return embeddable;
    }

    public void setEmbeddable(boolean embeddable) {
        this.embeddable = embeddable;
    }
}
