package com.wizzdi.basic.iot.service.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.wizzdi.basic.iot.model.FirmwareInstallationState;
import com.wizzdi.basic.iot.model.FirmwareUpdate;
import com.wizzdi.basic.iot.model.FirmwareUpdateInstallation;
import com.wizzdi.basic.iot.model.Remote;
import com.wizzdi.flexicore.security.request.BasicPropertiesFilter;
import com.wizzdi.flexicore.security.request.PaginationFilter;
import com.wizzdi.flexicore.security.validation.Create;
import com.wizzdi.flexicore.security.validation.IdValid;
import com.wizzdi.flexicore.security.validation.Update;

import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@IdValid.List({
        @IdValid(field = "targetRemoteIds", targetField = "targetRemotes", fieldType = Remote.class, groups = {Create.class, Update.class}),
        @IdValid(field = "firmwareUpdateIds", targetField = "firmwareUpdates", fieldType = FirmwareUpdate.class, groups = {Create.class, Update.class}),
})
public class FirmwareUpdateInstallationFilter extends PaginationFilter {



    private Set<String> firmwareUpdateIds =new HashSet<>();
    @JsonIgnore
    private List<FirmwareUpdate> firmwareUpdates;
    private Set<String> targetRemoteIds =new HashSet<>();
    @JsonIgnore
    private List<Remote> targetRemotes;
    private BasicPropertiesFilter basicPropertiesFilter;
    private OffsetDateTime availableAtTime;
    @JsonIgnore
    private OffsetDateTime timeForReminder;
    private Set<FirmwareInstallationState> firmwareInstallationStates;
    @JsonIgnore
    private List<FirmwareUpdateInstallation> notInstallations;

    private Set<String> versions;

    public Set<String> getFirmwareUpdateIds() {
        return firmwareUpdateIds;
    }

    public <T extends FirmwareUpdateInstallationFilter> T setFirmwareUpdateIds(Set<String> firmwareUpdateIds) {
        this.firmwareUpdateIds = firmwareUpdateIds;
        return (T) this;
    }

    @JsonIgnore
    public List<FirmwareUpdate> getFirmwareUpdates() {
        return firmwareUpdates;
    }

    public <T extends FirmwareUpdateInstallationFilter> T setFirmwareUpdates(List<FirmwareUpdate> firmwareUpdates) {
        this.firmwareUpdates = firmwareUpdates;
        return (T) this;
    }

    public Set<String> getTargetRemoteIds() {
        return targetRemoteIds;
    }

    public <T extends FirmwareUpdateInstallationFilter> T setTargetRemoteIds(Set<String> targetRemoteIds) {
        this.targetRemoteIds = targetRemoteIds;
        return (T) this;
    }

    @JsonIgnore
    public List<Remote> getTargetRemotes() {
        return targetRemotes;
    }

    public <T extends FirmwareUpdateInstallationFilter> T setTargetRemotes(List<Remote> targetRemotes) {
        this.targetRemotes = targetRemotes;
        return (T) this;
    }

    public BasicPropertiesFilter getBasicPropertiesFilter() {
        return basicPropertiesFilter;
    }

    public <T extends FirmwareUpdateInstallationFilter> T setBasicPropertiesFilter(BasicPropertiesFilter basicPropertiesFilter) {
        this.basicPropertiesFilter = basicPropertiesFilter;
        return (T) this;
    }

    public OffsetDateTime getAvailableAtTime() {
        return availableAtTime;
    }

    public <T extends FirmwareUpdateInstallationFilter> T setAvailableAtTime(OffsetDateTime availableAtTime) {
        this.availableAtTime = availableAtTime;
        return (T) this;
    }

    @JsonIgnore
    public OffsetDateTime getTimeForReminder() {
        return timeForReminder;
    }

    public <T extends FirmwareUpdateInstallationFilter> T setTimeForReminder(OffsetDateTime timeForReminder) {
        this.timeForReminder = timeForReminder;
        return (T) this;
    }

    public Set<FirmwareInstallationState> getFirmwareInstallationStates() {
        return firmwareInstallationStates;
    }

    public <T extends FirmwareUpdateInstallationFilter> T setFirmwareInstallationStates(Set<FirmwareInstallationState> firmwareInstallationStates) {
        this.firmwareInstallationStates = firmwareInstallationStates;
        return (T) this;
    }

    @JsonIgnore
    public List<FirmwareUpdateInstallation> getNotInstallations() {
        return notInstallations;
    }

    public <T extends FirmwareUpdateInstallationFilter> T setNotInstallations(List<FirmwareUpdateInstallation> notInstallations) {
        this.notInstallations = notInstallations;
        return (T) this;
    }

    public Set<String> getVersions() {
        return versions;
    }

    public <T extends FirmwareUpdateInstallationFilter> T setVersions(Set<String> versions) {
        this.versions = versions;
        return (T) this;
    }
}
