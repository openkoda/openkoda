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

package com.openkoda.model.file;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.openkoda.core.service.FileService;
import com.openkoda.dto.file.FileDto;
import com.openkoda.model.PrivilegeNames;
import com.openkoda.model.common.OpenkodaEntity;
import jakarta.persistence.*;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.Formula;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Blob;
import java.sql.SQLException;

/**
 * <p>Stores information about users' files</p>
 */
@Entity
@Table(name = "file")
public class File extends OpenkodaEntity {

    @Column
    private String filename;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private FileService.StorageType storageType;

    @Column
    private String filesystemPath;

    @Column(unique = true)
    private String uploadUuid;

    @Column
    private String contentType;

    @Column
    private long size;

    @Column(columnDefinition = "boolean default false")
    private boolean publicFile;

    @Lob
    @JsonIgnore
    private Blob content;

    @Formula("( '" + PrivilegeNames._readOrgData + "' )")
    private String requiredReadPrivilege;

    @Formula("( '" + PrivilegeNames._manageOrgData + "' )")
    private String requiredWritePrivilege;

    /**
     * Use this method only for files that will be stored in filesystem or database!
     */
    public InputStream getContentStream() throws IOException, SQLException, RuntimeException {
        switch (storageType) {
            case filesystem:
                return new FileInputStream(filesystemPath);
            case database:
                return content.getBinaryStream();
            default:
                throw new RuntimeException("Method File.getContentStream() used on file " +
                        "not stored in filesystem or database");
        }
    }

    /**
     * <p>Constructor for File.</p>
     */
    public File() {
        super(null);
    }

    /**
     * <p>Constructor for File.</p>
     *
     * @param organizationId a {@link Long} object.
     */
    public File(Long organizationId) {
        super(organizationId);
    }

    /**
     * <p>Constructor for File.</p>
     *
     * @param organizationId a {@link Long} object.
     * @param uuid           a {@link String} object.
     */
    public File(Long organizationId, String uuid) {
        super(organizationId);
    }

    /**
     * <p>Constructor for File.</p>
     *
     * @param organizationId a {@link Long} object.
     * @param filename       a {@link String} object.
     * @param contentType    a {@link String} object.
     * @param size           a {@link Long} object.
     * @param uploadUuid     a {@link String} object.
     * @param storageType    a {@link com.openkoda.core.service.FileService.StorageType} object.
     */
    public File(Long organizationId, String filename, String contentType, long size, String uploadUuid,
                FileService.StorageType storageType) {
        super(organizationId);
        this.filename = filename;
        this.uploadUuid = uploadUuid;
        this.contentType = contentType;
        this.size = size;
        this.storageType = storageType;
    }

    public File(String filename, String contentType, FileService.StorageType storageType) {
        super(null);
        this.filename = filename;
        this.contentType = contentType;
        this.storageType = storageType;
    }

    /**
     * <p>Constructor for File.</p>
     *
     * @param organizationId a {@link Long} object.
     * @param filename       a {@link String} object.
     * @param contentType    a {@link String} object.
     * @param size           a {@link Long} object.
     * @param uploadUuid     a {@link String} object.
     * @param storageType    a {@link com.openkoda.core.service.FileService.StorageType} object.
     * @param filesystemPath a {@link String} object.
     */
    public File(Long organizationId, String filename, String contentType, long size, String uploadUuid,
                FileService.StorageType storageType, String filesystemPath) {
        this(organizationId, filename, contentType, size, uploadUuid, storageType);
        this.filesystemPath = filesystemPath;
    }

    public String getFilename() {
        return this.filename;
    }

    public String getContentType() {
        return this.contentType;
    }

    public Blob getContent() {
        return this.content;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public void setContent(Blob content) {
        this.content = content;
    }

    @Override
    public String toAuditString() {
        return null;
    }

    public String getUploadUuid() {
        return uploadUuid;
    }

    public void setUploadUuid(String uploadUuid) {
        this.uploadUuid = uploadUuid;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public static FileDto toFileDto(File a) {
        if (a == null) {
            return new FileDto();
        }
        return new FileDto(a.getId(), a.getOrganizationId(), a.getFilename(), a.getContentType());
    }

    public FileDto toFileDto() {
        return toFileDto(this);
    }

    public String getUrl() {
        return toFileDto().downloadUrl;
    }

    public boolean isImage() {
        return StringUtils.startsWith(contentType, "image/");
    }

    public boolean isVideo() {
        return StringUtils.startsWith(contentType, "video/");
    }

    public FileService.StorageType getStorageType() {
        return storageType;
    }

    public void setStorageType(FileService.StorageType storageType) {
        this.storageType = storageType;
    }

    public String getFilesystemPath() {
        return filesystemPath;
    }

    public void setFilesystemPath(String filesystemPath) {
        this.filesystemPath = filesystemPath;
    }

    public boolean isPublicFile() {
        return publicFile;
    }

    public void setPublicFile(boolean publicFile) {
        this.publicFile = publicFile;
    }

    @Override
    public String getRequiredReadPrivilege() {
        return requiredReadPrivilege;
    }

    @Override
    public String getRequiredWritePrivilege() {
        return requiredWritePrivilege;
    }

    @Override
    public String toString() {
        return id + "\t" + contentType + "\t" + filename;
    }
}