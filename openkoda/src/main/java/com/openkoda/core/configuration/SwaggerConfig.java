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

import com.openkoda.core.flow.LoggingComponent;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static com.openkoda.controller.common.URLConstants.API_TOKEN;

/**
 * Configuration for Swagger UI generation based on
 */
//TODO: Might need more Security cofigs
@Configuration
public class SwaggerConfig implements LoggingComponent {

    @Value("${base.url:http://localhost:8080}") String baseUrl ;
    @Value("${application.name:Default Application}") String applicationName;
    @Value("${application.admin.email}") String contactEmail;

    private final static String API = "API";
    @Bean
    public OpenAPI openkodaOpenAPI(){
        return buildOpenApi("1.0", false);
    }

    @Bean
    public GroupedOpenApi publicApi(){
        return GroupedOpenApi.builder()
                .group("auth")
                .pathsToMatch("/api/auth/**")
                .packagesToScan("com.openkoda.controller.api.auth")
                .build();
    }

    @Bean
    public GroupedOpenApi v1Api(){
        return GroupedOpenApi.builder()
                .group("v1")
                .pathsToMatch("/api/v1/**")
                .packagesToScan("com.openkoda.controller.api.v1")
                .build();
    }

    protected OpenAPI buildOpenApi(String version, boolean secured){
        debug("[buildApiInfo]");

        OpenAPI openAPI =  new OpenAPI().info(buildOpenApiInfo(version));
        if(secured){
            openAPI.components(new Components().addSecuritySchemes(API_TOKEN, securityScheme()));
        }
        return openAPI;
    }

    protected Info buildOpenApiInfo(String version){
        return new Info()
                .title(applicationName)
                .description(applicationName)
                .version(version)
                .license(new License().name("Apache 2.0").url("http://springdoc.org"))
                .contact(new Contact()
                        .name(applicationName + " - Support Team")
                        .url(baseUrl)
                        .email(contactEmail));
    }

    private SecurityScheme securityScheme() {
        return new SecurityScheme()
                .scheme(API_TOKEN)
                .name(API)
                .type(SecurityScheme.Type.APIKEY)
                .in(SecurityScheme.In.HEADER);
    }
}