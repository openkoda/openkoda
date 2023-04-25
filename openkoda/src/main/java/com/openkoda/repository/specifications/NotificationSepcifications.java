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
