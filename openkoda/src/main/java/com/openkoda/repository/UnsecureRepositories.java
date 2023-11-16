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

package com.openkoda.repository;

import com.openkoda.repository.admin.AuditRepository;
import com.openkoda.repository.event.EventListenerRepository;
import com.openkoda.repository.event.SchedulerRepository;
import com.openkoda.repository.file.FileRepository;
import com.openkoda.repository.notifications.NotificationRepository;
import com.openkoda.repository.notifications.ReadNotificationRepository;
import com.openkoda.repository.organization.OrganizationRepository;
import com.openkoda.repository.task.EmailRepository;
import com.openkoda.repository.task.HttpRequestTaskRepository;
import com.openkoda.repository.user.*;
import com.openkoda.repository.user.external.*;
import jakarta.inject.Inject;
import org.springframework.stereotype.Component;

/**
 *
 *
 * @author Arkadiusz Drysch (adrysch@stratoflow.com)
 * 
 */
@Component("UnsecureRepositories")
public class UnsecureRepositories {
//    USER
    @Inject public UserRepository user;
    @Inject public UserRoleRepository userRole;
    @Inject public FacebookUserRepository facebookUser;
    @Inject public GoogleUserRepository googleUser;
    @Inject public LDAPUserRepository ldapUser;
    @Inject public SalesforceUserRepository salesforceUser;
    @Inject public LinkedinUserRepository linkedinUser;
    @Inject public ApiKeyRepository apiKey;
    @Inject public LoginAndPasswordRepository loginAndPassword;

//    ROLES
    @Inject public RoleRepository role;
    @Inject public GlobalRoleRepository globalRole;
    @Inject public OrganizationRoleRepository organizationRole;
    @Inject public GlobalOrganizationRoleRepository globalOrganizationRole;

//    TASK
    @Inject public EmailRepository email;
    @Inject public HttpRequestTaskRepository httpRequest;

//    EVENT LISTENERS & SCHEDULERS
    @Inject public SchedulerRepository scheduler;
    @Inject public EventListenerRepository eventListener;

    //    NOTIFICATIONS
    @Inject
    public ReadNotificationRepository readNotification;
    @Inject
    public NotificationRepository notification;

//    OTHER
    @Inject public OrganizationRepository organization;
    @Inject public AuditRepository audit;
    @Inject public FrontendResourceRepository frontendResource;
    @Inject public ControllerEndpointRepository controllerEndpoint;
    @Inject public ServerJsRepository serverJs;
    @Inject public TokenRepository token;
    @Inject public GlobalSearchRepository search;
    @Inject public MapEntityRepository mapEntity;
    @Inject public FileRepository file;
    @Inject public IntegrationRepository integration;
    @Inject public FormRepository form;



}
