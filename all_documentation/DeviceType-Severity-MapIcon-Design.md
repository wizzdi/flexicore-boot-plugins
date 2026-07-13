# DeviceType Severity and MapIcon Rules Design

**Version:** 1.0\
**Target:** FlexiCore Boot Plugins, Basic IoT, Angular UI

## Introduction

The original `DeviceType` model associated a single default `MapIcon`
with a device. This works for static assets but is insufficient for
operational devices whose visual representation and operational status
change according to their reported state.

This design introduces two complementary backend concepts:

1.  **MapIcon Rules** -- determine which icon should represent the
    current device state.
2.  **Severity Rules** -- determine how important or critical the
    current state is.

Although both are based on evaluating device state, they solve different
problems and therefore remain separate.

## Why this logic belongs in the backend

Implementing these rules only in the UI would require every client
(Angular, Flutter, dashboards, REST consumers, reports, etc.) to
duplicate the same evaluation logic.

Moving evaluation into the backend provides:

-   Deterministic behavior across all clients.
-   A single implementation of the evaluation engine.
-   Future automation based on backend-generated severity changes.
-   Better performance by evaluating rules once.
-   Historical querying based on stored severity and matched rules.

## DeviceType Extensions

A DeviceType now defines:

-   State Schema
-   Default MapIcon
-   MapIcon Rules
-   Severity Rules
-   Validation Severity Rules

The DeviceType therefore becomes the complete operational description of
the device.

## MapIcon Rules

A MapIcon rule determines **which icon should be displayed**.

Each rule contains:

-   Target MapIcon
-   Priority
-   Condition Group (ALL / ANY)
-   One or more Conditions

### Supported Conditions

Property paths support nested objects, for example:

-   battery.level
-   location.floor
-   sensor.temperature

Supported operators include:

-   

-   =

-   \<

-   \<=

-   Between

-   Equals

-   Not Equals

-   Enum contains

-   LIKE (%...%)

-   Starts With

-   Ends With

-   Exists

-   Missing

-   Null

### Priority

Multiple rules may match simultaneously.

Example:

Rule A

-   Temperature \> 40
-   Priority = 10

Rule B

-   Temperature \> 40
-   Battery \< 20
-   Priority = 100

Both rules match.

Rule B wins because its priority is higher.

## Severity Rules

Severity rules determine **how serious** the current state is.

Each rule contains:

-   Severity Name
-   Severity Value
-   Priority
-   Conditions

Example severity values:

  Severity        Value
  ------------- -------
  Normal              0
  Information        10
  Warning            40
  Major              60
  Critical          100

Severity Value defines the escalation order.

Priority determines which matching rule is selected.

These are independent concepts.

## Schema Validation

Incoming device state is validated against the JSON Schema associated
with the DeviceType.

Validation includes:

-   Required fields
-   Data type
-   Numeric ranges
-   String patterns
-   Enum values
-   Nested object validation

Validation occurs before persistence.

## Validation Severity Rules

Instead of returning only "valid" or "invalid", validation failures are
classified.

Examples:

  Validation Error            Suggested Severity
  --------------------------- --------------------
  Missing Required Property   Warning
  Maximum Exceeded            Critical
  Invalid Type                Critical
  Pattern Mismatch            Error
  Invalid Enum                Error
  Unknown Property            Information

These mappings are configurable per DeviceType.

Different properties may map to different severities.

Example:

-   battery.maximumExceeded → Warning
-   temperature.maximumExceeded → Critical

## Validation Pipeline

``` text
Incoming State
      │
      ▼
JSON Parse
      │
      ▼
JSON Schema Validation
      │
      ▼
Validation Exceptions
      │
      ▼
Validation Severity Rules
      │
      ▼
Operational Severity Rules
      │
      ▼
MapIcon Rules
      │
      ▼
Persist State
      │
      ▼
Notify Clients / Automation
```

## Multiple Validation Errors

If multiple validation errors occur, all are collected.

Example:

-   temperature = 300
-   battery = "abc"
-   gps missing

The validation engine produces:

-   Critical
-   Error
-   Warning

The resulting device severity becomes **Critical** according to the
configured aggregation policy.

## Benefits

### Backend

-   Single source of truth
-   Deterministic evaluation
-   Easier automation
-   Faster clients
-   Historical analytics
-   Consistent REST/MQTT behavior

### UI

The UI only renders:

-   Current MapIcon
-   Current Severity
-   Active Rule

No duplicated business logic is required.

## Future Extensions

The design naturally supports:

-   Hysteresis
-   Time-based conditions
-   Rule simulation
-   Alarm suppression
-   Escalation workflows
-   Fleet health dashboards
-   Notification integrations
-   Historical severity analytics

## Summary

The DeviceType evolves from a passive schema definition into the
complete operational definition of a device.

It specifies:

1.  Expected data (StateSchema)
2.  Validation rules
3.  Validation-to-severity mapping
4.  Operational severity rules
5.  MapIcon selection rules
6.  Rule priority

This architecture ensures consistent behavior across all clients while
enabling future monitoring, automation, reporting, and alerting without
requiring changes to client applications.
