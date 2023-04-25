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

import com.openkoda.core.form.FrontendMappingDefinition;
import com.openkoda.form.TemplateFormFieldNames;

import static com.openkoda.core.form.FrontendMappingDefinition.createFrontendMappingDefinition;
import static com.openkoda.model.Privilege.manageOrgData;
import static com.openkoda.model.Privilege.readOrgData;

public interface IntegrationFrontendMappingDefinitions extends TemplateFormFieldNames {
    String TRELLO_CONFIGURATION_FORM = "trelloConfigurationForm";
    String GITHUB_CONFIGURATION_FORM = "gitHubConfigurationForm";
    String JIRA_CONFIGURATION_FORM = "jiraConfigurationForm";
    String BASECAMP_CONFIGURATION_FORM = "basecampConfigurationForm";
    String SLACK_CONFIGURATION_FORM = "slackConfigurationForm";
    String MSTEAMS_CONFIGURATION_FORM = "msTeamsConfigurationForm";
    String EMAIL_CONFIGURATION_FORM = "emailConfigurationForm";

    FrontendMappingDefinition trelloConfigurationForm = createFrontendMappingDefinition(TRELLO_CONFIGURATION_FORM, readOrgData, manageOrgData,
            a -> a  .text(TRELLO_API_KEY_)
                    .text(TRELLO_API_TOKEN_)
                    .text(TRELLO_BOARD_NAME_)
                    .text(TRELLO_LIST_NAME_)
    );

    FrontendMappingDefinition gitHubConfigurationForm = createFrontendMappingDefinition(GITHUB_CONFIGURATION_FORM, readOrgData, manageOrgData,
            a -> a  .text(GITHUB_REPO_OWNER_)
                    .text(GITHUB_REPO_NAME_)
    );

    FrontendMappingDefinition gitHubConfigurationFormDisabled = createFrontendMappingDefinition(GITHUB_CONFIGURATION_FORM, readOrgData, null,
            a -> a  .text(GITHUB_REPO_NAME_)
                    .text(GITHUB_REPO_OWNER_)
    );

    FrontendMappingDefinition slackConfigurationForm = createFrontendMappingDefinition(SLACK_CONFIGURATION_FORM, readOrgData, manageOrgData,
            a -> a  .text(WEBHOOK_URL_)
    );

    FrontendMappingDefinition msTeamsConfigurationForm = createFrontendMappingDefinition(MSTEAMS_CONFIGURATION_FORM, readOrgData, manageOrgData,
            a -> a  .text(WEBHOOK_URL_)
    );

    FrontendMappingDefinition emailConfigurationForm = createFrontendMappingDefinition(EMAIL_CONFIGURATION_FORM, readOrgData, manageOrgData,
            a -> a  .text(EMAIL_)
    );

    FrontendMappingDefinition jiraConfigurationForm = createFrontendMappingDefinition(JIRA_CONFIGURATION_FORM, readOrgData, manageOrgData,
            a -> a  .text(ORGANIZATION_NAME_)
                    .text(PROJECT_NAME_)
    );

    FrontendMappingDefinition jiraConfigurationFormDisabled = createFrontendMappingDefinition(JIRA_CONFIGURATION_FORM, readOrgData, null,
            a -> a  .text(ORGANIZATION_NAME_)
                    .text(PROJECT_NAME_)
    );

    FrontendMappingDefinition basecampConfigurationForm = createFrontendMappingDefinition(BASECAMP_CONFIGURATION_FORM, readOrgData, manageOrgData,
            a -> a  .text(TODO_LIST_URL_)
    );

    FrontendMappingDefinition basecampConfigurationFormDisabled = createFrontendMappingDefinition(BASECAMP_CONFIGURATION_FORM, readOrgData, null,
            a -> a  .text(TODO_LIST_URL_)
    );

}
