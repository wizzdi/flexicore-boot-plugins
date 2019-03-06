package com.flexicore.territories.reponse;

public class AddressImportResponse {

    private boolean ok;

    private int createdStreet;
    private int createdCities;

    private int updatedStreet;
    private int updatedCities;

    private int unchangedStreet;
    private int unchangedCities;

    private int deletedStreet;
    private int deletedCities;

    public int getCreatedStreet() {
        return createdStreet;
    }

    public <T extends AddressImportResponse> T setCreatedStreet(int createdStreet) {
        this.createdStreet = createdStreet;
        return (T) this;
    }

    public int getCreatedCities() {
        return createdCities;
    }

    public <T extends AddressImportResponse> T setCreatedCities(int createdCities) {
        this.createdCities = createdCities;
        return (T) this;
    }

    public int getUpdatedStreet() {
        return updatedStreet;
    }

    public <T extends AddressImportResponse> T setUpdatedStreet(int updatedStreet) {
        this.updatedStreet = updatedStreet;
        return (T) this;
    }

    public int getUpdatedCities() {
        return updatedCities;
    }

    public <T extends AddressImportResponse> T setUpdatedCities(int updatedCities) {
        this.updatedCities = updatedCities;
        return (T) this;
    }

    public int getUnchangedStreet() {
        return unchangedStreet;
    }

    public <T extends AddressImportResponse> T setUnchangedStreet(int unchangedStreet) {
        this.unchangedStreet = unchangedStreet;
        return (T) this;
    }

    public int getUnchangedCities() {
        return unchangedCities;
    }

    public <T extends AddressImportResponse> T setUnchangedCities(int unchangedCities) {
        this.unchangedCities = unchangedCities;
        return (T) this;
    }

    public boolean isOk() {
        return ok;
    }

    public <T extends AddressImportResponse> T setOk(boolean ok) {
        this.ok = ok;
        return (T) this;
    }

    public int getDeletedStreet() {
        return deletedStreet;
    }

    public <T extends AddressImportResponse> T setDeletedStreet(int deletedStreet) {
        this.deletedStreet = deletedStreet;
        return (T) this;
    }

    public int getDeletedCities() {
        return deletedCities;
    }

    public <T extends AddressImportResponse> T setDeletedCities(int deletedCities) {
        this.deletedCities = deletedCities;
        return (T) this;
    }
}
