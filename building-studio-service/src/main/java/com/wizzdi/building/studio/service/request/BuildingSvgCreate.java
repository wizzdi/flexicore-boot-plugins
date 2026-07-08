package com.wizzdi.building.studio.service.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.wizzdi.building.studio.model.BuildingBundle;
import com.wizzdi.building.studio.model.BuildingDwg;
import com.wizzdi.flexicore.file.model.FileResource;
import com.wizzdi.flexicore.security.request.BasicCreate;
import com.wizzdi.flexicore.security.validation.Create;
import com.wizzdi.flexicore.security.validation.IdValid;
import com.wizzdi.flexicore.security.validation.Update;

@IdValid.List({
        @IdValid(targetField = "buildingBundle", field = "buildingBundleId", fieldType = BuildingBundle.class, groups = {Create.class, Update.class}),
        @IdValid(targetField = "sourceDwg", field = "sourceDwgId", fieldType = BuildingDwg.class, groups = {Create.class, Update.class}),
        @IdValid(targetField = "fileResource", field = "fileResourceId", fieldType = FileResource.class, groups = {Create.class, Update.class})
})
public class BuildingSvgCreate extends BasicCreate {
    private String externalId;
    private String buildingBundleId;
    private String sourceDwgId;
    private String fileResourceId;
    private String svgKind;
    private String selectedAreaJson;
    private Double width;
    private Double height;
    @JsonIgnore private BuildingBundle buildingBundle;
    @JsonIgnore private BuildingDwg sourceDwg;
    @JsonIgnore private FileResource fileResource;
    public String getExternalId() { return externalId; }
    public <T extends BuildingSvgCreate> T setExternalId(String externalId) { this.externalId = externalId; return (T) this; }
    public String getBuildingBundleId() { return buildingBundleId; }
    public <T extends BuildingSvgCreate> T setBuildingBundleId(String buildingBundleId) { this.buildingBundleId = buildingBundleId; return (T) this; }
    public String getSourceDwgId() { return sourceDwgId; }
    public <T extends BuildingSvgCreate> T setSourceDwgId(String sourceDwgId) { this.sourceDwgId = sourceDwgId; return (T) this; }
    public String getFileResourceId() { return fileResourceId; }
    public <T extends BuildingSvgCreate> T setFileResourceId(String fileResourceId) { this.fileResourceId = fileResourceId; return (T) this; }
    public String getSvgKind() { return svgKind; }
    public <T extends BuildingSvgCreate> T setSvgKind(String svgKind) { this.svgKind = svgKind; return (T) this; }
    public String getSelectedAreaJson() { return selectedAreaJson; }
    public <T extends BuildingSvgCreate> T setSelectedAreaJson(String selectedAreaJson) { this.selectedAreaJson = selectedAreaJson; return (T) this; }
    public Double getWidth() { return width; }
    public <T extends BuildingSvgCreate> T setWidth(Double width) { this.width = width; return (T) this; }
    public Double getHeight() { return height; }
    public <T extends BuildingSvgCreate> T setHeight(Double height) { this.height = height; return (T) this; }
    @JsonIgnore public BuildingBundle getBuildingBundle() { return buildingBundle; }
    public <T extends BuildingSvgCreate> T setBuildingBundle(BuildingBundle buildingBundle) { this.buildingBundle = buildingBundle; return (T) this; }
    @JsonIgnore public BuildingDwg getSourceDwg() { return sourceDwg; }
    public <T extends BuildingSvgCreate> T setSourceDwg(BuildingDwg sourceDwg) { this.sourceDwg = sourceDwg; return (T) this; }
    @JsonIgnore public FileResource getFileResource() { return fileResource; }
    public <T extends BuildingSvgCreate> T setFileResource(FileResource fileResource) { this.fileResource = fileResource; return (T) this; }
}
