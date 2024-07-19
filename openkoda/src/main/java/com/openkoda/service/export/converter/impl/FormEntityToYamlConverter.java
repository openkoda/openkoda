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

package com.openkoda.service.export.converter.impl;

import com.openkoda.core.flow.LoggingComponent;
import com.openkoda.model.component.Form;
import com.openkoda.service.export.dto.FormConversionDto;
import org.springframework.stereotype.Component;

import static com.openkoda.service.export.FolderPathConstants.*;

@Component
public class FormEntityToYamlConverter extends AbstractEntityToYamlConverter<Form, FormConversionDto> implements LoggingComponent   {

    public String getPathToContentFile(Form entity){
        String orgPath = entity.getOrganizationId() == null ? "" : SUBDIR_ORGANIZATION_PREFIX + entity.getOrganizationId() + "/";

        String entityExportPath = FORM_ + orgPath;
        return EXPORT_CODE_PATH_ + entityExportPath + String.format("%s.js", entity.getName());
    }

    @Override
    public String getContent(Form entity) {
        return entity.getCode();
    }

    public FormConversionDto getConversionDto(Form entity){
        FormConversionDto dto = new FormConversionDto();
        dto.setName(entity.getName());
        dto.setReadPrivilege(entity.getReadPrivilegeAsString());
        dto.setTableColumns(entity.getTableColumns());
        dto.setFilterColumns(entity.getFilterColumns());
        dto.setTableName(entity.getTableName());
        dto.setTableView(entity.getTableView());
        dto.setWritePrivilege(entity.getWritePrivilegeAsString());
        dto.setRegisterApiCrudController(entity.isRegisterApiCrudController());
        dto.setRegisterHtmlCrudController(entity.isRegisterHtmlCrudController());
        dto.setShowOnOrganizationDashboard(entity.isShowOnOrganizationDashboard());
        dto.setCode(getResourcePathToContentFile(entity));
        dto.setModule(entity.getModuleName());
        dto.setOrganizationId(entity.getOrganizationId());
        return dto;
    }

    public String getPathToYamlComponentFile(Form entity){
        return getYamlDefaultFilePath(EXPORT_CONFIG_PATH_ + FORM_, entity.getName(), entity.getOrganizationId());
    }
}
