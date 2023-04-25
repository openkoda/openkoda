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

package com.openkoda.form;

import com.openkoda.core.form.AbstractOrganizationRelatedEntityForm;
import com.openkoda.dto.ServerJsDto;
import com.openkoda.model.ServerJs;
import org.apache.commons.lang3.StringUtils;
import org.springframework.validation.BindingResult;

public class ServerJsForm extends AbstractOrganizationRelatedEntityForm<ServerJsDto, ServerJs> {

    public ServerJsForm() {
        super(ServerJsFrontendMappingDefinitions.serverJsFrontendMappingDefinition);
    }

    public ServerJsForm(Long organizationId, ServerJs entity) {
        super(organizationId, new ServerJsDto(), entity, ServerJsFrontendMappingDefinitions.serverJsFrontendMappingDefinition);
    }

    @Override
    public ServerJsForm populateFrom(ServerJs entity) {
        
        dto.name = entity.getName();
        dto.code = entity.getCode();
        dto.model = entity.getModel();
        dto.arguments = entity.getArguments();
        return this;
    }

    @Override
    protected ServerJs populateTo(ServerJs entity) {
        
        entity.setName(getSafeValue(entity.getName(), ServerJsFrontendMappingDefinitions.NAME_));
        entity.setCode(getSafeValue(entity.getCode(), ServerJsFrontendMappingDefinitions.CODE_));
        entity.setModel(getSafeValue(entity.getModel(), ServerJsFrontendMappingDefinitions.MODEL_));
        entity.setArguments(getSafeValue(entity.getArguments(), ServerJsFrontendMappingDefinitions.ARGUMENTS_));
        return entity;
    }

    @Override
    public ServerJsForm validate(BindingResult br) {
        debug("[validate]");
        if (StringUtils.isBlank(dto.name)) {
            br.rejectValue("dto.name", "not.empty");
        }
        if (StringUtils.isBlank(dto.code)) {
            br.rejectValue("dto.code", "not.empty");
        }
        return this;
    }
}