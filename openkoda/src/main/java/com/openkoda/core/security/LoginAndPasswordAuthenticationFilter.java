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

import com.openkoda.core.configuration.CustomAuthenticationFailureHandler;
import com.openkoda.core.configuration.CustomAuthenticationSuccessHandler;
import com.openkoda.repository.user.UserRepository;
import jakarta.inject.Inject;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.stereotype.Service;

/**
 * The login is in the LoginAndPassword table, and the username is User.email.
 * These two values can be different, so we need a filter to map them
 */
@Service("loginAndPasswordAuthenticationFilter")
public class LoginAndPasswordAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    @Inject
    private UserRepository userRepository;

    public LoginAndPasswordAuthenticationFilter(
            @Value("${page.after.auth.for.multiple.organizations:/html/organization/all}")
                    String pageAfterAuthForMultipleOrganizations,
            @Value("${page.after.auth.for.one.organization:/html/organization/%s/settings}")
                    String pageAfterAuthForOneOrganization,
            @Value("${page.after.auth.for.global.admin:/html/dashboard}")
                    String pageAfterAuthForGlobalAdmin,
            @Autowired SecurityContextRepository securityContextRepository
    ) {
        setAuthenticationSuccessHandler(
                new CustomAuthenticationSuccessHandler(
                        pageAfterAuthForMultipleOrganizations,
                        pageAfterAuthForOneOrganization,
                        pageAfterAuthForGlobalAdmin,
                        securityContextRepository));
        setAuthenticationFailureHandler(new CustomAuthenticationFailureHandler());
    }

    /**
     * Searches for a username in database
     * If a user is found - returns its email
     * Otherwise returns provided login
     */
    @Override
    protected String obtainUsername(HttpServletRequest request) {
        String login = super.obtainUsername(request);
        String username = userRepository.findUsernameLowercaseByLogin(login);
        return username != null ? username : login;
    }

    @Autowired
    @Override
    public void setAuthenticationManager(@Lazy AuthenticationManager authenticationManager) {
        super.setAuthenticationManager(authenticationManager);
    }
}
