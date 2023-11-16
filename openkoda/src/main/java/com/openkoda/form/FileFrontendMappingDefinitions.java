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

package com.openkoda.form;

import com.openkoda.core.form.FrontendMappingDefinition;
import com.openkoda.model.PrivilegeBase;

import static com.openkoda.core.form.FrontendMappingDefinition.createFrontendMappingDefinition;
import static com.openkoda.core.security.HasSecurityRules.*;
import static com.openkoda.model.Privilege.readOrgData;
import static com.openkoda.model.PrivilegeNames._readOrgData;

public interface FileFrontendMappingDefinitions {

    String FILE_FORM = "FileForm";
    String FILENAME_ = "filename";
    String CONTENT_TYPE_ = "contentType";
    String CONTENT_ = "content";
    String PUBLIC_FILE_ = "publicFile";

    PrivilegeBase readPrivilege = readOrgData;
    PrivilegeBase writePrivilege = readOrgData;

    String _readPrivilege = _readOrgData;
    String _writePrivilege = _readOrgData;

    String CHECK_CAN_READ = BB_OPEN + CHECK_CAN_EDIT_ATTRIBUTES + OR + HAS_ORG_PRIVILEGE_OPEN + _readPrivilege + HAS_ORG_PRIVILEGE_CLOSE + BB_CLOSE;
    String CHECK_CAN_WRITE = BB_OPEN + CHECK_CAN_EDIT_ATTRIBUTES + OR + HAS_ORG_PRIVILEGE_OPEN + _writePrivilege + HAS_ORG_PRIVILEGE_CLOSE + BB_CLOSE;

    FrontendMappingDefinition fileForm = createFrontendMappingDefinition(FILE_FORM, readPrivilege, writePrivilege,
            a -> a  .text(FILENAME_)
                    .checkbox(PUBLIC_FILE_)
                    .text(CONTENT_TYPE_)
    );
}