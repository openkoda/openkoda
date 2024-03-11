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
import com.openkoda.model.component.ServerJs;
import com.openkoda.service.export.dto.ServerJsConversionDto;
import org.springframework.stereotype.Component;

import static com.openkoda.service.export.FolderPathConstants.*;

@Component
public class ServerJsEntityToYamlConverter extends AbstractEntityToYamlConverter<ServerJs, ServerJsConversionDto> implements LoggingComponent {

    @Override
    public String getPathToContentFile(ServerJs entity) {
        String orgPath = entity.getOrganizationId() == null ? "" : SUBDIR_ORGANIZATION_PREFIX + entity.getOrganizationId() + "/";
        String entityExportPath = SERVER_SIDE_ + orgPath;
        return EXPORT_CODE_PATH_ + entityExportPath + String.format("%s.js", entity.getName());
    }

    @Override
    public String getContent(ServerJs entity) {
        return entity.getCode();
    }

    @Override
    public String getPathToYamlComponentFile(ServerJs entity) {
        return getYamlDefaultFilePath(EXPORT_CONFIG_PATH_ + SERVER_SIDE_, entity.getName(), entity.getOrganizationId());
    }

    @Override
    public ServerJsConversionDto getConversionDto(ServerJs entity) {
        ServerJsConversionDto dto = new ServerJsConversionDto();
        dto.setArguments(entity.getArguments());
        dto.setModel(entity.getModel());
        dto.setName(entity.getName());
        dto.setCode(getResourcePathToContentFile(entity));
        dto.setModule(entity.getModuleName());
        dto.setOrganizationId(entity.getOrganizationId());
        return dto;
    }
}
