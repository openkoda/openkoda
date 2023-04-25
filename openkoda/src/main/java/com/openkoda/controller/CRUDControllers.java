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

package com.openkoda.controller;

import com.openkoda.core.customisation.CustomisationService;
import com.openkoda.core.form.ReflectionBasedEntityForm;
import com.openkoda.dto.ServerJsDto;
import com.openkoda.form.ServerJsForm;
import com.openkoda.model.Privilege;
import com.openkoda.repository.SecureFrontendResourceRepository;
import com.openkoda.repository.SecureServerJsRepository;
import com.openkoda.repository.organization.SecureOrganizationRepository;
import jakarta.annotation.PostConstruct;
import jakarta.inject.Inject;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;

import static com.openkoda.form.FrontendMappingDefinitions.frontendResourceForm;
import static com.openkoda.form.FrontendMappingDefinitions.organizationsApi;
import static com.openkoda.form.ServerJsFrontendMappingDefinitions.serverJsFrontendMappingDefinition;

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
    CRUDControllerConfigurationMap crudControllerConfigurationMap;
    @Inject
    SecureServerJsRepository serverJsRepository;
    @Inject
    SecureFrontendResourceRepository frontendResourceRepository;
    @Inject
    SecureOrganizationRepository secureOrganizationRepository;

    /**
     * Registers generic controllers using {@link CustomisationService#registerOnApplicationStartListener(Consumer)}
     */
    @PostConstruct
    void init() {

        customisationService.registerOnApplicationStartListener(
                a -> crudControllerConfigurationMap.registerCRUDController(
                                serverJsFrontendMappingDefinition, serverJsRepository, ReflectionBasedEntityForm.class)
                        .setDtoClass(ServerJsDto.class)
                        .setFormClass(ServerJsForm.class)
                        .setGenericTableFields("name"));

        customisationService.registerOnApplicationStartListener(
                a -> crudControllerConfigurationMap.registerCRUDController(
                                frontendResourceForm, frontendResourceRepository, ReflectionBasedEntityForm.class)
                        .setGenericTableFields("name","includeInSitemap","type","urlPath")
                        .setTableView("frontend-resource-all"));

        customisationService.registerOnApplicationStartListener(
                a -> crudControllerConfigurationMap.registerCRUDController(
                                organizationsApi, secureOrganizationRepository, ReflectionBasedEntityForm.class, Privilege.readOrgData,Privilege.manageOrgData)
                        .setGenericTableFields("id","name"));

    }

}
