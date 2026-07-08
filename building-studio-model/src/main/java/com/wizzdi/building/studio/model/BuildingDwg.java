package com.wizzdi.building.studio.model;

import com.flexicore.model.Baseclass;
import com.wizzdi.flexicore.file.model.FileResource;
import jakarta.persistence.*;
import java.time.OffsetDateTime;

@Entity
@Table(indexes = {
        @Index(name = "building_dwg_bundle_idx", columnList = "buildingBundle_id"),
        @Index(name = "building_dwg_external_id_idx", columnList = "externalId"),
        @Index(name = "building_dwg_status_idx", columnList = "conversionStatus")
})
public class BuildingDwg extends Baseclass {
    private String externalId;

    @ManyToOne(targetEntity = BuildingBundle.class)
    private BuildingBundle buildingBundle;

    @ManyToOne(targetEntity = FileResource.class)
    private FileResource fileResource;

    @ManyToOne(targetEntity = FileResource.class)
    private FileResource dxfFileResource;

    private String originalFileName;
    private String conversionStatus;
    private String converterJobId;

    @Lob
    private String conversionLog;

    @Column(columnDefinition = "timestamp with time zone")
    private OffsetDateTime convertedAt;

    public String getExternalId() { return externalId; }
    public <T extends BuildingDwg> T setExternalId(String externalId) { this.externalId = externalId; return (T) this; }

    @ManyToOne(targetEntity = BuildingBundle.class)
    public BuildingBundle getBuildingBundle() { return buildingBundle; }
    public <T extends BuildingDwg> T setBuildingBundle(BuildingBundle buildingBundle) { this.buildingBundle = buildingBundle; return (T) this; }

    @ManyToOne(targetEntity = FileResource.class)
    public FileResource getFileResource() { return fileResource; }
    public <T extends BuildingDwg> T setFileResource(FileResource fileResource) { this.fileResource = fileResource; return (T) this; }

    @ManyToOne(targetEntity = FileResource.class)
    public FileResource getDxfFileResource() { return dxfFileResource; }
    public <T extends BuildingDwg> T setDxfFileResource(FileResource dxfFileResource) { this.dxfFileResource = dxfFileResource; return (T) this; }

    public String getOriginalFileName() { return originalFileName; }
    public <T extends BuildingDwg> T setOriginalFileName(String originalFileName) { this.originalFileName = originalFileName; return (T) this; }

    public String getConversionStatus() { return conversionStatus; }
    public <T extends BuildingDwg> T setConversionStatus(String conversionStatus) { this.conversionStatus = conversionStatus; return (T) this; }

    public String getConverterJobId() { return converterJobId; }
    public <T extends BuildingDwg> T setConverterJobId(String converterJobId) { this.converterJobId = converterJobId; return (T) this; }

    @Lob
    public String getConversionLog() { return conversionLog; }
    public <T extends BuildingDwg> T setConversionLog(String conversionLog) { this.conversionLog = conversionLog; return (T) this; }

    @Column(columnDefinition = "timestamp with time zone")
    public OffsetDateTime getConvertedAt() { return convertedAt; }
    public <T extends BuildingDwg> T setConvertedAt(OffsetDateTime convertedAt) { this.convertedAt = convertedAt; return (T) this; }
}
