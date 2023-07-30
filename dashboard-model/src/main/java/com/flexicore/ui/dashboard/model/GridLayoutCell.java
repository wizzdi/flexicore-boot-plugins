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
public class GridLayoutCell extends SecuredBasic {

    private String externalId;

    @ManyToOne(targetEntity = GridLayout.class)
    private GridLayout gridLayout;

    @JsonIgnore
    @OneToMany(targetEntity = CellToLayout.class)
    private List<CellToLayout> cellToLayouts=new ArrayList<>();

    @Convert(converter = JsonConverter.class)
    @Column(columnDefinition = "jsonb")
    private Map<String, Object> jsonNode;


    public GridLayoutCell() {
        super();
    }

    public String getExternalId() {
        return externalId;
    }

    public <T extends GridLayoutCell> T setExternalId(String externalId) {
        this.externalId = externalId;
        return (T) this;
    }

    @ManyToOne(targetEntity = GridLayout.class)
    public GridLayout getGridLayout() {
        return gridLayout;
    }

    public <T extends GridLayoutCell> T setGridLayout(GridLayout gridLayout) {
        this.gridLayout = gridLayout;
        return (T) this;
    }


    @JsonIgnore
    @OneToMany(targetEntity = CellToLayout.class)
    public List<CellToLayout> getCellToLayouts() {
        return cellToLayouts;
    }

    public <T extends GridLayoutCell> T setCellToLayouts(List<CellToLayout> cellToLayouts) {
        this.cellToLayouts = cellToLayouts;
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
