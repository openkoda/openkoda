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

import com.openkoda.model.component.ControllerEndpoint;
import com.openkoda.model.component.FrontendResource;
import com.openkoda.repository.SecureFrontendResourceRepository;
import com.openkoda.service.export.dto.ControllerEndpointConversionDto;
import jakarta.inject.Inject;
import org.springframework.stereotype.Component;

import static com.openkoda.service.export.FolderPathConstants.*;

@Component
public class ControllerEndpointEntityToYamlConverter extends AbstractEntityToYamlConverter<ControllerEndpoint, ControllerEndpointConversionDto> {


    @Inject
    public SecureFrontendResourceRepository frontendResourceRepository;

    @Override
    public String getPathToContentFile(ControllerEndpoint entity) {
        FrontendResource frontendResource = frontendResourceRepository.findOne(entity.getFrontendResourceId());
        String orgPath = entity.getOrganizationId() == null ? "" : SUBDIR_ORGANIZATION_PREFIX + entity.getOrganizationId() + "/";
        String entityExportPath = UI_COMPONENT_ + frontendResource.getAccessLevel().getPath() + orgPath;
        return EXPORT_CODE_PATH_ + entityExportPath
                + String.format("%s-%s-%s.js", frontendResource.getName(), entity.getHttpMethod().name(), entity.getSubPath());
    }

    @Override
    public String getContent(ControllerEndpoint entity) {
        return entity.getCode();
    }

    @Override
    public String getPathToYamlComponentFile(ControllerEndpoint entity) {
        return null;
    }

    @Override
    public ControllerEndpointConversionDto getConversionDto(ControllerEndpoint entity) {
        ControllerEndpointConversionDto dto = new ControllerEndpointConversionDto();
        dto.setCode(getResourcePathToContentFile(entity));
        dto.setHttpHeaders(entity.getHttpHeaders());
        dto.setHttpMethod(entity.getHttpMethod().name());
        dto.setModelAttributes(entity.getModelAttributes());
        dto.setSubpath(entity.getSubPath());
        dto.setResponseType(entity.getResponseType().name());
        dto.setModule(entity.getModuleName());
        dto.setOrganizationId(entity.getOrganizationId());
        return dto;
    }
}
