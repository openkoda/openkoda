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

package com.openkoda.repository.specifications;

import com.openkoda.model.notification.Notification;
import com.openkoda.model.notification.ReadNotification;
import jakarta.persistence.criteria.*;
import org.springframework.data.jpa.domain.Specification;

import java.util.Set;

public class NotificationSepcifications {

    /**
     * <p>findAllUnreadNotificationsSpecification</p>
     * Generates query for getting all Unread Notifications
     */
    public static Specification<Notification> allUnreadForUser(Long id, Set<Long> organizationIds) {
        return new Specification<Notification>() {
            @Override
            public Predicate toPredicate(Root<Notification> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

                Predicate allUserNotificationsPredicate = getAllUserNotificationsPredicate(root, cb, id, organizationIds);

                Subquery<Long> sq = query.subquery(Long.class);
                Root<ReadNotification> readNotificationRoot = sq.from(ReadNotification.class);
                sq.select(readNotificationRoot.get("notificationId"));

                Predicate readPredicate = cb.in(root.get("id")).value(sq).not();

                return cb.and(allUserNotificationsPredicate, readPredicate);
            }
        };
    }

    /**
     * Generates query for all 3 cases described above
     */
    static Predicate getAllUserNotificationsPredicate(Root<Notification> root, CriteriaBuilder cb, Long id, Set<Long> organizationIds) {

        Predicate hiddenFromAuthorPredicate = cb.not(cb.and(cb.isTrue(root.get("hiddenFromAuthor")), cb.equal(root.get("createdBy").get("createdById"), id)));
        Predicate nullOrganizationIdPredicate = root.get("organizationId").isNull();
        Predicate userIdSpecificPredicate = cb.equal(root.get("userId"), id);
        Predicate userSpecificaPredicate = cb.and(nullOrganizationIdPredicate, userIdSpecificPredicate);

        Predicate nullUserIdPredicate = root.get("userId").isNull();
        Predicate globalPredicate = cb.and(nullOrganizationIdPredicate, nullUserIdPredicate);
        if(!organizationIds.isEmpty()) {
            Predicate organizationIdSpecificPredicate = root.get("organizationId").in(organizationIds);
            Predicate organizationSpecificaPredicate = cb.and(nullUserIdPredicate, organizationIdSpecificPredicate);
            return cb.and(hiddenFromAuthorPredicate, cb.or(userSpecificaPredicate, organizationSpecificaPredicate, globalPredicate));
        }
        return cb.and(hiddenFromAuthorPredicate, cb.or(userSpecificaPredicate, globalPredicate));
    }
}
