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

import com.openkoda.core.helper.ApplicationContextProvider;
import com.openkoda.core.job.JobsScheduler;
import com.openkoda.core.repository.common.ScopedSecureRepository;
import com.openkoda.core.security.HasSecurityRules;
import com.openkoda.model.common.SearchableEntity;
import com.openkoda.model.common.SearchableOrganizationRelatedEntity;
import com.openkoda.model.common.SearchableRepositoryMetadata;
import jakarta.persistence.Table;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.boot.model.naming.CamelCaseToUnderscoresNamingStrategy;
import org.hibernate.boot.model.naming.Identifier;
import org.springframework.context.ApplicationContext;

import java.util.HashMap;
import java.util.Map;

import static com.openkoda.model.common.ModelConstants.INDEX_STRING_COLUMN;
import static com.openkoda.model.common.ModelConstants.UPDATED_ON;

/**
 *
 *
 * @author Arkadiusz Drysch (adrysch@stratoflow.com)
 * 
 */
public class SearchableRepositories {


    private static SecureRepository<?>[] searchableRepositories = {};
    private static SecureRepository<?>[] globalSearchableRepositories = {};

    private static boolean discoveryCompleted = false;

    /**
     * Keeps queries used for updating indexString field of entities implementing {@link SearchableEntity}. The field is used for global search {@link com.openkoda.controller.GlobalSearchController}.
     * By means of the queries the indexString is updated continuously as configured in {@link JobsScheduler#searchIndexUpdaterJob()}
     */
    private static String[] searchIndexUpdates = {};
    private static final CamelCaseToUnderscoresNamingStrategy camelCaseToUnderscoredNamingStrategy = new CamelCaseToUnderscoresNamingStrategy();

    /** Run on the application startup.
     */
    public synchronized static void discoverSearchableRepositories() {
        if (discoveryCompleted) {
            return;
        }
        ApplicationContext c = ApplicationContextProvider.getContext();

        Map<String, SecureRepository> searchableRepositoryBeans =
                c.getBeansOfType(SecureRepository.class);

        int searchableRepositoriesCount = 0;
        int globalSearchableRepositoriesCount = 0;

        //Count repositories
        for (Map.Entry<String, SecureRepository> e : searchableRepositoryBeans.entrySet() ) {
            //skipping GlobalSearchRepository
            if ("globalSearchRepository".equals(e.getKey())) {
                continue;
            }
            SearchableRepositoryMetadata gsa = getGlobalSearchableRepositoryAnnotation(e.getValue());
            if (gsa == null) {
                String message = String.format("Repository %s have to be annotated with SearchableRepositoryMetadata", e.getValue().getClass().getName());
                throw new RuntimeException(message);
            }
            searchableRepositoriesCount++;
            if (gsa.includeInGlobalSearch()) {
                globalSearchableRepositoriesCount++;
            }
        }
        searchableRepositories = new SecureRepository[searchableRepositoriesCount];
        searchIndexUpdates = new String[searchableRepositoriesCount];
        globalSearchableRepositories = new SecureRepository[globalSearchableRepositoriesCount];

        int sk = 0;
        int gsk = 0;
        for (Map.Entry<String, SecureRepository> e : searchableRepositoryBeans.entrySet() ) {
            //skipping GlobalSearchRepository
            if ("globalSearchRepository".equals(e.getKey())) {
                continue;
            }
            SearchableRepositoryMetadata gsa = getGlobalSearchableRepositoryAnnotation(e.getValue());
            String tableName = discoverTableName(gsa.entityClass());

            searchIndexUpdates[sk] = String.format("UPDATE %s SET %s = (%s) where (CURRENT_TIMESTAMP - %s < interval '00:01:01')",
                tableName, INDEX_STRING_COLUMN, gsa.searchIndexFormula(), UPDATED_ON);
            searchableRepositories[sk++] = e.getValue();
            if (gsa.includeInGlobalSearch()) {
                globalSearchableRepositories[gsk++] = e.getValue();
            }
            searchableRepositoryByEntityKey.put(gsa.entityKey(), e.getValue());
            searchableRepositoryMetadataByEntityKey.put(gsa.entityKey(), gsa);
            searchableRepositoryMetadataByEntityClass.put(gsa.entityClass(), gsa);
            if (SearchableOrganizationRelatedEntity.class.isAssignableFrom(gsa.entityClass())) {
                searchableOrganizationRelatedRepositoryMetadataByEntityKey.put(gsa.entityKey(), gsa);
                searchableOrganizationRelatedRepositoryMetadataByEntityClass.put(gsa.entityClass(), gsa);
            }
//            CustomPostgreSQLDialect.registerDescriptionFunction(gsa);
        }
        discoveryCompleted = true;
    }


    public static ScopedSecureRepository<?>[] getSearchableRepositories() {
        return searchableRepositories;
    }

    public static SearchableRepositoryMetadata getGlobalSearchableRepositoryAnnotation(ScopedSecureRepository r) {
        return r.getSearchableRepositoryMetadata();
    }

    private static final Map<String, SecureRepository> searchableRepositoryByEntityKey = new HashMap<>();
    private static final Map<String, SearchableRepositoryMetadata> searchableRepositoryMetadataByEntityKey = new HashMap<>();
    private static final Map<Class, SearchableRepositoryMetadata> searchableRepositoryMetadataByEntityClass = new HashMap<>();
    private static final Map<String, SearchableRepositoryMetadata> searchableOrganizationRelatedRepositoryMetadataByEntityKey = new HashMap<>();
    private static final Map<Class, SearchableRepositoryMetadata> searchableOrganizationRelatedRepositoryMetadataByEntityClass = new HashMap<>();
    public static ScopedSecureRepository getSearchableRepository(String entityKey, HasSecurityRules.SecurityScope scope) {
        return new SecureRepositoryWrapper(searchableRepositoryByEntityKey.get(entityKey), scope);
    }
    public static Class<SearchableEntity> getSearchableRepositoryEntityClass(String entityKey) {
        SearchableRepositoryMetadata gsa = searchableRepositoryMetadataByEntityKey.get(entityKey);
        if (gsa == null) { return null; }
        return (Class<SearchableEntity>) gsa.entityClass();
    }
    public static SearchableRepositoryMetadata getSearchableRepositoryMetadata(String entityKey) {
        return searchableRepositoryMetadataByEntityKey.get(entityKey);
    }
    public static SearchableRepositoryMetadata getSearchableRepositoryMetadata(Class entityClass) {
        return searchableRepositoryMetadataByEntityClass.get(entityClass);
    }

    public static String[] getSearchIndexUpdates() {
        return searchIndexUpdates;
    }
    public static String discoverTableName(Class c) {
        Table table = (Table) c.getAnnotation(Table.class);
        if ( table != null && StringUtils.isNotBlank(table.name())) {
            return table.name();
        }
        Identifier i = camelCaseToUnderscoredNamingStrategy.toPhysicalTableName(Identifier.toIdentifier(c.getSimpleName()), null);
        return i.getText();
    }


}
