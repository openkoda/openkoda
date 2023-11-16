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

package com.openkoda.uicomponent.live;

import com.openkoda.core.customisation.FrontendMapping;
import com.openkoda.core.customisation.FrontendMappingMap;
import com.openkoda.core.form.AbstractOrganizationRelatedEntityForm;
import com.openkoda.core.form.CRUDControllerConfiguration;
import com.openkoda.core.form.ReflectionBasedEntityForm;
import com.openkoda.core.multitenancy.TenantResolver;
import com.openkoda.core.repository.common.ScopedSecureRepository;
import com.openkoda.core.security.HasSecurityRules;
import com.openkoda.core.service.ValidationService;
import com.openkoda.form.RegisterUserForm;
import com.openkoda.model.User;
import com.openkoda.model.common.SearchableOrganizationRelatedEntity;
import com.openkoda.repository.SearchableRepositories;
import com.openkoda.service.user.UserService;
import com.openkoda.uicomponent.DataServices;
import jakarta.inject.Inject;
import org.springframework.stereotype.Component;
import org.springframework.validation.BeanPropertyBindingResult;

@Component
public class LiveDataServices implements DataServices {
    @Inject
    private FrontendMappingMap frontendMappingMap;

    @Inject
    private ValidationService validationService;

    @Inject
    private UserService userService;

    public ScopedSecureRepository<?> getRepository(String entityKey) {
        return SearchableRepositories.getSearchableRepository(entityKey, HasSecurityRules.SecurityScope.USER_IN_ORGANIZATION);
    }
    public ScopedSecureRepository<?> getRepository(String entityKey, HasSecurityRules.SecurityScope securityScope) {
        return SearchableRepositories.getSearchableRepository(entityKey, securityScope);
    }
    public ScopedSecureRepository<?> getRepository(String entityKey, String securityScope) {
        return SearchableRepositories.getSearchableRepository(entityKey, HasSecurityRules.SecurityScope.valueOf(securityScope));
    }
    public AbstractOrganizationRelatedEntityForm getForm(String frontendMappingName, SearchableOrganizationRelatedEntity entity) {
        FrontendMapping frontendMapping = frontendMappingMap.get(frontendMappingName);
        CRUDControllerConfiguration conf = CRUDControllerConfiguration.getBuilder("form", frontendMapping.definition(), frontendMapping.repository(), ReflectionBasedEntityForm.class);
        Long orgId = TenantResolver.getTenantedResource().organizationId;
        if(entity == null) {
            entity = conf.createNewEntity(orgId);
        }
        ReflectionBasedEntityForm result = (ReflectionBasedEntityForm) conf.createNewForm(orgId, entity);

        if(result.getBindingResult() == null){
            result.setBindingResult(new BeanPropertyBindingResult(result, result.getFrontendMappingDefinition().name));
        }

        return result;
    }

    @Override
    public User registerUserOrReturnExisting(String email, String firstName, String lastName) {
        RegisterUserForm form = new RegisterUserForm();
        form.setLogin(email);
        form.setFirstName(firstName);
        form.setLastName(lastName);
        return userService.registerUserOrReturnExisting(form, false);
    }

    public AbstractOrganizationRelatedEntityForm getForm(String frontendMappingName) {
        return getForm(frontendMappingName, null);
    }

    public SearchableOrganizationRelatedEntity saveForm(AbstractOrganizationRelatedEntityForm form, SearchableOrganizationRelatedEntity entity) {
        FrontendMapping frontendMapping = frontendMappingMap.get(form.frontendMappingDefinition.name);

        CRUDControllerConfiguration conf = CRUDControllerConfiguration.getBuilder("form", frontendMapping.definition(), frontendMapping.repository(), ReflectionBasedEntityForm.class);
        Long orgId = TenantResolver.getTenantedResource().organizationId;
        if (entity == null) {
            entity = conf.createNewEntity(orgId);
        }

        SearchableOrganizationRelatedEntity e = validationService.validateAndPopulateToEntity(form, form.getBindingResult(), entity);
        e = (SearchableOrganizationRelatedEntity) conf.getSecureRepository().saveOne(e);
        return e;

    }

    @Override
    public SearchableOrganizationRelatedEntity saveForm(AbstractOrganizationRelatedEntityForm form) {
        return saveForm(form, null);
    }


}
