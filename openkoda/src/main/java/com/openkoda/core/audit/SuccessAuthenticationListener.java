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

package com.openkoda.core.audit;

import com.openkoda.core.security.OrganizationUser;
import com.openkoda.core.service.event.ApplicationEventService;
import com.openkoda.core.tracker.LoggingComponentWithRequestId;
import com.openkoda.model.User;
import com.openkoda.repository.user.UserRepository;
import jakarta.inject.Inject;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

import static com.openkoda.core.service.event.ApplicationEvent.USER_LOGGED_IN;

/**
 * This class listens for AuthenticationSuccessEvent's.
 * On each success updates Users entity with actual data of login
 */
@Service
public class SuccessAuthenticationListener implements ApplicationListener<AuthenticationSuccessEvent>, LoggingComponentWithRequestId {

    @Inject
    private UserRepository userRepository;

    @Inject
    private ApplicationEventService eventService;

    /**
     * Invoked on successful user authentication
     * Updates last login datetime and emit event.
     */
    @Override
    public void onApplicationEvent(AuthenticationSuccessEvent event) {
        debug("[onApplicationEvent]");
        if (event.getAuthentication().isAuthenticated()) {
            Optional<User> user = getUser(event);
            user.ifPresent(u -> {
                u.setLastLogin(LocalDateTime.now());
                userRepository.save(u);
                eventService.emitEvent(USER_LOGGED_IN, u.getBasicUser());
            });
        }
    }

    /**
     * Extracts logged-in user from authentication event
     */
    private Optional<User> getUser(AuthenticationSuccessEvent event) {
        debug("[getUser]");
        Object principal = event.getAuthentication().getPrincipal();
        if (principal instanceof OrganizationUser) {
            return Optional.of(((OrganizationUser) principal).getUser());
        }
        if (principal instanceof String) {
            return Optional.of(userRepository.findByLogin((String) principal));
        }
        return Optional.empty();
    }
}
