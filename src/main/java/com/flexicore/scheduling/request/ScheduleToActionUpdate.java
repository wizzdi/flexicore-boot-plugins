package com.flexicore.scheduling.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flexicore.scheduling.model.ScheduleToAction;

public class ScheduleToActionUpdate extends ScheduleToActionCreate {

  private String id;
  @JsonIgnore private ScheduleToAction scheduleToAction;

  public String getId() {
    return id;
  }

  public <T extends ScheduleToActionUpdate> T setId(String id) {
    this.id = id;
    return (T) this;
  }

  @JsonIgnore
  public ScheduleToAction getScheduleToAction() {
    return scheduleToAction;
  }

  public <T extends ScheduleToActionUpdate> T setScheduleToAction(
      ScheduleToAction scheduleToAction) {
    this.scheduleToAction = scheduleToAction;
    return (T) this;
  }
}
