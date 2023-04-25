package com.openkoda.core.helper;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

public class SpringProfilesHelper {

    public static final String INITIALIZATION_PROFILE = "drop_and_init_database";
    public static final String TEST_PROFILE = "test";

    public static boolean isActiveProfile(String profile) {
        String profilesCommaSeparated = System.getProperty("spring.profiles.active");
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
