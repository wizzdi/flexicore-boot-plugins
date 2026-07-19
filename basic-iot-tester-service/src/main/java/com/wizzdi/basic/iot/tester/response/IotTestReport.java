package com.wizzdi.basic.iot.tester.response;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class IotTestReport {
    private String runId;
    private String tenantExternalId;
    private String gatewayExternalId;
    private Double gatewayLat;
    private Double gatewayLon;
    private String requestedByUserId;
    private String testUserId;
    private IotTestStatus status;
    private String currentStage;
    private OffsetDateTime startedAt;
    private OffsetDateTime completedAt;
    private long durationMs;
    private int passed;
    private int failed;
    private int skipped;
    private String firstFailure;
    private String jsonReportPath;
    private String markdownReportPath;
    private final Map<String, String> createdObjectIds = new ConcurrentHashMap<>();
    private final List<IotTestAssertion> assertions = new CopyOnWriteArrayList<>();

    public String getRunId() { return runId; }
    public IotTestReport setRunId(String runId) { this.runId = runId; return this; }
    public String getTenantExternalId() { return tenantExternalId; }
    public IotTestReport setTenantExternalId(String tenantExternalId) { this.tenantExternalId = tenantExternalId; return this; }
    public String getGatewayExternalId() { return gatewayExternalId; }
    public IotTestReport setGatewayExternalId(String gatewayExternalId) { this.gatewayExternalId = gatewayExternalId; return this; }
    public Double getGatewayLat() { return gatewayLat; }
    public IotTestReport setGatewayLat(Double gatewayLat) { this.gatewayLat = gatewayLat; return this; }
    public Double getGatewayLon() { return gatewayLon; }
    public IotTestReport setGatewayLon(Double gatewayLon) { this.gatewayLon = gatewayLon; return this; }
    public String getRequestedByUserId() { return requestedByUserId; }
    public IotTestReport setRequestedByUserId(String requestedByUserId) { this.requestedByUserId = requestedByUserId; return this; }
    public String getTestUserId() { return testUserId; }
    public IotTestReport setTestUserId(String testUserId) { this.testUserId = testUserId; return this; }
    public IotTestStatus getStatus() { return status; }
    public IotTestReport setStatus(IotTestStatus status) { this.status = status; return this; }
    public String getCurrentStage() { return currentStage; }
    public IotTestReport setCurrentStage(String currentStage) { this.currentStage = currentStage; return this; }
    public OffsetDateTime getStartedAt() { return startedAt; }
    public IotTestReport setStartedAt(OffsetDateTime startedAt) { this.startedAt = startedAt; return this; }
    public OffsetDateTime getCompletedAt() { return completedAt; }
    public IotTestReport setCompletedAt(OffsetDateTime completedAt) { this.completedAt = completedAt; return this; }
    public long getDurationMs() { return durationMs; }
    public IotTestReport setDurationMs(long durationMs) { this.durationMs = durationMs; return this; }
    public int getPassed() { return passed; }
    public IotTestReport setPassed(int passed) { this.passed = passed; return this; }
    public int getFailed() { return failed; }
    public IotTestReport setFailed(int failed) { this.failed = failed; return this; }
    public int getSkipped() { return skipped; }
    public IotTestReport setSkipped(int skipped) { this.skipped = skipped; return this; }
    public String getFirstFailure() { return firstFailure; }
    public IotTestReport setFirstFailure(String firstFailure) { this.firstFailure = firstFailure; return this; }
    public String getJsonReportPath() { return jsonReportPath; }
    public IotTestReport setJsonReportPath(String jsonReportPath) { this.jsonReportPath = jsonReportPath; return this; }
    public String getMarkdownReportPath() { return markdownReportPath; }
    public IotTestReport setMarkdownReportPath(String markdownReportPath) { this.markdownReportPath = markdownReportPath; return this; }
    public Map<String, String> getCreatedObjectIds() { return createdObjectIds; }
    public IotTestReport setCreatedObjectIds(Map<String, String> values) {
        createdObjectIds.clear();
        if (values != null) createdObjectIds.putAll(values);
        return this;
    }
    public List<IotTestAssertion> getAssertions() { return assertions; }
    public IotTestReport setAssertions(List<IotTestAssertion> values) {
        assertions.clear();
        if (values != null) assertions.addAll(values);
        return this;
    }
}
