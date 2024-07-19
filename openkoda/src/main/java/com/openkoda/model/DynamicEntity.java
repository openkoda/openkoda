package com.openkoda.model;

import com.openkoda.model.common.AuditableEntity;
import com.openkoda.model.common.ModelConstants;
import com.openkoda.model.common.TimestampedEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.util.Collection;

@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"table_name"}))
public class DynamicEntity extends TimestampedEntity implements AuditableEntity {
    @Id
    @SequenceGenerator(name = GLOBAL_ID_GENERATOR, sequenceName = GLOBAL_ID_GENERATOR, initialValue = ModelConstants.INITIAL_GLOBAL_VALUE, allocationSize = 10)
    @GeneratedValue(generator = ModelConstants.GLOBAL_ID_GENERATOR, strategy = GenerationType.SEQUENCE)
    private Long id;
    @NotNull
    @Column(name = "table_name")
    private String tableName;

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public String toAuditString() {
        return tableName;
    }

    @Override
    public Collection<String> ignorePropertiesInAudit() {
        return AuditableEntity.super.ignorePropertiesInAudit();
    }

    @Override
    public Collection<String> contentProperties() {
        return AuditableEntity.super.contentProperties();
    }

}
