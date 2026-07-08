package com.wizzdi.building.studio.model;

import com.flexicore.model.Baseclass;
import com.wizzdi.flexicore.file.model.FileResource;
import jakarta.persistence.*;

@Entity
@Table(indexes = {
        @Index(name = "building_svg_bundle_idx", columnList = "buildingBundle_id"),
        @Index(name = "building_svg_external_id_idx", columnList = "externalId")
})
public class BuildingSvg extends Baseclass {
    private String externalId;

    @ManyToOne(targetEntity = BuildingBundle.class)
    private BuildingBundle buildingBundle;

    @ManyToOne(targetEntity = BuildingDwg.class)
    private BuildingDwg sourceDwg;

    @ManyToOne(targetEntity = FileResource.class)
    private FileResource fileResource;

    private String svgKind;
    private Double width;
    private Double height;

    @Lob
    private String selectedAreaJson;

    public String getExternalId() { return externalId; }
    public <T extends BuildingSvg> T setExternalId(String externalId) { this.externalId = externalId; return (T) this; }

    @ManyToOne(targetEntity = BuildingBundle.class)
    public BuildingBundle getBuildingBundle() { return buildingBundle; }
    public <T extends BuildingSvg> T setBuildingBundle(BuildingBundle buildingBundle) { this.buildingBundle = buildingBundle; return (T) this; }

    @ManyToOne(targetEntity = BuildingDwg.class)
    public BuildingDwg getSourceDwg() { return sourceDwg; }
    public <T extends BuildingSvg> T setSourceDwg(BuildingDwg sourceDwg) { this.sourceDwg = sourceDwg; return (T) this; }

    @ManyToOne(targetEntity = FileResource.class)
    public FileResource getFileResource() { return fileResource; }
    public <T extends BuildingSvg> T setFileResource(FileResource fileResource) { this.fileResource = fileResource; return (T) this; }

    public String getSvgKind() { return svgKind; }
    public <T extends BuildingSvg> T setSvgKind(String svgKind) { this.svgKind = svgKind; return (T) this; }

    public Double getWidth() { return width; }
    public <T extends BuildingSvg> T setWidth(Double width) { this.width = width; return (T) this; }

    public Double getHeight() { return height; }
    public <T extends BuildingSvg> T setHeight(Double height) { this.height = height; return (T) this; }

    @Lob
    public String getSelectedAreaJson() { return selectedAreaJson; }
    public <T extends BuildingSvg> T setSelectedAreaJson(String selectedAreaJson) { this.selectedAreaJson = selectedAreaJson; return (T) this; }
}
