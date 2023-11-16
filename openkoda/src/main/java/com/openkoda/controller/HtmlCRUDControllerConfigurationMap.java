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
import com.openkoda.core.repository.common.ScopedSecureRepository;
import com.openkoda.model.Privilege;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Set;

@Component
public class HtmlCRUDControllerConfigurationMap extends AbstractCRUDControllerConfigurationMap{

    private final HashMap<String, CRUDControllerConfiguration> exposed = new HashMap<>();

    //TODO: this is dirty solution, to be trashed
    public CRUDControllerConfiguration registerAndExposeCRUDController(
            FrontendMappingDefinition frontendMappingDefinition,
            ScopedSecureRepository secureRepository,
            Class formClass
    ) {
        CRUDControllerConfiguration c = super.registerCRUDController(frontendMappingDefinition, secureRepository, formClass);
        exposed.put(frontendMappingDefinition.name, c);
        return c;
    }

    public Set<Entry<String, CRUDControllerConfiguration>> getExposed() {
        return exposed.entrySet();
    }

    public CRUDControllerConfiguration registerAndExposeCRUDController(
            FrontendMappingDefinition frontendMappingDefinition,
            ScopedSecureRepository secureRepository,
            Class formClass,
            Privilege readPrivilege, Privilege writePrivilege) {
        CRUDControllerConfiguration c = super.registerCRUDController(frontendMappingDefinition, secureRepository, formClass, readPrivilege, writePrivilege);
        exposed.put(frontendMappingDefinition.name, c);
        return c;
    }
}
