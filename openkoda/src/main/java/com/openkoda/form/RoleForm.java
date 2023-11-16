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
import com.openkoda.core.helper.PrivilegeHelper;
import com.openkoda.dto.user.RoleDto;
import com.openkoda.model.Role;
import org.springframework.validation.BindingResult;

import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

import static org.apache.commons.lang3.StringUtils.isBlank;

/**
 * @author Martyna Litkowska (mlitkowska@stratoflow.com)
 * @since 2019-01-25
 */
public class RoleForm extends AbstractEntityForm<RoleDto, Role> implements TemplateFormFieldNames {

    /**
     * <p>Constructor for AbstractEntityForm.</p>
     *
     * @param entity         a E dto.
     * @param frontendMappingDefinition a {@link FrontendMappingDefinition} dto.
     */
    public RoleForm(RoleDto dto, Role entity, FrontendMappingDefinition frontendMappingDefinition) {
        super(dto, entity, frontendMappingDefinition);
    }

    public RoleForm(Role entity) {
        super(new RoleDto(), entity, FrontendMappingDefinitions.roleForm);
    }

    public RoleForm() {
        super(null, null, FrontendMappingDefinitions.roleForm);
    }

    @Override
    public RoleForm populateFrom(Role entity) {
        dto.name = entity.getName();
        dto.type = entity.getType();
        dto.privileges = entity.getPrivilegesSet().stream().map(Enum::name).collect(Collectors.toList());
        return this;
    }

    @Override
    protected Role populateTo(Role entity) {

        entity.setName(getSafeValue(entity.getName(), NAME_));
        entity.setPrivilegesSet(getSafeValue(entity.getPrivilegesSet(), PRIVILEGES_,
                ((List<String> p) -> (p != null ?
                        p.stream().map(PrivilegeHelper::valueOfString).collect(Collectors.toSet()) : new HashSet<>()))));

        return entity;
    }

    @Override
    public RoleForm validate(BindingResult br) {
        if (isBlank(dto.name)) {
            br.rejectValue("dto.name", "not.empty");
        }
        if (isBlank(dto.type)) {
            br.rejectValue("dto.type", "not.empty");
        }
        return this;
    }

}
