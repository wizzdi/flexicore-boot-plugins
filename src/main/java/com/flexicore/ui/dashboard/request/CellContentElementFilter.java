package com.flexicore.ui.dashboard.request;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.wizzdi.flexicore.security.request.PaginationFilter;
import com.flexicore.ui.dashboard.model.CellContent;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class CellContentElementFilter extends PaginationFilter {

    private Set<String> cellContentIds=new HashSet<>();
    @JsonIgnore
    private List<CellContent> cellContents;

    public Set<String> getCellContentIds() {
        return cellContentIds;
    }

    public <T extends CellContentElementFilter> T setCellContentIds(Set<String> cellContentIds) {
        this.cellContentIds = cellContentIds;
        return (T) this;
    }

    @JsonIgnore
    public List<CellContent> getCellContents() {
        return cellContents;
    }

    public <T extends CellContentElementFilter> T setCellContents(List<CellContent> cellContents) {
        this.cellContents = cellContents;
        return (T) this;
    }

   private Map<String, Object> jsonNode;

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

    public <T extends CellContentElementFilter> T setJsonNode(Map<String, Object> jsonNode) {
        this.jsonNode = jsonNode;
        return (T) this;
    }
}
