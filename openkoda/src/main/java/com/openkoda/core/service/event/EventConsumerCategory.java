package com.openkoda.core.service.event;

import com.openkoda.model.OptionWithLabel;

public enum EventConsumerCategory implements OptionWithLabel {
    INTEGRATION("Integration"),
    BACKUP("Backup"),
    MESSAGE("Message"),
    PUSH_NOTIFICATION("Push Notification"),
    ROLE_MODIFICATION("Role"),
    SERVER_SIDE_CODE("Server Side Code"),
    ;

    private String label;

    EventConsumerCategory(String label) {
        this.label = label;
    }

    @Override
    public String getLabel() {
        return label;
    }
}
