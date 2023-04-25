/*
MIT License

Copyright (c) 2016-2022, Codedose CDX Sp. z o.o. Sp. K. <stratoflow.com>

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

package com.openkoda.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.openkoda.core.form.OrganizationRelatedMap;
import com.openkoda.core.helper.JsonHelper;
import com.openkoda.model.common.*;
import jakarta.persistence.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Formula;

@Entity
@Table (name = "map_entity")
public class MapEntity extends TimestampedEntity implements SearchableOrganizationRelatedEntity, AuditableEntityOrganizationRelated, EntityWithRequiredPrivilege {

    public static final String REFERENCE_FORMULA = DEFAULT_ORGANIZATION_RELATED_REFERENCE_FIELD_FORMULA;

    @Id
    @SequenceGenerator(name = ORGANIZATION_RELATED_ID_GENERATOR, sequenceName = ORGANIZATION_RELATED_ID_GENERATOR, initialValue = ModelConstants.INITIAL_ORGANIZATION_RELATED_VALUE, allocationSize = 10)
    @GeneratedValue(generator = ORGANIZATION_RELATED_ID_GENERATOR, strategy = GenerationType.SEQUENCE)
    private Long id;

    @Column(name = INDEX_STRING_COLUMN, length = INDEX_STRING_COLUMN_LENGTH, insertable = false)
    @ColumnDefault("''")
    private String indexString;

    //TODO Rule 4.4: should be marked with FetchType = LAZY
    @JsonIgnore
    @ManyToOne(optional = true)
    @JoinColumn(nullable = true, insertable = false, updatable = false, name = ORGANIZATION_ID)
    private Organization organization;

    @Column(nullable = true, name = ORGANIZATION_ID)
    private Long organizationId;

    @Formula(REFERENCE_FORMULA)
    private String referenceString;

    @Column
    private String key;

    @Column(length = 65536 * 4)
    private String value;

    @Transient
    public OrganizationRelatedMap valueAsMap;

    @Formula("( '" + PrivilegeNames._readOrgData + "' )")
    private String requiredReadPrivilege;

    @Formula("( '" + PrivilegeNames._manageOrgData + "' )")
    private String requiredWritePrivilege;

    public MapEntity() { }

    public MapEntity(Long organizationId) {
        this.organizationId = organizationId;
    }


    @Override
    public String getReferenceString() {
        return referenceString;
    }


    public OrganizationRelatedMap getValueAsMap() {
        if (valueAsMap == null) {
            valueAsMap = JsonHelper.from(this.value, OrganizationRelatedMap.class);
        }
        return valueAsMap;
    }

    public void setValueAsMap(OrganizationRelatedMap valueMap) {
        value = JsonHelper.to(valueMap);
    }

    public void updateValueFromMap() {
        value = JsonHelper.to(valueAsMap);
    }

    @Override
    public String toAuditString() {
        return null;
    }

    @Override
    public String getIndexString() {
        return this.indexString;
    }

    @Override
    public Long getId() {
        return this.id;
    }

    @Override
    public Long getOrganizationId() {
        return this.organizationId;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
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