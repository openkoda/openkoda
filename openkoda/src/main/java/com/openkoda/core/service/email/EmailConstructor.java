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

package com.openkoda.core.service.email;

import com.openkoda.controller.common.PageAttributes;
import com.openkoda.core.flow.PageModelMap;
import com.openkoda.core.helper.ModulesInterceptor;
import com.openkoda.core.tracker.LoggingComponentWithRequestId;
import com.openkoda.dto.CanonicalObject;
import com.openkoda.model.User;
import com.openkoda.model.file.File;
import com.openkoda.model.task.Email;
import jakarta.inject.Inject;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Map;

/**
 * <p>Preparing an email from template.</p>
 *
 * See also {@link com.openkoda.model.task.Email}
 *
 * @author Arkadiusz Drysch (adrysch@stratoflow.com)
 */
@Service
public class EmailConstructor implements LoggingComponentWithRequestId {

    @Value("${base.url:http://localhost:8080}")
    private String baseUrl;

    @Value("${application.name:Default Application}")
    private String applicationName;

    @Value("${application.description:A low-code framework for SaaS products}")
    private String applicationDescription;

    @Value("${mail.replyTo:}")
    private String mailReplyTo;

    @Inject
    private TemplateEngine templateEngine;

    @Inject
    ModulesInterceptor modulesInterceptor;

    private Context getContext() {
        debug("[getContext]");
        return new Context(LocaleContextHolder.getLocale());
    }

    /**
     * Create and set data into email
     *
     * @param emailTo        a {@link java.lang.String} object.
     * @param nameTo         a {@link java.lang.String} object.
     * @param subject        a {@link java.lang.String} object.
     * @param templateName   a {@link java.lang.String} object.
     * @param delayInSeconds a int.
     * @param model          a {@link java.util.Map} object in template.
     * @return a {@link com.openkoda.model.task.Email} object.
     */
    public Email prepareEmail(String emailTo, String nameTo, String subject, String templateName, int delayInSeconds,
                              Map<String, Object> model, File... attachments) {
        debug("[prepareEmail] {} {} {} {} {}", emailTo, nameTo, subject, templateName, delayInSeconds);
        model.put("applicationName", applicationName);
        String content = prepareContent( templateName, model );
        Email email = new Email();
        email.setEmailTo(emailTo);
        email.setContent(content);
        email.setSubject(subject);
        email.setNameTo(nameTo);
        email.setNameFrom(applicationName);
        email.setStartAfter(LocalDateTime.now().plusSeconds(delayInSeconds));
        if (attachments != null) {
            ArrayList<Long> fileIds = new ArrayList<>();
            for (File f: attachments) {
                fileIds.add(f.getId());
            }
            email.setFilesId(fileIds);
        }
        return email;
    }

    public Email prepareEmailWithTitleFromTemplate(String emailTo, String templateName, CanonicalObject object) {
        debug("[prepareEmailTitleFromTemplate] {}", templateName);
        PageModelMap model = new PageModelMap();
        model.put(PageAttributes.canonicalObject, object);
        return prepareEmailWithTitleFromTemplate(emailTo, null, emailTo, templateName, model);
    }

    public Email prepareEmailWithTitleFromTemplate(User recipient, String templateName) {
        debug("[prepareEmailTitleFromTemplate] {}", templateName);
        PageModelMap model = new PageModelMap();
        model.put(PageAttributes.userEntity, recipient);
        return prepareEmailWithTitleFromTemplate(recipient.getEmail(), null, recipient.getName(), templateName, model);
    }

    public Email prepareEmailWithTitleFromTemplate(User recipient, String templateName, PageModelMap model) {
        debug("[prepareEmailTitleFromTemplate] {}", templateName);
        model.put(PageAttributes.userEntity, recipient);
        return prepareEmailWithTitleFromTemplate(recipient.getEmail(), null, recipient.getName(), templateName, model);
    }

    public Email prepareEmailWithTitleFromTemplate(User recipient, String subject, String templateName, PageModelMap model) {
        debug("[prepareEmailTitleFromTemplate] {}", templateName);
        model.put(PageAttributes.userEntity, recipient);
        return prepareEmailWithTitleFromTemplate(recipient.getEmail(), subject, recipient.getName(), templateName, model);
    }

    public Email prepareEmailWithTitleFromTemplate(String emailTo, String subject, String nameTo, String templateName, PageModelMap model, File... attachments) {
        debug("[prepareEmailTitleFromTemplate] {}", templateName);
        Email email = this.prepareEmail(emailTo, nameTo, "", templateName, 0, model, attachments);
        String title = StringUtils.defaultIfBlank(subject, StringUtils.defaultIfBlank(getTitleFromHTML(email.getContent()), "System message"));
        email.setSubject(title);
        return email;
    }

    private String getTitleFromHTML(String content) {
        debug("[getTitleFromHTML]");
        return StringUtils.substringBetween(content, "<title>", "</title>");
    }

    /**
     * <p>prepareContent.</p>
     *
     * @param templateName a {@link java.lang.String} object.
     * @param model        a {@link java.util.Map} object.
     * @return a {@link java.lang.String} object.
     */
    public String prepareContent(String templateName, Map<String, Object> model) {
        debug("[prepareContent] {}", templateName);
        final Context ctx = getContext();

        ctx.setLocale(Locale.ENGLISH);
        ctx.setVariable("baseUrl", baseUrl);
        ctx.setVariable("mailReplyTo", mailReplyTo);

        modulesInterceptor.emailModelPreHandle(model);

        for (Map.Entry<String, Object> entry : model.entrySet()) {
            ctx.setVariable(entry.getKey(), entry.getValue());
        }

        return templateEngine.process(templateName, ctx);
    }

}
