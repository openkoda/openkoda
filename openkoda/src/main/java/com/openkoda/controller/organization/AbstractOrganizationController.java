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

import com.openkoda.core.controller.generic.AbstractController;
import com.openkoda.core.flow.Flow;
import com.openkoda.core.flow.PageModelMap;
import com.openkoda.core.security.HasSecurityRules;
import com.openkoda.core.service.event.ApplicationEvent;
import com.openkoda.dto.OrganizationDto;
import com.openkoda.form.GlobalOrgRoleForm;
import com.openkoda.form.InviteUserForm;
import com.openkoda.form.OrganizationForm;
import com.openkoda.model.Organization;
import com.openkoda.repository.specifications.UserSpecifications;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.validation.BindingResult;
import reactor.util.function.Tuples;

/**
 * <p>AbstractOrganizationController class.</p>
 * <p>Controller that provides actual functionality for different type of access (eg. API, HTML)</p>
 * <p>Implementing classes should take over http binding and forming a result whereas this controller should take care
 * of actual implementation</p>
 *
 * @author Arkadiusz Drysch (adrysch@stratoflow.com)
 *
 */
public class AbstractOrganizationController extends AbstractController implements HasSecurityRules {

    protected PageModelMap findOrganizationsFlow(
            String aSearchTerm,
            Specification<Organization> aSpecification,
            Pageable aPageable) {
        debug("[findOrganizationFlow] search {}", aSearchTerm);
        if(aPageable.getSort() == null) {
            aPageable = PageRequest.of(aPageable.getPageNumber(), aPageable.getPageSize(), Sort.Direction.DESC, "createdOn");
        }
        Pageable finalAPageable = aPageable;
        return Flow.init()
                .thenSet(organizationPage, a -> repositories.secure.organization.search(
                        aSearchTerm, null, aSpecification, finalAPageable))
                .execute();
    }

    protected Flow<Long, OrganizationForm, AbstractOrganizationController> findOrganizationFlow(Long id) {
        debug("[findOrganizationFlow] id {}", id);
        return Flow.init(this, id)
                .thenSet(organizationEntity, a -> repositories.unsecure.organization.findOne(id))
                .thenSet(organizationForm, a -> new OrganizationForm(id, a.result));
    }

    protected Flow findOrganizationWithSettingsFlow(Long id) {
        debug("[findOrganizationWithSettingsFlow] id {}", id);
        return findOrganizationFlow(id);
    }

    protected PageModelMap findOrganizationWithSettings(Long id) {
        debug("[findOrganizationWithSettings] id {}");
        return findOrganizationWithSettingsFlow(id)
                .execute();
    }

    protected PageModelMap getOrganizationSettings(Long organizationId, String userSearch, Pageable userPageable){
        return findOrganizationWithSettingsFlow(organizationId)
                .thenSet(organizationEntityId, a -> organizationId)
                .thenSet(userPage, a -> repositories.secure.user.search(userSearch, null, UserSpecifications.searchSpecification(organizationId), userPageable))
                .thenSet(inviteUserForm, a -> new InviteUserForm())
                .thenSet(globalOrgRoleForm, a -> new GlobalOrgRoleForm(services.organization.getNamesOfGlobalOrgRolesInOrganization(organizationId)))
                .execute();
    }

    protected PageModelMap getNewOrganizationSettings(Pageable userPageable) {
        return findOrganizationWithSettingsFlow(-1L)
                .thenSet(userPage, a -> repositories.secure.user.search(
                        "",  null, UserSpecifications.searchSpecification(-1L), userPageable))
                .thenSet(inviteUserForm, a -> new InviteUserForm())
                .execute();
    }
    protected PageModelMap deleteOrganization(Long id) {
        debug("[deleteOrganization] id {}", id);
        return Flow.init(this, id)
                .then(a -> repositories.unsecure.organization.deleteOne(a.result))
                .execute();
    }

    protected PageModelMap inviteUser(InviteUserForm form, Long organizationId, BindingResult br) {
        debug("[inviteUser] orgId {}", organizationId);
        return Flow.init(inviteUserForm, form)
                .thenSet(inviteUserForm, a -> form)
                .then(a -> services.user.validateIfUserDoesNotHaveRoleInOrganization(form.dto.email, organizationId, br))
                .then(a -> services.validation.validate(form, br))
                .then(a -> Tuples.of(
                        repositories.unsecure.user.findByEmailLowercase(form.dto.email),
                        repositories.unsecure.organization.findOne(organizationId)))
                .then(a -> services.user.inviteNewOrExistingUser(form, a.result.getT1(), a.result.getT2()))
                .execute();
    }

    protected PageModelMap globalOrgRole(GlobalOrgRoleForm form, Long organizationId){
        return Flow.init()
                .thenSet(globalOrgRoleForm, a -> form)
                .then(a -> Tuples.of(repositories.unsecure.role.findAllGlobalRoles(),
                        services.userRole.getUserRolesForOrganization(organizationId)))
                .then(a -> services.organization.updateGlobalOrgRolesInOrganization(organizationId, a.result.getT1(),form.dto.getGlobalOrganizationRoles(), a.result.getT2()))
                .execute();
    }

    protected PageModelMap removeUserRole(long userRoleId) {
        debug("[removeUserRole] userRoleId {}", userRoleId);
        return Flow.init()
                .then(a -> repositories.unsecure.userRole.deleteUserRole(userRoleId))
                .execute();
    }

    protected PageModelMap removeOrganization(long organizationId) {
        debug("[removeOrganization] organizationId {}", organizationId);
        return Flow.init()
                .thenSet(organizationEntity, a -> repositories.unsecure.organization.findOne(organizationId))
                .then(a -> services.organization.markSchemaAsDeleted(organizationId, a.model.get(organizationEntity).getAssignedDatasource()))
                .then(a -> services.organization.dropSchemaConstraints(organizationId, a.result, a.model.get(organizationEntity).getAssignedDatasource()))
                .then(a -> services.organization.removeOrganization(organizationId))
                .execute();
    }

    protected PageModelMap saveOrganization(
            Long organizationId,
            OrganizationForm form,
            BindingResult br) {
        debug("[saveOrganization] orgId {}", organizationId);
        return Flow.init()
                .thenSet(organizationForm, a -> form)
                .then(a -> repositories.unsecure.organization.findOne(organizationId))
                .then(a -> services.validation.validateAndPopulateToEntity(form, br,a.result))
                .thenSet(organizationEntity, a -> repositories.unsecure.organization.save(a.result))
                .then(a -> services.applicationEvent.emitEvent(ApplicationEvent.ORGANIZATION_MODIFIED, new OrganizationDto(a.result)))
                .execute();
    }

    /**
     * Creates new {@link Organization}
     * Firstly validates {@link OrganizationForm} and then calls {@link com.openkoda.service.organization.OrganizationService}
     * to createFormFieldDefinition new {@link Organization} entity
     */

    protected PageModelMap createOrganization(
            OrganizationForm form,
            BindingResult br) {
        debug("[createOrganization]");
        return Flow.init(transactional)
                .thenSet(organizationForm, a -> form)
                .then(a -> services.validation.validate(form, br))
                .thenSet(organizationEntity, a -> services.organization.createOrganization(form.dto.name, form.dto.assignedDatasource))
                .thenSetDefault(organizationForm)
                .execute();
    }

    protected PageModelMap getHistory(
            Long organizationId,
            Pageable auditPageable,
            String search) {
        debug("[getHistory] orgId {}, search {}", organizationId, search);
        return Flow.init(this, organizationId)
                .thenSet(organizationEntity, a -> repositories.unsecure.organization.findOne(a.result))
                .thenSet(auditPage, a -> repositories.unsecure.audit.findAllByOrganizationId(organizationId, search, auditPageable))
                .execute();
    }

    protected PageModelMap changeUserOrganizationRole(long organizationId, long userId, String roleName){
        debug("[changeUserRole] organizationId {}, userId {}, roleName {}", organizationId, userId, roleName);
        return Flow.init(transactional)
                .then(a -> repositories.unsecure.user.findById(userId))
                .then(a -> services.user.changeUserOrganizationRole(a.result, organizationId, roleName))
                .execute();
    }

}
