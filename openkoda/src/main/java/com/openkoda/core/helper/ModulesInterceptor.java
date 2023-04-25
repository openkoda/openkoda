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

package com.openkoda.core.helper;

import com.openkoda.core.tracker.LoggingComponentWithRequestId;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/* TODO: move to correct package */
/**
 * <p>ModulesInterceptor class.</p>
 * <p>Keeps and invokes interceptor code registered by modules
 */
@Component
public class ModulesInterceptor implements ReadableCode, LoggingComponentWithRequestId, HandlerInterceptor {

    public static interface PreHandler {
        Boolean preHandle(HttpServletRequest req, HttpServletResponse rest);
    }
    public static interface PostHandler {
        void postHandle(HttpServletRequest req, HttpServletResponse rest, ModelAndView modelAndView);
    }

    public static interface EmailModelPreHandler {
        void preHandle(Map<String, Object> model);
    }

    private List<PreHandler> preHandlers = new ArrayList<>();
    private List<PostHandler> postHandlers = new ArrayList<>();
    private List<EmailModelPreHandler> emailModelPreHandlers = new ArrayList<>();

    public boolean emailModelPreHandle(Map<String, Object> model) {
        for (EmailModelPreHandler h : emailModelPreHandlers) {
            h.preHandle(model);
        }
        return true;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        boolean result = true;
        for (PreHandler h : preHandlers) {
            result &= h.preHandle(request, response);
        }
        return result;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        //omit post handlers if modelAndView is null - it is a direct response body
        if (modelAndView == null) {return;}
        for (PostHandler h : postHandlers) {
            h.postHandle(request, response, modelAndView);
        }
    }

    public void registerPreHandler(PreHandler modulePreHandler) {
        preHandlers.add(modulePreHandler);
    }

    public void registerPostHandler(PostHandler modulePostHandler) {
        postHandlers.add(modulePostHandler);
    }

    public void registerEmailModelPreHandler(EmailModelPreHandler emailModelPreHandler) {
        emailModelPreHandlers.add(emailModelPreHandler);
    }

}
