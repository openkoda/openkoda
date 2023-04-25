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

import com.openkoda.core.form.AbstractEntityForm;
import com.openkoda.dto.user.EditUserDto;
import com.openkoda.model.User;
import org.springframework.validation.BindingResult;

/**
 * @author Martyna Litkowska (mlitkowska@stratoflow.com)
 * @since 2019-01-23
 */
public class EditUserForm extends AbstractEntityForm<EditUserDto, User> {


    public EditUserForm() {
        super(FrontendMappingDefinitions.editUserForm);
    }

    public EditUserForm(User entity) {
        super(new EditUserDto(), entity, FrontendMappingDefinitions.editUserForm);
    }

    @Override
    public EditUserForm validate(BindingResult br) {
        BasicUserForm.validate(dto, br);
        return this;
    }

    @Override
    public EditUserForm populateFrom(User entity) {
        BasicUserForm.populateFromEntity(dto, entity);
        dto.setEnabled(entity.isEnabled());
        dto.setLanguage(entity.getLanguage());
        dto.setGlobalRoleName(entity.getGlobalRoleName());
        return this;
    }

    /** {@inheritDoc} */
    @Override
    protected User populateTo(User entity) {
        entity.setFirstName( getSafeValue( entity.getFirstName(), FIRST_NAME_, emptyIfBlank) );
        entity.setLastName( getSafeValue( entity.getLastName(), LAST_NAME_, emptyIfBlank) );
        entity.setEmail( getSafeValue( entity.getEmail(), EMAIL_) );
        entity.setEnabled( getSafeValue( entity.isEnabled(), ENABLED_) );
        entity.setLanguage( getSafeValue( entity.getLanguage(), LANGUAGE));
        return entity;
    }

}
