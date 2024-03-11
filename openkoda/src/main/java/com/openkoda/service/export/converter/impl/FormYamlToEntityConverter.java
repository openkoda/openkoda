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

import com.openkoda.controller.ComponentProvider;
import com.openkoda.model.component.Form;
import com.openkoda.service.export.converter.YamlToEntityConverter;
import com.openkoda.service.export.converter.YamlToEntityParentConverter;
import com.openkoda.service.export.dto.FormConversionDto;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import java.util.Map;

import static com.openkoda.model.Privilege.valueOf;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Component
@YamlToEntityParentConverter(dtoClass = FormConversionDto.class)
public class FormYamlToEntityConverter extends ComponentProvider implements YamlToEntityConverter<Form, FormConversionDto> {

    @Override
    public Form convertAndSave(FormConversionDto dto, String filePath) {
        debug("[convertAndSave]");
        Form form = getForm(dto, loadResourceAsString(dto.getCode()));
        repositories.unsecure.entityUnrelatedQueries.createTableIfNotExists(form.getTableName());
        return repositories.secure.form.saveOne(form);
    }

    @Override
    public Form convertAndSave(FormConversionDto dto, String filePath, Map<String, String> resources) {
        debug("[convertAndSave]");
        Form form = getForm(dto, resources.get(dto.getCode()));
        repositories.unsecure.entityUnrelatedQueries.createTableIfNotExists(form.getTableName());
        form = repositories.secure.form.saveOne(form);
        return form;
    }

    @NotNull
    private Form getForm(FormConversionDto dto, String code) {
        Form form = new Form();
        form.setName(dto.getName());
        form.setReadPrivilege(isNotBlank(dto.getReadPrivilege()) ? valueOf(dto.getReadPrivilege()) : null);
        form.setWritePrivilege(isNotBlank(dto.getWritePrivilege()) ? valueOf(dto.getWritePrivilege()) : null);
        form.setRegisterApiCrudController(dto.isRegisterApiCrudController());
        form.setRegisterHtmlCrudController(dto.isRegisterHtmlCrudController());
        form.setTableColumns(dto.getTableColumns());
        form.setModuleName(dto.getModule());
        form.setTableName(dto.getTableName());
        form.setOrganizationId(dto.getOrganizationId());
        form.setCode(code);
        return form;
    }
}
