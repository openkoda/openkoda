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

package com.openkoda.uicomponent.preview;

import com.openkoda.core.form.AbstractOrganizationRelatedEntityForm;
import com.openkoda.core.repository.common.SecuredRepository;
import com.openkoda.model.User;
import com.openkoda.model.common.SearchableOrganizationRelatedEntity;
import com.openkoda.uicomponent.DataServices;

import java.util.HashMap;
import java.util.Map;

public class PreviewDataServices implements DataServices {


    public final static Map<String, SecuredRepository<?>> repositories = new HashMap<>();

    static {
        repositories.put("user", new PreviewRepository<>(PreviewModel.user1, PreviewModel.user2));
        repositories.put("file", new PreviewRepository<>(PreviewModel.file1, PreviewModel.file2));
        repositories.put("organization", new PreviewRepository<>(PreviewModel.organization1, PreviewModel.organization2));
    }

    public SecuredRepository<?> getRepository(String entityKey) {
        return repositories.get(entityKey);
    }
    public SearchableOrganizationRelatedEntity saveForm(AbstractOrganizationRelatedEntityForm form) {
        return null;
    }
    public SearchableOrganizationRelatedEntity saveForm(AbstractOrganizationRelatedEntityForm form, SearchableOrganizationRelatedEntity entity) {
        return null;
    }

    @Override
    public AbstractOrganizationRelatedEntityForm getForm(String frontendMappingName) {
        return null;
    }

    @Override
    public AbstractOrganizationRelatedEntityForm getForm(String frontendMappingName, SearchableOrganizationRelatedEntity entity) {
        return null;
    }

    @Override
    public User registerUserOrReturnExisting(String email, String firstName, String lastName) {
        return null;
    }

}
