# Example Application

## Build Openkoda
In the main catalogue: 

```mvn clean install```

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