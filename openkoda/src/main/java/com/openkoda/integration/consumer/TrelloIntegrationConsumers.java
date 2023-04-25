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

import com.fasterxml.jackson.databind.JsonNode;
import com.openkoda.dto.NotificationDto;
import com.openkoda.integration.controller.IntegrationComponentProvider;
import com.openkoda.integration.model.configuration.IntegrationModuleOrganizationConfiguration;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.social.support.URIBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

/**
 * This class contains consumers that cooperates with Trello
 */
@Service
public class TrelloIntegrationConsumers extends IntegrationComponentProvider {

    @Value("${api.trello.get.boards:https://api.trello.com/1/members/me/boards}")
    private String TRELLO_GET_BOARDS_API;
    @Value("${api.trello.get.lists:https://api.trello.com/1/boards/}")
    private String TRELLO_GET_LISTS_API;
    @Value("${api.trello.create.board:https://api.trello.com/1/boards/}")
    private String TRELLO_CREATE_BOARD_API;
    @Value("${api.trello.create.list:https://api.trello.com/1/lists}")
    private String TRELLO_CREATE_LIST_API;
    @Value("${api.trello.create.card:https://api.trello.com/1/cards}")
    private String TRELLO_CREATE_CARD_API;
    private RestTemplate restTemplate = new RestTemplate();

    public void createTrelloCardFromOrgNotification(NotificationDto notification) throws Exception {
        debug("[createTrelloCardFromOrgNotification]");
        if (!services.notification.isOrganization(notification)) {
            info("[createTrelloCardFromOrgNotification] Notification is not organizational.");
            return;
        }
        if(!notification.getPropagate()){
            return;
        }
        IntegrationModuleOrganizationConfiguration integrationConfiguration
                = integrationService.getInnerOrganizationConfig(notification.getOrganizationId());
        String apiKey = integrationConfiguration.getTrelloApiKey();
        String apiToken = integrationConfiguration.getTrelloApiToken();
        if (StringUtils.isBlank(apiKey) || StringUtils.isBlank(apiToken)) {
            warn("[createTrelloCardFromOrgNotification] Trello key or token unavailable");
            return;
        }
        if (StringUtils.isBlank(integrationConfiguration.getTrelloBoardName()) || StringUtils.isBlank(integrationConfiguration.getTrelloListName())) {
            warn("[createTrelloCardFromOrgNotification] Trello configuration is invalid: lack of board/list name.");
            return;
        }
        String boardId = getBoardId(integrationConfiguration);
        String listId;
        if (StringUtils.isBlank(boardId)) {
            boardId = createBoard(integrationConfiguration);
            listId = createList(integrationConfiguration, boardId);
        } else {
            listId = getListId(integrationConfiguration, boardId);
            if (StringUtils.isBlank(listId)) {
                listId = createList(integrationConfiguration, boardId);
            }
        }
        createNotificationCard(integrationConfiguration, listId, notification);
    }

    private String getBoardId(IntegrationModuleOrganizationConfiguration config) throws Exception {
        debug("[getBoardId]");
        URIBuilder builder = URIBuilder.fromUri(TRELLO_GET_BOARDS_API)
                .queryParam("fields", "name")
                .queryParam("key", config.getTrelloApiKey())
                .queryParam("token", config.getTrelloApiToken());
        ResponseEntity<List> response = restTemplate.getForEntity(builder.build(), List.class);
        integrationService.handleResponseError(response, "[getBoardId] Error while checking data integrity. Code: {}. Error: {}");
        List<Map<String, String>> boards = (List<Map<String, String>>) response.getBody();
        return boards.stream().filter(board -> board.get("name").equals(config.getTrelloBoardName()))
                .findAny().map(board -> board.get("id"))
                .orElse(null);

    }

    private String getListId(IntegrationModuleOrganizationConfiguration config, String boardId) throws Exception {
        debug("[getListId]");
        URIBuilder builder = URIBuilder.fromUri(TRELLO_GET_LISTS_API + boardId)
                .queryParam("lists", "all")
                .queryParam("fields", "lists")
                .queryParam("key", config.getTrelloApiKey())
                .queryParam("token", config.getTrelloApiToken());
        ResponseEntity<JsonNode> response = restTemplate.getForEntity(builder.build(), JsonNode.class);
        integrationService.handleResponseError(response, "[getBoardId] Error while checking data integrity. Code: {}. Error: {}");
        JsonNode lists = response.getBody();
        for (JsonNode list : lists.get("lists")) {
            if (list.get("name").asText().equals(config.getTrelloListName()) && !list.get("closed").asBoolean()) {
                return list.get("id").asText();
            }
        }
        return null;
    }

    private String createBoard(IntegrationModuleOrganizationConfiguration config) throws Exception {
        debug("[createBoard]");
        URIBuilder builder = URIBuilder.fromUri(TRELLO_CREATE_BOARD_API)
                .queryParam("name", config.getTrelloBoardName())
                .queryParam("defaultLists", "false")
                .queryParam("key", config.getTrelloApiKey())
                .queryParam("token", config.getTrelloApiToken());
        ResponseEntity<JsonNode> response = restTemplate.postForEntity(builder.build(), null, JsonNode.class);
        integrationService.handleResponseError(response, "[createBoard] Error while creating new Board. Code: {}. Error: {}");
        return response.getBody().get("id").asText();
    }

    private String createList(IntegrationModuleOrganizationConfiguration config, String boardId) throws Exception {
        debug("[createList]");
        URIBuilder builder = URIBuilder.fromUri(TRELLO_CREATE_LIST_API)
                .queryParam("name", config.getTrelloListName())
                .queryParam("idBoard", boardId)
                .queryParam("key", config.getTrelloApiKey())
                .queryParam("token", config.getTrelloApiToken());
        ResponseEntity<JsonNode> response = restTemplate.postForEntity(builder.build(), null, JsonNode.class);
        integrationService.handleResponseError(response, "[createBoard] Error while creating new Board. Code: {}. Error: {}");
        return response.getBody().get("id").asText();
    }

    private void createNotificationCard(IntegrationModuleOrganizationConfiguration config, String listId, NotificationDto notification) throws Exception {
        debug("[createNotificationCard]");
        URIBuilder builder = URIBuilder.fromUri(TRELLO_CREATE_CARD_API)
                .queryParam("idList", listId)
                .queryParam("pos", "0")
                .queryParam("name", "New notification!")
                .queryParam("desc", notification.getMessage())
                .queryParam("key", config.getTrelloApiKey())
                .queryParam("token", config.getTrelloApiToken());
        ResponseEntity<JsonNode> response = restTemplate.postForEntity(builder.build(), null, JsonNode.class);
        integrationService.handleResponseError(response, "[createBoard] Error while creating new Board. Code: {}. Error: {}");

    }

}
