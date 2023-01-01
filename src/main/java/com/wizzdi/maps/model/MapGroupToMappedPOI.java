package com.wizzdi.maps.model;

import com.flexicore.model.SecuredBasic;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(indexes = {
        @Index(name = "mapgroup_to_mappedpoi_idx",columnList = "mappedPOI_id,mapGroup_id")
})
public class MapGroupToMappedPOI extends SecuredBasic {

  @ManyToOne(targetEntity = MappedPOI.class)
  private MappedPOI mappedPOI;

  @ManyToOne(targetEntity = MapGroup.class)
  private MapGroup mapGroup;

  /** @return mappedPOI */
  @ManyToOne(targetEntity = MappedPOI.class)
  public MappedPOI getMappedPOI() {
    return this.mappedPOI;
  }

  /**
   * @param mappedPOI mappedPOI to set
   * @return MapGroupToMappedPOI
   */
  public <T extends MapGroupToMappedPOI> T setMappedPOI(MappedPOI mappedPOI) {
    this.mappedPOI = mappedPOI;
    return (T) this;
  }

  /** @return mapGroup */
  @ManyToOne(targetEntity = MapGroup.class)
  public MapGroup getMapGroup() {
    return this.mapGroup;
  }

  /**
   * @param mapGroup mapGroup to set
   * @return MapGroupToMappedPOI
   */
  public <T extends MapGroupToMappedPOI> T setMapGroup(MapGroup mapGroup) {
    this.mapGroup = mapGroup;
    return (T) this;
  }
}
