package com.wizzdi.maps.service.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.wizzdi.flexicore.file.model.FileResource;
import com.wizzdi.flexicore.security.request.BasicPropertiesFilter;
import com.wizzdi.flexicore.security.request.PaginationFilter;
import com.wizzdi.flexicore.security.validation.IdValid;
import com.wizzdi.maps.model.MappedPOI;
import com.wizzdi.maps.model.StatusHistory;
import java.util.List;
import java.util.Set;

/** Object Used to List MapIcon */
@com.wizzdi.flexicore.security.validation.IdValid.List({
  @IdValid(
      targetField = "fileResource",
      field = "fileResourceIds",
      fieldType = com.wizzdi.flexicore.file.model.FileResource.class),
  @IdValid(
      targetField = "mapIconMappedPOIs",
      field = "mapIconMappedPOIsIds",
      fieldType = com.wizzdi.maps.model.MappedPOI.class),
  @IdValid(
      targetField = "mapIconStatusHistories",
      field = "mapIconStatusHistoriesIds",
      fieldType = com.wizzdi.maps.model.StatusHistory.class)
})
public class MapIconFilter extends PaginationFilter {

  private BasicPropertiesFilter basicPropertiesFilter;

  private Set<String> externalId;
  private boolean externalIdExclude;

  @JsonIgnore private List<FileResource> fileResource;

  private Set<String> fileResourceIds;

  @JsonIgnore private List<MappedPOI> mapIconMappedPOIs;

  private Set<String> mapIconMappedPOIsIds;

  @JsonIgnore private List<StatusHistory> mapIconStatusHistories;

  private Set<String> mapIconStatusHistoriesIds;

  private Set<String> relatedType;

  /** @return basicPropertiesFilter */
  public BasicPropertiesFilter getBasicPropertiesFilter() {
    return this.basicPropertiesFilter;
  }

  /**
   * @param basicPropertiesFilter basicPropertiesFilter to set
   * @return MapIconFilter
   */
  public <T extends MapIconFilter> T setBasicPropertiesFilter(
      BasicPropertiesFilter basicPropertiesFilter) {
    this.basicPropertiesFilter = basicPropertiesFilter;
    return (T) this;
  }

  /** @return externalId */
  public Set<String> getExternalId() {
    return this.externalId;
  }

  /**
   * @param externalId externalId to set
   * @return MapIconFilter
   */
  public <T extends MapIconFilter> T setExternalId(Set<String> externalId) {
    this.externalId = externalId;
    return (T) this;
  }

  /** @return fileResource */
  @JsonIgnore
  public List<FileResource> getFileResource() {
    return this.fileResource;
  }

  /**
   * @param fileResource fileResource to set
   * @return MapIconFilter
   */
  public <T extends MapIconFilter> T setFileResource(List<FileResource> fileResource) {
    this.fileResource = fileResource;
    return (T) this;
  }

  /** @return fileResourceIds */
  public Set<String> getFileResourceIds() {
    return this.fileResourceIds;
  }

  /**
   * @param fileResourceIds fileResourceIds to set
   * @return MapIconFilter
   */
  public <T extends MapIconFilter> T setFileResourceIds(Set<String> fileResourceIds) {
    this.fileResourceIds = fileResourceIds;
    return (T) this;
  }

  /** @return mapIconMappedPOIs */
  @JsonIgnore
  public List<MappedPOI> getMapIconMappedPOIs() {
    return this.mapIconMappedPOIs;
  }

  /**
   * @param mapIconMappedPOIs mapIconMappedPOIs to set
   * @return MapIconFilter
   */
  public <T extends MapIconFilter> T setMapIconMappedPOIs(List<MappedPOI> mapIconMappedPOIs) {
    this.mapIconMappedPOIs = mapIconMappedPOIs;
    return (T) this;
  }

  /** @return mapIconMappedPOIsIds */
  public Set<String> getMapIconMappedPOIsIds() {
    return this.mapIconMappedPOIsIds;
  }

  /**
   * @param mapIconMappedPOIsIds mapIconMappedPOIsIds to set
   * @return MapIconFilter
   */
  public <T extends MapIconFilter> T setMapIconMappedPOIsIds(Set<String> mapIconMappedPOIsIds) {
    this.mapIconMappedPOIsIds = mapIconMappedPOIsIds;
    return (T) this;
  }

  /** @return mapIconStatusHistories */
  @JsonIgnore
  public List<StatusHistory> getMapIconStatusHistories() {
    return this.mapIconStatusHistories;
  }

  /**
   * @param mapIconStatusHistories mapIconStatusHistories to set
   * @return MapIconFilter
   */
  public <T extends MapIconFilter> T setMapIconStatusHistories(
      List<StatusHistory> mapIconStatusHistories) {
    this.mapIconStatusHistories = mapIconStatusHistories;
    return (T) this;
  }

  /** @return mapIconStatusHistoriesIds */
  public Set<String> getMapIconStatusHistoriesIds() {
    return this.mapIconStatusHistoriesIds;
  }

  /**
   * @param mapIconStatusHistoriesIds mapIconStatusHistoriesIds to set
   * @return MapIconFilter
   */
  public <T extends MapIconFilter> T setMapIconStatusHistoriesIds(
      Set<String> mapIconStatusHistoriesIds) {
    this.mapIconStatusHistoriesIds = mapIconStatusHistoriesIds;
    return (T) this;
  }

  /** @return relatedType */
  public Set<String> getRelatedType() {
    return this.relatedType;
  }

  /**
   * @param relatedType relatedType to set
   * @return MapIconFilter
   */
  public <T extends MapIconFilter> T setRelatedType(Set<String> relatedType) {
    this.relatedType = relatedType;
    return (T) this;
  }

  public boolean isExternalIdExclude() {
    return externalIdExclude;
  }

  public <T extends MapIconFilter> T setExternalIdExclude(boolean externalIdExclude) {
    this.externalIdExclude = externalIdExclude;
    return (T) this;
  }
}
