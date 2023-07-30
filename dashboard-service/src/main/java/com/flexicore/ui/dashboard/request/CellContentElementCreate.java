package com.flexicore.ui.dashboard.request;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.wizzdi.flexicore.security.request.BasicCreate;
import com.flexicore.ui.dashboard.model.CellContent;

import java.util.HashMap;
import java.util.Map;

public class CellContentElementCreate extends BasicCreate{

    private String cellContentId;
    @JsonIgnore
    private CellContent cellContent;
    private Map<String, Object> jsonNode=new HashMap<>();

    public String getCellContentId() {
        return cellContentId;
    }

    public <T extends CellContentElementCreate> T setCellContentId(String cellContentId) {
        this.cellContentId = cellContentId;
        return (T) this;
    }

    @JsonIgnore
    public CellContent getCellContent() {
        return cellContent;
    }

    public <T extends CellContentElementCreate> T setCellContent(CellContent cellContent) {
        this.cellContent = cellContent;
        return (T) this;
    }


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

    public <T extends CellContentElementCreate> T setJsonNode(Map<String, Object> jsonNode) {
        this.jsonNode = jsonNode;
        return (T) this;
    }
}
