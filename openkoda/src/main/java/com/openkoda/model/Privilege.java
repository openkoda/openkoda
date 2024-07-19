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

    isUser(GLOBAL_SETTINGS, 1, "Is User", _isUser),
    //   ADMIN
    canAccessGlobalSettings(GLOBAL_SETTINGS, 2, "Access", _canAccessGlobalSettings),

    //ORGANIZATION DATA
    readOrgData(ORGANIZATION, 3, "Read", _readOrgData),
    manageOrgData(ORGANIZATION, 4, "Manage", _manageOrgData),
    canChangeEntityOrganization(ORGANIZATION, 5, "Change Entity", _canChangeEntityOrganization),
    canUseReportingAI(ORGANIZATION, 6, "Use Reporting AI", _useReportingAI),
    canCreateReports(ORGANIZATION, 7, "Create Reports", _createReports),
    canReadReports(ORGANIZATION, 8, "Read Reports", _readReports),


    //   USER DATA
    canRecoverPassword(USER, 9, "Recover Password", _canRecoverPassword, true),
    canSeeUserEmail(USER, 10, "See Email", _canSeeUserEmail),

    canImpersonate(USER, 11, "Impersonate", _canImpersonate),

    canResetPassword(USER, 12, "Reset Passwords", _canResetPassword),
    canVerifyAccount(USER, 13, "Verify Password", _canVerifyAccount, true),

    readUserData(USER, 14, "Read Profiles", _readUserData),
    manageUserData(USER, 15, "Manage Profiles Data", _manageUserData),

    readUserRole(USER_ROLE, 16, "Read", _readUserRole),
    manageUserRoles(USER_ROLE, 17, "Manage", _manageUserRoles),


    //   HISTORY, LOGS, SYSTEM HEALTH
    canReadSupportData(SUPPORT, 18, "Read", _canReadSupportData),
    canManageSupportData(SUPPORT, 19, "Manage", _canManageSupportData),


    //TODO - to be removed
    readOrgAudit(HISTORY, 20, "Read Organization History", _readOrgAudit),

    //   FRONTEND RESOURCE
    readFrontendResource(FRONTEND_RESOURCE, 21, "Read", _readFrontendResource),
    manageFrontendResource(FRONTEND_RESOURCE, 22, "Manage", _manageFrontendResource),

    // REFRESHER TOKEN
    canRefreshTokens(TOKEN, 23, "Refresh", _canRefreshTokens),

    //ROLES, SERVER JS, EVENT LISTENERS, SCHEDULERS, THREADS
    canReadBackend(BACKEND, 24, "Read", _canReadBackend),
    canManageBackend(BACKEND, 25, "Manage", _canManageBackend),
    
    canImportData(ORGANIZATION, 26, "Import Data", _canImportData)
    ;

    private Long id;
    private String category = "General";
    private PrivilegeGroup group;
    private String label;
    private boolean hidden;

    Privilege(PrivilegeGroup group, long id, String label, String nameCheck) {
        this(group, id, label, nameCheck,false);
    }

    Privilege(PrivilegeGroup group, long id, String label, String nameCheck, boolean hidden) {
        this.id = id * this.idOffset();
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

    @Override
    public Long getId() {
        return this.id;
    }

    @Override
    public int idOffset() { return 1; }
}
