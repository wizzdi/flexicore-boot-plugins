package com.flexicore.territories.request;

public class ZipCreationContainer {
    private String name;
    private String description;

    public String getName() {
        return name;
    }

    public ZipCreationContainer setName(String name) {
        this.name = name;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public ZipCreationContainer setDescription(String description) {
        this.description = description;
        return this;
    }
}