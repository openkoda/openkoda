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

import com.openkoda.core.helper.collections.UnmodifiableMapWithRemove;
import com.openkoda.core.helper.collections.UnmodifiableSetWithRemove;
import com.openkoda.core.tracker.LoggingComponentWithRequestId;
import com.openkoda.model.Privilege;
import com.openkoda.model.PrivilegeBase;
import com.openkoda.model.authentication.LoggedUser;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.*;
import java.util.stream.Collectors;

public class OrganizationUser extends User implements OAuth2User, HasSecurityRules, LoggingComponentWithRequestId {

    private Set<PrivilegeBase> retainedPrivileges;
    private Set<String> globalPrivileges;
    private Set<String> globalRoles;
    private Map<Long, Set<String>> organizationPrivileges;
    private Map<Long, Set<String>> organizationRoles;
    private final Map<Long, String> organizationNames;
    private final com.openkoda.model.User user;
    private final Long defaultOrganizationId;
    private boolean isSpoofed;
    private boolean isSingleRequestAuth;
    private LoggedUser.AuthenticationMethods authMethod = LoggedUser.AuthenticationMethods.PASSWORD;
    private OAuth2User oauth2User;

    //a set that MUST NOT contain a existing Organization id
    //this is used in JPQL to support IN operator, which crashes on empty collection.
    public static final Long nonExistingOrganizationId = -1L;
    public static final Long nonExistingUserId = -2L;
    private static final Set<Long> nonExistingOrganizationIds = Collections.singleton(nonExistingOrganizationId);

    private static final String nonExistingPrivilege = " does not exist ";


    public OrganizationUser(String username, String password, boolean enabled, boolean accountNonExpired, boolean credentialsNonExpired, boolean accountNonLocked, Collection<? extends GrantedAuthority> authorities, Set<String> globalPrivileges, Set<String> globalRoles, Map<Long, Set<String>> organizationPrivileges, Map<Long, Set<String>> organizationRoles, com.openkoda.model.User user, Map<Long, String> organizationNames) {
        super(username, password, enabled, accountNonExpired, credentialsNonExpired, accountNonLocked, authorities);

        this.globalPrivileges = prepareImmutableSet(globalPrivileges, nonExistingPrivilege);
        this.globalRoles = prepareImmutableSet(globalRoles, null);
        this.organizationPrivileges = prepareImmutableSetsMap(organizationPrivileges, nonExistingPrivilege);
        this.organizationRoles = prepareImmutableSetsMap(organizationRoles, null);
        this.user = user;
        this.organizationNames = Collections.unmodifiableMap(organizationNames);
        this.isSpoofed = false;
        defaultOrganizationId = organizationNames.isEmpty() ? nonExistingOrganizationId : organizationNames.keySet().iterator().next();
    }

    private Map<Long, Set<String>> prepareImmutableSetsMap(Map<Long, Set<String>> map, String additionalValue) {
        debug("[prepareImmutableSetsMap]");
        Map<Long, Set<String>> result = new HashMap<>(map.size());

        for ( Map.Entry<Long, Set<String>> e : map.entrySet()) {
            result.put(e.getKey(), prepareImmutableSet(e.getValue(), additionalValue));
        }

        return new UnmodifiableMapWithRemove(result);

    }

    private Set<String> prepareImmutableSet(Set<String> set, String additionalValue) {
        debug("[prepareImmutableSet]");
        boolean noAdditional = (additionalValue == null);
        if (noAdditional) { return new UnmodifiableSetWithRemove(set); }

        Set<String> result = new HashSet(set.size() + 1);
        result.addAll(set);
        result.add(additionalValue);
        return new UnmodifiableSetWithRemove(result);
    }

    public static Optional<OrganizationUser> getFromContext() {
        return UserProvider.getFromContext();
    }

    public boolean hasGlobalPrivilege(String p) {
        return hasGlobalPrivilege(p, globalPrivileges);
    }

    public boolean hasOrgPrivilege(String p, Long orgId) {
        return hasOrgPrivilege(p, orgId, organizationPrivileges);
    }

    public boolean hasGlobalPrivilege(Privilege p) {
        return hasGlobalPrivilege(p, globalPrivileges);
    }

    public boolean hasOrgPrivilege(Privilege p, Long orgId) {
        return hasOrgPrivilege(p, orgId, organizationPrivileges);
    }

    public boolean hasGlobalOrOrgPrivilege(Privilege privilege, Long orgId) {
        return hasGlobalOrOrgPrivilege(privilege, orgId, globalPrivileges, organizationPrivileges);
    }

    public boolean hasGlobalOrOrgPrivilege(String privilegeName, Long orgId) {
        return hasGlobalOrOrgPrivilege(privilegeName, orgId, globalPrivileges, organizationPrivileges);
    }

    public Set<Long> getOrganizationIds() {
        trace("[getOrganizationIds]");
        Set<Long> result = organizationPrivileges.keySet();
        return result.isEmpty() ? nonExistingOrganizationIds : result;
    }

    public Set<Long> getOrganizationIdsWithPrivilege(String privilegeName) {
        debug("[getOrganizationIdsWithPrivilege] {}", privilegeName);
        Set<Long> result = organizationPrivileges.entrySet().stream().filter( a -> a.getValue().contains(privilegeName)).map( a -> a.getKey() ).collect(Collectors.toSet());
        return result.isEmpty() ? nonExistingOrganizationIds : result;
    }
    public List<String> getOrganizationWithPrivilegePairs() {
        debug("[getOrganizationWithPrivilegePairs]");
        List<String> result = new ArrayList<>();
        if(organizationPrivileges.entrySet().isEmpty()) {
            result.add("");
        }
        for (Map.Entry<Long, Set<String>> e : organizationPrivileges.entrySet()) {
            for (String s: e.getValue()) {
                result.add(e.getKey() + s);
            }
        }
        return result;
    }

    public Long getDefaultOrganizationId() {
        return defaultOrganizationId;
    }

    public Collection<?> getRolesInfo() {
        return Arrays.asList(globalRoles, organizationRoles);
    }

    public com.openkoda.model.User getUser() {
        return user;
    }

    public long getUserId() {
        return user == null ? nonExistingUserId : user.getId();
    }

    public Map<Long, String> getOrganizationNames() {
        return organizationNames;
    }

    public Set<String> getGlobalPrivileges() { return globalPrivileges;  }

    public boolean isSpoofed() {
        return isSpoofed;
    }

    public void setSpoofed(boolean spoofed) {
        isSpoofed = spoofed;
    }

    public Set<PrivilegeBase> getRetainedPrivileges() {
        return retainedPrivileges;
    }

    public boolean isSingleRequestAuth() {
        return isSingleRequestAuth;
    }

    public void setSingleRequestAuth(boolean singleRequestAuth) {
        isSingleRequestAuth = singleRequestAuth;
    }

    public LoggedUser.AuthenticationMethods getAuthMethod() {
        return authMethod;
    }

    public void setAuthMethod(LoggedUser.AuthenticationMethods authMethod) {
        this.authMethod = authMethod;
    }
    
    

    public Set<String> getGlobalRoles() {
        return globalRoles;
    }

    public void setGlobalRoles(Set<String> globalRoles) {
        this.globalRoles = globalRoles;
    }

    public Map<Long, Set<String>> getOrganizationPrivileges() {
        return organizationPrivileges;
    }

    public void setOrganizationPrivileges(Map<Long, Set<String>> organizationPrivileges) {
        this.organizationPrivileges = organizationPrivileges;
    }

    public Map<Long, Set<String>> getOrganizationRoles() {
        return organizationRoles;
    }

    public void setOrganizationRoles(Map<Long, Set<String>> organizationRoles) {
        this.organizationRoles = organizationRoles;
    }

    public void setGlobalPrivileges(Set<String> globalPrivileges) {
        this.globalPrivileges = globalPrivileges;
    }

    /**
     * Privileges should be immutable, but for certain authentication scenarios we want to narrow down the privileges.
     * So it is possible to retain privileges by the external set.
     * To serve this purpose we use collections that allow removal of elements, but block adding new ones.
     * @param privilegesToLeave
     */
    void retainPrivileges(Set<PrivilegeBase> privilegesToLeave) {
        debug("[retainPrivileges]");
        if (privilegesToLeave == null) {
            return;
        }
        retainedPrivileges = privilegesToLeave;
        Set<String> privileges = privilegesToLeave.stream().map(s -> s.name()).collect(Collectors.toSet());
        globalPrivileges.retainAll(privileges);
        organizationPrivileges.forEach( (k, v) -> v.retainAll(privileges));
    }

    public static final OrganizationUser empty = new OrganizationUser(
            "_anonymous_",
            "",
            true,
            true,
            true,
            true,
            Collections.emptySet(), Collections.singleton(" void privilege "), Collections.emptySet(), Collections
            .emptyMap(),
            Collections.emptyMap(), null, Collections.emptyMap());


    @Override
    public Map<String, Object> getAttributes() {
        return oauth2User.getAttributes();
    }

    @Override
    public String getName() {
        return oauth2User.getAttribute("name");
    }

    public void setOauth2User(OAuth2User oauth2User) {
        this.oauth2User = oauth2User;
    }
    
    public boolean resetPrivileges(Set<String> globalPrivileges, Set<String> globalRoles, Map<Long, Set<String>> organizationPrivileges, Map<Long, Set<String>> organizationRoles) {
        this.globalPrivileges = globalPrivileges;
        this.globalRoles = globalRoles;
        this.organizationPrivileges = organizationPrivileges;
        this.organizationRoles = organizationRoles;
        return true;
    }
}
