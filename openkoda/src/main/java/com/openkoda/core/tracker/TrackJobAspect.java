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

import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

/**
 * Aspect which picks up every @Scheduled annotated method and performs actions before run and after its completion
 *
 * @author Martyna Litkowska (mlitkowska@stratoflow.com)
 * @since 2019-07-26
 */
@Aspect
@Component
public class TrackJobAspect {

//    Before every scheduled method set job id for its thread
    @Before("@annotation(org.springframework.scheduling.annotation.Scheduled)")
    public void setJobIdForThread() {
        MDC.put(RequestIdHolder.PARAM_CRON_JOB_ID, RequestIdHolder.generate());
    }

//    After every scheduled method clear the thread context
    @After("@annotation(org.springframework.scheduling.annotation.Scheduled)")
    public void clearJobIdForThread() {
        MDC.clear();
    }
}
