package com.wizzdi.basic.iot.service.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.wizzdi.basic.iot.service.validators.ValidVersion;
import com.wizzdi.flexicore.file.model.FileResource;
import com.wizzdi.flexicore.security.request.BasicCreate;
import com.wizzdi.flexicore.security.validation.Create;
import com.wizzdi.flexicore.security.validation.IdValid;
import com.wizzdi.flexicore.security.validation.Update;

import javax.validation.constraints.NotNull;

@IdValid.List({@IdValid(field = "fileResourceId", targetField = "fileResource", fieldType = FileResource.class, groups = {Create.class, Update.class})})
public class FirmwareUpdateCreate extends BasicCreate {

    @NotNull(groups = Create.class)
    @ValidVersion(groups = {Create.class,Update.class})
    private String version;
    @JsonIgnore
    private FileResource fileResource;
    @NotNull(groups = Create.class)
    private String fileResourceId;


    @JsonIgnore
    public FileResource getFileResource() {
        return fileResource;
    }

    public <T extends FirmwareUpdateCreate> T setFileResource(FileResource fileResource) {
        this.fileResource = fileResource;
        return (T) this;
    }

    public String getFileResourceId() {
        return fileResourceId;
    }

    public <T extends FirmwareUpdateCreate> T setFileResourceId(String fileResourceId) {
        this.fileResourceId = fileResourceId;
        return (T) this;
    }

    public String getVersion() {
        return version;
    }

    public <T extends FirmwareUpdateCreate> T setVersion(String version) {
        this.version = version;
        return (T) this;
    }
}
