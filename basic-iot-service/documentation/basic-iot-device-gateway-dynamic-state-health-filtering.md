# Device and Gateway Dynamic State and Health Filtering

## Scope

`DeviceFilter` and `GatewayFilter` both extend `RemoteFilter`, so they share the same filters for reported state, user-defined properties, connectivity, current health, and map location. Use:

- `POST /plugins/Device/getAllDevices`
- `POST /plugins/Gateway/getAllGateways`
- `POST /plugins/Remote/getAllRemotes` when both remote types are acceptable

Although these fields are often described as BSON-style dynamic data, the current model accepts JSON objects and persists `deviceProperties` and `userAddedProperties` in PostgreSQL `jsonb` columns through `JsonConverter`.

## Current Projection Versus History

Choose the API according to the question being asked:

| Question | Filter or endpoint |
|---|---|
| What is the device or gateway reporting now? | `devicePropertiesFilter` on `DeviceFilter` or `GatewayFilter` |
| What metadata has a user added? | `userAddedPropertiesFilter` |
| What is its current calculated health? | `staticPropertiesFilter` against the health fields on `Remote` |
| What state did it report previously? | `POST /plugins/StateHistory/getAllStateHistories` |
| What health interval was active previously? | `POST /plugins/RemoteHealthHistory/getAllRemoteHealthHistories` |

All predicates within one request are combined with SQL `AND`.

## Dynamic Filter Grammar

Each map key is a property name. Its value is either a predicate or a nested node.

```json
{
  "temperature": {
    "type": "DynamicPredicateItem",
    "filterType": "GREATER_THAN_OR_EQUAL",
    "value": 30
  },
  "power": {
    "type": "DynamicNodeItem",
    "children": {
      "batteryPercent": {
        "type": "DynamicPredicateItem",
        "filterType": "LESS_THAN",
        "value": 20
      }
    }
  }
}
```

The example addresses `temperature` and the nested path `power.batteryPercent`. Supported `filterType` values are:

- equality: `EQUALS`, `NOT_EQUALS`
- collections: `IN`, `NOT_IN` with an array value
- text: `CONTAINS`
- ordering: `LESS_THAN`, `LESS_THAN_OR_EQUAL`, `GREATER_THAN`, `GREATER_THAN_OR_EQUAL`
- presence: `IS_NULL`, `IS_NOT_NULL` (the `value` may be omitted)

Send values with their real JSON type. For example, use `30`, not `"30"`, for numeric comparison and `true`, not `"true"`, for a Boolean. Property names and casing must match the stored state exactly. `IS_NULL` treats a missing path and a JSON null as equivalent.

The current schema describes valid fields but does not automatically validate a filter request. For heterogeneous device types, first restrict the query with `deviceTypeIds` or `deviceTypeFilter` so the same property path has a consistent type. The next section explains how to handle types or schema versions that use different paths.

## Searching Across Different Device Types

Different device types may represent the same logical value at different paths:

| Device type/schema | Raw state path | Logical property |
|---|---|---|
| Thermostat v1 | `temperature` | `environment.temperatureCelsius` |
| HVAC controller v3 | `environment.currentTemperature` | `environment.temperatureCelsius` |
| Weather station v2 | `sensors.air.tempC` | `environment.temperatureCelsius` |

Use the JSON Schema annotation `x-flexicore-property-id` as the stable logical property ID. Current schema ingestion normalizes annotated entries directly under the root `properties` object into `StatePropertyDefinition`, which records the schema, raw `propertyPath`, value type, and unit. The UI can query these definitions through:

```http
POST /plugins/StatePropertyDefinition/getAllStatePropertyDefinitions
```

For nested schema properties, the UI must currently derive the path directly from `stateJsonSchema`, or the mapping must be created explicitly; recursive normalization would require extending `StateSchemaHealthMetadataService`. The existing `DeviceFilter` still operates on the raw JSONB path and does not resolve the logical property ID automatically. It also combines every predicate with `AND`. Consequently, one current request cannot express:

```text
(schema A AND temperature >= 30)
OR
(schema B AND environment.currentTemperature >= 30)
```

### Current API: one request per schema branch

For a small result set, build one filter per device type/current schema, repeat the common conditions, and union devices by internal `Device.id`. A branch should constrain both the device type and `currentSchema.id` so a schema-version path change cannot produce incorrect results:

```json
{
  "deviceTypeIds": ["thermostat-type-id"],
  "connectivity": ["ON"],
  "staticPropertiesFilter": {
    "currentSchema": {
      "type": "DynamicNodeItem",
      "children": {
        "id": {
          "type": "DynamicPredicateItem",
          "filterType": "EQUALS",
          "value": "thermostat-schema-v1-id"
        }
      }
    }
  },
  "devicePropertiesFilter": {
    "temperature": {
      "type": "DynamicPredicateItem",
      "filterType": "GREATER_THAN_OR_EQUAL",
      "value": 30
    }
  }
}
```

Issue a second request for the HVAC schema using its type/schema IDs and `environment.currentTemperature`, then merge the responses. This is acceptable for a bounded dashboard or type-by-type view, but it cannot provide an exact global count, ordering, or page boundary without fetching and merging all matching branches.

### Recommended backend contract: schema-aware OR branches

For scalable cross-type search, add a composite request whose common filter is ANDed with an OR of schema-specific branches:

```json
{
  "commonFilter": {
    "connectivity": ["ON"]
  },
  "match": "ANY",
  "schemaBranches": [
    {
      "deviceTypeIds": ["thermostat-type-id"],
      "stateSchemaIds": ["thermostat-schema-v1-id"],
      "devicePropertiesFilter": {
        "temperature": {
          "type": "DynamicPredicateItem",
          "filterType": "GREATER_THAN_OR_EQUAL",
          "value": 30
        }
      }
    },
    {
      "deviceTypeIds": ["hvac-type-id"],
      "stateSchemaIds": ["hvac-schema-v3-id"],
      "devicePropertiesFilter": {
        "environment": {
          "type": "DynamicNodeItem",
          "children": {
            "currentTemperature": {
              "type": "DynamicPredicateItem",
              "filterType": "GREATER_THAN_OR_EQUAL",
              "value": 30
            }
          }
        }
      }
    }
  ],
  "pageSize": 100
}
```

This is a proposed extension, not an existing `DeviceFilter` payload. The repository predicate should be:

```text
security AND commonFilter AND (
    (deviceType/schema A AND vector A)
    OR
    (deviceType/schema B AND vector B)
)
```

Executing the branches in one database query preserves correct security filtering, total count, sorting, and pagination. A higher-level alternative is to let the client send a logical property ID and operator, then have the backend resolve `StatePropertyDefinition` records into these branches.

## Building a Schema-Driven Search UI

The UI should generate a search-field descriptor from each selected device type's `StateSchema.stateJsonSchema`, rather than hard-coding device fields. Recursively walk `properties` and retain the raw path plus:

- stable ID from `x-flexicore-property-id`;
- `type`, `format`, `title`, and `description`;
- `enum` values;
- `minimum`, `maximum`, and exclusive bounds;
- unit from `x-flexicore-unit`;
- schema ID, version, and device-type ID.

Merge descriptors with the same stable property ID only when their value types and units are compatible. Otherwise show them as type-specific fields or define a unit conversion before building branch values.

### Control and operator selection

| JSON Schema definition | Suggested UI | Filter emitted today |
|---|---|---|
| `number` or `integer` | number input, minimum/maximum, slider | equality or one ordering predicate |
| `string` with `enum` | select or multi-select | `EQUALS` or `IN` |
| plain `string` | text input with Exact/Contains | `EQUALS` or `CONTAINS` |
| `boolean` | Any/Yes/No selector | no predicate or `EQUALS` |
| nullable/optional field | Exists/Missing selector | `IS_NOT_NULL` or `IS_NULL` |
| `string` with `format: date-time` | date/time picker | ordering only when values use one canonical ISO-8601 representation |
| object | collapsible group | `DynamicNodeItem` containing child controls |
| array | type-specific control | not safely supported by the current text-path predicate implementation |

`CONTAINS` currently becomes SQL `LIKE '%value%'` and is case-sensitive. It is not a general raw `LIKE` expression: the user cannot provide wildcards, prefix-only matching, or case-insensitive `ILIKE` without a backend extension.

### Two-sided ranges need a filter extension

The current map permits only one `DynamicFilterItem` for a property key. The UI can emit `temperature >= 20` or `temperature <= 30`, but it cannot emit both for the same key in one request. Do not silently discard one bound.

A suitable extension is a predicate group:

```json
{
  "temperature": {
    "type": "DynamicPredicateGroup",
    "join": "AND",
    "predicates": [
      {
        "filterType": "GREATER_THAN_OR_EQUAL",
        "value": 20
      },
      {
        "filterType": "LESS_THAN_OR_EQUAL",
        "value": 30
      }
    ]
  }
}
```

Alternatively, add a typed `BETWEEN` operator with lower/upper values and inclusive flags. Either choice requires backend support before the UI sends it. Predicate groups are more flexible because the same representation also supports multiple exclusions or future OR conditions.

### Recommended UI request flow

1. Load the selected device types and their state schemas with `POST /plugins/StateSchema/getAllStateSchemas`.
2. Parse each `stateJsonSchema` and build searchable descriptors.
3. Group compatible descriptors by `x-flexicore-property-id`; retain a raw path for every schema branch.
4. Render the control and allowed operators from the schema type and constraints.
5. Keep common filters such as connectivity, health, gateway, and map area outside the schema branches.
6. Compile each entered logical criterion into one raw-path predicate per applicable schema.
7. Use per-schema requests only for small datasets; use a schema-aware composite backend endpoint for global pagination.

For stronger governance, schemas may add application annotations such as `x-flexicore-searchable`, `x-flexicore-search-operators`, `x-flexicore-search-label`, and `x-flexicore-search-order`. These are recommended UI metadata conventions; the current schema ingestion service does not enforce or normalize them.

## Filtering Current Device State and Health

This request finds connected devices of a given type whose current temperature is at least 40, whose nested battery value is below 20, and whose calculated health is critical:

```http
POST /plugins/Device/getAllDevices
Content-Type: application/json
```

```json
{
  "deviceTypeIds": ["device-type-internal-id"],
  "connectivity": ["ON"],
  "devicePropertiesFilter": {
    "temperature": {
      "type": "DynamicPredicateItem",
      "filterType": "GREATER_THAN_OR_EQUAL",
      "value": 40
    },
    "power": {
      "type": "DynamicNodeItem",
      "children": {
        "batteryPercent": {
          "type": "DynamicPredicateItem",
          "filterType": "LESS_THAN",
          "value": 20
        }
      }
    }
  },
  "staticPropertiesFilter": {
    "currentSeverityValue": {
      "type": "DynamicPredicateItem",
      "filterType": "GREATER_THAN_OR_EQUAL",
      "value": 80
    },
    "humanInterventionRequired": {
      "type": "DynamicPredicateItem",
      "filterType": "EQUALS",
      "value": true
    }
  },
  "pageSize": 100
}
```

`staticPropertiesFilter` addresses Java/JPA property names, not JSON-schema property names. Useful current health fields inherited by both devices and gateways include:

- `currentSeverityName`, `currentSeverityValue`, `currentSeverityRuleId`
- `severitySince`, `healthCalculatedAt`
- `humanInterventionRequired`
- `healthSummary`, `mitigationStatus`, `mitigationInstructions`
- `evaluatedHealthProfileId`, `healthEvaluationVersion`

A related entity can be traversed with a node. For example, filter by the internal health-profile ID:

```json
{
  "staticPropertiesFilter": {
    "healthProfile": {
      "type": "DynamicNodeItem",
      "children": {
        "id": {
          "type": "DynamicPredicateItem",
          "filterType": "EQUALS",
          "value": "health-profile-internal-id"
        }
      }
    }
  }
}
```

Prefer first-class typed fields where available: `remoteIds` for external device/gateway identities, `connectivity`, `lastSeenTo`, and `mappedPOIFilter`. Device filters additionally support `gatewayIds`, `deviceTypeIds`, `deviceTypeFilter`, and `verified`. `gatewayIds`, `deviceTypeIds`, and related-object IDs are internal entity IDs; `remoteIds` contains external IoT identities.

## Filtering Gateways and User-Added Values

Gateways use the identical dynamic grammar. This example selects gateways with a reported nested modem signal and an operator-assigned site code:

```http
POST /plugins/Gateway/getAllGateways
```

```json
{
  "devicePropertiesFilter": {
    "modem": {
      "type": "DynamicNodeItem",
      "children": {
        "rssi": {
          "type": "DynamicPredicateItem",
          "filterType": "LESS_THAN",
          "value": -90
        }
      }
    }
  },
  "userAddedPropertiesFilter": {
    "siteCode": {
      "type": "DynamicPredicateItem",
      "filterType": "EQUALS",
      "value": "TLV-01"
    }
  }
}
```

`devicePropertiesFilter` reads the latest device-reported projection. `userAddedPropertiesFilter` reads operator/application metadata; it is not overwritten merely because a device reports a new state.

## Filtering Stored State

State snapshots are created only when `keepStateHistory` is enabled. Query them with `StateHistoryFilter`:

```http
POST /plugins/StateHistory/getAllStateHistories
```

```json
{
  "remoteFilter": {
    "remoteIds": ["sensor-001"]
  },
  "timeAtStateFrom": "2026-07-01T00:00:00Z",
  "timeAtStateTo": "2026-08-01T00:00:00Z",
  "devicePropertiesFilter": {
    "temperature": {
      "type": "DynamicPredicateItem",
      "filterType": "GREATER_THAN",
      "value": 35
    }
  },
  "pageSize": 500
}
```

Top-level `devicePropertiesFilter` and `userAddedPropertiesFilter` apply to each historical snapshot. Dynamic filters placed inside `remoteFilter` apply to the remote's **current** projection, not to the snapshot. Results are ordered newest first. Here, `remoteFilter.remoteIds` uses the external IoT identity.

For numeric time-series totals, use `POST /plugins/StateHistory/getAllStateHistoriesAgg`:

```json
{
  "stateHistoryFilter": {
    "remoteFilter": {
      "remoteIds": ["meter-001"]
    },
    "timeAtStateFrom": "2026-07-01T00:00:00Z"
  },
  "groupByFieldName": "energy.kWh",
  "groupByRemote": true,
  "timeUnit": "HOURS"
}
```

Aggregation extracts the numeric path from historical `deviceProperties` and sums it per time bucket. Valid units are `MINUTES`, `HOURS`, `DAYS`, `WEEKS`, `MONTHS`, and `YEARS`.

## Filtering Stored Health

Current health is a projection on `Remote`; `RemoteHealthHistory` stores the intervals during which a health outcome was active. Query it separately:

```http
POST /plugins/RemoteHealthHistory/getAllRemoteHealthHistories
```

```json
{
  "remoteIds": ["remote-internal-id"],
  "minimumSeverityValue": 40,
  "maximumSeverityValue": 100,
  "humanInterventionRequired": true,
  "intervalStart": "2026-07-01T00:00:00Z",
  "intervalEnd": "2026-08-01T00:00:00Z",
  "pageSize": 100
}
```

Unlike `RemoteFilter.remoteIds`, health-history `remoteIds` expects internal `Remote.id` values. Other supported selectors are `remoteHealthHistoryIds`, `remoteHealthProfileIds`, and `matchedRuleIds`.

The interval filter uses overlap semantics: a record matches when it was active at any point in `[intervalStart, intervalEnd)`. A null `validUntil` identifies the currently open health interval. Responses are newest first and include populated `signalEvidence`, with values stored in the matching typed field (`numericValue`, `booleanValue`, `stringValue`, or `timestampValue`).

## Recommended Query Workflow

1. Restrict devices by gateway/device type and gateways by external identity when possible.
2. Add `devicePropertiesFilter` and `userAddedPropertiesFilter` for current dynamic values.
3. Add current health conditions through `staticPropertiesFilter`.
4. Use the returned internal `id` when querying `RemoteHealthHistory`.
5. Query `StateHistory` separately when the condition must be true at a past time.

Dynamic paths are translated to PostgreSQL JSON-path expressions. Frequently queried high-cardinality paths may need database expression indexes; ordinary entity indexes do not automatically index every `jsonb` path.
