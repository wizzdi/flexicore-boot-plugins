package com.wizzdi.basic.iot.client;

public class SchemaAction {

    private String id;
    private String name;
    private String jsonSchema;

    public String getId() {
        return id;
    }

    public <T extends SchemaAction> T setId(String id) {
        this.id = id;
        return (T) this;
    }

    public String getName() {
        return name;
    }

    public <T extends SchemaAction> T setName(String name) {
        this.name = name;
        return (T) this;
    }

    public String getJsonSchema() {
        return jsonSchema;
    }

    public <T extends SchemaAction> T setJsonSchema(String jsonSchema) {
        this.jsonSchema = jsonSchema;
        return (T) this;
    }
}
