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

import com.fasterxml.jackson.databind.JsonNode;
import com.openkoda.core.flow.Flow;
import com.openkoda.core.helper.PrivilegeHelper;
import com.openkoda.dto.NotificationDto;
import com.openkoda.integration.consumer.BasecampIntegrationConsumers;
import com.openkoda.integration.consumer.GitHubIntegrationConsumers;
import com.openkoda.integration.consumer.JiraIntegrationConsumers;
import com.openkoda.integration.consumer.TrelloIntegrationConsumers;
import com.openkoda.integration.controller.IntegrationComponentProvider;
import com.openkoda.integration.controller.common.IntegrationPageAttributes;
import com.openkoda.integration.controller.common.IntegrationURLConstants;
import com.openkoda.integration.form.*;
import com.openkoda.integration.model.IntegrationPrivilege;
import com.openkoda.integration.model.configuration.IntegrationModuleGlobalConfiguration;
import com.openkoda.integration.model.configuration.IntegrationModuleOrganizationConfiguration;
import com.openkoda.integration.model.dto.*;
import jakarta.annotation.PostConstruct;
import jakarta.inject.Inject;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/**
 * Service providing some functionality for the integration module
 */
@Service("integrationService")
public class IntegrationService extends IntegrationComponentProvider {

    @Inject
    private IntegrationModuleGlobalConfiguration integrationGlobalConfiguration;

    public static final String SHA3_256 = "SHA3-256";
    public static final String GITHUB_TOKEN_REQUEST_JSON = "{" +
            "\"client_id\":\"%s\"," +
            "\"client_secret\":\"%s\"," +
            "\"code\":\"%s\"" +
            "}";

    @Value("${api.basecamp.oauth.authorize:https://launchpad.37signals.com/authorization/new?type=web_server&client_id=%s&redirect_uri=%s&state=%s}")
    private String BASECAMP_OAUTH_AUTHORIZE_API;
    @Value("${api.basecamp.authorize.token:https://launchpad.37signals.com/authorization/token?type=web_server&client_id=%s&redirect_uri=%s&client_secret=%s&code=%s}")
    private String BASECAMP_AUTHORIZE_TOKEN_API;
    @Value("${api.basecamp.refresh.token:https://launchpad.37signals.com/authorization/token?type=refresh&refresh_token=%s&client_id=%s&redirect_uri=%s&client_secret=%s}")
    private String BASECAMP_REFRESH_TOKEN_API;
    @Value("${api.github.oauth.authorize:https://github.com/login/oauth/authorize?scope=%s&client_id=%s&redirect_uri=%s}")
    private String GITHUB_OAUTH_AUTHORIZE_API;
    @Value("${api.github.authorize.token:https://github.com/login/oauth/access_token}")
    private String GITHUB_AUTHORIZE_TOKEN_API;
    @Value("${api.github.access.scope:repo}")
    private String GITHUB_SCOPE;
    @Value("${api.jira.oauth.authorize:https://auth.atlassian.com/authorize?audience=api.atlassian.com&client_id=%s&scope=%s&redirect_uri=%s&state=%s&response_type=code&prompt=consent}")
    private String JIRA_CLOUD_OAUTH_AUTHORIZE_API;
    @Value("${api.jira.authorize.token:https://auth.atlassian.com/oauth/token}")
    private String JIRA_AUTHORIZE_TOKEN_API;
    @Value("${api.jira.access.scope:write:jira-work read:jira-work offline_access}")
    private String JIRA_SCOPE;

    @PostConstruct
    void init() {
        PrivilegeHelper.registerEnumClasses(new Class[]{IntegrationPrivilege.class});
        registerConsumers();
    }

    public void handleResponseError(ResponseEntity response, String errorMessage) throws Exception {
        debug("[handleResponseError]");
        if (!response.getStatusCode().is2xxSuccessful()) {
            error(errorMessage, response.getStatusCodeValue(), response.getBody());
            throw new Exception("Response is not successful");
        }
    }

    public IntegrationGitHubForm prepareGitHubFormForOrg(Long organizationId) {
        debug("[prepareGitHubFormForOrg]");
        IntegrationModuleOrganizationConfiguration organizationConfiguration = getOrganizationConfiguration(organizationId);
        String gitHubToken = organizationConfiguration.getGitHubToken();
        if (StringUtils.isBlank(gitHubToken)) {
            return new IntegrationGitHubForm(new IntegrationGitHubDto(), organizationConfiguration, IntegrationFrontendMappingDefinitions.gitHubConfigurationFormDisabled);
        }
        return new IntegrationGitHubForm(new IntegrationGitHubDto(), organizationConfiguration, IntegrationFrontendMappingDefinitions.gitHubConfigurationForm);
    }

    public IntegrationTrelloForm prepareTrelloFormForOrg(Long organizationId) {
        return new IntegrationTrelloForm(new IntegrationTrelloDto(), integrationService.getOrganizationConfiguration(organizationId));
    }

    public IntegrationBasecampForm prepareBasecampFormForOrg(Long organizationId) {
        debug("[prepareBasecampFormForOrg]");
        IntegrationModuleOrganizationConfiguration organizationConfiguration = getOrganizationConfiguration(organizationId);
        String basecampRefreshToken = organizationConfiguration.getBasecampRefreshToken();
        if (StringUtils.isBlank(basecampRefreshToken)) {
            return new IntegrationBasecampForm(new IntegrationBasecampDto(), organizationConfiguration, IntegrationFrontendMappingDefinitions.basecampConfigurationFormDisabled);
        }
        return new IntegrationBasecampForm(new IntegrationBasecampDto(), organizationConfiguration, IntegrationFrontendMappingDefinitions.basecampConfigurationForm);
    }

    public IntegrationSlackForm prepareSlackFormForOrg(Long organizationId) {
        return new IntegrationSlackForm(new IntegrationSlackDto(), integrationService.getOrganizationConfiguration(organizationId));
    }

    public IntegrationMsTeamsForm prepareMsTeamsFormForOrg(Long organizationId) {
        return new IntegrationMsTeamsForm(new IntegrationMsTeamsDto(), integrationService.getOrganizationConfiguration(organizationId));
    }

    public IntegrationJiraForm prepareJiraFormForOrg(Long organizationId) {
        IntegrationModuleOrganizationConfiguration organizationConfiguration = getOrganizationConfiguration(organizationId);
        String jiraToken = organizationConfiguration.getJiraToken();
        if (StringUtils.isBlank(jiraToken)) {
            return new IntegrationJiraForm(new IntegrationJiraDto(), organizationConfiguration, IntegrationFrontendMappingDefinitions.jiraConfigurationFormDisabled);
        }
        return new IntegrationJiraForm(new IntegrationJiraDto(), integrationService.getOrganizationConfiguration(organizationId));
    }

    public void getGitHubToken(Long orgId, String temporaryCode) throws Exception {
        debug("[getGitHubToken]");
        String tokenRequest = prepareGithubTokenRequest(temporaryCode);
        HttpHeaders headers = getHttpHeadersOfApplicationJson();
        HttpEntity<String> request = new HttpEntity<>(tokenRequest, headers);
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<JsonNode> response = restTemplate.postForEntity(GITHUB_AUTHORIZE_TOKEN_API, request, JsonNode.class);
        handleResponseError(response, "[getGitHubToken] Error during authorization of new token. Code: {}. Error: {}");
        JsonNode body = response.getBody();
        if (body.has("error") || !body.has("access_token")) {
            error("[getGitHubToken] Error: {}", body.has("error") ? body.get("error").asText() : "There is no access token");
            return;
        }
        String gitHubToken = body.get("access_token").asText();
        IntegrationModuleOrganizationConfiguration configuration = integrationService.getOrganizationConfiguration(orgId);
        configuration.setGitHubToken(gitHubToken);
        repositories.unsecure.integration.save(configuration);
    }

    public void getJiraToken(Long orgId, String temporaryCode) throws Exception {
        debug("[getJiraToken]");
        RestTemplate restTemplate = new RestTemplate();
        String tokenRequest = prepareJiraTokenRequest(temporaryCode, orgId);
        HttpHeaders headers = getHttpHeadersOfApplicationJson();
        HttpEntity<String> request = new HttpEntity<>(tokenRequest, headers);
        ResponseEntity<JsonNode> response = restTemplate.postForEntity(JIRA_AUTHORIZE_TOKEN_API, request, JsonNode.class);
        handleResponseError(response, "[getGitHubToken] Error during authorization of new token. Code: {}. Error: {}");
        JsonNode body = response.getBody();
        if (body.has("error") || !body.has("access_token")) {
            error("[getGitHubToken] Error: {}", body.has("error") ? body.get("error").asText() : "There is no access token");
        }
        IntegrationModuleOrganizationConfiguration configuration = integrationService.getOrganizationConfiguration(orgId);
        String jiraToken = body.get("access_token").asText();
        configuration.setJiraToken(jiraToken);
        String jiraRefreshToken = body.get("refresh_token").asText();
        configuration.setJiraRefreshToken(jiraRefreshToken);
        repositories.unsecure.integration.save(configuration);
    }

    private String prepareGithubTokenRequest(String temporaryCode) {
        debug("[prepareGithubTokenRequest]");
        IntegrationModuleGlobalConfiguration globalConfig = integrationService.getGlobalConfiguration();
        String request = String.format(GITHUB_TOKEN_REQUEST_JSON, globalConfig.gitHubClientId, globalConfig.gitHubClientSecret, temporaryCode);
        return request;
    }

    private String prepareJiraTokenRequest(String temporaryCode, Long orgId) {
        debug("[prepareJiraTokenRequest]");
        IntegrationModuleGlobalConfiguration globalConfig = integrationService.getGlobalConfiguration();
        String request = String.format("{" +
                "    \"grant_type\": \"authorization_code\"," +
                "    \"client_id\": \"%s\"," +
                "    \"client_secret\": \"%s\"," +
                "    \"code\": \"%s\",\n" +
                "    \"redirect_uri\": \"%s\"" +
                "}", globalConfig.jiraClientId, globalConfig.jiraClientSecret, temporaryCode, integrationUrlHelper.jiraTokenAccessUrl(orgId));
        return request;
    }

    public void getBasecampToken(Long orgId, String code) throws Exception {
        debug("[getBasecampToken]");
        String tokenRequestUrl = prepareBasecampAccessTokenRequestUrl(code, orgId);
        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);
        HttpEntity<String> request = new HttpEntity<>(headers);
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<JsonNode> response = restTemplate.postForEntity(tokenRequestUrl, request, JsonNode.class);
        handleResponseError(response, "[getBasecampToken] Error during authorization of new token. Code: {}. Error: {}");
        JsonNode body = response.getBody();
        if (body.has("error") || !body.has("access_token")) {
            error("[getBasecampToken] Error: {}", body.has("error") ? body.get("error").asText() : "There is no access token");
        } else {
            String basecampAccessToken = body.get("access_token").textValue();
            IntegrationModuleOrganizationConfiguration configuration = integrationService.getOrganizationConfiguration(orgId);
            configuration.setBasecampAccessToken(basecampAccessToken);
            String basecampRefreshToken = body.get("refresh_token").textValue();
            configuration.setBasecampRefreshToken(basecampRefreshToken);
            repositories.unsecure.integration.save(configuration);
        }
    }

    public boolean refreshBasecampToken(Long orgId) throws Exception {
        debug("[refreshBasecampToken]");
        IntegrationModuleOrganizationConfiguration configuration = integrationService.getOrganizationConfiguration(orgId);
        String tokenRequestUrl = prepareBasecampRefreshTokenRequestUrl(configuration.getBasecampRefreshToken(), orgId);
        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);
        HttpEntity<String> request = new HttpEntity<>(headers);
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<JsonNode> response = restTemplate.postForEntity(tokenRequestUrl, request, JsonNode.class);
        handleResponseError(response, "[refreshBasecampToken] Error during authorization of new token. Code: {}. Error: {}");
        JsonNode body = response.getBody();
        if (body.has("error") || !body.has("access_token")) {
            error("[getBasecampToken] Error: {}", body.has("error") ? body.get("error").asText() : "There is no access token");
            return false;
        }
        String basecampAccessToken = body.get("access_token").asText();
        configuration.setBasecampAccessToken(basecampAccessToken);
        repositories.unsecure.integration.save(configuration);
        return true;
    }

    public IntegrationModuleOrganizationConfiguration getOrganizationConfiguration(Long organizationId) {
        debug("[getOrganizationConfiguration]");
        return repositories.unsecure.integration.findByOrganizationId(organizationId);
    }

    public IntegrationModuleOrganizationConfiguration getInnerOrganizationConfig(Long organizationId) {
        debug("[getInnerOrganizationConfig]");
        return getOrganizationConfiguration(organizationId);
    }

    public IntegrationModuleGlobalConfiguration getGlobalConfiguration() {
        debug("[getGlobalConfiguration]");
        return integrationGlobalConfiguration;
    }

    public String gitHubOAuthUrl(Long orgId) {
        debug("[gitHubOAuthUrl]");
        String clientId = getGlobalConfiguration().gitHubClientId;
        return String.format(GITHUB_OAUTH_AUTHORIZE_API, GITHUB_SCOPE, clientId, integrationUrlHelper.gitHubTokenAccessUrl(orgId));
    }

    public String jiraOAuthUrl(Long orgId) {
        debug("[jiraOAuthUrl]");
        String clientId = getGlobalConfiguration().jiraClientId;
        String callbackUrl = integrationUrlHelper.jiraTokenAccessUrl(orgId);
        String oauthUrl = String.format(JIRA_CLOUD_OAUTH_AUTHORIZE_API, clientId, JIRA_SCOPE, callbackUrl, orgId);
        debug("[jiraOAuthUrl] returning url {}", oauthUrl);
        return oauthUrl;
    }

    public String basecampOAuthUrl(Long orgId) {
        debug("[basecampOAuthUrl]");
        String clientId = getGlobalConfiguration().basecampClientId;
        return String.format(BASECAMP_OAUTH_AUTHORIZE_API, clientId, integrationUrlHelper.basecampRedirectUrl(orgId), orgId);
    }

    private HttpHeaders getHttpHeadersOfApplicationJson() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }

    private String prepareBasecampAccessTokenRequestUrl(String code, Long orgId) {
        debug("[prepareBasecampRokenRequestUrl]");
        IntegrationModuleGlobalConfiguration globalConfig = integrationService.getGlobalConfiguration();
        return String.format(BASECAMP_AUTHORIZE_TOKEN_API, globalConfig.basecampClientId, integrationUrlHelper.basecampRedirectUrl(orgId), globalConfig.basecampClientSecret, code);
    }

    private String prepareBasecampRefreshTokenRequestUrl(String refreshToken, Long orgId) {
        debug("[prepareBasecampRefreshTokenRequestUrl]");
        IntegrationModuleGlobalConfiguration globalConfig = integrationService.getGlobalConfiguration();
        return String.format(BASECAMP_REFRESH_TOKEN_API, refreshToken, globalConfig.basecampClientId, integrationUrlHelper.basecampRedirectUrl(orgId), globalConfig.basecampClientSecret);
    }

    public void cleanOrgConfig(String application, Long orgId) {
        IntegrationModuleOrganizationConfiguration configuration = getOrganizationConfiguration(orgId);
        switch (application) {
            case IntegrationURLConstants._TRELLO:
                configuration.setTrelloApiToken(null);
                configuration.setTrelloApiKey(null);
                configuration.setTrelloBoardName(null);
                configuration.setTrelloListName(null);
                break;
            case IntegrationURLConstants._GITHUB:
                configuration.setGitHubToken(null);
                configuration.setGitHubRepoOwner(null);
                configuration.setGitHubRepoName(null);
                break;
            case IntegrationURLConstants._SLACK:
                configuration.setSlackWebhookUrl(null);
                break;
            case IntegrationURLConstants._MSTEAMS:
                configuration.setMsTeamsWebhookUrl(null);
                break;
            case IntegrationURLConstants._JIRA:
                configuration.setJiraOrganizationName(null);
                configuration.setJiraProjectName(null);
                configuration.setJiraToken(null);
                configuration.setJiraRefreshToken(null);
                break;
            case IntegrationURLConstants._BASECAMP:
                configuration.setBasecampAccessToken(null);
                configuration.setBasecampRefreshToken(null);
                configuration.setBasecampToDoListUrl(null);
                configuration.setBasecampProjectId(null);
                configuration.setBasecampAccountId(null);
                configuration.setBasecampToDoListId(null);
                break;
            default:
        }
        repositories.unsecure.integration.save(configuration);
    }

    public String prepareJsonString(String message){
        return StringUtils.replace(message, "\"", "\\\"");
    }

    public Flow<Long, ?, ?> initFlowForOrganizationConfiguration(Long organizationId, HttpServletRequest request) {
        return Flow.init(null, organizationId)
                .thenSet(IntegrationPageAttributes.integrationTrelloForm, a -> integrationService.prepareTrelloFormForOrg(organizationId))
                .thenSet(IntegrationPageAttributes.integrationGitHubForm, a -> integrationService.prepareGitHubFormForOrg(organizationId))
                .thenSet(IntegrationPageAttributes.integrationSlackForm, a -> integrationService.prepareSlackFormForOrg(organizationId))
                .thenSet(IntegrationPageAttributes.integrationMsTeamsForm, a -> integrationService.prepareMsTeamsFormForOrg(organizationId))
                .thenSet(IntegrationPageAttributes.integrationJiraForm, a -> integrationService.prepareJiraFormForOrg(organizationId))
                .thenSet(IntegrationPageAttributes.integrationBasecampForm, a -> integrationService.prepareBasecampFormForOrg(organizationId));
    }

    private void registerConsumers() {
        services.applicationEvent.registerEventConsumerWithMethod(NotificationDto.class, TrelloIntegrationConsumers.class, "createTrelloCardFromOrgNotification",
                "This creates Cards on the specified List and Board on Trello based on the Notification.");
        services.applicationEvent.registerEventConsumerWithMethod(NotificationDto.class, GitHubIntegrationConsumers.class, "createGitHubIssueFromOrgNotification",
                "This creates Issue on the specified repository on GitHub, based on the Notification.");
        services.applicationEvent.registerEventConsumerWithMethod(NotificationDto.class, JiraIntegrationConsumers.class, "createJiraIssueFromOrgNotification",
                "This creates Issue on the specified project on JIRA, based on the Notification.");
        services.applicationEvent.registerEventConsumerWithMethod(NotificationDto.class, BasecampIntegrationConsumers.class, "postBasecampToDo",
                "This posts a To-Do to Basecamp To-Do List based on the Notification.");
    }
}
