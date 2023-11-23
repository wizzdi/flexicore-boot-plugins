package com.flexicore.scheduling.response;

public class ActionResult {
    private Long executionTime;
    private String value;

    public Long getExecutionTime() {
        return executionTime;
    }

    public <T extends ActionResult> T setExecutionTime(Long executionTime) {
        this.executionTime = executionTime;
        return (T) this;
    }

    public String getValue() {
        return value;
    }

    public <T extends ActionResult> T setValue(String value) {
        this.value = value;
        return (T) this;
    }
}
