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

package com.openkoda.controller;

import com.openkoda.core.controller.generic.AbstractController;
import com.openkoda.core.flow.Flow;
import com.openkoda.core.flow.PageModelMap;
import com.openkoda.core.security.HasSecurityRules;
import com.openkoda.form.ServerJsForm;
import com.openkoda.model.ServerJs;
import com.openkoda.repository.SecureServerJsRepository;
import com.openkoda.repository.ServerJsRepository;
import jakarta.inject.Inject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.validation.BindingResult;

import java.io.Writer;

import static com.openkoda.form.ServerJsFrontendMappingDefinitions.*;

public class AbstractServerJsController extends AbstractController implements HasSecurityRules {

    @Inject
    SecureServerJsRepository secureServerJsRepository;

    @Inject
    ServerJsRepository unsecureServerJsRepository;

    public final static String serverJsUrl = "serverJs";
    public final static String SERVER_JS_ID = "serverJsId";
    public final static String _SERVER_JS_ID = "/{serverJsId}";


    protected PageModelMap searchServerJs(
            Long organizationId,
            String aSearchTerm,
            Specification<ServerJs> aSpecification,
            Pageable aPageable) {
        debug("[searchServerJs]");
        return Flow.init()
            .thenSet(serverJsPage, a -> secureServerJsRepository.search(aSearchTerm, organizationId, aSpecification, aPageable))
            .execute();
    }


    protected PageModelMap findServerJs(Long organizationId, long serverJsId) {
        debug("[findServerJs] serverJsId: {}", serverJsId);
        return Flow.init(serverJsId)
            .thenSet(serverJsEntity, a -> secureServerJsRepository.findOne(serverJsId))
            .thenSet(serverJsForm, a -> new ServerJsForm(organizationId, a.result))
            .execute();
    }


    protected PageModelMap updateServerJs(Long organizationId, long serverJsId, ServerJsForm formData, BindingResult br) {
        debug("[updateServerJs] serverJsId: {}", serverJsId);
        return Flow.init(serverJsForm, formData)
            .then(a -> secureServerJsRepository.findOne(serverJsId))
            .then(a -> services.validation.validateAndPopulateToEntity(formData, br, a.result))
            .thenSet(serverJsEntity, a -> unsecureServerJsRepository.save(a.result))
            .execute();
    }


    protected PageModelMap createServerJs(Long organizationId, ServerJsForm formData, BindingResult br) {
        debug("[createServerJs]");
        return Flow.init(serverJsForm, formData)
            .then(a -> services.validation.validateAndPopulateToEntity(formData, br, new ServerJs(organizationId)))
            .then(a -> unsecureServerJsRepository.save(a.result))
            .thenSet(serverJsForm, a -> new ServerJsForm())
            .execute();
    }


    protected PageModelMap removeServerJs(long serverJsId) {
        debug("[removeServerJs] serverJsId: {}", serverJsId);
        return Flow.init(serverJsId)
            .then(a -> unsecureServerJsRepository.deleteOne(serverJsId))
            .execute();
    }


    protected PageModelMap testServerJs(ServerJsForm serverJsFormData, BindingResult br, Writer log) {
        debug("[testServerJs]");
        return Flow.init(serverJsForm, serverJsFormData)
                .then(a -> services.validation.validate(serverJsFormData, br))
                .thenSet(scriptResult, a -> services.serverJSRunner.startServerJs(
                        a.result.dto.code, a.result.dto.model, a.result.dto.arguments, log))
                .execute();
    }

}
