# Typed Fleet Health, State-Schema Signals, and Remote Groups

**Target:** FlexiCore Boot Plugins / Basic IoT  
**Status:** Architecture proposal  
**Scope:** Backend model and APIs; no UI implementation

## 1. Executive recommendation

The fleet-health model should be split into three independent layers:

1. **Remote health profile** — converts the raw state of one `Device` or `Gateway` into canonical health signals and a current per-remote severity.
2. **Remote group** — defines which `Remote` records constitute a fleet or operational unit.
3. **Fleet health policy** — aggregates the normalized health of the group and produces a fleet-level severity and metrics.

```text
Raw Device/Gateway state
          │
          ▼
State schema property mappings
          │
          ▼
Canonical health signals
          │
          ▼
Per-remote health rules
          │
          ▼
Current Remote health projection
          │
          ▼
RemoteGroup membership
          │
          ▼
FleetHealthPolicy aggregation rules
          │
          ▼
FleetHealthSnapshot
```

The legacy `DeviceType.fleetHealthDefinitions` text/JSON field has been removed. Fleet health is represented by typed `FleetHealthPolicy`, `FleetHealthRule`, and `FleetHealthRuleCondition` entities, so it can support heterogeneous groups and survive schema evolution.

A `RemoteGroup` is recommended because both `Device` and `Gateway` already extend `Remote`. However, the group must answer only **which remotes are included**. It must not also contain the health calculation itself. A separately reusable `FleetHealthPolicy` answers **how health is calculated**.

## 2. Why the concerns must be separated

A `DeviceType` describes one class of device and its state. A fleet can represent a different concept:

- All temperature sensors of one type
- All remotes in a parking site
- A gateway and all devices connected through it
- Cameras, payment terminals, barriers, and gateways serving one car park
- All remotes belonging to a maintenance contract
- A temporary rollout population

The first fleet is homogeneous; the others are not. Therefore, attaching all fleet rules directly to `DeviceType` makes heterogeneous fleet health impossible without duplicating logic outside the model.

The recommended responsibilities are:

| Concern | Owner | Purpose |
|---|---|---|
| Raw state shape | `StateSchema` | Defines valid reported state |
| Stable logical state property | `StatePropertyDefinition` | Survives schema-version path changes |
| Canonical health meaning | `HealthSignalDefinition` | Gives different devices a shared vocabulary |
| Raw-to-canonical mapping | `HealthSignalBinding` | Maps a device/gateway value to a canonical signal |
| Per-remote status | `RemoteHealthProfile` and rules | Produces severity for one remote |
| Fleet population | `RemoteGroup` | Selects devices and gateways |
| Fleet calculation | `FleetHealthPolicy` and rules | Aggregates group health |
| Current/history result | `FleetHealthSnapshot` | Serves dashboards, reports, and alerts |

## 3. Common industry patterns

Popular IoT platforms generally separate identity/type, current twin state, groups/querying, and fleet aggregation:

- AWS IoT distinguishes thing types, static thing groups, query-driven dynamic groups, fleet indexing, and aggregate fleet metrics.
- Azure IoT Hub stores reported state and metadata in device twins and uses tags/query expressions to target or summarize populations.
- OPC UA uses typed information models so applications can consume device information independent of the underlying protocol.
- Eclipse Ditto models synchronized digital-twin state and links twins to model definitions.
- Prometheus recording rules materialize expensive aggregate calculations for efficient dashboards and repeated evaluation.

These systems do not require every heterogeneous device to expose the same raw JSON path. They rely on a model, metadata, labels, or normalized metric vocabulary before aggregation.

## 4. Popular architecture options

### Option A — Fleet rules directly on `DeviceType`

Each `DeviceType` owns multiple typed `FleetHealthRule` entities.

```text
DeviceType
   └── FleetHealthRule[]
          └── FleetHealthCondition[]
```

#### Advantages

- Simple for a homogeneous fleet.
- Easy to understand when every member has the same schema.
- Efficient queries by `deviceType_id`.
- Natural replacement for the current JSON field.

#### Limitations

- Cannot naturally aggregate devices of different types.
- Does not cover gateways unless gateways acquire a type model.
- Couples raw state paths to fleet policy.
- Rules must be duplicated across similar device types.
- A parking site or operational unit is not necessarily equivalent to a device type.

#### Appropriate use

Use as a convenience default, not as the core fleet model. A `DeviceType` may reference a default `FleetHealthPolicy` for a generated “all devices of this type” view.

---

### Option B — Canonical health signals

Each raw device property is mapped to a canonical health signal such as:

```text
availability.online
state.ageSeconds
power.batteryPercent
operation.errorActive
temperature.celsius
video.streamAvailable
storage.freePercent
```

Fleet rules reference the canonical signal, not a raw JSON path.

#### Advantages

- Supports heterogeneous devices.
- Decouples fleet rules from schema paths and schema versions.
- Provides strict value types and units.
- Enables shared rules across different device types.
- Makes missing capabilities explicit.

#### Limitations

- Requires a mapping layer.
- Device types must declare which signals they support.
- Unit conversion and enum mapping must be defined.

#### Appropriate use

This is the recommended foundation for both homogeneous and heterogeneous fleet health.

---

### Option C — Static `RemoteGroup`

A group contains explicit membership rows.

```text
RemoteGroup
   └── RemoteGroupMember[]
          └── Remote
```

#### Advantages

- Deterministic membership.
- Handles both `Device` and `Gateway` because both extend `Remote`.
- Supports operational groupings unrelated to device type.
- Suitable for permission-aware dashboards and maintenance ownership.

#### Limitations

- Membership must be maintained.
- New devices are not automatically included unless provisioning logic adds them.

#### Appropriate use

Use for sites, customers, installations, maintenance fleets, logical systems, and other intentionally managed populations.

---

### Option D — Dynamic `RemoteGroup`

Membership is derived from typed selectors such as device type, gateway, mapped area, property, or tag.

```text
RemoteGroup
   └── RemoteGroupSelector[]
          ├── Remote type selector
          ├── Device type selector
          ├── Gateway selector
          ├── Location selector
          └── Metadata selector
```

#### Advantages

- Automatically includes newly matching remotes.
- Similar to AWS dynamic thing groups and Azure twin queries.
- Suitable for deployment cohorts and fleet-wide reports.

#### Limitations

- Membership is more expensive to resolve.
- A changing denominator can make historical comparisons difficult.
- Arbitrary query text would be difficult to validate and secure.

#### Important restriction

Health-state fields should normally **not** determine the baseline membership of a health group. For example, defining a fleet as “currently online devices” would remove offline devices from the denominator and make the fleet appear healthier as failures increase.

Dynamic health groups should therefore be based primarily on stable identity/configuration metadata:

- Remote class
- Device type
- Gateway
- Site/location
- Organization/tenant scope
- Model/version
- User-assigned labels
- Provisioning attributes

State-based selectors may be supported for temporary query views, but they should not normally be used for persisted fleet-health baselines.

---

### Option E — Hierarchical or topology health

A group can contain child groups or represent a topology:

```text
Parking Site
├── Gateway A
│   ├── Camera 1
│   ├── Camera 2
│   └── Barrier Controller
└── Payment Systems
    ├── Terminal 1
    └── Terminal 2
```

#### Advantages

- Represents real operational dependency.
- Allows child-group health to roll up into parent health.
- Supports dashboards at site, region, and organization levels.

#### Limitations

- Cycles must be prevented.
- Double counting is possible when the same remote is reachable through multiple children.
- Topology dependency and ordinary grouping are not identical.

#### Recommendation

Support an optional parent/child group hierarchy, but preserve existing topology relations such as `Device.gateway`. Do not replace the real device-to-gateway relation with group membership.

## 5. Recommended backend architecture

### 5.1 `HealthSignalDefinition`

A canonical, reusable health concept.

```java
@Entity
public class HealthSignalDefinition extends Baseclass {
    private String externalId; // availability.online

    @Enumerated(EnumType.STRING)
    private HealthValueType valueType;

    private String unit;       // %, Celsius, seconds, bytes
    private String description;
    private boolean builtIn;
}
```

```java
public enum HealthValueType {
    BOOLEAN,
    INTEGER,
    DECIMAL,
    STRING,
    ENUM,
    DURATION_SECONDS,
    TIMESTAMP
}
```

Recommended built-in signals include:

| Signal | Type | Source |
|---|---|---|
| `availability.online` | Boolean | Connectivity projection |
| `state.ageSeconds` | Duration | `Remote.lastSeen` |
| `health.severityValue` | Integer | Per-remote health projection |
| `health.humanInterventionRequired` | Boolean | `Remote` field |
| `power.batteryPercent` | Decimal | State-schema mapping |
| `operation.errorActive` | Boolean | State-schema mapping |

Built-in signals from `Remote` fields do not need a state-schema mapping.

### 5.2 Stable state properties

A fleet rule should not point directly to `battery.level` in a particular schema document. Raw paths can move between schema versions.

Introduce a stable property definition at the device-type level:

```java
@Entity
public class StatePropertyDefinition extends Baseclass {
    @ManyToOne(targetEntity = DeviceType.class)
    private DeviceType deviceType;

    private String externalId; // battery.levelPercent

    @Enumerated(EnumType.STRING)
    private HealthValueType valueType;

    private String unit;
}
```

Then map each schema version to that logical property:

```java
@Entity
public class StateSchemaPropertyMapping extends Baseclass {
    @ManyToOne(targetEntity = StateSchema.class)
    private StateSchema stateSchema;

    @ManyToOne(targetEntity = StatePropertyDefinition.class)
    private StatePropertyDefinition stateProperty;

    private String jsonPointer; // /battery/level
}
```

Use JSON Pointer rather than an informal dotted path for persisted schema mappings. JSON Pointer is unambiguous when property names contain dots or special characters.

Example:

```text
Logical property: battery.levelPercent

StateSchema v1 → /battery
StateSchema v2 → /power/battery/percentage
StateSchema v3 → /diagnostics/power/batteryPercent
```

The same health signal continues working across all three schema versions.

### 5.3 `RemoteHealthProfile`

A reusable per-remote health model.

```java
@Entity
public class RemoteHealthProfile extends Baseclass {
    private String externalId;
    private boolean enabled;
}
```

Relations:

```text
DeviceType  ──ManyToOne──► RemoteHealthProfile
Gateway     ──ManyToOne──► RemoteHealthProfile
```

For gateways, a later `GatewayType` may own the profile instead of individual gateways. Until such a model exists, `Gateway.healthProfile` is acceptable.

The profile owns signal bindings and per-remote rules.

### 5.4 `HealthSignalBinding`

Maps one raw or built-in source to one canonical signal.

```java
@Entity
public class HealthSignalBinding extends Baseclass {
    @ManyToOne(targetEntity = RemoteHealthProfile.class)
    private RemoteHealthProfile healthProfile;

    @ManyToOne(targetEntity = HealthSignalDefinition.class)
    private HealthSignalDefinition signal;

    @Enumerated(EnumType.STRING)
    private HealthSignalSourceType sourceType;

    @ManyToOne(targetEntity = StatePropertyDefinition.class)
    private StatePropertyDefinition stateProperty;

    @Enumerated(EnumType.STRING)
    private HealthNormalizationType normalizationType;

    private Double scale;
    private Double offset;
    private boolean required;
}
```

```java
public enum HealthSignalSourceType {
    STATE_PROPERTY,
    REMOTE_LAST_SEEN,
    REMOTE_CONNECTIVITY,
    REMOTE_CURRENT_SEVERITY,
    REMOTE_HUMAN_INTERVENTION,
    DERIVED
}

public enum HealthNormalizationType {
    IDENTITY,
    BOOLEAN_INVERT,
    LINEAR_SCALE,
    ENUM_MAPPING,
    TIMESTAMP_AGE_SECONDS
}
```

Enum conversions should use typed rows:

```java
@Entity
public class HealthSignalEnumMapping extends Baseclass {
    @ManyToOne(targetEntity = HealthSignalBinding.class)
    private HealthSignalBinding binding;

    private String sourceValue;
    private String normalizedValue;
}
```

Example mappings:

```text
Device Type A: state /battery/value, range 0..1
    → power.batteryPercent using LINEAR_SCALE × 100

Device Type B: state /power/batteryPercent, range 0..100
    → power.batteryPercent using IDENTITY
```

### 5.5 Typed per-remote health rules

A profile contains multiple rules. This is the rigid replacement for free-form per-device health expressions.

```java
@Entity
public class RemoteHealthRule extends Baseclass {
    @ManyToOne(targetEntity = RemoteHealthProfile.class)
    private RemoteHealthProfile healthProfile;

    private int priority;
    private boolean enabled;

    @Enumerated(EnumType.STRING)
    private ConditionJoinType conditionJoinType;

    private String resultingSeverityName;
    private int resultingSeverityValue;
    private boolean humanInterventionRequired;
}
```

```java
@Entity
public class RemoteHealthRuleCondition extends Baseclass {
    @ManyToOne(targetEntity = RemoteHealthRule.class)
    private RemoteHealthRule rule;

    @ManyToOne(targetEntity = HealthSignalDefinition.class)
    private HealthSignalDefinition signal;

    @Enumerated(EnumType.STRING)
    private HealthComparisonOperator operator;

    private String stringValue;
    private Double numericValue;
    private Boolean booleanValue;
    private Double secondNumericValue; // BETWEEN
}
```

```java
public enum ConditionJoinType {
    ALL,
    ANY
}

public enum HealthComparisonOperator {
    EQUALS,
    NOT_EQUALS,
    GREATER_THAN,
    GREATER_THAN_OR_EQUAL,
    LESS_THAN,
    LESS_THAN_OR_EQUAL,
    BETWEEN,
    IN,
    EXISTS,
    MISSING
}
```

The service validates that the populated typed value matches the signal’s `HealthValueType`.

## 6. `RemoteGroup` design

### 6.1 Group entity

```java
@Entity
public class RemoteGroup extends Baseclass {
    private String externalId;

    @Enumerated(EnumType.STRING)
    private RemoteGroupMembershipMode membershipMode;

    @ManyToOne(targetEntity = RemoteGroup.class)
    private RemoteGroup parentGroup;

    @ManyToOne(targetEntity = FleetHealthPolicy.class)
    private FleetHealthPolicy fleetHealthPolicy;

    private boolean healthEnabled;
}
```

```java
public enum RemoteGroupMembershipMode {
    STATIC,
    DYNAMIC,
    HYBRID
}
```

Both `Device` and `Gateway` can be members because the relation targets `Remote`.

### 6.2 Static membership

Do not use a direct `ManyToMany`. Use a membership entity so membership can carry metadata and history.

```java
@Entity
public class RemoteGroupMember extends Baseclass {
    @ManyToOne(targetEntity = RemoteGroup.class)
    private RemoteGroup remoteGroup;

    @ManyToOne(targetEntity = Remote.class)
    private Remote remote;

    @ManyToOne(targetEntity = RemoteRoleDefinition.class)
    private RemoteRoleDefinition role;

    private boolean requiredMember;
    private Double weight;
    private OffsetDateTime activeFrom;
    private OffsetDateTime activeUntil;
}
```

A unique constraint should prevent duplicate active membership for the same group and remote.

`requiredMember` is useful for critical infrastructure. A group may become critical when its only gateway is offline even if 99 optional sensors remain healthy.

### 6.3 Member roles

```java
@Entity
public class RemoteRoleDefinition extends Baseclass {
    private String externalId; // PRIMARY_GATEWAY, CAMERA, PAYMENT_TERMINAL
}
```

Roles allow heterogeneous policies to target subsets without referring to concrete Java classes or device types.

### 6.4 Dynamic membership

Use typed selector rows instead of storing arbitrary SQL, JPQL, or Criteria field names.

```java
@Entity
public class RemoteGroupSelector extends Baseclass {
    @ManyToOne(targetEntity = RemoteGroup.class)
    private RemoteGroup remoteGroup;

    @Enumerated(EnumType.STRING)
    private RemoteGroupSelectorType selectorType;

    @Enumerated(EnumType.STRING)
    private RemoteGroupSelectorOperator operator;

    @ManyToOne(targetEntity = DeviceType.class)
    private DeviceType deviceType;

    @ManyToOne(targetEntity = Gateway.class)
    private Gateway gateway;

    @ManyToOne(targetEntity = MappedPOI.class)
    private MappedPOI mappedPOI;

    private String stringValue;
    private boolean negated;
}
```

```java
public enum RemoteGroupSelectorType {
    REMOTE_CLASS,
    DEVICE_TYPE,
    GATEWAY,
    MAPPED_POI,
    MAPPED_POI_DESCENDANT,
    VERSION,
    REMOTE_ID_PREFIX,
    USER_PROPERTY,
    CHILD_GROUP
}
```

Selectors should be translated by repository code using generated Criteria metamodel classes. Do not persist raw entity-field names and do not use `root.get("fieldName")`.

### 6.5 Static, dynamic, and hybrid semantics

- **STATIC:** only explicit `RemoteGroupMember` rows.
- **DYNAMIC:** only selector results.
- **HYBRID:** selector results plus explicit inclusions and exclusions.

For hybrid groups, membership rows can include an action:

```java
public enum RemoteGroupMembershipAction {
    INCLUDE,
    EXCLUDE
}
```

Resolution order:

1. Resolve typed selectors.
2. Add explicit `INCLUDE` members.
3. Remove explicit `EXCLUDE` members.
4. De-duplicate by `Remote.id`.
5. Apply current security scope.

## 7. Typed fleet-health policy

### 7.1 Policy

```java
@Entity
public class FleetHealthPolicy extends Baseclass {
    private String externalId;
    private boolean enabled;

    @Enumerated(EnumType.STRING)
    private FleetUnknownPolicy unknownPolicy;

    private Integer minimumPopulation;
}
```

A policy is reusable by many groups and, optionally, by several device types.

### 7.2 Rules

```java
@Entity
public class FleetHealthRule extends Baseclass {
    @ManyToOne(targetEntity = FleetHealthPolicy.class)
    private FleetHealthPolicy policy;

    private int priority;
    private boolean enabled;

    @Enumerated(EnumType.STRING)
    private ConditionJoinType conditionJoinType;

    private String resultingSeverityName;
    private int resultingSeverityValue;
    private boolean humanInterventionRequired;
}
```

### 7.3 Conditions

```java
@Entity
public class FleetHealthRuleCondition extends Baseclass {
    @ManyToOne(targetEntity = FleetHealthRule.class)
    private FleetHealthRule rule;

    @Enumerated(EnumType.STRING)
    private FleetMetricType metricType;

    @ManyToOne(targetEntity = HealthSignalDefinition.class)
    private HealthSignalDefinition signal;

    @ManyToOne(targetEntity = RemoteRoleDefinition.class)
    private RemoteRoleDefinition memberRole;

    @ManyToOne(targetEntity = DeviceType.class)
    private DeviceType memberDeviceType;

    @Enumerated(EnumType.STRING)
    private FleetDenominatorPolicy denominatorPolicy;

    @Enumerated(EnumType.STRING)
    private MissingSignalPolicy missingSignalPolicy;

    @Enumerated(EnumType.STRING)
    private HealthComparisonOperator memberValueOperator;

    private String memberStringValue;
    private Double memberNumericValue;
    private Boolean memberBooleanValue;

    @Enumerated(EnumType.STRING)
    private HealthComparisonOperator aggregateOperator;

    private Double aggregateThreshold;
    private Integer minimumMatchingMembers;
}
```

Recommended metric types:

```java
public enum FleetMetricType {
    MEMBER_COUNT,
    MEMBER_PERCENT,
    ONLINE_COUNT,
    ONLINE_PERCENT,
    OFFLINE_COUNT,
    OFFLINE_PERCENT,
    STALE_COUNT,
    STALE_PERCENT,
    CURRENT_SEVERITY_AT_OR_ABOVE_COUNT,
    CURRENT_SEVERITY_AT_OR_ABOVE_PERCENT,
    HUMAN_INTERVENTION_COUNT,
    HUMAN_INTERVENTION_PERCENT,
    SIGNAL_MATCHING_COUNT,
    SIGNAL_MATCHING_PERCENT,
    SIGNAL_AVERAGE,
    SIGNAL_MINIMUM,
    SIGNAL_MAXIMUM,
    SIGNAL_SUM,
    SIGNAL_PERCENTILE,
    MISSING_SIGNAL_COUNT,
    MISSING_SIGNAL_PERCENT
}
```

### 7.4 Denominator and missing-signal behavior

These fields are essential for heterogeneous fleets.

```java
public enum FleetDenominatorPolicy {
    ALL_GROUP_MEMBERS,
    MEMBERS_SUPPORTING_SIGNAL,
    REQUIRED_MEMBERS,
    FILTERED_MEMBERS
}

public enum MissingSignalPolicy {
    IGNORE,
    COUNT_AS_HEALTHY,
    COUNT_AS_UNHEALTHY,
    RESULT_UNKNOWN
}
```

Example: only cameras expose `video.streamAvailable`.

```text
Metric: SIGNAL_MATCHING_PERCENT
Signal: video.streamAvailable
Member value: false
Denominator: MEMBERS_SUPPORTING_SIGNAL
Threshold: >= 20%
Result: Major
```

Payment terminals and gateways do not distort the denominator because they do not support the video signal.

## 8. What in the state schema is linked to health?

### 8.1 Do not link a fleet rule directly to JSON Schema text

The JSON Schema describes shape and validation, but fleet health needs stable semantic identity.

A raw schema field should participate in health only when it has a `StatePropertyDefinition` and a `HealthSignalBinding`.

```text
StateSchema JSON property
        │ mapping by JSON Pointer
        ▼
StatePropertyDefinition
        │ normalized binding
        ▼
HealthSignalDefinition
        │ used by
        ├── RemoteHealthRuleCondition
        └── FleetHealthRuleCondition
```

### 8.2 Which fields should be mapped?

Only operationally meaningful fields, for example:

- Battery level
- Error status/code
- Temperature
- Storage usage
- Stream availability
- Door/barrier position
- Sensor tamper state
- Firmware/update status
- Internal component health

Do not automatically expose every state property as a fleet-health signal. High-cardinality identifiers, free text, and transient debug data are generally unsuitable for aggregation.

### 8.3 Schema annotations as an alternative

A lighter alternative is adding custom JSON Schema keywords:

```json
{
  "type": "number",
  "minimum": 0,
  "maximum": 100,
  "x-flexicore-property-id": "battery.levelPercent",
  "x-flexicore-health-signal": "power.batteryPercent",
  "x-flexicore-unit": "%"
}
```

This is convenient for schema import, but it should still be normalized into backend entities during schema creation/update. Runtime health evaluation should not repeatedly parse free-form schema annotations.

## 9. Non-homogeneous fleet examples

### 9.1 Parking-site group

Members:

| Role | Remote type | Count |
|---|---|---:|
| Primary gateway | Gateway | 1 |
| LPR camera | Device | 4 |
| Barrier controller | Device | 2 |
| Payment terminal | Device | 2 |
| Environmental sensor | Device | 8 |

Policy rules:

1. `requiredMember` offline count ≥ 1 → **Critical**.
2. `video.streamAvailable = false` among members supporting that signal ≥ 25% → **Major**.
3. Payment terminal `operation.errorActive = true` count ≥ 1 → **Major**.
4. `state.ageSeconds > 300` among all members ≥ 20% → **Warning**.
5. Otherwise → **Normal**.

The fleet is heterogeneous, but every condition is typed and uses a clearly defined denominator.

### 9.2 Device-type fleet

A generated dynamic group selects all devices of `TemperatureSensorV2`.

Policy rules:

1. `power.batteryPercent < 10` percentage ≥ 5% → **Major**.
2. Current per-device severity ≥ Warning percentage ≥ 20% → **Warning**.
3. Missing `temperature.celsius` percentage ≥ 2% → **Warning**.

This behaves like DeviceType-level fleet health while still using the general group/policy architecture.

### 9.3 Gateway subtree

A group selector chooses one gateway and all devices whose `Device.gateway` is that gateway.

Policy rules:

1. Gateway offline → **Critical**.
2. Devices offline ≥ 30% → **Major**.
3. Devices stale ≥ 10% → **Warning**.

The existing `Device.gateway` relationship remains the source of topology truth.

## 10. Snapshot and history model

Repeatedly scanning all current device JSON for every dashboard refresh will not scale. Persist the evaluated projection.

### 10.1 Per-remote projection

The existing fields on `Remote` are useful:

```text
currentSeverityName
currentSeverityValue
currentSeverityRuleId
severitySince
humanInterventionRequired
lastSeen
```

Consider adding a separate `RemoteHealthSnapshot` only when more diagnostic detail is needed, such as normalized signals and matched conditions.

### 10.2 Fleet snapshot

```java
@Entity
public class FleetHealthSnapshot extends Baseclass {
    @ManyToOne(targetEntity = RemoteGroup.class)
    private RemoteGroup remoteGroup;

    @ManyToOne(targetEntity = FleetHealthPolicy.class)
    private FleetHealthPolicy policy;

    private int populationCount;
    private int evaluatedCount;
    private int unknownCount;
    private String severityName;
    private int severityValue;
    private String matchedRuleId;
    private OffsetDateTime calculatedAt;
    private OffsetDateTime validUntil;
}
```

Metric details should use rows rather than JSON when they must be searchable:

```java
@Entity
public class FleetHealthMetricSnapshot extends Baseclass {
    @ManyToOne(targetEntity = FleetHealthSnapshot.class)
    private FleetHealthSnapshot snapshot;

    @Enumerated(EnumType.STRING)
    private FleetMetricType metricType;

    @ManyToOne(targetEntity = HealthSignalDefinition.class)
    private HealthSignalDefinition signal;

    private Double numericValue;
    private int numerator;
    private int denominator;
}
```

Keep current snapshot and historical snapshots separate. A current projection can be overwritten or pointed to by `RemoteGroup`; historical rows can follow a retention policy.

## 11. Evaluation lifecycle

### 11.1 On state update

1. Validate reported state against `StateSchema`.
2. Resolve `StateSchemaPropertyMapping` entries for the schema version.
3. Normalize configured values into canonical signals.
4. Evaluate `RemoteHealthRule` records by descending priority.
5. Update the current per-remote health projection.
6. Emit a remote-health-change event when severity or intervention status changes.
7. Mark affected groups dirty or enqueue group recalculation.

### 11.2 Fleet recalculation

1. Resolve group membership.
2. Apply security/ownership scope appropriate to the group.
3. Load current remote projections and required canonical signals.
4. Calculate each condition’s numerator, denominator, and value.
5. Evaluate fleet rules by descending priority.
6. Persist the current snapshot and optional history.
7. Emit a fleet-health-change event if the result changes.

### 11.3 Triggering options

| Strategy | Advantages | Limitations |
|---|---|---|
| Synchronous on every state change | Immediate | Expensive for large or overlapping groups |
| Debounced event-driven recalculation | Near-real-time, efficient | Requires queue/coalescing |
| Fixed schedule | Simple | Delayed status |
| Incremental counters | Fastest at scale | Most complex consistency model |

Recommended initial implementation: event-driven dirty marking with a short debounce, plus periodic full reconciliation.

Prometheus-style recording rules illustrate the value of materializing expensive aggregate results before dashboards repeatedly query them.

## 12. Permissions and information leakage

Fleet counts can disclose devices that a user cannot otherwise see. A precomputed group snapshot must therefore have an explicit security model.

Recommended rule:

- A secured `RemoteGroup` represents an intentionally shareable population.
- Users who can read the group and its snapshot may see aggregate counts even if individual membership details are separately restricted only when this behavior is explicitly accepted.
- Otherwise, calculate a user-context snapshot using only remotes accessible through the current `SecurityContext`.

Do not silently return a global precomputed count to a partially authorized caller.

For stable dashboard performance, prefer tenant/site-scoped groups whose access matches the access of their members.

## 13. Suggested APIs

### Group management

```text
POST /plugins/RemoteGroups/getAllRemoteGroups
POST /plugins/RemoteGroups/createRemoteGroup
PUT  /plugins/RemoteGroups/updateRemoteGroup
POST /plugins/RemoteGroups/getResolvedMembers
```

### Health model

```text
POST /plugins/HealthSignals/getAllHealthSignals
POST /plugins/RemoteHealthProfiles/getAllRemoteHealthProfiles
POST /plugins/RemoteHealthProfiles/createRemoteHealthProfile
PUT  /plugins/RemoteHealthProfiles/updateRemoteHealthProfile
POST /plugins/RemoteHealthProfiles/simulateRemoteHealth
```

### Fleet policy and evaluation

```text
POST /plugins/FleetHealthPolicies/getAllFleetHealthPolicies
POST /plugins/FleetHealthPolicies/createFleetHealthPolicy
PUT  /plugins/FleetHealthPolicies/updateFleetHealthPolicy
POST /plugins/FleetHealth/evaluateGroup
POST /plugins/FleetHealth/getCurrentGroupHealth
POST /plugins/FleetHealth/getGroupHealthHistory
POST /plugins/FleetHealth/simulatePolicy
```

Simulation should return:

- Resolved population
- Signal support counts
- Numerator and denominator for every condition
- Missing-signal behavior
- Matched rule
- Final severity

This is essential for safely authoring policies.

## 14. FlexiCore implementation conventions

All persistent configuration and relationship records should:

- Extend `Baseclass`.
- Use `ManyToOne` relationships rather than embeddables for reusable or independently secured records.
- Be registered in the module’s `persistence.xml`.
- Be added to the relevant `EntitiesHolder`.
- Use generated static metamodel classes in Criteria queries.
- Avoid stringly typed calls such as `root.get("deviceType")`.
- Use soft deletion consistently.
- Validate every referenced entity through the current `SecurityContext`.

Suggested unique constraints/indexes:

```text
HealthSignalDefinition.externalId
StatePropertyDefinition(deviceType_id, externalId)
StateSchemaPropertyMapping(stateSchema_id, stateProperty_id)
HealthSignalBinding(healthProfile_id, signal_id)
RemoteGroup.externalId
RemoteGroupMember(remoteGroup_id, remote_id, softDelete)
FleetHealthRule(policy_id, priority)
```

## 15. Recommended phased implementation

### Phase 1 — Typed homogeneous health

- Use typed `FleetHealthPolicy`, `FleetHealthRule`, and `FleetHealthRuleCondition` entities; the legacy JSON/text field is not part of the model.
- Introduce canonical built-in signals for connectivity, state age, current severity, and human intervention.
- Add `StatePropertyDefinition` and schema-version mappings.
- Add per-remote health profiles and typed rules.
- Generate an implicit dynamic group per DeviceType.

### Phase 2 — `RemoteGroup`

- Add static membership for `Remote`.
- Add member roles and required-member semantics.
- Add reusable `FleetHealthPolicy`.
- Support groups containing both devices and gateways.

### Phase 3 — Dynamic and hierarchical groups

- Add typed selectors.
- Add parent/child groups with cycle detection.
- Add gateway-subtree and location-descendant selectors.
- Add explicit include/exclude overrides.

### Phase 4 — Scale and history

- Add dirty-group event processing and debounce.
- Persist current and historical snapshots.
- Add incremental counters if needed.
- Add dashboard/report Dynamic Invokers over snapshots.

## 16. Final recommendation

Use the following core model:

```text
DeviceType ───────────────► RemoteHealthProfile ◄──────── Gateway
                                  │
                                  ├── HealthSignalBinding[]
                                  └── RemoteHealthRule[]

StateSchema ─► StateSchemaPropertyMapping ─► StatePropertyDefinition
                                                    │
                                                    ▼
                                         HealthSignalDefinition
                                                    │
                                                    ▼
Remote ─► RemoteGroupMember ─► RemoteGroup ─► FleetHealthPolicy
                                                │           │
                                                │           └── FleetHealthRule[]
                                                │                    └── FleetHealthRuleCondition[]
                                                ▼
                                      FleetHealthSnapshot
```

The most important decisions are:

1. **Use canonical health signals**, not raw state paths, in fleet policies.
2. **Use stable state-property entities and schema-version mappings** to survive schema evolution.
3. **Use `RemoteGroup` for both devices and gateways**, because both inherit from `Remote`.
4. **Keep group membership separate from health policy** so policies are reusable and groups remain understandable.
5. **Make denominator and missing-signal behavior explicit** for every heterogeneous aggregate.
6. **Persist evaluated snapshots** for dashboards, history, and alerts instead of rescanning raw JSON on every request.

This model remains rigid and type-safe while supporting simple homogeneous DeviceType fleets, mixed device/gateway installations, organizational groups, and future dashboard or alerting use cases.

## References

- [AWS IoT Core — Fleet indexing](https://docs.aws.amazon.com/iot/latest/developerguide/iot-indexing.html)
- [AWS IoT Core — Static thing groups](https://docs.aws.amazon.com/iot/latest/developerguide/thing-groups.html)
- [AWS IoT Core — Dynamic thing groups](https://docs.aws.amazon.com/iot/latest/developerguide/dynamic-thing-groups.html)
- [AWS IoT Core — Fleet metrics](https://docs.aws.amazon.com/iot/latest/developerguide/iot-fleet-metrics.html)
- [Azure IoT Hub — Device twins](https://learn.microsoft.com/en-us/azure/iot-hub/iot-hub-devguide-device-twins)
- [Azure IoT Hub — Query language](https://learn.microsoft.com/en-us/azure/iot-hub/iot-hub-devguide-query-language)
- [OPC UA Part 100 — Devices](https://reference.opcfoundation.org/specs/OPC-10000-100/full/)
- [Eclipse Ditto — Digital twins explained](https://eclipse.dev/ditto/intro-digitaltwins.html)
- [Prometheus — Recording rules](https://prometheus.io/docs/prometheus/latest/configuration/recording_rules/)
