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

package com.openkoda.repository.user;

import com.openkoda.core.repository.common.UnsecuredFunctionalRepositoryWithLongId;
import com.openkoda.core.security.HasSecurityRules;
import com.openkoda.model.User;
import com.openkoda.model.UserRole;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 *
 *
 * @author Arkadiusz Drysch (adrysch@stratoflow.com)
 *
 */
@Repository
public interface UserRoleRepository extends UnsecuredFunctionalRepositoryWithLongId<UserRole>, HasSecurityRules {

//    @Override
//    @PreAuthorize(CHECK_CAN_SAVE_USER_ROLES)
//    UserRole save(@Param("userRole") UserRole userRole);

//    default UserRole unsecureSave(@Param("userRole") UserRole userRole) {
//        return saveAndFlush(userRole);
//    }

    @Modifying
    @Transactional
    @Query("delete from UserRole dbUserRole where " + CHECK_CAN_MANAGE_USER_ROLES_JPQL + " AND dbUserRole.id = :id ")
    int deleteUserRole(@Param("id") Long aLong);

    @Modifying
    @Transactional
    @Query("delete from UserRole dbUserRole where " + CHECK_CAN_MANAGE_USER_ROLES_JPQL + " AND dbUserRole.roleId IN (select r.id from Role r where r.id = :id AND r.removable = true) ")
    int deleteUserRoleByRoleId(@Param("id") Long aLong);

    @Modifying
    @Transactional
    @Query("delete from UserRole dbUserRole where " + CHECK_CAN_MANAGE_USER_ROLES_JPQL + " AND dbUserRole.userId IS NULL AND dbUserRole.organizationId = :organization_id AND dbUserRole.roleId IN (select r.id from Role r where r.name IN :role_names) ")
    int deleteUserRoleByOrganizationIdAndRoleName(@Param("organization_id") Long aLong, @Param("role_names") Set<String> roleNames);

    @Modifying
    int deleteByRoleIdAndUserIdAndOrganizationId(long roleId, long userId, long organizationId);

    UserRole findByRoleIdAndUserIdAndOrganizationId(long roleId, long userId, long organizationId);

    @Query("select dbUserRole from UserRole dbUserRole where " + CHECK_CAN_MANAGE_USER_ROLES_JPQL + " AND dbUserRole.organizationId = :organization_id")
    List<UserRole> findAllUserRolesInOrganization(@Param("organization_id") Long aLong);

    @Query("select dbUserRole from UserRole dbUserRole where " + CHECK_CAN_MANAGE_USER_ROLES_JPQL + " AND dbUserRole.userId IS NOT NULL AND dbUserRole.organizationId = :organization_id AND (dbUserRole.role.name IN :role_names)")
    List<UserRole> findAllUserRolesInOrganizationWithRoles(@Param("organization_id") Long aLong, @Param("role_names") Set<String> roleNames);

    @Query("select dbUserRole from UserRole dbUserRole where " + CHECK_CAN_MANAGE_USER_ROLES_JPQL + " AND dbUserRole.userId = :user_id AND (dbUserRole.role.name IN :role_names)")
    List<UserRole> findAllUserRolesForUserWithRoles(@Param("user_id") Long aLong, @Param("role_names") Set<String> roleNames);

    @Query("select dbUserRole from UserRole dbUserRole where " + CHECK_CAN_MANAGE_USER_ROLES_JPQL + " AND dbUserRole.userId IS NULL AND dbUserRole.organizationId = :organization_id AND (dbUserRole.role.name IN :role_names)")
    List<UserRole> findAllGlobalRolesInOrganizationWithRoles(@Param("organization_id") Long aLong, @Param("role_names") Set<String> roleNames);

    @Query("select DISTINCT dbUserRole.userId from UserRole dbUserRole where " + CHECK_CAN_MANAGE_USER_ROLES_JPQL + " AND dbUserRole.organizationId = :organization_id")
    Set<Long> findAllUserIdsInOrganization(@Param("organization_id") Long aLong);

    @Query("select DISTINCT dbUserRole.userId from UserRole dbUserRole where " + CHECK_CAN_MANAGE_USER_ROLES_JPQL + " AND dbUserRole.organizationId = :organization_id AND dbUserRole.role.name = :role_name")
    Set<Long> findAllUserIdsInOrganizationWithRole(@Param("organization_id") Long aLong, @Param("role_name") String roleName);

    @Query(value = "SELECT DISTINCT dbUserRole.user from UserRole dbUserRole where dbUserRole.organizationId = :organizationId AND "
            + CHECK_CAN_READ_ROLES_JPQL)
    Set<User> getUsersInOrganization(@Param("organizationId") Long organizationId);

    @Query(value = "SELECT DISTINCT dbUserRole.user from UserRole dbUserRole where dbUserRole.organizationId = :organizationId AND dbUserRole.role.name = :role_name AND "
            + CHECK_CAN_READ_ROLES_JPQL)
    Set<User> getUsersInOrganizationWithRole(@Param("organizationId") Long organizationId, @Param("role_name") String roleName);

    @Query("SELECT count(*) > 0 from UserRole ur where (ur.userId = :id or ur.organizationId in ?#{principal.organizationIds}) and ur.updatedOn > :since")
    Optional<Boolean> wasModifiedSince(@Param("id") Long userId, @Param("since") LocalDateTime since);

    @Query("SELECT max(ur.updatedOn) from UserRole ur where (ur.userId = :id or ur.organizationId in ?#{principal.organizationIds})")
    LocalDateTime getLastUpdatedOn(@Param("id") Long userId);

    List<UserRole> findByUserIdAndOrganizationIdAndRoleId(Long userId, Long organizationId, Long roleId);

    Boolean existsByOrganizationIdAndRolePrivilegesContaining(Long organizationId, String privilege);

    List<UserRole> findByOrganizationIdAndUserIdIsNull(Long organizationId);

    List<UserRole> findByOrganizationIdAndUserId(Long organizationId, Long userId);
}
