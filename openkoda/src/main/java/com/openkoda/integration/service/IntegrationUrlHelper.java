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

package com.openkoda.integration.service;

import com.openkoda.controller.common.URLConstants;
import com.openkoda.core.tracker.LoggingComponentWithRequestId;
import com.openkoda.integration.controller.common.IntegrationURLConstants;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component("integrationUrl")
public class IntegrationUrlHelper implements URLConstants, LoggingComponentWithRequestId {

    @Value("${base.url:http://localhost:8080}")
    private String baseUrl;

//    INTEGRATION FORMS
    public String trelloFormUrl(Long orgId) {
        debug("[trelloFormUrl]");
        return _HTML_ORGANIZATION + '/' + orgId + IntegrationURLConstants._INTEGRATION + _SETTINGS + IntegrationURLConstants._TRELLO;
    }

    public String gitHubFormUrl(Long orgId) {
        debug("[gitHubFormUrl]");
        return _HTML_ORGANIZATION + '/' + orgId + IntegrationURLConstants._INTEGRATION + _SETTINGS + IntegrationURLConstants._GITHUB;
    }

    public String basecampFormUrl(Long orgId) {
        debug("[basecampFormUrl]");
        return _HTML_ORGANIZATION + '/' + orgId + IntegrationURLConstants._INTEGRATION + _SETTINGS + IntegrationURLConstants._BASECAMP;
    }

    public String jiraFormUrl(Long orgId) {
        debug("[gitHubFormUrl]");
        return _HTML_ORGANIZATION + '/' + orgId + IntegrationURLConstants._INTEGRATION + _SETTINGS + IntegrationURLConstants._JIRA;
    }

    public String slackFormUrl(Long orgId) {
        debug("[slackFormUrl]");
        return _HTML_ORGANIZATION + '/' + orgId + IntegrationURLConstants._INTEGRATION + _SETTINGS + IntegrationURLConstants._SLACK;
    }

    public String msTeamsFormUrl(Long orgId) {
        debug("[msTeamsFormUrl]");
        return _HTML_ORGANIZATION + '/' + orgId + IntegrationURLConstants._INTEGRATION + _SETTINGS + IntegrationURLConstants._MSTEAMS;
    }

//    TOKEN ACCESS
    public String gitHubTokenAccessUrl(Long orgId) {
        debug("[gitHubTokenAccessUrl]");
        return baseUrl + _HTML_ORGANIZATION + '/' + orgId + IntegrationURLConstants._INTEGRATION + IntegrationURLConstants._GITHUB;
    }

    public String jiraTokenAccessUrl(Long orgId) {
        return baseUrl + _HTML_ORGANIZATION + '/' + orgId + IntegrationURLConstants._INTEGRATION + IntegrationURLConstants._JIRA;
    }

    public String basecampRedirectUrl(Long orgId) {
        debug("[basecampRedirectUrl]");
        return baseUrl + _HTML_ORGANIZATION + '/' + orgId + IntegrationURLConstants._INTEGRATION + IntegrationURLConstants._BASECAMP;
    }

//    DISABLE INTEGRATION
    public String jiraDisableUrl(Long orgId) {
        return _HTML_ORGANIZATION + "/" + orgId + IntegrationURLConstants._INTEGRATION + _SETTINGS + IntegrationURLConstants._JIRA + IntegrationURLConstants._DISCONNECT;
    }

    public String basecampDisableUrl(Long orgId) {
        return _HTML_ORGANIZATION + "/" + orgId + IntegrationURLConstants._INTEGRATION + _SETTINGS + IntegrationURLConstants._BASECAMP + IntegrationURLConstants._DISCONNECT;
    }

    public String trelloDisableUrl(Long orgId) {
        return _HTML_ORGANIZATION + "/" + orgId + IntegrationURLConstants._INTEGRATION + _SETTINGS + IntegrationURLConstants._TRELLO + IntegrationURLConstants._DISCONNECT;
    }

    public String gitHubDisableUrl(Long orgId) {
        return _HTML_ORGANIZATION + "/" + orgId + IntegrationURLConstants._INTEGRATION + _SETTINGS + IntegrationURLConstants._GITHUB + IntegrationURLConstants._DISCONNECT;
    }

    public String msTeamsDisableUrl(Long orgId) {
        return _HTML_ORGANIZATION + "/" + orgId + IntegrationURLConstants._INTEGRATION + _SETTINGS + IntegrationURLConstants._MSTEAMS + IntegrationURLConstants._DISCONNECT;
    }

    public String slackDisableUrl(Long orgId) {
        return _HTML_ORGANIZATION + "/" + orgId + IntegrationURLConstants._INTEGRATION + _SETTINGS + IntegrationURLConstants._SLACK + IntegrationURLConstants._DISCONNECT;
    }

}
