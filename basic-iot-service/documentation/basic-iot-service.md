# Basic IoT Service

## Purpose
The Basic IoT Service manages the lifecycle and operational state of IoT devices and gateways. It bridges the gap between raw device reports and the persistent system model, including spatial integration.

## Key Operations

### Device & Gateway Management
- **Lifecycle**: Handles the creation, discovery, and secure onboarding of Gateways and Devices.
- **Hierarchical Tracking**: Manages the relationship between gateways and their connected devices.
- **Metadata**: Updates firmware versions, properties, and verification status.

### Connectivity & State
- **Connectivity Tracking**: Monitors `lastSeen` timestamps and maintains connectivity history.
- **State Schemas**: Validates and stores device state reports against defined `StateSchema` templates.

### Spatial Integration
- **Map Synchronization**: Coordinates the update of `MappedPOI` when devices report location changes.
- **Locking Mechanisms**: Supports locking name or location to prevent automated updates from overriding manual configurations.

## Gateway vs. Device

In the Basic IoT ecosystem, there is a clear distinction between **Gateways** and **Devices**:

*   **Gateway**: A proxy or aggregator that bridges local hardware to the FlexiCore Cloud. It is responsible for security (signing/verifying messages), managing connectivity, and relaying reports from multiple devices. A gateway has a unique `gatewayId` and an RSA key pair.
*   **Device**: The actual physical sensor or actuator. A device belongs to a gateway and is identified by a `deviceId` (often its MAC address or serial number).

### Protocol Architecture

The system uses two levels of communication:
1.  **Cloud Protocol (Gateway ↔ Cloud)**: An MQTT-based protocol using JSON payloads. Every message is signed with `SHA256withRSA`.
    - **Inbound (Cloud to Gateway)**: Topic `GATEWAY/{gatewayId}/IN`
    - **Outbound (Gateway to Cloud)**: Topic `GATEWAY/{gatewayId}/OUT`
2.  **Local Protocol (Device ↔ Gateway)**: This is implementation-specific. The gateway is responsible for translating local signals (Zigbee, Bluetooth, Modbus, etc.) into the Cloud Protocol format.

---

## Implementation Guide

### Java Implementation (SDK)

The easiest way to implement a gateway in Java is using the `basic-iot-client` library.

```java
// 1. Prepare credentials and subscribers
PrivateKey privateKey = loadPrivateKey("gateway-key.pem");
List<IOTMessageSubscriber> subscribers = List.of(new MyStateChangeHandler());

// 2. Initialize the client
BasicIOTClient client = new BasicIOTClient(
    "my-gateway-id", 
    privateKey, 
    new ObjectMapper(), 
    subscribers, 
    true,  // client mode
    false, // enable verification
    msg -> sendToMqtt(msg) // Callback for outgoing messages
);

// 3. Report device state
StateChanged report = new StateChanged()
    .setDeviceId("sensor-001")
    .setDeviceType("TemperatureSensor")
    .setStatus("OK")
    .setValue("temperature", 22.5);

client.sendMessage(report);
```

### Python Implementation (Manual)

To implement a gateway in Python, you must handle MQTT connectivity and RSA signatures manually.

**Requirements:** `paho-mqtt`, `cryptography`

1.  **Message Structure**: Payloads must be JSON with a `messageType` field (e.g., `com.wizzdi.basic.iot.client.StateChanged`).
2.  **Signature**: 
    - Construct the JSON payload (excluding the `signature` field).
    - Sign the JSON string using `SHA256withRSA`.
    - Base64 encode the signature and add it to the `signature` field in the final JSON.
3.  **Topic Mapping**: Publish to `GATEWAY/{gatewayId}/OUT`.

**Example Logic:**
```python
import json
import base64
from cryptography.hazmat.primitives import hashes
from cryptography.hazmat.primitives.asymmetric import padding

def sign_payload(payload_dict, private_key):
    # 1. Remove existing signature if any
    payload_dict.pop('signature', None)
    # 2. Serialize to compact JSON
    json_data = json.dumps(payload_dict, separators=(',', ':')).encode('utf-8')
    # 3. Sign
    signature = private_key.sign(
        json_data,
        padding.PKCS1v15(),
        hashes.SHA256()
    )
    # 4. Return Base64 string
    return base64.b64encode(signature).decode('utf-8')
```

### MicroPython (ESP32) & Python Gateway Example

This example demonstrates a gateway managing three devices: one via Serial and two via LAN.

#### 1. ESP32 Device (MicroPython - LAN/MQTT)
```python
import machine
import time
import json
from umqtt.simple import MQTTClient

# Configuration
GATEWAY_IP = "192.168.1.100"
DEVICE_ID = "esp32-lan-01"

def send_report(value):
    msg = {
        "deviceId": DEVICE_ID,
        "deviceType": "TempSensor",
        "value": value
    }
    client = MQTTClient(DEVICE_ID, GATEWAY_IP)
    client.connect()
    client.publish("local/devices/reports", json.dumps(msg))
    client.disconnect()

while True:
    val = machine.ADC(0).read() # Example sensor
    send_report(val)
    time.sleep(60)
```

#### 2. Python Gateway with JavaScript Rules (Node.js)
The gateway runs on Linux and executes JavaScript rules (Scenarios/Triggers) locally before syncing to the cloud.

##### Security & Data Flow
1. **Local Rules**: The gateway fetches JS code from the cloud or local storage.
2. **Context**: Rules receive a context object `x` containing the `scenarioEvent` and metadata.
3. **Execution**: The gateway uses Node.js to evaluate scripts and return actions.

##### Gateway Implementation (Python + Node.js Bridge)

```python
import subprocess
import json
import paho.mqtt.client as mqtt

def run_js_rule(script_path, context):
    # Bridge to Node.js to execute the rule
    # Context 'x' is passed as a JSON argument
    process = subprocess.run(
        ['node', 'rule_runner.js', script_path, json.dumps(context)],
        capture_output=True, text=True
    )
    if process.returncode == 0:
        return json.loads(process.stdout)
    return None

def on_local_msg(client, userdata, msg):
    event = json.loads(msg.payload)
    
    # Construct 'x' context (mimics EvaluateTriggerScriptContext)
    trigger_context = {
        "scenarioEvent": event,
        "logger": {"info": print, "error": print}
    }
    
    # 1. Evaluate Trigger
    is_active = run_js_rule("trigger_low_battery.js", trigger_context)
    
    if is_active:
        # 2. Evaluate Scenario (mimics EvaluateScenarioScriptContext)
        scenario_context = {
            "scenarioEvent": event,
            "actions": [{"id": "relay_off", "name": "Turn Off Relay"}]
        }
        actions_to_run = run_js_rule("scenario_save_power.js", scenario_context)
        
        if "relay_off" in actions_to_run:
            execute_local_action("relay_off")

# rule_runner.js (Node.js)
/*
const fs = require('fs');
const scriptPath = process.argv[2];
const x = JSON.parse(process.argv[3]);

// Load and evaluate the script
const script = fs.readFileSync(scriptPath, 'utf8');
eval(script);

// FlexiCore rules expect a function named 'evaluate'
const result = evaluate(x);
console.log(JSON.stringify(result));
*/
```

##### Example JavaScript Rule (evaluate.js)
Compatible with both FlexiCore Cloud and the Local Gateway.

```javascript
/**
 * @param x {EvaluateScenarioScriptContext}
 * @returns {string[]} List of action IDs to execute
 */
function evaluate(x) {
    var event = x.scenarioEvent;
    var actions = [];
    
    // Logic: If battery < 20%, trigger 'save_power' action
    if (event.values && event.values.battery < 20) {
        actions.push("relay_off");
    }
    
    return actions;
}
```

#### Advanced: Rules with Database Access
In more complex scenarios, a rule might need to compare the current event with previous data stored in the database.

##### Node.js Bridge with Database (PostgreSQL)
```javascript
// rule_runner_db.js
const { Client } = require('pg');
const fs = require('fs');

async function run() {
    const client = new Client({ connectionString: 'postgresql://user:pass@localhost:5432/gateway_db' });
    await client.connect();

    const scriptPath = process.argv[2];
    const x = JSON.parse(process.argv[3]);

    // Provide DB helpers to the context 'x'
    x.db = {
        // Read: Fetch the last recorded value for this device
        getLastValue: async (deviceId) => {
            const res = await client.query(
                'SELECT data FROM events WHERE device_id = $1 ORDER BY timestamp DESC LIMIT 1',
                [deviceId]
            );
            return res.rows[0] ? res.rows[0].data : null;
        },
        // Write: Save a custom state or flag back to the DB
        saveAudit: async (deviceId, message) => {
            await client.query(
                'INSERT INTO audit_logs (device_id, message, timestamp) VALUES ($1, $2, NOW())',
                [deviceId, message]
            );
        }
    };

    const script = fs.readFileSync(scriptPath, 'utf8');
    
    // Using an async wrapper because our DB helpers are async
    const wrapper = `async function runRule(x) { 
        ${script} 
        return await evaluate(x); 
    }`;
    
    eval(wrapper);
    const result = await runRule(x);
    
    console.log(JSON.stringify(result));
    await client.end();
}

run();
```

##### Advanced JavaScript Rule (db_rule.js)
This rule reads the previous battery level from the database and only triggers an action if the level is dropping too fast.

```javascript
/**
 * @param x {EvaluateScenarioScriptContext}
 */
async function evaluate(x) {
    const event = x.scenarioEvent;
    const deviceId = event.deviceId;
    const currentBattery = event.values.battery;
    
    // 1. Read from Database
    const lastEvent = await x.db.getLastValue(deviceId);
    
    if (lastEvent && lastEvent.values) {
        const lastBattery = lastEvent.values.battery;
        const drop = lastBattery - currentBattery;
        
        // 2. Logic: If battery dropped more than 5% since last report
        if (drop > 5) {
            // 3. Write to Database (Audit Log)
            await x.db.saveAudit(deviceId, `Rapid battery drop detected: ${drop}%`);
            return ["notify_admin", "power_save_mode"];
        }
    }
    
    return [];
}
```

---

## Configuration

The Basic IoT Service requires specific configuration in `application.properties` to function correctly.

### Required Properties

| Property | Description | Suggested Value |
| :--- | :--- | :--- |
| `basic.iot.keyPath` | Path to the server's RSA private key (PKCS#8). | `/home/flexicore/certs/server-key.pem` |
| `spring.datasource.hikari.maximum-pool-size` | Number of DB connections (used for message processing semaphores). | `200` |
| `basic.iot.id` | Unique ID for this server instance in the MQTT network. | `playground` |
| `basic.iot.mqtt.url` | The MQTT broker address. | `ssl://mqtt.example.com:8883` |

### Optional/Common Properties

| Property | Description | Default |
| :--- | :--- | :--- |
| `basic.iot.fota.baseUrl` | Base URL for firmware downloads. | - |
| `basic.iot.mqtt.username` | MQTT broker username. | - |
| `basic.iot.mqtt.password` | MQTT broker password. | - |
| `basic.iot.connectivityCheckInterval` | Interval for checking device connectivity (ms). | `450000` |

---

## Security & Key Generation

The server uses an RSA key pair to sign messages sent to gateways. The private key must be in **PKCS#8 format**.

### Generating the Key Pair (OpenSSL)

1.  **Generate a 2048-bit RSA Private Key:**
    ```bash
    openssl genrsa -out private.key 2048
    ```

2.  **Convert to PKCS#8 format (Required):**
    ```bash
    openssl pkcs8 -topk8 -inform PEM -outform PEM -nocrypt -in private.key -out server-key.pem
    ```

3.  **Extract the Public Key (for Endpoints):**
    ```bash
    openssl rsa -in server-key.pem -pubout -out server-public.pem
    ```

4.  **Deployment:**
    Place `server-key.pem` in the path specified by `basic.iot.keyPath` and ensure it is readable by the server process (e.g., `chmod 600`).

---

## Deployment Notes

*   **Plugin Management**: Do NOT place `basic-iot-client.jar` in the `plugins/` directory. It is a library, not a plugin. It is already bundled inside `basic-iot-service.jar`.
*   **Dependencies**: Ensure `basic-iot-model.jar` is present in the `entities/` folder.

---

## Structure
- `DeviceService`: Orchestrates device lifecycle and status.
- `GatewayService`: Handles gateway-specific security and proxy logic.
- `RemoteService`: Common logic for all `Remote` entities, including property management.
- `FirmwareUpdateService`: Manages the distribution and tracking of firmware packages.
