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

package com.openkoda.repository.admin;

import com.openkoda.core.repository.common.SearchableFunctionalRepositoryWithLongId;
import com.openkoda.core.security.HasSecurityRules;
import com.openkoda.model.common.Audit;
import com.openkoda.model.common.SearchableRepositoryMetadata;
import org.springframework.stereotype.Repository;

import static com.openkoda.controller.common.URLConstants.AUDIT;

/**
 *
 *
 * @author Arkadiusz Drysch (adrysch@stratoflow.com)
 *
 */
@Repository
@SearchableRepositoryMetadata(
        entityKey = AUDIT,
        entityClass = Audit.class,
        searchIndexFormula = """
            lower(COALESCE(entity_name, '') || ':' || COALESCE(entity_id, -1) || ' ' || change || ' ' || 
            (LPAD(coalesce(organization_id, 0)||'', 5, '0') || '/' || id)|| ' ' || COALESCE(ip_address, '') || ' '
            || ' modifiedby:' || COALESCE(CAST (user_id as text), '') || ' orgid:' || COALESCE(CAST (organization_id as text), '')
            || ' ' || request_id)
            """
)
public interface SecureAuditRepository extends SearchableFunctionalRepositoryWithLongId<Audit>, HasSecurityRules {


}
