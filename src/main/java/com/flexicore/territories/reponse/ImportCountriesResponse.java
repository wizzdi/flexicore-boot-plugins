package com.flexicore.territories.reponse;

public class ImportCountriesResponse {

    private int createdCountries;
    private int existingCountries;

    public int getCreatedCountries() {
        return createdCountries;
    }

    public <T extends ImportCountriesResponse> T setCreatedCountries(int createdCountries) {
        this.createdCountries = createdCountries;
        return (T) this;
    }

    public int getExistingCountries() {
        return existingCountries;
    }

    public <T extends ImportCountriesResponse> T setExistingCountries(int existingCountries) {
        this.existingCountries = existingCountries;
        return (T) this;
    }
}
