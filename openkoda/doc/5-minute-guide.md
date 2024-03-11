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

### Unpack downloaded .zip package.

The unpacked folder contains all components developed for the Openkoda Insurance Policy Management. 

Following instructions below they all will be imported to one's Openkoda Core instance.

### Initialize Database

Create an empty database named `openkoda_components`. Alternatively adjust settings in `src/main/resources/application-components.properties`.

### Run Initialization of Insurance Policy Management App 
`mvn spring-boot:run -Dspring-boot.run.profiles=openkoda,components,drop_and_init_database`

### Run Insurance Policy Management App
`mvn spring-boot:run -Dspring-boot.run.profiles=openkoda,components`