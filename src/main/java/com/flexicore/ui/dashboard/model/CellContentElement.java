package com.flexicore.ui.dashboard.model;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flexicore.model.SecuredBasic;
import com.wizzdi.dynamic.properties.converter.DynamicColumnDefinition;
import com.wizzdi.dynamic.properties.converter.JsonConverter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Entity
public class CellContentElement extends SecuredBasic {

    @DynamicColumnDefinition
    @Column(columnDefinition = "jsonb")
    @Convert(converter = JsonConverter.class)
    private Map<String, Object> jsonNode;

    @ManyToOne(targetEntity = CellContent.class)
    private CellContent cellContent;

    @JsonIgnore
    @OneToMany(targetEntity = DataMapper.class,mappedBy = "cellContentElement")
    private List<DataMapper> dataMappers=new ArrayList<>();


    public CellContentElement() {
        super();
    }


    @ManyToOne(targetEntity = CellContent.class)
    public CellContent getCellContent() {
        return cellContent;
    }

    public <T extends CellContentElement> T setCellContent(CellContent cellContent) {
        this.cellContent = cellContent;
        return (T) this;
    }

    @JsonIgnore
    @OneToMany(targetEntity = DataMapper.class,mappedBy = "cellContentElement")
    public List<DataMapper> getDataMappers() {
        return dataMappers;
    }

    public <T extends CellContentElement> T setDataMappers(List<DataMapper> dataMappers) {
        this.dataMappers = dataMappers;
        return (T) this;
    }

    @DynamicColumnDefinition
    @JsonIgnore
    @Column(columnDefinition = "jsonb")
    @Convert(converter = JsonConverter.class)
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
