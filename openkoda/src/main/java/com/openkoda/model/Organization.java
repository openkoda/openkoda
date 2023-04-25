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

import com.openkoda.dto.CanonicalObject;
import com.openkoda.model.common.*;
import jakarta.persistence.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.Formula;

/**
 *
 *
 * @author Arkadiusz Drysch (adrysch@stratoflow.com)
 * 
 */
@Entity
@DynamicUpdate
public class Organization extends TimestampedEntity implements AuditableEntity, SearchableOrganizationRelatedEntity, CanonicalObject, EntityWithRequiredPrivilege {

    public static final String REFERENCE_FORMULA = "(id)";

    @Id
    @SequenceGenerator(name = ORGANIZATION_ID_GENERATOR, sequenceName = ORGANIZATION_ID_GENERATOR,
            initialValue = ModelConstants.INITIAL_ORGANIZATION_VALUE, allocationSize = 1)
    @GeneratedValue(generator = ModelConstants.ORGANIZATION_ID_GENERATOR, strategy = GenerationType.SEQUENCE)
    private Long id;

    @Column
    private String name;

    //added to comply with com.openkoda.model.common.OrganizationRelatedEntity
    @Column(name = "id", insertable = false, updatable = false)
    private Long organizationId;

    @Column(name = INDEX_STRING_COLUMN, length = INDEX_STRING_COLUMN_LENGTH, insertable = false)
    @ColumnDefault("''")
    private String indexString;

    @Formula(REFERENCE_FORMULA)
    private String referenceString;


    @Column(name = "assigned_datasource", columnDefinition = "INTEGER DEFAULT 0")
    private int assignedDatasource;

    @Override
    public String getReferenceString() {
        return referenceString;
    }

    @Formula("( '" + PrivilegeNames._readOrgData + "' )")
    private String requiredReadPrivilege;

    @Formula("( '" + PrivilegeNames._manageOrgData + "' )")
    private String requiredWritePrivilege;

    public Organization() {
    }

    public Organization(Long organizationId){
        this.organizationId=organizationId;
    }

    public Organization(String name) {
        this.name = name;
        this.assignedDatasource = 0;
    }

    public Organization(String name, Integer assignedDatasource) {
        this.name = name;
        this.assignedDatasource = assignedDatasource == null ? 0 : assignedDatasource;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toAuditString() {
        return name;
    }

    @Override
    public Long getOrganizationId() {
        return organizationId;
    }

    @Override
    public String getIndexString() {
        return indexString;
    }

    public int getAssignedDatasource() {
        return assignedDatasource;
    }

    public void setAssignedDatasource(int assignedDatasource) {
        this.assignedDatasource = assignedDatasource;
    }

    @Override
    public String notificationMessage() {
        return String.format("org: %s, org id: %s", name, id);
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
