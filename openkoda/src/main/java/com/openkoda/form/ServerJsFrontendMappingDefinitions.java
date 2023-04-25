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

package com.openkoda.form;

import com.openkoda.core.form.FrontendMappingDefinition;
import com.openkoda.core.flow.PageAttr;
import com.openkoda.core.security.HasSecurityRules;
import com.openkoda.model.ServerJs;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;

import static com.openkoda.controller.common.URLConstants.SERVERJS;
import static com.openkoda.core.form.FrontendMappingDefinition.createFrontendMappingDefinition;
import static com.openkoda.form.TemplateFormFieldNames.TEST_BUTTON_;
import static com.openkoda.model.Privilege.*;

public interface ServerJsFrontendMappingDefinitions extends HasSecurityRules {

    String SERVER_JS_FORM = "ServerJsForm";
    
    String NAME_ = "name";
    String CODE_ = "code";
    String MODEL_ = "model";
    String ARGUMENTS_ = "arguments";

    PageAttr<ServerJs> serverJsEntity = new PageAttr<>("serverJsEntity");
    PageAttr<ServerJsForm> serverJsForm = new PageAttr<>("serverJsForm");
    PageAttr<Page<ServerJs>> serverJsPage = new PageAttr<>("serverJsPage");

    String _readPrivilege = _canReadBackend;
    String _writePrivilege = _canManageBackend;

    String CHECK_CAN_READ = HAS_GLOBAL_OR_ORG_PRIVILEGE_STRING_OPEN + _readPrivilege + HAS_ORG_PRIVILEGE_CLOSE;
    String CHECK_CAN_WRITE = HAS_GLOBAL_OR_ORG_PRIVILEGE_STRING_OPEN + _writePrivilege + HAS_ORG_PRIVILEGE_CLOSE;

    FrontendMappingDefinition serverJsFrontendMappingDefinition = createFrontendMappingDefinition(SERVERJS, canReadBackend, canManageBackend,
            a -> a  .text(NAME_)
                    .validate(v -> StringUtils.isBlank(v) ? "not.empty" : null)
                    .codeJs(CODE_)
                    .validate(v -> StringUtils.isBlank(v) ? "not.empty" : null)
                    .codeJs(MODEL_)
                    .codeJs(ARGUMENTS_)
                    .submitToNewTab(TEST_BUTTON_, "../test"));
}