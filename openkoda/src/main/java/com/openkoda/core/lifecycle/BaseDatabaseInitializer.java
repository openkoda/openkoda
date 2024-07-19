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

package com.openkoda.core.lifecycle;

import com.openkoda.controller.ComponentProvider;
import com.openkoda.core.helper.PrivilegeHelper;
import com.openkoda.core.multitenancy.QueryExecutor;
import com.openkoda.core.security.UserProvider;
import com.openkoda.model.*;
import com.openkoda.model.component.ServerJs;
import com.openkoda.service.export.ClasspathComponentImportService;
import jakarta.inject.Inject;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import reactor.util.function.Tuple2;

import java.util.*;
import java.util.stream.Collectors;

import static com.openkoda.core.helper.SpringProfilesHelper.TEST_PROFILE;

/**
 * Run database operations requires on the clean application initialization.
 * This particular class provides tasks needed for clean instance.
 * In order to extend or replace the scope of the database initialization the recommended approach is
 * to implement another Spring component that extends BaseDatabaseInitializer and is activated by
 * {@link org.springframework.context.annotation.Primary} annotation or specific profile.
 * This is a production routine of database setup, activated when Spring profile != "test".
 * For tests, see {@link com.openkoda.common.TestDataLoader} which is activated when Spring profile  == 'test'.
 *
 * See also {@link SearchViewCreator}
 *
 * @author Arkadiusz Drysch (adrysch@stratoflow.com)
 *
 */
@Component
@Profile("!" + TEST_PROFILE)
public class BaseDatabaseInitializer extends ComponentProvider {

    /**
     * Default global admin role created in application initialization
     */
    public static final String ROLE_ADMIN = "ROLE_ADMIN";

    /**
     * Default global user role created in application initialization
     */
    public static final String ROLE_USER = "ROLE_USER";

    /**
     * Default organization level admin role created in application initialization
     */
    public static final String ROLE_ORG_ADMIN = "ROLE_ORG_ADMIN";

    /**
     * Default organization level user role created in application initialization
     */
    public static final String ROLE_ORG_USER = "ROLE_ORG_USER";

    /**
     * Default organization level user role created in application initialization
     */
    public static final String CORE_MODULE = "core";

    /**
     * List of SQL initialization scripts executed on application initialization
     */
    List<String> globalInitializationScripts = Collections.emptyList();

    /**
     * Content of a script to be executed on database init. Used for example in
     * cloud instance setup, when post-init script cannot be added as a resource
     * file like the `init.sql`
     */
    private String initializationExternalScript;

    @Inject
    private PasswordEncoder passwordEncoder;

    @Value("${application.admin.email}")
    private String applicationAdminEmail;

    @Value("${init.admin.username}")
    private String initAdminUsername;

    @Value("${init.admin.password}")
    private String initAdminPassword;

    @Value("${init.admin.firstName:Mark}") private String initAdminFirstName;

    @Value("${init.admin.lastName:Administrator}") private String initAdminLastName;

    @Value("${base.url:http://localhost:8080}")
    private String baseUrl;

    /**
     * Flag indication that the setup was already executed
     */
    boolean alreadySetup = false;

    private QueryExecutor queryExecutor;

    private ClasspathComponentImportService classpathComponentImportService;

    public BaseDatabaseInitializer(
            @Autowired QueryExecutor queryExecutor,
            @Value("${global.initialization.scripts.commaseparated:}") String initializationScripts,
            @Value("${global.initialization.externalScript:}") String initializationExternalScript,
            @Autowired ClasspathComponentImportService classpathComponentImportService) {
        this.queryExecutor = queryExecutor;
        if (StringUtils.isNotBlank(initializationScripts)) {
            globalInitializationScripts = Arrays.stream(initializationScripts.split(",")).map(a -> StringUtils.trim(a)).collect(Collectors.toList());
        }

        if (StringUtils.isNotBlank(initializationExternalScript)) {
            // need to unescape double quotes most likely inserted by a bash scripts
            this.initializationExternalScript = initializationExternalScript.replace("\"", "");
        }

        this.classpathComponentImportService = classpathComponentImportService;
    }

    /**
     * Main procedure for database initialization
     * This method can be invoked by more customized inherited implementations of database initializers.
     * Any overriding implementation should respect the proceed parameter, and actually run the
     * initialization only when proceed == true
     * @param proceed indicates, whether the setup procedure should actually happen
     */
    @Transactional
    public void loadInitialData(boolean proceed) {
        debug("[onApplicationContextStarting] proceed {}", proceed);
        if (not(proceed)) {
            return;
        }

        try {
            UserProvider.setCronJobAuthentication();
            createCoreModule();
            createInitialRoles();
            createRegistrationFormServerJs();
            runInitializationScripts();
            classpathComponentImportService.loadAllComponents();
            alreadySetup = true;
        } finally {
            UserProvider.clearAuthentication();
        }

    }

    /**
     * Runs SQL initialization scripts
     */
    private void runInitializationScripts() {
        for(String s : globalInitializationScripts) {
            info("[runInitializationScripts] executing script {}", s);
            queryExecutor.runQueryFromResourceInTransaction(s);
        }

        if (this.initializationExternalScript != null) {
            queryExecutor.runQueriesInTransaction(this.initializationExternalScript);
        }
    }

//TODO - check if can be removed
    private void createRegistrationFormServerJs() {
        ServerJs registerFormServerJs = new ServerJs();
        registerFormServerJs.setCode("model");
        registerFormServerJs.setModel("{\"registerForm@com.openkoda.form.RegisterUserForm\" : {}}");
        registerFormServerJs.setName("initRegisterForm");
        repositories.unsecure.serverJs.save(registerFormServerJs);
    }

    /**
     * Creates initial default roles of application, which are:
     * ROLE_ADMIN: global user role created in application initialization
     * ROLE_USER: global user role created in application initialization
     * ROLE_ORG_ADMIN: organization level admin role created in application initialization
     * ROLE_ORG_USER: organization level user role created in application initialization
     *
     * Also, creates admin user with ROLE_ADMIN role, and credentials defined in properties file
     */
    private void createInitialRoles() {

        // in order to create admin the application.admin.email property must be set
        if (StringUtils.isBlank(applicationAdminEmail)) {
            System.out.println("*********************************************************************");
            System.out.println(" Initialization Error: set application.admin.email property.");
            System.out.println("*********************************************************************");
            System.exit(1);
        }

        Set<PrivilegeBase> adminPrivileges = PrivilegeHelper.getAdminPrivilegeSet();
        Set<PrivilegeBase> userPrivileges = new HashSet<>(PrivilegeHelper.getUserPrivilegeSet());
        Set<PrivilegeBase> unauthenticatedPrivileges = new HashSet<>(Arrays.asList());
        Set<PrivilegeBase> orgAdminPrivileges = new HashSet<>(PrivilegeHelper.getOrgAdminPrivilegeSet());
        Set<PrivilegeBase> orgUserPrivileges = new HashSet<>(PrivilegeHelper.getOrgUserPrivilegeSet());

        services.role.createOrUpdateGlobalRole("ROLE_UNAUTHENTICATED", unauthenticatedPrivileges, false);

        GlobalRole adminRole = services.role.createOrUpdateGlobalRole(ROLE_ADMIN, adminPrivileges, false);
        GlobalRole userRole = services.role.createOrUpdateGlobalRole(ROLE_USER, userPrivileges, false);
        OrganizationRole orgAdmin = services.role.createOrUpdateOrgRole(ROLE_ORG_ADMIN, orgAdminPrivileges, false);
        OrganizationRole orgUser = services.role.createOrUpdateOrgRole(ROLE_ORG_USER, orgUserPrivileges, false);

        if (repositories.unsecure.user.findByLogin(initAdminUsername) == null) {
            User u = services.user.createUser(initAdminFirstName, initAdminLastName, applicationAdminEmail, true,
                    new String[]{ROLE_ADMIN}, new Tuple2[]{});
            u.setLoginAndPassword(initAdminUsername, initAdminPassword, true);
            repositories.unsecure.loginAndPassword.save(u.getLoginAndPassword());
            repositories.unsecure.user.save(u);
        }
    }

    private void createCoreModule() {
        OpenkodaModule openkodaModule = new OpenkodaModule(CORE_MODULE);
        repositories.unsecure.openkodaModule.save(openkodaModule);
    }
}
