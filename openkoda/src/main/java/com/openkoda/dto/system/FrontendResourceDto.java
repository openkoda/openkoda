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

package com.openkoda.dto.system;

import com.openkoda.dto.CanonicalObject;
import com.openkoda.dto.OrganizationRelatedObject;
import com.openkoda.model.FrontendResource;


/**
 * @author Martyna Litkowska (mlitkowska@stratoflow.com)
 * @since 2019-02-19
 */
public class FrontendResourceDto implements CanonicalObject, OrganizationRelatedObject {

    public String name;
    public Long organizationId;
    public String content;
    public String contentEditable;
    public String testData;
    public String requiredPrivilege;
    public boolean includeInSitemap;
    public boolean embeddable;
    public FrontendResource.AccessLevel accessLevel;
    public FrontendResource.Type type;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(Long organizationId) {
        this.organizationId = organizationId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getRequiredPrivilege() {
        return requiredPrivilege;
    }

    public void setRequiredPrivilege(String requiredPrivilege) {
        this.requiredPrivilege = requiredPrivilege;
    }

    public FrontendResource.Type getType() {
        return type;
    }

    public void setType(FrontendResource.Type type) {
        this.type = type;
    }

    public String getTestData() {
        return testData;
    }

    public void setTestData(String testData) {
        this.testData = testData;
    }

    public boolean getIncludeInSitemap() {
        return includeInSitemap;
    }

    public void setIncludeInSitemap(boolean includeInSitemap) {
        this.includeInSitemap = includeInSitemap;
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

    public String getContentEditable() {
        return contentEditable;
    }

    public void setContentEditable(String contentEditable) {
        this.contentEditable = contentEditable;
    }

    public FrontendResource.AccessLevel getAccessLevel() {
        return accessLevel;
    }

    public void setAccessLevel(FrontendResource.AccessLevel accessLevel) {
        this.accessLevel = accessLevel;
    }

    @Override
    public String notificationMessage() {
        return String.format("CmsEntry %s of type: %s.", name, type);
    }

}
