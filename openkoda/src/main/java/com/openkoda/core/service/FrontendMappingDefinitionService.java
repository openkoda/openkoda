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

package com.openkoda.core.service;

import com.openkoda.core.form.*;
import com.openkoda.core.helper.PrivilegeHelper;
import com.openkoda.core.multitenancy.TenantResolver;
import com.openkoda.model.Privilege;
import com.openkoda.model.PrivilegeBase;
import com.openkoda.model.common.SearchableEntity;
import com.openkoda.model.common.SearchableOrganizationRelatedEntity;
import com.openkoda.repository.SearchableRepositories;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.function.Function;

import static com.openkoda.core.helper.PrivilegeHelper.valueOfString;

@Service
public class FrontendMappingDefinitionService {

    public FrontendMappingDefinition createFrontendMappingDefinition(
            String formName,
            Function<FormFieldDefinitionBuilderStart, FormFieldDefinitionBuilder> builder) {
        return FrontendMappingDefinition.createFrontendMappingDefinition(formName, Privilege.readOrgData, Privilege.readOrgData, builder);
    }

    public FrontendMappingDefinition createFrontendMappingDefinition(
            String formName,
            PrivilegeBase defaultReadPrivilege,
            PrivilegeBase defaultWritePrivilege,
            Function<FormFieldDefinitionBuilderStart, FormFieldDefinitionBuilder> builder) {
        return FrontendMappingDefinition.createFrontendMappingDefinition(formName, defaultReadPrivilege, defaultWritePrivilege, builder);
    }
    public FrontendMappingDefinition createFrontendMappingDefinition(
            String formName,
            String defaultReadPrivilege,
            String defaultWritePrivilege,
            Function<FormFieldDefinitionBuilderStart, FormFieldDefinitionBuilder> builder) {
        return FrontendMappingDefinition.createFrontendMappingDefinition(formName, (PrivilegeBase) PrivilegeHelper.valueOfString(defaultReadPrivilege), (PrivilegeBase) valueOfString(defaultWritePrivilege), builder);
    }

    public FrontendMappingDefinition createFrontendMappingDefinition(
            String formName,
            PrivilegeBase defaultReadPrivilege,
            PrivilegeBase defaultWritePrivilege,
            FrontendMappingFieldDefinition[] baseFormFields,
            Function<FormFieldDefinitionBuilderStart, FormFieldDefinitionBuilder> builder) {
        return FrontendMappingDefinition.createFrontendMappingDefinition(formName, defaultReadPrivilege, defaultWritePrivilege, baseFormFields, builder);
    }

    public ReflectionBasedEntityForm getForm(String formName,
                                             Function<FormFieldDefinitionBuilderStart, FormFieldDefinitionBuilder> builder) {
        return new ReflectionBasedEntityForm(createFrontendMappingDefinition(formName, builder), null, null);
    }

    public ReflectionBasedEntityForm getForm(String formName, Long organizationId,
                                             Function<FormFieldDefinitionBuilderStart, FormFieldDefinitionBuilder> builder) {
        return new ReflectionBasedEntityForm(createFrontendMappingDefinition(formName, builder), organizationId, null);
    }

    public ReflectionBasedEntityForm getForm(String formName, String privilegeName,
                                             Function<FormFieldDefinitionBuilderStart, FormFieldDefinitionBuilder> builder) {
        PrivilegeBase requiredPrivilege = (PrivilegeBase) valueOfString(privilegeName);
        return new ReflectionBasedEntityForm(createFrontendMappingDefinition(formName, requiredPrivilege, requiredPrivilege, builder), null, null);
    }

    public ReflectionBasedEntityForm getForm(String formName, Long organizationId, String privilegeName,
                                             Function<FormFieldDefinitionBuilderStart, FormFieldDefinitionBuilder> builder) {
        PrivilegeBase requiredPrivilege = (PrivilegeBase) valueOfString(privilegeName);
        return new ReflectionBasedEntityForm(createFrontendMappingDefinition(formName, requiredPrivilege, requiredPrivilege, builder), organizationId, null);
    }

    public ReflectionBasedEntityForm getEntityForm(String entityKey) {
        PrivilegeBase requiredPrivilege = Privilege.readOrgData;
        return new ReflectionBasedEntityForm(createFrontendMappingDefinition(entityKey + "Form",
                requiredPrivilege, requiredPrivilege, getBuilderForEntity(entityKey)), TenantResolver.getTenantedResource().organizationId, null);
    }

    private Function<FormFieldDefinitionBuilderStart, FormFieldDefinitionBuilder> getBuilderForEntity(String entityKey) {
        Class<SearchableOrganizationRelatedEntity> entityClass = (Class<SearchableOrganizationRelatedEntity>) SearchableRepositories.getSearchableRepositoryMetadata(entityKey).entityClass();
        Function<FormFieldDefinitionBuilderStart, FormFieldDefinitionBuilder> result =
            a -> {
                FormFieldDefinitionBuilder s = a.hidden("id");
                for (Field f : entityClass.getDeclaredFields()) {
                    Class type = f.getType();
                    String name = f.getName();
                    if (f.isEnumConstant()) {
                        s = s.datalist(name,
                                d -> d.enumDictionary((Enum[])type.getEnumConstants()))
                                .dropdown(name, name);
                    } else if(SearchableEntity.class.isAssignableFrom(type)) {
                        s.datalist(name, d -> d.dictionary(type))
                        .dropdownWithDisable(name + "Id", name);
                    } else if(name.endsWith("Id")) {
                        continue;
                    } else if(Boolean.class.equals(type)) {
                        s.checkbox(name);
                    } else if(LocalDate.class.equals(type)) {
                        s.date(name);
                    } else if(LocalDateTime.class.equals(type)) {
                        s.datetime(name);
                    } else if(type == int.class || type == double.class || type == long.class || type == float.class || Number.class.isAssignableFrom(type)) {
                        s.number(name);
                    } else {
                        s = s.text(f.getName());
                    }
                }
                return s;
            };
        return result;
    }


}
