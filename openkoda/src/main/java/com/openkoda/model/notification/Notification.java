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
import com.openkoda.model.Organization;
import com.openkoda.model.User;
import com.openkoda.model.common.*;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.Formula;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.List;

import static com.openkoda.model.common.ModelConstants.ORGANIZATION_ID;

/**
 * <p>Notifications that are shown to the user in the application</p>
 *
 * @author Michał Nowak (mnowak@stratoflow.com)
 *
 */
@Entity
@Table(
        name = "notification",
        uniqueConstraints = {@UniqueConstraint(columnNames = {"user_id", ORGANIZATION_ID})}
)
public class Notification extends TimestampedEntity implements AuditableEntity, OrganizationRelatedEntity, SearchableEntity {

    public static final String REFERENCE_FORMULA = "(id)";

    public enum NotificationType {
        PRIMARY("primary"), SUCCESS("success"), WARNING("warning"), FAILURE("failure"), INFO("info"), ERROR("error");
        private String type;

        NotificationType(String type) {
            this.type = type;
        }

        public String getType() {
            return type;
        }
    }

    @Id
    @SequenceGenerator(name = ORGANIZATION_RELATED_ID_GENERATOR, sequenceName = ORGANIZATION_RELATED_ID_GENERATOR, initialValue = ModelConstants.INITIAL_ORGANIZATION_RELATED_VALUE, allocationSize = 10)
    @GeneratedValue(generator = ORGANIZATION_RELATED_ID_GENERATOR, strategy = GenerationType.SEQUENCE)
    private Long id;

    @Column(length = 65535)
    private String message;

    @Column
    private String subject;

    @Column
    @Enumerated(EnumType.STRING)
    @NotNull
    private NotificationType type;

    @JsonIgnore
    @ManyToOne(optional = false, fetch=FetchType.LAZY)
    @JoinColumn(insertable = false, updatable = false, name = ORGANIZATION_ID)
    private Organization organization;
    @Column(updatable = false, name = ORGANIZATION_ID)
    private Long organizationId;

    @JsonIgnore
    @ManyToOne(optional = false, fetch=FetchType.LAZY)
    @JoinColumn(insertable = false, updatable = false, name = "user_id")
    private User user;
    @Column(updatable = false, name = "user_id")
    private Long userId;

    @Column(name = "required_privilege")
    private String requiredPrivilege;

    @OneToMany(mappedBy = "notification", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private List<ReadNotification> readNotifications;

    @Column(length = 65535)
    private String attachmentURL;

    /**
     * if true then the message if pushed to integrated services
     */
    @Column(columnDefinition = "boolean default false")
    private boolean propagate;

    @Column(columnDefinition = "boolean default false")
    private boolean hiddenFromAuthor;

    /**
     * <p>Constructor for Notification</p>
     */
    public Notification() {
    }

    /**
     * <p>Constructor for global Notification</p>
     */
    public Notification(String message, NotificationType type, String requiredPrivilege) {
        this.message = message;
        this.type = type;
        this.requiredPrivilege = requiredPrivilege;
    }

    /**
     * <p>Construcor for Notification with specified organizationId with custom subject</p>
     */
    public Notification(String subject, String message, NotificationType type, Long organizationId, String requiredPrivilege) {
        this(subject, message, type, organizationId, requiredPrivilege, false, false);
    }

    public Notification(String subject, String message, NotificationType type, Long organizationId, String requiredPrivilege, boolean propagate, boolean hiddenFromAuthor) {
        this.subject = subject;
        this.message = message;
        this.type = type;
        this.organizationId = organizationId;
        this.requiredPrivilege = requiredPrivilege;
        this.propagate = propagate;
        this.hiddenFromAuthor = hiddenFromAuthor;
    }

    /**
     * <p>Construcor for Notification with specified organizationId</p>
     */
    public Notification(String message, NotificationType type, Long organizationId, String requiredPrivilege) {
        this.message = message;
        this.type = type;
        this.organizationId = organizationId;
        this.requiredPrivilege = requiredPrivilege;
    }

    /**
     * <p>Contructor for Notification with specified userId</p>
     */
    public Notification(String message, NotificationType type, String requiredPrivilege, Long userId) {
        this.message = message;
        this.type = type;
        this.userId = userId;
        this.requiredPrivilege = requiredPrivilege;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }


    public String getSubject() {
        return subject;
    }

    public NotificationType getType() {
        return type;
    }

    public Organization getOrganization() {
        return organization;
    }

    public User getUser() {
        return user;
    }

    public Long getUserId() {
        return userId;
    }

    public String getRequiredPrivilege() {
        return requiredPrivilege;
    }

    public List<ReadNotification> getReadNotifications() {
        return readNotifications;
    }

    @Override
    public String toAuditString() {
        return "ID: "+id;
    }

    @Override
    public Long getOrganizationId() {
        return organizationId;
    }

    @Formula(REFERENCE_FORMULA)
    private String referenceString;
    @Override
    public String getReferenceString() {
        return referenceString;
    }

    @Column(name = INDEX_STRING_COLUMN, length = INDEX_STRING_COLUMN_LENGTH)
    private String indexString;
    @Override
    public String getIndexString() {
        return indexString;
    }

    @Override
    public Long getId() {
        return id;
    }

    public String getAttachmentURL() {
        return attachmentURL;
    }

    public void setAttachmentURL(String attachmentURL) {
        this.attachmentURL = attachmentURL;
    }

    public boolean isPropagate() {
        return propagate;
    }

    public void setPropagate(boolean propagate) {
        this.propagate = propagate;
    }

    public boolean isHiddenFromAuthor() {
        return hiddenFromAuthor;
    }

    public void setHiddenFromAuthor(boolean hiddenFromAuthor) {
        this.hiddenFromAuthor = hiddenFromAuthor;
    }
}
