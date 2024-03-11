package com.openkoda.dto;

import com.openkoda.core.helper.NameHelper;
import com.openkoda.model.Privilege;


public class DataAccessDto implements OrganizationRelatedObject{

    public Long id;
    public String name;
    public String code;
    public Long organizationId;
    public String readPrivilege;
    public String writePrivilege;
    public boolean registerApiCrudController;
    public boolean registerHtmlCrudController;
    public String tableColumns;
    public String existingTableName;
    public String newTableName;
    public boolean createNewTable;
    public String columnNames;
    public String tableView;

    public Long getId() {
        return id;
    }

    public Long setId(Long id) {
        this.id = id;
        return id;
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

    public Long getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(Long organizationId) {
        this.organizationId = organizationId;
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

    public String getTableColumns() {
        return tableColumns;
    }

    public void setTableColumns(String tableColumns) {
        this.tableColumns = tableColumns;
    }

    public String getExistingTableName() {
        return existingTableName;
    }

    public void setExistingTableName(String existingTableName) {
        this.existingTableName = existingTableName;
    }

    public String getNewTableName() {
        return newTableName;
    }

    public void setNewTableName(String newTableName) {
        this.newTableName = newTableName;
    }

    public boolean isCreateNewTable() {
        return createNewTable;
    }

    public void setCreateNewTable(boolean createNewTable) {
        this.createNewTable = createNewTable;
    }

    public String getColumnNames() {
        return columnNames;
    }

    public void setColumnNames(String columnNames) {
        this.columnNames = columnNames;
    }

    public String getTableName(){
        if(isCreateNewTable()){
            return getNewTableName();
        }
        return getExistingTableName();
    }

    public String getTableView() {
        return tableView;
    }

    public void setTableView(String tableView) {
        this.tableView = tableView;
    }

    public String getReadPrivilege() {
        return readPrivilege;
    }

    public void setReadPrivilege(String readPrivilege) {
        this.readPrivilege = readPrivilege;
    }

    public String getWritePrivilege() {
        return writePrivilege;
    }

    public void setWritePrivilege(String writePrivilege) {
        this.writePrivilege = writePrivilege;
    }
}
