package com.openkoda.core.helper;

import org.hibernate.annotations.Formula;

import com.openkoda.model.common.EntityWithRequiredPrivilege;

public class PrivilegeUtils {
    public static String getRequiredReadPrivilege(Class c) {
        String requiredReadPrivilege = "null";
        if (EntityWithRequiredPrivilege.class.isAssignableFrom(c)) {
            try {
                requiredReadPrivilege = c.getDeclaredField("requiredReadPrivilege").getAnnotation(Formula.class)
                        .value();
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            }
        }
        return requiredReadPrivilege;
    }
}
