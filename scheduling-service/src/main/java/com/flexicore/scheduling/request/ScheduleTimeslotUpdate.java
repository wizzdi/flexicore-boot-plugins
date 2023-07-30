package com.flexicore.scheduling.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flexicore.scheduling.model.ScheduleTimeslot;

public class ScheduleTimeslotUpdate extends ScheduleTimeslotCreate {

  private String id;
  @JsonIgnore private ScheduleTimeslot scheduleTimeslot;

  public String getId() {
    return id;
  }

  public <T extends ScheduleTimeslotUpdate> T setId(String id) {
    this.id = id;
    return (T) this;
  }

  @JsonIgnore
  public ScheduleTimeslot getScheduleTimeslot() {
    return scheduleTimeslot;
  }

  public <T extends ScheduleTimeslotUpdate> T setScheduleTimeslot(
      ScheduleTimeslot scheduleTimeslot) {
    this.scheduleTimeslot = scheduleTimeslot;
    return (T) this;
  }
}
