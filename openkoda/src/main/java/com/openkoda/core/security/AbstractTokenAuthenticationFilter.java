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
import com.openkoda.model.Token;
import com.openkoda.repository.user.TokenRepository;
import jakarta.inject.Inject;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.RequestMatcher;
import reactor.util.function.Tuple2;

/**
 * This is base class for Authentication filter using Token entity for authentication
 * Existing concrete implementations are:
 * - RequestParameterTokenAuthenticationFilter for tokens in URL
 * - ApiTokenHeaderAuthenticationFilter for tokens in HTTP Header
 * General class contact:
 * - checking if the token is valid
 * - prepare authentication token for Spring Security
 */
public abstract class AbstractTokenAuthenticationFilter extends AbstractAuthenticationProcessingFilter
        implements URLConstants, LoggingComponentWithRequestId {

    @Inject
    private TokenRepository tokenRepository;

    public AbstractTokenAuthenticationFilter(RequestMatcher requiresAuthenticationRequestMatcher) {
        super(requiresAuthenticationRequestMatcher);
    }

    /**
     * Extracts token string from request
     */
    protected abstract String extractTokenFromRequest(HttpServletRequest request);

    /**
     * This allow the concrete class to make steps before authentication, eg. additional checks
     */
    protected void beforeAuthentication(HttpServletRequest request, HttpServletResponse response, Token token){};

    /**
     * This allow the concrete class to make steps after authentication, eg. token invalidation
     */
    protected void afterAuthentication(HttpServletRequest request, HttpServletResponse response, Token token){};

    protected Authentication prepareAuthentication(Token token) {
        return new RequestTokenAuthenticationToken(
                token.getUser().getId(),
                token.getUser().getEmail(),
                token.getToken(),
                token.getPrivilegesSet(),
                token.isSingleRequest());
    }

    /**
     * Checks if the token is valid
     */
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) {
        debug("[attemptAuthentication] token auth for {}", request.getServletPath());
        if (!requiresAuthentication(request, response)) {
            warn("[attemptAuthentication] passed check but attempted auth {}", request.getRequestURI());
            throw new AuthenticationServiceException("Authentication not supported");
        }
        String requestToken = extractTokenFromRequest(request);
        Tuple2<Token, String> t = tokenRepository.findByBase64UserIdTokenIsValidTrue(requestToken);
        if (t.getT1() == null) {
            warn("[attemptAuthentication] {}", t.getT2());
            throw new AuthenticationServiceException(t.getT2());
        }
        Token token = t.getT1();
        Authentication apiHeaderToken = prepareAuthentication(token);
        beforeAuthentication(request, response, token);
        Authentication result = this.getAuthenticationManager().authenticate(apiHeaderToken);
        afterAuthentication(request, response, token);
        return result;
    }

    @Autowired
    @Override
    public void setAuthenticationManager(@Lazy AuthenticationManager authenticationManager) {
        super.setAuthenticationManager(authenticationManager);
    }
}