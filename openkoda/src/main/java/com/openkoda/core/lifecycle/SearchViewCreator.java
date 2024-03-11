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

package com.openkoda.core.lifecycle;

import com.openkoda.core.helper.SpringProfilesHelper;
import com.openkoda.core.multitenancy.QueryExecutor;
import com.openkoda.core.tracker.LoggingComponentWithRequestId;
import com.openkoda.model.Organization;
import com.openkoda.model.common.*;
import com.openkoda.repository.SearchableRepositories;
import com.openkoda.repository.SecureRepository;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.Formula;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.openkoda.model.common.ModelConstants.*;

@Component
public class SearchViewCreator implements LoggingComponentWithRequestId {

    @Inject
    protected QueryExecutor queryExecutor;

    public void prepareSearchableRepositories() {
        debug("[prepareSearchableRepositories]");
        String queryString = "";

        if (not(SpringProfilesHelper.isInitializationProfile())) {
            queryString = "DROP VIEW IF EXISTS global_search_view; ";
        }

        Map<String, SecureRepository> repositories = SearchableRepositories.getSearchableRepositoriesWithEntityKeys();
        EntityManager em = null;

        List<String> queries = new ArrayList<>(repositories.size());

        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, SecureRepository> r : repositories.entrySet()) {
            SearchableRepositoryMetadata gsa = SearchableRepositories.getGlobalSearchableRepositoryAnnotation(r.getValue());
            Class c = gsa.entityClass();
            String tableName = SearchableRepositories.discoverTableName(c);
            queries.add(prepareSubquery(c, tableName, gsa));
        }

        queryString += "CREATE OR REPLACE VIEW global_search_view AS " + StringUtils.join(queries, " union ");

        queryExecutor.runQueriesInTransaction(queryString);

    }

    private String prepareSubquery(Class c, String tableName, SearchableRepositoryMetadata gsa) {
        debug("[prepareSubquery]");
        boolean isOrganizationRelated = (OrganizationRelatedEntity.class.isAssignableFrom(c));
        boolean isTimestamped = (TimestampedEntity.class.isAssignableFrom(c)) || (OpenkodaEntity.class.isAssignableFrom(c));
        String organizationColumnName = (Organization.class.isAssignableFrom(c) ? "id" : ModelConstants.ORGANIZATION_ID);
        String urlFormula = isOrganizationRelated ?
                StringUtils.defaultIfBlank(gsa.organizationRelatedPathFormula(),
                ORG_RELATED_PATH_FORMULA_BASE + gsa.entityKey() + ID_VIEW_PATH_FORMULA) :
                StringUtils.defaultIfBlank(gsa.globalPathFormula(),
                GLOBAL_PATH_FORMULA_BASE + gsa.entityKey() + ID_VIEW_PATH_FORMULA);

        return String.format(
            "(select id, '%s' as name, %s as " + ModelConstants.ORGANIZATION_ID
                    + ", %s as " + ModelConstants.CREATED_ON
                    + ", %s as " + ModelConstants.UPDATED_ON
                    + ", (%s) as description"
                    + ", %s as " + REQUIRED_READ_PRIVILEGE_COLUMN
                    + ", (%s) as urlPath"
                    + ", %s from %s)",
                gsa.entityKey(),
                (isOrganizationRelated ? organizationColumnName : "null \\:\\: bigint"),
                (isTimestamped ? ModelConstants.CREATED_ON : "null \\:\\: timestamp"),
                (isTimestamped ? ModelConstants.UPDATED_ON : "null \\:\\: timestamp"),
                gsa.searchIndexFormula(),
                getRequiredReadPrivilege(c),
                urlFormula,
                ModelConstants.INDEX_STRING_COLUMN,
                tableName);
    }


    private String getRequiredReadPrivilege(Class c){
        String requiredReadPrivilege = "null";
        if(EntityWithRequiredPrivilege.class.isAssignableFrom(c)){
           try {
               requiredReadPrivilege = c.getDeclaredField("requiredReadPrivilege").getAnnotation(Formula.class).value();
           } catch (NoSuchFieldException e) {
               e.printStackTrace();
           }
        }
        return requiredReadPrivilege;
    }
}
