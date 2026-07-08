package com.wizzdi.building.studio.service.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.wizzdi.building.studio.model.BuildingBundleFile;
import com.wizzdi.maps.model.BuildingFloor;
import com.wizzdi.flexicore.security.request.BasicCreate;
import com.wizzdi.flexicore.security.validation.IdValid;

@IdValid.List({
        @IdValid(targetField = "buildingFloor", field = "buildingFloorId", fieldType = BuildingFloor.class),
        @IdValid(targetField = "bundleFile", field = "bundleFileId", fieldType = BuildingBundleFile.class)
})
public class BuildingFloorResourceCreate extends BasicCreate {
    private String buildingFloorId;
    private String bundleFileId;
    private String resourcePurpose;
    private Boolean activeForPurpose;
    @JsonIgnore private BuildingFloor buildingFloor;
    @JsonIgnore private BuildingBundleFile bundleFile;
    public String getBuildingFloorId() { return buildingFloorId; }
    public <T extends BuildingFloorResourceCreate> T setBuildingFloorId(String buildingFloorId) { this.buildingFloorId = buildingFloorId; return (T) this; }
    public String getBundleFileId() { return bundleFileId; }
    public <T extends BuildingFloorResourceCreate> T setBundleFileId(String bundleFileId) { this.bundleFileId = bundleFileId; return (T) this; }
    public String getResourcePurpose() { return resourcePurpose; }
    public <T extends BuildingFloorResourceCreate> T setResourcePurpose(String resourcePurpose) { this.resourcePurpose = resourcePurpose; return (T) this; }
    public Boolean getActiveForPurpose() { return activeForPurpose; }
    public <T extends BuildingFloorResourceCreate> T setActiveForPurpose(Boolean activeForPurpose) { this.activeForPurpose = activeForPurpose; return (T) this; }
    @JsonIgnore public BuildingFloor getBuildingFloor() { return buildingFloor; }
    public <T extends BuildingFloorResourceCreate> T setBuildingFloor(BuildingFloor buildingFloor) { this.buildingFloor = buildingFloor; return (T) this; }
    @JsonIgnore public BuildingBundleFile getBundleFile() { return bundleFile; }
    public <T extends BuildingFloorResourceCreate> T setBundleFile(BuildingBundleFile bundleFile) { this.bundleFile = bundleFile; return (T) this; }
}
