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

package com.openkoda.core.form;


import com.openkoda.core.helper.PrivilegeHelper;
import com.openkoda.core.security.HasSecurityRules;
import com.openkoda.dto.OrganizationRelatedObject;
import com.openkoda.model.common.OrganizationRelatedEntity;
import reactor.util.function.Tuples;

/**
 * This class is an extension to {@link AbstractEntityForm}
 * It assigns the {@link OrganizationRelatedEntity} to the form
 */
public abstract class AbstractOrganizationRelatedEntityForm<C extends OrganizationRelatedObject, E extends OrganizationRelatedEntity>
        extends AbstractEntityForm<C, E> implements OrganizationRelatedObject, HasSecurityRules {

    /**
     * ID of the {@link OrganizationRelatedEntity} assigned to this form
     */
    private Long organizationId;

    public AbstractOrganizationRelatedEntityForm() {
        super(null);
    }
    public AbstractOrganizationRelatedEntityForm(FrontendMappingDefinition frontendMappingDefinition) {
        super(frontendMappingDefinition);
    }

    public AbstractOrganizationRelatedEntityForm(Long organizationId, C dto, E entity, FrontendMappingDefinition frontendMappingDefinition) {
        super(dto, entity, frontendMappingDefinition);
        this.organizationId = organizationId;
        assertFormConsistency(this);
    }

    @Override
    public Long getOrganizationId() {
        return organizationId;
    }

    @Override
    final public void prepareFieldsReadWritePrivileges(E entity) {
        for (FrontendMappingFieldDefinition f : frontendMappingDefinition.fields) {
            readWriteForField.put(f,
                    Tuples.of(
                            PrivilegeHelper.getInstance().canReadFieldInOrganization(f, entity, organizationId),
                            PrivilegeHelper.getInstance().canWriteFieldInOrganization(f, entity, organizationId)));
        }
    }
}
