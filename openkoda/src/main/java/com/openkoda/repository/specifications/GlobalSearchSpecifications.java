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

package com.openkoda.repository.specifications;

import com.openkoda.core.security.OrganizationUser;
import com.openkoda.core.security.UserProvider;
import com.openkoda.model.GlobalEntitySearch;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;

import java.util.Optional;

public class GlobalSearchSpecifications {

    public static Specification<GlobalEntitySearch> createSpecification(String searchTerm) {
        return new Specification<GlobalEntitySearch>() {
            @Override
            public Predicate toPredicate(Root<GlobalEntitySearch> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder cb) {
                Predicate search = cb.like(cb.lower(root.get("indexString")), "%" + StringUtils.lowerCase(StringUtils.defaultString(searchTerm, "")) + "%");
                Optional<OrganizationUser> optionalUser = UserProvider.getFromContext();

                if (!optionalUser.isPresent()) {
                    return cb.disjunction();
                }

                OrganizationUser user = optionalUser.get();

                search = cb.and(
                        cb.or(cb.isNull(root.get("requiredReadPrivilege")), root.get("requiredReadPrivilege").in(user.getGlobalPrivileges())),
                        search);

                return search;
            }
        };
    }
}
