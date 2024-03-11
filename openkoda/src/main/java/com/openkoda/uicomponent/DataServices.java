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

package com.openkoda.uicomponent;

import com.openkoda.core.form.AbstractOrganizationRelatedEntityForm;
import com.openkoda.core.repository.common.ScopedSecureRepository;
import com.openkoda.model.User;
import com.openkoda.model.common.SearchableOrganizationRelatedEntity;
import com.openkoda.uicomponent.annotation.Autocomplete;

public interface DataServices {
    @Autocomplete(doc="Get data repository for an entity using its key value")
    ScopedSecureRepository<?> getRepository(String entityName);
    @Autocomplete(doc="Get data repository for an entity using its key value")
    ScopedSecureRepository<?> getRepository(String entityKey, String securityScope);
    @Autocomplete(doc="Save form data as a new entity record")
    SearchableOrganizationRelatedEntity saveForm(AbstractOrganizationRelatedEntityForm form);
    @Autocomplete(doc="Update an entity with form data")
    SearchableOrganizationRelatedEntity saveForm(AbstractOrganizationRelatedEntityForm form, SearchableOrganizationRelatedEntity entity);
    @Autocomplete(doc="Retrieve a form by its identifier (key)")
    AbstractOrganizationRelatedEntityForm getForm(String frontendMappingName);
    @Autocomplete(doc="Retrieve a form associated with a provided entity object")
    AbstractOrganizationRelatedEntityForm getForm(String frontendMappingName, SearchableOrganizationRelatedEntity entity);
    @Autocomplete(doc="Register a new user or return an existing user's data")
    User registerUserOrReturnExisting(String email, String firstName, String lastName);
}
