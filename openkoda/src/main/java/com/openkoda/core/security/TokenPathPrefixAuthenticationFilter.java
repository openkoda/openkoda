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

import com.openkoda.controller.common.URLConstants;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

/**
 * This class is a filter that allows to authenticate request that have "api-token" HTTP Header.
 * Use case: Api call.
 * General class contact:
 * - checking if the request can authenticate
 * - extract token string from the request
 */
@Service("tokenPathPrefixAuthenticationFilter")
public class TokenPathPrefixAuthenticationFilter extends AbstractTokenAuthenticationFilter implements URLConstants {

    public TokenPathPrefixAuthenticationFilter() {
        super( TokenPathPrefixAuthenticationFilter::checkRequestSupport );

        //on successful authentication, forward the request to the original target
        setAuthenticationSuccessHandler(
                (req, resp, auth) -> { req.getRequestDispatcher(req.getRequestURI().substring(req.getRequestURI().indexOf("/", 1))).forward(req, resp); });
    }

    /**
     * Checks is the request has api-token header
     */
    static boolean checkRequestSupport(HttpServletRequest request) {
        return request.getMethod().equals("GET") && StringUtils.startsWith(request.getRequestURI(), __T_);
    }

    @Override
    protected String extractTokenFromRequest(HttpServletRequest request) {
        return request.getRequestURI().substring(__T_.length(), request.getRequestURI().indexOf("/", 1));
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