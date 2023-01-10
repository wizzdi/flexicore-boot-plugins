package com.wizzdi.basic.iot.model;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flexicore.model.SecuredBasic;
import com.wizzdi.dynamic.properties.converter.JsonConverter;
import com.wizzdi.maps.model.MappedPOI;

import javax.persistence.*;
import java.util.HashMap;
import java.util.Map;

@Entity
@Table(indexes = {
        @Index(name = "remote_idx",columnList = "remoteId,gateway_id,dtype")
})
public class Remote extends SecuredBasic {

    @ManyToOne(targetEntity = ConnectivityChange.class)
    private ConnectivityChange lastConnectivityChange;
    @Column(unique = true)
    private String remoteId;
    private String version;
    @ManyToOne(targetEntity = MappedPOI.class)
    private MappedPOI mappedPOI;

    @ManyToOne(targetEntity = StateSchema.class)
    private StateSchema currentSchema;

    @Convert(converter = JsonConverter.class)
    @Column(columnDefinition = "jsonb")
    @JsonIgnore
    private Map<String, Object> other = new HashMap<>();
    private boolean lockLocation;


    @JsonAnySetter
    public void set(String key, Object val) {
        other.put(key, val);
    }


    @Column(columnDefinition = "jsonb")
    @Convert(converter = JsonConverter.class)
    @JsonIgnore
    public Map<String, Object> getOther() {
        return other;
    }

    @JsonAnyGetter
    public Map<String, Object> any() {
        return other;
    }

    public <T extends Remote> T setOther(Map<String, Object> other) {
        this.other = other;
        return (T) this;
    }

    @ManyToOne(targetEntity = ConnectivityChange.class)
    public ConnectivityChange getLastConnectivityChange() {
        return lastConnectivityChange;
    }

    public <T extends Remote> T setLastConnectivityChange(ConnectivityChange lastConnectivityChange) {
        this.lastConnectivityChange = lastConnectivityChange;
        return (T) this;
    }

    public String getRemoteId() {
        return remoteId;
    }

    public <T extends Remote> T setRemoteId(String remoteId) {
        this.remoteId = remoteId;
        return (T) this;
    }

    public String getVersion() {
        return version;
    }

    public <T extends Remote> T setVersion(String version) {
        this.version = version;
        return (T) this;
    }

    @ManyToOne(targetEntity = StateSchema.class)
    public StateSchema getCurrentSchema() {
        return currentSchema;
    }

    public <T extends Remote> T setCurrentSchema(StateSchema stateSchema) {
        this.currentSchema = stateSchema;
        return (T) this;
    }

    @ManyToOne(targetEntity = MappedPOI.class)
    public MappedPOI getMappedPOI() {
        return mappedPOI;
    }

    public <T extends Remote> T setMappedPOI(MappedPOI mappedPOI) {
        this.mappedPOI = mappedPOI;
        return (T) this;
    }

    public boolean isLockLocation() {
        return lockLocation;
    }

    public <T extends Remote> T setLockLocation(boolean lockLocation) {
        this.lockLocation = lockLocation;
        return (T) this;
    }
}
