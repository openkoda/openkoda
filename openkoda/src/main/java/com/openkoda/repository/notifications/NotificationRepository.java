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

package com.openkoda.repository.notifications;

import com.openkoda.core.repository.common.UnsecuredFunctionalRepositoryWithLongId;
import com.openkoda.model.notification.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Set;

public interface NotificationRepository extends UnsecuredFunctionalRepositoryWithLongId<Notification> {

    /**
     * <p>findAll</p>
     * <p>Returns new object NotificationKeeper which is holding a pair of Notification object and Notification id from ReadNotifications</p>
     * <p>We query for all Notifications based on 3 conditions: </p>
     * <p>1. Null organizationId and proper userId - user specific notification</p>
     * <p>2. Null userId and proper organizationIds - organization specific notification</p>
     * <p>3. Null userId and organizationId - global notification</p>
     * <p>Additionally we check priviliges</p>
     * <p>Return Page of NotificationKeeper ordered so Unread notifications (the ones with null value of rn.notificationId) come 1st</p>
     */
    @Query("SELECT new com.openkoda.repository.notifications.NotificationKeeper(n, rn.notificationId) FROM Notification n " +
            "LEFT JOIN n.readNotifications rn WHERE " +
            "NOT((n.hiddenFromAuthor = TRUE) AND (:userId = n.createdBy.createdById)) AND" +
            "(((n.organizationId IS NULL AND n.userId=:userId) OR " +
            "(n.userId IS NULL AND n.organizationId IN :organizationIds) OR " +
            "(n.userId IS NULL AND n.organizationId IS NULL)) " +
            ") ORDER BY rn.notificationId DESC, n.id DESC")
    Page<NotificationKeeper> findAll(@Param("userId") Long userId, @Param("organizationIds") Set<Long> organizationIds, Pageable pageable);

}
