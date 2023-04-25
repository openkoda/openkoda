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

package com.openkoda.integration.model.configuration;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.openkoda.model.Organization;
import com.openkoda.model.common.ModelConstants;
import com.openkoda.model.common.OrganizationRelatedEntity;
import jakarta.persistence.*;
import org.hibernate.annotations.Formula;

import static com.openkoda.model.common.ModelConstants.*;

@Entity
public class IntegrationModuleOrganizationConfiguration implements OrganizationRelatedEntity {

    public IntegrationModuleOrganizationConfiguration() {
    }

    /**
     * <p>Constructor for IntegrationModuleOrganizationConfiguration.</p>
     *
     * @param organizationId a {@link Long} object.
     */

    public IntegrationModuleOrganizationConfiguration(Long organizationId) {
        this.organizationId = organizationId;
    }

    @Id
    @SequenceGenerator(name = ORGANIZATION_RELATED_ID_GENERATOR, sequenceName = ORGANIZATION_RELATED_ID_GENERATOR, initialValue = ModelConstants.INITIAL_ORGANIZATION_RELATED_VALUE, allocationSize = 10)
    @GeneratedValue(generator = ORGANIZATION_RELATED_ID_GENERATOR, strategy = GenerationType.SEQUENCE)
    private Long id;

    @JsonIgnore
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(nullable = true, insertable = false, updatable = false, name = ORGANIZATION_ID)
    private Organization organization;
    @Column(nullable = true, name = ORGANIZATION_ID)
    private Long organizationId;

    @Formula(DEFAULT_ORGANIZATION_RELATED_REFERENCE_FIELD_FORMULA)
    private String referenceString;

    //        Trello
    public String trelloApiToken;
    public String trelloApiKey;
    public String trelloBoardName;
    public String trelloListName;
    //        GitHub
    public String gitHubToken;
    public String gitHubRepoName;
    public String gitHubRepoOwner;

    //        Basecamp
    public String basecampAccessToken;
    public String basecampRefreshToken;
    public String basecampAccountId;
    public String basecampProjectId;
    public String basecampToDoListId;
    public String basecampToDoListUrl;

    //        JIRA
    public String jiraToken;
    public String jiraRefreshToken;
    public String jiraOrganizationName;
    public String jiraProjectName;

    //        Slack
    public String slackWebhookUrl;

    //        Ms Teams
    public String msTeamsWebhookUrl;

    //        Email
    public String email;

    public String getTrelloApiKey() {
        return trelloApiKey;
    }

    public void setTrelloApiKey(String trelloApiKey) {
        this.trelloApiKey = trelloApiKey;
    }

    public String getTrelloApiToken() {
        return trelloApiToken;
    }

    public void setTrelloApiToken(String trelloApiToken) {
        this.trelloApiToken = trelloApiToken;
    }

    public String getTrelloBoardName() {
        return trelloBoardName;
    }

    public void setTrelloBoardName(String trelloBoardName) {
        this.trelloBoardName = trelloBoardName;
    }

    public String getTrelloListName() {
        return trelloListName;
    }

    public void setTrelloListName(String trelloListName) {
        this.trelloListName = trelloListName;
    }

    public String getGitHubToken() {
        return gitHubToken;
    }

    public void setGitHubToken(String gitHubToken) {
        this.gitHubToken = gitHubToken;
    }

    public String getGitHubRepoName() {
        return gitHubRepoName;
    }

    public void setGitHubRepoName(String gitHubRepoName) {
        this.gitHubRepoName = gitHubRepoName;
    }

    public String getGitHubRepoOwner() {
        return gitHubRepoOwner;
    }

    public void setGitHubRepoOwner(String gitHubRepoOwner) {
        this.gitHubRepoOwner = gitHubRepoOwner;
    }

    public String getSlackWebhookUrl() {
        return slackWebhookUrl;
    }

    public void setSlackWebhookUrl(String slackWebhookUrl) {
        this.slackWebhookUrl = slackWebhookUrl;
    }

    public String getMsTeamsWebhookUrl() {
        return msTeamsWebhookUrl;
    }

    public void setMsTeamsWebhookUrl(String msTeamsWebhookUrl) {
        this.msTeamsWebhookUrl = msTeamsWebhookUrl;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getJiraToken() {
        return jiraToken;
    }

    public void setJiraToken(String jiraToken) {
        this.jiraToken = jiraToken;
    }

    public String getJiraRefreshToken() {
        return jiraRefreshToken;
    }

    public void setJiraRefreshToken(String jiraRefreshToken) {
        this.jiraRefreshToken = jiraRefreshToken;
    }

    public String getJiraOrganizationName() {
        return jiraOrganizationName;
    }

    public void setJiraOrganizationName(String jiraOrganizationName) {
        this.jiraOrganizationName = jiraOrganizationName;
    }

    public String getJiraProjectName() {
        return jiraProjectName;
    }

    public void setJiraProjectName(String jiraProjectName) {
        this.jiraProjectName = jiraProjectName;
    }

    public String getBasecampAccountId() {
        return basecampAccountId;
    }

    public void setBasecampAccountId(String basecampAccountId) {
        this.basecampAccountId = basecampAccountId;
    }

    public String getBasecampAccessToken() {
        return basecampAccessToken;
    }

    public void setBasecampAccessToken(String basecampAccessToken) {
        this.basecampAccessToken = basecampAccessToken;
    }

    public String getBasecampRefreshToken() {
        return basecampRefreshToken;
    }

    public void setBasecampRefreshToken(String basecampRefreshToken) {
        this.basecampRefreshToken = basecampRefreshToken;
    }

    public String getBasecampProjectId() {
        return basecampProjectId;
    }

    public void setBasecampProjectId(String basecampProjectId) {
        this.basecampProjectId = basecampProjectId;
    }

    public String getBasecampToDoListId() {
        return basecampToDoListId;
    }

    public void setBasecampToDoListId(String basecampToDoListId) {
        this.basecampToDoListId = basecampToDoListId;
    }

    public String getBasecampToDoListUrl() {
        return basecampToDoListUrl;
    }

    public void setBasecampToDoListUrl(String basecampToDoListUrl) {
        this.basecampToDoListUrl = basecampToDoListUrl;
    }

    @Override
    public String getReferenceString() {
        return referenceString;
    }

    @Override
    public Long getOrganizationId() {
        return organizationId;
    }

    @Override
    public Long getId() {
        return id;
    }

    public Organization getOrganization() {
        return organization;
    }

    public void setOrganization(Organization organization) {
        this.organization = organization;
    }

    public void setOrganizationId(Long organizationId) {
        this.organizationId = organizationId;
    }
}
