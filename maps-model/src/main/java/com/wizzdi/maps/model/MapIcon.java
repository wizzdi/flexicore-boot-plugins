package com.wizzdi.maps.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flexicore.model.SecuredBasic;
import com.flexicore.model.SecurityTenant;
import com.wizzdi.flexicore.file.model.FileResource;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(indexes = {
        @Index(name = "mapicon_idx",columnList = "externalId,relatedType")
})
public class MapIcon extends SecuredBasic {

  @OneToMany(targetEntity = MappedPOI.class, mappedBy = "mapIcon")
  @JsonIgnore
  private List<MappedPOI> mapIconMappedPOIs;

  @OneToMany(targetEntity = StatusHistory.class, mappedBy = "mapIcon")
  @JsonIgnore
  private List<StatusHistory> mapIconStatusHistories;

  private String relatedType;

  @ManyToOne(targetEntity = FileResource.class)
  private FileResource fileResource;

  @Column(unique = true)
  private String externalId;

  /** @return mapIconMappedPOIs */
  @OneToMany(targetEntity = MappedPOI.class, mappedBy = "mapIcon")
  @JsonIgnore
  public List<MappedPOI> getMapIconMappedPOIs() {
    return this.mapIconMappedPOIs;
  }

  /**
   * @param mapIconMappedPOIs mapIconMappedPOIs to set
   * @return MapIcon
   */
  public <T extends MapIcon> T setMapIconMappedPOIs(List<MappedPOI> mapIconMappedPOIs) {
    this.mapIconMappedPOIs = mapIconMappedPOIs;
    return (T) this;
  }

  /** @return mapIconStatusHistories */
  @OneToMany(targetEntity = StatusHistory.class, mappedBy = "mapIcon")
  @JsonIgnore
  public List<StatusHistory> getMapIconStatusHistories() {
    return this.mapIconStatusHistories;
  }

  /**
   * @param mapIconStatusHistories mapIconStatusHistories to set
   * @return MapIcon
   */
  public <T extends MapIcon> T setMapIconStatusHistories(
      List<StatusHistory> mapIconStatusHistories) {
    this.mapIconStatusHistories = mapIconStatusHistories;
    return (T) this;
  }

  /** @return relatedType */
  public String getRelatedType() {
    return this.relatedType;
  }

  /**
   * @param relatedType relatedType to set
   * @return MapIcon
   */
  public <T extends MapIcon> T setRelatedType(String relatedType) {
    this.relatedType = relatedType;
    return (T) this;
  }

  /** @return fileResource */
  @ManyToOne(targetEntity = FileResource.class)
  public FileResource getFileResource() {
    return this.fileResource;
  }

  /**
   * @param fileResource fileResource to set
   * @return MapIcon
   */
  public <T extends MapIcon> T setFileResource(FileResource fileResource) {
    this.fileResource = fileResource;
    return (T) this;
  }

  /** @return externalId */
  public String getExternalId() {
    return this.externalId;
  }

  /**
   * @param externalId externalId to set
   * @return MapIcon
   */
  public <T extends MapIcon> T setExternalId(String externalId) {
    this.externalId = externalId;
    return (T) this;
  }

  @Transient
  public SecurityTenant getRelatedTenant(){
    return getSecurity()!=null?getSecurity().getTenant():null;
  }

  @Override
  public String toString() {
    return "MapIcon{" +
            "externalId='" + externalId + '\'' +
            '}';
  }
}
