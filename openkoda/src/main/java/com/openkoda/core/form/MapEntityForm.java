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

import com.openkoda.model.MapEntity;
import org.springframework.validation.BindingResult;

/**
 *
 *
 * @author Arkadiusz Drysch (adrysch@stratoflow.com)
 *
 */
public class MapEntityForm extends AbstractOrganizationRelatedEntityForm<OrganizationRelatedMap, MapEntity> {

    public MapEntityForm(FrontendMappingDefinition frontendMappingDefinition) {
        super(frontendMappingDefinition);
    }

    public MapEntityForm(FrontendMappingDefinition frontendMappingDefinition, Long organizationId, MapEntity entity) {
        super(organizationId, new OrganizationRelatedMap(), entity, frontendMappingDefinition);
    }

    @Override
    public <F extends Form> F validate(BindingResult br) {
        return null;
    }

    @Override
    protected MapEntityForm populateFrom(MapEntity entity) {
        dto = entity.getValueAsMap();
        return this;
    }

    @Override
    protected MapEntity populateTo(MapEntity entity) {
        if (singleFieldToUpdate != null) {
            entity.getValueAsMap().put(singleFieldToUpdate, dto.get(singleFieldToUpdate));
            entity.updateValueFromMap();
        } else {
            entity.setValueAsMap(dto);
        }
        return entity;
    }

    @Override
    public boolean isMapDto() {
        return true;
    }

}