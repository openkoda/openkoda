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

package com.openkoda.model.authentication;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.openkoda.model.User;
import jakarta.persistence.*;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * <p>Entity storing user login and password.</p>
 *
 * @author Arkadiusz Drysch (adrysch@stratoflow.com)
 */
@Entity
@DynamicUpdate
@Table(name = "login_and_password")
public class LoginAndPassword extends LoggedUser {

    final static List<String> ignoredProperties = Arrays.asList("password");

    @Id
    private Long id;

    @Column(unique = true)
    private String login;

    @JsonIgnore
    private String password;

    @MapsId
    @OneToOne
    @JoinColumn(name = "user_id")
    @JsonIgnore
    private User user;

    private boolean enabled;


    private static PasswordEncoder passwordEncoder;

    /**
     * <p>setPasswordEncoderOnce.</p>
     *
     * @param pe a {@link org.springframework.security.crypto.password.PasswordEncoder} object.
     */
    public static void setPasswordEncoderOnce(PasswordEncoder pe) {
        if (passwordEncoder != null) {
            //Password encoder already initialized
            return;
        }
        LoginAndPassword.passwordEncoder = pe;
    }

    /**
     * <p>Constructor for LoginAndPassword.</p>
     */
    public LoginAndPassword() {
    }

    /**
     * <p>Constructor for LoginAndPassword.</p>
     *
     * @param login         a {@link java.lang.String} object.
     * @param plainPassword a {@link java.lang.String} object.
     * @param user          a {@link com.openkoda.model.User} object.
     */
    public LoginAndPassword(String login, String plainPassword, User user, boolean enabled) {
        this.login = login;
        this.password = passwordEncoder.encode(plainPassword);
        this.user = user;
        this.enabled = enabled;
    }

    /**
     * <p>Getter for the field <code>login</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getLogin() {
        return login;
    }

    /**
     * <p>Setter for the field <code>login</code>.</p>
     *
     * @param login a {@link java.lang.String} object.
     */
    public void setLogin(String login) {
        this.login = login;
    }

    /**
     * <p>Getter for the field <code>password</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getPassword() {
        return password;
    }

    /**
     * <p>Setter for the field <code>password</code>.</p>
     *
     * @param password a {@link java.lang.String} object.
     */
    public void setPassword(String password) {
        this.password = password;
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
     * <p>Setter for the field <code>user</code>.</p>
     *
     * @param user a {@link com.openkoda.model.User} object.
     */
    public void setUser(User user) {
        this.user = user;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toAuditString() {
        return login;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Long getId() {
        return id;
    }

    @Override
    public Collection<String> ignorePropertiesInAudit() {
        return ignoredProperties;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
