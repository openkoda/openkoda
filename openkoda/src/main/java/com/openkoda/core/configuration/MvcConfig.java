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

package com.openkoda.core.configuration;

import com.openkoda.controller.HtmlCRUDControllerConfigurationMap;
import com.openkoda.controller.common.SessionData;
import com.openkoda.controller.common.URLConstants;
import com.openkoda.core.customisation.FrontendMappingMap;
import com.openkoda.core.exception.ErrorLoggingExceptionResolver;
import com.openkoda.core.form.MapFormArgumentResolver;
import com.openkoda.core.helper.ModulesInterceptor;
import com.openkoda.core.helper.SlashEndingUrlInterceptor;
import com.openkoda.core.helper.UrlHelper;
import com.openkoda.core.multitenancy.QueryExecutor;
import com.openkoda.core.service.FrontendResourceService;
import com.openkoda.model.MutableUserInOrganization;
import com.openkoda.service.export.ClasspathComponentImportService;
import jakarta.inject.Inject;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.*;
import org.springframework.core.convert.ConversionService;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.http.CacheControl;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.*;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import org.thymeleaf.templateresolver.StringTemplateResolver;

import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import static com.openkoda.core.service.FrontendResourceService.frontendResourceTemplateNamePrefix;

/**
 * <p>Configuration class for MVC configuration, ie. routes configuration, interceptors, template resolving, etc.</p>
 */
@Configuration
@EnableSpringDataWebSupport
@Import(SwaggerConfig.class)
public class MvcConfig implements URLConstants, WebMvcConfigurer  {

    public static final String USER_IN_ORG = "userInOrganization";


    @Value("${user.agent.excluded.from.error.log:}")
    String userAgentExcludedFromErrorLog;

    @Value("${frontendresource.load.always.from.resources:false}")
    boolean frontendResourceLoadAlwaysFromResources;

    @Value("${frontendresource.create.if.not.exist:false}")
    boolean frontendResourceCreateIfNotExist;

    @Value("${default.pages.homeview:home}")
    String homeViewName;

    @Inject
    private HandlerInterceptor modelEnricherInterceptor;

    @Inject
    private ModulesInterceptor modulesInterceptor;

    @Inject
    private SlashEndingUrlInterceptor slashEndingUrlInterceptor;

    @Inject
    public ClasspathComponentImportService classpathComponentImportService;

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/").setViewName( frontendResourceTemplateNamePrefix + homeViewName);
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/vendor/**").addResourceLocations("classpath:/public/vendor/").setCacheControl(CacheControl.maxAge(7, TimeUnit.DAYS).cachePublic());
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(modelEnricherInterceptor);
        registry.addInterceptor(modulesInterceptor);
        registry.addInterceptor(slashEndingUrlInterceptor);
    }

    @Override
    public void configureHandlerExceptionResolvers(List<HandlerExceptionResolver> exceptionResolvers) {
        exceptionResolvers.add(new ErrorLoggingExceptionResolver(userAgentExcludedFromErrorLog));
    }

    @Bean
    @Scope("request")
    public MutableUserInOrganization getUserInOrganization() {
        return new MutableUserInOrganization();
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
//        registry.addMapping("/data/**").allowedOrigins("http://localhost:4200");
    }

    @Bean
    @ConfigurationProperties(prefix = "datasources")
    public Datasources datasources() {
        return new Datasources();
    }


    @Bean
    @Description("Thymeleaf template resolver serving HTML 5")
    public ClassLoaderTemplateResolver templateResolver(QueryExecutor queryExecutor, FrontendResourceService frontendResourceService, TemplatePathFilteringProcessor filteringProcessor) {

        FrontendResourceOrClassLoaderTemplateResolver templateResolver = new FrontendResourceOrClassLoaderTemplateResolver(
                queryExecutor,
                frontendResourceService,
                classpathComponentImportService,
                frontendResourceLoadAlwaysFromResources,
                frontendResourceCreateIfNotExist,
                filteringProcessor);
        templateResolver.setPrefix("templates/");
        templateResolver.setCacheable(false);
        templateResolver.setSuffix(".html");
        templateResolver.setTemplateMode("HTML5");
        templateResolver.setCharacterEncoding("UTF-8");
        templateResolver.setOrder(1);
        return templateResolver;
    }


    @Bean
    @Description("Thymeleaf string resolver serving HTML 5")
    public StringTemplateResolver stringTemplateResolver() {

        StringTemplateResolver stringTemplateResolver = new StringTemplateResolver();
        stringTemplateResolver.setTemplateMode(TemplateMode.HTML);
        stringTemplateResolver.setOrder(1);
        return stringTemplateResolver;

    }

    @Autowired
    @Qualifier("mvcConversionService")
    public ObjectFactory<ConversionService> conversionService;

    @Inject
    public HtmlCRUDControllerConfigurationMap htmlCrudControllerConfigurationMap;

    @Inject
    FrontendMappingMap frontendMappingMap;

    @Inject
    public UrlHelper urlHelper;

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        argumentResolvers.add(new MapFormArgumentResolver(htmlCrudControllerConfigurationMap, frontendMappingMap, urlHelper));
    }

    @Bean
    public LocaleResolver localeResolver() {
        SessionLocaleResolver slr = new SessionLocaleResolver();
        slr.setDefaultLocale(Locale.ENGLISH);
        slr.setLocaleAttributeName(SessionData.LOCALE);
        return slr;
    }

}
