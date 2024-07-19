package com.openkoda.repository.ai;


import com.openkoda.core.repository.common.UnsecuredFunctionalRepositoryWithLongId;
import com.openkoda.core.security.HasSecurityRules;
import com.openkoda.model.report.QueryReport;
import org.springframework.stereotype.Repository;

@Repository
public interface QueryReportRepository extends UnsecuredFunctionalRepositoryWithLongId<QueryReport>, HasSecurityRules {
}
