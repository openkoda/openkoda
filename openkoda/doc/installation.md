# Prerequisites: Linux server setup

## Ubuntu Linux
Linux server with Ubuntu version 18.04.1 LTS or higher (latest LTS now is 22.04) secured according to the best standards.

### Update APT database:
```
apt update
```

## PostgreSQL Database Server
PostgreSQL database is used as a permanent data store.

### Install PostgreSQL Database 14.4 or higher
```
apt install postgresql-14
```

## CertBot
CertBot is used to enable HTTPS
```
apt install snapd
snap install certbot --classic
root@openkoda:~# certbot --version
certbot 1.31.0
```

## NGINX
NGINX is our request redirection service.

### Install NGINX
```
apt install nginx
```
### Start Nginx service
```
service nginx start
```

## Create NGINX configuration
Create configuration file for your domain in `/etc/nginx/conf.d.`
Filename should follow the pattern: `yourdomain.com.conf`

Example of a simple configuration file:
```
server {
        server_name     openkoda.com;

        location / {
                proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
                proxy_set_header X-Forwarded-Proto $scheme;
                proxy_set_header X-Forwarded-Port $server_port;
                proxy_pass http://127.0.0.1:8060/;
                proxy_buffer_size          32k;
                proxy_buffers              8 32k;
                proxy_busy_buffers_size    32k;
                proxy_read_timeout 3600s;
        }
}
```
### To test if the configuration is correct run:
```
nginx -t
```
## Reload NGINX configuration
```
service nginx reload
```
## Generate Certbot
```
certbot -d openkoda.com
```

## OpenJDK
Openkoda core applications run on JVM.
### Java 17
https://download.java.net/java/GA/jdk17.0.2/dfd4a8d0985749f896bed50d7138ee7f/8/GPL/openjdk-17.0.2_linux-x64_bin.tar.gz
```
cd /usr/lib/
mkdir jvm
cd jvm/
wget https://download.java.net/java/GA/jdk17.0.2/dfd4a8d0985749f896bed50d7138ee7f/8/GPL/openjdk-17.0.2_linux-x64_bin.tar.gz
tar -xzf openjdk-17.0.2_linux-x64_bin.tar.gz
```
Logout and log in.

Confirm available Java runtime version:

```
root@openkoda:~# java --version
openjdk 17.0.2 2022-01-18
OpenJDK Runtime Environment (build 17.0.2+8-86)
OpenJDK 64-Bit Server VM (build 17.0.2+8-86, mixed mode, sharing)
```

## Openkoda installation
### Create database in PostgreSQL
```
su postgres
psql
postgres=# create database openkoda;
postgres=# alter role postgres with password 'sample-password';
```
###Build Openkoda application
Maven (3.8.6+) will be required:
https://maven.apache.org/install.html

Build application with maven:

```
mvn -f openkoda/pom.xml clean install spring-boot:repackage -DskipTests
```

### Database initialization
Openkoda has a dedicated Spring profile to generate the application database objects, first creating the objects resulting from defined @Entity classes, then running custom db scripts from db-init directory.

The first run should be manually executed to initialize the data model:

<pre><code>java -Dloader.path=/BOOT-INF/classes -Dspring.profiles.active=openkoda,<b>drop_and_init_database</b> -jar openkoda.jar --server.port=8030</code></pre>

Once the execution is completed, the data model should be populated and the database is ready for the first production run.

Optionally, a property for initialization scripts can be configured in property file:
```
#Comma separated list of sql scripts for global database initialization
global.initialization.scripts.commaseparated=
```

#### Init Scripts

There are some initialization scripts which need to be run manually to enable the full functionality of Openkoda.

Scripts to run after the database init are available in `/etc` folder:
* init.sql
* remove_organization_by_id.sql

### Run Openkoda application
```
java -Dloader.path=/BOOT-INF/classes -Dsecure.cookie=false -jar openkoda.jar --spring.profiles.active=openkoda --server.port=8030
```

## Multitenancy Setup
### Single Database / Single Schema
All organizations data is stored in one database and all in the same public schema.

This is the default strategy.

It is enabled when the property listed below is set to false.
```
is.multitenancy=false
```

### Single Database / Many Schemas
All organizations data is stored in one database but there’s a separate schema for each organization.
So the data of an organization which has the ID equal to 121 will be stored in a schema named ```org_121```.

Set of properties enabling this form of multitenant architecture is listed below.

```
is.multitenancy=true
spring.jpa.properties.hibernate.multiTenancy=SCHEMA
spring.jpa.properties.hibernate.tenant_identifier_resolver=com.openkoda.core.multitenancy.TenantResolver
spring.jpa.properties.hibernate.multi_tenant_connection_provider=com.openkoda.core.multitenancy.HybridMultiTenantConnectionProvider

#The only datasource configuration being the primary one
datasources.list[0].name=primary
datasources.list[0].config.jdbcUrl=jdbc:postgresql://localhost:5432/openkoda
datasources.list[0].config.username=postgres
datasources.list[0].config.password=sample-password
```
Optional: 
```
#Comma separated list of sql scripts for the new tenant(organization) initialization
tenant.initialization.scripts.commaseparated=

#Comma separated list of database table names that should created for a new tenant(organization) initialization 
tenant.initialization.table.names.commaseparated=
```

### Multiple Databases / Many Schemas
Multiple databases multitenancy is similar to Single Database / Many Schemas approach. 
The key difference is there should be defined multiple datasources and (optionally) there can be a strategy for database selection per client request.

```
#Multitenancy Configuration

is.multitenancy=true
spring.jpa.properties.hibernate.multiTenancy=SCHEMA
spring.jpa.properties.hibernate.tenant_identifier_resolver=com.openkoda.core.multitenancy.TenantResolver
spring.jpa.properties.hibernate.multi_tenant_connection_provider=com.openkoda.core.multitenancy.HybridMultiTenantConnectionProvider

#Multiple datasources configuration

datasources.list[0].name=primary
datasources.list[0].config.jdbcUrl=jdbc:postgresql://localhost:5432/openkoda
datasources.list[0].config.username=postgres
datasources.list[0].config.password=sample-password

datasources.list[1].name=secondary
datasources.list[1].config.jdbcUrl=jdbc:postgresql://localhost:5432/openkoda_2
datasources.list[1].config.username=postgres
datasources.list[1].config.password=sample-password
…
datasources.list[n].name=nthdatasource
datasources.list[n].config.jdbcUrl=jdbc:postgresql://localhost:5432/openkoda_n
datasources.list[n].config.username=postgres
datasources.list[n].config.password=sample-password

#Optional custom database allocation strategy

#Qualified class name extending TenantDatabaseAllocationStrategy that implements tenant database selection strategy (default com.openkoda.core.configuration.DefaultTenantDatabaseAllocationStrategy)
multitenancy.database_allocation_strategy_class=

#Free form string passed a hint to the strategy constructor. Allows parametrization and/or fine tuning of the strategy.
multitenancy.database_allocation_strategy_class.hint=
```

## Maintanance
### Available application logs
**Openkoda:**

Application properties which set logs filename and logging level for Openkoda: 
```
logging.file=app.log
logging.level.com.openkoda=DEBUG
```
**PostgreSQL:**
```
/var/log/postgresql/postgresql-14-main.log
```

## Docker Installation
Will be available soon.
It is included in the Openkoda Roadmap.
