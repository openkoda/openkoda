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

import com.openkoda.controller.ComponentProvider;
import com.openkoda.core.flow.Tuple;
import com.openkoda.model.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service to handle authentication logic on spoofing user action
 *
 * @author Martyna Litkowska (mlitkowska@stratoflow.com)
 * @since 2019-03-08
 */
@Service
public class RunAsService extends ComponentProvider implements HasSecurityRules {

    @Autowired
    @Lazy
    SecurityContextRepository securityContextRepository;

    @PreAuthorize(CHECK_CAN_IMPERSONATE_OR_IS_SPOOFED)
    public boolean startRunAsUser(User user, HttpServletRequest request, HttpServletResponse response) {
        return authRunAsUser(user, true, request, response);
    }

    @PreAuthorize(CHECK_CAN_IMPERSONATE_OR_IS_SPOOFED)
    public boolean exitRunAsUser(long backToUserId, HttpServletRequest request, HttpServletResponse response) {
        User user = repositories.unsecure.user.findOne(backToUserId);
        return authRunAsUser(user, false, request, response);
    }

    public boolean authRunAsUser(User user, boolean isSpoofed, HttpServletRequest request, HttpServletResponse response) {
        debug("[authRunAsUser] user: {} isSpoofed: {}", user, isSpoofed);
        if(user != null) {
//            SecurityContextHolder.clearContext();
            List<Tuple> info = repositories.unsecure.user.getUserRolesAndPrivileges(user.getId());
            OrganizationUser userDetails = (OrganizationUser) OrganizationUserDetailsService.setUserDetails(user, info);
            userDetails.setSpoofed(isSpoofed);

            Authentication auth = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(auth);
            SecurityContext securityContext = SecurityContextHolder.getContext();
            securityContextRepository.saveContext(securityContext, request, response);

            return true;
        }
        debug("[authRunAsUser] user is null");
        return false;
    }
}
