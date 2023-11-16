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
import com.openkoda.dto.user.InviteUserDto;
import com.openkoda.model.User;
import org.springframework.validation.BindingResult;

/**
 * @author Arkadiusz Drysch (adrysch@stratoflow.com)
 * @since 1/26/17
 *
 * @author Arkadiusz Drysch (adrysch@stratoflow.com)
 *
 */
public class InviteUserForm extends AbstractEntityForm<InviteUserDto, User> {

    private static final String FORM_NAME = "inviteUserForm";


    /** Constant <code>inviteUserFields</code> */

    /** Constant <code>inviteForm</code> */

    /**
     * <p>Constructor for InviteUserForm.</p>
     *
     * @param entity a {@link com.openkoda.model.User} dto.
     */
    public InviteUserForm(InviteUserDto dto, User entity) {
        super(dto, entity, FrontendMappingDefinitions.inviteForm);
    }

    /**
     * <p>Constructor for InviteUserForm.</p>
     */
    public InviteUserForm() {
        super(FrontendMappingDefinitions.inviteForm);
    }

    /** {@inheritDoc} */
    @Override
    public InviteUserForm validate(BindingResult br) {
        BasicUserForm.validate(dto, br);
        if (dto.roleName == null || dto.roleName.isEmpty()) {br.rejectValue("dto.roleName", "not.empty");}
        return this;
    }

    /** {@inheritDoc} */
    @Override
    public InviteUserForm populateFrom(User entity) {
        BasicUserForm.populateFromEntity(dto, entity);
        return this;
    }

    /** {@inheritDoc} */
    @Override
    protected User populateTo(User entity) {
        entity.setFirstName( getSafeValue( entity.getFirstName(), FIRST_NAME_));
        entity.setLastName( getSafeValue( entity.getLastName(), LAST_NAME_));
        entity.setEmail( getSafeValue( entity.getEmail(), EMAIL_));

        return entity;
    }

}
