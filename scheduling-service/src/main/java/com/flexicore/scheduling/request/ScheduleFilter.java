package com.flexicore.scheduling.request;

import com.wizzdi.flexicore.security.request.BasicPropertiesFilter;
import com.wizzdi.flexicore.security.request.PaginationFilter;
import java.time.OffsetDateTime;
import java.util.Set;

public class ScheduleFilter extends PaginationFilter {

  private Set<Boolean> enabled;

  private Set<Boolean> monday;

  private Set<Boolean> saturday;

  private Set<OffsetDateTime> timeFrameEnd;

  private Set<Boolean> tuesday;

  private Set<Boolean> holiday;

  private Set<Boolean> sunday;

  private Set<Boolean> thursday;

  private Set<Boolean> friday;

  private Set<OffsetDateTime> timeFrameStart;

  private BasicPropertiesFilter basicPropertiesFilter;

  private Set<Boolean> wednesday;

  public Set<Boolean> getEnabled() {
    return this.enabled;
  }

  public <T extends ScheduleFilter> T setEnabled(Set<Boolean> enabled) {
    this.enabled = enabled;
    return (T) this;
  }

  public Set<Boolean> getMonday() {
    return this.monday;
  }

  public <T extends ScheduleFilter> T setMonday(Set<Boolean> monday) {
    this.monday = monday;
    return (T) this;
  }

  public Set<Boolean> getSaturday() {
    return this.saturday;
  }

  public <T extends ScheduleFilter> T setSaturday(Set<Boolean> saturday) {
    this.saturday = saturday;
    return (T) this;
  }

  public Set<OffsetDateTime> getTimeFrameEnd() {
    return this.timeFrameEnd;
  }

  public <T extends ScheduleFilter> T setTimeFrameEnd(Set<OffsetDateTime> timeFrameEnd) {
    this.timeFrameEnd = timeFrameEnd;
    return (T) this;
  }

  public Set<Boolean> getTuesday() {
    return this.tuesday;
  }

  public <T extends ScheduleFilter> T setTuesday(Set<Boolean> tuesday) {
    this.tuesday = tuesday;
    return (T) this;
  }

  public Set<Boolean> getHoliday() {
    return this.holiday;
  }

  public <T extends ScheduleFilter> T setHoliday(Set<Boolean> holiday) {
    this.holiday = holiday;
    return (T) this;
  }

  public Set<Boolean> getSunday() {
    return this.sunday;
  }

  public <T extends ScheduleFilter> T setSunday(Set<Boolean> sunday) {
    this.sunday = sunday;
    return (T) this;
  }

  public Set<Boolean> getThursday() {
    return this.thursday;
  }

  public <T extends ScheduleFilter> T setThursday(Set<Boolean> thursday) {
    this.thursday = thursday;
    return (T) this;
  }

  public Set<Boolean> getFriday() {
    return this.friday;
  }

  public <T extends ScheduleFilter> T setFriday(Set<Boolean> friday) {
    this.friday = friday;
    return (T) this;
  }

  public Set<OffsetDateTime> getTimeFrameStart() {
    return this.timeFrameStart;
  }

  public <T extends ScheduleFilter> T setTimeFrameStart(Set<OffsetDateTime> timeFrameStart) {
    this.timeFrameStart = timeFrameStart;
    return (T) this;
  }

  public BasicPropertiesFilter getBasicPropertiesFilter() {
    return this.basicPropertiesFilter;
  }

  public <T extends ScheduleFilter> T setBasicPropertiesFilter(
      BasicPropertiesFilter basicPropertiesFilter) {
    this.basicPropertiesFilter = basicPropertiesFilter;
    return (T) this;
  }

  public Set<Boolean> getWednesday() {
    return this.wednesday;
  }

  public <T extends ScheduleFilter> T setWednesday(Set<Boolean> wednesday) {
    this.wednesday = wednesday;
    return (T) this;
  }
}
