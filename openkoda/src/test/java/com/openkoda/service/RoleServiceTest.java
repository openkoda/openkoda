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

package com.openkoda.service;

import com.google.common.collect.Sets;
import com.openkoda.AbstractTest;
import com.openkoda.model.*;
import com.openkoda.service.user.RoleService;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.validation.BindingResult;

import java.util.HashSet;
import java.util.Set;

import static com.openkoda.model.Privilege.manageOrgData;
import static com.openkoda.model.Privilege.readOrgData;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.internal.verification.VerificationModeFactory.times;

/**
 * JUnits for {@link RoleService}
 *
 * @author Martyna Litkowska (mlitkowska@stratoflow.com)
 * @since 2019-01-28
 */
//@EnableMethodSecurity
public class RoleServiceTest extends AbstractTest {

    private static final String ROLE_NAME = "New Role";
    private static final HashSet<PrivilegeBase> ROLE_PRIVILEGES = Sets.newHashSet(readOrgData, manageOrgData);
    private static final Set<PrivilegeBase> ROLE_PRIVILEGES_EMPTY = Sets.newHashSet();

    @Autowired
    private RoleService roleService;

    @Test
    public void shouldNotCreateNewRoleByTypeAnyTestException() {
//          given
        mockAndAuthenticateUser(1l, "test@openkoda.com", "TEST", "");
//          when, then
        assertThrows(AccessDeniedException.class, () -> roleService.createRole(StringUtils.EMPTY, RoleService.ROLE_TYPE_GLOBAL, ROLE_PRIVILEGES_EMPTY));


    }

    @Test
    public void createNewRoleByTypeAnyTest() {
//        given
        mockAndAuthenticateUser(1l, "test@openkoda.com", "TEST", "(canManageBackend)");
//        when
        Mockito.when(globalRoleRepository.findByName(anyString()))
                .thenReturn(null);
        Mockito.when(organizationRoleRepository.findByName(anyString()))
                .thenReturn(null);

        Role result = roleService.createRole(StringUtils.EMPTY, RoleService.ROLE_TYPE_GLOBAL, ROLE_PRIVILEGES_EMPTY);

//        then
        assertNull(result);
    }

    @Test
    public void createNewRoleByTypeOrgTestException() {
//          given
        mockAndAuthenticateUser(1l, "test@openkoda.com", "TEST", "(canReadBackend)");

//          when, then
        assertThrows(AccessDeniedException.class, () -> roleService.createRole(ROLE_NAME, RoleService.ROLE_TYPE_ORG, ROLE_PRIVILEGES_EMPTY));
    }

    @Test
    @WithMockUser(roles = "TEST")
    public void createNewRoleByTypeOrgTest() {
//        given
        OrganizationRole role = new OrganizationRole();
        mockAndAuthenticateUser(1l, "test@openkoda.com", "TEST", "(canManageBackend)");

//        when
        Mockito.when(globalRoleRepository.findByName(anyString()))
                .thenReturn(null);
        Mockito.when(organizationRoleRepository.findByName(anyString()))
                .thenReturn(role);

        Role result = roleService.createRole(ROLE_NAME, RoleService.ROLE_TYPE_ORG, ROLE_PRIVILEGES_EMPTY);

//        then
        assertNotNull(result);
    }

    @Test
    public void shouldNotCreateNewRoleByTypeGlobalTestException() {
//          given
        mockAndAuthenticateUser(1l, "test@openkoda.com", "TEST", "(canReadBackend)");

//          when, then
        assertThrows(AccessDeniedException.class, () -> roleService.createRole(ROLE_NAME, RoleService.ROLE_TYPE_GLOBAL, ROLE_PRIVILEGES_EMPTY));

    }

    @Test
    public void createNewRoleByTypeGlobalTest() {
//        given
        GlobalRole role = new GlobalRole();
        User user = mockAndAuthenticateUser(1l, "test@openkoda.com", "TEST", "(canManageBackend)");

//        when
        Mockito.when(globalRoleRepository.findByName(anyString()))
                .thenReturn(role);
        Mockito.when(organizationRoleRepository.findByName(anyString()))
                .thenReturn(null);

        Role result = roleService.createRole(ROLE_NAME, RoleService.ROLE_TYPE_GLOBAL, ROLE_PRIVILEGES_EMPTY);

//        then
        assertNotNull(result);
    }

    @Test
    public void checkIfRoleNameNotExistsTest() {
//        given
        BindingResult br = mock(BindingResult.class);

//        when
        Mockito.when(globalRoleRepository.findByName(anyString()))
                .thenReturn(null);
        Mockito.when(organizationRoleRepository.findByName(anyString()))
                .thenReturn(null);

        boolean result = roleService.checkIfRoleNameAlreadyExists(ROLE_NAME, RoleService.ROLE_TYPE_GLOBAL, br);

//        then
        assertFalse(result);
    }

    @Test
    public void checkIfRoleNameExistsOrgTest() {
//        given
        BindingResult br = mock(BindingResult.class);
        OrganizationRole role = new OrganizationRole();

//        when
        Mockito.when(globalRoleRepository.findByName(anyString()))
                .thenReturn(null);
        Mockito.when(organizationRoleRepository.findByName(anyString()))
                .thenReturn(role);

        boolean result = roleService.checkIfRoleNameAlreadyExists(ROLE_NAME, RoleService.ROLE_TYPE_ORG, br);

//        then
        assertTrue(result);
        verify(br, times(1)).rejectValue(anyString(), anyString());
    }

    @Test
    public void checkIfRoleNameExistsGlobalTest() {
//        given
        BindingResult br = mock(BindingResult.class);
        GlobalRole role = new GlobalRole();

//        when
        Mockito.when(globalRoleRepository.findByName(anyString()))
                .thenReturn(role);
        Mockito.when(organizationRoleRepository.findByName(anyString()))
                .thenReturn(null);

        boolean result = roleService.checkIfRoleNameAlreadyExists(ROLE_NAME, RoleService.ROLE_TYPE_GLOBAL, br);

//        then
        assertTrue(result);
        verify(br, times(1)).rejectValue(anyString(), anyString());
    }

    @Test
    public void createGlobalRoleTest() {
//        given
        GlobalRole role = new GlobalRole();
        role.setName(ROLE_NAME);
        reset(globalRoleRepository);

//        when
        Mockito.when(globalRoleRepository.findByName(anyString()))
                .thenReturn(null)
                .thenReturn(role);

        GlobalRole result = roleService.createOrUpdateGlobalRole(ROLE_NAME, ROLE_PRIVILEGES, false);

//        then
        assertEquals (ROLE_NAME, result.getName());
        verify(globalRoleRepository, times(1)).save(any(GlobalRole.class));
    }

    @Test
    public void updateGlobalRoleTest() {
//        given
        GlobalRole role = new GlobalRole();
        role.setName(ROLE_NAME);
        reset(globalRoleRepository);

//        when
        Mockito.when(globalRoleRepository.findByName(anyString()))
                .thenReturn(role);

        GlobalRole result = roleService.createOrUpdateGlobalRole(ROLE_NAME, ROLE_PRIVILEGES, false);

//        then
        assertEquals (ROLE_NAME, result.getName());
        verify(globalRoleRepository, times(1)).save(any(GlobalRole.class));
    }

    @Test
    public void createOrgRoleTest() {
//        given
        OrganizationRole role = new OrganizationRole();
        role.setName(ROLE_NAME);
        reset(organizationRoleRepository);

//        when
        Mockito.when(organizationRoleRepository.findByName(anyString()))
                .thenReturn(null)
                .thenReturn(role);

        OrganizationRole result = roleService.createOrUpdateOrgRole(ROLE_NAME, ROLE_PRIVILEGES, false);

//        then
        assertEquals (ROLE_NAME, result.getName());
        verify(organizationRoleRepository, times(1)).save(any(OrganizationRole.class));
    }

    @Test
    public void updateOrgRoleTest() {
//        given
        OrganizationRole role = new OrganizationRole();
        role.setName(ROLE_NAME);
        reset(organizationRoleRepository);

//        when
        Mockito.when(organizationRoleRepository.findByName(anyString()))
                .thenReturn(role);

        OrganizationRole result = roleService.createOrUpdateOrgRole(ROLE_NAME, ROLE_PRIVILEGES, false);

//        then
        assertEquals (ROLE_NAME, result.getName());
        verify(organizationRoleRepository, times(1)).save(any(OrganizationRole.class));
    }

}
