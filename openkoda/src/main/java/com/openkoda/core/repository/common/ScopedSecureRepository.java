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
import com.openkoda.model.common.SearchableRepositoryMetadata;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public interface ScopedSecureRepository<T> {

    List<T> search(String fieldName, Object value);
    List<T> search(Specification<T> specification);
    List<T> search(Long organizationId);
    List<T> search(Long organizationId, Specification<T> specification);
    Page<T> search(int page, int size, String sortField, String sortDirection);
    Page<T> search(Specification<T> specification, int page, int size, String sortField, String sortDirection);
    Page<T> search(Long organizationId, Specification<T> specification, int page, int size, String sortField, String sortDirection);
    Page<T> search(String searchTerm, int page, int size, String sortField, String sortDirection);
    Page<T> search(String searchTerm, Specification<T> specification, int page, int size, String sortField, String sortDirection) ;
    Page<T> search(String searchTerm, Long organizationId, int page, int size, String sortField, String sortDirection);
    Page<T> search(String searchTerm, Long organizationId, Specification<T> specification, Pageable pageable);
    T findOne(Object idOrEntityOrSpecification);
    List<T> findAll();
    <S extends T> S saveOne(S entity);
    <S extends T> S saveForm(S entity, AbstractEntityForm form);
    <S extends T> List<S> saveAll(Object entitiesCollection);
    boolean deleteOne(Object idOrEntity);
    long deleteAll(Object idsOrEntitiesCollectionOrSpecification);
    boolean existsOne(Object idOrEntity);
    boolean existsAny(Object idsOrEntitiesCollectionOrSpecification);
    long count();
    long count(String fieldName, Object value);
    long count(Long organizationId, Specification<T> specification);
    T getNew();

    SearchableRepositoryMetadata getSearchableRepositoryMetadata();
}
