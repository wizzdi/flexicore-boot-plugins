package com.wizzdi.maps.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flexicore.model.SecuredBasic;
import java.util.List;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;

@Entity
public class MapGroup extends SecuredBasic {

  private String externalId;

  @OneToMany(targetEntity = MapGroupToMappedPOI.class, mappedBy = "mapGroup")
  @JsonIgnore
  private List<MapGroupToMappedPOI> mapGroupMapGroupToMappedPOIs;

  /** @return externalId */
  public String getExternalId() {
    return this.externalId;
  }

  /**
   * @param externalId externalId to set
   * @return MapGroup
   */
  public <T extends MapGroup> T setExternalId(String externalId) {
    this.externalId = externalId;
    return (T) this;
  }

  /** @return mapGroupMapGroupToMappedPOIs */
  @OneToMany(targetEntity = MapGroupToMappedPOI.class, mappedBy = "mapGroup")
  @JsonIgnore
  public List<MapGroupToMappedPOI> getMapGroupMapGroupToMappedPOIs() {
    return this.mapGroupMapGroupToMappedPOIs;
  }

  /**
   * @param mapGroupMapGroupToMappedPOIs mapGroupMapGroupToMappedPOIs to set
   * @return MapGroup
   */
  public <T extends MapGroup> T setMapGroupMapGroupToMappedPOIs(
      List<MapGroupToMappedPOI> mapGroupMapGroupToMappedPOIs) {
    this.mapGroupMapGroupToMappedPOIs = mapGroupMapGroupToMappedPOIs;
    return (T) this;
  }
}
