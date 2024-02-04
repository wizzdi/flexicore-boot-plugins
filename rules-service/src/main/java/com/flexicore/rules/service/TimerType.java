package com.flexicore.rules.service;

public enum TimerType {
    TOTAL("total"), TRIGGER("trigger"), EXECUTION("execution");

    private final String timerLogicalPart;

    TimerType(String timerLogicalPart) {
        this.timerLogicalPart = timerLogicalPart;
    }

    public String getTimerLogicalPart() {
        return timerLogicalPart;
    }
}