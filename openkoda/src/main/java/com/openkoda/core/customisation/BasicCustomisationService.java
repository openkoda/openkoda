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

package com.openkoda.core.customisation;

import com.openkoda.controller.ApiCRUDControllerConfigurationMap;
import com.openkoda.controller.ComponentProvider;
import com.openkoda.controller.HtmlCRUDControllerConfigurationMap;
import com.openkoda.core.audit.AuditInterceptor;
import com.openkoda.core.audit.PropertyChangeListener;
import com.openkoda.core.flow.PageAttr;
import com.openkoda.core.form.CRUDControllerConfiguration;
import com.openkoda.core.form.FrontendMappingDefinition;
import com.openkoda.core.form.ReflectionBasedEntityForm;
import com.openkoda.core.helper.SpringProfilesHelper;
import com.openkoda.core.lifecycle.BaseDatabaseInitializer;
import com.openkoda.core.lifecycle.SearchViewCreator;
import com.openkoda.core.multitenancy.MultitenancyService;
import com.openkoda.core.repository.common.ProfileSettingsRepository;
import com.openkoda.core.repository.common.SearchableFunctionalRepositoryWithLongId;
import com.openkoda.core.security.UserProvider;
import com.openkoda.core.service.BackupService;
import com.openkoda.core.service.FrontendMappingDefinitionService;
import com.openkoda.core.service.GenericWebhookService;
import com.openkoda.core.service.SlackService;
import com.openkoda.core.service.email.EmailService;
import com.openkoda.core.service.event.AbstractApplicationEvent;
import com.openkoda.core.service.event.ApplicationEvent;
import com.openkoda.core.service.event.EventConsumer;
import com.openkoda.dto.CanonicalObject;
import com.openkoda.dto.NotificationDto;
import com.openkoda.dto.OrganizationRelatedObject;
import com.openkoda.dto.system.ScheduledSchedulerDto;
import com.openkoda.integration.service.PushNotificationService;
import com.openkoda.model.User;
import com.openkoda.model.common.AuditableEntity;
import com.openkoda.model.common.SearchableEntity;
import com.openkoda.model.module.Module;
import com.openkoda.repository.SearchableRepositories;
import com.openkoda.service.role.RoleModificationsConsumers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import reactor.util.function.Tuple5;
import reactor.util.function.Tuples;

import java.io.File;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

import static com.openkoda.controller.common.URLConstants._LOGIN;
import static com.openkoda.core.helper.NameHelper.getClasses;


@Service
/**
 * <p>BasicCustomisationService class.</p>
 *
 * @author Arkadiusz Drysch (adrysch@stratoflow.com)
 *
 */
public class BasicCustomisationService extends ComponentProvider implements CustomisationService {

    @Autowired
    private AuditInterceptor auditInterceptor;

    @Autowired
    private ApplicationContext appContext;

    @Autowired
    private BaseDatabaseInitializer initialDataLoader;

    @Autowired
    private SearchViewCreator searchViewCreator;

    @Autowired
    private MultitenancyService multitenancyService;

    @Autowired
    private HtmlCRUDControllerConfigurationMap htmlCrudControllerConfigurationMap;

    @Autowired
    private ApiCRUDControllerConfigurationMap apiCrudControllerConfigurationMap;

    @Autowired
    private FrontendMappingMap frontendMappingMap;

    @Autowired
    private FrontendMappingDefinitionService frontendMappingDefinitionService;

    @Value("${init.admin.username}")
    private String initAdminUsername;

    @Value("${init.admin.password}")
    private String initAdminPassword;

    @Value("${base.url:http://localhost:8080}")
    private String baseUrlString;

    @Value("${application.classes.event:}")
    private String[] eventClasses;
    /**
     * {@inheritDoc}
     */
    public final <T extends AuditableEntity> PropertyChangeListener registerAuditableClass(Class<T> c, String classLabel) {
        return auditInterceptor.registerAuditableClass(c, classLabel);
    }

    private List<Consumer<CustomisationService>> onApplicationStartListeners = new ArrayList<>();

    /**
     * {@inheritDoc}
     */
    @Override
    public final <T> boolean registerEventListener(AbstractApplicationEvent event, Consumer<T> eventListener) {
        return services.applicationEvent.registerEventListener(event, eventListener);
    }

    @Override
    public final <T> boolean registerEventListener(AbstractApplicationEvent event, BiConsumer<T, String> eventListener, String staticData1, String staticData2, String staticData3, String staticData4) {
        return services.applicationEvent.registerEventListener(event, eventListener,  staticData1,  staticData2,  staticData3,  staticData4);
    }

    @Override
    public final <T> boolean registerEventConsumer(Class<T> eventClass, EventConsumer<T> eventConsumer) {
        return services.applicationEvent.registerEventConsumer(eventClass, eventConsumer);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Module registerModule(Module module) {
        return services.module.registerModule(module);
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void onApplicationStart() {
        try {
            UserProvider.setCronJobAuthentication();
            SearchableRepositories.discoverSearchableRepositories();
            searchViewCreator.prepareSearchableRepositories();
            initialDataLoader.loadInitialData(SpringProfilesHelper.isInitializationProfile());
            this.registerApplicationConsumers();
            services.eventListener.registerEventClasses((Class<AbstractApplicationEvent>[]) getClasses(eventClasses));
            services.eventListener.setAllAvailableAppEvents();
            services.eventListener.setAllAvailableAppConsumers();
            services.eventListener.registerAllEventListenersFromDb();
            services.scheduler.scheduleAllFromDb();
            for (Consumer<CustomisationService> c : onApplicationStartListeners) {
                c.accept(this);
            }
            services.applicationEvent.emitEvent(ApplicationEvent.APPLICATION_STARTED, LocalDateTime.now());
        } finally {
            UserProvider.clearAuthentication();
        }
        if (SpringProfilesHelper.isInitializationProfile()) {
            System.out.println("*********************************************************************");
            System.out.println(" Application initialized successfully.");
            System.out.println(String.format(" Start application without the %s profile", SpringProfilesHelper.INITIALIZATION_PROFILE));
            System.out.println(String.format(" and go to %s%s", baseUrlString, _LOGIN));
            System.out.println(String.format(" Credentials (u/p): %s / %s", initAdminUsername, initAdminPassword));
            System.out.println("*********************************************************************");
            SpringApplication.exit(appContext, () -> 0);
            System.exit(0);
        }
    }

    private void registerApplicationConsumers() {
        services.applicationEvent.registerEventConsumerWithMethod(User.class, EmailService.class, "sendAndSaveEmail",
                "Consumer that sends an email to User (event object), based on template specified by the second parameter.", String.class);
        services.applicationEvent.registerEventConsumerWithMethod(CanonicalObject.class, EmailService.class, "sendAndSaveEmail",
                "Consumer that sends an email to the email provided as second parameter, based on template specified by the first static parameter.", String.class, String.class);
        services.applicationEvent.registerEventConsumerWithMethod(ScheduledSchedulerDto.class, BackupService.class, "doFullBackup",
                "The consumer is supposed to do back-up. It will proceed only if the event parameter of ScheduledSchedulerDto object == consumer.parameter.backup " +
                        "property (default == 'backup').");
        services.applicationEvent.registerEventConsumerWithMethod(File.class, BackupService.class, "copyBackupFile",
                "This consumer will perform a secure copy of created backup archive file to remote host. If remote host isn't specified file will be copied into local path.");
        services.applicationEvent.registerEventConsumerWithMethod(CanonicalObject.class, ServerJSRunner.class, "runScriptJS",
                "This consumer will run Javascript that is defined as \"SERVER_JS\". Consumer is parametrized by the name of script.", String.class);
        services.applicationEvent.registerEventConsumerWithMethod(OrganizationRelatedObject.class, RoleModificationsConsumers.class, "modifyRoleForAllUsersInOrganization",
                "This consumer will run Javascript that is defined in Server-side Js. And modify all users role given by the script. Consumer is parametrized by the name of script.", String.class);
        services.applicationEvent.registerEventConsumerWithMethod(OrganizationRelatedObject.class, RoleModificationsConsumers.class, "modifyGlobalRoleForOrganization",
                "This consumer will run Javascript that is defined as \"SERVER_JS\". And add or remove roles given by the script. Consumer is parametrized by the name of script.", String.class);
        services.applicationEvent.registerEventConsumerWithMethod(NotificationDto.class, PushNotificationService.class, "createSlackPostMessageRequest",
                "This consumer generates a HttpRequest object which would then be found by a scheduled job and pushed as a message to the organization's Slack channel.");
        services.applicationEvent.registerEventConsumerWithMethod(NotificationDto.class, PushNotificationService.class, "createMsTeamsPostMessageRequest",
                "This consumer generates a HttpRequest object which would then be found by a scheduled job and pushed as a message to the organization's Ms Teams channel.");
        services.applicationEvent.registerEventConsumerWithMethod(NotificationDto.class, PushNotificationService.class, "createEmailNotification",
                "This consumer generates a notification Email which would then be send to a recipient by a scheduled job.");
        services.applicationEvent.registerEventConsumerWithMethod(CanonicalObject.class, SlackService.class, "sendToSlackWithCanonical",
                "Sends message generated in FrontendResource(first param) to slack via webHook(second param).", String.class, String.class);
        services.applicationEvent.registerEventConsumerWithMethod(ScheduledSchedulerDto.class, ServerJSRunner.class, "startScheduledServerJs",
                "Executes Server-side Js on Scheduler Event. Param1: Scheduler event data must match Static Parameter 1 in order to run. Param2: Name of the Server-side JS to run. All 4 Static Parameters are passed as arguments to Server js (arguments.length == 4)", String.class, String.class, String.class, String.class);
        services.applicationEvent.registerEventConsumerWithMethod(CanonicalObject.class, GenericWebhookService.class, "sendToUrlWithCanonical",
                "Sends message to url (first param) generated as JSON in FrontendResource(second param), with headers as JSON in FrontendResource(third param).", String.class, String.class, String.class);
        services.applicationEvent.registerEventConsumerWithMethod(LocalDateTime.class, ServerJSRunner.class, "startCustomisationServerJs",
                "Executes ServerJs with customisation service. Param1: Name of the ServerJS to run. Server js can access 'customisationService' object. All 4 Static Parameters are passed as arguments to Server js (arguments.length == 4)", String.class, String.class, String.class, String.class);
    }

    @Override
    public <T> void registerApplicationEventClass(Class<T> eventClass) {
        services.eventListener.registerEventClass(eventClass);
    }

    public final List<Tuple5<ProfileSettingsRepository, Function, PageAttr, String, String>> additionalSettingsForms = new ArrayList<>();

    @Override
    public <SE extends SearchableEntity, SF> void registerSettingsForm(
            ProfileSettingsRepository<SE> repository,
            Function<SE, SF> formConstructor,
            PageAttr<SF> formPageAttribute,
            String formFragmentFile,
            String formFragmentName) {
        additionalSettingsForms.add(Tuples.of(repository, formConstructor, formPageAttribute, formFragmentFile, formFragmentName));
    }

    @Override
    public void registerOnApplicationStartListener(Consumer<CustomisationService> c) {
        onApplicationStartListeners.add(c);
    }

    @Override
    public synchronized void registerFrontendMapping(FrontendMappingDefinition definition, SearchableFunctionalRepositoryWithLongId repository) {

        String uniqueName = definition.name;
        frontendMappingMap.put(uniqueName, new FrontendMapping(definition, repository));
    }

    @Override
    public CRUDControllerConfiguration registerHtmlCrudController(FrontendMappingDefinition definition, SearchableFunctionalRepositoryWithLongId repository) {
        return htmlCrudControllerConfigurationMap.registerCRUDController(definition, repository, ReflectionBasedEntityForm.class);
    }

    @Override
    public CRUDControllerConfiguration registerApiCrudController(FrontendMappingDefinition definition, SearchableFunctionalRepositoryWithLongId repository) {
        return apiCrudControllerConfigurationMap.registerCRUDController(definition, repository, ReflectionBasedEntityForm.class);
    }

    @EventListener(ContextRefreshedEvent.class)
    public CoreSettledEvent onApplicationEvent(ContextRefreshedEvent event) {
        if(this.getClass().equals(BasicCustomisationService.class)) {
            onApplicationStart();
            return new CoreSettledEvent();
        }
        return null;
    }

    public FrontendMappingDefinitionService getFrontendMappingDefinitionService() {
        return frontendMappingDefinitionService;
    }
}
