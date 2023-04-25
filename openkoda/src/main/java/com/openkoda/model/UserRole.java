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

package com.openkoda.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.openkoda.dto.user.UserRoleDto;
import com.openkoda.model.common.AuditableEntityOrganizationRelated;
import com.openkoda.model.common.ModelConstants;
import com.openkoda.model.common.SearchableEntity;
import com.openkoda.model.common.TimestampedEntity;
import jakarta.persistence.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Formula;

import java.io.Serializable;
import java.util.Optional;

import static com.openkoda.model.common.ModelConstants.ORGANIZATION_ID;

@Entity
/**
 * <p>UserRole class.</p>
 *
 * @author Arkadiusz Drysch (adrysch@stratoflow.com)
 *
 */
@Table(name = "users_roles",
        uniqueConstraints = {@UniqueConstraint(columnNames = {"user_id", ORGANIZATION_ID})}
)
public class UserRole extends TimestampedEntity implements AuditableEntityOrganizationRelated, SearchableEntity, Serializable {

    public static final String REFERENCE_FORMULA = DEFAULT_ORGANIZATION_RELATED_REFERENCE_FIELD_FORMULA;
    @Id
    @SequenceGenerator(name = ORGANIZATION_RELATED_ID_GENERATOR, sequenceName = ORGANIZATION_RELATED_ID_GENERATOR, initialValue = ModelConstants.INITIAL_ORGANIZATION_RELATED_VALUE, allocationSize = 10)
    @GeneratedValue(generator = ORGANIZATION_RELATED_ID_GENERATOR, strategy = GenerationType.SEQUENCE)
    private Long id;

    //TODO Rule 4.4: should be marked with FetchType = LAZY
    @JsonIgnore
    @ManyToOne(optional = true)
    @JoinColumn(nullable = true, insertable = false, updatable = false, name = "user_id")
    private User user;
    @Column(nullable = true, updatable = false, name = "user_id")
    private Long userId;

    //TODO Rule 4.4: should be marked with FetchType = LAZY
    @JsonIgnore
    @ManyToOne(optional = false)
    @JoinColumn(nullable = false, insertable = false, updatable = false, name = "role_id")
    private Role role;
    @Column(nullable = false, updatable = false, name = "role_id")
    private Long roleId;

    //TODO Rule 4.4: should be marked with FetchType = LAZY
    @JsonIgnore
    @ManyToOne(optional = true)
    @JoinColumn(nullable = true, insertable = false, updatable = false, name = ORGANIZATION_ID)
    private Organization organization;
    @Column(nullable = true, updatable = false, name = ORGANIZATION_ID)
    private Long organizationId;

    /**
     * <p>getRoleName.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    @JsonInclude()
    public String getRoleName() {
        return Optional.ofNullable(getRole()).map(a -> a.getName()).orElse("N/A");
    }

    /**
     * <p>getOrganizationName.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    @JsonInclude()
    public String getOrganizationName() {
        return organizationId == null ? null : getOrganization().getName();
    }


    /**
     * <p>Constructor for UserRole.</p>
     */
    public UserRole() {
    }

    /**
     * <p>Constructor for UserRole.</p>
     *
     * @param id a {@link java.lang.Long} object.
     */
    public UserRole(Long id) {
        this.id = id;
    }

    /**
     * <p>Constructor for UserRole.</p>
     *
     * @param id             a {@link java.lang.Long} object.
     * @param userId         a {@link java.lang.Long} object.
     * @param roleId         a {@link java.lang.Long} object.
     * @param organizationId a {@link java.lang.Long} object.
     */
    public UserRole(Long id, Long userId, Long roleId, Long organizationId) {
        this.id = id;
        this.userId = userId;
        this.roleId = roleId;
        this.organizationId = organizationId;
    }

    /**
     * <p>Constructor for UserRole.</p>
     *
     * @param id     a {@link java.lang.Long} object.
     * @param userId a {@link java.lang.Long} object.
     * @param roleId a {@link java.lang.Long} object.
     */
    public UserRole(Long id, Long userId, Long roleId) {
        this.id = id;
        this.userId = userId;
        this.roleId = roleId;
    }

    /**
     * <p>Getter for the field <code>id</code>.</p>
     *
     * @return a {@link java.lang.Long} object.
     */
    public Long getId() {
        return id;
    }

    /**
     * <p>Getter for the field <code>user</code>.</p>
     *
     * @return a {@link com.openkoda.model.User} object.
     */
    public User getUser() {
        return user;
    }

    /**
     * <p>Getter for the field <code>organization</code>.</p>
     *
     * @return a {@link com.openkoda.model.Organization} object.
     */
    public Organization getOrganization() {
        return organization;
    }

    /**
     * <p>Getter for the field <code>userId</code>.</p>
     *
     * @return a {@link java.lang.Long} object.
     */
    public Long getUserId() {
        return userId;
    }

    /**
     * <p>Getter for the field <code>roleId</code>.</p>
     *
     * @return a {@link java.lang.Long} object.
     */
    public Long getRoleId() {
        return roleId;
    }

    /**
     * <p>Getter for the field <code>organizationId</code>.</p>
     *
     * @return a {@link java.lang.Long} object.
     */
    public Long getOrganizationId() {
        return organizationId;
    }

    @Formula(REFERENCE_FORMULA)
    private String referenceString;

    @Override
    public String getReferenceString() {
        return referenceString;
    }


    /**
     * <p>Getter for the field <code>role</code>.</p>
     *
     * @return a {@link com.openkoda.model.Role} object.
     */
    public com.openkoda.model.Role getRole() {
        return role;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toAuditString() {
        StringBuilder sb = new StringBuilder();
        sb.append(Optional.ofNullable(getUser()).map(a -> a.getEmail()).orElse("N/A"));
        sb.append(":");
        sb.append(getRoleName());
        if (!isGlobal()) {
            sb.append("@");
            sb.append(organizationId);
        }
        return sb.toString();
    }


    @Override
    public String getIndexString() {
        return indexString;
    }

    @Column(name = INDEX_STRING_COLUMN, length = INDEX_STRING_COLUMN_LENGTH, insertable = false)
    @ColumnDefault("''")
    private String indexString;


    public UserRoleDto getUserRoleDto() {
        UserRoleDto dto = new UserRoleDto();
        dto.setId(this.id);
        dto.setRoleId(this.roleId);
        dto.setUserId(this.userId);
        dto.setOrganizationId(this.organizationId);
        return dto;
    }
}
