/*
MIT License

Copyright (c) 2016-2023, Openkoda CDX Sp. z o.o. Sp. K. <openkoda.com>

Permission is hereby granted, free of charge, to any person obtaining a copy of this software
 and associated documentation files (the "Software"), to deal in the Software without restriction, 
including without limitation the rights to use, copy, modify, merge, publish, distribute, 
sublicense, and/or sell copies of the Software, and to permit persons to whom the Software 
is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice 
shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, 
EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES 
OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE 
AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS 
OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES 
OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, 
TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION 
WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*/

package com.openkoda.core.controller.frontendresource;

import com.openkoda.controller.DefaultComponentProvider;
import com.openkoda.core.flow.Flow;
import com.openkoda.core.form.AbstractOrganizationRelatedEntityForm;
import com.openkoda.core.form.CRUDControllerConfiguration;
import com.openkoda.core.form.ReflectionBasedEntityForm;
import com.openkoda.model.PrivilegeBase;
import com.openkoda.model.common.ComponentEntity;
import com.openkoda.model.common.SearchableOrganizationRelatedEntity;
import com.openkoda.model.component.FrontendResource;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import static com.openkoda.controller.common.URLConstants._FRONTENDRESOURCE;
import static com.openkoda.controller.common.URLConstants._HTML_ORGANIZATION_ORGANIZATIONID;
import static com.openkoda.core.controller.generic.AbstractController._HTML;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;


/**
 * The controller for server-side generated html actions considering any {@link FrontendResource} management or CRUD operations.
 * See also {@link AbstractFrontendResourceController}
 */
@RestController
@RequestMapping({_HTML_ORGANIZATION_ORGANIZATIONID + _FRONTENDRESOURCE, _HTML + _FRONTENDRESOURCE})
public class FrontendResourceControllerHtml extends AbstractFrontendResourceController {

    /**
     * The default resource name of a content for {@link FrontendResource} with type {@link FrontendResource.Type}.HTML
     */
    @Value("${default.frontendResourcePage.template.name:frontend-resource-template}")
    String defaultFrontendResourcePageTemplate;

    /**
     * Triggers update of the {@link FrontendResource} and prepares the result response.
     * See also {@link AbstractFrontendResourceController}
     *
     * @param content a {@link java.lang.String} object.
     * @param id      a {@link java.lang.Long} object.
     * @return a {@link java.lang.Object} object.
     */
    @PostMapping(_ID)
    @ResponseBody
    @PreAuthorize(CHECK_CAN_MANAGE_FRONTEND_RESOURCES)
    public Object update(
            @PathVariable(value = ORGANIZATIONID, required = false) Long organizationId,
            @RequestParam String content,
            @PathVariable(ID) Long id) {
        debug("[update] FrontendResourceId: {}", id);
        return updateFrontendResource(content, id)
                .mav(a -> true, a -> a.get(message));
    }


    /**
     * Triggers the publish action of the {@link FrontendResource} and prepares the result response.
     * See also {@link AbstractFrontendResourceController}
     *
     * @param organizationId
     * @param frontendResourceId
     * @return java.lang.Object
     */
    @PostMapping(_ID + _PUBLISH)
    @PreAuthorize(CHECK_CAN_MANAGE_FRONTEND_RESOURCES)
    public Object publish(
            @PathVariable(value = ORGANIZATIONID, required = false) Long organizationId,
            @PathVariable(ID) Long frontendResourceId) {
        debug("[publish] FrontendResourceId: {}", frontendResourceId);
        return publishFrontendResource(frontendResourceId)
                .mav(a -> true);
    }

    /**
     * Triggers the publish action of all {@link FrontendResource} in the database and prepares the result response.
     * See also {@link AbstractFrontendResourceController}
     *
     * @return java.lang.Object
     */
    @PostMapping(_ALL + _PUBLISH)
    @PreAuthorize(CHECK_CAN_MANAGE_FRONTEND_RESOURCES)
    public Object publishAll() {
        debug("[publishAll]");
        return publishAllFrontendResource()
                .mav(a -> true);
    }


    /**
     * @deprecated
     * This method is no longer used due to changes in frontend resource flow
     * Use {@link #reloadToDraft(Long)} instead.
     */
    @Deprecated
    @PostMapping(_ID + _RELOAD)
    @PreAuthorize(CHECK_CAN_MANAGE_FRONTEND_RESOURCES)
    public Object reload(@PathVariable(ID) Long frontendResourceId) {
        debug("[reload] FrontendResourceId: {}", frontendResourceId);
        return reloadFrontendResource(frontendResourceId)
                .mav(a -> true);
    }

    /**
     * Reloads draft content of all {@link FrontendResource} .
     * See also {@link AbstractFrontendResourceController}
     *
     * @param frontendResourceId
     * @return java.lang.Object
     */
    @RequestMapping(value = _ID + _RELOAD_TO_DRAFT, method = POST)
    @PreAuthorize(CHECK_CAN_MANAGE_FRONTEND_RESOURCES)
    public Object reloadToDraft(@PathVariable(ID) Long frontendResourceId) {
        debug("[reload] FrontendResourceId: {}", frontendResourceId);
        return copyResourceContentToDraft(frontendResourceId)
                .mav(a -> true);
    }

    /**
     * Copies live content of {@link FrontendResource} to draft.
     * See also {@link AbstractFrontendResourceController}
     *
     * @param frontendResourceId
     * @return java.lang.Object
     */
    @RequestMapping(value = _ID + _COPY + _LIVE, method = POST)
    @PreAuthorize(CHECK_CAN_MANAGE_FRONTEND_RESOURCES)
    @ResponseBody
    public Object copyLiveToDraft(@PathVariable(ID) Long frontendResourceId) {
        debug("[copyLiveToDraft] FrontendResourceId: {}", frontendResourceId);
        return copyLiveContentToDraft(frontendResourceId)
                .mav(a -> true);
    }

    /**
     * Copies resource content of {@link FrontendResource} to draft.
     * See also {@link AbstractFrontendResourceController}
     *
     * @param frontendResourceId
     * @return java.lang.Object
     */
    @RequestMapping(value = _ID + _COPY + _RESOURCE, method = POST)
    @PreAuthorize(CHECK_CAN_MANAGE_FRONTEND_RESOURCES)
    @ResponseBody
    public Object copyResourceToDraft(@PathVariable(ID) Long frontendResourceId) {
        debug("[copyResourceToDraft] FrontendResourceId: {}", frontendResourceId);
        return copyResourceContentToDraft(frontendResourceId)
                .mav(a -> true);
    }

    /**
     * Downloads all {@link FrontendResource} from the database packed in a .ZIP file.
     * See also {@link com.openkoda.core.service.ZipService}, {@link com.openkoda.repository.FrontendResourceRepository}
     *
     * @return byte[]
     */
    @RequestMapping(value = _ZIP, method = GET, produces = "application/zip")
    @ResponseBody
    @PreAuthorize(CHECK_CAN_READ_FRONTEND_RESOURCES)
    public byte[] getAllZipped() {
        debug("[getAllZipped]");
        return services.zipService.zipFrontendResources(repositories.unsecure.frontendResource.findAll()).toByteArray();
    }

    @PostMapping(_NEW_SETTINGS)
    public Object saveNew(@Valid AbstractOrganizationRelatedEntityForm form, BindingResult br) {
        debug("[saveNew]");
        CRUDControllerConfiguration conf = controllers.htmlCrudControllerConfigurationMap.get(FRONTENDRESOURCE);
        PrivilegeBase privilege = conf.getPostNewPrivilege();
        Long organizationId = ((ReflectionBasedEntityForm) form).dto.getOrganizationId();
        if (not(hasGlobalOrOrgPrivilege(privilege, organizationId))) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return ((Flow<Object, AbstractOrganizationRelatedEntityForm, DefaultComponentProvider>)
                Flow.init(componentProvider, conf.getFormAttribute(), form))
                .then(a -> services.validation.validateAndPopulateToEntity(form, br, conf.createNewEntity(organizationId)))
                .then(a -> (SearchableOrganizationRelatedEntity)conf.getSecureRepository().saveOne(a.result))
                .then(a -> services.componentExport.exportToFileIfRequired((ComponentEntity) a.result))
                .thenSet(conf.getFormAttribute(), a -> conf.createNewForm(organizationId, a.result))
                .execute()
                .mav(conf.getFormSuccessFragment(), conf.getFormErrorFragment());
    }
    @PostMapping(_ID_SETTINGS)
    public Object save(
            @PathVariable(ID) Long objectId,
            @PathVariable(name = ORGANIZATIONID, required = false) Long organizationId,
            @Valid AbstractOrganizationRelatedEntityForm form, BindingResult br) {
        debug("[saveNew]");
        CRUDControllerConfiguration conf = controllers.htmlCrudControllerConfigurationMap.get(FRONTENDRESOURCE);
        PrivilegeBase privilege = conf.getPostSavePrivilege();
        if (not(hasGlobalOrOrgPrivilege(privilege, organizationId))) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return ((Flow<Object, AbstractOrganizationRelatedEntityForm, DefaultComponentProvider>)
                Flow.init(componentProvider, conf.getFormAttribute(), form))
                .then(a -> (SearchableOrganizationRelatedEntity)conf.getSecureRepository().findOne(objectId))
                .then(a -> services.validation.validateAndPopulateToEntity(form, br,a.result))
                .then(a -> (SearchableOrganizationRelatedEntity)conf.getSecureRepository().saveOne(a.result))
                .then(a -> services.componentExport.exportToFileIfRequired((ComponentEntity) a.result))
                .thenSet(conf.getFormAttribute(), a -> conf.createNewForm(organizationId, a.result))
                .execute()
                .mav(conf.getFormSuccessFragment(), conf.getFormErrorFragment());
    }
    @PostMapping(_ID_REMOVE)
    @Transactional
    public Object remove(
            @PathVariable(name=ID) Long objectId,
            @PathVariable(name = ORGANIZATIONID, required = false) Long organizationId) {
        CRUDControllerConfiguration conf = controllers.htmlCrudControllerConfigurationMap.get(FRONTENDRESOURCE);
        PrivilegeBase privilege = conf.getPostRemovePrivilege();
        if (not(hasGlobalOrOrgPrivilege(privilege, organizationId))) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return Flow.init(componentProvider, objectId)
                .then(a -> repositories.secure.scheduler.findOne(objectId))
                .then(a -> services.componentExport.removeExportedFilesIfRequired(a.result))
                .then(a -> conf.getSecureRepository().deleteOne(objectId))
                .execute()
                .mav(a -> true, a -> false);
    }
    @PostMapping(_ID_REMOVE + _DRAFT)
    @Transactional
    public Object removeDraft(
            @PathVariable(name=ID) Long objectId,
            @PathVariable(name = ORGANIZATIONID, required = false) Long organizationId) {
        CRUDControllerConfiguration conf = controllers.htmlCrudControllerConfigurationMap.get(FRONTENDRESOURCE);
        PrivilegeBase privilege = conf.getPostRemovePrivilege();
        if (not(hasGlobalOrOrgPrivilege(privilege, organizationId))) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return Flow.init(componentProvider, objectId)
                .thenSet(frontendResourceEntity, a -> repositories.secure.frontendResource.findOne(objectId))
                .then(a -> {
                    a.model.get(frontendResourceEntity).setDraftContent(null);
                    return a.result;
                })
                .then(a -> repositories.secure.frontendResource.saveOne(a.model.get(frontendResourceEntity)))
                .execute()
                .mav(a -> true, a -> false);
    }
}
