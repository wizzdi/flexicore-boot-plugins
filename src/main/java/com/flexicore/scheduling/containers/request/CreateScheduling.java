package com.flexicore.scheduling.containers.request;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class CreateScheduling {

    private String name;
    private String description;
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

    public String getTimeOfTheDayName() {
        return timeOfTheDayName;
    }

    public CreateScheduling setTimeOfTheDayName(String timeOfTheDayName) {
        this.timeOfTheDayName = timeOfTheDayName;
        return this;
    }

    public LocalDateTime getTimeOfTheDay() {
        return timeOfTheDay;
    }

    public CreateScheduling setTimeOfTheDay(LocalDateTime timeOfTheDay) {
        this.timeOfTheDay = timeOfTheDay;
        return this;
    }

    public long getMillisOffset() {
        return millisOffset;
    }

    public CreateScheduling setMillisOffset(long millisOffset) {
        this.millisOffset = millisOffset;
        return this;
    }

    public boolean isSunday() {
        return sunday;
    }

    public CreateScheduling setSunday(boolean sunday) {
        this.sunday = sunday;
        return this;
    }

    public boolean isMonday() {
        return monday;
    }

    public CreateScheduling setMonday(boolean monday) {
        this.monday = monday;
        return this;
    }

    public boolean isTuesday() {
        return tuesday;
    }

    public CreateScheduling setTuesday(boolean tuesday) {
        this.tuesday = tuesday;
        return this;
    }

    public boolean isWednesday() {
        return wednesday;
    }

    public CreateScheduling setWednesday(boolean wednesday) {
        this.wednesday = wednesday;
        return this;
    }

    public boolean isThursday() {
        return thursday;
    }

    public CreateScheduling setThursday(boolean thursday) {
        this.thursday = thursday;
        return this;
    }

    public boolean isFriday() {
        return friday;
    }

    public CreateScheduling setFriday(boolean friday) {
        this.friday = friday;
        return this;
    }

    public boolean isSaturday() {
        return saturday;
    }

    public CreateScheduling setSaturday(boolean saturday) {
        this.saturday = saturday;
        return this;
    }


    public String getDescription() {
        return description;
    }

    public CreateScheduling setDescription(String description) {
        this.description = description;
        return this;
    }
}
