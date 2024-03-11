package com.openkoda.model.common;

import com.openkoda.model.OpenkodaModule;
import jakarta.persistence.*;

import static com.openkoda.core.lifecycle.BaseDatabaseInitializer.CORE_MODULE;

@MappedSuperclass
public abstract class ComponentEntity extends OpenkodaEntity {

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(nullable = false, name = "module", referencedColumnName = "name", insertable = false, updatable = false)
    protected OpenkodaModule module;

    @Column(name = "module", nullable = false)
    protected String moduleName = CORE_MODULE;
    public ComponentEntity(Long organizationId) {
        super(organizationId);
    }

    public ComponentEntity() {
        super(null);
    }

    public OpenkodaModule getModule() {
        return module;
    }

    public String getModuleName() {
        return moduleName;
    }

    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }
}
