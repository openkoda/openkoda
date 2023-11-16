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

package com.openkoda.model;

import com.openkoda.model.common.OpenkodaEntity;
import jakarta.persistence.*;
import org.hibernate.annotations.Formula;

@Entity
@Table(name = "form")
public class Form extends OpenkodaEntity {

    @Column
    private String name;

    @Column(length = 65536 * 4)
    private String code;

//    read privilege for the registered form
    @Column(name = "read_privilege")
    @Enumerated(EnumType.STRING)
    private Privilege readPrivilege;

//    write privilege for the registered form
    @Column(name = "write_privilege")
    @Enumerated(EnumType.STRING)
    private Privilege writePrivilege;

//    read privilege for form entity
    @Formula("( '" + PrivilegeNames._canReadBackend + "' )")
    private String requiredReadPrivilege;

//    write privilege for form entity
    @Formula("( '" + PrivilegeNames._canManageBackend + "' )")
    private String requiredWritePrivilege;

    private boolean registerApiCrudController;

    private boolean registerHtmlCrudController;

    private String tableColumns;

    public Form() {
        super(null);
        registerApiCrudController = false;
        registerHtmlCrudController = false;
    }

    public Form(Long organizationId) {
        super(organizationId);
        registerApiCrudController = false;
        registerHtmlCrudController = false;
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

    public Privilege getReadPrivilege() {
        return readPrivilege;
    }

    public void setReadPrivilege(Privilege readPrivilege) {
        this.readPrivilege = readPrivilege;
    }

    public Privilege getWritePrivilege() {
        return writePrivilege;
    }

    public void setWritePrivilege(Privilege writePrivilege) {
        this.writePrivilege = writePrivilege;
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

    public String getTableColumns() {
        return tableColumns;
    }

    public void setTableColumns(String tableColumns) {
        this.tableColumns = tableColumns.replaceAll("\\s", "");
    }

    public String[] getTableColumnsList() {
        return tableColumns.split(",");
    }
}
