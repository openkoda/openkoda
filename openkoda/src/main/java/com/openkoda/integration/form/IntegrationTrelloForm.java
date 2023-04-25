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
import com.openkoda.core.tracker.LoggingComponentWithRequestId;
import com.openkoda.integration.model.configuration.IntegrationModuleOrganizationConfiguration;
import com.openkoda.integration.model.dto.IntegrationTrelloDto;
import org.apache.commons.lang3.StringUtils;
import org.springframework.validation.BindingResult;

public class IntegrationTrelloForm extends AbstractEntityForm<IntegrationTrelloDto, IntegrationModuleOrganizationConfiguration>
        implements LoggingComponentWithRequestId {

    public static final String LOWER_CASE_ALPHANUMERIC_REGEX = "[0-9a-z]*";

    public IntegrationTrelloForm() {
        super(new IntegrationTrelloDto(), null, IntegrationFrontendMappingDefinitions.trelloConfigurationForm);
    }

    public IntegrationTrelloForm(IntegrationTrelloDto dto, IntegrationModuleOrganizationConfiguration entity) {
        super(dto, entity, IntegrationFrontendMappingDefinitions.trelloConfigurationForm);
    }

    @Override
    public IntegrationTrelloForm populateFrom(IntegrationModuleOrganizationConfiguration entity) {
        dto.setTrelloApiKey(entity.getTrelloApiKey());
        dto.setTrelloApiToken(entity.getTrelloApiToken());
        dto.setTrelloBoardName(entity.getTrelloBoardName());
        dto.setTrelloListName(entity.getTrelloListName());
        return this;
    }

    @Override
    protected IntegrationModuleOrganizationConfiguration populateTo(IntegrationModuleOrganizationConfiguration entity) {
        entity.setTrelloApiKey(getSafeValue(entity.getTrelloApiKey(), TRELLO_API_KEY_));
        entity.setTrelloApiToken(getSafeValue(entity.getTrelloApiToken(), TRELLO_API_TOKEN_));
        entity.setTrelloBoardName(getSafeValue(entity.getTrelloBoardName(), TRELLO_BOARD_NAME_));
        entity.setTrelloListName(getSafeValue(entity.getTrelloListName(), TRELLO_LIST_NAME_));
        return entity;
    }

    @Override
    public IntegrationTrelloForm validate(BindingResult br) {
        if (StringUtils.isBlank(dto.getTrelloApiKey())) {
            br.rejectValue("dto.trelloApiKey", "not.empty");
        }
        if (!dto.getTrelloApiKey().matches(LOWER_CASE_ALPHANUMERIC_REGEX)) {
            br.rejectValue("dto.trelloApiKey", "is.alphanumeric");
        }
        if (dto.getTrelloApiKey().length() != 32) {
            br.rejectValue("dto.trelloApiKey", "wrong.size");
        }
        if (StringUtils.isBlank(dto.getTrelloApiToken())) {
            br.rejectValue("dto.trelloApiToken", "not.empty");
        }
        if (!dto.getTrelloApiToken().matches(LOWER_CASE_ALPHANUMERIC_REGEX)) {
            br.rejectValue("dto.trelloApiToken", "is.alphanumeric");
        }
        if (dto.getTrelloApiToken().length() != 64) {
            br.rejectValue("dto.trelloApiToken", "wrong.size");
        }
        if (StringUtils.isBlank(dto.getTrelloBoardName())) {
            br.rejectValue("dto.trelloBoardName", "not.empty");
        }
        if (StringUtils.isBlank(dto.getTrelloListName())) {
            br.rejectValue("dto.trelloListName", "not.empty");
        }
        return this;
    }
}
