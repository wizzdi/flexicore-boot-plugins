package com.wizzdi.basic.iot.model;

import com.flexicore.model.Baseclass;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;

@Entity
@Table(indexes = @Index(name = "remote_role_external_id_idx", columnList = "externalId"))
public class RemoteRoleDefinition extends Baseclass {

    private String externalId;

    public String getExternalId() {
        return externalId;
    }

    public <T extends RemoteRoleDefinition> T setExternalId(String externalId) {
        this.externalId = externalId;
        return (T) this;
    }
}
