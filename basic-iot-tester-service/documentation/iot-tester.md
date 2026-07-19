# Basic IoT end-to-end tester

## Purpose

`basic-iot-tester-service` is an explicitly enabled PF4J plugin that validates the Basic IoT production path without resetting, deleting, truncating, or directly editing database content. It publishes real MQTT messages back to the broker topic consumed by the same cloud process, waits for production responses, approves the resulting pending gateway through the normal secured service, and validates the resulting typed entities through production services.

When `gatewayExternalId` is omitted, every run generates a unique gateway external ID and therefore unique device IDs. When it is supplied, the tester uses that exact value. Every run stops at the first failed assertion, and test-created tenant, gateway, devices, schemas, policies, incidents, group, history, and notifications remain available for inspection after the run.

## Enablement and security

```properties
iot.tester.enabled=true
iot.tester.reportDirectory=/var/log/flexicore/iot-test-reports
iot.tester.maxDeviceCount=100
```

The plugin is disabled by default. Grant the controller operations only to administrators who are allowed to create tenants and run system tests.

For each tenant, the tester creates or reuses a dedicated tenant-administrator user with a deterministic fake address under `example.com`. It also creates or reuses a tenant-scoped `Tenant Administrators` role, assigns the user to that role, and grants the role `ALLOW` on the `SecurityWildcard` subject for FlexiCore's standard `All` operation. The randomly generated password is used only during user creation and is never written to reports or logs.

The plugin uses the SLF4J logger named `iot-tester`. Configure that logger to write to `/var/log/flexicore/iot-tester.log`.

## API

### Start

```http
POST /plugins/IotTester/start
Content-Type: application/json
```

```json
{
  "tenantExternalId": "iot-system-test",
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

Optional fields:

- `deviceCount`: number of devices to provision. Any positive value is accepted up to `iot.tester.maxDeviceCount`, whose default is `100`.
- `gatewayExternalId`: optional exact gateway external ID. When omitted or blank, the tester generates a unique value from the tenant external ID and run ID. When supplied, the tester uses the value exactly as provided. Reusing the same supplied value in a later run fails the uniqueness precheck if the earlier run created a pending or approved gateway.
- `lat` and `lon`: gateway map coordinates. Supply both together. Latitude must be from `-90` through `90`, and longitude from `-180` through `180`. When both are omitted, the tester retains the legacy default location (`31.9595535`, `34.816376`). Generated devices are placed at uniformly randomized points within a 200 m circle around this location.
- `emailDestination`: adds the EMAIL channel to the run-specific immediate policy.
- `whatsAppDestination`: adds the WHATSAPP channel to the run-specific immediate policy.
- `requireExternalDelivery`: when true, the run waits for at least one `DELIVERED` result for each configured external destination. Tenant provider credentials and templates must already be configured through the normal provider APIs.

The start response contains the `runId`, the effective `gatewayExternalId`, and initial `QUEUED` status. Only one run is active at a time. The requested tenant external ID remains stable across runs. With an omitted gateway external ID, the gateway and device identities are run-specific; with a supplied gateway external ID, that exact identity is used and must be unique.

### Report

```http
GET /plugins/IotTester/getReport/{runId}
GET /plugins/IotTester/getLatestReport
```

Completed JSON reports are reloaded from the report directory after a server restart.

### Abort

```http
POST /plugins/IotTester/abort/{runId}
```

## Test stages

1. **PREFLIGHT** — explicit enablement, authenticated provisioning administrator, MQTT adapter, signing public key, and safe request bounds.
2. **TENANT** — resolve the tenant by `SecurityTenant.externalId`; create it when absent; create an administrator provisioning context targeted to that tenant and validate the external ID.
3. **TENANT_ADMIN_SECURITY** — create or reuse the tenant's fake-email tester user, tenant administrator role, `RoleToUser` membership, and `RoleToBaseclass` permission whose target is the role, subject is `SecurityWildcard`, operation is FlexiCore `All`, and access is `ALLOW`. Rebuild the runtime security context from that user and verify the role is effective.
4. **MQTT_REGISTRATION** — publish `RegisterGateway` to `GATEWAY/{gatewayId}/OUT`, validate the correlated response, pending persistence, tenant binding, and idempotent repeated registration.
5. **GATEWAY_APPROVAL** — approve the actual `PendingGateway`, validate the resulting `Gateway` and its requested map coordinates, then re-register and require `alreadyRegistered` with the approved gateway ID.
6. **NOTIFICATION_POLICY** — create run-specific per-user tenant policies for immediate in-application delivery and hourly, daily, and weekly summaries. Optional email and WhatsApp channels are added to the immediate policy.
7. **DEVICE_SCHEMA** — publish one `UpdateStateSchema` per simulated device; validate unique device creation, one shared device type per logical sensor type, current schema, typed property/signal/profile normalization, and default state-history retention. Replay the temperature schema and verify idempotency.
8. **KEEPALIVE_CONNECTIVITY** — explicitly configure connectivity history off, publish timed keepalives, validate gateway/device `ON` projections and `lastSeen`, and prove that without history the existing connectivity row is updated rather than a new row being appended. The tester then enables typed connectivity history, publishes location telemetry for every device, verifies each `MappedPOI` is within 200 m of the gateway, and verifies its connected-state map icon. It also constructs a geospatial rectangle around the request `lat`/`lon`, queries `MappedPOIService` independently of the `Device.mappedPOI` object graph, and verifies that every generated device and expected icon is returned.
9. **STATE_AND_HEALTH** — publish normal, warning, and critical states; validate current state, severity values, human-intervention flag, typed state history, typed health history, and signal evidence. Disable history through an MQTT schema update, prove state and severity continue while no history row is added, then re-enable history and prove persistence resumes. Validate immediate built-in delivery, mark-as-read, and future hourly/daily/weekly scheduling.
10. **INCIDENTS_AND_ACTIONS** — prove repeated critical states create exactly one active action-required incident; add acknowledgement, in-progress, and repaired actions; validate status, actor, audit trail, and built-in action notifications.
11. **GROUP_AND_FLEET_HEALTH** — create a typed fleet policy, group, and membership; evaluate and validate critical aggregation, group history, action-required group incident, and group user actions.
12. **DISCONNECTION** — stop publishing keepalives and telemetry. In accelerated mode only persisted `lastSeen` timestamps are aged, then the real production connectivity check is called. Validate gateway/device `OFF`, each device's configured default/offline map icon both through the device projection and through the independent rectangle query, the current connectivity projection, and that with history enabled the `OFF` transition is appended as a separate typed connectivity-history row. With acceleration disabled, the tester waits for the scheduled production timeout path.
13. **RECOVERY** — publish valid telemetry for every device, not a bounced rapid keepalive; validate gateway/device reconnection, each device's recovered-state map icon through both the device projection and the independent rectangle query, and that history-enabled recovery appends a new `ON` row after the recorded `OFF` transition, then validate normal health, incident recovery flags, explicit user resolution, fleet recovery, group resolution, and built-in recovery notification.
14. **EXTERNAL_NOTIFICATIONS** — validate run-specific email/WhatsApp delivery records when destinations are supplied. Live provider acceptance is required only when `requireExternalDelivery=true`; otherwise the report records `DELIVERED`, `WAITING_FOR_ADAPTER`, `FAILED`, or other actual adapter status without pretending that external delivery succeeded.
15. **REPORT** — write JSON and Markdown reports containing every assertion and generated object ID.

## Reports and logs

The final files are written as:

```text
/var/log/flexicore/iot-test-reports/iot-test-{runId}.json
/var/log/flexicore/iot-test-reports/iot-test-{runId}.md
```

The report contains:

- overall status and first failure;
- requesting user and system test user IDs;
- timings and pass/fail/skip totals;
- IDs of the tenant, dedicated tester user, tenant administrator role, role membership, wildcard permission, pending gateway, gateway, devices, policies, incidents, groups, and memberships;
- every assertion with expected and actual values;
- external notification delivery statuses.

The `iot-tester` logger emits `TEST_RUN_*`, `TEST_STAGE_*`, `TEST_ASSERT_*`, and `TEST_MQTT_*` records. The run stops on the first failed assertion and logs the exception and current stage.

No API key, access token, Authorization header, decrypted provider secret, full provider credential, or private key is included in the report or tester log.

## Live email and WhatsApp

No external credentials are needed for MQTT registration, approval, schema, state, health, history, connectivity, incident, group, user-action, summary-scheduling, or built-in-notification tests.

For live SendGrid delivery, configure the target tenant with a valid encrypted SendGrid API key, verified sender, template/fallback content, and provide `emailDestination`.

For live WhatsApp Cloud delivery, configure the target tenant with a valid encrypted Meta access token, phone-number ID, approved template/language, and provide `whatsAppDestination` in E.164 form.

For a newly created test tenant, run once without live destinations, configure the provider settings through the normal tenant provider APIs, and run again with `requireExternalDelivery=true`.

## Isolation and limitations

The tester does not use direct SQL, native queries, `EntityManager`, table deletion, or cleanup shortcuts. Production MQTT handlers and typed FlexiCore security/common-user services perform all business operations. Repeated runs against the same tenant reuse the same fake-email user, tenant administrator role, role membership, and wildcard permission.

This is a comprehensive functional end-to-end scenario for the new provisioning, typed health, history, groups, incidents, actions, and notification changes. It is not a load test, broker-failure injector, cloud-restart test, SendGrid webhook test, or WhatsApp delivery/read webhook test. Those should be separate resilience and scale profiles after this functional suite passes reliably.


## Gateway-derived external IDs

The effective randomized gateway external ID is the namespace for every MQTT-provisioned device in a run.
Each device uses `<gatewayExternalId>-<kind>` as both its MQTT device ID and persisted `Device.externalId`.
Each device type uses `<gatewayExternalId>.device-type.<kind>`, and each state schema uses
`<deviceTypeExternalId>.schema.<version>`. The test asserts these identities directly and performs provisioning
lookups by external ID, never by display name.

## Tenant isolation

After the dedicated tenant-administrator user is created, all service writes use that user's `SecurityContext` and its `tenantToCreateIn`. Runtime queries do not pass tenant IDs back into service filters; they use exact run-owned external IDs and reject any returned object whose persisted tenant differs from the requested tenant. A final `TENANT_ISOLATION` stage uses the provisioning administrator only for read-only cross-tenant auditing and proves that the tester user, role, randomized gateway, devices, device types, mapped POIs, schemas, policies, incidents and groups are owned by the requested tenant. Data from previous runs or other tenants may exist and is ignored unless it collides with an exact run external ID, in which case the test fails.
