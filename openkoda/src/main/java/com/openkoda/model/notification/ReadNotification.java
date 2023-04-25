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

package com.openkoda.model.notification;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.openkoda.model.User;
import com.openkoda.model.common.ModelConstants;
import com.openkoda.model.common.TimestampedEntity;
import jakarta.persistence.*;


/**
 * <p>Notifications read by the user.</p>
 *
 * @author Michał Nowak (mnowak@stratoflow.com)
 *
 */
@Entity
@Table(
        name = "read_notification",
        uniqueConstraints = {@UniqueConstraint(columnNames = {"user_id", "notification_id"})}
)
public class ReadNotification extends TimestampedEntity {

    @JsonIgnore
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(nullable = false, insertable = false, updatable = false, name = "user_id")
    private User user;
    @Column(nullable = false, updatable = false, name = "user_id")
    private Long userId;

    @JsonIgnore
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(nullable = false, insertable = false, updatable = false, name = "notification_id")
    private Notification notification;
    @Column(nullable = false, updatable = false, name = "notification_id")
    private Long notificationId;

    @Id
    @SequenceGenerator(name = ORGANIZATION_RELATED_ID_GENERATOR, sequenceName = ORGANIZATION_RELATED_ID_GENERATOR, initialValue = ModelConstants.INITIAL_ORGANIZATION_RELATED_VALUE, allocationSize = 10)
    @GeneratedValue(generator = ORGANIZATION_RELATED_ID_GENERATOR, strategy = GenerationType.SEQUENCE)
    private Long id;


    /**
     * <p>Contructor for ReadNotification</p>
     */
    public ReadNotification() {
    }

    /**
     * <p>Contructor for ReadNotification</p>
     */
    public ReadNotification(Long userId, Long notificationId) {
        this.userId = userId;
        this.notificationId = notificationId;
    }

    public User getUser() {
        return user;
    }

    public Long getUserId() {
        return userId;
    }

    public Notification getNotification() {
        return notification;
    }

    public Long getNotificationId() {
        return notificationId;
    }

    public Long getId() {
        return id;
    }
}

