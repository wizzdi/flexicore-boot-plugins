package com.flexicore.scheduling.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flexicore.scheduling.model.ScheduleAction;

public class ScheduleActionUpdate extends ScheduleActionCreate {

  private String id;
  @JsonIgnore private ScheduleAction scheduleAction;

  public String getId() {
    return id;
  }

  public <T extends ScheduleActionUpdate> T setId(String id) {
    this.id = id;
    return (T) this;
  }

  @JsonIgnore
  public ScheduleAction getScheduleAction() {
    return scheduleAction;
  }

  public <T extends ScheduleActionUpdate> T setScheduleAction(ScheduleAction scheduleAction) {
    this.scheduleAction = scheduleAction;
    return (T) this;
  }
}
