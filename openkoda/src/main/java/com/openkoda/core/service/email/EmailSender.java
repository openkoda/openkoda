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

import com.openkoda.core.tracker.LoggingComponentWithRequestId;
import com.openkoda.model.file.File;
import com.openkoda.model.task.Email;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * <p>Abstract EmailSender class.</p>
 *
 * @author Arkadiusz Drysch (adrysch@stratoflow.com)
 */
public abstract class EmailSender implements LoggingComponentWithRequestId {

    @Value("${mail.from:}")
    protected String mailFrom;

    @Value("${mail.replyTo:}")
    String replyTo;

    /**
     * <p>sendMail.</p>
     *
     * @param email a {@link com.openkoda.model.task.Email} object.
     * @return a {@link com.openkoda.model.task.Email} object.
     */
    public Email sendMail(Email email) {
        debug("[sendMail] {}", email);
        try {
            debug("[sendMail] {}", email);
            email.start();
            sendEmail(email.getFullFrom(mailFrom), email.getFullTo(), email.getSubject(), email.getContent(), email.getAttachmentURL(), email.getFiles());
            email.complete();
        } catch (Exception e) {
            error(e, "[sendMail] {}", email);
            email.fail();
        }
        return email;
    }

    /**
     * <p>sendEmail.</p>
     *
     * @param fullFrom a {@link java.lang.String} object.
     * @param fullTo   a {@link java.lang.String} object.
     * @param subject  a {@link java.lang.String} object.
     * @param html     a {@link java.lang.String} object.
     */
    public abstract boolean sendEmail(String fullFrom, String fullTo, String subject, String html, String attachmentURL, List<File> attachments);


    /**
     * Creating temporary attachment for email
     *
     * @param attachmentURL
     * @return a {@link java.nio.file.Path}  to temporary attachment.
     */
    protected Path prepareTempAttachmentFile(String attachmentURL) {
        debug("[prepareTempAttachmentFile] {}", attachmentURL);
        if (StringUtils.isBlank(attachmentURL)) {
            return null;
        }
        Path tmpFile = null;
        try {
            RestTemplate rTemplate = new RestTemplate();
            ResponseEntity<byte[]> tmp = rTemplate.getForEntity(attachmentURL, byte[].class);
            String fileName = StringUtils.substringAfterLast(tmp.getHeaders().get("Content-Disposition").get(0), "=");
            String extension = "." + StringUtils.substringAfterLast(fileName, ".");
            fileName = StringUtils.substringBeforeLast(fileName, ".");
            tmpFile = Files.createTempFile(fileName, extension);
            Files.write(tmpFile, tmp.getBody());
        } catch (IOException e) {
            error(e, "Error while creating temporary attachment for email from url({}):", attachmentURL);
        }
        return tmpFile;
    }

}
