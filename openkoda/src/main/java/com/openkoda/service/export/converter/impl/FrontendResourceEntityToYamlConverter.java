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
import com.openkoda.model.FrontendResource;
import com.openkoda.service.export.converter.EntityToYamlConverter;
import com.openkoda.service.export.dto.ControllerEndpointConversionDto;
import com.openkoda.service.export.dto.FrontendResourceConversionDto;
import com.openkoda.service.export.util.ZipUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;
import java.util.zip.ZipOutputStream;

import static com.openkoda.service.export.FolderPathConstants.*;

@Component
public class FrontendResourceEntityToYamlConverter extends ComponentProvider implements EntityToYamlConverter<FrontendResource, FrontendResourceConversionDto> {

    private ZipUtils zipUtils;

    private ControllerEndpointEntityToYamlConverter controllerEndpointEntityToYamlConverter;

    @Autowired
    public FrontendResourceEntityToYamlConverter(ControllerEndpointEntityToYamlConverter controllerEndpointEntityToYamlConverter, ZipUtils zipUtils) {
        this.controllerEndpointEntityToYamlConverter = controllerEndpointEntityToYamlConverter;
        this.zipUtils = zipUtils;
    }

    @Override
    public FrontendResourceConversionDto exportToYamlAndAddToZip(FrontendResource entity, ZipOutputStream zipOut) {
        debug("[exportToYamlAndAddToZip]");

        String orgPath = entity.getOrganizationId() == null ? "" : SUBDIR_ORGANIZATION_PREFIX + entity.getOrganizationId() + "/";
        String entityExportPath = (entity.getResourceType().equals(FrontendResource.ResourceType.UI_COMPONENT) ? UI_COMPONENT_ : FRONTEND_RESOURCE_) + entity.getAccessLevel().getPath() + orgPath;
        String contentFilePath = EXPORT_RESOURCES_PATH_ + entityExportPath + entity.getName() + entity.getType().getExtension();
        String resourceFilePath = EXPORT_CONFIG_PATH_ + entityExportPath + entity.getName() + ".yaml";

        zipUtils.addToZipFile(entity.getContent(), contentFilePath, zipOut);

        FrontendResourceConversionDto dto = populateDto(entity, contentFilePath);

        List<ControllerEndpointConversionDto> controllerEndpointDtos = repositories.unsecure.controllerEndpoint.findByFrontendResourceId(entity.getId()).stream()
                .map(controllerEndpoint -> controllerEndpointEntityToYamlConverter.exportToYamlAndAddToZip(controllerEndpoint, zipOut))
                .collect(Collectors.toList());

        dto.setControllerEndpoints(controllerEndpointDtos);


        zipUtils.addToZipFile(dtoToYamlString(dto), resourceFilePath, zipOut);

        return dto;
    }

    private FrontendResourceConversionDto populateDto(FrontendResource entity, String contentFilePath) {
        FrontendResourceConversionDto frontendResourceConversionDto = new FrontendResourceConversionDto();
        frontendResourceConversionDto.setContent(contentFilePath);
        frontendResourceConversionDto.setIncludeInSitemap(entity.getIncludeInSitemap());
        frontendResourceConversionDto.setName(entity.getName());
        frontendResourceConversionDto.setAccessLevel(entity.getAccessLevel());
        frontendResourceConversionDto.setRequiredPrivilege(entity.getRequiredPrivilege());
        frontendResourceConversionDto.setType(entity.getType().name());
        frontendResourceConversionDto.setResourceType(entity.getResourceType().name());

        return frontendResourceConversionDto;
    }

}
