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

package com.openkoda.service;

import com.openkoda.AbstractTest;
import com.openkoda.core.flow.PageModelMap;
import com.openkoda.dto.NotificationDto;
import com.openkoda.integration.model.configuration.IntegrationModuleOrganizationConfiguration;
import com.openkoda.integration.service.PushNotificationService;
import com.openkoda.model.Organization;
import com.openkoda.model.User;
import com.openkoda.model.notification.Notification;
import com.openkoda.model.task.Email;
import com.openkoda.model.task.HttpRequestTask;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;

import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link PushNotificationService}
 *
 * @author Martyna Litkowska (mlitkowska@stratoflow.com)
 * @since 2019-07-03
 */
public class PushNotificationServiceTest extends AbstractTest {

    @Inject
    private PushNotificationService pushNotificationService;

    @Value("${attribute.webhook.slack:slack_webhook}")
    private String slackWebhook;

    @Value("${attribute.webhook.ms-teams:ms_teams_webhook}")
    private String msTeamsWebhook;

    @Test
    public void checkGlobalNotificationNotPushedToSlack() {
//        given
        NotificationDto notificationDto = new NotificationDto("message", Notification.NotificationType.SUCCESS, null);

//        when
        pushNotificationService.createSlackPostMessageRequest(notificationDto);

//        then
        verify(integrationService, never()).getOrganizationConfiguration(anyLong());
    }

    @Test
    public void checkAnyUserNotificationNotPushedToSlack() {
//        given
        NotificationDto notificationDto = new NotificationDto("message", Notification.NotificationType.SUCCESS, null, 1L);

//        when
        pushNotificationService.createSlackPostMessageRequest(notificationDto);

//        then
        verify(integrationService, never()).getOrganizationConfiguration(anyLong());
    }

    @Test
    public void checkOrganizationOnlyNotificationNotPushedToSlackNoAttributePresent() {
//        given
        long organizationId = 1L;
        NotificationDto notificationDto = new NotificationDto("message", Notification.NotificationType.SUCCESS, organizationId, null);
        notificationDto.propagate = Boolean.TRUE;
        IntegrationModuleOrganizationConfiguration organizationConfiguration = prepareIntegrationModuleOrganizationConfiguration(organizationId);
        organizationConfiguration.slackWebhookUrl = null;

//        when
        when(integrationService.getOrganizationConfiguration(organizationId)).thenReturn(organizationConfiguration);
        pushNotificationService.createSlackPostMessageRequest(notificationDto);

//        then
        verify(integrationService, times(1)).getOrganizationConfiguration(anyLong());
        verify(httpRequestTaskRepository, never()).save(any(HttpRequestTask.class));
    }

    @Test
    public void checkOrganizationOnlyNotificationPushedToSlack() {
//        given
        long organizationId = 1L;
        NotificationDto notificationDto = new NotificationDto("message", Notification.NotificationType.SUCCESS, organizationId, null);
        notificationDto.propagate = Boolean.TRUE;
        IntegrationModuleOrganizationConfiguration organizationConfiguration = prepareIntegrationModuleOrganizationConfiguration(organizationId);

//        when
        when(integrationService.getOrganizationConfiguration(organizationId)).thenReturn(organizationConfiguration);
        pushNotificationService.createSlackPostMessageRequest(notificationDto);

//        then
        verify(integrationService, times(1)).getOrganizationConfiguration(anyLong());
        verify(httpRequestTaskRepository, times(1)).save(any(HttpRequestTask.class));
    }

    private IntegrationModuleOrganizationConfiguration prepareIntegrationModuleOrganizationConfiguration(long organizationId) {
        IntegrationModuleOrganizationConfiguration result = new IntegrationModuleOrganizationConfiguration(organizationId);
        result.trelloApiToken = result.trelloApiKey = result.trelloBoardName = result.trelloListName = result.gitHubToken
            = result.gitHubRepoName = result.gitHubRepoOwner = result.jiraToken = result.jiraRefreshToken = result.jiraOrganizationName
            = result.jiraProjectName = result.slackWebhookUrl = result.msTeamsWebhookUrl = result.email = "foo";
        return result;
    }

    @Test
    public void checkGlobalNotificationNotPushedToMsTeams() {
//        given
        NotificationDto notificationDto = new NotificationDto("message", Notification.NotificationType.SUCCESS, null);

//        when
        pushNotificationService.createMsTeamsPostMessageRequest(notificationDto);

//        then
        verify(integrationService, never()).getOrganizationConfiguration(anyLong());
    }

    @Test
    public void checkAnyUserNotificationNotPushedToMsTeams() {
//        given
        NotificationDto notificationDto = new NotificationDto("message", Notification.NotificationType.SUCCESS, null, 1L);

//        when
        pushNotificationService.createMsTeamsPostMessageRequest(notificationDto);

//        then
        verify(integrationService, never()).getOrganizationConfiguration(anyLong());
    }

    @Test
    public void checkOrganizationOnlyNotificationNotPushedToMsTeamsNoAttributePresent() {
//        given
        long organizationId = 1L;
        NotificationDto notificationDto = new NotificationDto("message", Notification.NotificationType.SUCCESS, organizationId, null);
        notificationDto.propagate = Boolean.TRUE;
        IntegrationModuleOrganizationConfiguration organizationConfiguration = prepareIntegrationModuleOrganizationConfiguration(organizationId);
        organizationConfiguration.msTeamsWebhookUrl = null;

//        when
        when(integrationService.getOrganizationConfiguration(organizationId)).thenReturn(organizationConfiguration);
        pushNotificationService.createMsTeamsPostMessageRequest(notificationDto);

//        then
        verify(integrationService, times(1)).getOrganizationConfiguration(anyLong());
        verify(httpRequestTaskRepository, never()).save(any(HttpRequestTask.class));
    }

    @Test
    public void checkOrganizationOnlyNotificationPushedToMsTeams() {
//        given
        long organizationId = 1L;
        NotificationDto notificationDto = new NotificationDto("message", Notification.NotificationType.SUCCESS, organizationId, null);
        notificationDto.propagate = Boolean.TRUE;
        IntegrationModuleOrganizationConfiguration organizationConfiguration = prepareIntegrationModuleOrganizationConfiguration(organizationId);

//        when
        when(integrationService.getOrganizationConfiguration(organizationId)).thenReturn(organizationConfiguration);
        pushNotificationService.createMsTeamsPostMessageRequest(notificationDto);

//        then
        verify(integrationService, times(1)).getOrganizationConfiguration(anyLong());
        verify(httpRequestTaskRepository, times(1)).save(any(HttpRequestTask.class));
    }

    @Test
    public void whenCreateGlobalNotificationNoEmailSent() {
//        given
        NotificationDto notificationDto = new NotificationDto("message", Notification.NotificationType.SUCCESS,null);

//        when
        pushNotificationService.createEmailNotification(notificationDto);

//        then
        verify(userRepository, never()).findOne(anyLong());
        verify(emailRepository, never()).save(any(Email.class));
    }

    @Test
    public void whenCreateUserNotificationEmailSent() {
//        given
        long userId = 1L;
        NotificationDto notificationDto = new NotificationDto("message", Notification.NotificationType.SUCCESS, null, userId);
        notificationDto.propagate = Boolean.TRUE;
        User user = new User("First", "Last", "email@email.com");
        Email email = mock(Email.class);

//        when
        when(userRepository.findOne(userId)).thenReturn(user);
        when(emailConstructor.prepareEmail(anyString(), anyString(), anyString(), anyString(), anyInt(), any(PageModelMap.class))).thenReturn(email);
        when(emailRepository.save(any(Email.class))).thenReturn(email);
        pushNotificationService.createEmailNotification(notificationDto);

//        then
        verify(emailRepository, times(1)).saveAll(any(List.class));
    }

    @Test
    public void whenCreateOrganizationNotificationAndNoUsersNoEmailSent() {
//        given
        long organizationId = 1L;
        NotificationDto notificationDto = new NotificationDto("message", Notification.NotificationType.SUCCESS, organizationId, null);
        notificationDto.propagate = Boolean.TRUE;
        Organization organization = new Organization("Test Organization");
        organization.setId(organizationId);

//        when
        when(userRoleRepository.getUsersInOrganization(organizationId)).thenReturn(Collections.emptySet());
        when(organizationRepository.findOne(organizationId)).thenReturn(organization);
        pushNotificationService.createEmailNotification(notificationDto);

//        then
        verify(userRoleRepository, times(1)).getUsersInOrganization(anyLong());
        verify(emailRepository, never()).saveAll(any(List.class));
    }

    @Test
    public void whenCreateOrganizationNotificationEmailSent() {
//        given
        long organizationId = 1L;
        NotificationDto notificationDto = new NotificationDto("message", Notification.NotificationType.SUCCESS, organizationId, null);
        notificationDto.propagate = Boolean.TRUE;
        Organization organization = new Organization("Test Organization");
        organization.setId(organizationId);
        Email email = mock(Email.class);
        User user = new User();
        user.setEmail("test@email.com");

//        when
        when(userRoleRepository.getUsersInOrganization(organizationId)).thenReturn(Collections.singleton(user));
        when(organizationRepository.findOne(organizationId)).thenReturn(organization);
        when(emailConstructor.prepareEmail(anyString(), anyString(), anyString(), anyString(), anyInt(), any(PageModelMap.class))).thenReturn(email);
        when(emailRepository.save(any(Email.class))).thenReturn(email);
        pushNotificationService.createEmailNotification(notificationDto);

//        then
        verify(userRoleRepository, times(1)).getUsersInOrganization(anyLong());
        verify(emailRepository, times(1)).saveAll(any(List.class));
    }
}
