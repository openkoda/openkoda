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

package com.openkoda.controller.api;

import com.openkoda.controller.ComponentProvider;
import com.openkoda.controller.DefaultComponentProvider;
import com.openkoda.controller.common.URLConstants;
import com.openkoda.core.flow.Flow;
import com.openkoda.core.flow.PageAttr;
import com.openkoda.core.form.CRUDControllerConfiguration;
import com.openkoda.core.form.ReflectionBasedEntityForm;
import com.openkoda.core.security.HasSecurityRules;
import com.openkoda.model.PrivilegeBase;
import com.openkoda.model.common.SearchableOrganizationRelatedEntity;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.util.HashMap;


abstract public class CRUDApiController<E extends SearchableOrganizationRelatedEntity> extends ComponentProvider implements URLConstants, HasSecurityRules {

    @Inject
    protected DefaultComponentProvider componentProvider;

    private final String key;

    public CRUDApiController(String key) {
        this.key = key;
    }

    @GetMapping(value=_ALL, produces=MediaType.APPLICATION_JSON_VALUE)
    public Object getAll(
            @PathVariable(name=ORGANIZATIONID, required = false) Long organizationId,
            @Qualifier("obj") Pageable aPageable,
            @RequestParam(required = false, defaultValue = "", name = "obj_search") String search) {
        debug("[getAll]");
        CRUDControllerConfiguration conf = controllers.crudControllerConfigurationMap.get(key);
        PrivilegeBase privilege = conf.getGetAllPrivilege();
        if (not(hasGlobalOrOrgPrivilege(privilege, organizationId))) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return Flow.init(componentProvider)
                .then( a -> (Page<SearchableOrganizationRelatedEntity>) conf.getSecureRepository().search(search, organizationId, conf.getAdditionalSpecification(), aPageable))
                .thenSet(genericTableViewMap, a -> ReflectionBasedEntityForm.calculateFieldsValuesWithReadPrivilegesAsMap(conf.getFrontendMappingDefinition(), a.result, conf.getTableFormFieldNames()))
                .execute()
                .getAsMap(genericTableViewMap);
    }

    @GetMapping(value = "{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Object settings(
            @PathVariable(name = ID) Long objectId,
            @PathVariable(name = ORGANIZATIONID, required = false) Long organizationId
            ) {

        CRUDControllerConfiguration conf = controllers.crudControllerConfigurationMap.get(key);
        PrivilegeBase privilege = conf.getGetSettingsPrivilege();
        if (not(hasGlobalOrOrgPrivilege(privilege, organizationId))) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return Flow.init(componentProvider, objectId)
                .then(a -> conf.getSecureRepository().findOne(objectId))
                .thenSet(conf.getEntityPageAttribute(), a -> ReflectionBasedEntityForm.calculateFieldValuesWithReadPrivilegesAsMap(conf.getFrontendMappingDefinition(), (SearchableOrganizationRelatedEntity) a.result, conf.getFrontendMappingDefinition().getNamesOfValuedTypeFields()))
                .execute().getAsMap(conf.getEntityPageAttribute());
    }

    @PostMapping(value="{id}/update", produces = MediaType.APPLICATION_JSON_VALUE)
    public Object save(
            @PathVariable(ID) Long objectId,
            @PathVariable(name = ORGANIZATIONID, required = false) Long organizationId,
            @RequestBody HashMap<String,String> params) {
        debug("[saveNew]");
        CRUDControllerConfiguration conf = controllers.crudControllerConfigurationMap.get(key);
        PrivilegeBase privilege = conf.getPostSavePrivilege();
        if (not(hasGlobalOrOrgPrivilege(privilege, organizationId))) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return
                Flow.init(componentProvider)
                .thenSet((PageAttr<E>) conf.getEntityPageAttribute(), a -> (E) conf.getSecureRepository().findOne(objectId))
                .thenSet((PageAttr<ReflectionBasedEntityForm>) conf.getFormAttribute(), a -> (ReflectionBasedEntityForm) conf.createNewForm(organizationId, a.result))
                 .then(a -> a.result.prepareDto(params, (E) a.model.get(conf.getEntityPageAttribute())))
                .thenSet(isValid,a -> services.validation.validateAndPopulateToEntity((ReflectionBasedEntityForm) a.model.get(conf.getFormAttribute()), (E) a.model.get(conf.getEntityPageAttribute())))
                .then( a -> {
                    if(a.result) {
                        conf.getSecureRepository().saveOne(a.model.get(conf.getEntityPageAttribute()));
                    }
                    return 1;
                })
                .execute()
                .getAsMap(isValid);
    }

    @PostMapping(value="create", produces = MediaType.APPLICATION_JSON_VALUE)
    public Object saveNew(
            @PathVariable(name = ORGANIZATIONID, required = false) Long organizationId,
            @RequestBody HashMap<String,String> params) {
        debug("[saveNew]");
        CRUDControllerConfiguration conf = controllers.crudControllerConfigurationMap.get(key);
        PrivilegeBase privilege = conf.getPostNewPrivilege();
        if (not(hasGlobalOrOrgPrivilege(privilege, organizationId))) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return Flow.init(componentProvider)
                .thenSet((PageAttr<E>) conf.getEntityPageAttribute(), a -> (E) conf.createNewEntity(organizationId))
                .thenSet((PageAttr<ReflectionBasedEntityForm>) conf.getFormAttribute(), a -> (ReflectionBasedEntityForm) conf.createNewForm(organizationId, a.result))
                .then(a -> a.result.prepareDto(params, (SearchableOrganizationRelatedEntity) a.model.get(conf.getEntityPageAttribute())))
                .thenSet(isValid,a -> services.validation.validateAndPopulateToEntity((ReflectionBasedEntityForm) a.model.get(conf.getFormAttribute()), (E) a.model.get(conf.getEntityPageAttribute())))
                .then( a -> {
                    if(a.result) {
                        conf.getSecureRepository().saveOne(a.model.get(conf.getEntityPageAttribute()));
                    }
                    return 1;
                })
                .execute()
                .getAsMap(isValid);
    }

    @PostMapping(value="{id}/remove", produces = MediaType.APPLICATION_JSON_VALUE)
    public Object remove(
            @PathVariable(name=ID) Long objectId,
            @PathVariable(name = ORGANIZATIONID, required = false) Long organizationId) {
        CRUDControllerConfiguration conf = controllers.crudControllerConfigurationMap.get(key);
        PrivilegeBase privilege = conf.getPostRemovePrivilege();
        if (not(hasGlobalOrOrgPrivilege(privilege, organizationId))) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return Flow.init(componentProvider, objectId)
                .then(a -> conf.getSecureRepository().deleteOne(objectId))
                .execute();
    }
}
