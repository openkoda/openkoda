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

package com.openkoda.controller.admin;

import org.springframework.validation.BindingResult;

import com.openkoda.core.controller.generic.AbstractController;
import com.openkoda.core.flow.Flow;
import com.openkoda.core.flow.PageModelMap;
import com.openkoda.dto.EmailConfigDto;
import com.openkoda.form.EmailConfigForm;
import com.openkoda.model.EmailConfig;

public class AbstractIntegrationsController extends AbstractController {

    protected PageModelMap getIntegrations(){
        return findEmailConfigFlow()
                .execute();
    }
        
    protected Flow<Object, EmailConfigForm, AbstractIntegrationsController> findEmailConfigFlow() {
        debug("[findEmailConfigFlow]");
        return Flow.init(this)
                .thenSet(emailConfig, a -> repositories.unsecure.emailConfig.findAll().stream().findFirst().orElse(new EmailConfig()))
                .thenSet(emailConfigForm, a -> new EmailConfigForm(new EmailConfigDto(), a.result)); 
    }

    
    protected PageModelMap saveEmailConfig(EmailConfigForm form, BindingResult br) {
        debug("[saveEmailConfig] emailConfig [{}, {}]", form.getDto().getId(), form.getDto().getHost());
        return Flow.init(emailConfigForm, form)
                .thenSet(emailConfigForm, a -> form)
                .then(a -> repositories.unsecure.emailConfig.findAll().stream().findFirst().orElse(new EmailConfig()))
                .then(a -> services.validation.validateAndPopulateToEntity(form, br,a.result))            
                .thenSet(emailConfig, a -> repositories.unsecure.emailConfig.save(a.result))                
                .execute();
    }
}
