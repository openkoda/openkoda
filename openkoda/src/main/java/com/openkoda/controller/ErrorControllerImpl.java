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

package com.openkoda.controller;

import com.openkoda.controller.common.URLConstants;
import com.openkoda.core.exception.ErrorLoggingExceptionResolver;
import com.openkoda.core.flow.PageModelMap;
import com.openkoda.core.helper.ReadableCode;
import com.openkoda.core.tracker.LoggingComponentWithRequestId;
import com.openkoda.core.tracker.RequestIdHolder;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.time.LocalDateTime;
import java.util.*;

import static com.openkoda.controller.common.PageAttributes.*;

@Controller
public class ErrorControllerImpl implements ErrorController, LoggingComponentWithRequestId, URLConstants, ReadableCode {

    private static final int lastErrorsSize = 30;
    private static final List lastErrors = new ArrayList(lastErrorsSize + 5);
    private static final Set<String> unavailablePages = new HashSet<>();
    private static final HttpStatus pageNotFoundStatus = HttpStatus.NOT_FOUND;

    @Value("${user.agent.excluded.from.error.log:}")
    String userAgentExcludedFromErrorLog;

    @RequestMapping("/error")
    @ResponseBody
    public Object handleError(
            @RequestParam(name = "requestId", required = false) String reqId,
            @RequestParam(name = "status", required = false, defaultValue = "NOT_FOUND") HttpStatus responseStatus,
            HttpServletRequest request) {
        debug("[handleError] ReqId: {}", reqId);
        PageModelMap model = new PageModelMap();
        Optional<String> requestUri = getErrorRequestUri(request);
        Optional<String> requestErrorMessage = getErrorMessage(request);
        model.put(errorMessage, requestErrorMessage.orElse(null));
        model.put(errorHttpStatus, responseStatus);

        if (reqId == null) {
            reqId = RequestIdHolder.getId();
            String userAgent = request.getHeader("User-Agent");
            boolean isExcludedUserAgent = ErrorLoggingExceptionResolver.isExcludedUserAgent(userAgent);
            if (not(isExcludedUserAgent)) {
                if(pageNotFoundStatus.equals(responseStatus) && requestUri.isPresent()) {
                    if(unavailablePages.contains(requestUri.get())) {
                        model.put(requestId, reqId);
                        return new ModelAndView("frontend-resource/error", model, responseStatus);
                    }
                    unavailablePages.add(requestUri.get());
                }
                lastErrors.add(String.format("%s %s %s", responseStatus, requestUri, LocalDateTime.now()));
                if (lastErrors.size() >= lastErrorsSize) {
                    warn("Last ca. {} errors outside the controller flow: {}", lastErrorsSize, lastErrors.toString());
                    lastErrors.clear();
                }
            }
        }
        if (getErrorRequestUri(request).map( a -> a.startsWith(_API) ).orElse(false) ) {
            return ResponseEntity.status(responseStatus).body(model);
        }
        model.put(requestId, reqId);
        return new ModelAndView("frontend-resource/error", model, responseStatus);
    }

    private Optional<String> getErrorRequestUri(HttpServletRequest request) {
        return Optional.ofNullable((String) request.getAttribute("jakarta.servlet.error.request_uri"));
    }

    private Optional<String> getErrorMessage(HttpServletRequest request) {
        return Optional.ofNullable((String) request.getAttribute("jakarta.servlet.error.message"));
    }

    private Integer getErrorStatusCode(HttpServletRequest request) {
        return (Integer) request.getAttribute("jakarta.servlet.error.status_code");
    }
}
