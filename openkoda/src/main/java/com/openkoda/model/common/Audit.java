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

import jakarta.persistence.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Formula;

import java.util.Collection;

/**
 * An Entity class that represents audit entry created by the system on selected
 * application events like: access to particular data, execution of particular
 * actions etc.
 *
 * @author Arkadiusz Drysch (adrysch@stratoflow.com)
 */
@Entity
public class Audit extends TimestampedEntity implements SearchableEntity, OrganizationRelatedEntity {

   public static final String REFERENCE_FORMULA = DEFAULT_ORGANIZATION_RELATED_REFERENCE_FIELD_FORMULA;

   public enum AuditOperation {
      ADD, EDIT, DELETE, BROWSE, ASSIGN;
   }

   public enum Severity {
      INFO, WARNING, ERROR
   }


   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   private Long id;

   @Column
   private Long userId;

   @Column(length=2047)
   private String userRoleIds;

   /**
    * <p>Getter for the field <code>userRoleIds</code>.</p>
    *
    * @return a {@link java.lang.String} object.
    */
   public String getUserRoleIds() {
      return userRoleIds;
   }

   /**
    * <p>Setter for the field <code>userRoleIds</code>.</p>
    *
    * @param userRoleIds a {@link java.util.Collection} object.
    */
   public void setUserRoleIds(Collection<?> userRoleIds) {
      this.userRoleIds = userRoleIds == null ? "" : userRoleIds.toString();
   }

   private String entityName;
   private String entityKey;

   @Enumerated(EnumType.STRING)
   private AuditOperation operation;

   @Enumerated(EnumType.STRING)
   private Severity severity;

   @Column
   private Long entityId;

   @Column
   private Long organizationId;

   @Column(length=16380)
   private String change;

   @Column
   private String ipAddress;

   @Column
   private String requestId;

   @Column(columnDefinition = "TEXT")
   private String content;

   @Column(name = INDEX_STRING_COLUMN, length = INDEX_STRING_COLUMN_LENGTH, insertable = false)
   @ColumnDefault("''")
   private String indexString;

   @Formula(REFERENCE_FORMULA)
   private String referenceString;

   @Override
   public String getReferenceString() {
      return referenceString;
   }


   /**
    * <p>Getter for the field <code>userId</code>.</p>
    *
    * @return a {@link java.lang.Long} object.
    */
   public Long getUserId() {
      return userId;
   }

   /**
    * <p>Setter for the field <code>userId</code>.</p>
    *
    * @param userId a {@link java.lang.Long} object.
    */
   public void setUserId(Long userId) {
      this.userId = userId;
   }

   /**
    * <p>Getter for the field <code>id</code>.</p>
    *
    * @return a {@link java.lang.Long} object.
    */
   public Long getId() {
      return id;
   }

   /**
    * <p>Getter for the field <code>entityId</code>.</p>
    *
    * @return a {@link java.lang.Long} object.
    */
   public Long getEntityId() {
      return entityId;
   }

   /**
    * <p>Setter for the field <code>entityId</code>.</p>
    *
    * @param entityId a {@link java.lang.Long} object.
    */
   public void setEntityId(Long entityId) {
      this.entityId = entityId;
   }

   /**
    * <p>Getter for the field <code>severity</code>.</p>
    *
    * @return a {@link com.openkoda.model.common.Audit.Severity} object.
    */
   public Severity getSeverity() {
      return severity;
   }

   /**
    * <p>Setter for the field <code>severity</code>.</p>
    *
    * @param severity a {@link com.openkoda.model.common.Audit.Severity} object.
    */
   public void setSeverity(Severity severity) {
      this.severity = severity;
   }

   /**
    * <p>Getter for the field <code>change</code>.</p>
    *
    * @return a {@link java.lang.String} object.
    */
   public String getChange() {
      return change;
   }

   /**
    * <p>Setter for the field <code>change</code>.</p>
    *
    * @param change a {@link java.lang.String} object.
    */
   public void setChange(String change) {
      this.change = change;
   }

   /**
    * <p>Getter for the field <code>ipAddress</code>.</p>
    *
    * @return a {@link java.lang.String} object.
    */
   public String getIpAddress() {
      return ipAddress;
   }

   /**
    * <p>Setter for the field <code>ipAddress</code>.</p>
    *
    * @param ipAddress a {@link java.lang.String} object.
    */
   public void setIpAddress(String ipAddress) {
      this.ipAddress = ipAddress;
   }

   public String getRequestId() {
      return requestId;
   }

   public void setRequestId(String requestId) {
      this.requestId = requestId;
   }

   /**
    * <p>Getter for the field <code>entityName</code>.</p>
    *
    * @return a {@link java.lang.String} object.
    */
   public String getEntityName() {
      return entityName;
   }

   /**
    * <p>Setter for the field <code>entityName</code>.</p>
    *
    * @param entityName a {@link java.lang.String} object.
    */
   public void setEntityName(String entityName) {
      this.entityName = entityName;
   }

   public String getEntityKey() {
      return entityKey;
   }

   public void setEntityKey(String entityKey) {
      this.entityKey = entityKey;
   }

   /**
    * <p>Getter for the field <code>operation</code>.</p>
    *
    * @return a {@link com.openkoda.model.common.Audit.AuditOperation} object.
    */
   public AuditOperation getOperation() {
      return operation;
   }

   /**
    * <p>Setter for the field <code>operation</code>.</p>
    *
    * @param operation a {@link com.openkoda.model.common.Audit.AuditOperation} object.
    */
   public void setOperation(AuditOperation operation) {
      this.operation = operation;
   }

   /**
    * <p>Getter for the field <code>organizationId</code>.</p>
    *
    * @return a {@link java.lang.Long} object.
    */
   public Long getOrganizationId() {
      return organizationId;
   }

   /**
    * <p>Setter for the field <code>organizationId</code>.</p>
    *
    * @param organizationId a {@link java.lang.Long} object.
    */
   public void setOrganizationId(Long organizationId) {
      this.organizationId = organizationId;
   }

   /** {@inheritDoc} */
   @Override
   public String getIndexString() {
      return indexString;
   }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
