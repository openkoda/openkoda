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

package com.openkoda.controller.role;

import com.openkoda.core.cache.RequestSessionCacheService;
import com.openkoda.core.controller.generic.AbstractController;
import com.openkoda.core.flow.Flow;
import com.openkoda.core.flow.PageModelMap;
import com.openkoda.core.helper.PrivilegeHelper;
import com.openkoda.form.RoleForm;
import com.openkoda.model.PrivilegeBase;
import com.openkoda.model.Role;
import jakarta.inject.Inject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;

import java.util.HashSet;
import java.util.stream.Collectors;

/**
 * <p>Controller that provides actual Role related functionality for different type of access (eg. API, HTML)</p>
 * <p>Implementing classes should take over http binding and forming a result whereas this controller should take care
 * of actual implementation</p>
 *
 * @author Martyna Litkowska (mlitkowska@stratoflow.com)
 * @since 2019-01-24
 */
public class AbstractRoleController extends AbstractController {

    @Inject private RequestSessionCacheService cacheService;
    
    protected PageModelMap findRolesFlow(
            String aSearchTerm,
            Specification<Role> aSpecification,
            Pageable aPageable) {
        debug("[findRolesFlow] search {}", aSearchTerm);
        return Flow.init()
                .thenSet(rolePage, a -> repositories.secure.role.search(aSearchTerm, null, aSpecification, aPageable))
                .execute();
    }

    protected PageModelMap findRole(long roleId) {
        debug("[findRole] roleId {}", roleId);
        return Flow.init()
                .thenSet(roleEntity, a -> repositories.unsecure.role.findOne(roleId))
                .thenSet(roleForm, a -> new RoleForm(a.result))
                .thenSet(rolesEnum, a -> PrivilegeHelper.allEnumsToList())
                .execute();
    }

    protected PageModelMap createRole(RoleForm roleFormData, BindingResult br) {
        debug("[createRole]");
        return Flow.init(roleForm, roleFormData)
                .thenSet(rolesEnum, a -> PrivilegeHelper.allEnumsToList())
                .then(a -> services.role.checkIfRoleNameAlreadyExists(roleFormData.dto.name, roleFormData.dto.type, br))
                .then(a -> services.validation.validate(roleFormData, br))
                .then(a -> roleFormData.dto.privileges != null ?
                        roleFormData.dto.privileges.stream().map(PrivilegeHelper::valueOfString).collect(Collectors.toSet())
                        : new HashSet<PrivilegeBase>()
                )
                .then(a -> services.role.createRole(roleFormData.dto.name, roleFormData.dto.type, a.result))
                .thenSet(roleForm, a -> new RoleForm())
                .execute();
    }

    @Transactional
    public PageModelMap deleteRole(long roleId) {
        debug("[deleteRole] roleId {}", roleId);
        return Flow.init()
                .then(a -> repositories.unsecure.userRole.deleteUserRoleByRoleId(roleId))
                .then(a -> repositories.unsecure.role.deleteRole(roleId))
                .execute();
    }

    protected PageModelMap updateRole(long roleId, RoleForm roleFormData, BindingResult br) {
        debug("[updateRole] roleId {}", roleId);
        return Flow.init(roleForm, roleFormData)
                .thenSet(rolesEnum, a -> PrivilegeHelper.allEnumsToList())
                .then(a -> repositories.unsecure.role.findOne(roleId))
                .then(a -> services.validation.validateAndPopulateToEntity(roleFormData, br,a.result))
                .thenSet(roleEntity, a -> repositories.unsecure.role.save(a.result))
                .then(a -> services.privilege.notifyOnPrivilagesChange())
                .execute();
    }
}
