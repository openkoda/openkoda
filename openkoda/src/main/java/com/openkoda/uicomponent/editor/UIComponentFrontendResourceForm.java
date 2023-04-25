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

import com.openkoda.core.form.AbstractOrganizationRelatedEntityForm;
import com.openkoda.core.form.Form;
import com.openkoda.core.form.FrontendMappingDefinition;
import com.openkoda.form.FrontendMappingDefinitions;
import com.openkoda.model.FrontendResource;
import org.springframework.validation.BindingResult;

public class UIComponentFrontendResourceForm<CD extends UIComponentFrontendResourceDto> extends AbstractOrganizationRelatedEntityForm<CD, FrontendResource> {

    public UIComponentFrontendResourceForm() {
        super(null, (CD)new UIComponentFrontendResourceDto(), null, FrontendMappingDefinitions.uiComponentFrontendResourceForm);
    }

    public UIComponentFrontendResourceForm(Long organizationId, FrontendResource entity) {
        this(organizationId, entity, FrontendMappingDefinitions.uiComponentFrontendResourceForm);
    }

    public UIComponentFrontendResourceForm(Long organizationId, FrontendResource entity, FrontendMappingDefinition frontendMappingDefinition) {
        super(organizationId, (CD)new UIComponentFrontendResourceDto(), entity, frontendMappingDefinition);
    }

    @Override
    protected UIComponentFrontendResourceForm populateFrom(FrontendResource entity) {
        debug("[populateFrom] {}", entity);
        dto.organizationId = entity.getOrganizationId();
        dto.urlPath = entity.getUrlPath();
        dto.content = entity.getContent();
        dto.isPublic = entity.isPublic();
        return this;
    }

    @Override
    protected FrontendResource populateTo(FrontendResource entity) {
        entity.setUrlPath(getSafeValue(entity.getUrlPath(), URL_PATH_, emptyIfBlank));
        entity.setContent(getSafeValue(entity.getContent(), CONTENT_, nullIfBlank));
        entity.setName(getSafeValue(entity.getUrlPath(), URL_PATH_, nullIfBlank));
        entity.setPublic(getSafeValue(entity.isPublic(), IS_PUBLIC));
        entity.setType(FrontendResource.Type.UI_COMPONENT);
        return entity;
    }

    @Override
    public <F extends Form> F validate(BindingResult br) {
        return null;
    }

}
