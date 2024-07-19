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

package com.openkoda.repository;

import com.openkoda.core.form.AbstractEntityForm;
import com.openkoda.core.form.FrontendMappingFieldDefinition;
import com.openkoda.core.repository.common.SearchableFunctionalRepositoryWithLongId;
import com.openkoda.model.common.SearchableEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.repository.NoRepositoryBean;
import reactor.util.function.Tuple3;

import java.util.List;
import java.util.Set;

import static com.openkoda.core.security.HasSecurityRules.SecurityScope.USER;

@NoRepositoryBean
public interface SecureRepository<T extends SearchableEntity> extends SearchableFunctionalRepositoryWithLongId<T> {

    SecurityScope DEFAULT_SCOPE = USER;
    
    default List<T> search(String fieldName, Object value) {
        return SearchableFunctionalRepositoryWithLongId.super.search(DEFAULT_SCOPE, fieldName, value);
    }

    default List<T> search(Specification<T> specification) {
        return SearchableFunctionalRepositoryWithLongId.super.search(DEFAULT_SCOPE, specification);
    }

    default List<T> search(Long organizationId) {
        return SearchableFunctionalRepositoryWithLongId.super.search(DEFAULT_SCOPE, organizationId);
    }

    default List<T> search(Long organizationId, Specification<T> specification) {
        return SearchableFunctionalRepositoryWithLongId.super.search(DEFAULT_SCOPE, organizationId, specification);
    }

    
    default Page<T> search(int page, int size, String sortField, String sortDirection) {
        return SearchableFunctionalRepositoryWithLongId.super.search(DEFAULT_SCOPE, page, size, sortField, sortDirection);
    }

    
    default Page<T> search(Specification<T> specification, int page, int size, String sortField, String sortDirection) {
        return SearchableFunctionalRepositoryWithLongId.super.search(DEFAULT_SCOPE, specification, page, size, sortField, sortDirection);
    }

    
    default Page<T> search(Long organizationId, Specification<T> specification, int page, int size, String sortField, String sortDirection) {
        return SearchableFunctionalRepositoryWithLongId.super.search(DEFAULT_SCOPE, organizationId, specification, page, size, sortField, sortDirection);
    }

    
    default Page<T> search(String searchTerm, int page, int size, String sortField, String sortDirection) {
        return SearchableFunctionalRepositoryWithLongId.super.search(DEFAULT_SCOPE, searchTerm, page, size, sortField, sortDirection);
    }

    
    default Page<T> search(String searchTerm, Specification<T> specification, int page, int size, String sortField, String sortDirection) {
        return SearchableFunctionalRepositoryWithLongId.super.search(DEFAULT_SCOPE, searchTerm, specification, page, size, sortField, sortDirection);
    }

    
    default Page<T> search(String searchTerm, Long organizationId, int page, int size, String sortField, String sortDirection) {
        return SearchableFunctionalRepositoryWithLongId.super.search(DEFAULT_SCOPE, searchTerm, organizationId, page, size, sortField, sortDirection);
    }

    
    default Page<T> search(String searchTerm, Long organizationId, Specification<T> specification, Pageable pageable) {
        return SearchableFunctionalRepositoryWithLongId.super.search(DEFAULT_SCOPE, searchTerm, organizationId, specification, pageable);
    }

    @Override
    default Page<T> search(String searchTerm, Long organizationId, Specification<T> specification, Pageable pageable, List<Tuple3<String, FrontendMappingFieldDefinition, String>> filters) {
        return SearchableFunctionalRepositoryWithLongId.super.search(DEFAULT_SCOPE, searchTerm, organizationId, specification, pageable, filters);
    }
    
    @Override
    default Page<T> search(String searchTerm, Set<Long> organizationIds, Specification<T> specification,
            Pageable pageable, List<Tuple3<String, FrontendMappingFieldDefinition, String>> filters) {
        return SearchableFunctionalRepositoryWithLongId.super.search(DEFAULT_SCOPE, searchTerm, organizationIds, specification, pageable, filters);
    }

    default List<T> search(String searchTerm, Long organizationId, Specification<T> specification, List<Tuple3<String, FrontendMappingFieldDefinition, String>> filters) {
        return SearchableFunctionalRepositoryWithLongId.super.search(DEFAULT_SCOPE, searchTerm, organizationId, specification, filters);
    }

    default T findOne(Object idOrEntityOrSpecification) {
        return SearchableFunctionalRepositoryWithLongId.super.findOne(DEFAULT_SCOPE, idOrEntityOrSpecification);
    }

    
    default List<T> findAll() {
        return SearchableFunctionalRepositoryWithLongId.super.findAll(DEFAULT_SCOPE);
    }

    
    default <S extends T> S saveOne(S entity) {
        return SearchableFunctionalRepositoryWithLongId.super.saveOne(DEFAULT_SCOPE, entity);
    }

    
    default <S extends T> S saveForm(S entity, AbstractEntityForm form) {
        return SearchableFunctionalRepositoryWithLongId.super.saveForm(DEFAULT_SCOPE, entity, form);
    }

    
    default <S extends T> List<S> saveAll(Object entitiesCollection) {
        return SearchableFunctionalRepositoryWithLongId.super.saveAll(DEFAULT_SCOPE, entitiesCollection);
    }

    
    default boolean deleteOne(Object idOrEntity) {
        return SearchableFunctionalRepositoryWithLongId.super.deleteOne(DEFAULT_SCOPE, idOrEntity);
    }

    
    default long deleteAll(Object idsOrEntitiesCollectionOrSpecification) {
        return SearchableFunctionalRepositoryWithLongId.super.deleteAll(DEFAULT_SCOPE, idsOrEntitiesCollectionOrSpecification);
    }

    
    default boolean existsOne(Object idOrEntity) {
        return SearchableFunctionalRepositoryWithLongId.super.existsOne(DEFAULT_SCOPE, idOrEntity);
    }

    
    default boolean existsAny(Object idsOrEntitiesCollectionOrSpecification) {
        return SearchableFunctionalRepositoryWithLongId.super.existsAny(DEFAULT_SCOPE, idsOrEntitiesCollectionOrSpecification);
    }

    
    default long count() {
        return SearchableFunctionalRepositoryWithLongId.super.count(DEFAULT_SCOPE);
    }

    
    default long count(String fieldName, Object value) {
        return SearchableFunctionalRepositoryWithLongId.super.count(DEFAULT_SCOPE, fieldName, value);
    }

    default long count(Long organizationId, Specification<T> specification) {
        return SearchableFunctionalRepositoryWithLongId.super.count(DEFAULT_SCOPE, organizationId, specification);
    }

    default SecureRepositoryWrapper<T> scoped(SecurityScope scope) {
        return new SecureRepositoryWrapper<>(this, scope);
    }

}
