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
 * Entity storing the api key
 */
@Entity

@DynamicUpdate
@Table(name = "api_key")
public class ApiKey extends LoggedUser {

    final static List<String> ignoredProperties = Arrays.asList("apiKey");

    @Id
    private Long id;

    @JsonIgnore
    private String apiKey;

    @MapsId
    @OneToOne
    @JoinColumn(name = "user_id")
    @JsonIgnore
    private User user;

    private static PasswordEncoder passwordEncoder;

    /**
     * <p>setPasswordEncoderOnce.</p>
     *
     * @param pe a {@link PasswordEncoder} object.
     */
    public static void setPasswordEncoderOnce(PasswordEncoder pe) {
        if (passwordEncoder != null) {
            //Password encoder already initialized
            return;
        }
        ApiKey.passwordEncoder = pe;
    }

    public ApiKey() {
    }

    public ApiKey(String plainApiKey, User user) {
        this.apiKey = passwordEncoder.encode(plainApiKey);
        this.user = user;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public String toAuditString() {
        return id + "";
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public void setPlainApiKey(String plainApiKey) {
        this.apiKey = passwordEncoder.encode(plainApiKey);
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public Collection<String> ignorePropertiesInAudit() {
        return ignoredProperties;
    }
}