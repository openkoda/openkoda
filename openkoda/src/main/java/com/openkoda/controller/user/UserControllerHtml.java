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

package com.openkoda.controller.user;

import com.openkoda.form.EditUserForm;
import com.openkoda.repository.specifications.UserSpecifications;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import static com.openkoda.controller.common.URLConstants._HTML_USER;
import static com.openkoda.core.security.HasSecurityRules.*;


@Controller
/**
 * <p>UserControllerHtml class.</p>
 * <p>Intended to be controller for server-side generated html actions, whereas AbstractUserController does
 * the actual logic.</p>
 * <p>General contract is: resolve HTTP bindings, delegate work to AbstractUserController and provide
 * ModelAndView</p>
 *
 * @author Arkadiusz Drysch (adrysch@stratoflow.com)
 *
 */
@RequestMapping(_HTML_USER)
public class UserControllerHtml extends AbstractUserController {

    @Value("${page.after.auth:/html/organization/all}")
    private String pageAfterAuth;

    @PreAuthorize(CHECK_CAN_READ_USER_DATA)
    @GetMapping(_ALL)
    public Object getAll(
            @Qualifier("user") Pageable userPageable,
            @RequestParam(required = false, defaultValue = "", name = "user_search") String search,
            HttpServletRequest request) {
        debug("[getAll] search {}", search);
        return findUsers(search, UserSpecifications.searchSpecification(null), userPageable)
            .mav(USER + "-" + ALL);
    }

    @PreAuthorize(CHECK_CAN_READ_USER_SETTINGS)
    @GetMapping(_ID + _PROFILE)
    public Object profile(@PathVariable(ID) Long userId,
                          @Qualifier("module") Pageable modulePageable,
                          @RequestParam(required = false, defaultValue = "", name = "module_search") String search) {
        debug("[profile] userId {} search {}", userId, search);
        return getUsersProfile(userId)
                .mav(USER + '-' + PROFILE);
    }

    @PreAuthorize(CHECK_CAN_READ_USER_SETTINGS)
    @GetMapping(_ID_SETTINGS)
    public Object settings(@PathVariable(ID) Long userId) {
        debug("[settings] userId {}", userId);
        return getUsersProfile(userId)
                .mav("user-settings");
    }

    @PreAuthorize(CHECK_CAN_MANAGE_USER_SETTINGS)
    @PostMapping(_ID_SETTINGS)
    public Object update(@PathVariable(ID) Long userId, @Valid EditUserForm userFormData, BindingResult br) {
        debug("[update] userId {}", userId);
        return saveUser(userId, userFormData, br)
            .mav(ENTITY + '-' + FORMS + "::user-settings-form-success",
                    ENTITY + '-' + FORMS + "::user-settings-form-error");
    }

    @PreAuthorize(CHECK_CAN_IMPERSONATE)
    @GetMapping(_ID + _SPOOF)
    public Object spoof(@PathVariable(ID) Long userId, HttpSession session, HttpServletRequest request,
                        HttpServletResponse response) {
        debug("[spoof] userId {}", userId);
        return spoofUser(userId, session, request, response)
                .mav("generic-forms::go-to(url='" + pageAfterAuth + "')");
    }

    @PreAuthorize(CHECK_IS_SPOOFED)
    @GetMapping(_SPOOF + _EXIT)
    public Object exitSpoof(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
        debug("[exitSpoof]");
        return stopSpoofingUser(session, request, response)
                .mav("generic-forms::go-to(url='" + pageAfterAuth + "')");
    }

    @PreAuthorize(CHECK_IS_THIS_USERID)
    @PostMapping(_ID_SETTINGS + _APIKEY)
    public Object resetApiKey(@PathVariable(ID) Long userId) {
        debug("[resetApiKey]");
        return doResetApiKey()
                .mav("snippets::apiKey");
    }
}
