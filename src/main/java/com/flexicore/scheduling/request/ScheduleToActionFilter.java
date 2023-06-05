package com.flexicore.scheduling.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flexicore.scheduling.model.Schedule;
import com.flexicore.scheduling.model.ScheduleAction;
import com.wizzdi.flexicore.security.request.BasicPropertiesFilter;
import com.wizzdi.flexicore.security.request.PaginationFilter;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ScheduleToActionFilter extends PaginationFilter {

  @JsonIgnore private List<Schedule> schedule;
  private Set<String> scheduleIds=new HashSet<>();

  @JsonIgnore private List<ScheduleAction> scheduleAction;
  private Set<String> scheduleActionIds=new HashSet<>();

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

  public Set<String> getScheduleIds() {
    return scheduleIds;
  }

  public <T extends ScheduleToActionFilter> T setScheduleIds(Set<String> scheduleIds) {
    this.scheduleIds = scheduleIds;
    return (T) this;
  }

  public Set<String> getScheduleActionIds() {
    return scheduleActionIds;
  }

  public <T extends ScheduleToActionFilter> T setScheduleActionIds(Set<String> scheduleActionIds) {
    this.scheduleActionIds = scheduleActionIds;
    return (T) this;
  }
}
