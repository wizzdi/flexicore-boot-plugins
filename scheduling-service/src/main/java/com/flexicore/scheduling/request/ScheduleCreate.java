package com.flexicore.scheduling.request;

import com.wizzdi.flexicore.security.request.BasicCreate;
import java.time.OffsetDateTime;

public class ScheduleCreate extends BasicCreate {

  private Boolean enabled;

  private Boolean monday;

  private Boolean saturday;

  private OffsetDateTime timeFrameEnd;

  private Boolean tuesday;

  private Boolean holiday;

  private Boolean sunday;

  private Boolean thursday;

  private Boolean friday;

  private OffsetDateTime timeFrameStart;

  private Boolean wednesday;
  private String log;
  private String selectedTimeZone;
  public Boolean isEnabled() {
    return this.enabled;
  }

  public <T extends ScheduleCreate> T setEnabled(Boolean enabled) {
    this.enabled = enabled;
    return (T) this;
  }

  public Boolean isMonday() {
    return this.monday;
  }

  public <T extends ScheduleCreate> T setMonday(Boolean monday) {
    this.monday = monday;
    return (T) this;
  }

  public Boolean isSaturday() {
    return this.saturday;
  }

  public <T extends ScheduleCreate> T setSaturday(Boolean saturday) {
    this.saturday = saturday;
    return (T) this;
  }

  public OffsetDateTime getTimeFrameEnd() {
    return this.timeFrameEnd;
  }

  public <T extends ScheduleCreate> T setTimeFrameEnd(OffsetDateTime timeFrameEnd) {
    this.timeFrameEnd = timeFrameEnd;
    return (T) this;
  }

  public Boolean isTuesday() {
    return this.tuesday;
  }

  public <T extends ScheduleCreate> T setTuesday(Boolean tuesday) {
    this.tuesday = tuesday;
    return (T) this;
  }

  public Boolean isHoliday() {
    return this.holiday;
  }

  public <T extends ScheduleCreate> T setHoliday(Boolean holiday) {
    this.holiday = holiday;
    return (T) this;
  }

  public Boolean isSunday() {
    return this.sunday;
  }

  public <T extends ScheduleCreate> T setSunday(Boolean sunday) {
    this.sunday = sunday;
    return (T) this;
  }

  public Boolean isThursday() {
    return this.thursday;
  }

  public <T extends ScheduleCreate> T setThursday(Boolean thursday) {
    this.thursday = thursday;
    return (T) this;
  }

  public Boolean isFriday() {
    return this.friday;
  }

  public <T extends ScheduleCreate> T setFriday(Boolean friday) {
    this.friday = friday;
    return (T) this;
  }

  public OffsetDateTime getTimeFrameStart() {
    return this.timeFrameStart;
  }

  public <T extends ScheduleCreate> T setTimeFrameStart(OffsetDateTime timeFrameStart) {
    this.timeFrameStart = timeFrameStart;
    return (T) this;
  }

  public Boolean isWednesday() {
    return this.wednesday;
  }

  public <T extends ScheduleCreate> T setWednesday(Boolean wednesday) {
    this.wednesday = wednesday;
    return (T) this;
  }

  public String getLog() {
    return log;
  }

  public <T extends ScheduleCreate> T setLog(String log) {
    this.log = log;
    return (T) this;
  }

  public String getSelectedTimeZone() {
    return selectedTimeZone;
  }

  public ScheduleCreate setSelectedTimeZone(String selectedTimeZone) {
    this.selectedTimeZone = selectedTimeZone;
    return this;
  }
}
