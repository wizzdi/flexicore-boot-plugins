package com.wizzdi.basic.iot.tester.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.flexicore.annotations.rest.All;
import com.flexicore.model.Baseclass;
import com.flexicore.model.Clazz;
import com.flexicore.model.Role;
import com.flexicore.model.RoleToBaseclass;
import com.flexicore.model.RoleToUser;
import com.flexicore.model.SecurityOperation;
import com.flexicore.model.SecurityTenant;
import com.flexicore.model.User;
import com.wizzdi.basic.iot.client.BasicIOTClient;
import com.wizzdi.basic.iot.client.BasicIOTConnection;
import com.wizzdi.basic.iot.client.BadMessage;
import com.wizzdi.basic.iot.client.IOTMessage;
import com.wizzdi.basic.iot.client.IOTMessageSubscriber;
import com.wizzdi.basic.iot.client.KeepAlive;
import com.wizzdi.basic.iot.client.RegisterGateway;
import com.wizzdi.basic.iot.client.RegisterGatewayReceived;
import com.wizzdi.basic.iot.client.StateChanged;
import com.wizzdi.basic.iot.client.StateChangedReceived;
import com.wizzdi.basic.iot.client.UpdateStateSchema;
import com.wizzdi.basic.iot.client.UpdateStateSchemaReceived;
import com.wizzdi.basic.iot.model.ConditionJoinType;
import com.wizzdi.basic.iot.model.Connectivity;
import com.wizzdi.basic.iot.model.ConnectivityChange;
import com.wizzdi.basic.iot.model.Device;
import com.wizzdi.basic.iot.model.DeviceType;
import com.wizzdi.basic.iot.model.FleetHealthMetricType;
import com.wizzdi.basic.iot.model.FleetHealthPolicy;
import com.wizzdi.basic.iot.model.FleetUnknownPolicy;
import com.wizzdi.basic.iot.model.Gateway;
import com.wizzdi.basic.iot.model.HealthComparisonOperator;
import com.wizzdi.basic.iot.model.HealthIncident;
import com.wizzdi.basic.iot.model.HealthIncidentAction;
import com.wizzdi.basic.iot.model.HealthIncidentActionType;
import com.wizzdi.basic.iot.model.HealthIncidentStatus;
import com.wizzdi.basic.iot.model.HealthNotificationChannel;
import com.wizzdi.basic.iot.model.HealthNotificationDelivery;
import com.wizzdi.basic.iot.model.HealthNotificationDeliveryMode;
import com.wizzdi.basic.iot.model.HealthNotificationDeliveryStatus;
import com.wizzdi.basic.iot.model.HealthNotificationEventType;
import com.wizzdi.basic.iot.model.HealthNotificationPolicy;
import com.wizzdi.basic.iot.model.HealthNotificationScopeType;
import com.wizzdi.basic.iot.model.PendingGateway;
import com.wizzdi.basic.iot.model.Remote;
import com.wizzdi.basic.iot.model.RemoteGroup;
import com.wizzdi.basic.iot.model.RemoteGroupMembershipAction;
import com.wizzdi.basic.iot.model.RemoteGroupToRemote;
import com.wizzdi.basic.iot.model.RemoteHealthHistory;
import com.wizzdi.basic.iot.model.StateHistory;
import com.wizzdi.basic.iot.service.controller.PendingGatewayController;
import com.wizzdi.basic.iot.service.request.ApproveGatewaysRequest;
import com.wizzdi.basic.iot.service.request.ConnectivityChangeFilter;
import com.wizzdi.basic.iot.service.request.DeviceFilter;
import com.wizzdi.basic.iot.service.request.DeviceTypeFilter;
import com.wizzdi.basic.iot.service.request.FleetHealthPolicyCreate;
import com.wizzdi.basic.iot.service.request.FleetHealthRuleConditionCreate;
import com.wizzdi.basic.iot.service.request.FleetHealthRuleCreate;
import com.wizzdi.basic.iot.service.request.GatewayFilter;
import com.wizzdi.basic.iot.service.request.HealthIncidentActionCreate;
import com.wizzdi.basic.iot.service.request.HealthIncidentFilter;
import com.wizzdi.basic.iot.service.request.HealthNotificationChannelPreferenceCreate;
import com.wizzdi.basic.iot.service.request.HealthNotificationDeliveryFilter;
import com.wizzdi.basic.iot.service.request.HealthNotificationPolicyCreate;
import com.wizzdi.basic.iot.service.request.MarkHealthNotificationsRequest;
import com.wizzdi.basic.iot.service.request.PendingGatewayFilter;
import com.wizzdi.basic.iot.service.request.RemoteFilter;
import com.wizzdi.basic.iot.service.request.RemoteGroupCreate;
import com.wizzdi.basic.iot.service.request.RemoteGroupHealthHistoryFilter;
import com.wizzdi.basic.iot.service.request.RemoteGroupToRemoteCreate;
import com.wizzdi.basic.iot.service.request.RemoteHealthHistoryFilter;
import com.wizzdi.basic.iot.service.request.RemoteUpdate;
import com.wizzdi.basic.iot.service.request.StateHistoryFilter;
import com.wizzdi.basic.iot.service.response.RemoteGroupHealthSnapshot;
import com.wizzdi.basic.iot.service.service.BasicIOTLogic;
import com.wizzdi.basic.iot.service.service.ConnectivityChangeService;
import com.wizzdi.basic.iot.service.service.DeviceService;
import com.wizzdi.basic.iot.service.service.DeviceTypeService;
import com.wizzdi.basic.iot.service.service.DeviceTypeToMapIconService;
import com.wizzdi.basic.iot.service.service.FleetHealthPolicyService;
import com.wizzdi.basic.iot.service.service.GatewayService;
import com.wizzdi.basic.iot.service.service.HealthHistoryService;
import com.wizzdi.basic.iot.service.service.HealthIncidentService;
import com.wizzdi.basic.iot.service.service.HealthNotificationPolicyService;
import com.wizzdi.basic.iot.service.service.HealthNotificationService;
import com.wizzdi.basic.iot.service.service.PendingGatewayService;
import com.wizzdi.basic.iot.service.service.RemoteGroupFleetHealthService;
import com.wizzdi.basic.iot.service.service.RemoteGroupService;
import com.wizzdi.basic.iot.service.service.RemoteGroupToRemoteService;
import com.wizzdi.basic.iot.service.service.RemoteService;
import com.wizzdi.basic.iot.service.service.StateHistoryService;
import com.wizzdi.basic.iot.tester.request.IotTestStartRequest;
import com.wizzdi.basic.iot.tester.response.IotTestAssertion;
import com.wizzdi.basic.iot.tester.response.IotTestAssertionStatus;
import com.wizzdi.basic.iot.tester.response.IotTestReport;
import com.wizzdi.basic.iot.tester.response.IotTestStatus;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import com.wizzdi.flexicore.common.user.request.CommonUserCreate;
import com.wizzdi.flexicore.common.user.request.CommonUserFilter;
import com.wizzdi.flexicore.common.user.service.CommonUserService;
import com.wizzdi.flexicore.security.configuration.SecurityContext;
import com.wizzdi.flexicore.security.interfaces.SecurityContextProvider;
import com.wizzdi.flexicore.security.request.RoleCreate;
import com.wizzdi.flexicore.security.request.RoleFilter;
import com.wizzdi.flexicore.security.request.RoleToBaseclassCreate;
import com.wizzdi.flexicore.security.request.RoleToBaseclassFilter;
import com.wizzdi.flexicore.security.request.RoleToUserCreate;
import com.wizzdi.flexicore.security.request.RoleToUserFilter;
import com.wizzdi.flexicore.security.request.SecurityTenantCreate;
import com.wizzdi.flexicore.security.request.SecurityTenantFilter;
import com.wizzdi.flexicore.security.service.RoleService;
import com.wizzdi.flexicore.security.service.RoleToBaseclassService;
import com.wizzdi.flexicore.security.service.RoleToUserService;
import com.wizzdi.flexicore.security.service.SecurityOperationService;
import com.wizzdi.flexicore.security.service.SecurityTenantService;
import com.wizzdi.segmantix.model.Access;
import com.wizzdi.maps.model.MapIcon;
import com.wizzdi.maps.model.MappedPOI;
import com.wizzdi.maps.service.request.LocationArea;
import com.wizzdi.maps.service.request.MappedPOIFilter;
import com.wizzdi.maps.service.service.MappedPOIService;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.pf4j.Extension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.integration.mqtt.inbound.MqttPahoMessageDrivenChannelAdapter;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.RSAPrivateCrtKey;
import java.security.spec.RSAPublicKeySpec;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.SplittableRandom;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@Component
@Extension
public class IotTesterService implements Plugin, IOTMessageSubscriber<IOTMessage> {
    private static final Logger logger = LoggerFactory.getLogger("iot-tester");
    private static final String STAGE_PREFLIGHT = "PREFLIGHT";
    private static final String STAGE_TENANT = "TENANT";
    private static final String STAGE_TENANT_ADMIN = "TENANT_ADMIN_SECURITY";
    private static final String STAGE_REGISTRATION = "MQTT_REGISTRATION";
    private static final String STAGE_APPROVAL = "GATEWAY_APPROVAL";
    private static final String STAGE_POLICY = "NOTIFICATION_POLICY";
    private static final String STAGE_SCHEMA = "DEVICE_SCHEMA";
    private static final String STAGE_KEEPALIVE = "KEEPALIVE_CONNECTIVITY";
    private static final String STAGE_HEALTH = "STATE_AND_HEALTH";
    private static final String STAGE_INCIDENTS = "INCIDENTS_AND_ACTIONS";
    private static final String STAGE_GROUP = "GROUP_AND_FLEET_HEALTH";
    private static final String STAGE_DISCONNECT = "DISCONNECTION";
    private static final String STAGE_RECOVERY = "RECOVERY";
    private static final String STAGE_EXTERNAL = "EXTERNAL_NOTIFICATIONS";
    private static final String STAGE_TENANT_ISOLATION = "TENANT_ISOLATION";
    private static final String STAGE_REPORT = "REPORT";
    private static final double DEFAULT_GATEWAY_LAT = 31.9595535;
    private static final double DEFAULT_GATEWAY_LON = 34.816376;
    private static final double DEVICE_LOCATION_RADIUS_METERS = 200.0;
    private static final double MAP_QUERY_MARGIN_METERS = 25.0;
    private static final double EARTH_RADIUS_METERS = 6_371_000.0;
    private static final double LOCATION_TOLERANCE = 0.0000001;
    private static final String CONNECTED_MAP_STATUS = "connected";
    private static final String RECOVERED_MAP_STATUS = "recovered";
    private static final String SECURITY_WILDCARD_CLAZZ_NAME = "SecurityWildcard";

    @Value("${iot.tester.enabled:false}")
    private boolean enabled;
    @Value("${iot.tester.reportDirectory:/var/log/flexicore/iot-test-reports}")
    private String reportDirectory;
    @Value("${iot.tester.maxDeviceCount:100}")
    private int maxDeviceCount;

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private BasicIOTClient basicIOTClient;
    @Autowired
    private BasicIOTConnection basicIOTConnection;
    @Autowired
    private BasicIOTLogic basicIOTLogic;
    @Autowired
    private PrivateKey privateKey;
    @Autowired
    private ConnectivityChangeService connectivityChangeService;
    @Autowired
    private SecurityTenantService securityTenantService;
    @Autowired
    private CommonUserService commonUserService;
    @Autowired
    private RoleService roleService;
    @Autowired
    private RoleToUserService roleToUserService;
    @Autowired
    private RoleToBaseclassService roleToBaseclassService;
    @Autowired
    private SecurityOperationService securityOperationService;
    @Autowired
    private SecurityContext adminSecurityContext;
    @Autowired
    private SecurityContextProvider securityContextProvider;
    @Autowired
    private PendingGatewayService pendingGatewayService;
    @Autowired
    private GatewayService gatewayService;
    @Autowired
    private DeviceService deviceService;
    @Autowired
    private DeviceTypeService deviceTypeService;
    @Autowired
    private DeviceTypeToMapIconService deviceTypeToMapIconService;
    @Autowired
    private MappedPOIService mappedPOIService;
    @Autowired
    private RemoteService remoteService;
    @Autowired
    private StateHistoryService stateHistoryService;
    @Autowired
    private HealthHistoryService healthHistoryService;
    @Autowired
    private HealthIncidentService healthIncidentService;
    @Autowired
    private HealthNotificationPolicyService healthNotificationPolicyService;
    @Autowired
    private HealthNotificationService healthNotificationService;
    @Autowired
    private FleetHealthPolicyService fleetHealthPolicyService;
    @Autowired
    private RemoteGroupService remoteGroupService;
    @Autowired
    private RemoteGroupToRemoteService remoteGroupToRemoteService;
    @Autowired
    private RemoteGroupFleetHealthService remoteGroupFleetHealthService;

    private final ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();
    private final Map<String, IotTestReport> reports = new ConcurrentHashMap<>();
    private final Map<String, MqttRequestCorrelation> mqttRequestCorrelations = new ConcurrentHashMap<>();
    private final Map<String, RunExecution> activeRuns = new ConcurrentHashMap<>();
    private final Map<String, String> activeGatewayExternalIds = new ConcurrentHashMap<>();
    private final Map<String, ReentrantLock> tenantBootstrapLocks = new ConcurrentHashMap<>();
    private final ThreadLocal<RunExecution> currentRunExecution = new ThreadLocal<>();
    private final AtomicReference<String> latestRunId = new AtomicReference<>();
    private volatile String simulatedGatewayPublicKey;

    public IotTestReport start(IotTestStartRequest supplied, SecurityContext callerSecurityContext) {
        if (!enabled) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "IoT tester is disabled. Set iot.tester.enabled=true in the active server configuration and restart FlexiCore.");
        }
        if (callerSecurityContext == null || callerSecurityContext.getUser() == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "An authenticated caller is required");
        }
        IotTestStartRequest request = normalize(supplied);
        if (request.getTenantExternalId() == null || request.getTenantExternalId().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "tenantExternalId is required");
        }
        String runId = OffsetDateTime.now().toString().replaceAll("[^0-9]", "")
                + "-" + UUID.randomUUID().toString().substring(0, 8);
        String suppliedGatewayExternalId = request.getGatewayExternalId();
        String gatewayExternalId;
        if (suppliedGatewayExternalId == null || suppliedGatewayExternalId.isBlank()) {
            String gatewayRunSuffix = "-" + runId;
            String gatewayExternalIdPrefix = "iot-tester-" + safeToken(request.getTenantExternalId());
            int maximumPrefixLength = 128 - gatewayRunSuffix.length();
            if (gatewayExternalIdPrefix.length() > maximumPrefixLength) {
                gatewayExternalIdPrefix = gatewayExternalIdPrefix.substring(0, maximumPrefixLength);
            }
            gatewayExternalId = gatewayExternalIdPrefix + gatewayRunSuffix;
        } else {
            gatewayExternalId = suppliedGatewayExternalId.trim();
            if (!gatewayExternalId.matches("[A-Za-z0-9._-]{1,128}")) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "gatewayExternalId may contain only letters, numbers, dot, underscore and hyphen, maximum 128 characters");
            }
        }
        request.setGatewayExternalId(gatewayExternalId);
        IotTestReport report = new IotTestReport()
                .setRunId(runId)
                .setTenantExternalId(request.getTenantExternalId().trim())
                .setGatewayExternalId(gatewayExternalId)
                .setGatewayLat(request.getLat())
                .setGatewayLon(request.getLon())
                .setRequestedByUserId(callerSecurityContext.getUser().getId())
                .setStatus(IotTestStatus.QUEUED)
                .setStartedAt(OffsetDateTime.now());
        String existingRun = activeGatewayExternalIds.putIfAbsent(gatewayExternalId, runId);
        if (existingRun != null) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Gateway externalId is already used by active test run " + existingRun + ": " + gatewayExternalId);
        }

        RunExecution runExecution = new RunExecution(runId, gatewayExternalId);
        activeRuns.put(runId, runExecution);
        reports.put(runId, report);
        latestRunId.set(runId);
        try {
            Future<?> future = CompletableFuture.runAsync(() -> execute(report, request), executor);
            runExecution.setFuture(future);
            return report;
        } catch (RuntimeException e) {
            activeRuns.remove(runId, runExecution);
            activeGatewayExternalIds.remove(gatewayExternalId, runId);
            reports.remove(runId, report);
            throw e;
        }
    }

    public IotTestReport getReport(String runId) {
        if (runId == null || runId.isBlank()) return null;
        IotTestReport current = reports.get(runId);
        if (current != null) return current;
        Path path = Path.of(reportDirectory).resolve("iot-test-" + safeFileToken(runId) + ".json");
        if (!Files.isRegularFile(path)) return null;
        try {
            IotTestReport loaded = objectMapper.readValue(path.toFile(), IotTestReport.class);
            reports.putIfAbsent(runId, loaded);
            return reports.get(runId);
        } catch (IOException e) {
            logger.error("TEST_REPORT_READ_FAILED runId={} path={}", runId, path, e);
            return null;
        }
    }

    public IotTestReport getLatestReport() {
        String runId = latestRunId.get();
        if (runId != null) return getReport(runId);
        Path directory = Path.of(reportDirectory);
        if (!Files.isDirectory(directory)) return null;
        try (var files = Files.list(directory)) {
            Path latest = files.filter(Files::isRegularFile)
                    .filter(path -> path.getFileName().toString().startsWith("iot-test-"))
                    .filter(path -> path.getFileName().toString().endsWith(".json"))
                    .max((a, b) -> Long.compare(lastModified(a), lastModified(b)))
                    .orElse(null);
            if (latest == null) return null;
            String name = latest.getFileName().toString();
            String loadedRunId = name.substring("iot-test-".length(), name.length() - ".json".length());
            latestRunId.compareAndSet(null, loadedRunId);
            return getReport(loadedRunId);
        } catch (IOException e) {
            logger.error("TEST_REPORT_LIST_FAILED directory={}", directory, e);
            return null;
        }
    }

    public IotTestReport abort(String runId) {
        IotTestReport report = reports.get(runId);
        if (report == null) return null;
        RunExecution runExecution = activeRuns.get(runId);
        if (runExecution != null) {
            logger.info("TEST_RUN_ABORT_REQUESTED runId={} gatewayExternalId={}",
                    runExecution.runId, runExecution.gatewayExternalId);
            runExecution.abort();
        }
        return report;
    }

    @PostConstruct
    public void registerRuntimeSubscriber() {
        boolean added = basicIOTClient.addSubscriber(this);
        logger.info("TEST_MQTT_SUBSCRIBER_REGISTERED added={} subscriberCount={}",
                added, basicIOTClient.getSubscriberCount());
    }

    @Override
    public void onIOTMessage(IOTMessage iotMessage) {
        if (iotMessage == null || iotMessage.getMessage() == null) return;
        if (iotMessage instanceof BadMessage badMessage) {
            String requestId = originalMessageId(badMessage);
            MqttRequestCorrelation correlation =
                    requestId == null ? null : mqttRequestCorrelations.get(requestId);
            if (correlation != null) {
                correlation.queue().offer(iotMessage);
                logger.error("TEST_MQTT_BAD_MESSAGE runId={} gatewayExternalId={} requestId={} error={}",
                        correlation.runId(), correlation.gatewayExternalId(), requestId, badMessage.getError());
                return;
            }
        }
        String topic = iotMessage.getMessage().getHeaders()
                .get(BasicIOTClient.MQTT_RECEIVED_TOPIC, String.class);
        if (topic == null) return;
        MqttRequestCorrelation correlation = mqttRequestCorrelations.get(topic);
        if (correlation != null) {
            correlation.queue().offer(iotMessage);
            logger.debug("TEST_MQTT_RESPONSE runId={} gatewayExternalId={} topic={} expected={} type={} id={}",
                    correlation.runId(), correlation.gatewayExternalId(), topic,
                    correlation.responseType().getSimpleName(),
                    iotMessage.getClass().getSimpleName(), iotMessage.getId());
        }
    }

    private String originalMessageId(BadMessage badMessage) {
        if (badMessage.getOriginalMessage() == null || badMessage.getOriginalMessage().isBlank()) return null;
        try {
            return objectMapper.readTree(badMessage.getOriginalMessage()).path("id").asText(null);
        } catch (JsonProcessingException e) {
            logger.warn("TEST_MQTT_BAD_MESSAGE_ID_PARSE_FAILED error={}", badMessage.getError(), e);
            return null;
        }
    }

    private void execute(IotTestReport report, IotTestStartRequest request) {
        long startedNanos = System.nanoTime();
        RunExecution runExecution = activeRuns.get(report.getRunId());
        currentRunExecution.set(runExecution);
        RunContext context = new RunContext(report, request);
        MDC.put("runId", report.getRunId());
        MDC.put("tenantExternalId", report.getTenantExternalId());
        MDC.put("gatewayId", report.getGatewayExternalId());
        report.setStatus(IotTestStatus.RUNNING);
        logger.info("TEST_RUN_START runId={} tenantExternalId={} gatewayId={}",
                report.getRunId(), report.getTenantExternalId(), report.getGatewayExternalId());
        try {
            stage(report, STAGE_PREFLIGHT, () -> preflight(context));
            bootstrapTenantAdministrator(context);
            stage(report, STAGE_REGISTRATION, () -> registration(context));
            stage(report, STAGE_APPROVAL, () -> approval(context));
            stage(report, STAGE_POLICY, () -> notificationPolicy(context));
            stage(report, STAGE_SCHEMA, () -> deviceSchemas(context));
            stage(report, STAGE_KEEPALIVE, () -> keepAlive(context));
            stage(report, STAGE_HEALTH, () -> stateAndHealth(context));
            stage(report, STAGE_INCIDENTS, () -> incidentsAndActions(context));
            stage(report, STAGE_GROUP, () -> groupAndFleetHealth(context));
            stage(report, STAGE_DISCONNECT, () -> disconnection(context));
            stage(report, STAGE_RECOVERY, () -> recovery(context));
            stage(report, STAGE_EXTERNAL, () -> externalNotifications(context));
            stage(report, STAGE_TENANT_ISOLATION, () -> tenantIsolation(context));
            report.setStatus(IotTestStatus.PASSED);
        } catch (AbortException e) {
            report.setStatus(IotTestStatus.ABORTED).setFirstFailure(e.getMessage());
            logger.warn("TEST_RUN_ABORTED runId={} reason={}", report.getRunId(), e.getMessage());
        } catch (Throwable e) {
            report.setStatus(IotTestStatus.FAILED);
            if (report.getFirstFailure() == null) report.setFirstFailure(compactError(e));
            logger.error("TEST_RUN_FAILED runId={} stage={} firstFailure={}",
                    report.getRunId(), report.getCurrentStage(), report.getFirstFailure(), e);
        } finally {
            report.setCompletedAt(OffsetDateTime.now())
                    .setDurationMs(TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startedNanos));
            summarize(report);
            try {
                report.setCurrentStage(STAGE_REPORT);
                writeReports(report);
            } catch (Throwable e) {
                logger.error("TEST_REPORT_WRITE_FAILED runId={}", report.getRunId(), e);
                if (report.getStatus() == IotTestStatus.PASSED) {
                    report.setStatus(IotTestStatus.FAILED);
                    report.setFirstFailure("Report writing failed: " + compactError(e));
                }
                summarize(report);
            }
            logger.info("TEST_RUN_END runId={} status={} passed={} failed={} skipped={} durationMs={} jsonReport={} markdownReport={}",
                    report.getRunId(), report.getStatus(), report.getPassed(), report.getFailed(), report.getSkipped(),
                    report.getDurationMs(), report.getJsonReportPath(), report.getMarkdownReportPath());
            if (runExecution != null) {
                activeRuns.remove(report.getRunId(), runExecution);
            }
            activeGatewayExternalIds.remove(report.getGatewayExternalId(), report.getRunId());
            currentRunExecution.remove();
            MDC.clear();
        }
    }

    private void bootstrapTenantAdministrator(RunContext context) {
        ReentrantLock lock = tenantBootstrapLocks.computeIfAbsent(
                context.report.getTenantExternalId(), ignored -> new ReentrantLock());
        try {
            lock.lockInterruptibly();
            checkAbort();
            stage(context.report, STAGE_TENANT, () -> tenant(context));
            stage(context.report, STAGE_TENANT_ADMIN, () -> tenantAdminSecurity(context));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new AbortException("IoT test interrupted while waiting for tenant bootstrap");
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

    private void preflight(RunContext context) {
        verify(context.report, "PRE-001", "MQTT inbound adapter is available and running", "running adapter",
                () -> basicIOTConnection.getMqttPahoMessageDrivenChannelAdapter(), Objects::nonNull,
                value -> value == null ? "null" : value.getClass().getSimpleName() + ":running=" + value.isRunning());
        verify(context.report, "PRE-001A", "MQTT inbound adapter is running", "true",
                () -> basicIOTConnection.getMqttPahoMessageDrivenChannelAdapter().isRunning(),
                Boolean.TRUE::equals, String::valueOf);
        verify(context.report, "PRE-002", "Public key can be derived for the simulated gateway", "non-blank unwrapped Base64 X.509 public key",
                this::getSimulatedGatewayPublicKey, value -> value != null && !value.isBlank(),
                value -> value == null ? "null" : "available(" + value.length() + " chars)");
        verify(context.report, "PRE-003", "Provisioning administrator is authenticated", "authenticated administrator",
                () -> adminSecurityContext.getUser(), Objects::nonNull,
                value -> value == null ? "null" : value.getId());
        verify(context.report, "PRE-004", "Requested device count is safe", "1.." + maxDeviceCount,
                context.request::getDeviceCount,
                value -> value != null && value > 0 && value <= maxDeviceCount,
                String::valueOf);
        verify(context.report, "PRE-005", "Report directory exists and is writable", "writable directory",
                this::prepareReportDirectory, path -> Files.isDirectory(path) && Files.isWritable(path),
                String::valueOf);
        verify(context.report, "PRE-005A", "Gateway latitude is valid", "-90..90",
                context.request::getLat, value -> value != null && value >= -90 && value <= 90,
                String::valueOf);
        verify(context.report, "PRE-005B", "Gateway longitude is valid", "-180..180",
                context.request::getLon, value -> value != null && value >= -180 && value <= 180,
                String::valueOf);
        if (Boolean.TRUE.equals(context.request.getRequireExternalDelivery())) {
            verify(context.report, "PRE-006", "Live external delivery has a destination", "email and/or WhatsApp destination",
                    () -> hasText(context.request.getEmailDestination()) || hasText(context.request.getWhatsAppDestination()),
                    Boolean.TRUE::equals, String::valueOf);
        }
    }

    private String getSimulatedGatewayPublicKey() {
        String cached = simulatedGatewayPublicKey;
        if (cached != null) {
            return cached;
        }
        if (!(privateKey instanceof RSAPrivateCrtKey rsaPrivateKey)) {
            logger.error("TEST_PREFLIGHT_KEY_DERIVATION_FAILED reason=private-key-is-not-RSA-CRT type={}",
                    privateKey == null ? "null" : privateKey.getClass().getName());
            return null;
        }
        try {
            RSAPublicKeySpec publicKeySpec = new RSAPublicKeySpec(
                    rsaPrivateKey.getModulus(), rsaPrivateKey.getPublicExponent());
            PublicKey publicKey = KeyFactory.getInstance("RSA").generatePublic(publicKeySpec);
            cached = Base64.getEncoder().encodeToString(publicKey.getEncoded());
            simulatedGatewayPublicKey = cached;
            return cached;
        } catch (Exception e) {
            logger.error("TEST_PREFLIGHT_KEY_DERIVATION_FAILED reason=unable-to-derive-public-key", e);
            return null;
        }
    }

    private void tenant(RunContext context) {
        List<SecurityTenant> matching = securityTenantService.listAllTenants(
                new SecurityTenantFilter().setExternalIds(Set.of(context.report.getTenantExternalId())),
                adminSecurityContext);
        if (matching.size() > 1) {
            fail(context.report, "TEN-001", "Resolve test tenant by external ID", "zero or one tenant",
                    String.valueOf(matching.size()), null);
        }
        boolean created = matching.isEmpty();
        context.tenant = created
                ? securityTenantService.createTenant(new SecurityTenantCreate()
                    .setExternalId(context.report.getTenantExternalId())
                    .setName("IoT tester " + context.report.getTenantExternalId())
                    .setDescription("Tenant created by IoT end-to-end tester run " + context.report.getRunId()), adminSecurityContext)
                : matching.get(0);
        pass(context.report, "TEN-001", "Resolve or create test tenant", "one tenant",
                (created ? "created:" : "existing:") + context.tenant.getId());
        context.provisioningSecurityContext = securityContextProvider.getSecurityContext(adminSecurityContext.getUser());
        context.provisioningSecurityContext.setTenantToCreateIn(context.tenant);
        context.report.getCreatedObjectIds().put("tenant", context.tenant.getId());
        verify(context.report, "TEN-002", "Provisioning security context targets the requested tenant", context.tenant.getId(),
                () -> context.provisioningSecurityContext.getTenantToCreateIn(),
                value -> value != null && Objects.equals(value.getId(), context.tenant.getId()),
                value -> value == null ? "null" : value.getId());
        verify(context.report, "TEN-003", "Resolved tenant retains the requested unique external ID",
                context.report.getTenantExternalId(), context.tenant::getExternalId,
                context.report.getTenantExternalId()::equals, String::valueOf);
    }

    private void tenantAdminSecurity(RunContext context) {
        String testUserEmail = tenantAdminEmail(context.report.getTenantExternalId());
        CommonUserFilter userFilter = new CommonUserFilter();
        userFilter.setEmails(Set.of(testUserEmail));
        userFilter.setUserSecurityTenants(List.of(context.tenant));
        List<User> matchingUsers = commonUserService.listAllUsers(userFilter, context.provisioningSecurityContext);
        if (matchingUsers.size() > 1) {
            fail(context.report, "ADM-001", "Resolve tenant administrator user by email", "zero or one user",
                    String.valueOf(matchingUsers.size()), null);
        }
        boolean userCreated = matchingUsers.isEmpty();
        if (userCreated) {
            CommonUserCreate userCreate = new CommonUserCreate();
            userCreate.setTenant(context.tenant);
            userCreate.setEmail(testUserEmail);
            userCreate.setPassword(UUID.randomUUID().toString());
            userCreate.setName("IoT tester tenant administrator");
            userCreate.setDescription("Tenant administrator created by the Basic IoT end-to-end tester");
            context.testUser = commonUserService.createUser(userCreate, context.provisioningSecurityContext);
        } else {
            context.testUser = matchingUsers.get(0);
        }
        pass(context.report, "ADM-001", "Resolve or create tenant administrator user", "one tenant user",
                (userCreated ? "created:" : "existing:") + context.testUser.getId());
        context.report.getCreatedObjectIds().put("testUser", context.testUser.getId());
        verify(context.report, "ADM-002", "Tenant administrator user has the deterministic fake email",
                testUserEmail, context.testUser::getEmail, testUserEmail::equals, String::valueOf);
        verifyTenantCollection(context, "ADM-002A",
                "Tenant administrator user is owned by the requested tenant", List.of(context.testUser));

        CommonUserFilter persistedUserFilter = new CommonUserFilter();
        persistedUserFilter.setUserIds(Set.of(context.testUser.getId()));
        persistedUserFilter.setUserSecurityTenants(List.of(context.tenant));
        verify(context.report, "ADM-003", "Tenant administrator user belongs to the requested tenant",
                context.tenant.getId(),
                () -> commonUserService.listAllUsers(persistedUserFilter, context.provisioningSecurityContext),
                users -> users.size() == 1 && Objects.equals(users.get(0).getId(), context.testUser.getId()),
                users -> users.stream().map(User::getId).collect(Collectors.joining(",")));

        String roleExternalId = tenantAdminRoleExternalId(context.report.getTenantExternalId());
        RoleFilter roleFilter = new RoleFilter();
        roleFilter.setExternalIds(Set.of(roleExternalId));
        roleFilter.setTenants(List.of(context.tenant));
        List<Role> matchingRoles = roleService.listAllRoles(roleFilter, context.provisioningSecurityContext);
        if (matchingRoles.size() > 1) {
            fail(context.report, "ADM-004", "Resolve tenant administrator role by external ID",
                    "zero or one role", String.valueOf(matchingRoles.size()), null);
        }
        boolean roleCreated = matchingRoles.isEmpty();
        if (roleCreated) {
            RoleCreate roleCreate = new RoleCreate();
            roleCreate.setExternalId(roleExternalId);
            roleCreate.setSuperAdmin(false);
            roleCreate.setName("Tenant Administrators");
            roleCreate.setDescription("Tenant-wide administrator role created by the Basic IoT end-to-end tester");
            context.tenantAdminRole = roleService.createRole(roleCreate, context.provisioningSecurityContext);
        } else {
            context.tenantAdminRole = matchingRoles.get(0);
        }
        pass(context.report, "ADM-004", "Resolve or create the tenant administrator role",
                "one role in tenant " + context.tenant.getId(),
                (roleCreated ? "created:" : "existing:") + context.tenantAdminRole.getId());
        context.report.getCreatedObjectIds().put("tenantAdminRole", context.tenantAdminRole.getId());
        verify(context.report, "ADM-005", "Tenant administrator role has the expected tenant and external ID",
                context.tenant.getId() + ":" + roleExternalId,
                () -> context.tenantAdminRole,
                role -> role.getTenant() != null
                        && Objects.equals(role.getTenant().getId(), context.tenant.getId())
                        && Objects.equals(role.getExternalId(), roleExternalId)
                        && !role.isSuperAdmin(),
                role -> (role.getTenant() == null ? "null" : role.getTenant().getId())
                        + ":" + role.getExternalId() + ":superAdmin=" + role.isSuperAdmin());

        RoleToUserFilter roleToUserFilter = new RoleToUserFilter();
        roleToUserFilter.setRoles(List.of(context.tenantAdminRole));
        roleToUserFilter.setUsers(List.of(context.testUser));
        List<RoleToUser> memberships = roleToUserService.listAllRoleToUsers(
                roleToUserFilter, context.provisioningSecurityContext);
        if (memberships.size() > 1) {
            fail(context.report, "ADM-006", "Resolve tenant administrator role membership",
                    "zero or one membership", String.valueOf(memberships.size()), null);
        }
        boolean membershipCreated = memberships.isEmpty();
        if (membershipCreated) {
            RoleToUserCreate membershipCreate = new RoleToUserCreate();
            membershipCreate.setRole(context.tenantAdminRole);
            membershipCreate.setRoleId(context.tenantAdminRole.getId());
            membershipCreate.setSecurityUser(context.testUser);
            membershipCreate.setSecurityUserId(context.testUser.getId());
            membershipCreate.setName("IoT tester tenant administrator membership");
            membershipCreate.setDescription("Assigns the IoT tester user to the tenant administrator role");
            context.tenantAdminMembership = roleToUserService.createRoleToUser(
                    membershipCreate, context.provisioningSecurityContext);
        } else {
            context.tenantAdminMembership = memberships.get(0);
        }
        pass(context.report, "ADM-006", "Tenant administrator user is assigned to the role",
                "exactly one RoleToUser",
                (membershipCreated ? "created:" : "existing:") + context.tenantAdminMembership.getId());
        context.report.getCreatedObjectIds().put("tenantAdminRoleToUser", context.tenantAdminMembership.getId());
        verify(context.report, "ADM-006A", "Role membership links the expected user and tenant administrator role",
                context.testUser.getId() + ":" + context.tenantAdminRole.getId(),
                () -> context.tenantAdminMembership,
                membership -> membership.getUser() != null
                        && Objects.equals(membership.getUser().getId(), context.testUser.getId())
                        && membership.getRole() != null
                        && Objects.equals(membership.getRole().getId(), context.tenantAdminRole.getId()),
                membership -> (membership.getUser() == null ? "null" : membership.getUser().getId())
                        + ":" + (membership.getRole() == null ? "null" : membership.getRole().getId()));
        verifyTenantCollection(context, "ADM-006B",
                "RoleToUser membership is owned by the requested tenant", List.of(context.tenantAdminMembership));

        SecurityOperation allOperation = securityOperationService.getAllOperations();
        verify(context.report, "ADM-007", "FlexiCore standard All operation is available",
                SecurityOperationService.getStandardAccessId(All.class),
                () -> allOperation,
                operation -> operation != null
                        && Objects.equals(operation.getId(), SecurityOperationService.getStandardAccessId(All.class)),
                operation -> operation == null ? "null" : operation.getId());

        Clazz securityWildcard = Clazz.ofName(SECURITY_WILDCARD_CLAZZ_NAME);
        RoleToBaseclassFilter permissionFilter = new RoleToBaseclassFilter();
        permissionFilter.setRoles(List.of(context.tenantAdminRole));
        permissionFilter.setClazzes(List.of(securityWildcard));
        permissionFilter.setOperations(List.of(allOperation));
        List<RoleToBaseclass> permissions = roleToBaseclassService.listAllRoleToBaseclasss(
                permissionFilter, context.provisioningSecurityContext);
        if (permissions.size() > 1) {
            fail(context.report, "ADM-008", "Resolve tenant administrator wildcard permission",
                    "zero or one permission", String.valueOf(permissions.size()), null);
        }
        boolean permissionCreated = permissions.isEmpty();
        if (permissionCreated) {
            RoleToBaseclassCreate permissionCreate = new RoleToBaseclassCreate();
            permissionCreate.setRole(context.tenantAdminRole);
            permissionCreate.setRoleId(context.tenantAdminRole.getId());
            permissionCreate.setClazz(securityWildcard);
            permissionCreate.setOperation(allOperation);
            permissionCreate.setOperationId(allOperation.getId());
            permissionCreate.setAccess(Access.allow);
            permissionCreate.setName("Tenant administrator wildcard permission");
            permissionCreate.setDescription("Allows every operation on the FlexiCore security wildcard");
            context.tenantAdminPermission = roleToBaseclassService.createRoleToBaseclass(
                    permissionCreate, context.provisioningSecurityContext);
        } else {
            context.tenantAdminPermission = permissions.get(0);
        }
        pass(context.report, "ADM-008",
                "Tenant administrator role is allowed all operations on SecurityWildcard",
                "RoleToBaseclass(role, SecurityWildcard, All, ALLOW)",
                (permissionCreated ? "created:" : "existing:") + context.tenantAdminPermission.getId());
        context.report.getCreatedObjectIds().put("tenantAdminWildcardPermission",
                context.tenantAdminPermission.getId());
        verify(context.report, "ADM-008A", "Wildcard permission stores the expected target, subject, operation, and access",
                context.tenantAdminRole.getId() + ":" + SECURITY_WILDCARD_CLAZZ_NAME
                        + ":" + allOperation.getId() + ":" + Access.allow,
                () -> context.tenantAdminPermission,
                permission -> permission.getRole() != null
                        && Objects.equals(permission.getRole().getId(), context.tenantAdminRole.getId())
                        && Objects.equals(permission.getSecuredType(), SECURITY_WILDCARD_CLAZZ_NAME)
                        && Objects.equals(permission.getOperationId(), allOperation.getId())
                        && permission.getAccess() == Access.allow,
                permission -> (permission.getRole() == null ? "null" : permission.getRole().getId())
                        + ":" + permission.getSecuredType()
                        + ":" + permission.getOperationId()
                        + ":" + permission.getAccess());
        verifyTenantCollection(context, "ADM-008B",
                "RoleToBaseclass permission is owned by the requested tenant", List.of(context.tenantAdminPermission));

        SecurityOperation runtimeOperation = resolveRuntimeOperation(context, allOperation);
        context.securityContext = securityContextProvider.getSecurityContext(context.testUser);
        context.securityContext.setTenantToCreateIn(context.tenant);
        context.securityContext.setOperation(runtimeOperation);
        verify(context.report, "ADM-009A",
                "Tenant administrator runtime context is confined to the requested tenant",
                "only " + context.tenant.getId(),
                () -> context.securityContext.getTenants(),
                tenants -> tenants != null && !tenants.isEmpty()
                        && tenants.stream().allMatch(tenant -> tenant != null
                        && Objects.equals(tenant.getId(), context.tenant.getId())),
                tenants -> tenants == null ? "null" : tenants.stream()
                        .map(tenant -> tenant == null ? "null" : tenant.getId())
                        .collect(Collectors.joining(",")));
        context.report.setTestUserId(context.testUser.getId());
        verify(context.report, "ADM-009", "Runtime test security context uses the tenant administrator user",
                context.testUser.getId() + ":" + context.tenant.getId(),
                () -> context.securityContext,
                securityContext -> securityContext.getUser() != null
                        && Objects.equals(securityContext.getUser().getId(), context.testUser.getId())
                        && securityContext.getTenantToCreateIn() != null
                        && Objects.equals(securityContext.getTenantToCreateIn().getId(), context.tenant.getId()),
                securityContext -> (securityContext.getUser() == null ? "null" : securityContext.getUser().getId())
                        + ":" + (securityContext.getTenantToCreateIn() == null
                        ? "null" : securityContext.getTenantToCreateIn().getId()));
        verify(context.report, "ADM-010", "Runtime test security context contains the tenant administrator role",
                context.tenantAdminRole.getId(),
                () -> securityContextRoles(context.securityContext),
                roles -> roles.stream()
                        .anyMatch(role -> Objects.equals(role.getId(), context.tenantAdminRole.getId())),
                roles -> roles.stream().map(Role::getId).collect(Collectors.joining(",")));
    }

    @SuppressWarnings("unchecked")
    private List<Role> securityContextRoles(SecurityContext securityContext) {
        if (securityContext == null || securityContext.getAllRoles() == null) {
            return List.of();
        }
        return (List<Role>) (List<?>) securityContext.getAllRoles();
    }

    private String tenantAdminEmail(String tenantExternalId) {
        String localTenant = safeToken(tenantExternalId).toLowerCase(Locale.ROOT);
        String hash = Integer.toUnsignedString(Objects.hashCode(tenantExternalId), 36);
        return "iot-tester-" + localTenant + "-" + hash + "@example.com";
    }

    private String tenantAdminRoleExternalId(String tenantExternalId) {
        String localTenant = safeToken(tenantExternalId).toLowerCase(Locale.ROOT);
        String hash = Integer.toUnsignedString(Objects.hashCode(tenantExternalId), 36);
        return "iot-tester." + localTenant + "." + hash + ".tenant-admin";
    }

    private void registration(RunContext context) {
        List<Gateway> existingGateways = gatewayService.listAllGateways(null,
                new GatewayFilter().setExternalIds(Set.of(context.report.getGatewayExternalId())));
        verify(context.report, "REG-001", "Gateway identity is unused before registration", "0 existing gateways",
                () -> existingGateways.size(), value -> value == 0, String::valueOf);
        List<PendingGateway> existingPending = pendingGatewayService.listAllPendingGateways(null,
                new PendingGatewayFilter().setExternalIds(Set.of(context.report.getGatewayExternalId())));
        verify(context.report, "REG-002", "Gateway identity is unused in pending registrations", "0 active pending gateways",
                () -> existingPending.stream().filter(f -> f.getRegisteredGateway() == null).count(),
                value -> value == 0L, String::valueOf);

        RegisterGateway request = new RegisterGateway()
                .setTenantExternalId(context.report.getTenantExternalId())
                .setPublicKey(getSimulatedGatewayPublicKey())
                .setNoSignatureCapabilities(false)
                .setLat(context.request.getLat())
                .setLon(context.request.getLon());
        RegisterGatewayReceived response = mqttRequest(context, request, RegisterGatewayReceived.class);
        verify(context.report, "REG-003", "Cloud accepts MQTT gateway registration", RegisterGatewayReceived.STATUS_REGISTERED,
                response::getRegistrationStatus,
                RegisterGatewayReceived.STATUS_REGISTERED::equals, String::valueOf);
        verify(context.report, "REG-004", "Registration response is correlated", request.getId(),
                response::getRegisterGatewayId, request.getId()::equals, String::valueOf);

        context.pendingGateway = waitFor(context, "REG-005", "PendingGateway is persisted in the requested tenant",
                "one pending gateway in tenant " + context.tenant.getId(),
                () -> pendingGatewayService.listAllPendingGateways(context.securityContext,
                        new PendingGatewayFilter().setExternalIds(Set.of(context.report.getGatewayExternalId())))
                        .stream().filter(f -> f.getRegisteredGateway() == null).findFirst().orElse(null),
                value -> value != null && value.getTenant() != null
                        && Objects.equals(value.getTenant().getId(), context.tenant.getId()),
                value -> value == null ? "null" : value.getId());
        context.report.getCreatedObjectIds().put("pendingGateway", context.pendingGateway.getId());
        verify(context.report, "REG-005A", "PendingGateway externalId equals the MQTT gateway identity",
                context.report.getGatewayExternalId(), context.pendingGateway::getExternalId,
                context.report.getGatewayExternalId()::equals, String::valueOf);

        RegisterGatewayReceived repeated = mqttRequest(context, new RegisterGateway()
                .setTenantExternalId(context.report.getTenantExternalId())
                .setPublicKey(getSimulatedGatewayPublicKey())
                .setNoSignatureCapabilities(false), RegisterGatewayReceived.class);
        verify(context.report, "REG-006", "Repeated registration is idempotent while pending",
                RegisterGatewayReceived.STATUS_ALREADY_PENDING,
                repeated::getRegistrationStatus,
                RegisterGatewayReceived.STATUS_ALREADY_PENDING::equals, String::valueOf);
        waitFor(context, "REG-007", "Repeated MQTT registration leaves exactly one active PendingGateway",
                "1 active pending gateway",
                () -> pendingGatewayService.listAllPendingGateways(context.securityContext,
                        new PendingGatewayFilter().setExternalIds(Set.of(context.report.getGatewayExternalId())))
                        .stream().filter(f -> f.getRegisteredGateway() == null).count(),
                value -> value == 1L, String::valueOf);
    }

    private void approval(RunContext context) {
        ApproveGatewaysRequest request = new ApproveGatewaysRequest()
                .setPendingGatewayIds(Set.of(context.pendingGateway.getId()));
        gatewayService.validateFiltering(request, context.securityContext);
        verify(context.report, "APP-000",
                "Gateway approval derives the target tenant from the exact PendingGateway external identity",
                context.tenant.getId(), request::getTargetTenant,
                value -> value != null && Objects.equals(value.getId(), context.tenant.getId()),
                value -> value == null ? "null" : value.getId());
        List<Gateway> approved = gatewayService.approveGateways(context.securityContext, request).getList();
        verify(context.report, "APP-001", "Pending gateway approval creates one gateway", "1",
                approved::size, value -> value == 1, String::valueOf);
        context.gateway = approved.get(0);
        context.report.getCreatedObjectIds().put("gateway", context.gateway.getId());
        verify(context.report, "APP-002", "Approved gateway belongs to the requested tenant", context.tenant.getId(),
                () -> context.gateway.getTenant(),
                value -> value != null && Objects.equals(value.getId(), context.tenant.getId()),
                value -> value == null ? "null" : value.getId());
        verify(context.report, "APP-003", "Approved gateway externalId equals the MQTT gateway identity",
                context.report.getGatewayExternalId(), context.gateway::getExternalId,
                context.report.getGatewayExternalId()::equals, String::valueOf);
        verify(context.report, "APP-003A", "Approved gateway retains the protocol remoteId",
                context.report.getGatewayExternalId(), context.gateway::getRemoteId,
                context.report.getGatewayExternalId()::equals, String::valueOf);
        MappedPOI gatewayMappedPOI = context.gateway.getMappedPOI();
        verify(context.report, "APP-MAP-001", "Approved gateway has a map point", "non-null MappedPOI",
                () -> gatewayMappedPOI, Objects::nonNull,
                value -> value == null ? "null" : value.getId());
        verify(context.report, "APP-MAP-002", "Gateway map point uses the requested latitude and longitude",
                formatLocation(context.request.getLat(), context.request.getLon()),
                () -> gatewayMappedPOI,
                value -> value != null
                        && coordinatesMatch(value.getLat(), context.request.getLat())
                        && coordinatesMatch(value.getLon(), context.request.getLon()),
                value -> value == null ? "null" : formatLocation(value.getLat(), value.getLon()));
        context.report.getCreatedObjectIds().put("gatewayMappedPOI", gatewayMappedPOI.getId());

        RegisterGatewayReceived repeated = mqttRequest(context, new RegisterGateway()
                .setTenantExternalId(context.report.getTenantExternalId())
                .setPublicKey(getSimulatedGatewayPublicKey())
                .setNoSignatureCapabilities(false), RegisterGatewayReceived.class);
        verify(context.report, "APP-004", "Gateway re-registration reports already registered",
                RegisterGatewayReceived.STATUS_ALREADY_REGISTERED,
                repeated::getRegistrationStatus,
                RegisterGatewayReceived.STATUS_ALREADY_REGISTERED::equals, String::valueOf);
        verify(context.report, "APP-005", "Registration response identifies the approved gateway", context.gateway.getId(),
                repeated::getRegisteredGatewayId, context.gateway.getId()::equals, String::valueOf);
    }

    private SecurityOperation resolveRuntimeOperation(RunContext context, SecurityOperation allOperation) {
        try {
            Method method = PendingGatewayController.class.getMethod(
                    "getAllPendingGateways", PendingGatewayFilter.class, SecurityContext.class);
            SecurityOperation runtimeOperation = securityOperationService.getOperation(method);
            verify(context.report, "ADM-007A",
                    "Concrete runtime operation is available for direct protected service calls",
                    "non-All PendingGateway/getAllPendingGateways operation",
                    () -> runtimeOperation,
                    operation -> operation != null
                            && !Objects.equals(operation.getId(), allOperation.getId()),
                    operation -> operation == null
                            ? "null"
                            : operation.getId() + ":" + operation.getName());
            return runtimeOperation;
        } catch (NoSuchMethodException e) {
            fail(context.report, "ADM-007A",
                    "Resolve concrete runtime operation for direct protected service calls",
                    "PendingGatewayController.getAllPendingGateways(PendingGatewayFilter, SecurityContext)",
                    "method not found", e);
            return null;
        }
    }

    private void notificationPolicy(RunContext context) {
        List<HealthNotificationChannelPreferenceCreate> preferences = new ArrayList<>();
        preferences.add(new HealthNotificationChannelPreferenceCreate()
                .setChannel(HealthNotificationChannel.IN_APP)
                .setDeliveryMode(HealthNotificationDeliveryMode.IMMEDIATE)
                .setEnabled(true)
                .setTimeZone("Asia/Jerusalem")
                .setName("IoT tester in-app"));
        if (hasText(context.request.getEmailDestination())) {
            preferences.add(new HealthNotificationChannelPreferenceCreate()
                    .setChannel(HealthNotificationChannel.EMAIL)
                    .setDeliveryMode(HealthNotificationDeliveryMode.IMMEDIATE)
                    .setDestination(context.request.getEmailDestination().trim())
                    .setEnabled(true)
                    .setTimeZone("Asia/Jerusalem")
                    .setName("IoT tester email"));
        }
        if (hasText(context.request.getWhatsAppDestination())) {
            preferences.add(new HealthNotificationChannelPreferenceCreate()
                    .setChannel(HealthNotificationChannel.WHATSAPP)
                    .setDeliveryMode(HealthNotificationDeliveryMode.IMMEDIATE)
                    .setDestination(context.request.getWhatsAppDestination().trim())
                    .setEnabled(true)
                    .setTimeZone("Asia/Jerusalem")
                    .setName("IoT tester WhatsApp"));
        }
        HealthNotificationPolicyCreate create = new HealthNotificationPolicyCreate()
                .setUserId(context.securityContext.getUser().getId())
                .setScopeType(HealthNotificationScopeType.TENANT)
                .setEnabled(true)
                .setMinimumSeverityValue(0)
                .setEscalationOnly(false)
                .setNotifyOnRecovery(true)
                .setNotifyOnIncidentActions(true)
                .setIncidentOnly(false)
                .setChannelPreferences(preferences)
                .setName("IoT tester policy " + context.report.getRunId())
                .setDescription("Run-specific health notification policy");
        healthNotificationPolicyService.validate(create, context.securityContext);
        context.notificationPolicy = healthNotificationPolicyService.create(create, context.securityContext);
        context.report.getCreatedObjectIds().put("notificationPolicy", context.notificationPolicy.getId());
        verify(context.report, "POL-001", "Run-specific notification policy is enabled", "true",
                context.notificationPolicy::isEnabled, Boolean.TRUE::equals, String::valueOf);
        verify(context.report, "POL-002", "Built-in notification channel is configured", "IN_APP immediate",
                () -> context.notificationPolicy.getChannelPreferences().stream()
                        .anyMatch(f -> f.getChannel() == HealthNotificationChannel.IN_APP
                                && f.getDeliveryMode() == HealthNotificationDeliveryMode.IMMEDIATE && f.isEnabled()),
                Boolean.TRUE::equals, String::valueOf);
        createSummaryPolicy(context, HealthNotificationDeliveryMode.HOURLY_SUMMARY, "POL-003");
        createSummaryPolicy(context, HealthNotificationDeliveryMode.DAILY_SUMMARY, "POL-004");
        createSummaryPolicy(context, HealthNotificationDeliveryMode.WEEKLY_SUMMARY, "POL-005");
    }

    private void createSummaryPolicy(RunContext context, HealthNotificationDeliveryMode mode, String testId) {
        HealthNotificationChannelPreferenceCreate preference = new HealthNotificationChannelPreferenceCreate()
                .setChannel(HealthNotificationChannel.IN_APP)
                .setDeliveryMode(mode)
                .setEnabled(true)
                .setLocale("en")
                .setTimeZone("Asia/Jerusalem")
                .setSummaryLocalTime(LocalTime.of(8, 0))
                .setSummaryDayOfWeek(1)
                .setName("IoT tester " + mode);
        HealthNotificationPolicyCreate create = new HealthNotificationPolicyCreate()
                .setUserId(context.securityContext.getUser().getId())
                .setScopeType(HealthNotificationScopeType.TENANT)
                .setEnabled(true)
                .setMinimumSeverityValue(0)
                .setEscalationOnly(false)
                .setNotifyOnRecovery(true)
                .setNotifyOnIncidentActions(true)
                .setIncidentOnly(false)
                .setChannelPreferences(List.of(preference))
                .setName("IoT tester " + mode + " policy " + context.report.getRunId())
                .setDescription("Run-specific scheduling validation policy");
        healthNotificationPolicyService.validate(create, context.securityContext);
        HealthNotificationPolicy policy = healthNotificationPolicyService.create(create, context.securityContext);
        context.summaryPolicies.add(policy);
        context.report.getCreatedObjectIds().put("notificationPolicy:" + mode, policy.getId());
        verify(context.report, testId, mode + " policy is persisted with Asia/Jerusalem scheduling", mode,
                () -> policy.getChannelPreferences().stream().findFirst().orElse(null),
                value -> value != null && value.isEnabled() && value.getDeliveryMode() == mode
                        && "Asia/Jerusalem".equals(value.getTimeZone()),
                value -> value == null ? "null" : value.getDeliveryMode() + ":" + value.getTimeZone());
    }

    private void deviceSchemas(RunContext context) {
        for (int i = 0; i < context.request.getDeviceCount(); i++) {
            String kind = logicalDeviceKind(i);
            String deviceSuffix = i < 3 ? kind : kind + "-" + (i + 1);
            String deviceId = context.report.getGatewayExternalId() + "-" + deviceSuffix;
            String typeExternalId = context.report.getGatewayExternalId() + ".device-type." + kind;
            if (!context.deviceTypeExternalIds.contains(typeExternalId)) {
                context.deviceTypeExternalIds.add(typeExternalId);
            }
            context.expectedDeviceTypeExternalIdByDevice.put(deviceId, typeExternalId);
            String schema = "temperature".equals(kind)
                    ? temperatureSchema(context, typeExternalId, true)
                    : simpleSchema(kind, typeExternalId);
            UpdateStateSchema message = new UpdateStateSchema()
                    .setDeviceId(deviceId)
                    .setDeviceType("IoT tester " + kind)
                    .setDeviceTypeExternalId(typeExternalId)
                    .setVersion(1)
                    .setJsonSchema(schema);
            UpdateStateSchemaReceived response = mqttRequest(context, message, UpdateStateSchemaReceived.class);
            verify(context.report, "SCH-" + String.format("%03d", i + 1),
                    "Cloud acknowledges schema for " + deviceId, message.getId(),
                    response::getUpdateStateSchemaId, message.getId()::equals, String::valueOf);
            context.deviceRemoteIds.add(deviceId);
        }

        context.devices = waitFor(context, "SCH-100", "All MQTT-provisioned devices are persisted exactly once",
                context.deviceRemoteIds.size() + " unique devices",
                () -> deviceService.listAllDevices(context.securityContext,
                        new DeviceFilter().setGatewayIds(Set.of(context.gateway.getId()))
                                .setExternalIds(Set.copyOf(context.deviceRemoteIds))),
                value -> value.size() == context.deviceRemoteIds.size()
                        && value.stream().allMatch(device -> belongsToTenant(device, context.tenant))
                        && value.stream().map(Device::getExternalId).collect(Collectors.toSet()).equals(Set.copyOf(context.deviceRemoteIds))
                        && value.stream().allMatch(device -> Objects.equals(device.getRemoteId(), device.getExternalId())),
                value -> value.stream().map(Device::getExternalId).sorted().toList().toString());
        context.temperatureDevice = context.devices.stream()
                .filter(f -> Objects.equals(f.getRemoteId(), context.report.getGatewayExternalId() + "-temperature"))
                .findFirst().orElseThrow();
        context.report.getCreatedObjectIds().put("temperatureDevice", context.temperatureDevice.getId());
        context.devices.forEach(f -> context.report.getCreatedObjectIds().put("device:" + f.getRemoteId(), f.getId()));
        verify(context.report, "SCH-100A",
                "Every device references the shared logical DeviceType external ID assigned for this run",
                context.expectedDeviceTypeExternalIdByDevice,
                () -> context.devices,
                value -> value.size() == context.deviceRemoteIds.size()
                        && value.stream().allMatch(device -> Objects.equals(
                        context.expectedDeviceTypeExternalIdByDevice.get(device.getExternalId()),
                        device.getDeviceType().getExternalId())),
                value -> value.stream().collect(Collectors.toMap(Device::getExternalId,
                        device -> device.getDeviceType().getExternalId(), (left, right) -> left,
                        LinkedHashMap::new)).toString());

        verify(context.report, "SCH-101", "Temperature device has a persisted schema", "non-null current schema",
                context.temperatureDevice::getCurrentSchema, Objects::nonNull,
                value -> value == null ? "null" : value.getId());
        verify(context.report, "SCH-102", "Schema metadata enables state history by default for the test type", "true",
                context.temperatureDevice::isKeepStateHistory, Boolean.TRUE::equals, String::valueOf);
        verify(context.report, "SCH-103", "Schema metadata creates and assigns a typed health profile", "non-null profile",
                () -> context.temperatureDevice.getDeviceType().getDefaultHealthProfile(), Objects::nonNull,
                value -> value == null ? "null" : value.getExternalId());
        verify(context.report, "SCH-103A", "StateSchema externalId is derived from the DeviceType externalId and version",
                context.temperatureDevice.getDeviceType().getExternalId() + ".schema.1",
                () -> context.temperatureDevice.getCurrentSchema().getExternalId(),
                (context.temperatureDevice.getDeviceType().getExternalId() + ".schema.1")::equals, String::valueOf);
        List<DeviceType> deviceTypes = waitFor(context, "SCH-104",
                "MQTT schema provisioning creates exactly one device type for each external ID",
                context.deviceTypeExternalIds.size(),
                () -> deviceTypeService.listAllDeviceTypes(context.securityContext,
                        new DeviceTypeFilter().setExternalIds(Set.copyOf(context.deviceTypeExternalIds))),
                value -> value.size() == context.deviceTypeExternalIds.size()
                        && value.stream().allMatch(deviceType -> belongsToTenant(deviceType, context.tenant))
                        && value.stream().map(DeviceType::getExternalId).collect(Collectors.toSet())
                        .equals(Set.copyOf(context.deviceTypeExternalIds)),
                value -> value.stream().map(DeviceType::getExternalId).sorted().toList().toString());
        context.report.getCreatedObjectIds().put("deviceTypeCount", String.valueOf(deviceTypes.size()));
        Map<String, Long> expectedDevicesPerType = context.expectedDeviceTypeExternalIdByDevice.values().stream()
                .collect(Collectors.groupingBy(value -> value, LinkedHashMap::new, Collectors.counting()));
        verify(context.report, "SCH-104A",
                "Each logical DeviceType is created once and reused by every device of that type",
                expectedDevicesPerType,
                () -> context.devices,
                value -> value.stream().collect(Collectors.groupingBy(
                        device -> device.getDeviceType().getExternalId(), LinkedHashMap::new, Collectors.counting()))
                        .equals(expectedDevicesPerType),
                value -> value.stream().collect(Collectors.groupingBy(
                        device -> device.getDeviceType().getExternalId(), LinkedHashMap::new, Collectors.counting()))
                        .toString());

        String temperatureTypeExternalId = context.temperatureDevice.getDeviceType().getExternalId();
        UpdateStateSchema replay = new UpdateStateSchema()
                .setDeviceId(context.temperatureDevice.getRemoteId())
                .setDeviceType(context.temperatureDevice.getDeviceType().getName())
                .setDeviceTypeExternalId(temperatureTypeExternalId)
                .setVersion(1)
                .setJsonSchema(temperatureSchema(context, temperatureTypeExternalId, true));
        UpdateStateSchemaReceived replayResponse = mqttRequest(context, replay, UpdateStateSchemaReceived.class);
        verify(context.report, "SCH-105", "Replaying the same schema is acknowledged idempotently", replay.getId(),
                replayResponse::getUpdateStateSchemaId, replay.getId()::equals, String::valueOf);
        waitFor(context, "SCH-106", "Schema replay does not duplicate the device or device type",
                "one device and one device type",
                () -> deviceService.listAllDevices(context.securityContext,
                        new DeviceFilter().setExternalIds(Set.of(context.temperatureDevice.getExternalId()))).size()
                        + ":" + deviceTypeService.listAllDeviceTypes(context.securityContext,
                        new DeviceTypeFilter().setExternalIds(Set.of(temperatureTypeExternalId))).size(),
                "1:1"::equals, String::valueOf);
    }

    private void keepAlive(RunContext context) {
        configureConnectivityHistory(context, false, "KA-HISTORY-001");
        List<ConnectivityChange> withoutHistoryBaseline = waitFor(context, "KA-HISTORY-002",
                "Gateway has one current connectivity row before history recording is enabled",
                "exactly one OFF row",
                () -> connectivityChanges(context),
                value -> value.size() == 1 && value.get(0).getConnectivity() == Connectivity.OFF,
                this::connectivityHistorySummary);
        context.connectivityProjectionIdWithoutHistory = withoutHistoryBaseline.get(0).getId();
        context.connectivityChangeCountWithoutHistory = withoutHistoryBaseline.size();

        for (int i = 0; i < context.request.getKeepAliveCount(); i++) {
            checkAbort();
            mqttSend(context, new KeepAlive().setDeviceIds(Set.copyOf(context.deviceRemoteIds)));
            pass(context.report, "KA-" + String.format("%03d", i + 1),
                    "Keepalive message is published through MQTT", "published", "message " + (i + 1));
            sleep(context.request.getKeepAliveIntervalMs());
        }
        context.gateway = waitFor(context, "KA-100", "Gateway connectivity becomes ON", Connectivity.ON,
                () -> findGateway(context), value -> value != null && connectivity(value) == Connectivity.ON,
                value -> value == null ? "null" : String.valueOf(connectivity(value)));
        context.devices = waitFor(context, "KA-101", "All device connectivity projections become ON", "all ON",
                () -> findDevices(context),
                value -> value.size() == context.deviceRemoteIds.size()
                        && value.stream().allMatch(f -> connectivity(f) == Connectivity.ON),
                value -> connectivitySummary(value));
        verify(context.report, "KA-102", "Gateway last-seen timestamp is maintained", "non-null",
                context.gateway::getLastSeen, Objects::nonNull, String::valueOf);

        List<ConnectivityChange> withoutHistoryAfterOn = waitFor(context, "KA-HISTORY-003",
                "With connectivity history disabled, the ON transition updates the current row without appending a row",
                "same row ID, same row count, current value ON",
                () -> connectivityChanges(context),
                value -> value.size() == context.connectivityChangeCountWithoutHistory
                        && value.stream().anyMatch(change -> Objects.equals(change.getId(), context.connectivityProjectionIdWithoutHistory)
                        && change.getConnectivity() == Connectivity.ON),
                this::connectivityHistorySummary);
        context.report.getCreatedObjectIds().put("gatewayConnectivityRowsWithoutHistory",
                String.valueOf(withoutHistoryAfterOn.size()));

        configureConnectivityHistory(context, true, "KA-HISTORY-004");
        context.connectivityChangeCountWhenHistoryEnabled = withoutHistoryAfterOn.size();
        placeDevicesOnMap(context);
    }

    private void placeDevicesOnMap(RunContext context) {
        List<Device> devices = findDevices(context);
        for (Device device : devices) {
            GeoPoint location = randomPointWithinRadius(context);
            context.deviceLocations.put(device.getRemoteId(), location);
            MapIcon expectedIcon = resolveMapIcon(device, CONNECTED_MAP_STATUS, context);
            context.expectedConnectedIconIds.put(device.getRemoteId(), expectedIcon == null ? null : expectedIcon.getId());

            StateChanged message = stateMessage(device, CONNECTED_MAP_STATUS)
                    .setLatitude(location.lat())
                    .setLongitude(location.lon());
            StateChangedReceived response = mqttRequest(context, message, StateChangedReceived.class);
            verify(context.report, "MAP-STATE-" + safeTestIdToken(device.getRemoteId()),
                    "Cloud acknowledges map telemetry for " + device.getRemoteId(), message.getId(),
                    response::getStateChangedId, message.getId()::equals, String::valueOf);
        }

        context.devices = waitFor(context, "MAP-001",
                "Every generated device has a persisted map point within 200 metres of the gateway",
                devices.size() + " mapped devices within " + DEVICE_LOCATION_RADIUS_METERS + " m",
                () -> findDevices(context),
                value -> value.size() == devices.size()
                        && value.stream().allMatch(device -> hasExpectedLocation(device, context)),
                value -> mappedDeviceSummary(value, context));
        for (Device device : context.devices) {
            context.report.getCreatedObjectIds().put("mappedPOI:" + device.getRemoteId(),
                    device.getMappedPOI().getId());
        }
        verify(context.report, "MAP-002",
                "Connected devices use the map icon resolved for their connected state",
                context.expectedConnectedIconIds,
                () -> context.devices,
                value -> value.stream().allMatch(device ->
                        hasExpectedMapIcon(device, context.expectedConnectedIconIds.get(device.getRemoteId()))),
                this::mappedIconSummary);
        verifyMapIconsOutOfBand(context, "MAP-OOB", context.expectedConnectedIconIds,
                "connected-state");
    }

    private String logicalDeviceKind(int index) {
        return switch (index % 3) {
            case 0 -> "temperature";
            case 1 -> "humidity";
            default -> "motion";
        };
    }

    private String logicalDeviceKind(Device device) {
        String externalId = device.getDeviceType() == null ? null : device.getDeviceType().getExternalId();
        if (externalId != null) {
            if (externalId.endsWith(".device-type.temperature")) return "temperature";
            if (externalId.endsWith(".device-type.humidity")) return "humidity";
            if (externalId.endsWith(".device-type.motion")) return "motion";
        }
        throw new IotTestFailureException("Unsupported tester device type for " + device.getRemoteId()
                + ": " + externalId);
    }

    private StateChanged stateMessage(Device device, String status) {
        StateChanged message = new StateChanged()
                .setDeviceId(device.getRemoteId())
                .setDeviceType(device.getDeviceType().getName())
                .setDeviceTypeExternalId(device.getDeviceType().getExternalId())
                .setVersion("1")
                .setStatus(status);
        switch (logicalDeviceKind(device)) {
            case "temperature" -> message.setValue("temperature", 22.0).setValue("online", true);
            case "humidity" -> message.setValue("value", 50.0);
            case "motion" -> message.setValue("value", false);
            default -> throw new IotTestFailureException("Unsupported tester device type for " + device.getRemoteId());
        }
        return message;
    }

    private void configureConnectivityHistory(RunContext context, boolean enabled, String testId) {
        List<Remote> remotes = new ArrayList<>();
        Gateway gateway = findGateway(context);
        if (gateway != null) {
            remotes.add(gateway);
        }
        remotes.addAll(findDevices(context));
        for (Remote remote : remotes) {
            RemoteUpdate update = new RemoteUpdate();
            update.setId(remote.getId());
            update.setRemote(remote);
            update.setKeepConnectivityHistory(enabled);
            remoteService.updateRemote(update, context.securityContext);
        }
        context.gateway = findGateway(context);
        context.devices = findDevices(context);
        verify(context.report, testId,
                "Typed connectivity history is configured " + enabled + " for the test gateway and devices",
                "gateway and all devices keepConnectivityHistory=" + enabled,
                () -> {
                    List<Remote> persisted = new ArrayList<>();
                    if (context.gateway != null) {
                        persisted.add(context.gateway);
                    }
                    persisted.addAll(context.devices);
                    return persisted;
                },
                value -> value.size() == context.deviceRemoteIds.size() + 1
                        && value.stream().allMatch(remote -> remote.isKeepConnectivityHistory() == enabled),
                value -> value.stream()
                        .map(remote -> remote.getRemoteId() + "=" + remote.isKeepConnectivityHistory())
                        .toList().toString());
    }

    private void stateAndHealth(RunContext context) {
        context.runEventStart = OffsetDateTime.now().minusSeconds(1);
        // Map placement initializes temperature devices at 22.0. Use a different normal
        // value here so this stage always produces a real state transition, regardless
        // of fleet size or how long map placement took.
        sendTemperatureState(context, 20.0, "normal");
        context.temperatureDevice = waitForSeverity(context, "HLT-001", 0, "NORMAL", false);
        verifyTemperatureValue(context, "HLT-002", 20.0);

        sendTemperatureState(context, 35.0, "warning");
        context.temperatureDevice = waitForSeverity(context, "HLT-003", 40, "WARNING", false);

        sendTemperatureState(context, 45.0, "critical");
        context.temperatureDevice = waitForSeverity(context, "HLT-004", 80, "CRITICAL", true);

        List<StateHistory> stateHistory = waitFor(context, "HLT-005", "Accepted state changes are persisted in state history",
                "at least 3 state-history records",
                () -> stateHistory(context), value -> value.size() >= 3,
                value -> "records=" + value.size());
        context.report.getCreatedObjectIds().put("stateHistoryCount", String.valueOf(stateHistory.size()));

        List<RemoteHealthHistory> healthHistory = waitFor(context, "HLT-006", "Severity transitions are persisted in typed health history",
                "warning and critical health-history records",
                () -> requireTenantResults(context, "remote health history query",
                        healthHistoryService.getAllRemoteHealthHistory(context.securityContext,
                                new RemoteHealthHistoryFilter().setRemoteIds(Set.of(context.temperatureDevice.getId()))
                                        .setIntervalStart(context.runEventStart).setPageSize(100)).getList()),
                value -> value.stream().anyMatch(f -> Objects.equals(f.getSeverityValue(), 40))
                        && value.stream().anyMatch(f -> Objects.equals(f.getSeverityValue(), 80)),
                value -> value.stream().map(RemoteHealthHistory::getSeverityValue).toList().toString());
        verify(context.report, "HLT-007", "Critical history contains typed signal evidence", "at least one evidence row",
                () -> healthHistory.stream().filter(f -> Objects.equals(f.getSeverityValue(), 80))
                        .flatMap(f -> f.getSignalEvidence() == null ? java.util.stream.Stream.empty()
                                : f.getSignalEvidence().stream()).count(),
                value -> value > 0, String::valueOf);

        int historyBeforeDisable = stateHistory.size();
        updateTemperatureHistorySetting(context, false, 2, "HLT-HISTORY-001");
        sendTemperatureState(context, 44.0, "history-disabled");
        context.temperatureDevice = waitFor(context, "HLT-HISTORY-002",
                "State and health continue updating while state history is disabled",
                "temperature 44.0 and severity 80",
                () -> findTemperatureDevice(context),
                value -> value != null
                        && value.getDeviceProperties().get("temperature") instanceof Number number
                        && Math.abs(number.doubleValue() - 44.0) < 0.0001
                        && Objects.equals(value.getCurrentSeverityValue(), 80),
                value -> value == null ? "null" : "temperature=" + value.getDeviceProperties().get("temperature")
                        + ", severity=" + value.getCurrentSeverityValue());
        sleep(Math.max(1_000L, context.request.getPollIntervalMs() * 4));
        verify(context.report, "HLT-HISTORY-003",
                "No state-history row is added while history is disabled",
                historyBeforeDisable,
                () -> stateHistory(context).size(),
                value -> value == historyBeforeDisable,
                String::valueOf);

        updateTemperatureHistorySetting(context, true, 3, "HLT-HISTORY-004");
        sendTemperatureState(context, 43.0, "history-reenabled");
        waitFor(context, "HLT-HISTORY-005",
                "State-history persistence resumes after it is re-enabled",
                "more than " + historyBeforeDisable + " rows",
                () -> stateHistory(context).size(),
                value -> value > historyBeforeDisable,
                String::valueOf);

        List<HealthNotificationDelivery> inApp = waitFor(context, "HLT-008", "Critical health event is delivered to the built-in notification channel",
                "delivered HEALTH_CHANGED or INCIDENT_OPENED IN_APP notification",
                () -> notificationDeliveries(context, HealthNotificationChannel.IN_APP),
                value -> value.stream().anyMatch(f -> f.getStatus() == HealthNotificationDeliveryStatus.DELIVERED
                        && f.getHealthNotificationOutbox() != null
                        && (f.getHealthNotificationOutbox().getEventType() == HealthNotificationEventType.HEALTH_CHANGED
                        || f.getHealthNotificationOutbox().getEventType() == HealthNotificationEventType.INCIDENT_OPENED)),
                this::deliverySummary);
        HealthNotificationDelivery criticalNotification = inApp.stream()
                .filter(f -> f.getStatus() == HealthNotificationDeliveryStatus.DELIVERED
                        && f.getHealthNotificationOutbox() != null
                        && (f.getHealthNotificationOutbox().getEventType() == HealthNotificationEventType.HEALTH_CHANGED
                        || f.getHealthNotificationOutbox().getEventType() == HealthNotificationEventType.INCIDENT_OPENED))
                .findFirst().orElseThrow();
        context.report.getCreatedObjectIds().put("criticalInAppNotification", criticalNotification.getId());
        HealthNotificationDelivery marked = healthNotificationService.mark(context.securityContext,
                new MarkHealthNotificationsRequest()
                        .setHealthNotificationDeliveryIds(Set.of(criticalNotification.getId()))
                        .setRead(true)).stream().findFirst().orElseThrow();
        verify(context.report, "HLT-009", "Built-in notification can be marked as read", "non-null readAt",
                marked::getReadAt, Objects::nonNull, String::valueOf);
        validateSummaryScheduling(context, HealthNotificationDeliveryMode.HOURLY_SUMMARY, "HLT-010");
        validateSummaryScheduling(context, HealthNotificationDeliveryMode.DAILY_SUMMARY, "HLT-011");
        validateSummaryScheduling(context, HealthNotificationDeliveryMode.WEEKLY_SUMMARY, "HLT-012");
        context.report.getCreatedObjectIds().put("inAppDeliveryCountAtCritical", String.valueOf(inApp.size()));
    }

    private void incidentsAndActions(RunContext context) {
        List<HealthIncident> openIncidents = waitFor(context, "INC-001",
                "Critical device health opens exactly one action-required incident despite repeated critical states",
                "one open severity-80 incident",
                () -> healthIncidentService.getAll(context.securityContext,
                        new HealthIncidentFilter().setRemoteIds(Set.of(context.temperatureDevice.getId()))
                                .setOpenedAfter(context.runEventStart).setPageSize(100)).getList().stream()
                        .filter(f -> f.getStatus() != HealthIncidentStatus.RESOLVED
                                && f.getStatus() != HealthIncidentStatus.IGNORED).toList(),
                value -> value.size() == 1 && value.get(0).isActionRequired()
                        && Objects.equals(value.get(0).getSeverityValue(), 80),
                value -> value.stream().map(f -> f.getId() + ":" + f.getStatus() + ":" + f.getSeverityValue()).toList().toString());
        context.remoteIncident = openIncidents.get(0);
        context.report.getCreatedObjectIds().put("remoteIncident", context.remoteIncident.getId());

        HealthIncidentAction acknowledged = addIncidentAction(context, "INC-002", HealthIncidentActionType.ACKNOWLEDGED,
                "IoT tester acknowledged the critical temperature");
        verify(context.report, "INC-003", "Acknowledgement records the acting user", context.securityContext.getUser().getId(),
                acknowledged::getPerformedBy,
                value -> value != null && Objects.equals(value.getId(), context.securityContext.getUser().getId()),
                value -> value == null ? "null" : value.getId());
        addIncidentAction(context, "INC-004", HealthIncidentActionType.IN_PROGRESS,
                "IoT tester started investigation");
        addIncidentAction(context, "INC-005", HealthIncidentActionType.REPAIRED,
                "IoT tester applied simulated cooling action");

        context.remoteIncident = waitFor(context, "INC-006", "Incident status tracks user actions", HealthIncidentStatus.IN_PROGRESS,
                () -> findIncident(context.remoteIncident.getId(), context),
                value -> value != null && value.getStatus() == HealthIncidentStatus.IN_PROGRESS
                        && value.getLatestActionSummary() != null,
                value -> value == null ? "null" : value.getStatus() + ":" + value.getLatestActionSummary());

        waitFor(context, "INC-007", "Incident actions create built-in notifications", "incident action notification",
                () -> notificationDeliveries(context, HealthNotificationChannel.IN_APP),
                value -> value.stream().anyMatch(f -> f.getHealthNotificationOutbox() != null
                        && Set.of(HealthNotificationEventType.INCIDENT_ACKNOWLEDGED,
                                HealthNotificationEventType.INCIDENT_UPDATED)
                        .contains(f.getHealthNotificationOutbox().getEventType())),
                this::deliverySummary);
    }

    private void groupAndFleetHealth(RunContext context) {
        FleetHealthRuleConditionCreate condition = new FleetHealthRuleConditionCreate()
                .setMetricType(FleetHealthMetricType.SEVERITY_AT_OR_ABOVE_COUNT)
                .setOperator(HealthComparisonOperator.GE)
                .setThreshold(1.0)
                .setSeverityThresholdValue(60)
                .setName("At least one critical member");
        FleetHealthRuleCreate rule = new FleetHealthRuleCreate()
                .setPriority(100)
                .setEnabled(true)
                .setConditionJoinType(ConditionJoinType.ALL)
                .setResultingSeverityName("CRITICAL")
                .setResultingSeverityValue(80)
                .setHumanInterventionRequired(true)
                .setMinimumStableMillis(0L)
                .setRecoveryStableMillis(0L)
                .setConditions(List.of(condition))
                .setName("critical-member");
        FleetHealthPolicyCreate policyCreate = new FleetHealthPolicyCreate()
                .setExternalId("iot-tester." + context.report.getRunId() + ".fleet-policy")
                .setEnabled(true)
                .setMinimumPopulation(1)
                .setDefaultSeverityName("NORMAL")
                .setDefaultSeverityValue(0)
                .setActionRequiredFromSeverityValue(60)
                .setUnknownPolicy(FleetUnknownPolicy.USE_DEFAULT)
                .setRules(List.of(rule))
                .setName("IoT tester fleet policy " + context.report.getRunId());
        fleetHealthPolicyService.validate(policyCreate, context.securityContext);
        context.fleetHealthPolicy = fleetHealthPolicyService.create(policyCreate, context.securityContext);
        context.report.getCreatedObjectIds().put("fleetHealthPolicy", context.fleetHealthPolicy.getId());
        pass(context.report, "GRP-001", "Fleet health policy is created", "created", context.fleetHealthPolicy.getId());
        verifyTenantCollection(context, "GRP-001A",
                "Fleet health policy is owned by the requested tenant", List.of(context.fleetHealthPolicy));

        RemoteGroupCreate groupCreate = new RemoteGroupCreate()
                .setExternalId("iot-tester." + context.report.getRunId() + ".group")
                .setFleetHealthPolicyId(context.fleetHealthPolicy.getId())
                .setHealthEnabled(true)
                .setName("IoT tester group " + context.report.getRunId());
        remoteGroupService.validate(groupCreate, context.securityContext);
        context.remoteGroup = remoteGroupService.create(groupCreate, context.securityContext);
        context.report.getCreatedObjectIds().put("remoteGroup", context.remoteGroup.getId());
        pass(context.report, "GRP-002", "Remote group is created", "created", context.remoteGroup.getId());
        verifyTenantCollection(context, "GRP-002A",
                "Remote group is owned by the requested tenant", List.of(context.remoteGroup));

        RemoteGroupToRemoteCreate membershipCreate = new RemoteGroupToRemoteCreate()
                .setRemoteGroupId(context.remoteGroup.getId())
                .setRemoteId(context.temperatureDevice.getId())
                .setMembershipAction(RemoteGroupMembershipAction.INCLUDE)
                .setRequiredMember(true)
                .setWeight(1.0)
                .setName("IoT tester critical device membership");
        remoteGroupToRemoteService.validateForCreate(membershipCreate, context.securityContext);
        RemoteGroupToRemote membership = remoteGroupToRemoteService.create(membershipCreate, context.securityContext);
        String membershipId = membership.getId();
        context.report.getCreatedObjectIds().put("remoteGroupMembership", membershipId);
        pass(context.report, "GRP-003", "Critical device is added to the remote group", "created", membershipId);
        verifyTenantCollection(context, "GRP-003A",
                "Remote-group membership is owned by the requested tenant", List.of(membership));

        RemoteGroupHealthSnapshot snapshot = remoteGroupFleetHealthService.evaluate(context.remoteGroup.getId(), context.securityContext);
        verify(context.report, "GRP-004", "Fleet aggregation detects one critical member", "severity 80, population 1",
                () -> snapshot,
                value -> value != null && value.populationCount() == 1
                        && Objects.equals(value.severityValue(), 80) && value.humanInterventionRequired(),
                value -> value == null ? "null" : "severity=" + value.severityValue() + ", population=" + value.populationCount());

        waitFor(context, "GRP-005", "Group health transition is persisted", "critical group history",
                () -> requireTenantResults(context, "remote-group health history query",
                        healthHistoryService.getAllRemoteGroupHealthHistory(context.securityContext,
                                new RemoteGroupHealthHistoryFilter().setRemoteGroupIds(Set.of(context.remoteGroup.getId()))
                                        .setMinimumSeverityValue(80).setPageSize(100)).getList()),
                value -> !value.isEmpty(), value -> "records=" + value.size());

        context.groupIncident = waitFor(context, "GRP-006", "Critical group health opens a group incident",
                "one action-required group incident",
                () -> healthIncidentService.getAll(context.securityContext,
                        new HealthIncidentFilter().setRemoteGroupIds(Set.of(context.remoteGroup.getId()))
                                .setOpenedAfter(context.runEventStart).setPageSize(100)).getList().stream()
                        .findFirst().orElse(null),
                value -> value != null && value.isActionRequired(),
                value -> value == null ? "null" : value.getId() + ":" + value.getStatus());
        context.report.getCreatedObjectIds().put("groupIncident", context.groupIncident.getId());
        addIncidentAction(context, "GRP-007", context.groupIncident, HealthIncidentActionType.ACKNOWLEDGED,
                "IoT tester acknowledged the group incident");
        addIncidentAction(context, "GRP-008", context.groupIncident, HealthIncidentActionType.REPAIRED,
                "IoT tester recorded the group-level remediation");
        context.groupIncident = waitFor(context, "GRP-009", "Group incident status tracks group user actions",
                HealthIncidentStatus.IN_PROGRESS,
                () -> findIncident(context.groupIncident.getId(), context),
                value -> value != null && value.getStatus() == HealthIncidentStatus.IN_PROGRESS
                        && value.getLatestActionSummary() != null,
                value -> value == null ? "null" : value.getStatus() + ":" + value.getLatestActionSummary());
    }

    private void disconnection(RunContext context) {
        sleep(context.request.getDisconnectGraceMs());
        if (Boolean.TRUE.equals(context.request.getAccelerateDisconnect())) {
            OffsetDateTime stale = OffsetDateTime.now().minusDays(2);
            List<Remote> remotes = new ArrayList<>();
            remotes.add(findGateway(context));
            remotes.addAll(findDevices(context));
            for (Remote remote : remotes) {
                if (remote == null) continue;
                remoteService.updateRemote(new RemoteUpdate()
                        .setId(remote.getId())
                        .setRemote(remote)
                        .setLastSeen(stale), context.securityContext);
            }
            pass(context.report, "OFF-001",
                    "Keepalive timestamps are aged only to accelerate the production timeout check",
                    "stale timestamps", stale.toString());
            basicIOTLogic.checkConnectivity();
        } else {
            pass(context.report, "OFF-001", "Gateway simulator stops publishing keepalives and telemetry",
                    "no further MQTT messages until OFF", "waiting for the scheduled production timeout check");
        }
        context.gateway = waitFor(context, "OFF-002", "Production connectivity check marks gateway OFF", Connectivity.OFF,
                () -> findGateway(context), value -> value != null && connectivity(value) == Connectivity.OFF,
                value -> value == null ? "null" : String.valueOf(connectivity(value)));
        context.devices = waitFor(context, "OFF-003", "Production connectivity check marks all devices OFF", "all OFF",
                () -> findDevices(context), value -> value.size() == context.deviceRemoteIds.size()
                        && value.stream().allMatch(f -> connectivity(f) == Connectivity.OFF),
                this::connectivitySummary);
        verify(context.report, "OFF-004", "Gateway connectivity change is timestamped", "non-null connectivity change",
                context.gateway::getLastConnectivityChange, Objects::nonNull,
                value -> value == null ? "null" : value.getId());
        int expectedAfterOff = context.connectivityChangeCountWhenHistoryEnabled + 1;
        List<ConnectivityChange> connectivityChanges = waitFor(context, "OFF-005",
                "With connectivity history enabled, the OFF transition is appended as a new typed history row",
                expectedAfterOff + " rows containing ON and OFF",
                () -> connectivityChanges(context),
                value -> value.size() >= expectedAfterOff
                        && value.stream().anyMatch(f -> f.getConnectivity() == Connectivity.ON)
                        && value.stream().anyMatch(f -> f.getConnectivity() == Connectivity.OFF),
                this::connectivityHistorySummary);
        context.report.getCreatedObjectIds().put("gatewayConnectivityChangeCount", String.valueOf(connectivityChanges.size()));
        Map<String, String> expectedOfflineIcons = defaultMapIconIds(context.devices, context);
        waitFor(context, "OFF-MAP-001",
                "Disconnected devices use their configured default map icon",
                expectedOfflineIcons,
                () -> findDevices(context),
                value -> value.size() == context.deviceRemoteIds.size()
                        && value.stream().allMatch(device ->
                        hasExpectedMapIcon(device, expectedOfflineIcons.get(device.getRemoteId()))),
                this::mappedIconSummary);
        verifyMapIconsOutOfBand(context, "OFF-MAP-OOB", expectedOfflineIcons,
                "disconnected/default");
    }

    private void recovery(RunContext context) {
        sendTemperatureState(context, 22.0, "recovered");
        sendAuxiliaryRecoveryStates(context);
        context.gateway = waitFor(context, "REC-001", "Gateway reconnects after valid MQTT telemetry", Connectivity.ON,
                () -> findGateway(context), value -> value != null && connectivity(value) == Connectivity.ON,
                value -> value == null ? "null" : String.valueOf(connectivity(value)));
        context.devices = waitFor(context, "REC-002", "All devices reconnect after valid MQTT telemetry", "all ON",
                () -> findDevices(context), value -> value.size() == context.deviceRemoteIds.size()
                        && value.stream().allMatch(f -> connectivity(f) == Connectivity.ON),
                this::connectivitySummary);
        Map<String, String> expectedRecoveredIcons = stateMapIconIds(context.devices, RECOVERED_MAP_STATUS, context);
        waitFor(context, "REC-MAP-001",
                "Recovered devices use the map icon resolved for their recovered state",
                expectedRecoveredIcons,
                () -> findDevices(context),
                value -> value.size() == context.deviceRemoteIds.size()
                        && value.stream().allMatch(device ->
                        hasExpectedMapIcon(device, expectedRecoveredIcons.get(device.getRemoteId()))),
                this::mappedIconSummary);
        verifyMapIconsOutOfBand(context, "REC-MAP-OOB", expectedRecoveredIcons,
                "recovered-state");
        int expectedAfterRecovery = context.connectivityChangeCountWhenHistoryEnabled + 2;
        waitFor(context, "REC-002A", "With connectivity history enabled, recovery appends a new ON transition after OFF",
                expectedAfterRecovery + " rows with at least two ON records and one OFF record",
                () -> connectivityChanges(context),
                value -> value.size() >= expectedAfterRecovery
                        && value.stream().filter(f -> f.getConnectivity() == Connectivity.ON).count() >= 2
                        && value.stream().anyMatch(f -> f.getConnectivity() == Connectivity.OFF),
                this::connectivityHistorySummary);

        context.temperatureDevice = waitForSeverity(context, "REC-003", 0, "NORMAL", false);
        context.remoteIncident = waitFor(context, "REC-004", "Open device incident records health recovery", "healthRecovered=true",
                () -> findIncident(context.remoteIncident.getId(), context),
                value -> value != null && value.isHealthRecovered(),
                value -> value == null ? "null" : "status=" + value.getStatus() + ", recovered=" + value.isHealthRecovered());

        addIncidentAction(context, "REC-005", HealthIncidentActionType.RESOLVED,
                "IoT tester verified recovery and resolved the incident");
        context.remoteIncident = waitFor(context, "REC-006", "Resolved user action closes the device incident",
                HealthIncidentStatus.RESOLVED,
                () -> findIncident(context.remoteIncident.getId(), context),
                value -> value != null && value.getStatus() == HealthIncidentStatus.RESOLVED
                        && value.getResolvedBy() != null,
                value -> value == null ? "null" : String.valueOf(value.getStatus()));

        RemoteGroupHealthSnapshot snapshot = remoteGroupFleetHealthService.evaluate(context.remoteGroup.getId(), context.securityContext);
        verify(context.report, "REC-007", "Fleet health returns to normal after device recovery", "severity 0",
                () -> snapshot, value -> value != null && Objects.equals(value.severityValue(), 0)
                        && !value.humanInterventionRequired(),
                value -> value == null ? "null" : "severity=" + value.severityValue());
        context.groupIncident = waitFor(context, "REC-008", "Open group incident records fleet health recovery",
                "healthRecovered=true",
                () -> findIncident(context.groupIncident.getId(), context),
                value -> value != null && value.isHealthRecovered(),
                value -> value == null ? "null" : "status=" + value.getStatus() + ", recovered=" + value.isHealthRecovered());
        addIncidentAction(context, "REC-009", context.groupIncident, HealthIncidentActionType.RESOLVED,
                "IoT tester verified fleet recovery and resolved the group incident");
        context.groupIncident = waitFor(context, "REC-010", "Resolved user action closes the group incident",
                HealthIncidentStatus.RESOLVED,
                () -> findIncident(context.groupIncident.getId(), context),
                value -> value != null && value.getStatus() == HealthIncidentStatus.RESOLVED
                        && value.getResolvedBy() != null,
                value -> value == null ? "null" : String.valueOf(value.getStatus()));

        waitFor(context, "REC-011", "Recovery is delivered to the built-in notification channel", HealthNotificationEventType.HEALTH_RECOVERED,
                () -> notificationDeliveries(context, HealthNotificationChannel.IN_APP),
                value -> value.stream().anyMatch(f -> f.getStatus() == HealthNotificationDeliveryStatus.DELIVERED
                        && f.getHealthNotificationOutbox() != null
                        && f.getHealthNotificationOutbox().getEventType() == HealthNotificationEventType.HEALTH_RECOVERED),
                this::deliverySummary);
    }

    private void externalNotifications(RunContext context) {
        validateExternalChannel(context, HealthNotificationChannel.EMAIL, context.request.getEmailDestination(), "EXT-EMAIL");
        validateExternalChannel(context, HealthNotificationChannel.WHATSAPP, context.request.getWhatsAppDestination(), "EXT-WHATSAPP");
    }

    private void validateExternalChannel(RunContext context, HealthNotificationChannel channel,
                                         String destination, String testPrefix) {
        if (!hasText(destination)) {
            skip(context.report, testPrefix + "-001", channel + " notification pipeline",
                    "destination and tenant provider configuration", "destination not supplied");
            return;
        }
        List<HealthNotificationDelivery> deliveries = waitFor(context, testPrefix + "-001",
                channel + " policy creates delivery records", "at least one delivery",
                () -> notificationDeliveries(context, channel), value -> !value.isEmpty(), this::deliverySummary);
        verify(context.report, testPrefix + "-002", channel + " destination is retained",
                "all delivery destinations match " + maskDestination(destination),
                () -> deliveries.stream().allMatch(f -> Objects.equals(destination.trim(), f.getDestination())),
                Boolean.TRUE::equals, String::valueOf);
        if (!Boolean.TRUE.equals(context.request.getRequireExternalDelivery())) {
            pass(context.report, testPrefix + "-003", channel + " adapter outcome is reported",
                    "delivery record status", deliverySummary(deliveries));
            return;
        }
        waitFor(context, testPrefix + "-003", channel + " live provider accepts a delivery",
                HealthNotificationDeliveryStatus.DELIVERED,
                () -> notificationDeliveries(context, channel),
                value -> value.stream().anyMatch(f -> f.getStatus() == HealthNotificationDeliveryStatus.DELIVERED),
                this::deliverySummary);
    }

    private Device waitForSeverity(RunContext context, String testId, int severityValue,
                                   String severityName, boolean intervention) {
        return waitFor(context, testId, "Device health projection reaches " + severityName,
                severityName + "(" + severityValue + "), intervention=" + intervention,
                () -> findTemperatureDevice(context),
                value -> value != null && Objects.equals(value.getCurrentSeverityValue(), severityValue)
                        && Objects.equals(value.getCurrentSeverityName(), severityName)
                        && value.isHumanInterventionRequired() == intervention
                        && value.getHealthCalculatedAt() != null,
                value -> value == null ? "null" : value.getCurrentSeverityName() + "(" + value.getCurrentSeverityValue()
                        + "), intervention=" + value.isHumanInterventionRequired());
    }

    private void verifyTemperatureValue(RunContext context, String testId, double expected) {
        verify(context.report, testId, "Current device state contains the accepted temperature", String.valueOf(expected),
                () -> findTemperatureDevice(context).getDeviceProperties().get("temperature"),
                value -> value instanceof Number number && Math.abs(number.doubleValue() - expected) < 0.0001,
                String::valueOf);
    }

    private HealthIncidentAction addIncidentAction(RunContext context, String testId,
                                                    HealthIncidentActionType type, String description) {
        return addIncidentAction(context, testId, context.remoteIncident, type, description);
    }

    private HealthIncidentAction addIncidentAction(RunContext context, String testId, HealthIncident incident,
                                                    HealthIncidentActionType type, String description) {
        if (incident == null) {
            throw new IotTestFailureException("Cannot add " + type + " action to a null incident");
        }
        HealthIncidentAction action = healthIncidentService.addAction(new HealthIncidentActionCreate()
                .setHealthIncidentId(incident.getId())
                .setActionType(type)
                .setActionDescription(description)
                .setName("IoT tester " + type), context.securityContext);
        verify(context.report, testId, "User action " + type + " is persisted", type,
                action::getActionType, type::equals, String::valueOf);
        verifyTenantCollection(context, testId + "-TENANT",
                "Incident action is owned by the requested tenant", List.of(action));
        return action;
    }

    private void sendAuxiliaryRecoveryStates(RunContext context) {
        for (Device device : findDevices(context)) {
            if (Objects.equals(device.getId(), context.temperatureDevice.getId())) continue;
            StateChanged message = new StateChanged()
                    .setDeviceId(device.getRemoteId())
                    .setDeviceType(device.getDeviceType().getName())
                    .setDeviceTypeExternalId(device.getDeviceType().getExternalId())
                    .setVersion("1")
                    .setStatus("recovered");
            switch (logicalDeviceKind(device)) {
                case "temperature" -> message.setValue("temperature", 22.0).setValue("online", true);
                case "humidity" -> message.setValue("value", 50.0);
                case "motion" -> message.setValue("value", false);
                default -> throw new IotTestFailureException("Unsupported tester device type for " + device.getRemoteId());
            }
            StateChangedReceived response = mqttRequest(context, message, StateChangedReceived.class);
            verify(context.report, "REC-STATE-" + safeTestIdToken(device.getRemoteId()),
                    "Cloud acknowledges recovery telemetry for " + device.getRemoteId(), message.getId(),
                    response::getStateChangedId, message.getId()::equals, String::valueOf);
        }
    }

    private void sendTemperatureState(RunContext context, double temperature, String status) {
        StateChanged message = new StateChanged()
                .setDeviceId(context.temperatureDevice.getRemoteId())
                .setDeviceType(context.temperatureDevice.getDeviceType().getName())
                .setDeviceTypeExternalId(context.temperatureDevice.getDeviceType().getExternalId())
                .setVersion("1")
                .setStatus(status)
                .setValue("temperature", temperature)
                .setValue("online", true);
        StateChangedReceived response = mqttRequest(context, message, StateChangedReceived.class);
        verify(context.report, "STATE-" + status.toUpperCase(), "Cloud acknowledges " + status + " state",
                message.getId(), response::getStateChangedId, message.getId()::equals, String::valueOf);
        MapIcon expectedIcon = resolveMapIcon(context.temperatureDevice, status, context);
        verifySingleMapIconOutOfBand(context,
                "MAP-STATUS-" + status.toUpperCase(java.util.Locale.ROOT).replaceAll("[^A-Z0-9]+", "-"),
                context.temperatureDevice.getRemoteId(),
                expectedIcon == null ? null : expectedIcon.getId(),
                status);
    }

    private void updateTemperatureHistorySetting(RunContext context, boolean enabled, int version, String testId) {
        String typeExternalId = context.temperatureDevice.getDeviceType().getExternalId();
        UpdateStateSchema message = new UpdateStateSchema()
                .setDeviceId(context.temperatureDevice.getRemoteId())
                .setDeviceType(context.temperatureDevice.getDeviceType().getName())
                .setDeviceTypeExternalId(typeExternalId)
                .setVersion(version)
                .setJsonSchema(temperatureSchema(context, typeExternalId, enabled));
        UpdateStateSchemaReceived response = mqttRequest(context, message, UpdateStateSchemaReceived.class);
        verify(context.report, testId, "Cloud acknowledges schema history setting " + enabled,
                message.getId(), response::getUpdateStateSchemaId, message.getId()::equals, String::valueOf);
        context.temperatureDevice = waitFor(context, testId + "A",
                "Device history setting becomes " + enabled + " through MQTT schema ingestion",
                String.valueOf(enabled),
                () -> findTemperatureDevice(context),
                value -> value != null && value.isKeepStateHistory() == enabled,
                value -> value == null ? "null" : String.valueOf(value.isKeepStateHistory()));
    }

    private <T extends IOTMessage> T mqttRequest(RunContext context, IOTMessage request, Class<T> responseType) {
        checkAbort();
        String requestId = UUID.randomUUID().toString();
        request.setId(requestId).setGatewayId(context.report.getGatewayExternalId()).setSentAt(OffsetDateTime.now());
        BlockingQueue<IOTMessage> queue = new LinkedBlockingQueue<>();
        MqttRequestCorrelation correlation = new MqttRequestCorrelation(
                context.report.getRunId(), context.report.getGatewayExternalId(), responseType, queue);
        MqttRequestCorrelation existing = mqttRequestCorrelations.putIfAbsent(requestId, correlation);
        if (existing != null) {
            throw new IotTestFailureException("Duplicate MQTT request correlation ID " + requestId);
        }
        MqttPahoMessageDrivenChannelAdapter adapter = basicIOTConnection.getMqttPahoMessageDrivenChannelAdapter();
        boolean topicAdded = false;
        try {
            adapter.addTopic(requestId, 1);
            topicAdded = true;
            logger.info("TEST_MQTT_SEND runId={} stage={} gatewayId={} topic={} type={} id={}",
                    context.report.getRunId(), context.report.getCurrentStage(), context.report.getGatewayExternalId(),
                    BasicIOTClient.getOutTopic(context.report.getGatewayExternalId()),
                    request.getClass().getSimpleName(), requestId);
            basicIOTClient.reply(request, BasicIOTClient.getOutTopic(context.report.getGatewayExternalId()));
            long deadline = System.nanoTime() + TimeUnit.MILLISECONDS.toNanos(context.request.getStepTimeoutMs());
            while (System.nanoTime() < deadline) {
                checkAbort();
                IOTMessage response = queue.poll(Math.min(context.request.getPollIntervalMs(), 500L), TimeUnit.MILLISECONDS);
                if (response == null) continue;
                if (responseType.isInstance(response)) return responseType.cast(response);
                if (response instanceof BadMessage badMessage) {
                    fail(context.report, "MQTT-BAD-" + requestId.substring(0, 8),
                            "MQTT request is accepted and verified by the cloud",
                            responseType.getSimpleName(),
                            "BadMessage: " + String.valueOf(badMessage.getError()), null);
                }
                logger.warn("TEST_MQTT_UNEXPECTED_RESPONSE requestId={} expected={} actual={}", requestId,
                        responseType.getName(), response.getClass().getName());
            }
            fail(context.report, "MQTT-TIMEOUT-" + requestId.substring(0, 8),
                    "Cloud returns the expected MQTT response before timeout",
                    responseType.getSimpleName(),
                    "timeout after " + context.request.getStepTimeoutMs() + " ms for request " + requestId, null);
            return null;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new AbortException("IoT test interrupted");
        } catch (JsonProcessingException e) {
            throw new IotTestFailureException("Failed publishing MQTT request " + requestId, e);
        } finally {
            mqttRequestCorrelations.remove(requestId, correlation);
            if (topicAdded) {
                adapter.removeTopic(requestId);
            }
        }
    }

    private void mqttSend(RunContext context, IOTMessage message) {
        checkAbort();
        message.setId(UUID.randomUUID().toString())
                .setGatewayId(context.report.getGatewayExternalId())
                .setSentAt(OffsetDateTime.now());
        try {
            logger.info("TEST_MQTT_SEND runId={} stage={} gatewayId={} topic={} type={} id={}",
                    context.report.getRunId(), context.report.getCurrentStage(), context.report.getGatewayExternalId(),
                    BasicIOTClient.getOutTopic(context.report.getGatewayExternalId()),
                    message.getClass().getSimpleName(), message.getId());
            basicIOTClient.reply(message, BasicIOTClient.getOutTopic(context.report.getGatewayExternalId()));
        } catch (JsonProcessingException e) {
            throw new IotTestFailureException("Failed publishing MQTT message " + message.getId(), e);
        }
    }

    private List<StateHistory> stateHistory(RunContext context) {
        StateHistoryFilter filter = new StateHistoryFilter()
                .setRemoteFilter(new RemoteFilter().setRemoteIds(Set.of(context.temperatureDevice.getRemoteId())))
                .setTimeAtStateFrom(context.runEventStart)
                .setPageSize(500);
        stateHistoryService.validateFiltering(filter, context.securityContext);
        return requireTenantResults(context, "state history query",
                stateHistoryService.listAllStateHistories(context.securityContext, filter));
    }

    private List<ConnectivityChange> connectivityChanges(RunContext context) {
        ConnectivityChangeFilter filter = new ConnectivityChangeFilter();
        filter.setRemoteIds(new java.util.HashSet<>(Set.of(context.gateway.getId())));
        filter.setPageSize(100);
        connectivityChangeService.validateFiltering(filter, context.securityContext);
        return requireTenantResults(context, "connectivity history query",
                connectivityChangeService.listAllConnectivityChanges(context.securityContext, filter));
    }

    private String connectivityHistorySummary(List<ConnectivityChange> changes) {
        return changes.stream()
                .map(change -> change.getId() + ":" + change.getConnectivity())
                .toList().toString();
    }

    private void validateSummaryScheduling(RunContext context, HealthNotificationDeliveryMode mode, String testId) {
        waitFor(context, testId, mode + " delivery is scheduled in the future and remains pending",
                mode + " PENDING with future scheduledAt",
                () -> notificationDeliveries(context, HealthNotificationChannel.IN_APP, mode),
                value -> value.stream().anyMatch(f -> f.getStatus() == HealthNotificationDeliveryStatus.PENDING
                        && f.getScheduledAt() != null
                        && f.getScheduledAt().isAfter(f.getHealthNotificationOutbox().getOccurredAt())),
                this::deliverySummary);
    }

    private Gateway findGateway(RunContext context) {
        List<Gateway> values = requireTenantResults(context, "gateway externalId query",
                gatewayService.listAllGateways(context.securityContext,
                        new GatewayFilter().setExternalIds(Set.of(context.report.getGatewayExternalId()))));
        return values.stream().findFirst().orElse(null);
    }

    private List<Device> findDevices(RunContext context) {
        if (context.gateway == null) return List.of();
        return requireTenantResults(context, "device externalId query",
                deviceService.listAllDevices(context.securityContext,
                        new DeviceFilter().setGatewayIds(Set.of(context.gateway.getId()))
                                .setExternalIds(Set.copyOf(context.deviceRemoteIds))));
    }

    private Device findTemperatureDevice(RunContext context) {
        List<Device> values = requireTenantResults(context, "temperature device externalId query",
                deviceService.listAllDevices(context.securityContext,
                        new DeviceFilter().setExternalIds(Set.of(context.temperatureDevice.getExternalId()))));
        return values.stream().findFirst().orElse(null);
    }

    private HealthIncident findIncident(String id, RunContext context) {
        List<HealthIncident> values = requireTenantResults(context, "health incident id query",
                healthIncidentService.getAll(context.securityContext,
                        new HealthIncidentFilter().setHealthIncidentIds(Set.of(id)).setPageSize(10)).getList());
        return values.stream().findFirst().orElse(null);
    }

    private List<HealthNotificationDelivery> notificationDeliveries(RunContext context,
                                                                     HealthNotificationChannel channel) {
        return notificationDeliveries(context, channel, null);
    }

    private List<HealthNotificationDelivery> notificationDeliveries(RunContext context,
                                                                     HealthNotificationChannel channel,
                                                                     HealthNotificationDeliveryMode mode) {
        HealthNotificationDeliveryFilter filter = new HealthNotificationDeliveryFilter()
                .setUserIds(Set.of(context.securityContext.getUser().getId()))
                .setChannels(Set.of(channel))
                .setOccurredAfter(context.runEventStart == null ? context.report.getStartedAt() : context.runEventStart)
                .setPageSize(500);
        if (mode != null) filter.setDeliveryModes(Set.of(mode));
        healthNotificationService.validateFiltering(filter, context.securityContext);
        Set<String> runPolicyIds = new java.util.HashSet<>();
        if (context.notificationPolicy != null) runPolicyIds.add(context.notificationPolicy.getId());
        context.summaryPolicies.stream().map(HealthNotificationPolicy::getId).forEach(runPolicyIds::add);
        List<HealthNotificationDelivery> values = healthNotificationService.getAll(context.securityContext, filter).getList().stream()
                .filter(delivery -> delivery.getHealthNotificationPolicy() != null
                        && runPolicyIds.contains(delivery.getHealthNotificationPolicy().getId()))
                .toList();
        return requireTenantResults(context, "notification delivery query", values);
    }

    private String temperatureSchema(RunContext context, String typeExternalId, boolean keepStateHistory) {
        Map<String, Object> criticalCondition = new LinkedHashMap<>();
        criticalCondition.put("healthSignal", "environment.temperatureCelsius");
        criticalCondition.put("operator", "GE");
        criticalCondition.put("numericValue", 40);
        criticalCondition.put("priority", 0);
        Map<String, Object> warningCondition = new LinkedHashMap<>();
        warningCondition.put("healthSignal", "environment.temperatureCelsius");
        warningCondition.put("operator", "GE");
        warningCondition.put("numericValue", 30);
        warningCondition.put("priority", 0);

        Map<String, Object> criticalRule = new LinkedHashMap<>();
        criticalRule.put("id", "critical-temperature");
        criticalRule.put("priority", 100);
        criticalRule.put("resultingSeverityName", "CRITICAL");
        criticalRule.put("resultingSeverityValue", 80);
        criticalRule.put("humanInterventionRequired", true);
        criticalRule.put("summary", "Temperature is critical");
        criticalRule.put("mitigationInstructions", "Inspect and cool the device");
        criticalRule.put("conditions", List.of(criticalCondition));

        Map<String, Object> warningRule = new LinkedHashMap<>();
        warningRule.put("id", "warning-temperature");
        warningRule.put("priority", 50);
        warningRule.put("resultingSeverityName", "WARNING");
        warningRule.put("resultingSeverityValue", 40);
        warningRule.put("humanInterventionRequired", false);
        warningRule.put("summary", "Temperature is high");
        warningRule.put("conditions", List.of(warningCondition));

        Map<String, Object> profile = new LinkedHashMap<>();
        profile.put("externalId", typeExternalId + ".health");
        profile.put("name", "IoT tester temperature health " + context.report.getRunId());
        profile.put("enabled", true);
        profile.put("defaultSeverityName", "NORMAL");
        profile.put("defaultSeverityValue", 0);
        profile.put("actionRequiredFromSeverityValue", 60);
        profile.put("rules", List.of(criticalRule, warningRule));

        Map<String, Object> temperature = new LinkedHashMap<>();
        temperature.put("type", "number");
        temperature.put("x-flexicore-property-id", typeExternalId + ".temperature");
        temperature.put("x-flexicore-health-signal", "environment.temperatureCelsius");
        temperature.put("x-flexicore-unit", "C");
        temperature.put("x-flexicore-aggregatable", true);
        Map<String, Object> online = Map.of("type", "boolean");

        Map<String, Object> root = new LinkedHashMap<>();
        root.put("$schema", "https://json-schema.org/draft/2020-12/schema");
        root.put("type", "object");
        root.put("x-flexicore-keep-state-history", keepStateHistory);
        root.put("x-flexicore-health-profile", profile);
        root.put("properties", Map.of("temperature", temperature, "online", online));
        return toJson(root);
    }

    private String simpleSchema(String kind, String typeExternalId) {
        Map<String, Object> value = new LinkedHashMap<>();
        if ("humidity".equals(kind)) {
            value.put("type", "number");
            value.put("minimum", 0);
            value.put("maximum", 100);
        } else if ("motion".equals(kind)) {
            value.put("type", "boolean");
        } else {
            value.put("type", "string");
        }
        value.put("x-flexicore-property-id", typeExternalId + ".value");
        Map<String, Object> root = new LinkedHashMap<>();
        root.put("$schema", "https://json-schema.org/draft/2020-12/schema");
        root.put("type", "object");
        root.put("x-flexicore-keep-state-history", true);
        root.put("properties", Map.of("value", value));
        return toJson(root);
    }

    private String toJson(Object value) {
        try {
            return objectMapper.writeValueAsString(value);
        } catch (JsonProcessingException e) {
            throw new IotTestFailureException("Failed creating test JSON schema", e);
        }
    }

    private Connectivity connectivity(Remote remote) {
        return remote == null || remote.getLastConnectivityChange() == null
                ? null : remote.getLastConnectivityChange().getConnectivity();
    }

    private MapIcon resolveMapIcon(Device device, String status, RunContext context) {
        MapIcon configured = deviceTypeToMapIconService.getMapIconForState(
                device.getDeviceType(), status, context.securityContext);
        return configured != null ? configured : deviceTypeService.getOrCreateMapIcon(
                status,
                device.getDeviceType().getExternalId(),
                device.getDeviceType().getName(),
                device.getClass(),
                context.securityContext);
    }

    private Map<String, String> stateMapIconIds(Collection<Device> devices, String status, RunContext context) {
        Map<String, String> values = new LinkedHashMap<>();
        for (Device device : devices) {
            MapIcon icon = resolveMapIcon(device, status, context);
            values.put(device.getRemoteId(), icon == null ? null : icon.getId());
        }
        return values;
    }

    private Map<String, String> defaultMapIconIds(Collection<Device> devices, RunContext context) {
        Map<String, String> values = new LinkedHashMap<>();
        for (Device device : devices) {
            MapIcon icon = deviceTypeToMapIconService.getDefaultMapIcon(
                    device.getDeviceType(), context.securityContext);
            values.put(device.getRemoteId(), icon == null ? null : icon.getId());
        }
        return values;
    }

    private boolean hasExpectedMapIcon(Device device, String expectedIconId) {
        return expectedIconId != null
                && device.getMappedPOI() != null
                && device.getMappedPOI().getMapIcon() != null
                && expectedIconId.equals(device.getMappedPOI().getMapIcon().getId());
    }

    private void verifyMapIconsOutOfBand(RunContext context, String testPrefix,
                                         Map<String, String> expectedIconIds, String stateDescription) {
        LocationArea rectangle = mapQueryRectangle(context);
        List<MappedPOI> mappedPOIs = waitFor(context, testPrefix + "-RECT",
                "Map-service rectangle query returns every generated device independently of Device.mappedPOI",
                expectedIconIds.size() + " device map points inside " + formatRectangle(rectangle),
                () -> queryDeviceMapPoints(context, rectangle),
                value -> value.size() == expectedIconIds.size()
                        && mappedExternalIds(value).equals(expectedIconIds.keySet()),
                this::mappedPOIOutOfBandSummary);
        waitFor(context, testPrefix + "-ICON",
                "Map-service rectangle query returns the correct " + stateDescription + " icon for every device",
                expectedIconIds,
                () -> queryDeviceMapPoints(context, rectangle),
                value -> expectedIconIds.values().stream().noneMatch(Objects::isNull)
                        && value.size() == expectedIconIds.size()
                        && mappedIconIds(value).equals(expectedIconIds),
                this::mappedPOIOutOfBandSummary);
        if (context.report.getCreatedObjectIds().putIfAbsent("mapQueryRectangle", formatRectangle(rectangle)) == null) {
            context.report.getCreatedObjectIds().put("mapQueryResultCount", String.valueOf(mappedPOIs.size()));
        }
    }

    private void verifySingleMapIconOutOfBand(RunContext context, String testId, String remoteId,
                                              String expectedIconId, String stateDescription) {
        LocationArea rectangle = mapQueryRectangle(context);
        waitFor(context, testId,
                "Map-service rectangle query returns the correct icon after " + stateDescription + " telemetry",
                remoteId + "=" + expectedIconId,
                () -> queryDeviceMapPoints(context, rectangle),
                value -> expectedIconId != null
                        && Objects.equals(mappedIconIds(value).get(remoteId), expectedIconId),
                this::mappedPOIOutOfBandSummary);
    }

    private List<MappedPOI> queryDeviceMapPoints(RunContext context, LocationArea rectangle) {
        MappedPOIFilter filter = new MappedPOIFilter()
                .setLocationArea(rectangle)
                .setExternalId(new HashSet<>(context.deviceRemoteIds))
                .setRelatedType(Set.of(Device.class.getCanonicalName()));
        filter.setHasLocation(true);
        filter.setPageSize(Math.max(1, context.deviceRemoteIds.size()));
        mappedPOIService.validate(filter, context.securityContext);
        return requireTenantResults(context, "map rectangle query",
                mappedPOIService.getAllMappedPOIs(filter, context.securityContext).getList());
    }

    private LocationArea mapQueryRectangle(RunContext context) {
        double halfExtentMeters = DEVICE_LOCATION_RADIUS_METERS + MAP_QUERY_MARGIN_METERS;
        double latitude = context.request.getLat();
        double longitude = context.request.getLon();
        double latitudeDelta = Math.toDegrees(halfExtentMeters / EARTH_RADIUS_METERS);
        double longitudeScale = Math.abs(Math.cos(Math.toRadians(latitude)));
        double longitudeDelta = longitudeScale < 0.000001
                ? 180.0
                : Math.min(180.0, Math.toDegrees(halfExtentMeters / (EARTH_RADIUS_METERS * longitudeScale)));
        return new LocationArea()
                .setLatStart(Math.max(-90.0, latitude - latitudeDelta))
                .setLatEnd(Math.min(90.0, latitude + latitudeDelta))
                .setLonStart(Math.max(-180.0, longitude - longitudeDelta))
                .setLonEnd(Math.min(180.0, longitude + longitudeDelta));
    }

    private Set<String> mappedExternalIds(Collection<MappedPOI> mappedPOIs) {
        return mappedPOIs.stream().map(MappedPOI::getExternalId).collect(Collectors.toSet());
    }

    private Map<String, String> mappedIconIds(Collection<MappedPOI> mappedPOIs) {
        Map<String, String> values = new LinkedHashMap<>();
        for (MappedPOI mappedPOI : mappedPOIs) {
            values.put(mappedPOI.getExternalId(), mappedPOI.getMapIcon() == null
                    ? null : mappedPOI.getMapIcon().getId());
        }
        return values;
    }

    private String mappedPOIOutOfBandSummary(Collection<MappedPOI> mappedPOIs) {
        return mappedPOIs.stream()
                .map(mappedPOI -> mappedPOI.getExternalId() + "="
                        + formatLocation(mappedPOI.getLat(), mappedPOI.getLon())
                        + ":icon=" + (mappedPOI.getMapIcon() == null ? "null"
                        : mappedPOI.getMapIcon().getId() + "/" + mappedPOI.getMapIcon().getExternalId()))
                .sorted()
                .collect(Collectors.joining(", "));
    }

    private String formatRectangle(LocationArea rectangle) {
        return String.format(java.util.Locale.ROOT,
                "[lat %.7f..%.7f, lon %.7f..%.7f]",
                rectangle.getLatStart(), rectangle.getLatEnd(),
                rectangle.getLonStart(), rectangle.getLonEnd());
    }

    private boolean hasExpectedLocation(Device device, RunContext context) {
        MappedPOI mappedPOI = device.getMappedPOI();
        GeoPoint expected = context.deviceLocations.get(device.getRemoteId());
        if (mappedPOI == null || expected == null || mappedPOI.getLat() == null || mappedPOI.getLon() == null) {
            return false;
        }
        return coordinatesMatch(mappedPOI.getLat(), expected.lat())
                && coordinatesMatch(mappedPOI.getLon(), expected.lon())
                && distanceMeters(context.request.getLat(), context.request.getLon(),
                mappedPOI.getLat(), mappedPOI.getLon()) <= DEVICE_LOCATION_RADIUS_METERS + 0.5;
    }

    private GeoPoint randomPointWithinRadius(RunContext context) {
        double distance = Math.sqrt(context.locationRandom.nextDouble()) * DEVICE_LOCATION_RADIUS_METERS;
        double bearing = context.locationRandom.nextDouble() * Math.PI * 2;
        double angularDistance = distance / EARTH_RADIUS_METERS;
        double startLat = Math.toRadians(context.request.getLat());
        double startLon = Math.toRadians(context.request.getLon());
        double resultLat = Math.asin(Math.sin(startLat) * Math.cos(angularDistance)
                + Math.cos(startLat) * Math.sin(angularDistance) * Math.cos(bearing));
        double resultLon = startLon + Math.atan2(
                Math.sin(bearing) * Math.sin(angularDistance) * Math.cos(startLat),
                Math.cos(angularDistance) - Math.sin(startLat) * Math.sin(resultLat));
        double normalizedLon = (Math.toDegrees(resultLon) + 540) % 360 - 180;
        return new GeoPoint(Math.toDegrees(resultLat), normalizedLon);
    }

    private double distanceMeters(double fromLat, double fromLon, double toLat, double toLon) {
        double latDistance = Math.toRadians(toLat - fromLat);
        double lonDistance = Math.toRadians(toLon - fromLon);
        double fromLatRadians = Math.toRadians(fromLat);
        double toLatRadians = Math.toRadians(toLat);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(fromLatRadians) * Math.cos(toLatRadians)
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        return EARTH_RADIUS_METERS * 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
    }

    private boolean coordinatesMatch(Double actual, Double expected) {
        return actual != null && expected != null && Math.abs(actual - expected) <= LOCATION_TOLERANCE;
    }

    private String formatLocation(Double lat, Double lon) {
        if (lat == null || lon == null) return "null";
        return String.format(java.util.Locale.ROOT, "%.7f, %.7f", lat, lon);
    }

    private String mappedDeviceSummary(Collection<Device> devices, RunContext context) {
        return devices.stream().map(device -> {
            MappedPOI mappedPOI = device.getMappedPOI();
            if (mappedPOI == null) return device.getRemoteId() + "=unmapped";
            String distance = mappedPOI.getLat() == null || mappedPOI.getLon() == null
                    ? "unknown"
                    : String.format(java.util.Locale.ROOT, "%.2fm", distanceMeters(
                    context.request.getLat(), context.request.getLon(), mappedPOI.getLat(), mappedPOI.getLon()));
            String icon = mappedPOI.getMapIcon() == null ? "null" : mappedPOI.getMapIcon().getId();
            return device.getRemoteId() + "=" + formatLocation(mappedPOI.getLat(), mappedPOI.getLon())
                    + ":" + distance + ":icon=" + icon;
        }).sorted().collect(Collectors.joining(", "));
    }

    private String mappedIconSummary(Collection<Device> devices) {
        return devices.stream()
                .map(device -> device.getRemoteId() + "="
                        + (device.getMappedPOI() == null || device.getMappedPOI().getMapIcon() == null
                        ? "null" : device.getMappedPOI().getMapIcon().getId()))
                .sorted().collect(Collectors.joining(", "));
    }

    private String connectivitySummary(Collection<? extends Remote> remotes) {
        return remotes.stream().map(f -> f.getRemoteId() + "=" + connectivity(f))
                .sorted().collect(Collectors.joining(", "));
    }

    private String deliverySummary(List<HealthNotificationDelivery> deliveries) {
        return deliveries.stream()
                .map(f -> f.getChannel() + ":" + f.getStatus() + ":" +
                        (f.getHealthNotificationOutbox() == null ? "no-event" : f.getHealthNotificationOutbox().getEventType()))
                .collect(Collectors.joining(", "));
    }

    private void stage(IotTestReport report, String stage, ThrowingRunnable runnable) {
        checkAbort();
        report.setCurrentStage(stage);
        MDC.put("stage", stage);
        long started = System.nanoTime();
        logger.info("TEST_STAGE_START runId={} stage={}", report.getRunId(), stage);
        try {
            runnable.run();
            logger.info("TEST_STAGE_END runId={} stage={} status=PASSED durationMs={}",
                    report.getRunId(), stage, TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - started));
        } catch (Throwable e) {
            logger.error("TEST_STAGE_END runId={} stage={} status=FAILED durationMs={} error={}",
                    report.getRunId(), stage, TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - started), compactError(e), e);
            if (!(e instanceof IotTestFailureException) && !(e instanceof AbortException)) {
                recordUnexpectedStageFailure(report, stage, e);
            }
            if (e instanceof RuntimeException runtimeException) throw runtimeException;
            if (e instanceof Error error) throw error;
            throw new IotTestFailureException("Stage " + stage + " failed", e);
        }
    }

    private void recordUnexpectedStageFailure(IotTestReport report, String stage, Throwable error) {
        String actual = compactError(error);
        IotTestAssertion assertion = new IotTestAssertion()
                .setTestId("STAGE-" + stage)
                .setStage(stage)
                .setDescription("Stage completes without an unexpected exception")
                .setExpected("completed")
                .setActual(actual)
                .setStatus(IotTestAssertionStatus.FAILED)
                .setStartedAt(OffsetDateTime.now())
                .setCompletedAt(OffsetDateTime.now())
                .setDurationMs(0)
                .setError(actual);
        report.getAssertions().add(assertion);
        if (report.getFirstFailure() == null) {
            report.setFirstFailure(actual);
        }
        logger.error("TEST_ASSERT_FAIL runId={} stage={} testId={} expected={} actual={} error={}",
                report.getRunId(), stage, assertion.getTestId(), assertion.getExpected(), actual, actual);
    }

    private void tenantIsolation(RunContext context) {
        verify(context.report, "TISO-001",
                "Runtime writes are confined to the requested tenant",
                context.tenant.getId(),
                () -> context.securityContext,
                securityContext -> securityContext != null
                        && securityContext.getTenantToCreateIn() != null
                        && Objects.equals(securityContext.getTenantToCreateIn().getId(), context.tenant.getId())
                        && securityContext.getTenants() != null
                        && !securityContext.getTenants().isEmpty()
                        && securityContext.getTenants().stream().allMatch(tenant -> tenant != null
                        && Objects.equals(tenant.getId(), context.tenant.getId())),
                securityContext -> securityContext == null ? "null"
                        : "createIn=" + tenantId(securityContext.getTenantToCreateIn())
                        + ", readable=" + (securityContext.getTenants() == null ? "null"
                        : securityContext.getTenants().stream()
                        .map(this::tenantId).collect(Collectors.joining(","))));

        List<Baseclass> directObjects = new ArrayList<>();
        addIfNotNull(directObjects, context.testUser);
        addIfNotNull(directObjects, context.tenantAdminRole);
        addIfNotNull(directObjects, context.tenantAdminMembership);
        addIfNotNull(directObjects, context.tenantAdminPermission);
        addIfNotNull(directObjects, context.pendingGateway);
        addIfNotNull(directObjects, context.gateway);
        directObjects.addAll(context.devices);
        addIfNotNull(directObjects, context.notificationPolicy);
        directObjects.addAll(context.summaryPolicies);
        addIfNotNull(directObjects, context.remoteIncident);
        addIfNotNull(directObjects, context.fleetHealthPolicy);
        addIfNotNull(directObjects, context.remoteGroup);
        addIfNotNull(directObjects, context.groupIncident);
        context.devices.stream().map(Device::getCurrentSchema).filter(Objects::nonNull).forEach(directObjects::add);
        verifyTenantCollection(context, "TISO-002",
                "Every directly created or resolved test object belongs to the requested tenant", directObjects);

        CommonUserFilter globalUserFilter = new CommonUserFilter();
        globalUserFilter.setEmails(Set.of(tenantAdminEmail(context.report.getTenantExternalId())));
        verifyTenantMatches(context, "TISO-003",
                "The deterministic tester user exists only in the requested tenant", 1,
                commonUserService.listAllUsers(globalUserFilter, adminSecurityContext));

        RoleFilter globalRoleFilter = new RoleFilter();
        globalRoleFilter.setExternalIds(Set.of(tenantAdminRoleExternalId(context.report.getTenantExternalId())));
        verifyTenantMatches(context, "TISO-004",
                "The tenant administrator role externalId resolves only in the requested tenant", 1,
                roleService.listAllRoles(globalRoleFilter, adminSecurityContext));

        verifyTenantMatches(context, "TISO-005",
                "The randomized gateway externalId resolves only in the requested tenant", 1,
                gatewayService.listAllGateways(adminSecurityContext,
                        new GatewayFilter().setExternalIds(Set.of(context.report.getGatewayExternalId()))));

        DeviceFilter globalDeviceFilter = new DeviceFilter();
        globalDeviceFilter.setExternalIds(Set.copyOf(context.deviceRemoteIds));
        globalDeviceFilter.setPageSize(Math.max(1, context.deviceRemoteIds.size()));
        verifyTenantMatches(context, "TISO-006",
                "Every run-specific device externalId resolves only in the requested tenant",
                context.deviceRemoteIds.size(),
                deviceService.listAllDevices(adminSecurityContext, globalDeviceFilter));

        Set<String> deviceTypeExternalIds = new HashSet<>(context.deviceTypeExternalIds);
        DeviceTypeFilter globalDeviceTypeFilter = new DeviceTypeFilter();
        globalDeviceTypeFilter.setExternalIds(deviceTypeExternalIds);
        globalDeviceTypeFilter.setPageSize(Math.max(1, deviceTypeExternalIds.size()));
        verifyTenantMatches(context, "TISO-007",
                "Every run-specific device-type externalId resolves only in the requested tenant",
                deviceTypeExternalIds.size(),
                deviceTypeService.listAllDeviceTypes(adminSecurityContext, globalDeviceTypeFilter));

        MappedPOIFilter mappedPOIFilter = new MappedPOIFilter()
                .setExternalId(new HashSet<>(context.deviceRemoteIds))
                .setRelatedType(Set.of(Device.class.getCanonicalName()));
        mappedPOIFilter.setPageSize(Math.max(1, context.deviceRemoteIds.size()));
        mappedPOIService.validate(mappedPOIFilter, adminSecurityContext);
        List<MappedPOI> mappedPOIs = mappedPOIService.getAllMappedPOIs(mappedPOIFilter, adminSecurityContext).getList();
        verifyTenantMatches(context, "TISO-008",
                "Every run-specific mapped POI externalId resolves only in the requested tenant",
                context.deviceRemoteIds.size(), mappedPOIs);
        verifyTenantCollection(context, "TISO-009",
                "Every map icon returned for this run belongs to the requested tenant",
                mappedPOIs.stream().map(MappedPOI::getMapIcon).toList());
    }

    private <T extends Baseclass> List<T> requireTenantResults(RunContext context, String source, List<T> values) {
        List<T> wrongTenant = values.stream()
                .filter(value -> !belongsToTenant(value, context.tenant))
                .toList();
        if (!wrongTenant.isEmpty()) {
            throw new IotTestFailureException("Tenant isolation violation in " + source
                    + "; expected tenant=" + context.tenant.getId()
                    + "; actual=" + tenantObjectSummary(wrongTenant));
        }
        return values;
    }

    private void verifyTenantMatches(RunContext context, String testId, String description,
                                     int expectedCount, List<? extends Baseclass> values) {
        verify(context.report, testId, description,
                expectedCount + " objects in tenant " + context.tenant.getId(),
                () -> values,
                result -> result.size() == expectedCount
                        && result.stream().allMatch(value -> belongsToTenant(value, context.tenant)),
                this::tenantObjectSummary);
    }

    private void verifyTenantCollection(RunContext context, String testId, String description,
                                        Collection<? extends Baseclass> values) {
        verify(context.report, testId, description,
                "all objects in tenant " + context.tenant.getId(),
                () -> values,
                result -> result.stream().allMatch(value -> belongsToTenant(value, context.tenant)),
                this::tenantObjectSummary);
    }

    private boolean belongsToTenant(Baseclass value, SecurityTenant tenant) {
        return value != null && value.getTenant() != null
                && Objects.equals(value.getTenant().getId(), tenant.getId());
    }

    private String tenantObjectSummary(Collection<? extends Baseclass> values) {
        return values.stream()
                .map(value -> value == null ? "null"
                        : value.getClass().getSimpleName() + ":" + value.getId()
                        + "@" + tenantId(value.getTenant()))
                .collect(Collectors.joining(","));
    }

    private String tenantId(SecurityTenant tenant) {
        return tenant == null ? "null" : tenant.getId();
    }

    private void addIfNotNull(Collection<Baseclass> values, Baseclass value) {
        if (value != null) {
            values.add(value);
        }
    }

    private <T> T verify(IotTestReport report, String testId, String description, Object expected,
                         Supplier<T> supplier, Predicate<T> predicate, java.util.function.Function<T, String> formatter) {
        checkAbort();
        long started = System.nanoTime();
        OffsetDateTime startedAt = OffsetDateTime.now();
        try {
            T value = supplier.get();
            String actual = formatter.apply(value);
            if (!predicate.test(value)) {
                fail(report, testId, description, String.valueOf(expected), actual, null);
            }
            pass(report, testId, description, String.valueOf(expected), actual, startedAt, started);
            return value;
        } catch (IotTestFailureException | AbortException e) {
            throw e;
        } catch (Throwable e) {
            fail(report, testId, description, String.valueOf(expected), null, e);
            return null;
        }
    }

    private <T> T waitFor(RunContext context, String testId, String description, Object expected,
                          Supplier<T> supplier, Predicate<T> predicate,
                          java.util.function.Function<T, String> formatter) {
        checkAbort();
        long started = System.nanoTime();
        OffsetDateTime startedAt = OffsetDateTime.now();
        T latest = null;
        Throwable lastError = null;
        long deadline = System.nanoTime() + TimeUnit.MILLISECONDS.toNanos(context.request.getStepTimeoutMs());
        while (System.nanoTime() < deadline) {
            checkAbort();
            try {
                latest = supplier.get();
                if (predicate.test(latest)) {
                    pass(context.report, testId, description, String.valueOf(expected), formatter.apply(latest), startedAt, started);
                    return latest;
                }
            } catch (Throwable e) {
                lastError = e;
            }
            sleep(context.request.getPollIntervalMs());
        }
        fail(context.report, testId, description, String.valueOf(expected),
                latest == null ? "null" : formatter.apply(latest), lastError);
        return latest;
    }

    private void pass(IotTestReport report, String testId, String description, Object expected, Object actual) {
        pass(report, testId, description, String.valueOf(expected), String.valueOf(actual),
                OffsetDateTime.now(), System.nanoTime());
    }

    private void pass(IotTestReport report, String testId, String description, String expected, String actual,
                      OffsetDateTime startedAt, long startedNanos) {
        IotTestAssertion assertion = new IotTestAssertion()
                .setTestId(testId).setStage(report.getCurrentStage()).setDescription(description)
                .setExpected(expected).setActual(actual).setStatus(IotTestAssertionStatus.PASSED)
                .setStartedAt(startedAt).setCompletedAt(OffsetDateTime.now())
                .setDurationMs(TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startedNanos));
        report.getAssertions().add(assertion);
        logger.info("TEST_ASSERT_PASS runId={} stage={} testId={} expected={} actual={} durationMs={}",
                report.getRunId(), report.getCurrentStage(), testId, expected, actual, assertion.getDurationMs());
    }

    private void skip(IotTestReport report, String testId, String description, Object expected, Object actual) {
        IotTestAssertion assertion = new IotTestAssertion()
                .setTestId(testId).setStage(report.getCurrentStage()).setDescription(description)
                .setExpected(String.valueOf(expected)).setActual(String.valueOf(actual))
                .setStatus(IotTestAssertionStatus.SKIPPED)
                .setStartedAt(OffsetDateTime.now()).setCompletedAt(OffsetDateTime.now()).setDurationMs(0);
        report.getAssertions().add(assertion);
        logger.info("TEST_ASSERT_SKIP runId={} stage={} testId={} expected={} actual={}",
                report.getRunId(), report.getCurrentStage(), testId, expected, actual);
    }

    private void fail(IotTestReport report, String testId, String description,
                      String expected, String actual, Throwable error) {
        String failure = description + "; expected=" + expected + "; actual=" + actual
                + (error == null ? "" : "; error=" + compactError(error));
        IotTestAssertion assertion = new IotTestAssertion()
                .setTestId(testId).setStage(report.getCurrentStage()).setDescription(description)
                .setExpected(expected).setActual(actual).setStatus(IotTestAssertionStatus.FAILED)
                .setStartedAt(OffsetDateTime.now()).setCompletedAt(OffsetDateTime.now()).setDurationMs(0)
                .setError(error == null ? null : compactError(error));
        report.getAssertions().add(assertion);
        if (report.getFirstFailure() == null) report.setFirstFailure(failure);
        logger.error("TEST_ASSERT_FAIL runId={} stage={} testId={} expected={} actual={} error={}",
                report.getRunId(), report.getCurrentStage(), testId, expected, actual,
                error == null ? null : compactError(error), error);
        throw new IotTestFailureException(failure, error);
    }

    private void summarize(IotTestReport report) {
        report.setPassed((int) report.getAssertions().stream()
                .filter(f -> f.getStatus() == IotTestAssertionStatus.PASSED).count());
        report.setFailed((int) report.getAssertions().stream()
                .filter(f -> f.getStatus() == IotTestAssertionStatus.FAILED).count());
        report.setSkipped((int) report.getAssertions().stream()
                .filter(f -> f.getStatus() == IotTestAssertionStatus.SKIPPED).count());
    }

    private void writeReports(IotTestReport report) throws IOException {
        Path directory = Path.of(reportDirectory);
        Files.createDirectories(directory);
        Path json = directory.resolve("iot-test-" + report.getRunId() + ".json");
        Path markdown = directory.resolve("iot-test-" + report.getRunId() + ".md");
        report.setJsonReportPath(json.toString()).setMarkdownReportPath(markdown.toString());
        byte[] bytes = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsBytes(report);
        Files.write(json, bytes, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        Files.writeString(markdown, markdown(report), StandardCharsets.UTF_8,
                StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
    }

    private String markdown(IotTestReport report) {
        StringBuilder value = new StringBuilder();
        value.append("# Basic IoT end-to-end test report\n\n")
                .append("- Run ID: `").append(report.getRunId()).append("`\n")
                .append("- Status: **").append(report.getStatus()).append("**\n")
                .append("- Tenant external ID: `").append(report.getTenantExternalId()).append("`\n")
                .append("- Gateway external ID: `").append(report.getGatewayExternalId()).append("`\n")
                .append("- Gateway location: `").append(formatLocation(report.getGatewayLat(), report.getGatewayLon())).append("`\n")
                .append("- Requested by user ID: `").append(report.getRequestedByUserId()).append("`\n")
                .append("- Test user ID: `").append(report.getTestUserId()).append("`\n")
                .append("- Started: ").append(report.getStartedAt()).append("\n")
                .append("- Completed: ").append(report.getCompletedAt()).append("\n")
                .append("- Duration: ").append(report.getDurationMs()).append(" ms\n")
                .append("- Passed: ").append(report.getPassed()).append("\n")
                .append("- Failed: ").append(report.getFailed()).append("\n")
                .append("- Skipped: ").append(report.getSkipped()).append("\n\n");
        if (report.getFirstFailure() != null) {
            value.append("## First failure\n\n`").append(escapeMarkdown(report.getFirstFailure())).append("`\n\n");
        }
        value.append("## Created objects\n\n| Type | ID |\n|---|---|\n");
        report.getCreatedObjectIds().entrySet().stream().sorted(Map.Entry.comparingByKey())
                .forEach(f -> value.append('|').append(escapeMarkdown(f.getKey())).append('|')
                        .append(escapeMarkdown(f.getValue())).append("|\n"));
        value.append("\n## Assertions\n\n| Stage | Test | Status | Description | Expected | Actual | Duration ms |\n")
                .append("|---|---|---|---|---|---|---:|\n");
        for (IotTestAssertion assertion : report.getAssertions()) {
            value.append('|').append(escapeMarkdown(assertion.getStage()))
                    .append('|').append(escapeMarkdown(assertion.getTestId()))
                    .append('|').append(assertion.getStatus())
                    .append('|').append(escapeMarkdown(assertion.getDescription()))
                    .append('|').append(escapeMarkdown(assertion.getExpected()))
                    .append('|').append(escapeMarkdown(assertion.getActual()))
                    .append('|').append(assertion.getDurationMs()).append("|\n");
        }
        return value.toString();
    }

    private String escapeMarkdown(String value) {
        return value == null ? "" : value.replace("|", "\\|").replace("\n", " ");
    }

    private IotTestStartRequest normalize(IotTestStartRequest supplied) {
        IotTestStartRequest value = supplied == null ? new IotTestStartRequest() : supplied;
        if (value.getLat() == null && value.getLon() == null) {
            value.setLat(DEFAULT_GATEWAY_LAT).setLon(DEFAULT_GATEWAY_LON);
        }
        if (value.getDeviceCount() == null) value.setDeviceCount(3);
        if (value.getStepTimeoutMs() == null || value.getStepTimeoutMs() < 1_000) value.setStepTimeoutMs(30_000L);
        if (value.getPollIntervalMs() == null || value.getPollIntervalMs() < 25) value.setPollIntervalMs(250L);
        if (value.getKeepAliveCount() == null || value.getKeepAliveCount() < 1) value.setKeepAliveCount(3);
        if (value.getKeepAliveIntervalMs() == null || value.getKeepAliveIntervalMs() < 0) value.setKeepAliveIntervalMs(1_000L);
        if (value.getAccelerateDisconnect() == null) value.setAccelerateDisconnect(true);
        if (value.getDisconnectGraceMs() == null || value.getDisconnectGraceMs() < 0) value.setDisconnectGraceMs(1_000L);
        if (value.getRequireExternalDelivery() == null) value.setRequireExternalDelivery(false);
        return value;
    }

    private Path prepareReportDirectory() {
        Path directory = Path.of(reportDirectory);
        try {
            return Files.createDirectories(directory);
        } catch (IOException e) {
            throw new IotTestFailureException("Cannot create IoT tester report directory " + directory, e);
        }
    }

    private long lastModified(Path path) {
        try {
            return Files.getLastModifiedTime(path).toMillis();
        } catch (IOException e) {
            return Long.MIN_VALUE;
        }
    }


    private String maskDestination(String value) {
        if (value == null || value.isBlank()) return "not supplied";
        String trimmed = value.trim();
        int at = trimmed.indexOf('@');
        if (at > 0) {
            String local = trimmed.substring(0, at);
            String domain = trimmed.substring(at + 1);
            String maskedLocal = local.length() <= 2 ? "**" : local.substring(0, 1) + "***" + local.substring(local.length() - 1);
            return maskedLocal + "@" + domain;
        }
        String digits = trimmed.replaceAll("[^0-9+]", "");
        if (digits.length() <= 4) return "****";
        return "***" + digits.substring(digits.length() - 4);
    }

    private String safeFileToken(String value) {
        return value == null ? "" : value.replaceAll("[^A-Za-z0-9._-]", "");
    }

    private String safeTestIdToken(String value) {
        String safe = value == null ? "value" : value.replaceAll("[^A-Za-z0-9._-]", "-");
        String tail = safe.length() <= 24 ? safe : safe.substring(safe.length() - 24);
        return Integer.toUnsignedString(Objects.hashCode(value), 36) + "-" + tail;
    }

    private String safeToken(String value) {
        String safe = value == null ? "tenant" : value.replaceAll("[^A-Za-z0-9._-]", "-");
        return safe.length() <= 40 ? safe : safe.substring(0, 40);
    }

    private void checkAbort() {
        RunExecution runExecution = currentRunExecution.get();
        if ((runExecution != null && runExecution.abortRequested.get())
                || Thread.currentThread().isInterrupted()) {
            throw new AbortException("Abort requested");
        }
    }

    private void sleep(long millis) {
        if (millis <= 0) return;
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new AbortException("IoT test interrupted");
        }
    }

    private String compactError(Throwable error) {
        if (error == null) return null;
        String message = error.getMessage();
        return error.getClass().getSimpleName() + (message == null ? "" : ": " + message);
    }

    private boolean hasText(String value) {
        return value != null && !value.isBlank();
    }

    @PreDestroy
    public void close() {
        boolean removed = basicIOTClient.removeSubscriber(this);
        logger.info("TEST_MQTT_SUBSCRIBER_UNREGISTERED removed={} subscriberCount={}",
                removed, basicIOTClient.getSubscriberCount());
        executor.shutdownNow();
    }

    @FunctionalInterface
    private interface ThrowingRunnable {
        void run();
    }

    private static class RunExecution {
        private final String runId;
        private final String gatewayExternalId;
        private final AtomicBoolean abortRequested = new AtomicBoolean(false);
        private volatile Future<?> future;

        private RunExecution(String runId, String gatewayExternalId) {
            this.runId = runId;
            this.gatewayExternalId = gatewayExternalId;
        }

        private void setFuture(Future<?> future) {
            this.future = future;
            if (abortRequested.get()) {
                future.cancel(true);
            }
        }

        private void abort() {
            abortRequested.set(true);
            Future<?> currentFuture = future;
            if (currentFuture != null) {
                currentFuture.cancel(true);
            }
        }
    }

    private record MqttRequestCorrelation(
            String runId,
            String gatewayExternalId,
            Class<? extends IOTMessage> responseType,
            BlockingQueue<IOTMessage> queue) {
    }

    private static class AbortException extends RuntimeException {
        private AbortException(String message) { super(message); }
    }

    private record GeoPoint(double lat, double lon) {
    }

    private static class RunContext {
        private final IotTestReport report;
        private final IotTestStartRequest request;
        private SecurityTenant tenant;
        private SecurityContext provisioningSecurityContext;
        private SecurityContext securityContext;
        private User testUser;
        private Role tenantAdminRole;
        private RoleToUser tenantAdminMembership;
        private RoleToBaseclass tenantAdminPermission;
        private PendingGateway pendingGateway;
        private Gateway gateway;
        private List<Device> devices = List.of();
        private final List<String> deviceRemoteIds = new ArrayList<>();
        private final List<String> deviceTypeExternalIds = new ArrayList<>();
        private final Map<String, String> expectedDeviceTypeExternalIdByDevice = new LinkedHashMap<>();
        private final Map<String, GeoPoint> deviceLocations = new LinkedHashMap<>();
        private final Map<String, String> expectedConnectedIconIds = new LinkedHashMap<>();
        private final SplittableRandom locationRandom;
        private Device temperatureDevice;
        private HealthNotificationPolicy notificationPolicy;
        private final List<HealthNotificationPolicy> summaryPolicies = new ArrayList<>();
        private HealthIncident remoteIncident;
        private FleetHealthPolicy fleetHealthPolicy;
        private RemoteGroup remoteGroup;
        private HealthIncident groupIncident;
        private OffsetDateTime runEventStart;
        private String connectivityProjectionIdWithoutHistory;
        private int connectivityChangeCountWithoutHistory;
        private int connectivityChangeCountWhenHistoryEnabled;

        private RunContext(IotTestReport report, IotTestStartRequest request) {
            this.report = report;
            this.request = request;
            this.locationRandom = new SplittableRandom(report.getRunId().hashCode());
        }
    }
}
