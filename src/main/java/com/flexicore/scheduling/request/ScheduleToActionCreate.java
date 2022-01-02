package com.flexicore.scheduling.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flexicore.scheduling.model.Schedule;
import com.flexicore.scheduling.model.ScheduleAction;
import com.wizzdi.flexicore.security.request.BasicCreate;

public class ScheduleToActionCreate extends BasicCreate {

  @JsonIgnore private Schedule schedule;

  @JsonIgnore private ScheduleAction scheduleAction;

  @JsonIgnore
  public Schedule getSchedule() {
    return this.schedule;
  }

  public <T extends ScheduleToActionCreate> T setSchedule(Schedule schedule) {
    this.schedule = schedule;
    return (T) this;
  }

  @JsonIgnore
  public ScheduleAction getScheduleAction() {
    return this.scheduleAction;
  }

  public <T extends ScheduleToActionCreate> T setScheduleAction(ScheduleAction scheduleAction) {
    this.scheduleAction = scheduleAction;
    return (T) this;
  }
}
