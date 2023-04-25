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

package com.openkoda.core.configuration;

import com.openkoda.core.audit.PropertyChangeInterceptor;
import com.openkoda.core.flow.LoggingComponent;
import com.openkoda.core.security.OrganizationUser;
import com.openkoda.core.tracker.DebugLogsDecoratorWithRequestId;
import com.openkoda.model.common.TimestampedEntity.UID;
import org.springframework.boot.autoconfigure.orm.jpa.HibernatePropertiesCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

/**
 * <p>Configuration class that configures bean that
 * extracts username used to {@link org.springframework.data.annotation.CreatedBy}
 * and {@link org.springframework.data.annotation.LastModifiedBy} annotated fields in entity classes.
 * </p>
 */
@Configuration
@EnableJpaAuditing
public class AuditConfig {

    public static class SpringSecurityAuditorAware implements AuditorAware<UID>, LoggingComponent {

        private static final UID UNKNOWN = new UID();

        /**
         * @return UID extracted from SecurityContextHolder
         */
        public Optional<UID> getCurrentAuditor() {
            debug("[getCurrentAuditor]");
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            if (authentication == null || !authentication.isAuthenticated()) {
                return Optional.of(UNKNOWN);
            }

            Object principal = authentication.getPrincipal();

            if (principal instanceof OrganizationUser) {
                String name = ((OrganizationUser) authentication.getPrincipal()).getUsername();
                Long id = ((OrganizationUser) authentication.getPrincipal()).getUserId();

                return Optional.of(new UID(name, id));
            }

            return Optional.of(UNKNOWN);

        }
    }

    @Bean
    public AuditorAware<UID> auditorProvider() {
        return new SpringSecurityAuditorAware();
    }

    @Bean
    public DebugLogsDecoratorWithRequestId logsProvider() {
        return new DebugLogsDecoratorWithRequestId();
    }

    @Bean
    public HibernatePropertiesCustomizer hibernatePropertiesCustomizer(PropertyChangeInterceptor interceptor) {
        return props -> props.put("hibernate.session_factory.interceptor", interceptor);
    }

}
