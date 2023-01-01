package com.wizzdi.basic.iot.model;

import com.flexicore.model.SecuredBasic;

import javax.persistence.*;

@Entity
@Table(indexes = {
        @Index(name = "schema_Action_idx",columnList = "stateSchema_id,externalId,name")
})
public class SchemaAction extends SecuredBasic {

    @ManyToOne(targetEntity = StateSchema.class)
    private StateSchema stateSchema;
    private String externalId;

    @Lob
    private String actionSchema;

    @ManyToOne(targetEntity = StateSchema.class)

    public StateSchema getStateSchema() {
        return stateSchema;
    }

    public <T extends SchemaAction> T setStateSchema(StateSchema stateSchema) {
        this.stateSchema = stateSchema;
        return (T) this;
    }

    @Lob
    public String getActionSchema() {
        return actionSchema;
    }

    public <T extends SchemaAction> T setActionSchema(String actionSchema) {
        this.actionSchema = actionSchema;
        return (T) this;
    }

    public String getExternalId() {
        return externalId;
    }

    public <T extends SchemaAction> T setExternalId(String externalId) {
        this.externalId = externalId;
        return (T) this;
    }
}
