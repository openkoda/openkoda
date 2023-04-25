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

package com.openkoda.core.form;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.openkoda.core.helper.PrivilegeHelper;
import com.openkoda.form.TemplateFormFieldNames;
import com.openkoda.model.common.LongIdEntity;
import org.springframework.validation.BindingResult;
import reactor.util.function.Tuples;

/**
 * This class is an extension to {@link AbstractForm}
 * It assigns the entity object to the form and provides methods for entity handling
 *
 * @author Arkadiusz Drysch (adrysch@stratoflow.com)
 * 
 */
public abstract class AbstractEntityForm<D, E extends LongIdEntity> extends AbstractForm<D> implements TemplateFormFieldNames {

    /**
     * Entity object assigned to the form
     */
    final public E entity;

    /**
     * ID of the entity object assigned to the form
     */
    public final Long id;

    @JsonIgnore
    private BindingResult bindingResult;

    public AbstractEntityForm(FrontendMappingDefinition frontendMappingDefinition) {
        this( null, null, frontendMappingDefinition);
    }

    public AbstractEntityForm(D dto, E entity, FrontendMappingDefinition frontendMappingDefinition) {
        super(dto, frontendMappingDefinition);
        this.entity = entity;
        if (entity == null) {
            id = null;
        } else {
            id = entity.getId();
            populateFrom(entity);
        }

    }

    /**
     *
     * Populates read/write privileges for form fields and form field values to entity properties
     *
     * @param entity Entity assigned to the form
     * @return {@link LongIdEntity}
     */
    final public E populateToEntity(E entity) {
        prepareFieldsReadWritePrivileges(entity);
        return populateTo(entity);
    }

    @Override
    public final void process() {
        prepareFieldsReadWritePrivileges(entity);
    }

    public final Long getId() {
        return id;
    }

    @Deprecated
    public E recoverEntity(E entity) {
//        this.entity = entity;
        return entity;
    }

    /**
     * Populates the readWriteForField map with read and write privileges for every field in form definition
     *
     * @param entity Entity assigned to the form
     */
    public void prepareFieldsReadWritePrivileges(E entity) {
        for (FrontendMappingFieldDefinition f : frontendMappingDefinition.fields) {
            readWriteForField.put(f,
                Tuples.of(
                    PrivilegeHelper.getInstance().canReadField(f, entity),
                    PrivilegeHelper.getInstance().canWriteField(f, entity)));
        }
    }


    /**
     * Should populate form fields with entity values
     *
     * @param entity Entity assigned to the form
     * @return {@link AbstractEntityForm}
     */
    abstract protected <F extends AbstractEntityForm<D, E>> F populateFrom(E entity);

    /**
     * Should populate entity properties with form fields values
     *
     * @param entity Entity assigned to the form
     * @return {@link LongIdEntity}
     */
    abstract protected E populateTo(E entity);

    public BindingResult getBindingResult() {
        return bindingResult;
//        return null;
    }

    public void setBindingResult(BindingResult bindingResult) {
        this.bindingResult = bindingResult;
    }
}
