/*
MIT License

Copyright (c) 2016-2022, Codedose CDX Sp. z o.o. Sp. K. <stratoflow.com>

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

import com.openkoda.controller.common.PageAttributes;
import com.openkoda.core.controller.generic.AbstractController;
import com.openkoda.core.flow.BasePageAttributes;
import com.openkoda.core.flow.Flow;
import com.openkoda.core.flow.PageAttr;
import com.openkoda.core.flow.PageModelMap;
import com.openkoda.core.form.AbstractOrganizationRelatedEntityForm;
import com.openkoda.core.helper.JsonHelper;
import com.openkoda.core.security.HasSecurityRules;
import com.openkoda.core.security.UserProvider;
import com.openkoda.core.service.FrontendResourceService;
import com.openkoda.dto.system.CmsDto;
import com.openkoda.form.FrontendResourceForm;
import com.openkoda.form.FrontendResourcePageForm;
import com.openkoda.form.RegisterUserForm;
import com.openkoda.model.ControllerEndpoint;
import com.openkoda.model.FrontendResource;
import com.openkoda.model.file.File;
import com.openkoda.repository.FrontendResourceRepository;
import com.openkoda.uicomponent.JsFlowRunner;
import jakarta.inject.Inject;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Map;

import static com.openkoda.core.service.FrontendResourceService.frontendResourceTemplateNamePrefix;


/**
 * Controller provides {@link FrontendResource} related functionalities considering its management and CRUD
 * See also {@link FrontendResourceControllerHtml}
 *
 * @author Martyna Litkowska (mlitkowska@stratoflow.com)
 * @since 2019-02-20
 */
public class AbstractFrontendResourceController extends AbstractController implements HasSecurityRules {

    @Inject
    private JsFlowRunner jsFlowRunner;

    /**
     * Validates the data and updates the content of {@link FrontendResource} which ID is equal to the one provided  .
     * See also {@link FrontendResourceService}, {@link com.openkoda.repository.FrontendResourceRepository}
     *
     * @param content code String
     * @param frontendResourceId
     * @return com.openkoda.core.flow.PageModelMap
     */
    protected PageModelMap updateFrontendResource(String content, Long frontendResourceId){
        return Flow.init(transactional)
                .then(a -> services.frontendResource.validateContent(content, null, "dto.content"))
                .then(a -> repositories.unsecure.frontendResource.updateContent(frontendResourceId, content))
                .then(a -> repositories.unsecure.frontendResource.evictOne(frontendResourceId))
                .execute();
    }

    /**
     * Retrieves {@link FrontendResource} from the database having the ID equal to frontendResourceId and prepares {@link FrontendResourceForm}.
     * See also {@link com.openkoda.repository.FrontendResourceRepository}
     *
     * @param organizationId
     * @param frontendResourceId
     * @return com.openkoda.core.flow.PageModelMap
     */
    protected PageModelMap findFrontendResource(Long organizationId, long frontendResourceId) {
        debug("[findFrontendResource] FrontendResourceId: {}", frontendResourceId);
        return Flow.init(frontendResourceId)
                .thenSet(frontendResourceEntity, a -> repositories.unsecure.frontendResource.findOne(frontendResourceId))
                .thenSet(frontendResourceForm, a -> new FrontendResourceForm(organizationId, a.model.get(frontendResourceEntity)))
                .execute();
    }

    /**
     * Publishes the {@link FrontendResource} having the ID provided.
     * Meaning that it replaces the actual live content of a resource with the draft content.
     * See also {@link FrontendResourceService}, {@link com.openkoda.repository.FrontendResourceRepository}
     *
     * @param frontendResourceId
     * @return com.openkoda.core.flow.PageModelMap
     */
    protected PageModelMap publishFrontendResource(long frontendResourceId) {
        debug("[publishFrontendResource] FrontendResourceId: {}", frontendResourceId);
        return Flow.init()
                .then(a -> repositories.unsecure.frontendResource.findOne(frontendResourceId))
                .then(a -> services.frontendResource.publish(a.result))
                .thenSet(frontendResourceEntity, a -> repositories.unsecure.frontendResource.save(a.result))
                .execute();
    }


    /**
     * Publishes all {@link FrontendResource} available in the database.
     * Meaning it replaces the actual live content of a resource with the draft content for each {@link FrontendResource} found in the database which is a draft.
     * See also {@link FrontendResourceService}, {@link com.openkoda.repository.FrontendResourceRepository}
     *
     * @return com.openkoda.core.flow.PageModelMap
     */
    protected PageModelMap publishAllFrontendResource() {
        debug("[publishAllFrontendResource]");
        return Flow.init(transactional)
                .then(a -> repositories.unsecure.frontendResource.findAllAsStreamByIsDraftTrue())
                .then(a -> services.frontendResource.publishAll(a.result))
                .execute();
    }


    /**
     * Clears the content of the {@link FrontendResource} with the given ID, keeping it as a draft, and loads the content directly from app resources or get the default.
     * See also {@link FrontendResourceService}, {@link com.openkoda.repository.FrontendResourceRepository}
     *
     * @param frontendResourceId
     * @return com.openkoda.core.flow.PageModelMap
     */
    protected PageModelMap reloadFrontendResource(long frontendResourceId) {
        debug("[reloadFrontendResource] FrontendResourceId: {}", frontendResourceId);
        return Flow.init()
                .then(a -> repositories.unsecure.frontendResource.findOne(frontendResourceId))
                .then(a -> services.frontendResource.clear(a.result))
                .then(a -> {
                    a.result.setContent(services.frontendResource.getContentOrDefault(a.result.getType(), a.result.getName()));
                    return a.result;
                })
                .then(a -> repositories.unsecure.frontendResource.save(a.result))
                .execute();
    }

    /**
     * Sets the draft content of {@link FrontendResource} by loading it directly from app resources or getting the default.
     * See also {@link FrontendResourceService}, {@link com.openkoda.repository.FrontendResourceRepository}
     *
     * @param frontendResourceId
     * @return com.openkoda.core.flow.PageModelMap
     */
    protected PageModelMap reloadFrontendResourceToDraft(long frontendResourceId) {
        debug("[loadFrontendResourceToDraft] FrontendResourceId: {}", frontendResourceId);
        return Flow.init()
                .then(a -> repositories.unsecure.frontendResource.findOne(frontendResourceId))
                .then(a -> {
                    a.result.setDraftContent(services.frontendResource.getContentOrDefault(a.result.getType(), a.result.getName()));
                    return a.result;
                })
                .then(a -> repositories.unsecure.frontendResource.save(a.result))
                .execute();
    }

    /**
     * Validates data provided for the new {@link FrontendResource} entry and if successful saves the new {@link FrontendResource} in the database.
     * See also {@link FrontendResourceService}, {@link com.openkoda.repository.FrontendResourceRepository}, {@link com.openkoda.core.service.ValidationService}
     *
     * @param frontendResourceFormData
     * @param br
     * @return com.openkoda.core.flow.PageModelMap
     */
    protected PageModelMap createFrontendResource(FrontendResourceForm frontendResourceFormData, BindingResult br) {
        debug("[createFrontendResource]");
        return Flow.init(frontendResourceForm, frontendResourceFormData)
                .then(a -> services.frontendResource.checkNameExists(((CmsDto) frontendResourceFormData.dto).name, br))
                .then(a -> services.frontendResource.validateContent(((CmsDto) frontendResourceFormData.dto).content, br, "dto.content"))
                .then(a -> services.validation.validateAndPopulateToEntity(frontendResourceFormData, br, new FrontendResource()))
                .then(a -> repositories.unsecure.frontendResource.save(((FrontendResource)a.result)))
                .thenSet(frontendResourceForm, a -> new FrontendResourceForm())
                .execute();
    }

    /**
     * Prepares an empty {@link FrontendResourceForm}
     *
     * @param organizationId
     * @param type
     * @return com.openkoda.core.flow.PageModelMap
     */
    protected PageModelMap getFrontendResourceForm(Long organizationId, FrontendResource.Type type) {
        debug("[createFrontendResource]");
        return Flow.init()
                .thenSet(organizationRelatedForm, a ->new FrontendResourceForm())
                .execute();
    }

    /**
     * Finds the requested {@link FrontendResource} and prepares model for its display.
     * For {@link FrontendResource} with {@link FrontendResource.Type} equal to {@link FrontendResource.Type.UI_COMPONENT} it processes
     * any {@link ControllerEndpoint} assigned to this entity which match the subPath and the {@link com.openkoda.model.ControllerEndpoint.HttpMethod} requested.
     * See also {@link com.openkoda.repository.FrontendResourceRepository}, {@link com.openkoda.repository.ControllerEndpointRepository}
     *
     * @param organizationId
     * @param frontendResourcePath url path of the {@link FrontendResource}
     * @param subPath              sub path for the resource being invoked
     * @param httpServletRequest   {@link HttpServletRequest}
     * @param addRegisterForm      should model containt the {@link RegisterUserForm} object
     * @param isPublic             is the requested resource public
     * @param preview              is this triggered in the preview mode
     * @param requestParams        params retrieved from {@link HttpServletRequest}
     * @param form
     * @return {@link ModelAndView} or {@link ResponseEntity}
     */
    protected Object invokeFrontendResourceEntry(Long organizationId,
                                                 String frontendResourcePath,
                                                 String subPath,
                                                 HttpServletRequest httpServletRequest,
                                                 boolean addRegisterForm,
                                                 boolean isPublic,
                                                 boolean preview,
                                                 Map<String,String> requestParams,
                                                 AbstractOrganizationRelatedEntityForm form) {
        debug("[invokeFrontendResourceEntry] FrontendResourcePath: {}", frontendResourcePath);
        FrontendResourceRepository fr = repositories.unsecure.frontendResource;
        FrontendResource frontendResource = null;
        if (isPublic) {
            frontendResource = fr.findByUrlPathAndIsPublic(frontendResourcePath, isPublic);
        } else if ( organizationId != null ) {
            frontendResource = fr.findNonPublicByUrlPathAndOrganizationId(frontendResourcePath, organizationId);
            if (frontendResource == null) {
                frontendResource = fr.findNonPublicByUrlPathAndOrganizationIdIsNull(frontendResourcePath);
            }
        } else {
            frontendResource = fr.findNonPublicByUrlPathAndOrganizationIdIsNull(frontendResourcePath);
        }

        ModelAndView mav = new ModelAndView();
        if(addRegisterForm) {
            mav.addObject("registerForm", new RegisterUserForm());
        }
        if (frontendResource != null) {
            mav.setViewName(FrontendResourceService.frontendResourceTemplateNamePrefix + frontendResource.getName());
            if(frontendResource.getType().equals(FrontendResource.Type.UI_COMPONENT)) {
                ControllerEndpoint.HttpMethod httpMethod = ControllerEndpoint.HttpMethod.valueOf(httpServletRequest.getMethod());
                ControllerEndpoint controllerEndpoint = repositories.unsecure.controllerEndpoint.findByFrontendResourceIdAndSubPathAndHttpMethod(
                    frontendResource.getId(),
                    subPath,
                    httpMethod);
                if(controllerEndpoint != null) {
                    return evaluateControllerEndpoint(organizationId, frontendResourcePath, subPath, httpServletRequest,
                            isPublic, mav, frontendResource, httpMethod, controllerEndpoint, preview, requestParams, form);
                }
            }
        } else {
            debug("[invokeFrontendResourceEntry] FrontendResourceEntry not found in db: {}", frontendResourcePath);
            mav.setViewName(frontendResourceTemplateNamePrefix + frontendResourcePath);
        }
        return mav;
    }

    /**
     * Evaluates the {@link ControllerEndpoint} JS flow and prepares the result response.
     * See also {@link com.openkoda.repository.FrontendResourceRepository}, {@link com.openkoda.repository.ControllerEndpointRepository}, {@link JsFlowRunner}
     *
     * @param organizationId
     * @param frontendResourcePath url path of the {@link FrontendResource}
     * @param subPath sub path for the resource being invoked
     * @param httpServletRequest {@link HttpServletRequest}
     * @param isPublic is the requested resource public
     * @param mav {@link ModelAndView}
     * @param frontendResource {@link FrontendResource}
     * @param httpMethod {@link ControllerEndpoint.HttpMethod}
     * @param controllerEndpoint {@link ControllerEndpoint}
     * @param preview is this triggered in the preview mode
     * @param requestParams params retrieved from {@link HttpServletRequest}
     * @return {@link ModelAndView} or {@link ResponseEntity}
     */
    private Object evaluateControllerEndpoint(Long organizationId,
                                              String frontendResourcePath,
                                              String subPath,
                                              HttpServletRequest httpServletRequest,
                                              boolean isPublic,
                                              ModelAndView mav,
                                              FrontendResource frontendResource,
                                              ControllerEndpoint.HttpMethod httpMethod,
                                              ControllerEndpoint controllerEndpoint,
                                              boolean preview,
                                              Map<String,String> requestParams,
                                              AbstractOrganizationRelatedEntityForm form) {
        if(controllerEndpoint.getResponseType().equals(ControllerEndpoint.ResponseType.HTML)){
//                        display frontend resource normally and run if available
            debug("[determineFrontendResourceEntry] Run ControllerEndpoint Flow Id {} for HTTP Method {}",
                    controllerEndpoint.getId(), httpServletRequest.getMethod());
            long userId = UserProvider.getUserIdOrNotExistingId();
            PageModelMap pageModelMap = preview
                    ? jsFlowRunner.runPreviewFlow(controllerEndpoint.getCode(), requestParams, organizationId, userId, form)
                    : jsFlowRunner.runLiveFlow(controllerEndpoint.getCode(), requestParams, organizationId, userId, form);
            mav.getModelMap().putAll(pageModelMap);
            boolean isError = Boolean.TRUE.equals(pageModelMap.get(BasePageAttributes.isError));
            if (form == null) {
                if (isError) {
                    mav.setViewName(WEBENDPOINTS + "-" + SETTINGS + "::" + PREVIEW + "-error");
                }
            } else {
                mav.getModelMap().put("organizationRelatedForm", form);
                if (isError) {
                    mav.setViewName("generic-settings-entity-form::generic-settings-form-error");
                } else {
                    mav.setViewName("generic-settings-entity-form::generic-settings-form-reload");
                }
            }
            return mav;
        } else {
//                        get controller endpoint result
            PageModelMap pageModelMap = getControllerEndpointResult(organizationId, frontendResourcePath, subPath, isPublic, frontendResource, controllerEndpoint, httpMethod, preview, requestParams, form);
            return new ResponseEntity(pageModelMap.get(PageAttributes.controllerEndpointResult), pageModelMap.get(httpHeaders), HttpStatus.OK);
        }
    }


    /**
     * Retrieves {@link FrontendResource} of type {@link FrontendResource.Type.PAGE} with the given ID and prepares the {@link FrontendResourcePageForm}.
     * See also {@link FrontendResourceService}
     *
     * @param organizationId
     * @param frontendResourceId
     * @return com.openkoda.core.flow.PageModelMap
     */
    // Frontend Resource Pages
    protected PageModelMap findFrontendResourcePage(Long organizationId, Long frontendResourceId) {
        debug("[findFrontendResourcePage] FrontendResourceId: {}", frontendResourceId);
        Pageable page = PageRequest.of(0, 100);
        return Flow.init(isPageEditor, true)
                .thenSet(frontendResourceEntity, a -> services.frontendResource.prepareFrontendResourcePageEntity(frontendResourceId))
                .thenSet(frontendResourcePageForm, a -> new FrontendResourcePageForm(organizationId, a.result))
                .execute();
    }

    /**
     * Validates the data of a {@link FrontendResource} of type {@link FrontendResource.Type.PAGE} and updates the record in the database having the given ID
     * See also {@link FrontendResourceService}, {@link com.openkoda.repository.FrontendResourceRepository}, {@link com.openkoda.core.service.ValidationService}
     *
     * @param frontendResourceForm
     * @param frontendResourceId
     * @param br
     * @return com.openkoda.core.flow.PageModelMap
     */
    protected PageModelMap updateFrontendResourcePage(FrontendResourcePageForm frontendResourceForm, Long frontendResourceId, BindingResult br) {
        debug("[createFrontendResource]");
        return Flow.init(transactional)
                .thenSet(frontendResourcePageForm, a -> frontendResourceForm)
                .then(a -> services.frontendResource.checkNameExists(frontendResourceForm.dto.name, br))
                .then(a -> services.frontendResource.prepareFrontendResourcePage(frontendResourceId, frontendResourceForm.dto.contentEditable))
                .then(a -> services.validation.validateAndPopulateToEntity(frontendResourceForm, br, a.result))
                .thenSet(frontendResourceEntity, a -> repositories.unsecure.frontendResource.save(a.result))
                .execute();
    }

    /**
     * Prepares the response object for the evaluation result of {@link ControllerEndpoint} JS flow.
     * It applies only for {@link ControllerEndpoint.ResponseType} values {@link ControllerEndpoint.ResponseType.MODEL_AS_JSON}
     * and {@link ControllerEndpoint.ResponseType.FILE}
     * See also {@link JsFlowRunner}
     *
     * @param organizationId
     * @param frontendResourceUrl url path of the {@link FrontendResource}
     * @param subPath sub path for the resource being invoked
     * @param isPublic is the requested resource public
     * @param frontendResource {@link FrontendResource}
     * @param cEndpoint {@link ControllerEndpoint}
     * @param httpMethod {@link ControllerEndpoint.HttpMethod}
     * @param preview is this triggered in the preview mode
     * @param requestParams params retrieved from {@link HttpServletRequest}
     * @return com.openkoda.core.flow.PageModelMap
     */
    protected PageModelMap getControllerEndpointResult(Long organizationId,
                                                       String frontendResourceUrl,
                                                       String subPath,
                                                       boolean isPublic,
                                                       FrontendResource frontendResource,
                                                       ControllerEndpoint cEndpoint,
                                                       ControllerEndpoint.HttpMethod httpMethod,
                                                       boolean preview,
                                                       Map<String,String> requestParams,
                                                       AbstractOrganizationRelatedEntityForm form) {
        debug("[getControllerEndpointResult] organizationId: {} frontendResourceUrl: {} subPath: {}", organizationId, frontendResourceUrl, subPath);
        long userId = UserProvider.getUserIdOrNotExistingId();
        return Flow.init()
                .thenSet(controllerEndpoint, a -> cEndpoint)
                .thenSet(frontendResourceEntity, a -> frontendResource)
                .thenSet(uiComponentModel, a -> a.result != null ? (preview ? jsFlowRunner.runPreviewFlow(cEndpoint.getCode(), requestParams, organizationId, userId, form) : jsFlowRunner.runLiveFlow(cEndpoint.getCode(), requestParams, organizationId, userId, form)) : new PageModelMap())
                .thenSet(httpHeaders, a -> setupHttpHeaders(cEndpoint))
                .thenSet(controllerEndpointResult, a -> {
                    switch (cEndpoint.getResponseType()) {
                        case MODEL_AS_JSON -> {
                            return getModelAsJsonResult(a.model.get(uiComponentModel), a.model.get(httpHeaders), cEndpoint);
                        }
                        case FILE -> {
                            InputStreamResource file = getFileResult(a.model.get(uiComponentModel), a.model.get(httpHeaders), cEndpoint);
                            if (file != null) return file;
                        }
                    }
                    return null;
                })
                .execute();
    }

    private HttpHeaders setupHttpHeaders(ControllerEndpoint controllerEndpoint) {
        debug("[setupHttpHeaders]");
        HttpHeaders responseHeaders = new HttpHeaders();
        for (Map.Entry<String, String> httpHeader : controllerEndpoint.getHttpHeadersMap().entrySet()) {
            responseHeaders.add(httpHeader.getKey(), httpHeader.getValue());
        }
        return responseHeaders;
    }

    /**
     * Converts {@link PageModelMap} result model to JSON String.
     *
     * @param resultModel
     * @param httpHeaders
     * @param controllerEndpoint
     * @return String
     */
    private String getModelAsJsonResult(PageModelMap resultModel, HttpHeaders httpHeaders, ControllerEndpoint controllerEndpoint) {
        debug("[getModelAsJsonResult]");
        httpHeaders.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_UTF8_VALUE);
        return JsonHelper.to(StringUtils.isNotBlank(controllerEndpoint.getModelAttributes()) ?
                resultModel.getAsMap(Arrays.stream(controllerEndpoint.getPageAttributesNames())
                        .map(attr -> PageAttr.getByName(attr) != null ?
                                PageAttr.getByName(attr) : new PageAttr(attr)).toArray(PageAttr[]::new))
                : resultModel.getAsMap(isError));
    }

    private InputStreamResource getFileResult(PageModelMap resultModel, HttpHeaders httpHeaders, ControllerEndpoint controllerEndpoint) {
        debug("[getFileResult]");
        File file = (File) (resultModel.get(controllerEndpoint.getPageAttributesNames()[0]));
        setupFileResultHeaders(httpHeaders, file);
        try {
            return new InputStreamResource(file.getContentStream());
        } catch (IOException | SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Sets {@link HttpHeaders} for File type {@link ControllerEndpoint}
     *
     * @param httpHeaders
     * @param file
     */
    private void setupFileResultHeaders(HttpHeaders httpHeaders, File file) {
        debug("[setupFileResultHeaders]");
        if(!httpHeaders.containsKey(HttpHeaders.CONTENT_TYPE)){
            httpHeaders.add(HttpHeaders.CONTENT_TYPE,
                    file.getContentType() != null ? file.getContentType() : MediaType.IMAGE_PNG_VALUE);
        }
        if(!httpHeaders.containsKey(HttpHeaders.ACCEPT_RANGES)){
            httpHeaders.add(HttpHeaders.ACCEPT_RANGES, "bytes");
        }
        if(!httpHeaders.containsKey(HttpHeaders.CACHE_CONTROL)){
            httpHeaders.add(HttpHeaders.CACHE_CONTROL, "max-age=604800, public");
        }
        if(!httpHeaders.containsKey(HttpHeaders.CONTENT_DISPOSITION)){
            httpHeaders.add(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + file.getFilename() + "\"");
        }
    }

}
