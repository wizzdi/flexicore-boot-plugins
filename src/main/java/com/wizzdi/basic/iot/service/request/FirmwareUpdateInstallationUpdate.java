package com.wizzdi.basic.iot.service.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.wizzdi.basic.iot.model.FirmwareUpdateInstallation;
import com.wizzdi.flexicore.security.validation.Create;
import com.wizzdi.flexicore.security.validation.IdValid;
import com.wizzdi.flexicore.security.validation.Update;

@IdValid.List({@IdValid(field = "id", targetField = "firmwareUpdateInstallation", fieldType = FirmwareUpdateInstallation.class, groups = {Create.class, Update.class})})

public class FirmwareUpdateInstallationUpdate extends FirmwareUpdateInstallationCreate{

    private String id;
    @JsonIgnore
    private FirmwareUpdateInstallation firmwareUpdateInstallation;


    public String getId() {
        return id;
    }

    public <T extends FirmwareUpdateInstallationUpdate> T setId(String id) {
        this.id = id;
        return (T) this;
    }
    @JsonIgnore
    public FirmwareUpdateInstallation getFirmwareUpdateInstallation() {
        return firmwareUpdateInstallation;
    }

    public <T extends FirmwareUpdateInstallationUpdate> T setFirmwareUpdateInstallation(FirmwareUpdateInstallation firmwareUpdateInstallation) {
        this.firmwareUpdateInstallation = firmwareUpdateInstallation;
        return (T) this;
    }
}
