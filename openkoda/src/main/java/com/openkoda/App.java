/*
MIT License

Copyright (c) 2016-2023, Openkoda CDX Sp. z o.o. Sp. K. <openkoda.com>

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
documentation files (the "Software"), to deal in the Software without restriction, including without limitation
the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software,
and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice
shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR
A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS
OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR
IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*/

package com.openkoda;

import com.openkoda.core.customisation.BasicCustomisationService;
import com.openkoda.core.helper.SpringProfilesHelper;
import com.openkoda.model.common.OpenkodaEntity;
import com.openkoda.repository.SecureRepository;
import com.openkoda.service.dynamicentity.DynamicEntityDescriptor;
import com.openkoda.service.dynamicentity.DynamicEntityDescriptorFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AdviceMode;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.persistenceunit.PersistenceUnitPostProcessor;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.bind.annotation.RestController;
import reactor.util.function.Tuple2;

import java.util.Arrays;
import java.util.HashMap;

import static com.openkoda.service.dynamicentity.DynamicEntityRegistrationService.PACKAGE;
import static com.openkoda.service.dynamicentity.DynamicEntityRegistrationService.createAndLoadDynamicClasses;

/**
 * <p>App class.</p>
 *
 * @author Arkadiusz Drysch (adrysch@stratoflow.com)
 * 
 */
@SpringBootApplication(
        scanBasePackages = "com.openkoda")
@EnableCaching(mode = AdviceMode.ASPECTJ)
@RestController
@Configuration
@EnableJpaRepositories(basePackages = {"com.openkoda","com.openkoda.dynamicentity.generated"})
@EnableAutoConfiguration(exclude = { SecurityAutoConfiguration.class })
@EnableTransactionManagement
public class App extends SpringBootServletInitializer {
    protected static ConfigurableApplicationContext context;
    private static Class mainClass;

    /**
     * <p>main.</p>
     *
     * @param args an array of {@link java.lang.String} objects.
     */
    public static void main(String[] args) {
        startApp(App.class, args);
    }

    protected static void startApp(Class appClass, String[] args) {
        mainClass = appClass;
        boolean isForce = args != null && Arrays.stream(args).anyMatch(a -> "--force".equals(a));
        initializationSafetyCheck(isForce);
        System.setProperty("jakarta.xml.bind.JAXBContextFactory", "com.sun.xml.bind.v2.ContextFactory");
        context = SpringApplication.run(appClass, args);
        BasicCustomisationService customisationService = context.getBean(BasicCustomisationService.class);
    }

    protected static void initializationSafetyCheck(boolean isforce) {
        try {
            if (SpringProfilesHelper.isInitializationProfile()) {
                System.out.println("*********************************************************************");
                System.out.println(" Application starts in initialization mode.");
                System.out.println(" " + Character.toString(0x1F480) + " This will irreversibly delete all data in the database.");
                System.out.println(" " + Character.toString(0x1F480) + " Continue? [y to continue]");
                System.out.println("*********************************************************************");
                if(isforce) {
                    System.out.println(" Force mode, assuming yes");
                    return;
                }
                int c = System.in.read();
                if (c != 'y') {
                    System.out.println(" Breaking the initialization");
                    System.exit(0);
                }
            }
        } catch (Exception e) {
            System.exit(1);
        }
    }

    public static void restart() {

        System.out.println("*********************************************************************");
        System.out.println(" Application restart...");
        System.out.println("*********************************************************************");

        ApplicationArguments args = context.getBean(ApplicationArguments.class);

        Thread thread = new Thread(() -> {
            context.close();
            for (DynamicEntityDescriptor ded : DynamicEntityDescriptorFactory.loadableInstances()) {
                Tuple2<Class<? extends OpenkodaEntity>, Class<? extends SecureRepository<? extends OpenkodaEntity>>> rt = createAndLoadDynamicClasses(ded, App.class.getClassLoader());
            }
            context = new SpringApplicationBuilder(mainClass).run(args.getSourceArgs());
        });

        thread.setDaemon(false);
        thread.start();
    }

    public PersistenceUnitPostProcessor persistenceUnitPostProcessor() {
        return pui -> {
            for (DynamicEntityDescriptor a : DynamicEntityDescriptorFactory.instances()) {
                pui.addManagedClassName(PACKAGE + a.getSufixedEntityName());
            }
        };
    }

    @Bean("entityBuilder")
    public EntityManagerFactoryBuilder entityManagerFactoryBuilder() {
        HibernateJpaVendorAdapter hibernateJpaVendorAdapter = new HibernateJpaVendorAdapter();
        EntityManagerFactoryBuilder a = new EntityManagerFactoryBuilder(hibernateJpaVendorAdapter, new HashMap<>(), null);
        a.setPersistenceUnitPostProcessors(persistenceUnitPostProcessor());
        return a;
    }

}
