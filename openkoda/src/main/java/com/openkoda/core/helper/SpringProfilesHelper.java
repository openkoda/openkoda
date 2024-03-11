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

package com.openkoda.core.helper;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SpringProfilesHelper {

    public static final String INITIALIZATION_PROFILE = "drop_and_init_database";
    public static final String TEST_PROFILE = "test";
    public static final String SPRING_PROFILES_ACTIVE_PROP = "spring.profiles.active";
    public static final String SPRING_PROFILES_ACTIVE_ENV = "SPRING_PROFILES_ACTIVE";

    private final static Pattern PROFILE_PATTERN = Pattern.compile(".*--spring\\.profiles\\.active=([\\w,]+) .*");
    
    public static boolean isActiveProfile(String profile) {
        String profilesCommaSeparated = System.getProperty(SPRING_PROFILES_ACTIVE_PROP);
        if (profilesCommaSeparated == null) {
            Matcher m = PROFILE_PATTERN.matcher(System.getProperty("sun.java.command"));
            if (m.matches() ) {
                profilesCommaSeparated = m.group(1);
            }
        }

        if (profilesCommaSeparated == null) {
            profilesCommaSeparated = System.getenv(SPRING_PROFILES_ACTIVE_ENV);
        }

        if (profilesCommaSeparated == null) {
            return false;
        }
        
        return ArrayUtils.contains(StringUtils.split(profilesCommaSeparated, ','), profile);
    }

    public static boolean isInitializationProfile() {
        return isActiveProfile(INITIALIZATION_PROFILE);
    }
    public static boolean isTestProfile() {
        return isActiveProfile(TEST_PROFILE);
    }
}
