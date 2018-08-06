package com.flexicore.scheduling.containers.response;

public class SchedulingMethod {

    private String name;
    private String description;

    public SchedulingMethod(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public SchedulingMethod setName(String name) {
        this.name = name;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public SchedulingMethod setDescription(String description) {
        this.description = description;
        return this;
    }
}
