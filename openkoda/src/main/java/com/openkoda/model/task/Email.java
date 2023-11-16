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

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.openkoda.model.Organization;
import com.openkoda.model.common.AuditableEntityOrganizationRelated;
import com.openkoda.model.file.File;
import jakarta.persistence.*;
import org.hibernate.annotations.Formula;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * <p>Stores email information in order to be sent by email sender job.</p>
 * See also {@link com.openkoda.core.job.EmailSenderJob}
 *
 * @author Arkadiusz Drysch (adrysch@stratoflow.com)
 *
 */
@Entity
@DiscriminatorValue("email")
public class Email extends Task implements AuditableEntityOrganizationRelated {

    public static final String REFERENCE_FORMULA = DEFAULT_ORGANIZATION_RELATED_REFERENCE_FIELD_FORMULA;
    final static List<String> contentProperties = Arrays.asList("content");

    private String nameFrom;
    private String emailTo;
    private String nameTo;

    @Column(length = 65535)
    private String content;
    private String subject;

    //TODO Rule 4.4: should be marked with FetchType = LAZY
    @JsonIgnore
    @ManyToOne(optional = true)
    @JoinColumn(nullable = true, insertable = false, updatable = false, name = ORGANIZATION_ID)
    private Organization organization;
    @Column(nullable = true, name = ORGANIZATION_ID)
    private Long organizationId;

    private String attachmentURL;

    @ManyToMany(fetch = FetchType.LAZY, cascade = {})
    @JoinTable(
            name="file_reference",
            foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT),
            inverseJoinColumns =  @JoinColumn(name = "file_id"),
            joinColumns = @JoinColumn(name = "organization_related_entity_id", insertable = false, updatable = false)
    )
    @JsonIgnore
    @OrderColumn(name="sequence")
    protected List<File> files;

    @ElementCollection(fetch = FetchType.LAZY, targetClass = Long.class)
    @CollectionTable(name = "file_reference", joinColumns = @JoinColumn(name = "organization_related_entity_id"), foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    @Column(name="file_id")
    @OrderColumn(name="sequence")
    protected List<Long> filesId = new ArrayList<>();

    /**
     * <p>Constructor for Email.</p>
     */
    public Email() {
    }

    /**
     * <p>Constructor for Email.</p>
     *
     * @param nameFrom a {@link String} object.
     * @param emailTo a {@link String} object.
     * @param nameTo a {@link String} object.
     * @param content a {@link String} object.
     * @param subject a {@link String} object.
     */
    public Email(String nameFrom, String emailTo, String nameTo, String content, String subject) {
        this.nameFrom = nameFrom;
        this.emailTo = emailTo;
        this.nameTo = nameTo;
        this.content = content;
        this.subject = subject;
    }

    /**
     * <p>getFullFrom.</p>
     *
     * @param emailFrom a {@link String} object.
     * @return a {@link String} object.
     */
    public String getFullFrom(String emailFrom) {
        return String.format("%s <%s>", nameFrom, emailFrom);
    }

    /**
     * <p>getFullTo.</p>
     *
     * @return a {@link String} object.
     */
    public String getFullTo() {
        return String.format("%s <%s>", nameTo, emailTo);
    }

    /**
     * <p>Getter for the field <code>nameFrom</code>.</p>
     *
     * @return a {@link String} object.
     */
    public String getNameFrom() {
        return nameFrom;
    }

    /**
     * <p>Setter for the field <code>nameFrom</code>.</p>
     *
     * @param nameFrom a {@link String} object.
     */
    public void setNameFrom(String nameFrom) {
        this.nameFrom = nameFrom;
    }

    /**
     * <p>Getter for the field <code>emailTo</code>.</p>
     *
     * @return a {@link String} object.
     */
    public String getEmailTo() {
        return emailTo;
    }

    /**
     * <p>Setter for the field <code>emailTo</code>.</p>
     *
     * @param emailTo a {@link String} object.
     */
    public void setEmailTo(String emailTo) {
        this.emailTo = emailTo;
    }

    /**
     * <p>Getter for the field <code>nameTo</code>.</p>
     *
     * @return a {@link String} object.
     */
    public String getNameTo() {
        return nameTo;
    }

    /**
     * <p>Setter for the field <code>nameTo</code>.</p>
     *
     * @param nameTo a {@link String} object.
     */
    public void setNameTo(String nameTo) {
        this.nameTo = nameTo;
    }

    /**
     * <p>Getter for the field <code>content</code>.</p>
     *
     * @return a {@link String} object.
     */
    public String getContent() {
        return content;
    }

    /**
     * <p>Setter for the field <code>content</code>.</p>
     *
     * @param content a {@link String} object.
     */
    public void setContent(String content) {
        this.content = content;
    }

    /**
     * <p>Getter for the field <code>subject</code>.</p>
     *
     * @return a {@link String} object.
     */
    public String getSubject() {
        return subject;
    }

    /**
     * <p>Setter for the field <code>subject</code>.</p>
     *
     * @param subject a {@link String} object.
     */
    public void setSubject(String subject) {
        this.subject = subject;
    }

    @Override
    public String toAuditString() {
        return "ID: " + this.getId();
    }

    @Override
    public Collection<String> contentProperties() {
        return contentProperties;
    }

    public Organization getOrganization() {
        return organization;
    }

    public void setOrganization(Organization organization) {
        this.organization = organization;
    }

    @Override
    public Long getOrganizationId() {
        return organizationId;
    }

    @Formula(REFERENCE_FORMULA)
    private String referenceString;

    @Override
    public String getReferenceString() {
        return referenceString;
    }

    public void setOrganizationId(Long organizationId) {
        this.organizationId = organizationId;
    }

    public void setAttachmentURL(String attachmentURL) {
        this.attachmentURL = attachmentURL;
    }

    public String getAttachmentURL() {
        return attachmentURL;
    }

    @Override
    public String toString() {
        return getId() + ", " + emailTo + ", attempts:" + getAttempts();
    }

    public List<File> getFiles() {
        return files;
    }

    public void setFiles(List<File> files) {
        this.files = files;
    }

    public List<Long> getFilesId() {
        return filesId;
    }

    public void setFilesId(List<Long> filesId) {
        this.filesId = filesId;
    }
}
