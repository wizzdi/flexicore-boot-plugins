package com.wizzdi.basic.iot.service.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.wizzdi.basic.iot.model.FirmwareInstallationState;
import com.wizzdi.basic.iot.model.FirmwareUpdate;
import com.wizzdi.basic.iot.model.Remote;
import com.wizzdi.flexicore.security.request.BasicCreate;
import com.wizzdi.flexicore.security.validation.Create;
import com.wizzdi.flexicore.security.validation.IdValid;
import com.wizzdi.flexicore.security.validation.Update;

import jakarta.validation.Valid;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;

import java.time.OffsetDateTime;

@IdValid.List({
        @IdValid(field = "targetRemoteId", targetField = "targetRemote", fieldType = Remote.class, groups = {Create.class, Update.class}),
        @IdValid(field = "firmwareUpdateId", targetField = "firmwareUpdate", fieldType = FirmwareUpdate.class, groups = {Create.class, Update.class}),
})
public class FirmwareUpdateInstallationCreate extends BasicCreate {

    @NotNull(groups = Create.class)
    private String firmwareUpdateId;
    @JsonIgnore
    private FirmwareUpdate firmwareUpdate;
    @NotNull(groups = Create.class)
    private String targetRemoteId;
    @JsonIgnore
    private Remote targetRemote;
    private OffsetDateTime targetInstallationDate;
    @JsonIgnore
    private OffsetDateTime dateInstalled;
    @JsonIgnore
    private OffsetDateTime nextTimeForReminder;
    @Null(groups = Create.class,message = "firwareInstallationState must NOT be provided when creating")
    private FirmwareInstallationState firmwareInstallationState;
    private OffsetDateTime expirationDate;


    public String getFirmwareUpdateId() {
        return firmwareUpdateId;
    }

    public <T extends FirmwareUpdateInstallationCreate> T setFirmwareUpdateId(String firmwareUpdateId) {
        this.firmwareUpdateId = firmwareUpdateId;
        return (T) this;
    }

    public FirmwareUpdate getFirmwareUpdate() {
        return firmwareUpdate;
    }

    public <T extends FirmwareUpdateInstallationCreate> T setFirmwareUpdate(FirmwareUpdate firmwareUpdate) {
        this.firmwareUpdate = firmwareUpdate;
        return (T) this;
    }

    public String getTargetRemoteId() {
        return targetRemoteId;
    }

    public <T extends FirmwareUpdateInstallationCreate> T setTargetRemoteId(String targetRemoteId) {
        this.targetRemoteId = targetRemoteId;
        return (T) this;
    }

    public Remote getTargetRemote() {
        return targetRemote;
    }

    public <T extends FirmwareUpdateInstallationCreate> T setTargetRemote(Remote targetRemote) {
        this.targetRemote = targetRemote;
        return (T) this;
    }

    public OffsetDateTime getTargetInstallationDate() {
        return targetInstallationDate;
    }

    public <T extends FirmwareUpdateInstallationCreate> T setTargetInstallationDate(OffsetDateTime targetInstallationDate) {
        this.targetInstallationDate = targetInstallationDate;
        return (T) this;
    }

    @JsonIgnore
    public OffsetDateTime getDateInstalled() {
        return dateInstalled;
    }

    public <T extends FirmwareUpdateInstallationCreate> T setDateInstalled(OffsetDateTime dateInstalled) {
        this.dateInstalled = dateInstalled;
        return (T) this;
    }

    @JsonIgnore
    public OffsetDateTime getNextTimeForReminder() {
        return nextTimeForReminder;
    }

    public <T extends FirmwareUpdateInstallationCreate> T setNextTimeForReminder(OffsetDateTime nextTimeForReminder) {
        this.nextTimeForReminder = nextTimeForReminder;
        return (T) this;
    }

    public FirmwareInstallationState getFirmwareInstallationState() {
        return firmwareInstallationState;
    }

    public <T extends FirmwareUpdateInstallationCreate> T setFirmwareInstallationState(FirmwareInstallationState firmwareInstallationState) {
        this.firmwareInstallationState = firmwareInstallationState;
        return (T) this;
    }

    public OffsetDateTime getExpirationDate() {
        return expirationDate;
    }

    public <T extends FirmwareUpdateInstallationCreate> T setExpirationDate(OffsetDateTime expirationDate) {
        this.expirationDate = expirationDate;
        return (T) this;
    }

    @JsonIgnore
    @AssertTrue(message = "firmware installation must be CANCELLED or not provided", groups = Update.class)
    public boolean isFirmwareInstallationStateValidForUpdate() {
        return firmwareInstallationState == null || firmwareInstallationState == FirmwareInstallationState.CANCELLED;
    }

}
