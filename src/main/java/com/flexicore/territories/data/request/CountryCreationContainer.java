package com.flexicore.territories.data.request;

public class CountryCreationContainer {
    private String name;
    private String description;

    public String getName() {
        return name;
    }

    public CountryCreationContainer setName(String name) {
        this.name = name;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public CountryCreationContainer setDescription(String description) {
        this.description = description;
        return this;
    }
}