package com.openkoda.model;

import com.openkoda.model.common.*;
import jakarta.persistence.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Formula;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.time.LocalDateTime;

import static com.openkoda.model.common.ModelConstants.*;

@Entity
@Table(name = "dynamic_privilege",
    indexes = {
            @Index(columnList = "name")
    }
)
public class DynamicPrivilege implements PrivilegeBase, SearchableEntity, LongIdEntity, AuditableEntity, Serializable, EntityWithRequiredPrivilege  {

    private static final long serialVersionUID = -4712574897029645493L;

    @Id
    @SequenceGenerator(name = GLOBAL_ID_GENERATOR, sequenceName = GLOBAL_ID_GENERATOR, initialValue = ModelConstants.INITIAL_GLOBAL_VALUE, allocationSize = 10)
    @GeneratedValue(generator = ModelConstants.GLOBAL_ID_GENERATOR, strategy = GenerationType.SEQUENCE)
    private Long id;
    
    @Column(unique = false)
    private String category;
    
    @Column(name = "privilege_group")
    @Enumerated(EnumType.STRING)
    private PrivilegeGroup group;
    
    @Column(unique = true, nullable = false)
    private String name;
    
    @Column(unique = true, nullable = false)
    private String label;
    
    @LastModifiedDate
    @Column(name = UPDATED_ON, columnDefinition = "TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP", insertable=false)
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime updatedOn;
    
    @Column(name = INDEX_STRING_COLUMN, length = INDEX_STRING_COLUMN_LENGTH, insertable = false)
    @ColumnDefault("''")
    private String indexString;

    @Column(columnDefinition = "boolean default true")
    private Boolean removable;
    
    @Formula("( '" + PrivilegeNames._canReadBackend + "' )")
    private String requiredReadPrivilege;

    @Formula("( '" + PrivilegeNames._canManageBackend + "' )")
    private String requiredWritePrivilege;
    
    @Override
    public String getCategory() {
        return category;
    }

    @Override
    public PrivilegeGroup getGroup() {
        return group;
    }

    public Long getId() {
        return id;
    }

    @Override
    public String getLabel() {
        return label;
    }

    @Override
    public String name() {
        return name;
    }
    
    public String getName() {
        return name();
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setGroup(PrivilegeGroup group) {
        this.group = group;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }
    
    public void setLabel(String label) {
        this.label = label;
    }

    public LocalDateTime getUpdatedOn() {
        return updatedOn;
    }

    public void setUpdatedOn(LocalDateTime updatedOn) {
        this.updatedOn = updatedOn;
    }

    public String getIndexString() {
        return indexString;
    }

    public void setIndexString(String indexString) {
        this.indexString = indexString;
    }

    public Boolean getRemovable() {
        return removable;
    }

    public void setRemovable(Boolean removable) {
        this.removable = removable;
    }

    public String getRequiredReadPrivilege() {
        return requiredReadPrivilege;
    }

    public void setRequiredReadPrivilege(String requiredReadPrivilege) {
        this.requiredReadPrivilege = requiredReadPrivilege;
    }

    public String getRequiredWritePrivilege() {
        return requiredWritePrivilege;
    }

    public void setRequiredWritePrivilege(String requiredWritePrivilege) {
        this.requiredWritePrivilege = requiredWritePrivilege;
    }
    
    @Override
    public String toAuditString() {
        return this.name;
    }
    
    @Override
    public int hashCode() {
        return name.hashCode();
    }
    
    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof DynamicPrivilege)) {
            return false;
        }
        
        return this.name.equals(((DynamicPrivilege)obj).name);
    }
    
    @Override
    public String toString() {
        return this.name;
    }
}
