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
import com.openkoda.model.component.Scheduler;
import com.openkoda.service.export.dto.SchedulerConversionDto;
import org.springframework.stereotype.Component;

import static com.openkoda.service.export.FolderPathConstants.EXPORT_CONFIG_PATH_;
import static com.openkoda.service.export.FolderPathConstants.SCHEDULER_;

@Component
public class SchedulerEntityToYamlConverter extends AbstractEntityToYamlConverter<Scheduler, SchedulerConversionDto> implements LoggingComponent {

    @Override
    public String getPathToContentFile(Scheduler entity) {
        return null;
    }

    @Override
    public String getContent(Scheduler entity) {
        return null;
    }

    @Override
    public String getPathToYamlComponentFile(Scheduler entity) {
        return getYamlDefaultFilePath(EXPORT_CONFIG_PATH_ + SCHEDULER_, entity.getEventData(), entity.getOrganizationId());
    }

    @Override
    public SchedulerConversionDto getConversionDto(Scheduler entity) {
        SchedulerConversionDto dto = new SchedulerConversionDto();
        dto.setCronExpression(entity.getCronExpression());
        dto.setEventData(entity.getEventData());
        dto.setOnMasterOnly(entity.isOnMasterOnly());
        dto.setModule(entity.getModuleName());
        dto.setOrganizationId(entity.getOrganizationId());
        return dto;
    }
}
