package com.flexicore.ui.dashboard.request;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.wizzdi.flexicore.security.request.BasicCreate;

import java.util.HashMap;
import java.util.Map;

public class GridLayoutCreate extends BasicCreate{


   private Map<String, Object> jsonNode=new HashMap<>();

    @JsonIgnore
    public Map<String, Object> getJsonNode() {
        return this.jsonNode;
    }

    @JsonAnyGetter
    public Map<String, Object> any() {
        return this.jsonNode;
    }

    @JsonAnySetter
    public void add(String key, Object value) {
        jsonNode.put(key, value);
    }

    public <T extends GridLayoutCreate> T setJsonNode(Map<String, Object> jsonNode) {
        this.jsonNode = jsonNode;
        return (T) this;
    }
}
