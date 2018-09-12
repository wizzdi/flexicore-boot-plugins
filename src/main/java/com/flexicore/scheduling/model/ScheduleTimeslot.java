package com.flexicore.scheduling.model;

import com.flexicore.model.Baseclass;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import java.time.LocalDateTime;

@Entity
public class ScheduleTimeslot extends Baseclass {
    private static ScheduleTimeslot s_Singleton=new ScheduleTimeslot();
    public  static ScheduleTimeslot s() {return s_Singleton;}


    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private TimeOfTheDayName startTimeOfTheDayName;
    private double timeOfTheDayNameStartLat;
    private double timeOfTheDayNameStartLon;

    private TimeOfTheDayName endTimeOfTheDayName;
    private double timeOfTheDayNameEndLat;
    private double timeOfTheDayNameEndLon;
    private long coolDownIntervalBeforeRepeat;
    private long millisOffset;

    @ManyToOne(targetEntity = Schedule.class)
    private Schedule schedule;


    public LocalDateTime getStartTime() {
        return startTime;
    }

    public ScheduleTimeslot setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
        return this;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public ScheduleTimeslot setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
        return this;
    }
    @ManyToOne(targetEntity = Schedule.class)
    public Schedule getSchedule() {
        return schedule;
    }

    public ScheduleTimeslot setSchedule(Schedule schedule) {
        this.schedule = schedule;
        return this;
    }

    public TimeOfTheDayName getStartTimeOfTheDayName() {
        return startTimeOfTheDayName;
    }

    public ScheduleTimeslot setStartTimeOfTheDayName(TimeOfTheDayName startTimeOfTheDayName) {
        this.startTimeOfTheDayName = startTimeOfTheDayName;
        return this;
    }

    public double getTimeOfTheDayNameStartLat() {
        return timeOfTheDayNameStartLat;
    }

    public ScheduleTimeslot setTimeOfTheDayNameStartLat(double timeOfTheDayNameStartLat) {
        this.timeOfTheDayNameStartLat = timeOfTheDayNameStartLat;
        return this;
    }

    public double getTimeOfTheDayNameStartLon() {
        return timeOfTheDayNameStartLon;
    }

    public ScheduleTimeslot setTimeOfTheDayNameStartLon(double timeOfTheDayNameStartLon) {
        this.timeOfTheDayNameStartLon = timeOfTheDayNameStartLon;
        return this;
    }

    public long getCoolDownIntervalBeforeRepeat() {
        return coolDownIntervalBeforeRepeat;
    }

    public ScheduleTimeslot setCoolDownIntervalBeforeRepeat(long coolDownIntervalBeforeRepeat) {
        this.coolDownIntervalBeforeRepeat = coolDownIntervalBeforeRepeat;
        return this;
    }

    public long getMillisOffset() {
        return millisOffset;
    }

    public ScheduleTimeslot setMillisOffset(long millisOffset) {
        this.millisOffset = millisOffset;
        return this;
    }


    public TimeOfTheDayName getEndTimeOfTheDayName() {
        return endTimeOfTheDayName;
    }

    public ScheduleTimeslot setEndTimeOfTheDayName(TimeOfTheDayName endTimeOfTheDayName) {
        this.endTimeOfTheDayName = endTimeOfTheDayName;
        return this;
    }

    public double getTimeOfTheDayNameEndLat() {
        return timeOfTheDayNameEndLat;
    }

    public ScheduleTimeslot setTimeOfTheDayNameEndLat(double timeOfTheDayNameEndLat) {
        this.timeOfTheDayNameEndLat = timeOfTheDayNameEndLat;
        return this;
    }

    public double getTimeOfTheDayNameEndLon() {
        return timeOfTheDayNameEndLon;
    }

    public ScheduleTimeslot setTimeOfTheDayNameEndLon(double timeOfTheDayNameEndLon) {
        this.timeOfTheDayNameEndLon = timeOfTheDayNameEndLon;
        return this;
    }
}
