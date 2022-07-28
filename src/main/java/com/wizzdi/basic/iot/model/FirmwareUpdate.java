package com.wizzdi.basic.iot.model;

import com.flexicore.model.FileResource;
import com.flexicore.model.SecuredBasic;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Entity
public class FirmwareUpdate extends SecuredBasic {

    private String version;

    @ManyToOne(targetEntity = FileResource.class)
    private FileResource fileResource;

    @ManyToOne(targetEntity = FileResource.class)
    public FileResource getFileResource() {
        return fileResource;
    }

    public <T extends FirmwareUpdate> T setFileResource(FileResource fileResource) {
        this.fileResource = fileResource;
        return (T) this;
    }

    public String getVersion() {
        return version;
    }

    public <T extends FirmwareUpdate> T setVersion(String version) {
        this.version = version;
        return (T) this;
    }
}
