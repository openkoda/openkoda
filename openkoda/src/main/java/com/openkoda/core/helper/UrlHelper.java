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

package com.openkoda.core.helper;

import com.openkoda.controller.common.URLConstants;
import com.openkoda.core.multitenancy.TenantResolver;
import com.openkoda.model.FrontendResource;
import com.openkoda.model.Organization;
import com.openkoda.model.common.LongIdEntity;
import com.openkoda.model.event.EventListenerEntry;
import com.openkoda.model.file.File;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.security.SecureRandom;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 
 *
 * @author Arkadiusz Drysch (adrysch@stratoflow.com)
 * 
 */
@Component("url")
@Primary
public class UrlHelper implements URLConstants, ReadableCode {

    private static UrlHelper instance;

    private final static Random random = new SecureRandom();

    private static final Pattern htmlOrganizationPath = Pattern.compile(URLConstants._HTML_ORGANIZATION + "/([0-9]+).*$");
    private static final Pattern entityKeyPath = Pattern.compile(URLConstants._HTML + "(" + _ORGANIZATION + "/[0-9]+)?/([A-Za-z]+)/.*$");
    private static final Pattern organizationIdAndEntityKeyPath = Pattern.compile(URLConstants._HTML + "(" + _ORGANIZATION + "/([0-9]+))?/([A-Za-z]+)?(/.*)?$");


    @Value("${base.url:http://localhost:8080}")
    private String baseUrl;

    @Value("${logo.image.href:/html/organization/%s/dashboard}")
    private String logoImageHref;
    @Value("${logo.image.href.global:/html/organization/all}")
    private String logoImageHrefGlobal;

    @PostConstruct void init() {
        instance = this;
    }

    public static UrlHelper getInstance() {
        return instance;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public static String getBaseUrlOrEmpty() {
        return instance == null ? "" : instance.baseUrl;
    }

    public String entityBase(Long organizationId, String entityKey) {
        return organizationId == null || entityKey.equals(ORGANIZATION) ? _HTML + "/" + entityKey : _HTML + _ORGANIZATION + "/" + organizationId + "/" + entityKey;
    }

    public String entityBase(String entityKey) {
        return entityBase(null, entityKey);
    }

    public String entityBase(String entityKey, Long id) {
        return entityBase(null, entityKey) + "/" + id;
    }

    public String entityBase(Long organizationId, String entityKey, Long id) {
        return entityBase(organizationId, entityKey) + "/" + id;
    }

    public String operation(Long organizationId, String entityKey, Long entityId, String operation) {
        String base = entityBase(organizationId, entityKey);
        return entityId == null ? base + _NEW + operation : base + "/" + entityId + operation;
    }

    public String operation(Long organizationId, String entityKey, String operation) {
        return operation(organizationId, entityKey, null, operation);
    }

    public String operation(String entityKey, Long entityId, String operation) {
        return operation(null, entityKey, entityId, operation);
    }

    public String operation(String entityKey, String operation) {
        return entityBase(entityKey) + operation;
    }

    public String form(Long organizationId, String entityKey, Long entityId) {
        return operation(organizationId, entityKey, entityId, _SETTINGS);
    }

    public String remove(Long organizationId, String entityKey, Long entityId) {
        return operation(organizationId, entityKey, entityId, _REMOVE);
    }

    public String form(Organization organization, String entityKey, LongIdEntity entity) {
        return form(organization == null ? null : organization.getId(), entityKey, entity == null ? null : entity.getId());
    }

    public String form(String entityKey) {
        return form((Long)null, entityKey, (Long)null);
    }

    public String form(String entityKey, Long entityId) {
        return form(null, entityKey, entityId);
    }

    public String form(String entityKey, LongIdEntity entity) {
        return form(null, entityKey, entity);
    }

    public String form(Long organizationId, String entityKey) {
        return form(organizationId, entityKey, null);
    }

    public String form(Organization organization, String entityKey) {
        return form(organization, entityKey, null);
    }

    public String all(Long organizationId, String entityKey) {
        return entityBase(organizationId, entityKey) + _ALL;
    }

    public String all(Organization organization, String entityKey) {
        return all(organization == null ? null : organization.getId(), entityKey);
    }

    public String all(String entityKey) {
        return all((Long)null, entityKey);
    }

    //    ORGANIZATION URLS
    public String organizationBase(long id) {
        return _HTML + _ORGANIZATION + "/" + id;
    }

    public String base() {
        return _HTML;
    }

    public String allOrganizations() {
        return all(ORGANIZATION);
    }
    public String newOrganization() {
        return form(ORGANIZATION);
    }
    public String removeOrganization(Long organizationId) {
        return organizationBase(organizationId) + _ENTITY + _REMOVE;
    }
    public String organizationSettings(long id) { return form(id, ORGANIZATION, id); }
    public String organizationDashboard(long id) { return operation(id, ORGANIZATION, id, _DASHBOARD); }
    public String organizationModifyMemberRole(long id, long userId) {
        return operation(id, ORGANIZATION, id, _MEMBER) + "?userId=" + userId;
    }
    public String logoImageUrl(Long organizationId) { return organizationId == null ? logoImageHrefGlobal : String.format(logoImageHref, organizationId);}

    public String resetApiKey(long id) {
        return operation(USER, id, _SETTINGS + _APIKEY);
    }

//    USER URLS

    public String userBase(long id) {
        return entityBase(USER, id);
    }

    public String users() {
        return all(USER);
    }

    public String userProfile(long id) {
        return operation(USER, id, _PROFILE);
    }

    public String fileContent(long orgId, long fileId) {
        return operation(orgId, FILE, fileId, _CONTENT);
    }

    public String userSettings(long id) {
        return form(USER, id);
    }

    public String resetPassword() {
        return _PASSWORD + _RECOVERY;
    }

    public String spoof(long id) {
        return operation(USER, id, _SPOOF);
    }

    //TODO
    public String exitSpoof() {
        return operation(USER, _SPOOF + _EXIT);
    }

//    ROLE URLS

    public String roleBase(long id) {
        return entityBase(ROLE, id);
    }

    public String allRoles() {
        return all(ROLE);
    }

    public String newRole() {
        return form(ROLE);
    }

    public String roleSettings(long id) {
        return operation(ROLE, id, _SETTINGS);
    }

    public String rolePrivileges(long id) {
        return operation(ROLE, id, _PRIVILEGES);
    }

//    HISTORY URLS

    public String allAudit() {
        return all(AUDIT);
    }
    public String allAudit(String search) {
        return all(AUDIT) + "?audit_search=" + encode(search);
    }

    public String organizationHistory(long id) {
        return operation(id, ORGANIZATION, id, _HISTORY);
    }

    public String downloadAuditContent(long id) {
        return operation(AUDIT, id, _CONTENT);
    }

//    MODULE URLS

    public String allModules() {
        return all(MODULE);
    }

    public String allUserOrgModules(long orgId, long userId) {
        return operation(orgId, USER, userId, _MODULE + _ALL);
    }

    public String moduleSettings(String moduleName, Long userId, Long orgId) {
        if (userId != null && orgId != null) {
            return userOrganizationModuleSettings(moduleName, userId, orgId);
        } else if (userId != null) {
            return userGlobalModuleSettings(moduleName, userId);
        } else if (orgId != null) {
            return organizationModuleSettings(moduleName, orgId);
        } else {
            return globalModuleSettings(moduleName);
        }
    }

    public String globalModuleSettings(String moduleName) {
        return operation(MODULE, "/" + moduleName + _SETTINGS);
    }

    //TODO
    public String userGlobalModuleSettings(String moduleName, long userId) {
        return _HTML + _MODULE + "/" + moduleName + _USER + "/" + userId + _SETTINGS;
    }

    public String organizationModuleBase(String moduleName, long orgId) {
        return entityBase(orgId, MODULE) + "/" + moduleName;
    }

    public String organizationModuleSettings(String moduleName, long orgId) {
        return organizationModuleBase(moduleName, orgId) + _SETTINGS;
    }

    public String userOrganizationModuleSettings(String moduleName, long userId, long orgId) {
        return organizationModuleBase(moduleName, orgId) + _USER + "/" + userId + _SETTINGS;
    }

    public String module(long organizationId, String moduleName) {
        return organizationBase(organizationId) + "/" + "module-" + moduleName;
    }

    public String organizationModule(long id, String module) {
        return module(id, module);
    }

//    FRONTEND RESOURCE URLS

    public String frontendResourceEntry(String name) {
        return operation(FRONTENDRESOURCE, "/" + name);
    }

    public String allFrontendResource() {
        return all(FRONTENDRESOURCE);
    }
    public String allFiles() {
        return all(FILE);
    }

    public String deleteFrontendResource(long frontendResourceId) {
        return operation(FRONTENDRESOURCE,  frontendResourceId, _REMOVE);
    }
    public String frontendResourceSettings(long frontendResourceId) {
        return form(FRONTENDRESOURCE, frontendResourceId);
    }

    public String publishFrontendResource(long frontendResourceId) {
        return operation(FRONTENDRESOURCE,  frontendResourceId, _PUBLISH);
    }

    public String clearFrontendResource(long frontendResourceId) {return operation(FRONTENDRESOURCE,  frontendResourceId, _CLEAR); }
    public String reloadFrontendResource(long frontendResourceId) {return operation(FRONTENDRESOURCE,  frontendResourceId, _RELOAD); }
    public String publishAllFrontendResource() {return all(FRONTENDRESOURCE) + _PUBLISH; }
    public String clearAllFrontendResource() {return all(FRONTENDRESOURCE) + _CLEAR; }
    public String reloadFrontendResourceToDraft(long frontendResourceId) {return _HTML + _FRONTENDRESOURCE + "/" + frontendResourceId + _RELOAD_TO_DRAFT; }

    public String newFrontendResourceEntry() {
        return form(FRONTENDRESOURCE);
    }

    public String zipFrontendResource() {
        return operation(FRONTENDRESOURCE, _ZIP);
    }
//    FRONTEND ELEMENTS

    public String allUIComponents() {
        return all(WEBENDPOINT);
    }

    public String newUIComponent() {
        return form(WEBENDPOINT);
    }

    public String UIComponentSettings(long UIComponentId) {
        return form(WEBENDPOINT, UIComponentId);
    }

//    FORM URLS
    public String allForm() {
        return all(FORM);
    }


    //    SERVER JS URLS
    public String allServerJs() {
        return all(SERVERJS);
    }
    public String allPageBuilder() {
        return all(PAGEBUILDER);
    }

    public String allThreads() {
        return entityBase(THREAD);
    }

    public String interruptThread(long id) {
        return operation(THREAD, id, _INTERRUPT);
    }

    public String removeThread(long id) {
        return operation(THREAD, id, _REMOVE);
    }

//    EVENT LISTENER URLS
    public String allEventListeners(){
        return all(EVENTLISTENER);
    }

    public String newEventListener(){
        return form(EVENTLISTENER);
    }

    public String eventListenerSettings(long eventListenerId){
        return form(EVENTLISTENER, eventListenerId);
    }

    public String sendEvent(){
        return operation(EVENTLISTENER, _SEND);
    }

//    Admin Dashboard

    public String adminDashboard() {
        return _HTML + _DASHBOARD;
    }

//    SCHEDULER URLS

    public String allSchedulers(){
        return all(SCHEDULER);
    }
    public String newScheduler(){
        return form(SCHEDULER);
    }

    public String schedulerSettings(long schedulerId){
        return form(SCHEDULER, schedulerId);
    }

//    LOGS URLS

    public String allLogs() {
        return all(LOGS);
    }

    public String downloadLogs() {
        return operation(LOGS, _DOWNLOAD);
    }

    public String logsSettings(){
        return operation(LOGS, _SETTINGS);
    }


//   NOTIFICATIONS

    public String notificationsAll(long userId, Long organizationId){
        return operation(organizationId, NOTIFICATION, userId, _ALL);
    }

    public String markNotificationsAsRead(long userId, Long organizationId, String unreadNotificationsListString) {
        return operation(organizationId, NOTIFICATION, userId, _MARK_READ + "?unreadNotifications=" + unreadNotificationsListString);
    }

    public String markAllNotificationsAsRead(long userId, Long organizationId) {
        return operation(organizationId, NOTIFICATION, userId, _ALL + _MARK_READ);
    }

//    SYSTEM HEALTH
    public String systemHealth() {
        return entityBase(SYSTEM_HEATH);
    }

//   AFFILIATION

    public String affiliationCodeAll(long orgId) {
        return all(orgId, AFFILIATION_CODE);
    }

    public String affiliationEventAll(long orgId, String searchParam) {
        return all(orgId, AFFILIATION_EVENT) + "/?obj_search=" + searchParam;
    }

    public String getAffiliationLink(String affiliationCode) {
        return baseUrl + "?aff_code=" + affiliationCode;
    }

//  YAML EXPORT

    public String exportAllYamlResources(){
        return operation(ORGANIZATION, _EXPORT_YAML + _ALL);
    }
    public String exportAllYamlResourcesForOrg(long id) {
        return operation(ORGANIZATION, id, _EXPORT_YAML + _ALL);
    }
    public String yamlAllFrontendResources(){
        return operation(FRONTENDRESOURCE, _EXPORT_YAML);
    }

    public String yamlAllUiComponents(){
        return operation(UI_COMPONENT,  _EXPORT_YAML);
    }

    public String yamlAllFormResources(){
        return operation(FORM, _EXPORT_YAML);
    }
    public String yamlAllServerJsResources(){
        return operation(SERVERJS, _EXPORT_YAML);
    }

    public String yamlAllEventResources(){
        return operation(EVENTLISTENER, _EXPORT_YAML);
    }

    public String yamlAllSchedulerResources(){
        return operation(SCHEDULER, _EXPORT_YAML);
    }

    public String yamlFrontendResource(long id){
        return operation(FRONTENDRESOURCE, id, _EXPORT_YAML);
    }
    public String yamlServerJs(long id){
        return operation(SERVERJS, id, _EXPORT_YAML);
    }
    public String yamlForm(long id){
        return operation(FORM, id, _EXPORT_YAML);
    }
    public String yamlEventListener(long id){
        return operation(EVENTLISTENER, id, _EXPORT_YAML);
    }
    public String yamlScheduler(long id){
        return operation(SCHEDULER, id, _EXPORT_YAML);
    }

//    OTHER

    public String joinStrings(String ... strings){
        return String.join(", ", strings).replaceAll("(, |)null", "");
    }

    public String getEventListenerSignature(EventListenerEntry eventListenerEntry){

        return eventListenerEntry.getConsumerClassName(true) + "::" + eventListenerEntry.getConsumerMethodName() + "(" + eventListenerEntry.getEventObjectType(true) + ")";
    }

    public boolean isAsc(PageImpl page, String property) {
        Sort.Direction direction = getDirection(page, property);
        return Sort.Direction.ASC.equals(direction);
    }

    public String otherDirection(Sort.Direction direction) {
        return (Sort.Direction.DESC.equals(direction)) ? Sort.Direction.ASC.name() : Sort.Direction.DESC.name();
    }

    public String pageSort(PageImpl page, String property) {
        if (property == null) {
            return "id,ASC";
        }

        boolean orderIsNull = isOrder(page, property);

        if (isOrder(page, property)) {
            return property + "," + otherDirection(page.getSort().getOrderFor(property).getDirection());
        }

        return property + ",ASC";
    }

    public boolean isOrder(PageImpl page, String property) {
        return getOrder(page, property) != null;
    }

    public Sort.Direction getDirection(PageImpl page, String property) {
        Sort.Order order = getOrder(page, property);
        return order == null ? null : order.getDirection();
    }

    public Sort.Order getOrder(PageImpl page, String property) {
        boolean pageIsNull = ( page == null );
        boolean sortIsNull = pageIsNull || ( page.getSort() == null );
        Sort.Order order = sortIsNull ? null : ( page.getSort().getOrderFor(property) );
        return order;
    }

    public String pageableParams(String property, String qualifier, PageImpl page, String search) {
        if (page == null) {
            return "";
        }
        return String.format("?%s_page=%d&%s_size=%d&%s_sort=%s&%s_search=%s", qualifier, page.getNumber(), qualifier, page.getSize(), qualifier, pageSort(page, property), qualifier, (search == null ? "" : search));
    }

    public String createThymeleafExpressionForPagination(String qualifer ,String pageParam){
        return "@{${#ctx.springRequestContext.requestUri}(${qualifier} + '_page' =" + pageParam +" , ${qualifier} + '_size' = ${page.size}, ${qualifier} + '_sort' =" + "${param. " + qualifer + "_sort}, ${qualifier} + '_search' =" + "${param." + qualifer + "_search})}";
    }

    public static String getSearchForParamPrefix(HttpServletRequest request, String paramPrefix) {
        return StringUtils.defaultString(request.getParameter(paramPrefix + "search"));
    }

    public static Pageable getPageableForParamPrefix(HttpServletRequest request, String paramPrefix) {
        int page = Integer.parseInt(StringUtils.defaultIfBlank(request.getParameter(paramPrefix + "page"), "0"));
        int size = Integer.parseInt(StringUtils.defaultIfBlank(request.getParameter(paramPrefix + "size"), "10"));
        String sortString = StringUtils.defaultIfBlank(request.getParameter(paramPrefix + "sort"), "id,DESC");
        String[] sortValues = sortString.split(",");

        String sortProperty = sortValues[0];
        Sort.Direction sortDirection = Sort.Direction.valueOf(sortValues[1]);

        Pageable workLogPageable = PageRequest.of(page, size, sortDirection, sortProperty);
        return workLogPageable;
    }

    public String features() {
        return "/features";
    }

    public static String getPublicFileURL(File f) {
        return String.format("/frontend-resource-asset-%d/%s", f.getId(), encode(f.getFilename()));
    }

    public String getAbsoluteFileURL(File f) {
        return getBaseUrl() + getPublicFileURL(f);
    }

    public static String encode(String string) {
        return URLEncoder.encode(string, Charset.defaultCharset());
    }

    public Long getOrganizationIdFromUrlOrNull(HttpServletRequest request) {
        Matcher m = htmlOrganizationPath.matcher(request.getServletPath());

        if (not(m.matches())) {
            return null;
        }

        return Long.parseLong(m.group(1));
    }


    public String getEntityKeyOrNull(HttpServletRequest request) {
        Matcher m = entityKeyPath.matcher(request.getServletPath());

        if (not(m.matches())) {
            return null;
        }
        return m.group(2);
    }

    public TenantResolver.TenantedResource getTenantedResource(HttpServletRequest request) {
        Matcher m = organizationIdAndEntityKeyPath.matcher(request.getServletPath());
        String orgIdParam = request.getParameter(ORGANIZATIONID);
        String orgIdString;

        if (not(m.matches())) {
            return TenantResolver.nonExistingTenantedResource;
        } else {
            orgIdString = m.group(2);
        }

        if(StringUtils.isEmpty(orgIdString)) {
            orgIdString = orgIdParam;
        }

        if(!StringUtils.isEmpty(orgIdParam)) {
            if(!orgIdString.equals(orgIdParam)) {
                throw new RuntimeException("Access denied");
            }
        }

        Long orgId = orgIdString == null ? null : Long.parseLong(orgIdString);
        String entityKey = m.group(3);

        if(entityKey.equals(CN) || entityKey.equals(CI)) {
            entityKey = null;
        }

        FrontendResource.AccessLevel accessLevel = null;

        if (!request.getRequestURI().contains(_HTML)) {
            accessLevel = FrontendResource.AccessLevel.PUBLIC;
        } else if (request.getRequestURI().contains(_HTML) && !request.getRequestURI().contains(_ORGANIZATION)) {
            accessLevel = FrontendResource.AccessLevel.GLOBAL;
        } else if (request.getRequestURI().contains(_HTML_ORGANIZATION)) {
            accessLevel = FrontendResource.AccessLevel.ORGANIZATION;
        }

        return new TenantResolver.TenantedResource(orgId, request.getLocalAddr(), entityKey, request.getMethod(), accessLevel);
    }

    public int randomInt(int fromInclusive, int toExclusive) {
        return fromInclusive + random.nextInt(toExclusive - fromInclusive);
    }

    public String getUiComponentPreviewUrl(Long organizationId, String frontendResourceUrl, FrontendResource.AccessLevel accessLevel) {
        String accessLevelPath = "";
        String orgIdParam = (organizationId != null ? "&organizationId=" + organizationId : "");

        if (accessLevel.equals(FrontendResource.AccessLevel.GLOBAL)) {
            accessLevelPath = _HTML;
        } else if (accessLevel.equals(FrontendResource.AccessLevel.ORGANIZATION)) {
            accessLevelPath = _HTML_ORGANIZATION + (organizationId != null ? "/" + organizationId : "");
            orgIdParam = "";
        }

        return accessLevelPath + _CN + "/" + frontendResourceUrl + "?draft=true" + orgIdParam;
    }

    public String getUiComponentSettingsUrl(Long organizationId, Long frontendResourceId) {
        return baseUrl +
                (organizationId == null ? _HTML + _WEBENDPOINT : _HTML_ORGANIZATION + "/" + organizationId + _WEBENDPOINT)
                + "/" + frontendResourceId + _SETTINGS;
    }

}
