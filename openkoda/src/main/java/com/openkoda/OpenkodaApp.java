package com.openkoda;

import com.openkoda.core.helper.SpringProfilesHelper;
import com.openkoda.model.common.OpenkodaEntity;
import com.openkoda.repository.EntityUnrelatedQueries;
import com.openkoda.repository.SecureRepository;
import com.openkoda.service.dynamicentity.DynamicEntityDescriptor;
import com.openkoda.service.dynamicentity.DynamicEntityDescriptorFactory;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.util.function.Tuple2;

import java.io.IOException;
import java.util.List;
import java.util.stream.Stream;

import static com.openkoda.core.helper.SpringProfilesHelper.SPRING_PROFILES_ACTIVE_ENV;
import static com.openkoda.core.helper.SpringProfilesHelper.SPRING_PROFILES_ACTIVE_PROP;
import static com.openkoda.service.dynamicentity.DynamicEntityRegistrationService.createAndLoadDynamicClasses;

public class OpenkodaApp {

    @Autowired
    EntityUnrelatedQueries entityUnrelatedQueries;
    public static void main(String[] args) throws ClassNotFoundException, IOException {
        OpenkodaApp.startOpenkodaApp(App.class, args);
    }

    public static void startOpenkodaApp(Class appClass, String[] args) throws IOException, ClassNotFoundException {
        setProfiles(args);
        JDBCApp.main(args);
        if(!SpringProfilesHelper.isInitializationProfile()) {
            for (DynamicEntityDescriptor a : DynamicEntityDescriptorFactory.loadableInstances()) {
                Tuple2<Class<? extends OpenkodaEntity>, Class<? extends SecureRepository<? extends OpenkodaEntity>>> rt =
                        createAndLoadDynamicClasses(a, App.class.getClassLoader());
            }

        }
        App.startApp(appClass, args);
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
