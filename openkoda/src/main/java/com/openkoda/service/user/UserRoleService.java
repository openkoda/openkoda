package com.openkoda.service.user;

import com.openkoda.controller.ComponentProvider;
import com.openkoda.model.Privilege;
import com.openkoda.model.UserRole;
import com.openkoda.repository.specifications.UserRoleSpecification;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserRoleService extends ComponentProvider {

    public List<UserRole> getUserRolesForOrganization(Long organizationId){
        return repositories.secure.userRole.findAll(UserRoleSpecification.getUserRolesForOrganizations(), Privilege.canAccessGlobalSettings, organizationId);
    }
}
