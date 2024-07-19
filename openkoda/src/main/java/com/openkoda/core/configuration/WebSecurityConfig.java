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

import com.openkoda.controller.common.URLConstants;
import com.openkoda.core.security.*;
import jakarta.annotation.Resource;
import jakarta.servlet.Filter;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.boot.web.servlet.ServletContextInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.expression.WebExpressionAuthorizationManager;
import org.springframework.security.web.authentication.RememberMeServices;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.rememberme.TokenBasedRememberMeServices;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.web.filter.CompositeFilter;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.security.web.util.matcher.RegexRequestMatcher.regexMatcher;


@Configuration
/**
 * <p>WebSecurityConfig covers configuration and bean definitions for Security aspect of the application.</p>
 * <
 */
@Order(SecurityProperties.BASIC_AUTH_ORDER)
@EnableMethodSecurity(securedEnabled = true, prePostEnabled = true)
@ComponentScan(basePackages = "com.openkoda")
@EnableWebSecurity
public class WebSecurityConfig implements URLConstants {

    public static final String CHANGE_PASSWORD_PRIVILEGE = "CHANGE_PASSWORD_PRIVILEGE";

    @Value("${local.network:127.0.0.1/32}")
    private String localNetwork;

    @Value("${page.after.logout:/home}")
    private String pageAfterLogin;

    @Value("${rememberme.key:uniqueRememberKey}")
    private String rememberMeKey;

    @Value("${rememberme.parameter:remember-me}")
    private String rememberMeParameter;

    @Value("${rememberme.parameter:remember-me}")
    private String rememberMeCookieName;

    @Value("${application.pages.public:}")
    public String[] publicPages;

    @Value("${application.pages.csrf-disabled}")
    public String[] csrfDisabledPages;

    @Value("${page.after.auth:/html/organization/all}")
    private String pageAfterAuth;


    @Resource(name = "customUserDetailsService")
    protected OrganizationUserDetailsService customUserDetailsService;

    @Resource(name = "requestParameterTokenAuthenticationFilter")
    protected RequestParameterTokenAuthenticationFilter requestParameterTokenAuthenticationFilter;

    @Resource(name = "loginAndPasswordAuthenticationFilter")
    protected LoginAndPasswordAuthenticationFilter loginAndPasswordAuthenticationFilter;

    @Resource(name = "apiTokenHeaderAuthenticationFilter")
    protected ApiTokenHeaderAuthenticationFilter apiTokenHeaderAuthenticationFilter;

    @Resource(name = "tokenPathPrefixAuthenticationFilter")
    protected TokenPathPrefixAuthenticationFilter tokenPathPrefixAuthenticationFilter;

    @Resource(name = "loginByPasswordOrTokenAuthenticationProvider")
    protected LoginByPasswordOrTokenAuthenticationProvider loginByPasswordOrTokenAuthenticationProvider;

    private ApplicationAwarePasswordEncoder passwordEncoder;

    public WebSecurityConfig(@Autowired ApplicationAwarePasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }


    /*
     * Global rules:
     * - UserDetailsService is responsible for reading user (with privileges) by username
     *   and setting the password from LoginAndPassword if exists.
     *   Username is email from User entity.
     * - AuthenticationProvider is responsible for checking if the request with authentication data is correct, ie.
     *   - for form authentication (default, username and password) it checks password from LoginAndPassword
     *   - for requestToken it checks if request parameter "token" exists in Token entity for given user
     *   - for apiToken it checks it "api-token" Header exists in Token entity for given user
     * See LoginByPasswordOrTokenAuthenticationProvider.additionalAuthenticationChecks()
     */

    @Bean
    public AuthenticationManager authenticationManager() {
        List<AuthenticationProvider> authenticationProviders = new ArrayList<>();
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
        daoAuthenticationProvider.setPasswordEncoder(passwordEncoder);
        daoAuthenticationProvider.setUserDetailsService(customUserDetailsService);
        authenticationProviders.add(daoAuthenticationProvider);
        authenticationProviders.add(loginByPasswordOrTokenAuthenticationProvider);
        return new ProviderManager(authenticationProviders);
    }


    @Value("${page.after.auth.for.one.organization:/html/organization/%s/settings}")
    private String pageAfterAuthForOneOrganization;

    @Value("${page.after.auth.for.multiple.organizations:/html/organization/all}")
    private String pageAfterAuthForMultipleOrganizations;

    @Bean
    @Order(10)
    public SecurityFilterChain webSecurityFilterChain(HttpSecurity http) throws Exception {
        return webHttpSecurity(http).build();
    }
    @Bean
    @Order(20)
    public SecurityFilterChain apiAuthSecurityFilterChain(HttpSecurity http) throws Exception {
        return apiAuthHttpSecurity(http).build();
    }
    @Bean
    @Order(21)
    public SecurityFilterChain apiTokenSecurityFilterChain(HttpSecurity http) throws Exception {
        return apiTokenHttpSecurity(http).build();
    }
    @Bean
    @Order(22)
    public SecurityFilterChain apiV1SecurityFilterChain(HttpSecurity http) throws Exception {
        return apiV1HttpSecurity(http).build();
    }
    @Bean
    @Order(23)
    public SecurityFilterChain apiV2SecurityFilterChain(HttpSecurity http) throws Exception {
        return apiV2HttpSecurity(http).build();
    }
    @Bean
    @Order(50)
    public SecurityFilterChain publicWebSecurityFilterChain(HttpSecurity http) throws Exception {
        return publicWebHttpSecurity(http).build();
    }

    public HttpSecurity webHttpSecurity(HttpSecurity http) throws Exception {
        return http.securityMatcher(_HTML + "/**")
                .addFilterBefore(ssoFilter(), UsernamePasswordAuthenticationFilter.class)
                .addFilterAfter(loginAndPasswordAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .headers().frameOptions().sameOrigin().and()
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(publicPages).permitAll()
                        .requestMatchers(_HTML + _ADMIN + _ANY).hasRole("_ADMIN")
                        .requestMatchers(_HTML + _LOCAL + "/**").access(new WebExpressionAuthorizationManager(
                                    "hasIpAddress('" + localNetwork + "')"
                            ))
                        .requestMatchers(_HTML + "/**").authenticated()
                )
                .formLogin(form -> form
                        .loginPage(_LOGIN)
                        .loginProcessingUrl("/perform_login")
                )
                .rememberMe(rememberMe -> rememberMe.rememberMeServices(rememberMeServices(customUserDetailsService))
                        .rememberMeCookieName(rememberMeCookieName)
                        .rememberMeParameter(rememberMeParameter)
                        .key(rememberMeKey)
                );
    }
    public HttpSecurity apiAuthHttpSecurity(HttpSecurity http) throws Exception {
        return http.securityMatcher(_API_AUTH_ANT_EXPRESSION)
                .anonymous(AbstractHttpConfigurer::disable)
                .csrf(AbstractHttpConfigurer::disable)
                .requestCache(AbstractHttpConfigurer::disable)
                .sessionManagement(sessionManagement -> sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
    }
    public HttpSecurity apiTokenHttpSecurity(HttpSecurity http) throws Exception {
        return http.securityMatcher(_TOKEN_PREFIX_ANT_EXPRESSION)
                .addFilterBefore(tokenPathPrefixAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .anonymous(AbstractHttpConfigurer::disable)
                .csrf(AbstractHttpConfigurer::disable)
                .requestCache(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth.anyRequest().authenticated())
                .sessionManagement(sessionManagement -> sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
    }
    public HttpSecurity apiV1HttpSecurity(HttpSecurity http) throws Exception {
       return http.securityMatcher(_API_V1_ANT_EXPRESSION)
                .addFilterBefore(apiTokenHeaderAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .anonymous(AbstractHttpConfigurer::disable)
                .csrf(AbstractHttpConfigurer::disable)
                .requestCache(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth.anyRequest().authenticated())
                .sessionManagement(sessionManagement -> sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(eh -> eh.authenticationEntryPoint((request, response, authException) -> response.sendError(HttpServletResponse.SC_UNAUTHORIZED)));
    }
    public HttpSecurity apiV2HttpSecurity(HttpSecurity http) throws Exception {
        return http.securityMatcher(_API_V2_ANT_EXPRESSION)
                .addFilterBefore(apiTokenHeaderAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .anonymous(AbstractHttpConfigurer::disable)
                .csrf(AbstractHttpConfigurer::disable)
                .requestCache(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth.anyRequest().authenticated())
                .sessionManagement(sessionManagement -> sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(eh -> eh.authenticationEntryPoint((request, response, authException) -> response.sendError(HttpServletResponse.SC_UNAUTHORIZED)));
    }
    public HttpSecurity publicWebHttpSecurity(HttpSecurity http) throws Exception {
        return http.securityMatcher("/**")
                .addFilterBefore(ssoFilter(), UsernamePasswordAuthenticationFilter.class)
                .addFilterAfter(loginAndPasswordAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .headers().frameOptions().sameOrigin().and()
                .csrf(csrf -> csrf
                        .ignoringRequestMatchers(csrfDisabledPages)
                )
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(publicPages).permitAll()
                        .requestMatchers(regexMatcher("/" + FRONTENDRESOURCEREGEX + FRONTENDRESOURCE_ORGID_PARAM_REGEX)).permitAll()
                        .requestMatchers(regexMatcher("/" + FRONTENDRESOURCEREGEX + FRONTENDRESOURCE_AUTH_PARAMS_REGEX)).authenticated()
                        .requestMatchers(_PASSWORD + "/**").authenticated()
                )
                .logout(logout -> logout
                        .logoutUrl(_LOGOUT)
                        .deleteCookies("JSESSIONID")
                        .logoutSuccessHandler( (request, response, authentication) -> {
                            String email = authentication.getName();
                            customUserDetailsService.unsubscribeUser(email);
                            response.sendRedirect("/");
                            })
                );
    }
    private Filter ssoFilter() {
        CompositeFilter filter = new CompositeFilter();
        List<Filter> filters = new ArrayList<>();
        requestParameterTokenAuthenticationFilter.setAuthenticationSuccessHandler(new RequestParameterTokenAuthenticationSuccessHandler(securityContextRepository()));
        filters.add(requestParameterTokenAuthenticationFilter);
        filter.setFilters(filters);
        return filter;
    }

    @Bean
    public SecurityEvaluationContextExtension securityEvaluationContextExtension() {
        return new SecurityEvaluationContextExtension();
    }

    @Bean
    public ServletContextInitializer servletContextInitializer(@Value("${secure.cookie:true}") boolean secure) {
        return new ServletContextInitializer() {

            @Override
            public void onStartup(ServletContext servletContext) throws ServletException {
                servletContext.getSessionCookieConfig().setSecure(secure);
            }
        };
    }

    @Bean()
    public SecurityContextRepository securityContextRepository(){
        return new HttpSessionSecurityContextRepository();
    }

    @Bean
    RememberMeServices rememberMeServices(UserDetailsService userDetailsService) {
        TokenBasedRememberMeServices.RememberMeTokenAlgorithm encodingAlgorithm = TokenBasedRememberMeServices.RememberMeTokenAlgorithm.SHA256;
        TokenBasedRememberMeServices rememberMe = new TokenBasedRememberMeServices(rememberMeKey, userDetailsService, encodingAlgorithm);
        rememberMe.setMatchingAlgorithm(TokenBasedRememberMeServices.RememberMeTokenAlgorithm.MD5);
        return rememberMe;
    }


}
