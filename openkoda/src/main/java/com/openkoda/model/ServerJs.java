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

package com.openkoda.model;

import com.openkoda.controller.common.PageAttributes;
import com.openkoda.core.flow.PageModelMap;
import com.openkoda.core.helper.JsonHelper;
import com.openkoda.model.common.OpenkodaEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.Formula;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

@Entity
@Table (name = "server_js")
public class ServerJs extends OpenkodaEntity {

    @Column
    private String name;

    @Column(length = 65536 * 4)
    private String code;

    @Column(length = 65536 * 4)
    private String model;

    @Column(length = 65536 * 4)
    private String arguments;

    @Formula("( '" + PrivilegeNames._canReadBackend + "' )")
    private String requiredReadPrivilege;

    @Formula("( '" + PrivilegeNames._canManageBackend + "' )")
    private String requiredWritePrivilege;

    public ServerJs() {
        super(null);
    }

    public ServerJs(Long organizationId) {
        super(organizationId);
    }

    public ServerJs(String code, String model, String arguments) {
        super(null);
        this.code = code;
        this.model = model;
        this.arguments = arguments;
    }
    
    public String getName() {
        return this.name;
    }

    public String getCode() {
        return this.code;
    }

    public String getModel() {
        return this.model;
    }

    //TODO: move this logic to some helper class
    public PageModelMap getModelMap() throws IOException {
        PageModelMap result = JsonHelper.fromDebugJson(this.model);
        result.put(PageAttributes.arguments,
            StringUtils.isBlank(this.arguments) ?
                new ArrayList<>() :
                Arrays.asList(StringUtils.split(this.arguments, "\n")));
        return result;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getArguments() {
        return arguments;
    }

    public void setArguments(String arguments) {
        this.arguments = arguments;
    }

    @Override
    public String toAuditString() {
        return null;
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