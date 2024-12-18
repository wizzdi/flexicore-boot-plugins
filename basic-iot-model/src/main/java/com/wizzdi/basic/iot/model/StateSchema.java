package com.wizzdi.basic.iot.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flexicore.model.Baseclass;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(indexes = {
        @Index(name = "state_schema_idx",columnList = "deviceType_id,version,name")
})
public class StateSchema extends Baseclass {

    private int version;
    private boolean userAddedSchema;
    @Lob
    private String stateJsonSchema;
    @ManyToOne(targetEntity = DeviceType.class)
    private DeviceType deviceType;

    @JsonIgnore
    @OneToMany(targetEntity = Remote.class)
    private List<Remote> remotes=new ArrayList<>();

    public int getVersion() {
        return version;
    }

    public <T extends StateSchema> T setVersion(int version) {
        this.version = version;
        return (T) this;
    }

    public String getStateJsonSchema() {
        return stateJsonSchema;
    }

    public <T extends StateSchema> T setStateJsonSchema(String stateJsonSchema) {
        this.stateJsonSchema = stateJsonSchema;
        return (T) this;
    }

    @ManyToOne(targetEntity = DeviceType.class)
    public DeviceType getDeviceType() {
        return deviceType;
    }

    public <T extends StateSchema> T setDeviceType(DeviceType deviceType) {
        this.deviceType = deviceType;
        return (T) this;
    }

    @JsonIgnore
    @OneToMany(targetEntity = Remote.class)
    public List<Remote> getRemotes() {
        return remotes;
    }

    public <T extends StateSchema> T setRemotes(List<Remote> remotes) {
        this.remotes = remotes;
        return (T) this;
    }

    public boolean isUserAddedSchema() {
        return userAddedSchema;
    }

    public <T extends StateSchema> T setUserAddedSchema(boolean userAddedSchema) {
        this.userAddedSchema = userAddedSchema;
        return (T) this;
    }
}
