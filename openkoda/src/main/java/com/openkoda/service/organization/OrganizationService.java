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

package com.openkoda.service.organization;

import com.openkoda.controller.ComponentProvider;
import com.openkoda.core.multitenancy.MultitenancyService;
import com.openkoda.core.service.event.ApplicationEvent;
import com.openkoda.dto.OrganizationDto;
import com.openkoda.model.GlobalOrganizationRole;
import com.openkoda.model.Organization;
import com.openkoda.model.UserRole;
import jakarta.inject.Inject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.Array;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;


/**
 *
 *
 * @author Arkadiusz Drysch (adrysch@stratoflow.com)
 *
 */

@Service
public class OrganizationService extends ComponentProvider {

    @Autowired
    private DataSource dataSource;

    @Inject
    protected MultitenancyService multitenancyService;

    /**
     * <p>createOrganization.</p>
     *
     * @param organizationName a {@link java.lang.String} object.
     * @return a {@link com.openkoda.model.Organization} object.
     */
    public Organization createOrganization(String organizationName, Integer assignedDatasource) {
        debug("[createOrganization] {}", organizationName);
        Organization result = repositories.unsecure.organization.save(new Organization(organizationName, assignedDatasource));
        multitenancyService.createTenant(result.getId());
        services.applicationEvent.emitEvent(ApplicationEvent.ORGANIZATION_CREATED, new OrganizationDto(result));
        return result;
    }

    public Organization createOrganization(String organizationName, boolean setupTrial){
        debug("[createOrganization] {}", organizationName);
        Organization result = repositories.unsecure.organization.save(new Organization(organizationName));
        services.applicationEvent.emitEvent(ApplicationEvent.ORGANIZATION_CREATED, new OrganizationDto(result, setupTrial));
        return result;
    }

    public boolean removeOrganization(Long orgId) {
        debug("[removeOrganization] OrgId: {}", orgId);
        Connection connection = DataSourceUtils.getConnection(dataSource);
        boolean deleted = true;
        try (PreparedStatement preparedStatement = connection.prepareStatement("call remove_organizations_by_id(?)")) {
            Array org_ids = connection.createArrayOf("bigint", new Long[]{orgId});
            preparedStatement.setArray(1, org_ids);
            preparedStatement.execute();
        } catch (SQLException throwables) {
            deleted = false;
            throw new RuntimeException("sql exception in [removeOrganization]", throwables);
        } finally {
            DataSourceUtils.releaseConnection(connection, dataSource);
        }
        return true;
    }

    public String markSchemaAsDeleted(long orgId, int assignedDatasource) {
        debug("[markSchemeAsRemoved] OrgId: {}", orgId);
        return multitenancyService.markSchemaAsDeleted(orgId, assignedDatasource);
    }

    public Boolean dropSchemaConstraints(long orgId, String schemaName, int assignedDatasource) {
        debug("[dropSchemaConstraints] OrgId: {}", orgId);
        return multitenancyService.dropSchemaConstraints(orgId, schemaName, assignedDatasource);
    }

    public boolean updateGlobalOrgRolesInOrganization(Long organizationId, List<GlobalOrganizationRole> allGlobalOrgRoles,
                                                  List<String> dtoGlobalOrgRoles, List<UserRole> existingGlobalOrgRolesInOrganization){
        for(GlobalOrganizationRole gor : allGlobalOrgRoles){
            if(dtoGlobalOrgRoles.contains(gor.getName())){
                if(!has(existingGlobalOrgRolesInOrganization, gor)){
                    repositories.secure.userRole.saveOne(new UserRole(null, null, gor.getId(), organizationId));
                }
            } else {
                if(has(existingGlobalOrgRolesInOrganization,gor)){
                    repositories.secure.userRole.deleteOne(get(existingGlobalOrgRolesInOrganization, gor));
                }
            }
        }
        return true;
    }

    private boolean has(List<UserRole> existingGlobalOrgRolesInOrganization, GlobalOrganizationRole gor) {
        for(UserRole ur : existingGlobalOrgRolesInOrganization){
            if(Objects.equals(ur.getRoleId(), gor.getId())){
                return true;
            }
        }
        return false;
    }

    private UserRole get(List<UserRole> existingGlobalOrgRolesInOrganization, GlobalOrganizationRole gor) {
        for(UserRole ur : existingGlobalOrgRolesInOrganization){
            if(Objects.equals(ur.getRoleId(), gor.getId())){
                return ur;
            }
        }
        return null;
    }

    public List<String> getNamesOfGlobalOrgRolesInOrganization(Long organizationId){
        return services.userRole.getUserRolesForOrganization(organizationId)
                .stream()
                .map(UserRole::getRoleName)
                .collect(Collectors.toList());
    }


}
