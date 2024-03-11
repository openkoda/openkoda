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

package com.openkoda.integration.model;

import com.openkoda.model.PrivilegeBase;
import com.openkoda.model.PrivilegeGroup;

/**
 * @author Martyna Litkowska (mlitkowska@stratoflow.com)
 * @since 2019-10-02
 */
public enum IntegrationPrivilege implements PrivilegeBase, IntegrationPrivilegeName {

    canIntegrateWithSlack("Slack", _canIntegrateWithSlack),
    canIntegrateWithMsTeams("Ms Teams", _canIntegrateWithMsTeams),
    canIntegrateWithGitHub("GitHub", _canIntegrateWithGitHub),
    canIntegrateWithJira("Jira", _canIntegrateWithJira),
    canIntegrateWithBasecamp("Basecamp", _canIntegrateWithBasecamp),
    canIntegrateWithTrello("Trello", _canIntegrateWithTrello);

    private String label;
    private String category = "Integration";

    IntegrationPrivilege(String label, String nameCheck) {
        this.label = label;
        checkName(nameCheck);
    }

    @Override
    public String getLabel() {
        return label;
    }

    @Override
    public PrivilegeGroup getGroup() {
        return null;
    }

    @Override
    public String getCategory() {
        return category;
    }

}
