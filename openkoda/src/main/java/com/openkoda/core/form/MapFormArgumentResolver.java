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

package com.openkoda.core.form;

import com.openkoda.controller.HtmlCRUDControllerConfigurationMap;
import com.openkoda.core.customisation.FrontendMapping;
import com.openkoda.core.customisation.FrontendMappingMap;
import com.openkoda.core.helper.ReadableCode;
import com.openkoda.core.helper.UrlHelper;
import com.openkoda.core.multitenancy.TenantResolver;
import com.openkoda.core.security.HasSecurityRules;
import com.openkoda.model.common.SearchableOrganizationRelatedEntity;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.PropertyValues;
import org.springframework.core.MethodParameter;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.ServletRequestParameterPropertyValues;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.util.Map;

public class MapFormArgumentResolver implements HandlerMethodArgumentResolver, HasSecurityRules, ReadableCode {

    private final HtmlCRUDControllerConfigurationMap htmlCRUDControllerConfigurationMap;
    private final FrontendMappingMap frontendMappingMap;
    private final UrlHelper urlHelper;

    public MapFormArgumentResolver(HtmlCRUDControllerConfigurationMap htmlCRUDControllerConfigurationMap, FrontendMappingMap frontendMappingMap, UrlHelper urlHelper) {
        this.htmlCRUDControllerConfigurationMap = htmlCRUDControllerConfigurationMap;
        this.frontendMappingMap = frontendMappingMap;
        this.urlHelper = urlHelper;
    }

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return AbstractOrganizationRelatedEntityForm.class.equals(parameter.getParameterType());
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        String entityKey = urlHelper.getEntityKeyOrNull((HttpServletRequest) webRequest.getNativeRequest());

        AbstractOrganizationRelatedEntityForm form = null;
        if(entityKey != null) {
            CRUDControllerConfiguration conf = htmlCRUDControllerConfigurationMap.get(entityKey);
            if (conf != null) {
                form = conf.createNewForm();
                bindForm(mavContainer, webRequest, binderFactory, form);
                return form;
            }
        }
        String mappingDefinitionName = webRequest.getParameter("frontendMappingDefinition");
        if (StringUtils.isNotBlank(mappingDefinitionName)) {
            FrontendMapping frontendMapping = frontendMappingMap.get(mappingDefinitionName);
            if (frontendMapping != null) {
                CRUDControllerConfiguration conf = CRUDControllerConfiguration.getBuilder("form", frontendMapping.definition(), frontendMapping.repository(), ReflectionBasedEntityForm.class);
                Long orgId = TenantResolver.getTenantedResource().organizationId;
                SearchableOrganizationRelatedEntity entity = conf.createNewEntity(orgId);
                form = conf.createNewForm(orgId, entity);
                bindForm(mavContainer, webRequest, binderFactory, form);
                return form;
            }
        }
        return null;

//        throw new RuntimeException("Cannot resolve AbstractOrganizationRelatedEntityForm for url " + webRequest.getContextPath());
    }

    private static void bindForm(ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory, AbstractOrganizationRelatedEntityForm form) throws Exception {
        ServletWebRequest swr = (ServletWebRequest) webRequest;
        PropertyValues pv = new ServletRequestParameterPropertyValues(swr.getRequest());
        WebDataBinder pn = binderFactory.createBinder(webRequest, form, "form");
        pn.bind(pv);

        BindingResult br = pn.getBindingResult();
        form.setBindingResult(br);
        Map<String, Object> bindingResultModel = br.getModel();
        mavContainer.removeAttributes(bindingResultModel);
        mavContainer.addAllAttributes(bindingResultModel);
    }
}