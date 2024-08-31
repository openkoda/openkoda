# First stage: Build the JAR file
FROM eclipse-temurin:17-jdk AS build

# Set the working directory inside the container
WORKDIR /app

# Copy the Maven project files
COPY openkoda/pom.xml ./openkoda/
COPY openkoda/src/ ./openkoda/src
#COPY docker/entrypoint.sh ./docker/

# Build the JAR file
RUN mvn -f openkoda/pom.xml clean install spring-boot:repackage -DskipTests

# Second stage: Create the final image
FROM eclipse-temurin:17-jdk

# Set the JAR file location
ARG JAR_FILE=openkoda/target/openkoda.jar

# Set up user and group
ARG UID=20000
ARG GID=20000
RUN groupadd -r -g $GID openkoda-cloud && useradd -m -u $UID -g $GID -s /bin/bash openkoda-cloud

# Set the working directory inside the container
WORKDIR /app
RUN mkdir /data /var/log/openkoda /config

# Copy the JAR file from the build stage
COPY --from=build /app/openkoda/target/openkoda.jar /app/application.jar

# Copy the entry script
COPY docker/entrypoint.sh /app/

# Make the entry script executable
RUN chmod +x entrypoint.sh
RUN chown -R openkoda-cloud:openkoda-cloud /app /data /config /var/log/openkoda
RUN chmod ugo+rwX /data /var/log/openkoda /config

# Set environment variables
ENV SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/openkoda
ENV SPRING_DATASOURCE_USERNAME=postgres
ENV SPRING_DATASOURCE_PASSWORD=postgres
ENV BASE_URL=http://localhost:8080/
ENV APPLICATION_ADMIN_EMAIL=almbolonzi@gmail.com
ENV INIT_ADMIN_USERNAME=admin
ENV INIT_ADMIN_PASSWORD=admin123
ENV INIT_ADMIN_FIRSTNAME=Alex
ENV INIT_ADMIN_LASTNAME=Mbolonzi
ENV INIT_EXTERNAL_SCRIPT=
ENV FILE_STORAGE_FILESYSTEM_PATH=/data
ENV SPRING_PROFILES_ACTIVE=openkoda,development
ENV STORAGE_TYPE=db
ENV SPRING_CONFIG_LOCATION=classpath:/,/config/

USER openkoda-cloud

# Set the entry script as the default command to run when the container starts
ENTRYPOINT ["./entrypoint.sh"]