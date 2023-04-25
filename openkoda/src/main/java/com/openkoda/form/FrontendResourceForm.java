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

package com.openkoda.form;

import com.openkoda.core.form.AbstractOrganizationRelatedEntityForm;
import com.openkoda.core.form.FieldType;
import com.openkoda.core.form.FrontendMappingDefinition;
import com.openkoda.core.service.FrontendResourceService;
import com.openkoda.dto.system.CmsDto;
import com.openkoda.model.FrontendResource;
import org.apache.commons.lang3.StringUtils;
import org.springframework.validation.BindingResult;

/**
 * @author Martyna Litkowska (mlitkowska@stratoflow.com)
 * @since 2019-02-19
 */
public class FrontendResourceForm<CD extends CmsDto> extends AbstractOrganizationRelatedEntityForm<CD, FrontendResource> {


    public FrontendResourceForm() {
        super(null, (CD)new CmsDto(), null, FrontendMappingDefinitions.frontendResourceForm);
    }

    public FrontendResourceForm(FrontendMappingDefinition frontendMappingDefinition) {
        super(frontendMappingDefinition);
    }

    public FrontendResourceForm(CD dto) {
        super(null, dto, null, FrontendMappingDefinitions.frontendResourceForm);
    }

    public FrontendResourceForm(Long organizationId, FrontendResource entity) {
        this(organizationId, entity, FrontendMappingDefinitions.frontendResourceForm);
    }

    public FrontendResourceForm(Long organizationId, FrontendResource entity, FrontendMappingDefinition frontendMappingDefinition) {
        super(organizationId, (CD)new CmsDto(), entity, frontendMappingDefinition);
    }

    public FrontendResourceForm(Long organizationId, CD dto, FrontendResource entity, FrontendMappingDefinition frontendMappingDefinition) {
        super(organizationId, dto, entity, frontendMappingDefinition);
    }

    @Override
    public FrontendResourceForm populateFrom(FrontendResource entity) {
        debug("[populateFrom] {}", entity);
        dto.name = entity.getName();
        dto.organizationId = entity.getOrganizationId();
        dto.urlPath = entity.getUrlPath();
        dto.content = entity.isDraft() ? entity.getDraftContent() : entity.getContent();
        dto.contentEditable = FrontendResourceService.CONTENT_EDITABLE_BEGIN + StringUtils.substringBetween(dto.content, FrontendResourceService.CONTENT_EDITABLE_BEGIN, FrontendResourceService.CONTENT_EDITABLE_END) + FrontendResourceService.CONTENT_EDITABLE_END;
        dto.requiredPrivilege = entity.getRequiredPrivilege();
        dto.includeInSitemap = entity.getIncludeInSitemap();
        dto.type = entity.getType();
        return this;
    }

    static FieldType getCodeType(Object e) {
        if (e == null) {
            return FieldType.code_html;
        }
        switch (FrontendResource.Type.valueOf(e + "")) {
            case JS:
                return FieldType.code_js;
            case CSS:
                return FieldType.code_css;
            default:
                return FieldType.code_html;
        }
    }

    @Override
    protected FrontendResource populateTo(FrontendResource entity) {
        entity.setName(getSafeValue(entity.getName(), NAME_));
        entity.setOrganizationId(getSafeValue(entity.getOrganizationId(), ORGANIZATION_ID_));
        entity.setUrlPath(getSafeValue(entity.getUrlPath(), URL_PATH_, nullIfBlank));
        entity.setDraftContent(getSafeValue(entity.getDraftContent(), CONTENT_, nullIfBlank));
        entity.setType(getSafeValue(entity.getType(), TYPE_));
        entity.setIncludeInSitemap(getSafeValue(entity.getIncludeInSitemap(), INCLUDE_IN_SITEMAP_));
        entity.setRequiredPrivilege(getSafeValue(entity.getRequiredPrivilege(), REQUIRED_PRIVILEGE_, nullIfBlank));
        return entity;
    }

    @Override
    public FrontendResourceForm validate(BindingResult br) {
        debug("[validate]");
        return this;
    }

}
