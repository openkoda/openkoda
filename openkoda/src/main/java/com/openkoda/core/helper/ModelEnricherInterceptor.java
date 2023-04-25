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

package com.openkoda.core.helper;

import com.openkoda.controller.common.PageAttributes;
import com.openkoda.controller.common.SessionData;
import com.openkoda.controller.notification.NotificationController;
import com.openkoda.core.multitenancy.TenantResolver;
import com.openkoda.core.security.OrganizationUser;
import com.openkoda.core.security.UserProvider;
import com.openkoda.core.service.SessionService;
import com.openkoda.core.tracker.LoggingComponentWithRequestId;
import com.openkoda.model.MutableUserInOrganization;
import com.openkoda.model.Organization;
import com.openkoda.model.Privilege;
import com.openkoda.model.notification.Notification;
import com.openkoda.repository.SecureEntityDictionaryRepository;
import com.openkoda.repository.organization.OrganizationRepository;
import com.openkoda.service.captcha.CaptchaService;
import com.openkoda.service.notification.NotificationService;
import jakarta.inject.Inject;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import java.text.SimpleDateFormat;
import java.util.*;

import static com.openkoda.controller.common.PageAttributes.*;
import static com.openkoda.controller.common.URLConstants.DEBUG_MODEL;
import static com.openkoda.controller.common.URLConstants.EXTERNAL_SESSION_ID;
/* TODO: move to correct package */
/**
 * <p>ModelEnricherInterceptor class.</p>
 * <p>This class adds organization.entity and organization.entity.id to request attributes so that
 * they can be added automatically to modelAndView
 *
 * The Interceptor is NOT for security reasons.
 *
 */
@Component
public class ModelEnricherInterceptor implements ReadableCode, LoggingComponentWithRequestId, HandlerInterceptor {

    @Inject
    SecureEntityDictionaryRepository secureEntityDictionaryRepository;
    @Inject
    OrganizationRepository organizationRepository;
    @Inject
    NotificationService notificationService;
    @Inject
    NotificationController notificationController;
    @Inject
    UrlHelper urlHelper;
    @Inject
    TenantResolver tenantResolver;
    @Inject
    CaptchaService captchaService;
    @Value("${default.layout:main}")
    String defaultLayoutName;

    @Inject
    SessionService sessionService;

    private static String resourcesVersion;

    /**
     * <p>Constructor for ModelEnricherInterceptor.</p>
     */
    public ModelEnricherInterceptor() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyMMddHHmm");
        resourcesVersion = sdf.format(new Date());
    }


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        debug("[preHandle]");

        //if there is a captcha token in the request, handle it
        captchaService.handleCaptcha(request);

        //if there is external session provided, add it to request attributes
        //and it will be added to request Id
        if (request.getParameter(EXTERNAL_SESSION_ID) != null) {
            RequestContextHolder.getRequestAttributes().setAttribute(EXTERNAL_SESSION_ID, request.getParameter(EXTERNAL_SESSION_ID), 0);
        }
        Optional<OrganizationUser> user = UserProvider.getFromContext();

        TenantResolver.TenantedResource tr = urlHelper.getTenantedResource(request);
        tenantResolver.setTenantedResource(tr);

        if (not(user.isPresent())) {
            debug("[preHandle] no user present");
            return true;
        }

        Long orgId = tr.organizationId;
        Long sessionOrgId = (Long)sessionService.getAttributeIfSessionExists(SessionData.CURRENT_ORGANIZATION_ID);

        if (orgId == null) {
            debug("[preHandle] not matching organization path");
            if (sessionOrgId == null || sessionOrgId == OrganizationUser.nonExistingOrganizationId ) {
                return true;
            }
            orgId = sessionOrgId;
        }

        OrganizationUser loggedUser = user.get();

        if (loggedUser.hasGlobalOrOrgPrivilege(Privilege.readOrgData, orgId)) {
            Organization org = organizationRepository.findOne(orgId);
            if (org == null) {
                throw new RuntimeException(
                        String.format("Request [%s] for orgId [%d] that does not exist.", request.getRequestURI(), orgId));
            }
            request.setAttribute(ORGANIZATION_ENTITY_ID, orgId);
            request.setAttribute(ORGANIZATION_ENTITY, org);
            sessionService.setAttributeIfSessionExists(SessionData.CURRENT_ORGANIZATION_ID, orgId);
        }

        return true;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        debug("[postHandle]");
        if (modelAndView == null || StringUtils.startsWith(modelAndView.getViewName(), "redirect:") || modelAndView.getView() instanceof RedirectView) {
            return;
        }

        Map<String, Object> model = modelAndView.getModel();

        //resolve org

        boolean modelHasOrganization = model != null && model.containsKey(organizationEntity.name);
        boolean modelHasOrganizationId = model != null && model.containsKey(organizationEntityId.name);


        Long orgId = (Long) request.getAttribute(organizationEntityId.name);
        Organization org = (Organization) request.getAttribute(organizationEntity.name);

        //check consistency between url with organization id and model returned from controller
        //org in the controller should be the same as in the url for consistency reasons
        if (modelHasOrganizationId && orgId != null) {
            Object modelOrgId = model.get(organizationEntityId.name);
            if (not(orgId.equals(modelOrgId))) {
                error("organizationEntityId [{}] page attribute should match the org id [{}] in the url [{}]. Check the Flow model or ModelAndView.",
                        modelOrgId, orgId, request.getRequestURI());
            }
        }
        if (modelHasOrganization && org != null) {
            Organization modelOrg = (Organization) model.get(organizationEntity.name);
            if (not(org.getOrganizationId().equals(modelOrg.getId()))) {
                error("organizationEntity [{}] page attribute should match the org id [{}] in the url [{}]. Check the Flow model or ModelAndView.",
                        modelOrg.getId(), org.getOrganizationId(), request.getRequestURI());
            }
        }

        if (org != null || orgId != null) {
            orgId = orgId != null ? orgId : org.getOrganizationId();
            model.put(organizationDictionariesJson.name, secureEntityDictionaryRepository.getOrganizationDictionaries(orgId));
            model.put(organizationEntity.name, org);
            model.put(organizationEntityId.name, orgId);
        }

        //resolve user
        Optional<OrganizationUser> user = UserProvider.getFromContext();
        boolean isUser = user.map(a -> a.getUser()).map(a -> a.getId()).isPresent();
        boolean userIsInOrg = orgId != null && isUser && user.get().getOrganizationIds().contains(orgId);

        MutableUserInOrganization userInOrg = ApplicationContextProvider.getContext().getBean(MutableUserInOrganization.class);

        if (isUser) {
            debug("[postHandle] isUser");
            Long userId = user.get().getUser().getId();
            userInOrg.setUserId(userId);
        }

        if (userIsInOrg) {
            debug("[postHandle] userIsInOrg");
            userInOrg.setOrganizationId(orgId);
        }

        model.put(commonDictionaries.name, secureEntityDictionaryRepository.getCommonDictionaries());
        model.put(commonDictionariesNames.name, secureEntityDictionaryRepository.getCommonDictionariesNames());
        model.put(defaultLayout.name, defaultLayoutName);
        model.put(PageAttributes.resourcesVersion.name, resourcesVersion);
        model.put(PageAttributes.modelAndView.name, modelAndView);

        //Add Notifications to model for dropdown display
        if (isUser) {
            Long userId = user.get().getUser().getId();

            Set<Long> organizationIds;
            if(orgId != null && user.get().getOrganizationIds().contains(orgId)) {
                organizationIds = Collections.singleton(orgId);
            } else {
                organizationIds = user.get().getOrganizationIds();
            }

            List<Notification> usersUnreadNotificationsList = notificationService.getUsersUnreadNotifications(userId, organizationIds, PageRequest.of(0, 5));

            String unreadNotificationsIdListString = notificationService.getIdListAsString(usersUnreadNotificationsList);
            int unreadNotificationsNumber = notificationService.getUsersUnreadNotificationsNumber(userId, organizationIds);

            model.put(readNotificationsList.name, null);
            model.put(unreadNotificationsList.name, usersUnreadNotificationsList);
            model.put(PageAttributes.unreadNotificationsIdListString.name, unreadNotificationsIdListString);
            model.put(PageAttributes.unreadNotificationsNumber.name, unreadNotificationsNumber);
            model.put(userEntityId.name, userId);
        }

        if (isUser && user.get().hasGlobalPrivilege(Privilege.canAccessGlobalSettings) && request.getParameterMap().containsKey(DEBUG_MODEL)) {
            modelAndView.setViewName("model");
            String s = JsonHelper.toDebugJson(model);
            modelAndView.getModel().clear();
            modelAndView.getModel().put("modelJson", s);
        }


    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        // After completing the request, if the authentication was for a single request, logout the user
        Optional<OrganizationUser> user = UserProvider.getFromContext();
        boolean isUser = user.map(a -> a.getUser()).map(a -> a.getId()).isPresent();

        if (isUser && user.get().isSingleRequestAuth()) {
            if (user.get().isSingleRequestAuth()) {
                UserProvider.clearAuthentication();
            }
        }
    }

}
