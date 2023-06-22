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

package com.openkoda.model;

import com.openkoda.core.security.OrganizationUser;
import com.openkoda.dto.user.BasicUser;
import com.openkoda.model.authentication.*;
import com.openkoda.model.common.*;
import jakarta.persistence.*;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.Formula;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Entity
/**
 * <p>User class.</p>
 *
 * @author Arkadiusz Drysch (adrysch@stratoflow.com)
 * 
 */
@DynamicUpdate
@Table(name = "users",
        uniqueConstraints = @UniqueConstraint(columnNames = ModelConstants.EMAIL))
public class User extends TimestampedEntity implements AuditableEntity, SearchableEntity, EntityWithRequiredPrivilege, IsManyOrganizationsRelatedEntity {

    @Id
    @SequenceGenerator(name = GLOBAL_ID_GENERATOR, sequenceName = GLOBAL_ID_GENERATOR, initialValue = ModelConstants.INITIAL_GLOBAL_VALUE, allocationSize = 10)
    @GeneratedValue(generator = ModelConstants.GLOBAL_ID_GENERATOR, strategy = GenerationType.SEQUENCE)
    private Long id;

    private String firstName;
    private String lastName;

    @Formula("(coalesce(first_name, '')||' '||coalesce(last_name, ''))")
    private String name;

    @Column(name = ModelConstants.EMAIL)
    private String email;

    private boolean enabled;
    private boolean tokenExpired;

    @Column
    private String language;

    @OneToMany(mappedBy = "user", fetch = FetchType.EAGER, targetEntity = UserRole.class)
    private Collection<UserRole> roles = Collections.emptyList();

    @OneToOne(cascade = CascadeType.ALL, mappedBy = "user")
    @PrimaryKeyJoinColumn
    private FacebookUser facebookUser;

    @OneToOne(cascade = CascadeType.ALL, mappedBy = "user")
    @PrimaryKeyJoinColumn
    private GoogleUser googleUser;

    @OneToOne(cascade = CascadeType.ALL, mappedBy = "user")
    @PrimaryKeyJoinColumn
    private LoginAndPassword loginAndPassword;

    @OneToOne(cascade = CascadeType.ALL, mappedBy = "user")
    @PrimaryKeyJoinColumn
    private LDAPUser ldapUser;

    @OneToOne(cascade = CascadeType.ALL, mappedBy = "user")
    @PrimaryKeyJoinColumn
    private SalesforceUser salesforceUser;

    @OneToOne(cascade = CascadeType.ALL, mappedBy = "user")
    @PrimaryKeyJoinColumn
    private LinkedinUser linkedinUser;

    @OneToOne(cascade = CascadeType.ALL, mappedBy = "user")
    @PrimaryKeyJoinColumn
    private ApiKey apiKey;

    private String picture;

    @Column
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime lastLogin;

    @Formula("( CASE id WHEN " + ModelConstants.USER_ID_PLACEHOLDER + " THEN NULL ELSE '" + PrivilegeNames._readUserData + "' END )")
    private String requiredReadPrivilege;

    @Formula("( '" + PrivilegeNames._manageUserData + "' )")
    private String requiredWritePrivilege;

    @ElementCollection
    @CollectionTable(name = "user_property",
            joinColumns = {
                    @JoinColumn(name = "user_id", referencedColumnName = "id")
            })
    @MapKeyColumn(name = "name")
    @Column(name = "value")
    private Map<String, String> properties = new HashMap<>();


    /**
     * <p>Constructor for User.</p>
     */
    public User() {
    }

    /**
     * <p>Constructor for User.</p>
     *
     * @param googleUser a {@link com.openkoda.model.authentication.GoogleUser} object.
     */
    public User(GoogleUser googleUser) {
        this.email = googleUser.getEmail().toLowerCase();
        this.name = googleUser.getName();
        this.firstName = googleUser.getFirstName();
        this.lastName = googleUser.getLastName();
        this.picture = googleUser.getPicture();
        this.googleUser = googleUser;
        this.enabled = true;
    }

    /**
     * <p>Constructor for User.</p>
     *
     * @param facebookUser a {@link com.openkoda.model.authentication.FacebookUser} object.
     */
    public User(FacebookUser facebookUser) {
        this.email = facebookUser.getEmail().toLowerCase();
        this.name = facebookUser.getName();
        this.firstName = facebookUser.getFirstName();
        this.lastName = facebookUser.getLastName();
        this.picture = facebookUser.getPicture();
        this.facebookUser = facebookUser;
        this.enabled = true;
    }

    public User(LDAPUser ldapUser) {
        this.email = ldapUser.getEmail().toLowerCase();
        this.name = ldapUser.getCn();
        this.firstName = ldapUser.getGivenName();
        this.lastName = ldapUser.getSn();
        this.ldapUser = ldapUser;
        this.enabled = true;
    }

    public User(SalesforceUser salesforceUser) {
        this.email = salesforceUser.getEmail().toLowerCase();
        this.name = salesforceUser.getName();
        this.firstName = salesforceUser.getFirstName();
        this.lastName = salesforceUser.getLastName();
        this.picture = salesforceUser.getPicture();
        this.salesforceUser = salesforceUser;
        this.enabled = true;
    }

    public User(LinkedinUser linkedinUser) {
        this.email = linkedinUser.getEmail().toLowerCase();
        this.firstName = linkedinUser.getFirstName();
        this.lastName = linkedinUser.getLastName();
        this.picture = linkedinUser.getProfilePicture();
        this.linkedinUser = linkedinUser;
        this.enabled = true;
    }

    /**
     * <p>Constructor for User.</p>
     *
     * @param firstName a {@link java.lang.String} object.
     * @param lastName a {@link java.lang.String} object.
     * @param email a {@link java.lang.String} object.
     */
    public User(String firstName, String lastName, String email) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email.toLowerCase();
    }

    /**
     * <p>Constructor for User.</p>
     *
     * @param id a {@link java.lang.Long} object.
     */
    public User(Long id) {
        this.id = id;
    }

    /**
     * <p>Getter for the field <code>id</code>.</p>
     *
     * @return a {@link java.lang.Long} object.
     */
    public Long getId() {
        return id;
    }

    /**
     * <p>Getter for the field <code>firstName</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * <p>Setter for the field <code>firstName</code>.</p>
     *
     * @param firstName a {@link java.lang.String} object.
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * <p>Getter for the field <code>lastName</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * <p>Setter for the field <code>lastName</code>.</p>
     *
     * @param lastName a {@link java.lang.String} object.
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     * <p>Getter for the field <code>email</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getEmail() {
        return email;
    }

    /**
     * <p>Setter for the field <code>email</code>.</p>
     *
     * @param email a {@link java.lang.String} object.
     */
    public void setEmail(String email) {
        this.email = email != null ? email.toLowerCase() : null;
    }

    /**
     * <p>Getter for the field <code>loginAndPassword</code>.</p>
     *
     * @return a {@link com.openkoda.model.authentication.LoginAndPassword} object.
     */
    public LoginAndPassword getLoginAndPassword() {
        return loginAndPassword;
    }

    /**
     * <p>Setter for the field <code>loginAndPassword</code>.</p>
     *
     * @param loginAndPassword a {@link com.openkoda.model.authentication.LoginAndPassword} object.
     */
    public void setLoginAndPassword(LoginAndPassword loginAndPassword) {
        this.loginAndPassword = loginAndPassword;
    }

    /**
     * <p>Setter for the field <code>loginAndPassword</code>.</p>
     *
     * @param login a {@link java.lang.String} object.
     * @param plainPassword a {@link java.lang.String} object.
     */
    public void setLoginAndPassword(String login, String plainPassword, boolean enabled) {
        this.loginAndPassword = new LoginAndPassword(login, plainPassword, this, enabled);
    }

    /**
     * <p>isEnabled.</p>
     *
     * @return a boolean.
     */
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * <p>Setter for the field <code>enabled</code>.</p>
     *
     * @param enabled a boolean.
     */
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    /**
     * <p>isTokenExpired.</p>
     *
     * @return a boolean.
     */
    public boolean isTokenExpired() {
        return tokenExpired;
    }

    /**
     * <p>Setter for the field <code>tokenExpired</code>.</p>
     *
     * @param tokenExpired a boolean.
     */
    public void setTokenExpired(boolean tokenExpired) {
        this.tokenExpired = tokenExpired;
    }

    /**
     * <p>Getter for the field <code>roles</code>.</p>
     *
     * @return a {@link java.util.Collection} object.
     */
    public Collection<UserRole> getRoles() {
        return roles;
    }

    /**
     * <p>Setter for the field <code>roles</code>.</p>
     *
     * @param roles a {@link java.util.Collection} object.
     */
    public void setRoles(Collection<UserRole> roles) {
        this.roles = roles;
    }

    /** {@inheritDoc} */
    @Override
    public String toAuditString() {
        return StringUtils.isNotEmpty(firstName) && StringUtils.isNotEmpty(lastName) ?
                String.format("%s %s, %s", firstName, lastName, email) : String.format("%s", email);
    }

    @Column(name = INDEX_STRING_COLUMN, length = INDEX_STRING_COLUMN_LENGTH, insertable = false)
    @ColumnDefault("''")
    private String indexString;

    /** {@inheritDoc} */
    @Override
    public String getIndexString() {
        return indexString;
    }

    /**
     * <p>projectRolesVisibleTo.</p>
     *
     * @param u a {@link java.util.Optional} object.
     * @return a {@link java.util.List} object.
     */
    public List<UserRole> projectRolesVisibleTo(Optional<OrganizationUser> u) {
        if (!u.isPresent()) {
            return Collections.emptyList();
        }
        Collection<Long> organizationIds = u.get().getOrganizationIds();
        boolean returnAll = u.get().hasGlobalPrivilege(Privilege.canReadBackend);
        return roles.stream().filter( a -> !a.isGlobal() && (returnAll || organizationIds.contains(a.getOrganizationId()))).collect(Collectors.toList());
    }

    public List<UserRole> projectRolesVisibleForOrg(Long orgId) {
        if (orgId == null) {
            return Collections.emptyList();
        }
        return roles.stream().filter( a -> !a.isGlobal() && Objects.equals(orgId, a.getOrganizationId())).collect(Collectors.toList());
    }

    /**
     * <p>Getter for the field <code>name</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getName() {
        return name == null ? this.email : name;
    }

    /**
     * <p>Getter for the field <code>facebookUser</code>.</p>
     *
     * @return a {@link com.openkoda.model.authentication.FacebookUser} object.
     */
    public FacebookUser getFacebookUser() {
        return facebookUser;
    }

    /**
     * <p>Getter for the field <code>googleUser</code>.</p>
     *
     * @return a {@link com.openkoda.model.authentication.GoogleUser} object.
     */
    public GoogleUser getGoogleUser() {
        return googleUser;
    }

    /**
     * <p>Getter for the field <code>picture</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getPicture() {
        return picture;
    }

    /**
     * <p>Setter for the field <code>picture</code>.</p>
     *
     * @param picture a {@link java.lang.String} object.
     */
    public void setPicture(String picture) {
        this.picture = picture;
    }

    /**
     * <p>Setter for the field <code>facebookUser</code>.</p>
     *
     * @param facebookUser a {@link com.openkoda.model.authentication.FacebookUser} object.
     */
    public void setFacebookUser(FacebookUser facebookUser) {
        this.facebookUser = facebookUser;
    }

    /**
     * <p>Setter for the field <code>googleUser</code>.</p>
     *
     * @param googleUser a {@link com.openkoda.model.authentication.GoogleUser} object.
     */
    public void setGoogleUser(GoogleUser googleUser) {
        this.googleUser = googleUser;
    }

    public LDAPUser getLdapUser() {
        return ldapUser;
    }

    public void setLdapUser(LDAPUser ldapUser) {
        this.ldapUser = ldapUser;
    }

    public SalesforceUser getSalesforceUser() {
        return salesforceUser;
    }

    public void setSalesforceUser(SalesforceUser salesforceUser) {
        this.salesforceUser = salesforceUser;
    }

    public LinkedinUser getLinkedinUser() {
        return linkedinUser;
    }

    public void setLinkedinUser(LinkedinUser linkedinUser) {
        this.linkedinUser = linkedinUser;
    }

    public String getLoginMethods() {
        List<String> loginMethods = new ArrayList<>();
        if(this.getLoginAndPassword() != null) loginMethods.add("Login / Password");
        if(this.getFacebookUser() != null) loginMethods.add("Facebook");
        if(this.getGoogleUser() != null) loginMethods.add("Google");
        if(this.getLdapUser() != null) loginMethods.add("LDAP");
        if(this.getSalesforceUser() != null) loginMethods.add("Salesforce");
        if(this.getLinkedinUser() != null) loginMethods.add("Linkedin");
        return String.join(", ", loginMethods);
    }

    public LocalDateTime getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(LocalDateTime loggedIn) {
        this.lastLogin = loggedIn;
    }

    public void setUpdatedOn(LocalDateTime updatedOn) {
        this.updatedOn = updatedOn;
    }

    @Override
    public String getRequiredReadPrivilege() {
        return requiredReadPrivilege;
    }

    @Override
    public String getRequiredWritePrivilege() {
        return requiredWritePrivilege;
    }

    @Formula("(select array_agg(ur.organization_id) from users_roles ur where ur.organization_id is not null and ur.user_id = id)")
//    @Type(value = com.openkoda.core.customisation.LongArrayType.class)
    @JdbcTypeCode(SqlTypes.ARRAY)
    private Long[] organizationIds;

    @Override
    public Long[] getOrganizationIds() {
        return organizationIds;
    }

    public ApiKey getApiKey() {
        return apiKey;
    }

    public void setApiKey(ApiKey apiKey) {
        this.apiKey = apiKey;
    }

    public String getLanguage() { return language; }

    public void setLanguage(String language) { this.language = language; }

    public BasicUser getBasicUser() {
        BasicUser user = new BasicUser();
        user.setId(this.id);
        user.setEmail(this.email);
        user.setFirstName(this.firstName);
        user.setLastName(this.lastName);
        return user;
    }

    public Set<LoggedUser.AuthenticationMethods> getAuthenticationMethods() {
        Set<LoggedUser.AuthenticationMethods> result = new HashSet<>();
        if (this.getLoginAndPassword() != null) { result.add(LoggedUser.AuthenticationMethods.PASSWORD); }
        if (this.getGoogleUser() != null) { result.add(LoggedUser.AuthenticationMethods.SOCIAL_GOOGLE); }
        if (this.getLinkedinUser() != null) { result.add(LoggedUser.AuthenticationMethods.SOCIAL_LINKEDIN); }
        if (this.getSalesforceUser() != null) { result.add(LoggedUser.AuthenticationMethods.SOCIAL_SALESFORCE); }
        if (this.getFacebookUser() != null) { result.add(LoggedUser.AuthenticationMethods.SOCIAL_FACEBOOK); }
        if (this.getLdapUser() != null) { result.add(LoggedUser.AuthenticationMethods.LDAP); }
        return result;
    }

    public String getGlobalRoleName() {
        List<Role> globals = roles.stream()
                .map(a -> a.getRole())
                .filter(role -> role.getType().equals("GLOBAL"))
                .collect(Collectors.toList());
        return globals.isEmpty() ? null : globals.get(0).getName();
    }

    public String getProperty(String name) {
        return properties.get(name);
    }

    public String setProperty(String name, String value) {
        return properties.put(name, value);
    }

}