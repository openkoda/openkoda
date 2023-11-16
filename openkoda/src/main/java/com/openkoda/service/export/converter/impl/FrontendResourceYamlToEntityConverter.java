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
import com.openkoda.service.export.converter.YamlToEntityConverter;
import com.openkoda.service.export.converter.YamlToEntityParentConverter;
import com.openkoda.service.export.dto.ControllerEndpointConversionDto;
import com.openkoda.service.export.dto.FrontendResourceConversionDto;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.openkoda.service.export.FolderPathConstants.*;

@Component
@YamlToEntityParentConverter(dtoClass = FrontendResourceConversionDto.class)
public class FrontendResourceYamlToEntityConverter extends ComponentProvider implements YamlToEntityConverter<FrontendResource, FrontendResourceConversionDto> {

    @Autowired
    ControllerEndpointYamlToEntityConverter controllerEndpointYamlToEntityConverter;

    @Override
    public FrontendResource convertAndSave(FrontendResourceConversionDto dto, String filePath) {
        debug("[convertAndSave]");

//        get access level and orgId from file path
        String accessLevel = filePath.contains(UI_COMPONENT_) ? StringUtils.substringBetween(filePath, UI_COMPONENT_, "/")
                : StringUtils.substringBetween(filePath, FRONTEND_RESOURCE_, "/");
        if(StringUtils.isNotEmpty(accessLevel)) {
            dto.setAccessLevel(FrontendResource.AccessLevel.valueOf(accessLevel.toUpperCase()));
        }

        String orgIdString = StringUtils.substringBetween(filePath, SUBDIR_ORGANIZATION_PREFIX, "/");
        if(StringUtils.isNotEmpty(orgIdString)) {
            dto.setOrganizationId(Long.parseLong(orgIdString));
        }

        FrontendResource frontendResource = convertToFrontendResource(dto);
        repositories.secure.frontendResource.saveOne(frontendResource);
        if(dto.getControllerEndpoints() != null){
            convertControllerEndpoints(dto.getControllerEndpoints(), frontendResource.getId());
        }

        return frontendResource;
    }

    private void convertControllerEndpoints(List<ControllerEndpointConversionDto> controllerEndpointConversionDtos, Long frontendResourceId) {
        for (ControllerEndpointConversionDto controllerEndpointConversionDto : controllerEndpointConversionDtos) {
            controllerEndpointConversionDto.setFrontendResourceId(frontendResourceId);
            controllerEndpointYamlToEntityConverter.convertAndSave(controllerEndpointConversionDto, null);
        }
    }

    private FrontendResource convertToFrontendResource(FrontendResourceConversionDto dto){
        FrontendResource frontendResource = new FrontendResource();
        frontendResource.setName(dto.getName());
        frontendResource.setIncludeInSitemap(dto.getIncludeInSitemap());
        frontendResource.setAccessLevel(dto.getAccessLevel());
        frontendResource.setRequiredPrivilege(dto.getRequiredPrivilege());
        frontendResource.setType(dto.getType());
        frontendResource.setResourceType(dto.getResourceType());
        frontendResource.setContent(loadResourceAsString(dto.getContent()));
        frontendResource.setOrganizationId(dto.getOrganizationId());
        return frontendResource;
    }
}
