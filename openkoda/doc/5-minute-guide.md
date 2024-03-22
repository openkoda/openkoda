# 5 Minute Guide

### Build Openkoda Core
In the main catalogue: 

```mvn clean install```

### Use Application Templates

* [Timelog](#timelog)
* [Insurance Policy Management](#insurance-policy-management)

## Timelog

### Build

In `examples/timelog` catalogue:

```mvn install  spring-boot:repackage```

### Initialize Database

Create an empty database named `timelog`. Alternatively adjust settings in `src/main/resources/application-timelog.properties`.

In `examples/timelog/target` catalogue: 

```
java -Dloader.path=/BOOT-INF/classes -Dspring.profiles.active=openkoda,drop_and_init_database,timelog -jar timelog-0.1.0.jar --server.port=8080
```

### Run

In `examples/timelog/target` catalogue: 

```
java -Dloader.path=/BOOT-INF/classes -Dsecure.cookie=false -jar timelog-0.1.0.jar --server.port=8080
```

## Insurance Policy Management

Insurance Policy Management application .zip is available [here](https://github.com/openkoda/openkoda/tree/main/examples/timelog). 

The package contains all components developed for the Openkoda Insurance Policy Management.

There are two options of running the Insurance application locally:

### Option 1: Import .zip package to your clean Openkoda Core instance

1. Log in to Openkoda dashboard as an Admin.
2. Go to Configuration -> Import/Export (`/html/components`).
3. Choose the downloaded `.zip` file and Import it.
4. After a successful import, the application will take a short time to restart.
5. Log in to Dashboard again. All of the insurance components should be already visible there. 

### Option 2. Unpack the .zip and run the Insurance App from your IDE  

1. Unpack the downloaded .zip.
2. Create an empty database named `openkoda_components` in Postgres.\
Alternatively adjust the settings in `src/main/resources/application-components.properties`.
3. Enter the unpacked contents folder and open a command line to run Initialization of Insurance Policy Management App\
`mvn spring-boot:run -Dspring-boot.run.profiles=openkoda,components,drop_and_init_database`
4. Run Insurance Policy Management App\
`mvn spring-boot:run -Dspring-boot.run.profiles=openkoda,components`