package com.wizzdi.basic.iot.service.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.wizzdi.basic.iot.model.FirmwareInstallationState;
import com.wizzdi.basic.iot.model.FirmwareUpdate;
import com.wizzdi.flexicore.security.request.BasicCreate;
import com.wizzdi.flexicore.security.validation.IdValid;

import javax.validation.constraints.NotNull;
import java.time.OffsetDateTime;

@IdValid.List({
        @IdValid(field = "firmwareUpdateId", targetField = "firmwareUpdate", fieldType = FirmwareUpdate.class),
})
public class FirmwareUpdateInstallationMassCreate extends BasicCreate {

    @NotNull
    private String firmwareUpdateId;
    @JsonIgnore
    private FirmwareUpdate firmwareUpdate;
    @NotNull
    private RemoteFilter remoteFilter;
    private OffsetDateTime targetInstallationDate;
    @JsonIgnore
    private OffsetDateTime dateInstalled;
    @JsonIgnore
    private OffsetDateTime nextTimeForReminder;
    @JsonIgnore
    private FirmwareInstallationState firmwareInstallationState;


    public String getFirmwareUpdateId() {
        return firmwareUpdateId;
    }

    public <T extends FirmwareUpdateInstallationMassCreate> T setFirmwareUpdateId(String firmwareUpdateId) {
        this.firmwareUpdateId = firmwareUpdateId;
        return (T) this;
    }

    public FirmwareUpdate getFirmwareUpdate() {
        return firmwareUpdate;
    }

    public <T extends FirmwareUpdateInstallationMassCreate> T setFirmwareUpdate(FirmwareUpdate firmwareUpdate) {
        this.firmwareUpdate = firmwareUpdate;
        return (T) this;
    }

    public RemoteFilter getRemoteFilter() {
        return remoteFilter;
    }

    public <T extends FirmwareUpdateInstallationMassCreate> T setRemoteFilter(RemoteFilter remoteFilter) {
        this.remoteFilter = remoteFilter;
        return (T) this;
    }

    public OffsetDateTime getTargetInstallationDate() {
        return targetInstallationDate;
    }

    public <T extends FirmwareUpdateInstallationMassCreate> T setTargetInstallationDate(OffsetDateTime targetInstallationDate) {
        this.targetInstallationDate = targetInstallationDate;
        return (T) this;
    }

    @JsonIgnore
    public OffsetDateTime getDateInstalled() {
        return dateInstalled;
    }

    public <T extends FirmwareUpdateInstallationMassCreate> T setDateInstalled(OffsetDateTime dateInstalled) {
        this.dateInstalled = dateInstalled;
        return (T) this;
    }

    @JsonIgnore
    public OffsetDateTime getNextTimeForReminder() {
        return nextTimeForReminder;
    }

    public <T extends FirmwareUpdateInstallationMassCreate> T setNextTimeForReminder(OffsetDateTime nextTimeForReminder) {
        this.nextTimeForReminder = nextTimeForReminder;
        return (T) this;
    }

    @JsonIgnore
    public FirmwareInstallationState getFirmwareInstallationState() {
        return firmwareInstallationState;
    }

    public <T extends FirmwareUpdateInstallationMassCreate> T setFirmwareInstallationState(FirmwareInstallationState firmwareInstallationState) {
        this.firmwareInstallationState = firmwareInstallationState;
        return (T) this;
    }
}
