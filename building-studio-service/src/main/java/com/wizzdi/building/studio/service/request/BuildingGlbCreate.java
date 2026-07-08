package com.wizzdi.building.studio.service.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.wizzdi.building.studio.model.BuildingBundle;
import com.wizzdi.building.studio.model.BuildingDwg;
import com.wizzdi.building.studio.model.BuildingSvg;
import com.wizzdi.flexicore.file.model.FileResource;
import com.wizzdi.flexicore.security.request.BasicCreate;
import com.wizzdi.flexicore.security.validation.Create;
import com.wizzdi.flexicore.security.validation.IdValid;
import com.wizzdi.flexicore.security.validation.Update;

@IdValid.List({
        @IdValid(targetField = "buildingBundle", field = "buildingBundleId", fieldType = BuildingBundle.class, groups = {Create.class, Update.class}),
        @IdValid(targetField = "sourceDwg", field = "sourceDwgId", fieldType = BuildingDwg.class, groups = {Create.class, Update.class}),
        @IdValid(targetField = "sourceSvg", field = "sourceSvgId", fieldType = BuildingSvg.class, groups = {Create.class, Update.class}),
        @IdValid(targetField = "fileResource", field = "fileResourceId", fieldType = FileResource.class, groups = {Create.class, Update.class})
})
public class BuildingGlbCreate extends BasicCreate {
    private String externalId;
    private String buildingBundleId;
    private String sourceDwgId;
    private String sourceSvgId;
    private String fileResourceId;
    private String glbKind;
    private String cameraPointsJson;
    @JsonIgnore private BuildingBundle buildingBundle;
    @JsonIgnore private BuildingDwg sourceDwg;
    @JsonIgnore private BuildingSvg sourceSvg;
    @JsonIgnore private FileResource fileResource;
    public String getExternalId() { return externalId; }
    public <T extends BuildingGlbCreate> T setExternalId(String externalId) { this.externalId = externalId; return (T) this; }
    public String getBuildingBundleId() { return buildingBundleId; }
    public <T extends BuildingGlbCreate> T setBuildingBundleId(String buildingBundleId) { this.buildingBundleId = buildingBundleId; return (T) this; }
    public String getSourceDwgId() { return sourceDwgId; }
    public <T extends BuildingGlbCreate> T setSourceDwgId(String sourceDwgId) { this.sourceDwgId = sourceDwgId; return (T) this; }
    public String getSourceSvgId() { return sourceSvgId; }
    public <T extends BuildingGlbCreate> T setSourceSvgId(String sourceSvgId) { this.sourceSvgId = sourceSvgId; return (T) this; }
    public String getFileResourceId() { return fileResourceId; }
    public <T extends BuildingGlbCreate> T setFileResourceId(String fileResourceId) { this.fileResourceId = fileResourceId; return (T) this; }
    public String getGlbKind() { return glbKind; }
    public <T extends BuildingGlbCreate> T setGlbKind(String glbKind) { this.glbKind = glbKind; return (T) this; }
    public String getCameraPointsJson() { return cameraPointsJson; }
    public <T extends BuildingGlbCreate> T setCameraPointsJson(String cameraPointsJson) { this.cameraPointsJson = cameraPointsJson; return (T) this; }
    @JsonIgnore public BuildingBundle getBuildingBundle() { return buildingBundle; }
    public <T extends BuildingGlbCreate> T setBuildingBundle(BuildingBundle buildingBundle) { this.buildingBundle = buildingBundle; return (T) this; }
    @JsonIgnore public BuildingDwg getSourceDwg() { return sourceDwg; }
    public <T extends BuildingGlbCreate> T setSourceDwg(BuildingDwg sourceDwg) { this.sourceDwg = sourceDwg; return (T) this; }
    @JsonIgnore public BuildingSvg getSourceSvg() { return sourceSvg; }
    public <T extends BuildingGlbCreate> T setSourceSvg(BuildingSvg sourceSvg) { this.sourceSvg = sourceSvg; return (T) this; }
    @JsonIgnore public FileResource getFileResource() { return fileResource; }
    public <T extends BuildingGlbCreate> T setFileResource(FileResource fileResource) { this.fileResource = fileResource; return (T) this; }
}
