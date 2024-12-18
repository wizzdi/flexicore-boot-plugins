package com.wizzdi.maps.model;

import com.flexicore.model.Baseclass;
import java.time.OffsetDateTime;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(indexes = {
        @Index(name = "status_history_idx",columnList = "mappedPOI_id,mapIcon_id,dateAtStatus")
})
public class StatusHistory extends Baseclass {

  @ManyToOne(targetEntity = MappedPOI.class)
  private MappedPOI mappedPOI;

  @ManyToOne(targetEntity = MapIcon.class)
  private MapIcon mapIcon;

  private OffsetDateTime dateAtStatus;

  /** @return mappedPOI */
  @ManyToOne(targetEntity = MappedPOI.class)
  public MappedPOI getMappedPOI() {
    return this.mappedPOI;
  }

  /**
   * @param mappedPOI mappedPOI to set
   * @return StatusHistory
   */
  public <T extends StatusHistory> T setMappedPOI(MappedPOI mappedPOI) {
    this.mappedPOI = mappedPOI;
    return (T) this;
  }

  /** @return mapIcon */
  @ManyToOne(targetEntity = MapIcon.class)
  public MapIcon getMapIcon() {
    return this.mapIcon;
  }

  /**
   * @param mapIcon mapIcon to set
   * @return StatusHistory
   */
  public <T extends StatusHistory> T setMapIcon(MapIcon mapIcon) {
    this.mapIcon = mapIcon;
    return (T) this;
  }

  /** @return dateAtStatus */
  public OffsetDateTime getDateAtStatus() {
    return this.dateAtStatus;
  }

  /**
   * @param dateAtStatus dateAtStatus to set
   * @return StatusHistory
   */
  public <T extends StatusHistory> T setDateAtStatus(OffsetDateTime dateAtStatus) {
    this.dateAtStatus = dateAtStatus;
    return (T) this;
  }
}
