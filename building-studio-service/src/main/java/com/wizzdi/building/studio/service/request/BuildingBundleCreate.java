package com.wizzdi.building.studio.service.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.wizzdi.building.studio.model.BuildingDwg;
import com.wizzdi.building.studio.model.BuildingGlb;
import com.wizzdi.building.studio.model.BuildingSvg;
import com.wizzdi.flexicore.security.request.BasicCreate;
import com.wizzdi.flexicore.security.validation.Create;
import com.wizzdi.flexicore.security.validation.IdValid;
import com.wizzdi.flexicore.security.validation.Update;

@IdValid.List({
        @IdValid(targetField = "sourceDwg", field = "sourceDwgId", fieldType = BuildingDwg.class, groups = {Create.class, Update.class}),
        @IdValid(targetField = "activeSvg", field = "activeSvgId", fieldType = BuildingSvg.class, groups = {Create.class, Update.class}),
        @IdValid(targetField = "activeGlb", field = "activeGlbId", fieldType = BuildingGlb.class, groups = {Create.class, Update.class})
})
public class BuildingBundleCreate extends BasicCreate {
    private String externalId;
    private String status;
    private String sourceDwgId;
    private String activeSvgId;
    private String activeGlbId;
    @JsonIgnore private BuildingDwg sourceDwg;
    @JsonIgnore private BuildingSvg activeSvg;
    @JsonIgnore private BuildingGlb activeGlb;
    public String getExternalId() { return externalId; }
    public <T extends BuildingBundleCreate> T setExternalId(String externalId) { this.externalId = externalId; return (T) this; }
    public String getStatus() { return status; }
    public <T extends BuildingBundleCreate> T setStatus(String status) { this.status = status; return (T) this; }
    public String getSourceDwgId() { return sourceDwgId; }
    public <T extends BuildingBundleCreate> T setSourceDwgId(String sourceDwgId) { this.sourceDwgId = sourceDwgId; return (T) this; }
    public String getActiveSvgId() { return activeSvgId; }
    public <T extends BuildingBundleCreate> T setActiveSvgId(String activeSvgId) { this.activeSvgId = activeSvgId; return (T) this; }
    public String getActiveGlbId() { return activeGlbId; }
    public <T extends BuildingBundleCreate> T setActiveGlbId(String activeGlbId) { this.activeGlbId = activeGlbId; return (T) this; }
    @JsonIgnore public BuildingDwg getSourceDwg() { return sourceDwg; }
    public <T extends BuildingBundleCreate> T setSourceDwg(BuildingDwg sourceDwg) { this.sourceDwg = sourceDwg; return (T) this; }
    @JsonIgnore public BuildingSvg getActiveSvg() { return activeSvg; }
    public <T extends BuildingBundleCreate> T setActiveSvg(BuildingSvg activeSvg) { this.activeSvg = activeSvg; return (T) this; }
    @JsonIgnore public BuildingGlb getActiveGlb() { return activeGlb; }
    public <T extends BuildingBundleCreate> T setActiveGlb(BuildingGlb activeGlb) { this.activeGlb = activeGlb; return (T) this; }
}
