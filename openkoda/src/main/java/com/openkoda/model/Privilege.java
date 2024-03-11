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

import static com.openkoda.model.PrivilegeGroup.*;

/**
 * <p>Privilege class.</p>
 *
 * @author Arkadiusz Drysch (adrysch@stratoflow.com)
 * 
 */
public enum Privilege implements PrivilegeBase, PrivilegeNames {

    isUser(GLOBAL_SETTINGS,"Is User", _isUser),
    //   ADMIN
    canAccessGlobalSettings(GLOBAL_SETTINGS,"Access", _canAccessGlobalSettings),

    //ORGANIZATION DATA
    readOrgData(ORGANIZATION,"Read", _readOrgData),
    manageOrgData(ORGANIZATION,"Manage", _manageOrgData),
    canChangeEntityOrganization(ORGANIZATION,"Change Entity", _canChangeEntityOrganization),


    //   USER DATA
    canRecoverPassword(USER,"Recover Password", _canRecoverPassword, true),
    canSeeUserEmail(USER,"See Email", _canSeeUserEmail),

    canImpersonate(USER,"Impersonate", _canImpersonate),

    canResetPassword(USER,"Reset Passwords", _canResetPassword),
    canVerifyAccount(USER,"Verify Password", _canVerifyAccount, true),

    readUserData(USER,"Read Profiles", _readUserData),
    manageUserData(USER,"Manage Profiles Data", _manageUserData),

    readUserRole(USER_ROLE,"Read", _readUserRole),
    manageUserRoles(USER_ROLE,"Manage", _manageUserRoles),


    //   HISTORY, LOGS, SYSTEM HEALTH
    canReadSupportData(SUPPORT,"Read", _canReadSupportData),
    canManageSupportData(SUPPORT,"Manage", _canManageSupportData),


    //TODO - to be removed
    readOrgAudit(HISTORY,"Read Organization History", _readOrgAudit),

    //   FRONTEND RESOURCE
    readFrontendResource(FRONTEND_RESOURCE,"Read", _readFrontendResource),
    manageFrontendResource(FRONTEND_RESOURCE,"Manage", _manageFrontendResource),

    // REFRESHER TOKEN
    canRefreshTokens(TOKEN,"Refresh", _canRefreshTokens),

    //ROLES, SERVER JS, EVENT LISTENERS, SCHEDULERS, THREADS
    canReadBackend(BACKEND,"Read", _canReadBackend),
    canManageBackend(BACKEND,"Manage", _canManageBackend);

    private String category = "General";
    private PrivilegeGroup group;
    private String label;
    private boolean hidden;

    Privilege(PrivilegeGroup group, String label, String nameCheck) {
        this(group, label, nameCheck,false);
    }

    Privilege(PrivilegeGroup group, String label, String nameCheck, boolean hidden) {
        this.label = label;
        this.group = group;
        this.hidden = hidden;
        checkName(nameCheck);
    }

    @Override
    public String getLabel() {
        return label;
    }

    @Override
    public PrivilegeGroup getGroup() {
        return group;
    }

    @Override
    public String getCategory() {
        return category;
    }

    @Override
    public boolean isHidden() {
        return hidden;
    }


}
