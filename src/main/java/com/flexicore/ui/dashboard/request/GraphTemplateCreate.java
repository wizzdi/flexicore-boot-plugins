package com.flexicore.ui.dashboard.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flexicore.model.FileResource;
import com.flexicore.request.BaseclassCreate;

public class GraphTemplateCreate extends BaseclassCreate {

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

    @Override
    public boolean supportingDynamic() {
        return true;
    }
}
