package com.openkoda.model;

import com.openkoda.model.common.ModelConstants;
import com.openkoda.model.common.OpenkodaEntity;
import jakarta.persistence.*;
import org.hibernate.annotations.Formula;

@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"name"}))
public class OpenkodaModule extends OpenkodaEntity {

    @Column
    private String name;

    @Formula("( '" + PrivilegeNames._canAccessGlobalSettings + "' )")
    private String requiredWritePrivilege;
    @Formula("( '" + PrivilegeNames._canAccessGlobalSettings + "' )")
    private String requiredReadPrivilege;

    public OpenkodaModule(Long organizationId) {
        super(organizationId);
    }

    public OpenkodaModule() {
        super(null);
    }

    public OpenkodaModule(String name) {
        super(null);
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public String getRequiredReadPrivilege() {
        return requiredReadPrivilege;
    }

    @Override
    public String getRequiredWritePrivilege() {
        return requiredWritePrivilege;
    }
}
