package com.wizzdi.basic.iot.model;

import com.flexicore.model.SecuredBasic;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import java.time.OffsetDateTime;

@Entity
public class ConnectivityChange extends SecuredBasic {

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
