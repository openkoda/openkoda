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

package com.openkoda.integration.form;

import com.openkoda.core.form.AbstractEntityForm;
import com.openkoda.core.form.FrontendMappingDefinition;
import com.openkoda.core.tracker.LoggingComponentWithRequestId;
import com.openkoda.integration.model.configuration.IntegrationModuleOrganizationConfiguration;
import com.openkoda.integration.model.dto.IntegrationBasecampDto;
import org.apache.commons.lang3.StringUtils;
import org.springframework.validation.BindingResult;

import java.util.function.Function;
import java.util.regex.Pattern;

public class IntegrationBasecampForm extends AbstractEntityForm<IntegrationBasecampDto, IntegrationModuleOrganizationConfiguration>
        implements LoggingComponentWithRequestId {
    public IntegrationBasecampForm() {
        super(new IntegrationBasecampDto(), null, IntegrationFrontendMappingDefinitions.basecampConfigurationForm);
    }

    public IntegrationBasecampForm(IntegrationBasecampDto dto, IntegrationModuleOrganizationConfiguration entity) {
        super(dto, entity, IntegrationFrontendMappingDefinitions.basecampConfigurationForm);
    }

    public IntegrationBasecampForm(FrontendMappingDefinition formDef) {
        super(new IntegrationBasecampDto(), null, formDef);
    }

    public IntegrationBasecampForm(IntegrationBasecampDto dto, IntegrationModuleOrganizationConfiguration entity, FrontendMappingDefinition formDef) {
        super(dto, entity, formDef);
    }

    @Override
    public IntegrationBasecampForm populateFrom(IntegrationModuleOrganizationConfiguration entity) {
        dto.setToDoListUrl(entity.getBasecampToDoListUrl());
        return this;
    }

    @Override
    protected IntegrationModuleOrganizationConfiguration populateTo(IntegrationModuleOrganizationConfiguration entity) {
        entity.setBasecampAccountId(getSafeValue(entity.getBasecampToDoListUrl(), TODO_LIST_URL_, accountIdFromUrl));
        entity.setBasecampProjectId(getSafeValue(entity.getBasecampToDoListUrl(), TODO_LIST_URL_, projectIdFromUrl));
        entity.setBasecampToDoListId(getSafeValue(entity.getBasecampToDoListUrl(), TODO_LIST_URL_, toDoListIdFromUrl));
        entity.setBasecampToDoListUrl(dto.getToDoListUrl());
        return entity;
    }

    @Override
    public IntegrationBasecampForm validate(BindingResult br) {
        if (StringUtils.isBlank(dto.getToDoListUrl())) {
            br.rejectValue("dto.toDoListUrl", "not.empty");
        }
        Pattern pattern = Pattern.compile("^(https?|ftp|file):\\/\\/3.basecamp.com\\/[0-9]*\\/buckets\\/[0-9]*\\/todolists\\/[0-9]*");
        if (!pattern.matcher(dto.getToDoListUrl()).matches()) {
            br.rejectValue("dto.toDoListUrl", "not.valid");
        }
        return null;
    }

    private Function <String, String> accountIdFromUrl = ((String s) -> StringUtils.substringBetween(dto.getToDoListUrl(), ".com/", "/"));
    private Function <String, String> projectIdFromUrl = ((String s) -> StringUtils.substringBetween(dto.getToDoListUrl(), "buckets/", "/"));
    private Function <String, String> toDoListIdFromUrl = ((String s) -> StringUtils.substringAfterLast(dto.getToDoListUrl(), "todolists/"));
}
