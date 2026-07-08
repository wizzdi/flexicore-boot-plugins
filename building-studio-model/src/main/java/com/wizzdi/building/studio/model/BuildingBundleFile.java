package com.wizzdi.building.studio.model;

import com.flexicore.model.Baseclass;
import com.wizzdi.flexicore.file.model.FileResource;
import jakarta.persistence.*;

@Entity
@Table(indexes = {
        @Index(name = "building_bundle_file_bundle_idx", columnList = "buildingBundle_id,artifactType"),
        @Index(name = "building_bundle_file_external_id_idx", columnList = "externalId"),
        @Index(name = "building_bundle_file_entity_idx", columnList = "artifactEntityType,artifactEntityId")
})
public class BuildingBundleFile extends Baseclass {
    private String externalId;

    @ManyToOne(targetEntity = BuildingBundle.class)
    private BuildingBundle buildingBundle;

    @ManyToOne(targetEntity = FileResource.class)
    private FileResource fileResource;

    private String artifactType;
    private String artifactEntityType;
    private String artifactEntityId;
    private boolean defaultForType;

    public String getExternalId() { return externalId; }
    public <T extends BuildingBundleFile> T setExternalId(String externalId) { this.externalId = externalId; return (T) this; }

    @ManyToOne(targetEntity = BuildingBundle.class)
    public BuildingBundle getBuildingBundle() { return buildingBundle; }
    public <T extends BuildingBundleFile> T setBuildingBundle(BuildingBundle buildingBundle) { this.buildingBundle = buildingBundle; return (T) this; }

    @ManyToOne(targetEntity = FileResource.class)
    public FileResource getFileResource() { return fileResource; }
    public <T extends BuildingBundleFile> T setFileResource(FileResource fileResource) { this.fileResource = fileResource; return (T) this; }

    public String getArtifactType() { return artifactType; }
    public <T extends BuildingBundleFile> T setArtifactType(String artifactType) { this.artifactType = artifactType; return (T) this; }

    public String getArtifactEntityType() { return artifactEntityType; }
    public <T extends BuildingBundleFile> T setArtifactEntityType(String artifactEntityType) { this.artifactEntityType = artifactEntityType; return (T) this; }

    public String getArtifactEntityId() { return artifactEntityId; }
    public <T extends BuildingBundleFile> T setArtifactEntityId(String artifactEntityId) { this.artifactEntityId = artifactEntityId; return (T) this; }

    public boolean isDefaultForType() { return defaultForType; }
    public <T extends BuildingBundleFile> T setDefaultForType(boolean defaultForType) { this.defaultForType = defaultForType; return (T) this; }
}
