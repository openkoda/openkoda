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

package com.openkoda.controller.organization;

import com.openkoda.form.GlobalOrgRoleForm;
import com.openkoda.form.InviteUserForm;
import com.openkoda.form.OrganizationForm;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.List;

import static com.openkoda.controller.common.URLConstants._HTML_ORGANIZATION;
import static com.openkoda.core.service.FrontendResourceService.frontendResourceTemplateNamePrefix;
import static org.springframework.web.bind.annotation.RequestMethod.DELETE;

@Controller
/**
 * <p>OrganizationControllerHtml class.</p>
 * <p>Intended to be controller for server-side generated html actions, whereas AbstractOrganizationController does
 * the actual logic.</p>
 * <p>General contract is: resolve HTTP bindings, delegate work to AbstractOrganizationController and provide
 * ModelAndView</p>
 *
 * @author Arkadiusz Drysch (adrysch@stratoflow.com)
 *
 */
@ResponseBody
@RequestMapping(_HTML_ORGANIZATION)
public class OrganizationControllerHtml extends AbstractOrganizationController {

    @PreAuthorize(CHECK_CAN_READ_ORG_DATA)
    @GetMapping(_ORGANIZATIONID + _DASHBOARD)
    public Object getOrganization(@PathVariable(ORGANIZATIONID) Long organizationId,
                      @Qualifier("user") Pageable userPageable,
                      @RequestParam(required = false, defaultValue = "", name = "user_search") String search,
                      HttpServletRequest request) {
        debug("[getOrganization] orgId {} search {}", organizationId, search);
        Object result = findOrganizationWithSettings(organizationId)
                .mav(request, frontendResourceTemplateNamePrefix + DASHBOARD, (a) -> a.get(organizationEntity));
        return result;
    }

    @PreAuthorize(CHECK_CAN_READ_ORG_DATA)
    @GetMapping(_ORGANIZATIONID + _SETTINGS)
    public Object settings(@PathVariable(ORGANIZATIONID) Long organizationId,
                           @Qualifier("user") Pageable userPageable,
                           @RequestParam(required = false, defaultValue = "", name = "user_search") String userSearch,
                           @Qualifier("module") Pageable modulePageable,
                           @RequestParam(required = false, defaultValue = "", name = "module_search") String moduleSearch,
                           HttpServletRequest request) {
        debug("[settings] orgId {} userSearch {} moduleSearch {}", organizationId, userSearch, moduleSearch);
        return getOrganizationSettings(organizationId, userSearch, userPageable)
                .mav(ORGANIZATION + "-settings");
    }

    @PreAuthorize(CHECK_CAN_SAVE_USER_ROLES)
    @PostMapping(_ORGANIZATIONID + _MEMBER)
    public Object setUserRole(@PathVariable(ORGANIZATIONID) long organizationId,
                           @RequestParam(name = "userId") long userId, String userRoleName) {
        debug("[setUserRole] organizationId {} userId {}", organizationId, userId);
        return changeUserOrganizationRole(organizationId, userId, userRoleName)
                .mav(a -> true, a -> false);
    }

    @PreAuthorize(CHECK_CAN_MANAGE_ORG_DATA)
    @GetMapping(_ORGANIZATIONID + _HISTORY)
    public Object history(@PathVariable(ORGANIZATIONID) Long organizationId,
                          @Qualifier("audit") Pageable auditPageable,
                          @RequestParam(required = false, defaultValue = "", name = "audit_search") String search,
                          HttpServletRequest request) {
        debug("[history] orgId {}, ");
        return getHistory(organizationId, auditPageable, search)
                .mav(ORGANIZATION + "-" + HISTORY);
    }

    @PreAuthorize(CHECK_CAN_MANAGE_ORG_DATA)
    @GetMapping(_NEW + _SETTINGS)
    public Object newOrganization(@Qualifier("user") Pageable userPageable) {
        debug("[newOrganization]");
        return getNewOrganizationSettings(userPageable)
                .mav(ORGANIZATION + '-' + NEW);
    }

    @PreAuthorize(CHECK_CAN_MANAGE_ORG_DATA)
    @PostMapping(_NEW + _SETTINGS)
    public Object saveNew(@Valid OrganizationForm organizationForm, BindingResult br) {
        debug("[saveNew]");
        return createOrganization(organizationForm, br)
                .mav(ENTITY + '-' + FORMS + "::organization-settings-form-success",
                        ENTITY + '-' + FORMS + "::organization-settings-form-error");
    }

    @PreAuthorize(CHECK_CAN_MANAGE_ORG_DATA)
    @RequestMapping(value = _ORGANIZATIONID, method = DELETE)
    public Object delete(@PathVariable(ORGANIZATIONID) Long organizationId) {
        debug("[delete] orgId {}", organizationId);
        return deleteOrganization(organizationId);
    }

    @GetMapping(_ALL)
    //TODO Rule 1.4 All methods in non-public controllers must have @PreAuthorize
    public Object getAll(
            @Qualifier("organization") Pageable organizationPageable,
            @RequestParam(required = false, defaultValue = "", name = "organization_search") String search) {
        debug("[getAll] search {}", search);
        return findOrganizationsFlow(search, null, organizationPageable)
                .mav(ORGANIZATION + "-" + ALL);
    }

    @PreAuthorize(CHECK_CAN_MANAGE_ORG_DATA)
    @PostMapping(_ORGANIZATIONID +_SETTINGS)
    public Object post(
            @PathVariable(ORGANIZATIONID) Long organizationId,
            OrganizationForm form,
            BindingResult br) {
        debug("[post] orgId {}", organizationId);
        Object result = saveOrganization(organizationId, form, br)
                .mav(ENTITY + "-" + FORMS + "::organization-settings-form-success",
                ENTITY + "-" + FORMS + "::organization-settings-form-error");
        return result;
    }


    @PreAuthorize(CHECK_CAN_MANAGE_ORG_DATA)
    @PostMapping(_ORGANIZATIONID + _INVITE)
    public Object inviteUser(@PathVariable(ORGANIZATIONID) Long organizationId, @Valid InviteUserForm userFormData, BindingResult
            br) {
        debug("[inviteUser] orgId {}", organizationId);
        return inviteUser(userFormData, organizationId, br)
                .mav(ENTITY + '-' + FORMS + "::invite-user-form-success",
                        ENTITY + '-' + FORMS + "::invite-user-form-error");
    }

    @PreAuthorize(CHECK_CAN_ACCESS_GLOBAL_SETTINGS)
    @PostMapping(_ORGANIZATIONID + "/addGlobalOrgRole")
    public Object addGlobalOrgRole(@PathVariable(ORGANIZATIONID) Long organizationId, @Valid GlobalOrgRoleForm globalOrgRoleForm){
        return globalOrgRole(globalOrgRoleForm, organizationId)
                .mav(ENTITY + '-' + FORMS + "::global-org-role-form-success",
                ENTITY + '-' + FORMS + "::global-org-role-form-error");
    }

    @PreAuthorize(CHECK_CAN_MANAGE_ORG_DATA)
    @PostMapping(_ORGANIZATIONID + _REMOVE)
    public Object removeUser(@PathVariable(ORGANIZATIONID) Long organizationId,
                             @RequestParam(name = "userRoleId", required = true) long userRoleId
                             ) {
        debug("[removeUser] orgId {}", organizationId);
        return removeUserRole(userRoleId)
                .mav( a -> true, a -> false);
    }

    @PreAuthorize(CHECK_CAN_MANAGE_ORG_DATA)
    @PostMapping(_ORGANIZATIONID + _ENTITY + _REMOVE)
    public Object removeOrganizationData(@PathVariable(ORGANIZATIONID) Long organizationId){
        debug("[removeOrganizationData] organizationId {}", organizationId);
        return removeOrganization(organizationId)
                .mav( a -> true, a -> false);
    }

    @PreAuthorize(CHECK_CAN_MANAGE_ORG_DATA)
    @GetMapping(_ORGANIZATIONID + _RULE_LINE + "/{type}")
    public Object getStatementLineForm(@PathVariable(ORGANIZATIONID) Long organizationId, @PathVariable("type") String type,
                                       @RequestParam("index") Long index, @RequestParam("datalistId") String datalistId,
                                       @RequestParam("fieldName") String fieldName, @RequestParam("key") String key,
                                       @RequestParam("disabled") String disabled, @RequestParam("advanced") String advanced,
                                       @RequestParam("indexForKey") String indexForKey, @RequestParam("indexToDisplay") String indexToDisplay,
                                       @RequestParam("indexForImgUrl") String indexForImgUrl) {
        debug("[getStatementLineForm] orgId {}", organizationId);
        return new ModelAndView("forms::rule-part(index=" + index + ", type=" + type + ", rule=null, datalistId=" + datalistId
                + ", disabled=" + disabled + ", key=" + key + ", name=" + fieldName + ", advanced=" + advanced + ", indexForKey=" + indexForKey
                + ", indexToDisplay=" + indexToDisplay + ", indexForImgUrl=" + indexForImgUrl + ")");
    }


    @PreAuthorize(CHECK_CAN_MANAGE_ORG_DATA)
    @GetMapping(_ORGANIZATIONID + _RULE + _SEARCH + _SELECTED)
    public Object getStatementSearchSelectedBox(@PathVariable(ORGANIZATIONID) Long organizationId,
                                                @RequestParam("fieldName") String fieldName, @RequestParam("selectedId") String selectedId,
                                                @RequestParam("imgUrl") String imgUrl, @RequestParam("label") String label,
                                                @RequestParam("url") String url) {
        debug("[getStatementSearchSelectedBox] orgId {}", organizationId);
        return new ModelAndView("forms::selected-searchable(selectedId=" + selectedId + ", fieldName=" + fieldName
                + ", imgUrl='" + imgUrl + "', label='" + label + "', url='" + url + "')");
    }

}
