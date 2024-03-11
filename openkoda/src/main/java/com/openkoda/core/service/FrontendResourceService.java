/*
MIT License

Copyright (c) 2016-2023, Openkoda CDX Sp. z o.o. Sp. K. <openkoda.com>

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

package com.openkoda.core.service;

import com.openkoda.controller.ComponentProvider;
import com.openkoda.core.exception.FrontendResourceValidationException;
import com.openkoda.model.component.FrontendResource;
import jakarta.annotation.PostConstruct;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Entities;
import org.jsoup.safety.Whitelist;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.stream.Stream;

import static com.openkoda.service.export.FolderPathConstants.SUBDIR_ORGANIZATION_PREFIX;

@Service
/**
 * <p>FrontendResourceEntryService class.</p>
 *
 * @author Arkadiusz Drysch (adrysch@stratoflow.com)
 * 
 */
@Component("frontendResource")
public class FrontendResourceService extends ComponentProvider {

    private FrontendResourceService instance;

    public static final String CONTENT_EDITABLE_BEGIN = "<!--CONTENT EDITABLE START-->";
    public static final String CONTENT_EDITABLE_END = "<!--CONTENT EDITABLE END-->";
    public static final String frontendResourceTemplateNamePrefix = "frontend-resource/";
    public static final String frontendResourceXmlNamePrefix = "templates/xml/";
    public static final String frontendResourceFolderClasspath = "/templates/frontend-resource/";

    @Value("${default.frontendResourcePage.template.name:frontend-resource-template}")
    String defaultFrontendResourcePageTemplate;

    @Value("${base.url:http://localhost:8080}")
    String baseUrl;


    @PostConstruct
    void init() {
        instance = this;
    }

    private Document.OutputSettings outputSettings =
            new Document.OutputSettings()
                    .prettyPrint(false)
                    .syntax(Document.OutputSettings.Syntax.html)
                    .escapeMode(Entities.EscapeMode.base);

    /**
     * <p>validateContent.</p>
     *
     * @param dtoFieldName
     * @param content a {@link java.lang.String} object.
     * @return a boolean.
     */
    public boolean validateContent(String content, BindingResult br, String dtoFieldName) {
        debug("[validateContent] content: {}", content);
        String cleaned = Jsoup.clean(content, baseUrl, Whitelist.relaxed(), outputSettings);
        if (!content.equals(cleaned)) {
            if(br != null) {
                br.rejectValue(dtoFieldName, "not.valid");
            }
            throw new FrontendResourceValidationException(false, cleaned);
        }
        return true;
    }

    public boolean checkNameExists(String name, BindingResult br) {
        debug("[checkNameExists] {}", name);
        boolean exists = repositories.unsecure.frontendResource.findByName(name) != null;
        if (exists && br != null) {
            br.rejectValue("dto.name", "name.exists");
        }
        return exists;
    }

    public FrontendResource publish(FrontendResource frontendResource) {
        debug("[publish] {}", frontendResource);
        if (not(frontendResource.isDraft())) {
            return frontendResource;
        }
        frontendResource.setContent( frontendResource.getDraftContent() );
        frontendResource.setDraftContent( null );
        return frontendResource;
    }

    public FrontendResource clear(FrontendResource frontendResource) {
        debug("[clear] {}", frontendResource);
        if (not(frontendResource.isContentExists())) {
            return frontendResource;
        }
        frontendResource.setDraftContent( frontendResource.getContent() );
        frontendResource.setContent( null );
        return frontendResource;
    }

    public Stream<FrontendResource> publishAll(Stream<FrontendResource> result) {
        debug("[publishAll]");
        result.forEach( a -> repositories.unsecure.frontendResource.save(publish(a)));
        return result;
    }

    public Stream<FrontendResource> clearAll(Stream<FrontendResource> result) {
        debug("[clearAll]");
        result.forEach( a -> repositories.unsecure.frontendResource.save(clear(a)));
        return result;
    }

    public String getContentOrDefault(FrontendResource.Type type, String frontendResourceName, FrontendResource.AccessLevel accessLevel, Long organizationId) {
        debug("[getContent] {}", frontendResourceName);
        String contentBasePath = frontendResourceFolderClasspath;
        contentBasePath += accessLevel.getPath();

        String resourceName = (organizationId == null || accessLevel == FrontendResource.AccessLevel.GLOBAL)
                ?
                contentBasePath + frontendResourceName + type.getExtension() :
                contentBasePath + SUBDIR_ORGANIZATION_PREFIX + organizationId + "/" + frontendResourceName + type.getExtension();
        try {
            InputStream resourceIO = this.getClass().getResourceAsStream(resourceName);
            if (resourceIO != null) {
                return IOUtils.toString(resourceIO);
            }
        } catch (IOException e) {
            error("Could not load FrontendResource from templates", e);
        }
        return "";
    }

    public URL resourceURL(FrontendResource frontendResource) {
        String extension = frontendResource.getType() == FrontendResource.Type.HTML ? ".html" : "";
        return  frontendResource.getOrganizationId() == null ?
                this.getClass().getResource(frontendResourceFolderClasspath
                        + frontendResource.getAccessLevel().getPath()
                        + frontendResource.getName()
                        + extension)
                : this.getClass().getResource(frontendResourceFolderClasspath
                        + frontendResource.getAccessLevel().getPath()
                        + SUBDIR_ORGANIZATION_PREFIX
                        + frontendResource.getOrganizationId()
                        + "/"
                        + frontendResource.getName()
                        + extension);
    }

    public boolean resourceExists(FrontendResource frontendResource) {
        return resourceURL(frontendResource) != null;
    }

    public FrontendResource prepareFrontendResourcePage(Long frontendResourceId, String contentEditable) {
        FrontendResource frontendResource = frontendResourceId == null ? new FrontendResource() : repositories.unsecure.frontendResource.findOne(frontendResourceId);
        FrontendResource baseFrontendResource = frontendResourceId == null ? repositories.unsecure.frontendResource.findByName(defaultFrontendResourcePageTemplate) : frontendResource;
        String before = StringUtils.substringBefore(baseFrontendResource.getContent(), CONTENT_EDITABLE_BEGIN);
        String after = StringUtils.substringAfter(baseFrontendResource.getContent(), CONTENT_EDITABLE_END);
        frontendResource.setContent(before + contentEditable + after);
        return frontendResource;
    }

    public FrontendResource prepareFrontendResourcePageEntity(Long frontendResourceId) {
        FrontendResource result = null;
        if (frontendResourceId == null) {
            result = new FrontendResource();
            FrontendResource base = repositories.unsecure.frontendResource.findByName(defaultFrontendResourcePageTemplate);
            result.setContent(base.getContent());
        } else {
            result = repositories.unsecure.frontendResource.findOne(frontendResourceId);
        }
        return result;
    }

    public String resourceHash(FrontendResource frontendResource) {
        String content = getContentOrDefault(frontendResource.getType(), frontendResource.getName(), frontendResource.getAccessLevel(), frontendResource.getOrganizationId());
        String md5 = DigestUtils.md5Hex(content);
        return StringUtils.substring(md5, 0, FrontendResource.HASH_TRUNCATED_LENGTH);
    }

    public String getResourceURL(FrontendResource frontendResource) {
        URL url = resourceURL(frontendResource);
        return url == null ? "" : url.toString();
    }

}
