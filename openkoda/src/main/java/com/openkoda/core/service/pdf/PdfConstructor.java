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

package com.openkoda.core.service.pdf;

import com.lowagie.text.pdf.BaseFont;
import com.openkoda.core.tracker.LoggingComponentWithRequestId;
import jakarta.inject.Inject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.parser.Parser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.xhtmlrenderer.pdf.ITextRenderer;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

/**
 *
 * <p>Create a pdf file</p>
 *
 * @author Arkadiusz Drysch (adrysch@stratoflow.com)
 *
 */
@Service
public class PdfConstructor implements LoggingComponentWithRequestId {

    @Value("${base.url:http://localhost:8080}")
    private String baseUrl;

    @Value("${mail.from:}")
    private String emailNameFrom;

    @Value("${font.path:/fonts/arialuni.ttf}")
    private String fontPath;

    @Inject
    private TemplateEngine templateEngine;

    private Context getContext() {
        debug("[getContext]");
        return new Context(LocaleContextHolder.getLocale());
    }

    public String prepareContent(String templateName, Map<String, Object> model) {
        debug("[prepareContent] {}", templateName);
        final Context ctx = getContext();
        ctx.setVariable("baseUrl", baseUrl);

        for (Map.Entry<String, Object> entry : model.entrySet()) {
            ctx.setVariable(entry.getKey(), entry.getValue());
        }

        return templateEngine.process(templateName, ctx);
    }

    /**
     * <p>Create a pdf file</p>
     *
     * @return true when successful
     * @throws Exception otherwise.
     */
    public boolean writeDocumentToStream(String templateName, OutputStream os, Map<String, Object> model) throws
            Exception {
        debug("[writeDocumentToStream] {}", templateName);
        try {

            ITextRenderer renderer = new ITextRenderer();
            renderer.getFontResolver().addFont(fontPath, BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED);

            String htmlContent = prepareContent(templateName, model);
            // we need to create the target PDF
            // we'll create one page per input string, but we call layout for the first
            Document document = Jsoup.parse(htmlContent, "", Parser.xmlParser());
            renderer.setDocumentFromString(document.html());
            renderer.layout();
            renderer.createPDF(os, false);

            // complete the PDF
            renderer.finishPDF();

        } finally {
            if (os != null) {
                try {
                    os.close();
                } catch (IOException e) { /*ignore*/ }
            }
        }
        return true;
    }

}
