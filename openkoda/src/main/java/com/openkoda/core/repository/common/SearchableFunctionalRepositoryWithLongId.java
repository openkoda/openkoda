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

package com.openkoda.core.repository.common;

import com.openkoda.core.form.AbstractEntityForm;
import com.openkoda.core.multitenancy.TenantResolver;
import com.openkoda.core.security.HasSecurityRules;
import com.openkoda.core.security.OrganizationUser;
import com.openkoda.core.security.UserProvider;
import com.openkoda.core.tracker.LoggingComponentWithRequestId;
import com.openkoda.model.common.*;
import com.openkoda.repository.SearchableRepositories;
import jakarta.persistence.criteria.*;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Constructor;
import java.util.*;

/**
 * Most of the entities in Openkoda have Long ID.
 * Also, repositories are often used in chained lambda expressions.
 * Some repositories provide standard functionality for secured search.
 * This interface simplifies development by introducing repository interface assuming both
 * functional and searchable interface and Long ID entities.
 * All the functions here are and should use secureSpecification.
 *
 * @author Arkadiusz Drysch (adrysch@stratoflow.com)
 *
 */
@NoRepositoryBean
public interface SearchableFunctionalRepositoryWithLongId<T extends SearchableEntity> extends FunctionalRepositoryWithLongId<T>, UnscopedSecureRepository<T>, ScopedSecureRepository<T>, JpaSpecificationExecutor<T>, ModelConstants, HasSecurityRules, LoggingComponentWithRequestId {

    default <S extends T> boolean hasWritePrivilegeForEntity(SecurityScope scope, S s){
        if(requiresPrivilege(s.getClass())){
            String requiredPrivilege = ((EntityWithRequiredPrivilege) s).getRequiredWritePrivilege();
            if (requiredPrivilege == null) {
                return true;
            }
            Optional<OrganizationUser> userOptional = UserProvider.getFromContext();
            if(userOptional.isPresent()){
                OrganizationUser user = userOptional.get();
                if(user.hasGlobalPrivilege(requiredPrivilege)){
                    return true;
                } else if(isOrganizationRelated(s.getClass())){
                    Long organizationId = ((OrganizationRelatedEntity) s).getOrganizationId();
                    return user.hasOrgPrivilege(requiredPrivilege, organizationId);
                }
            }
        } else {
            return true;
        }
        return false;
    }

    /**
     * Constructs search specification using `LIKE` clause on 'indexString' field for each search term provided.
     * LIKE clauses are combined with AND. eg.:
     * ['John', 'Smith'] -> (indexString LIKE '%john%') and (indexString LIKE '%smith%')
     * @param searchTerm array of search terms
     */
    default Specification<T> searchSpecification(String ... searchTerm) {
        if (ArrayUtils.isEmpty(searchTerm)) {
            return (root, query, cb) -> cb.conjunction();
        }
        return (root, query, cb) -> {
             Predicate[] searchPredicates = new Predicate[searchTerm.length];
             for (int i = 0; i < searchTerm.length; i++) {
                 searchPredicates[i] = cb.like(cb.lower(root.get("indexString")), "%" + StringUtils.lowerCase(StringUtils.defaultString(searchTerm[i], "")) + "%");
             }
             return cb.and(searchPredicates);
        };
    }

    /**
     * Constructs search specification for organizationId field of the mapped repository entity.
     */
    default Specification<T> organizationIdSpecification(Long organizationId) {
        return (root, query, cb) -> organizationId == null ? cb.conjunction() : cb.equal(root.get("organizationId"), organizationId);
    }

    /**
     * Specification that matches "id" (Long) for entities
     */
    default Specification<T> idSpecification(Long id) {
        return (root, query, cb) -> cb.equal(root.get("id"), id);
    }

    default Specification<T> idsSpecification(Collection<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return (root, query, cb) -> cb.disjunction();
        }
        return (root, query, cb) -> root.get("id").in(ids);
    }

    default Specification<T> fieldSpecification(String fieldName, Object value) {
        return (root, query, cb) -> {
            Expression<String> rr = (value instanceof String) ? cb.toString(root.get(fieldName)) : root.get(fieldName);
            return cb.equal(rr, value);
        };
    }

    /**
     * Specification that wraps provided specification with security checks
     */
    default Specification<T> secureSpecification(SecurityScope scope, Specification<T> specification, Enum requiredPrivilege) {

        return new Specification<T>() {
            @Override
            public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                return toSecurePredicate(specification, requiredPrivilege, root, query, cb, scope);
            }
        };

    }

    /**
     * Specification that wraps provided specification with security checks and imposes constraints on organization
     */
    default Specification<T> secureSpecification(SecurityScope scope, Specification<T> specification, Enum requiredPrivilege, Long organizationId){
        return secureSpecification(scope, Specification.where(specification).and(organizationIdSpecification(organizationId)), requiredPrivilege);
    }

    @Override
    default List<T> search(SecurityScope scope, String fieldName, Object value) {
        Specification<T> s = fieldSpecification(fieldName, value);
        return this.findAll(secureSpecification(scope, s, null));
    }


    @Override
    default List<T> search(SecurityScope scope, Specification<T> specification) {
        return this.findAll(secureSpecification(scope, specification, null));
    }

    @Override
    default List<T> search(SecurityScope scope, Long organizationId) {
        return this.findAll(secureSpecification(scope, null, null, organizationId));
    }

    @Override
    default List<T> search(SecurityScope scope, Long organizationId, Specification<T> specification) {
        return this.findAll(secureSpecification(scope, specification, null, organizationId));
    }

    @Override
    default Page<T> search(SecurityScope scope, int page, int size, String sortField, String sortDirection) {
        return this.findAll(secureSpecification(scope, null, null), PageRequest.of(page, size, Sort.Direction.valueOf(sortDirection), sortField));
    }

    @Override
    default Page<T> search(SecurityScope scope, Specification<T> specification, int page, int size, String sortField, String sortDirection) {
        return this.findAll(secureSpecification(scope, specification, null), PageRequest.of(page, size, Sort.Direction.valueOf(sortDirection), sortField));
    }

    @Override
    default Page<T> search(SecurityScope scope, Long organizationId, Specification<T> specification, int page, int size, String sortField, String sortDirection) {
        return this.findAll(secureSpecification(scope, specification, null, organizationId), PageRequest.of(page, size, Sort.Direction.valueOf(sortDirection), sortField));
    }

    @Override
    default Page<T> search(SecurityScope scope, String searchTerm, int page, int size, String sortField, String sortDirection) {
        return this.findAll(secureSpecification(scope, searchSpecification(searchTerm), null), PageRequest.of(page, size, Sort.Direction.valueOf(sortDirection), sortField));
    }

    @Override
    default Page<T> search(SecurityScope scope, String searchTerm, Specification<T> specification, int page, int size, String sortField, String sortDirection) {
        return this.findAll(secureSpecification(scope, searchSpecification(searchTerm).and(specification), null), PageRequest.of(page, size, Sort.Direction.valueOf(sortDirection), sortField));
    }

    @Override
    default Page<T> search(SecurityScope scope, String searchTerm, Long organizationId, int page, int size, String sortField, String sortDirection) {
        return this.findAll(secureSpecification(scope, searchSpecification(searchTerm), null, organizationId), PageRequest.of(page, size, Sort.Direction.valueOf(sortDirection), sortField));
    }

    @Override
    default Page<T> search(SecurityScope scope, String searchTerm, Long organizationId, Specification<T> specification, Pageable pageable) {
        return this.findAll(secureSpecification(scope, searchSpecification(searchTerm).and(specification), null, organizationId), pageable);
    }

    @Override
    default T findOne(SecurityScope scope, Object idOrEntityOrSpecification) {
        if (idOrEntityOrSpecification == null) { return null; }
        Long id = extractEntityId(idOrEntityOrSpecification);
        if (idOrEntityOrSpecification instanceof Specification s) {
            return findOne(secureSpecification(scope, (Specification<T>)s, null)).orElse(null);
        }
        return findOne(secureSpecification(scope, idSpecification(id), null)).orElse(null);
    }

    @Nullable
    private static Long extractEntityId(Object idOrEntityOrSpecification) {
        Long id = null;
        if (idOrEntityOrSpecification instanceof Number n) {
            id = n.longValue();
        } else if (idOrEntityOrSpecification instanceof String s) {
            id = Long.parseLong(s);
        } else if (idOrEntityOrSpecification instanceof LongIdEntity e) {
            id = e.getId();
        }
        return id;
    }

    @Override
    default List<T> findAll(SecurityScope scope) {
        return findAll(secureSpecification(scope, null, null));
    }

    @Override
    default <S extends T> S saveOne(SecurityScope scope, S entity) {
        if(hasWritePrivilegeForEntity(scope, entity)){
            return save(entity);
        }
        throw new AccessDeniedException("Operation not allowed");
    }

    @Override
    default <S extends T> S saveForm(SecurityScope scope, S entity, AbstractEntityForm form) {
        form.populateToEntity((LongIdEntity) entity);
        return saveOne(scope, entity);
    }

    @Override
    default <S extends T> List<S> saveAll(SecurityScope scope, Object entitiesCollection) {
        Iterable<S> iterable = null;
        if (entitiesCollection == null) {
            return null;
        }
        if (entitiesCollection.getClass().isArray()) {
            iterable = Arrays.asList((S[]) entitiesCollection);
        } else if (entitiesCollection instanceof Iterable i) {
            iterable = i;
        }
        for (S s : iterable) {
            if(!hasWritePrivilegeForEntity(scope, s)){
                throw new AccessDeniedException("Operation not allowed");
            }
        }
        return saveAll(iterable);
    }

    @Override
    @Transactional
    default boolean deleteOne(SecurityScope scope, Object idOrEntity) {
        if (idOrEntity == null) {return false;}
        Long entityId = extractEntityId(idOrEntity);
        //TODO: check writePrivilege
//        if(hasWritePrivilegeForEntity(t)){
        long deletedCount = delete(idSpecification(entityId));
        return deletedCount > 0;
//            return true;
//        }
//        throw new AccessDeniedException("Operation not allowed");
    }

    @Override
    default long deleteAll(SecurityScope scope, Object idsOrEntitiesCollectionOrSpecification) {
        if (idsOrEntitiesCollectionOrSpecification == null) {
            return 0;
        }
        if (idsOrEntitiesCollectionOrSpecification instanceof Specification<?> s) {
            return delete(secureSpecification(scope, (Specification<T>) s, null));
        }
        List<Long> ids = toIdList(idsOrEntitiesCollectionOrSpecification);
        return delete(secureSpecification(scope, idsSpecification(ids), null));
    }

    @Override
    default boolean existsOne(SecurityScope scope, Object idOrEntity) {
        if (idOrEntity == null) {return false;}
        Long entityId = extractEntityId(idOrEntity);
        boolean entityExists = exists(secureSpecification(scope, idSpecification(entityId), null));
        return entityExists;
    }

    @Override
    default boolean existsAny(SecurityScope scope, Object idsOrEntitiesCollectionOrSpecification) {
        if (idsOrEntitiesCollectionOrSpecification == null) {
            return false;
        }
        if (idsOrEntitiesCollectionOrSpecification instanceof Specification<?> s) {
            return exists(secureSpecification(scope, (Specification<T>) s, null));
        }
        List<Long> ids = toIdList(idsOrEntitiesCollectionOrSpecification);
        return exists(secureSpecification(scope, idsSpecification(ids), null));
    }

    private static List<Long> toIdList(Object idsOrEntitiesCollection) {
        Iterable iterable = null;
        if (idsOrEntitiesCollection.getClass().isArray()) {
            iterable = Arrays.asList((Object[]) idsOrEntitiesCollection);
        } else if (idsOrEntitiesCollection instanceof Iterable i) {
            iterable = i;
        }
        if (iterable != null) {
            List<Long> ids = new ArrayList<>();
            for (Object i : iterable) {
                Long id = extractEntityId(i);
                if (id != null) { ids.add(id); }
            }
            return ids;
        }
        return null;
    }

    @Override
    default long count(SecurityScope scope) {
        return count(secureSpecification(scope, null, null));
    }

    @Override
    default long count(SecurityScope scope, String fieldName, Object value) {
        return count(secureSpecification(scope, fieldSpecification(fieldName, value), null));
    }

    @Override
    default long count(SecurityScope scope, Specification<T> specification) {
        return count(secureSpecification(scope, specification, null));
    }

    @Override
    default long count(SecurityScope scope, Long organizationId, Specification<T> specification) {
        return count(secureSpecification(scope, specification, null, organizationId));
    }
    @Override
    default T getNew() {
        try {
            Class entityClass = SearchableRepositories.getGlobalSearchableRepositoryAnnotation(this).entityClass();
            Constructor c = entityClass.getConstructor(Long.class);
            T obj = (T) c.newInstance(TenantResolver.getTenantedResource().organizationId);
            return obj;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    default SearchableRepositoryMetadata getSearchableRepositoryMetadata() {
        Class<?>[] interfaces = this.getClass().getInterfaces();
        if (interfaces == null || interfaces.length == 0) { return null; }
        SearchableRepositoryMetadata gsa = interfaces[0].getAnnotation(SearchableRepositoryMetadata.class);
        return gsa;
    }
}
