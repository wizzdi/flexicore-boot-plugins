package com.flexicore.scheduling.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flexicore.model.Baseclass;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Schedule extends Baseclass {
    private static Schedule s_Singleton = new Schedule();

    public static Schedule s() {
        return s_Singleton;
    }

    @JsonIgnore
    @OneToMany(targetEntity = ScheduleToAction.class,mappedBy = "leftside",cascade = {CascadeType.PERSIST,CascadeType.MERGE})
    private List<ScheduleToAction> scheduleToActions=new ArrayList<>();

    private LocalDateTime timeFrameStart;
    private LocalDateTime timeFrameEnd;
    private String timeOfTheDayName;
    private LocalDateTime timeOfTheDay;
    private long millisOffset;
    private boolean sunday;
    private boolean monday;
    private boolean tuesday;
    private boolean wednesday;
    private boolean thursday;
    private boolean friday;
    private boolean saturday;


    public LocalDateTime getTimeFrameStart() {
        return timeFrameStart;
    }

    public Schedule setTimeFrameStart(LocalDateTime timeFrameStart) {
        this.timeFrameStart = timeFrameStart;
        return this;
    }

    public LocalDateTime getTimeFrameEnd() {
        return timeFrameEnd;
    }

    public Schedule setTimeFrameEnd(LocalDateTime timeFrameEnd) {
        this.timeFrameEnd = timeFrameEnd;
        return this;
    }

    public String getTimeOfTheDayName() {
        return timeOfTheDayName;
    }

    public Schedule setTimeOfTheDayName(String timeOfTheDayName) {
        this.timeOfTheDayName = timeOfTheDayName;
        return this;
    }

    public LocalDateTime getTimeOfTheDay() {
        return timeOfTheDay;
    }

    public Schedule setTimeOfTheDay(LocalDateTime timeOfTheDay) {
        this.timeOfTheDay = timeOfTheDay;
        return this;
    }

    public long getMillisOffset() {
        return millisOffset;
    }

    public Schedule setMillisOffset(long millisOffset) {
        this.millisOffset = millisOffset;
        return this;
    }

    public boolean isSunday() {
        return sunday;
    }

    public Schedule setSunday(boolean sunday) {
        this.sunday = sunday;
        return this;
    }

    public boolean isMonday() {
        return monday;
    }

    public Schedule setMonday(boolean monday) {
        this.monday = monday;
        return this;
    }

    public boolean isTuesday() {
        return tuesday;
    }

    public Schedule setTuesday(boolean tuesday) {
        this.tuesday = tuesday;
        return this;
    }

    public boolean isWednesday() {
        return wednesday;
    }

    public Schedule setWednesday(boolean wednesday) {
        this.wednesday = wednesday;
        return this;
    }

    public boolean isThursday() {
        return thursday;
    }

    public Schedule setThursday(boolean thursday) {
        this.thursday = thursday;
        return this;
    }

    public boolean isFriday() {
        return friday;
    }

    public Schedule setFriday(boolean friday) {
        this.friday = friday;
        return this;
    }

    public boolean isSaturday() {
        return saturday;
    }

    public Schedule setSaturday(boolean saturday) {
        this.saturday = saturday;
        return this;
    }

    @JsonIgnore
    @OneToMany(targetEntity = ScheduleToAction.class,mappedBy = "leftside",cascade = {CascadeType.PERSIST,CascadeType.MERGE})
    public List<ScheduleToAction> getScheduleToActions() {
        return scheduleToActions;
    }

    public Schedule setScheduleToActions(List<ScheduleToAction> scheduleToActions) {
        this.scheduleToActions = scheduleToActions;
        return this;
    }
}
