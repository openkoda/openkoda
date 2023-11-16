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

import com.openkoda.core.audit.IpService;
import com.openkoda.core.helper.ApplicationContextProvider;
import com.openkoda.core.security.UserProvider;
import com.openkoda.core.tracker.RequestIdHolder;
import com.openkoda.model.common.Audit;
import com.openkoda.repository.admin.AuditRepository;
import jakarta.annotation.PostConstruct;
import jakarta.inject.Inject;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import java.io.PrintWriter;
import java.io.StringWriter;

@Service
public class AuditService {

    @Inject
    private AuditRepository auditRepository;

    private static AuditService instance;

    @PostConstruct void init() {
        instance = this;
    }


    public static boolean createErrorAuditForException(Throwable exception, String message) {
        Audit a = new Audit();
        String reqId = RequestIdHolder.getId();
        a.setSeverity(Audit.Severity.ERROR);
        a.setUserId(UserProvider.getUserIdOrNotExistingId());
        a.setOperation(Audit.AuditOperation.BROWSE);
        a.setRequestId(reqId);
        a.setIpAddress(getIpService().getCurrentUserIpAddress());
        a.setChange(message);
        if (exception != null) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            exception.printStackTrace(pw);
            a.setContent(sw.toString());
        }
        if (instance != null) {
            instance.auditRepository.saveAudit(a);
        }
        return true;
    }

    public static boolean createSimpleInfoAudit(String message){
        return createSimpleInfoAudit(message, null);
    }

    public static boolean createSimpleInfoAudit(String message, String content){
        Audit a = new Audit();
        a.setSeverity(Audit.Severity.INFO);
        a.setChange(message);
        a.setContent(content);
        a.setRequestId("");//otherwise indexString update doesn't work for this record
        if (instance != null) {
            instance.auditRepository.saveAudit(a);
        }
        return true;
    }
    private static IpService getIpService() {
        return getContext().getBean(IpService.class);
    }

    private static ApplicationContext getContext() {
        return ApplicationContextProvider.getContext();
    }


}
