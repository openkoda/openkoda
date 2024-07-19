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

package com.openkoda.form;

public interface TemplateFormFieldNames {
    String NAME_= "name";
    String LABEL_= "label";
    String TYPE_ = "type";
    String ROLE_TYPES_ = "roleTypes";
    String PRIVILEGES_ = "privileges";
    String PRIVILEGE_GROUPS_ = "privilegeGroups";
    String EMAIL_ = "email";
    String FIRST_NAME_ = "firstName";
    String LAST_NAME_ = "lastName";
    String ROLE_NAME_ = "roleName";
    String GLOBAL_ROLE_NAME_ = "globalRoleName";
    String ORGANIZATION_ROLES_ = "organizationRoles";
    String GLOBAL_USER_ROLES_ = "globalRoles";
    String ENABLED_ = "enabled";
    String SINGLE_FIELD_TO_UPDATE = "singleFieldToUpdate";

    String BOOLEAN_VALUES_ = "booleanValues";
    String ORGANIZATION_ID_ = "organizationId";
    String SERVER_JS_ID_ = "serverJsId";
    String SERVER_JS_ = "serverJs";
    String CRON_EXPRESSION_ = "cronExpression";
    String EVENT_DATA_ = "eventData";
    String ON_MASTER_ONLY_ = "onMasterOnly";
    String BUFFER_SIZE_FIELD_ = "bufferSizeField";
    String LOGGING_CLASSES_ = "loggingClasses";
    String ALL_LOGGERS_ = "allLoggers";
    String EVENT_ = "event";
    String EVENTS_ = "events";
    String EVENTS_CLASSES_ = "eventClasses";
    String CONSUMER_ = "consumer";
    String CONSUMERS_ = "consumers";

    String STATIC_DATA_1_ = "staticData1";
    String STATIC_DATA_2_ = "staticData2";
    String STATIC_DATA_3_ = "staticData3";
    String STATIC_DATA_4_ = "staticData4";

    String URL_PATH_ = "urlPath";
    String REQUIRED_PRIVILEGE_ = "requiredPrivilege";
    String READ_PRIVILEGE = "readPrivilege";
    String WRITE_PRIVILEGE = "writePrivilege";
    String FRONTEND_RESOURCE_TYPE_ = "frontendResourceType";
    String TITLE= "title";
    String CATEGORY= "category";
    String CATEGORY_SELECT= "categorySelect";
    String IMAGE_URL= "imageUrl";
    String MIN_IMAGE_URL= "minImageUrl";
    String SEO_IMAGE_ALT= "seoImageAlt";
    String SEO_META_DESCRIPTION= "seoMetaDescription";
    String AUTHORS= "authors";
    String AUTHORS_SELECT= "authorsSelect";
    String READING_TIME = "readingTime";
    String PUBLISHED = "published";
    String INCLUDE_IN_SITEMAP_ = "includeInSitemap";
    String EMBEDDABLE_ = "embeddable";
    String CONTENT_ = "content";
    String DRAFT_CONTENT_ = "draftContent";
    String CONTENT_EDITABLE_ = "contentEditable";
    String JS_CODE_ = "jsCode";
    String TEST_DATA_ = "testData";
    String TEST_BUTTON_ = "testButton";
    String DEFAULT_VALUE_ = "defaultValue";
    String LEVEL_ = "level";
    String ATTRIBUTE_LEVEL_ = "attributeLevel";
    String READ_ONLY_ = "readOnly";
    String ID_ = "id";
    String USER_ID_ = "userId";
    String ROLE_ID_ = "roleId";
    String VALUE_ = "value";
    String TOTAL_AMOUNT_ = "totalAmount";
    String NET_AMOUNT_ = "netAmount";
    String TAX_AMOUNT_ = "taxAmount";
    String IMAGES_ = "images";
    String ACCESS_LEVEL = "accessLevel";
    String ACCESS_LEVELS = "accessLevels";

    String PLAN_ID_ = "planId";
    String PLAN_NAME_ = "planName";
    String DESCRIPTION = "description";
    String STARTED_ON_ = "startedOn";
    String PAID_ON = "paidOn";
    String STATUS_ = "status";
    String CURRENCY_ = "currency";
    String SCHEDULED_AT_ = "scheduledAt";
    String ATTACHMENT_URL_ = "attachmentURL";
    String MESSAGE_ = "message";
    String SUBJECT_ = "subject";
    String NOTIFICATION_TYPE_ = "notificationType";
    String PROPAGATE = "propagate";

    String SUBSCRIPTION_ID_ = "subscriptionId";
    String NEXT_BILLING_ = "nextBilling";
    String CURRENT_BILLING_START = "currentBillingStart";
    String CURRENT_BILLING_END = "currentBillingEnd";
    String NEXT_BILLING_DATE = "nextBillingDate";
    String BILLING_START_DATE = "billingStartDate";
    String BILLING_END_DATE = "billingEndDate";
    String NEXT_AMOUNT_ = "nextAmount";
    String PRICE_ = "price"; //PaymentSubscriptionController.java
    String PLAN_FULL_NAME_ = "planFullName";
    String SUBSCRIPTION_STATUS_ = "subscriptionStatus";

    String SELLER_COMPANY_NAME_ = "sellerCompanyName";
    String SELLER_COMPANY_ADDRESS_LINE_ = "sellerCompanyAddressLine1";
    String SELLER_COMPANY_ADDRESS_LINE_2 = "sellerCompanyAddressLine2";
    String SELLER_COMPANY_COUNTRY_ = "sellerCompanyCountry";
    String SELLER_COMPANY_TAX_NO_ = "sellerCompanyTaxNo";

    String BUYER_COMPANY_NAME_ = "buyerCompanyName";
    String BUYER_COMPANY_ADDRESS_LINE_1_ = "buyerCompanyAddressLine1";
    String BUYER_COMPANY_ADDRESS_LINE_2_ = "buyerCompanyAddressLine2";
    String BUYER_COMPANY_COUNTRY_ = "buyerCompanyCountry";
    String BUYER_COMPANY_TAX_NO_ = "buyerCompanyTaxNo";

    String INVOICE_IDENTIFIER_ = "invoiceIdentifier";
    String ITEM_ = "item";
    String TAX_ = "tax";
    String CREATED_ON_ = "createdOn";

    String ORGANIZATION_NAME_ = "organizationName";
    String TODO_LIST_URL_ = "toDoListUrl";
    String WEBHOOK_URL_ = "webhookUrl";
    String GITHUB_REPO_OWNER_ = "gitHubRepoOwner";
    String GITHUB_REPO_NAME_ = "gitHubRepoName";

    String PROJECT_NAME_ = "projectName";

    String TRELLO_API_KEY_ = "trelloApiKey";
    String TRELLO_API_TOKEN_ = "trelloApiToken";
    String TRELLO_BOARD_NAME_ = "trelloBoardName";
    String TRELLO_LIST_NAME_ = "trelloListName";

    String URL_ = "url";

    String LANGUAGE = "language";
    String LANGUAGES = "languages";
    String ASSIGNED_DATASOURCE_ = "assignedDatasource";
    String SETUP_TRIAL = "setupTrial";

    String SUB_PATH = "subPath";
    String CODE = "code";
    String HTTP_HEADERS = "httpHeaders";
    String HTTP_METHOD = "httpMethod";
    String MODEL_ATTRIBUTES = "modelAttributes";
    String RESPONSE_TYPE = "responseType";
    String FRONTEND_RESOURCE_ID = "frontendResourceId";
    String REGISTER_AS_AUDITABLE = "registerAsAuditable";
    String REGISTER_API_CRUD_CONTROLLER = "registerApiCrudController";
    String REGISTER_HTML_CRUD_CONTROLLER = "registerHtmlCrudController";
    String SHOW_ON_ORGANIZATION_DASHBOARD = "showOnOrganizationDashboard";
    String TABLE_COLUMNS = "tableColumns";
    String FILTER_COLUMNS = "filterColumns";
    String FILTER_AVAILABLE_COLUMNS = "filterAvailableColumns";

    String EMAIL_HOST = "host";
    String EMAIL_PORT = "port";
    String EMAIL_USERNAME = "username";
    String EMAIL_PROTOCOL = "protocol";
    String EMAIL_PASSWORD = "password";
    String EMAIL_FROM = "from";
    String EMAIL_SSL = "ssl";
    String EMAIL_SMTP_AUTH = "smtpAuth";
    String EMAIL_STARTTLS = "starttls";
    String EMAIL_REPLY_TO = "replyTo";
    String EMAIL_MAILGUN_API_KEY = "mailgunApiKey";

    String QUERY = "query";
    String LOGO_ID = "logoId";
    String PERSONALIZE_DASHBOARD = "personalizeDashboard";
    String MAIN_BRAND_COLOR = "mainBrandColor";
    String SECOND_BRAND_COLOR = "secondBrandColor";
}
