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

import com.openkoda.controller.frontendresource.RestrictedFrontendResourceController;
import com.openkoda.core.controller.generic.AbstractController;
import com.openkoda.core.flow.Flow;
import com.openkoda.core.flow.Tuple;
import com.openkoda.core.form.CRUDControllerConfiguration;
import com.openkoda.core.helper.JsonHelper;
import com.openkoda.core.helper.ModelEnricherInterceptor;
import com.openkoda.core.security.HasSecurityRules;
import com.openkoda.form.PageBuilderForm;
import com.openkoda.model.component.FrontendResource;
import com.openkoda.model.file.File;
import jakarta.inject.Inject;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;
import jakarta.validation.Valid;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.server.RequestPath;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import org.springframework.web.servlet.view.RedirectView;
import org.thymeleaf.IEngineConfiguration;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebExpressionContext;
import org.thymeleaf.spring6.expression.ThymeleafEvaluationContext;
import org.thymeleaf.web.servlet.IServletWebExchange;
import org.thymeleaf.web.servlet.JakartaServletWebApplication;

import java.io.IOException;
import java.security.Principal;
import java.util.*;

import static com.openkoda.controller.common.URLConstants.*;
import static org.springframework.web.util.ServletRequestPathUtils.PATH_ATTRIBUTE;

@Controller
@RequestMapping({_HTML_ORGANIZATION_ORGANIZATIONID + _PAGEBUILDER, _HTML + _PAGEBUILDER})
public class PageBuilderController extends AbstractController implements HasSecurityRules {

    @Inject
    HtmlCRUDControllerConfigurationMap crudControllerConfigurationMap;

    public record EmbeddableComponents(List<Tuple> frontendResources, List<Tuple> uiComponents, Set<Map.Entry<String, CRUDControllerConfiguration>> tables, List<File> images){};

    @GetMapping({_NEW_SETTINGS})
    @ResponseBody
    @PreAuthorize(CHECK_CAN_ACCESS_GLOBAL_SETTINGS)
    public Object newPage(@PathVariable(value = ORGANIZATIONID, required = false) Long organizationId) {
        return Flow.init()
            .thenSet(embeddableComponents, a -> new EmbeddableComponents(
                    repositories.unsecure.frontendResource.findAllEmbeddableResources(),
                    repositories.unsecure.frontendResource.findAllEmbeddableUiComponents(),
                    crudControllerConfigurationMap.getExposed(),
                    repositories.unsecure.file.findByContentTypeStartsWith("image/")
            ))
            .thenSet(pageBuilderForm, a -> new PageBuilderForm(organizationId, null))
            .execute().mav("page/builder");
    }


    @GetMapping({_ID_SETTINGS})
    @ResponseBody
    @PreAuthorize(CHECK_CAN_ACCESS_GLOBAL_SETTINGS)
    public Object edit(@PathVariable(value = ORGANIZATIONID, required = false) Long organizationId,
                       @PathVariable("id") Long id) {
        return Flow.init()
            .thenSet(embeddableComponents, a -> new EmbeddableComponents(
                    repositories.unsecure.frontendResource.findAllEmbeddableResources(),
                    repositories.unsecure.frontendResource.findAllEmbeddableUiComponents(),
                    crudControllerConfigurationMap.getExposed(),
                    repositories.unsecure.file.findByContentTypeStartsWith("image/")
            ))
            .thenSet(frontendResourceEntity, a -> repositories.secure.frontendResource.findOne(id))
            .thenSet(pageBuilderForm, a -> new PageBuilderForm(organizationId, a.result))
            .execute().mav("page/builder");
    }

    @PostMapping({_NEW_SETTINGS})
    @ResponseBody
    @PreAuthorize(CHECK_CAN_ACCESS_GLOBAL_SETTINGS)
    public Object saveNew(@PathVariable(value = ORGANIZATIONID, required = false) Long organizationId,
                          @Valid PageBuilderForm form,
                          BindingResult br) {
        FrontendResource fr = Flow.init()
                .then(a -> new FrontendResource(organizationId))
                .then(a -> services.validation.validateAndPopulateToEntity(form, br, a.result))
                .thenSet(frontendResourceEntity, a -> repositories.secure.frontendResource.save(a.result))
                .thenSet(pageBuilderForm, a -> new PageBuilderForm(organizationId, a.result))
                .execute().get(frontendResourceEntity);
        return new RedirectView(_HTML + _PAGEBUILDER + "/" + fr.getId() + _SETTINGS);
    }
    @PostMapping({_ID_SETTINGS})
    @ResponseBody
    @PreAuthorize(CHECK_CAN_ACCESS_GLOBAL_SETTINGS)
    public Object save(@PathVariable(value = ORGANIZATIONID, required = false) Long organizationId,
                       @Valid PageBuilderForm form,
                       @PathVariable(value = ID, required = false) Long frontendResourceId,
                       BindingResult br) {
        Flow.init()
                .thenSet(frontendResourceEntity, a -> repositories.unsecure.frontendResource.findDashboardDefinition(frontendResourceId))
                .then(a -> services.validation.validateAndPopulateToEntity(form, br, a.result))
                .thenSet(frontendResourceEntity, a -> repositories.secure.frontendResource.save(a.result))
                .thenSet(pageBuilderForm, a -> new PageBuilderForm(organizationId, a.result))
            .execute();
        return new RedirectView(_HTML + _PAGEBUILDER + "/" + frontendResourceId + _SETTINGS);

    }


    public static class EmbeddedHttpServletRequest extends HttpServletRequestWrapper {

        /**
         * Constructs a request object wrapping the given request.
         *
         * @param request The request to wrap
         * @throws IllegalArgumentException if the request is null
         */
        public EmbeddedHttpServletRequest(HttpServletRequest request) {
            super(request);
        }

        @Override
        public String getParameter(String name) {
            if ("__view".equals(name)) {
                return "embedded";
            }
            return super.getParameter(name);
        }
    }
    public static class FakeHttpServletRequest extends HttpServletRequestWrapper {

        private final String requestURI;
        private final String queryString;
        private final String requestURL;

        public FakeHttpServletRequest(HttpServletRequest request,  String newRequestURI, String newQueryString, String newRequestURL) {
            super(request);
            this.requestURI = newRequestURI;
            this.queryString = newQueryString;
            this.requestURL = "http://localhost:8080" + newRequestURI;
        }

        @Override
        public String getRequestURI() {
            return requestURI != null ? requestURI : super.getRequestURI();
        }

        @Override
        public String getQueryString() {
            return queryString != null ? queryString : super.getQueryString();
        }

        @Override
        public StringBuffer getRequestURL() {
            return new StringBuffer(requestURL != null ? requestURL : super.getRequestURL().toString());
        }

        @Override
        public String getAuthType() {
            return super.getAuthType();
        }

        @Override
        public Cookie[] getCookies() {
            return super.getCookies();
        }

        @Override
        public long getDateHeader(String name) {
            return super.getDateHeader(name);
        }

        @Override
        public String getHeader(String name) {
            return super.getHeader(name);
        }

        @Override
        public Enumeration<String> getHeaders(String name) {
            return super.getHeaders(name);
        }

        @Override
        public Enumeration<String> getHeaderNames() {
            return super.getHeaderNames();
        }

        @Override
        public int getIntHeader(String name) {
            return super.getIntHeader(name);
        }

        @Override
        public HttpServletMapping getHttpServletMapping() {
            return super.getHttpServletMapping();
        }

        @Override
        public String getMethod() {
            return super.getMethod();
        }

        @Override
        public String getPathInfo() {
            return super.getPathInfo();
        }

        @Override
        public String getPathTranslated() {
            return super.getPathTranslated();
        }

        @Override
        public String getContextPath() {
            return super.getContextPath();
        }

        @Override
        public String getRemoteUser() {
            return super.getRemoteUser();
        }

        @Override
        public boolean isUserInRole(String role) {
            return super.isUserInRole(role);
        }

        @Override
        public Principal getUserPrincipal() {
            return super.getUserPrincipal();
        }

        @Override
        public String getRequestedSessionId() {
            return super.getRequestedSessionId();
        }

        @Override
        public String getServletPath() {
            return super.getServletPath();
        }

        @Override
        public HttpSession getSession(boolean create) {
            return super.getSession(create);
        }

        @Override
        public HttpSession getSession() {
            return super.getSession();
        }

        @Override
        public Collection<Part> getParts() throws IOException, ServletException {
            return super.getParts();
        }

        @Override
        public Part getPart(String name) throws IOException, ServletException {
            return super.getPart(name);
        }

        @Override
        public Map<String, String> getTrailerFields() {
            return super.getTrailerFields();
        }

        @Override
        public Object getAttribute(String name) {
            if (PATH_ATTRIBUTE.equals(name)) {
                return RequestPath.parse(requestURI, getContextPath());
            }
            return super.getAttribute(name);
        }
    }

    @Autowired
    private RequestMappingHandlerMapping handlerMapping;

    @Autowired
    TemplateEngine templateEngine;

    @Autowired
    ViewResolver viewResolver;

    @Autowired
    ModelEnricherInterceptor modelEnricherInterceptor;

    @Autowired
    RestrictedFrontendResourceController restrictedFrontendResourceController;

    @Autowired
    CRUDControllerHtml crudControllerHtml;

    @Autowired
    ApplicationContext context;



    @GetMapping(_ID + "/view")
    @ResponseBody
    public Object invokeUrls(@PathVariable(value = ORGANIZATIONID, required = false) Long organizationId,
                             @PathVariable("id") Long id,
                             @RequestParam(required = false, defaultValue = "", name = "obj_search") String commonSearch,
                             @RequestParam Map<String,String> requestParams,
                             HttpServletRequest request, HttpServletResponse response) {
        String dashboardName = requestParams.get("dn");
        return Flow.init()
                .thenSet(frontendResourceEntity, a ->
                        StringUtils.isBlank(dashboardName) ?
                                repositories.unsecure.frontendResource.findDashboardDefinition(id) :
                                repositories.unsecure.frontendResource.findDashboardDefinitionByName(dashboardName))
                .then(a -> {

                    List<Object> responses = JsonHelper.from(a.result.getContent(), List.class);
                    for (Object wo : responses) {
                        Map<String, Object> w = (Map<String, Object>)wo;
                        try {
                            boolean generate = Boolean.parseBoolean(w.get("generate") + "");
                            if (!generate) {
                                continue;
                            }

                            EmbeddedHttpServletRequest r = new EmbeddedHttpServletRequest(request);
                            String widgetId = w.get("id") + "";
                            String widgetName = w.get("name") + "";
                            String widgetType = w.get("type") + "";
                            Object resp = null;
                            switch (widgetType) {
                                case "webEndpoint":
                                    resp = restrictedFrontendResourceController.openFrontendResourcePage(organizationId, widgetName, null, false, requestParams, null, r, response);
                                    break;
                                case "frontendResource":
                                    Long webEndpointId = Long.parseLong(widgetId);
                                    resp = restrictedFrontendResourceController.openFrontendResourcePage(organizationId, webEndpointId, null, false, requestParams, null, r, response);
                                    break;
                                case "table":
                                    resp = crudControllerHtml.getAll(organizationId, widgetId, commonSearch, r);
                                    break;

                            }

                            if (resp instanceof ModelAndView) {
                                ModelAndView mav = (ModelAndView) resp;
                                modelEnricherInterceptor.postHandle(r, response, null, mav);
                                IServletWebExchange exchange = JakartaServletWebApplication.buildApplication(r.getServletContext()).buildExchange(r, response);
                                final IEngineConfiguration configuration = templateEngine.getConfiguration();
                                WebExpressionContext ctx = new WebExpressionContext(configuration, exchange);
                                ctx.setVariable(ThymeleafEvaluationContext.THYMELEAF_EVALUATION_CONTEXT_CONTEXT_VARIABLE_NAME,
                                        new ThymeleafEvaluationContext(context, null));

                                ctx.setVariables(mav.getModel());
                                String html = templateEngine.process(mav.getViewName(), ctx);
                                w.put("content", html);
                            } else {
                                w.put("content", "Widget is not a HTML");
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    a.model.get(frontendResourceEntity).setContent(JsonHelper.to(responses));
                    return responses;
                })

        .execute().mav("page/view");

    }

}
