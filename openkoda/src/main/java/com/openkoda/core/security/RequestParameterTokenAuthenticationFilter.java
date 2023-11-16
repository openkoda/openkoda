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

import com.openkoda.core.configuration.RequestParameterTokenAuthenticationSuccessHandler;
import com.openkoda.model.Token;
import com.openkoda.repository.user.TokenRepository;
import jakarta.inject.Inject;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.stereotype.Service;

/**
 * This class is a filter that allows to authenticate GET request that have "token" request parameter.
 * Use case: Url to a resource that can authenticate a user, eg. link to a download file in an email.
 * General class contact:
 * - checking if the request supports authentication
 * - extracting the token from the request
 */
@Service("requestParameterTokenAuthenticationFilter")
public class RequestParameterTokenAuthenticationFilter extends AbstractTokenAuthenticationFilter {

    @Inject
    private TokenRepository tokenRepository;

    @Autowired
    SecurityContextRepository securityContextRepository;

    public RequestParameterTokenAuthenticationFilter() {
        super( RequestParameterTokenAuthenticationFilter::checkRequestSupport );
        setAuthenticationSuccessHandler(new RequestParameterTokenAuthenticationSuccessHandler(securityContextRepository));
    }

    /**
     * Checks if the request is GET and contains token for authentication
     */
    static boolean checkRequestSupport(HttpServletRequest request) {
        return request.getMethod().equals("GET") && StringUtils.isNotBlank(request.getParameter(TOKEN));
    }

    @Override
    protected String extractTokenFromRequest(HttpServletRequest request) {
        return request.getParameter(TOKEN);
    }

    @Override
    protected void afterAuthentication(HttpServletRequest request, HttpServletResponse response, Token token) {
        tokenRepository.saveAndFlush(token.invalidateIfSingleUse());
    }

    protected void setDetails(HttpServletRequest request, UsernamePasswordAuthenticationToken authRequest) {
        authRequest.setDetails(this.authenticationDetailsSource.buildDetails(request));
    }

    @Autowired
    @Override
    public void setAuthenticationManager(@Lazy AuthenticationManager authenticationManager) {
        super.setAuthenticationManager(authenticationManager);
    }

}
