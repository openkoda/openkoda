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

package com.openkoda.integration.consumer;

import com.fasterxml.jackson.databind.JsonNode;
import com.openkoda.dto.NotificationDto;
import com.openkoda.integration.controller.IntegrationComponentProvider;
import com.openkoda.integration.model.configuration.IntegrationModuleGlobalConfiguration;
import com.openkoda.integration.model.configuration.IntegrationModuleOrganizationConfiguration;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class JiraIntegrationConsumers extends IntegrationComponentProvider {

    private static final String JIRA_CREATE_ISSUE_REQUEST_JSON = "{\"fields\": {" +
            "    \"summary\": \"%s\"," +
            "    \"description\": \"%s\"," +
            "    \"issuetype\": {" +
            "      \"id\": \"%s\"" +
            "    }," +
            "    \"project\": {" +
            "      \"id\": \"%s\"" +
            "    }" +
            "  }" +
            "}";
    @Value("${api.jira.refresh.token:https://auth.atlassian.com/oauth/token}")
    private String JIRA_REFRESH_TOKEN_API;
    @Value("${api.jira.get.cloudId:https://api.atlassian.com/oauth/token/accessible-resources}")
    private String JIRA_GET_CLOUDID_API;
    @Value("${api.jira.get.project.list:https://api.atlassian.com/ex/jira/%s/rest/api/2/project}")
    private String JIRA_GET_PROJECT_LIST_API;
    @Value("${api.jira.get.issue.type.list:https://api.atlassian.com/ex/jira/%s/rest/api/2/issuetype}")
    private String JIRA_GET_ISSUE_TYPES_API;
    @Value("${api.jira.create.issue:https://api.atlassian.com/ex/jira/%s/rest/api/2/issue}")
    private String JIRA_CREATE_ISSUE_API;
    private static final String JIRA_REFRESH_TOKEN_JSON = "{" +
            "\"grant_type\":\"refresh_token\"," +
            "\"client_id\":\"%s\"," +
            "\"client_secret\":\"%s\"," +
            "\"refresh_token\":\"%s\"" +
            "}";
    private RestTemplate restTemplate = new RestTemplate();

    public void createJiraIssueFromOrgNotification(NotificationDto notification) throws Exception {
        debug("[createJiraIssueFromOrgNotification]");
        if (!services.notification.isOrganization(notification)) {
            info("[createJiraIssueFromOrgNotification] Notification is not organizational.");
            return;
        }
        if(!notification.getPropagate()){
            return;
        }
        IntegrationModuleOrganizationConfiguration configuration
                = integrationService.getOrganizationConfiguration(notification.getOrganizationId());
        String refreshToken = configuration.getJiraRefreshToken();
        if (StringUtils.isBlank(refreshToken)) {
            warn("[createJiraIssueFromOrgNotification] Jira token not found, try to reconnect.");
            return;
        }
        refreshToken(configuration, integrationService.getGlobalConfiguration());
        String token = configuration.getJiraToken();
        String organizationName = configuration.getJiraOrganizationName();
        String cloudId = getCloudId(token, organizationName);
        String projectName = configuration.getJiraProjectName();
        String projectId = getProjectId(token, cloudId, projectName);
        String issueTypeId = getIssueTypeId(token, cloudId, "Task");

        createJiraIssue(notification, token, cloudId, projectId, issueTypeId);
    }

    private void createJiraIssue(NotificationDto notification, String token, String cloudId, String projectId, String issueTypeId) throws Exception {
        debug("[createJiraIssue]");
        HttpHeaders headers = prepareAuthorizationHeader(token);
        headers.setContentType(MediaType.APPLICATION_JSON);
        String issueRequestJson = prepareJiraIssueRequest(notification, projectId, issueTypeId);
        HttpEntity<String> entity = new HttpEntity<>(issueRequestJson, headers);
        String createIssueUrl = prepareCreateIssueUrl(cloudId);
        ResponseEntity<JsonNode> response = restTemplate.exchange(createIssueUrl, HttpMethod.POST, entity, JsonNode.class);
        integrationService.handleResponseError(response, "[createJiraIssueFromOrgNotification] Error when creating issue. Code: {}. Error: {}");
    }

    private String prepareCreateIssueUrl(String cloudId) {
        return String.format(JIRA_CREATE_ISSUE_API, cloudId);
    }

    private String prepareJiraIssueRequest(NotificationDto notification, String projectId, String issueTypeId) {
        String jsonMessage = integrationService.prepareJsonString(notification.getMessage());
        return String.format(JIRA_CREATE_ISSUE_REQUEST_JSON, "New notification from Jira", jsonMessage, issueTypeId, projectId);
    }

    private void refreshToken(IntegrationModuleOrganizationConfiguration config, IntegrationModuleGlobalConfiguration globalConfig) throws Exception {
        debug("[refreshToken]");
        String refreshToken = config.getJiraRefreshToken();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        String refreshRequest = String.format(JIRA_REFRESH_TOKEN_JSON, globalConfig.jiraClientId, globalConfig.jiraClientSecret, refreshToken);
        HttpEntity<String> entity = new HttpEntity<>(refreshRequest, headers);
        ResponseEntity<JsonNode> response = restTemplate.exchange(JIRA_REFRESH_TOKEN_API, HttpMethod.POST, entity, JsonNode.class);
        integrationService.handleResponseError(response, "[refreshToken] Error during refresh of token. Code: {}. Error: {}");
        if (!response.getBody().has("access_token")) {
            error("[refreshToken] There is no access token. Body: \n{}", response.getBody().asText());
            throw new Exception("[refreshToken] No token in successful request");
        }
        String accessToken = response.getBody().get("access_token").asText();
        config.setJiraToken(accessToken);
        repositories.unsecure.integration.save(config);
    }

    private String getIssueTypeId(String token, String cloudId, String taskType) throws Exception {
        debug("[getIssueTypeId]");
        HttpHeaders headers = prepareAuthorizationHeader(token);
        String getIssueTypesUrl = String.format(JIRA_GET_ISSUE_TYPES_API, cloudId);
        HttpEntity entity = new HttpEntity(headers);
        ResponseEntity<JsonNode> response = restTemplate.exchange(getIssueTypesUrl, HttpMethod.GET, entity, JsonNode.class);
        integrationService.handleResponseError(response, "[getIssueTypeId] Error when accessing issue type Id. Code: {}. Error: {}");
        for (JsonNode issueType : response.getBody()) {
            String name = issueType.get("name").asText();
            if (name.equals(taskType)) {
                return issueType.get("id").asText();
            }
        }
        error("[getIssueTypeId] Cannot find Id for the issue type");
        throw new Exception("Cannot find Id for the issue type");
    }

    private String getProjectId(String token, String cloudId, String projectName) throws Exception {
        debug("[getProjectId]");
        HttpHeaders headers = prepareAuthorizationHeader(token);
        String getProjectUrl = String.format(JIRA_GET_PROJECT_LIST_API, cloudId);
        HttpEntity entity = new HttpEntity(headers);
        ResponseEntity<JsonNode> response = restTemplate.exchange(getProjectUrl, HttpMethod.GET, entity, JsonNode.class);
        integrationService.handleResponseError(response, "[getProjectId] Error when accessing project Id. Code: {}. Error: {}");
        for (JsonNode project : response.getBody()) {
            String name = project.get("name").asText();
            if (name.equals(projectName)) {
                return project.get("id").asText();
            }
        }
        error("[getProjectId] Cannot find Id for the project provided");
        throw new Exception("Cannot find Id for the project provided");
    }

    private String getCloudId(String token, String organizationName) throws Exception {
        debug("[getCloudId]");
        ResponseEntity<JsonNode> cloudIdResponse = requestCloudId(token);
        integrationService.handleResponseError(cloudIdResponse, "[getCloudId] Error when reaching to JIRA cloudId. Code: {}. Error: {}");
        for (JsonNode resource : cloudIdResponse.getBody()) {
            String resourceOrgName = resource.get("name").asText();
            if (resourceOrgName.equals(organizationName)) {
                return resource.get("id").asText();
            }
        }
        error("[getCloudId] Cannot find Id for the organization provided");
        throw new Exception("Cannot find Id for the organization provided");
    }

    private ResponseEntity<JsonNode> requestCloudId(String token) {
        HttpHeaders headers = prepareAuthorizationHeader(token);
        HttpEntity entity = new HttpEntity(headers);
        return restTemplate.exchange(JIRA_GET_CLOUDID_API, HttpMethod.GET, entity, JsonNode.class);
    }

    private HttpHeaders prepareAuthorizationHeader(String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        return headers;
    }
}
