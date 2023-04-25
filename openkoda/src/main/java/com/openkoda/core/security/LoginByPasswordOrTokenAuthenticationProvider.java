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

import com.openkoda.controller.common.URLConstants;
import com.openkoda.core.tracker.LoggingComponentWithRequestId;
import com.openkoda.model.authentication.LoggedUser;
import com.openkoda.repository.user.TokenRepository;
import jakarta.inject.Inject;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsChecker;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

/**
 * Authentication provider that supports Login And password (delegated to superclass)
 * and Request Token Authentication
 */
@Service("loginByPasswordOrTokenAuthenticationProvider")
public class LoginByPasswordOrTokenAuthenticationProvider extends DaoAuthenticationProvider
        implements URLConstants, HasSecurityRules, LoggingComponentWithRequestId {

    /** {@inheritDoc} */
    @Inject
    @Override
    public void setUserDetailsService(UserDetailsService userDetailsService) {
        super.setUserDetailsService(userDetailsService);
    }

    @Inject
    private TokenRepository tokenRepository;

    @Override
    public boolean supports(Class<?> authentication) {
        return (super.supports(authentication) || RequestTokenAuthenticationToken.class.isAssignableFrom(authentication));
    }

    public LoginByPasswordOrTokenAuthenticationProvider() {
        UserDetailsChecker checks = getPreAuthenticationChecks();
    }

    @Override
    protected Authentication createSuccessAuthentication(Object principal, Authentication authentication, UserDetails user) {
        return super.createSuccessAuthentication(principal, authentication, user);
    }


    @Override
    protected void additionalAuthenticationChecks(UserDetails userDetails, UsernamePasswordAuthenticationToken authentication) {
        debug("[additionalAuthenticationChecks]");
        if (authentication instanceof PreauthenticatedReloadUserToken) {
            //not supported
            warn("[additionalAuthenticationChecks] PreauthenticatedReloadUserToken not supported");
        } else if (authentication instanceof RequestTokenAuthenticationToken) {
            debug("[additionalAuthenticationChecks]");
            RequestTokenAuthenticationToken requestToken = (RequestTokenAuthenticationToken) authentication;
            //narrow down privileges given to the user if the token requires it
            OrganizationUser ou = (OrganizationUser) userDetails;
            if (requestToken.hasPrivileges() ) {
                ou.retainPrivileges(requestToken.getPrivileges());
            }
            ou.setSingleRequestAuth(requestToken.isSingleRequest());
            ou.setAuthMethod(LoggedUser.AuthenticationMethods.TOKEN);
        } else {
            super.additionalAuthenticationChecks(userDetails, authentication);
        }

    }

}
