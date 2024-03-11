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

package com.openkoda.uicomponent.live;

import com.openkoda.controller.common.PageAttributes;
import com.openkoda.core.flow.PageModelMap;
import com.openkoda.core.service.WebsocketService;
import com.openkoda.core.service.email.EmailService;
import com.openkoda.model.User;
import com.openkoda.model.file.File;
import com.openkoda.model.task.Email;
import com.openkoda.repository.user.UserRepository;
import com.openkoda.uicomponent.MessagesServices;
import jakarta.inject.Inject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Component
public class LiveMessagesServices implements MessagesServices {

    private static final String DEFAULT_EMAIL_TEMPLATE = "default";
    
    private static final String EMAIL_TEMPLATE_PATH = "frontend-resource/global/email/";
    
    @Inject
    UserRepository userRepository;
    @Inject EmailService emailService;
    @Inject WebsocketService websocketService;

    public Email sendEmail(User recipient, String emailTemplateName, PageModelMap model, Long orgId) {
        return emailService.sendAndSaveOrganizationEmail(recipient, emailTemplateName, model, orgId);
    }

    public Email sendEmail(Long userId, String subject, String message) {
        PageModelMap model = new PageModelMap();
        model.put(PageAttributes.message, message);
        return sendEmail(userRepository.findOne(userId).getEmail(), subject, DEFAULT_EMAIL_TEMPLATE, model, null);
    }

    public Email sendEmail(String email, String subject, String message, List<File> filesToAttach) {
        PageModelMap model = new PageModelMap();
        model.put(PageAttributes.message, message);
        return sendEmail(email, subject, DEFAULT_EMAIL_TEMPLATE, model, filesToAttach);
    }
    
    @Override
    public Email sendEmail(String email, String subject, String message, List<File> attachments, LocalDateTime sendOn) {
        PageModelMap model = new PageModelMap();
        model.put(PageAttributes.message, message);
        return sendEmail(email, subject, DEFAULT_EMAIL_TEMPLATE, model, attachments, sendOn);
    }

    @Override
    public Email sendEmail(String email, String subject, String resourceName, Map<String, Object> messageModel,
            List<File> attachments) {
        return sendEmail(email, subject, resourceName, messageModel, attachments, null);
    }

    @Override
    public Email sendEmail(String email, String subject, String resourceName, Map<String, Object> messageModel,
            List<File> attachments, LocalDateTime sendOn) {
        PageModelMap model = new PageModelMap();
        model.putAll(messageModel);
        return emailService.sendAndSaveEmail(email, subject, EMAIL_TEMPLATE_PATH + resourceName, model, sendOn, attachments == null ? null : attachments.toArray(File[]::new));
    }

    public boolean sendToWebsocketUser(User user, String channelName, Object payload) {
        return websocketService.sendToUserChannel(user, channelName, payload);
    }
    public boolean sendToWebsocketChannel(String channelName, Object payload){
        return websocketService.sendToChannel(channelName, payload);
    }
}
