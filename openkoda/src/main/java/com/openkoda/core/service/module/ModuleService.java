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

package com.openkoda.core.service.module;

import com.openkoda.controller.ComponentProvider;
import com.openkoda.controller.common.PageAttributes;
import com.openkoda.core.helper.UserHelper;
import com.openkoda.core.service.event.ApplicationEvent;
import com.openkoda.dto.OrganizationDto;
import com.openkoda.dto.user.BasicUser;
import com.openkoda.dto.user.UserRoleDto;
import com.openkoda.integration.model.configuration.IntegrationModuleOrganizationConfiguration;
import com.openkoda.model.module.Module;
import jakarta.annotation.PostConstruct;
import jakarta.inject.Inject;
import jakarta.validation.constraints.NotNull;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>ModuleService class.</p>
 *
 * @author Arkadiusz Drysch (adrysch@stratoflow.com)
 *
 */
@Service("modules")
@DependsOn("applicationContextProvider")
public class ModuleService extends ComponentProvider implements PageAttributes {

    @Inject
    ApplicationContext applicationContext;

    private final SortedSet<Module> modules = new TreeSet<>(Comparator.comparingInt(Module::getOrdinal));
    private final Map<String, Module> modulesByName = new HashMap<>();

    /**
     * <p>registerModule.</p>
     *
     * @param module a {@link com.openkoda.model.module.Module} object.
     * @return a {@link com.openkoda.model.module.Module} object.
     */
    synchronized public Module registerModule(Module module) {
        debug("[registerModule] {}", module.getName());
        modules.add(module);
        modulesByName.put(module.getName(), module);
        return module;
    }

    @Inject
    UserHelper userHelper;

    @PostConstruct
    void init() {
        services.applicationEvent.registerEventListener(ApplicationEvent.USER_CREATED,
                this::createConfigurationsForUser);
        services.applicationEvent.registerEventListener(ApplicationEvent.USER_DELETED,
                this::deleteConfigurationsForUser);
        services.applicationEvent.registerEventListener(ApplicationEvent.ORGANIZATION_CREATED,
                this::createConfigurationsForOrganization);
        services.applicationEvent.registerEventListener(ApplicationEvent.ORGANIZATION_DELETED,
                this::deleteConfigurationsForOrganization);
        services.applicationEvent.registerEventListener(ApplicationEvent.USER_ROLE_CREATED,
                this::createConfigurationsForOrganizationUser);
        services.applicationEvent.registerEventListener(ApplicationEvent.USER_ROLE_DELETED,
                this::deleteConfigurationsForOrganizationUser);

    }

    /**
     * <p>getIterator.</p>
     *
     * @return a {@link java.util.Iterator} object.
     */
    public Iterator<Module> getIterator() {
        return modules.iterator();
    }

    /**
     * <p>getModulesForNames.</p>
     *
     * @param moduleNames a {@link java.util.List} object.
     * @return a {@link java.util.List} object.
     */
    public List<Module> getModulesForNames(@NotNull List<String> moduleNames) {
        return modules.stream().filter(a -> moduleNames.contains(a.getName())).collect(Collectors.toList());
    }

    /**
     * <p>getModuleForName.</p>
     *
     * @param moduleName a {@link java.lang.String} object.
     * @return a {@link com.openkoda.model.module.Module} object.
     */
    public Module getModuleForName(@NotNull String moduleName) {
        return modulesByName.get(moduleName);
    }


    public boolean createConfigurationsForUser(BasicUser user) {
        debug("[createConfigurationsForUser] {}", user);
        return true;
    }

    public boolean deleteConfigurationsForUser(BasicUser user) {
        debug("[deleteConfigurationsForUser] {}", user);
        return true;
    }

    public boolean createConfigurationsForOrganization(OrganizationDto organization) {
        debug("[createConfigurationsForOrganization] {}", organization);
        repositories.unsecure.integration.save(new IntegrationModuleOrganizationConfiguration(organization.getOrganizationId()));
        return true;
    }

    public boolean deleteConfigurationsForOrganization(OrganizationDto organization) {
        return true;
    }

    public boolean createConfigurationsForOrganizationUser(UserRoleDto ur) {
        debug("[createConfigurationsForOrganizationUser] user role: {}", ur);
        if (ur.isGlobal()) return true;
        return true;

    }

    public boolean deleteConfigurationsForOrganizationUser(UserRoleDto ur) {
        debug("[deleteConfigurationsForOrganizationUser] user role: {}", ur);
        return true;

    }

    public boolean addModulePrivilegesToRole(String roleName, Set<Enum> privileges) {
        debug("[addModulePrivilegesToRole] role: {} privilages: {}", roleName, privileges);
        services.role.addPrivilegesToRole(roleName, privileges);
        return true;
    }

}
