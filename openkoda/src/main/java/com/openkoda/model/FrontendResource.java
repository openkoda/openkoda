/*
MIT License

Copyright (c) 2016-2022, Codedose CDX Sp. z o.o. Sp. K. <stratoflow.com>

Permission is hereby granted, free of charge, to any person obtaining a copy of this software
 and associated documentation files (the "Software"), to deal in the Software without restriction, 
including without limitation the rights to use, copy, modify, merge, publish, distribute, 
sublicense, and/or sell copies of the Software, and to permit persons to whom the Software 
is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice 
shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, 
EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES 
OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE 
AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS 
OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES 
OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, 
TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION 
WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*/

package com.openkoda.model;

import com.openkoda.model.common.ModelConstants;
import com.openkoda.model.common.OpenkodaEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.Formula;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

@Entity
/**
 * <p>FrontendResource class.</p>
 *
 * @author Arkadiusz Drysch (adrysch@stratoflow.com)
 * 
 */
@Table (
    name = "frontend_resource",
    uniqueConstraints = @UniqueConstraint(columnNames = "name")
)
public class FrontendResource extends OpenkodaEntity {

    final static List<String> contentProperties = Arrays.asList("content", "draftContent");
    public static final int HASH_TRUNCATED_LENGTH = 15;

    public enum Type {
        HTML(".html"), CSS(".css"), JS(".js"), XML(".xml"), PAGE(".html"), UI_COMPONENT(".html");

        private String extension;

        Type(String extension) {
            this.extension = extension;
        }

        public String getExtension() {
            return extension;
        }

        public static Type getEntryTypeFromPath(String path) {
            if (path.endsWith(".js")) {
                return Type.JS;
            }
            if (path.endsWith(".css")) {
                return Type.CSS;
            }
            if (path.endsWith(".xml")) {
                return Type.XML;
            }
            return Type.HTML;
        }
    }

    @NotNull
    private String name;

    @Column(name = "url_path")
    private String urlPath;

    @Column(length = 65536 * 4)
    private String content;

    @Column(name = "draft_content", length = 65536 * 4)
    private String draftContent;

    @Column(name = ModelConstants.REQUIRED_PRIVILEGE_COLUMN)
    private String requiredPrivilege;

    @Column(name = "include_in_sitemap")
    private boolean includeInSitemap;

    @Formula(" (url_path is not null) ")
    private boolean isPage;

    @Formula(" (required_privilege is null) ")
    private boolean unsecured;

    @Formula(" (draft_content is not null) ")
    private boolean draft;

    @Formula(" (content is not null) ")
    private boolean contentExists;

    @Formula(" (LEFT(MD5(content), " + HASH_TRUNCATED_LENGTH + " )) ")
    private String contentHash;

    @Formula(" (LEFT(MD5(draft_content), " + HASH_TRUNCATED_LENGTH + " )) ")
    private String draftHash;

    @Enumerated(EnumType.STRING)
    @NotNull
    private Type type = Type.HTML;

    @Column(columnDefinition = "boolean default false")
    private boolean isPublic;

    @Formula("( '" + PrivilegeNames._readFrontendResource + "' )")
    private String requiredReadPrivilege;

    @Formula("( '" + PrivilegeNames._manageFrontendResource + "' )")
    private String requiredWritePrivilege;

    public FrontendResource(String name, String urlPath, String content, Enum requiredPrivilege, Type type) {
        super(null);
        this.name = name;
        this.urlPath = urlPath;
        this.content = content;
        this.type = type;
        this.requiredPrivilege = requiredPrivilege == null ? null : requiredPrivilege.name();
    }

    public FrontendResource() {
        super(null);
    }

    public FrontendResource(Long organizationId) {
        super(organizationId);
    }

    public boolean isUnsecured() {
        return unsecured;
    }

    /**
     * <p>Getter for the field <code>name</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getName() {
        return name;
    }

    /**
     * <p>Setter for the field <code>name</code>.</p>
     *
     * @param name a {@link java.lang.String} object.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * <p>Getter for the field <code>content</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getContent() {
        return content;
    }

    public boolean isPage() {
        return isPage;
    }

    public String getUrlPath() {
        return urlPath;
    }

    public void setUrlPath(String urlPath) {
        this.urlPath = urlPath;
    }

    public boolean getIncludeInSitemap() {
        return includeInSitemap;
    }

    public void setIncludeInSitemap(boolean includeInSitemap) {
        this.includeInSitemap = includeInSitemap;
    }

    public String getRequiredPrivilege() {
        return requiredPrivilege;
    }

    public void setRequiredPrivilege(String requiredPrivilege) {
        this.requiredPrivilege = requiredPrivilege;
    }

    /**
     * <p>Setter for the field <code>content</code>.</p>
     *
     * @param content a {@link java.lang.String} object.
     * @return a {@link FrontendResource} object.
     */
    public void setContent(String content) {
        this.content = content;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    @Override
    public Collection<String> contentProperties() {
        return contentProperties;
    }

    public String getDraftContent() {
        return draftContent;
    }

    public void setDraftContent(String draftContent) {
        this.draftContent = draftContent;
    }

    public boolean isDraft() {
        return draft;
    }

    public boolean isContentExists() {
        return contentExists;
    }

    public String getContentHash() { return contentHash == null ? "" : contentHash; }

    public void setContentHash(String contentHash) { this.contentHash = contentHash; }

    public String getDraftHash() { return draftHash == null ? "" : draftHash; }

    public void setDraftHash(String draftHash) { this.draftHash = draftHash; }

    @Override
    public String getRequiredReadPrivilege() {
        return requiredReadPrivilege;
    }

    @Override
    public String getRequiredWritePrivilege() {
        return requiredWritePrivilege;
    }
    public boolean isPublic() {
        return isPublic;
    }

    public void setPublic(boolean aPublic) {
        isPublic = aPublic;
    }

}
