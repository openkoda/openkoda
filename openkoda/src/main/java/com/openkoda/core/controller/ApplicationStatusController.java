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

package com.openkoda.core.controller;

import com.openkoda.core.tracker.LoggingComponentWithRequestId;
import com.openkoda.repository.user.UserRepository;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;


/**
 * Makes check-up of system components for monitoring.
 * General contract: <br/>
 * * public /ping action <br/>
 * * returns code 200 if okay <br/>
 * * returns 5XX if not okay <br/>
 * * additional information in response optional but recommended <br/>
 * * the additional information must NOT share any sensitive data (the action is public!)
 *
 * @author Arkadiusz Drysch (adrysch@stratoflow.com)
 * 
 */
@RestController
public class ApplicationStatusController implements LoggingComponentWithRequestId {

    @Autowired
    UserRepository userRepository;

    boolean measureTime(String entryName, Map<String, String> log, Supplier f) {
        debug("[measureTime]");
        long start = System.currentTimeMillis();
        boolean result = true;
        try {
            f.get();
            log.put(entryName, "OK");
        } catch (Exception e) {
            log.put(entryName, "ERROR");
            result = false;
        }
        long end = System.currentTimeMillis();
        log.put(entryName + "Time", (end - start) + "");
        return result;
    }


    /**
     * <p>ping.</p>
     *
     * @param response a {@link jakarta.servlet.http.HttpServletResponse} object.
     * @return a {@link java.util.Map} object.
     */
    @GetMapping("/ping")
    public Map<String, String> ping(HttpServletResponse response) {
        debug("[ping]");
        Map<String, String> log = new LinkedHashMap<>();

        boolean dbWorks = measureTime("db", log, userRepository::count);

        boolean isOk = dbWorks;

        if (!isOk) {
            response.setStatus(HttpStatus.SERVICE_UNAVAILABLE.value());
        }

        return log;

    }

}
