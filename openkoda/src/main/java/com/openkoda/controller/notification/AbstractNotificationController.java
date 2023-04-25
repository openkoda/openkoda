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

package com.openkoda.controller.notification;

import com.openkoda.core.controller.generic.AbstractController;
import com.openkoda.core.flow.Flow;
import com.openkoda.core.flow.PageModelMap;
import com.openkoda.core.security.OrganizationUser;
import com.openkoda.core.security.UserProvider;
import org.springframework.data.domain.Pageable;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;

public class AbstractNotificationController extends AbstractController {

    /**
     * <p>getAllNotifications</p>
     * Gets all user specific notifications
     */
    protected PageModelMap getAllNotifications(Long userId, Pageable notificationPageable) {
        debug("[getAllNotifications] UserId: {}", userId);
        Optional<OrganizationUser> user = UserProvider.getFromContext();
        Set<Long> organizationIds = user.get().getOrganizationIds();
        return Flow.init()
                .thenSet(notificationPage, a -> repositories.unsecure.notification.findAll(userId, organizationIds, notificationPageable))
                .execute();
    }

    /**
     * <p>getAllNotifications</p>
     * Gets all user specific notifications
     */
    protected PageModelMap getAllNotifications(Long userId, Long organizationId, Pageable notificationPageable) {
        debug("[getAllNotifications] UserId: {} orgId: {}", userId, organizationId);
        return Flow.init()
                .thenSet(notificationPage, a -> repositories.unsecure.notification.findAll(userId, Collections.singleton(organizationId), notificationPageable))
                .execute();
    }

    /**
     * <p>markAsRead</p>
     * Saves notifications into ReadNotificationRepository
     * @return
     */
    protected PageModelMap markAsRead(String unreadNotifications, Long userId) {
        debug("[markAsRead] UserId: {}", userId);
        return Flow.init()
                .then(a -> services.notification.markAsRead(unreadNotifications, userId))
                .execute();
    }

    /**
     * Marks all user's notifications as read
     *
     * @param userId
     * @return
     */
    protected PageModelMap markAllAsRead(Long userId, Long organizationId) {
        debug("[markAllAsRead] UserId: {} OrgId: {}", userId, organizationId);
        return Flow.init()
                .then(a -> services.notification.markAllAsRead(userId, organizationId))
                .execute();
    }
}
