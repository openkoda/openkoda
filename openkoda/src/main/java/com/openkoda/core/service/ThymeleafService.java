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

        Set<String> fragments = Collections.singleton(fragment);
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
