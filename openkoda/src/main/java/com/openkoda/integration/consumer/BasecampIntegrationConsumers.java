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

package com.openkoda.integration.consumer;

import com.openkoda.dto.NotificationDto;
import com.openkoda.integration.controller.IntegrationComponentProvider;
import com.openkoda.integration.model.configuration.IntegrationModuleOrganizationConfiguration;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/**
 * Consumers for Basecamp integration
 */
@Service
public class BasecampIntegrationConsumers extends IntegrationComponentProvider {

    @Value("${api.basecamp.post.message:https://3.basecampapi.com/%s/buckets/%s/todolists/%s/todos.json}")
    public String BASECAMP_POST_TODO_URL;

    private RestTemplate restTemplate = new RestTemplate();

    public void postBasecampToDo(NotificationDto notification) throws Exception {
        debug("[postBasecampToDo]");
        if (!services.notification.isOrganization(notification)) {
            info("[postBasecampToDo] Notification is not organization level.");
            return;
        }
        if(!notification.getPropagate()){
            return;
        }
        IntegrationModuleOrganizationConfiguration organizationConfig
                = integrationService.getOrganizationConfiguration(notification.getOrganizationId());
        if (StringUtils.isEmpty(organizationConfig.getBasecampAccessToken())) {
            warn("[postBasecampToDo] Missing Access Token.");
            return;
        }
        String requestUrl = prepareBasecampToDoUrl(organizationConfig);
        String toDoRequest = prepareToDoData(notification.getMessage());
        HttpEntity<String> entity = new HttpEntity<>(toDoRequest, prepareHeaders(organizationConfig.getBasecampAccessToken()));
        ResponseEntity<String> response = restTemplate.exchange(requestUrl, HttpMethod.POST, entity, String.class);
        handleBasecampError(response, notification);
    }

    private String prepareBasecampToDoUrl(IntegrationModuleOrganizationConfiguration organizationConfig) {
        debug("[prepareBasecampToDoUrl]");
        String accountId = organizationConfig.getBasecampAccountId();
        String projectId = organizationConfig.getBasecampProjectId();
        String toDoListId = organizationConfig.getBasecampToDoListId();
        if (StringUtils.isBlank(accountId) || StringUtils.isBlank(projectId) || StringUtils.isBlank(toDoListId)) {
            error("[prepareBasecampToDoUrl] Basecamp accountId, projectId or toDoListId not set");
            return null;
        }
        return String.format(BASECAMP_POST_TODO_URL, accountId, projectId, toDoListId);
    }

    private String prepareToDoData(String message) {
        debug("[prepareToDoData]");
        String messageJson = "{" +
                "\"content\":\"%s\"," +
                "\"description\":\"%s\"" +
                "}";
        return String.format(messageJson, messages.get(
                "notification.basecamp.subject"),
                StringUtils.replace(message, "\"", "\\\"")
        );
    }

    private HttpHeaders prepareHeaders(String accessToken) {
        debug("[prepareHeaders]");
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
        headers.set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        return headers;
    }

    private void handleBasecampError(ResponseEntity response, NotificationDto notification) throws Exception {
        debug("[handleBasecampError]");
        if (response.getStatusCode().equals(HttpStatus.UNAUTHORIZED)) {
            if (integrationService.refreshBasecampToken(notification.getOrganizationId())) {
                postBasecampToDo(notification);
            }
        } else {
            integrationService.handleResponseError(response, "[postBasecampToDo] Error while posting message. Code: {}. Error: {}");
        }
    }
}
