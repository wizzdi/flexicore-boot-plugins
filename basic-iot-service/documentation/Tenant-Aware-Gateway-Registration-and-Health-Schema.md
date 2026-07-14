# Tenant-aware gateway registration and health-schema normalization

## Registration contract

`RegisterGateway.tenantExternalId` is mandatory. The server resolves it through `SecurityTenantService` and creates `PendingGateway` with a `SecurityContext` whose `tenantToCreateIn` is the resolved tenant.

Registration remains idempotent under the existing per-gateway lock. An existing pending or approved gateway in the same tenant returns `alreadyPending` or `alreadyRegistered`. A record with the same gateway id in another tenant returns `tenantMismatch`. An unknown external id returns `tenantNotFound`.

`RegisterGatewayReceived` includes `tenantId` and `tenantExternalId` so the edge can log the tenant selected by the cloud.

Approval without `ApproveGatewaysRequest.tenantId` now uses the tenant of the selected pending gateway. A batch spanning more than one pending-gateway tenant must provide an explicit target tenant or be split.

## Health metadata contract

`StateSchemaHealthMetadataService` reads health metadata only while a schema is ingested and normalizes it into typed entities. Runtime evaluation continues to use typed model records and does not repeatedly parse JSON Schema text.

Supported property keywords:

- `x-flexicore-property-id`
- `x-flexicore-health-signal`
- `x-flexicore-unit`
- `x-flexicore-aggregatable`
- `x-flexicore-health-value-type` as an optional explicit `HealthSignalValueType`

Supported top-level keywords:

- `x-flexicore-keep-state-history`
- `x-flexicore-health-profile`

The health profile object supports the fields of `RemoteHealthProfileCreate`, with rule conditions referring to a schema signal by external id through `healthSignal`.

The importer upserts state properties, signals and the profile, builds direct state-property mappings, synchronizes rules and conditions, attaches the profile to the device type and configures state history. Identical schema payloads are processed once per state-schema record per server process to prevent evaluation-version churn during periodic schema republishing.
