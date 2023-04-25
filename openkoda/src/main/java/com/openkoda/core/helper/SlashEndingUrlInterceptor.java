package com.openkoda.core.helper;

import com.openkoda.core.tracker.LoggingComponentWithRequestId;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class SlashEndingUrlInterceptor implements LoggingComponentWithRequestId, HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        debug("[preHandle]");
        String uri = request.getRequestURI();

        if (!uri.equals("/") && uri.endsWith("/")) {
            response.sendRedirect(uri.substring(0, uri.length() - 1));
            return false;
        }
        return true;
    }
}