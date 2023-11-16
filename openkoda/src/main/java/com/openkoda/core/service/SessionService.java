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

package com.openkoda.core.service;

import com.openkoda.core.tracker.LoggingComponentWithRequestId;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Service
public class SessionService implements LoggingComponentWithRequestId {

    private static SessionService instance;

    public HttpSession getSession(boolean create) {
        debug("[getSession]");
        boolean isSession = RequestContextHolder.getRequestAttributes() != null;
        if (!isSession) { return null; }
        ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        return attr.getRequest().getSession(create);
    }

    public Object getSessionAttribute(String id){
        return getSession(true).getAttribute(id);
    }

    public Object getAttributeIfSessionExists(String id) {
        HttpSession s = getSession(false);
        return s != null ? s.getAttribute(id) : null;
    }
    public boolean setAttributeIfSessionExists(String id, Object value) {
        HttpSession s = getSession(false);
        if (s != null) {
            s.setAttribute(id, value);
        }
        return true;
    }
    public boolean removeAttribute(String id) {
        HttpSession s = getSession(false);
        if (s != null) {
            s.removeAttribute(id);
        }
        return true;
    }

    @PostConstruct void init() {
        instance = this;
    }

    public final static SessionService getInstance() {
        return instance;
    }
}
