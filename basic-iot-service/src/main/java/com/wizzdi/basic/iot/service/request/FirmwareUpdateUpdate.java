package com.wizzdi.basic.iot.service.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.wizzdi.basic.iot.model.FirmwareUpdate;
import com.wizzdi.flexicore.security.validation.Create;
import com.wizzdi.flexicore.security.validation.IdValid;
import com.wizzdi.flexicore.security.validation.Update;

@IdValid.List({@IdValid(field = "id", targetField = "firmwareUpdate", fieldType = FirmwareUpdate.class, groups = {Create.class, Update.class})})

public class FirmwareUpdateUpdate extends FirmwareUpdateCreate{

    private String id;
    @JsonIgnore
    private FirmwareUpdate firmwareUpdate;


    public String getId() {
        return id;
    }

    public <T extends FirmwareUpdateUpdate> T setId(String id) {
        this.id = id;
        return (T) this;
    }
    @JsonIgnore
    public FirmwareUpdate getFirmwareUpdate() {
        return firmwareUpdate;
    }

    public <T extends FirmwareUpdateUpdate> T setFirmwareUpdate(FirmwareUpdate firmwareUpdate) {
        this.firmwareUpdate = firmwareUpdate;
        return (T) this;
    }
}
