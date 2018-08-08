package com.flexicore.scheduling.containers.request;

import com.flexicore.scheduling.model.TimeOfTheDayName;

import java.time.LocalDateTime;

public class CreateScheduling {

    private String name;
    private String description;
    private LocalDateTime timeFrameStart;
    private LocalDateTime timeFrameEnd;
    private TimeOfTheDayName timeOfTheDayName;
    private Double timeOfTheDayNameLat;
    private Double timeOfTheDayNameLon;

    private LocalDateTime timeOfTheDayStart;
    private LocalDateTime timeOfTheDayEnd;
    private Long millisOffset;
    private Long coolDownIntervalBeforeRepeat;
    private Boolean sunday;
    private Boolean monday;
    private Boolean tuesday;
    private Boolean wednesday;
    private Boolean thursday;
    private Boolean friday;
    private Boolean saturday;
    private Boolean holiday;


    public String getName() {
        return name;
    }

    public CreateScheduling setName(String name) {
        this.name = name;
        return this;
    }

    public LocalDateTime getTimeFrameStart() {
        return timeFrameStart;
    }

    public CreateScheduling setTimeFrameStart(LocalDateTime timeFrameStart) {
        this.timeFrameStart = timeFrameStart;
        return this;
    }

    public LocalDateTime getTimeFrameEnd() {
        return timeFrameEnd;
    }

    public CreateScheduling setTimeFrameEnd(LocalDateTime timeFrameEnd) {
        this.timeFrameEnd = timeFrameEnd;
        return this;
    }

    public TimeOfTheDayName getTimeOfTheDayName() {
        return timeOfTheDayName;
    }

    public CreateScheduling setTimeOfTheDayName(TimeOfTheDayName timeOfTheDayName) {
        this.timeOfTheDayName = timeOfTheDayName;
        return this;
    }



    public Long getMillisOffset() {
        return millisOffset;
    }

    public CreateScheduling setMillisOffset(Long millisOffset) {
        this.millisOffset = millisOffset;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public CreateScheduling setDescription(String description) {
        this.description = description;
        return this;
    }

    public Boolean getSunday() {
        return sunday;
    }

    public CreateScheduling setSunday(Boolean sunday) {
        this.sunday = sunday;
        return this;
    }

    public Boolean getMonday() {
        return monday;
    }

    public CreateScheduling setMonday(Boolean monday) {
        this.monday = monday;
        return this;
    }

    public Boolean getTuesday() {
        return tuesday;
    }

    public CreateScheduling setTuesday(Boolean tuesday) {
        this.tuesday = tuesday;
        return this;
    }

    public Boolean getWednesday() {
        return wednesday;
    }

    public CreateScheduling setWednesday(Boolean wednesday) {
        this.wednesday = wednesday;
        return this;
    }

    public Boolean getThursday() {
        return thursday;
    }

    public CreateScheduling setThursday(Boolean thursday) {
        this.thursday = thursday;
        return this;
    }

    public Boolean getFriday() {
        return friday;
    }

    public CreateScheduling setFriday(Boolean friday) {
        this.friday = friday;
        return this;
    }

    public Boolean getSaturday() {
        return saturday;
    }

    public CreateScheduling setSaturday(Boolean saturday) {
        this.saturday = saturday;
        return this;
    }

    public LocalDateTime getTimeOfTheDayStart() {
        return timeOfTheDayStart;
    }

    public CreateScheduling setTimeOfTheDayStart(LocalDateTime timeOfTheDayStart) {
        this.timeOfTheDayStart = timeOfTheDayStart;
        return this;
    }

    public LocalDateTime getTimeOfTheDayEnd() {
        return timeOfTheDayEnd;
    }

    public CreateScheduling setTimeOfTheDayEnd(LocalDateTime timeOfTheDayEnd) {
        this.timeOfTheDayEnd = timeOfTheDayEnd;
        return this;
    }

    public Long getCoolDownIntervalBeforeRepeat() {
        return coolDownIntervalBeforeRepeat;
    }

    public CreateScheduling setCoolDownIntervalBeforeRepeat(Long coolDownIntervalBeforeRepeat) {
        this.coolDownIntervalBeforeRepeat = coolDownIntervalBeforeRepeat;
        return this;
    }

    public Double getTimeOfTheDayNameLat() {
        return timeOfTheDayNameLat;
    }

    public CreateScheduling setTimeOfTheDayNameLat(Double timeOfTheDayNameLat) {
        this.timeOfTheDayNameLat = timeOfTheDayNameLat;
        return this;
    }

    public Double getTimeOfTheDayNameLon() {
        return timeOfTheDayNameLon;
    }

    public CreateScheduling setTimeOfTheDayNameLon(Double timeOfTheDayNameLon) {
        this.timeOfTheDayNameLon = timeOfTheDayNameLon;
        return this;
    }

    public Boolean getHoliday() {
        return holiday;
    }

    public CreateScheduling setHoliday(Boolean holiday) {
        this.holiday = holiday;
        return this;
    }
}
