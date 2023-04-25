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
import com.openkoda.core.helper.PrivilegeHelper;
import com.openkoda.dto.user.BasicUser;
import com.openkoda.model.User;
import jakarta.annotation.PostConstruct;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.openkoda.core.service.event.ApplicationEvent.USER_MODIFIED;
import static com.openkoda.model.PrivilegeNames.*;

@Service
public class UserProvider extends ComponentProvider {

    private static UserProvider instance;

    @PostConstruct
    private void init() {
        instance = this;
        services.applicationEvent.registerEventListener(USER_MODIFIED, this::markUserAsModified);
    }

    private void markUserAsModified(BasicUser u) {
        repositories.unsecure.user.setUserAsModified(u.getId());
    }

    public static final Optional<OrganizationUser> getFromContext() {
        return getFromContext(true);
    }

    public static boolean isAuthenticated() {
        Optional<SecurityContext> context = Optional.ofNullable(SecurityContextHolder.getContext());
        return context.map( a -> a.getAuthentication() ).map( a -> a.isAuthenticated() ).orElse(false);
    }

    public static boolean isAnonymous() {
        SecurityContext c = SecurityContextHolder.getContext();
        return (c == null || c.getAuthentication() == null || c.getAuthentication() instanceof AnonymousAuthenticationToken);
    }

    private static final Optional<OrganizationUser> getFromContext(boolean checkWasModified) {

        SecurityContext context = SecurityContextHolder.getContext();
        if (context == null) {
            return Optional.empty();
        }

        Authentication authentication = context.getAuthentication();
        if (authentication == null) {
            return Optional.empty();
        }
        Object principal = authentication.getPrincipal();


        if (principal instanceof OrganizationUser) {
            Optional<OrganizationUser> p = Optional.ofNullable((OrganizationUser) principal);

            if (!p.map(a -> a.getUser()).isPresent()) {
                return p;
            }

            if (!(checkWasModified)) {
                return p;
            }

            User u = p.get().getUser();
            Long userId = u.getId();

            Optional<Boolean> wasModified = instance.repositories.unsecure.user.wasModifiedSince(userId, u.getUpdatedOn());
            Optional<Boolean> rolesWereModified = instance.repositories.unsecure.userRole.wasModifiedSince(userId, u.getUpdatedOn());
            boolean wasModifiedValue = wasModified.orElse(false);
            boolean rolesWereModifiedValue = rolesWereModified.orElse(false);
            if (wasModifiedValue || rolesWereModifiedValue) {

                User user = instance.repositories.unsecure.user.findOne(userId);
                LocalDateTime updatedOn = wasModifiedValue ? instance.repositories.unsecure.user.getUpdatedOn(userId) : instance.repositories.unsecure.userRole.getLastUpdatedOn(userId);

                user.setUpdatedOn(updatedOn);

                List<Tuple> info = instance.repositories.unsecure.user.getUserRolesAndPrivileges(user.getId());

                OrganizationUser userDetails = OrganizationUserDetailsService.setUserDetails(user, info, p.get());

                Authentication a = new PreAuthenticatedAuthenticationToken(
                        userDetails, "N/A", userDetails.getAuthorities());
                context.setAuthentication(a);
                SecurityContextHolder.setContext(context);

                return getFromContext();
            }

            return p;


        }
        return Optional.empty();
    }

    public static final long getUserIdOrNotExistingId() {
        long userId =
                getFromContext(false)
                .map( a -> a.getUserId() )
                .orElse( OrganizationUser.nonExistingUserId );
        return userId;
    }

    public static final String getUserIdOrNotExistingIdAsString() {
        return Long.toString(getUserIdOrNotExistingId());
    }

    public static final void clearAuthentication() {
        SecurityContextHolder.getContext().setAuthentication(null);
    }

    public static final void setCronJobAuthentication() {

        Set<String> globalPrivileges = PrivilegeHelper.getAdminPrivilegeStrings();
        Set<String> globalRoles = new HashSet<>();
        Map<Long, Set<String>> organizationPrivileges = new HashMap<>();
        Map<Long, Set<String>> organizationRoles = new HashMap<>();
        Map<Long, String> organizationNames = new LinkedHashMap<>();
        Collection<? extends GrantedAuthority> authorities = new ArrayList<>();

        UserDetails userDetails = new OrganizationUser(
                "_job_", "",
                true, true, true, true,
                authorities, globalPrivileges, globalRoles, organizationPrivileges, organizationRoles, null,
                organizationNames);

        Authentication a = new PreAuthenticatedAuthenticationToken(
                userDetails, "N/A", userDetails.getAuthorities());

        SecurityContextHolder.getContext().setAuthentication(a);

    }

    public static final void setOAuthAuthentication() {

        Set<String> globalPrivileges = Stream.of(_readUserData, _manageUserRoles).collect(Collectors.toSet());

        UserDetails userDetails = new OrganizationUser(
                "_oauth_", "",
                true, true, true, true,
                Collections.EMPTY_LIST, globalPrivileges, Collections.EMPTY_SET, Collections.EMPTY_MAP, Collections.EMPTY_MAP, null,
                Collections.EMPTY_MAP);

        Authentication a = new PreAuthenticatedAuthenticationToken(
                userDetails, "N/A", userDetails.getAuthorities());

        SecurityContextHolder.getContext().setAuthentication(a);

    }

    public static final void setConsumerAuthentication() {

        Set<String> globalPrivileges = Stream.of(_canReadBackend, _manageUserRoles).collect(Collectors.toSet());

        UserDetails userDetails = new OrganizationUser(
                "_consumer_", "",
                true, true, true, true,
                Collections.EMPTY_LIST, globalPrivileges, Collections.EMPTY_SET, Collections.EMPTY_MAP, Collections.EMPTY_MAP, null,
                Collections.EMPTY_MAP);


        Authentication a = new PreAuthenticatedAuthenticationToken(
                userDetails, "N/A", userDetails.getAuthorities());

        SecurityContextHolder.getContext().setAuthentication(a);

    }

}
