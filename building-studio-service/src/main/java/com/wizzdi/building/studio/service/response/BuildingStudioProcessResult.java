package com.wizzdi.building.studio.service.response;

public class BuildingStudioProcessResult {
    private String status;
    private String log;
    private String dxfPath;
    private String svgPath;
    private String glbPath;
    private String pngPath;
    private Double svgWidth;
    private Double svgHeight;
    private String selectedAreaJson;
    private String cameraPointsJson;

    public String getStatus() { return status; }
    public BuildingStudioProcessResult setStatus(String status) { this.status = status; return this; }
    public String getLog() { return log; }
    public BuildingStudioProcessResult setLog(String log) { this.log = log; return this; }
    public String getDxfPath() { return dxfPath; }
    public BuildingStudioProcessResult setDxfPath(String dxfPath) { this.dxfPath = dxfPath; return this; }
    public String getSvgPath() { return svgPath; }
    public BuildingStudioProcessResult setSvgPath(String svgPath) { this.svgPath = svgPath; return this; }
    public String getGlbPath() { return glbPath; }
    public BuildingStudioProcessResult setGlbPath(String glbPath) { this.glbPath = glbPath; return this; }
    public String getPngPath() { return pngPath; }
    public BuildingStudioProcessResult setPngPath(String pngPath) { this.pngPath = pngPath; return this; }
    public Double getSvgWidth() { return svgWidth; }
    public BuildingStudioProcessResult setSvgWidth(Double svgWidth) { this.svgWidth = svgWidth; return this; }
    public Double getSvgHeight() { return svgHeight; }
    public BuildingStudioProcessResult setSvgHeight(Double svgHeight) { this.svgHeight = svgHeight; return this; }
    public String getSelectedAreaJson() { return selectedAreaJson; }
    public BuildingStudioProcessResult setSelectedAreaJson(String selectedAreaJson) { this.selectedAreaJson = selectedAreaJson; return this; }
    public String getCameraPointsJson() { return cameraPointsJson; }
    public BuildingStudioProcessResult setCameraPointsJson(String cameraPointsJson) { this.cameraPointsJson = cameraPointsJson; return this; }
}
