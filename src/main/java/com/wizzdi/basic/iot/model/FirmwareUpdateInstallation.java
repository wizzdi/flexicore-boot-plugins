package com.wizzdi.basic.iot.model;

import com.flexicore.model.SecuredBasic;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import java.time.OffsetDateTime;

@Entity
public class FirmwareUpdateInstallation extends SecuredBasic {

    @ManyToOne(targetEntity = FirmwareUpdate.class)
    private FirmwareUpdate firmwareUpdate;
    @ManyToOne(targetEntity = Remote.class)
    private Remote targetRemote;
    @Column(columnDefinition = "timestamp with time zone")
    private OffsetDateTime targetInstallationDate;
    @Column(columnDefinition = "timestamp with time zone")
    private OffsetDateTime nextTimeForReminder;
    @Column(columnDefinition = "timestamp with time zone")
    private OffsetDateTime dateInstalled;

    private FirmwareInstallationState firmwareInstallationState;


    @ManyToOne(targetEntity = Remote.class)
    public Remote getTargetRemote() {
        return targetRemote;
    }

    public <T extends FirmwareUpdateInstallation> T setTargetRemote(Remote targetGateway) {
        this.targetRemote = targetGateway;
        return (T) this;
    }

    public OffsetDateTime getTargetInstallationDate() {
        return targetInstallationDate;
    }

    public <T extends FirmwareUpdateInstallation> T setTargetInstallationDate(OffsetDateTime installationDate) {
        this.targetInstallationDate = installationDate;
        return (T) this;
    }

    public OffsetDateTime getDateInstalled() {
        return dateInstalled;
    }

    public <T extends FirmwareUpdateInstallation> T setDateInstalled(OffsetDateTime dateInstalled) {
        this.dateInstalled = dateInstalled;
        return (T) this;
    }

    @ManyToOne(targetEntity = FirmwareUpdate.class)
    public FirmwareUpdate getFirmwareUpdate() {
        return firmwareUpdate;
    }

    public <T extends FirmwareUpdateInstallation> T setFirmwareUpdate(FirmwareUpdate firmwareUpdate) {
        this.firmwareUpdate = firmwareUpdate;
        return (T) this;
    }

    public OffsetDateTime getNextTimeForReminder() {
        return nextTimeForReminder;
    }

    public <T extends FirmwareUpdateInstallation> T setNextTimeForReminder(OffsetDateTime lastReminderSent) {
        this.nextTimeForReminder = lastReminderSent;
        return (T) this;
    }

    public FirmwareInstallationState getFirmwareInstallationState() {
        return firmwareInstallationState;
    }

    public <T extends FirmwareUpdateInstallation> T setFirmwareInstallationState(FirmwareInstallationState firmwareInstallationState) {
        this.firmwareInstallationState = firmwareInstallationState;
        return (T) this;
    }
}
