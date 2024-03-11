package com.openkoda.core.service.email;

import java.util.Properties;

import org.apache.commons.lang3.StringUtils;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Service;

import com.openkoda.model.EmailConfig;
import com.openkoda.repository.EmailConfigRepository;

import jakarta.inject.Inject;
import jakarta.mail.internet.MimeMessage;

/**
 * Wrapper for a default JavaMailSender implementation. This one uses SMTP parameters stored in DB and uses them - if they exist.
 * If some of those parameters are not in DB, then it uses default values privided through properties (spring.mail...)
 * 
 * @author mboronski
 */
@Service
public class EmailConfigJavaMailSender extends JavaMailSenderImpl implements JavaMailSender {

    @Inject private EmailConfigRepository emailConfigRepository;
    
    private EmailConfig emailConfig;
    
    @Override
    public String getHost() {
        return StringUtils.defaultIfBlank(emailConfig != null ? emailConfig.getHost() : null, super.getHost());
    }
    
    @Override
    public String getPassword() {
        return StringUtils.defaultIfBlank(emailConfig != null ? emailConfig.getPassword() : null, super.getPassword());
    }
    
    @Override
    public int getPort() {
        if(emailConfig != null && emailConfig.getPort() != null) {
            return emailConfig.getPort();
        }
        
        return super.getPort();
    }
    
    @Override
    public String getProtocol() {
        if(emailConfig != null && emailConfig.getSsl() != null) {
            return Boolean.TRUE.equals(emailConfig.getSsl()) ? "smtps" : "smtp";
        }
        
        return StringUtils.defaultIfBlank(emailConfig != null ? emailConfig.getProtocol() : null, super.getProtocol());
    }
    
    @Override
    public String getUsername() {
        return StringUtils.defaultIfBlank(emailConfig != null ? emailConfig.getUsername() : null, super.getUsername());
    }
    
    @Override
    public Properties getJavaMailProperties() {
        Properties mailProps = new Properties(super.getJavaMailProperties());
        if(emailConfig != null) {
            if(emailConfig.getSmtpAuth() != null) {
                mailProps.setProperty("spring.mail.properties.mail.smtp.auth", emailConfig.getSmtpAuth().toString());
            }
            
            if(emailConfig.getSsl() != null) {
                mailProps.setProperty("spring.mail.smtp.ssl.enable", emailConfig.getSmtpAuth().toString());
            }
            
            if(emailConfig.getStarttls() != null) {
                mailProps.setProperty("spring.mail.properties.mail.smtp.starttls.enabl", emailConfig.getStarttls().toString());
            }
        }

        return mailProps;
    }
    
    @Override
    protected void doSend(MimeMessage[] mimeMessages, Object[] originalMessages) throws MailException {
        emailConfig = emailConfigRepository.findAll().stream().findFirst().orElse(null);
        super.doSend(mimeMessages, originalMessages);
    }
}