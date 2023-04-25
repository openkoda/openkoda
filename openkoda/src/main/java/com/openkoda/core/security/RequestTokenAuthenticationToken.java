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

package com.openkoda.core.security;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import java.util.Collections;
import java.util.Set;

/**
 * Authentication Token given to Spring Security to authenticate user with Request Token.
 * The object is created by Authentication Filter, eg AbstractTokenAuthenticationFilter.
 * Token entity can limit (but not extend) the privileges given to the user.
 */
public class RequestTokenAuthenticationToken extends UsernamePasswordAuthenticationToken {


    private final String token;

    private final Long userId;

    private final Set<Enum> privileges;

    private final boolean singleRequest;

    public RequestTokenAuthenticationToken(Long userId, String email, String token, Set<Enum> privileges, boolean singleRequest) {
        super(email, null);
        this.token = token;
        this.userId = userId;
        this.privileges = Collections.unmodifiableSet(privileges);
        this.singleRequest = singleRequest;
        setAuthenticated(false);
    }

    public String getToken() {
        return token;
    }

    public Long getUserId() {
        return userId;
    }

    public Set<Enum> getPrivileges() {
        return privileges;
    }

    public boolean isSingleRequest() {
        return singleRequest;
    }

    public boolean hasPrivileges() {
        return !privileges.isEmpty();
    }
}
