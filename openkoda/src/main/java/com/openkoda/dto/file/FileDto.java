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

package com.openkoda.dto.file;

import com.google.gson.annotations.Expose;
import com.openkoda.dto.CanonicalObject;
import com.openkoda.dto.OrganizationRelatedObject;

import static com.openkoda.controller.common.URLConstants.*;

public class FileDto implements CanonicalObject, OrganizationRelatedObject{

    public Long organizationId;

    @Expose
    public Long id;
    @Expose
    public String filename;
    @Expose
    public String contentType;
    @Expose
    public String downloadUrl;
    @Expose
    public String deleteUrl;
    @Expose
    public Boolean publicFile;

    //TODO Rule 5.5: DTO should not have code
    private static String getUrlBase(Long id, Long organizationId) {
        return organizationId == null ? (_HTML + _FILE + "/" + id) : (_HTML_ORGANIZATION + "/" + organizationId + _FILE + "/" + id);
    }

    public FileDto(Long id, Long organizationId, String filename, String contentType, String downloadUrl) {
        this(id, organizationId, filename, contentType);
        this.downloadUrl = downloadUrl;
    }

    public FileDto(Long id, Long organizationId, String filename, String contentType) {
        this.id = id;
        this.organizationId = organizationId;
        this.filename = filename;
        this.contentType = contentType;
        this.downloadUrl = getUrlBase(id, organizationId) + "/content";
        this.deleteUrl = getUrlBase(id, organizationId) + "/remove";
    }

    public FileDto() {
    }

    public String getFilename() {
        return this.filename;
    }

    public String getContentType() {
        return this.contentType;
    }


    
    public void setFilename(String filename) {
        this.filename = filename;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }


    @Override
    public String notificationMessage() {
        return "";
    }

    @Override
    public Long getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(Long organizationId) {
        this.organizationId = organizationId;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

    public String getDeleteUrl() {
        return deleteUrl;
    }

    public void setDeleteUrl(String deleteUrl) {
        this.deleteUrl = deleteUrl;
    }

    public Boolean getPublicFile() {
        return publicFile;
    }

    public void setPublicFile(Boolean publicFile) {
        this.publicFile = publicFile;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}