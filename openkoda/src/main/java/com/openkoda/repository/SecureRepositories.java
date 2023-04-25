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

package com.openkoda.repository;

import com.openkoda.repository.admin.SecureAuditRepository;
import com.openkoda.repository.event.SecureEventListenerRepository;
import com.openkoda.repository.event.SecureSchedulerRepository;
import com.openkoda.repository.file.SecureFileRepository;
import com.openkoda.repository.notifications.SecureNotificationRepository;
import com.openkoda.repository.organization.SecureOrganizationRepository;
import com.openkoda.repository.user.SecureRoleRepository;
import com.openkoda.repository.user.SecureUserRepository;
import com.openkoda.repository.user.SecureUserRoleRepository;
import jakarta.inject.Inject;
import org.springframework.stereotype.Component;

/**
 *
 *
 * @author Arkadiusz Drysch (adrysch@stratoflow.com)
 * 
 */
@Component("SecureRepositories")
public class SecureRepositories {

    @Inject public SecureFrontendResourceRepository frontendResource;
    @Inject public SecureRoleRepository role;
    @Inject public SecureEventListenerRepository eventListener;
    @Inject public SecureSchedulerRepository scheduler;
    @Inject public SecureUserRoleRepository userRole;
    @Inject public SecureUserRepository user;
    @Inject public SecureOrganizationRepository organization;
    @Inject public SecureNotificationRepository notification;
    @Inject public SecureAuditRepository audit;
    @Inject public SecureFileRepository file;
    @Inject public SecureServerJsRepository serverJs;


}
