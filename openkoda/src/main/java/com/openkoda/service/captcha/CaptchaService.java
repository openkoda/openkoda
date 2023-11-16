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

package com.openkoda.service.captcha;

import com.openkoda.controller.ComponentProvider;
import com.openkoda.core.audit.IpService;
import com.openkoda.core.configuration.ReCaptchaConfiguration;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.RequestContextHolder;

import java.net.URI;
import java.util.regex.Pattern;

import static com.openkoda.controller.common.URLConstants.CAPTCHA_VERIFIED;
import static com.openkoda.controller.common.URLConstants.RECAPTCHA_TOKEN;
import static com.openkoda.service.captcha.ValidationLevel.*;

@Service("captchaService")
/**
 *  Captcha verification service.
 */
public class CaptchaService extends ComponentProvider {

    @Autowired
    ReCaptchaConfiguration configuration;
    @Autowired
    IpService ipService;

    private static Pattern RESPONSE_PATTERN = Pattern.compile("[A-Za-z0-9_-]+");

    private RestTemplate restTemplate;

    @PostConstruct
    private void init() {
        restTemplate = new RestTemplate();
    }

    public boolean handleCaptcha(HttpServletRequest request){
        ValidationLevel validation = configuration.validationLevel;
        String captchaToken = request.getParameter(RECAPTCHA_TOKEN);
        if(validation == none){
            RequestContextHolder.getRequestAttributes().setAttribute(CAPTCHA_VERIFIED, true, 0);
            return true;
        }
        ValidationLevel passedLevel = processResponse(captchaToken, ipService.getCurrentUserIpAddress());
        boolean isVerified = passedLevel.compareTo(validation)>=0;
        RequestContextHolder.getRequestAttributes().setAttribute(CAPTCHA_VERIFIED, isVerified, 0);
        return true;
    }

    private ValidationLevel processResponse(String response, String clientIP) {
        debug("[processResponse] {}", clientIP);
        GoogleResponse googleResponse;
        if(!responseSanityCheck(response)) {
            return none;
        }

        URI verifyUri = URI.create(String.format(
                "https://www.google.com/recaptcha/api/siteverify?secret=%s&response=%s&remoteip=%s",
                configuration.secretKey, response, clientIP));

        try{
            googleResponse = restTemplate.getForObject(verifyUri, GoogleResponse.class);
        } catch (ResourceAccessException e) {
            warn("Google recaptcha verification servers may be down",e);
            return normal;
        } catch (RuntimeException e) {
            warn("Unexpected exception",e);
            return none;
        }

        if(googleResponse.isSuccess()) {
            return strict;
        }
        return none;
    }

    private boolean responseSanityCheck(String response) {
        return StringUtils.hasLength(response) && RESPONSE_PATTERN.matcher(response).matches();
    }

}