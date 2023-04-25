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

package com.openkoda.form;

import com.openkoda.core.form.FrontendMappingDefinition;
import com.openkoda.core.form.ReflectionBasedEntityForm;
import com.openkoda.core.multitenancy.MultitenancyService;
import com.openkoda.core.security.HasSecurityRules;
import com.openkoda.model.FrontendResource;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;

import static com.openkoda.controller.common.URLConstants.FRONTENDRESOURCE;
import static com.openkoda.core.form.FrontendMappingDefinition.createFrontendMappingDefinition;
import static com.openkoda.model.Privilege.*;

public interface FrontendMappingDefinitions extends HasSecurityRules, TemplateFormFieldNames {

    String USER_FORM = "userForm";
    String SCHEDULER_FORM = "schedulerForm";
    String ROLE_FORM = "roleForm";
    String ORGANIZATION_FORM = "organizationForm";
    String LOGGER_FORM = "loggerForm";
    String FRONTEND_RESOURCE_FORM = "frontendResourceForm";
    String FRONTEND_RESOURCE_PAGE_FORM = "frontendResourcePageForm";
    String CONTROLLER_ENDPOINT_FORM = "controllerEndpoint";
    String EVENT_LISTENER_FORM = "eventListenerForm";
    String SEND_EVENT_FORM = "sendEventForm";
    String ATTRIBUTE_DEFINITION_FORM = "attributeDefinitionForm";
    String EDIT_USER_FORM = "editUserForm";
    String INVITE_USER_FORM_NAME = "inviteUserForm";
    String TABLE_COMPACT_CSS = "table-compact ";


    FrontendMappingDefinition roleForm = createFrontendMappingDefinition(ROLE_FORM, canReadBackend, canManageBackend,
        a -> a  .text(NAME_)
                .dropdown(TYPE_, ROLE_TYPES_)
                .checkboxList(PRIVILEGES_, PRIVILEGES_).additionalCss(TABLE_COMPACT_CSS));

    FrontendMappingDefinition userForm = createFrontendMappingDefinition(USER_FORM, null, null,
        a -> a  .text(EMAIL_).additionalPrivileges(CHECK_IS_NEW_USER_OR_OWNER, CHECK_IF_CAN_WRITE_USER)
                .text(FIRST_NAME_).additionalPrivileges(CHECK_IS_NEW_USER_OR_OWNER, CHECK_IS_NEW_USER_OR_OWNER)
                .text(LAST_NAME_).additionalPrivileges(CHECK_IS_NEW_USER_OR_OWNER, CHECK_IS_NEW_USER_OR_OWNER)
    );

    FrontendMappingDefinition inviteForm = createFrontendMappingDefinition(INVITE_USER_FORM_NAME, null, null, FrontendMappingDefinitions.userForm.fields,
        a -> a  .dropdown(ROLE_NAME_, ORGANIZATION_ROLES_).additionalPrivileges(CHECK_IS_NEW_USER_OR_OWNER, CHECK_IS_NEW_USER_OR_OWNER)
    );

    FrontendMappingDefinition globalOrgFrom = createFrontendMappingDefinition("globalOrgForm", canAccessGlobalSettings, canAccessGlobalSettings,
            a -> a.checkboxList("globalOrganizationRoles", "globalOrganizationRoles"));

    FrontendMappingDefinition editUserForm = createFrontendMappingDefinition(EDIT_USER_FORM, readUserData, manageUserData, FrontendMappingDefinitions.userForm.fields,
        a -> a  .dropdown(ENABLED_, BOOLEAN_VALUES_)
                .dropdown(LANGUAGE, LANGUAGES).additionalPrivileges(CHECK_IS_NEW_USER_OR_OWNER, CHECK_IS_NEW_USER_OR_OWNER)
                .dropdown(GLOBAL_ROLE_NAME_, GLOBAL_USER_ROLES_).additionalPrivileges(canAccessGlobalSettings, canAccessGlobalSettings)
            );

    FrontendMappingDefinition schedulerForm = createFrontendMappingDefinition(SCHEDULER_FORM, canReadBackend, canManageBackend,
            a -> a  .organizationSelect(ORGANIZATION_ID_)
                    .text(CRON_EXPRESSION_)
                    .text(EVENT_DATA_)
                    .checkbox(ON_MASTER_ONLY_)
    );

    FrontendMappingDefinition organizationForm = createFrontendMappingDefinition(ORGANIZATION_FORM, readOrgData, manageOrgData,
            a -> a  .text(NAME_)
                    .dropdown(ASSIGNED_DATASOURCE_, "datasources", false)
                        .additionalPrivileges(
                                (u, e) -> MultitenancyService.isMultitenancy() && u.hasGlobalPrivilege(canAccessGlobalSettings),
                                (u, e) -> MultitenancyService.isMultitenancy() && u.hasGlobalPrivilege(canAccessGlobalSettings))
                        .additionalCss(!MultitenancyService.isMultitenancy() ? "d-none" : "")
// Replace with this lines to disable datasource selection in Organization creation form
//                            (u, e) -> e != null && HybridMultiTenantConnectionProvider.isMultitenancy() && u.hasGlobalPrivilege(canAccessGlobalSettings),
//                            (u, e) -> e != null && HybridMultiTenantConnectionProvider.isMultitenancy() && u.hasGlobalPrivilege(canAccessGlobalSettings))
    );

    FrontendMappingDefinition loggerForm = createFrontendMappingDefinition(LOGGER_FORM, canReadSupportData, canManageSupportData,
            a -> a  .text(BUFFER_SIZE_FIELD_)
                    .checkboxList(LOGGING_CLASSES_, f -> f.getDictionaryRepository().getLoggersDictionary()).additionalCss(TABLE_COMPACT_CSS)
    );

    FrontendMappingDefinition eventListenerForm = createFrontendMappingDefinition(EVENT_LISTENER_FORM, canReadBackend, canManageBackend,
            a -> a  .organizationSelect(ORGANIZATION_ID_)
                    .dropdown(EVENT_, EVENTS_)
                    .radioList(CONSUMER_, CONSUMERS_)
                    .text(STATIC_DATA_1_)
                    .text(STATIC_DATA_2_)
                    .text(STATIC_DATA_3_)
                    .text(STATIC_DATA_4_)

    );

    FrontendMappingDefinition frontendResourceForm = createFrontendMappingDefinition(FRONTENDRESOURCE, readFrontendResource, manageFrontendResource,
            a -> a  .text(NAME_)
                        .validate(v -> StringUtils.isBlank(v) ? "not.empty" : null)
                    .organizationSelect(ORGANIZATION_ID_)
                    .text(URL_PATH_)
                        .validate(v -> v.matches("[0-9a-z\\-/]*(?:\\.css|\\.js)?") ? null : "not.matching.urlpath.frontendreosurce")
                    .dropdown(REQUIRED_PRIVILEGE_, PRIVILEGES_, true)
                    .sectionWithDropdown(TYPE_, FRONTEND_RESOURCE_TYPE_)
                        //.valueType(FrontendResource.Type.class)
                        .additionalCss("frontendResourceType").validate(v -> v != null ? null : "not.empty")
                    .checkbox(INCLUDE_IN_SITEMAP_)
                    .customFieldType(DRAFT_CONTENT_,  f -> FrontendResourceForm.getCodeType(((ReflectionBasedEntityForm)f).dto.get(TYPE_)))
                    .valueSupplier(f -> {
                        FrontendResource ce = (FrontendResource) ((ReflectionBasedEntityForm) f).entity;
                        return ce == null ? "" : (ce.isDraft() ? ce.getDraftContent() : ce.getContent());
                    })
                    .validateForm((ReflectionBasedEntityForm f) ->
                        (f.dto.get(TYPE_) == FrontendResource.Type.CSS.name() && !f.dto.get(URL_PATH_).toString().endsWith(FrontendResource.Type.CSS.getExtension())) ||
                        (f.dto.get(URL_PATH_).toString().endsWith(FrontendResource.Type.CSS.getExtension()) && f.dto.get(TYPE_) != FrontendResource.Type.CSS.name()) ||
                        ((f.dto.get(TYPE_) == FrontendResource.Type.JS.name()) && !f.dto.get(URL_PATH_).toString().endsWith(FrontendResource.Type.JS.getExtension())) ||
                        (f.dto.get(URL_PATH_).toString().endsWith(FrontendResource.Type.JS.getExtension()) && (f.dto.get(TYPE_) != FrontendResource.Type.JS.name())) ?
                                Map.of(TYPE_, "incompatible.frontend-resource.types", URL_PATH_, "incompatible.frontend-resource.types") : null)
    );
    FrontendMappingDefinition uiComponentFrontendResourceForm = createFrontendMappingDefinition(FRONTENDRESOURCE, readFrontendResource, manageFrontendResource,
            a -> a  .text(URL_PATH_)
                    .codeHtml(CONTENT_)
                    .checkbox(IS_PUBLIC)
    );

    FrontendMappingDefinition uiComponentControllerEndpointForm = createFrontendMappingDefinition(CONTROLLER_ENDPOINT_FORM, readFrontendResource, manageFrontendResource,
            a -> a  .text(SUB_PATH)
                    .dropdown(HTTP_METHOD, HTTP_METHOD)
                    .sectionWithDropdown(RESPONSE_TYPE, RESPONSE_TYPE)
                    .codeWithAutocomplete(CODE)
                    .textarea(HTTP_HEADERS)
                    .textarea(MODEL_ATTRIBUTES)
    );

    FrontendMappingDefinition frontendResourcePageForm = createFrontendMappingDefinition(FRONTEND_RESOURCE_PAGE_FORM, readFrontendResource, manageFrontendResource,
            a -> a
                    .text(URL_PATH_)
                    .textarea(CONTENT_EDITABLE_)
    );

    FrontendMappingDefinition attributeFormNew = createFrontendMappingDefinition(ATTRIBUTE_DEFINITION_FORM, canSeeAttributes, canDefineAttributes,
            a -> a  .text(NAME_)
                    .text(DEFAULT_VALUE_)
                    .dropdown(LEVEL_, ATTRIBUTE_LEVEL_)
                    .checkbox(READ_ONLY_)
    );

    FrontendMappingDefinition attributeFormModify = createFrontendMappingDefinition(ATTRIBUTE_DEFINITION_FORM, canSeeAttributes, canDefineAttributes,
            a -> a  .text(NAME_).additionalPrivileges(canSeeAttributes, null)
                    .text(DEFAULT_VALUE_)
                    .dropdown(LEVEL_, ATTRIBUTE_LEVEL_).additionalPrivileges(canSeeAttributes, null)
                    .checkbox(READ_ONLY_)
    );

    FrontendMappingDefinition sendEventForm = createFrontendMappingDefinition(SEND_EVENT_FORM, canReadBackend, canManageBackend,
            a -> a  .dropdownNonDto(EVENT_, EVENTS_)
    );

    FrontendMappingDefinition sendEventInvoiceDto = createFrontendMappingDefinition(SEND_EVENT_FORM, canReadBackend, canManageBackend,
            a -> a  .dropdownNonDto(EVENT_, EVENTS_)
                    .text(SELLER_COMPANY_NAME_)
                    .text(SELLER_COMPANY_ADDRESS_LINE_)
                    .text(SELLER_COMPANY_ADDRESS_LINE_2)
                    .text(SELLER_COMPANY_COUNTRY_)
                    .text(SELLER_COMPANY_TAX_NO_)
                    .text(BUYER_COMPANY_NAME_)
                    .text(BUYER_COMPANY_ADDRESS_LINE_1_)
                    .text(BUYER_COMPANY_ADDRESS_LINE_2_)
                    .text(BUYER_COMPANY_COUNTRY_)
                    .text(BUYER_COMPANY_TAX_NO_)
                    .text(INVOICE_IDENTIFIER_)
                    .text(ITEM_)
                    .text(CURRENCY_)
                    .text(VALUE_)
                    .text(TAX_)
                    .text(CREATED_ON_)
                    .text(ORGANIZATION_ID_)
    );

    FrontendMappingDefinition sendEventPaymentDto = createFrontendMappingDefinition(SEND_EVENT_FORM, canReadBackend, canManageBackend,
            a -> a  .dropdownNonDto(EVENT_, EVENTS_)
                    .text(TOTAL_AMOUNT_)
                    .text(NET_AMOUNT_)
                    .text(TAX_AMOUNT_)
                    .text(PLAN_ID_)
                    .text(PLAN_NAME_)
                    .text(DESCRIPTION)
                    .text(STATUS_)
                    .text(CURRENCY_)
                    .text(ORGANIZATION_ID_)
    );

    FrontendMappingDefinition sendEventPlanDto = createFrontendMappingDefinition(SEND_EVENT_FORM, canReadBackend, canManageBackend,
            a -> a  .dropdownNonDto(EVENT_, EVENTS_)
                    .text(ORGANIZATION_ID_)
                    .text(PLAN_NAME_)
    );

    FrontendMappingDefinition sendEventSubscriptionDto = createFrontendMappingDefinition(SEND_EVENT_FORM, canReadBackend, canManageBackend,
            a -> a  .dropdownNonDto(EVENT_, EVENTS_)
                    .text(SUBSCRIPTION_ID_)
                    .text(NEXT_BILLING_)
                    .text(CURRENT_BILLING_START)
                    .text(CURRENT_BILLING_END)
                    .text(NEXT_AMOUNT_)
                    .text(PRICE_)
                    .text(PLAN_NAME_)
                    .text(PLAN_FULL_NAME_)
                    .text(SUBSCRIPTION_STATUS_)
                    .text(CURRENCY_)
                    .text(ORGANIZATION_ID_)
    );

    FrontendMappingDefinition sendEventFrontendResourceDto = createFrontendMappingDefinition(SEND_EVENT_FORM, canReadBackend, canManageBackend,
            a -> a  .dropdownNonDto(EVENT_, EVENTS_)
                    .text(NAME_)
                    .text(ORGANIZATION_ID_)
                    .text(URL_PATH_)
                    .text(CONTENT_)
                    .text(TEST_DATA_)
                    .text(REQUIRED_PRIVILEGE_)
                    .text(INCLUDE_IN_SITEMAP_)
                    .text(TYPE_)
    );

    FrontendMappingDefinition sendEventScheduledSchedulerDto = createFrontendMappingDefinition(SEND_EVENT_FORM, canReadBackend, canManageBackend,
            a -> a  .dropdownNonDto(EVENT_, EVENTS_)
                    .text(SCHEDULED_AT_)
                    .text(CRON_EXPRESSION_)
                    .text(EVENT_DATA_)
                    .text(ORGANIZATION_ID_)
    );

    FrontendMappingDefinition sendEventBasicUser = createFrontendMappingDefinition(SEND_EVENT_FORM, canReadBackend, canManageBackend,
            a -> a  .dropdownNonDto(EVENT_, EVENTS_)
                    .text(ID_)
                    .text(FIRST_NAME_)
                    .text(LAST_NAME_)
                    .text(EMAIL_)
    );

    FrontendMappingDefinition sendEventUserRoleDto = createFrontendMappingDefinition(SEND_EVENT_FORM, canReadBackend, canManageBackend,
            a -> a  .dropdownNonDto(EVENT_, EVENTS_)
                    .text(ID_)
                    .text(USER_ID_)
                    .text(ROLE_ID_)
                    .text(ORGANIZATION_ID_)
    );

    FrontendMappingDefinition sendEventNotificationDto = createFrontendMappingDefinition(SEND_EVENT_FORM, canReadBackend, canManageBackend,
            a -> a  .dropdownNonDto(EVENT_, EVENTS_)
                    .text(ATTACHMENT_URL_)
                    .text(MESSAGE_)
                    .text(SUBJECT_)
                    .text(NOTIFICATION_TYPE_)
                    .text(USER_ID_)
                    .text(ORGANIZATION_ID_)
                    .text(REQUIRED_PRIVILEGE_)
                    .checkbox(PROPAGATE)
    );

    FrontendMappingDefinition sendEventOrganizationDto = createFrontendMappingDefinition(SEND_EVENT_FORM, canReadBackend, canManageBackend,
            a -> a  .dropdownNonDto(EVENT_, EVENTS_)
                    .text(ID_)
                    .text(NAME_)
                    .checkbox(SETUP_TRIAL)
    );

    FrontendMappingDefinition organizationsApi = createFrontendMappingDefinition("organization", readOrgData, manageOrgData,
            a -> a.hidden(ID_)
                  .text(NAME_)
    );
}
