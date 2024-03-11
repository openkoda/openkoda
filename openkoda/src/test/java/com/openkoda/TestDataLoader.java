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

package com.openkoda;

import com.openkoda.controller.common.URLConstants;
import com.openkoda.core.lifecycle.BaseDatabaseInitializer;
import com.openkoda.core.multitenancy.QueryExecutor;
import com.openkoda.core.security.UserProvider;
import com.openkoda.form.RegisterUserForm;
import com.openkoda.model.*;
import com.openkoda.model.common.Audit;
import com.openkoda.model.component.FrontendResource;
import com.openkoda.model.component.Scheduler;
import com.openkoda.model.component.event.EventListenerEntry;
import com.openkoda.service.export.ComponentImportService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.openkoda.core.helper.SpringProfilesHelper.TEST_PROFILE;


/**
 * This class is intented to insert all data required for testing purposes.
 */
@Component
@Profile(TEST_PROFILE)
public class TestDataLoader extends BaseDatabaseInitializer {

    public TestDataLoader(
            @Autowired QueryExecutor queryExecutor,
            @Value("${global.initialization.scripts.commaseparated:}") String initializationScripts,
            @Autowired ComponentImportService componentImportService) {
        super(queryExecutor, initializationScripts, null, componentImportService);
    }

    @Transactional
    @Override
    public void loadInitialData(boolean proceed) {
        super.loadInitialData(true);
        try{
            UserProvider.setCronJobAuthentication();
            User admin = repositories.unsecure.user.findByLogin("admin");
            if (admin != null) {
                Organization org = createOrganization("Test Org");
                services.user.addOrgRoleToUser(admin, new Tuple2[]{Tuples.of("ROLE_ORG_ADMIN", org.getId())});
            }

        } finally {
            UserProvider.clearAuthentication();
        }
    }

    public Organization createOrganization(String name) {
        return services.organization.createOrganization(name,0);
    }

    public User createUser(String firstName,
                           String lastName,
                           String email,
                           Tuple2<String /* roleName */, Long /* orgId */>... orgRoles) {

        return services.user.createUser(firstName, lastName, email, orgRoles);
    }

    public User createUser(String firstName,
                           String lastName,
                           String email,
                           String... globalRoles) {
        return services.user.createUser(firstName, lastName, email, globalRoles);
    }

    public User createUser(String firstName,
                           String lastName,
                           String email,
                           boolean userEnabled,
                           String[] globalRoles,
                           Tuple2<String /* roleName */, Long /* orgId */>[] orgRoles) {


        User user = services.user.createUser(firstName, lastName, email, userEnabled, globalRoles, orgRoles);
        user.setLoginAndPassword(firstName, lastName, true);

        return repositories.unsecure.user.save(user);
    }

    /**
     * @return an existing user if found, otherwise create a new user
     */
    public User getUser(String firstName,
                        String lastName,
                        String email,
                        boolean userEnabled,
                        String[] globalRoles,
                        String orgRoles) {

        User user = repositories.unsecure.user.findByLogin(email);
        if (user != null) {
            return user;
        }
        Organization org = findOrganizationByName("Test Org");
        return createUser(firstName, lastName, email, userEnabled, globalRoles,  new Tuple2[]{Tuples.of(orgRoles, org.getId())});
    }

    public User registerUser(String firstName, String lastName, String login, String password) {
        RegisterUserForm registerUserForm = new RegisterUserForm();
        registerUserForm.setFirstName(firstName);
        registerUserForm.setLastName(lastName);
        registerUserForm.setLogin(login);
        registerUserForm.setPassword(password);

        return services.user.registerUserOrReturnExisting(registerUserForm);
    }

    public Role createGlobalRole(String name, Set<Enum> privileges, boolean removable) {
        GlobalRole role = new GlobalRole(name);
        role.setPrivilegesSet(privileges);
        role.setRemovable(removable);
        repositories.unsecure.role.save(role);
        return repositories.unsecure.role.findByName(name);
    }

    public Organization findOrganizationByName(String name) {
        return repositories.unsecure.organization.findByName(name);
    }

    public Role createOrganizationRole(String name, Set<Enum> privileges, boolean removable) {
        OrganizationRole role = new OrganizationRole(name);
        role.setPrivilegesSet(privileges);
        role.setRemovable(removable);
        repositories.unsecure.role.save(role);
        return repositories.unsecure.role.findByName(name);
    }

    public FrontendResource createFrontendResource(String name, String URL, String content, FrontendResource.Type type) {
        if (StringUtils.isBlank(name)) {
            throw new RuntimeException("[createFrontendResource] Name cannot be empty");
        }
        if (type == null) {
            throw new RuntimeException("[createFrontendResource] Type cannot be empty");
        }
        if (!URL.matches(URLConstants.FRONTENDRESOURCEREGEX)) {
            throw new RuntimeException("[createFrontendResource] URL does not match required regex");
        }
        if (not(type.equals(FrontendResource.Type.getEntryTypeFromPath(URL)))) {
            throw new RuntimeException("[createFrontendResource] URL and type must match (.js for JS, .css for CSS, .xml for XML, HTML otherwise)");
        }
        FrontendResource frontendResource = new FrontendResource(name, URL, content, null, type);
        return repositories.unsecure.frontendResource.save(frontendResource);
    }

    public Scheduler createScheduler(String croonExpression, String eventData) {
        Scheduler scheduler = new Scheduler(croonExpression, eventData, false);
        return repositories.unsecure.scheduler.save(scheduler);
    }

    public EventListenerEntry createEventListenerEntry(String eventClassName, String eventName, String eventObjectType, String consumerClassName, String consumerMethodName) {
        EventListenerEntry eventListenerEntry = new EventListenerEntry(eventClassName, eventName, eventObjectType, consumerClassName, consumerMethodName);
        return repositories.unsecure.eventListener.save(eventListenerEntry);
    }

    public EventListenerEntry createEventListenerEntry(String eventClassName, String eventName, String eventObjectType, String consumerClassName, String consumerMethodName, String consumerParameterClassName, String staticData1, String staticData2, String staticData3, String staticData4) {
        EventListenerEntry eventListenerEntry = new EventListenerEntry(eventClassName, eventName, eventObjectType, consumerClassName, consumerMethodName, consumerParameterClassName, staticData1, staticData2, staticData3, staticData4);
        return repositories.unsecure.eventListener.save(eventListenerEntry);
    }


    public Audit createInfoAudit(Long userId, String message, String change, String content) {

        Audit audit = new Audit();
        audit.setUserId(userId);
        audit.setChange(change);
        audit.setContent(content);
        audit.setSeverity(Audit.Severity.INFO);
        return repositories.unsecure.audit.save(audit);
    }

    public long getNumberOfRecordsInDatabase(String column) {

        if ("user".equals(column)) {
            return repositories.unsecure.user.count();
        } else if ("audit".equals(column)) {
            return repositories.unsecure.audit.count();
        } else if ("organization".equals(column)) {
            return repositories.unsecure.organization.count();
        } else if ("role".equals(column)) {
            return repositories.unsecure.role.count();
        } else if ("frontendResource".equals(column)) {
            return repositories.unsecure.frontendResource.count();
        } else if ("event".equals(column)) {
            return repositories.unsecure.eventListener.count();
        } else if ("scheduler".equals(column)) {
            return repositories.unsecure.scheduler.count();
        }

        return 0;
    }

    public void createRecordsForColumn(String column, long numberOfTestRecords) {
        User admin = repositories.unsecure.user.findByLogin("admin");
        List<Organization> organizations = repositories.unsecure.organization.findAll();

        if ("event".equals(column)) {
            if (numberOfTestRecords > 0) {
                createEventListenerEntry("com.openkoda.core.service.event.ApplicationEvent", "SCHEDULER_EXECUTED", "com.openkoda.dto.system.ScheduledSchedulerDto", "com.openkoda.core.service.BackupService", "doFullBackup");
            }
            if (numberOfTestRecords > 1) {
                createEventListenerEntry("com.openkoda.core.service.event.ApplicationEvent", "USER_CREATED", "com.openkoda.dto.user.BasicUser", "com.openkoda.core.service.email.EmailService", "sendAndSaveEmail", "com.openkoda.dto.CanonicalObject", "test", "test@test.com", null, null);
            }
            return;
        }

        for (int i = 0; i < numberOfTestRecords; i++) {
            if ("user".equals(column)) {
                createUser("test_" + i, "test_" + i, "test_" + i, "ROLE_USER");
            } else if ("audit".equals(column)) {
                createInfoAudit(admin.getId(), "Add/Edit/Remove", "test_" + i, "test_" + i);
            } else if ("organization".equals(column)) {
                createOrganization("test_" + i);
            } else if ("role".equals(column)) {
                Set<Enum> privileges = new HashSet<>();
                privileges.addAll(Arrays.asList(Privilege.values()));
                createGlobalRole("test_role_" + i, privileges, true);
            } else if ("module".equals(column)) {
                return;
            } else if ("frontendResource".equals(column)) {
                createFrontendResource("frontendResource#test_" + i, "test" + i, "<!DOCTYPE html><html><body><h1>TEST</h1></body></html>", FrontendResource.Type.HTML);
            } else if ("scheduler".equals(column)) {
                createScheduler("* * * * * 1", "test_" + i);
            }
        }
    }
}

