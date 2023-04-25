package com.openkoda.repository;

import com.openkoda.core.repository.common.UnsecuredFunctionalRepositoryWithLongId;
import com.openkoda.core.security.HasSecurityRules;
import com.openkoda.model.ControllerEndpoint;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ControllerEndpointRepository extends UnsecuredFunctionalRepositoryWithLongId<ControllerEndpoint>, HasSecurityRules {

    List<ControllerEndpoint> findByFrontendResourceId(long frontendResourceId);

    ControllerEndpoint findByFrontendResourceIdAndSubPathAndHttpMethod(long frontendResourceId, String subPath, ControllerEndpoint.HttpMethod httpMethod);
}
