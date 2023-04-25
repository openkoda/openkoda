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

package com.openkoda.core.helper;

import com.openkoda.core.security.OrganizationUser;
import com.openkoda.core.security.UserProvider;
import com.openkoda.model.User;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

/**
 *
 *<p>UserHelper provides a set of static methods for provides operations on user</p>
 *
 * @author Arkadiusz Drysch (adrysch@stratoflow.com)
 *
 */
@Component("user")
public class UserHelper {

    /**
     * <p>Returns set of organizations.</p>
     *
     * @return a {@link java.util.Set} object.
     */
    public Set<Map.Entry<Long, String>> organizations() {
        return UserProvider.getFromContext().map( OrganizationUser::getOrganizationNames ).map(Map::entrySet).orElse(Collections.emptySet());
    }

    /**
     * <p>Check if user has organizations.</p>
     *
     * @return a boolean.
     */
    public boolean hasOrganizations() {
        return UserProvider.getFromContext().map( OrganizationUser::getOrganizationNames ).map(a -> a.size() > 0).orElse(false);
    }

    /**
     * <p>Returns user id.</p>
     *
     * @return a {@link java.lang.Long} object.
     */
    public Long getUserId() {
        return UserProvider.getFromContext().map( OrganizationUser::getUser).map(User::getId ).orElse( -1L );
    }

    /**
     * <p>Returns user first name and lastname if exist, email address otherwise.</p>
     *
     * @return a {@link java.lang.Long} object.
     */
    public String getFullName() {
        return UserProvider.getFromContext().map( OrganizationUser::getUser)
                .map(user -> StringUtils.isNotBlank(user.getFirstName()) && StringUtils.isNotBlank(user.getLastName())
                        ? user.getFirstName() + " " + user.getLastName() : user.getEmail()).orElse( "Unlogged");
    }

    /**
     * <p>Returns user email.</p>
     *
     * @return a {@link java.lang.Long} object.
     */
    public String getEmail() {
        return UserProvider.getFromContext().map(OrganizationUser::getUser).map(User::getEmail).orElse("");
    }
}
