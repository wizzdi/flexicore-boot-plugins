package com.flexicore.scheduling.model;

import com.flexicore.model.Baseclass;

import java.time.LocalTime;
import java.time.OffsetDateTime;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;

@Entity
public class ScheduleTimeslot extends Baseclass {


  private TimeOfTheDayName startTimeOfTheDayName;

  private LocalTime startTime;
  @Column(columnDefinition = "timestamp with time zone")

  private OffsetDateTime lastExecution;
 @Lob
 private String log;

  private Long startMillisOffset;

  private TimeOfTheDayName endTimeOfTheDayName;

  private Double timeOfTheDayNameEndLon;

  private Double timeOfTheDayNameEndLat;

  private LocalTime endTime;

  private Long coolDownIntervalBeforeRepeat;


  private Long endMillisOffset;

  @ManyToOne(targetEntity = Schedule.class)
  private Schedule schedule;

  private Double timeOfTheDayNameStartLat;

  private Double timeOfTheDayNameStartLon;


  /** @return startTimeOfTheDayName */
  public TimeOfTheDayName getStartTimeOfTheDayName() {
    return this.startTimeOfTheDayName;
  }

  /**
   * @param startTimeOfTheDayName startTimeOfTheDayName to set
   * @return ScheduleTimeslot
   */
  public <T extends ScheduleTimeslot> T setStartTimeOfTheDayName(
      TimeOfTheDayName startTimeOfTheDayName) {
    this.startTimeOfTheDayName = startTimeOfTheDayName;
    return (T) this;
  }

  /** @return startTime */
  public LocalTime getStartTime() {
    return this.startTime;
  }

  /**
   * @param startTime startTime to set
   * @return ScheduleTimeslot
   */
  public <T extends ScheduleTimeslot> T setStartTime(LocalTime startTime) {
    this.startTime = startTime;
    return (T) this;
  }

  /** @return lastExecution */
  public OffsetDateTime getLastExecution() {
    return this.lastExecution;
  }

  /**
   * @param lastExecution lastExecution to set
   * @return ScheduleTimeslot
   */
  public <T extends ScheduleTimeslot> T setLastExecution(OffsetDateTime lastExecution) {
    this.lastExecution = lastExecution;
    return (T) this;
  }

  /** @return startMillisOffset */
  public Long getStartMillisOffset() {
    return this.startMillisOffset;
  }

  /**
   * @param startMillisOffset startMillisOffset to set
   * @return ScheduleTimeslot
   */
  public <T extends ScheduleTimeslot> T setStartMillisOffset(Long startMillisOffset) {
    this.startMillisOffset = startMillisOffset;
    return (T) this;
  }

  /** @return endTimeOfTheDayName */
  public TimeOfTheDayName getEndTimeOfTheDayName() {
    return this.endTimeOfTheDayName;
  }

  /**
   * @param endTimeOfTheDayName endTimeOfTheDayName to set
   * @return ScheduleTimeslot
   */
  public <T extends ScheduleTimeslot> T setEndTimeOfTheDayName(
      TimeOfTheDayName endTimeOfTheDayName) {
    this.endTimeOfTheDayName = endTimeOfTheDayName;
    return (T) this;
  }

  /** @return timeOfTheDayNameEndLon */
  public Double getTimeOfTheDayNameEndLon() {
    return this.timeOfTheDayNameEndLon;
  }

  /**
   * @param timeOfTheDayNameEndLon timeOfTheDayNameEndLon to set
   * @return ScheduleTimeslot
   */
  public <T extends ScheduleTimeslot> T setTimeOfTheDayNameEndLon(Double timeOfTheDayNameEndLon) {
    this.timeOfTheDayNameEndLon = timeOfTheDayNameEndLon;
    return (T) this;
  }

  /** @return timeOfTheDayNameEndLat */
  public Double getTimeOfTheDayNameEndLat() {
    return this.timeOfTheDayNameEndLat;
  }

  /**
   * @param timeOfTheDayNameEndLat timeOfTheDayNameEndLat to set
   * @return ScheduleTimeslot
   */
  public <T extends ScheduleTimeslot> T setTimeOfTheDayNameEndLat(Double timeOfTheDayNameEndLat) {
    this.timeOfTheDayNameEndLat = timeOfTheDayNameEndLat;
    return (T) this;
  }

  /** @return endTime */
  public LocalTime getEndTime() {
    return this.endTime;
  }

  /**
   * @param endTime endTime to set
   * @return ScheduleTimeslot
   */
  public <T extends ScheduleTimeslot> T setEndTime(LocalTime endTime) {
    this.endTime = endTime;
    return (T) this;
  }

  /** @return coolDownIntervalBeforeRepeat */
  public Long getCoolDownIntervalBeforeRepeat() {
    return this.coolDownIntervalBeforeRepeat;
  }

  /**
   * @param coolDownIntervalBeforeRepeat coolDownIntervalBeforeRepeat to set
   * @return ScheduleTimeslot
   */
  public <T extends ScheduleTimeslot> T setCoolDownIntervalBeforeRepeat(
      Long coolDownIntervalBeforeRepeat) {
    this.coolDownIntervalBeforeRepeat = coolDownIntervalBeforeRepeat;
    return (T) this;
  }


  /** @return endMillisOffset */
  public Long getEndMillisOffset() {
    return this.endMillisOffset;
  }

  /**
   * @param endMillisOffset endMillisOffset to set
   * @return ScheduleTimeslot
   */
  public <T extends ScheduleTimeslot> T setEndMillisOffset(Long endMillisOffset) {
    this.endMillisOffset = endMillisOffset;
    return (T) this;
  }

  /** @return schedule */
  @ManyToOne(targetEntity = Schedule.class)
  public Schedule getSchedule() {
    return this.schedule;
  }

  /**
   * @param schedule schedule to set
   * @return ScheduleTimeslot
   */
  public <T extends ScheduleTimeslot> T setSchedule(Schedule schedule) {
    this.schedule = schedule;
    return (T) this;
  }

  /** @return timeOfTheDayNameStartLat */
  public Double getTimeOfTheDayNameStartLat() {
    return this.timeOfTheDayNameStartLat;
  }

  /**
   * @param timeOfTheDayNameStartLat timeOfTheDayNameStartLat to set
   * @return ScheduleTimesloto
   */
  public <T extends ScheduleTimeslot> T setTimeOfTheDayNameStartLat(
      Double timeOfTheDayNameStartLat) {
    this.timeOfTheDayNameStartLat = timeOfTheDayNameStartLat;
    return (T) this;
  }

  /** @return timeOfTheDayNameStartLon */
  public Double getTimeOfTheDayNameStartLon() {
    return this.timeOfTheDayNameStartLon;
  }

  /**
   * @param timeOfTheDayNameStartLon timeOfTheDayNameStartLon to set
   * @return ScheduleTimeslot
   */
  public <T extends ScheduleTimeslot> T setTimeOfTheDayNameStartLon(
      Double timeOfTheDayNameStartLon) {
    this.timeOfTheDayNameStartLon = timeOfTheDayNameStartLon;
    return (T) this;
  }

  public String getLog() {
    return log;
  }

  public <T extends ScheduleTimeslot> T setLog(String log) {
    this.log = log;
    return (T) this;
  }
}
