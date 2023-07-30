package com.flexicore.scheduling.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flexicore.scheduling.model.Schedule;

public class ScheduleUpdate extends ScheduleCreate {

  private String id;
  @JsonIgnore private Schedule schedule;

  public String getId() {
    return id;
  }

  public <T extends ScheduleUpdate> T setId(String id) {
    this.id = id;
    return (T) this;
  }

  @JsonIgnore
  public Schedule getSchedule() {
    return schedule;
  }

  public <T extends ScheduleUpdate> T setSchedule(Schedule schedule) {
    this.schedule = schedule;
    return (T) this;
  }
}
