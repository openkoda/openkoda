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
import jakarta.persistence.*;
import org.hibernate.annotations.ColumnDefault;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * <p>Abstract entity adding creating by information </p>
 *
 * @author Arkadiusz Drysch (adrysch@stratoflow.com)
 * 
 */
@MappedSuperclass
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@EntityListeners(AuditingEntityListener.class)
public abstract class TimestampedEntity implements ModelConstants, Serializable {

    @Embeddable
    public static class UID implements Serializable {
        String createdBy;
        Long createdById;

        public UID() {
            this.createdBy = "UNKNOWN";
            this.createdById = -1L;
        }

        public UID(String name, Long id) {
            this.createdById = id;
            this.createdBy = name;
        }

        public String getCreatedBy() {
            return createdBy;
        }

        public void setCreatedBy(String createdBy) {
            this.createdBy = createdBy;
        }

        public Long getCreatedById() {
            return createdById;
        }

        public void setCreatedById(Long createdById) {
            this.createdById = createdById;
        }

        @Override
        public String toString() {
            return createdBy + ", ID=" + createdById;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            UID uid = (UID) o;
            return Objects.equals(createdBy, uid.createdBy) &&
                    Objects.equals(createdById, uid.createdById);
        }

        @Override
        public int hashCode() {
            return Objects.hash(createdBy, createdById);
        }
    }

    @CreatedBy
    @JsonIgnore
    private UID createdBy;

    @CreatedDate
    @Column(name = CREATED_ON, columnDefinition = "TIMESTAMP WITH TIME ZONE", insertable = false, updatable = false)
    @ColumnDefault("CURRENT_TIMESTAMP")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime createdOn;

    @LastModifiedBy
    @AttributeOverrides(value = {
            @AttributeOverride(name = "createdBy", column = @Column(name = "modifiedBy")),
            @AttributeOverride(name = "createdById", column = @Column(name = "modifiedById"))
    })
    @JsonIgnore
    private UID modifiedBy;

    @LastModifiedDate
    @Column(name = UPDATED_ON, columnDefinition = "TIMESTAMP WITH TIME ZONE", insertable = false)
    @ColumnDefault("CURRENT_TIMESTAMP")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    protected LocalDateTime updatedOn;

    /**
     * This method should return entity id
     *
     * @return entity id
     */
    public abstract Long getId();

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return new StringBuilder().append(this.getClass().getSimpleName()).append(" id: ").append(getId()).toString();
    }

    /**
     * <p>postUpdate.</p>
     */
    @PostUpdate
    protected void postUpdate() {
        updatedOn = LocalDateTime.now();
    }

    /**
     * <p>Getter for the field <code>createdOn</code>.</p>
     *
     * @return a {@link java.time.LocalDateTime} object.
     */
    public LocalDateTime getCreatedOn() {
        return createdOn;
    }

    /**
     * <p>Getter for the field <code>updatedOn</code>.</p>
     *
     * @return a {@link java.time.LocalDateTime} object.
     */
    public LocalDateTime getUpdatedOn() {
        return updatedOn;
    }

    /**
     * <p>Getter for the field <code>createdBy</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public UID getCreatedBy() {
        return createdBy;
    }

    /**
     * <p>Getter for the field <code>modifiedBy</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public UID getModifiedBy() {
        return modifiedBy;
    }
}
