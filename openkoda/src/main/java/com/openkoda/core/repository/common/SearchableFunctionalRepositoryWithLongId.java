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

import com.openkoda.model.common.SearchableEntity;
import jakarta.persistence.criteria.Predicate;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.query.Param;

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
public interface SearchableFunctionalRepositoryWithLongId<T extends SearchableEntity> extends SecuredFunctionalRepositoryWithLongId<T> {

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
                 searchPredicates[i] = cb.like(root.get("indexString"), "%" + StringUtils.lowerCase(StringUtils.defaultString(searchTerm[i], "")) + "%");
             }
             return cb.and(searchPredicates);
        };
    }

    /**
     * Returns a result page for provided search term filtered by secure specification.
     */
    default Page<T> search(@Param("searchTerm") String searchTerm, Pageable pageable) {
        return this.findAll(searchSpecification(searchTerm), null, pageable);
    }

    /**
     * Returns a result page for provided search term filtered by secure specification and privilege.
     * The privilege filter works like that:
     * - if the entity is not organization related then check if user has global requiredPrivilege
     * - if the entity is organization related, then check if user had this privilege in that organization
     * - if the entity is related to many organizations, then check if user had this privilege in any of these related organizations
     */
    default Page<T> search(@Param("searchTerm") String searchTerm, Enum requiredPrivilege, Pageable pageable) {
        return this.findAll(searchSpecification(searchTerm), requiredPrivilege, pageable);
    }

    /**
     * Returns a result page for provided search term filtered by secure specification and provided additional specification.
     */
    default Page<T> search(@Param("searchTerm") String searchTerm, Specification<T> additionalSpecification, Pageable pageable) {
        Specification<T> specification = Specification.where(searchSpecification(searchTerm)).and(additionalSpecification);
        return this.findAll(specification, null, pageable);
    }

    /**
     * Returns a result page for provided search term filtered by additional requiredPrivilege, secure specification and provided additional specification.
     * For requiredPrivilege contract, see {@link #search(String, Enum, Pageable)}
     */
    default Page<T> search(@Param("searchTerm") String searchTerm, Specification<T> additionalSpecification, Enum requiredPrivilege, Pageable pageable) {
        Specification<T> specification = Specification.where(searchSpecification(searchTerm)).and(additionalSpecification);
        return this.findAll(specification, requiredPrivilege, pageable);
    }

    /**
     * Returns a result page related to specified organization for provided search term filtered by secure specification.
     */
    default Page<T> search(@Param("searchTerm") String searchTerm, Long organizationId, Pageable pageable) {
        return this.findAll(searchSpecification(searchTerm), null, organizationId, pageable);
    }

    /**
     * Returns a result page related to specified organization for provided additional requiredPrivilege and search term filtered by secure specification.
     * For requiredPrivilege contract, see {@link #search(String, Enum, Pageable)}
     */
    default Page<T> search(@Param("searchTerm") String searchTerm, Long organizationId, Enum requiredPrivilege, Pageable pageable) {
        return this.findAll(searchSpecification(searchTerm), requiredPrivilege, organizationId, pageable);
    }

    /**
     * Returns a result page related to specified organization for provided additional specification and search term filtered by secure specification.
     */
    default Page<T> search(String searchTerm, Long organizationId, Specification<T> additionalSpecification, Pageable pageable) {
        return search(searchTerm.split(" "), organizationId, additionalSpecification, pageable);
    }

    /**
     * Returns a result page related to specified organization for provided additional specification and search terms filtered by secure specification.
     * See {@link #searchSpecification(String...)} for search term filtering routine.
     */
    default Page<T> search(String[] searchTerms, Long organizationId, Specification<T> additionalSpecification, Pageable pageable) {
        Specification<T> specification = Specification.where(searchSpecification(searchTerms)).and(additionalSpecification);
        return this.findAll(specification, null, organizationId, pageable);
    }

    /**
     * Returns a result page related to specified organization for provided additional requiredPrivilege, additional specification and search term filtered by secure specification.
     * For requiredPrivilege contract, see {@link #search(String, Enum, Pageable)}
     */
    default Page<T> search(@Param("searchTerm") String searchTerm, Long organizationId, Specification<T> additionalSpecification, Enum requiredPrivilege, Pageable pageable) {
        Specification<T> specification = Specification.where(searchSpecification(searchTerm)).and(additionalSpecification);
        return this.findAll(specification, requiredPrivilege, organizationId, pageable);
    }


}
