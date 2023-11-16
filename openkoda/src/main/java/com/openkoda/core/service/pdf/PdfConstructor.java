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

package com.openkoda.core.service.pdf;

import com.openkoda.core.tracker.LoggingComponentWithRequestId;
import jakarta.inject.Inject;
import org.jsoup.Jsoup;
import org.jsoup.parser.Parser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.xhtmlrenderer.pdf.ITextRenderer;

import java.io.*;
import java.util.Collections;
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
     * <p>Create a pdf file with separate pages for each model provided</p>
     * <p>When models is null or empty, the template will be fed with empty model and one pdf will be generated</p>
     * @return true when successful
     * @throws Exception otherwise.
     */
    public byte[] writeDocumentToByteArray(String templateName, Map<String, Object> ... models) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        writeDocumentToOutputStream(templateName, baos, models);
        return baos.toByteArray();
    }

    /**
     * <p>Create a pdf file with separate pages for each model provided</p>
     * <p>When models is null or empty, the template will be fed with empty model and one pdf will be generated</p>
     * @return true when successful
     * @throws Exception otherwise.
     */
    public InputStream writeDocumentToStream(String templateName, Map<String, Object> ... models) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        writeDocumentToOutputStream(templateName, baos, models);
        return new ByteArrayInputStream(baos.toByteArray());
    }

    /**
     * <p>Create a pdf file with separate pages for each model provided</p>
     * <p>When models is null or empty, the template will be fed with empty model and one pdf will be generated</p>
     * @return true when successful
     * @throws Exception otherwise.
     */
    public boolean writeDocumentToOutputStream(String templateName, OutputStream os, Map<String, Object> ... models) {
        debug("[writeDocumentToStream] {}", templateName);
        try {


            ITextRenderer renderer = new ITextRenderer();
//            renderer.getFontResolver().addFont(fontPath, BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED);
            Map<String, Object> firstModel = models == null || models.length == 0 ? Collections.emptyMap() : models[0];
            String htmlContent = prepareContent(templateName, firstModel);
            // we need to create the target PDF
            // we'll create one page per input string, but we call layout for the first
            String html = Jsoup.parse(htmlContent, "", Parser.xmlParser()).html();

            renderer.setDocumentFromString(html);
            renderer.layout();
            renderer.createPDF(os, false);

            //creating next pages
            for (int i = 1; i < models.length; i++) {
                String nextContent = prepareContent(templateName, models[i]);
                String nextHtml = Jsoup.parse(nextContent, "", Parser.xmlParser()).html();
                renderer.setDocumentFromString(nextHtml);
                renderer.layout();
                renderer.writeNextDocument();
            }
            // complete the PDF
            renderer.finishPDF();

        } catch (Exception e) {
            error(e, "Error generating pdf for template {} and models {} ", templateName, models);
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
