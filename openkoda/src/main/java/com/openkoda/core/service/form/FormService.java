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

package com.openkoda.core.service.form;

import com.openkoda.controller.ComponentProvider;
import com.openkoda.core.form.AbstractForm;
import com.openkoda.core.form.FrontendMappingDefinition;
import com.openkoda.core.helper.ClusterHelper;
import com.openkoda.core.repository.common.ScopedSecureRepository;
import com.openkoda.core.security.HasSecurityRules;
import com.openkoda.core.service.event.ClusterEventSenderService;
import com.openkoda.model.Form;
import com.openkoda.model.ServerJs;
import jakarta.inject.Inject;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Service provides any {@link Form} entity related actions
 * It allows to register any new {@link Form}, do the reload of the updated {@link Form} and remove already registered forms.
 */
@Service
public class FormService extends ComponentProvider implements HasSecurityRules {

    @Inject
    private ClusterEventSenderService clusterEventSenderService;

    /**
     * Unregister and register the updated form again
     * @param formId
     * @return boolean result
     */
    public boolean reloadForm(Long formId) {
        debug("[reloadForm]");
        Form form = repositories.unsecure.form.findOne(formId);
        if(form != null) {
            if(unregisterForm(form)) {
                return registerForm(form);
            }
        }
        return false;
    }

    /**
     * Register form
     * @param formId
     * @return boolean result
     */
    public boolean addForm(Long formId) {
        debug("[addForm]");
        Form form = repositories.unsecure.form.findOne(formId);
        if(form != null) {
            return registerForm(form);
        }
        return false;
    }

    /**
     * Unregister form
     * @param formId
     * @return boolean result
     */
    public boolean removeForm(Long formId) {
        debug("[removeForm]");
        Form form = repositories.unsecure.form.findOne(formId);
        if(form != null) {
            return unregisterForm(form);
        }
        return false;
    }

    /**
     * Load all form entities from database and register each of them
     */
    public void loadAllFormsFromDb() {
        debug("[loadFormsFromDb]");
        List<Form> all = repositories.unsecure.form.findAll();
        all.forEach(this::registerForm);
    }

    /**
     * Register form with given ID
     * @param formId
     * @return boolean result
     */
    public boolean loadClusterAware(long formId) {
        debug("[loadClusterAware] {}", formId);
        if (ClusterHelper.isCluster()) {
            return clusterEventSenderService.loadForm(formId);
        }
        return addForm(formId);
    }

    /**
     * Unregister form with given ID
     * @param formId
     * @return boolean result
     */
    public boolean removeClusterAware(long formId) {
        debug("[removeClusterAware] {}", formId);
        if (ClusterHelper.isCluster()) {
            return clusterEventSenderService.removeForm(formId);
        }
        return removeForm(formId);
    }

    /**
     * Reload (unregister and register again) form with given ID
     * @param formId
     * @return boolean result
     */
    public boolean reloadClusterAware(long formId) {
        debug("[reloadClusterAware] {}", formId);
        if (ClusterHelper.isCluster()) {
            return clusterEventSenderService.reloadForm(formId);
        }
        return reloadForm(formId);
    }

    private boolean registerForm(Form form) {
        debug("[registerForm]");
        String finalScript = "let form = services.frontendMappingDefinition.createFrontendMappingDefinition(\""
                +form.getName() + "\",\"" + form.getReadPrivilege().name() + "\",\"" + form.getWritePrivilege().name() + "\","
                + form.getCode() + ");\nform";

        ServerJs serverJs = new ServerJs(finalScript, StringUtils.EMPTY, null);
        ScopedSecureRepository repository = services.data.getRepository(form.getName());

        Map<String, Object> model = new HashMap();
        FrontendMappingDefinition formFieldDefinitionBuilder = services.serverJSRunner.evaluateServerJs(serverJs, model, null, FrontendMappingDefinition.class);

        services.customisation.registerFrontendMapping(formFieldDefinitionBuilder, repository);
        AbstractForm.markDirty(form.getName());

        if(form.isRegisterHtmlCrudController()){
            services.customisation.registerHtmlCrudController(formFieldDefinitionBuilder, repository, form.getReadPrivilege(), form.getWritePrivilege()).setGenericTableFields(form.getTableColumnsList());
        }
        if(form.isRegisterApiCrudController()){
            services.customisation.registerApiCrudController(formFieldDefinitionBuilder, repository, form.getReadPrivilege(), form.getWritePrivilege());
        }
        return true;
    }

    private boolean unregisterForm(Form form) {
        debug("[unregisterForm]");
        services.customisation.unregisterFrontendMapping(form.getName());
        services.customisation.unregisterHtmlCrudController(form.getName());
        services.customisation.unregisterApiCrudController(form.getName());
        return true;
    }

}
