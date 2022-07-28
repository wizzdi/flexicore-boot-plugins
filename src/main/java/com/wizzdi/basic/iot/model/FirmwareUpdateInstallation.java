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
    private OffsetDateTime installationDateStart;
    @Column(columnDefinition = "timestamp with time zone")
    private OffsetDateTime dateInstalled;

    @ManyToOne(targetEntity = Remote.class)
    public Remote getTargetRemote() {
        return targetRemote;
    }

    public <T extends FirmwareUpdateInstallation> T setTargetRemote(Remote targetGateway) {
        this.targetRemote = targetGateway;
        return (T) this;
    }

    public OffsetDateTime getInstallationDateStart() {
        return installationDateStart;
    }

    public <T extends FirmwareUpdateInstallation> T setInstallationDateStart(OffsetDateTime installationDate) {
        this.installationDateStart = installationDate;
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
}
