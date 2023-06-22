package com.openkoda.core.configuration;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.security.web.context.SecurityContextRepository;

import java.io.IOException;

import static com.openkoda.controller.common.URLConstants.TOKEN;

/**
 * On successful authentication, do a redirect to the url, but without the token parameter.
 */
public class RequestParameterTokenAuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    private SecurityContextRepository securityContextRepository;
    public RequestParameterTokenAuthenticationSuccessHandler(SecurityContextRepository securityContextRepository) {
        this.securityContextRepository = securityContextRepository;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        this.securityContextRepository.saveContext(SecurityContextHolder.getContext(), request, response);
        String redirectUrl = getUrlWithoutTokenParameter(request);
        response.sendRedirect(redirectUrl);
    }

    private String getUrlWithoutTokenParameter(HttpServletRequest request) {
        String queryReplacement = request.getQueryString().replaceAll(TOKEN + "=[0-9a-zA-Z_\\-]+[=]*", "");
        return request.getServletPath()
                + (StringUtils.isEmpty(queryReplacement) ? "" : "?" + queryReplacement);
    }
}
