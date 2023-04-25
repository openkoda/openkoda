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

package com.openkoda;

import com.openkoda.core.flow.Tuple;
import com.openkoda.core.flow.mbean.DebugLogsDecorator;
import com.openkoda.core.lifecycle.BaseDatabaseInitializer;
import com.openkoda.core.lifecycle.SearchViewCreator;
import com.openkoda.core.security.RunAsService;
import com.openkoda.core.service.LogConfigService;
import com.openkoda.core.service.email.EmailConstructor;
import com.openkoda.core.service.event.ApplicationEventService;
import com.openkoda.integration.service.IntegrationService;
import com.openkoda.model.User;
import com.openkoda.repository.FrontendResourceRepository;
import com.openkoda.repository.event.EventListenerRepository;
import com.openkoda.repository.event.SecureSchedulerRepository;
import com.openkoda.repository.organization.OrganizationRepository;
import com.openkoda.repository.task.EmailRepository;
import com.openkoda.repository.task.HttpRequestTaskRepository;
import com.openkoda.repository.user.*;
import com.openkoda.service.user.TokenService;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Collections;
import java.util.Optional;

import static org.mockito.Mockito.*;

/**
 * @author Martyna Litkowska (mlitkowska@stratoflow.com)
 * @since 2019-01-31
 */

@SpringBootTest( webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ExtendWith(SpringExtension.class)
public abstract class AbstractTest {

    @MockBean
    protected GlobalRoleRepository globalRoleRepository;

    @MockBean
    protected OrganizationRoleRepository organizationRoleRepository;

    @MockBean
    protected UserRepository userRepository;

    @MockBean
    protected RoleRepository roleRepository;

    @MockBean
    protected UserRoleRepository userRoleRepository;

    @MockBean
    protected OrganizationRepository organizationRepository;

    @MockBean
    protected EmailRepository emailRepository;

    @MockBean
    protected FrontendResourceRepository frontendResourceRepository;

    @MockBean
    protected SecureSchedulerRepository schedulerRepository;

    @MockBean
    protected HttpRequestTaskRepository httpRequestTaskRepository;

    @MockBean
    protected EventListenerRepository eventListenerRepository;

    @MockBean
    protected LoginAndPasswordRepository loginAndPasswordRepository;

//    SERVICES

    @MockBean
    protected TokenService tokenService;

    @MockBean
    protected ApplicationEventService applicationEventService;

    @MockBean
    protected LogConfigService logConfigService;

    @MockBean
    protected IntegrationService integrationService;

//    OTHER

    @MockBean
    protected EmailConstructor emailConstructor;

    @MockBean
    protected BaseDatabaseInitializer baseInitialDataLoaderl;

    @MockBean
    protected SearchViewCreator searchViewCreator;

    @MockBean
    protected DebugLogsDecorator debugLogsDecorator;

    @Autowired
    private RunAsService runAsService;

    protected User mockAndAuthenticateUser(Long userId, String email, String role, String globalPrivileges) {
        reset(userRepository);
        User user = mock(User.class);
        when(user.getEmail()).thenReturn("test@openkoda.com");
        when(user.getId()).thenReturn(userId);
        when(user.getName()).thenReturn("Test User");
        when(userRepository.getUserRolesAndPrivileges(anyLong())).thenReturn ( Collections.singletonList(new Tuple(Long.valueOf(1), "TEST", globalPrivileges, null, null)));
        when(userRepository.wasModifiedSince(eq(userId), any())).thenReturn(Optional.of(false));
       //TODO: Will need to provide actual request and response
        runAsService.authRunAsUser(user, false, new MockHttpServletRequest(), new MockHttpServletResponse());
        return user;
    }

}
