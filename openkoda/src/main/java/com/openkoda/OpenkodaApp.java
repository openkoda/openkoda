package com.openkoda;

import com.openkoda.core.helper.SpringProfilesHelper;
import com.openkoda.repository.NativeQueries;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import static com.openkoda.core.helper.SpringProfilesHelper.SPRING_PROFILES_ACTIVE_ENV;
import static com.openkoda.core.helper.SpringProfilesHelper.SPRING_PROFILES_ACTIVE_PROP;
import static com.openkoda.service.dynamicentity.DynamicEntityRegistrationService.buildAndLoadDynamicClasses;

public class OpenkodaApp {

    @Autowired
    NativeQueries nativeQueries;

    public static void main(String[] args) throws ClassNotFoundException, IOException, URISyntaxException {
        boolean isForce = args != null && Arrays.stream(args).anyMatch(a -> "--force".equals(a));
        App.initializationSafetyCheck(isForce);
        OpenkodaApp.startOpenkodaApp(App.class, args);
    }

    public static void startOpenkodaApp(Class appClass, String[] args) throws IOException, ClassNotFoundException, URISyntaxException {
        setProfiles(args);
        JDBCApp.main(args);
        if(!SpringProfilesHelper.isInitializationProfile()) {
            buildAndLoadDynamicClasses(App.class.getClassLoader());
        }
        App.startApp(appClass, args, true);
    }
    /* Used for setting active spring profiles when they are provided in args array

    * */
    private static void setProfiles(String[] args){
        if(System.getProperty(SPRING_PROFILES_ACTIVE_PROP) == null && args != null && args.length > 0){
            //profiles are set as param in command line (for components app)
            //important: the argument in command line -Dspring-boot.run.profiles is switched to --spring.profiles.active in args variable
            List<String> filteredArgs = Stream.of(args).filter(a -> a.contains(SPRING_PROFILES_ACTIVE_PROP)).toList();
            if(!filteredArgs.isEmpty()) {
                System.setProperty(SPRING_PROFILES_ACTIVE_PROP, filteredArgs.get(0).split("=")[1]);
            } else if (System.getenv(SPRING_PROFILES_ACTIVE_ENV) != null) {
//                In case we start Openkoda as a cloud instance configured via environment properties in a Dockerfile
                System.setProperty(SPRING_PROFILES_ACTIVE_PROP, System.getenv(SPRING_PROFILES_ACTIVE_ENV));
            }
        }
    }
}
