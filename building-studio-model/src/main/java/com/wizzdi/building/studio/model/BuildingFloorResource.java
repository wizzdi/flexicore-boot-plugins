package com.wizzdi.building.studio.model;

import com.flexicore.model.Baseclass;
import com.wizzdi.maps.model.BuildingFloor;
import jakarta.persistence.*;

@Entity
@Table(indexes = {
        @Index(name = "building_floor_resource_idx", columnList = "buildingFloor_id,bundleFile_id,resourcePurpose")
})
public class BuildingFloorResource extends Baseclass {

    @ManyToOne(targetEntity = BuildingFloor.class)
    private BuildingFloor buildingFloor;

    @ManyToOne(targetEntity = BuildingBundleFile.class)
    private BuildingBundleFile bundleFile;

    private String resourcePurpose;
    private boolean activeForPurpose;

    @ManyToOne(targetEntity = BuildingFloor.class)
    public BuildingFloor getBuildingFloor() { return buildingFloor; }
    public <T extends BuildingFloorResource> T setBuildingFloor(BuildingFloor buildingFloor) { this.buildingFloor = buildingFloor; return (T) this; }

    @ManyToOne(targetEntity = BuildingBundleFile.class)
    public BuildingBundleFile getBundleFile() { return bundleFile; }
    public <T extends BuildingFloorResource> T setBundleFile(BuildingBundleFile bundleFile) { this.bundleFile = bundleFile; return (T) this; }

    public String getResourcePurpose() { return resourcePurpose; }
    public <T extends BuildingFloorResource> T setResourcePurpose(String resourcePurpose) { this.resourcePurpose = resourcePurpose; return (T) this; }

    public boolean isActiveForPurpose() { return activeForPurpose; }
    public <T extends BuildingFloorResource> T setActiveForPurpose(boolean activeForPurpose) { this.activeForPurpose = activeForPurpose; return (T) this; }
}
