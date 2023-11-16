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

package com.openkoda.controller;

import com.openkoda.core.customisation.CustomisationService;
import com.openkoda.core.form.ReflectionBasedEntityForm;
import com.openkoda.form.PageBuilderForm;
import com.openkoda.model.FrontendResource;
import com.openkoda.model.Privilege;
import com.openkoda.repository.SecureFormRepository;
import com.openkoda.repository.SecureFrontendResourceRepository;
import com.openkoda.repository.SecureServerJsRepository;
import com.openkoda.repository.organization.SecureOrganizationRepository;
import jakarta.annotation.PostConstruct;
import jakarta.inject.Inject;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;

import static com.openkoda.form.FrontendMappingDefinitions.*;

/**
 * Used for registration of generic controllers configurations {@link com.openkoda.core.form.CRUDControllerConfiguration} on application start.
 * It is a place where generic controllers are defined.
 * {@link CRUDControllerHtml} provides request handlers for typical operations which are available out of the box after controller registration
 */
@Component
public class CRUDControllers {

    @Inject
    CustomisationService customisationService;
    @Inject
    HtmlCRUDControllerConfigurationMap htmlCrudControllerConfigurationMap;
    @Inject
    SecureServerJsRepository serverJsRepository;
    @Inject
    SecureFrontendResourceRepository frontendResourceRepository;
    @Inject
    SecureOrganizationRepository secureOrganizationRepository;
    @Inject
    SecureFormRepository formRepository;

    /**
     * Registers generic controllers using {@link CustomisationService#registerOnApplicationStartListener(Consumer)}
     */
    @PostConstruct
    void init() {

        customisationService.registerOnApplicationStartListener(
                a -> htmlCrudControllerConfigurationMap.registerCRUDController(
                                organizationsApi, secureOrganizationRepository, ReflectionBasedEntityForm.class, Privilege.readOrgData,Privilege.manageOrgData)
                        .setGenericTableFields("id","name"));
        customisationService.registerOnApplicationStartListener(
                a -> htmlCrudControllerConfigurationMap.registerCRUDController(PAGE_BUILDER_FORM,
                                PageBuilderForm.pageBuilderForm, frontendResourceRepository, PageBuilderForm.class, Privilege.canAccessGlobalSettings,Privilege.canAccessGlobalSettings)
                        .setGenericTableFields("name","urlPath")
                        .setAdditionalPredicate((r, q, cb) -> cb.equal(r.get("resourceType"), FrontendResource.ResourceType.DASHBOARD)));
        customisationService.registerOnApplicationStartListener(
                a -> htmlCrudControllerConfigurationMap.registerCRUDController(
                                frontendResourceForm, frontendResourceRepository, ReflectionBasedEntityForm.class)
                        .setGenericTableFields("name","includeInSitemap","type","urlPath")
                        .setTableView("frontend-resource-all"));

    }

}
