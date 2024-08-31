# Use an official OpenJdk runtime as a parent image
FROM eclipse-temurin:17-jdk
#Set jar file
ARG JAR_FILE=openkoda/build/openkoda.jar
ARG UID=20000
ARG GID=20000

RUN groupadd -r -g $GID openkoda-cloud && useradd -m -u $UID -g $GID -s /bin/bash openkoda-cloud
# Set the working directory inside the container
WORKDIR /app
RUN mkdir /data /var/log/openkoda /config

# Copy the entry script and Spring Boot application JAR into the container
COPY docker/entrypoint.sh /app/
COPY ${JAR_FILE} application.jar

# Make the entry script executable
RUN chmod +x entrypoint.sh
RUN chown -R openkoda-cloud:openkoda-cloud /app /data /config /var/log/openkoda
RUN chmod ugo+rwX /data /var/log/openkoda /config

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

