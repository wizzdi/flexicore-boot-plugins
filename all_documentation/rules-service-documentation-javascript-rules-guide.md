# JavaScript Rules Guide

This document provides a deep dive into writing JavaScript for the Rules Service, focusing on how to use passed arguments, access database entities, and interact with Java classes.

## The Entry Point: `evaluateScript`

Every JavaScript rule must define an `evaluateScript(x)` function. This function is called by the `ScenarioManager` when evaluating a scenario.

```javascript
function evaluateScript(x) {
    // Your logic here
    return []; // Return a list of action IDs to execute
}
```

### The `x` Argument (Context)

The `x` argument is an instance of `EvaluateScenarioScriptContext`. It provides access to the following:

| Property / Method | Type | Description |
| :--- | :--- | :--- |
| `x.scenarioEvent` | `ScenarioEvent` | The event that triggered the scenario. Contains device data, measurements, etc. |
| `x.actions` | `List<ActionContext>` | List of actions associated with the scenario. |
| `x.scenarioTriggers` | `List<ScenarioTrigger>` | List of triggers that can activate this scenario. |
| `x.logger` | `Logger` | SLF4J logger for debugging. Logs to a specific file associated with the scenario. |
| `x.toJson(obj)` | `String` | Helper method to convert an object to a JSON string. |
| `x.getEvent(trigger)` | `ScenarioSavableEvent` | Fetches the last event associated with a specific trigger. |

## Accessing Database Entities

The `scenarioEvent` is usually the gateway to your data. Depending on the event type, it might contain a `remote` (Device/Gateway) which in turn has a `mappedPOI`.

### Example: Accessing Device Info
```javascript
var device = x.scenarioEvent.remote;
var poiName = device.mappedPOI.name;
var address = device.mappedPOI.address;
x.logger.info("Processing event for device: " + poiName + " at " + address.street.name);
```

## Manipulating Actions

Scenarios define potential actions. The JavaScript logic decides **which** actions to run and **how** they should behave by modifying their parameters.

### Execution Parameters Holder
Each action has an `executeInvokerRequest.executionParametersHolder`. This is where you set the values that will be passed to the action's Java implementation.

#### Example: Setting Alert Parameters
```javascript
var action = x.actions[0];
var holder = action.executeInvokerRequest.executionParametersHolder;

holder.alertLevel = com.wizzdi.alerts.AlertLevel.WARNING;
holder.alertCategory = "Device Alerts";
holder.alertContent = "High temperature detected on " + x.scenarioEvent.remote.mappedPOI.name;
holder.relatedType = "com.wizzdi.basic.iot.model.Device";
holder.relatedId = x.scenarioEvent.remote.id;
```

#### Example: Setting Dynamic Values
For generic actions, you might use `setValue(key, value)`:
```javascript
holder.setValue("DimOnOff", true);
holder.setValue("DimLevel", 100);
```

## Java Interop

Since the Rules Service runs on the Rhino engine (or GraalJS in newer versions), you can directly interact with Java classes.

### Common Java Types
- **Sets**: `java.util.Set.of("email1@test.com", "email2@test.com")`
- **Maps**: `java.util.Map.of("key", "value")`
- **Enums**: `com.wizzdi.alerts.AlertLevel.WARNING`

### Networking (External APIs)
You can use standard Java networking classes to make HTTP calls:
```javascript
var url = new java.net.URL("https://api.example.com/notify");
var conn = url.openConnection();
conn.setRequestMethod("POST");
conn.setDoOutput(true);
// ... standard Java I/O to send/receive data
```

## Logging and Debugging

Use `x.logger` to output information. You can find these logs in the UI under the Scenario's log file or directly in the `FileResource` path defined for the scenario.

```javascript
x.logger.info("Condition evaluated: " + someCondition);
x.logger.error("Failed to process action: " + action.getId());
```

## Returning Action IDs

The function **must** return an array of strings containing the IDs of the actions you want to execute. If you don't want to run any actions, return `[]` or `null`.

```javascript
return [action.id]; // Run one action
return [action1.id, action2.id]; // Run multiple actions
return []; // Run nothing
```
