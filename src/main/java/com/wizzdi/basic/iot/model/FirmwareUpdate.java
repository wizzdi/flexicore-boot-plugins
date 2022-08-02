package com.wizzdi.basic.iot.model;

import com.flexicore.model.SecuredBasic;
import com.wizzdi.flexicore.file.model.FileResource;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Entity
public class FirmwareUpdate extends SecuredBasic {

    private String version;
    private String crc;

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

    public String getCrc() {
        return crc;
    }

    public <T extends FirmwareUpdate> T setCrc(String crc) {
        this.crc = crc;
        return (T) this;
    }
}
