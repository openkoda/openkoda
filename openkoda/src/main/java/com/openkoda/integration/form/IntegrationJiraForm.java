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

package com.openkoda.integration.form;

import com.openkoda.core.form.AbstractEntityForm;
import com.openkoda.core.form.FrontendMappingDefinition;
import com.openkoda.core.tracker.LoggingComponentWithRequestId;
import com.openkoda.integration.model.configuration.IntegrationModuleOrganizationConfiguration;
import com.openkoda.integration.model.dto.IntegrationJiraDto;
import org.springframework.validation.BindingResult;

public class IntegrationJiraForm extends AbstractEntityForm<IntegrationJiraDto, IntegrationModuleOrganizationConfiguration> implements LoggingComponentWithRequestId {
    public IntegrationJiraForm() {
        super(new IntegrationJiraDto(), null, IntegrationFrontendMappingDefinitions.jiraConfigurationForm);
    }

    public IntegrationJiraForm(IntegrationJiraDto integrationJiraDto, IntegrationModuleOrganizationConfiguration organizationConfiguration) {
        super(integrationJiraDto, organizationConfiguration, IntegrationFrontendMappingDefinitions.jiraConfigurationForm);
    }

    public IntegrationJiraForm(IntegrationJiraDto integrationJiraDto, IntegrationModuleOrganizationConfiguration organizationConfiguration, FrontendMappingDefinition formDef) {
        super(integrationJiraDto, organizationConfiguration, formDef);
    }

    @Override
    public IntegrationJiraForm populateFrom(IntegrationModuleOrganizationConfiguration entity) {
        dto.setProjectName(entity.getJiraProjectName());
        dto.setOrganizationName(entity.getJiraOrganizationName());
        return this;
    }

    @Override
    protected IntegrationModuleOrganizationConfiguration populateTo(IntegrationModuleOrganizationConfiguration entity) {
        entity.setJiraProjectName(getSafeValue(entity.getJiraProjectName(), PROJECT_NAME_));
        entity.setJiraOrganizationName(getSafeValue(entity.getJiraOrganizationName(), ORGANIZATION_NAME_));
        return entity;
    }

    @Override
    public IntegrationJiraForm validate(BindingResult br) {
        return this;
    }
}
