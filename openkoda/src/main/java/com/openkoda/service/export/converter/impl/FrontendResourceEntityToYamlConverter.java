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
import com.openkoda.repository.ControllerEndpointRepository;
import com.openkoda.service.export.dto.ControllerEndpointConversionDto;
import com.openkoda.service.export.dto.FrontendResourceConversionDto;
import jakarta.inject.Inject;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;
import java.util.zip.ZipOutputStream;

import static com.openkoda.service.export.FolderPathConstants.*;

@Component
public class FrontendResourceEntityToYamlConverter extends AbstractEntityToYamlConverter<FrontendResource, FrontendResourceConversionDto>{



    @Inject
    private ControllerEndpointEntityToYamlConverter controllerEndpointEntityToYamlConverter;
    @Inject
    public ControllerEndpointRepository controllerEndpointRepository;


    @Override
    public FrontendResourceConversionDto addToZip(FrontendResource entity, ZipOutputStream zipOut){
         List<ControllerEndpoint> controllerEndpoints = controllerEndpointRepository.findByFrontendResourceId(entity.getId());
         for(ControllerEndpoint ce : controllerEndpoints){
             controllerEndpointEntityToYamlConverter.addToZip(ce, zipOut);
         }
         return super.addToZip(entity, zipOut);

    }
    @Override
    public String getPathToContentFile(FrontendResource entity) {
        return EXPORT_RESOURCES_PATH_ + getExportPath(entity) + entity.getName() + entity.getType().getExtension();
    }

    @Override
    public String getContent(FrontendResource entity) {
        return entity.getContent();
    }

    @Override
    public String getPathToYamlComponentFile(FrontendResource entity) {
        return EXPORT_CONFIG_PATH_ + getExportPath(entity) + entity.getName() + ".yaml";
    }

    @Override
    public FrontendResourceConversionDto getConversionDto(FrontendResource entity) {
        FrontendResourceConversionDto dto = populateDto(entity);
        List<ControllerEndpointConversionDto> controllerEndpointDtos = controllerEndpointRepository.findByFrontendResourceId(entity.getId()).stream()
                .map(controllerEndpoint -> controllerEndpointEntityToYamlConverter.getConversionDto(controllerEndpoint))
                .collect(Collectors.toList());
        dto.setControllerEndpoints(controllerEndpointDtos);
        return dto;
    }

    private String getExportPath(FrontendResource entity){
        String orgPath = entity.getOrganizationId() == null ? "" : SUBDIR_ORGANIZATION_PREFIX + entity.getOrganizationId() + "/";
        return (entity.getResourceType().equals(FrontendResource.ResourceType.UI_COMPONENT) ? UI_COMPONENT_ : FRONTEND_RESOURCE_) + entity.getAccessLevel().getPath() + orgPath;
    }

    private FrontendResourceConversionDto populateDto(FrontendResource entity) {
        FrontendResourceConversionDto dto = new FrontendResourceConversionDto();
        dto.setContent(getResourcePathToContentFile(entity));
        dto.setIncludeInSitemap(entity.getIncludeInSitemap());
        dto.setName(entity.getName());
        dto.setAccessLevel(entity.getAccessLevel());
        dto.setRequiredPrivilege(entity.getRequiredPrivilege());
        dto.setType(entity.getType().name());
        dto.setResourceType(entity.getResourceType().name());
        dto.setModule(entity.getModuleName());
        dto.setOrganizationId(entity.getOrganizationId());
        dto.setEmbeddable(entity.isEmbeddable());
        return dto;
    }
}
