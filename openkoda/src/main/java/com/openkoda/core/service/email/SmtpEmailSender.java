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
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeUtility;
import jakarta.servlet.ServletContext;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.Executors;

/**
 * <p>Sending mail via Smtp</p>
 * Configuration in application properties.
 *
 * <p>Activated in spring smtp profile</p>
 *
 * @author Arkadiusz Drysch (adrysch@stratoflow.com)
 *
 */
@Service
public class SmtpEmailSender extends EmailSender {

    @Value("${application.logo:/vendor/swagger-ui/springfox-swagger-ui/favicon-32x32.png}")
    String appLogoPath;

    @Inject
    ServletContext context;

    @Inject
    private JavaMailSender mailSender;

    @Inject
    private MessageSource messageSource;


    /**
     * {@inheritDoc}
     */
    @Override
    public boolean sendEmail(String fullFrom, String fullTo, String subject, String html, String attachmentURL, List<File> attachments) {
        debug("[sendEmail] {} -> {} Subject: {}", fullFrom, fullTo, subject);
        try {
            final MimeMessage mimeMessage = mailSender.createMimeMessage();
            final MimeMessageHelper message = new MimeMessageHelper( mimeMessage , true , "UTF-8" );
            message.setSubject( subject );
            message.setFrom( fullFrom );
            message.setReplyTo( replyTo );
            message.addTo( fullTo );
            message.setText( html , true );

            if ( StringUtils.isNotBlank( attachmentURL ) ) {
                try {
                    Path tmpFile = prepareTempAttachmentFile(attachmentURL);
                    if (tmpFile != null) {
                        message.addAttachment( MimeUtility.encodeText( "attachment" , "UTF-8" , null ) , new FileSystemResource(tmpFile.toFile()) );
                    }
                } catch (UnsupportedEncodingException e) {
                    warn("[sendEmail]", e);
                }
            }

            if (attachments != null) {
                for (File f: attachments) {
                    try {
                        byte[] bytes = IOUtils.toByteArray(f.getContentStream());
                        ByteArrayResource r = new ByteArrayResource(bytes, f.getFilename());
                        message.addAttachment(f.getFilename(), r, f.getContentType());
                    } catch (Exception e) {
                        error(e, "Couldn't attach {} to email", f.getFilename());
                    }
                }
            }

            Executors.defaultThreadFactory().newThread( () -> {
                try {
                    mailSender.send( mimeMessage );
                } catch (MailException e) {
                    error( "[sendEmail] Error sending email to {} : {}", fullTo, e );

                }
                info( "[sendEmail] Mail to {} sent", fullTo );
            } ).start();
        } catch (MessagingException e) {
            error("[sendEmail] Error sending email to {} : {}", fullTo, e);
        }
        return true;
    }



}
