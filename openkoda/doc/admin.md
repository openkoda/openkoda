# Admin Menu

Dashboard admin functionalities are available only for users having the global Admin role in the system.
Navigating admin options is available through the admin menu which is placed in the sidebar on the left.

## System Health
Displays server's system statistics. Consists of the following parts:  

### Cluster

### Heap Memory
It shows three statistics: maximum heap size, heap size in use and free (available) heap size. All these numbers are in MB. 
Additionally, it displays heap size in use as percent of maximum heap size.

### Partitions
It presents all available partitions - their path, total space nad free space (both numbers in GB) and used space as percent of the total space.

### Others
Shows basic settings for database logging:
#### DB Log Statement
Displays the level of logging for database operations, as returned by the database query
`SELECT setting FROM pg_settings WHERE name = 'log_statement'`. Possible values:
* *none* - logging is turned off
* *ddl* - logs only ddl operations (like `CREATE`, `ALTER`, `DROP`)
* *mod* - logs ddl operations and data-modifying statements (like `INSERT`, `UPDATE`, `DELETE`)
* *all* - logs everything

#### Min DB Statement Duration
Displays the minimum execution time above which all statements are logged. Value `-1` is equal to off.
Based on the result of the database query `SELECT setting FROM pg_settings WHERE name = 'log_min_duration_statement'`.

### Task Statistics
Available only for Linux based servers. Requires sysstat tool being installed and enabled on the server.
Shows list of tasks currently being managed by the Linux kernel, as returned by the Linux command pidstat. 

## Logs
Allows to easily display a bunch of recent errors in the application. You can configure number of the errors visible (500 is default) and select which java classes should be included in the logging.

## Audit
Shows audit of important events in the application: user logging, create/update/delete of entities, errors that occured in the application.
It displays timestamp, id, IP address and description of the event. For errors it additionally allows to download full stack trace.
Search box to easily navigate through audit messages is also available.

## Event Listeners
Allows configuration of actions to be taken when an event occurs. 
Possible events: 
* user created, modified, deleted, registered, verified, logged in
* organization created, modified, deleted
* user role created, modified, deleted
* event listener created, modified, deleted
* trial activated, expired
* **application error**
* notification created
* backup created, backup file copied
* scheduler executed

Possible actions:
* create Cards on the specified List and Board on Trello
* create Issue on the specified repository on GitHub
* create Issue on the specified project on JIRA
* posts a To-Do to Basecamp To-Do List
* send a message to Slack channel
* send a message to MsTeams channel
* send an email
* and more

### Application Errors Tracking

Tack application error occurrences with event listeners. 

Steps to configure errors logging on Slack channel:
1. Go to `Admin -> Event Listeners -> New`
2. In the 'Event Name' dropdown select APPLICATION_ERROR
3. In the 'Consumer' methods list select `SlackService :: sendToSlackWithCanonical(CanonicalObject)`
4. Fill in the two mandatory static parameters:
   1. first is a name of the html template to generate the message, the basic one would be `frontend-resource/canonical-object-notification`, 
   2. the second is a URL to the webhook to your Slack channel where you want to track error messages, its value should 
   look similarly to `https://hooks.slack.com/services/<key>`
   
## Spoof
This functionality is available only for admins and allows to log into application on behalf of some other user.
Used mainly for errors investigation and fixing as it allows to see the application dashboard from the perspective of a spoofed user.
To spoof a user:
1. Go to `Admin -> Users`
2. Find the user and click "Spoof" 
3. To exit spoof click "Exit spoof" from the dropdown in the top right corner od the Dashboard.

# Configuration

## App Branding Setup

### Name and Description

Setup App's name and description by editing the .properties file: 
```
application.name=Your App Name
application.description=Your App Short Description
```

### Logo Links

Setup link for logo images in the Dashboard by editing the .properties file: 
```
# Organization logo path - when user has role in organization and has its context active
logo.image.href=/html/organization/%s/dashboard
# Global logo path - when user has no organization loaded
logo.image.href.global=/html/organization/all
```

### Openkoda Template Personalization

#### Replace Openkoda Dashboard Logo

Change the logo visible in the top left corner of the dashboard view by editing `main-page-fragments`
frontend resource in the dashboard.

To do so, you need to:
1. Go to Admin -> Files,
2. Upload your logo,
3. Copy its `Absolute URL Path`,
4. Go to Admin -> Resources,
5. Find Frontend Resource with the name `main-page-fragments`,
6. Click on that name,
7. When on Frontend Resource settings page, edit the Draft Content by pasting the path you just copied into the src attribute of the logo image,
```
<th:block th:fragment="logo">
<img class="logo" src="/your-logo-path.svg"/>
</th:block>
```
8. Save,
9. In table of Resources, row of `main-page-fragments`, pick dropdown in the column 'Draft' and select action 'Push To Live'.

#### Setup Logo Sent In Emails

Set up the logo which should be displayed in email messages sent from the application by setting 
the logo's path in the .properties file:
```
application.logo=/path/to/your/logo.png
```

_Important: The logo element needs to be present in application's email templates html to be visible in email messages. 
Below, there's an example of html code which would insert the logo image into the message._

```
<img src="cid:logo.png" alt="MyAppLogo"/>
```

## Auth methods

###General configuration

Openkoda allows to login with multiple authentication methods. Each of them requires definition of (some of) the following properties:

#### clientId
The application ID received after app registration in the appropriate authentication provider (e.g. Facebook App, Linkedin App etc.)

#### clientSecret
The application secret key received after app registration in the appropriate authentication provider (e.g. Facebook App, Linkedin App etc.)

#### accessTokenUri
URL used to obtain the access token

#### userAuthorizationUri
URL to which the user is to be redirected to authorize the access token

#### tokenName
The name of an access token used when sending requests with the token authentication. Default value is `access_token`.

#### scope
The scope of an access token

#### clientAuthenticationScheme
Method of transmitting client authentication credentials when obtaining an access token.
Possible values:
* *header* - send as the Authorization http header
* *query* - send a query parameter in the URI
* *form* - send in the form body
* *none* - do not send at all

The default value is `header`.

#### authenticationScheme
Method of transmitting a token when sending requests with the token authentication.
Possible values:
* *header* - send as the Authorization http header
* *query* - send a query parameter in the URI
* *form* - send in the form body
* *none* - do not send at all

The default value is `header`.

#### preEstablishedRedirectUri
The redirect URI that has been pre-established with the server (doesn't depend on the current request)

#### useCurrentUri
Set to `false` if `preEstablishedRedirectUri` should be used rather than the redirect URI extracted from the current request

#### userInfoUri
The URL of an endpoint returning the user info 

### Configurations of implemented authentication methods

All properties listed below can be edited in the `.properties` file.

`clientId` and `clientSecret` should be substituted with the real values.

#### Facebook

To enable Facebook OAuth2 Authentication:
1. Register App in Facebook Developers Console.
2. Copy Client ID and Client Secret from Facebook Developers Console and paste it into the Spring OAuth2 properties for Facebook.
3. Enable Facebook Authentication in Openkoda.


Openkoda Facebook OAuth2 list of properties:
```
authentication.facebook=true
spring.security.oauth2.client.registration.facebook.client-id=xxx
spring.security.oauth2.client.registration.facebook.client-secret=xxx
spring.security.oauth2.client.registration.facebook.scope=public_profile,email
spring.security.oauth2.client.provider.facebook.token-uri=https://graph.facebook.com/oauth/access_token
spring.security.oauth2.client.provider.facebook.authorization-uri=https://www.facebook.com/dialog/oauth
spring.security.oauth2.client.provider.facebook.user-info-uri=https://graph.facebook.com/me?fields=email,first_name,last_name,id,name,cover,picture,age_range
```

Useful links:
* [Create Facebook App](https://developers.facebook.com/docs/development/create-an-app/)
* [Get Facebook User Info](https://developers.facebook.com/docs/graph-api/reference/user/)


#### Google

To enable Google OAuth2 Authentication:
1. Register App in Google Developers Console and create Access Credentials.
2. Copy Client ID and Client Secret from Google Developers Console and paste it into the Spring OAuth2 properties for Google.
3. Enable Google Authentication in Openkoda.

Openkoda Google OAuth2 list of properties:

```
authentication.google=true
spring.security.oauth2.client.registration.google.client-id=xxx
spring.security.oauth2.client.registration.google.client-secret=xxx
spring.security.oauth2.client.registration.google.scope=email,profile
spring.security.oauth2.client.provider.google.authorization-uri=https://accounts.google.com/o/oauth2/auth
spring.security.oauth2.client.provider.google.token-uri=https://accounts.google.com/o/oauth2/token
spring.security.oauth2.client.provider.google.user-info-uri=https://www.googleapis.com/oauth2/v3/userinfo
spring.security.oauth2.client.provider.google.user-name-attribute=name
```

Useful links:
* [Google OAuth2 Documentation](https://developers.google.com/identity/protocols/oauth2/web-server)
* [Configure Consent Screen in Google Console](https://developers.google.com/workspace/guides/configure-oauth-consent)
* [Create Access Credentials in Google Console](https://developers.google.com/workspace/guides/create-credentials)

#### Salesforce

```
authentication.salesforce=true
salesforce.client.clientId=xxx
salesforce.client.clientSecret=xxx
salesforce.client.accessTokenUri=https://login.salesforce.com/services/oauth2/token
salesforce.client.userAuthorizationUri=https://login.salesforce.com/services/oauth2/authorize
#salesforce.client.clientAuthenticationScheme=form
#salesforce.client.authenticationScheme=header
salesforce.client.preEstablishedRedirectUri=${base.url}/connect/salesforce
salesforce.client.useCurrentUri=false
salesforce.resource.userInfoUri=https://login.salesforce.com/services/oauth2/userinfo
```

Useful links:
* [Salesforce Authentication Documentation](https://help.salesforce.com/s/articleView?id=sf.remoteaccess_oauth_flows.htm&type=5)
* [Salesforce User Data](https://help.salesforce.com/s/articleView?id=sf.remoteaccess_using_userinfo_endpoint.htm&type=5) 

#### LinkedIn

```
authentication.linkedin=true
linkedin.client.clientId=xxx
linkedin.client.clientSecret=xxx
linkedin.client.clientAuthenticationScheme=form
linkedin.client.authenticationScheme=header
linkedin.client.accessTokenUri=https://www.linkedin.com/oauth/v2/accessToken
linkedin.client.userAuthorizationUri=https://www.linkedin.com/oauth/v2/authorization
linkedin.client.preEstablishedRedirectUri=${base.url}/connect/linkedin
linkedin.client.useCurrentUri=false
linkedin.client.scope=r_liteprofile,r_emailaddress
linkedin.resource.userInfoUri=https://api.linkedin.com/v2/me?projection=(id,localizedFirstName,localizedLastName,profilePicture(displayImage~:playableStreams))
linkedin.resource.emailAddressUri=https://api.linkedin.com/v2/emailAddress?q=members&projection=(elements*(handle~))
```


Useful links:
* [LinkedIn Authentication Documentation](https://learn.microsoft.com/en-us/linkedin/shared/authentication/authorization-code-flow?tabs=HTTPS1#step-2-request-an-authorization-code)
* [LinkedIn Grant Types](https://www.linkedin.com/pulse/grant-types-oauth-20-overview-dhruv-patel?trk=public_profile_article_view) 
* [LinkedIn User Data](https://learn.microsoft.com/en-us/linkedin/shared/integrations/people/profile-api)



#### LDAP
To enable LDAP authentication, use the following flag
<pre><code>authentication.ldap=true</code></pre>
And fill the rest of the properties
<pre><code>#Your ldap url
ldap.urls= ldap://11.11.1.1:111/

#base for the directory name
ldap.base.dn= ou=people,dc=openkoda,dc=com

#principal name that performs user search
ldap.username= cn=search,dc=openkoda,dc=com

#principal password
ldap.password= xxx

#user search strategy
ldap.user.dn.pattern = uid={0}
</code></pre>

When correct configuration is provided, you can use the login form by providing username/password of a user from your LDAP instance

Useful links:
* [LDAP Documentation](https://ldap.com/ldap-urls/).



## Multitenancy

### Organization Delete

In case of multitenancy setup with many schemas, there's a separate schema created in the database per organization. 
Organization schemas are named following the pattern `org_<organization-ID>`, so for organization with the ID 121, schema name would be `org_121`.


In case of organization delete, Openkoda renames the schema of the deleted organization to `deleted_<organization-ID>`. 
So for the example mentioned above, the renamed schema name would be `deleted_121`.


## Integrations
You can integrate Openkoda with plenty of applications using the `.properties` file. 
Properties for each application are described in this section.

### Jira

#### Jira authorization link
```
api.jira.oauth.authorize=
```

If you don't have the authorization link, follow the 
[Jira Documentation](https://developer.atlassian.com/cloud/confluence/oauth-2-3lo-apps/).

#### OAuth 2.0 API token
```
api.jira.authorize.token
```

#### OAuth 2.0 refresh token
```
api.jira.refresh.token
```

#### Jira access scope
```
api.jira.access.scope
```
You can find Jira available scopes 
[here](https://developer.atlassian.com/cloud/jira/platform/scopes-for-oauth-2-3LO-and-forge-apps/).

#### URL for obtaining Jira Cloud ID
```
api.jira.get.cloudId
```

#### URL for obtaining Jira project list
```
api.jira.get.project.list
```

#### URL for obtaining Jira issue type list.
```
api.jira.get.issue.type.list
```

#### URL for creating an issue
```
api.jira.create.issue
```

### GitHub

#### Authorization link
```
api.github.oauth.authorize
```
If you don't have one, follow the
[GitHub Official Documentation](https://docs.github.com/en/rest/guides/basics-of-authentication?apiVersion=2022-11-28).

#### Authorization token
```
api.github.authorize.token
```

#### URL for creating issues
```
api.github.create.issue
```
For more information
[see here](https://docs.github.com/en/rest/issues?apiVersion=2022-11-28).

#### Access scope
```
api.github.access.scope
```
You can find GitHub available scopes
[here](https://docs.github.com/en/apps/oauth-apps/building-oauth-apps/scopes-for-oauth-apps).


### Trello

Set Trello boards, lists and cards URLs:

```
api.trello.get.boards
api.trello.get.lists
api.trello.create.board
api.trello.create.list
api.trello.create.card
```

Every URL property for this section might be found
[here](https://developer.atlassian.com/cloud/trello/rest/api-group-actions/).

### Slack

Set Slack Webhook to your channel:
```
attribute.webhook.slack
```
For more information check
[Slack documentation](https://api.slack.com/messaging/webhooks).

### MS Teams

Set MsTeams webhook for notifications:
```
attribute.webhook.ms-teams
```
For more information check
[Teams documentation](https://learn.microsoft.com/en-us/microsoftteams/platform/webhooks-and-connectors/how-to/add-incoming-webhook?tabs=dotnet).

### Basecamp

Set Basecamp authorization URL:
```
api.basecamp.oauth.authorize
```
If you don't have one, follow the
[Basecamp Official Documentation](https://github.com/basecamp/api/blob/master/sections/authentication.md).

Set Basecamp OAuth 2.0 API token:
```
api.basecamp.authorize.token
```

Set OAuth 2.0 refresh token:
```
api.basecamp.refresh.token
```

Set URL for posting messages:
```
api.basecamp.post.message
```
For more information see
[Basecamp Official Documentation](https://github.com/basecamp/bc3-api/blob/master/sections/messages.md#create-a-message).

### Backup
To enable backup functionality:

1. Create an event listener:
   1. Go to `Admin -> Event Listener -> New`
   2. Set event name to `SCHEDULER_EXECUTED`
   3. Select `BackupService :: doFullBackup(ScheduledSchedulerDto)` in `Consumer` field
2. Create a scheduler:
   1. Go to `Admin -> Schedulers -> New`
   2. Set a cron expression to set when the backup should be triggered, example value `0 0 23 * * MON` 
   would trigger the backup every Monday at 11 p.m.
   3. In the field `Event Data` type `backup` 

#### With the following properties you can control the way system performs backups.

Name of a directory where a backup file will be saved:
```
backup.file.directory
```

The pattern of a timestamp that will be appended to the backup filename. Default value `yyyyMMdd-HHmm` 
```
backup.date.pattern
```

List of comma separated backup options:
```
backup.options
```
 Available backup options:
* BACKUP_DATABASE - dump a whole database into an .sql file
* BACKUP_PROPERTIES - include in a backup a .properties file located under a path defined in `backup.application.properties`
* SCP_ENABLED - copy a backup archive file into a remote host defined in `backup.scp.host` 

A path to a .properties file that will be included in a backup: 
```
backup.application.properties
```
_To enable a .properties file in a backup add `BACKUP_PROPERTIES` to the `backup.options` property_ 


Full path to a GPG executable file. Default value is `gpg` 
```
backup.gpg.executable
```

GPG key name:
```
backup.gpg.key.name
```

A path to a GPG key file:
```
backup.gpg.key.file
```

The full path to a scp executable file. Default value is `scp`
```
backup.scp.executable
```

Address of a remote server where the backup file will be copied to: 
```
backup.scp.host
```

A path to a target folder a backup file will be copied:
```
backup.scp.target
```


The full path to a pg_dump executable file. Default value `pg_dump`:
```
backup.pg_dump.executable
```


## Mail

Configuration for Openkoda mailing services.

### Local

Running the application with the `local` profile enables printing email messages to the console instead 
of sending actual emails.

Sender mail address:
```
mail.from
```

Reply to mail address:
```
mail.replyTo
```

### SMTP

Running the application with the `smtp` profiles enables email messages to be sent with SMTP.

Host address:
```
spring.mail.host
```

Host port:
```
spring.mail.port
```

Username:
```
spring.mail.username
```

Password:
```
spring.mail.password
```

Enables SMTP authentication:
```
spring.mail.properties.mail.smtp.auth
```

Enables TLS-protected connection:
```
spring.mail.properties.mail.smtp.starttls.enable
```

### Mailgun

This is the default email sender in Openkoda.

API key:
```
mailgun.apikey
```

API URL:
```
mailgun.apiurl
```
Check [Mailgun Documentation](https://documentation.mailgun.com/en/latest/#mailgun-documentation) for more information.

## File storage

Set the type of storage:
```
file.storage.type
```

Possible values for storage type: 
* *database* - store as blob in the database 
* *filesystem* - store in the local filesystem
* *amazon* - store in AWS S3

Default value is `database`.

### Filesystem

Path to the storage location, default value is `/tmp`:
```
file.storage.filesystem.path
```

### AWS S3
The name of an AWS S3 Bucket in which files will be stored: 
```
file.storage.amazon.bucket
```
Note that the bucket name will be visible in a presigned URL used to download files from an AWS S3 server.

Time in seconds for a presigned url to be expired by an AWS S3 server, default value is `10`:
```
file.storage.amazon.presigned-url.expiry.time.seconds
```

# Maintenance

## Log files

By default, Openkoda will only log to the console and will not write log files. 
To enable writing log files in addition to the console output you need to set the `logging.file`
property in `.properties` file.

**Example**
```
logging.file=app.log
```

To set the logging level you need to set the `logging.level.<logger-name>=<level>` property in 
`.properties` file. 

To set the logging level for all packages you should put `root` in place of `<logger-name>`. 
In case you want to set the logging level for the specific package you should put the package in place of `<logger-name>`.  

Level can be one of the following values: TRACE, DEBUG, INFO, WARN, ERROR, FATAL, OFF.

**Example**
```
logging.level.root=DEBUG
logging.level.com.openkoda=DEBUG
```

## Application Tracking

### Logs

When logged into the Global Admin account in Openkoda it is possible to review logs actions history from there.

To review Logs, go to `Admin -> Logs`. There you can find a table with two columns representing log entries, 
first is the identification key, the second is log content.

#### Download

To download the logs click the button in the top right corner of the Logs card.

#### Settings

It is possible to change the logging scope by enabling or disabling particular loggers in the application. 
There's a button named `Settings` in the top right corner of the Logs card. It will redirect you to Logs settings page 
where you can enable loggers from the list of all available ones for the application. 

Once enabled, logs for selected loggers can be tracked in the Logs table. 

### Audit

Go to `Admin -> Audit` to review table containing the history of operations for the application. It is possible to filter 
content by typing ids or keywords in the search box in the top right corner of the Audit card.

For any entity properties considered as large content there's a button named `Content`. It is visible at the end of rows 
and triggers the download of a file containing the content. Due to its size it cannot be displayed in the table. 


## System Health

#### Dashboard

Go to `Admin -> System Health` to review system health dashboard.
It presents information about partitions and its usage, heap memory and other available data. 

#### Properties

In `.properties` file there are properties which point to system health check executables. These can be edited 
accordingly to your operating system and tools available.

```
system.cat.executable=cat
system.pidstat.executable=pidstat
system.apt.executable=apt
```

Openkoda reviews RAM, CPU and disk usage in a scheduled job. It produces error logs for any system health properties
outreaching percentage max limits.
Max limits have default values set to 75 percent and can be changed in `.properties` file.

```
max.disk.percentage=75
max.ram.percentage=75
max.cpu.percentage=75
```