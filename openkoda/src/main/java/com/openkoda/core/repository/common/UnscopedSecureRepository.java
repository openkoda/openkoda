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
import com.openkoda.core.form.FrontendMappingFieldDefinition;
import com.openkoda.core.security.HasSecurityRules.SecurityScope;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.repository.NoRepositoryBean;
import reactor.util.function.Tuple3;

import java.util.List;
import java.util.Set;

@NoRepositoryBean
public interface UnscopedSecureRepository<T> {

    List<T> search(SecurityScope scope, String fieldName, Object value);
    List<T> search(SecurityScope scope, Long organizationId);
    List<T> search(SecurityScope scope, Specification<T> specification);
    List<T> search(SecurityScope scope, Long organizationId, Specification<T> specification);
    Page<T> search(SecurityScope scope, int page, int size, String sortField, String sortDirection);
    Page<T> search(SecurityScope scope, Specification<T> specification, int page, int size, String sortField, String sortDirection);
    Page<T> search(SecurityScope scope, Long organizationId, Specification<T> specification, int page, int size, String sortField, String sortDirection);
    Page<T> search(SecurityScope scope, String searchTerm, int page, int size, String sortField, String sortDirection);
    Page<T> search(SecurityScope scope, String searchTerm, Specification<T> specification, int page, int size, String sortField, String sortDirection) ;
    Page<T> search(SecurityScope scope, String searchTerm, Long organizationId, int page, int size, String sortField, String sortDirection);
    Page<T> search(SecurityScope scope, String searchTerm, Long organizationId, Specification<T> specification, Pageable pageable);
    Page<T> search(SecurityScope scope, String searchTerm, Long organizationId, Specification<T> specification, Pageable pageable, List<Tuple3<String, FrontendMappingFieldDefinition, String>> filters);
    Page<T> search(SecurityScope scope, String searchTerm, Set<Long> organizationId, Specification<T> specification, Pageable pageable, List<Tuple3<String, FrontendMappingFieldDefinition, String>> filters);
    List<T> search(SecurityScope scope, String searchTerm, Long organizationId, Specification<T> specification, List<Tuple3<String, FrontendMappingFieldDefinition, String>> filters);
    T findOne(SecurityScope scope, Object idOrEntityOrSpecification);
    List<T> findAll(SecurityScope scope);
    <S extends T> S saveOne(SecurityScope scope, S entity);
    <S extends T> S saveForm(SecurityScope scope, S entity, AbstractEntityForm form);
    <S extends T> List<S> saveAll(SecurityScope scope, Object entitiesCollection);
    boolean deleteOne(SecurityScope scope, Object idOrEntity);
    long deleteAll(SecurityScope scope, Object idsOrEntitiesCollectionOrSpecification);
    boolean existsOne(SecurityScope scope, Object idOrEntity);
    boolean existsAny(SecurityScope scope, Object idsOrEntitiesCollectionOrSpecification);
    long count(SecurityScope scope);
    long count(SecurityScope scope, String fieldName, Object value);
    long count(SecurityScope scope, Specification<T> specification);
    long count(SecurityScope scope, Long organizationId, Specification<T> specification);
    T getNew();
}
