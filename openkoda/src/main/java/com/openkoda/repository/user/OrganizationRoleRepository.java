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

package com.openkoda.repository.user;

import com.openkoda.core.flow.Tuple;
import com.openkoda.core.repository.common.FunctionalRepositoryWithLongId;
import com.openkoda.core.security.HasSecurityRules;
import com.openkoda.model.OrganizationRole;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 *
 *
 * @author Arkadiusz Drysch (adrysch@stratoflow.com)
 *
 */
@Repository
public interface OrganizationRoleRepository extends FunctionalRepositoryWithLongId<OrganizationRole>, HasSecurityRules {


    @Override
    OrganizationRole save(OrganizationRole organizationRole);

    /**
     * <p>findByName.</p>
     *
     * @param name a {@link java.lang.String} object.
     * @return a {@link com.openkoda.model.OrganizationRole} object.
     */
    OrganizationRole findByName(String name);

    /**
     * <p>findAllAsTuple.</p>
     *
     * @return a {@link java.util.List} object.
     */
    @Query("select new com.openkoda.core.flow.Tuple(dbOrganizationRole.name, 'label.role.' || dbOrganizationRole.name) FROM OrganizationRole dbOrganizationRole order by name")
    List<Tuple> findAllAsTupleWithLabelName();


}
