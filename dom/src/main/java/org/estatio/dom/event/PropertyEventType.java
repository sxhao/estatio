package org.estatio.dom.event;

import org.estatio.dom.Titled;

public enum PropertyEventType implements Titled {

    PROPERTY_DISRUPTION("Disruption"), 
    PROPERTY_EXTENSION("Extension"), 
    PROPERTY_REFURBISHMENT("Extension"), 
    PROPERTY_EVENT("Event"), 
    PROPERTY_TASK("Task");

    private String title;

    private PropertyEventType(String title) {
        this.title = title;
    }

    public String title() {
        return title;
    }
}
