package com.flexicore.license.request;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flexicore.license.model.LicensingEntity;
import com.flexicore.license.request.LicensingEntityCreate;

/**
 * Created by Asaf on 17/10/2016.
 */
public class LicensingEntityUpdate extends LicensingEntityCreate {

    private String id;
    @JsonIgnore
    private LicensingEntity licensingEntity;

    public String getId() {
        return id;
    }

    public <T extends LicensingEntityUpdate> T setId(String id) {
        this.id = id;
        return (T) this;
    }

    @JsonIgnore
    public LicensingEntity getLicensingEntity() {
        return licensingEntity;
    }

    public <T extends LicensingEntityUpdate> T setLicensingEntity(LicensingEntity licensingEntity) {
        this.licensingEntity = licensingEntity;
        return (T) this;
    }
}
