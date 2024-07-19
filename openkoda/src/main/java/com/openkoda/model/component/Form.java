/*
MIT License

Copyright (c) 2016-2023, Openkoda CDX Sp. z o.o. Sp. K. <openkoda.com>

Permission is hereby granted, free of charge, to any person obtaining a copy of this software
 and associated documentation files (the "Software"), to deal in the Software without restriction,
including without limitation the rights to use, copy, modify, merge, publish, distribute,
sublicense, and/or sell copies of the Software, and to permit persons to whom the Software
is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice
shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE
AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS
OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES
OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*/

package com.openkoda.model.component;

import com.openkoda.core.helper.PrivilegeHelper;
import com.openkoda.model.PrivilegeBase;
import com.openkoda.model.PrivilegeNames;
import com.openkoda.model.common.ComponentEntity;
import jakarta.persistence.*;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.Formula;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "form")

public class Form extends ComponentEntity {

    private static final long serialVersionUID = 6206245838333775713L;

    final static List<String> contentProperties = Arrays.asList("code");

    @Column
    private String name;

    @Column(length = 65536 * 4)
    private String code;

//    read privilege for the registered form
    @Transient
    private PrivilegeBase readPrivilege;
    
    @Transient
    private long readPrivilegeTimestamp;
    
    @Access( AccessType.PROPERTY )
    @Column(name = "read_privilege")
    private String readPrivilegeString;
    
    @Transient
    private long readPrivilegeStringTimestamp;
    
//    write privilege for the registered form
    @Transient
    private PrivilegeBase writePrivilege;
    
    @Transient
    private long writePrivilegeTimestamp;
    
    //  write privilege for the registered form
    @Access( AccessType.PROPERTY )
    @Column(name = "write_privilege")
    private String writePrivilegeString;
    @Transient
    private long writePrivilegeStringTimestamp;
    
//    read privilege for form entity
    @Formula("( '" + PrivilegeNames._canReadBackend + "' )")
    private String requiredReadPrivilege;

//    write privilege for form entity
    @Formula("( '" + PrivilegeNames._canManageBackend + "' )")
    private String requiredWritePrivilege;

    private boolean registerApiCrudController;

    private boolean registerHtmlCrudController;

    private boolean showOnOrganizationDashboard;

    @Column(columnDefinition = "boolean default true")
    private boolean registerAsAuditable;
    ///@Transient
    ///private boolean registerWithEvents;
    private String tableColumns;
    private String filterColumns;

    private String tableName;

    private String tableView;

    public Form() {
        super(null);
        registerApiCrudController = false;
        registerHtmlCrudController = false;
        showOnOrganizationDashboard = false;
    }

    public Form(Long organizationId) {
        super(organizationId);
        registerApiCrudController = false;
        registerHtmlCrudController = false;
        showOnOrganizationDashboard = false;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public PrivilegeBase getReadPrivilege() {
        if(this.readPrivilegeStringTimestamp > this.readPrivilegeTimestamp || readPrivilegeTimestamp == 0) {
            this.readPrivilege = PrivilegeHelper.valueOfString(this.readPrivilegeString);
        }
        
        return readPrivilege;
    }

    public String getReadPrivilegeString() {
        return this.readPrivilegeString;
    }
    
    public void setReadPrivilege(PrivilegeBase readPrivilege) {
        this.readPrivilege = readPrivilege;
        this.readPrivilegeStringTimestamp = System.nanoTime();
        if(readPrivilege != null) {
            this.readPrivilegeString = readPrivilege.name();
        }
    }
    
    public void setReadPrivilegeString(String privilegeString) {
        this.readPrivilegeString = privilegeString;
        this.readPrivilegeStringTimestamp = System.nanoTime();
    }

    public PrivilegeBase getWritePrivilege() {
        if(this.writePrivilegeStringTimestamp > this.writePrivilegeTimestamp || writePrivilegeTimestamp == 0) {
            this.writePrivilege = PrivilegeHelper.valueOfString(this.writePrivilegeString);
        }
        
        return writePrivilege;
    }
    
    public String getWritePrivilegeString() {
        return this.writePrivilegeString;
    }
    
    public void setWritePrivilegeString(String privilegeString) {
        this.writePrivilegeString = privilegeString;
        this.writePrivilegeStringTimestamp = System.nanoTime();
    }

    public void setWritePrivilege(PrivilegeBase writePrivilege) {
        this.writePrivilege = writePrivilege;
        this.writePrivilegeStringTimestamp = System.nanoTime();
        if(writePrivilege != null) {
            this.writePrivilegeString = writePrivilege.name();
        }
    }

    @Override
    public String getRequiredReadPrivilege() {
        return requiredReadPrivilege;
    }

    @Override
    public String getRequiredWritePrivilege() {
        return requiredWritePrivilege;
    }

    public boolean isRegisterApiCrudController() {
        return registerApiCrudController;
    }

    public void setRegisterApiCrudController(boolean registerApiCrudController) {
        this.registerApiCrudController = registerApiCrudController;
    }

    public boolean isRegisterHtmlCrudController() {
        return registerHtmlCrudController;
    }

    public void setRegisterHtmlCrudController(boolean registerHtmlCrudController) {
        this.registerHtmlCrudController = registerHtmlCrudController;
    }

    public boolean isShowOnOrganizationDashboard() {
        return showOnOrganizationDashboard;
    }

    public void setShowOnOrganizationDashboard(boolean showOnOrganizationDashboard) {
        this.showOnOrganizationDashboard = showOnOrganizationDashboard;
    }

    public boolean isRegisterAsAuditable() {
        return registerAsAuditable;
    }

    public void setRegisterAsAuditable(boolean registerAsAuditable) {
        this.registerAsAuditable = registerAsAuditable;
    }

    public String getTableColumns() {
        return tableColumns;
    }

    public void setTableColumns(String tableColumns) {
        this.tableColumns = StringUtils.isNotEmpty(tableColumns) ? tableColumns.replaceAll("\\s", "") : null;
    }

    public String[] getTableColumnsList() {
        return tableColumns != null ? tableColumns.split(",") : ArrayUtils.EMPTY_STRING_ARRAY;
    }

    public String getFilterColumns() {
        return filterColumns;
    }

    public void setFilterColumns(String filterColumns) {
        this.filterColumns = StringUtils.isNotEmpty(filterColumns) ? filterColumns.replaceAll("\\s", "") : null;
    }

    public String[] getFilterColumnsList() {
        return filterColumns != null ? filterColumns.split(",") : ArrayUtils.EMPTY_STRING_ARRAY;
    }

    public String getTableName() {
        return tableName;
    }
    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getTableView() {
        return tableView;
    }

    public void setTableView(String tableView) {
        this.tableView = tableView;
    }

    public String getReadPrivilegeAsString(){
        return this.readPrivilegeString;
    }

    public String getWritePrivilegeAsString(){
        return this.writePrivilegeString;
    }

    @Override
    public Collection<String> contentProperties() {
        return contentProperties;
    }
}
