package com.openkoda.repository.ai;

import com.openkoda.model.common.SearchableRepositoryMetadata;
import com.openkoda.model.report.QueryReport;
import com.openkoda.repository.SecureRepository;
import org.springframework.stereotype.Repository;

import static com.openkoda.controller.common.URLConstants.QUERY_REPORT;


@Repository
@SearchableRepositoryMetadata(
        entityKey = QUERY_REPORT,
        entityClass = QueryReport.class,
        descriptionFormula = "'name: ' || COALESCE(name, '') || ' query:  ' || COALESCE(query, '') || ' orgId:' || COALESCE(CAST (organization_id as text), '')",
        searchIndexFormula = "'name: ' || lower(COALESCE(name, '')) || ' query:  ' || lower(COALESCE(query, '')) || ' orgId:' || COALESCE(CAST (organization_id as text), '')"
)
public interface SecureQueryReportRepository extends SecureRepository<QueryReport> {

}
