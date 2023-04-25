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

import com.openkoda.core.helper.PrivilegeHelper;
import com.openkoda.model.common.AuditableEntity;
import com.openkoda.model.common.TimestampedEntity;
import jakarta.persistence.*;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.Formula;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.*;

/**
 *
 *
 * @author Arkadiusz Drysch (adrysch@stratoflow.com)
 * 
 */
@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "token"}))
public class Token extends TimestampedEntity implements AuditableEntity {

    private static SecureRandom random = new SecureRandom();
    private static final int DEFAULT_EXPIRATION_TIME_IN_SECONDS = 2 * 24 * 3600; //2 days
    final static List<String> ignoredProperties = Arrays.asList("token");
    
    public Token(User u, boolean singleRequest, boolean singleUse, int expirationTimeInSeconds, Enum ... privileges) {
        this(u, expirationTimeInSeconds, privileges);
        this.singleUse = singleUse;
        this.singleRequest = singleRequest;
    }

    public Token(User u, boolean singleUse, Enum ... privileges) {
        this(u, privileges);
        this.singleUse = singleUse;
    }

    public Token(User u) {
        this();
        user = u;
        userId = user.getId();
    }

    public Token(User u, int expirationTimeInSeconds) {
        this(expirationTimeInSeconds);
        user = u;
        userId = user.getId();
    }

    public Token(User u, int expirationTimeInSeconds, Enum ... privileges) {
        this(u, expirationTimeInSeconds);
        this.privileges = PrivilegeHelper.toJoinedStringInParenthesis(privileges);
    }

    public Token(User u, Enum ... privileges) {
        this(u);
        this.privileges = PrivilegeHelper.toJoinedStringInParenthesis(privileges);
    }

    /**
     * <p>Constructor for Token.</p>
     *
     * @param expirationTimeInSeconds a int.
     */
    public Token(int expirationTimeInSeconds) {
        used = false;
        byte[] tokenBytes = new byte[61];
        random.nextBytes(tokenBytes);
        token = Base64.getUrlEncoder().encodeToString(tokenBytes);
        expiresOn = LocalDateTime.now().plusSeconds(expirationTimeInSeconds);
    }

    /**
     * <p>Constructor for Token.</p>
     */
    public Token() {
        this(DEFAULT_EXPIRATION_TIME_IN_SECONDS);
    }

    @Id
    @SequenceGenerator(name = GLOBAL_ID_GENERATOR, sequenceName = GLOBAL_ID_GENERATOR, initialValue = INITIAL_GLOBAL_VALUE, allocationSize = 10)
    @GeneratedValue(generator = GLOBAL_ID_GENERATOR, strategy = GenerationType.SEQUENCE)
    private Long id;

    //TODO Rule 4.4: should be marked with @JsonIgnore and FetchType = LAZY
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(insertable = false, updatable = false, name = "user_id")
    private Long userId;

    private String token;

    @Column(name = "used")
    private boolean used;

    @Column(name = "single_use")
    private boolean singleUse = true;

    @Column(name = "single_request")
    private boolean singleRequest = false;

    @Column(name = "expires_on")
    private LocalDateTime expiresOn;

    @Formula("(used = false AND expires_on > current_timestamp)")
    private boolean isValid;

    @Transient
    private Set<Enum> privilegesSet;

    /**
     * Token authentication can limit (but not extend) the privileges given to the user.
     * If privileges == null, then all user's privileges are given to the user on authentication.
     * Any privilege that user does not have, is ignored.
     */
    @Column(length = 65535)
    private String privileges;

    public Set<Enum> getPrivilegesSet() {
        if ( privilegesSet == null ) {
            privilegesSet = PrivilegeHelper.fromJoinedStringInParenthesisToPrivilegeEnumSet( privileges );
        }
        return privilegesSet;
    }

    public boolean hasPrivileges() {
        return privileges != null;
    }

    /**
     * <p>invalidate.</p>
     *
     * @return a {@link com.openkoda.model.Token} object.
     */
    public Token invalidate() {
        used = true;
        return this;
    }

    /**
     * <p>invalidate if single-use.</p>
     *
     * @return a {@link com.openkoda.model.Token} object.
     */
    public Token invalidateIfSingleUse() {
        if (singleUse) {
            invalidate();
        }
        return this;
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
     * <p>Getter for the field <code>token</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getToken() {
        return token;
    }

    /**
     * <p>isUsed.</p>
     *
     * @return a boolean.
     */
    public boolean isUsed() {
        return used;
    }

    /**
     * <p>Getter for the field <code>expiresOn</code>.</p>
     *
     * @return a {@link java.time.LocalDateTime} object.
     */
    public LocalDateTime getExpiresOn() {
        return expiresOn;
    }

    /**
     * <p>isValid.</p>
     *
     * @return a boolean.
     */
    public boolean isValid() {
        return isValid;
    }

    public String getPrivileges() {
        return privileges;
    }

    public Long getUserId() {
        return userId;
    }

    public String getUserIdAndTokenBase64String() {
        return Base64.getEncoder().encodeToString(StringUtils.join(userId, ":", token).getBytes());
    }

    public boolean isSingleRequest() {
        return singleRequest;
    }

    @Override
    public Collection<String> ignorePropertiesInAudit() {
        return ignoredProperties;
    }

    @Override
    public String toAuditString() {
        return " ID: " + id;
    }
}
