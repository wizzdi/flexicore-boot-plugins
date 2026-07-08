package com.wizzdi.building.studio.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flexicore.model.Baseclass;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(indexes = {
        @Index(name = "building_bundle_external_id_idx", columnList = "externalId"),
        @Index(name = "building_bundle_status_idx", columnList = "status")
})
public class BuildingBundle extends Baseclass {

    private String externalId;
    private String status;

    @ManyToOne(targetEntity = BuildingDwg.class)
    private BuildingDwg sourceDwg;

    @ManyToOne(targetEntity = BuildingSvg.class)
    private BuildingSvg activeSvg;

    @ManyToOne(targetEntity = BuildingGlb.class)
    private BuildingGlb activeGlb;

    @JsonIgnore
    @OneToMany(targetEntity = BuildingBundleFile.class, mappedBy = "buildingBundle")
    private List<BuildingBundleFile> files = new ArrayList<>();

    public String getExternalId() { return externalId; }
    public <T extends BuildingBundle> T setExternalId(String externalId) { this.externalId = externalId; return (T) this; }

    public String getStatus() { return status; }
    public <T extends BuildingBundle> T setStatus(String status) { this.status = status; return (T) this; }

    @ManyToOne(targetEntity = BuildingDwg.class)
    public BuildingDwg getSourceDwg() { return sourceDwg; }
    public <T extends BuildingBundle> T setSourceDwg(BuildingDwg sourceDwg) { this.sourceDwg = sourceDwg; return (T) this; }

    @ManyToOne(targetEntity = BuildingSvg.class)
    public BuildingSvg getActiveSvg() { return activeSvg; }
    public <T extends BuildingBundle> T setActiveSvg(BuildingSvg activeSvg) { this.activeSvg = activeSvg; return (T) this; }

    @ManyToOne(targetEntity = BuildingGlb.class)
    public BuildingGlb getActiveGlb() { return activeGlb; }
    public <T extends BuildingBundle> T setActiveGlb(BuildingGlb activeGlb) { this.activeGlb = activeGlb; return (T) this; }

    @OneToMany(targetEntity = BuildingBundleFile.class, mappedBy = "buildingBundle")
    @JsonIgnore
    public List<BuildingBundleFile> getFiles() { return files; }
    public <T extends BuildingBundle> T setFiles(List<BuildingBundleFile> files) { this.files = files; return (T) this; }
}
