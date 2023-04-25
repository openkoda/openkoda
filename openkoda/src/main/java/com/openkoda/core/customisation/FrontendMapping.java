package com.openkoda.core.customisation;

import com.openkoda.core.form.FrontendMappingDefinition;
import com.openkoda.core.repository.common.SearchableFunctionalRepositoryWithLongId;

public record FrontendMapping(FrontendMappingDefinition definition, SearchableFunctionalRepositoryWithLongId repository) { }
