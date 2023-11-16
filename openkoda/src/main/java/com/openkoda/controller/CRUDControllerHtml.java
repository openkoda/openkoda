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

import com.openkoda.core.controller.generic.AbstractController;
import com.openkoda.core.flow.Flow;
import com.openkoda.core.flow.PageAttr;
import com.openkoda.core.form.AbstractOrganizationRelatedEntityForm;
import com.openkoda.core.form.CRUDControllerConfiguration;
import com.openkoda.core.form.ReflectionBasedEntityForm;
import com.openkoda.core.security.HasSecurityRules;
import com.openkoda.model.PrivilegeBase;
import com.openkoda.model.common.SearchableOrganizationRelatedEntity;
import jakarta.validation.Valid;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.InvocationTargetException;
import java.util.LinkedHashMap;
import java.util.Map;

import static com.openkoda.controller.common.URLConstants._HTML_ORGANIZATION_ORGANIZATIONID;
import static com.openkoda.core.controller.generic.AbstractController._HTML;


/**
 * Controller that handles requests for generic controllers registered in {@link HtmlCRUDControllerConfigurationMap}.
 */
@RestController
@RequestMapping({_HTML_ORGANIZATION_ORGANIZATIONID + "/{obj}", _HTML + "/{obj}"})
public class CRUDControllerHtml extends AbstractController implements HasSecurityRules {

    @Value("${default.layout:main}")
    String defaultLayoutName;

    /** GET request that displays list of instances of entity {@link CRUDControllerConfiguration#getEntityClass()} associated with a generic controller registered under {@param objKey}.
     * The list is restricted to the result of search with the search term {@param search}
     *
     * @param organizationId
     * @param objKey - key under which the controller configuration is registered
     * @param aPageable
     * @param search
     * @return
     */
    @GetMapping(_ALL)
    //TODO Rule 1.2: All business logic delegation should be in Abstract Controller
    //TODO Rule 1.4 All methods in non-public controllers must have @PreAuthorize
    public Object getAll(
            @PathVariable(name=ORGANIZATIONID, required = false) Long organizationId,
            @PathVariable(name="obj", required=true) String objKey,
            @Qualifier("obj") Pageable aPageable,
            @RequestParam(required = false, defaultValue = "", name = "obj_search") String search
            ) {
        debug("[getAll]");
        CRUDControllerConfiguration conf = controllers.htmlCrudControllerConfigurationMap.get(objKey);
        PrivilegeBase privilege = conf.getGetAllPrivilege();
        if (not(hasGlobalOrOrgPrivilege(privilege, organizationId))) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        return Flow.init(componentProvider)
                .thenSet((PageAttr<Page<SearchableOrganizationRelatedEntity>>)conf.getEntityPageAttribute(), a -> (Page<SearchableOrganizationRelatedEntity>) conf.getSecureRepository().search(search, organizationId, conf.getAdditionalSpecification(), aPageable))
                .thenSet(genericTableViewList, a -> ReflectionBasedEntityForm.calculateFieldsValuesWithReadPrivileges(conf.getFrontendMappingDefinition(), a.result, conf.getTableFormFieldNames()))
                .thenSet(genericTableViewHeaders, a -> ReflectionBasedEntityForm.getFieldsHeaders(conf.getFrontendMappingDefinition(), conf.getTableFormFieldNames()))
                .thenSet(isMapEntity, a -> conf.isMapEntity())
                .thenSet(frontendMappingDefinition, a -> conf.getFrontendMappingDefinition())
                .thenSet(menuItem,a->objKey)
                .execute()
                .mav(conf.getTableView());
    }

    /** Similar to {@link CRUDControllerHtml#getAll(Long, String, Pageable, String)} but returns json.
     * Returned properties are set by {@param properties}
     * @param organizationId
     * @param objKey
     * @param aPageable
     * @param properties - comma separated list of entity properties to be returned
     * @param search
     * @return
     * @throws IllegalAccessException
     * @throws NoSuchMethodException
     * @throws InvocationTargetException
     */
    @GetMapping(_ALL + ".json")
    //TODO Rule 1.2: All business logic delegation should be in Abstract Controller
    //TODO Rule 1.4 All methods in non-public controllers must have @PreAuthorize
    public Object getAllAsJson(
            @PathVariable(value = ORGANIZATIONID, required = false) Long organizationId,
            @PathVariable(name="obj", required=true) String objKey,
            @Qualifier("obj") Pageable aPageable,
            @RequestParam(name = "properties", required = false) String properties,
            @RequestParam(required = false, defaultValue = "", name = "obj_search") String search
            ) throws IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        debug("[getAll]");
        CRUDControllerConfiguration conf = controllers.htmlCrudControllerConfigurationMap.get(objKey);
        PrivilegeBase privilege = conf.getGetAllPrivilege();

        if (not(hasGlobalOrOrgPrivilege(privilege, organizationId))) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        //check read privileges for particular fields
        String[] propertyNames = StringUtils.isBlank(properties) ? new String[0] : properties.split(",");
        for (String p : propertyNames) {
            PrivilegeBase propertyPrivilege = conf.getFieldReadPrivilege(p);
            if (not(hasGlobalOrOrgPrivilege(propertyPrivilege, organizationId))) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
        }
        Page<SearchableOrganizationRelatedEntity> resultPage = (Page) Flow.init()
                .thenSet(conf.getEntityPageAttribute(),
                        a -> conf.getSecureRepository().search(search, organizationId, conf.getAdditionalSpecification(), aPageable))
                .thenSet(frontendMappingDefinition, a -> conf.getFrontendMappingDefinition())
                .thenSet(isMapEntity, a -> conf.isMapEntity())
                .execute().get(conf.getEntityPageAttribute());

        //convert page to result
        Map<Long, Object[]> result = new LinkedHashMap<>();
        for(SearchableOrganizationRelatedEntity o : resultPage.getContent()) {
            Object[] propertyValues = new Object[propertyNames.length];
            for (int i = 0; i < propertyNames.length; i++) {
                propertyValues[i] = PropertyUtils.getProperty(o, propertyNames[i]);
            }
            result.put(o.getId(), propertyValues);
        }

        return result;
    }

    /** Displays a screen that allows to create a new instance of entity {@link CRUDControllerConfiguration#getEntityClass()} associated with a generic controller registered under {@param objKey}.
     * @param organizationId
     * @param objKey
     * @return
     */
    @GetMapping(_NEW_SETTINGS)
    //TODO Rule 1.2: All business logic delegation should be in Abstract Controller
    //TODO Rule 1.4 All methods in non-public controllers must have @PreAuthorize
    public Object create(
            @PathVariable(name = ORGANIZATIONID, required = false) Long organizationId,
            @PathVariable(name="obj", required=true) String objKey) {

        CRUDControllerConfiguration conf = controllers.htmlCrudControllerConfigurationMap.get(objKey);
        PrivilegeBase privilege = conf.getGetNewPrivilege();
        if (not(hasGlobalOrOrgPrivilege(privilege, organizationId))) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return Flow.init(transactional)
                .thenSet(conf.getFormAttribute(), a -> conf.createNewForm(organizationId, null))
                .thenSet(menuItem,a->objKey)
                .execute()
                .mav(conf.getSettingsView());
    }

    /** Displays a screen that allows to update an instance of entity {@link CRUDControllerConfiguration#getEntityClass()} associated with a generic controller registered under {@param objKey}.
     *  The id of the instance is {@param objectId}
     * @param objectId
     * @param organizationId
     * @param objKey
     * @return
     */
    @GetMapping(_ID_SETTINGS)
    //TODO Rule 1.2: All business logic delegation should be in Abstract Controller
    //TODO Rule 1.4 All methods in non-public controllers must have @PreAuthorize
    public Object setting(
            @PathVariable(name = ID) Long objectId,
            @PathVariable(name = ORGANIZATIONID, required = false) Long organizationId,
            @PathVariable(name="obj", required=true) String objKey) {

        CRUDControllerConfiguration conf = controllers.htmlCrudControllerConfigurationMap.get(objKey);
        PrivilegeBase privilege = conf.getGetSettingsPrivilege();
        if (not(hasGlobalOrOrgPrivilege(privilege, organizationId))) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return Flow.init(componentProvider, objectId)
                .then(a -> conf.getSecureRepository().findOne(objectId))
                .thenSet(conf.getFormAttribute(), ac -> conf.createNewForm(organizationId, (SearchableOrganizationRelatedEntity) ac.result))
                .thenSet(menuItem,a->objKey)
                .execute()
                .mav(conf.getSettingsView());
    }

    /** Handles a request that creates a new instance of entity {@link CRUDControllerConfiguration#getEntityClass()} associated with a generic controller registered under {@param objKey}.
     * The response is either success or error message depending on the validation of data provided in {@param form}
     * @param organizationId
     * @param objKey
     * @param form
     * @param br
     * @return
     */
    @PostMapping(_NEW_SETTINGS)
    //TODO Rule 1.2: All business logic delegation should be in Abstract Controller
    //TODO Rule 1.4 All methods in non-public controllers must have @PreAuthorize
    public Object saveNew(
            @PathVariable(name = ORGANIZATIONID, required = false) Long organizationId,
            @PathVariable(name="obj", required=true) String objKey,
            @Valid AbstractOrganizationRelatedEntityForm form, BindingResult br) {
        debug("[saveNew]");
        CRUDControllerConfiguration conf = controllers.htmlCrudControllerConfigurationMap.get(objKey);
        PrivilegeBase privilege = conf.getPostNewPrivilege();
        if (not(hasGlobalOrOrgPrivilege(privilege, organizationId))) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return ((Flow<Object, AbstractOrganizationRelatedEntityForm, DefaultComponentProvider>)
                Flow.init(componentProvider, conf.getFormAttribute(), form))
                .then(a -> services.validation.validateAndPopulateToEntity(form, br, conf.createNewEntity(organizationId)))
                .then(a -> (SearchableOrganizationRelatedEntity)conf.getSecureRepository().saveOne(a.result))
                .thenSet(conf.getFormAttribute(), a -> conf.createNewForm(organizationId, a.result))
                .execute()
                .mav(conf.getFormSuccessFragment(), conf.getFormErrorFragment());
    }

    /** Handles a request that updates an instance of entity {@link CRUDControllerConfiguration#getEntityClass()} associated with a generic controller registered under {@param objKey}.
     *  The id of the instance is {@param objectId}. The response is either success or error message depending on the validation of data provided in {@param form}
     *
     * @param objectId
     * @param organizationId
     * @param objKey
     * @param form
     * @param br
     * @return
     */
    @PostMapping(_ID_SETTINGS)
    //TODO Rule 1.2: All business logic delegation should be in Abstract Controller
    //TODO Rule 1.4 All methods in non-public controllers must have @PreAuthorize
    public Object save(
            @PathVariable(ID) Long objectId,
            @PathVariable(name = ORGANIZATIONID, required = false) Long organizationId,
            @PathVariable(name="obj", required=true) String objKey,
            @Valid AbstractOrganizationRelatedEntityForm form, BindingResult br) {
        debug("[saveNew]");
        CRUDControllerConfiguration conf = controllers.htmlCrudControllerConfigurationMap.get(objKey);
        PrivilegeBase privilege = conf.getPostSavePrivilege();
        if (not(hasGlobalOrOrgPrivilege(privilege, organizationId))) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return ((Flow<Object, AbstractOrganizationRelatedEntityForm, DefaultComponentProvider>)
                Flow.init(componentProvider, conf.getFormAttribute(), form))
                .then(a -> (SearchableOrganizationRelatedEntity)conf.getSecureRepository().findOne(objectId))
                .then(a -> services.validation.validateAndPopulateToEntity(form, br,a.result))
                .then(a -> (SearchableOrganizationRelatedEntity)conf.getSecureRepository().saveOne(a.result))
                .thenSet(conf.getFormAttribute(), a -> conf.createNewForm(organizationId, a.result))
                .execute()
                .mav(conf.getFormSuccessFragment(), conf.getFormErrorFragment());
    }

    /** Handles a request that deletes an instance of entity {@link CRUDControllerConfiguration#getEntityClass()} associated with a generic controller registered under {@param objKey}.
     *  The id of the instance is {@param objectId}. The response is either true or false depending on the success of the operation
     *
     * @param objectId
     * @param organizationId
     * @param objKey
     * @return
     */
    @PostMapping(_ID_REMOVE)
    //TODO Rule 1.2: All business logic delegation should be in Abstract Controller
    //TODO Rule 1.4 All methods in non-public controllers must have @PreAuthorize
    public Object remove(
            @PathVariable(name=ID) Long objectId,
            @PathVariable(name = ORGANIZATIONID, required = false) Long organizationId,
            @PathVariable(name="obj", required=true) String objKey) {
        CRUDControllerConfiguration conf = controllers.htmlCrudControllerConfigurationMap.get(objKey);
        PrivilegeBase privilege = conf.getPostRemovePrivilege();
        if (not(hasGlobalOrOrgPrivilege(privilege, organizationId))) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return Flow.init(componentProvider, objectId)
                .then(a -> conf.getSecureRepository().deleteOne(objectId))
                .execute()
                .mav(a -> true, a -> false);
    }

}
