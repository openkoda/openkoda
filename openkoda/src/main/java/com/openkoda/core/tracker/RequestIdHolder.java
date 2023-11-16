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

package com.openkoda.core.tracker;

import com.openkoda.core.helper.ApplicationContextProvider;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static com.openkoda.controller.common.URLConstants.EXTERNAL_SESSION_ID;

/**
 * Helper component to provide a request id of current web request (from {@link WebRequestIdHolder} or in case of cron
 * job the id is taken from {@link MDC} which is internally attached to the executing thread
 */
@Component
public class RequestIdHolder {

    public static String PARAM_CRON_JOB_ID = "jobId";
    private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    public static String generate() {
        return formatter.format(LocalDateTime.now()) + "-" + RandomStringUtils.randomAlphanumeric(8);
    }

    public static String getId() {
        return RequestContextHolder.getRequestAttributes() != null ?
                    ApplicationContextProvider.getContext().getBean(WebRequestIdHolder.class).getWebRequestId()
                    + StringUtils.defaultString((String)RequestContextHolder.getRequestAttributes().getAttribute(EXTERNAL_SESSION_ID, 0))
                : cronJobId();
    }

    public static String cronJobId() {
        return StringUtils.isNotEmpty(MDC.get(RequestIdHolder.PARAM_CRON_JOB_ID)) ? MDC.get(PARAM_CRON_JOB_ID) : "";
    }
}
