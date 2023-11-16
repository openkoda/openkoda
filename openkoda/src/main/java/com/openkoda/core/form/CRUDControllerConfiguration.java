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

package com.openkoda.core.form;

import com.openkoda.controller.common.PageAttributes;
import com.openkoda.core.flow.PageAttr;
import com.openkoda.core.repository.common.ScopedSecureRepository;
import com.openkoda.dto.OrganizationRelatedObject;
import com.openkoda.model.MapEntity;
import com.openkoda.model.Privilege;
import com.openkoda.model.PrivilegeBase;
import com.openkoda.model.common.SearchableOrganizationRelatedEntity;
import com.openkoda.repository.SearchableRepositories;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class CRUDControllerConfiguration<D extends OrganizationRelatedObject, E extends SearchableOrganizationRelatedEntity, F extends AbstractOrganizationRelatedEntityForm<D, E>> {
        private final String key;
        private final FrontendMappingDefinition frontendMappingDefinition;
        private final Class<E> entityClass;
        private final boolean isMapEntity;
        private final ScopedSecureRepository<E> secureRepository;
        private final Constructor<E> entityConstructor;
        private Class<F> formClass;

        private boolean frontendMappingDefinitionInConstructor = false;
        private Constructor<F> formConstructor;
        private Constructor<F> formEntityConstructor;
        private Class<D> dtoClass = (Class<D>) OrganizationRelatedMap.class;
        private PrivilegeBase defaultControllerPrivilege = Privilege.isUser;
        private PrivilegeBase getAllPrivilege;
        private PrivilegeBase getNewPrivilege;
        private PrivilegeBase getSettingsPrivilege;
        private PrivilegeBase postNewPrivilege;
        private PrivilegeBase postSavePrivilege;
        private PrivilegeBase postRemovePrivilege;
        private PageAttr<?> entityPageAttribute = PageAttributes.organizationRelatedEntityPage;
        private PageAttr<?> entityAttribute = PageAttributes.organizationRelatedEntity;
        private PageAttr<?> formAttribute = PageAttributes.organizationRelatedForm;
        private String tableView = "generic-all";
        private String settingsView = "generic-settings";
        private String formNewFragment = "generic-settings-entity-form::generic-settings-form-new";
        private String formSuccessFragment = "generic-settings-entity-form::generic-settings-form-success";
        private String formErrorFragment = "generic-settings-entity-form::generic-settings-form-error";
        private Specification<E> additionalPredicate;

        private String[] genericTableFields;

        private CRUDControllerConfiguration(String key, FrontendMappingDefinition frontendMappingDefinition,
                                            ScopedSecureRepository<E> secureRepository,
                                            Class<F> formClass) {
                try {
                        this.key = key;
                        this.frontendMappingDefinition = frontendMappingDefinition;
                        this.secureRepository = secureRepository;
                        this.entityClass = (Class<E>) SearchableRepositories.getGlobalSearchableRepositoryAnnotation(secureRepository).entityClass();
                        this.isMapEntity = this.entityClass.equals(MapEntity.class);
                        this.entityConstructor = this.entityClass.getConstructor(Long.class);
                        this.formClass = formClass;
                        detectFormConstructor();
                } catch (NoSuchMethodException | SecurityException e) {
                        throw new RuntimeException(e);
                }
        }
        private CRUDControllerConfiguration(String key, FrontendMappingDefinition frontendMappingDefinition,
                                            ScopedSecureRepository<E> secureRepository,
                                            Class<F> formClass, Privilege readPrivilege, Privilege writePrivilege) {
                try {
                        this.key = key;
                        this.frontendMappingDefinition = frontendMappingDefinition;
                        this.secureRepository = secureRepository;
                        this.entityClass = (Class<E>) SearchableRepositories.getGlobalSearchableRepositoryAnnotation(secureRepository).entityClass();
                        this.isMapEntity = this.entityClass.equals(MapEntity.class);
                        this.entityConstructor = this.entityClass.getConstructor(Long.class);
                        this.formClass = formClass;
                        detectFormConstructor();
                        this.getAllPrivilege = readPrivilege;
                        this.getNewPrivilege = writePrivilege;
                        this.getSettingsPrivilege = readPrivilege;
                        this.postNewPrivilege = writePrivilege;
                        this.postSavePrivilege = writePrivilege;
                        this.postRemovePrivilege = writePrivilege;
                } catch (NoSuchMethodException | SecurityException e) {
                        throw new RuntimeException(e);
                }
        }
        public static CRUDControllerConfiguration getBuilder(
                String key,
                FrontendMappingDefinition frontendMappingDefinition,
                ScopedSecureRepository secureRepository,
                Class formClass) {
                return new CRUDControllerConfiguration(key, frontendMappingDefinition,
                        secureRepository, formClass);
        }

        public static CRUDControllerConfiguration getBuilder(
                String key,
                FrontendMappingDefinition frontendMappingDefinition,
                ScopedSecureRepository secureRepository,
                Class formClass,
                Privilege readPrivilege,
                Privilege writePrivilege){
                return new CRUDControllerConfiguration(key, frontendMappingDefinition,
                        secureRepository, formClass, readPrivilege, writePrivilege);
        }

        public CRUDControllerConfiguration<D, E, F> setFormClass(Class<F> formClass) {
                this.formClass = formClass;
                try {
                        detectFormConstructor();
                } catch (NoSuchMethodException e) {
                        throw new RuntimeException(e);
                }
                return this;
        }

        public CRUDControllerConfiguration<D, E, F> setDtoClass(Class<D> dtoClass) {
                this.dtoClass = dtoClass;
                return this;
        }

        public CRUDControllerConfiguration<D, E, F> setDefaultControllerPrivilege(PrivilegeBase defaultControllerPrivilege) {
                this.defaultControllerPrivilege = defaultControllerPrivilege;
                return this;
        }

        public CRUDControllerConfiguration<D, E, F> setGetAllPrivilege(PrivilegeBase getAllPrivilege) {
                this.getAllPrivilege = getAllPrivilege;
                return this;
        }

        public CRUDControllerConfiguration<D, E, F> setGetNewPrivilege(PrivilegeBase getNewPrivilege) {
                this.getNewPrivilege = getNewPrivilege;
                return this;
        }

        public CRUDControllerConfiguration<D, E, F> setGetSettingsPrivilege(PrivilegeBase getSettingsPrivilege) {
                this.getSettingsPrivilege = getSettingsPrivilege;
                return this;
        }

        public CRUDControllerConfiguration<D, E, F> setPostNewPrivilege(PrivilegeBase postNewPrivilege) {
                this.postNewPrivilege = postNewPrivilege;
                return this;
        }

        public CRUDControllerConfiguration<D, E, F> setPostSavePrivilege(PrivilegeBase postSavePrivilege) {
                this.postSavePrivilege = postSavePrivilege;
                return this;
        }

        public CRUDControllerConfiguration<D, E, F> setPostRemovePrivilege(PrivilegeBase postRemovePrivilege) {
                this.postRemovePrivilege = postRemovePrivilege;
                return this;
        }

        public CRUDControllerConfiguration<D, E, F> setTableView(String tableView) {
                this.tableView = tableView;
                return this;
        }

        public String[] getTableFormFieldNames() {
                return genericTableFields;
        }

        public CRUDControllerConfiguration<D, E, F> setGenericTableFields(String... genericTableFields) {
                this.genericTableFields = genericTableFields;
                return this;
        }

        public CRUDControllerConfiguration<D, E, F> setSettingsView(String settingsView) {
                this.settingsView = settingsView;
                return this;
        }

        public String getFormNewFragment() {
                return formNewFragment;
        }

        public CRUDControllerConfiguration<D, E, F> setFormNewFragment(String formNewFragment) {
                this.formNewFragment = formNewFragment;
                return this;
        }

        public String getFormSuccessFragment() {
                return formSuccessFragment;
        }

        public CRUDControllerConfiguration<D, E, F> setFormSuccessFragment(String formSuccessFragment) {
                this.formSuccessFragment = formSuccessFragment;
                return this;
        }

        public String getFormErrorFragment() {
                return formErrorFragment;
        }

        public CRUDControllerConfiguration<D, E, F> setFormErrorFragment(String formErrorFragment) {
                this.formErrorFragment = formErrorFragment;
                return this;
        }

        public String getKey() {
                return key;
        }

        public FrontendMappingDefinition getFrontendMappingDefinition() {
                return frontendMappingDefinition;
        }

        public Class<F> getFormClass() {
                return formClass;
        }

        public Class<D> getDtoClass() {
                return dtoClass;
        }

        public Class<E> getEntityClass() {
                return entityClass;
        }

        public PrivilegeBase getDefaultControllerPrivilege() {
                return defaultControllerPrivilege;
        }

        public PrivilegeBase getGetAllPrivilege() {
                return getAllPrivilege != null ? getAllPrivilege : defaultControllerPrivilege;
        }

        public PrivilegeBase getGetNewPrivilege() {
                return getNewPrivilege != null ? getNewPrivilege : defaultControllerPrivilege;
        }

        public PrivilegeBase getGetSettingsPrivilege() {
                return getSettingsPrivilege != null ? getSettingsPrivilege : defaultControllerPrivilege;
        }

        public PrivilegeBase getPostNewPrivilege() {
                return postNewPrivilege != null ? postNewPrivilege : defaultControllerPrivilege;
        }

        public PrivilegeBase getPostSavePrivilege() {
                return postSavePrivilege != null ? postSavePrivilege : defaultControllerPrivilege;
        }

        public PrivilegeBase getPostRemovePrivilege() {
                return postRemovePrivilege != null ? postRemovePrivilege : defaultControllerPrivilege;
        }

        public PrivilegeBase getFieldReadPrivilege(String fieldName) {
                return frontendMappingDefinition.findField(fieldName).readPrivilege;
        }

        public PrivilegeBase getFieldWritePrivilege(String fieldName) {
                return frontendMappingDefinition.findField(fieldName).writePrivilege;
        }

        public String getTableView() {
                return tableView;
        }

        public String getSettingsView() {
                return settingsView;
        }


        public ScopedSecureRepository<E> getSecureRepository() {
                return secureRepository;
        }

        public E createNewEntity(Long organizationId) {
                try {
                        E entity = entityConstructor.newInstance(organizationId);
                        if (isMapEntity) {
                                ((MapEntity) entity).setKey(key);
                        }
                        return entity;
                } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                        throw new RuntimeException(e);
                }
        }

        public F createNewForm() {
                try {
                        if (frontendMappingDefinitionInConstructor) {
                                return formConstructor.newInstance(frontendMappingDefinition);
                        } else {
                                return formConstructor.newInstance();
                        }

                } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                        throw new RuntimeException(e);
                }
        }

        public F createNewForm(Long organizationId, E entity) {
                try {
                        if (frontendMappingDefinitionInConstructor) {
                                return formEntityConstructor.newInstance(frontendMappingDefinition, organizationId, entity);
                        } else {
                                return formEntityConstructor.newInstance(organizationId, entity);
                        }
                } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                        throw new RuntimeException(e);
                }
        }

        private void detectFormConstructor() throws NoSuchMethodException {
                try {
                        this.formConstructor = this.formClass.getDeclaredConstructor(FrontendMappingDefinition.class);
                        if (ReflectionBasedEntityForm.class.equals(formClass)) {
                                this.formEntityConstructor = this.formClass.getDeclaredConstructor(FrontendMappingDefinition.class, Long.class, SearchableOrganizationRelatedEntity.class);
                        } else {
                                this.formEntityConstructor = this.formClass.getDeclaredConstructor(FrontendMappingDefinition.class, Long.class, entityClass);
                        }
                        frontendMappingDefinitionInConstructor = true;
                } catch (NoSuchMethodException e) {
                        this.formConstructor = this.formClass.getDeclaredConstructor();
                        this.formEntityConstructor = this.formClass.getDeclaredConstructor(Long.class, entityClass);
                        frontendMappingDefinitionInConstructor = false;
                }
        }

        public Specification<E> getAdditionalSpecification() {
                return isMapEntity ? (additionalPredicate != null ? (root, query, cb) -> cb.and(cb.equal(root.get("key"), key), additionalPredicate.toPredicate(root, query, cb))
                        : (root, query, cb) -> cb.equal(root.get("key"), key)) : (additionalPredicate != null ? ((root, query, cb) -> additionalPredicate.toPredicate(root, query, cb))  : null);
        }

        public boolean isMapEntity() {
                return isMapEntity;
        }

        public PageAttr<Page<E>> getEntityPageAttribute() {
                return (PageAttr<Page<E>>) entityPageAttribute;
        }

        public CRUDControllerConfiguration<D, E, F> setEntityPageAttribute(PageAttr<Page<E>> entityPageAttribute) {
                this.entityPageAttribute = entityPageAttribute;
                return this;
        }

        public PageAttr<E> getEntityAttribute() {
                return (PageAttr<E>) entityAttribute;
        }

        public CRUDControllerConfiguration<D, E, F> setEntityAttribute(PageAttr<E> entityAttribute) {
                this.entityAttribute = entityAttribute;
                return this;
        }

        public PageAttr<F> getFormAttribute() {
                return (PageAttr<F>) formAttribute;
        }

        public CRUDControllerConfiguration<D, E, F> setFormAttribute(PageAttr<F> formAttribute) {
                this.formAttribute = formAttribute;
                return this;
        }

        public CRUDControllerConfiguration<D, E, F> setAdditionalPredicate(Specification<E> additionalPredicate) {
                this.additionalPredicate = additionalPredicate;
                return this;
        }
}


