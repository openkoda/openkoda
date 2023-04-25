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
                Predicate search = cb.like(root.get("indexString"), "%" + StringUtils.lowerCase(StringUtils.defaultString(searchTerm, "")) + "%");
                Optional<OrganizationUser> optionalUser = UserProvider.getFromContext();

                if (!optionalUser.isPresent()) {
                    return cb.disjunction();
                }

                OrganizationUser user = optionalUser.get();

                search = cb.and(
                        root.get("requiredReadPrivilege").in(user.getGlobalPrivileges()),
                        search);

                return search;
            }
        };
    }
}
