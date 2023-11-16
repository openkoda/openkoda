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

import com.openkoda.model.file.File;
import jakarta.inject.Inject;
import jakarta.servlet.ServletContext;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;
import java.util.List;

/**
 *
 * <p>Sending mail via Mailgun</p>
 * Mailgun configuration in application properties.
 *
 * @author Arkadiusz Drysch (adrysch@stratoflow.com)
 *
 */
@Service
@Profile("mailgun")
@Primary
public class MailgunEmailSender extends EmailSender {

    @Value("${mailgun.apikey:}")
    String mailgunApiKey;

    @Value("${mailgun.apiurl:}")
    String mailgunApiUrl;

    @Value("${application.logo:/vendor/swagger-ui/springfox-swagger-ui/favicon-32x32.png}")
    String appLogoPath;

    @Inject
    ServletContext context;

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean sendEmail(String fullFrom, String fullTo, String subject, String html, String attachmentURL, List<File> attachments) {
        debug("[sendEmail] {} -> {} Subject: {}", fullFrom, fullTo, subject);
        RestTemplate rTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        String authorizationHeader = "Basic " + new String(Base64.getEncoder().encode(("api:" + mailgunApiKey).getBytes(Charset.defaultCharset())));
        headers.set("Authorization", authorizationHeader);
        MultiValueMap<String, Object> mvmap = new LinkedMultiValueMap<>();
        mvmap.add("from", fullFrom);
        mvmap.add("to", fullTo);
        mvmap.add("subject", subject);
        mvmap.add("h:Reply-To", replyTo);
        if(StringUtils.isNotEmpty(appLogoPath)) {
            mvmap.add("inline", new ClassPathResource(appLogoPath));
        }
        mvmap.add("html", html);

        Path tmpFile = prepareTempAttachmentFile(attachmentURL);
        if (tmpFile != null) {
            mvmap.add("attachment", new FileSystemResource(tmpFile.toFile()));
        }

        HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<>(mvmap, headers);
        ResponseEntity<String> response = rTemplate.postForEntity(mailgunApiUrl, request, String.class);

        try {
            if (tmpFile != null) {
                Files.deleteIfExists(tmpFile);
            }
        } catch (IOException e) {
            error(e, "Error while wiping attachment {}", tmpFile);
        }
        return true;
    }

}
