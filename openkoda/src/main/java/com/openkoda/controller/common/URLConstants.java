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

package com.openkoda.controller.common;

/**
 * Handy constants to define URLs in the application
 *
 * @author Arkadiusz Drysch (adrysch@stratoflow.com)
 * 
 */
public interface URLConstants {

    String NUMBERREGEX = "[0-9]+";
    String _HOME = "/home";
    String SEND = "send";
    //String CREATE_EVENT = "createEvent";
    String REDIRECT = "redirect:";
    String FORWARD = "forward:";
    String _XML_HEADER = "Accept=application/xml";
    String _XML_EXTENSION = ".xml";
    String XML = "xml";
    String _HTML = "/html";
    String _ALL = "/all";
    String _SAVE = "/save";
    String _EXIT = "/exit";
    String _ZIP = "/zip";
    String _YAML = "/yaml";
    String COMPONENT = "component";
    String _COMPONENT = "/" + COMPONENT;
    String EXPORT = "export";
    String _EXPORT = "/" + EXPORT;
    String _EXPORT_YAML = _EXPORT + _YAML;
    String EXPORT_YAML = EXPORT + _YAML;

    String IMPORT = "import";
    String _IMPORT = "/" + IMPORT;

    String _PUBLISH = "/publish";
    String _CLEAR = "/clear";
    String _RELOAD = "/reload";
    String _RELOAD_TO_DRAFT = "/reload-to-draft";
    String _PUBLIC = "/public";
    String REGISTER = "register";
    String _REGISTER = "/" + REGISTER;
    String _ATTEMPT = "/attempt";
    String _SITEMAP_INDEX = "/sitemap_index";
    String _SITEMAP = "/sitemap";
    String _PAGES_SITEMAP = "/pages-sitemap";
    String _GENERAL_SITEMAP = "/general-sitemap";
    String _RECOVERY = "/recovery";
    String _CHANGE = "/change";
    String _VERIFY = "/verify";
    String _LOGIN = "/login";
    String FORM = "form";
    String _FORM = "/" + FORM;
    String OPENKODA_MODULE = "openkodaModule";
    String _OPENKODA_MODULE = "/" + OPENKODA_MODULE;
    String _RULE = "/rule";
    String _RULE_LINE = "/rule-line";
    String _LOGOUT = "/logout";
    String _ANY = "/**";
    String ORGANIZATION = "organization";
    String EMAIL_CONFIG = "emailConfig";
    String SETTINGS = "settings";
    String TEST = "test";
    String INVITE = "invite";
    String FORMS = "forms";
    String DASHBOARD = "dashboard";
    String REGISTERED_CRUDS = "registered-cruds";
    String _REGISTERED_CRUDS = "/" + REGISTERED_CRUDS;
    String PROFILE = "profile";
    String MEMBER = "member";
    String FILE = "file";
    String HISTORY = "history";
    String EVENTS = "events";
    String ADMIN = "admin";
    String LOGS = "logs";
    String USER = "user";
    String TOKEN = "token";
    String VERIFY_TOKEN = "verifyToken";
    String KEY = "key";
    String PASSWORD = "password";
    String FRONTENDRESOURCE = "frontendresource";
    String _FRONTENDRESOURCE = "/" + FRONTENDRESOURCE;

    String UI_COMPONENT = "uiComponent";
    String _UI_COMPONENT = "/" + UI_COMPONENT;
    String WEBENDPOINT = "webEndpoint";
    String PAGEBUILDER = "pageBuilder";
    String CONTROLLER_ENDPOINT = "controllerEndpoint";
    String SERVERJS = "serverjs";
    String ROLE = "role";
    String PRIVILEGE = "privilege";
    String PRIVILEGES = "privileges";
    String MODULE = "module";
    String CONTENT = "content";
    String TYPE = "type";
    String EVENTLISTENER = "eventListener";
    String CUSTOM_EVENT = "customEvent";
    String SCHEDULER = "scheduler";
    String SPOOF = "spoof";
    String ENTITY = "entity";
    String _ENTITY = "/" + ENTITY;
    String NEW = "new";
    String _NEW = "/" + NEW;
    String ALL = "all";
    String AUDIT = "audit";
    String PREVIEW = "preview";
    String _SETTINGS = "/" + SETTINGS;
    String _AUDIT = "/" + AUDIT;
    String _MODULE = "/" + MODULE;
    String MODULENAME = "moduleName";
    String _MODULENAME = "/{" + MODULENAME + "}";
    String ORGANIZATIONID = "organizationId";
    String _ORGANIZATIONID = "/{" + ORGANIZATIONID + "}";
    String ID = "id";
    String _ID = "/{" + ID + ":" + NUMBERREGEX + "}";
    String OBJID = "objid";
    String _OBJID = "/{" + OBJID + "}";
    String USERID = "userId";
    String _USERID = "/{" + USERID + "}";
    String _PROFILE = "/" + PROFILE;
    String _MEMBER = "/" + MEMBER;
    String _FILE = "/" + FILE;
    String _WEBENDPOINT = "/" + WEBENDPOINT;
    String _PAGEBUILDER = "/" + PAGEBUILDER;
    String _SERVERJS = "/" + SERVERJS;
    String _ORGANIZATION = "/" + ORGANIZATION;
    String _EMAIL_CONFIG = "/" + EMAIL_CONFIG;
    String _DASHBOARD = "/" + DASHBOARD;
    String _HISTORY = "/" + HISTORY;
    String _EVENTS = "/" + EVENTS;
    String _TEST = "/" + TEST;
    String _INVITE = "/" + INVITE;
    String _REMOVE = "/remove";
    String _INTERRUPT = "/interrupt";
    String _ADMIN = "/" + ADMIN;
    String _LOGS = "/" + LOGS;
    String _USER = "/" + USER;
    String _ROLE = "/" + ROLE;
    String _PRIVILEGE = "/" + PRIVILEGE;
    String _CONTENT = "/" + CONTENT;
    String _UPLOAD = "/upload";
    String _EVENTLISTENER = "/" + EVENTLISTENER;
    String _CUSTOM_EVENT = "/" + CUSTOM_EVENT;
    //String _CREATE_EVENT = "/" + CREATE_EVENT;
    String _SCHEDULER = "/" + SCHEDULER;
    String _SPOOF = "/" + SPOOF;
    String _NEW_SETTINGS = _NEW + _SETTINGS;
    String _ID_SETTINGS = _ID + _SETTINGS;
    String _ID_REMOVE = _ID + _REMOVE;
    String _HTML_ORGANIZATION = _HTML + _ORGANIZATION;
    String _HTML_USER = _HTML + _USER;
    String _HTML_ROLE = _HTML + _ROLE ;
    String _HTML_PRIVILEGE = _HTML + _PRIVILEGE ;
    String _USER_SETTINGS = "/" + USER + _USERID + _SETTINGS;
    String _PREVIEW = "/" + PREVIEW;
    String VIEW = "view";
    String _VIEW = "/" + VIEW;

    String _MODULE_MODULENAME_SETTINGS = _MODULE + _MODULENAME + _SETTINGS;
    String _MODULE_MODULENAME_USER_SETTINGS = _MODULE + _MODULENAME + _USER_SETTINGS;

    String _ORGANIZATION_ORGANIZATIONID = _ORGANIZATION + _ORGANIZATIONID;

    String _ORGANIZATION_ORGANIZATIONID_MODULE = _ORGANIZATION_ORGANIZATIONID + _MODULE;
    String _ORGANIZATION_ORGANIZATIONID_MODULE_MODULENAME = _ORGANIZATION_ORGANIZATIONID_MODULE + _MODULENAME;
    String _ORGANIZATION_ORGANIZATIONID_MODULE_MODULENAME_SETTINGS = _ORGANIZATION_ORGANIZATIONID_MODULE_MODULENAME + _SETTINGS;
    String _ORGANIZATION_ORGANIZATIONID_MODULE_MODULENAME_USER_SETTINGS = _ORGANIZATION_ORGANIZATIONID_MODULE_MODULENAME + _USER_SETTINGS;

    String _HTML_ORGANIZATION_ORGANIZATIONID = _HTML_ORGANIZATION + _ORGANIZATIONID;
    String _PASSWORD = "/" + PASSWORD;
    String _PRIVILEGES = "/" + PRIVILEGES;
    String _DOWNLOAD = "/download";
    String _SEARCH = "/search";
    String _SELECTED = "/selected";
    String ATTRIBUTE = "attribute";
    String _ATTRIBUTE = "/" + ATTRIBUTE;
    String NOTIFICATION = "notification";
    String _NOTIFICATION = "/" + NOTIFICATION;
    String _CONNECT = "/connect";
    String _MARK_READ = "/mark-read";
    String SYSTEM_HEATH = "system-health";
    String _SYSTEM_HEATH = "/" + SYSTEM_HEATH;
    String VALIDATE = "validate";
    String _VALIDATE = "/" + VALIDATE;
    String THREAD = "thread";
    String _THREAD = "/" + THREAD;
    String _THREAD_ID_INTERRUPT = _THREAD + _ID + _INTERRUPT;
    String _THREAD_ID_REMOVE = _THREAD + _ID_REMOVE;
    String _LOCAL = "/local";
    String _RESEND = "/resend";
    String _SEND = "/" + SEND;
    String _EMIT = "/emit";
    String _VERIFICATION = "/verification";
    String _COMPONENTS = "/components";
    String INTEGRATIONS = "integrations";
    String _INTEGRATIONS = "/" + INTEGRATIONS;

    String FRONTENDRESOURCEREGEX = "[0-9a-zA-Z\\-\\/]*\\@?(?:\\.css|\\.js|\\.xml|\\.txt|\\.csv|\\.json|\\.html)?";
    String FRONTENDRESOURCE_ORGID_PARAM_REGEX = "(?:\\?organizationId=?.*)?";
    String EMAILRESOURCE_DISCRIMINATOR = "@";

    String FRONTENDRESOURCE_AUTH_PARAMS_REGEX = "(\\?(.*=?.*)?(\\&.*=?.*)?)?";
    String LANGUAGEPREFIX = "pl";
    String EXCLUDE_SWAGGER_UI_REGEX = "^(?!.*swagger-ui)";
    String URL_WITH_DASH_REGEX = "[0-9a-zA-Z\\-]+";
    String URL_REGEX = "[hH][Tt][Tt][Pp][Ss]?://.*";
    String IP_COMMA_SEPARATED_LIST = "(((([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])\\.){3}([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])(\\/([4-9]|[12][0-9]|3[0-2]))?)([,]|$))*";
    String LOWERCASE_NUMERIC_UNDERSCORE_REGEXP = "[a-z0-9_]+$";

    String DEBUG_MODEL = "debugModel";
    String DRAFT = "draft";
    String _DRAFT = "/" + DRAFT;
    String COPY = "copy";
    String _COPY = "/" + COPY;
    String RESOURCE = "resource";
    String _RESOURCE = "/" + RESOURCE;
    String LIVE = "live";
    String _LIVE = "/" + LIVE;

    String _APIKEY = "/apikey";
    String _TOKEN = "/token";
    String __T_ = "/_t_";
    String _TOKENREFRESHER = "/tokenrefresher";
    String _REFRESH = "/refresh";
    String _RESET = "/reset";
    String _API = "/api";
    String _V1 = "/v1";
    String _V2 = "/v2";
    String _AUTH = "/auth";

    String _API_AUTH = _API + _AUTH;
    String _API_AUTH_ANT_EXPRESSION = _API_AUTH + "/**";
    String _TOKEN_PREFIX_ANT_EXPRESSION = __T_ + "**/**";

    String _API_V1 = _API + _V1;
    String _API_V1_ANT_EXPRESSION = _API_V1 + "/**";
    String _API_V1_ORGANIZATION = _API_V1 + _ORGANIZATION;

    String _API_V2 = _API + _V2;
    String _API_V2_ANT_EXPRESSION = _API_V2 + "/**";
    String _API_V2_ORGANIZATION = _API_V2 + _ORGANIZATION;
    String _API_V2_ORGANIZATION_ORGANIZATIONID = _API_V2_ORGANIZATION + _ORGANIZATIONID;

    String API_TOKEN = "api-token";
    String EXTERNAL_SESSION_ID = "esid";
    String RECAPTCHA_TOKEN = "g-recaptcha-response";
    String CAPTCHA_VERIFIED = "captchaVerified";

    String AFFILIATION_CODE = "affiliationCode"; //TODO change to affiliationcode or remove if not used
    String _AFFILIATION_CODE = "/" + AFFILIATION_CODE;
    String AFFILIATION_EVENT = "affiliationEvent";  //TODO change to affiliationevent or remove if not used
    String _AFFILIATION_EVENT = "/" + AFFILIATION_EVENT;

    String CI = "ci";
    String _CI = "/" + CI;
    String CN = "cn";
    String _CN = "/" + CN;
    
    String FILE_ASSET = "/file-asset-";
    String AI = "ai";
    String _AI = "/" + AI;
    String PROMPT = "prompt";
    String _PROMPT = "/" + PROMPT;
    String QUERY = "query";
    String _QUERY = "/" + QUERY;

    String _CSV = "/csv";
    String _REPORT = "/report";
    String QUERY_REPORT = "queryreport";
    String _QUERY_REPORT = "/" + QUERY_REPORT;

}
