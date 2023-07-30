package com.flexicore.ui.dashboard.model;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flexicore.model.SecuredBasic;

import com.wizzdi.dynamic.properties.converter.JsonConverter;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Entity
public class GridLayout extends SecuredBasic {


   
    @Convert(converter = JsonConverter.class)
    @Column(columnDefinition = "jsonb")
    private Map<String, Object> jsonNode;

    @JsonIgnore
    @OneToMany(targetEntity = GridLayoutCell.class,mappedBy = "gridLayout")
    private List<GridLayoutCell> cells=new ArrayList<>();

    public GridLayout() {
        super();
    }

    @JsonIgnore
    @OneToMany(targetEntity = GridLayoutCell.class,mappedBy = "gridLayout")
    public List<GridLayoutCell> getCells() {
        return cells;
    }

    public <T extends GridLayout> T setCells(List<GridLayoutCell> cells) {
        this.cells = cells;
        return (T) this;
    }
    @JsonIgnore
    @Convert(converter = JsonConverter.class)
    @Column(columnDefinition = "jsonb")
    public Map<String, Object> getJsonNode() {
        return jsonNode;
    }

    @JsonAnyGetter
    public Map<String, Object> any() {
        return jsonNode;
    }

    public <T extends SecuredBasic> T setJsonNode(Map<String, Object> jsonNode) {
        this.jsonNode = jsonNode;
        return (T) this;
    }

}
