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
import com.openkoda.model.ControllerEndpoint;
import com.openkoda.model.FrontendResource;
import com.openkoda.service.export.converter.EntityToYamlConverter;
import com.openkoda.service.export.dto.ControllerEndpointConversionDto;
import com.openkoda.service.export.util.ZipUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.zip.ZipOutputStream;

import static com.openkoda.service.export.FolderPathConstants.*;

@Component
public class ControllerEndpointEntityToYamlConverter extends ComponentProvider implements EntityToYamlConverter<ControllerEndpoint, ControllerEndpointConversionDto> {

    @Autowired
    ZipUtils zipUtils;

    @Override
    public ControllerEndpointConversionDto exportToYamlAndAddToZip(ControllerEndpoint entity, ZipOutputStream zipOut) {
        debug("[exportToYamlAndAddToZip]");

        FrontendResource frontendResource = repositories.secure.frontendResource.findOne(entity.getFrontendResourceId());

        String orgPath = entity.getOrganizationId() == null ? "" : SUBDIR_ORGANIZATION_PREFIX + entity.getOrganizationId() + "/";

        String entityExportPath = UI_COMPONENT_ + entity.getFrontendResource().getAccessLevel().getPath() + orgPath;
        String codeFilePath = EXPORT_CODE_PATH_ + entityExportPath
                + String.format("%s-%s-%s.js", frontendResource.getName(), entity.getHttpMethod().name(), entity.getSubPath());

        zipUtils.addToZipFile(entity.getCode(), codeFilePath, zipOut);


        ControllerEndpointConversionDto dto = new ControllerEndpointConversionDto();
        dto.setCode(codeFilePath);
        dto.setHttpHeaders(entity.getHttpHeaders());
        dto.setHttpMethod(entity.getHttpMethod().name());
        dto.setModelAttributes(entity.getModelAttributes());
        dto.setSubpath(entity.getSubPath());
        dto.setResponseType(entity.getResponseType().name());

        return dto;
    }

}
