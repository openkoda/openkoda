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

package com.openkoda.service.notification;

import com.openkoda.controller.ComponentProvider;
import com.openkoda.core.service.event.ApplicationEvent;
import com.openkoda.dto.NotificationDto;
import com.openkoda.model.User;
import com.openkoda.model.notification.Notification;
import com.openkoda.model.notification.ReadNotification;
import com.openkoda.repository.notifications.NotificationKeeper;
import com.openkoda.repository.notifications.NotificationRepository;
import com.openkoda.repository.notifications.SecureNotificationRepository;
import jakarta.inject.Inject;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import static com.openkoda.repository.specifications.NotificationSepcifications.allUnreadForUser;

@Service
public class NotificationService extends ComponentProvider {

    @Inject
    SecureNotificationRepository secureNotificationRepository;
    @Inject
    NotificationRepository notificationRepository;

    /**
     * <p>getUsersUnreadNotificationsNumber</p>
     * returns number of all unread Notifications for a user
     */
    public int getUsersUnreadNotificationsNumber(Long userId, Set<Long> organizationIds) {
        debug("[getUsersUnreadNotificationsNumber]");
        return Math.toIntExact(secureNotificationRepository.count(allUnreadForUser(userId, organizationIds)));
    }

    /**
     * <p>getUsersUnreadNotification</p>
     */
    public List<Notification> getUsersUnreadNotifications(Long userId, Set<Long> organizationIds, Pageable notificationPageable) {
        debug("[getUsersUnreadNotifications]");

        Page<NotificationKeeper> keepers = notificationRepository.findAll(userId, organizationIds, notificationPageable);

        List<Notification> unreadNotificationsList = new ArrayList<>();
        for (NotificationKeeper k : keepers.getContent()) {
            if (k.getReadNotificationId() == null) {
                unreadNotificationsList.add(k.getNotification());
            }
        }
        return unreadNotificationsList;
    }

    /**
     * Change list of ids into string for easier sending to controller
     */
    public String getIdListAsString(List<Notification> notifications) {
        debug("[getIdListAsString]");

        List<Long> usersUnreadNotificationsIdList = new ArrayList<>();
        for (Notification n : notifications) {
            usersUnreadNotificationsIdList.add(n.getId());
        }
        String idString = usersUnreadNotificationsIdList.toString().replace("[", "").replace("]", "").replaceAll("\\s+", "");

        return idString;
    }

    public boolean createGlobalNotification(Notification.NotificationType type, String message, String requiredPrivilege, String attachmentURL) {
        debug("[createGlobalNotification]");
        Notification notification = new Notification(message, type, requiredPrivilege);
        notification.setAttachmentURL(attachmentURL);
        notificationRepository.save(notification);

        services.applicationEvent.emitEvent(ApplicationEvent.NOTIFICATION_CREATED, new NotificationDto(notification));
        return true;
    }

    public Notification createOrganizationNotification(Notification.NotificationType type, String message, Long organizationId, String requiredPrivilege, String attachmentURL) {
        debug("[createOrganizationNotification]");
        Notification notification = new Notification(message, type, organizationId, requiredPrivilege);
        notification.setAttachmentURL(attachmentURL);
        Notification n = notificationRepository.save(notification);

        services.applicationEvent.emitEvent(ApplicationEvent.NOTIFICATION_CREATED, new NotificationDto(notification));
        return n;
    }

    public Notification createOrganizationNotificationWithSubject(Notification.NotificationType type, String subject, String message, Long organizationId, String requiredPrivilege, String attachmentURL) {
        debug("[createOrganizationNotificationWithSubject]");
        return createOrganizationNotificationWithSubject(type, subject, message, organizationId, requiredPrivilege, attachmentURL, false, false);
    }


    public Notification createOrganizationNotificationWithSubject(Notification.NotificationType type, String subject, String message, Long organizationId, String requiredPrivilege, String attachmentURL, boolean propagate, boolean hiddenFromAuthor) {
        debug("[createOrganizationNotificationWithSubject]");
        Notification notification = new Notification(subject, message, type, organizationId, requiredPrivilege, propagate, hiddenFromAuthor);
        notification.setAttachmentURL(attachmentURL);
        Notification n = notificationRepository.save(notification);

        services.applicationEvent.emitEvent(ApplicationEvent.NOTIFICATION_CREATED, new NotificationDto(notification));
        return n;
    }

    public Notification createUserNotification(Notification.NotificationType type, String message, String requiredPrivilege, Long userId, String attachmentURL) {
        debug("[createUserNotification]");
        Notification notification = new Notification(message, type, requiredPrivilege, userId);
        notification.setAttachmentURL(attachmentURL);
        Notification n = notificationRepository.save(notification);

        services.applicationEvent.emitEvent(ApplicationEvent.NOTIFICATION_CREATED, new NotificationDto(notification));
        return n;
    }

    public boolean markAsRead(String unreadNotifications, Long userId) {
        debug("[markAsRead] userId: {}", userId);
        if (StringUtils.isNotBlank(unreadNotifications)) {
            List<String> idStringList = Arrays.asList(unreadNotifications.split(","));
            repositories.unsecure.readNotification.saveAll(idStringList.stream().map(idString -> new ReadNotification(userId, Long.valueOf(idString))).collect(Collectors.toSet()));
            return true;
        }
        return false;
    }

    public boolean markAllAsRead(Long userId, Long organizationId) {
        debug("[markAllAsRead] userId: {} orgId: {}", userId, organizationId);
        User user = repositories.unsecure.user.findOne(userId);
        if (user != null) {
            Set<Long> orgsId = new HashSet<>();
            if(organizationId != null && Arrays.asList(user.getOrganizationIds()).contains(organizationId)) {
                orgsId = Collections.singleton(organizationId);
            } else if(user.getOrganizationIds() != null) {
                orgsId = Set.of(user.getOrganizationIds());
            }
            List<Notification> allUnreadForUser = repositories.secure.notification.search(allUnreadForUser(userId, orgsId));
            repositories.unsecure.readNotification.saveAll(allUnreadForUser.stream().map(notification -> new ReadNotification(userId, notification.getId())).collect(Collectors.toSet()));
            return true;
        }
        return false;
    }

    public boolean isGlobal(NotificationDto notification) {
        debug("[isGlobal]");
        return notification.getOrganizationId() == null && notification.getUserId() == null;
    }

    public boolean isOrganization(NotificationDto notification) {
        debug("[isOrganization]");
        return notification.getOrganizationId() != null && notification.getUserId() == null;
    }

    public boolean isUsers(NotificationDto notification) {
        debug("[isUsers]");
        return notification.getUserId() != null && notification.getOrganizationId() == null;
    }

}
