package com.flexicore.scheduling.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flexicore.model.SecuredBasic;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;

@Entity
public class Schedule extends SecuredBasic {

  @JsonIgnore
  @OneToMany(targetEntity = ScheduleToAction.class, mappedBy = "schedule")
  private List<ScheduleToAction> scheduleToActions = new ArrayList<>();

  @JsonIgnore
  @OneToMany(targetEntity = ScheduleTimeslot.class, mappedBy = "schedule")
  private List<ScheduleTimeslot> timeslots = new ArrayList<>();
  private boolean enabled;
  private boolean monday;
  private boolean saturday;
  @Column(columnDefinition = "timestamp with time zone")
  private OffsetDateTime timeFrameEnd;
  private boolean tuesday;
  private boolean holiday;
  private boolean sunday;
  private boolean thursday;
  private boolean friday;
  @Column(columnDefinition = "timestamp with time zone")
  private OffsetDateTime timeFrameStart;
  private boolean wednesday;
  private String startTimeOffsetId;
  private String endTimeOffsetId;


  /** @return enabled */
  public boolean isEnabled() {
    return this.enabled;
  }

  /**
   * @param enabled enabled to set
   * @return Schedule
   */
  public <T extends Schedule> T setEnabled(boolean enabled) {
    this.enabled = enabled;
    return (T) this;
  }

  /** @return monday */
  public boolean isMonday() {
    return this.monday;
  }

  /**
   * @param monday monday to set
   * @return Schedule
   */
  public <T extends Schedule> T setMonday(boolean monday) {
    this.monday = monday;
    return (T) this;
  }

  /** @return saturday */
  public boolean isSaturday() {
    return this.saturday;
  }

  /**
   * @param saturday saturday to set
   * @return Schedule
   */
  public <T extends Schedule> T setSaturday(boolean saturday) {
    this.saturday = saturday;
    return (T) this;
  }

  /** @return timeFrameEnd */
  public OffsetDateTime getTimeFrameEnd() {
    return this.timeFrameEnd;
  }

  /**
   * @param timeFrameEnd timeFrameEnd to set
   * @return Schedule
   */
  public <T extends Schedule> T setTimeFrameEnd(OffsetDateTime timeFrameEnd) {
    this.timeFrameEnd = timeFrameEnd;
    return (T) this;
  }

  /** @return tuesday */
  public boolean isTuesday() {
    return this.tuesday;
  }

  /**
   * @param tuesday tuesday to set
   * @return Schedule
   */
  public <T extends Schedule> T setTuesday(boolean tuesday) {
    this.tuesday = tuesday;
    return (T) this;
  }

  /** @return holiday */
  public boolean isHoliday() {
    return this.holiday;
  }

  /**
   * @param holiday holiday to set
   * @return Schedule
   */
  public <T extends Schedule> T setHoliday(boolean holiday) {
    this.holiday = holiday;
    return (T) this;
  }

  /** @return sunday */
  public boolean isSunday() {
    return this.sunday;
  }

  /**
   * @param sunday sunday to set
   * @return Schedule
   */
  public <T extends Schedule> T setSunday(boolean sunday) {
    this.sunday = sunday;
    return (T) this;
  }

  /** @return thursday */
  public boolean isThursday() {
    return this.thursday;
  }

  /**
   * @param thursday thursday to set
   * @return Schedule
   */
  public <T extends Schedule> T setThursday(boolean thursday) {
    this.thursday = thursday;
    return (T) this;
  }

  /** @return friday */
  public boolean isFriday() {
    return this.friday;
  }

  /**
   * @param friday friday to set
   * @return Schedule
   */
  public <T extends Schedule> T setFriday(boolean friday) {
    this.friday = friday;
    return (T) this;
  }

  /** @return timeFrameStart */
  public OffsetDateTime getTimeFrameStart() {
    return this.timeFrameStart;
  }

  /**
   * @param timeFrameStart timeFrameStart to set
   * @return Schedule
   */
  public <T extends Schedule> T setTimeFrameStart(OffsetDateTime timeFrameStart) {
    this.timeFrameStart = timeFrameStart;
    return (T) this;
  }

  /** @return wednesday */
  public boolean isWednesday() {
    return this.wednesday;
  }

  /**
   * @param wednesday wednesday to set
   * @return Schedule
   */
  public <T extends Schedule> T setWednesday(boolean wednesday) {
    this.wednesday = wednesday;
    return (T) this;
  }
  @JsonIgnore
  @OneToMany(targetEntity = ScheduleToAction.class, mappedBy = "schedule")
  public List<ScheduleToAction> getScheduleToActions() {
    return scheduleToActions;
  }

  public Schedule setScheduleToActions(
          List<ScheduleToAction> scheduleToActions) {
    this.scheduleToActions = scheduleToActions;
    return this;
  }
  @JsonIgnore
  @OneToMany(targetEntity = ScheduleTimeslot.class, mappedBy = "schedule")
  public List<ScheduleTimeslot> getTimeslots() {
    return timeslots;
  }
  public Schedule setTimeslots(List<ScheduleTimeslot> timeslots) {
    this.timeslots = timeslots;
    return this;
  }

  public String getStartTimeOffsetId() {
    return startTimeOffsetId;
  }

  public <T extends Schedule> T setStartTimeOffsetId(String startTimeOffsetId) {
    this.startTimeOffsetId = startTimeOffsetId;
    return (T) this;
  }

  public String getEndTimeOffsetId() {
    return endTimeOffsetId;
  }

  public <T extends Schedule> T setEndTimeOffsetId(String endTimeOffsetId) {
    this.endTimeOffsetId = endTimeOffsetId;
    return (T) this;
  }
}
