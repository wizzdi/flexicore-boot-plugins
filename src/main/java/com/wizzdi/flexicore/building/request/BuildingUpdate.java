package com.wizzdi.flexicore.building.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.wizzdi.flexicore.building.model.Building;

public class BuildingUpdate extends BuildingCreate {

  private String id;
  @JsonIgnore private Building building;

  public String getId() {
    return id;
  }

  public <T extends BuildingUpdate> T setId(String id) {
    this.id = id;
    return (T) this;
  }

  @JsonIgnore
  public Building getBuilding() {
    return building;
  }

  public <T extends BuildingUpdate> T setBuilding(Building building) {
    this.building = building;
    return (T) this;
  }
}
