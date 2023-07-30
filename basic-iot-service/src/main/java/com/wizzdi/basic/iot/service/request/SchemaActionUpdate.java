package com.wizzdi.basic.iot.service.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.wizzdi.basic.iot.model.SchemaAction;


public class SchemaActionUpdate extends SchemaActionCreate{

    private String id;
    @JsonIgnore
    private SchemaAction schemaAction;


    public String getId() {
        return id;
    }

    public <T extends SchemaActionUpdate> T setId(String id) {
        this.id = id;
        return (T) this;
    }
    @JsonIgnore
    public SchemaAction getSchemaAction() {
        return schemaAction;
    }

    public <T extends SchemaActionUpdate> T setSchemaAction(SchemaAction schemaAction) {
        this.schemaAction = schemaAction;
        return (T) this;
    }
}
