package com.wizzdi.basic.iot.model;

import com.flexicore.model.Baseclass;

import jakarta.persistence.*;
import java.time.OffsetDateTime;

@Entity
@Table(indexes = {
        @Index(name = "connectivity_change_idx",columnList = "remote_id,date,connectivity")
})
public class ConnectivityChange extends Baseclass {

    @ManyToOne(targetEntity = Remote.class)
    private Remote remote;
    private Connectivity connectivity;
    @Column(columnDefinition = "timestamp with time zone")
    private OffsetDateTime date;

    @ManyToOne(targetEntity = Remote.class)
    public Remote getRemote() {
        return remote;
    }

    public <T extends ConnectivityChange> T setRemote(Remote remote) {
        this.remote = remote;
        return (T) this;
    }

    public Connectivity getConnectivity() {
        return connectivity;
    }

    public <T extends ConnectivityChange> T setConnectivity(Connectivity connectivity) {
        this.connectivity = connectivity;
        return (T) this;
    }

    public OffsetDateTime getDate() {
        return date;
    }

    public <T extends ConnectivityChange> T setDate(OffsetDateTime date) {
        this.date = date;
        return (T) this;
    }
}
