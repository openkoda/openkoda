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

package com.openkoda.dto;

import com.openkoda.model.notification.Notification;
import org.apache.commons.lang3.StringUtils;

public class NotificationDto implements CanonicalObject {

    public String attachmentURL;
    public String message;
    public String subject;
    public Notification.NotificationType notificationType;
    public Long userId;
    public Long organizationId;
    public String requiredPrivilege;
    public boolean propagate;

    public NotificationDto(){
    }

    public NotificationDto(String message, Notification.NotificationType type) {
        this.message = message;
        this.notificationType = type;
    }
    /**
     * <p>GlobalNotificationDto constructr</p>
     */
    public NotificationDto(String message, Notification.NotificationType type, String requiredPrivilege) {
        this.message = message;
        this.notificationType = type;
        this.requiredPrivilege = requiredPrivilege;
    }
    /**
     * <p>UserNotificationDto constructr</p>
     */
    public NotificationDto(String message, Notification.NotificationType type, String requiredPrivilege, Long userId) {
        this.message = message;
        this.notificationType = type;
        this.userId = userId;
        this.requiredPrivilege = requiredPrivilege;
    }

    /**
     * <p>OrganizationNotificationDto constructr</p>
     */
    public NotificationDto(String message, Notification.NotificationType type, Long organizationId, String requiredPrivilege) {
        this.message = message;
        this.notificationType = type;
        this.organizationId = organizationId;
        this.requiredPrivilege = requiredPrivilege;
    }

    /**
     * <p>OrganizationUserNotificationDto constructr</p>
     */
    public NotificationDto(String message, Notification.NotificationType type, Long organizationId, String requiredPrivilege, Long userId) {
        this.message = message;
        this.notificationType = type;
        this.organizationId = organizationId;
        this.userId = userId;
        this.requiredPrivilege = requiredPrivilege;
    }

    public NotificationDto(Notification notification) {
        this.message = notification.getMessage();
        this.subject = notification.getSubject();
        this.organizationId = notification.getOrganizationId();
        this.requiredPrivilege = notification.getRequiredPrivilege();
        this.notificationType = notification.getType();
        this.userId = notification.getUserId();
        this.attachmentURL = notification.getAttachmentURL();
        this.propagate = notification.isPropagate();
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSubject() {
        return StringUtils.isNotBlank(subject) ? subject : "Notification";
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public Notification.NotificationType getNotificationType() {
        return notificationType;
    }

    public void setNotificationType(Notification.NotificationType notificationType) {
        this.notificationType = notificationType;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(Long organizationId) {
        this.organizationId = organizationId;
    }

    public String getRequiredPrivilege() {
        return requiredPrivilege;
    }

    public void setRequiredPrivilege(String requiredPrivilege) {
        this.requiredPrivilege = requiredPrivilege;
    }

    public String getAttachmentURL() {
        return attachmentURL;
    }

    public void setAttachmentURL(String attachmentURL) {
        this.attachmentURL = attachmentURL;
    }

    public Boolean getPropagate() {
        return propagate;
    }

    public void setPropagate(Boolean propagate) {
        this.propagate = propagate;
    }

    @Override
    public String notificationMessage() {
        StringBuilder sb = new StringBuilder("Notification ");
        if(userId != null && organizationId != null) {
            sb.append(String.format("for %d(UID), within %d(OrgID) ", userId, organizationId));
        }
        sb.append(String.format("of type %s: \"%s\"", notificationType, message));
        return sb.toString();
    }
}
