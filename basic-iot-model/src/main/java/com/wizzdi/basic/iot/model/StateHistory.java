package com.wizzdi.basic.iot.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flexicore.model.SecuredBasic;
import com.wizzdi.dynamic.properties.converter.JsonConverter;
import com.wizzdi.maps.model.MapIcon;
import com.wizzdi.maps.model.MappedPOI;
import jakarta.persistence.*;

import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.Map;

@Entity
@Table(indexes = {
        @Index(name = "remote_idx",columnList = "remote_id,timeAtState")
})
public class StateHistory extends SecuredBasic {

   @ManyToOne(targetEntity = Remote.class)
   private Remote remote;

    @Column(columnDefinition = "timestamp with time zone")
   private OffsetDateTime timeAtState;

    @Convert(converter = JsonConverter.class)
    @Column(columnDefinition = "jsonb")
    private Map<String, Object> deviceProperties = new HashMap<>();

    @Convert(converter = JsonConverter.class)
    @Column(columnDefinition = "jsonb")
    private Map<String, Object> userAddedProperties = new HashMap<>();



    @Column(columnDefinition = "jsonb")
    @Convert(converter = JsonConverter.class)
    public Map<String, Object> getDeviceProperties() {
        return deviceProperties;
    }



    public <T extends StateHistory> T setDeviceProperties(Map<String, Object> other) {
        this.deviceProperties = other;
        return (T) this;
    }

    @Convert(converter = JsonConverter.class)
    @Column(columnDefinition = "jsonb")
    public Map<String, Object> getUserAddedProperties() {
        return userAddedProperties;
    }

    public <T extends StateHistory> T setUserAddedProperties(Map<String, Object> userAddedProperties) {
        this.userAddedProperties = userAddedProperties;
        return (T) this;
    }

    @ManyToOne(targetEntity = Remote.class)
    public Remote getRemote() {
        return remote;
    }

    public <T extends StateHistory> T setRemote(Remote remote) {
        this.remote = remote;
        return (T) this;
    }

    public OffsetDateTime getTimeAtState() {
        return timeAtState;
    }

    public <T extends StateHistory> T setTimeAtState(OffsetDateTime timeAtState) {
        this.timeAtState = timeAtState;
        return (T) this;
    }
}
