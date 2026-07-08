package com.wizzdi.building.studio.service.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.wizzdi.building.studio.model.BuildingBundle;
import com.wizzdi.flexicore.file.model.FileResource;
import com.wizzdi.flexicore.security.request.BasicCreate;
import com.wizzdi.flexicore.security.validation.Create;
import com.wizzdi.flexicore.security.validation.IdValid;
import com.wizzdi.flexicore.security.validation.Update;

@IdValid.List({
        @IdValid(targetField = "buildingBundle", field = "buildingBundleId", fieldType = BuildingBundle.class, groups = {Create.class, Update.class}),
        @IdValid(targetField = "fileResource", field = "fileResourceId", fieldType = FileResource.class, groups = {Create.class, Update.class}),
        @IdValid(targetField = "dxfFileResource", field = "dxfFileResourceId", fieldType = FileResource.class, groups = {Create.class, Update.class})
})
public class BuildingDwgCreate extends BasicCreate {
    private String externalId;
    private String buildingBundleId;
    private String fileResourceId;
    private String dxfFileResourceId;
    private String originalFileName;
    @JsonIgnore private BuildingBundle buildingBundle;
    @JsonIgnore private FileResource fileResource;
    @JsonIgnore private FileResource dxfFileResource;
    public String getExternalId() { return externalId; }
    public <T extends BuildingDwgCreate> T setExternalId(String externalId) { this.externalId = externalId; return (T) this; }
    public String getBuildingBundleId() { return buildingBundleId; }
    public <T extends BuildingDwgCreate> T setBuildingBundleId(String buildingBundleId) { this.buildingBundleId = buildingBundleId; return (T) this; }
    public String getFileResourceId() { return fileResourceId; }
    public <T extends BuildingDwgCreate> T setFileResourceId(String fileResourceId) { this.fileResourceId = fileResourceId; return (T) this; }
    public String getDxfFileResourceId() { return dxfFileResourceId; }
    public <T extends BuildingDwgCreate> T setDxfFileResourceId(String dxfFileResourceId) { this.dxfFileResourceId = dxfFileResourceId; return (T) this; }
    public String getOriginalFileName() { return originalFileName; }
    public <T extends BuildingDwgCreate> T setOriginalFileName(String originalFileName) { this.originalFileName = originalFileName; return (T) this; }
    @JsonIgnore public BuildingBundle getBuildingBundle() { return buildingBundle; }
    public <T extends BuildingDwgCreate> T setBuildingBundle(BuildingBundle buildingBundle) { this.buildingBundle = buildingBundle; return (T) this; }
    @JsonIgnore public FileResource getFileResource() { return fileResource; }
    public <T extends BuildingDwgCreate> T setFileResource(FileResource fileResource) { this.fileResource = fileResource; return (T) this; }
    @JsonIgnore public FileResource getDxfFileResource() { return dxfFileResource; }
    public <T extends BuildingDwgCreate> T setDxfFileResource(FileResource dxfFileResource) { this.dxfFileResource = dxfFileResource; return (T) this; }
}
