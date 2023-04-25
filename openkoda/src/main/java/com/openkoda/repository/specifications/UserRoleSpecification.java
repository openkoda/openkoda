package com.openkoda.repository.specifications;

import com.openkoda.model.UserRole;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;

public class UserRoleSpecification {

    public static Specification<UserRole> getUserRolesForOrganizations() {

        return new Specification<UserRole>() {
            @Override
            public Predicate toPredicate(Root<UserRole> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                return root.get("userId").isNull();
            }
        };
    }

    public static Specification<UserRole> getUserRolesForUsers() {

        return new Specification<UserRole>() {
            @Override
            public Predicate toPredicate(Root<UserRole> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                return root.get("userId").isNotNull();
            }
        };
    }
}
