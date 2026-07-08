package com.wizzdi.building.studio.service.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.wizzdi.building.studio.model.BuildingBundle;
import com.wizzdi.flexicore.security.validation.IdValid;
import com.wizzdi.flexicore.security.validation.Update;

@IdValid(targetField = "buildingBundle", field = "id", fieldType = BuildingBundle.class, groups = {Update.class})
public class BuildingBundleUpdate extends BuildingBundleCreate {
    private String id;
    @JsonIgnore private BuildingBundle buildingBundle;
    public String getId() { return id; }
    public <T extends BuildingBundleUpdate> T setId(String id) { this.id = id; return (T) this; }
    @JsonIgnore public BuildingBundle getBuildingBundle() { return buildingBundle; }
    public <T extends BuildingBundleUpdate> T setBuildingBundle(BuildingBundle buildingBundle) { this.buildingBundle = buildingBundle; return (T) this; }
}
