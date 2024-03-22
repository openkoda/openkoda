<p align="center">
    <img alt="Openkoda Logo" src="https://github.com/openkoda/.github/assets/14223954/698c333f-4791-4c6b-95d4-aa6eff7dc6d3" width="70%"/>
</p>

<div align="center">
  <h3>Ready-to-use development platform that accelerates the process of building business applications and internal tools.</h3>
</div>

<div align="center">
    <a href="https://opensource.org/licenses/MIT"><img alt="License: MIT" src="https://img.shields.io/badge/License-MIT-18B2C6.svg"/></a>
    <a href="https://openkoda.com/product/"><img alt="Openkoda: 1.5.1" src="https://img.shields.io/static/v1?label=Openkoda&message=1.5.1&color=18B2C6"/></a>
    <img alt="Java: 17.0.2" src="https://img.shields.io/static/v1?label=Java&message=17.0.2&color=18B2C6"/>
    <img alt="Spring Boot: 3.0.5" src="https://img.shields.io/static/v1?label=Spring%20Boot&message=3.0.5&color=18B2C6"/>
</div>

<br/>

* **Reduce development time and effort**. Use pre-built functionalities and out-of-the-box features.
* **Adopt a flexible and scalable approach**. Build applications with dynamic entities. Choose from multiple multi-tenancy models.
* **Use technology you already know**: Java, Spring Boot, JavaScript, HTML, Hibernate, PostgreSQL
* **Extend as you wish**. Openkoda offers unlimited customization and integration options.

![openkoda admin](https://github.com/openkoda/.github/assets/14223954/9acded2e-a3e6-4480-805e-7ac38ebdafc0)


### 📌Contents

🧩 [Integrations](#-integrations)\
🚀 [How to start](#-getting-started)\
✅ [Out-of-the-box features](#-out-of-the-box-features)\
👨‍💻 [Tech stack](#-tech-stack)\
💡 [Sample applications](#-sample-applications)\
💙 [Contribution](#-contribution)\
📜 [Release notes](#️-release-notes)\
🤝 [Partners](#-partners)

### 🧩 Integrations

Enhance your application by adding integrations.

#### Open Source

<div>
    <img height="60" alt="logo-slack" src="https://github.com/openkoda/openkoda/assets/14223954/bffafc23-6a72-4a8b-86cf-4a073cfe9c3b"/>&nbsp;&nbsp;
    <img height="60" alt="logo-discord" src="https://github.com/openkoda/openkoda/assets/14223954/f3b72e42-04c1-42c1-b268-76ef524a805c"/>&nbsp;&nbsp;
    <img height="60" alt="logo-basecamp" src="https://github.com/openkoda/openkoda/assets/14223954/d26eccd9-39f2-4cc5-af86-e3e74bad95cf"/>&nbsp;&nbsp;
    <img height="60" alt="logo-github" src="https://github.com/openkoda/openkoda/assets/14223954/ba648de4-4cbf-4007-aff4-82c94942a65d"/>&nbsp;&nbsp;
    <img height="40" alt="logo-jira" src="https://github.com/openkoda/openkoda/assets/14223954/e45c0174-e07e-49dc-bdf5-2c1415538682">&nbsp;&nbsp;
    <img height="60" alt="logo-trello" src="https://github.com/openkoda/openkoda/assets/14223954/f18841b5-e6ad-4807-9b9c-f0d5622300f7">&nbsp;&nbsp;
</div>


#### Enterprise

<div>
    <img height="60" alt="logo-google" src="https://github.com/openkoda/openkoda/assets/14223954/ae5cb4fd-4fb2-43ab-9a4a-3ca1deaf1aaf"/>&nbsp;&nbsp;
    <img height="60" alt="logo-facebook" src="https://github.com/openkoda/openkoda/assets/14223954/42620407-eb57-4a04-a67e-6bc7bbbb4e7c"/>&nbsp;&nbsp;
    <img height="60" alt="logo-stripe" src="https://github.com/openkoda/openkoda/assets/14223954/33594d22-07f6-4a20-ad71-f18cbb428fc4"/>&nbsp;&nbsp;
    <img height="60" alt="logo-ms-teams" src="https://github.com/openkoda/openkoda/assets/14223954/61b60851-b821-4f94-8fe5-e7af910017ce"/>&nbsp;&nbsp;
    <img height="60" alt="logo-ldap" src="https://github.com/openkoda/openkoda/assets/14223954/47058144-3584-4059-9239-42d7192b475a"/>&nbsp;&nbsp;
</div>

### 👨‍💻 Tech stack
* Java (17+)
* Spring Boot 3.x
* Hibernate
* PostgreSQL
* GraalVM

### 🚀 Getting started

#### Installation

There are two installation options to start application development with Openkoda:
* Building from sources
* Running as a Docker container

#### Option #1: Build from Source

Prerequisites:

Git, Java 17+, Maven 3.8+, PostgreSQL 14+

1. [Create an empty database](https://github.com/openkoda/openkoda.git)
2. Clone or download this Git repository
3. Build application with maven:
```
mvn -f openkoda/pom.xml clean install spring-boot:repackage -DskipTests
```
4. Initialize the database in a first run:
```
java -Dloader.path=/BOOT-INF/classes -Dspring.profiles.active=openkoda,drop_and_init_database -jar openkoda.jar --server.port=<http port>
```
5. Run Openkoda 
```
java -Dloader.path=/BOOT-INF/classes -Dsecure.cookie=false -jar openkoda.jar --spring.profiles.active=openkoda --server.port=<http port>
```

Detailed instructions can be found in the [Installation](https://github.com/openkoda/openkoda/blob/main/openkoda/doc/installation.md) manual.

#### Option #2: Run as a Docker Container

Docker images are available at Docker Hub : https://hub.docker.com/r/openkoda/openkoda

It can be launched via simple:
```
docker pull openkoda/openkoda:latest
```

Please note that in that case Postgres needs to be already in place and `SPRING_DATASOURCE_URL`, `SPRING_DATASOURCE_USERNAME`, `SPRING_DATASOURCE_PASSWORD` env variables needs to be adjusted when running docker ([see Docker Hub for detailed options](https://hub.docker.com/r/openkoda/openkoda))

##### Docker compose

A simpler option may be to use the Docker Compose scripts located in the: `./docker/docker-compose.yaml` and `./docker/docker-compose-no-db.yaml` - depending on your preference, with or without Postgres as a part of the docker service.

CMS

<img alt="openkoda-frontendresource-all" src="https://github.com/openkoda/.github/assets/14223954/3e4e5563-53d3-4e7b-9ccf-8a69ea346bc1"/>

Organization Settings

<img alt="openkoda-organization-settings" src="https://github.com/openkoda/.github/assets/14223954/275135d1-6c99-48fa-9224-008183d02085"/>

Job Request

<img alt="openkoda-job-request" src="https://github.com/openkoda/openkoda/assets/14223954/2d26ddfd-3bee-4cc3-a4e0-be08d522bc96"/>

Event Litener

<img alt="openkoda-event-listener" src="https://github.com/openkoda/openkoda/assets/14223954/ac5d52b5-5509-4f37-b5b2-b7a3d9aaa631"/>

Forgot Password

<img alt="openkoda-forgot-password" src="https://github.com/openkoda/openkoda/assets/14223954/f4c78aca-dc1d-4f42-8ba2-903d641a4229"/>

### ✅ Out-of-the-box features

To significantly reduce development time and effort, Openkoda offers pre-built functionality and out-of-the-box features.

#### 🔀 Dynamic entities: 
Create database table, CRUD functionality, form, and overview with no need of re-compilation

#### 🛠️ Application admin panel:
* **App Configurations**: Manage email settings, roles, privileges, and HTML templates.
* **Audit Screen**: Track changes to data for accountability.
* **System Logs**: Review logs for activity insights and troubleshooting.
* **System Health**: Get a quick overview of system performance and status.

#### 👤User Management
* Invite users to the organization
* Set roles globally and within the organization context
* Access user profile settings
* Spoof user (available in admin mode)

#### 🔑 Roles and Privileges
* Create global or organization-specific roles
* Assign privileges from a list to each role

#### 🏢 Organization management
* Separate organization data
* Implement security rules for data access
* Customize your own dashboard 
* Assign organizational roles, such as member or admin, to users.

#### 📝 CMS
* Modify HTML templates
* Edit draft versions of resources
* Introduce new public resources

#### 🗂️ Resource Management
* Manage file overview
* Resize images
* Set files to public access

#### 🔊 Event Listeners: 
Respond to application events (e.g., user creation, login, application start) with built-in Openkoda handlers (e.g., messaging, push notifications).

#### 💾 Backups: 
Embedded database backup routines

#### 📥 Import and export: 
Export components from current app and easily import them into another Openkoda Core instance

#### 🗄️ Multiple Multi-tenancy models: 
Openkoda supports the following multi-tenancy setups:
* Single Database / Single Schema
* Single Database / Many Schemas
* Multiple Databases / Many Schemas

See [multitenancy setup](https://github.com/openkoda/openkoda/blob/main/openkoda/doc/installation.md#multitenancy-setup) for more details

#### 🔄 Job Requests
Schedule jobs to be performed in time intervals
Process jobs with event listeners

#### 🔔 Notifications

Synchronize your application with notifications channels:
Email
Slack
Jira
GitHub
Trello
Basecamp


#### ✉️ Email Sender
Customize email templates via CMS
Schedule emails

### 💡 Sample applications

Openkoda Application Templates are sample applications built with Openkoda.

They represent a standard set of functions for a traditional web application provided by Openkoda Core, as well as business functionalities created specifically for these examples. 

Application Templates can be easily extended, taking into account both the data storage schema and any custom functionality.

Learn more in our [5-minute guide](https://github.com/openkoda/openkoda/blob/main/openkoda/doc/5-minute-guide.md).

**Timelog**

Timelog is a time tracking solution for companies of all sizes. It allows employees to record hours spent on specific tasks, while managers generate monthly performance reports. [Learn more](https://openkoda.com/time-tracking-software/).

![timelog user](https://github.com/openkoda/openkoda/assets/14223954/ecaf54d2-6112-4c45-a67f-15ac7b150452)
![timelog admin](https://github.com/openkoda/openkoda/assets/14223954/e9669bef-5929-4fd6-92e8-8e35865a9261)

**Insurance Policy Management** 

Insurance Policy Management is a dynamic policy data storage tool with a variety of embeddable widgets for personalized customer and policy dashboards. 
Widgets include: message senders, email schedulers, attachment and task lists, notes, and detailed customer/policy information to improve operational efficiency and customer engagement. [Learn more](https://openkoda.com/insurance-policy-management-software/).

![insurance user](https://github.com/openkoda/openkoda/assets/14223954/cb2f4065-59a4-42da-915d-4fd3d810fc19)
![insurance admin](https://github.com/openkoda/openkoda/assets/14223954/ac47b4ba-246e-4772-b47c-69bbfe8512fe)


### 💙 Contribution

Openkoda is an open source project under [MIT license](https://github.com/openkoda/openkoda/blob/main/LICENSE). It’s built by developers for developers. 

If you have ideas for improvement, contribute and let's innovate together. 

How to contribute:
1. Create a fork
2. Create a feature branch from main branch
3. Push
4. Create a Pull Request to an upstream main branch

[**Detailed contribution rules**](https://github.com/openkoda/openkoda/blob/main/openkoda/CONTRIBUTING.md)

### 📢 Follow, learn, and spread the word 

[Openkoda Community](https://github.com/orgs/openkoda/repositories): Become a part of Openkoda\
[YouTube](https://www.youtube.com/channel/UCN0LzuxOYIDdKDX9W0sGFlg): Learn how to use Openkoda\
[LinkedIn](https://www.linkedin.com/company/openkoda): Stay up to date\
[About us](https://openkoda.com/about-us/): Let us introduce ourselves

### 🗃️ Release notes

Openkoda is constantly evolving. Check out the changelog:

#### Openkoda 1.5. 🚀

* **Dynamic Entities**: Now create database tables, perform full CRUD operations and generate forms.
* **New Dashboard UI**: Enhanced for better readability and smoother navigation flow.
* **Files Assignment**: Support for dynamically registered entities.
* **Organization-Level Email Configuration**: Customize email settings at the organization level.
* **Bug Fixes**: Various fixes for improved app stability and performance.

#### Openkoda 1.4.3. 

* **Page Builder**: Introducing a tool for creating custom dashboards.
* **Web Forms Assistance**: Streamlined web form creation based on your data model definitions.
* **YAML Components Import/Export**: Easily manage components such as web forms, endpoints, server code, event listeners, schedulers, and frontend resources.
* **Dashboard UI**: Upgrades for an improved dashboard interface.
* **Updates & Security**: Minor adjustments and security fixes.

### 🤝 Partners

**Openkoda source code is completely free and is available under the [MIT license](https://github.com/openkoda/openkoda/blob/main/LICENSE).​**

Join us as a partner in transforming the software development market by delivering maximum value to your clients using Openkoda. The goal is to simplify the process of building enterprise applications, allowing developers to focus on core business logic.

Learn more about [Openkoda Partner Program](https://openkoda.com/partners/).

#### ☁️ Managed Cloud

Our enterprise managed cloud allows for easy deployment and scaling of your Openkoda applications. [Contact us](https://openkoda.com/contact/) for more information.

##
<div style="text-align: center;" align="center">
    <a href="https://www.facebook.com/Openkoda/"><img alt="Openkoda Facebook" src="openkoda/src/main/resources/public/vendor/fontawesome-free/svgs/brands/facebook.svg" width="20px"></a>
    <a href="https://www.linkedin.com/company/openkoda"><img alt="Openkoda Facebook" src="openkoda/src/main/resources/public/vendor/fontawesome-free/svgs/brands/linkedin.svg" width="20px" style="margin-left: 10px"></a>
</div>
