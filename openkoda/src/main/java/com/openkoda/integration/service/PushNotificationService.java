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

import com.openkoda.core.flow.PageModelMap;
import com.openkoda.core.security.UserProvider;
import com.openkoda.core.service.email.StandardEmailTemplates;
import com.openkoda.dto.NotificationDto;
import com.openkoda.integration.controller.IntegrationComponentProvider;
import com.openkoda.integration.model.configuration.IntegrationModuleOrganizationConfiguration;
import com.openkoda.model.Organization;
import com.openkoda.model.User;
import com.openkoda.model.task.Email;
import com.openkoda.model.task.HttpRequestTask;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Service containing different consumers methods which push created notifications to external apis or email
 *
 * @author Martyna Litkowska (mlitkowska@stratoflow.com)
 * @since 2019-07-03
 */
@Service
public class PushNotificationService extends IntegrationComponentProvider {

    public void createSlackPostMessageRequest(NotificationDto notification) {
        debug("[createSlackPostRequest]");
        if(!notification.getPropagate()){
            return;
        }
        if (notification.getOrganizationId() != null && notification.getUserId() == null) {
            IntegrationModuleOrganizationConfiguration organizationConfiguration = integrationService.getOrganizationConfiguration(notification.getOrganizationId());
            String slackWebhookUrl = organizationConfiguration.getSlackWebhookUrl();
            if (slackWebhookUrl != null && !slackWebhookUrl.isEmpty()) {
                int linkStartIndex = notification.getMessage().indexOf("<a href=\"");
                String requestJson;
                if (linkStartIndex != -1) {
                    String linkTitle = StringUtils.substringBetween(notification.getMessage(), "\">", "</a>");
                    String linkHref = StringUtils.substringBetween(notification.getMessage(), "<a href=\"", "\">");
                    String msgWithRemovedAnchorTag = notification.getMessage().substring(0, notification.getMessage().indexOf("<a href=\""));
                    requestJson = String.format("{\"text\":\"%s\",\"attachments\":[{\"title\":\"%s\",\"title_link\":\"%s\"}]}",
                            msgWithRemovedAnchorTag, linkTitle, linkHref);
                } else {
                    requestJson = String.format("{\"text\":\"%s\"}", notification.getMessage());
                }
                requestJson = requestJson.replaceAll("<br/>", "");
                debug("Creating Slack push message for organization notification");
                HttpRequestTask httpRequestTask = new HttpRequestTask(slackWebhookUrl, requestJson);
                repositories.unsecure.httpRequest.save(httpRequestTask);
            }
        }
    }

    public void createMsTeamsPostMessageRequest(NotificationDto notification) {
        debug("[createMsTeamsPostMessageRequest]");
        if(!notification.getPropagate()) {
            return;
        }
        if (notification.getOrganizationId() != null && notification.getUserId() == null) {
            IntegrationModuleOrganizationConfiguration organizationConfiguration = integrationService.getOrganizationConfiguration(notification.getOrganizationId());
            String msTeamsWebhookUrl = organizationConfiguration.getMsTeamsWebhookUrl();
            if (msTeamsWebhookUrl != null && !msTeamsWebhookUrl.isEmpty()) {
                debug("Creating Ms Teams push message for organization notification");
                HttpRequestTask httpRequestTask = new HttpRequestTask(msTeamsWebhookUrl,
                        String.format("{\"text\":\"%s\"}", StringUtils.replace(notification.getMessage(), "\"", "\\\"")));
                repositories.unsecure.httpRequest.save(httpRequestTask);
            }
        }
    }

    public void createEmailNotification(NotificationDto notification) {
        debug("[createEmailNotification]");
        if(!notification.getPropagate()){
            return;
        }
        List<Email> emailMsgs = new ArrayList<>();
        PageModelMap model = new PageModelMap();
        model.put(notificationMessage, notification.getMessage());
        if (notification.getUserId() != null) {
            debug("Notification is user specific.");
            User user = repositories.unsecure.user.findOne(notification.getUserId());
            model.put(userEntity, user);
            Email prepared = services.emailConstructor.prepareEmail(
                    user.getEmail(),
                    user.getName().isEmpty() ? user.getEmail() : user.getName(),
                    notification.getSubject(),
                    StandardEmailTemplates.NOTIFICATION_USER_EMAIL,
                    5,
                    model);
            prepared.setAttachmentURL(notification.getAttachmentURL());
            emailMsgs.add(prepared);
        } else if (notification.getOrganizationId() != null) {
            debug("Notification is organization specific.");
            Organization organization = repositories.unsecure.organization.findOne(notification.getOrganizationId());
            model.put(organizationEntity, organization);
            UserProvider.setConsumerAuthentication();
            for (User user : repositories.unsecure.userRole.getUsersInOrganization(organization.getId())) {
                model.put(userEntity, user);
                Email prepared = services.emailConstructor.prepareEmail(
                            user.getEmail(),
                            organization.getName(),
                            notification.getSubject(),
                        StandardEmailTemplates.NOTIFICATION_ORGANIZATION_EMAIL,
                            5,
                        model);
                prepared.setAttachmentURL(notification.getAttachmentURL());
                emailMsgs.add(prepared);
            }
            UserProvider.clearAuthentication();
        } else {
            debug("Notification is global - not creating email for that.");
        }

        if (!emailMsgs.isEmpty()) {
            repositories.unsecure.email.saveAll(emailMsgs);
        }
    }
}
