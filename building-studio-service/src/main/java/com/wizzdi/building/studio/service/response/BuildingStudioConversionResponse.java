package com.wizzdi.building.studio.service.response;

import com.wizzdi.building.studio.model.*;
import java.util.ArrayList;
import java.util.List;

public class BuildingStudioConversionResponse {
    private BuildingBundle buildingBundle;
    private BuildingDwg buildingDwg;
    private BuildingSvg svg;
    private BuildingGlb glb;
    private List<BuildingBundleFile> files = new ArrayList<>();
    private String status;
    private String logTail;

    public BuildingBundle getBuildingBundle() { return buildingBundle; }
    public <T extends BuildingStudioConversionResponse> T setBuildingBundle(BuildingBundle buildingBundle) { this.buildingBundle = buildingBundle; return (T) this; }
    public BuildingDwg getBuildingDwg() { return buildingDwg; }
    public <T extends BuildingStudioConversionResponse> T setBuildingDwg(BuildingDwg buildingDwg) { this.buildingDwg = buildingDwg; return (T) this; }
    public BuildingSvg getSvg() { return svg; }
    public <T extends BuildingStudioConversionResponse> T setSvg(BuildingSvg svg) { this.svg = svg; return (T) this; }
    public BuildingGlb getGlb() { return glb; }
    public <T extends BuildingStudioConversionResponse> T setGlb(BuildingGlb glb) { this.glb = glb; return (T) this; }
    public List<BuildingBundleFile> getFiles() { return files; }
    public <T extends BuildingStudioConversionResponse> T setFiles(List<BuildingBundleFile> files) { this.files = files; return (T) this; }
    public String getStatus() { return status; }
    public <T extends BuildingStudioConversionResponse> T setStatus(String status) { this.status = status; return (T) this; }
    public String getLogTail() { return logTail; }
    public <T extends BuildingStudioConversionResponse> T setLogTail(String logTail) { this.logTail = logTail; return (T) this; }
}
