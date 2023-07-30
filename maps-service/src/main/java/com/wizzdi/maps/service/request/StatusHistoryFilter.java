package com.wizzdi.maps.service.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.wizzdi.flexicore.security.request.BasicPropertiesFilter;
import com.wizzdi.flexicore.security.request.PaginationFilter;
import com.wizzdi.flexicore.security.validation.IdValid;
import com.wizzdi.maps.model.MapIcon;
import com.wizzdi.maps.model.MappedPOI;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Set;

/** Object Used to List StatusHistory */
@com.wizzdi.flexicore.security.validation.IdValid.List({
  @IdValid(
      targetField = "mappedPOI",
      field = "mappedPOIIds",
      fieldType = com.wizzdi.maps.model.MappedPOI.class),
  @IdValid(
      targetField = "mapIcon",
      field = "mapIconIds",
      fieldType = com.wizzdi.maps.model.MapIcon.class)
})
public class StatusHistoryFilter extends PaginationFilter {

  private BasicPropertiesFilter basicPropertiesFilter;

  private Set<OffsetDateTime> dateAtStatus;

  private OffsetDateTime startDate;
  private OffsetDateTime endDate;

  @JsonIgnore private List<MapIcon> mapIcon;

  private Set<String> mapIconIds;

  private MappedPOIFilter mappedPOIFilter;

  @JsonIgnore private List<MappedPOI> mappedPOI;

  private Set<String> mappedPOIIds;

  /** @return basicPropertiesFilter */
  public BasicPropertiesFilter getBasicPropertiesFilter() {
    return this.basicPropertiesFilter;
  }

  /**
   * @param basicPropertiesFilter basicPropertiesFilter to set
   * @return StatusHistoryFilter
   */
  public <T extends StatusHistoryFilter> T setBasicPropertiesFilter(
      BasicPropertiesFilter basicPropertiesFilter) {
    this.basicPropertiesFilter = basicPropertiesFilter;
    return (T) this;
  }

  /** @return dateAtStatus */
  public Set<OffsetDateTime> getDateAtStatus() {
    return this.dateAtStatus;
  }

  /**
   * @param dateAtStatus dateAtStatus to set
   * @return StatusHistoryFilter
   */
  public <T extends StatusHistoryFilter> T setDateAtStatus(Set<OffsetDateTime> dateAtStatus) {
    this.dateAtStatus = dateAtStatus;
    return (T) this;
  }

  /** @return mapIcon */
  @JsonIgnore
  public List<MapIcon> getMapIcon() {
    return this.mapIcon;
  }

  /**
   * @param mapIcon mapIcon to set
   * @return StatusHistoryFilter
   */
  public <T extends StatusHistoryFilter> T setMapIcon(List<MapIcon> mapIcon) {
    this.mapIcon = mapIcon;
    return (T) this;
  }

  /** @return mapIconIds */
  public Set<String> getMapIconIds() {
    return this.mapIconIds;
  }

  /**
   * @param mapIconIds mapIconIds to set
   * @return StatusHistoryFilter
   */
  public <T extends StatusHistoryFilter> T setMapIconIds(Set<String> mapIconIds) {
    this.mapIconIds = mapIconIds;
    return (T) this;
  }

  /** @return mappedPOI */
  @JsonIgnore
  public List<MappedPOI> getMappedPOI() {
    return this.mappedPOI;
  }

  /**
   * @param mappedPOI mappedPOI to set
   * @return StatusHistoryFilter
   */
  public <T extends StatusHistoryFilter> T setMappedPOI(List<MappedPOI> mappedPOI) {
    this.mappedPOI = mappedPOI;
    return (T) this;
  }

  /** @return mappedPOIIds */
  public Set<String> getMappedPOIIds() {
    return this.mappedPOIIds;
  }

  /**
   * @param mappedPOIIds mappedPOIIds to set
   * @return StatusHistoryFilter
   */
  public <T extends StatusHistoryFilter> T setMappedPOIIds(Set<String> mappedPOIIds) {
    this.mappedPOIIds = mappedPOIIds;
    return (T) this;
  }

  public MappedPOIFilter getMappedPOIFilter() {
    return mappedPOIFilter;
  }

  public <T extends StatusHistoryFilter> T setMappedPOIFilter(MappedPOIFilter mappedPOIFilter) {
    this.mappedPOIFilter = mappedPOIFilter;
    return (T) this;
  }

  public OffsetDateTime getStartDate() {
    return startDate;
  }

  public <T extends StatusHistoryFilter> T setStartDate(OffsetDateTime startDate) {
    this.startDate = startDate;
    return (T) this;
  }

  public OffsetDateTime getEndDate() {
    return endDate;
  }

  public <T extends StatusHistoryFilter> T setEndDate(OffsetDateTime endDate) {
    this.endDate = endDate;
    return (T) this;
  }
}
