/*
MIT License

Copyright (c) 2016-2023, Openkoda CDX Sp. z o.o. Sp. K. <openkoda.com>

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
documentation files (the "Software"), to deal in the Software without restriction, including without limitation
the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software,
and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice
shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR
A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS
OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR
IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*/

package com.openkoda.model.common;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.openkoda.model.Organization;
import jakarta.persistence.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Formula;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@MappedSuperclass
@Inheritance(
        strategy = InheritanceType.TABLE_PER_CLASS
)
@EntityListeners({AuditingEntityListener.class})
public abstract class OpenkodaEntity implements ModelConstants, Serializable, SearchableOrganizationRelatedEntity, AuditableEntityOrganizationRelated, EntityWithRequiredPrivilege {

    @Id
    @SequenceGenerator(name = ORGANIZATION_RELATED_ID_GENERATOR, sequenceName = ORGANIZATION_RELATED_ID_GENERATOR, initialValue = ModelConstants.INITIAL_ORGANIZATION_RELATED_VALUE, allocationSize = 10)
    @GeneratedValue(generator = ORGANIZATION_RELATED_ID_GENERATOR, strategy = GenerationType.SEQUENCE)
    protected Long id;

    @JsonIgnore
    @ManyToOne(optional = true, fetch = FetchType.LAZY)
    @JoinColumn(nullable = true, insertable = false, updatable = false, name = ORGANIZATION_ID)
    protected Organization organization;

    @Column(nullable = true, name = ORGANIZATION_ID, updatable = false)
    protected Long organizationId;

    @Formula(DEFAULT_ORGANIZATION_RELATED_REFERENCE_FIELD_FORMULA)
    protected String referenceString;

    @Column(name = INDEX_STRING_COLUMN, length = INDEX_STRING_COLUMN_LENGTH, insertable = false)
    @ColumnDefault("''")
    protected String indexString;

    @CreatedBy
    @JsonIgnore
    protected TimestampedEntity.UID createdBy;
    @CreatedDate
    @Column(
            name = "created_on",
            columnDefinition = "TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP",
            insertable = false,
            updatable = false
    )
    @DateTimeFormat(
            iso = DateTimeFormat.ISO.DATE_TIME
    )
    protected LocalDateTime createdOn;
    @LastModifiedBy
    @AttributeOverrides({@AttributeOverride(
            name = "createdBy",
            column = @Column(
                    name = "modifiedBy"
            )
    ), @AttributeOverride(
            name = "createdById",
            column = @Column(
                    name = "modifiedById"
            )
    )})
    @JsonIgnore
    protected TimestampedEntity.UID modifiedBy;
    @LastModifiedDate
    @Column(
            name = "updated_on",
            columnDefinition = "TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP",
            insertable = false
    )
    @DateTimeFormat(
            iso = DateTimeFormat.ISO.DATE_TIME
    )
    protected LocalDateTime updatedOn;


    @Override
    public String toAuditString() {
        return this.getClass().getSimpleName();
    }

    @Override
    public String getRequiredReadPrivilege() {
        return null;
    }

    @Override
    public String getRequiredWritePrivilege() {
        return null;
    }

    @Override
    public Long getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(Long organizationId) {
        if (this.organizationId == null)
            this.organizationId = organizationId;
    }

    @Override
    public String getReferenceString() {
        return referenceString;
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public String getIndexString() {
        return indexString;
    }

    public OpenkodaEntity(Long organizationId) {
        this.organizationId = organizationId;
    }

    public LocalDateTime getUpdatedOn() {
        return updatedOn;
    }

    public LocalDateTime getCreatedOn() {
        return createdOn;
    }

    @ElementCollection
    @CollectionTable(name = "entity_property",
            joinColumns = {
                    @JoinColumn(name = "entity_id", referencedColumnName = "id")
            },
            foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    @MapKeyColumn(name = "name")
    @Column(name = "value")
    private Map<String, String> properties = new HashMap<>();

    public String getProperty(String name) {
        return properties.get(name);
    }

    public String setProperty(String name, String value) {
        return properties.put(name, value);
    }


}
