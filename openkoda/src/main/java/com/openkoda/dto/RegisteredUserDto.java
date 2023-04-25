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

package com.openkoda.dto;

import com.openkoda.form.RegisterUserForm;
import jakarta.servlet.http.Cookie;
import org.apache.commons.lang3.StringUtils;

public class RegisteredUserDto implements CanonicalObject {

    public String login;
    public String firstName;
    public String lastName;
    //TODO: change to 'websiteUrl' - it is more generic for projects where users register with a website url.
    public String websiteUrl;
    public String nickname;
    public long organizationId;
    public long userId;

    //TODO Rule 5.1 All fields in a DTO must be either a simple field (String, numbers, boolean, enum) or other DTO or collection of these
    //as cookies allows us to track registartion sources, they are an optional part of the DTO
    public Cookie[] cookies;

    public RegisteredUserDto(RegisterUserForm registerUserForm, long userId, long organizationId, Cookie[] cookies) {
        this.login = registerUserForm.getLogin();
        this.firstName = registerUserForm.getFirstName();
        this.lastName = registerUserForm.getLastName();
        this.websiteUrl = registerUserForm.getWebsiteUrl();
        this.nickname = registerUserForm.getNickname();
        this.organizationId = organizationId;
        this.userId = userId;
        this.cookies = cookies;
    }

    public RegisteredUserDto(String login, String firstName, String lastName, String websiteUrl, String nickname, long userId, long organizationId, Cookie[] cookies) {
        this.login = login;
        this.firstName = firstName;
        this.lastName = lastName;
        this.websiteUrl = websiteUrl;
        this.nickname = nickname;
        this.organizationId = organizationId;
        this.userId = userId;
        this.cookies = cookies;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getWebsiteUrl() {
        return websiteUrl;
    }

    public void setWebsiteUrl(String websiteUrl) {
        this.websiteUrl = websiteUrl;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public long getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(long organizationId) {
        this.organizationId = organizationId;
    }

    public Cookie[] getCookies() {
        return cookies;
    }

    public void setCookies(Cookie[] cookies) {
        this.cookies = cookies;
    }

    @Override
    public String notificationMessage() {
        StringBuilder sb = new StringBuilder("Registered User ");
        if(StringUtils.isNotEmpty(firstName) && StringUtils.isNotEmpty(lastName)) {
            sb.append(String.format("%s %s,", firstName, lastName));
        }
        sb.append(String.format("%s ", login));
        if(StringUtils.isNotEmpty(websiteUrl)) {
            sb.append(String.format("URL: %s ", websiteUrl));
        }
        if(StringUtils.isNotEmpty(nickname)) {
            sb.append(String.format("Nickname: %s ", nickname));
        }
        return sb.toString();
    }
}
