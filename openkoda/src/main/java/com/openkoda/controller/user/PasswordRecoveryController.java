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

import com.openkoda.core.controller.generic.AbstractController;
import com.openkoda.core.security.OrganizationUser;
import com.openkoda.form.PasswordChangeForm;
import com.openkoda.model.User;
import com.openkoda.model.authentication.LoggedUser;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import static com.openkoda.controller.common.URLConstants._PASSWORD;
import static com.openkoda.core.service.FrontendResourceService.frontendResourceTemplateNamePrefix;

@Controller
/**
 * <p>PasswordRecoveryController is for all actions related to... - wait for it - password recovery! Ba-dum-tsss!</p>
 *
 * @author Arkadiusz Drysch (adrysch@stratoflow.com)
 *
 */
@RequestMapping(_PASSWORD)
public class PasswordRecoveryController extends AbstractController {

    @Value("${page.after.password.recovery:}")
    private String passwordRecoveryPageCustomUrl;

    @Value("${page.after.password.change:}")
    private String passwordChangePageCustomUrl;

    @GetMapping(_RECOVERY + _FORM)
    @ResponseBody
    public Object passwordRecoverForm() {
        debug("[passwordRecoverForm]");
        ModelAndView mav = new ModelAndView("password-recovery::password-recovery-form");
        return mav;
    }

    /**
     * <p>passwordRecovery.</p>
     *
     * @param email a {@link java.lang.String} object.
     * @return a {@link java.lang.Object} object.
     */
    @PostMapping(_RECOVERY)
    @ResponseBody
    public Object passwordRecovery(@RequestParam String email) {
        debug("[passwordRecovery] email {}", email);
        User user = repositories.unsecure.user.findByEmailLowercase(email);
        boolean userExists = user != null;
        boolean userHasPasswordAuthentication = userExists && user.getAuthenticationMethods().contains(LoggedUser.AuthenticationMethods.PASSWORD);
        if (userHasPasswordAuthentication) {
            debug("[passwordRecovery] userExists and has password authentication");
            services.user.passwordRecovery(user);
        }
        if (!passwordRecoveryPageCustomUrl.isEmpty()) {
            debug("[passwordRecovery] default Url");
            return new ModelAndView(REDIRECT + passwordRecoveryPageCustomUrl + "/" + userExists);
        }
        ModelAndView mav = new ModelAndView(frontendResourceTemplateNamePrefix + "forgot-password");
        if (userHasPasswordAuthentication) {
            mav.addObject("success", "Email sent.");
        } else if (userExists) { // user exists but has no password
            mav.addObject("error", "User with given email exists but has no password authentication method enabled.");
        } else {
            mav.addObject("error", "User doesn't exist.");
        }
        mav.addObject("email", email);
        return mav;
    }

    @PostMapping(_USER + _RECOVERY)
    @ResponseBody
    public Object passwordRecoveryUserSettings(@RequestParam String email) {
        User user = repositories.unsecure.user.findByEmailLowercase(email);
        if(user != null) {
            services.user.passwordRecovery(user);
            return new ModelAndView("forms::post-alert(messageSource='template.resetPassword.sent', formClass='alert-success')");
        } return new ModelAndView("forms::post-alert(messageSource='template.resetPassword.sent.error', formClass='alert-danger')");
    }

    @GetMapping(_RECOVERY + _VERIFY)
    @ResponseBody
    public Object passwordRecoveryTokenCheck(HttpServletRequest request) {
        debug("[passwordRecoveryTokenCheck]");
        ModelAndView mav = new ModelAndView(
                !passwordChangePageCustomUrl.isEmpty() ?
                        REDIRECT + passwordChangePageCustomUrl : FORWARD + _PASSWORD + _CHANGE
        );
        return mav;
    }

    @GetMapping(_CHANGE)
    @ResponseBody
    public Object passwordChangeForm() {
        debug("[passwordChangeForm]");
        ModelAndView mav = new ModelAndView(frontendResourceTemplateNamePrefix + "password-recovery");
        Object user = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (user instanceof OrganizationUser) {
            mav.addObject("passwordChangeForm", new PasswordChangeForm(((OrganizationUser) user).getUser().getId()));
        }
        return mav;
    }

    @PostMapping(_CHANGE + _SAVE)
    @ResponseBody
    public Object passwordChange(@Valid @ModelAttribute PasswordChangeForm passwordChangeForm) {
        debug("[passwordChange]");
        OrganizationUser organizationUser = (OrganizationUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (organizationUser.getUserId() != passwordChangeForm.getUserId()) {
            warn("[passwordChange] possible hacker attack - blocking password change");
            SecurityContextHolder.clearContext();
            ModelAndView mav =  new ModelAndView(frontendResourceTemplateNamePrefix + "email/password-recovery");
            mav.addObject("passwordChangeForm", null);
            return mav;
        }
        User user = repositories.unsecure.user.findOne(passwordChangeForm.getUserId());
        services.user.changePassword(user, passwordChangeForm.getPassword());
        SecurityContextHolder.clearContext();
        RedirectView redirectView = new RedirectView();
        redirectView.setExposeModelAttributes(false);
        redirectView.setUrl(_LOGIN + "?passwordChanged");
        return redirectView;
    }
}
