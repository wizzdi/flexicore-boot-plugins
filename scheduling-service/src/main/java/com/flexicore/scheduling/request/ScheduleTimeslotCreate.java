package com.flexicore.scheduling.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flexicore.scheduling.model.Schedule;
import com.flexicore.scheduling.model.TimeOfTheDayName;
import com.wizzdi.flexicore.security.request.BasicCreate;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.Lob;

import java.time.OffsetDateTime;

public class ScheduleTimeslotCreate extends BasicCreate {

    private String log;
  private TimeOfTheDayName startTimeOfTheDayName;

  private OffsetDateTime startTime;

  @JsonIgnore
  private OffsetDateTime lastExecution;

  private Long startMillisOffset;

  private TimeOfTheDayName endTimeOfTheDayName;

  private String scheduleId;

  private Double timeOfTheDayNameEndLon;

  private Double timeOfTheDayNameEndLat;

  private OffsetDateTime endTime;

  private Long coolDownIntervalBeforeRepeat;


  private Long endMillisOffset;

  @JsonIgnore private Schedule schedule;

  private Double timeOfTheDayNameStartLat;

  private Double timeOfTheDayNameStartLon;

  public TimeOfTheDayName getStartTimeOfTheDayName() {
    return this.startTimeOfTheDayName;
  }

  public <T extends ScheduleTimeslotCreate> T setStartTimeOfTheDayName(
      TimeOfTheDayName startTimeOfTheDayName) {
    this.startTimeOfTheDayName = startTimeOfTheDayName;
    return (T) this;
  }

  public OffsetDateTime getStartTime() {
    return this.startTime;
  }

  public <T extends ScheduleTimeslotCreate> T setStartTime(OffsetDateTime startTime) {
    this.startTime = startTime;
    return (T) this;
  }

  @JsonIgnore
  public OffsetDateTime getLastExecution() {
    return this.lastExecution;
  }

  public <T extends ScheduleTimeslotCreate> T setLastExecution(OffsetDateTime lastExecution) {
    this.lastExecution = lastExecution;
    return (T) this;
  }

  public Long getStartMillisOffset() {
    return this.startMillisOffset;
  }

  public <T extends ScheduleTimeslotCreate> T setStartMillisOffset(Long startMillisOffset) {
    this.startMillisOffset = startMillisOffset;
    return (T) this;
  }

  public TimeOfTheDayName getEndTimeOfTheDayName() {
    return this.endTimeOfTheDayName;
  }

  public <T extends ScheduleTimeslotCreate> T setEndTimeOfTheDayName(
      TimeOfTheDayName endTimeOfTheDayName) {
    this.endTimeOfTheDayName = endTimeOfTheDayName;
    return (T) this;
  }

  public String getScheduleId() {
    return this.scheduleId;
  }

  public <T extends ScheduleTimeslotCreate> T setScheduleId(String scheduleId) {
    this.scheduleId = scheduleId;
    return (T) this;
  }

  public Double getTimeOfTheDayNameEndLon() {
    return this.timeOfTheDayNameEndLon;
  }

  public <T extends ScheduleTimeslotCreate> T setTimeOfTheDayNameEndLon(
      Double timeOfTheDayNameEndLon) {
    this.timeOfTheDayNameEndLon = timeOfTheDayNameEndLon;
    return (T) this;
  }

  public Double getTimeOfTheDayNameEndLat() {
    return this.timeOfTheDayNameEndLat;
  }

  public <T extends ScheduleTimeslotCreate> T setTimeOfTheDayNameEndLat(
      Double timeOfTheDayNameEndLat) {
    this.timeOfTheDayNameEndLat = timeOfTheDayNameEndLat;
    return (T) this;
  }

  public OffsetDateTime getEndTime() {
    return this.endTime;
  }

  public <T extends ScheduleTimeslotCreate> T setEndTime(OffsetDateTime endTime) {
    this.endTime = endTime;
    return (T) this;
  }

  public Long getCoolDownIntervalBeforeRepeat() {
    return this.coolDownIntervalBeforeRepeat;
  }

  public <T extends ScheduleTimeslotCreate> T setCoolDownIntervalBeforeRepeat(
      Long coolDownIntervalBeforeRepeat) {
    this.coolDownIntervalBeforeRepeat = coolDownIntervalBeforeRepeat;
    return (T) this;
  }


  public Long getEndMillisOffset() {
    return this.endMillisOffset;
  }

  public <T extends ScheduleTimeslotCreate> T setEndMillisOffset(Long endMillisOffset) {
    this.endMillisOffset = endMillisOffset;
    return (T) this;
  }

  @JsonIgnore
  public Schedule getSchedule() {
    return this.schedule;
  }

  public <T extends ScheduleTimeslotCreate> T setSchedule(Schedule schedule) {
    this.schedule = schedule;
    return (T) this;
  }

  public Double getTimeOfTheDayNameStartLat() {
    return this.timeOfTheDayNameStartLat;
  }

  public <T extends ScheduleTimeslotCreate> T setTimeOfTheDayNameStartLat(
      Double timeOfTheDayNameStartLat) {
    this.timeOfTheDayNameStartLat = timeOfTheDayNameStartLat;
    return (T) this;
  }

  public Double getTimeOfTheDayNameStartLon() {
    return this.timeOfTheDayNameStartLon;
  }

  public <T extends ScheduleTimeslotCreate> T setTimeOfTheDayNameStartLon(
      Double timeOfTheDayNameStartLon) {
    this.timeOfTheDayNameStartLon = timeOfTheDayNameStartLon;
    return (T) this;
  }

  public String getLog() {
    return log;
  }

  public <T extends ScheduleTimeslotCreate> T setLog(String log) {
    this.log = log;
    return (T) this;
  }
}
