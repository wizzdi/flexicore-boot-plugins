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
    private TimeOfTheDayName timeOfTheDayName;
    private double timeOfTheDayNameLat;
    private double timeOfTheDayNameLon;
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

    public TimeOfTheDayName getTimeOfTheDayName() {
        return timeOfTheDayName;
    }

    public ScheduleTimeslot setTimeOfTheDayName(TimeOfTheDayName timeOfTheDayName) {
        this.timeOfTheDayName = timeOfTheDayName;
        return this;
    }

    public double getTimeOfTheDayNameLat() {
        return timeOfTheDayNameLat;
    }

    public ScheduleTimeslot setTimeOfTheDayNameLat(double timeOfTheDayNameLat) {
        this.timeOfTheDayNameLat = timeOfTheDayNameLat;
        return this;
    }

    public double getTimeOfTheDayNameLon() {
        return timeOfTheDayNameLon;
    }

    public ScheduleTimeslot setTimeOfTheDayNameLon(double timeOfTheDayNameLon) {
        this.timeOfTheDayNameLon = timeOfTheDayNameLon;
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
}
