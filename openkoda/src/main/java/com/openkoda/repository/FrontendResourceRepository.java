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

package com.openkoda.repository;

import com.openkoda.core.flow.Tuple;
import com.openkoda.core.repository.common.UnsecuredFunctionalRepositoryWithLongId;
import com.openkoda.core.security.HasSecurityRules;
import com.openkoda.model.FrontendResource;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
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
public interface FrontendResourceRepository extends UnsecuredFunctionalRepositoryWithLongId<FrontendResource>, HasSecurityRules {

    String FRONTEND_RESOURCES = "frontendResources";

    @Query("select c from FrontendResource c where c.unsecured = TRUE and c.isPage = TRUE and c.includeInSitemap = TRUE and (c.type = 'HTML' or c.type = 'PAGE')")
    Collection<FrontendResource> getEntriesToSitemap();

    @Cacheable(cacheNames = FRONTEND_RESOURCES, key = "#p0", unless = "#result == null")
    @Query("select dbFrontendResource from FrontendResource dbFrontendResource where dbFrontendResource.name = :name and " +
            "( dbFrontendResource.unsecured = TRUE OR " + CHECK_CAN_MANAGE_FRONTEND_RESOURCES_OR_HAS_REQUIRED_PRIVILEGE_JPQL + ")")
    FrontendResource findByName(@Param("name") String name);

    @Query("select c from FrontendResource c where c.name = :name")
    FrontendResource findByNameUnsecured(@Param("name") String name);


    @Cacheable(cacheNames = FRONTEND_RESOURCES, key = "#p0", unless = "#result == null")
    @Query("select dbFrontendResource from FrontendResource dbFrontendResource where dbFrontendResource.isPage = TRUE and " +
            "dbFrontendResource.urlPath = :urlPath and dbFrontendResource.isPublic = :isPublic and " +
            "( dbFrontendResource.unsecured = TRUE OR " + CHECK_CAN_MANAGE_FRONTEND_RESOURCES_OR_HAS_REQUIRED_PRIVILEGE_JPQL + ")")
    FrontendResource findByUrlPathAndIsPublic(@Param("urlPath") String urlPath, @Param("isPublic") Boolean isPublic);

    @Cacheable(cacheNames = FRONTEND_RESOURCES, key = "#p0", unless = "#result == null")
    @Query("select dbFrontendResource from FrontendResource dbFrontendResource where dbFrontendResource.isPage = TRUE and " +
            "dbFrontendResource.urlPath = :urlPath and dbFrontendResource.isPublic = FALSE and dbFrontendResource.organizationId = :orgId and " +
            "( dbFrontendResource.unsecured = TRUE OR " + CHECK_CAN_MANAGE_FRONTEND_RESOURCES_OR_HAS_REQUIRED_PRIVILEGE_JPQL + ")")
    FrontendResource findNonPublicByUrlPathAndOrganizationId(@Param("urlPath") String urlPath, @Param("orgId") Long organizationId);

    @Cacheable(cacheNames = FRONTEND_RESOURCES, key = "#p0", unless = "#result == null")
    @Query("select dbFrontendResource from FrontendResource dbFrontendResource where dbFrontendResource.isPage = TRUE and " +
            "dbFrontendResource.urlPath = :urlPath and dbFrontendResource.isPublic = FALSE and dbFrontendResource.organizationId is null and " +
            "( dbFrontendResource.unsecured = TRUE OR " + CHECK_CAN_MANAGE_FRONTEND_RESOURCES_OR_HAS_REQUIRED_PRIVILEGE_JPQL + ")")
    FrontendResource findNonPublicByUrlPathAndOrganizationIdIsNull(@Param("urlPath") String urlPath);

    @Query("select fr from FrontendResource fr where fr.draft = TRUE")
    Stream<FrontendResource> findAllAsStreamByIsDraftTrue();

    @Query("select fr from FrontendResource fr where fr.contentExists = TRUE")
    Stream<FrontendResource> findAllAsStreamByContentExists();

    @Query("select fr from FrontendResource fr")
    Stream<FrontendResource> findAllAsStream();

    Page<FrontendResource> findByType(FrontendResource.Type type, Pageable pageable);

    List<FrontendResource> findByTypeOrderByCreatedOnDesc(FrontendResource.Type type);

    FrontendResource findByTypeAndUrlPath(FrontendResource.Type type, String urlPath);

    @Modifying
    @PreAuthorize(CHECK_CAN_MANAGE_FRONTEND_RESOURCES)
        @Query(value = "update frontend_resource set content = :content where id = :id and ( " + CHECK_CAN_READ_FRONTEND_RESOURCES_JPQL + ") ", nativeQuery = true)
    Integer updateContent(@Param("id") Long id, @Param("content") String content);

    @CacheEvict(cacheNames = FRONTEND_RESOURCES, key = "#result.name")
    default FrontendResource evictOne(Long id) {
        return findOne(id);
    }

    @Query("select new com.openkoda.core.flow.Tuple(fr.id, fr.name) FROM FrontendResource fr order by name")
    List<Tuple> findAllAsTuple();
}
