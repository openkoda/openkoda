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

package com.openkoda.integration.service;

import com.openkoda.integration.controller.IntegrationComponentProvider;
import com.openkoda.integration.model.configuration.IntegrationModuleGlobalConfiguration;
import com.openkoda.integration.model.configuration.IntegrationModuleOrganizationConfiguration;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;


@Component("integrationCheck")
public class IntegrationCheckService extends IntegrationComponentProvider {

    public boolean isJiraConfiguredGlobally(){
        IntegrationModuleGlobalConfiguration globalConfig = integrationService.getGlobalConfiguration();
        String jiraClientId = globalConfig.jiraClientId;
        String jiraClientSecret = globalConfig.jiraClientSecret;
        return StringUtils.isNotBlank(jiraClientId) && StringUtils.isNotBlank(jiraClientSecret);
    }

    public boolean isBasecampConfiguredGlobally(){
        IntegrationModuleGlobalConfiguration globalConfig = integrationService.getGlobalConfiguration();
        String basecampClientId = globalConfig.basecampClientId;
        String basecampClientSecret = globalConfig.basecampClientSecret;
        return StringUtils.isNotBlank(basecampClientId) && StringUtils.isNotBlank(basecampClientSecret);
    }

    public boolean isGitHubConfiguredGlobally(){
        IntegrationModuleGlobalConfiguration globalConfig = integrationService.getGlobalConfiguration();
        String gitHubClientId = globalConfig.gitHubClientId;
        String gitHubClientSecret = globalConfig.gitHubClientSecret;
        return StringUtils.isNotBlank(gitHubClientId) && StringUtils.isNotBlank(gitHubClientSecret);
    }

    public boolean isGitHubConnected(Long orgId) {
        return StringUtils.isNotEmpty(integrationService.getOrganizationConfiguration(orgId).getGitHubToken());
    }

    public boolean isJiraConnected(Long orgId) {
        return StringUtils.isNotEmpty(integrationService.getOrganizationConfiguration(orgId).getJiraToken());
    }

    public boolean isBasecampConnected(Long orgId) {
        return StringUtils.isNotEmpty(integrationService.getOrganizationConfiguration(orgId).getBasecampAccessToken());
    }


    public boolean isTrelloIntegrated(Long orgId) {
        IntegrationModuleOrganizationConfiguration config = integrationService.getOrganizationConfiguration(orgId);
        return StringUtils.isNotBlank(config.getTrelloApiKey()) && StringUtils.isNotBlank(config.getTrelloApiToken())
                && StringUtils.isNotBlank(config.getTrelloBoardName()) && StringUtils.isNotBlank(config.getTrelloListName());
    }

    public boolean isGitHubIntegrated(Long orgId) {
        IntegrationModuleOrganizationConfiguration config = integrationService.getOrganizationConfiguration(orgId);
        return StringUtils.isNotBlank(config.getGitHubToken()) && StringUtils.isNotBlank(config.getGitHubRepoName())
                && StringUtils.isNotBlank(config.getGitHubRepoOwner());
    }

    public boolean isSlackIntegrated(Long orgId) {
        IntegrationModuleOrganizationConfiguration config = integrationService.getOrganizationConfiguration(orgId);
        return StringUtils.isNotBlank(config.getSlackWebhookUrl());
    }

    public boolean isMsTeamsIntegrated(Long orgId) {
        IntegrationModuleOrganizationConfiguration config = integrationService.getOrganizationConfiguration(orgId);
        return StringUtils.isNotBlank(config.getMsTeamsWebhookUrl());
    }

    public boolean isJiraIntegrated(Long orgId) {
        IntegrationModuleOrganizationConfiguration config = integrationService.getOrganizationConfiguration(orgId);
        return StringUtils.isNotBlank(config.getJiraToken()) && StringUtils.isNotBlank(config.getJiraOrganizationName())
                && StringUtils.isNotBlank(config.getJiraProjectName());
    }

    public boolean isBasecampIntegrated(Long orgId) {
        IntegrationModuleOrganizationConfiguration config = integrationService.getOrganizationConfiguration(orgId);
        return StringUtils.isNotBlank(config.getBasecampAccessToken()) && StringUtils.isNotBlank(config.getBasecampToDoListUrl());
    }
}
