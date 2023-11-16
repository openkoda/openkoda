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

import com.openkoda.core.form.*;
import com.openkoda.core.repository.common.ScopedSecureRepository;
import com.openkoda.model.Privilege;
import com.openkoda.model.PrivilegeBase;
import com.openkoda.repository.SecureMapEntityRepository;
import jakarta.inject.Inject;

import java.util.HashMap;
import java.util.function.Function;


/**
 * Used for storing configurations of registered generic controllers {@link CRUDControllerConfiguration}
 * Registration of configurations is invoked on application start in {@link CRUDControllers}
 */
public abstract class AbstractCRUDControllerConfigurationMap extends HashMap<String, CRUDControllerConfiguration> {

    @Inject
    SecureMapEntityRepository secureMapEntityRepository;

    /** creates a generic controller configuration {@link CRUDControllerConfiguration} and registers it
     * @param key - key in the hashmap under which the controller is registered
     * @param defaultReadPrivilege
     * @param defaultWritePrivilege
     * @param builder
     * @param secureRepository
     * @param formClass
     * @return
     */
    public CRUDControllerConfiguration registerCRUDControllerBuilder(
            String key,
            PrivilegeBase defaultReadPrivilege,
            PrivilegeBase defaultWritePrivilege,
            Function<FormFieldDefinitionBuilderStart,
            FormFieldDefinitionBuilder> builder,
            ScopedSecureRepository secureRepository,
            Class formClass
            ) {

        CRUDControllerConfiguration controllerConfiguration = CRUDControllerConfiguration.getBuilder(key,
                FrontendMappingDefinition.createFrontendMappingDefinition(key, defaultReadPrivilege, defaultWritePrivilege, builder),
                secureRepository, formClass);
        this.put(key, controllerConfiguration);
        return controllerConfiguration;
    }

    /** creates a generic controller configuration {@link CRUDControllerConfiguration} and registers it
     * @param frontendMappingDefinition
     * @param secureRepository
     * @param formClass
     * @return
     */
    public CRUDControllerConfiguration registerCRUDController(
            FrontendMappingDefinition frontendMappingDefinition,
            ScopedSecureRepository secureRepository,
            Class formClass
            ) {
        String key = frontendMappingDefinition.name;
        CRUDControllerConfiguration controllerConfiguration = CRUDControllerConfiguration.getBuilder(key,
                frontendMappingDefinition, secureRepository, formClass);
        this.put(key, controllerConfiguration);
        return controllerConfiguration;
    }

    /**
     * unregisteres the generic controller configuration for given key
     * @param key
     */
    public void unregisterCRUDController(String key) {
        this.remove(key);
    }

    /** creates a generic controller configuration {@link CRUDControllerConfiguration} and registers it
     * with {@link ReflectionBasedEntityForm}
     * @param frontendMappingDefinition
     * @param secureRepository
     * @return
     */
    public CRUDControllerConfiguration registerCRUDController(
            FrontendMappingDefinition frontendMappingDefinition,
            ScopedSecureRepository secureRepository) {
        return registerCRUDController(frontendMappingDefinition, secureRepository, ReflectionBasedEntityForm.class);
    }

    /** creates a generic controller configuration {@link CRUDControllerConfiguration} and registers it
     * @param key - key in the hashmap under which the controller is registered
     * @param defaultReadPrivilege
     * @param defaultWritePrivilege
     * @param builder
     * @param secureRepository
     * @return
     */
    public CRUDControllerConfiguration registerCRUDControllerBuilder(
            String key,
            PrivilegeBase defaultReadPrivilege,
            PrivilegeBase defaultWritePrivilege,
            Function<FormFieldDefinitionBuilderStart, FormFieldDefinitionBuilder> builder,
            ScopedSecureRepository secureRepository) {

        CRUDControllerConfiguration controllerConfiguration = CRUDControllerConfiguration.getBuilder(key,
                FrontendMappingDefinition.createFrontendMappingDefinition(key, defaultReadPrivilege, defaultWritePrivilege, builder),
                secureRepository, ReflectionBasedEntityForm.class);
        this.put(key, controllerConfiguration);
        return controllerConfiguration;
    }


    public CRUDControllerConfiguration registerCRUDController(
            FrontendMappingDefinition frontendMappingDefinition,
            ScopedSecureRepository secureRepository,
            Class formClass,
            Privilege defaultReadPrivilege,
            Privilege defaultWritePrivilege) {

        String key = frontendMappingDefinition.name;
        CRUDControllerConfiguration controllerConfiguration = CRUDControllerConfiguration.getBuilder(key,
                frontendMappingDefinition, secureRepository, formClass, defaultReadPrivilege, defaultWritePrivilege);
        this.put(key, controllerConfiguration);
        return controllerConfiguration;
    }

    public CRUDControllerConfiguration registerCRUDController(
            String key,
            FrontendMappingDefinition frontendMappingDefinition,
            ScopedSecureRepository secureRepository,
            Class formClass,
            Privilege defaultReadPrivilege,
            Privilege defaultWritePrivilege) {

        CRUDControllerConfiguration controllerConfiguration = CRUDControllerConfiguration.getBuilder(key,
                frontendMappingDefinition, secureRepository, formClass, defaultReadPrivilege, defaultWritePrivilege);
        this.put(key, controllerConfiguration);
        return controllerConfiguration;
    }

    /** creates a generic controller configuration {@link CRUDControllerConfiguration} for entity {@link com.openkoda.model.MapEntity}
     *  and registers it
     *
     * @param key - key in the hashmap under which the controller is registered
     * @param builder
     * @return
     */
    @Deprecated
    public CRUDControllerConfiguration registerCRUDControllerBuilder(
            String key,
            Function<FormFieldDefinitionBuilderStart,
            FormFieldDefinitionBuilder> builder
        ) {

        CRUDControllerConfiguration controllerConfiguration = CRUDControllerConfiguration.getBuilder(key,
                FrontendMappingDefinition.createFrontendMappingDefinition(key, Privilege.canAccessGlobalSettings, Privilege.canAccessGlobalSettings, builder),
                secureMapEntityRepository, MapEntityForm.class);
        this.put(key, controllerConfiguration);
        return controllerConfiguration;
    }

    /**
     * registers a generic controller configuration {@link CRUDControllerConfiguration}
     * @param key - key in the hashmap under which the controller is registered
     * @param controllerConfiguration
     * @return
     */
    public CRUDControllerConfiguration registerCRUDControllerBuilder(
            String key,
            CRUDControllerConfiguration controllerConfiguration
            ) {
        this.put(key, controllerConfiguration);
        return controllerConfiguration;
    }

}
