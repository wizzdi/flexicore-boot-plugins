package com.wizzdi.building.studio.service.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.wizzdi.building.studio.model.BuildingDwg;
import com.wizzdi.flexicore.security.validation.IdValid;
import jakarta.validation.constraints.NotNull;

@IdValid(targetField = "buildingDwg", field = "buildingDwgId", fieldType = BuildingDwg.class)
public class BuildingStudioConversionRequest {
    @NotNull
    private String buildingDwgId;
    @JsonIgnore
    private BuildingDwg buildingDwg;
    private Boolean exportSvg = true;
    private Boolean exportGlb = true;
    private Boolean exportPng = true;
    private String outputVersion;
    public String getBuildingDwgId() { return buildingDwgId; }
    public <T extends BuildingStudioConversionRequest> T setBuildingDwgId(String buildingDwgId) { this.buildingDwgId = buildingDwgId; return (T) this; }
    @JsonIgnore public BuildingDwg getBuildingDwg() { return buildingDwg; }
    public <T extends BuildingStudioConversionRequest> T setBuildingDwg(BuildingDwg buildingDwg) { this.buildingDwg = buildingDwg; return (T) this; }
    public Boolean getExportSvg() { return exportSvg; }
    public <T extends BuildingStudioConversionRequest> T setExportSvg(Boolean exportSvg) { this.exportSvg = exportSvg; return (T) this; }
    public Boolean getExportGlb() { return exportGlb; }
    public <T extends BuildingStudioConversionRequest> T setExportGlb(Boolean exportGlb) { this.exportGlb = exportGlb; return (T) this; }
    public Boolean getExportPng() { return exportPng; }
    public <T extends BuildingStudioConversionRequest> T setExportPng(Boolean exportPng) { this.exportPng = exportPng; return (T) this; }
    public String getOutputVersion() { return outputVersion; }
    public <T extends BuildingStudioConversionRequest> T setOutputVersion(String outputVersion) { this.outputVersion = outputVersion; return (T) this; }
}
