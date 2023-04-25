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

package com.openkoda.core.configuration;

import com.openkoda.core.security.OrganizationUser;
import com.openkoda.core.security.UserProvider;
import com.openkoda.core.tracker.LoggingComponentWithRequestId;
import com.openkoda.model.Privilege;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;

import java.io.IOException;
import java.util.Optional;
import java.util.Set;

/**
 * <p>CustomAuthenticationSuccessHandler</p>
 */
public class CustomAuthenticationSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler implements LoggingComponentWithRequestId {

    /**
     * default target url when user is in multiple organizations
     */
    private String pageAfterAuthForMultipleOrganizations;

    /**
     * default target url when user is in single organizations
     */
    private String pageAfterAuthForOneOrganization;

    /**
     * default target url when user is global admin
     */
    private String pageAfterAuthForGlobalAdmin;

    /**
     * context repository for saving the logged users context
     */
    private SecurityContextRepository securityContextRepository;

    /**
     * Request Cache, where unauthenticated request are stored and waiting for user login
     */
    private RequestCache requestCache = new HttpSessionRequestCache();

    public CustomAuthenticationSuccessHandler(String pageAfterAuthForMultipleOrganizations, String pageAfterAuthForOneOrganization, String pageAfterAuthForGlobalAdmin, SecurityContextRepository securityContextRepository) {
        this.pageAfterAuthForMultipleOrganizations = pageAfterAuthForMultipleOrganizations;
        this.pageAfterAuthForOneOrganization = pageAfterAuthForOneOrganization;
        this.pageAfterAuthForGlobalAdmin = pageAfterAuthForGlobalAdmin;
        this.securityContextRepository = securityContextRepository;
    }

    /**
     * Handles redirect after login, based on number of organizations assigned to user
     */
    @Override
    public void onAuthenticationSuccess(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Authentication authentication) throws IOException, ServletException {
        debug("[onAuthenticationSuccess]");
        SavedRequest savedRequest = this.requestCache.getRequest(httpServletRequest, httpServletResponse);
        this.securityContextRepository.saveContext(SecurityContextHolder.getContext(), httpServletRequest, httpServletResponse);
        if (savedRequest != null) {
            debug("[onAuthenticationSuccess] SavedRequest is {}", savedRequest.getRedirectUrl());
            super.onAuthenticationSuccess(httpServletRequest, httpServletResponse, authentication);
        } else {
            Optional<OrganizationUser> user = UserProvider.getFromContext();
            OrganizationUser authenticatedUser = user.get();
            Set<Long> organizationIds = authenticatedUser.getOrganizationIds();

            Long onlyOrgId = organizationIds.size() == 1 ? organizationIds.iterator().next() : -1L;

            debug("[onAuthenticationSuccess] user orgsIds {}, primary {}", organizationIds == null ? "[]" : organizationIds.toString(), onlyOrgId);

            if(authenticatedUser.hasGlobalPrivilege(Privilege.canAccessGlobalSettings)) {
                debug("[onAuthenticationSuccess] redirecting to admin dashboard {}", pageAfterAuthForGlobalAdmin);
                httpServletResponse.sendRedirect(pageAfterAuthForGlobalAdmin);
            } else if (onlyOrgId != -1L) {
                String redirectUrl = String.format(pageAfterAuthForOneOrganization, onlyOrgId);
                debug("[onAuthenticationSuccess] redirecting to single {}", redirectUrl);
                httpServletResponse.sendRedirect(redirectUrl);
            } else {
                debug("[onAuthenticationSuccess] redirecting to multiple {}", pageAfterAuthForMultipleOrganizations);
                httpServletResponse.sendRedirect(pageAfterAuthForMultipleOrganizations);
            }
        }
    }
}
