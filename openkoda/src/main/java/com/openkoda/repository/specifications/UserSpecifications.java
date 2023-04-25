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

package com.openkoda.repository.specifications;

import com.openkoda.core.flow.Tuple;
import com.openkoda.core.helper.ReadableCode;
import com.openkoda.core.security.OrganizationUser;
import com.openkoda.core.security.UserProvider;
import com.openkoda.model.Privilege;
import com.openkoda.model.User;
import com.openkoda.model.UserRole;
import jakarta.persistence.criteria.*;
import org.springframework.data.jpa.domain.Specification;

import java.util.Arrays;
import java.util.Optional;

/**
 *
 *
 * @author Arkadiusz Drysch (adrysch@stratoflow.com)
 *
 */
public class UserSpecifications implements ReadableCode {

    public static Specification<Tuple> dict() {
        return new Specification<Tuple>() {
            @Override
            public Predicate toPredicate(Root<Tuple> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                return null;
            }
        };
    }
    public static Specification<UserRole> searchUserRoleSpecification(Long specificOrganizationId) {

        return new Specification<UserRole>() {
            @Override
            public Predicate toPredicate(Root<UserRole> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

                if (specificOrganizationId != null) {
                    return cb.equal(root.get("organizationId"), specificOrganizationId);
                }

                return cb.conjunction();

            }

        };

    }


    public static Specification<User> searchSpecification(Long specificOrganizationId) {

        return new Specification<User>() {
            @Override
            public Predicate toPredicate(Root<User> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                Optional<OrganizationUser> optionalUser = UserProvider.getFromContext();
                Predicate noResult = cb.disjunction();

                if(!optionalUser.isPresent()) {
                    return noResult;
                }
                OrganizationUser organizationUser = optionalUser.get();
                if (organizationUser.hasGlobalPrivilege(Privilege.readUserData) && specificOrganizationId == null) {
                    return cb.conjunction();
                }
                CollectionJoin<User, UserRole> roles = root.joinCollection("roles");
                if (specificOrganizationId != null) {
                    if ( organizationUser.hasGlobalOrOrgPrivilege(Privilege.readUserData, specificOrganizationId) ) {
                        query.distinct(true);
                        return roles.get("organizationId").in(Arrays.asList(specificOrganizationId));
                    }
                    return noResult;
                }

                if (organizationUser.getOrganizationIds().isEmpty()) {
                    return noResult;
                }

                return roles.get("organizationId").in(organizationUser.getOrganizationIds());

            }

        };
    }
}
