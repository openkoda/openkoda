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

import com.openkoda.controller.ComponentProvider;
import com.openkoda.controller.common.PageAttributes;
import com.openkoda.core.flow.PageModelMap;
import com.openkoda.dto.CanonicalObject;
import com.openkoda.model.task.HttpRequestTask;
import jakarta.inject.Inject;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Map;

/**
 * Sends message to URL. Collaborates with Thymeleaf in message and headers generation(via FrontendResource).
 */
@Service
public class GenericWebhookService extends ComponentProvider {

    @Inject
    private TemplateEngine templateEngine;

    public boolean sendToUrlWithCanonical(CanonicalObject object, String url, String jsonContentTemplateName, String jsonHeadersTemplateName) {
        debug("[sendToUrlWithCanonical]");
        PageModelMap model = new PageModelMap();
        model.put(PageAttributes.canonicalObject, object);
        String message = prepareContent(jsonContentTemplateName, model);
        String headers = prepareContent(jsonHeadersTemplateName, model);
        HttpRequestTask httpRequestTask = new HttpRequestTask(url, message, headers);
        repositories.unsecure.httpRequest.save(httpRequestTask);
        return true;
    }

    public String prepareContent(String templateName, Map<String, Object> model) {
        debug("[prepareContent] {}", templateName);
        final Context ctx = new Context(LocaleContextHolder.getLocale());

        for (Map.Entry<String, Object> entry : model.entrySet()) {
            ctx.setVariable(entry.getKey(), entry.getValue());
        }

        return templateEngine.process(templateName, ctx);
    }
}
