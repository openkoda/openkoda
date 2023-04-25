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

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public interface SecuredRepository<T> {

    Page<T> search(String searchTerm, Pageable pageable);
    Page<T> search(String searchTerm, Enum requiredPrivilege, Pageable pageable);
    Page<T> search(String searchTerm, Specification<T> additionalSpecification, Pageable pageable);
    Page<T> search(String searchTerm, Specification<T> additionalSpecification, Enum requiredPrivilege, Pageable pageable);
    Page<T> search(String searchTerm, Long organizationId, Pageable pageable);
    Page<T> search(String searchTerm, Long organizationId, Enum requiredPrivilege, Pageable pageable);
    Page<T> search(String searchTerm, Long organizationId, Specification<T> additionalSpecification, Pageable pageable);
    Page<T> search(String[] searchTerms, Long organizationId, Specification<T> additionalSpecification, Pageable pageable);
    Page<T> search(String searchTerm, Long organizationId, Specification<T> additionalSpecification, Enum requiredPrivilege, Pageable pageable);

    T findOne(Long id);
    T findOne(Long id, Enum requiredPrivilege);
    T findOneWithSpecification(Specification<T> specification);

    List<T> findAll();
    Page<T> findAll(Pageable pageable);
    List<T> findAll(Enum requiredPrivilege);
    Page<T> findAll(Enum requiredPrivilege, Pageable pageable);
    Page<T> findAll(Specification<T> specification, Enum requiredPrivilege, Pageable pageable);
    List<T> findAll(Specification<T> specification, Enum requiredPrivilege, Long organizationId);
    Page<T> findAll(Specification<T> specification, Enum requiredPrivilege, Long organizationId, Pageable pageable);

    List<T> findBy(Specification<T> specification);
    Page<T> findBy(Specification<T> specification, Pageable pageable);

    List<T> findBy(String fieldName, Object value);

    long count();
    long count(Enum requiredPrivilege);

    long countBy(Specification<T> specification);
    boolean exists(Long aLong);
    boolean deleteOne(T t);
    boolean deleteOne(Long id);
    <S extends T> S saveOne(S s);

    T findOne(String longIdAsString);
    boolean deleteOne(String longIdAsString);
    boolean exists(String longIdAsString);

}
