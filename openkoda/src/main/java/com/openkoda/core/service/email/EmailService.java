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

package com.openkoda.core.service.email;

import com.openkoda.controller.ComponentProvider;
import com.openkoda.core.flow.PageModelMap;
import com.openkoda.core.security.UserProvider;
import com.openkoda.dto.CanonicalObject;
import com.openkoda.dto.OrganizationRelatedObject;
import com.openkoda.model.User;
import com.openkoda.model.file.File;
import com.openkoda.model.task.Email;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

/**
 *
 *  <p>Saving emails in database from where
 *  they are later sent by {@link com.openkoda.core.job.EmailSenderJob}</p>
 *
 */
@Service
public class EmailService extends ComponentProvider {

    public Email sendAndSaveEmail(User recipient, String emailTemplateName) {
        debug("[sendAndSaveEmail] Sends {} to {}", emailTemplateName, recipient);
        return repositories.unsecure.email.save(services.emailConstructor.prepareEmailWithTitleFromTemplate(recipient, emailTemplateName));
    }
    public Email sendAndSaveEmail(User recipient, String emailTemplateName, PageModelMap model) {
        debug("[sendAndSaveEmail] Sends {} to {}", emailTemplateName, recipient);
        return repositories.unsecure.email.save(services.emailConstructor.prepareEmailWithTitleFromTemplate(recipient, null, emailTemplateName, model));
    }

    public Email sendAndSaveEmail(User recipient, String subject, String emailTemplateName, PageModelMap model) {
        debug("[sendAndSaveEmail] Sends {} to {}", emailTemplateName, recipient);
        return repositories.unsecure.email.save(services.emailConstructor.prepareEmailWithTitleFromTemplate(recipient, subject, emailTemplateName, model));
    }

    public Email sendAndSaveEmail(String email, String subject, String emailTemplateName, PageModelMap model, File... attachments) {
        debug("[sendAndSaveEmail] Sends {} to {}", emailTemplateName, email);
        return repositories.unsecure.email.save(services.emailConstructor.prepareEmailWithTitleFromTemplate(email, subject, email, emailTemplateName, model, attachments));
    }

    public Email sendAndSaveEmail(String email, String subject, String emailTemplateName, PageModelMap model, LocalDateTime sendOn, File... attachments) {
        debug("[sendAndSaveEmail] Sends {} to {}", emailTemplateName, email);
        Email emailToSend = services.emailConstructor.prepareEmailWithTitleFromTemplate(email, subject, email, emailTemplateName, model, attachments);
        if(sendOn != null) {
            emailToSend.setStartAfter(sendOn);
        }
        
        return repositories.unsecure.email.save(emailToSend);
    }
    
    public Email sendAndSaveOrganizationEmail(User recipient, String emailTemplateName, PageModelMap model, Long orgId) {
        debug("[sendAndSaveEmail] Sends {} to {}", emailTemplateName, recipient);
        Email orgEmail = services.emailConstructor.prepareEmailWithTitleFromTemplate(recipient, emailTemplateName, model);
        orgEmail.setOrganizationId(orgId);
        return repositories.unsecure.email.save(orgEmail);
    }

    public Email sendAndSaveEmail(CanonicalObject object, String templateName, String email) {
        debug("[sendAndSaveEmail] Sends {} to {}", templateName, email);
        return repositories.unsecure.email.save(services.emailConstructor.prepareEmailWithTitleFromTemplate(email, templateName, object));
    }

    public boolean sendEmailToAllInOrganization(OrganizationRelatedObject object, String templateName) {
        debug("[sendEmailToAllInOrganization] Sends {} to organization {} users", templateName, object.getOrganizationId());
        UserProvider.setConsumerAuthentication();
        repositories.unsecure.userRole.getUsersInOrganization(object.getOrganizationId())
                .forEach(user -> sendAndSaveEmail(user, templateName));
        UserProvider.clearAuthentication();
        return true;
    }

    public boolean sendEmailToUsersWithRoleInOrganization(Long organizationId, String templateName, PageModelMap model, String roleName) {
        debug("[sendEmailToUsersWithRoleInOrganization] Sends {} to organization {} users with role {}", templateName, organizationId, roleName);
        UserProvider.setConsumerAuthentication();
        repositories.unsecure.userRole.getUsersInOrganizationWithRole(organizationId, roleName)
                .forEach(user -> sendAndSaveOrganizationEmail(user, templateName, model, organizationId));
        UserProvider.clearAuthentication();
        return true;
    }
}
