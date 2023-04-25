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

import com.openkoda.core.helper.PrivilegeHelper;
import com.openkoda.model.common.*;
import jakarta.persistence.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Formula;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Set;

import static com.openkoda.model.common.ModelConstants.*;

@Entity
/**
 * <p>Abstract Role class.</p>
 *
 * @author Arkadiusz Drysch (adrysch@stratoflow.com)
 *
 */
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "type")
@Table(name = "roles")
public abstract class Role implements SearchableEntity, LongIdEntity, AuditableEntity, Serializable, EntityWithRequiredPrivilege {

    @Id
    @SequenceGenerator(name = GLOBAL_ID_GENERATOR, sequenceName = GLOBAL_ID_GENERATOR, initialValue = ModelConstants.INITIAL_GLOBAL_VALUE, allocationSize = 10)
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="IdOrGenerated")
    @GenericGenerator(name="IdOrGenerated", strategy="com.openkoda.core.customisation.UseIdOrGenerate")
    private Long id;

    @LastModifiedDate
    @Column(name = UPDATED_ON, columnDefinition = "TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP", insertable=false)
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime updatedOn;

    private String name;

    @Column(length = 65535)
    private String privileges;

    @Transient
    private Set<Enum> privilegesSet;

    @Column(name = INDEX_STRING_COLUMN, length = INDEX_STRING_COLUMN_LENGTH, insertable = false)
    @ColumnDefault("''")
    private String indexString;

    @Column(columnDefinition = "boolean default true")
    private Boolean removable;

    @Formula("( '" + PrivilegeNames._canReadBackend + "' )")
    private String requiredReadPrivilege;

    @Formula("( '" + PrivilegeNames._canManageBackend + "' )")
    private String requiredWritePrivilege;

    public String getType(){
        DiscriminatorValue val = this.getClass().getAnnotation( DiscriminatorValue.class );
        return val == null ? null : val.value();
    }

    public Role() {}

    public Role(String name) {
        this.name = name;
    }

    public Role(Long id, String name) {
        this.name = name;
        this.id = id;
    }

    /**
     * <p>Getter for the field <code>privilegesSet</code>.</p>
     *
     * @return a {@link java.util.Set} object.
     */
    public Set<Enum> getPrivilegesSet() {
        if ( privilegesSet == null ) {
            privilegesSet = PrivilegeHelper.fromJoinedStringInParenthesisToPrivilegeEnumSet( privileges );
        }
        return privilegesSet;
    }

    /**
     * <p>hasPrivilege.</p>
     *
     * @param privilege a {@link com.openkoda.model.Privilege} object.
     * @return a boolean.
     */
    public boolean hasPrivilege(Privilege privilege) {
        return getPrivilegesSet().contains(privilege);
    }
    public boolean hasPrivilege(String privilege) {
        return getPrivilegesSet().stream().anyMatch(a -> a.name().equals(privilege));
    }

    public void addPrivilege(Enum privilege) {
        Set<Enum> privileges = getPrivilegesSet();
        privileges.add(privilege);
        setPrivilegesSet(privileges);
    }

    /**
     * <p>Getter for the field <code>name</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getName() {
        return name;
    }

    /**
     * <p>Setter for the field <code>name</code>.</p>
     *
     * @param name a {@link java.lang.String} object.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * <p>Setter for the field <code>privilegesSet</code>.</p>
     *
     * @param privilegesSet a {@link java.util.Set} object.
     */
    public void setPrivilegesSet(Set<Enum> privilegesSet) {
        this.privilegesSet = privilegesSet;
        this.privileges = PrivilegeHelper.toJoinedStringInParenthesis( privilegesSet );
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
     * <p>Getter for the field <code>privileges</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getPrivileges() {
        return privileges;
    }

    @Override
    public String getIndexString() {
        return indexString;
    }

    public LocalDateTime getUpdatedOn() {
        return updatedOn;
    }

    @PostUpdate
    protected void postUpdate() {
        updatedOn = LocalDateTime.now();
    }

    public Boolean getRemovable() {
        return removable;
    }

    public void setRemovable(Boolean removable) {
        this.removable = removable;
    }

    @Override
    public String toAuditString() {
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
