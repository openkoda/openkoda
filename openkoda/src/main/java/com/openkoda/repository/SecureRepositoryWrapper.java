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
import com.openkoda.core.repository.common.ScopedSecureRepository;
import com.openkoda.core.repository.common.SearchableFunctionalRepositoryWithLongId;
import com.openkoda.core.security.HasSecurityRules;
import com.openkoda.model.common.SearchableEntity;
import com.openkoda.model.common.SearchableRepositoryMetadata;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public class SecureRepositoryWrapper<T extends SearchableEntity> implements ScopedSecureRepository<T> {
    private final SearchableFunctionalRepositoryWithLongId<T> wrapped;
    private final HasSecurityRules.SecurityScope scope;

    public SecureRepositoryWrapper(SearchableFunctionalRepositoryWithLongId<T> wrapped, HasSecurityRules.SecurityScope scope) {
        this.wrapped = wrapped;
        this.scope = scope;
    }

    public List<T> search(String fieldName, Object value) {
        return wrapped.search(scope, fieldName, value);
    }

    
    public List<T> search(Specification<T> specification) {
        return wrapped.search(scope, specification);
    }

    
    public List<T> search(Long organizationId, Specification<T> specification) {
        return wrapped.search(scope, organizationId, specification);
    }

    public List<T> search(Long organizationId) {
        return wrapped.search(scope, organizationId);
    }

    public Page<T> search(int page, int size, String sortField, String sortDirection) {
        return wrapped.search(scope, page, size, sortField, sortDirection);
    }

    public Page<T> search(Specification<T> specification, int page, int size, String sortField, String sortDirection) {
        return wrapped.search(scope, specification, page, size, sortField, sortDirection);
    }

    
    public Page<T> search(Long organizationId, Specification<T> specification, int page, int size, String sortField, String sortDirection) {
        return wrapped.search(scope, organizationId, specification, page, size, sortField, sortDirection);
    }

    
    public Page<T> search(String searchTerm, int page, int size, String sortField, String sortDirection) {
        return wrapped.search(scope, searchTerm, page, size, sortField, sortDirection);
    }

    
    public Page<T> search(String searchTerm, Specification<T> specification, int page, int size, String sortField, String sortDirection) {
        return wrapped.search(scope, searchTerm, specification, page, size, sortField, sortDirection);
    }

    
    public Page<T> search(String searchTerm, Long organizationId, int page, int size, String sortField, String sortDirection) {
        return wrapped.search(scope, searchTerm, organizationId, page, size, sortField, sortDirection);
    }

    
    public Page<T> search(String searchTerm, Long organizationId, Specification<T> specification, Pageable pageable) {
        return wrapped.search(scope, searchTerm, organizationId, specification, pageable);
    }

    
    public T findOne(Object idOrEntityOrSpecification) {
        return wrapped.findOne(scope, idOrEntityOrSpecification);
    }

    
    public List<T> findAll() {
        return wrapped.findAll(scope);
    }

    
    public <S extends T> S saveOne(S entity) {
        return wrapped.saveOne(scope, entity);
    }

    
    public <S extends T> S saveForm(S entity, AbstractEntityForm form) {
        return wrapped.saveForm(scope, entity, form);
    }

    
    public <S extends T> List<S> saveAll(Object entitiesCollection) {
        return wrapped.saveAll(scope, entitiesCollection);
    }

    
    public boolean deleteOne(Object idOrEntity) {
        return wrapped.deleteOne(scope, idOrEntity);
    }

    
    public long deleteAll(Object idsOrEntitiesCollectionOrSpecification) {
        return wrapped.deleteAll(scope, idsOrEntitiesCollectionOrSpecification);
    }

    
    public boolean existsOne(Object idOrEntity) {
        return wrapped.existsOne(scope, idOrEntity);
    }

    
    public boolean existsAny(Object idsOrEntitiesCollectionOrSpecification) {
        return wrapped.existsAny(scope, idsOrEntitiesCollectionOrSpecification);
    }

    
    public long count() {
        return wrapped.count(scope);
    }

    
    public long count(String fieldName, Object value) {
        return wrapped.count(scope, fieldName, value);
    }

    
    public long count(Specification<T> specification) {
        return wrapped.count(scope, specification);
    }

    
    public long count(Long organizationId, Specification<T> specification) {
        return wrapped.count(scope, organizationId, specification);
    }

    @Override
    public T getNew() {
        return wrapped.getNew();
    }

    @Override
    public SearchableRepositoryMetadata getSearchableRepositoryMetadata() {
        Class<?>[] interfaces = wrapped.getClass().getInterfaces();
        if (interfaces == null || interfaces.length == 0) { return null; }
        SearchableRepositoryMetadata gsa = interfaces[0].getAnnotation(SearchableRepositoryMetadata.class);
        return gsa;
    }

    public boolean isSet() {
        return this.wrapped != null;
    }
}
