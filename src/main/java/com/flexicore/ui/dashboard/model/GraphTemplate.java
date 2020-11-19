package com.flexicore.ui.dashboard.model;

import com.flexicore.model.Baseclass;
import com.flexicore.model.FileResource;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Entity
public class GraphTemplate extends Baseclass {

    @ManyToOne(targetEntity = FileResource.class)
    private FileResource fileResource;

    @ManyToOne(targetEntity = FileResource.class)
    public FileResource getFileResource() {
        return fileResource;
    }

    public <T extends GraphTemplate> T setFileResource(FileResource fileResource) {
        this.fileResource = fileResource;
        return (T) this;
    }
}
