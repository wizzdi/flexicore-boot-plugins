package com.wizzdi.building.studio.model;

import com.flexicore.model.Baseclass;
import com.wizzdi.flexicore.file.model.FileResource;
import jakarta.persistence.*;

@Entity
@Table(indexes = {
        @Index(name = "building_glb_bundle_idx", columnList = "buildingBundle_id"),
        @Index(name = "building_glb_external_id_idx", columnList = "externalId")
})
public class BuildingGlb extends Baseclass {
    private String externalId;

    @ManyToOne(targetEntity = BuildingBundle.class)
    private BuildingBundle buildingBundle;

    @ManyToOne(targetEntity = BuildingDwg.class)
    private BuildingDwg sourceDwg;

    @ManyToOne(targetEntity = BuildingSvg.class)
    private BuildingSvg sourceSvg;

    @ManyToOne(targetEntity = FileResource.class)
    private FileResource fileResource;

    private String glbKind;

    @Lob
    private String cameraPointsJson;

    public String getExternalId() { return externalId; }
    public <T extends BuildingGlb> T setExternalId(String externalId) { this.externalId = externalId; return (T) this; }

    @ManyToOne(targetEntity = BuildingBundle.class)
    public BuildingBundle getBuildingBundle() { return buildingBundle; }
    public <T extends BuildingGlb> T setBuildingBundle(BuildingBundle buildingBundle) { this.buildingBundle = buildingBundle; return (T) this; }

    @ManyToOne(targetEntity = BuildingDwg.class)
    public BuildingDwg getSourceDwg() { return sourceDwg; }
    public <T extends BuildingGlb> T setSourceDwg(BuildingDwg sourceDwg) { this.sourceDwg = sourceDwg; return (T) this; }

    @ManyToOne(targetEntity = BuildingSvg.class)
    public BuildingSvg getSourceSvg() { return sourceSvg; }
    public <T extends BuildingGlb> T setSourceSvg(BuildingSvg sourceSvg) { this.sourceSvg = sourceSvg; return (T) this; }

    @ManyToOne(targetEntity = FileResource.class)
    public FileResource getFileResource() { return fileResource; }
    public <T extends BuildingGlb> T setFileResource(FileResource fileResource) { this.fileResource = fileResource; return (T) this; }

    public String getGlbKind() { return glbKind; }
    public <T extends BuildingGlb> T setGlbKind(String glbKind) { this.glbKind = glbKind; return (T) this; }

    @Lob
    public String getCameraPointsJson() { return cameraPointsJson; }
    public <T extends BuildingGlb> T setCameraPointsJson(String cameraPointsJson) { this.cameraPointsJson = cameraPointsJson; return (T) this; }
}
