package com.wizzdi.maps.service.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.wizzdi.flexicore.file.model.FileResource;
import com.wizzdi.flexicore.security.request.BasicCreate;

/** Object Used to Create MapIcon */
public class MapIconCreate extends BasicCreate {

  private String fileResourceId;

  @JsonIgnore private FileResource fileResource;

  private String relatedType;

  private String externalId;

  /** @return fileResourceId */
  public String getFileResourceId() {
    return this.fileResourceId;
  }

  /**
   * @param fileResourceId fileResourceId to set
   * @return MapIconCreate
   */
  public <T extends MapIconCreate> T setFileResourceId(String fileResourceId) {
    this.fileResourceId = fileResourceId;
    return (T) this;
  }

  /** @return fileResource */
  @JsonIgnore
  public FileResource getFileResource() {
    return this.fileResource;
  }

  /**
   * @param fileResource fileResource to set
   * @return MapIconCreate
   */
  public <T extends MapIconCreate> T setFileResource(FileResource fileResource) {
    this.fileResource = fileResource;
    return (T) this;
  }

  /** @return relatedType */
  public String getRelatedType() {
    return this.relatedType;
  }

  /**
   * @param relatedType relatedType to set
   * @return MapIconCreate
   */
  public <T extends MapIconCreate> T setRelatedType(String relatedType) {
    this.relatedType = relatedType;
    return (T) this;
  }

  /** @return externalId */
  public String getExternalId() {
    return this.externalId;
  }

  /**
   * @param externalId externalId to set
   * @return MapIconCreate
   */
  public <T extends MapIconCreate> T setExternalId(String externalId) {
    this.externalId = externalId;
    return (T) this;
  }
}
