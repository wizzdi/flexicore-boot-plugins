package com.wizzdi.maps.service.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.wizzdi.flexicore.file.model.FileResource;
import com.wizzdi.flexicore.security.request.BasicCreate;
import com.wizzdi.flexicore.security.validation.IdValid;

/** Object Used to Create MapIcon */
@com.wizzdi.flexicore.security.validation.IdValid.List({
  @IdValid(
      targetField = "fileResource",
      field = "fileResourceId",
      fieldType = com.wizzdi.flexicore.file.model.FileResource.class,
      groups = {
        com.wizzdi.flexicore.security.validation.Update.class,
        com.wizzdi.flexicore.security.validation.Create.class
      })
})
public class MapIconCreate extends BasicCreate {

  private String externalId;

  @JsonIgnore private FileResource fileResource;

  private String fileResourceId;

  private String relatedType;

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
}
