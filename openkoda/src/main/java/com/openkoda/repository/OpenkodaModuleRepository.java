package com.openkoda.repository;

import com.openkoda.core.repository.common.UnsecuredFunctionalRepositoryWithLongId;
import com.openkoda.core.security.HasSecurityRules;
import com.openkoda.model.OpenkodaModule;
import org.springframework.stereotype.Repository;


@Repository
public interface OpenkodaModuleRepository extends UnsecuredFunctionalRepositoryWithLongId<OpenkodaModule>, HasSecurityRules {

    OpenkodaModule findByName(String name);
    boolean existsByName(String name);
}
