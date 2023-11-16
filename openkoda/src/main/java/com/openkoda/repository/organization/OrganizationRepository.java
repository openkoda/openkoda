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

package com.openkoda.repository.organization;

import com.openkoda.core.repository.common.UnsecuredFunctionalRepositoryWithLongId;
import com.openkoda.core.security.HasSecurityRules;
import com.openkoda.model.Organization;
import com.openkoda.model.common.ModelConstants;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

@Repository
public interface OrganizationRepository extends UnsecuredFunctionalRepositoryWithLongId<Organization>, ModelConstants, HasSecurityRules {

    @Override
    @Query("select dbOrganization from Organization dbOrganization WHERE " + CHECK_CAN_READ_ORG_DATA_OR_IS_ORG_MEMEBER_JPQL)
    Page<Organization> findAll(Pageable page);

    @Override
    @Query("select dbOrganization from Organization dbOrganization where dbOrganization.id = :id AND " + CHECK_CAN_READ_ORG_DATA_OR_IS_ORG_MEMEBER_JPQL)
    Organization findOne(@Param("id") Long organizationId);

    @Query("select o from Organization o where o.id = :id")
    Organization findById(@Param("id") long id);

    @Query("select o from Organization o where o.name = :name")
    Organization findByName(@Param("name") String name);

    @Override
    @Query("delete from Organization dbOrganization where dbOrganization.id = :id AND " + CHECK_CAN_READ_ORG_DATA_OR_IS_ORG_MEMEBER_JPQL)
    boolean deleteOne(@Param("id") Long aLong);

    @Query("select id from Organization o")
    Stream<Long> findAllIdsAsStream();

    @Query("select o.id from Organization o where o.name not like '(disabled)%'")
    List<Long> findActiveOrganizationIdsAsList();


    @Transactional
    @Override
    <S extends Organization> S save(S entity);

    @Query(nativeQuery = true, value = "select assigned_datasource, string_agg(id\\:\\:varchar, ',') from organization group by assigned_datasource")
    List<Object[]> findDatasourceAssignments();
    default Map<Long, Integer > findOrganizationToDatasourceIndexMap() {
        List<Object[]> datasourceAssignments = findDatasourceAssignments();
        Map<Long, Integer> result = new HashMap<>();
        for (Object[] o: datasourceAssignments) {
            int datasourceIndex = ((Number)o[0]).intValue();
            String organizationsString = "" + o[1];
            if (StringUtils.isBlank(organizationsString)) {
                continue;
            }
            String[] organizations = organizationsString.split(",");
            for (String org : organizations) {
                result.put(Long.parseLong(org), datasourceIndex);
            }
        }
        return result;
    }


}
