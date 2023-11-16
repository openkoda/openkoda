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

import com.openkoda.dto.NotificationDto;
import com.openkoda.integration.controller.IntegrationComponentProvider;
import com.openkoda.integration.model.configuration.IntegrationModuleOrganizationConfiguration;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/**
 * This class contains consumers that cooperates with GitHub
 */
@Service
public class GitHubIntegrationConsumers extends IntegrationComponentProvider {

    @Value("${api.github.create.issue:https://api.github.com/repos/%s/%s/issues}")
    private String GITHUB_CREATE_ISSUE_API;
    private final String GITHUB_ISSUE_TITLE = "notification.github.title";
    private RestTemplate restTemplate = new RestTemplate();
    private static final String GITHUB_ISSUE_JSON = "{" +
            "\"title\":\"%s\"," +
            "\"body\":\"%s\"" +
            "}";

    public void createGitHubIssueFromOrgNotification(NotificationDto notification) throws Exception {
        debug("[createGitHubIssueFromOrgNotification]");
        if (!services.notification.isOrganization(notification)) {
            info("[createGitHubIssueFromOrgNotification] Notification is not organizational.");
            return;
        }
        if(!notification.getPropagate()){
            return;
        }
        IntegrationModuleOrganizationConfiguration integrationConfiguration
                = integrationService.getInnerOrganizationConfig(notification.getOrganizationId());
        String repoName = integrationConfiguration.getGitHubRepoName();
        String repoOwner = integrationConfiguration.getGitHubRepoOwner();
        if (StringUtils.isBlank(repoName) || StringUtils.isBlank(repoOwner)) {
            warn("[createGitHubIssueFromOrgNotification] GitHub owner or repo not introduced");
            return;
        }
        String url = String.format(GITHUB_CREATE_ISSUE_API, repoOwner, repoName);
        HttpHeaders headers = prepareAuthorizationHeader(integrationConfiguration.getGitHubToken());
        String message = integrationService.prepareJsonString(notification.getMessage());
        String issueRequest = String.format(GITHUB_ISSUE_JSON, messages.get(GITHUB_ISSUE_TITLE), message);
        HttpEntity<String> entity = new HttpEntity<>(issueRequest, headers);
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
        integrationService.handleResponseError(response, "[createGitHubIssueFromOrgNotification] Error while creating new Issue. Code: {}. Error: {}");
    }

    private HttpHeaders prepareAuthorizationHeader(String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.AUTHORIZATION, "token " + token);
        return headers;
    }
}
