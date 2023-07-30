package com.flexicore.license.request;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flexicore.license.model.LicensingProduct;
import com.flexicore.license.request.LicensingProductCreate;

/**
 * Created by Asaf on 17/10/2016.
 */
public class LicensingProductUpdate extends LicensingProductCreate {

    private String id;
    @JsonIgnore
    private LicensingProduct licensingProduct;

    public String getId() {
        return id;
    }

    public <T extends LicensingProductUpdate> T setId(String id) {
        this.id = id;
        return (T) this;
    }

    @JsonIgnore
    public LicensingProduct getLicensingProduct() {
        return licensingProduct;
    }

    public <T extends LicensingProductUpdate> T setLicensingProduct(LicensingProduct licensingProduct) {
        this.licensingProduct = licensingProduct;
        return (T) this;
    }
}
