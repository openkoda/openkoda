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
import com.openkoda.core.helper.PrivilegeHelper;
import com.openkoda.model.DynamicPrivilege;
import com.openkoda.model.PrivilegeBase;
import com.openkoda.model.PrivilegeGroup;
import com.openkoda.service.export.converter.YamlToEntityConverter;
import com.openkoda.service.export.converter.YamlToEntityParentConverter;
import com.openkoda.service.export.dto.PrivilegeConversionDto;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@YamlToEntityParentConverter(dtoClass = PrivilegeConversionDto.class)
public class PrivilegeYamlToEntityConverter extends ComponentProvider implements YamlToEntityConverter<PrivilegeBase, PrivilegeConversionDto> {

    @Override
    public PrivilegeBase convertAndSave(PrivilegeConversionDto dto, String filePath) {
        debug("[convertAndSave]");
        DynamicPrivilege form = getPrivilege(dto);
        return services.privilege.createOrUpdateDynamicPrivilege(form);
    }

    @Override
    public PrivilegeBase convertAndSave(PrivilegeConversionDto dto, String filePath, Map<String, String> resources) {
        debug("[convertAndSave]");
        DynamicPrivilege form = getPrivilege(dto);
        return services.privilege.createOrUpdateDynamicPrivilege(form);
    }

    @NotNull
    private DynamicPrivilege getPrivilege(PrivilegeConversionDto dto) {
        PrivilegeBase form = PrivilegeHelper.getInstance().valueOfString(dto.getName());
        DynamicPrivilege dynamicPrivilege;
        if(form == null) {
            dynamicPrivilege = new DynamicPrivilege();
            dynamicPrivilege.setName(dto.getName());
        } else {
            dynamicPrivilege = (DynamicPrivilege)form;
        }
        
        dynamicPrivilege.setGroup(PrivilegeGroup.valueOf(dto.getGroup()));
        dynamicPrivilege.setCategory(dto.getCategory());
        dynamicPrivilege.setLabel(dto.getLabel());
        dynamicPrivilege.setIndexString(dto.getIndexString());
        // Dynamic priovilages are always removable
        dynamicPrivilege.setRemovable(true);
        return dynamicPrivilege;
    }
}
