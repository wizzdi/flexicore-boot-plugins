package com.wizzdi.basic.iot.tester.response;

import java.time.OffsetDateTime;

public class IotTestAssertion {
    private String testId;
    private String stage;
    private String description;
    private String expected;
    private String actual;
    private IotTestAssertionStatus status;
    private OffsetDateTime startedAt;
    private OffsetDateTime completedAt;
    private long durationMs;
    private String error;

    public String getTestId() { return testId; }
    public IotTestAssertion setTestId(String testId) { this.testId = testId; return this; }
    public String getStage() { return stage; }
    public IotTestAssertion setStage(String stage) { this.stage = stage; return this; }
    public String getDescription() { return description; }
    public IotTestAssertion setDescription(String description) { this.description = description; return this; }
    public String getExpected() { return expected; }
    public IotTestAssertion setExpected(String expected) { this.expected = expected; return this; }
    public String getActual() { return actual; }
    public IotTestAssertion setActual(String actual) { this.actual = actual; return this; }
    public IotTestAssertionStatus getStatus() { return status; }
    public IotTestAssertion setStatus(IotTestAssertionStatus status) { this.status = status; return this; }
    public OffsetDateTime getStartedAt() { return startedAt; }
    public IotTestAssertion setStartedAt(OffsetDateTime startedAt) { this.startedAt = startedAt; return this; }
    public OffsetDateTime getCompletedAt() { return completedAt; }
    public IotTestAssertion setCompletedAt(OffsetDateTime completedAt) { this.completedAt = completedAt; return this; }
    public long getDurationMs() { return durationMs; }
    public IotTestAssertion setDurationMs(long durationMs) { this.durationMs = durationMs; return this; }
    public String getError() { return error; }
    public IotTestAssertion setError(String error) { this.error = error; return this; }
}
