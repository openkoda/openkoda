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

package com.openkoda.repository.user;

import com.openkoda.core.flow.Tuple;
import com.openkoda.core.repository.common.UnsecuredFunctionalRepositoryWithLongId;
import com.openkoda.core.security.HasSecurityRules;
import com.openkoda.model.Organization;
import com.openkoda.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 *
 *
 * @author Arkadiusz Drysch (adrysch@stratoflow.com)
 * 
 */
@Repository
public interface UserRepository extends UnsecuredFunctionalRepositoryWithLongId<User>, HasSecurityRules {

    @Query("select ur.organization from UserRole ur where ur.organizationId is not null and ur.userId = :id")
    List<Organization> findOrganizations(@Param("id") Long userId);

    @PreAuthorize(CHECK_CAN_READ_USER_DATA)
    Page<User> findAll(Specification specification, Pageable pageable);

    @Query("SELECT u FROM User u WHERE u.loginAndPassword.login = :login ")
    User findByLogin(@Param("login") String login);

    @Query("SELECT u FROM User u WHERE u.email = :email AND NOT EXISTS (SELECT lap FROM LoginAndPassword lap WHERE lap.user = u)")
    User findByEmailWithoutLogin(@Param("email") String email);

    @Query("SELECT u.email FROM User u WHERE u.loginAndPassword.login = LOWER(:login) AND u.loginAndPassword.enabled = true")
    String findUsernameLowercaseByLoginIsEnabled(@Param("login") String login);

    @Query("SELECT u.email FROM User u WHERE u.loginAndPassword.login = LOWER(:login)")
    String findUsernameLowercaseByLogin(@Param("login") String login);

    @Query("SELECT u FROM User u WHERE LOWER(u.email) = LOWER(:email) ")
    User findByEmailLowercase(@Param("email") String email);

    @Query("SELECT u.id FROM User u WHERE LOWER(u.email) = LOWER(:email) ")
    Long findIdByEmailLowercase(@Param("email") String email);

    @Query("SELECT u FROM User u WHERE u.id = :id")
    User findById(@Param("id") long id);

    @Query("SELECT u.updatedOn FROM User u WHERE u.id = :userId")
    LocalDateTime getUpdatedOn(@Param("userId") long id);

    @Query("SELECT u FROM User u WHERE u.googleUser.googleId = :googleId")
    User findByGoogleId(@Param("googleId") String googleId);

    @Query("SELECT u FROM User u WHERE u.facebookUser.facebookId = :facebookId ")
    User findByFacebookId(@Param("facebookId") String facebookId);

    @Query("SELECT u FROM User u WHERE u.ldapUser.uid = :ldapId ")
    User findByLDAPId(@Param("ldapId") String ldapId);

    @Query("SELECT u FROM User u WHERE u.salesforceUser.salesforceId = :salesforceId ")
    User findBySalesforceId(@Param("salesforceId") String salesforceId);

    @Query("SELECT u FROM User u WHERE u.linkedinUser.linkedinId = :linkedinId ")
    User findByLinkedinId(@Param("linkedinId") String linkedinId);

    @Override
    @Query("select DISTINCT ur.user from UserRole ur where ?#{hasRole('ROLE_ADMIN')} = true OR ur.userId = ?#{principal.user.id} OR ur.organizationId in ?#{principal.organizationIds} ")
    Page<User> findAll(Pageable page);

    @Query("SELECT new com.openkoda.core.flow.Tuple(dbUserRole.role.name, dbUserRole.role.privileges) from UserRole dbUserRole where dbUserRole.userId = :id and dbUserRole.organizationId is null AND "
            + CHECK_CAN_READ_USER_OR_OWNER_JPQL)
    List<Tuple> getUserGlobalRolePrivileges(@Param("id") Long userId);

    @Query("SELECT new com.openkoda.core.flow.Tuple(dbUserRole.role.name, dbUserRole.role.privileges, dbUserRole.organizationId, dbUserRole.organization.name) from UserRole dbUserRole where dbUserRole.userId = :id AND "
            + CHECK_CAN_READ_USER_OR_OWNER_JPQL + " order by dbUserRole.organization.name")
    List<Tuple> getUserRolesAndPrivileges2(@Param("id") Long userId);

    @Query("""
        SELECT
            new com.openkoda.core.flow.Tuple(
            dbUserRole.id,
            dbUserRole.role.name,
            dbUserRole.role.privileges,
            dbUserRole.organizationId,
            COALESCE(dbOrganization.name, ''))
        FROM UserRole dbUserRole
        LEFT JOIN dbUserRole.organization dbOrganization
        WHERE dbUserRole.userId = :id
            OR ((dbUserRole.userId is null)
                AND (dbUserRole.organizationId in
                    (select dbUserRole2.organizationId from UserRole dbUserRole2 where dbUserRole2.userId = :id)))
        ORDER BY dbUserRole.id
        """)
    List<Tuple> getUserRolesAndPrivileges(@Param("id") Long userId);

    @Query("SELECT new com.openkoda.core.flow.Tuple(dbUserRole.organizationId, dbUserRole.role.privileges) from UserRole dbUserRole where dbUserRole.userId = :id and dbUserRole.organizationId is not null AND "
            + CHECK_CAN_READ_USER_OR_OWNER_JPQL)
    List<Tuple> getUserOrganizationRolePrivileges(@Param("id") Long userId);

    @Query("SELECT u.updatedOn > :since from User u where u.id = :id")
    Optional<Boolean> wasModifiedSince(@Param("id") Long userId, @Param("since") LocalDateTime since);

    @Modifying
    @Transactional
    @Query("UPDATE User u SET u.updatedOn = CURRENT_TIMESTAMP where u.id = :id")
    void setUserAsModified(@Param("id") Long userId);

    @Query(value = "SELECT dbUserRole.user from UserRole dbUserRole where dbUserRole.userId = :userId and dbUserRole.organizationId = :organizationId and dbUserRole.role.privileges like %:privilege% AND "
            + CHECK_CAN_READ_USER_OR_OWNER_JPQL)
    User getUserByIdInOrganizationWithPrivilege(@Param("userId") Long userId,
                                                @Param("organizationId") Long organizationId,
                                                @Param("privilege") String privilege);

    @Query("SELECT u.email FROM User u WHERE u.id = :id")
    String findUserEmailByUserId(Long id);
}
