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

package com.openkoda.service.role;

import com.openkoda.controller.ComponentProvider;
import com.openkoda.controller.common.PageAttributes;
import com.openkoda.core.customisation.ServerJSRunner;
import com.openkoda.core.security.UserProvider;
import com.openkoda.dto.OrganizationRelatedObject;
import com.openkoda.model.Role;
import com.openkoda.model.UserRole;
import jakarta.inject.Inject;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class RoleModificationsConsumers extends ComponentProvider {

    @Inject
    ServerJSRunner serverJSRunner;

    public boolean modifyRoleForAllUsersInOrganization(OrganizationRelatedObject organizationRelatedObject, String scriptName) {
        debug("[modifyRoleForAllUsersInOrganization]");

        Map<String, Boolean> userRolesFromScript = runModifyRolesScript(organizationRelatedObject, scriptName);

        Set<String> rolesToRemove = getRoleNamesToRemove(userRolesFromScript);
        Set<String> rolesToAdd = getRoleNamesToAdd(userRolesFromScript);

        if (CollectionUtils.isNotEmpty(rolesToRemove)) {
//            search user roles which exist and should be removed
            List<UserRole> userRolesToRemove = repositories.unsecure.userRole.findAllUserRolesInOrganizationWithRoles(organizationRelatedObject.getOrganizationId(), rolesToRemove);
            repositories.unsecure.userRole.deleteAll(userRolesToRemove);
        }

        if (CollectionUtils.isNotEmpty(rolesToAdd)) {
            Set<Long> userIdsInOrganization = repositories.unsecure.userRole.findAllUserIdsInOrganization(organizationRelatedObject.getOrganizationId());
            Map<String, Role> rolesMappedByName = repositories.unsecure.role.findAll().stream().collect(Collectors.toMap(Role::getName, Function.identity()));

            List<UserRole> userRolesToAdd = new ArrayList<>();

            for (String s : rolesToAdd) {

                Set<Long> userIdsWithRole = repositories.unsecure.userRole.findAllUserIdsInOrganizationWithRole(organizationRelatedObject.getOrganizationId(), s);

                //prepare user Ids where we want to add the role
                Set<Long> userIdsWithoutRole = new HashSet<>();
                userIdsWithoutRole.addAll(userIdsInOrganization);
                userIdsWithoutRole.removeAll(userIdsWithRole);

                userIdsWithoutRole.forEach(a -> userRolesToAdd.add(new UserRole(null, a, rolesMappedByName.get(s).getId(), organizationRelatedObject.getOrganizationId())));
            }

            repositories.unsecure.userRole.saveAll(userRolesToAdd);
        }
        return true;

    }
    public boolean modifyGlobalRoleForOrganization(OrganizationRelatedObject organizationRelatedObject, String scriptName) {
        debug("[modifyGlobalRoleForOrganization]");

        Map<String, Boolean> globalRolesFromScript = runModifyRolesScript(organizationRelatedObject, scriptName);
        Set<String> rolesToRemove = getRoleNamesToRemove(globalRolesFromScript);
        Set<String> rolesToAdd = getRoleNamesToAdd(globalRolesFromScript);

        if (CollectionUtils.isNotEmpty(rolesToRemove)) {
//            search global organization roles which exist and should be removed
            UserProvider.setConsumerAuthentication();
            List<UserRole> rolesToBeRemoved = repositories.unsecure.userRole.findAllGlobalRolesInOrganizationWithRoles(organizationRelatedObject.getOrganizationId(), rolesToRemove);
            repositories.unsecure.userRole.deleteAll(rolesToBeRemoved);
            UserProvider.clearAuthentication();
        }

        if (CollectionUtils.isNotEmpty(rolesToAdd)) {
            Map<String, Role> rolesMappedByName = repositories.unsecure.role.findAll().stream().collect(Collectors.toMap(Role::getName, Function.identity()));

            List<UserRole> rolesToBeAdded = new ArrayList<>();
            List<String> rolesAlreadyAdded = repositories.unsecure.userRole.findAllGlobalRolesInOrganizationWithRoles(organizationRelatedObject.getOrganizationId(), rolesToAdd)
                    .stream().map(UserRole::getRoleName).collect(Collectors.toList());
            rolesToAdd.stream().filter(role -> !rolesAlreadyAdded.contains(role))
                    .forEach(role -> rolesToBeAdded.add(new UserRole(null, null, rolesMappedByName.get(role).getId(), organizationRelatedObject.getOrganizationId())));

            repositories.unsecure.userRole.saveAll(rolesToBeAdded);
        }
        return true;
    }

    private Map<String, Boolean> runModifyRolesScript(OrganizationRelatedObject organizationRelatedObject, String scriptName) {
        debug("[runModifyRolesScript]");
        Map<String, Object> model = new HashMap<>(Map.of(PageAttributes.organizationRelatedObject.name, organizationRelatedObject));
        Map scriptResult = serverJSRunner.evaluateServerJsScript(scriptName, model, null, Map.class);

        if (scriptResult == null) {
            error("[modifyGlobalRoleForOrganization] Script returned null");
            return Collections.EMPTY_MAP;
        }
        return scriptResult;
    }

    private Set<String> getRoleNamesToRemove(Map<String, Boolean> roles) {
        debug("[getRoleNamesToRemove]");
        return roles.entrySet().stream().filter(a -> not(a.getValue())).map(Map.Entry::getKey).collect(Collectors.toSet());
    }

    private Set<String> getRoleNamesToAdd(Map<String, Boolean> roles) {
        debug("[getRoleNamesToAdd]");
        return roles.entrySet().stream().filter(Map.Entry::getValue).map(Map.Entry::getKey).collect(Collectors.toSet());
    }

}

