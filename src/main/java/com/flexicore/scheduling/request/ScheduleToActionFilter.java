package com.flexicore.scheduling.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flexicore.scheduling.model.Schedule;
import com.flexicore.scheduling.model.ScheduleAction;
import com.wizzdi.flexicore.security.request.BasicPropertiesFilter;
import com.wizzdi.flexicore.security.request.PaginationFilter;
import java.util.List;

public class ScheduleToActionFilter extends PaginationFilter {

  @JsonIgnore private List<Schedule> schedule;

  @JsonIgnore private List<ScheduleAction> scheduleAction;

  private BasicPropertiesFilter basicPropertiesFilter;

  @JsonIgnore
  public List<Schedule> getSchedule() {
    return this.schedule;
  }

  public <T extends ScheduleToActionFilter> T setSchedule(List<Schedule> schedule) {
    this.schedule = schedule;
    return (T) this;
  }

  @JsonIgnore
  public List<ScheduleAction> getScheduleAction() {
    return this.scheduleAction;
  }

  public <T extends ScheduleToActionFilter> T setScheduleAction(
      List<ScheduleAction> scheduleAction) {
    this.scheduleAction = scheduleAction;
    return (T) this;
  }

  public BasicPropertiesFilter getBasicPropertiesFilter() {
    return this.basicPropertiesFilter;
  }

  public <T extends ScheduleToActionFilter> T setBasicPropertiesFilter(
      BasicPropertiesFilter basicPropertiesFilter) {
    this.basicPropertiesFilter = basicPropertiesFilter;
    return (T) this;
  }
}
