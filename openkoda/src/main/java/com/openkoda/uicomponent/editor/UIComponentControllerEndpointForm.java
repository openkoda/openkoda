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
import com.openkoda.model.ControllerEndpoint;
import org.springframework.validation.BindingResult;

public class UIComponentControllerEndpointForm<CD extends UIComponentControllerEndpointDto> extends AbstractOrganizationRelatedEntityForm<CD, ControllerEndpoint> {

    public UIComponentControllerEndpointForm() {
        super(null, (CD)new UIComponentControllerEndpointDto(), null, FrontendMappingDefinitions.uiComponentControllerEndpointForm);
    }

    public UIComponentControllerEndpointForm(Long organizationId, ControllerEndpoint entity) {
        this(organizationId, entity, FrontendMappingDefinitions.uiComponentControllerEndpointForm);
    }

    public UIComponentControllerEndpointForm(Long organizationId, long frontendResourceId) {
        super(organizationId, (CD)new UIComponentControllerEndpointDto(organizationId, frontendResourceId), null, FrontendMappingDefinitions.uiComponentControllerEndpointForm);
    }

    public UIComponentControllerEndpointForm(Long organizationId, ControllerEndpoint entity, FrontendMappingDefinition frontendMappingDefinition) {
        super(organizationId, (CD)new UIComponentControllerEndpointDto(), entity, frontendMappingDefinition);
    }

    @Override
    protected UIComponentControllerEndpointForm populateFrom(ControllerEndpoint entity) {
        debug("[populateFrom] {}", entity);
        dto.organizationId = entity.getOrganizationId();
        dto.subPath = entity.getSubPath();
        dto.code = entity.getCode();
        dto.httpHeaders = entity.getHttpHeaders();
        dto.httpMethod = entity.getHttpMethod();
        dto.modelAttributes = entity.getModelAttributes();
        dto.responseType = entity.getResponseType();
        dto.frontendResourceId = entity.getFrontendResourceId();
        return this;
    }

    @Override
    protected ControllerEndpoint populateTo(ControllerEndpoint entity) {
        entity.setSubPath(getSafeValue(entity.getSubPath(), SUB_PATH, emptyIfBlank));
        entity.setCode(getSafeValue(entity.getCode(), CODE, nullIfBlank));
        entity.setHttpHeaders(getSafeValue(entity.getHttpHeaders(), HTTP_HEADERS, nullIfBlank));
        entity.setHttpMethod(getSafeValue(entity.getHttpMethod(), HTTP_METHOD));
        entity.setModelAttributes(getSafeValue(entity.getModelAttributes(), MODEL_ATTRIBUTES, nullIfBlank));
        entity.setResponseType(getSafeValue(entity.getResponseType(), RESPONSE_TYPE));
        return entity;
    }

    @Override
    public <F extends Form> F validate(BindingResult br) {
        return null;
    }


}
