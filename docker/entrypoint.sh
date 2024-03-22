#!/bin/sh
echo $OPENKODA_DATABASE_URL

CONTAINER_FIRST_STARTUP="CONTAINER_FIRST_STARTUP"
if [ ! -e /data/$CONTAINER_FIRST_STARTUP ]; then
    touch /data/$CONTAINER_FIRST_STARTUP

    echo "-- First container startup --"
    java \
        -Dloader.path=/BOOT-INF/classes \
        -Dspring.profiles.active=openkoda,drop_and_init_database,$SPRING_PROFILES_ACTIVE \
        -Dapplication.admin.email=$APPLICATION_ADMIN_EMAIL \
        -Dinit.admin.username=$INIT_ADMIN_USERNAME \
        -Dinit.admin.password=$INIT_ADMIN_PASSWORD \
        -Dinit.admin.firstName=$INIT_ADMIN_FIRSTNAME \
        -Dinit.admin.lastName=$INIT_ADMIN_LASTNAME \
        -Dfile.storage.filesystem.path=$FILE_STORAGE_FILESYSTEM_PATH \
        -Dlogging.file.name=/var/log/openkoda/openkoda.log \
        -Dfile.storage.type=$STORAGE_TYPE \
        -Dspring.config.location=$SPRING_CONFIG_LOCATION \
        -Dglobal.initialization.externalScript="$INIT_EXTERNAL_SCRIPT" \
        -jar application.jar  --server.port=8080 --force -y \
    && java \
        -Dloader.path=/BOOT-INF/classes \
        -Dspring.profiles.active=openkoda,$SPRING_PROFILES_ACTIVE \
        -Dapplication.admin.email=$APPLICATION_ADMIN_EMAIL \
        -Dbase.url=$BASE_URL \
        -Dfile.storage.filesystem.path=$FILE_STORAGE_FILESYSTEM_PATH \
        -Dlogging.file.name=/var/log/openkoda/openkoda.log \
        -Dfile.storage.type=$STORAGE_TYPE \
        -Dspring.config.location=$SPRING_CONFIG_LOCATION \
        -jar application.jar  --server.port=8080 --force -y
else
    echo "-- Not first container startup --"
    java \
        -Dloader.path=/BOOT-INF/classes \
        -Dspring.profiles.active=openkoda,$SPRING_PROFILES_ACTIVE \
        -Dapplication.admin.email=$APPLICATION_ADMIN_EMAIL \
        -Dbase.url=$BASE_URL \
        -Dfile.storage.filesystem.path=$FILE_STORAGE_FILESYSTEM_PATH \
        -Dlogging.file.name=/var/log/openkoda/openkoda.log \
        -Dfile.storage.type=$STORAGE_TYPE \
        -Dspring.config.location=$SPRING_CONFIG_LOCATION \
        -jar application.jar  --server.port=8080 --force -y
fi
