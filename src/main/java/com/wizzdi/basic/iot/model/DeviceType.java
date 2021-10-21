package com.wizzdi.basic.iot.model;

import com.flexicore.model.SecuredBasic;

import javax.persistence.Entity;
import javax.persistence.Lob;

@Entity
public class DeviceType extends SecuredBasic {

    @Lob
    private String stateJsonSchema;
    private int version;


    @Lob
    public String getStateJsonSchema() {
        return stateJsonSchema;
    }

    public <T extends DeviceType> T setStateJsonSchema(String stateJsonSchema) {
        this.stateJsonSchema = stateJsonSchema;
        return (T) this;
    }

    public int getVersion() {
        return version;
    }

    public <T extends DeviceType> T setVersion(int version) {
        this.version = version;
        return (T) this;
    }
}
