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

import com.openkoda.core.flow.Tuple;
import com.openkoda.core.repository.common.UnsecuredFunctionalRepositoryWithLongId;
import com.openkoda.core.security.HasSecurityRules;
import com.openkoda.model.OpenkodaModule;
import com.openkoda.model.component.FrontendResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

/**
 *
 */
@Repository
public interface FrontendResourceRepository extends UnsecuredFunctionalRepositoryWithLongId<FrontendResource>, HasSecurityRules, ComponentEntityRepository<FrontendResource> {

    String FRONTEND_RESOURCES = "frontendResources";

    @Query("select c from FrontendResource c where c.unsecured = TRUE and c.isPage = TRUE and c.includeInSitemap = TRUE and c.type = 'HTML'")
    Collection<FrontendResource> getEntriesToSitemap();

    @Query("select dbFrontendResource from FrontendResource dbFrontendResource where dbFrontendResource.name = :name and " +
            "( dbFrontendResource.unsecured = TRUE OR " + CHECK_CAN_MANAGE_FRONTEND_RESOURCES_OR_HAS_REQUIRED_PRIVILEGE_JPQL + ")")
    FrontendResource findByName(@Param("name") String name);

    @Query("select dbFrontendResource from FrontendResource dbFrontendResource " +
            "where dbFrontendResource.name = :urlPath and " +
            "(:orgId is null and dbFrontendResource.organizationId is null or :orgId is not null and dbFrontendResource.organizationId = :orgId) and " +
            "dbFrontendResource.accessLevel = :accessLevel and " +
            "( dbFrontendResource.unsecured = TRUE OR " + CHECK_CAN_MANAGE_FRONTEND_RESOURCES_OR_HAS_REQUIRED_PRIVILEGE_JPQL + ")")
    FrontendResource findByNameOrUrlPathAndOrganizationId(@Param("urlPath") String urlPath,
                                                          @Param("orgId") Long organizationId,
                                                          @Param("accessLevel") FrontendResource.AccessLevel accessLevel);

    @Query("select c from FrontendResource c where c.name = :name")
    FrontendResource findByNameUnsecured(@Param("name") String name);

    @Query("""
            select dbFrontendResource,
            case 
                when dbFrontendResource.accessLevel = 'PUBLIC' and dbFrontendResource.organizationId is not null then 1
                when dbFrontendResource.accessLevel = 'PUBLIC' and dbFrontendResource.organizationId is null then 2
                when dbFrontendResource.accessLevel = 'GLOBAL' and dbFrontendResource.organizationId is not null then 1
                when dbFrontendResource.accessLevel = 'GLOBAL' and dbFrontendResource.organizationId is null then 2
                when dbFrontendResource.accessLevel = 'ORGANIZATION' and dbFrontendResource.organizationId is not null then 1
                when dbFrontendResource.accessLevel = 'ORGANIZATION' and dbFrontendResource.organizationId is null then 2
                when dbFrontendResource.accessLevel = 'GLOBAL' and 'ORGANIZATION' = cast(:accessLevel as text) and dbFrontendResource.organizationId is not null then 3
                when dbFrontendResource.accessLevel = 'GLOBAL' and 'ORGANIZATION' = cast(:accessLevel as text) and dbFrontendResource.organizationId is null then 4
                else 10 end as priority
            from FrontendResource dbFrontendResource where dbFrontendResource.isPage = TRUE and
                ((:urlPath is not null and dbFrontendResource.name = :urlPath) or (:frontendResourceId is not null and dbFrontendResource.id = :frontendResourceId))
                and (dbFrontendResource.accessLevel = :accessLevel or dbFrontendResource.accessLevel = 'GLOBAL' and 'ORGANIZATION' = :#{#accessLevel.name()})
                and (dbFrontendResource.organizationId = :orgId or dbFrontendResource.organizationId is null ) and
                ( dbFrontendResource.unsecured = TRUE OR ((?#{principal.hasGlobalPrivilege('readFrontendResource') OR principal.hasGlobalPrivilege('manageFrontendResource')}) = TRUE OR dbFrontendResource.requiredPrivilege IN ?#{principal.globalPrivileges}))
            """)
    Page<Object[]> findByUrlPathAndAccessLevelAndOrganizationId(@Param("urlPath") String urlPath,
                                                                        @Param("frontendResourceId") Long frontendResourceId,
                                                                        @Param("accessLevel") FrontendResource.AccessLevel accessLevel,
                                                                        @Param("orgId") Long organizationId,
                                                                        Pageable pageable);

    @Query("select fr from FrontendResource fr where fr.draft = TRUE")
    Stream<FrontendResource> findAllAsStreamByIsDraftTrue();

    @Query("select fr from FrontendResource fr where fr.contentExists = TRUE")
    Stream<FrontendResource> findAllAsStreamByContentExists();

    @Query("select fr from FrontendResource fr")
    Stream<FrontendResource> findAllAsStream();

    Page<FrontendResource> findByType(FrontendResource.Type type, Pageable pageable);

    Page<FrontendResource> findByResourceType(FrontendResource.ResourceType resourceType, Pageable pageable);
    Page<FrontendResource> findByResourceTypeAndIndexStringContainingIgnoreCase(FrontendResource.ResourceType resourceType, String search, Pageable pageable);

    List<FrontendResource> findByTypeOrderByCreatedOnDesc(FrontendResource.Type type);

    @Modifying
    @PreAuthorize(CHECK_CAN_MANAGE_FRONTEND_RESOURCES)
        @Query(value = "update frontend_resource set content = :content where id = :id and ( " + CHECK_CAN_READ_FRONTEND_RESOURCES_JPQL + ") ", nativeQuery = true)
    Integer updateContent(@Param("id") Long id, @Param("content") String content);

    default FrontendResource evictOne(Long id) {
        return findOne(id);
    }

    @Query("select new com.openkoda.core.flow.Tuple(fr.id, fr.name) FROM FrontendResource fr order by fr.name")
    List<Tuple> findAllAsTuple();

    @Query("select new com.openkoda.core.flow.Tuple(fr.id, fr.name) FROM FrontendResource fr where fr.embeddable = TRUE and fr.resourceType = 'RESOURCE' order by fr.name")
    List<Tuple> findAllEmbeddableResources();

    @Query("select new com.openkoda.core.flow.Tuple(fr.id, fr.name) FROM FrontendResource fr where fr.embeddable = TRUE and fr.resourceType = 'UI_COMPONENT' order by fr.name")
    List<Tuple> findAllEmbeddableUiComponents();

    @Query("select fr.name FROM FrontendResource fr where fr.embeddable = FALSE and fr.resourceType = 'RESOURCE' order by fr.name")
    Object[] findAllNonEmbeddableResourcesNames();

    @Query("select fr FROM FrontendResource fr where fr.id = :id and fr.resourceType = 'DASHBOARD'")
    FrontendResource findDashboardDefinition(@Param("id") Long id);

    @Query("select fr FROM FrontendResource fr where fr.name = :name and fr.resourceType = 'DASHBOARD'")
    FrontendResource findDashboardDefinitionByName(@Param("name") String name);

    FrontendResource findByNameAndAccessLevelAndOrganizationId(String name, FrontendResource.AccessLevel accessLevel, Long organizationId);

    @Modifying
    @Query("delete from FrontendResource where module = :module")
    void deleteByModule(OpenkodaModule module);

}
