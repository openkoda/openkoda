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

package com.openkoda.model.common;

import java.text.SimpleDateFormat;

import static com.openkoda.controller.common.URLConstants.*;

/**
 * <p>ModelConstants interface.</p>
 *
 * @author Arkadiusz Drysch (adrysch@stratoflow.com)
 * 
 */
public interface ModelConstants {
   /** Constant <code>GENERAL_ID_GENERATOR="generalIdGenerator"</code> */

   String ID = "id";
   String GLOBAL_ID_GENERATOR = "seqGlobalId";
   String ORGANIZATION_ID_GENERATOR = "seqOrganizationId";
   String ORGANIZATION_RELATED_ID_GENERATOR = "seqOrganizationRelatedId";
   String MASS_ID_GENERATOR = "massGlobalId";

   /** Constant <code>simpleDateFormat</code> */
   SimpleDateFormat simpleDateFormat = new SimpleDateFormat( "dd/MM/yy" );
   /** Constant <code>INDEX_STRING_COLUMN="index_string"</code> */
   String INDEX_STRING_COLUMN = "index_string";
   /** Constant <code>INDEX_STRING_COLUMN_LENGTH=2047</code> */
   int INDEX_STRING_COLUMN_LENGTH = 16300;
   /** Constant <code>UPDATED_ON="updated_on"</code> */
   String UPDATED_ON = "updated_on";
   /** Constant <code>CREATED_ON="created_on"</code> */
   String CREATED_ON = "created_on";

   String ORGANIZATION_ID = "organization_id";
   String SERVER_JS_ID = "server_js_id";
   String FRONTEND_RESOURCE_ID = "frontend_resource_id";
   String EMAIL = "email";

   String DEFAULT_ORGANIZATION_RELATED_REFERENCE_FIELD_FORMULA =
           "(LPAD(coalesce(" + ORGANIZATION_ID + ", 0)||'', 5, '0') || '/' || id)";

   int INITIAL_GLOBAL_VALUE = 10000;
   int INITIAL_PRIVILEGE_VALUE = 100000;
   int INITIAL_ORGANIZATION_VALUE = 122;
   int INITIAL_ORGANIZATION_RELATED_VALUE = 120150;

   String USER_ID_PLACEHOLDER = "##userId##";
   String REQUIRED_PRIVILEGE_COLUMN = "required_privilege";
   String REQUIRED_PRIVILEGE = "requiredPrivilege";
   String REQUIRED_READ_PRIVILEGE = "requiredReadPrivilege";
   String REQUIRED_WRITE_PRIVILEGE = "requiredWritePrivilege";
   String REQUIRED_READ_PRIVILEGE_COLUMN = "required_read_privilege";
   String GLOBAL_PATH_FORMULA_BASE = "'" + _HTML + "/";
   String ORG_RELATED_PATH_FORMULA_BASE = "'" + _HTML + "' || case when " + ORGANIZATION_ID + " is null then '' else '" + _ORGANIZATION + "/'||" + ORGANIZATION_ID + " end || '/";
   String ID_PATH_FORMULA = "/' || id || '" + _SETTINGS + "'";
   String ID_VIEW_PATH_FORMULA = "/' || id || '" + _VIEW + "'";
}
