package com.flexicore.scheduling.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flexicore.scheduling.model.Schedule;
import com.flexicore.scheduling.model.ScheduleAction;
import com.wizzdi.flexicore.security.request.BasicCreate;

import java.time.OffsetDateTime;

public class ScheduleToActionCreate extends BasicCreate {

  @JsonIgnore private Schedule schedule;
  private String scheduleId;
  private OffsetDateTime lastExecution;

  @JsonIgnore private ScheduleAction scheduleAction;
  private String scheduleActionId;

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

  public String getScheduleId() {
    return scheduleId;
  }

  public <T extends ScheduleToActionCreate> T setScheduleId(String scheduleId) {
    this.scheduleId = scheduleId;
    return (T) this;
  }

  public String getScheduleActionId() {
    return scheduleActionId;
  }

  public <T extends ScheduleToActionCreate> T setScheduleActionId(String scheduleActionId) {
    this.scheduleActionId = scheduleActionId;
    return (T) this;
  }

  public OffsetDateTime getLastExecution() {
    return lastExecution;
  }

  public <T extends ScheduleToActionCreate> T setLastExecution(OffsetDateTime lastExecution) {
    this.lastExecution = lastExecution;
    return (T) this;
  }
}
