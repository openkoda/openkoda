package com.openkoda.model;

public enum PrivilegeGroup {
    GLOBAL_SETTINGS("Global Settings"),
    ORGANIZATION("Organization"),
    USER("User"),
    USER_ROLE("User Role"),
    SUPPORT("Support"),
    HISTORY("History"),
    FRONTEND_RESOURCE("Frontend Resource"),
    TOKEN("Token"),
    BACKEND("Backend")
    ;

    String label;

    PrivilegeGroup(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
