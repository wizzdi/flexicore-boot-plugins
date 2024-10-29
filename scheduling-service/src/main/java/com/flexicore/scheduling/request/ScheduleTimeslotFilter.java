package com.flexicore.scheduling.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flexicore.scheduling.model.Schedule;
import com.wizzdi.flexicore.security.request.BasicPropertiesFilter;
import com.wizzdi.flexicore.security.request.PaginationFilter;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Set;

public class ScheduleTimeslotFilter extends PaginationFilter {



  private Set<OffsetDateTime> lastExecution;


  private Set<String> scheduleIds;


  @JsonIgnore private List<Schedule> schedule;

  private BasicPropertiesFilter basicPropertiesFilter;



  public Set<OffsetDateTime> getLastExecution() {
    return this.lastExecution;
  }

  public <T extends ScheduleTimeslotFilter> T setLastExecution(Set<OffsetDateTime> lastExecution) {
    this.lastExecution = lastExecution;
    return (T) this;
  }
  public Set<String> getScheduleIds() {
    return this.scheduleIds;
  }

  public <T extends ScheduleTimeslotFilter> T setScheduleIds(Set<String> scheduleIds) {
    this.scheduleIds = scheduleIds;
    return (T) this;
  }

  @JsonIgnore
  public List<Schedule> getSchedule() {
    return this.schedule;
  }

  public <T extends ScheduleTimeslotFilter> T setSchedule(List<Schedule> schedule) {
    this.schedule = schedule;
    return (T) this;
  }

  public BasicPropertiesFilter getBasicPropertiesFilter() {
    return this.basicPropertiesFilter;
  }

  public <T extends ScheduleTimeslotFilter> T setBasicPropertiesFilter(
      BasicPropertiesFilter basicPropertiesFilter) {
    this.basicPropertiesFilter = basicPropertiesFilter;
    return (T) this;
  }

}
