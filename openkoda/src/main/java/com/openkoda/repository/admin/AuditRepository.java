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

package com.openkoda.repository.admin;

import com.openkoda.core.repository.common.UnsecuredFunctionalRepositoryWithLongId;
import com.openkoda.core.security.HasSecurityRules;
import com.openkoda.model.common.Audit;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 *
 * @author Arkadiusz Drysch (adrysch@stratoflow.com)
 *
 */
@Repository
public interface AuditRepository extends UnsecuredFunctionalRepositoryWithLongId<Audit>, HasSecurityRules {

    //TODO: is it used anywhere?
    @PreAuthorize(CHECK_CAN_READ_SUPPORT_DATA)
    Page<Audit> findAll(Specification<Audit> specification, Pageable pageable);

    //fixme: extract security rule
    @Query("select o from Audit o where (o.organizationId = null OR o.organizationId = :organizationId) AND o.userId = ?#{principal.user.id} AND o.indexString like CONCAT('%' , LOWER(:search) , '%') AND " + CHECK_CAN_READ_ORG_AUDIT_JPQL)
    Page<Audit> findAllByOrganizationId(@Param("organizationId") Long organizationId, @Param("search") String search, Pageable pageable);

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    default Audit saveAudit(Audit a) {
        return save(a);
    }
}
