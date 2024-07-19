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
import com.openkoda.model.DynamicPrivilege;
import com.openkoda.service.export.dto.PrivilegeConversionDto;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.zip.ZipOutputStream;

import static com.openkoda.service.export.FolderPathConstants.*;

@Component
public class PrivilegeEntityToYamlConverter extends AbstractEntityToYamlConverter<DynamicPrivilege, PrivilegeConversionDto> implements LoggingComponent   {

    public String getPathToContentFile(DynamicPrivilege entity){
        return EXPORT_CODE_PATH_ + FORM_ + String.format("%s.yaml", entity.getName());
    }

    @Override
    public String getContent(DynamicPrivilege entity) {
        return null;
    }

    @Override
    public void getUpgradeScript(DynamicPrivilege entity, List<String> dbUpgradeEntries) {
        if(dbUpgradeEntries != null) {
            dbUpgradeEntries.add("INSERT INTO public.dynamic_privilege (id,category,privilege_group,index_string,\"label\",\"name\",removable,updated_on) VALUES\n"
            + String.format("(nextval('seq_global_id'),'%s', '%s' ,'%s','%s','%s',true,now());"
            , entity.getCategory(), entity.getGroup().name(), entity.getIndexString(), entity.getLabel(), entity.getName()));
        }
    }
    
    public PrivilegeConversionDto getConversionDto(DynamicPrivilege entity){
        PrivilegeConversionDto dto = new PrivilegeConversionDto();
        dto.setName(entity.getName());
        dto.setCategory(entity.getCategory());
        dto.setGroup(entity.getGroup().name());
        dto.setLabel(entity.getLabel());
        return dto;
    }

    public String getPathToYamlComponentFile(DynamicPrivilege entity){
        return getYamlDefaultFilePath(EXPORT_PRIVILEGE_PATH_, entity.getName(), null);
    }
    
    @Override
    public PrivilegeConversionDto addToZip(DynamicPrivilege entity, ZipOutputStream zipOut, Set<String> zipEntries) {
        return super.addToZip(entity, zipOut, zipEntries);
    }
}
