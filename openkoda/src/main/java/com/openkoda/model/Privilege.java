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

/**
 * <p>Privilege class.</p>
 *
 * @author Arkadiusz Drysch (adrysch@stratoflow.com)
 * 
 */
public enum Privilege implements PrivilegeBase, PrivilegeNames {

    isUser("Global Settings - Is User", _isUser),

    //ORGANIZATION DATA
    readOrgData("Organization - Can Read", _readOrgData),
    manageOrgData("Organization - Can Manage", _manageOrgData),
    canChangeEntityOrganization("Organization - Can Change Entity", _canChangeEntityOrganization),

    //   ADMIN
    canAccessGlobalSettings("Global Settings - Can Access", _canAccessGlobalSettings),

    //   USER DATA
    canRecoverPassword("User - Can recover password", _canRecoverPassword, true),
    canSeeUserEmail("User - Can see Email", _canSeeUserEmail),

    canImpersonate("User - Can Impersonate", _canImpersonate),

    canResetPassword("User - Can Reset Passwords", _canResetPassword),
    canVerifyAccount("User - Can verify password", _canVerifyAccount, true),

    readUserData("User - Can Read Profiles", _readUserData),
    manageUserData("User - Can Manage Profiles Data", _manageUserData),

    readUserRole("User Role - Can Read", _readUserRole),
    manageUserRoles("User Role - Can Manage", _manageUserRoles),


    //   PAYMENTS
    @Deprecated
    administratePayments("Payments - Can Administrate", _administratePayments),

    //   HISTORY, LOGS, SYSTEM HEALTH
    canReadSupportData("Support - Can Read", _canReadSupportData),
    canManageSupportData("Support - Can Manage", _canManageSupportData),



    //TODO - to be removed
    readOrgAudit("History - Can Read Organization History", _readOrgAudit),

    //   FRONTEND RESOURCE
    readFrontendResource("Frontend Resource - Can Read", _readFrontendResource),
    manageFrontendResource("Frontend Resource - Can Manage", _manageFrontendResource),

    // REFRESHER TOKEN
    canRefreshTokens("Token - Can Refresh", _canRefreshTokens),

    //ROLES, SERVER JS, EVENT LISTENERS, SCHEDULERS, THREADS
    canReadBackend("Backend - Can Read", _canReadBackend),
    canManageBackend("Backend - Can Manage", _canManageBackend);

    private String label;
    private boolean hidden;

    Privilege(String label, String nameCheck) {
        this(label, nameCheck, false);
    }

    Privilege(String label, String nameCheck, boolean hidden) {
        this.label = "General - " + label;
        this.hidden = hidden;
        checkName(nameCheck);
    }

    @Override
    public String getLabel() {
        return label;
    }

    @Override
    public boolean isHidden() {
        return hidden;
    }


}
