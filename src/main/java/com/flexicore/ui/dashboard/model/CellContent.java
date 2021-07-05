package com.flexicore.ui.dashboard.model;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flexicore.model.SecuredBasic;
import com.wizzdi.dynamic.properties.converter.DynamicColumnDefinition;
import com.wizzdi.dynamic.properties.converter.JsonConverter;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Entity
public class CellContent extends SecuredBasic {

    @Column(columnDefinition = "jsonb")
    @Convert(converter = JsonConverter.class)
    @DynamicColumnDefinition
    private Map<String, Object> jsonNode;

    @JsonIgnore
    @OneToMany(targetEntity = CellContentElement.class,mappedBy = "cellContent")
    private List<CellContentElement> cellContentElements=new ArrayList<>();

    public CellContent() {
        super();
    }


    @JsonIgnore
    @OneToMany(targetEntity = CellContentElement.class,mappedBy = "cellContent")
    public List<CellContentElement> getCellContentElements() {
        return cellContentElements;
    }

    public <T extends CellContent> T setCellContentElements(List<CellContentElement> cellContentElements) {
        this.cellContentElements = cellContentElements;
        return (T) this;
    }

    @JsonIgnore
    @Column(columnDefinition = "jsonb")
    @Convert(converter = JsonConverter.class)
    @DynamicColumnDefinition
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
