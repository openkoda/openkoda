package com.openkoda.repository;

import com.openkoda.core.repository.common.UnsecuredFunctionalRepositoryWithLongId;
import com.openkoda.core.security.HasSecurityRules;
import com.openkoda.integration.model.configuration.IntegrationModuleOrganizationConfiguration;
import org.springframework.stereotype.Repository;

@Repository
public interface IntegrationRepository extends UnsecuredFunctionalRepositoryWithLongId<IntegrationModuleOrganizationConfiguration>, HasSecurityRules {

    IntegrationModuleOrganizationConfiguration findByOrganizationId(Long organizationId);
}
