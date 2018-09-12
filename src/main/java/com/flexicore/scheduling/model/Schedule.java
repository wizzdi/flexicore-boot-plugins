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

    @JsonIgnore
    @OneToMany(targetEntity = ScheduleTimeslot.class,mappedBy = "schedule",cascade = {CascadeType.PERSIST,CascadeType.MERGE})
    private List<ScheduleTimeslot> timeslots=new ArrayList<>();

    private LocalDateTime timeFrameStart;
    private LocalDateTime timeFrameEnd;

    private boolean sunday;
    private boolean monday;
    private boolean tuesday;
    private boolean wednesday;
    private boolean thursday;
    private boolean friday;
    private boolean saturday;
    private boolean holiday;


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




    public boolean isHoliday() {
        return holiday;
    }

    public Schedule setHoliday(boolean holiday) {
        this.holiday = holiday;
        return this;
    }

    @JsonIgnore
    @OneToMany(targetEntity = ScheduleTimeslot.class,mappedBy = "schedule",cascade = {CascadeType.PERSIST,CascadeType.MERGE})
    public List<ScheduleTimeslot> getTimeslots() {
        return timeslots;
    }

    public Schedule setTimeslots(List<ScheduleTimeslot> timeslots) {
        this.timeslots = timeslots;
        return this;
    }
}
