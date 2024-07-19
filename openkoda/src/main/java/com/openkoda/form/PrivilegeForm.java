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

package com.openkoda.form;

import com.openkoda.core.form.AbstractEntityForm;
import com.openkoda.core.form.FrontendMappingDefinition;
import com.openkoda.dto.user.PrivilegeDto;
import com.openkoda.model.DynamicPrivilege;
import com.openkoda.model.PrivilegeBase;
import org.springframework.validation.BindingResult;

import static org.apache.commons.lang3.StringUtils.isBlank;

/**
 * @author mboronski
 * @since 2024-05-27
 */
public class PrivilegeForm extends AbstractEntityForm<PrivilegeDto, PrivilegeBase> implements TemplateFormFieldNames {

    /**
     * <p>Constructor for AbstractEntityForm.</p>
     *
     * @param entity         a E dto.
     * @param frontendMappingDefinition a {@link FrontendMappingDefinition} dto.
     */
    public PrivilegeForm(PrivilegeDto dto, PrivilegeBase entity, FrontendMappingDefinition frontendMappingDefinition) {
        super(dto, entity, frontendMappingDefinition);
    }

    public PrivilegeForm(PrivilegeBase entity) {
        super(new PrivilegeDto(), entity, FrontendMappingDefinitions.privilegeForm);
    }

    public PrivilegeForm() {
        super(null, null, FrontendMappingDefinitions.privilegeForm);
    }

    @Override
    public PrivilegeForm populateFrom(PrivilegeBase entity) {
        dto.setId(entity.getId());
        dto.setName(entity.name());
        dto.setLabel(entity.getLabel());
        dto.setCategory(entity.getCategory());
        dto.setPrivilegeGroup(entity.getGroup());
        return this;
    }

    @Override
    protected DynamicPrivilege populateTo(PrivilegeBase entity) {
        DynamicPrivilege dynamicEntiry = (DynamicPrivilege)entity;
        dynamicEntiry.setId(getSafeValue(entity.getId(), ID_));
        dynamicEntiry.setName(getSafeValue(entity.name(), NAME_));
        dynamicEntiry.setLabel(getSafeValue(entity.getLabel(), LABEL_));
        dynamicEntiry.setCategory(getSafeValue(entity.getCategory(), "category"));
        dynamicEntiry.setGroup(getSafeValue(entity.getGroup(), "group"));
        return dynamicEntiry;
    }

    @Override
    public PrivilegeForm validate(BindingResult br) {
        if (isBlank(dto.getName())) {
            br.rejectValue("dto.name", "not.empty");
        }
        
        if (isBlank(dto.getLabel())) {
            br.rejectValue("dto.label", "not.empty");
        }
        
        if (isBlank(dto.getCategory())) {
            br.rejectValue("dto.category", "not.empty");
        }
        
        if (dto.getPrivilegeGroup() == null) {
            br.rejectValue("dto.group", "not.empty");
        }
        return this;
    }

}
