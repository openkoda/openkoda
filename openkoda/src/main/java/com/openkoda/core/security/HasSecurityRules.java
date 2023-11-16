/*
MIT License

Copyright (c) 2016-2023, Openkoda CDX Sp. z o.o. Sp. K. <openkoda.com>

Permission is hereby granted, free of charge, to any person obtaining a copy of this software
 and associated documentation files (the "Software"), to deal in the Software without restriction, 
including without limitation the rights to use, copy, modify, merge, publish, distribute, 
sublicense, and/or sell copies of the Software, and to permit persons to whom the Software 
is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice 
shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, 
EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES 
OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE 
AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS 
OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES 
OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, 
TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION 
WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*/

package com.openkoda.core.security;


import com.openkoda.core.form.AbstractOrganizationRelatedEntityForm;
import com.openkoda.core.form.FrontendMappingFieldDefinition;
import com.openkoda.core.multitenancy.TenantResolver;
import com.openkoda.core.tracker.LoggingComponentWithRequestId;
import com.openkoda.model.*;
import com.openkoda.model.common.EntityWithRequiredPrivilege;
import com.openkoda.model.common.LongIdEntity;
import com.openkoda.model.common.OrganizationRelatedEntity;
import jakarta.persistence.criteria.*;
import org.springframework.data.jpa.domain.Specification;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiFunction;

import static com.openkoda.model.PrivilegeNames.*;

/**
 * This interface is introduced to group all security-rules of any kind
 */
public interface HasSecurityRules extends LoggingComponentWithRequestId {

    //Building blocks
    String BB_CLOSE = ")";
    String BB_OPEN = "(";
    String BB_STRING_CLOSE = "')";
    String BB_STRING_OPEN = "('";
    String BB_JPQL_OPEN = "(?#{";
    String BB_JPQL_CLOSE = "})";
    String STRING_CLOSE_COMMA = "' ,";
    String OR = " OR ";
    String IN = " IN ";
    String IS_TRUE = " = TRUE";
    String JPQL_CLOSE_IS_TRUE = BB_JPQL_CLOSE + IS_TRUE;
    String HASH_ORGANIZATION_ID = "#organizationId";

    String HAS_GLOBAL_PRIVILEGE_STRING_OPEN = "principal.hasGlobalPrivilege" + BB_STRING_OPEN;
    String HAS_ORG_PRIVILEGE_OPEN = "principal.hasOrgPrivilege" + BB_STRING_OPEN;
    String HAS_GLOBAL_OR_ORG_PRIVILEGE_STRING_OPEN = "principal.hasGlobalOrOrgPrivilege" + BB_STRING_OPEN;
    String HAS_ORG_PRIVILEGE_CLOSE = STRING_CLOSE_COMMA + HASH_ORGANIZATION_ID + BB_CLOSE;
    String GLOBAL_PRIVILEGES = "?#{principal.globalPrivileges}";
    String ORGANIZATION_IDS = "?#{principal.organizationIds}";

    //Basic, single privilege based rules - can be directly used in @PreAuthorize
    String CHECK_IS_SPOOFED =                       "(principal.isSpoofed())";
    String CHECK_IS_THIS_USERID =                   "(principal.getUserId() == #userId)";
    String CHECK_CAN_READ_FRONTEND_RESOURCES =                     HAS_GLOBAL_PRIVILEGE_STRING_OPEN + _readFrontendResource + BB_STRING_CLOSE;
//    String CHECK_CAN_READ_ORG_DATA =                HAS_GLOBAL_PRIVILEGE_STRING_OPEN + _readOrgData               + BB_STRING_CLOSE;
    String CHECK_CAN_MANAGE_FRONTEND_RESOURCES =    HAS_GLOBAL_PRIVILEGE_STRING_OPEN + _manageFrontendResource    + BB_STRING_CLOSE;
    String CHECK_CAN_EDIT_ATTRIBUTES =              HAS_GLOBAL_PRIVILEGE_STRING_OPEN + _canEditAttributes         + BB_STRING_CLOSE;
    String CHECK_CAN_SEE_ATTRIBUTES =               HAS_GLOBAL_PRIVILEGE_STRING_OPEN + _canSeeAttributes          + BB_STRING_CLOSE;
    String CHECK_CAN_DEFINE_ATTRIBUTES =            HAS_GLOBAL_PRIVILEGE_STRING_OPEN + _canSeeAttributes          + BB_STRING_CLOSE;
    String CHECK_CAN_MANAGE_EVENT_LISTENERS =       HAS_GLOBAL_PRIVILEGE_STRING_OPEN + _canManageBackend          + BB_STRING_CLOSE;
    String CHECK_CAN_READ_SUPPORT_DATA =            HAS_GLOBAL_PRIVILEGE_STRING_OPEN + _canReadSupportData        + BB_STRING_CLOSE;
    String CHECK_CAN_MANAGE_SUPPORT_DATA =          HAS_GLOBAL_PRIVILEGE_STRING_OPEN + _canManageSupportData      + BB_STRING_CLOSE;
    String CHECK_CAN_READ_ROLES =                   HAS_GLOBAL_PRIVILEGE_STRING_OPEN + _canReadBackend            + BB_STRING_CLOSE;
    String CHECK_CAN_MANAGE_ROLES =                 HAS_GLOBAL_PRIVILEGE_STRING_OPEN + _canManageBackend          + BB_STRING_CLOSE;
    String CHECK_CAN_READ_USER_DATA =               HAS_GLOBAL_PRIVILEGE_STRING_OPEN + _readUserData              + BB_STRING_CLOSE;
    String CHECK_CAN_READ_ORG_AUDIT =               HAS_GLOBAL_PRIVILEGE_STRING_OPEN + _readOrgAudit              + BB_STRING_CLOSE;
    String CHECK_CAN_MANAGE_USER_ROLES =            HAS_GLOBAL_PRIVILEGE_STRING_OPEN + _manageUserRoles           + BB_STRING_CLOSE;
    String CHECK_CAN_IMPERSONATE =                  HAS_GLOBAL_PRIVILEGE_STRING_OPEN + _canImpersonate            + BB_STRING_CLOSE;
    String CHECK_CAN_ACCESS_GLOBAL_SETTINGS =       HAS_GLOBAL_PRIVILEGE_STRING_OPEN + _canAccessGlobalSettings   + BB_STRING_CLOSE;
    String CHECK_CAN_READ_BACKEND =                 HAS_GLOBAL_PRIVILEGE_STRING_OPEN + _canReadBackend            + BB_STRING_CLOSE;
    String CHECK_CAN_MANAGE_BACKEND =               HAS_GLOBAL_PRIVILEGE_STRING_OPEN + _canManageBackend          + BB_STRING_CLOSE;

    //More complex rules - can be directly used in @PreAuthorize
    String CHECK_CAN_MANAGE_ORG_DATA =              HAS_GLOBAL_OR_ORG_PRIVILEGE_STRING_OPEN + _manageOrgData  + HAS_ORG_PRIVILEGE_CLOSE;
    String CHECK_CAN_READ_ORG_DATA =                HAS_GLOBAL_OR_ORG_PRIVILEGE_STRING_OPEN + _readOrgData  + HAS_ORG_PRIVILEGE_CLOSE;
    String CHECK_CAN_READ_USER_SETTINGS =           BB_OPEN +  HAS_GLOBAL_OR_ORG_PRIVILEGE_STRING_OPEN + _readUserData + HAS_ORG_PRIVILEGE_CLOSE + OR  + CHECK_IS_THIS_USERID + BB_CLOSE;
    String CHECK_CAN_MANAGE_USER_SETTINGS =         BB_OPEN +  HAS_GLOBAL_OR_ORG_PRIVILEGE_STRING_OPEN + _manageUserData + HAS_ORG_PRIVILEGE_CLOSE + OR  + CHECK_IS_THIS_USERID + BB_CLOSE;
    String CHECK_CAN_IMPERSONATE_OR_IS_SPOOFED =    BB_OPEN + CHECK_CAN_IMPERSONATE         + OR + CHECK_IS_SPOOFED                                                 + BB_CLOSE;
    String CHECK_CAN_READ_FACEBOOK_USER =           BB_OPEN + CHECK_CAN_READ_USER_DATA + OR + "principal.user.facebookUser.facebookId == #facebookId"          + BB_CLOSE;
    String CHECK_CAN_READ_GOOGLE_USER =             BB_OPEN + CHECK_CAN_READ_USER_DATA + OR + "principal.user.googleUser.googleId == #googleId"                + BB_CLOSE;
    String CHECK_CAN_READ_LDAP_USER =               BB_OPEN + CHECK_CAN_READ_USER_DATA + OR + "principal.user.ldapUser.uid == #uid"                            + BB_CLOSE;
    String CHECK_CAN_READ_SALESFORCE_USER =         BB_OPEN + CHECK_CAN_READ_USER_DATA + OR + "principal.user.salesforceUser.salesforceId == #salesforceId"    + BB_CLOSE;
    String CHECK_CAN_READ_LINKEDIN_USER =           BB_OPEN + CHECK_CAN_READ_USER_DATA + OR + "principal.user.linkedinUser.linkedinId == #linkedinId"          + BB_CLOSE;
    String CHECK_CAN_INVITE_USER_TO_ORG =           HAS_GLOBAL_OR_ORG_PRIVILEGE_STRING_OPEN + _manageOrgData               + "', #organization.id)";
    String CHECK_CAN_SAVE_USER_ROLES =              HAS_GLOBAL_OR_ORG_PRIVILEGE_STRING_OPEN + _manageUserRoles       + "', #organizationId)";

    String CHECK_CAN_EDIT_USER_ATTRIBUTES =         BB_OPEN + CHECK_CAN_EDIT_ATTRIBUTES     + OR +  HAS_GLOBAL_PRIVILEGE_STRING_OPEN    + _canEditUserAttributes    + BB_STRING_CLOSE       + BB_CLOSE;
    String CHECK_CAN_EDIT_ORG_ATTRIBUTES =          BB_OPEN + CHECK_CAN_EDIT_ATTRIBUTES     + OR +  HAS_ORG_PRIVILEGE_OPEN              + _canEditOrgAttributes     + HAS_ORG_PRIVILEGE_CLOSE + BB_CLOSE;

    //Basic JPQL rules - can be used in @Query
    String CHECK_CAN_READ_FRONTEND_RESOURCES_JPQL = BB_JPQL_OPEN + CHECK_CAN_READ_FRONTEND_RESOURCES + JPQL_CLOSE_IS_TRUE;
    String CHECK_CAN_READ_ROLES_JPQL =              BB_JPQL_OPEN + CHECK_CAN_READ_ROLES       + JPQL_CLOSE_IS_TRUE;
    String CHECK_CAN_MANAGE_ROLES_JPQL =            BB_JPQL_OPEN + CHECK_CAN_MANAGE_ROLES     + JPQL_CLOSE_IS_TRUE;
    String CHECK_CAN_READ_ORG_AUDIT_JPQL =          BB_JPQL_OPEN + CHECK_CAN_READ_ORG_AUDIT   + JPQL_CLOSE_IS_TRUE;

    //More complex JPQL rules - can be used in @Query
    String CHECK_CAN_MANAGE_FRONTEND_RESOURCES_OR_HAS_REQUIRED_PRIVILEGE_JPQL =  BB_OPEN + BB_JPQL_OPEN + CHECK_CAN_READ_FRONTEND_RESOURCES + OR + CHECK_CAN_MANAGE_FRONTEND_RESOURCES + JPQL_CLOSE_IS_TRUE + OR + "dbFrontendResource.requiredPrivilege" + IN + GLOBAL_PRIVILEGES + BB_CLOSE;
    String CHECK_CAN_READ_ORG_DATA_OR_IS_ORG_MEMEBER_JPQL =       BB_OPEN + BB_JPQL_OPEN + CHECK_CAN_READ_ORG_DATA                                  + JPQL_CLOSE_IS_TRUE + OR + "dbOrganization.id" + IN + ORGANIZATION_IDS             + BB_CLOSE;
    String CHECK_CAN_READ_USER_OR_OWNER_JPQL =                    BB_OPEN + BB_JPQL_OPEN + CHECK_CAN_READ_USER_DATA + JPQL_CLOSE_IS_TRUE + OR + "dbUserRole.userId = ?#{principal.user.id}"             + BB_CLOSE;
    String CHECK_CAN_MANAGE_USER_ROLES_JPQL =                     BB_OPEN + BB_JPQL_OPEN + CHECK_CAN_MANAGE_USER_ROLES                              + JPQL_CLOSE_IS_TRUE + OR + "dbUserRole.organizationId" + IN + ORGANIZATION_IDS     + BB_CLOSE;

    //Lambda rules
    BiFunction<OrganizationUser, LongIdEntity, Boolean> CHECK_IS_NEW_USER_OR_OWNER =
            (u, a) -> (a) == null
            || ((EntityWithRequiredPrivilege) a).getRequiredReadPrivilege() == null
            || u.hasGlobalPrivilege(((EntityWithRequiredPrivilege) a).getRequiredReadPrivilege());
    BiFunction<OrganizationUser, LongIdEntity, Boolean> CHECK_IF_CAN_WRITE_USER =
            (u, a) -> (a) == null
            || ((EntityWithRequiredPrivilege) a).getRequiredWritePrivilege() == null
            || u.hasGlobalPrivilege(((EntityWithRequiredPrivilege) a).getRequiredWritePrivilege());


    //Methods
    default boolean hasGlobalPrivilege(String p, Set<String> globalPrivileges) {
        if (p == null) { return false; }
        return globalPrivileges.contains(p);
    }

    default boolean hasOrgPrivilege(String p, Long orgId, Map<Long, Set<String>> organizationPrivileges) {
        if (p == null || orgId == null) { return false; }
        return organizationPrivileges.containsKey(orgId) && organizationPrivileges.get(orgId).contains(p);
    }

    default boolean hasGlobalPrivilege(PrivilegeBase p, Set<String> globalPrivileges) {
        if (p == null) { return false; }
        return globalPrivileges.contains(p.name());
    }

    default boolean hasOrgPrivilege(PrivilegeBase p, Long orgId, Map<Long, Set<String>> organizationPrivileges) {
        if (p == null || orgId == null) { return false; }
        return organizationPrivileges.containsKey(orgId) && organizationPrivileges.get(orgId).contains(p.name());
    }

    default boolean hasGlobalOrOrgPrivilege(PrivilegeBase privilege, Long orgId, Set<String> globalPrivileges, Map<Long, Set<String>> organizationPrivileges) {
        return hasGlobalPrivilege(privilege, globalPrivileges) || hasOrgPrivilege(privilege, orgId, organizationPrivileges);
    }

    default boolean hasGlobalOrOrgPrivilege(String privilegeName, Long orgId, Set<String> globalPrivileges, Map<Long, Set<String>> organizationPrivileges) {
        return hasGlobalPrivilege(privilegeName, globalPrivileges) || hasOrgPrivilege(privilegeName, orgId, organizationPrivileges);
    }

    default boolean hasGlobalPrivilege(PrivilegeBase p) {
        if (p == null) { return false; }
        return hasGlobalPrivilege(p.name());
    }

    default boolean hasGlobalOrOrgPrivilege(User u, String p, Long orgId) {
        Collection<UserRole> urs = u.getRoles();
        for( UserRole ur : urs ) {
            if (ur.isGlobal() && ur.getRole().hasPrivilege(p)) {
                return true;
            }
            if (!ur.isGlobal() && ur.getOrganizationId().equals(orgId) && ur.getRole().hasPrivilege(p)) {
                return true;
            }
        }
        return false;
    }
    default boolean hasGlobalPrivilege(String p) {
        Optional<OrganizationUser> user = UserProvider.getFromContext();
        if (user.isPresent()) {
            return user.get().hasGlobalPrivilege(p);
        }
        return false;
    }

    default boolean hasOrgPrivilege(PrivilegeBase p, Long orgId) {
        if (p == null) { return false; }
        return hasOrgPrivilege(p.name(), orgId);
    }

    default boolean hasOrgPrivilege(String p, Long orgId) {
        Optional<OrganizationUser> user = UserProvider.getFromContext();
        if (user.isPresent()) {
            return user.get().hasOrgPrivilege(p, orgId);
        }
        return false;
    }

    default boolean hasGlobalOrOrgPrivilege(String p, Long orgId) {
        return hasGlobalPrivilege(p) || hasOrgPrivilege(p, orgId);
    }

    default boolean hasGlobalOrOrgPrivilege(PrivilegeBase p, Long orgId) {
        return hasGlobalOrOrgPrivilege(p.name(), orgId);
    }

    default boolean canSeeAdminPanel(){
        return hasGlobalPrivilege(Privilege.canAccessGlobalSettings) || hasGlobalPrivilege(Privilege.canReadBackend) || hasGlobalPrivilege(Privilege.canReadSupportData)
                || hasGlobalPrivilege(Privilege.readUserData) || hasGlobalPrivilege(Privilege.readOrgData) || hasGlobalPrivilege(Privilege.readFrontendResource);
    }
    default Optional<OrganizationUser> getLoggedOrganizationUser() {
        return UserProvider.getFromContext();
    }

    default boolean canReadFieldInOrganization(FrontendMappingFieldDefinition field, LongIdEntity entity, Long organizationId) {
        return hasFieldPrivileges(field.readPrivilege, field.canReadCheck, entity, organizationId);
    }

    default boolean canReadField(FrontendMappingFieldDefinition field, LongIdEntity entity) {
        return hasFieldPrivileges(field.readPrivilege, field.canReadCheck, entity, null);
    }

    default boolean canReadOption(OptionWithPrivilege option, Long orgId) {
        return hasOrgPrivilege(option.getPrivilege(), orgId) || hasGlobalPrivilege(option.getPrivilege());
    }

    default boolean canReadGlobalField(FrontendMappingFieldDefinition field) {
        return hasGlobalPrivilege(field.readPrivilege);
    }

    default boolean canWriteFieldInOrganization(FrontendMappingFieldDefinition field, LongIdEntity entity, Long organizationId) {
        return hasFieldPrivileges(field.writePrivilege, field.canWriteCheck, entity, organizationId);
    }

    default boolean canWriteField(FrontendMappingFieldDefinition field, LongIdEntity entity) {
        return hasFieldPrivileges(field.writePrivilege, field.canWriteCheck, entity, null);
    }

    default boolean canWriteGlobalField(FrontendMappingFieldDefinition field) {
        return hasGlobalPrivilege(field.writePrivilege);
    }
    default boolean isOrganizationRelated(Class<?> cls){
        return OrganizationRelatedEntity.class.isAssignableFrom(cls);
    }
    default boolean requiresPrivilege(Class<?> cls){
        return EntityWithRequiredPrivilege.class.isAssignableFrom(cls);
    }
    default boolean isManyOrganizationRelated(Class<?> cls){
        return IsManyOrganizationsRelatedEntity.class.isAssignableFrom(cls);
    }

    default boolean hasFieldPrivileges(
            PrivilegeBase fieldPrivilege,
            BiFunction<OrganizationUser, LongIdEntity, Boolean> check,
            LongIdEntity entity,
            Long organizationId) {

        Optional<OrganizationUser> u = UserProvider.getFromContext();

        if (!u.isPresent()) {
            return false;
        }

        OrganizationUser user = u.get();

        //if user has global privilege, then he has access to the field
        boolean canDo = user.hasGlobalPrivilege(fieldPrivilege);
        boolean entityExists = entity != null;


        if (!canDo) {

            if (entityExists) {
                //if entity exists and is organization related, then check if user has privilege in that organization
                if (!canDo && isOrganizationRelated(entity.getClass())) {
                    OrganizationRelatedEntity e = (OrganizationRelatedEntity) entity;
                    if (e.getOrganizationId() != null) {
                        canDo = user.hasOrgPrivilege(fieldPrivilege, e.getOrganizationId());
                    }
                }

            } else {

                //if the entity does not exist (and user has no global privilege), then it's a new entity
                //check against organization
                if (organizationId != null) {
                    canDo = user.hasOrgPrivilege(fieldPrivilege, organizationId);
                }

            }

        }

        //if still has no access but there is check function, check it against the entity (entity can be null)
        if (!canDo && check != null) {
            Boolean checkResult = check.apply(user, entity);
            canDo = (checkResult != null && checkResult);
        }

        return canDo;
    }

    default boolean isItYou(Long userId) {
        return UserProvider.getFromContext()
                .map(OrganizationUser::getUser)
                .map(User::getId)
                .map(a -> a.equals(userId))
                .orElse(false);
    }


    //Some specialized methods
    default boolean canResetPassword() {
        return hasGlobalPrivilege(Privilege.canResetPassword);
    }

    default boolean canEditUserData() {
        return hasGlobalPrivilege(Privilege.manageUserData);
    }

    default boolean canImpersonate() {
        return hasGlobalPrivilege(Privilege.canImpersonate);
    }

    default boolean isSpoofMode() {
        Optional<OrganizationUser> user = getLoggedOrganizationUser();
        return user.map(OrganizationUser::isSpoofed).orElse(false);
    }

    default boolean isOrgAdmin(Organization org) {
        if (org == null) {
            return false;
        }
        return hasOrgPrivilege(Privilege.manageOrgData, org.getId());
    }

    default boolean isItYou(User user) {
        return user != null && isItYou(user.getId());
    }

    default boolean canSeeEmail(User user) {
        return canSeeEmail(user.getId());
    }

    default boolean canSeeEmail(Long userId) {
        return isItYou(userId) || hasGlobalPrivilege(Privilege.canSeeUserEmail);
    }

    default boolean continueBuildingSearchPredicate(Predicate search) {
        return not(search.getExpressions().isEmpty()
                && Predicate.BooleanOperator.OR.equals(search.getOperator()));
    }


    enum SecurityScope {
        ALL(false, false, false), // all data
        GLOBAL(true, false, false), // orgId == null
        ORGANIZATION(true, false, true), //orgId == current organization
        USER(true, true,false), // all entities (GLOBAL and ORGANIZATION) where user has privileges
        USER_IN_ORGANIZATION(true, true, true); // orgId == current organization and where user has privileges


        SecurityScope(boolean requiresUser ,boolean checkUserPrivileges, boolean currentOrganization) {
            this.checkUserPrivileges = checkUserPrivileges;
            this.currentOrganization = currentOrganization;
        }

        private boolean requiresUser;
        private boolean checkUserPrivileges;
        private boolean currentOrganization;


    }

    default <T> Predicate toSecurePredicate(Specification<T> specification, Enum requiredPrivilege, Root<T> root, CriteriaQuery<?> query, CriteriaBuilder cb, SecurityScope scope) {
        debug("[toPredicate] for Entity:{} ", root.getModel().getName());

        Optional<OrganizationUser> optionalUser = UserProvider.getFromContext();
        Class<?> rootJavaType = root.getModel().getJavaType();

        Predicate search = specification == null ? cb.conjunction() : specification.toPredicate(root, query, cb);

        //security scope is ALL, returning all by predicate
        if (scope == SecurityScope.ALL) {
            return search;
        }

        //all security scopes except ALL require user
        if(!optionalUser.isPresent()) {
            debug("[toPredicate] No user");
            search = cb.disjunction();
        }

        boolean isOrganizationEntity = isOrganizationRelated(rootJavaType);
        if (scope == SecurityScope.GLOBAL) {
            return isOrganizationEntity ? cb.and(search, cb.isNull(root.get("organizationId"))) : search;
        }

        if (scope.currentOrganization) {
            Long currentOrganizationId = TenantResolver.getTenantedResource().organizationId;
            if (!isOrganizationEntity || currentOrganizationId == null) {
                search = cb.disjunction();
            }
            search = cb.and(search, cb.equal(root.get("organizationId"), currentOrganizationId));
            if (scope == SecurityScope.ORGANIZATION) {
                return search;
            }
        }

        if(continueBuildingSearchPredicate(search)) {
            //the code below provide basic security for search
            boolean isEntityWithRequiredPrivilege = requiresPrivilege(rootJavaType);
            boolean isExternalRequiredPrivilege = requiredPrivilege != null;
            boolean hasManyOrganizationsEntity = isManyOrganizationRelated(rootJavaType);
            OrganizationUser user = optionalUser.get();

            Path orgId = isOrganizationEntity ? root.get("organizationId") : null;
            Path orgsId = hasManyOrganizationsEntity ? root.get("organizationIds") : null;
            Path privilege = isEntityWithRequiredPrivilege ? root.get("requiredReadPrivilege") : null;

            debug("[toPredicate] isEntityWithRequiredPrivilege:{} isExternalRequiredPrivilege:{} isOrganizationEntity:{} hasManyOrganizationsEntity:{}",
                    isEntityWithRequiredPrivilege, isExternalRequiredPrivilege, isOrganizationEntity, hasManyOrganizationsEntity);

            if (isExternalRequiredPrivilege) {
                search = getSearchPredicateForExternallyProvidedPrivilege(cb, search, isOrganizationEntity, hasManyOrganizationsEntity, user, orgId, orgsId, requiredPrivilege);
            }

            if (isEntityWithRequiredPrivilege && continueBuildingSearchPredicate(search)) {
                search = getSearchPredicateForEntityBasedPrivilege(cb, search, isOrganizationEntity, hasManyOrganizationsEntity, user, orgId, orgsId, privilege);
            }
        }


        return search;
    }

        //Repository predicates
    default Predicate getSearchPredicateForEntityBasedPrivilege(CriteriaBuilder cb, Predicate search, boolean isOrganizationEntity, boolean hasManyOrganizationsEntity, OrganizationUser user, Expression organizationIdPath, Expression organizationIdsPath, Path requiredPrivilegePath) {

        Predicate entityWithRequiredPrivilegeCheck;
        if (isOrganizationEntity) {

            // check global privilege
            Predicate globalEntityCheck = cb.and(
                    requiredPrivilegePath.isNotNull(),
                    requiredPrivilegePath.in(user.getGlobalPrivileges()));

            // check check org level privilege
            Expression orgPrivilegePair = cb.concat(organizationIdPath, requiredPrivilegePath);
            Predicate organizationEntityCheck = cb.and(
                    organizationIdPath.isNotNull(),
                    requiredPrivilegePath.isNotNull(),
                    orgPrivilegePair.in(user.getOrganizationWithPrivilegePairs()));

            //check passes if has either global or org-level privilege
            entityWithRequiredPrivilegeCheck = cb.or(globalEntityCheck, organizationEntityCheck);
            debug("[getSearchPredicateForExternallyProvidedPrivilege] (entityPrivilege != null AND entityPrivilege in {}) OR " +
                    "(orgId != null AND entityPrivilege != null AND orgId+entityPrivilege in {})", user.getGlobalPrivileges(), user.getOrganizationWithPrivilegePairs());

        } else if (hasManyOrganizationsEntity) {
            // check global privilege
            Predicate globalEntityCheck = cb.and(
                    requiredPrivilegePath.isNotNull(),
                    requiredPrivilegePath.in(user.getGlobalPrivileges()));

            // check org level privilege
            Expression orgsWithPrivilege = cb.function("arrays_suffix", Array.class, organizationIdsPath, requiredPrivilegePath);
            Predicate inAnyOrgCheck = cb.isTrue(cb.function("arrays_overlap", Boolean.class, orgsWithPrivilege, cb.literal(user.getOrganizationWithPrivilegePairs())));
            Predicate organizationEntityCheck = cb.and(organizationIdsPath.isNotNull(), inAnyOrgCheck);

            //check passes if has either global or org-level privilege
            entityWithRequiredPrivilegeCheck = cb.or(globalEntityCheck, organizationEntityCheck);


        } else {
            entityWithRequiredPrivilegeCheck = cb.and(
                    requiredPrivilegePath.isNotNull(),
                    requiredPrivilegePath.in(user.getGlobalPrivileges()));
        }

        search = cb.and(cb.or(requiredPrivilegePath.isNull(), entityWithRequiredPrivilegeCheck), search);
        return search;

    }

    default Predicate getSearchPredicateForExternallyProvidedPrivilege(CriteriaBuilder cb, Predicate search, boolean isOrganizationEntity, boolean hasManyOrganizationsEntity, OrganizationUser user,
                           Expression organizationIdPath, Expression organizationIdsPath, Enum requiredPrivilege) {

        boolean hasGlobalPrivilegeForExternalRequiredPrivilege;
        hasGlobalPrivilegeForExternalRequiredPrivilege = user.hasGlobalPrivilege(requiredPrivilege.name());

        // from here user has NOT the global privilege
        if (not(hasGlobalPrivilegeForExternalRequiredPrivilege)) {
            debug("[getSearchPredicateForExternallyProvidedPrivilege] user has NOT the global privilege");
            Set<Long> orgs = user.getOrganizationIdsWithPrivilege(requiredPrivilege.name());
            if (isOrganizationEntity) {
                //user has NOT the global privilege and the entity is organization related
                // check if user has org privilege
                search = cb.and(
                        organizationIdPath.isNotNull(),
                        organizationIdPath.in(orgs),
                        search);
                debug("[getSearchPredicateForExternallyProvidedPrivilege] org != null AND org id in {}", orgs);
            } else if (hasManyOrganizationsEntity) {
                //user has NOT the global privilege and the entity is assigned to many organizations
                // check if user has org privilege in any of related organizations
                Predicate inAnyOrgCheck = cb.isTrue(cb.function("arrays_overlap", Boolean.class, organizationIdsPath, cb.literal(orgs)));
                search = cb.and(
                        organizationIdsPath.isNotNull(),
                        inAnyOrgCheck,
                        search);
                debug("[getSearchPredicateForExternallyProvidedPrivilege] org != null AND any org id in {}", orgs);
            } else {
                // the entity is global, and user has no global privilege, return empty result
                debug("[getSearchPredicateForExternallyProvidedPrivilege] empty result");
                search = cb.disjunction();
            }

        }
        return search;

    }

    default void assertFormConsistency(AbstractOrganizationRelatedEntityForm f) {

    }

}
