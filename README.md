
<p align="center">
    <img alt="Openkoda Logo" src="https://github.com/openkoda/.github/assets/14223954/698c333f-4791-4c6b-95d4-aa6eff7dc6d3" width="30%"/>
</p>

<div align="center">
  <h3>Platform that speeds up building enterprise systems, SaaS applications, and internal tools.</h3>
</div>

<div align="center">
    <a href="https://opensource.org/licenses/MIT"><img alt="License: MIT" src="https://img.shields.io/badge/License-MIT-18B2C6.svg"/></a>
    <a href="https://openkoda.com/product/"><img alt="Openkoda: 1.7.1" src="https://img.shields.io/static/v1?label=Openkoda&message=1.7.1&color=18B2C6"/></a>
    <img alt="Java: 17.0.2" src="https://img.shields.io/static/v1?label=Java&message=17.0.2&color=18B2C6"/>
    <img alt="Spring Boot: 3.0.5" src="https://img.shields.io/static/v1?label=Spring%20Boot&message=3.0.5&color=18B2C6"/>
</div>

<br/>


* **Open-source, MIT license**
* **Build new applications & replace legacy systems.**
* **Deliver enterprise and SaaS applications 60% faster thanks to ready-to-use features.**
* **Unlimited users. No proprietary technologies. No vendor lock-in.**
* **Pre-built application templates: Embedded Insurance, Claim Management, Policy Management, plus Property Management and Time Tracking.**


### When to choose Openkoda

* **Enterprise foundation**. Start with authentication, role-based security, advanced user management, multitenancy, multiple organizations support, SQL reporting, REST API, file & resource management, full audit trail, application & data automated backup, and admin dashboard already in place.
* **Reduce development time and effort**. Use industry-specific application templates, pre-built functionalities and ready to use features.
* **Adopt a flexible and scalable approach**. Build applications with dynamic entities. Choose from multiple multi-tenancy models.
* **Use technology you already know**: Java, Spring Boot, JavaScript, HTML, Hibernate, PostgreSQL
* **Extend as you wish**. Openkoda offers unlimited customization and integration options.


![openkoda admin](https://github.com/openkoda/.github/assets/14223954/9acded2e-a3e6-4480-805e-7ac38ebdafc0)


### Contents

🚀 [Quick start](#-quick-start)\
💾 [Installation](#-installation)\
✅ [Features](#-features)\
🧩 [Integrations](#-integrations)\
👨‍💻 [Tech stack](#-tech-stack)\
💡 [Sample applications](#-sample-applications)\
💡 [Application screenshots](#-application-screenshots)\
💙 [Contribution](#-contribution)\
📜 [Release notes](#️-release-notes)\
🤝 [Partners](#-partners)

## 🚀 Quick start

The fastest way to start using Openkoda is to run the system with Docker Compose scripts and a Docker image that contains all the necessary prerequisites: Java, PostgreSQL, and Openkoda.

Make sure Docker Compose is installed on your system.

To download and start the complete system image, simply run this command in your terminal:
```
curl https://raw.githubusercontent.com/openkoda/openkoda/main/docker/docker-compose.yaml | docker compose -f - up
```

Once the startup is complete, you can access the Openkoda web application at the following local URL:
```
https://localhost:8080
```
Log in as an Administrator using the default credentials: **login: admin** and **password: admin123**.

### Importing Openkoda application template

You may find some existing sample application templates in the [examples](https://github.com/openkoda/openkoda/tree/main/examples) directory in this repository.

Download the selected application package (as a .zip archive), then import the application within the Openkoda web interface by selecting: **Configuration > Import/Export** from the menu.

**Note:** If the only message you see after starting the import process is "IMPORT FILE," it may indicate that no components were found in the uploaded package, or the .zip file is corrupted or empty. Double-check that the file is not empty on your local filesystem before uploading.



## 💾 Installation 


### Installation options

There are two installation options to start application development with Openkoda:
* Running as a Docker container
* Building from sources


### Option #1: Run as a Docker Container

Docker images are available at Docker Hub : https://hub.docker.com/r/openkoda/openkoda

Download the latest version of the Openkoda Docker image from Docker Hub:
```
docker pull openkoda/openkoda:latest
```

Please note that in that case Postgres needs to be already in place and `SPRING_DATASOURCE_URL`, `SPRING_DATASOURCE_USERNAME`, `SPRING_DATASOURCE_PASSWORD` env variables need to be adjusted when running docker ([see Docker Hub for detailed options](https://hub.docker.com/r/openkoda/openkoda))

#### Docker compose

A simpler option may be to use the Docker Compose scripts located in the: `./docker/docker-compose.yaml` and `./docker/docker-compose-no-db.yaml` - depending on your preference, with or without Postgres as a part of the docker service. Here is a useful one-liner :
```
curl https://raw.githubusercontent.com/openkoda/openkoda/main/docker/docker-compose.yaml | docker compose -f - up
```

### Option #2: Build from Sources

Prerequisites:

Git, Java 17+, Maven 3.8+, PostgreSQL 14+

1. [Create an empty PostgreSQL database](https://github.com/openkoda/openkoda.git)
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

### Running dev/test instance without https

Openkoda uses strict secure cookies setting to increase security.

If you want to run a development/test instance of Openkoda without https and not using localhost then you need to disable secure cookies setting by:

* Set the SECURE_COOKIE environment variable to false before starting docker compose, eg. SET SECURE_COOKIE=false (windows), SECURE_COOKIE=false (linux)
* You may need to delete your locally cached Openkoda docker images with docker rmi
* Then proceed as described here: https://github.com/openkoda/openkoda?tab=readme-ov-file#docker-compose


### Resolving installation issues

If you encounter any issues during the installation or when running Openkoda for the first time, please:

* Review the existing GitHub tickets, as a similar problem may have been reported before.
* If not, raise a new ticket, including steps to reproduce the issue and relevant logs—our development team regularly reviews reported issues.

## ✅ Features

To significantly reduce development time and effort, Openkoda offers pre-built functionality and out-of-the-box features.

(*enterprise options)

### 🧑‍💻 Enterprise Application Foundation

Build your application without losing time on functionality every application always needs. Start with authentication, user management, multitenancy, and admin dashboard already in place.

#### Openkoda Industry-Specific Application Templates

Start with our pre-built domain-specific Openkoda application templates giving you a solid base to build your custom applications. Get an enterprise foundation with a predefined data model, useful dashboards, and ready-to-use business features.

#### Customize and Extend Using Open-Source Java, JavaScript, and PostgreSQL

Extend your application with open-source, scalable, and high-performance Java and JavaScript (back-end), HTML/CSS (front-end), and PostgreSQL (database).

#### *Data Model Builder: Customize Your Data Model and Views with Development Kit UI

Easily update, rename, and create new tables and attributes in your custom data model using our Development Kit UI. Use our intelligent editor with an autocomplete feature to choose from a variety of data types, create dropdowns, multiselect, or conditional types of fields. All inputs are validated, and your changes are applied immediately to both UI and the database.

Dynamically configure your data screens – decide which attributes should be presented on the entity’s UI screen and which ones should be used as filters.

#### Dashboard Builder: Create Your Own Data Dashboards with Drag & Drop

Create useful dashboards for different user types and different organizations using pre-built widgets. Design custom views by combining widgets, images, and data tables in our drag & drop visual editor.

#### Modify Look & Feel of Your Application with Custom HTML/CSS

Craft a unique user experience by customizing HTML/CSS of your Openkoda application.

#### *Custom Business Logic

Define custom business logic from the UI using server-side JavaScript.

### 📊 Smart Reporting

#### SQL Reporting

Build reports and query your data using SQL. Define your periodic reports directly within Openkoda. Save and share useful reports and summaries across your coworkers.

#### *Reporting AI

Use natural language to create reports or query your application data ad-hoc. Iterate and update your prompt to adjust the results. Run AI-generated SQL queries without sharing your data outside the system. Save the report to run periodically or download it to Excel to share with your coworkers.

#### *Data Visualization

Visualize report data with a custom chart. Use ChartJs to visualize reports and embed it in dashboards and custom application screens.

### ⚙️Automations

#### Email Sender

Send emails to users, administrators or co-workers. Set up an email address for sending notifications, updates, and other communications.

#### In-App Notifications

Programmatically send in-app notifications to a specific user, to users in a specific organization, or globally within the application.

#### *Data-driven PDF, Word and Excel Document Generation

Generate PDF, Word and Excel documents and attachments based on data-driven dynamic placeholders, calculated fields (including multi-row and multi-column tables), and conditional selection of your visual document templates.

#### *Business Processes Automations
Use Custom Events and Event Listeners and automate your business processes and trigger specific actions, like: send a reminder to your client about overdue payment or alert your sales team about specific opportunities. Use Job Scheduler to define how often the system should trigger the event.

#### *Custom Automation Logic
Define custom business rules from the UI using server-side Javascript.

Automate your business processes and trigger specific actions, for example: "find contracts about to expire and send notification to the relevant parties, or send reminders for overdue payments", or "generate and send attachments for a new contract".

### 🔄 Custom Integrations

#### Automatically Updated REST API

Connect other applications to an automatically generated secure REST API for standard data model operations and endpoints implemented in your Openkoda application.

#### Custom Integrations

Use open-source Java or other standard programming languages to extend your system with custom integrations.

#### Integrations with External Applications

Enhance your application by integrating your application with the systems you already use, like: Slack, Discord, Basecamp, GitHub, Jira, Trello.

#### *Custom Integrations from the UI

Seamlessly connect with other systems and extend functionality with custom integration using intelligent server-side Javascript code editor.

### 👥 Advanced Security and User Management

#### Advanced User Management

Manage users across multiple organizations: invite new users, define user settings, apply roles globally and within organization context, manage passwords.

#### Role-Based Security Model

Define and manage user roles based on access privileges defined on the application, organization, table, individual object attribute, application view or at any other level. Create a precisely secured environment for your coworkers, customers and partners.

#### Custom Privileges

Define your custom access privileges types from UI to configure even more adjusted and secured environment.

### 🏢Scalable Multitenancy

#### Multiple Organizations Support

Create as many organizations, co-existing within a single Openkoda instance, as you want (organizations in a single database). Personalize the organizations with different logos and color palette.

#### *Advanced Multitenancy for Multiple Organizations Support
Introduce physical data separation and increase application scalability with advanced multi-tenancy models: organizations in separate schemas and organizations in separate databases.

#### Clustering
High-availability, high-reliability clustering deployment options.

See [multitenancy setup](https://github.com/openkoda/openkoda/blob/main/openkoda/doc/installation.md#multitenancy-setup) for more details

### 🗂️ Flexible File Management

#### File & Resource Management

Centralize and efficiently manage all your files and resources stored in a database or local filesystem. Simplify access for your users to your digital assets. Store and manage your documents, images and files within your Openkoda application. Select database or local filesystem for physical storage. Simplify access to your digital assets.

#### *Advanced File & Resource Management
Store and manage your documents, images and files within your Openkoda application. Select database, local filesystem or Amazon S3 for physical storage. Simplify access to your digital assets. Centralize and efficiently manage all your files and resources in one location. Simplify access for your users to your digital assets.

### 📥Import/Export your Data and Customizations

#### Export Data to CSV/Excel
Easily download your filtered data directly to Excel/CSV for further analysis.

#### *Import Data from CSV/Excel
Upload your CSV/Excel file to create new and update existing records. Ensure data quality with built-in import validation.

#### Components Import & Export
Simplify application management process. Upgrade your system easily by importing new application components (including: dashboards, forms, views, web endpoints, event listeners, schedulers, server side code) from a single ZIP file. Export and move your customizations across Openkoda instances.

#### *Maven Project Package
The exported ZIP archive with your Openkoda application contains a ready to use Maven project, so that you easily set up your local Java development environment and start extending your application with your favourite IDE.

### 🛠️Control and Maintenance

#### Full Audit Trail

Automatically track all changes made to any data within the application. Audit trail logs detailed records of modifications, ensuring transparency and accountability for all user actions.

#### Application & Data Backup System

Automatically backup your application and data to ensure security and recovery. Easily schedule and manage the backup process to protect your system against data loss and system failures.

#### System Health & System Logs
Monitor the actual health and performance of your system with real-time insights. Access detailed system logs to track events, diagnose issues, and ensure optimal operation.

## 🧩 Integrations

The following integrations are already implemented to enhance your application:

#### Openkoda Open Source

<div>
    <img height="40" alt="logo-slack" src="https://github.com/openkoda/openkoda/assets/14223954/bffafc23-6a72-4a8b-86cf-4a073cfe9c3b"/>&nbsp;&nbsp;
    <img height="40" alt="logo-discord" src="https://github.com/openkoda/openkoda/assets/14223954/f3b72e42-04c1-42c1-b268-76ef524a805c"/>&nbsp;&nbsp;
    <img height="40" alt="logo-basecamp" src="https://github.com/openkoda/openkoda/assets/14223954/d26eccd9-39f2-4cc5-af86-e3e74bad95cf"/>&nbsp;&nbsp;
    <img height="40" alt="logo-github" src="https://github.com/openkoda/openkoda/assets/14223954/ba648de4-4cbf-4007-aff4-82c94942a65d"/>&nbsp;&nbsp;
    <img height="40" alt="logo-jira" src="https://github.com/openkoda/openkoda/assets/14223954/e45c0174-e07e-49dc-bdf5-2c1415538682">&nbsp;&nbsp;
    <img height="40" alt="logo-trello" src="https://github.com/openkoda/openkoda/assets/14223954/f18841b5-e6ad-4807-9b9c-f0d5622300f7">&nbsp;&nbsp;
</div>


#### Openkoda Enterprise

<div>
    <img height="40" alt="logo-google" src="https://github.com/openkoda/openkoda/assets/14223954/ae5cb4fd-4fb2-43ab-9a4a-3ca1deaf1aaf"/>&nbsp;&nbsp;
    <img height="40" alt="logo-facebook" src="https://github.com/openkoda/openkoda/assets/14223954/42620407-eb57-4a04-a67e-6bc7bbbb4e7c"/>&nbsp;&nbsp;
    <img height="40" alt="logo-stripe" src="https://github.com/openkoda/openkoda/assets/14223954/33594d22-07f6-4a20-ad71-f18cbb428fc4"/>&nbsp;&nbsp;
    <img height="40" alt="logo-ms-teams" src="https://github.com/openkoda/openkoda/assets/14223954/61b60851-b821-4f94-8fe5-e7af910017ce"/>&nbsp;&nbsp;
    <img height="40" alt="logo-ldap" src="https://github.com/openkoda/openkoda/assets/14223954/47058144-3584-4059-9239-42d7192b475a"/>&nbsp;&nbsp;
</div>

## 👨‍💻 Tech stack
* Java (17+)
* Spring Boot 3.x
* Hibernate
* PostgreSQL
* GraalVM

## 💡 Sample applications

Openkoda Application Templates are sample applications built with Openkoda.

They represent a standard set of functions for a traditional web application provided by Openkoda Core, as well as business functionalities created specifically for these examples. 

Application Templates can be easily extended, taking into account both the data storage schema and any custom functionality.

Learn more in our [5-minute guide](https://github.com/openkoda/openkoda/blob/main/openkoda/doc/5-minute-guide.md).

### Timelog - Time Tracking / Timesheets Application

Timelog is a time tracking solution for companies of all sizes. 

It allows employees to record hours spent on specific tasks, while managers generate monthly performance reports.

[Learn more](https://openkoda.com/time-tracking-software/)

![timelog user](https://github.com/openkoda/openkoda/assets/14223954/ecaf54d2-6112-4c45-a67f-15ac7b150452)

![timelog admin](https://github.com/openkoda/openkoda/assets/14223954/e9669bef-5929-4fd6-92e8-8e35865a9261)


### Insurance Policy Management

Insurance Policy Management is a dynamic policy data storage tool with a variety of embeddable widgets for personalized customer and policy dashboards. 

Widgets include: message senders, email schedulers, attachment and task lists, notes, and detailed customer/policy information to improve operational efficiency and customer engagement. [Learn more](https://openkoda.com/insurance-policy-management-software/).

![insurance user](https://github.com/openkoda/openkoda/assets/14223954/cb2f4065-59a4-42da-915d-4fd3d810fc19)

![insurance admin](https://github.com/openkoda/openkoda/assets/14223954/ac47b4ba-246e-4772-b47c-69bbfe8512fe)

### Weather Appllication

A simple application that provides weather forecast for selected vacation spots.

Watch the short video to see the building process:

[![How to build a weather app in less than 20 minutes?](https://github.com/openkoda/openkoda/assets/10715247/19c670f1-281f-463c-b93c-0715ebef6402)](https://youtu.be/gob4j072Isg)

## 💡 Application screenshots

CMS

<img alt="openkoda-frontendresource-all" src="https://github.com/openkoda/.github/assets/14223954/3e4e5563-53d3-4e7b-9ccf-8a69ea346bc1"/>

Organization Settings

<img alt="openkoda-organization-settings" src="https://github.com/openkoda/.github/assets/14223954/275135d1-6c99-48fa-9224-008183d02085"/>

Job Request

<img alt="openkoda-job-request" src="https://github.com/openkoda/openkoda/assets/14223954/2d26ddfd-3bee-4cc3-a4e0-be08d522bc96"/>

Event Listener

<img alt="openkoda-event-listener" src="https://github.com/openkoda/openkoda/assets/14223954/ac5d52b5-5509-4f37-b5b2-b7a3d9aaa631"/>

Forgot Password

<img alt="openkoda-forgot-password" src="https://github.com/openkoda/openkoda/assets/14223954/f4c78aca-dc1d-4f42-8ba2-903d641a4229"/>

### 💙 Contribution

Openkoda is an open source project under [MIT license](https://github.com/openkoda/openkoda/blob/main/LICENSE). It’s built by developers for developers. 

If you have ideas for improvement, contribute and let's innovate together. 

How to contribute:
1. Create a fork
2. Create a feature branch from main branch
3. Push
4. Create a Pull Request to an upstream main branch

[**Detailed contribution rules**](https://github.com/openkoda/openkoda/blob/main/openkoda/CONTRIBUTING.md)

## 📢 Follow, learn, and spread the word 

[Openkoda Community](https://github.com/orgs/openkoda/repositories): Become a part of Openkoda\
[YouTube](https://www.youtube.com/channel/UCN0LzuxOYIDdKDX9W0sGFlg): Learn how to use Openkoda\
[LinkedIn](https://www.linkedin.com/company/openkoda): Stay up to date\
[About us](https://openkoda.com/about-us/): Let us introduce ourselves

## 🗃️ Release notes

Openkoda is constantly evolving. Check out the changelog:

### Openkoda 1.7.1 🚀

* Support for dynamic privileges management
* Optimize custom dashboard view
* Manually create data reports with sql queries
* Improve dashboard UI
* Filter dynamic entities by their attributes
* Refactor components .zip export
* Update Insurance App components .zip
* Fix issues and bugs

### Openkoda 1.5

* **Dynamic Entities**: Now create database tables, perform full CRUD operations and generate forms.
* **New Dashboard UI**: Enhanced for better readability and smoother navigation flow.
* **Files Assignment**: Support for dynamically registered entities.
* **Organization-Level Email Configuration**: Customize email settings at the organization level.
* **Bug Fixes**: Various fixes for improved app stability and performance.

### Openkoda 1.4.3

* **Page Builder**: Introducing a tool for creating custom dashboards.
* **Web Forms Assistance**: Streamlined web form creation based on your data model definitions.
* **YAML Components Import/Export**: Easily manage components such as web forms, endpoints, server code, event listeners, schedulers, and frontend resources.
* **Dashboard UI**: Upgrades for an improved dashboard interface.
* **Updates & Security**: Minor adjustments and security fixes.

## 🤝 Partners

**Openkoda source code is completely free and is available under the [MIT license](https://github.com/openkoda/openkoda/blob/main/LICENSE).​**

Join us as a partner in transforming the software development market by delivering maximum value to your clients using Openkoda. The goal is to simplify the process of building enterprise applications, allowing developers to focus on core business logic.

Learn more about [Openkoda Partner Program](https://openkoda.com/partners/).
