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

package com.openkoda.model;

public interface PrivilegeNames {

    public static final String _isUser = "isUser";

    //ORGANIZATION DATA
    public static final String _readOrgData = "readOrgData";
    public static final String _manageOrgData = "manageOrgData";
    public static final String _useReportingAI = "canUseReportingAI";
    public static final String _createReports = "canCreateReports";
    public static final String _readReports = "canReadReports";

    //   ADMIN
    public static final String _canAccessGlobalSettings = "canAccessGlobalSettings";
    public static final String _canImpersonate = "canImpersonate";
    public static final String _canChangeEntityOrganization = "canChangeEntityOrganization";
    public static final String _canImportData = "canImportData";
    public static final String _canSeeUserEmail = "canSeeUserEmail";
    public static final String _canResetPassword = "canResetPassword";

    //   USER DATA
    public static final String _readUserData = "readUserData";
    public static final String _manageUserData = "manageUserData";

    public static final String _readUserRole = "readUserRole";
    public static final String _manageUserRoles = "manageUserRoles";

    //   PAYMENTS
    public static final String _administratePayments = "administratePayments";

    //   AUDIT
    public static final String _readOrgAudit = "readOrgAudit";

    //   FRONTEND RESOURCE
    public static final String _readFrontendResource = "readFrontendResource";
    public static final String _manageFrontendResource = "manageFrontendResource";

    // ATTRIBUTES
    public static final String _canSeeAttributes = "canSeeAttributes";
    public static final String _canEditAttributes = "canEditAttributes";
    public static final String _canEditUserAttributes = "canEditUserAttributes";
    public static final String _canEditOrgAttributes = "canEditOrgAttributes";
    public static final String _canDefineAttributes = "canDefineAttributes";

    public static final String _canRecoverPassword = "canRecoverPassword";
    public static final String _canVerifyAccount = "canVerifyAccount";
    public static final String _canRefreshTokens = "canRefreshTokens";
    public static final String _canManageBackend = "canManageBackend";
    public static final String _canReadBackend = "canReadBackend";

    public static final String _canReadSupportData = "canReadSupportData";
    public static final String _canManageSupportData = "canManageSupportData";

}
