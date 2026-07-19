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

## Gateway Onboarding & Approval

The system provides a secure, two-step onboarding process for new gateways. This ensures that only authorized hardware can interact with the system.

### 1. Registration (PendingGateway)
When a new gateway first connects, it must register itself. This can happen in two ways:
- **MQTT Registration**: The gateway sends a `com.wizzdi.basic.iot.client.RegisterGateway` message to the cloud.
- **REST API**: A registration request is sent to `/plugins/PendingGateway/registerGateway`.

In both cases, a `PendingGateway` entity is created in the database. This entity stores:
- `gatewayId`: The unique identifier requested by the gateway.
- `publicKey`: The RSA public key provided by the gateway for future message verification.
- `noSignatureCapabilities`: A flag indicating if the gateway is capable of signing messages.

At this stage, the gateway is in a "pending" state and cannot yet report device data or receive commands.

### 2. Approval
An administrator must review and approve the `PendingGateway`. This is done via the `/plugins/Gateway/approveGateways` endpoint.

When a `PendingGateway` is approved:
1.  A formal **Gateway** entity is created, inheriting the properties (ID, Public Key) from the `PendingGateway`.
2.  A **SecurityUser** is automatically created for the gateway, allowing it to authenticate and interact with the system's APIs and MQTT broker securely.
3.  The `PendingGateway` entry is typically removed or marked as processed.

Once approved, the gateway is fully operational.

---

## Device State Schemas

The `StateSchema` defines the "digital twin" of a device. It uses JSON Schema to specify which properties a device can report (read) or receive as commands (write).

### How the Cloud Learns About Schemas

The Gateway is responsible for informing the Cloud about the schema of the devices it manages. This happens through two primary mechanisms:

#### 1. Dynamic Schema Update (`UpdateStateSchema`)
When a gateway discovers a new device type or a firmware update changes the device's capabilities, it can send an `UpdateStateSchema` message to the cloud.
- **Payload**: Includes the `deviceType`, the full **JSON Schema** for the state, a `version` number, and a list of `SchemaActions`.
- **Processing**: The Cloud receives this message and uses `BasicIOTLogic` to either find an existing schema matching the version or create a new `StateSchema` entity. It also creates `SchemaAction` entities for any defined commands.
- **Association**: The device instance is then linked to this specific version of the schema.

#### 2. Schema Selection (`SetStateSchema`)
If the schema version is already known to the cloud (e.g., it was previously uploaded or is part of a standard library), the gateway can simply send a `SetStateSchema` message.
- **Payload**: Includes the `deviceType` and the requested `version`.
- **Processing**: The Cloud looks up the existing `StateSchema` for that device type and version. If found, it updates the device's current schema link and returns a confirmation to the gateway.

### How it Works
- **Read-Only Properties**: Represent sensors or internal device states (e.g., Temperature, Battery Level, RSSI). These are reported by the device to the cloud.
- **Writable Properties**: Represent actuators or configuration settings (e.g., Relay Status, Reporting Interval, Thresholds). The cloud can send commands to update these values.
- **Validation**: Every `StateChanged` report and every command is validated against the schema. This ensures data integrity and prevents invalid configurations from being sent to hardware.

### Schema Examples
Based on the standard `StateSchema`, here are examples of available properties and their configurations:

| Property | Type | Options / Constraints | Description |
| :--- | :--- | :--- | :--- |
| `AccelerometerImpact` | boolean | `readOnly: true` | Indicates if an impact was detected. |
| `AccelerometerFullScale` | integer | `enum: [2, 4, 8, 16]` | Sample value range (e.g., ±2g, ±4g). |
| `AccelerometerTreshold` | integer | `min: 2000, max: 16000` | Threshold for impact alerts (in mG). |
| `RSSI` | integer | `min: -120, max: 0, readOnly: true` | Signal strength indicator. |
| `Net_Type` | integer | `enum: [0..7], readOnly: true` | Network type (e.g., LTE, GSM). |
| `Oper_Code` | integer | `min: 0, max: 4294967295, readOnly: true` | Network operator code. |
| `KeepAlivePeriod` | integer | `min: 1, max: 1440` | MQTT keep-alive interval (minutes). |
| `SamplingPeriod` | integer | `min: 1, max: 65535` | How often sensors are read (seconds). |
| `ReportingPeriod` | integer | `min: 1, max: 65535` | How often data is sent to cloud (seconds). |
| `DigitalOutput1` | boolean | - | Toggle for a physical relay or digital pin. |
| `BatteryLevel` | number | `min: 0, max: 100, readOnly: true` | Battery percentage. |
| `Temperature` | number | `readOnly: true` | Ambient temperature in Celsius. |
| `Humidity` | number | `min: 0, max: 100, readOnly: true` | Relative humidity percentage. |
| `FirmwareVersion` | string | `maxLength: 64, readOnly: true` | Current running firmware version. |
| `DeepSleep` | boolean | - | Put device into low-power sleep mode. |
| `LedIndicator` | boolean | - | Enable/Disable status LEDs. |
| `BuzzerVolume` | integer | `min: 0, max: 10` | Volume level for device buzzer. |
| `Uptime` | integer | `readOnly: true` | Seconds since last boot. |
| `StorageUsage` | integer | `min: 0, max: 100, readOnly: true` | Internal flash usage percentage. |
| `ModemStatsPeriod` | integer | `min: 1, max: 10080` | Interval for reporting network stats. |

---

## Security Layers & Key Management

The system uses two distinct layers of security. It is critical to distinguish between them for correct deployment.

#### 1. Message Signing (Application Layer)
Used to prove the identity of the sender of an `IOTMessage`.
- **Identity Verification:** Only the **message ID** is signed (encrypted with the private key) rather than the entire payload. This design provides:
    - **Performance:** Minimizes CPU usage on resource-constrained IoT devices by avoiding expensive RSA operations on large JSON strings.
    - **Readability:** Allows the message payload to be inspected and routed by intermediaries (if needed) without requiring full decryption, while still guaranteeing that the message identity is authentic.
    - **Integrity & Replay Protection:** Since every message has a unique signed ID and timestamp, the system can detect and reject duplicate or forged messages.
- **Gateway:** Holds its own **RSA Private Key**. This key is used by the `basic-iot-client` to sign the `id` field of every message.
- **Cloud:** Holds its own **RSA Private Key** (defined by `basic.iot.keyPath`) to sign commands sent to gateways. It also stores the **Public Keys** of all registered devices in the database to verify their incoming messages.
- **Format:** RSA PKCS#8 PEM.

#### 2. MQTT TLS Authentication (Transport Layer)
Used to encrypt the communication channel and authenticate the client to the EMQX/MQTT broker.
- **Data Privacy:** While the application layer only signs the ID, the **entire communication** (including the payload) is encrypted at the transport layer using TLS. This avoids the overhead of double-encryption at the application level.
- **CA Certificate (`cacert.pem`):** The Root Certificate of the CA that signed the broker and client certificates. Required by both Gateway and Cloud to verify the broker's identity.
- **Client Certificate (`.crt`):** A certificate proving the identity of the Gateway/Cloud to the broker. Usually issued per `iotId`.
- **Client Private Key (`.key`):** The private key associated with the client certificate.
- **Format:** X.509 certificates and RSA private keys.

---

## Gateway-Centric Architecture

It is important to understand that in this system, **Devices do not communicate directly with the Cloud**.

1.  **Device Definition**: Devices are physically and logically attached to a **Gateway**. The Cloud only knows about a device through its association with a specific Gateway.
2.  **Proxy Communication**: The Gateway acts as a secure proxy. It collects local reports from sensors (using local protocols like Zigbee or Modbus), packages them into the Cloud Protocol format, and signs them using its own credentials.
3.  **Security Boundary**: The Gateway is the security boundary. The Cloud trusts the Gateway to accurately report the state of its connected devices. This simplifies device management, as the Cloud only needs to maintain security relationships (RSA keys) with a relatively small number of Gateways rather than thousands of individual sensors.

---

## MQTT Topic Structure

The system uses a structured topic hierarchy to manage communication between the Cloud and Gateways.

### Cloud Listening Topics
The Basic IoT Service uses a single wildcard subscription to receive standard messages from all gateways. It does **not** need to subscribe to a separate topic for every gateway.
- **Topic**: `GATEWAY/+/OUT`
- **Direction**: Gateway → Cloud
- **Purpose**: Receiving state changes, registration requests, and responses to commands from any authorized gateway. The `+` wildcard allows the cloud to capture messages from any `gatewayId` in a single stream.

### Gateway Listening Topics
Each gateway is responsible for listening to its own specific command topic:
- **Topic**: `GATEWAY/{gatewayId}/IN`
- **Direction**: Cloud → Gateway
- **Purpose**: Receiving commands, configuration updates, and firmware update triggers from the cloud.

### Temporary Reply Topics
When a component (Cloud or Gateway) expects a direct response to a specific request, it may use a temporary topic for the reply.
- **Mechanism**: The `basic-iot-client` can dynamically add a topic to its subscriber (e.g., using a UUID as the topic name) to listen for a one-time response.
- **Cleanup**: Once the response is received or a timeout occurs, the client automatically unsubscribes from the temporary topic to keep the broker's subscription list clean.

---

## Startup MQTT Test Cycle

To ensure the system is fully operational upon startup, the Basic IoT Service performs an automated MQTT test cycle.

### How it Works
1.  **Delay**: After the application context is refreshed, the service waits for a configurable period (default: 60 seconds, defined by `basic.iot.start.delay`).
2.  **Test Message**: The `MqttTestCycleService` generates a unique `IOTMessage` with a random UUID.
3.  **Signing & Transmission**: The message is signed using the server's private key and sent to a dedicated test topic: `mqtt-test`.
4.  **Verification**: The service listens for this message on the same `mqtt-test` topic. Because the server can now derive its own public key, it verifies the signature of its own test message.
5.  **Timeout**: The system waits up to 10 seconds for the message to return.

### Results
-   **Success**: If the message is received and verified, a log entry indicates that the MQTT test cycle passed.
-   **Failure**: If the message is not received within the timeout, or if verification fails, the system logs: **"No MQTT support, period"**. This indicates a critical failure in the MQTT broker connection or the security provider configuration.

---

## Configuration

The Basic IoT Service requires specific configuration in `application.properties` to function correctly.

### Required Properties

| Property | Description | Suggested Value |
| :--- | :--- | :--- |
| `basic.iot.keyPath` | Path to the server's RSA private key (PKCS#8). | `/home/flexicore/certs/server-key.pem` |
| `spring.datasource.hikari.maximum-pool-size` | Number of DB connections (used for message processing semaphores). | `200` |
| `basic.iot.id` | Unique ID for this server instance in the MQTT network. | `playground` |
| `basic.iot.mqtt.enabled` | Whether to enable MQTT communication. If `false`, the service works in "database-only" mode. | `true` |
| `basic.iot.mqtt.url` | The MQTT broker address. | `ssl://mqtt.example.com:8883` |
| `basic.iot.mqtt.certsBaseDir` | Directory containing MQTT TLS certificates and keys. | `/home/flexicore/mqttcerts` |
| `basic.iot.mqtt.caCertificatePath` | MQTT broker CA certificate path. Optional. Defaults to `cacert.pem` under `basic.iot.mqtt.certsBaseDir`. | `/home/flexicore/mqttcerts/cacert.pem` |
| `basic.iot.mqtt.clientCertificatePath` | MQTT client certificate path. Optional. Defaults to `<basic.iot.id>.crt` under `basic.iot.mqtt.certsBaseDir`. | `/home/flexicore/mqttcerts/playground.crt` |
| `basic.iot.mqtt.clientKeyPath` | MQTT client private key path (PKCS#8). Optional. Defaults to `<basic.iot.id>.key` under `basic.iot.mqtt.certsBaseDir`. | `/home/flexicore/mqttcerts/playground.key` |
| `basic.iot.mqtt.clientCertificateBase64` | Base64 or PEM-encoded MQTT client certificate for mutual TLS. Takes precedence over truststore-only TLS when used together with the private key and CA properties. | - |
| `basic.iot.mqtt.clientPrivateKeyBase64` | Base64 or PEM-encoded MQTT client private key for mutual TLS. The key must be PKCS#8. | - |
| `basic.iot.mqtt.caCertificateBase64` | Base64 or PEM-encoded MQTT broker CA certificate for mutual TLS. | - |
| `basic.iot.mqtt.privateKeyAlgorithm` | Private key algorithm used for the MQTT client key. | `RSA` |
| `basic.iot.start.delay` | Seconds to delay the initialization of the MQTT client factory. Used to ensure other critical system components are ready and to prevent immediate startup failure if some configurations are lazily loaded. | `30` |

### Resilient Startup
The service is designed to be resilient to missing or incorrect configuration:
- **`basic.iot.keyPath`**: If the application-level signing key is missing or invalid, the error is logged, and the `privateKey` bean is set to `null`. The service will start but won't be able to sign outgoing messages or verify some incoming ones.
- **Lazy Initialization**: Many IoT-specific beans (like `BasicIOTClient`) are marked as `@Lazy`. This means they won't be initialized until they are actually needed, preventing a failure during the initial Spring context refresh if their dependencies are not yet fully configured.
- **Startup Delay**: The `basic.iot.start.delay` property adds a sleep interval during the initialization of the MQTT factory, providing a buffer for the environment to stabilize.

### Optional/Common Properties

| Property | Description | Default |
| :--- | :--- | :--- |
| `basic.iot.fota.baseUrl` | Base URL for firmware downloads. | - |
| `basic.iot.connectivityCheckInterval` | Interval for checking device connectivity (ms). | `450000` |

---

## Security & Key Generation

The server uses an RSA key pair to sign messages sent to gateways. The private key must be in **PKCS#8 format**. A convenience script `generate_key.sh` is provided in the project root to automate this process.

### Using the Generation Script

The `generate_key.sh` script automates the creation of the RSA key pair, extraction of the public key, and ensures the correct PKCS#8 format.

1.  **Run the script:**
    ```bash
    ./generate_key.sh [optional_target_path]
    ```
2.  **Configuration Detection**: The script automatically checks `application.properties` for the `basic.iot.keyPath` value and uses it as the default target.
3.  **Safety Checks**: If a key already exists at the target location, the script will prompt for confirmation before overwriting.
4.  **Automatic Setup**: It creates any missing parent directories and generates both:
    -   **Private Key (PKCS#8)**: Used by the server for signing (at `basic.iot.keyPath`).
    -   **Public Key**: Generated as `.pub.pem` alongside the private key. This key should be distributed to gateways so they can verify the server's identity.

### Manual Key Generation (OpenSSL)

If you prefer to generate keys manually:

1.  **Generate a 2048-bit RSA Private Key:**
    ```bash
    openssl genrsa -out private.key 2048
    ```

2.  **Convert to PKCS#8 format (Required):**
    ```bash
    openssl pkcs8 -topk8 -inform PEM -outform PEM -nocrypt -in private.key -out server-key.pem
    ```

3.  **Extract the Public Key (for Gateways):**
    ```bash
    openssl rsa -in server-key.pem -pubout -out server-public.pem
    ```

### Key Distribution & Trust

*   **Cloud Verification**: Gateways MUST have the server's **Public Key** to verify signed commands received on the `IN` topic.
*   **Gateway Verification**: The Cloud automatically requests and caches Gateway public keys from the database when a message is received from a specific `gatewayId`.
*   **Deployment**: Ensure the private key at `basic.iot.keyPath` is protected (e.g., `chmod 600`).

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


## MQTT provisioning identity

MQTT provisioning treats `externalId` as the sole creation identity. Gateway protocol IDs are stored as both
`Gateway.externalId` and `Gateway.remoteId`; device IDs are stored as both `Device.externalId` and
`Device.remoteId`. `remoteId` remains the MQTT protocol address, but it is not used to decide whether a new
Gateway or Device must be created. Pending gateways, gateways, devices, device types, state schemas and map
icons created by this flow are resolved only by deterministic external IDs. Display names are never fallback
identity keys. A supplied `deviceTypeExternalId` is mandatory when MQTT creates a new device.
