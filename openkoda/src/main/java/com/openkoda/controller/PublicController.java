/*
MIT License

Copyright (c) 2016-2023, Openkoda CDX Sp. z o.o. Sp. K. <openkoda.com>

Permission is hereby granted, free of charge, to any person obtaining a copy of this software
 and associated documentation files (the "Software"), to deal in the Software without restriction, 
including without limitation the rights to use, copy, modify, merge, publish, distribute, 
sublicense, and/or sell copies of the Software, and to permit persons to whom the Software 
is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice 
shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, 
EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES 
OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE 
AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS 
OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES 
OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, 
TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION 
WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*/

package com.openkoda.controller;

import com.openkoda.core.controller.generic.AbstractController;
import com.openkoda.core.security.HasSecurityRules;
import com.openkoda.core.security.UserProvider;
import com.openkoda.core.service.ValidationService;
import com.openkoda.form.RegisterUserForm;
import com.openkoda.model.Privilege;
import com.openkoda.model.Token;
import com.openkoda.model.User;
import com.openkoda.model.authentication.LoggedUser;
import jakarta.inject.Inject;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;
import reactor.util.function.Tuple2;

import java.util.Set;

import static com.openkoda.core.service.FrontendResourceService.frontendResourceTemplateNamePrefix;
import static com.openkoda.core.service.event.ApplicationEvent.USER_VERIFIED;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@Controller
/**
 * <p>Controller that is a general place for putting all public actions, ie. actions that don't require
 * authentication.</p>
 * <p>There are a few more specialized controllers for such actions (eg. PasswordRecoveryController or FrontendResourceController,
 * but this a a go-to place for all the other actions.
 * </p>
 *
 * @author Arkadiusz Drysch (adrysch@stratoflow.com)
 * 
 */
public class PublicController extends AbstractController implements HasSecurityRules {

    @Value("${page.after.register:/logout}")
    private String pageAfterRegister;

    @Value("${redirect.login.page.url:}")
    private String loginPageCustomUrl;

    @Inject
    ValidationService validationService;

    @RequestMapping(value = {_REGISTER + _ATTEMPT, "/{languagePrefix:" + LANGUAGEPREFIX + "$}" + _REGISTER + _ATTEMPT}, method = POST, consumes = "application/json", headers = "content-type=application/x-www-form-urlencoded")
    @ResponseBody
    public RedirectView registerAttempt(@PathVariable(value = "languagePrefix", required = false) String languagePrefix, String email, RedirectAttributes redirectAttributes) {
        debug("[registerAttempt]");
        RedirectView rv = new RedirectView((languagePrefix == null ? "" : "/" + languagePrefix) + _REGISTER);
        rv.setExposeModelAttributes(false);
        redirectAttributes.addFlashAttribute("registerAttemptEmail", email);
        return rv;
    }

    @RequestMapping(value = {_REGISTER, "/{languagePrefix:" + LANGUAGEPREFIX + "$}" + _REGISTER}, method = POST, consumes = "application/json", headers = "content-type=application/x-www-form-urlencoded")
    @ResponseBody
    public Object registerUser(@PathVariable(value = "languagePrefix", required = false) String languagePrefix, @ModelAttribute("registerForm") @Valid RegisterUserForm registerUserForm, HttpServletRequest request) {
        debug("[registerUser]");
        String languagePrefix_ = (languagePrefix == null ? "" : languagePrefix + "/");
        languagePrefix = (languagePrefix == null ? "" : languagePrefix);
        if(!validationService.isCaptchaVerified()){
            ModelAndView mav = new ModelAndView(frontendResourceTemplateNamePrefix + languagePrefix_ + "register");
            mav.addObject(error.name, "ReCaptcha response unverified");
            return mav;
        }
        Tuple2<User, Boolean> userOrReturnExisting = services.user.registerUserOrReturnExisting(registerUserForm, request.getCookies(), languagePrefix, true);
        if (userOrReturnExisting.getT2()) {
            User existingUser = userOrReturnExisting.getT1();
            Set<LoggedUser.AuthenticationMethods> existingMethods = existingUser.getAuthenticationMethods();
            debug("[registerUser] User with given login {} already exists with auth methods: {}", registerUserForm.getLogin(), existingMethods);
            ModelAndView mav = new ModelAndView(frontendResourceTemplateNamePrefix + languagePrefix_ + "register");
            if (existingMethods.contains(LoggedUser.AuthenticationMethods.PASSWORD)) {
                mav.addObject(error.name, "User with given login already exists.");
            } else {
                mav.addObject(error.name, "User with given email exists with different authentication methods.");
            }
            return mav;
        }
        ModelAndView mav = new ModelAndView(frontendResourceTemplateNamePrefix + languagePrefix_ + "thank-you");
        mav.addObject(userEntity.name, repositories.unsecure.user.findByLogin(registerUserForm.getLogin()));
        return mav;
    }

    @GetMapping(_REGISTER + _VERIFY)
    @ResponseBody
    public Object verifyUser(@RequestParam(VERIFY_TOKEN) String base64UserIdToken) {
        debug("[verifyUser]");

        Token token = services.token.verifyAndInvalidateToken(base64UserIdToken);

        if(token != null && token.getPrivilegesSet().contains(Privilege.canVerifyAccount)) {
            debug("[verifyUser] Trying to verify user {} with token {}", token.getUserId(), token.getId());
            User user = token.getUser();
            user.setEnabled(true);
            user.getLoginAndPassword().setEnabled(true);
            repositories.unsecure.user.saveAndFlush(user);
            services.applicationEvent.emitEvent(USER_VERIFIED, user.getBasicUser());
            return new ModelAndView(frontendResourceTemplateNamePrefix + "account-verification-success");
        }

        if (token != null) {
            warn("[verifyUser] failed verification for {} with token {}", token.getUserId(), token.getId());
        } else {
            warn("[verifyUser] failed verification");
        }

        return new ModelAndView(frontendResourceTemplateNamePrefix + "account-verification-fail");
    }

    @PostMapping(_RESEND + _VERIFICATION)
    @ResponseBody
    public Object resendVerificationLink(@RequestParam String email) {
        debug("[resendVerificationLink] email: {}", email);
        ModelAndView mav = new ModelAndView(frontendResourceTemplateNamePrefix + "resend-verification");
        mav.addObject("verification", services.user.resendAccountVerificationEmail(email));
        return mav;
    }


    @GetMapping(_LOGIN + _FORM)
    @ResponseBody
    public Object getLoginForm(@RequestParam(required = false) String error, @RequestParam(required = false) String logout) {
        debug("[getLoginForm]");
        ModelAndView mav = new ModelAndView(frontendResourceTemplateNamePrefix + "login::login-form");
        if (error != null) {
            mav.addObject("param.error", error);
        }
        if (logout != null) {
            mav.addObject("param.logout", logout);
        }
        return mav;
    }

    @GetMapping(_HOME)
    public Object getHome(){
        debug("[getHome]");
        return new ModelAndView(REDIRECT + "/");
    }

    @GetMapping("/has-file-access")
    public void hasAccess(
            @RequestParam(ID) Long fileId,
            HttpServletRequest request,
            HttpServletResponse response) {
        for (Cookie c : request.getCookies()) {
            System.out.println(c.getName() + " " + c.getValue());
        }
        if (!UserProvider.isAuthenticated() || repositories.secure.file.findOne(fileId) == null ) {
            response.setStatus(403);
        } else {
            response.setStatus(200);
        }
    }

}
