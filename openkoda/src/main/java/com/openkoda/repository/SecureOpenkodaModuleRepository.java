package com.openkoda.repository;

import com.openkoda.model.OpenkodaModule;
import com.openkoda.model.common.SearchableRepositoryMetadata;
import org.springframework.stereotype.Repository;

import static com.openkoda.controller.common.URLConstants.MODULE;


@Repository
@SearchableRepositoryMetadata(
        entityKey = MODULE,
        descriptionFormula = "(''||name)",
        entityClass = OpenkodaModule.class
)
public interface SecureOpenkodaModuleRepository extends SecureRepository<OpenkodaModule> {

}
