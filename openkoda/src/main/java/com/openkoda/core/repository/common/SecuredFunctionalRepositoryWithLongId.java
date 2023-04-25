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

package com.openkoda.core.repository.common;

import com.openkoda.core.security.HasSecurityRules;
import com.openkoda.core.security.OrganizationUser;
import com.openkoda.core.security.UserProvider;
import com.openkoda.core.tracker.LoggingComponentWithRequestId;
import com.openkoda.model.common.EntityWithRequiredPrivilege;
import com.openkoda.model.common.ModelConstants;
import com.openkoda.model.common.OrganizationRelatedEntity;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * A JPA super-interface for secure repositories.
 * The contract of the interface is following:
 * - expose secure operations with using secureSpecification
 * - methods that are considered insecure (present in super-interface) should be overidden and throw
 * AccessDeniedException(OPERATION_NOT_SECURE)
 * @author Arkadiusz Drysch (adrysch@stratoflow.com)
 *
 */
@NoRepositoryBean
public interface SecuredFunctionalRepositoryWithLongId<T> extends FunctionalRepositoryWithLongId<T>, JpaSpecificationExecutor<T>, ModelConstants, HasSecurityRules, LoggingComponentWithRequestId, SecuredRepository<T> {

    String OPERATION_NOT_SECURE = "Operation not secure";

    /**
     * Specification that matches "id" (Long) for entities
     */
    default Specification<T> idSpecification(Long id) {
        return (root, query, cb) -> cb.equal(root.get("id"), id);
    }

    /**
     * Specification that wraps provided specification with security checks
     */
    default Specification<T> secureSpecification(Specification<T> specification, Enum requiredPrivilege) {

        return new Specification<T>() {
            @Override
            public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                return toSecurePredicate(specification, requiredPrivilege, root, query, cb);
            }
        };

    }

    /**
     * Specification that wraps provided specification with security checks and imposes constraints on organization
     */
    default Specification<T> secureSpecification(Specification<T> specification,  Enum requiredPrivilege, Long organizationId){
        return secureSpecification(Specification.where(specification).and(organizationIdSpecification(organizationId)), requiredPrivilege);
    }
    /**
     * Constructs search specification for organizationId field of the mapped repository entity.
     */
    default Specification<T> organizationIdSpecification(Long organizationId) {
        return (root, query, cb) -> organizationId == null ? cb.conjunction() : cb.equal(root.get("organizationId"), organizationId);
    }

    @Override
    default T findOne(Long id) {
        return this.findOne(secureSpecification(idSpecification(id), null)).orElse(null);
    }

    default T findOneWithSpecification(Specification<T> specification) {
        return this.findOne(secureSpecification(specification, null)).orElse(null);
    }

    @Override
    default List<T> findAll() {
        return this.findAll(secureSpecification(null, null));
    }

    @Override
    default long count() {
        return this.count(secureSpecification(null, null));
    }

    @Override
    default Page<T> findAll(Pageable pageable) {
        return this.findAll(secureSpecification(null, null), pageable);
    }

    
    @Override @Transactional
    default <S extends T> S saveOne(S s) {
        if(hasWritePrivilegeForEntity(s)){
            return save(s);
        }
        throw new AccessDeniedException("Operation not allowed");
    }
    @Transactional
    default <S extends T> List<S> save(Iterable<S> iterable) {
        for (S s : iterable) {
            if(!hasWritePrivilegeForEntity(s)){
                throw new AccessDeniedException("Operation not allowed");
            }
        }
        return saveAll(iterable);
    }

    @Override @Transactional
    default boolean deleteOne(T t) {
        if (t == null) {return false;}
        if(hasWritePrivilegeForEntity(t)){
            delete(t);
            return true;
        }
        throw new AccessDeniedException("Operation not allowed");
    }

    @Override @Transactional
    default boolean deleteOne(Long id) {
        return deleteOne(findOne(id));
    }

    @Transactional
    default void delete(Iterable<? extends T> iterable) {
        for (T t : iterable) {
            if(!hasWritePrivilegeForEntity(t)){
                throw new AccessDeniedException("Operation not allowed");
            }
        }
        deleteAll(iterable);
    }
    @Transactional
    default <S extends T> S saveOneAndFlush(S s) {
        if(hasWritePrivilegeForEntity(s)){
            return saveAndFlush(s);
        }
        throw new AccessDeniedException("Operation not allowed");
    }

    default <S extends T> boolean hasWritePrivilegeForEntity(S s){
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


    default T findOne(Long id, Enum requiredPrivilege) {
        return this.findOne(secureSpecification(idSpecification(id), requiredPrivilege)).orElse(null);
    }

    default List<T> findAll(Enum requiredPrivilege) {
        return this.findAll(secureSpecification(null, requiredPrivilege));
    }

    default long count(Enum requiredPrivilege) {
        return this.count(secureSpecification(null, requiredPrivilege));
    }

    default Page<T> findAll(Enum requiredPrivilege, Pageable pageable) {
        return this.findAll(secureSpecification(null, requiredPrivilege), pageable);
    }

    default Page<T> findAll(Specification<T> specification, Enum requiredPrivilege, Pageable pageable) {
        return this.findAll(secureSpecification(specification, requiredPrivilege), pageable);
    }

    default List<T> findBy(Specification<T> specification) {
        return this.findAll(secureSpecification(specification, null));
    }

    default List<T> findBy(String fieldName, Object value) {
        Specification<T> s = (root, query, cb) -> cb.equal(root.get(fieldName), value);
        return this.findAll(secureSpecification(s, null));
    }

    default Page<T> findBy(Specification<T> specification, Pageable pageable) {
        return this.findAll(secureSpecification(specification, null), pageable);
    }

    default List<T> findAll(Specification<T> specification, Enum requiredPrivilege, Long organizationId){
        return this.findAll(secureSpecification(specification, requiredPrivilege, organizationId));
    }

    default Page<T> findAll(Specification<T> specification, Enum requiredPrivilege, Long organizationId, Pageable pageable){
        return this.findAll(secureSpecification(specification, requiredPrivilege, organizationId), pageable);
    }


    default long countBy(Specification<T> specification) {
        return this.count(secureSpecification(specification, null));
    }


    @Override @Deprecated
    Page<T> findAll(Specification<T> specification, Pageable pageable);


    @Override @Deprecated
    List<T> findAll(Specification<T> specification);

    @Override @Deprecated
    long count(Specification<T> specification);

    @Override @Deprecated
    Optional<T> findOne(Specification<T> specification);

    @Override @Deprecated
    default List<T> findAll(Sort sort) { throw new AccessDeniedException(OPERATION_NOT_SECURE); }

    @Override @Deprecated
    default List<T> findAllById(Iterable<Long> iterable) { throw new AccessDeniedException(OPERATION_NOT_SECURE); }

    @Override @Deprecated
    <S extends T> List<S> saveAll(Iterable<S> iterable);

    @Override @Deprecated
    default void flush() { throw new AccessDeniedException(OPERATION_NOT_SECURE); }

    @Override @Deprecated
    <S extends T> S saveAndFlush(S s);

    @Override @Deprecated
    default void deleteInBatch(Iterable<T> iterable) { throw new AccessDeniedException(OPERATION_NOT_SECURE); }

    @Override @Deprecated
    default void deleteAllInBatch() { throw new AccessDeniedException(OPERATION_NOT_SECURE); }

    @Override @Deprecated
    default T getOne(Long aLong) { throw new AccessDeniedException(OPERATION_NOT_SECURE); }

    @Override @Deprecated
    default <S extends T> List<S> findAll(Example<S> example) { throw new AccessDeniedException(OPERATION_NOT_SECURE); }

    @Override @Deprecated
    default <S extends T> List<S> findAll(Example<S> example, Sort sort) { throw new AccessDeniedException(OPERATION_NOT_SECURE); }

    @Override @Deprecated
    default List<T> findAll(Specification<T> specification, Sort sort) { throw new AccessDeniedException(OPERATION_NOT_SECURE); }


   @Override @Deprecated
    <S extends T> S save(S s);

    @Override @Deprecated
    default boolean exists(Long aLong) { throw new AccessDeniedException(OPERATION_NOT_SECURE); }


    @Override @Deprecated
    void delete(T t);

    @Override @Deprecated
    void deleteAll(Iterable<? extends T> iterable);

    @Override @Deprecated
    default void deleteAll() { throw new AccessDeniedException(OPERATION_NOT_SECURE); }

    @Override @Deprecated
    default <S extends T> Optional<S> findOne(Example<S> example) { throw new AccessDeniedException(OPERATION_NOT_SECURE); }

    @Override @Deprecated
    default <S extends T> Page<S> findAll(Example<S> example, Pageable pageable) { throw new AccessDeniedException(OPERATION_NOT_SECURE); }

    @Override @Deprecated
    default <S extends T> long count(Example<S> example) { throw new AccessDeniedException(OPERATION_NOT_SECURE); }

    @Override @Deprecated
    default <S extends T> boolean exists(Example<S> example) { throw new AccessDeniedException(OPERATION_NOT_SECURE); }

    @Override
    default T findOne(String longIdAsString) {
        try {
            return findOne(Long.parseLong(longIdAsString));
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    @Override
    default boolean deleteOne(String longIdAsString) {
        try {
            return deleteOne(Long.parseLong(longIdAsString));
        } catch (NumberFormatException ex) {
            return false;
        }
    }

    @Override
    default boolean exists(String longIdAsString) {
        try {
            return exists(Long.parseLong(longIdAsString));
        } catch (NumberFormatException ex) {
            return false;
        }
    }
}
