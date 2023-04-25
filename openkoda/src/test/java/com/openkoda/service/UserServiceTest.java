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

package com.openkoda.service;

import com.openkoda.AbstractTest;
import com.openkoda.core.flow.PageModelMap;
import com.openkoda.core.flow.Tuple;
import com.openkoda.core.service.event.ApplicationEvent;
import com.openkoda.dto.user.InviteUserDto;
import com.openkoda.form.InviteUserForm;
import com.openkoda.form.RegisterUserForm;
import com.openkoda.model.*;
import com.openkoda.model.authentication.LoginAndPassword;
import com.openkoda.model.task.Email;
import com.openkoda.service.user.UserService;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuple3;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * JUnits for {@link UserService}
 *
 * @author Martyna Litkowska (mlitkowska@stratoflow.com)
 * @since 2019-01-29
 */
public class UserServiceTest extends AbstractTest {

    public static final String USER_FIRST_NAME = "Brad";
    public static final String USER_LAST_NAME = "Pitt";
    public static final String USER_EMAIL = "brad@pitt.com";
    public static final String ORG_ROLE_USER = "ROLE_ORG_USER";


    @Autowired
    private UserService userService;

    @Test
    public void createUserTest() {
//        given
        reset(userRepository);
        User user = mock(User.class);

//        when
        when(user.getId()).thenReturn(1L);
        when(userRepository.save(any(User.class))).thenReturn(user);
        userService.createUser(USER_FIRST_NAME, USER_LAST_NAME, USER_EMAIL, true, null, null);

//        then
        verify(userRepository).save(any(User.class));
        verify(userRepository).findOne(anyLong());
    }

    @Test
    public void addOrgRoleNullToUserTest() {
//        given
        reset(userRepository);
        User user = mock(User.class);

//        when
        List<UserRole> result = userService.addOrgRoleToUser(user, null);

//        then
        verify(roleRepository, never()).findByName(anyString());
        verify(userRepository, never()).save(any(User.class));
        assertEquals(0, result.size());
    }

    @Test
    public void addOrgRolesToUserTest() {
//        given
        reset(userRepository);
        User user = mock(User.class);
        Role role = mock(Role.class);
        Tuple2 uRole = new Tuple(ORG_ROLE_USER, 1L).t2();
        UserRole userRole = mock(UserRole.class);

//        when
        when(roleRepository.findByName(anyString())).thenReturn(role);
        when(user.getId()).thenReturn(1L);
        when(role.getId()).thenReturn(1L);
        when(userRoleRepository.save(any(UserRole.class))).thenReturn(userRole);

        List result = userService.addOrgRoleToUser(user, uRole);

//        then
        verify(roleRepository).findByName(anyString());
        verify(userRoleRepository).save(any(UserRole.class));
        assertEquals(1, result.size());
    }

    @Test
    public void addWrongOrgRolesToUserTest() {
//        given
        reset(userRepository);
        User user = mock(User.class);
        Tuple2 uRole = new Tuple(ORG_ROLE_USER, 1L).t2();
        UserRole userRole = mock(UserRole.class);
//        when
        when(roleRepository.findByName(anyString())).thenReturn(null);

        List result = userService.addOrgRoleToUser(user, uRole);

//        then
        verify(roleRepository).findByName(anyString());
        verify(userRepository, never()).save(any(User.class));
        assertEquals(0, result.size());
    }

    @Test
    public void addGlobalRoleNullToUserTest() {
//        given
        reset(userRepository);
        User user = mock(User.class);

//        when
        List<UserRole> result = userService.addGlobalRoleToUser(user, null);

//        then
        assertEquals(0, result.size());
    }

    @Test
    public void inviteNewOrExistingUserNullOrgTestException() {
        // given
        reset(userRepository);
        User user = mock(User.class);
        InviteUserForm userForm = new InviteUserForm();
        mockAndAuthenticateUser(1l, "test@openkoda.com", "TEST", "");

//          when, then
        assertThrows(IllegalArgumentException.class, () -> userService.inviteNewOrExistingUser(userForm, user, null));
    }


    @Test
    public void inviteNewOrExistingUserNullOrgTest() {
//        given
        User user = mockAndAuthenticateUser(1l, "test@openkoda.com", "TEST", "");
        InviteUserForm userForm = new InviteUserForm();

//        when
        try {
            Tuple3<Email, User, InviteUserForm> result = userService.inviteNewOrExistingUser(userForm, user, null);
        } catch (Exception e) {
            //success - this method should throw exception when organization is null;
            return;
        }
        fail("Organization must not be null");

    }

    @Test
    public void inviteNewUserTestException() {
        // given
        Organization organization = mock(Organization.class);
        InviteUserForm userForm = new InviteUserForm();
        mockAndAuthenticateUser(1l, "test@openkoda.com", "TEST", "");

//          when, then
        assertThrows(AccessDeniedException.class, () -> userService.inviteNewOrExistingUser(userForm, null, organization));

    }
    @Test
    public void inviteNewUserTest() {
//        given
        User user = mockAndAuthenticateUser(1l, "test@openkoda.com", "TEST", "(manageOrgData)");
        Organization organization = mock(Organization.class);
        Email email = mock(Email.class);
        LoginAndPassword loginAndPassword = mock(LoginAndPassword.class);
        InviteUserForm userForm = new InviteUserForm(new InviteUserDto(), user);
        userForm.dto.setFirstName(USER_FIRST_NAME);
        userForm.dto.setLastName(USER_LAST_NAME);
        userForm.dto.setEmail(USER_EMAIL);

//        when
        when(organization.getId()).thenReturn(1L);
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(applicationEventService.emitEvent(any(ApplicationEvent.class), any(User.class))).thenReturn(true);
        when(userRepository.findOne(anyLong())).thenReturn(user);
        when(tokenService.createTokenForUser(any(User.class), eq(Privilege.canResetPassword))).thenReturn(new Token());
        when(organizationRepository.findOne(anyLong())).thenReturn(organization);
        when(emailConstructor.prepareEmail(anyString(), anyString(), anyString(), anyString(), anyInt(), any(PageModelMap.class))).thenReturn(email);
        when(emailRepository.save(any(Email.class))).thenReturn(email);
        when(loginAndPasswordRepository.save(any(LoginAndPassword.class))).thenReturn(loginAndPassword);

        Tuple3<Email, User, InviteUserForm> result = userService.inviteNewOrExistingUser(userForm, null, organization);

//        then
        verify(userRepository, times(2)).save(any(User.class));
        assertEquals(email, email);
        assertEquals(user, result.getT2());
        assertNull(result.getT3().dto);
    }

    @Test
    public void inviteExistingUserTestException() {
        // given
        User user = mock(User.class);
        Organization organization = mock(Organization.class);
        InviteUserForm userForm = new InviteUserForm();
        mockAndAuthenticateUser(1l, "test@openkoda.com", "TEST", "");

//          when, then
        assertThrows(AccessDeniedException.class, () -> userService.inviteNewOrExistingUser(userForm, user, organization));


    }

    @Test
    public void inviteExistingUserTest() {
//        given
        User user = mockAndAuthenticateUser(1l, "test@openkoda.com", "TEST", "(manageOrgData)");
        Organization organization = mock(Organization.class);
        Email email = mock(Email.class);
        InviteUserForm userForm = new InviteUserForm(new InviteUserDto(), user);

//        when
        when(organization.getId()).thenReturn(1L);
        when(organizationRepository.findOne(anyLong())).thenReturn(organization);
        when(emailConstructor.prepareEmail(anyString(), anyString(), anyString(), anyString(), anyInt(), any(PageModelMap.class))).thenReturn(email);
        when(emailRepository.save(any(Email.class))).thenReturn(email);

        Tuple3<Email, User, InviteUserForm> result = userService.inviteNewOrExistingUser(userForm, user, organization);

//        then
        verify(userRepository, times(0)).save(any(User.class));
        verify(userRepository, times(0)).findOne(anyLong());
        assertEquals(email, email);
        assertEquals(user, result.getT2());
        assertNull(result.getT3().dto);
    }

    @Test
    public void registerUserAlreadyExistsTest() {
        //        given
        reset(userRepository);
        User user = mock(User.class);
        RegisterUserForm userForm = new RegisterUserForm();
        userForm.setLogin("test@login.com");

//        when
        when(userRepository.findByEmailLowercase(anyString())).thenReturn(user);

        User result = userService.registerUserOrReturnExisting(userForm);

//        then
        verify(userRepository, times(0)).save(any(User.class));
        assertNotNull(result);
    }

    @Test
    public void registerUserTest() {
        //        given
        reset(userRepository);
        User user = mock(User.class);
        Organization organization = mock(Organization.class);
        RegisterUserForm userForm = new RegisterUserForm();
        userForm.setLogin(USER_EMAIL);
        userForm.setFirstName(USER_FIRST_NAME);
        userForm.setLastName(USER_LAST_NAME);
        userForm.setPassword(StringUtils.EMPTY);

//        when
        when(userRepository.findByLogin(anyString())).thenReturn(null);
        when(organizationRepository.save(any(Organization.class))).thenReturn(organization);
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(user.getId()).thenReturn(1L);
        when(user.getName()).thenReturn(USER_FIRST_NAME + USER_LAST_NAME);
        when(user.getEmail()).thenReturn(USER_EMAIL);
        when(userRepository.findOne(anyLong())).thenReturn(user);
        when(organization.getId()).thenReturn(1L);
        when(tokenService.createTokenForUser(user, Privilege.canVerifyAccount)).thenReturn(new Token());

        User result = userService.registerUserOrReturnExisting(userForm);

//        then
        verify(userRepository, times(2)).save(any(User.class));

        assertNull(result);
    }

    @Test
    public void changePasswordNullLoginPasswordTest() {
//        given
        reset(userRepository);
        User user = mock(User.class);

//        when
        when(user.getLoginAndPassword()).thenReturn(null);

        userService.changePassword(user, StringUtils.EMPTY);

//        then
        verify(userRepository, never()).saveAndFlush(any(User.class));
    }

    @Test
    public void changePasswordTest() {
//        given
        reset(userRepository);
        User user = mock(User.class);
        LoginAndPassword loginAndPassword = mock(LoginAndPassword.class);
        String newPassword = "new-password";

//        when
        when(user.getLoginAndPassword()).thenReturn(loginAndPassword);
        when(userRepository.saveAndFlush(any(User.class))).thenReturn(user);

        userService.changePassword(user, newPassword);

//        then
        verify(loginAndPassword).setPassword(anyString());
        verify(userRepository).saveAndFlush(any(User.class));
    }

    @Test
    public void resendVerificationEmailEmpty() {
//        given
        String email = "";

//        when
        boolean result = userService.resendAccountVerificationEmail(email);

//        then
        assertFalse(result);
    }

    @Test
    public void resendVerificationEmailUserNull() {
//        given

//        when
        when(userRepository.findByEmailLowercase(USER_EMAIL)).thenReturn(null);
        boolean result = userService.resendAccountVerificationEmail(USER_EMAIL);

//        then
        assertFalse(result);
    }

    @Test
    public void resendVerificationEmailUserNotNull() {
//        given
        User user = mock(User.class);
        user.setEmail(USER_EMAIL);

//        when
        when(userRepository.findByEmailLowercase(USER_EMAIL)).thenReturn(user);
        when(user.getId()).thenReturn(1L);
        when(user.getName()).thenReturn(USER_FIRST_NAME + USER_LAST_NAME);
        when(user.getEmail()).thenReturn(USER_EMAIL);
        when(tokenService.createTokenForUser(user, Privilege.canVerifyAccount)).thenReturn(new Token());
        boolean result = userService.resendAccountVerificationEmail(USER_EMAIL);

//        then
        assertTrue(result);
    }

//    @Ignore
//    @Test
//    public void getMonthlyRequestLimitTest() {
////        given
//        reset(userRepository);
//        Organization organization = mock(Organization.class);
//        BraintreeCustomer customer = mock(BraintreeCustomer.class);
//        Long requestsNumber = 15L;
//
////        when
//        when(organization.getCustomer()).thenReturn(customer);
//        when(customer.getCurrentBillingPeriodStart()).thenReturn(new Date());
//        when(customer.getMaxRequests()).thenReturn(requestsNumber);
//
//        Long result = userService.getMonthlyRequestLimit(organization);
//
////        then
//        assertEquals(requestsNumber, result);
//    }
//
//    @Ignore
//    @Test
//    public void getMonthlyRequestLimitNullCustomerTest() {
////        given
//        reset(userRepository);
//        Organization organization = mock(Organization.class);
//        Long defaultRequestsNumber = 10L;
//
////        when
//        when(organization.getCustomer()).thenReturn(null);
//
//        Long result = userService.getMonthlyRequestLimit(organization);
//
////        then
//        assertEquals(defaultRequestsNumber, result);
//    }
//
//
//    @Ignore
//    @Test
//    public void getMonthlyRequestLimitNullBillingPeriodStartTest() {
////        given
//        reset(userRepository);
//        Organization organization = mock(Organization.class);
//        BraintreeCustomer customer = mock(BraintreeCustomer.class);
//        Long defaultRequestsNumber = 10L;
//
////        when
//        when(organization.getCustomer()).thenReturn(customer);
//        when(customer.getCurrentBillingPeriodStart()).thenReturn(null);
//
//        Long result = userService.getMonthlyRequestLimit(organization);
//
////        then
//        assertEquals(defaultRequestsNumber, result);
//    }
}
