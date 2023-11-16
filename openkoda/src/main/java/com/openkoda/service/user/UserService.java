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

package com.openkoda.service.user;

import com.openkoda.controller.ComponentProvider;
import com.openkoda.controller.common.PageAttributes;
import com.openkoda.core.flow.PageModelMap;
import com.openkoda.core.helper.Messages;
import com.openkoda.core.security.HasSecurityRules;
import com.openkoda.core.security.OrganizationUser;
import com.openkoda.core.security.UserProvider;
import com.openkoda.core.service.email.StandardEmailTemplates;
import com.openkoda.dto.RegisteredUserDto;
import com.openkoda.form.InviteUserForm;
import com.openkoda.form.RegisterUserForm;
import com.openkoda.model.*;
import com.openkoda.model.authentication.LoginAndPassword;
import com.openkoda.model.task.Email;
import com.openkoda.service.organization.OrganizationCreationStrategy;
import com.openkoda.service.organization.OrganizationService;
import jakarta.inject.Inject;
import jakarta.servlet.http.Cookie;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuple3;
import reactor.util.function.Tuples;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static com.openkoda.controller.common.URLConstants.*;
import static com.openkoda.core.lifecycle.BaseDatabaseInitializer.ROLE_USER;
import static com.openkoda.core.service.event.ApplicationEvent.*;

@Service
/**
 * <p>UserService class.</p>
 *
 * @author Arkadiusz Drysch (adrysch@stratoflow.com)
 * 
 */
public class UserService extends ComponentProvider implements HasSecurityRules {
    @Value("${organization.creation.strategy:ASSIGN}")
    OrganizationCreationStrategy creationStrategy;

    @Value("${organization.creation.strategy.assign.id:121}")
    Long defaultOrgId;

    @Value("${organization.creation.strategy.no.organization.users:false}")
    boolean canCreateUserWithoutOrg;

    @Value("${role.global.user:ROLE_USER}")
    private String roleGlobalUser;

    @Value("${role.org.admin:ROLE_ORG_ADMIN}")
    private String roleOrgAdmin;

    @Value("${base.url:http://localhost:8080}")
    private String baseUrl;

    @Value("${application.name:Default Application}")
    private String applicationName;

    @Value("${user.initial.password.length:15}")
    private int initialPasswordLength;

    @Inject
    private OrganizationService organizationService;

    private static PasswordEncoder passwordEncoder;

    @Inject
    private Messages messages;

    public User createUser(String firstName,
                           String lastName,
                           String email,
                           boolean userEnabled,
                           String[] globalRoles,
                           Tuple2<String /* roleName */, Long /* orgId */>[] orgRoles) {
        debug("[createUser] with name {} {} and email {}", firstName, lastName, email);
        User u = new User(firstName, lastName, email.toLowerCase());
        u.setEnabled(userEnabled);
        u = repositories.unsecure.user.save(u);
        addGlobalRoleToUser(u, globalRoles);
        addOrgRoleToUser(u, orgRoles);
        services.applicationEvent.emitEvent(USER_CREATED, u.getBasicUser());
        debug("[createUser] event USER_CREATED emitted");
        return repositories.unsecure.user.findOne(u.getId());
    }

    public User createUser(User user) {
        String[] globalRoles = {roleGlobalUser};
        return createUser(user.getFirstName(), user.getLastName(), user.getEmail(), globalRoles);
    }

    public User createUser(String firstName,
                           String lastName,
                           String email,
                           Tuple2<String /* roleName */, Long /* orgId */>... orgRoles) {
        String[] globalRoles = {roleGlobalUser};
        return createUser(firstName, lastName, email, true, globalRoles, orgRoles);
    }

    public User createUser(String firstName,
                           String lastName,
                           String email,
                           String... globalRoles) {
        return createUser(firstName, lastName, email, true, globalRoles, null);
    }

    public List<UserRole> addOrgRoleToUser(User u, Tuple2<String, Long>... orgRoles) {
        debug("[addOrgRoleToUser] userId: {}", u.getId());
        if (orgRoles == null) {
            return Collections.emptyList();
        }
        List<UserRole> result = new ArrayList<>(orgRoles.length);
        for (Tuple2<String, Long> t : orgRoles) {
            Role r = repositories.unsecure.role.findByName(t.getT1());
            if (r != null) {
                UserRole ur = new UserRole(null, u.getId(), r.getId(), t.getT2());
                result.add(repositories.unsecure.userRole.save(ur));
                services.applicationEvent.emitEvent(USER_ROLE_CREATED, ur.getUserRoleDto());
            }
        }
        return result;
    }

    public List<UserRole> addGlobalRoleToUser(User u, String... globalRoles) {
        debug("[addGlobalRoleToUser] userId: {}", u.getId());
        if (globalRoles == null) {
            return Collections.emptyList();
        }
        return addOrgRoleToUser(u, Stream.of(globalRoles).map(a -> Tuples.of(a, null)).toArray(Tuple2[]::new));
    }


    public UserRole changeUserOrganizationRole(User u, Long organizationId, String roleName) {
        debug("[changeUserOrganizationRole] userId: {}", u.getId());
        return changeUserRole(u, Tuples.of(roleName, organizationId));
    }

    @PreAuthorize(CHECK_CAN_ACCESS_GLOBAL_SETTINGS)
    public UserRole changeUserGlobalRole(User u, String roleName) {
        debug("[changeUserGlobalRole] userId: {}", u.getId());
        return changeUserRole(u, Tuples.of(roleName, null));
    }

    private UserRole changeUserRole(User u, Tuple2<String, Long> role){
        debug("[changeUserOrganizationRole] userId: {}", u.getId());
        String type = role.getT2() == null ? "GLOBAL" : "ORG";
        Optional<UserRole> oldRole = u.getRoles().stream()
                .filter(r -> r.getRole().getType().equals(type) && (r.getOrganizationId() == null || r.getOrganizationId().equals(role.getT2())))
                .findAny();
        Role r = repositories.unsecure.role.findByName(role.getT1());
        if (r == null) {
            return oldRole.isPresent() ? oldRole.get() : null;
        }
        if (oldRole.isPresent() && oldRole.get().getRole().getName().equals(role.getT1())) {
            return oldRole.get();
        }
        if (oldRole.isPresent()) {
            repositories.unsecure.userRole.deleteUserRole(oldRole.get().getId());
        }
        UserRole ur = new UserRole(null, u.getId(), r.getId(), role.getT2());
        return repositories.unsecure.userRole.save(ur);
    }

    @PreAuthorize(CHECK_CAN_INVITE_USER_TO_ORG)
    public Tuple3<Email, User, InviteUserForm> inviteNewOrExistingUser(InviteUserForm userForm, User user, Organization
            organization) {
        debug("[inviteNewOrExistingUser]");

        if (organization == null) {
            RuntimeException e = new RuntimeException("Organization must not be null");
            error("Organization must not be null", e);
        }

        Long orgId = organization.getId();
        String template;
        String subject;
        PageModelMap model = new PageModelMap();

        if (user == null) {
            user = createUser(userForm.dto.firstName, userForm.dto.lastName, userForm.dto.email, Tuples.of(userForm.dto.roleName, orgId));
            user.setLoginAndPassword(user.getEmail(), RandomStringUtils.randomAlphanumeric(initialPasswordLength), false);
            repositories.unsecure.loginAndPassword.save(user.getLoginAndPassword());
            user = repositories.unsecure.user.save(user);
            template = StandardEmailTemplates.INVITE_NEW;
            subject = "Invitation to join new organization in " + applicationName;
            model.put(passwordRecoveryLink, getPasswordRecoveryLink(user));
            debug("[inviteNewOrExistingUser] user {} created and invited", user.getId());
        }
        else {
            addOrgRoleToUser(user, Tuples.of(userForm.dto.roleName, orgId));
            template = StandardEmailTemplates.INVITE_EXISTING;
            subject = "Invitation to join your organization in " + applicationName;
            debug("[inviteNewOrExistingUser] user {} invited", user.getId());
        }

        model.put(userEntity, user);
        if (getLoggedOrganizationUser().isPresent()) {
            model.put("invitedBy", getLoggedOrganizationUser().get().getUser().getEmail());
        }
        model.put(organizationEntity, repositories.unsecure.organization.findOne(orgId));


        Email email = services.emailConstructor.prepareEmail(
                user.getEmail(), user.getName().isEmpty() ? user.getEmail() : user.getName(), subject, template, 5, model);

        email = repositories.unsecure.email.save(email);

        return Tuples.of(email, user, new InviteUserForm());
    }

    public User getCurrentUser() {
        debug("[getCurrentUser]");
        return UserProvider.getFromContext().map(OrganizationUser::getUser).orElse(null);
    }

    /**
     * <p>getCurrentOrganizationUser.</p>
     *
     * @return a {@link java.util.Optional} object.
     */
    public Optional<OrganizationUser> getCurrentOrganizationUser() {
        debug("[getCurrentOrganizationUser]");
        return UserProvider.getFromContext();
    }

    public User registerUserOrReturnExisting(RegisterUserForm registerUserForm) {
        return registerUserOrReturnExisting(registerUserForm, null, "", true).getT1();
    }

    public User registerUserOrReturnExisting(RegisterUserForm registerUserForm, boolean asSystemUser) {
        return registerUserOrReturnExisting(registerUserForm, null, "", asSystemUser).getT1();
    }

    public Tuple2<User, Boolean> registerUserOrReturnExisting(RegisterUserForm registerUserForm, Cookie[] cookies, String languagePrefix, boolean asSystemUser) {
        debug("[registerUserOrReturnExisting]");
        try {
            User existingUser = repositories.unsecure.user.findByEmailLowercase(registerUserForm.getLogin());
            boolean userAlreadyExists = existingUser != null;
            if (!userAlreadyExists) {

                List<String> globalRoles = new ArrayList<>();
                //user with this email does not exist so create a brand new one with new organization
                if (asSystemUser) {
                    UserProvider.setCronJobAuthentication();
                    globalRoles = Collections.singletonList(roleGlobalUser);
                }
                Organization organization = createOrganizationOrAssignToDefault(registerUserForm);
                User user = registerUserWithStrategy(registerUserForm, cookies, organization, globalRoles.toArray(String[]::new));
                repositories.unsecure.loginAndPassword.save(user.getLoginAndPassword());
                existingUser = repositories.unsecure.user.save(user);
                if (asSystemUser) {
                    sendAccountVerificationEmail(existingUser, languagePrefix);
                }
                debug("[registerUserOrReturnExisting] new user with id {} registered", user.getId());
            }

            return Tuples.of(existingUser, userAlreadyExists);

        } finally {
            if (asSystemUser) {
                UserProvider.clearAuthentication();
            }
        }
    }

    public User registerUserOrReturnExisting(String firstName, Organization organization, String userLogin, String email, String websiteUrl, String emailTemplateName, String emailTitle, String orgRole) {
        debug("[registerUserOrReturnExisting]");
        try {
            PageModelMap model = new PageModelMap();

            User user = repositories.unsecure.user.findByEmailLowercase(email);

            if (user == null) {
                //user with this email does not exist so create a brand new one with new organization
                UserProvider.setCronJobAuthentication();

                user = createUser(firstName, null, userLogin, true, new String[]{ROLE_USER}, new Tuple2[]{Tuples.of(orgRole, organization.getId())});
                user.setLoginAndPassword(user.getEmail(), RandomStringUtils.randomAlphanumeric(initialPasswordLength), false);
                user = repositories.unsecure.user.save(user);
                debug("[registerUserOrReturnExisting] new user with id {} registered", user.getId());
            } else {
                addOrgRoleToUser(user, Tuples.of(orgRole, organization.getId()));
                user = repositories.unsecure.user.save(user);
            }

            model.put(userEntity, user);
            model.put(organizationEntity, organization);
            model.put(PageAttributes.websiteUrl, websiteUrl);
            model.put(passwordRecoveryLink, getPasswordRecoveryLink(user));
            Email emailMsg = services.emailConstructor.prepareEmail(email, user.getName().isEmpty() ? email : user.getName(),
                    emailTitle, emailTemplateName, 5, model);
            repositories.unsecure.email.save(emailMsg);

            return user;

        } finally {
            UserProvider.clearAuthentication();
        }
    }

    /**
     * <p>setPasswordEncoderOnce.</p>
     *
     * @param pe a {@link org.springframework.security.crypto.password.PasswordEncoder} object.
     */
    public static void setPasswordEncoderOnce(PasswordEncoder pe) {
        if (passwordEncoder != null) {
            //Password encoder already initialized
            return;
        }
        UserService.passwordEncoder = pe;
    }

    private Organization createOrganizationOrAssignToDefault(RegisterUserForm registerUserForm) {
        if (creationStrategy.equals(OrganizationCreationStrategy.CREATE)) {
            return organizationService.createOrganization(StringUtils.substringBefore(registerUserForm.getLogin(), "@"), 0);
        }
        return repositories.unsecure.organization.findOne(defaultOrgId);
    }

    private User registerUserWithStrategy(RegisterUserForm registerUserForm, Cookie[] cookies, Organization organization, String[] globalRoles) {
        User user;
        if (organization == null) {
            if (canCreateUserWithoutOrg) {
                user = createUser(registerUserForm.getFirstName(), registerUserForm.getLastName(), registerUserForm.getLogin(), globalRoles);
                user.setLoginAndPassword(user.getEmail(), registerUserForm.getPassword(), false);
                services.applicationEvent.emitEvent(USER_REGISTERED, new RegisteredUserDto(registerUserForm, user.getId(), 0l, cookies));
            } else {
                throw new NullPointerException("Can't create user without organization.");
            }
        } else {
            Tuple2<String, Long> orgRole = Tuples.of(roleOrgAdmin, organization.getId());
            Tuple2[] orgRoles = {orgRole};
            user = createUser(registerUserForm.getFirstName(), registerUserForm.getLastName(), registerUserForm.getLogin(), false, globalRoles, orgRoles);
            user.setLoginAndPassword(user.getEmail(), registerUserForm.getPassword() != null ? registerUserForm.getPassword() : RandomStringUtils.randomAlphanumeric(initialPasswordLength), false);
            services.applicationEvent.emitEvent(USER_REGISTERED, new RegisteredUserDto(registerUserForm, user.getId(), organization.getId(), cookies));
        }
        return user;
    }

    public User registerUser(String firstName, Organization organization, String userLogin,  String email, String websiteUrl, String emailTemplateName, String emailTitle, String orgRole) {
        debug("[registerUserOrReturnExisting]");
        try {
            PageModelMap model = new PageModelMap();


                //user with this email does not exist so create a brand new one with new organization
            UserProvider.setCronJobAuthentication();

            User user = createUser(firstName, null, userLogin, true, null, new Tuple2[]{Tuples.of(orgRole, organization.getId())});
            user.setLoginAndPassword(user.getEmail(), RandomStringUtils.randomAlphanumeric(initialPasswordLength), false);
            user = repositories.unsecure.user.save(user);
            debug("[registerUserOrReturnExisting] new user with id {} registered", user.getId());

            model.put(userEntity, user);
            model.put(organizationEntity, organization);
            model.put(PageAttributes.websiteUrl, websiteUrl);
            model.put(passwordRecoveryLink, getPasswordRecoveryLink(user));
            Email emailMsg = services.emailConstructor.prepareEmail(email, user.getName().isEmpty() ? email : user.getName(),
                    emailTitle, emailTemplateName, 5, model);
            repositories.unsecure.email.save(emailMsg);

            return user;

        } finally {
            UserProvider.clearAuthentication();
        }
    }
    /**
     * This is for new users authenticating through social services, ldap or salesforce
     * It creates an organization for the user and sets appropriate roles
     *
     * @param user
     */
    public boolean createOrganizationAndSetRoles(User user) {
        debug("[createOrganizationAndSetRoles] user: {}", user);
        Organization organization = organizationService.createOrganization(StringUtils.substringBefore(user.getEmail(), "@"), 0);
        String[] globalRoles = {roleGlobalUser};
        Tuple2<String, Long> orgRole = Tuples.of(roleOrgAdmin, organization.getId());
        Tuple2[] orgRoles = {orgRole};

        debug("[createOrganizationAndSetRoles] adding global roles for user");
        addGlobalRoleToUser(user, globalRoles);
        debug("[createOrganizationAndSetRoles] adding org (id: {}) roles for user", organization.getId());
        addOrgRoleToUser(user, orgRoles);
        return true;
    }

    public boolean resendAccountVerificationEmail(String email) {
        debug("[resendAccountVerificationEmail] email: {}", email);
        if(StringUtils.isBlank(email)) {
            debug("[resendAccountVerificationEmail] email cannot be blank!");
            return false;
        }
        User user = repositories.unsecure.user.findByEmailLowercase(email);
        if (user != null) {
            sendAccountVerificationEmail(user, "");
            return true;
        }
        debug("[resendAccountVerificationEmail] user with email {} not found", email);
        return false;
    }

    private User sendAccountVerificationEmail(User user, String languagePrefix) {
        debug("[sendAccountVerificationEmail] userId: {}", user.getId());

        PageModelMap model = new PageModelMap();

        String template = StandardEmailTemplates.WELCOME + (StringUtils.isEmpty(languagePrefix)? "" : "-" + languagePrefix);
        languagePrefix = languagePrefix == "" ? "" : "." + languagePrefix;
        String subject = messages.get("email.welcome.subject" + languagePrefix) + " " + applicationName;

        model.put(userEntity, user);
        model.put(accountVerificationLink, getAccountVerificationLink(user));

        Email email = services.emailConstructor.prepareEmailWithTitleFromTemplate(
                user.getEmail(), null, user.getName(), template, model);

        repositories.unsecure.email.save(email);
        return user;
    }

    /**
     * <p>changePassword.</p>
     *
     * @param user        a {@link com.openkoda.model.User} object.
     * @param newPassword a {@link java.lang.String} object.
     */
    public boolean changePassword(User user, String newPassword) {
        debug("[changePassword]");
        LoginAndPassword loginAndPassword = user.getLoginAndPassword();
        if (loginAndPassword != null) {
            loginAndPassword.setPassword(passwordEncoder.encode(newPassword));
            loginAndPassword.setEnabled(true);
            repositories.unsecure.user.saveAndFlush(user);
            debug("[changePassword] password for user {} changed successfully", user.getId());
        }
        return true;
    }

    /**
     * Returns user if password matches the one stored in DB and user is enabled.
     * @param user  a {@link com.openkoda.model.User} object.
     * @param passwordToVerify a {@link java.lang.String} object.
     * @return true if password matches the one stored in DB and user is enabled. Null otherwise
     */
    public User verifyPassword(User user, String passwordToVerify) {
        debug("[verifyPassword]");
        LoginAndPassword loginAndPassword = user.getLoginAndPassword();
        if (loginAndPassword != null) {
            if (passwordEncoder.matches(passwordToVerify, loginAndPassword.getPassword()) && loginAndPassword.isEnabled()) {
                return user;
            }
        }
        return null;
    }

    public String getPasswordRecoveryLink(User user) {
        debug("[getPasswordRecoveryLink] userId: {}", user.getId());
        Token token = services.token.createTokenForUser(user, Privilege.canResetPassword);

        return baseUrl + _PASSWORD + _RECOVERY + _VERIFY + "?"
                + TOKEN + "=" + token.getUserIdAndTokenBase64String();
    }

    public String getAccountVerificationLink(User user) {
        debug("[getAccountVerificationLink] userId: {}", user.getId());
        Token token = services.token.createTokenForUser(user, Privilege.canVerifyAccount);

        return baseUrl + _REGISTER + _VERIFY + "?"
                + VERIFY_TOKEN + "=" + token.getUserIdAndTokenBase64String();
    }

    /**
     * <p>passwordRecovery.</p>
     *
     * @param user a {@link com.openkoda.model.User} object.
     */
    public boolean passwordRecovery(User user) {
        debug("[passwordRecovery] userId: {}", user.getId());
        PageModelMap model = new PageModelMap();
        model.put(userEntity, user);
        model.put(passwordRecoveryLink, getPasswordRecoveryLink(user));

        Email emailMsg = services.emailConstructor.prepareEmailWithTitleFromTemplate(
                user.getEmail(),
                null,
                user.getName().isEmpty() ? user.getEmail() : user.getName(),
                StandardEmailTemplates.PASSWORD_RECOVERY,
                model);

        repositories.unsecure.email.save(emailMsg);
        return true;
    }

    /**
     * <p>Getter for the field <code>passwordEncoder</code>.</p>
     *
     * @return a {@link org.springframework.security.crypto.password.PasswordEncoder} object.
     */
    public PasswordEncoder getPasswordEncoder() {
        return passwordEncoder;
    }

    public boolean validateIfUserDoesNotHaveRoleInOrganization(String email, Long organizationId, BindingResult br){
        boolean doesNotHave = true;
        if(email != null && !email.isBlank()) {
            User user = repositories.unsecure.user.findByEmailLowercase(email);
            if (user != null) {
                doesNotHave = repositories.unsecure.userRole.findByOrganizationIdAndUserId(organizationId, user.getId()).isEmpty();
            }
            if (!doesNotHave) {
                br.rejectValue("dto.email", "email.exists.in.organization");
            }
        }
        return doesNotHave;
    }

}
