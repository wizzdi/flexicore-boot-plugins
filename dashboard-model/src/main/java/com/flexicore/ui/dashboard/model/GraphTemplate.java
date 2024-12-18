package com.flexicore.ui.dashboard.model;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flexicore.model.Baseclass;

import com.wizzdi.dynamic.properties.converter.JsonConverter;
import com.wizzdi.flexicore.file.model.FileResource;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import java.util.Map;

@Entity
public class GraphTemplate extends Baseclass {

    @Convert(converter = JsonConverter.class)
    @Column(columnDefinition = "jsonb")
    private Map<String, Object> jsonNode;


    @ManyToOne(targetEntity = FileResource.class)
    private FileResource fileResource;

    public GraphTemplate() {
    }


    @ManyToOne(targetEntity = FileResource.class)
    public FileResource getFileResource() {
        return fileResource;
    }

    public <T extends GraphTemplate> T setFileResource(FileResource fileResource) {
        this.fileResource = fileResource;
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

    public <T extends Baseclass> T setJsonNode(Map<String, Object> jsonNode) {
        this.jsonNode = jsonNode;
        return (T) this;
    }
}
