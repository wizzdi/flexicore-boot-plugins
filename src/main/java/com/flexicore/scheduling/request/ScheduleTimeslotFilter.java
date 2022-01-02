package com.flexicore.scheduling.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flexicore.scheduling.model.Schedule;
import com.flexicore.scheduling.model.TimeOfTheDayName;
import com.wizzdi.flexicore.security.request.BasicPropertiesFilter;
import com.wizzdi.flexicore.security.request.PaginationFilter;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Set;

public class ScheduleTimeslotFilter extends PaginationFilter {

  private Set<String> endTimeOffsetId;

  private Set<TimeOfTheDayName> startTimeOfTheDayName;

  private Set<OffsetDateTime> startTime;

  private Set<OffsetDateTime> lastExecution;

  private Set<Long> startMillisOffset;

  private Set<TimeOfTheDayName> endTimeOfTheDayName;

  private Set<String> scheduleIds;

  private Set<Double> timeOfTheDayNameEndLon;

  private Set<Double> timeOfTheDayNameEndLat;

  private Set<OffsetDateTime> endTime;

  private Set<Long> coolDownIntervalBeforeRepeat;

  private Set<String> startTimeOffsetId;

  private Set<Long> endMillisOffset;

  @JsonIgnore private List<Schedule> schedule;

  private BasicPropertiesFilter basicPropertiesFilter;

  private Set<Double> timeOfTheDayNameStartLat;

  private Set<Double> timeOfTheDayNameStartLon;

  public Set<String> getEndTimeOffsetId() {
    return this.endTimeOffsetId;
  }

  public <T extends ScheduleTimeslotFilter> T setEndTimeOffsetId(Set<String> endTimeOffsetId) {
    this.endTimeOffsetId = endTimeOffsetId;
    return (T) this;
  }

  public Set<TimeOfTheDayName> getStartTimeOfTheDayName() {
    return this.startTimeOfTheDayName;
  }

  public <T extends ScheduleTimeslotFilter> T setStartTimeOfTheDayName(
      Set<TimeOfTheDayName> startTimeOfTheDayName) {
    this.startTimeOfTheDayName = startTimeOfTheDayName;
    return (T) this;
  }

  public Set<OffsetDateTime> getStartTime() {
    return this.startTime;
  }

  public <T extends ScheduleTimeslotFilter> T setStartTime(Set<OffsetDateTime> startTime) {
    this.startTime = startTime;
    return (T) this;
  }

  public Set<OffsetDateTime> getLastExecution() {
    return this.lastExecution;
  }

  public <T extends ScheduleTimeslotFilter> T setLastExecution(Set<OffsetDateTime> lastExecution) {
    this.lastExecution = lastExecution;
    return (T) this;
  }

  public Set<Long> getStartMillisOffset() {
    return this.startMillisOffset;
  }

  public <T extends ScheduleTimeslotFilter> T setStartMillisOffset(Set<Long> startMillisOffset) {
    this.startMillisOffset = startMillisOffset;
    return (T) this;
  }

  public Set<TimeOfTheDayName> getEndTimeOfTheDayName() {
    return this.endTimeOfTheDayName;
  }

  public <T extends ScheduleTimeslotFilter> T setEndTimeOfTheDayName(
      Set<TimeOfTheDayName> endTimeOfTheDayName) {
    this.endTimeOfTheDayName = endTimeOfTheDayName;
    return (T) this;
  }

  public Set<String> getScheduleIds() {
    return this.scheduleIds;
  }

  public <T extends ScheduleTimeslotFilter> T setScheduleIds(Set<String> scheduleIds) {
    this.scheduleIds = scheduleIds;
    return (T) this;
  }

  public Set<Double> getTimeOfTheDayNameEndLon() {
    return this.timeOfTheDayNameEndLon;
  }

  public <T extends ScheduleTimeslotFilter> T setTimeOfTheDayNameEndLon(
      Set<Double> timeOfTheDayNameEndLon) {
    this.timeOfTheDayNameEndLon = timeOfTheDayNameEndLon;
    return (T) this;
  }

  public Set<Double> getTimeOfTheDayNameEndLat() {
    return this.timeOfTheDayNameEndLat;
  }

  public <T extends ScheduleTimeslotFilter> T setTimeOfTheDayNameEndLat(
      Set<Double> timeOfTheDayNameEndLat) {
    this.timeOfTheDayNameEndLat = timeOfTheDayNameEndLat;
    return (T) this;
  }

  public Set<OffsetDateTime> getEndTime() {
    return this.endTime;
  }

  public <T extends ScheduleTimeslotFilter> T setEndTime(Set<OffsetDateTime> endTime) {
    this.endTime = endTime;
    return (T) this;
  }

  public Set<Long> getCoolDownIntervalBeforeRepeat() {
    return this.coolDownIntervalBeforeRepeat;
  }

  public <T extends ScheduleTimeslotFilter> T setCoolDownIntervalBeforeRepeat(
      Set<Long> coolDownIntervalBeforeRepeat) {
    this.coolDownIntervalBeforeRepeat = coolDownIntervalBeforeRepeat;
    return (T) this;
  }

  public Set<String> getStartTimeOffsetId() {
    return this.startTimeOffsetId;
  }

  public <T extends ScheduleTimeslotFilter> T setStartTimeOffsetId(Set<String> startTimeOffsetId) {
    this.startTimeOffsetId = startTimeOffsetId;
    return (T) this;
  }

  public Set<Long> getEndMillisOffset() {
    return this.endMillisOffset;
  }

  public <T extends ScheduleTimeslotFilter> T setEndMillisOffset(Set<Long> endMillisOffset) {
    this.endMillisOffset = endMillisOffset;
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

  public Set<Double> getTimeOfTheDayNameStartLat() {
    return this.timeOfTheDayNameStartLat;
  }

  public <T extends ScheduleTimeslotFilter> T setTimeOfTheDayNameStartLat(
      Set<Double> timeOfTheDayNameStartLat) {
    this.timeOfTheDayNameStartLat = timeOfTheDayNameStartLat;
    return (T) this;
  }

  public Set<Double> getTimeOfTheDayNameStartLon() {
    return this.timeOfTheDayNameStartLon;
  }

  public <T extends ScheduleTimeslotFilter> T setTimeOfTheDayNameStartLon(
      Set<Double> timeOfTheDayNameStartLon) {
    this.timeOfTheDayNameStartLon = timeOfTheDayNameStartLon;
    return (T) this;
  }
}
