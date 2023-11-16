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

import com.openkoda.core.helper.SpringProfilesHelper;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AdviceMode;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;

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
@EnableJpaRepositories
@EnableAutoConfiguration(exclude = { SecurityAutoConfiguration.class })
public class App extends SpringBootServletInitializer {
    private static ConfigurableApplicationContext context;

    /**
     * <p>main.</p>
     *
     * @param args an array of {@link java.lang.String} objects.
     */
    public static void main(String[] args) {
        startApp(App.class, args);
    }

    protected static void startApp(Class appClass, String[] args) {
        boolean isForce = args != null && Arrays.stream(args).anyMatch(a -> "--force".equals(a));
        initializationSafetyCheck(isForce);
        System.setProperty("jakarta.xml.bind.JAXBContextFactory", "com.sun.xml.bind.v2.ContextFactory");
        context = SpringApplication.run(appClass, args);
    }

    protected static void initializationSafetyCheck(boolean isforce) {
        try {
            if (SpringProfilesHelper.isInitializationProfile()) {
                System.out.println("*********************************************************************");
                System.out.println(" Application starts in initialization mode.");
                System.out.println(" This will irreversibly delete all data in the database.");
                System.out.println(" Continue? [y to continue]");
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
        ApplicationArguments args = context.getBean(ApplicationArguments.class);

        Thread thread = new Thread(() -> {
            context.close();
            context = SpringApplication.run(App.class, args.getSourceArgs());
        });

        thread.setDaemon(false);
        thread.start();
    }

}
