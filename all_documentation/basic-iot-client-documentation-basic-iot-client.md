# Basic IoT Client

## Overview
The Basic IoT Client provides a client-side library for interacting with the Basic IoT Service. It facilitates communication for external devices or services that need to report state or receive commands from a FlexiCore-based cloud.

## Purpose
The primary role of the Basic IoT Client is to enable **Edge Gateways** to "talk" with the **Cloud**. In a typical IoT architecture:
1.  **Edge Gateway:** A local device (e.g., Raspberry Pi, Industrial PC) that collects data from local sensors/actuators.
2.  **Basic IoT Client:** Embedded in the Edge Gateway's software to manage secure, signed communication.
3.  **Basic IoT Service (Cloud):** Receives state reports, manages device connectivity, and sends commands back to the gateways.

## Secure Communication
The system utilizes two distinct layers of security to protect data and verify identities.

### 1. Message Signing (Application Layer)
This layer ensures that every message received by the cloud was actually sent by the claimed gateway and has not been tampered with.
-   **Signing Key:** The Gateway holds an **RSA Private Key**.
-   **Operation:** The client signs the unique `id` of the `IOTMessage` using `SHA256withRSA`.
-   **Verification:** The Cloud uses the Gateway's **Public Key** (stored in the database) to verify the signature.
-   **Location:** Usually stored as a PEM file on the gateway (e.g., `/home/gateway/keys/signing.key`).

### 2. MQTT TLS Authentication (Transport Layer)
This layer provides encryption for the data in transit and authenticates the Gateway's connection to the MQTT broker (e.g., EMQX).
-   **CA Certificate (`cacert.pem`):** Required to verify the MQTT broker's identity.
-   **Client Certificate (`.crt`):** A certificate issued by the CA that identifies the gateway to the broker.
-   **Client Private Key (`.key`):** The private key corresponding to the client certificate.
-   **Location:** Typically stored in a dedicated certs folder (e.g., `/home/gateway/certs/`).

---

## Java Example
Using the library in a Java-based edge application:

```java
// Initialize the client
BasicIOTClient client = new BasicIOTClient(
    "my-gateway-001",           // gatewayId
    myPrivateKey,               // RSA Private Key for signing
    objectMapper,               // Jackson ObjectMapper
    List.of(new MySubscriber()) // Message subscribers
);

// Report device state
StateChanged state = new StateChanged()
    .setDeviceId("sensor-1")
    .setDeviceType("temperature")
    .setValue("temp", 22.5)
    .setStatus("OK");

client.sendMessage(state);
```

## Node-RED Integration
Node-RED is often used on local edge instances to bridge various industrial protocols to the cloud. You can map Node-RED local instances to communicate with a FlexiCore-based cloud using the Basic IoT protocol.

### Architecture
`Sensors -> [Node-RED (Local)] -> [MQTT Broker] -> [Basic IoT Service (Cloud)]`

### Implementation Options
You can integrate Node-RED with the Basic IoT Service in two ways:

#### Option A: Native Node-RED (JavaScript)
Since Node-RED is JavaScript-based, you can use the standard MQTT nodes. However, to satisfy the Basic IoT Service's security requirements, you must format the JSON payload correctly and handle RSA signing in JavaScript.

##### 1. Payload Structure
The cloud expects a JSON object matching the `IOTMessage` structure:
```json
{
  "messageType": "com.wizzdi.basic.iot.client.StateChanged",
  "id": "uuid-v4-string",
  "gatewayId": "my-gateway-001",
  "sentAt": "2026-06-20T10:30:00Z",
  "deviceId": "local-device-id",
  "temp": 22.5,
  "signature": "BASE64_SIGNATURE_OF_ID"
}
```

##### 2. Node-RED Flow Example
-   **Function Node:** Calculate the signature and format the message.
    ```javascript
    const crypto = require('crypto');
    const privateKey = '...'; // Your PEM private key
    const msgId = msg.payload.id || crypto.randomUUID();
    
    // Sign the ID
    const sign = crypto.createSign('SHA256');
    sign.update(msgId);
    const signature = sign.sign(privateKey, 'base64');
    
    msg.payload = {
        messageType: "com.wizzdi.basic.iot.client.StateChanged",
        id: msgId,
        gatewayId: "my-gateway-001",
        sentAt: new Date().toISOString(),
        deviceId: msg.payload.deviceId,
        status: "OK",
        temp: msg.payload.temperature,
        signature: signature
    };
    return msg;
    ```
-   **MQTT Out Node:** Publish to the topic `GATEWAY/my-gateway-001/OUT`.

#### Option B: Java Bridge (Recommended for Robustness)
For many deployments, it is better to run a small Java application (the "Java Bridge") alongside Node-RED.

**Why use a Java Bridge?**
-   **Security Abstraction:** The Java Bridge uses the official `basic-iot-client` library, which handles RSA signing, message formatting, and retry logic automatically.
-   **Performance:** Java's RSA implementation is highly optimized.
-   **Stability:** Offloads protocol complexity from the Node-RED flow, making the flows easier to maintain.

**Architecture:**
`Sensors -> [Node-RED] -> (Local MQTT/TCP) -> [Java Bridge (Basic IoT Client)] -> (Cloud MQTT) -> [Basic IoT Service]`

In this setup, Node-RED sends simple JSON to a local topic (e.g., `local/sensor/data`). The Java Bridge subscribes to this local topic, wraps the data in an `IOTMessage` using the `basic-iot-client` library, and handles the secure communication with the cloud.

### Connectivity Mapping
When the Edge Gateway connects, the Basic IoT Service tracks its `lastSeen` timestamp. Node-RED should send periodic `KeepAlive` messages or regular `StateChanged` updates to ensure the cloud marks the gateway as "Online".
