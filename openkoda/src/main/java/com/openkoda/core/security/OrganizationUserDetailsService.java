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

package com.openkoda.core.security;

import com.openkoda.controller.common.SessionData;
import com.openkoda.controller.common.URLConstants;
import com.openkoda.core.flow.Tuple;
import com.openkoda.core.helper.PrivilegeHelper;
import com.openkoda.core.service.SessionService;
import com.openkoda.core.tracker.LoggingComponentWithRequestId;
import com.openkoda.model.User;
import com.openkoda.repository.user.UserRepository;
import com.openkoda.service.user.BasicPrivilegeService.PrivilegeChangeEvent;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Primary;
import org.springframework.context.event.EventListener;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * User Details Service that loads User from database and
 * prepares information about roles, organizations and privileges
 */
@Primary
@Service("customUserDetailsService")
@Transactional
public class OrganizationUserDetailsService implements UserDetailsService, URLConstants, LoggingComponentWithRequestId {

    @Inject
    private UserRepository userRepository;

    private final Map<String, List<Tuple>> subscribedUsers = new ConcurrentHashMap<>();

    /** {@inheritDoc} */
    @Override
    public UserDetails loadUserByUsername(String email) {
        debug("[loadUserByUsername] {}", email);
        User user = userRepository.findByEmailLowercase(email);

        if (user == null) {
            throw new UsernameNotFoundException("User not found");
        }
        List<Tuple> info = userRepository.getUserRolesAndPrivileges(user.getId());
        
        OrganizationUser organizationUser = setUserDetails(null, user, info);
        if(!subscribedUsers.containsKey(email)) {
            subscribedUsers.put(email, new ArrayList<>());
        }

        subscribedUsers.get(email).add(new Tuple(organizationUser, user));
        
        return organizationUser;
    }
    
    public UserDetails reloadUserByUsername(OrganizationUser organizationUser, User user) {
        debug("[reloadUserByUsername] {}", user.getEmail());
        List<Tuple> info = userRepository.getUserRolesAndPrivileges(user.getId());
        return setUserDetails(organizationUser, user, info);
    }

    public OrganizationUser setUserDetails(User user, List<Tuple> info, OrganizationUser base) {
        OrganizationUser newOrganizationUser = setUserDetails(null, user, info);
        newOrganizationUser.retainPrivileges(base.getRetainedPrivileges());
        newOrganizationUser.setSingleRequestAuth(base.isSingleRequestAuth());
        newOrganizationUser.setAuthMethod(base.getAuthMethod());
        return newOrganizationUser;
    }
    
    public OrganizationUser setUserDetails(User user, List<Tuple> info) {
        return setUserDetails(null, user, info);
    }
    /**
     * <p>setUserDetails.</p>
     *
     * @param user a {@link com.openkoda.model.User} object.
     * @param info a {@link java.util.List} object.
     * @return a {@link org.springframework.security.core.userdetails.UserDetails} object.
     */
    public OrganizationUser setUserDetails(final OrganizationUser organizationUser, final User user, List<Tuple> info) {
        Set<String> globalPrivileges = new HashSet<>();
        Set<String> globalRoles = new HashSet<>();
        Map<Long, Set<String>> organizationPrivileges = new HashMap<>();
        Map<Long, Set<String>> organizationRoles = new HashMap<>();
        Map<Long, String> organizationNames = new LinkedHashMap<>();
        Long firstOrganizationId = null;
        
        for (Tuple t : info) {
            Long userRoleId = t.v(Long.class, 0);
            String roleName = t.v(String.class, 1);
            String privilegesString = t.v(String.class, 2);
            Long organizationId = t.v(Long.class, 3);
            String organizationName = t.v(String.class, 4);

            if (organizationId == null) {
                globalPrivileges.addAll(PrivilegeHelper.fromJoinedStringToStringSet(privilegesString));
                globalRoles.add(roleName);
            } else {
                Set<String> roles = organizationRoles.get(organizationId);
                if (roles == null) {
                    roles = new HashSet<>();
                    organizationRoles.put(organizationId, roles);
                }
                Set<String> privileges = organizationPrivileges.get(organizationId);
                if (privileges == null) {
                    privileges = new HashSet<>();
                    organizationPrivileges.put(organizationId, privileges);
                }
                roles.add(roleName);
                privileges.addAll(PrivilegeHelper.fromJoinedStringToStringSet(privilegesString));
                organizationNames.put(organizationId, organizationName);

                if (firstOrganizationId == null) {
                    firstOrganizationId = organizationId;
                }
            }
        }

        Collection<? extends GrantedAuthority> authorities = globalRoles.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList());
        OrganizationUser newOrganizationUser = organizationUser;
        if(organizationUser == null) {
            newOrganizationUser = new OrganizationUser(
                    //User's email is username
                    user.getEmail(),
    
                    //if there is LoginAndPassword then use the password, default form authentication will need it
                    user.getLoginAndPassword() == null ? "" : user.getLoginAndPassword().getPassword(),
    
                    user.isEnabled(), true, true, true,
                    authorities, globalPrivileges, globalRoles, organizationPrivileges, organizationRoles, user, organizationNames);
            if(SecurityContextHolder.getContext().getAuthentication() != null && SecurityContextHolder.getContext().getAuthentication().getPrincipal() instanceof OrganizationUser) {
                OrganizationUser principal = (OrganizationUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
                newOrganizationUser.setAuthMethod(principal.getAuthMethod());
                newOrganizationUser.setSpoofed(principal.isSpoofed());
            }
        } else {
            organizationUser.resetPrivileges(globalPrivileges, globalRoles, organizationPrivileges, organizationRoles);

        }

        Locale userLocale = Locale.forLanguageTag(StringUtils.defaultIfBlank(user.getLanguage(), "en"));

        SessionService ss = SessionService.getInstance();
        ss.setAttributeIfSessionExists(SessionData.LOCALE, userLocale);

        return newOrganizationUser;
    }
    
    public boolean unsubscribeUser(String email) {
        subscribedUsers.remove(email);
        return true;
    }
    
    @EventListener(classes = PrivilegeChangeEvent.class)
    protected void onPrivilegesChanged( ) {
        subscribedUsers.entrySet().stream().flatMap( e -> e.getValue().stream()).forEach( u -> {
            debug("[onPrivilegesChanged] Privileges have changed, handling OrganizationUser {}", ((OrganizationUser)u.getV0()).getUsername());
            reloadUserByUsername((OrganizationUser)u.getV0(), (User)u.getV1());
            reloadUserByUsername(UserProvider.getFromContext().get(), (User)u.getV1());
        });
    }
}
