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

package com.openkoda.core.customisation;

import com.openkoda.core.audit.PropertyChangeListener;
import com.openkoda.core.flow.PageAttr;
import com.openkoda.core.form.CRUDControllerConfiguration;
import com.openkoda.core.form.FrontendMappingDefinition;
import com.openkoda.core.repository.common.ProfileSettingsRepository;
import com.openkoda.core.repository.common.ScopedSecureRepository;
import com.openkoda.core.service.event.AbstractApplicationEvent;
import com.openkoda.core.service.event.EventConsumer;
import com.openkoda.model.Privilege;
import com.openkoda.model.common.AuditableEntity;
import com.openkoda.model.common.SearchableEntity;
import com.openkoda.model.module.Module;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;


/**
 *
 * @author Arkadiusz Drysch (adrysch@stratoflow.com)
 *
 */
public interface CustomisationService {


    <T extends AuditableEntity> PropertyChangeListener registerAuditableClass(Class<T> c, String classLabel);

    <T> boolean registerEventListener(AbstractApplicationEvent event, Consumer<T> eventListener);

    <T> boolean registerEventListener(AbstractApplicationEvent event, BiConsumer<T, String> eventListener, String staticData1, String staticData2, String staticData3, String staticData4);

    <T> boolean registerEventConsumer(Class<T> eventClass, EventConsumer<T> eventConsumer);

    Module registerModule(Module module);

    void onApplicationStart();

    <T> void registerApplicationEventClass(Class<T> eventClass);

    <SE extends SearchableEntity, SF> void registerSettingsForm(
            ProfileSettingsRepository<SE> repository,
            Function<SE, SF> formConstructor,
            PageAttr<SF> formPageAttribute,
            String formFragmentFile,
            String formFragmentName);

    void registerOnApplicationStartListener(Consumer<CustomisationService> c);

    void registerFrontendMapping(FrontendMappingDefinition definition, ScopedSecureRepository repository);

    void unregisterFrontendMapping(String key);
    CRUDControllerConfiguration registerHtmlCrudController(FrontendMappingDefinition definition, ScopedSecureRepository repository);
    CRUDControllerConfiguration registerHtmlCrudController(FrontendMappingDefinition definition, ScopedSecureRepository repository, Privilege readPrivilege, Privilege writePrivilege);
    default CRUDControllerConfiguration registerHtmlCrudController(FrontendMappingDefinition definition, ScopedSecureRepository repository, String readPrivilege, String writePrivilege) {
        return registerHtmlCrudController(definition, repository, Privilege.valueOf(readPrivilege), Privilege.valueOf(writePrivilege));
    }
    void unregisterHtmlCrudController(String key);
    CRUDControllerConfiguration registerApiCrudController(FrontendMappingDefinition definition, ScopedSecureRepository repository);
    CRUDControllerConfiguration registerApiCrudController(FrontendMappingDefinition definition, ScopedSecureRepository repository, Privilege readPrivilege, Privilege writePrivilege);
    default CRUDControllerConfiguration registerApiCrudController(FrontendMappingDefinition definition, ScopedSecureRepository repository, String readPrivilege, String writePrivilege) {
        return registerApiCrudController(definition, repository, Privilege.valueOf(readPrivilege), Privilege.valueOf(writePrivilege));
    }
    void unregisterApiCrudController(String key);

}
