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

import com.openkoda.core.form.CRUDControllerConfiguration;
import com.openkoda.core.form.FrontendMappingDefinition;
import com.openkoda.core.helper.PrivilegeHelper;
import com.openkoda.core.repository.common.ScopedSecureRepository;
import com.openkoda.model.PrivilegeBase;
import com.openkoda.model.component.Form;
import com.openkoda.repository.FormRepository;
import jakarta.annotation.PostConstruct;
import jakarta.inject.Inject;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import static java.util.stream.Collectors.toCollection;

@Component
public class HtmlCRUDControllerConfigurationMap extends AbstractCRUDControllerConfigurationMap{

    private static final long serialVersionUID = 2132602257370923654L;
    private static HtmlCRUDControllerConfigurationMap instance;

    @PostConstruct
    public void init() {
        instance = this;
    }

    @Inject
    FormRepository formRepository;
    private final HashMap<String, CRUDControllerConfiguration> exposed = new HashMap<>();

    //TODO: this is dirty solution, to be trashed
    public CRUDControllerConfiguration registerAndExposeCRUDController(
            FrontendMappingDefinition frontendMappingDefinition,
            ScopedSecureRepository secureRepository,
            Class formClass
    ) {
        CRUDControllerConfiguration c = super.registerCRUDController(frontendMappingDefinition, secureRepository, formClass);
        setOrgIdAndExpose(frontendMappingDefinition.name, c);
        return c;
    }

    public Set<Entry<String, CRUDControllerConfiguration>> getExposed() {
        return exposed.entrySet();
    }
    public Set<Entry<String, CRUDControllerConfiguration>> getExposed(Long organizationId){
        return getExposed(organizationId, getExposed());
    }
    public LinkedHashSet<Entry<String, CRUDControllerConfiguration>> getExposedSorted() {
        return getExposedSorted(null);
    }
    public LinkedHashSet<Entry<String, CRUDControllerConfiguration>> getExposedSorted(Long organizationId) {
        return getExposed(organizationId, getExposed()).stream()
                .sorted(Map.Entry.comparingByKey()).collect(toCollection(LinkedHashSet::new));
    }
    private LinkedHashSet<Entry<String, CRUDControllerConfiguration>> getExposed(Long organizationId, Set<Entry<String, CRUDControllerConfiguration>> initialExposedSet) {
        if(organizationId == null){
            return new LinkedHashSet<>(initialExposedSet);
        }
        return initialExposedSet.stream()
                .filter(e -> e.getValue().getOrganizationId() == null || organizationId == null || organizationId.equals(e.getValue().getOrganizationId()))
                .filter( e -> PrivilegeHelper.getInstance().hasGlobalOrOrgPrivilege(e.getValue().getGetAllPrivilege(), organizationId))
                .filter(controller -> {
                    String formName = controller.getValue().getFrontendMappingDefinition().name;
                    Form form = formRepository.findByName(formName);
                    return form.isShowOnOrganizationDashboard();
                })
                .collect(toCollection(LinkedHashSet::new));
    }


    public CRUDControllerConfiguration registerAndExposeCRUDController(
            FrontendMappingDefinition frontendMappingDefinition,
            ScopedSecureRepository secureRepository,
            Class formClass,
            PrivilegeBase readPrivilege, PrivilegeBase writePrivilege) {
        CRUDControllerConfiguration c = super.registerCRUDController(frontendMappingDefinition, secureRepository, formClass, readPrivilege, writePrivilege);
        setOrgIdAndExpose(frontendMappingDefinition.name, c);
        return c;
    }

    @Override
    public void unregisterCRUDController(String key) {
        exposed.remove(key);
        super.unregisterCRUDController(key);
    }

    public static HtmlCRUDControllerConfigurationMap getControllers() {
        return instance;
    }

    private void setOrgIdAndExpose(String formName, CRUDControllerConfiguration conf){
        Form form = formRepository.findByName(formName);
        conf.setOrganizationId(form != null ? form.getOrganizationId() : null);
        exposed.put(conf.getKey(), conf);
    }
}
