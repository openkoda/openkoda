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

import com.openkoda.controller.common.PageAttributes;
import com.openkoda.core.controller.generic.AbstractController;
import com.openkoda.core.flow.Flow;
import com.openkoda.core.flow.PageAttr;
import com.openkoda.core.form.AbstractOrganizationRelatedEntityForm;
import com.openkoda.core.form.CRUDControllerConfiguration;
import com.openkoda.core.form.ReflectionBasedEntityForm;
import com.openkoda.core.security.HasSecurityRules;
import com.openkoda.core.security.OrganizationUser;
import com.openkoda.core.security.UserProvider;
import com.openkoda.model.PrivilegeBase;
import com.openkoda.model.common.SearchableOrganizationRelatedEntity;
import com.openkoda.model.file.File;
import com.openkoda.service.dynamicentity.DynamicEntityRegistrationService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

import java.io.IOException;
import java.lang.reflect.Field;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static com.openkoda.controller.common.URLConstants._HTML;
import static com.openkoda.controller.common.URLConstants._HTML_ORGANIZATION_ORGANIZATIONID;
import static com.openkoda.core.repository.common.SearchableFunctionalRepositoryWithLongId.searchSpecificationFactory;

/**
 * Controller that handles requests for generic controllers registered in {@link HtmlCRUDControllerConfigurationMap}.
 */
@RestController
@RequestMapping({_HTML_ORGANIZATION_ORGANIZATIONID + "/{obj}", _HTML + "/{obj}"})
public class CRUDControllerHtml extends AbstractController implements HasSecurityRules {

    public static final String OBJ_FILTER_PREFIX = "obj_filter_";
    @Value("${default.layout:main}")
    String defaultLayoutName;

    /** GET request that displays list of instances of entity {@link CRUDControllerConfiguration#getEntityClass()} associated with a generic controller registered under {@param objKey}.
     * The list is restricted to the result of search with the search term {@param search}
     *
     * @param organizationId
     * @param objKey - key under which the controller configuration is registered
     * @param commonSearch
     * @return
     */
    @SuppressWarnings("unchecked")
    @GetMapping(_ALL)
    //TODO Rule 1.2: All business logic delegation should be in Abstract Controller
    //TODO Rule 1.4 All methods in non-public controllers must have @PreAuthorize
    public Object getAll(
            @PathVariable(name=ORGANIZATIONID, required = false) Long organizationId,
            @PathVariable(name="obj", required=true) String objKey,
            @RequestParam(required = false, defaultValue = "", name = "obj_search") String commonSearch,
            HttpServletRequest request
            ) {
        debug("[getAll]");
        Pageable aPageable = createPageable(request, objKey);
        String search = createSearch(request, objKey);
        Tuple2<String, Map<String, String>> remainingParams = createRemainingParams(request, objKey);
        Map<String, String> objFilters = createFilters(request, objKey);


        @SuppressWarnings("rawtypes")
        CRUDControllerConfiguration conf = controllers.htmlCrudControllerConfigurationMap.getIgnoreCase(objKey);
        Set<Long> organizationIdsWithPrivilege;
        OrganizationUser user = UserProvider.getFromContext().get();
        if(user.isSuperUser()) {
            organizationIdsWithPrivilege = null;
        } else {
            organizationIdsWithPrivilege = UserProvider.getFromContext().get().getOrganizationRoles().keySet().stream()
                .filter( orgId -> hasGlobalOrOrgPrivilege(conf.getGetAllPrivilege(), orgId))
                .collect(Collectors.toSet());
            if (notValidAccess(conf.getGetAllPrivilege(), conf.getOrganizationId(), organizationId) && organizationIdsWithPrivilege.isEmpty()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
        }

        // if there is no sorting set in the request, by default sort using the first column available (ususally ID or NAME)
        if(aPageable.getSort().isUnsorted() &&  conf.getTableFormFieldNames().length > 0) {
            String sortColumn = conf.getTableFormFieldNames()[0];
            aPageable = PageRequest.of(aPageable.getPageNumber(), aPageable.getPageSize(), Sort.Direction.ASC, sortColumn);
        }

        final Pageable finalPageable = aPageable;
        Map<String, Boolean> fieldColumnVisibility = new HashMap<>();
        return Flow.init(componentProvider)
                .thenSet(searchTerm, a -> search)
                .thenSet(PageAttributes.objFilters, a -> objFilters)
                .thenSet((PageAttr<Page<SearchableOrganizationRelatedEntity>>)conf.getEntityPageAttribute(), a ->  {
                    final Long effectiveOrganizationId = (organizationId == null && organizationIdsWithPrivilege != null && organizationIdsWithPrivilege.size() == 1) ? organizationIdsWithPrivilege.iterator().next() : organizationId;
                    if(effectiveOrganizationId != null) {
                            return (Page<SearchableOrganizationRelatedEntity>) conf.getSecureRepository()
                        .search(search, effectiveOrganizationId, searchSpecificationFactory(commonSearch).and(conf.getAdditionalSpecification()), finalPageable, ReflectionBasedEntityForm.getFilterTypesAndValues(conf.getFrontendMappingDefinition(), objFilters));
                    } else {
                        return (Page<SearchableOrganizationRelatedEntity>) conf.getSecureRepository()
                            .search(search, organizationIdsWithPrivilege, searchSpecificationFactory(commonSearch).and(conf.getAdditionalSpecification()), finalPageable, ReflectionBasedEntityForm.getFilterTypesAndValues(conf.getFrontendMappingDefinition(), objFilters));
                    }
                })
                .thenSet(genericTableViewList, a -> ReflectionBasedEntityForm.calculateFieldsValuesWithReadPrivileges(conf.getFrontendMappingDefinition(), a.result.toList(), conf.getTableFormFieldNames(), fieldColumnVisibility, organizationId))
                .thenSet(genericTableViewHeaders, a -> ReflectionBasedEntityForm.getFieldsHeaders(conf.getFrontendMappingDefinition(), conf.getTableFormFieldNames(), fieldColumnVisibility, organizationId))
                .thenSet(genericTableFilters, a -> ReflectionBasedEntityForm.getFilterFields(conf.getFrontendMappingDefinition(), conf.getFilterFieldNames()))
                .thenSet(genericViewNavigationFragment, a -> conf.getNavigationFragment())
                .thenSet(isMapEntity, a -> conf.isMapEntity())
                .thenSet(frontendMappingDefinition, a -> conf.getFrontendMappingDefinition())
                .thenSet(menuItem, a -> conf.getMenuItem() != null ? conf.getMenuItem() : objKey)
                .thenSet(organizationRelatedObjectKey, a -> objKey)
                .thenSet(organizationRelatedForm, a -> conf.createNewForm())
                .thenSet(isAuditable, a -> services.customisation.isAuditableClass(conf.getEntityClass()))
                .thenSet(remainingParameters, remainingParametersMap, a -> remainingParams)
                .execute()
                .mav(conf.getTableViewWebEndpoint() != null ? conf.getTableViewWebEndpoint() : conf.getTableView());
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
        CRUDControllerConfiguration conf = controllers.htmlCrudControllerConfigurationMap.getIgnoreCase(objKey);
        if (notValidAccess(conf.getGetNewPrivilege(), conf.getOrganizationId(), organizationId)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return Flow.init(transactional)
                .thenSet(conf.getFormAttribute(), a -> conf.createNewForm(organizationId, null))
                .thenSet(genericViewNavigationFragment, a -> conf.getNavigationFragment())
                .thenSet(menuItem, a -> conf.getMenuItem() != null ? conf.getMenuItem() : objKey)
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

        CRUDControllerConfiguration conf = controllers.htmlCrudControllerConfigurationMap.getIgnoreCase(objKey);
        if (notValidAccess(conf.getGetSettingsPrivilege(), conf.getOrganizationId(), organizationId)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return Flow.init(componentProvider, objectId)
                .then(a -> conf.getSecureRepository().findOne(objectId))
                .thenSet(conf.getFormAttribute(), ac -> conf.createNewForm(organizationId, (SearchableOrganizationRelatedEntity) ac.result))
                .thenSet(genericViewNavigationFragment, a -> conf.getNavigationFragment())
                .thenSet(menuItem, a -> conf.getMenuItem() != null ? conf.getMenuItem() : objKey)
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
    @Transactional
    //TODO Rule 1.2: All business logic delegation should be in Abstract Controller
    //TODO Rule 1.4 All methods in non-public controllers must have @PreAuthorize
    public Object saveNew(
            @PathVariable(name = ORGANIZATIONID, required = false) Long organizationId,
            @PathVariable(name="obj", required=true) String objKey,
            @Valid AbstractOrganizationRelatedEntityForm form, BindingResult br) {
        debug("[saveNew]");
        CRUDControllerConfiguration conf = controllers.htmlCrudControllerConfigurationMap.getIgnoreCase(objKey);
        if (notValidAccess(conf.getPostNewPrivilege(), conf.getOrganizationId(), organizationId)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        ///
        return ((Flow<Object, AbstractOrganizationRelatedEntityForm, DefaultComponentProvider>)
                Flow.init(componentProvider, conf.getFormAttribute(), form))
                .then(a -> services.validation.validateAndPopulateToEntity(form, br, conf.createNewEntity(organizationId)))
                .then(a -> (SearchableOrganizationRelatedEntity)conf.getSecureRepository().saveOne(a.result))
                .thenSet(conf.getFormAttribute(), a -> conf.createNewForm(organizationId, a.result))
                .thenSet(organizationRelatedObjectKey, a -> objKey)
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
    @Transactional
    //TODO Rule 1.2: All business logic delegation should be in Abstract Controller
    //TODO Rule 1.4 All methods in non-public controllers must have @PreAuthorize
    public Object save(
            @PathVariable(ID) Long objectId,
            @PathVariable(name = ORGANIZATIONID, required = false) Long organizationId,
            @PathVariable(name="obj", required=true) String objKey,
            @Valid AbstractOrganizationRelatedEntityForm form, BindingResult br) {
        debug("[saveNew]");
        CRUDControllerConfiguration conf = controllers.htmlCrudControllerConfigurationMap.getIgnoreCase(objKey);
        if (notValidAccess(conf.getPostSavePrivilege(), conf.getOrganizationId(), organizationId)) {
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
    @Transactional
    //TODO Rule 1.2: All business logic delegation should be in Abstract Controller
    //TODO Rule 1.4 All methods in non-public controllers must have @PreAuthorize
    public Object remove(
            @PathVariable(name=ID) Long objectId,
            @PathVariable(name = ORGANIZATIONID, required = false) Long organizationId,
            @PathVariable(name="obj", required=true) String objKey) {
        CRUDControllerConfiguration conf = controllers.htmlCrudControllerConfigurationMap.getIgnoreCase(objKey);
        if (notValidAccess(conf.getPostRemovePrivilege(), conf.getOrganizationId(), organizationId)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return Flow.init(componentProvider, objectId)
                .then(a -> conf.getSecureRepository().deleteOne(objectId))
                .execute()
                .mav(a -> true, a -> false);
    }

    @GetMapping(_ID + _VIEW)
    //TODO Rule 1.2: All business logic delegation should be in Abstract Controller
    //TODO Rule 1.4 All methods in non-public controllers must have @PreAuthorize
    public Object view(
            @PathVariable(name = ID) Long objectId,
            @PathVariable(name = ORGANIZATIONID, required = false) Long organizationId,
            @PathVariable(name="obj", required=true) String objKey) {

        CRUDControllerConfiguration conf = controllers.htmlCrudControllerConfigurationMap.getIgnoreCase(objKey);
        if (notValidAccess(conf.getGetSettingsPrivilege(), conf.getOrganizationId(), organizationId)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return Flow.init(componentProvider, objectId)
                .thenSet(organizationRelatedObjectKey, a -> objKey)
                .thenSet(organizationRelatedEntity, a -> (SearchableOrganizationRelatedEntity) conf.getSecureRepository().findOne(objectId))
                .thenSet(organizationRelatedObjectMap, a -> convertUsingReflection(a.result))
                .thenSet(genericViewNavigationFragment, a -> conf.getNavigationFragment())
                .thenSet(menuItem, a -> conf.getMenuItem() != null ? conf.getMenuItem() : objKey)
                .execute()
                .mav(conf.getReadView());
    }

    @Transactional(readOnly = true)
    @GetMapping(_REPORT + _CSV)
    public void getCsvReport(
            @PathVariable(name=ORGANIZATIONID, required = false) Long organizationId,
            @PathVariable(name="obj", required=true) String objKey,
            @RequestParam(required = false, defaultValue = "", name = "obj_search") String search,
            @RequestParam(required = false) Map<String, String> filters,
            HttpServletResponse response
    ) throws SQLException, IOException {
        debug("[getCsvReport]");
        Map<String, String> objFilters = filters.entrySet().stream().filter(entry -> entry.getKey().startsWith(OBJ_FILTER_PREFIX) && StringUtils.isNotEmpty(entry.getValue()))
                .collect(Collectors.toMap(entry -> StringUtils.substringAfter(entry.getKey(), OBJ_FILTER_PREFIX), Map.Entry::getValue));

        CRUDControllerConfiguration conf = controllers.htmlCrudControllerConfigurationMap.getIgnoreCase(objKey);
        if (notValidAccess(conf.getGetAllPrivilege(), conf.getOrganizationId(), organizationId)) {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
        }
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm");
        Map<String, Boolean> fieldColumnVisibility = new HashMap<>();
        File report = Flow.init(componentProvider)
                .thenSet(genericTableHeaders, a -> conf.getReportFormFieldNames())
                .then(a -> (List<SearchableOrganizationRelatedEntity>) conf.getSecureRepository()
                        .search(search, organizationId, conf.getAdditionalSpecification(), ReflectionBasedEntityForm.getFilterTypesAndValues(conf.getFrontendMappingDefinition(), objFilters)))
                .thenSet(genericTableViewList, a -> ReflectionBasedEntityForm.calculateFieldsValuesWithReadPrivileges(conf.getFrontendMappingDefinition(), a.result, a.model.get(genericTableHeaders), fieldColumnVisibility, organizationId))
                .thenSet(genericTableViewHeaders, a -> ReflectionBasedEntityForm.getFieldsHeaders(conf.getFrontendMappingDefinition(), a.model.get(genericTableHeaders), fieldColumnVisibility, organizationId))
                .thenSet(file, a -> {
                    try {
                        return services.csv.createCSV(String.format("%s_%s.csv", objKey, dtf.format(LocalDateTime.now())), a.model.get(genericTableViewList), a.model.get(genericTableHeaders));
                    } catch (IOException | SQLException e) {
                        error("[getCsvReport]", e);
                        return null;
                    }
                })
                .execute()
                .get(file);
        services.file.getFileContentAndPrepareResponse(report, true, false, response);
    }

    private boolean notValidAccess(PrivilegeBase privilege, Long confOrganizationId, Long organizationId){
        return !hasGlobalOrOrgPrivilege(privilege, organizationId);
    }

    private Map<String, Object> convertUsingReflection(Object object) {
        Map<String, Object> map = new HashMap<>();
        Field[] fields = object.getClass().getDeclaredFields();

        try {
            for (Field field: fields) {
                if(!field.getType().getName().startsWith(DynamicEntityRegistrationService.PACKAGE)) {
                    field.setAccessible(true);
                    map.put(field.getName(), field.get(object));
                }
            }
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }

        return map;
    }

    public static String createSearch(HttpServletRequest request, String qualifier) {
        String result = request.getParameter(qualifier + "_search");
        if(result == null) {
            Optional<Map.Entry<String, String[]>> searches = request.getParameterMap().entrySet().stream().filter( e -> StringUtils.equalsAnyIgnoreCase(qualifier  + "_search", e.getKey())).findFirst();
            if(searches != null && searches.isPresent() && searches.get().getValue().length > 0) {
                return searches.get().getValue()[searches.get().getValue().length - 1];
            }
        } else {
            return result;
        }
        
        return "";
    }

    public static Tuple2<String, Map<String, String>> createRemainingParams(HttpServletRequest request, String qualifier) {
        StringBuilder result = new StringBuilder();
        Map<String, String> resultMap = new HashMap<>();
        String prefix = qualifier + "_";
        Enumeration<String> n = request.getParameterNames();
        while (n.hasMoreElements()) {
            String k = n.nextElement();
            if (StringUtils.startsWithIgnoreCase(k, prefix)) { continue; }
            String[] vals = request.getParameterValues(k);
            String v = vals != null ? vals[vals.length - 1] : null;
            resultMap.put(k, v);
            result.append('&');
            result.append(k);
            result.append('=');
            result.append(v);
        }
        return Tuples.of(result.toString(), resultMap);
    }

    public static Map<String, String> createFilters(HttpServletRequest request, String qualifier) {
        Map<String, String> result = new HashMap<>();
        String prefix = qualifier + "_filter_";
        Enumeration<String> n = request.getParameterNames();
        while (n.hasMoreElements()) {
            String s = n.nextElement();
            if (s.toLowerCase().startsWith(prefix)) {
                String v = request.getParameter(s);
                if (StringUtils.isNotBlank(v)) {
                    result.put(s.substring(prefix.length()), request.getParameter(s));
                }
            }
        }

        return result;
    }

    public static Pageable createPageable(HttpServletRequest request, String qualifier) {
        int page = NumberUtils.isParsable(request.getParameter(qualifier + "_page")) ? Integer.parseInt(request.getParameter(qualifier + "_page")) : 0;
        int size = NumberUtils.isParsable(request.getParameter(qualifier + "_size")) ? Integer.parseInt(request.getParameter(qualifier + "_size")) : 10;
        String sortParam = request.getParameter(qualifier + "_sort");

        if (sortParam != null && !sortParam.isEmpty()) {
            String[] sortParams = sortParam.split(",");
            Sort sort;
            if (sortParams.length > 1) {
                Sort.Direction direction = Sort.Direction.fromString(sortParams[1]);
                sort = Sort.by(direction, sortParams[0]);
            } else {
                sort = Sort.by(sortParams[0]);
            }
            return PageRequest.of(page, size, sort);
        } else {
            return PageRequest.of(page, size);
        }
    }
}
