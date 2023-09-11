package com.wizzdi.basic.iot.client;

public enum TimerType {
    TOTAL("total"), PROCESSING("processing"), WAITING("waiting"), PARSING("parsing"), VERIFYING("verifying");

    private final String timerLogicalPart;

    TimerType(String timerLogicalPart) {
        this.timerLogicalPart = timerLogicalPart;
    }

    public String getTimerLogicalPart() {
        return timerLogicalPart;
    }
}
