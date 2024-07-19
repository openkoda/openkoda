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
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.exceptions.TemplateProcessingException;
import org.thymeleaf.templateresolver.StringTemplateResolver;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

import static com.openkoda.controller.common.PageAttributes.errorMessage;

@Service
public class ThymeleafService implements LoggingComponentWithRequestId {

    private TemplateEngine templateEngine;
    private StringTemplateResolver stringTemplateResolver;

    public ThymeleafService(TemplateEngine templateEngine, StringTemplateResolver stringTemplateResolver) {
        this.templateEngine = templateEngine;
        this.stringTemplateResolver = stringTemplateResolver;
//        templateEngine.addTemplateResolver(stringTemplateResolver);
    }

    public String prepareContent(String templateName, Map<String, Object> model) {
        return prepareContent(templateName, null, model);
    }

    public String prepareContent(String templateName, String fragment, Map<String, Object> model) {
        debug("[prepareContent] {}", templateName);
        final Context ctx = new Context(LocaleContextHolder.getLocale());

        for (Map.Entry<String, Object> entry : model.entrySet()) {
            ctx.setVariable(entry.getKey(), entry.getValue());
        }

        Set<String> fragments = fragment != null ? Collections.singleton(fragment) : Collections.emptySet();
        String result = templateEngine.process(templateName, fragments,  ctx);
        return result;
    }

    public String prepareContentForHtml(String html, Map<String, Object> model) {
        debug("[prepareContentForHtml]");
        if(model.containsKey(errorMessage.name)) {
            html = "<span th:text=\"${errorMessage}\"></span>";
        } else if(StringUtils.isBlank(html)) {
            return StringUtils.EMPTY;
        }
        TemplateEngine stringTemplateEngine = new TemplateEngine();
        stringTemplateEngine.addTemplateResolver(stringTemplateResolver);
        final Context ctx = new Context(LocaleContextHolder.getLocale());

        for (Map.Entry<String, Object> entry : model.entrySet()) {
            ctx.setVariable(entry.getKey(), entry.getValue());
        }
        stringTemplateEngine.getTemplateResolvers();
        try {
            return stringTemplateEngine.process(html, ctx);
        } catch (TemplateProcessingException e) {
            return e.getCause().getMessage();
        }
    }


}
