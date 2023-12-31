package com.flexicore.ui.component.request;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Created by Asaf on 12/07/2017.
 */

public class UIComponentRegistrationContainer {
    private String name;
    private String description;

    @Schema(required = true)
    private String externalId;
    private String groups;

    public String getExternalId() {
        return externalId;
    }

    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }

    public String getName() {
        return name;
    }

    public <T extends UIComponentRegistrationContainer> T setName(String name) {
        this.name = name;
        return (T) this;
    }

    public String getDescription() {
        return description;
    }

    public <T extends UIComponentRegistrationContainer> T setDescription(String description) {
        this.description = description;
        return (T) this;
    }

    public String getGroups() {
        return groups;
    }

    public <T extends UIComponentRegistrationContainer> T setGroups(String groups) {
        this.groups = groups;
        return (T) this;
    }
}
