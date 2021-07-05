package com.flexicore.ui.dashboard.request;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.wizzdi.flexicore.file.model.FileResource;
import com.wizzdi.flexicore.security.request.BasicCreate;

import java.util.Map;

public class GraphTemplateCreate extends BasicCreate{

    private String fileResourceId;
    @JsonIgnore
    private FileResource fileResource;

    public String getFileResourceId() {
        return fileResourceId;
    }

    public <T extends GraphTemplateCreate> T setFileResourceId(String fileResourceId) {
        this.fileResourceId = fileResourceId;
        return (T) this;
    }

    @JsonIgnore
    public FileResource getFileResource() {
        return fileResource;
    }

    public <T extends GraphTemplateCreate> T setFileResource(FileResource fileResource) {
        this.fileResource = fileResource;
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

    public <T extends GraphTemplateCreate> T setJsonNode(Map<String, Object> jsonNode) {
        this.jsonNode = jsonNode;
        return (T) this;
    }
}
