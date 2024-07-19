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

package com.openkoda.service.user;

import com.openkoda.controller.ComponentProvider;
import com.openkoda.core.security.HasSecurityRules;
import com.openkoda.model.*;
import com.openkoda.service.user.BasicPrivilegeService.PrivilegeChangeEvent;
import jakarta.inject.Inject;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 *
 *
 * @author Arkadiusz Drysch (adrysch@stratoflow.com)
 *
 */

@Service
public class RoleService extends ComponentProvider implements HasSecurityRules {

    public static final String ROLE_TYPE_ORG = "ORG";
    public static final String ROLE_TYPE_GLOBAL = "GLOBAL";
    public static final String ROLE_TYPE_GLOBAL_ORG = "GLOBAL_ORG";
    
    @Inject private ApplicationEventPublisher applicationEventPublisher;
    

    public GlobalRole createOrUpdateGlobalRole(String name, Set<PrivilegeBase> privileges, boolean removable) {
        debug("[createOrUpdateGlobalRole] name: {}; privilages: {}; removable: {}", name, privileges, removable);
        GlobalRole role = repositories.unsecure.globalRole.findByName(name);
        if (role == null) {
            role = new GlobalRole(name);
        }
        role.setPrivilegesSet(privileges);
        role.setRemovable(removable);
        repositories.unsecure.globalRole.save(role);
        role = repositories.unsecure.globalRole.findByName(name);
        applicationEventPublisher.publishEvent(new PrivilegeChangeEvent(this));
        return role;
    }

    public OrganizationRole createOrUpdateOrgRole(String name, Set<PrivilegeBase> privileges, boolean removable) {
        return createOrUpdateOrgRole(null, name, privileges, removable);
    }

    public OrganizationRole createOrUpdateOrgRole(Long id, String name, Set<PrivilegeBase> privileges, boolean removable) {
        debug("[createOrUpdateOrgRole] orgName: {}; privilages: {}; removable: {}", name, privileges, removable);
        OrganizationRole role = repositories.unsecure.organizationRole.findByName(name);
        if (role == null) {
            role = new OrganizationRole(id, name);
        }
        role.setPrivilegesSet(privileges);
        role.setRemovable(removable);
        repositories.unsecure.organizationRole.save(role);
        role = repositories.unsecure.organizationRole.findByName(name);
        applicationEventPublisher.publishEvent(new PrivilegeChangeEvent(this));
        return role;
    }

    public GlobalOrganizationRole createOrUpdateGlobalOrgRole(String name, Set<PrivilegeBase> privileges, boolean removable) {
        return createOrUpdateGlobalOrgRole(null, name, privileges, removable);
    }

    public GlobalOrganizationRole createOrUpdateGlobalOrgRole(Long id, String name, Set<PrivilegeBase> privileges, boolean removable) {
        debug("[createOrUpdateGlobalOrgRole] orgName: {}; privilages: {}; removable: {}", name, privileges, removable);
        GlobalOrganizationRole role = repositories.unsecure.globalOrganizationRole.findByName(name);
        if (role == null) {
            role = new GlobalOrganizationRole(id, name);
        }
        role.setPrivilegesSet(privileges);
        role.setRemovable(removable);
        repositories.unsecure.globalOrganizationRole.save(role);
        role = repositories.unsecure.globalOrganizationRole.findByName(name);
        applicationEventPublisher.publishEvent(new PrivilegeChangeEvent(this));
        return role;
    }

    /**
     * Creates new role considering its type (discrimination value)
     */
    @PreAuthorize(CHECK_CAN_MANAGE_ROLES)
    public Role createRole(String name, String type, Set<PrivilegeBase> privileges) {
        debug("[createRole] Creating role {} of type {} and privileges {}", name, type, privileges);
        if (type.equals(ROLE_TYPE_GLOBAL)) {
            return createOrUpdateGlobalRole(name, privileges, true);
        } else if (type.equals(ROLE_TYPE_ORG)) {
            return createOrUpdateOrgRole(name, privileges, true);
        } else if (type.equals(ROLE_TYPE_GLOBAL_ORG)) {
            return createOrUpdateGlobalOrgRole(name, privileges, true);
        }
        return null;
    }

    /**
     * Validates whether role with given name and type already exists in the database
     */
    public boolean checkIfRoleNameAlreadyExists(String name, String type, BindingResult br) {
        debug("[checkIfRoleNameAlreadyExists]");
        boolean roleExists = false;
        if (type.equals(ROLE_TYPE_GLOBAL)) {
            roleExists = repositories.unsecure.globalRole.findByName(name) != null;
        } else if (type.equals(ROLE_TYPE_ORG)) {
            roleExists = repositories.unsecure.organizationRole.findByName(name) != null;
        } else if (type.equals(ROLE_TYPE_GLOBAL_ORG)) {
            roleExists = repositories.unsecure.globalOrganizationRole.findByName(name) != null;
        }
        if (roleExists) {
            debug("[checkIfRoleNameAlreadyExists] role with name {} and type {} already exists", name, type);
            br.rejectValue("name", "name.exists");
        }
        return roleExists;
    }

    public Role addPrivilegesToRole(String roleName, Set<PrivilegeBase> privileges) {
        debug("[addPrivilagesToRole] role name: {}; privilages: {}", roleName, privileges);
        Role role = repositories.unsecure.role.findByName(roleName);
        if (role == null) {
            debug("[addPrivilegesToRole] role {} not found", roleName);
            return null;
        }
        privileges.addAll(role.getPrivilegesSet());
        role.setPrivilegesSet(privileges);
        role = repositories.unsecure.role.save(role);
        applicationEventPublisher.publishEvent(new PrivilegeChangeEvent(this));
        return role;
    }
    
    public void removePrivilegesFromRoles(Set<PrivilegeBase> privileges) {
        debug("[addPrivilagesToRole] privilages: {}", privileges);
        List<Role> roles = repositories.unsecure.role.findAll();
        List<Role> modifiedRoles = new ArrayList<>();
        for (Role role : roles) {
            Set<PrivilegeBase> currentPrivs = new HashSet<>(role.getPrivilegesSet());
            if(currentPrivs.removeAll(privileges)) {
                modifiedRoles.add(role);
            }
            
            role.setPrivilegesSet(currentPrivs); 
        }
        
        applicationEventPublisher.publishEvent(new PrivilegeChangeEvent(this));
        repositories.unsecure.role.saveAll(modifiedRoles);
    }

}
