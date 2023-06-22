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

package com.openkoda.controller.common;

import com.openkoda.core.audit.SystemHealthStatus;
import com.openkoda.core.flow.BasePageAttributes;
import com.openkoda.core.flow.PageAttr;
import com.openkoda.core.flow.PageModelMap;
import com.openkoda.core.flow.mbean.LoggingEntriesStack;
import com.openkoda.core.form.AbstractOrganizationRelatedEntityForm;
import com.openkoda.core.form.FrontendMappingDefinition;
import com.openkoda.core.form.FrontendMappingFieldDefinition;
import com.openkoda.core.helper.ReadableCode;
import com.openkoda.core.repository.common.ProfileSettingsRepository;
import com.openkoda.dto.CanonicalObject;
import com.openkoda.dto.OrganizationDto;
import com.openkoda.dto.OrganizationRelatedObject;
import com.openkoda.dto.ServerJsThreadDto;
import com.openkoda.dto.web.OrganizationWebPageDto;
import com.openkoda.dto.web.WebPage;
import com.openkoda.form.*;
import com.openkoda.integration.model.configuration.IntegrationModuleOrganizationConfiguration;
import com.openkoda.model.*;
import com.openkoda.model.authentication.ApiKey;
import com.openkoda.model.common.Audit;
import com.openkoda.model.common.SearchableOrganizationRelatedEntity;
import com.openkoda.model.event.EventListenerEntry;
import com.openkoda.model.event.Scheduler;
import com.openkoda.model.notification.Notification;
import com.openkoda.repository.notifications.NotificationKeeper;
import com.openkoda.uicomponent.dto.UIComponentControllerEndpointList;
import com.openkoda.uicomponent.editor.UIComponentControllerEndpointForm;
import com.openkoda.uicomponent.editor.UIComponentFrontendResourceForm;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.ModelAndView;
import reactor.util.function.Tuple5;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

/**
 * Helper class to keep all Page Attributes in one place.<br/>
 * A PageAttr represents a page attribute (part of model in MVC) name. <br/>
 * The idea is to avoid using Strings and attach a type, which gives us compile-time validation of model attributes.
 *
 * @author Arkadiusz Drysch (adrysch@stratoflow.com)
 * 
 * @see PageAttr
 */
public interface PageAttributes extends BasePageAttributes, ReadableCode {

    String ORGANIZATION_ENTITY = "organizationEntity";
    String ORGANIZATION_ENTITY_ID = "organizationEntityId";
    String ORGANIZATION_ATTRIBUTES = "organizationAttributes";
    String ORGANIZATION_DICTIONARIES_JSON = "organizationDictionariesJson";
    String GLOBAL_USER_ATTRIBUTES = "globalUserAttributes";

    String COMMON_DICTIONARIES = "commonDictionaries";
    String COMMON_DICTIONARIES_NAMES = "commonDictionariesNames";
    String ORGANIZATION_USER_ATTRIBUTES = "organizationUserAttributes";


    PageAttr<String > requestId = new PageAttr<>("requestId");
    PageAttr<String > commonDictionaries = new PageAttr<>(COMMON_DICTIONARIES);
    PageAttr<Set<String> > commonDictionariesNames = new PageAttr<>(COMMON_DICTIONARIES_NAMES);
    PageAttr<String > organizationDictionariesJson = new PageAttr<>(ORGANIZATION_DICTIONARIES_JSON);
    PageAttr<Map<String, String> > organizationAttributes = new PageAttr<>(ORGANIZATION_ATTRIBUTES);
    PageAttr<Map<String, String> > organizationUserAttributes = new PageAttr<>(ORGANIZATION_USER_ATTRIBUTES);
    PageAttr<Map<String, String> > globalUserAttributes = new PageAttr<>(GLOBAL_USER_ATTRIBUTES);
    PageAttr<Map<Long, String> > searchResultPage = new PageAttr<>("searchResultPage");
    PageAttr<String> planEntityId = new PageAttr<>("planEntityId");
    PageAttr<Long> organizationEntityId = new PageAttr<>(ORGANIZATION_ENTITY_ID);
    PageAttr<Long> userEntityId = new PageAttr<>("userEntityId");
    PageAttr<Organization> organizationEntity = new PageAttr<>(ORGANIZATION_ENTITY);
    PageAttr<OrganizationDto> organizationDto = new PageAttr<>("organizationDto");
    PageAttr<OrganizationForm> organizationForm = new PageAttr<>("organizationForm", () -> new OrganizationForm());
    PageAttr<EditUserForm> editUserForm = new PageAttr<>("editUserForm");
    PageAttr<InviteUserForm> inviteUserForm = new PageAttr<>("inviteUserForm");
    PageAttr<GlobalOrgRoleForm> globalOrgRoleForm = new PageAttr<>("globalOrgRoleForm");
    PageAttr<Page<Organization>> organizationPage = new PageAttr<>("organizationPage");
    PageAttr<Audit> auditEntity = new PageAttr<>("auditEntity");
    PageAttr<User> userEntity = new PageAttr<>("userEntity");
    PageAttr<Page<User>> userPage = new PageAttr<>("userPage");
    PageAttr<Page<Audit>> auditPage = new PageAttr<>("auditPage");
    PageAttr<Long> longEntityId = new PageAttr<>("longEntityId");
    PageAttr<Pageable> pageable = new PageAttr<>("pageable");
    PageAttr<String> searchTerm = new PageAttr<>("searchTerm");
    PageAttr<Specification> specification = new PageAttr<>("specification");
    PageAttr<String> passwordRecoveryLink = new PageAttr<>("passwordRecoveryLink");
    PageAttr<String> accountVerificationLink = new PageAttr<>("accountVerificationLink");
    PageAttr<String> websiteUrl = new PageAttr<>("websiteUrl");
    PageAttr<Page<Role>> rolePage = new PageAttr<>("rolePage");
    PageAttr<Role> roleEntity = new PageAttr<>("roleEntity");
    PageAttr<List<Enum>> rolesEnum = new PageAttr<>("rolesEnum");
    PageAttr<RoleForm> roleForm = new PageAttr<>("roleForm");
    PageAttr<Page<FrontendResource>> frontendResourcePage = new PageAttr<>("frontendResourcePage");
    PageAttr<FrontendResource> frontendResourceEntity = new PageAttr<>("frontendResourceEntity");
    PageAttr<FrontendResourceForm> frontendResourceForm = new PageAttr<>("frontendResourceForm");
    PageAttr<UIComponentFrontendResourceForm> uiComponentFrontendResourceForm = new PageAttr<>("uiComponentFrontendResourceForm");
    PageAttr<FrontendResourcePageForm> frontendResourcePageForm = new PageAttr<>("frontendResourcePageForm");
    PageAttr<ControllerEndpoint> controllerEndpoint = new PageAttr<>("controllerEndpoint");
    PageAttr<List<ControllerEndpoint>> controllerEndpoints = new PageAttr<>("controllerEndpoints");
    PageAttr<UIComponentControllerEndpointForm> uiComponentControllerEndpointForm = new PageAttr<>("uiComponentControllerEndpointFormList[0]");
    PageAttr<UIComponentControllerEndpointList> uiComponentControllerEndpointFormList = new PageAttr<>("uiComponentControllerEndpointList");
    PageAttr<PageModelMap> uiComponentModel = new PageAttr<>("uiComponentModel");
    PageAttr<Object> controllerEndpointResult = new PageAttr<>("controllerEndpointResult");
    PageAttr<String> uiComponentPreviewUrl = new PageAttr<>("uiComponentPreviewUrl");
    PageAttr<String> uiComponentUrl = new PageAttr<>("uiComponentUrl");
    PageAttr<EventListenerForm> eventListenerForm = new PageAttr<>("eventListenerForm");
    PageAttr<SendEventForm> sendEventForm = new PageAttr<SendEventForm>("sendEventForm");
    PageAttr<SchedulerForm> schedulerForm = new PageAttr<>("schedulerForm");
    PageAttr<Page<EventListenerEntry>> eventListenerPage = new PageAttr<>("eventListenerPage");
    PageAttr<Page<Scheduler>> schedulerPage = new PageAttr<>("schedulerPage");
    PageAttr<EventListenerEntry> eventListenerEntity = new PageAttr<>("eventListenerEntity");
    PageAttr<EventListenerEntry> eventListenerEntityToUnregister = new PageAttr<>("eventListenerEntityToUnregister");
    PageAttr<Scheduler> schedulerEntity = new PageAttr<>("schedulerEntity");
    PageAttr<String> clientToken = new PageAttr<>("clientToken");
    PageAttr<Token> tokenEntity = new PageAttr<>("tokenEntity");
    PageAttr<IntegrationModuleOrganizationConfiguration> organizationIntegrationModuleConfiguration = new PageAttr<>
            ("organizationIntegrationModuleConfiguration");
    PageAttr<List<Map.Entry<String, String>>> logsEntryList = new PageAttr<>("logsEntryList");
    PageAttr<LoggerForm> loggerForm = new PageAttr<>("loggerForm");
    PageAttr<List<String>> logClassNamesList = new PageAttr<>("logClassNamesList");
    PageAttr<List<String>> arguments = new PageAttr<>("arguments");
    PageAttr<Integer> bufferSize = new PageAttr<>("bufferSize");
    PageAttr<Page<GlobalEntitySearch>> searchPage = new PageAttr<>("searchPage");
    PageAttr<OrganizationRelatedObject> organizationRelatedObject = new PageAttr<>("organizationRelatedObject");
    PageAttr<SearchableOrganizationRelatedEntity> organizationRelatedEntity = new PageAttr<>("organizationRelatedEntity");
    PageAttr<Page<SearchableOrganizationRelatedEntity>> organizationRelatedEntityPage = new PageAttr<>("organizationRelatedEntityPage");
    PageAttr<ModelAndView> modelAndView = new PageAttr<>("modelAndView");
    PageAttr<String> defaultLayout = new PageAttr<>("defaultLayout");
    PageAttr<String> resourcesVersion = new PageAttr<>("resourcesVersion");
    PageAttr<WebPage> webPageDto = new PageAttr<>("webPageDto");
    PageAttr<OrganizationWebPageDto> organizationWebPageDto = new PageAttr<>("organizationWebPageDto");
    PageAttr<List<Notification>> readNotificationsList = new PageAttr<>("readNotificationsList");
    PageAttr<List<Notification>> unreadNotificationsList = new PageAttr<>("unreadNotificationsList");
    PageAttr<Map<ServerJsThreadDto, LoggingEntriesStack<String>>> serverJsThreads = new PageAttr<>("serverJsThreads");
    PageAttr<String> unreadNotificationsIdListString = new PageAttr<>("unreadNotificationsIdListString");
    PageAttr<String> notificationMessage = new PageAttr<>("notificationMessage");
    PageAttr<String> menuItem = new PageAttr<>("menuItem");
    PageAttr<SystemHealthStatus> systemHealthStatus = new PageAttr<>("systemHealthStatus");
    PageAttr<CanonicalObject> canonicalObject = new PageAttr<>("canonicalObject");

    PageAttr<Integer> unreadNotificationsNumber = new PageAttr<>("unreadNotificationsNumber");
    PageAttr<Page<NotificationKeeper>> notificationPage = new PageAttr<>("notificationPage");
    PageAttr<Object> scriptResult = new PageAttr<>("scriptResult");
    PageAttr<Page<OrganizationDto>> organizationDtoPage = new PageAttr<>("organizationDtoPage");
    PageAttr<ApiKey> apiKeyEntity = new PageAttr<>("apiKeyEntity");
    PageAttr<String> plainApiKeyString = new PageAttr<>("plainApiKeyString");
    PageAttr<String> baseUrl = new PageAttr<>("baseUrl");
    PageAttr<String> errorMessage = new PageAttr<>("errorMessage");
    PageAttr<HttpHeaders> httpHeaders = new PageAttr<>("httpHeaders");
    PageAttr<HttpStatus> errorHttpStatus = new PageAttr<>("errorHttpStatus");
    PageAttr<List<String>> errorList = new PageAttr<>("errorList");
    PageAttr<AbstractOrganizationRelatedEntityForm> organizationRelatedForm = new PageAttr<>("organizationRelatedForm");
    PageAttr<FrontendMappingDefinition> frontendMappingDefinition = new PageAttr<>("frontendMappingDefinition");
    PageAttr<List<Object[]>> genericTableViewList = new PageAttr<>("genericTableViewList");
    PageAttr<List<Map<String,Object>>> genericTableViewMap = new PageAttr<>("genericTableViewMap");
    PageAttr<List<FrontendMappingFieldDefinition>> genericTableViewHeaders = new PageAttr<>("genericTableViewHeaders");
    PageAttr<Boolean> isMapEntity = new PageAttr<>("isMapEntity");
    PageAttr<Boolean> isPageEditor = new PageAttr<>("isPageEditor");
    PageAttr<Map<Object, Object[]>> rulesSelectedElements = new PageAttr<>("rulesSelectedElements");
    PageAttr<List<Tuple5<ProfileSettingsRepository, Function, PageAttr, String, String>>> additionalSettingsForms = new PageAttr<>("additionalSettingsForms");
    PageAttr<Boolean> isValid = new PageAttr<>("isValid");
    PageAttr<String> redirectUrl = new PageAttr<>("redirectUrl");


    default <S, T> Function<Page<S>, Page<T>> pageConverter(Function<? super S, ? extends T> elementConverter) {
        return (page) -> page.map(elementConverter);
    }
}
