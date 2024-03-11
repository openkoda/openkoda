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

package com.openkoda.form;

import org.apache.commons.lang3.StringUtils;
import org.springframework.validation.BindingResult;

import com.openkoda.core.form.AbstractEntityForm;
import com.openkoda.dto.EmailConfigDto;
import com.openkoda.model.EmailConfig;

/**
 * Email config settings (SMTP or other mail service integrations)
 *
 */
public class EmailConfigForm extends AbstractEntityForm<EmailConfigDto, EmailConfig> {

    /**
     * <p>Constructor for EmailConfigForm.</p>
     *
     * @param entity a {@link com.openkoda.model.EmailConfig} dto.
     */
    public EmailConfigForm(EmailConfigDto emailDto, EmailConfig entity) {
        super(emailDto, entity, FrontendMappingDefinitions.emailConfigForm);
    }

    /**
     * <p>Constructor for OrganizationForm.</p>
     */
    public EmailConfigForm() {  
        super(FrontendMappingDefinitions.emailConfigForm);
    }

    /** {@inheritDoc} */
    @Override
    public EmailConfigForm validate(BindingResult br) {
        if(StringUtils.isBlank(dto.getHost()) && StringUtils.isBlank(dto.getMailgunApiKey())) { br.rejectValue("dto.host", "not.empty", defaultErrorMessage); br.rejectValue("dto.mailgunApiKey", "not.empty", defaultErrorMessage); };
        if(StringUtils.isNotBlank(dto.getHost()) ) {
            if(StringUtils.isBlank(dto.getUsername())) { br.rejectValue("dto.username", "not.empty", defaultErrorMessage); };
            if(StringUtils.isBlank(dto.getPassword())) { br.rejectValue("dto.password", "not.empty", defaultErrorMessage); };
            if(StringUtils.isBlank(dto.getFrom())) { br.rejectValue("dto.from", "not.empty", defaultErrorMessage); };
        }

        return this;
    }

    /** {@inheritDoc} */
    @Override
    public EmailConfigForm populateFrom(EmailConfig entity) {
        dto.setHost(entity.getHost());
        dto.setId(entity.getId());
        dto.setPort(entity.getPort());
        dto.setUsername(entity.getUsername());
        dto.setPassword(entity.getPassword());
        dto.setFrom(entity.getFrom());
        dto.setReplyTo(entity.getReplyTo());
        dto.setSmtpAuth(entity.getSmtpAuth());
        dto.setSsl(entity.getSsl());
        dto.setStarttls(entity.getStarttls());
        dto.setMailgunApiKey(entity.getMailgunApiKey());
        return this;
    }

    /** {@inheritDoc} */
    @Override
    protected EmailConfig populateTo(EmailConfig entity) {
        //entity.setName(getSafeValue(entity.getId(), NAME_));
        entity.setHost(getSafeValue(entity.getHost(), EMAIL_HOST));
        entity.setPort(getSafeValue(entity.getPort(), EMAIL_PORT));
        entity.setUsername(getSafeValue(entity.getUsername(), EMAIL_USERNAME));
        entity.setPassword(getSafeValue(entity.getPassword(), EMAIL_PASSWORD));
        entity.setFrom(getSafeValue(entity.getFrom(), EMAIL_FROM));
        entity.setReplyTo(getSafeValue(entity.getReplyTo(), EMAIL_REPLY_TO));
        entity.setSmtpAuth(getSafeValue(entity.getSmtpAuth(), EMAIL_SMTP_AUTH));
        entity.setSsl(getSafeValue(entity.getSsl(), EMAIL_SSL));
        entity.setStarttls(getSafeValue(entity.getStarttls(), EMAIL_STARTTLS));
        entity.setMailgunApiKey(getSafeValue(entity.getMailgunApiKey(), EMAIL_MAILGUN_API_KEY));
        return entity;
    }
}
