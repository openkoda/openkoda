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

package com.openkoda.model.event;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.openkoda.core.helper.NameHelper;
import com.openkoda.model.Organization;
import com.openkoda.model.PrivilegeNames;
import com.openkoda.model.common.*;
import jakarta.persistence.*;
import org.apache.commons.lang3.StringUtils;
import org.assertj.core.util.Arrays;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Formula;

/**
 * Model entity which keeps all the listeners registered in application in app's database
 *
 * @author Martyna Litkowska (mlitkowska@stratoflow.com)
 * @since 2019-03-11
 */
@Entity
@Table(name = "event_listener",
        uniqueConstraints = @UniqueConstraint(columnNames = {"event_name", "consumer_method_name", "static_data_1", "static_data_2", "static_data_3", "static_data_4"}))
public class EventListenerEntry extends TimestampedEntity implements SearchableOrganizationRelatedEntity, LongIdEntity,
        AuditableEntityOrganizationRelated, EntityWithRequiredPrivilege {

    public static final String REFERENCE_FORMULA = DEFAULT_ORGANIZATION_RELATED_REFERENCE_FIELD_FORMULA;

    @Id
    @SequenceGenerator(name = ORGANIZATION_RELATED_ID_GENERATOR, sequenceName = ORGANIZATION_RELATED_ID_GENERATOR, initialValue = ModelConstants.INITIAL_ORGANIZATION_RELATED_VALUE, allocationSize = 10)
    @GeneratedValue(generator = ModelConstants.ORGANIZATION_RELATED_ID_GENERATOR, strategy = GenerationType.SEQUENCE)
    private Long id;

    @Column(name = "event_class_name")
    private String eventClassName;

    @Column(name = "event_name")
    private String eventName;

    @Column(name = "event_object_type")
    private String eventObjectType;

    @Column(name = "consumer_class_name")
    private String consumerClassName;

    @Column(name = "consumer_method_name")
    private String consumerMethodName;

    @Column(name = "consumer_parameter_class_name")
    private String consumerParameterClassName;

    @Column(name = "static_data_1")
    private String staticData1;

    @Column(name = "static_data_2")
    private String staticData2;

    @Column(name = "static_data_3")
    private String staticData3;

    @Column(name = "static_data_4")
    private String staticData4;

    @Column(name = INDEX_STRING_COLUMN, length = INDEX_STRING_COLUMN_LENGTH, insertable = false)
    @ColumnDefault("''")
    private String indexString;

    //TODO Rule 4.4: should be marked with FetchType = LAZY
    @JsonIgnore
    @ManyToOne(optional = true)
    @JoinColumn(nullable = true, insertable = false, updatable = false, name = ModelConstants.ORGANIZATION_ID)
    private Organization organization;
    @Column(nullable = true, name = ModelConstants.ORGANIZATION_ID)
    private Long organizationId;

    @Formula(REFERENCE_FORMULA)
    private String referenceString;

    @Formula("( '" + PrivilegeNames._canReadBackend + "' )")
    private String requiredReadPrivilege;

    @Formula("( '" + PrivilegeNames._canManageBackend + "' )")
    private String requiredWritePrivilege;

    @Override
    public String getReferenceString() {
        return referenceString;
    }


    public EventListenerEntry() {
    }

    public EventListenerEntry(String eventClassName, String eventName, String eventObjectType, String consumerClassName, String consumerMethodName) {
        this.eventClassName = eventClassName;
        this.eventName = eventName;
        this.eventObjectType = eventObjectType;
        this.consumerClassName = consumerClassName;
        this.consumerMethodName = consumerMethodName;
        this.staticData1 = null;
        this.staticData2 = null;
        this.staticData3 = null;
        this.staticData4 = null;
    }

    public EventListenerEntry(String eventClassName, String eventName, String eventObjectType, String consumerClassName, String consumerMethodName,
                              String consumerParameterClassName, String staticData1, String staticData2, String staticData3, String staticData4) {
        this.eventClassName = eventClassName;
        this.eventName = eventName;
        this.eventObjectType = eventObjectType;
        this.consumerClassName = consumerClassName;
        this.consumerMethodName = consumerMethodName;
        this.consumerParameterClassName = consumerParameterClassName;
        this.staticData1 = staticData1;
        this.staticData2 = staticData2;
        this.staticData3 = staticData3;
        this.staticData4 = staticData4;
    }


    public EventListenerEntry(EventListenerEntry entry) {
        this.eventClassName = entry.getEventClassName();
        this.eventName = entry.getEventName();
        this.eventObjectType = entry.getEventObjectType();
        this.consumerClassName = entry.getConsumerClassName();
        this.consumerMethodName = entry.getConsumerMethodName();
        this.staticData1 = entry.getStaticData1();
        this.staticData2 = entry.getStaticData2();
        this.staticData3 = entry.getStaticData3();
        this.staticData4 = entry.getStaticData4();
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEventClassName() {
        return eventClassName;
    }

    public String getEventClassName(boolean userFriendly) {
        if (userFriendly) {
            return NameHelper.getClassName(getEventClassName());
        }
        return getEventClassName();
    }


    public void setEventClassName(String eventClassName) {
        this.eventClassName = eventClassName;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public String getConsumerClassName() {
        return consumerClassName;
    }

    public String getConsumerClassName(boolean userFriendly) {
        if (userFriendly) {
            return NameHelper.getClassName(getConsumerClassName());
        }
        return getConsumerClassName();
    }

    public void setConsumerClassName(String consumerClassName) {
        this.consumerClassName = consumerClassName;
    }

    public String getConsumerMethodName() {
        return consumerMethodName;
    }

    public void setConsumerMethodName(String consumerMethodName) {
        this.consumerMethodName = consumerMethodName;
    }

    public String getStaticData1() {
        return staticData1;
    }

    public void setStaticData1(String staticData1) {
        this.staticData1 = staticData1;
    }


    public String getStaticData2() {
        return staticData2;
    }

    public void setStaticData2(String staticData2) {
        this.staticData2 = staticData2;
    }

    public String getStaticData3() {
        return staticData3;
    }

    public void setStaticData3(String staticData3) {
        this.staticData3 = staticData3;
    }

    public String getStaticData4() {
        return staticData4;
    }

    public void setStaticData4(String staticData4) {
        this.staticData4 = staticData4;
    }

    public void setIndexString(String indexString) {
        this.indexString = indexString;
    }

    public String getEventObjectType() {
        return eventObjectType;
    }

    public String getEventObjectType(boolean userFriendly) {
        if (userFriendly)
            return NameHelper.getClassName(getEventObjectType());
        return getEventObjectType();
    }

    public void setEventObjectType(String eventObjectType) {
        this.eventObjectType = eventObjectType;
    }

    @Override
    public String getIndexString() {
        return indexString;
    }

    @Override
    public Long getId() {
        return id;
    }

    public String getEventString() {
        return StringUtils.join(Arrays.array(eventClassName, eventName, eventObjectType), ",");
    }

    public String getConsumerString() {
        int n = staticData1 == null ? 0 : staticData2 == null ? 1 :staticData3 == null ? 2 :staticData4 == null ? 3 : 4;
        return Consumer.canonicalMethodName(consumerClassName, consumerMethodName, consumerParameterClassName, n);
    }

    public Organization getOrganization() {
        return organization;
    }

    public void setOrganization(Organization organization) {
        this.organization = organization;
    }

    @Override
    public Long getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(Long organizationId) {
        this.organizationId = organizationId;
    }

    public String getConsumerParameterClassName() {
        return consumerParameterClassName;
    }

    public void setConsumerParameterClassName(String consumerParameterClassName) {
        this.consumerParameterClassName = consumerParameterClassName;
    }

    @Override
    public String toString() {
        return "EventListenerEntry{" +
                "eventClassName='" + eventClassName + '\'' +
                ", eventName='" + eventName + '\'' +
                ", eventObjectType='" + eventObjectType + '\'' +
                ", consumerClassName='" + consumerClassName + '\'' +
                ", consumerMethodName='" + consumerMethodName + '\'' +
                ", staticData1='" + staticData1 + '\'' +
                ", staticData2='" + staticData2 + '\'' +
                ", staticData3='" + staticData3 + '\'' +
                ", staticData4='" + staticData4 + '\'' +
                '}';
    }
    @Override
    public String toAuditString() {
        return "ID: " + this.getId();
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
