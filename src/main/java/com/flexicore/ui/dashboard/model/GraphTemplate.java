package com.flexicore.ui.dashboard.model;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flexicore.model.Baseclass;
import com.flexicore.model.SecuredBasic;
import com.flexicore.security.SecurityContext;
import com.wizzdi.dynamic.properties.converter.DynamicColumnDefinition;
import com.wizzdi.dynamic.properties.converter.JsonConverter;
import com.wizzdi.flexicore.file.model.FileResource;

import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import java.util.Map;

@Entity
public class GraphTemplate extends SecuredBasic {

    @Convert(converter = JsonConverter.class)
    @DynamicColumnDefinition
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
