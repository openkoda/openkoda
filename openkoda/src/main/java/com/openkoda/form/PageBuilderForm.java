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

package com.openkoda.form;

import com.openkoda.core.form.AbstractOrganizationRelatedEntityForm;
import com.openkoda.core.form.FrontendMappingDefinition;
import com.openkoda.dto.system.FrontendResourceDto;
import com.openkoda.model.FrontendResource;
import org.springframework.validation.BindingResult;

import static com.openkoda.controller.common.URLConstants.FRONTENDRESOURCEREGEX;
import static com.openkoda.core.form.FrontendMappingDefinition.createFrontendMappingDefinition;
import static com.openkoda.form.FrontendMappingDefinitions.PAGE_BUILDER_FORM;
import static com.openkoda.model.Privilege.manageFrontendResource;
import static com.openkoda.model.Privilege.readFrontendResource;

/**
 * @author Martyna Litkowska (mlitkowska@stratoflow.com)
 * @since 2019-02-19
 */
public class PageBuilderForm<CD extends FrontendResourceDto> extends AbstractOrganizationRelatedEntityForm<CD, FrontendResource> {

    public final static FrontendMappingDefinition pageBuilderForm = createFrontendMappingDefinition(PAGE_BUILDER_FORM, readFrontendResource, manageFrontendResource,
            a -> a  .text(NAME_)                    .validate(v -> v.matches(FRONTENDRESOURCEREGEX) ? null : "not.matching.name")
                    .text(CONTENT_)

    );


    public PageBuilderForm() {
        super(null, (CD)new FrontendResourceDto(), null, pageBuilderForm);
    }

    public PageBuilderForm(FrontendMappingDefinition frontendMappingDefinition) {
        super(frontendMappingDefinition);
    }

    public PageBuilderForm(CD dto) {
        super(null, dto, null, pageBuilderForm);
    }

    public PageBuilderForm(Long organizationId, FrontendResource entity) {
        this(organizationId, entity, pageBuilderForm);
    }

    public PageBuilderForm(Long organizationId, FrontendResource entity, FrontendMappingDefinition frontendMappingDefinition) {
        super(organizationId, (CD)new FrontendResourceDto(), entity, frontendMappingDefinition);
    }

    public PageBuilderForm(Long organizationId, CD dto, FrontendResource entity, FrontendMappingDefinition frontendMappingDefinition) {
        super(organizationId, dto, entity, frontendMappingDefinition);
    }

    @Override
    public PageBuilderForm populateFrom(FrontendResource entity) {
        debug("[populateFrom] {}", entity);
        dto.name = entity.getName();
        dto.content = entity.isDraft() ? entity.getDraftContent() : entity.getContent();
        return this;
    }

    @Override
    protected FrontendResource populateTo(FrontendResource entity) {
        entity.setName(getSafeValue(entity.getName(), NAME_));
        entity.setContent(getSafeValue(entity.getContent(), CONTENT_, nullIfBlank));
        entity.setType(FrontendResource.Type.JSON);
        entity.setIncludeInSitemap(false);
        entity.setEmbeddable(false);
        entity.setRequiredPrivilege(null);
        entity.setResourceType(FrontendResource.ResourceType.DASHBOARD);
        return entity;
    }

    @Override
    public PageBuilderForm validate(BindingResult br) {
        debug("[validate]");
        return this;
    }

}
