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

package com.openkoda.uicomponent.preview;

import com.openkoda.core.repository.common.SecuredRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.Arrays;
import java.util.List;

public class PreviewRepository<T> implements SecuredRepository<T> {

    private final Class<T> entityClass;
    private final List<T> allList;
    private final List<T> subList;
    private final Page<T> allPage;
    private final Page<T> subPage;
    private final T last;

    public PreviewRepository(T ... all) {
        if(all == null || all.length == 0) {
            throw new RuntimeException("Preview Repository not initialized with values");
        }
        entityClass = (Class<T>) all[0].getClass();
        allList = Arrays.asList(all);
        subList = Arrays.asList(all[0]);
        allPage = new PageImpl<>(allList);
        subPage = new PageImpl<>(subList);
        last = all[all.length -1];
    }



    @Override
    public Page<T> search(String searchTerm, Pageable pageable) {
        return subPage;
    }

    @Override
    public Page<T> search(String searchTerm, Enum requiredPrivilege, Pageable pageable) {
        return subPage;
    }

    @Override
    public Page<T> search(String searchTerm, Specification<T> additionalSpecification, Pageable pageable) {
        return subPage;
    }

    @Override
    public Page<T> search(String searchTerm, Specification<T> additionalSpecification, Enum requiredPrivilege, Pageable pageable) {
        return subPage;
    }

    @Override
    public Page<T> search(String searchTerm, Long organizationId, Pageable pageable) {
        return subPage;
    }

    @Override
    public Page<T> search(String searchTerm, Long organizationId, Enum requiredPrivilege, Pageable pageable) {
        return subPage;
    }

    @Override
    public Page<T> search(String searchTerm, Long organizationId, Specification<T> additionalSpecification, Pageable pageable) {
        return subPage;
    }

    @Override
    public Page<T> search(String[] searchTerms, Long organizationId, Specification<T> additionalSpecification, Pageable pageable) {
        return subPage;
    }

    @Override
    public Page<T> search(String searchTerm, Long organizationId, Specification<T> additionalSpecification, Enum requiredPrivilege, Pageable pageable) {
        return subPage;
    }

    @Override
    public T findOne(Long id) {
        return last;
    }

    @Override
    public T findOne(Long id, Enum requiredPrivilege) {
        return last;
    }

    @Override
    public T findOneWithSpecification(Specification<T> specification) {
        return last;
    }

    @Override
    public List<T> findAll() {
        return allList;
    }

    @Override
    public Page<T> findAll(Pageable pageable) {
        return allPage;
    }

    @Override
    public List<T> findAll(Enum requiredPrivilege) {
        return allList;
    }

    @Override
    public Page<T> findAll(Enum requiredPrivilege, Pageable pageable) {
        return allPage;
    }

    @Override
    public Page<T> findAll(Specification<T> specification, Enum requiredPrivilege, Pageable pageable) {
        return allPage;
    }

    @Override
    public List<T> findBy(Specification<T> specification) {
        return subList;
    }

    @Override
    public Page<T> findBy(Specification<T> specification, Pageable pageable) {
        return subPage;
    }

    @Override
    public List<T> findBy(String fieldName, Object value) {
        return subList;
    }

    @Override
    public List<T> findAll(Specification<T> specification, Enum requiredPrivilege, Long organizationId) { return subList;}

    @Override
    public Page<T> findAll(Specification<T> specification, Enum requiredPrivilege, Long organizationId, Pageable pageable) { return subPage;}

    @Override
    public long count() {
        return allList.size();
    }

    @Override
    public long count(Enum requiredPrivilege) {
        return allList.size();
    }

    @Override
    public long countBy(Specification<T> specification) {
        return allList.size();
    }

    @Override
    public <S extends T> S saveOne(S s) {
        return s;
    }

    @Override
    public boolean exists(Long aLong) {
        return true;
    }

    @Override
    public boolean deleteOne(T t) {
        return true;
    }
    @Override
    public boolean deleteOne(Long id) {
        return true;
    }

    @Override
    public T findOne(String longIdAsString) {
        return null;
    }

    @Override
    public boolean deleteOne(String longIdAsString) {
        return false;
    }

    @Override
    public boolean exists(String longIdAsString) {
        return false;
    }
}
