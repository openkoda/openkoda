package com.openkoda.service.export.dto;

import static com.openkoda.core.lifecycle.BaseDatabaseInitializer.CORE_MODULE;

public class ComponentDto {

    private String module = CORE_MODULE;
    private Long organizationId;

    public String getModule() {
//        return StringUtils.isNotEmpty(module) ? module : CORE_MODULE;
        return module;
    }

    public void setModule(String module) {
        this.module = module;
    }

    public Long getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(Long organizationId) {
        this.organizationId = organizationId;
    }
}
