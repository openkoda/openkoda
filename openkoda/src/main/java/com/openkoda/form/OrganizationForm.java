/*
MIT License

Copyright (c) 2016-2022, Codedose CDX Sp. z o.o. Sp. K. <stratoflow.com>

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

import com.openkoda.core.form.AbstractOrganizationRelatedEntityForm;
import com.openkoda.core.multitenancy.MultitenancyService;
import com.openkoda.dto.OrganizationDto;
import com.openkoda.model.Organization;
import org.apache.commons.lang3.StringUtils;
import org.springframework.validation.BindingResult;

/**
 * @author Arkadiusz Drysch (adrysch@stratoflow.com)
 * @since 1/26/17
 *
 * @author Arkadiusz Drysch (adrysch@stratoflow.com)
 *
 */
public class OrganizationForm extends AbstractOrganizationRelatedEntityForm<OrganizationDto, Organization> {

    /**
     * <p>Constructor for OrganizationForm.</p>
     *
     * @param entity a {@link com.openkoda.model.Organization} dto.
     */
    public OrganizationForm(Long organizationId, Organization entity) {
        super(organizationId, new OrganizationDto(), entity, FrontendMappingDefinitions.organizationForm);
    }

    /**
     * <p>Constructor for OrganizationForm.</p>
     */
    public OrganizationForm() {
        super(FrontendMappingDefinitions.organizationForm);
    }

    /** {@inheritDoc} */
    @Override
    public OrganizationForm validate(BindingResult br) {
        if(StringUtils.isBlank(dto.name)) { br.rejectValue("dto.name", "not.empty", defaultErrorMessage); };
        return this;
    }

    /** {@inheritDoc} */
    @Override
    public OrganizationForm populateFrom(Organization entity) {
        dto.name = entity.getName();
        dto.id = entity.getId();
        dto.assignedDatasource = entity.getAssignedDatasource();
        return this;
    }

    /** {@inheritDoc} */
    @Override
    protected Organization populateTo(Organization entity) {
        entity.setName(getSafeValue(entity.getName(), NAME_));
        if(MultitenancyService.isMultitenancy()) {
            entity.setAssignedDatasource(getSafeValue(entity.getAssignedDatasource(), ASSIGNED_DATASOURCE_));
        }
        return entity;
    }

}
