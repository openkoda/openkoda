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
import com.openkoda.core.form.FrontendMappingDefinition;
import com.openkoda.dto.user.BasicUser;
import com.openkoda.model.User;
import org.springframework.validation.BindingResult;

import java.util.regex.Pattern;

import static com.openkoda.form.FrontendMappingDefinitions.userForm;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

/**
 * @author Arkadiusz Drysch (adrysch@stratoflow.com)
 * @since 1/26/17
 *
 * @author Arkadiusz Drysch (adrysch@stratoflow.com)
 *
 */
public class BasicUserForm extends AbstractEntityForm<BasicUser, User> {

    private Object dtoField;

    /**
     * <p>Constructor for BasicUserForm.</p>
     *
     * @param entity a {@link com.openkoda.model.User} dto.
     */
    public BasicUserForm(BasicUser dto, User entity) {
        super(dto, entity, userForm);
    }

    /**
     * <p>Constructor for BasicUserForm.</p>
     *
     * @param entity a {@link com.openkoda.model.User} dto.
     * @param form   a {@link FrontendMappingDefinition} dto.
     */
    public BasicUserForm(BasicUser dto, User entity, FrontendMappingDefinition form) {
        super(dto, entity, form);
    }

    /**
     * <p>Constructor for BasicUserForm.</p>
     */
    public BasicUserForm() {
        super(null, null, userForm);
    }

    protected static BasicUser validate(BasicUser dto, BindingResult br) {

        if (isNotBlank(dto.email) && !emailIsValid(dto.email)) {
            br.rejectValue("dto.email", "not.valid");
        }
        if (isBlank(dto.email) && dto.email!=null) {
            br.rejectValue("dto.email", "not.empty");
        }
        return dto;
    }

    protected static boolean emailIsValid(String email){
        final Pattern pattern = Pattern.compile("^([a-zA-Z0-9_\\-\\.\\+]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$");
        if(pattern.matcher(email).matches()){
            return true;
        }
        return false;
    }

    protected static BasicUser populateFromEntity(BasicUser dto, User entity) {
        dto.firstName = entity.getFirstName();
        dto.lastName = entity.getLastName();
        dto.email = entity.getEmail();
        return dto;
    }

    @Override
    public BasicUserForm populateFrom(User entity) {
        populateFromEntity(dto, entity);
        return this;
    }

    @Override
    protected User populateTo(User entity) {
        entity.setFirstName( getSafeValue( entity.getFirstName(), FIRST_NAME_) );
        entity.setFirstName( getSafeValue( entity.getLastName(), LAST_NAME_) );
        entity.setFirstName( getSafeValue( entity.getEmail(), EMAIL_) );
        return entity;
    }

    public BasicUserForm validate(BindingResult br) {
        validate(dto, br);
        return this;
    }
}