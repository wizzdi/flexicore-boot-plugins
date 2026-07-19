# Basic IoT End-to-End Tester: Test Plan, Scenarios, and Coverage

## 1. Purpose

The `basic-iot-tester-service` plugin is a staged end-to-end functional test for the Basic IoT cloud implementation. Its purpose is to prove that the large provisioning, schema, state, history, health, incident, group, connectivity, and notification additions work together through the same production paths used by a real gateway.

The tester does **not** reset the database, truncate tables, delete existing records, or insert test records directly with SQL. It creates isolated test data with run-specific identifiers and leaves those records available for investigation after the run.

The test is deliberately fail-fast:

- stages run sequentially;
- every assertion is logged;
- the first failed assertion terminates the remaining functional stages;
- the failure, current stage, expected value, actual value, and exception are retained in the final report;
- JSON and Markdown reports are generated even when a test fails or is aborted, unless report-file creation itself fails.

This suite answers the following system-level question:

> Can a previously unknown gateway register over MQTT, be approved into the correct tenant, provision devices and schemas, report state and health transitions, create incidents and notifications, participate in fleet health, become disconnected, recover, and produce a complete auditable result?

## 2. What is a real production-path test

The test intentionally mixes two categories of operation.

### 2.1 Operations driven through MQTT

The following inputs are published as MQTT messages to the same outbound gateway topic consumed by the Basic IoT cloud handler:

- `RegisterGateway`
- `UpdateStateSchema`
- `KeepAlive`
- `StateChanged`

For request/response messages, the tester:

1. creates a unique message ID;
2. subscribes temporarily to that message ID as the response topic;
3. publishes the request to `GATEWAY/{gatewayExternalId}/OUT`;
4. waits for the production handler to publish the correlated response;
5. checks that the response type and correlation ID are correct;
6. removes the temporary subscription.

The tester therefore does not call the gateway-registration, device-provisioning, or device-state service directly to imitate successful MQTT ingestion.

### 2.2 Operations driven through secured cloud services

Administrative and verification operations use the same typed services used by the REST controllers:

- tenant lookup and creation;
- common-user creation and tenant assignment;
- tenant administrator role creation, user membership, and wildcard/all-operations permission creation;
- pending-gateway lookup;
- gateway approval;
- notification-policy creation;
- device, device-type, schema, history, and health queries;
- incident actions;
- group, membership, and fleet-policy creation;
- connectivity evaluation;
- notification query and mark-as-read.

All filters are typed request objects. The tester does not use direct SQL, native SQL, JPQL strings, `EntityManager`, or untyped Criteria literals.

## 3. Safety and isolation model

### 3.1 Explicit enablement

The plugin is disabled unless the following property is set:

```properties
iot.tester.enabled=true
```

The report directory defaults to:

```properties
iot.tester.reportDirectory=/var/log/flexicore/iot-test-reports
```

### 3.2 Tenant isolation

The start request contains `tenantExternalId`.

The tester:

1. searches for a `SecurityTenant` with that external ID;
2. fails if more than one tenant is returned;
3. creates the tenant when none exists;
4. creates an administrator provisioning context whose `tenantToCreateIn` is the resolved tenant;
5. creates or reuses a deterministic fake-email user in the tenant;
6. creates or reuses a tenant-scoped `Tenant Administrators` role;
7. creates or reuses the `RoleToUser` membership;
8. creates or reuses a `RoleToBaseclass` permission with `SecurityWildcard`, the standard `All` operation, and `Access.ALLOW`;
9. rebuilds the runtime security context from the tenant administrator user and verifies the role is effective before gateway registration begins.

The authenticated API caller is recorded as `requestedByUserId`. The dedicated tenant administrator is recorded as `testUserId`. Its address uses the reserved `example.com` domain and is stable per tenant, so repeated runs reuse the same user. The password is randomly generated only when the user is first created and is never included in a report or log.

### 3.3 Tenant administrator security graph

The tester provisions the same security graph exposed by the permission UI:

```text
Target:    tenant administrator Role
Subject:   Clazz.ofName("SecurityWildcard")
Operation: SecurityOperationService.getAllOperations()
Access:    ALLOW
```

The persisted permission is a `RoleToBaseclass`. The user is connected to the role with `RoleToUser`. No direct SQL or synthetic permission cache entry is used. A `SecurityLinkGroup` is not required for a single permission; the UI uses that entity only as an optional container when submitting one or more permission combinations.

Role identity is stable per tenant through a deterministic external ID. User identity is stable through the deterministic fake email. Therefore, running the test repeatedly in the same tenant does not create duplicate users, roles, memberships, or wildcard permissions.

### 3.4 Run-specific identities

A run ID is generated from the start time plus an eight-character UUID suffix.

When `gatewayExternalId` is omitted, the gateway identity has this form:

```text
iot-tester-{safeTenantExternalId}-{runId}
```

Generated devices have unique identities derived from the effective gateway external ID. Devices are assigned to the three logical sensor types in round-robin order:

```text
{gatewayExternalId}-temperature
{gatewayExternalId}-humidity
{gatewayExternalId}-motion
{gatewayExternalId}-temperature-4
{gatewayExternalId}-humidity-5
{gatewayExternalId}-motion-6
...
```

Device-type identity is shared by all devices of the same logical type. Exactly one device type per logical type is created in each run:

```text
{gatewayExternalId}.device-type.temperature
{gatewayExternalId}.device-type.humidity
{gatewayExternalId}.device-type.motion
```

The randomized gateway identity makes those three type external IDs unique to the run, while devices within the run reuse them. For example, a 99-device run creates 99 devices but only three device types, with 33 devices assigned to each type.

The fleet objects use run-specific external IDs:

```text
iot-tester.{runId}.fleet-policy
iot-tester.{runId}.group
```

Run-specific IDs prevent accidental overlap between normal runs. Supplying a fixed `gatewayExternalId` is useful for a named test, but that identity must not already exist as a gateway or active pending gateway.

### 3.5 No destructive cleanup

The tester does not automatically delete:

- a tenant created by the run;
- pending or approved gateways;
- devices or device types;
- state schemas;
- state or health history;
- incidents or incident actions;
- notification policies or deliveries;
- groups, memberships, or fleet policies.

Leaving the data in place is intentional. It allows the operator to compare the report with persisted records and diagnose the exact first failure. Use unique generated gateway IDs for repeated runs.

## 4. API

Base path:

```text
/plugins/IotTester
```

### 4.1 Start a test

```http
POST /plugins/IotTester/start
Content-Type: application/json
Authorization: Bearer <token>
```

Recommended first-run body:

```json
{
  "tenantExternalId": "iot-system-test",
  "gatewayExternalId": "iot-test-gateway-001",
  "lat": 32.0853,
  "lon": 34.7818,
  "deviceCount": 3,
  "stepTimeoutMs": 30000,
  "pollIntervalMs": 250,
  "keepAliveCount": 3,
  "keepAliveIntervalMs": 1000,
  "accelerateDisconnect": true,
  "disconnectGraceMs": 1000,
  "requireExternalDelivery": false
}
```

The API returns immediately with a report in `QUEUED` state. The test then runs on a virtual-thread executor. Only one run can be active at a time; a second start request receives HTTP `409 Conflict`.

The maximum number of devices is configurable. The default is:

```properties
iot.tester.maxDeviceCount=100
```

### 4.2 Start-request fields

| Field | Default | Behavior |
|---|---:|---|
| `tenantExternalId` | none | Required. Existing tenant external ID or the external ID of the tenant the tester should create. |
| `gatewayExternalId` | generated | Optional gateway identity. Allowed characters are letters, digits, dot, underscore, and hyphen; maximum 128 characters. |
| `lat` | `31.9595535` | Gateway latitude from -90 through 90. Supply together with `lon`. |
| `lon` | `34.816376` | Gateway longitude from -180 through 180. Supply together with `lat`. Devices are randomized uniformly within 200 m of this point. |
| `deviceCount` | `3` | Number of MQTT-provisioned devices. Any positive value is accepted up to `iot.tester.maxDeviceCount`; the configured maximum defaults to `100`. |
| `stepTimeoutMs` | `30000` | Maximum wait for an MQTT response or asynchronous persisted result. Values below 1000 are normalized to 30000. |
| `pollIntervalMs` | `250` | Polling interval for persisted/service state. Values below 25 are normalized to 250. |
| `keepAliveCount` | `3` | Number of keepalive messages sent before connectivity assertions. Values below 1 are normalized to 3. |
| `keepAliveIntervalMs` | `1000` | Delay between keepalive messages. Negative values are normalized to 1000. |
| `accelerateDisconnect` | `true` | Ages persisted `lastSeen` values and calls the production connectivity checker so the offline scenario completes quickly. |
| `disconnectGraceMs` | `1000` | Quiet period before the disconnection check. Negative values are normalized to 1000. |
| `emailDestination` | omitted | Adds an immediate email channel to the run-specific policy. It does not configure SendGrid credentials. |
| `whatsAppDestination` | omitted | Adds an immediate WhatsApp channel to the run-specific policy. It does not configure Meta credentials or templates. |
| `requireExternalDelivery` | `false` | When true, at least one configured external channel must reach `DELIVERED`; otherwise the actual adapter outcome is recorded without failing solely because a provider is absent. |

### 4.3 Get a report

```http
GET /plugins/IotTester/getReport/{runId}
GET /plugins/IotTester/getLatestReport
```

Active reports are returned from memory. Completed reports can be reloaded from the JSON report file after a server restart.

### 4.4 Abort a run

```http
POST /plugins/IotTester/abort/{runId}
```

The abort flag and task interruption are checked between stages, while polling, while waiting for MQTT responses, and during sleeps. An aborted run is finalized with status `ABORTED` and a report is written.

## 5. Scenario topology

```text
Authenticated provisioning administrator
          |
          | POST /plugins/IotTester/start
          v
 basic-iot-tester-service
          |
          +--> Tenant tester user (`@example.com`)
          +--> Tenant Administrators role
          +--> RoleToUser membership
          +--> RoleToBaseclass(SecurityWildcard, All, ALLOW)
          |
          v
 Runtime tenant-administrator security context
          |
          |  RegisterGateway / UpdateStateSchema /
          |  KeepAlive / StateChanged
          v
      MQTT broker
          |
          v
 Basic IoT MQTT production handler
          |
          +--> PendingGateway --> approval --> Gateway
          |
          +--> DeviceType / StateSchema / Device
          |
          +--> current state / StateHistory
          |
          +--> health evaluation / RemoteHealthHistory / evidence
          |
          +--> HealthIncident / HealthIncidentAction
          |
          +--> RemoteGroup / fleet evaluation
          |
          +--> notification outbox / delivery / built-in notification
          |
          +--> ConnectivityChange
          |
          v
  Typed service assertions and report
```

## 6. Generated device and health scenarios

### 6.1 Device population

The first three devices are deterministic:

| Device | Schema value | Purpose |
|---|---|---|
| Temperature | numeric temperature and boolean online | Full state-history, health, incident, notification, recovery, and group scenario. |
| Humidity | numeric value constrained from 0 to 100 | Additional schema/device provisioning and recovery telemetry. |
| Motion | boolean value | Additional typed schema/device provisioning and recovery telemetry. |

Devices 4 through 20 are generic string-valued devices. They expand device and device-type provisioning coverage but do not create additional independent health profiles.

### 6.2 Temperature schema

The temperature device schema includes:

- JSON Schema draft 2020-12 declaration;
- object type;
- `temperature` numeric property;
- `online` boolean property;
- stable property external ID;
- normalized health signal `environment.temperatureCelsius`;
- unit `C`;
- aggregation metadata;
- configurable `x-flexicore-keep-state-history`;
- an embedded typed health profile.

### 6.3 Health profile and rules

The generated health profile has:

| Setting | Value |
|---|---|
| Default severity | `NORMAL`, value `0` |
| Warning threshold | temperature greater than or equal to `30` |
| Warning result | `WARNING`, value `40`, no mandatory human intervention |
| Critical threshold | temperature greater than or equal to `40` |
| Critical result | `CRITICAL`, value `80`, human intervention required |
| Action-required threshold | severity value `60` |
| Critical mitigation | inspect and cool the device |

The test sends this sequence:

```text
22 C -> NORMAL(0)
35 C -> WARNING(40)
45 C -> CRITICAL(80), action required
44 C -> CRITICAL while state history is disabled
43 C -> CRITICAL after state history is re-enabled
22 C -> NORMAL(0), recovery
```

This sequence validates current-state updates, threshold selection, action-required behavior, typed history, history enable/disable, incident deduplication, recovery, and notification generation.

## 7. Stage-by-stage execution

The run executes the following stages in order.

### 7.1 `PREFLIGHT`

Purpose: reject an invalid environment before creating test data.

Validations:

- the MQTT inbound adapter exists;
- the MQTT inbound adapter is running;
- the cloud server public key is available;
- the provisioning administrator is authenticated;
- `deviceCount` is greater than zero and does not exceed `iot.tester.maxDeviceCount`;
- the report directory can be created and is writable;
- when live external delivery is mandatory, at least one external destination is supplied.

A failure here means no gateway scenario should be trusted or started.

### 7.2 `TENANT`

Purpose: establish the tenant boundary for every generated object.

Scenario:

1. query `SecurityTenant` by `externalId`;
2. fail if duplicates exist;
3. create a tenant if none exists;
4. target an administrator provisioning context to that tenant.

Validations:

- exactly one tenant is resolved or created;
- the provisioning context points to that tenant;
- the tenant retains the requested external ID.

### 7.3 `TENANT_ADMIN_SECURITY`

Purpose: create a real tenant administrator identity and execute the remainder of the suite as that user rather than as the bootstrap administrator.

Stable identities:

- user email: `iot-tester-{safeTenant}-{hash}@example.com`;
- role external ID: `iot-tester.{safeTenant}.{hash}.tenant-admin`.

Scenario:

1. create or reuse the fake-email user in the requested tenant through `CommonUserService`;
2. create or reuse the tenant-scoped `Tenant Administrators` role through `RoleService`;
3. create or reuse one `RoleToUser` assignment;
4. resolve FlexiCore's standard `All` operation with `SecurityOperationService`;
5. create or reuse one `RoleToBaseclass` whose role is the permission target, whose secured type is `SecurityWildcard`, whose operation is `All`, and whose access is `ALLOW`;
6. build a new runtime security context from the tenant administrator user.

Validations:

- the user has the expected fake email and belongs to the tenant;
- the role has the expected tenant and external ID and is not a global super-administrator role;
- exactly one membership links the expected user and role;
- the persisted permission stores the expected role, wildcard subject, All-operation ID, and `ALLOW` access;
- the runtime context uses the dedicated user, targets the tenant, and contains the tenant administrator role.

The user's randomly generated password is never written to the report or log. Repeated runs in the same tenant reuse this security graph.

### 7.4 `MQTT_REGISTRATION`

Purpose: prove that a real gateway registration message creates a pending gateway.

Preconditions checked:

- no approved gateway already uses the selected gateway identity;
- no active pending gateway already uses the selected identity.

Published registration data includes:

- tenant external ID;
- server public key;
- signature-capability flag;
- latitude and longitude for the initial registration.

Validations:

- cloud response status is `registered`;
- the response references the request message ID;
- a `PendingGateway` is persisted;
- the pending gateway belongs to the requested tenant;
- a second registration returns `already pending`;
- repeated registration leaves exactly one active pending gateway.

This stage validates registration idempotency before approval.

### 7.5 `GATEWAY_APPROVAL`

Purpose: prove the pending record can be approved and the MQTT identity becomes a registered gateway.

Scenario:

1. approve the actual pending-gateway ID using `GatewayService`;
2. target approval to the requested tenant;
3. send another `RegisterGateway` MQTT request.

Validations:

- approval returns exactly one gateway;
- the gateway belongs to the requested tenant;
- `Gateway.remoteId` equals the MQTT gateway identity;
- re-registration returns `already registered`;
- the response contains the approved gateway entity ID.

### 7.6 `NOTIFICATION_POLICY`

Purpose: create deterministic, run-scoped notification behavior before health events are generated.

Created policies:

1. immediate tenant-scoped policy for the acting test user;
2. hourly-summary in-app policy;
3. daily-summary in-app policy;
4. weekly-summary in-app policy.

Immediate policy settings:

- minimum severity `0`;
- not escalation-only;
- notify on recovery;
- notify on incident actions;
- not incident-only;
- built-in `IN_APP` immediate channel;
- optional immediate `EMAIL` channel;
- optional immediate `WHATSAPP` channel.

Summary-policy settings:

- in-app delivery;
- locale `en`;
- timezone `Asia/Jerusalem`;
- local summary time 08:00;
- weekly summary day value `1`.

Validations:

- immediate policy is enabled;
- immediate in-app preference exists;
- hourly, daily, and weekly preferences persist with `Asia/Jerusalem`.

### 7.7 `DEVICE_SCHEMA`

Purpose: provision the complete device inventory through MQTT schema messages.

For every requested device, the tester sends `UpdateStateSchema` containing:

- device remote ID;
- device-type name;
- device-type external ID;
- schema version;
- JSON schema.

Validations:

- every schema request receives a correlated acknowledgement;
- all expected devices exist exactly once under the approved gateway;
- all expected device remote IDs match;
- every device references the expected shared logical device-type external ID;
- the temperature device has a current persisted schema;
- state history is enabled from schema metadata;
- the temperature device type has a typed default health profile;
- exactly one device type exists for each logical type used by the run;
- the persisted device counts per type match the round-robin assignment;
- replaying the same temperature schema is acknowledged;
- schema replay leaves one device and one device type.

This stage covers device and device-type idempotency, schema persistence, schema-to-health normalization, and default history behavior.

### 7.8 `KEEPALIVE_CONNECTIVITY`

Purpose: establish the online projection through the real keepalive message path.

Scenario:

- send the configured number of `KeepAlive` MQTT messages;
- include every generated device remote ID;
- wait the configured interval between messages.

Validations:

- each keepalive is published;
- gateway connectivity becomes `ON`;
- all generated devices become `ON`;
- gateway `lastSeen` is populated.

### 7.9 `STATE_AND_HEALTH`

Purpose: validate state ingestion, state history, typed health, evidence, built-in notification, and summary scheduling.

#### Normal state

The tester sends temperature `22` and verifies:

- MQTT state acknowledgement;
- current state contains `22`;
- severity is `NORMAL(0)`;
- human intervention is false;
- health calculation timestamp exists.

#### Warning state

The tester sends temperature `35` and verifies:

- MQTT state acknowledgement;
- severity is `WARNING(40)`;
- human intervention is false.

#### Critical state

The tester sends temperature `45` and verifies:

- MQTT state acknowledgement;
- severity is `CRITICAL(80)`;
- human intervention is true.

#### Typed persistence

The tester then verifies:

- at least three accepted state-history records exist;
- typed health history contains warning and critical transitions;
- critical health history contains one or more typed signal-evidence records.

#### History-disable scenario

The tester sends a new schema version with state history disabled, followed by temperature `44`.

It proves that:

- schema ingestion changes `keepStateHistory` to false;
- current temperature changes to `44`;
- severity remains critical;
- health evaluation continues;
- the number of state-history rows does not increase.

#### History-reenable scenario

The tester sends another schema version with history enabled, then temperature `43`.

It proves that:

- the device setting becomes true through MQTT schema ingestion;
- state-history persistence resumes.

#### Built-in notification scenario

The tester verifies that a critical health or incident event produces a delivered `IN_APP` notification. It then marks that notification as read and checks that `readAt` is populated.

#### Summary-scheduling scenario

For hourly, daily, and weekly modes, the tester verifies that a delivery:

- exists for the corresponding run-specific policy;
- remains `PENDING` before its due time;
- has a non-null future `scheduledAt` later than the outbox occurrence time.

The test validates summary scheduling, not the passage of a real hour, day, or week.

### 7.10 `INCIDENTS_AND_ACTIONS`

Purpose: validate action-required incidents and the auditable human-action workflow.

Scenario:

- the device remains critical across repeated critical updates;
- the action-required threshold is below the critical severity;
- the tester queries incidents opened during this run.

Validations:

- exactly one non-resolved/non-ignored critical incident exists;
- the incident has severity value `80`;
- `actionRequired` is true;
- repeated critical telemetry does not create uncontrolled duplicate open incidents.

User actions created:

1. `ACKNOWLEDGED`
2. `IN_PROGRESS`
3. `REPAIRED`

Further validations:

- each action is persisted with the requested action type;
- acknowledgement records the acting user;
- incident status becomes `IN_PROGRESS`;
- latest action summary is populated;
- incident actions create built-in `INCIDENT_ACKNOWLEDGED` or `INCIDENT_UPDATED` notifications.

Resolution is intentionally deferred until the recovery stage so the test can prove the difference between health recovery and explicit human closure.

### 7.11 `GROUP_AND_FLEET_HEALTH`

Purpose: validate group membership, fleet policy evaluation, group history, and group incidents.

Created fleet policy:

- enabled;
- minimum population `1`;
- default `NORMAL(0)`;
- action required from severity `60`;
- unknown-member policy uses the default;
- rule triggers when at least one member has severity at or above `60`;
- rule result is `CRITICAL(80)` with human intervention required.

Created group:

- linked to the run-specific fleet policy;
- health evaluation enabled.

Created membership:

- includes the temperature device;
- member is required;
- weight is `1.0`.

Validations:

- fleet policy, group, and membership are persisted;
- evaluated population is `1`;
- group severity is `80`;
- human intervention is required;
- a critical group-health history record exists;
- a group action-required incident opens.

Group actions created:

1. `ACKNOWLEDGED`
2. `REPAIRED`

The tester verifies that the group incident reaches `IN_PROGRESS` and stores the latest action summary.

### 7.12 `DISCONNECTION`

Purpose: prove offline projection and persisted connectivity history.

The tester first stops publishing keepalives and telemetry, then waits `disconnectGraceMs`.

#### Accelerated mode

When `accelerateDisconnect=true`, the tester:

1. updates gateway and device `lastSeen` values to a timestamp two days in the past through `RemoteService`;
2. calls the production `BasicIOTLogic.checkConnectivity()` method.

This avoids waiting for the normal scheduler timeout while still exercising the production connectivity decision and persistence logic. It does not directly set connectivity to `OFF`.

#### Scheduled mode

When `accelerateDisconnect=false`, no timestamp is changed. The tester waits for the normal scheduled connectivity timeout path. `stepTimeoutMs` must be longer than the configured production offline timeout and scheduler delay.

Validations in both modes:

- gateway becomes `OFF`;
- every generated device becomes `OFF`;
- gateway has a persisted last-connectivity-change reference;
- typed connectivity history contains at least one `ON` record and one `OFF` record.

### 7.13 `RECOVERY`

Purpose: prove recovery from both health failure and connectivity loss.

Scenario:

- send temperature `22` as recovered telemetry;
- send valid recovery state for every other device:
  - humidity `50`;
  - motion `false`;
  - generic value `ok`.

Validations:

- every recovery MQTT message is acknowledged;
- gateway reconnects to `ON`;
- all devices reconnect to `ON`;
- connectivity history contains a new `ON` transition after the `OFF` transition;
- temperature device health returns to `NORMAL(0)`;
- the open device incident records `healthRecovered=true`.

The tester then adds a device-incident `RESOLVED` action and verifies:

- incident status becomes `RESOLVED`;
- `resolvedBy` is populated.

Fleet recovery validations:

- group/fleet severity returns to `0`;
- human intervention is no longer required;
- group incident records health recovery;
- a group `RESOLVED` action closes the group incident;
- `resolvedBy` is populated.

Notification validation:

- a delivered built-in notification exists for `HEALTH_RECOVERED`.

This stage proves that health recovery is recorded first and explicit user resolution remains an independent auditable action.

### 7.14 `EXTERNAL_NOTIFICATIONS`

Purpose: validate optional email and WhatsApp delivery records without making external credentials mandatory for the core suite.

For each channel:

- when no destination is supplied, the check is marked `SKIPPED`;
- when a destination is supplied, at least one delivery record must exist;
- every returned run-specific delivery must retain the supplied destination;
- destinations are masked in logs and reports where summarized.

When `requireExternalDelivery=false`:

- actual adapter outcomes are recorded;
- statuses such as `DELIVERED`, `WAITING_FOR_ADAPTER`, `FAILED`, or other provider outcomes do not get rewritten as success;
- absence of a configured provider does not fail the core functional suite by itself.

When `requireExternalDelivery=true`:

- at least one delivery for every supplied external channel must reach `DELIVERED` within `stepTimeoutMs`;
- provider configuration, credentials, templates, and connectivity must already be valid for the target tenant.

### 7.15 `REPORT`

Purpose: persist a complete result independently of the REST response lifecycle.

Files:

```text
/var/log/flexicore/iot-test-reports/iot-test-{runId}.json
/var/log/flexicore/iot-test-reports/iot-test-{runId}.md
```

Report content:

- run status;
- current/final stage;
- tenant and gateway external IDs;
- requesting and acting user IDs;
- start, completion, and total duration;
- passed, failed, and skipped counts;
- first failure;
- generated object IDs;
- every assertion with stage, test ID, status, description, expected value, actual value, and duration;
- report-file paths.

## 8. Coverage matrix

| Capability | Covered | Evidence produced |
|---|---:|---|
| Tenant lookup by external ID | Yes | Tenant ID and external-ID assertions |
| Tenant creation when missing | Yes | Created/existing result and tenant ID |
| MQTT gateway registration | Yes | Correlated `RegisterGatewayReceived` |
| Pending-gateway persistence | Yes | Pending gateway ID and tenant assertion |
| Repeated pending registration idempotency | Yes | One active pending gateway |
| Gateway approval | Yes | Approved gateway ID |
| Registered-gateway acknowledgement | Yes | `already registered` and gateway ID |
| Device provisioning over MQTT | Yes | Device IDs and correlated schema responses |
| Device-type creation by external ID | Yes | Exact generated device-type set |
| Schema persistence | Yes | Current schema ID |
| Schema replay idempotency | Yes | One device and one type |
| Default state-history setting | Yes | `keepStateHistory=true` |
| Disable history without disabling health | Yes | Stable history count and updated critical state |
| Re-enable history | Yes | History count increases again |
| Current state projection | Yes | Temperature property checks |
| Normal/warning/critical health | Yes | Severity name/value assertions |
| Human-intervention threshold | Yes | Critical intervention flag |
| Typed health history | Yes | Warning and critical history values |
| Typed signal evidence | Yes | Evidence-row count |
| Single active device incident | Yes | Exactly one open critical incident |
| Incident acknowledgement | Yes | Action and acting-user assertions |
| Incident progress and repair actions | Yes | Persisted actions and status |
| Explicit incident resolution | Yes | `RESOLVED` and `resolvedBy` |
| Built-in immediate notification | Yes | Delivered in-app record |
| Mark built-in notification read | Yes | `readAt` timestamp |
| Incident-action notification | Yes | Incident event type in outbox |
| Recovery notification | Yes | Delivered `HEALTH_RECOVERED` |
| Hourly/daily/weekly scheduling | Yes | Pending future scheduled deliveries |
| Gateway keepalive | Yes | MQTT sends, `ON`, `lastSeen` |
| Device connectivity projection | Yes | All devices `ON`/`OFF`/`ON` |
| Connectivity history | Yes | Typed `ON`, `OFF`, recovery `ON` records |
| Remote group creation | Yes | Group ID |
| Required group membership | Yes | Membership ID |
| Fleet rule evaluation | Yes | Severity 80, population 1 |
| Group health history | Yes | Critical group-history record |
| Group incident and actions | Yes | Incident ID and action assertions |
| Fleet recovery | Yes | Severity 0 and resolved group incident |
| Email delivery-record pipeline | Optional | Delivery records and adapter status |
| WhatsApp delivery-record pipeline | Optional | Delivery records and adapter status |
| Live provider acceptance | Optional strict mode | At least one `DELIVERED` record |
| Stop on first error | Yes | First failure and immediate stage termination |
| JSON and Markdown results | Yes | Files under configured report directory |

## 9. Logging

The plugin uses the logger name:

```text
iot-tester
```

Recommended output file:

```text
/var/log/flexicore/iot-tester.log
```

Important log events:

```text
TEST_RUN_START
TEST_STAGE_START
TEST_MQTT_SEND
TEST_MQTT_RESPONSE
TEST_MQTT_UNEXPECTED_RESPONSE
TEST_ASSERT_PASS
TEST_ASSERT_SKIP
TEST_ASSERT_FAIL
TEST_STAGE_END
TEST_RUN_FAILED
TEST_RUN_ABORTED
TEST_REPORT_WRITE_FAILED
TEST_RUN_END
```

MDC includes the run ID, tenant external ID, and gateway identity. Each assertion log includes the stage and test ID, allowing the log to be correlated directly with the JSON or Markdown report.

The tester does not intentionally log:

- SendGrid API keys;
- Meta access tokens;
- decrypted provider secrets;
- private signing keys;
- complete authorization headers.

Email and phone destinations are masked in summary output.

## 10. Interpreting results

### 10.1 Overall status

| Status | Meaning |
|---|---|
| `QUEUED` | Start request accepted; execution has not begun. |
| `RUNNING` | One of the functional stages is active. |
| `PASSED` | Every mandatory assertion passed; optional missing destinations may be skipped. |
| `FAILED` | The first mandatory assertion failed, or report writing failed after an otherwise successful run. |
| `ABORTED` | An operator requested abort or the execution thread was interrupted. |

### 10.2 Assertion status

| Status | Meaning |
|---|---|
| `PASSED` | Actual state satisfied the expected predicate. |
| `FAILED` | The expected predicate was not reached before timeout or an operation raised an error. |
| `SKIPPED` | Optional external-channel input was not supplied. |

### 10.3 First-failure workflow

When a run fails:

1. read `firstFailure` in the report;
2. note `currentStage` and the final failed assertion;
3. search `iot-tester.log` for the run ID and test ID;
4. inspect IDs in `createdObjectIds` through the normal APIs;
5. compare Basic IoT and notification-adapter logs for the same timestamps;
6. correct the production path, then rerun with a new generated gateway identity.

## 11. External provider prerequisites

### 11.1 SendGrid

Live email validation requires tenant-scoped configuration containing:

- encrypted SendGrid API key;
- verified sender address;
- sender name;
- usable template or fallback content;
- reachable SendGrid API;
- valid test recipient.

The tester does not create or log these secrets.

### 11.2 WhatsApp Cloud

Live WhatsApp validation requires tenant-scoped configuration containing:

- encrypted Meta access token;
- phone-number ID;
- WhatsApp Business Account configuration;
- approved template;
- correct template language;
- E.164 test recipient;
- reachable Meta Graph API.

### 11.3 What `DELIVERED` proves

Within this test, `DELIVERED` proves that the configured adapter reported successful provider acceptance according to the current notification implementation. Final inbox delivery, WhatsApp handset delivery, and read status require provider webhooks and are outside this test.

## 12. Recommended execution sequence

### 12.1 Core functional run

Run without external destinations:

```json
{
  "tenantExternalId": "iot-system-test",
  "deviceCount": 3,
  "accelerateDisconnect": true,
  "requireExternalDelivery": false
}
```

Expected outcome:

- all core stages pass;
- email and WhatsApp assertions are skipped;
- tenant, gateway, devices, health records, incidents, group, and in-app notifications remain inspectable.

### 12.2 External adapter observation run

Configure destinations but do not require delivery:

```json
{
  "tenantExternalId": "iot-system-test",
  "deviceCount": 3,
  "emailDestination": "iot-test@example.com",
  "whatsAppDestination": "+972501234567",
  "requireExternalDelivery": false
}
```

Expected outcome:

- delivery records must be created;
- the report shows the actual provider/adapter status;
- core validation can still pass when a provider is intentionally unavailable.

### 12.3 Strict live-provider run

After tenant provider configuration is verified:

```json
{
  "tenantExternalId": "iot-system-test",
  "deviceCount": 3,
  "stepTimeoutMs": 60000,
  "emailDestination": "iot-test@example.com",
  "whatsAppDestination": "+972501234567",
  "requireExternalDelivery": true
}
```

Expected outcome:

- every supplied external channel produces at least one delivered record;
- a provider or template error fails the test in `EXTERNAL_NOTIFICATIONS`.

### 12.4 Production scheduler disconnect run

Set:

```json
{
  "tenantExternalId": "iot-system-test",
  "deviceCount": 3,
  "accelerateDisconnect": false,
  "stepTimeoutMs": 180000,
  "requireExternalDelivery": false
}
```

Choose `stepTimeoutMs` according to the actual keepalive/offline timeout and scheduler frequency. This variation proves the scheduler-driven path without accelerated timestamp aging.

## 13. Current exclusions

This plugin is a comprehensive functional end-to-end scenario, but it is not intended to replace every specialized test.

Not covered by the current single-run suite:

- high-volume load or sustained MQTT throughput;
- hundreds of simultaneous gateways;
- MQTT broker outage and reconnect backoff;
- cloud-process restart in the middle of a run;
- database failover;
- duplicate QoS delivery injection beyond the explicit registration/schema replay checks;
- arbitrary out-of-order state-message injection;
- multiple-tenant attack scenarios in the same run;
- role-by-role negative authorization testing;
- provider webhook signature verification;
- final SendGrid inbox delivery confirmation;
- final WhatsApp delivered/read webhook confirmation;
- real passage of hourly, daily, or weekly summary periods;
- automated deletion of generated test data;
- UI rendering or push-client subscription behavior.

These should be implemented as separate resilience, security-negative, provider-webhook, scale, and UI test profiles after this functional suite passes consistently.

## 14. Definition of a successful core run

A core run is successful when all of the following are true:

1. a unique pending gateway is created by MQTT;
2. the pending gateway belongs to the requested tenant;
3. approval creates the expected gateway;
4. post-approval registration identifies that gateway;
5. all requested devices are created exactly once, and one shared device type is created for each logical sensor type used by the run;
6. keepalive makes the gateway and all devices online;
7. state changes produce normal, warning, and critical health projections;
8. state and typed health history are persisted;
9. signal evidence identifies the critical source;
10. disabling history does not disable current state or health;
11. re-enabling history resumes persistence;
12. critical health creates exactly one active action-required incident;
13. user actions are persisted and attributed;
14. built-in health and incident notifications are delivered and readable;
15. hourly, daily, and weekly deliveries are scheduled correctly;
16. group membership and fleet health become critical;
17. group history and group incident are created;
18. stopping gateway traffic results in offline connectivity and typed transitions;
19. valid telemetry reconnects all remotes;
20. health and fleet projections recover;
21. explicit user actions resolve both incidents;
22. recovery creates a built-in notification;
23. the final report contains no failed assertions and lists every generated object needed for audit.

### Out-of-band map-icon verification

For every run, the tester derives a latitude/longitude envelope centered on the request coordinates. The half extent is the 200 m device-placement radius plus a 25 m query margin. It queries `MappedPOIService` with the envelope, tenant, `Device` related type, and the exact generated device external IDs. This deliberately avoids using `Device.getMappedPOI()` as the source of the assertion.

The independent map query verifies:

- every generated device appears inside the rectangle;
- connected-state telemetry selects the expected connected icon;
- normal, warning, critical, history-disabled, and history-reenabled temperature telemetry selects the expected state icon;
- connectivity loss selects each device type's default/offline icon;
- recovery telemetry restores the expected recovered-state icon.
