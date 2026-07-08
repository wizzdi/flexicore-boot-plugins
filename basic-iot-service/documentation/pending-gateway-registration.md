# Pending Gateway Registration and Approval

## Purpose

A `PendingGateway` is the temporary onboarding record for a gateway that has announced itself but has not yet been approved as an operational `Gateway`.

The gateway self-registration flow stores the gateway identity, public key, signature capability and optional Internet-connection location. An operator later approves the pending record. Approval creates the real `Gateway`, creates the gateway security user, creates or reuses the map point, and links the `PendingGateway` to the approved gateway.

This prevents unknown hardware from immediately operating as a trusted gateway.

## Main entities

| Entity | Purpose |
| --- | --- |
| `PendingGateway` | Temporary onboarding record. Contains the data sent by the gateway during registration. |
| `Gateway` | Approved operational gateway. Its `remoteId` is copied from `PendingGateway.gatewayId`. |
| `SecurityUser` | Automatically created during approval. The gateway uses this identity after approval. |
| `MappedPOI` | Map point. A pending gateway uses the pending-gateway icon; after approval the point is switched to the gateway icon and linked to the approved `Gateway`. |
| `MapIcon` | Visual icon used by the map. `pending-gateway` is used for pending records; `default-gateway-icon` / **Default Gateway Icon** is used for approved gateways. If that gateway icon does not exist, the server creates a generic one automatically. |

## Registration entry points

### 1. MQTT registration message

Message type:

```text
com.wizzdi.basic.iot.client.RegisterGateway
```

The MQTT registration message extends `IOTMessage`, so the gateway identity is taken from `IOTMessage.gatewayId`. The message body adds the registration fields:

```json
{
  "messageType": "com.wizzdi.basic.iot.client.RegisterGateway",
  "id": "message-uuid",
  "gatewayId": "edge-gateway-001",
  "publicKey": "-----BEGIN PUBLIC KEY-----...-----END PUBLIC KEY-----",
  "noSignatureCapabilities": false,
  "lat": 32.0853,
  "lon": 34.7818
}
```

`RegisterGateway.isRequireAuthentication()` returns `false`, because the gateway is not approved yet and therefore cannot authenticate as a known gateway. The server still records the public key so that approved traffic can be verified later.

The standalone gateway sends `lat` and `lon` by default when it can resolve them from the active Internet connection. It does not read gateway coordinates from `gateway.properties` and it does not infer the gateway position from configured local devices. If public Internet geolocation is unavailable, registration is still sent without coordinates.

### 2. REST self-registration

Endpoint:

```http
POST /plugins/PendingGateway/registerGateway
Content-Type: application/json
```

This endpoint receives a `PendingGatewayCreate` and creates a `PendingGateway` using the service admin security context. It is intended for gateway self-registration or controlled integration tooling, not for UI approval.

Minimal request:

```json
{
  "name": "edge-gateway-001",
  "gatewayId": "edge-gateway-001",
  "publicKey": "-----BEGIN PUBLIC KEY-----...-----END PUBLIC KEY-----",
  "noSignatureCapabilities": false,
  "lat": 32.0853,
  "lon": 34.7818
}
```

Validation behavior for this endpoint:

1. `registeredGatewayId`, if sent, is ignored and cleared.
2. `gatewayId` is required.
3. `publicKey` is required.
4. `lat` and `lon` are optional but should be sent when the gateway can infer its location.
5. The pending gateway is persisted.
6. When both `lat` and `lon` are provided, a `MappedPOI` is created or updated with the pending-gateway map icon.

### 3. Admin/manual creation

Endpoint:

```http
POST /plugins/PendingGateway/createPendingGateway
Content-Type: application/json
```

This creates a `PendingGateway` with the caller's `SecurityContext`. It is useful for admin-created pending records, imports, tests or tooling.

Normal onboarding should leave `registeredGatewayId` empty. Approval sets the `registeredGateway` link automatically.

## `PendingGatewayCreate` fields

`PendingGatewayCreate` extends `BasicCreate`, so it also supports the normal FlexiCore basic fields such as `name`, `description` and `unsetProperties`.

| Field | Required for `/registerGateway` | Purpose |
| --- | --- | --- |
| `name` | Optional | Display name for the pending record. In the MQTT flow the server sets it to `gatewayId`. Copied to `Gateway.name` on approval. |
| `description` | Optional | Operator note or source description. Copied to `Gateway.description` on approval. |
| `gatewayId` | Yes | Stable external gateway identifier. Copied to `Gateway.remoteId` on approval and used by the MQTT protocol. |
| `publicKey` | Yes | Gateway public key. Stored on the pending record and copied to `Gateway.publicKey` on approval. |
| `registeredGatewayId` | No | Internal link to an approved gateway. It is cleared by `/registerGateway`; normal approval sets this automatically. |
| `noSignatureCapabilities` | Optional | Indicates that the gateway cannot sign messages. Copied to the approved `Gateway`. |
| `lat` | Optional | Gateway latitude resolved by the gateway from its active Internet connection. Stored on the pending record and used for the pending map point and approved gateway map point. |
| `lon` | Optional | Gateway longitude resolved by the gateway from its active Internet connection. Stored on the pending record and used for the pending map point and approved gateway map point. |

## MQTT registration response

The MQTT path returns a `RegisterGatewayReceived` message.

| Field | Purpose |
| --- | --- |
| `registerGatewayId` | Original registration message `id`, used by the sender to correlate the response. |
| `registrationStatus` | `registered`, `alreadyPending`, `alreadyRegistered`, or `invalid`. |
| `registeredGatewayRemoteId` | The external gateway id involved in the registration. |
| `pendingGatewayId` | Internal ID of the pending gateway when a new or already-pending record exists. |
| `registeredGatewayId` | Internal ID of the approved gateway when the gateway is already approved. |
| `registrationMessage` | Human-readable status message. |

Server-side MQTT registration checks:

1. Reject missing or blank `gatewayId` with `registrationStatus = invalid`.
2. Trim `gatewayId`.
3. If an approved `Gateway` already exists with `remoteId = gatewayId`, return `alreadyRegistered`.
4. If an unapproved `PendingGateway` already exists with this `gatewayId`, return `alreadyPending`. If the new message includes `lat`/`lon`, the existing pending record and pending map point are updated.
5. If a historical `PendingGateway` already points to an approved gateway, return `alreadyRegistered`.
6. Otherwise create a new `PendingGateway` with `gatewayId`, `publicKey`, `noSignatureCapabilities`, `lat`, `lon`, and `name = gatewayId`, then return `registered`.

The registration logic synchronizes by `gatewayId` to avoid duplicate pending records during concurrent registration attempts.

## Listing pending gateways for approval

Endpoint:

```http
POST /plugins/PendingGateway/getAllPendingGateways
Content-Type: application/json
```

Example request for unapproved pending gateways:

```json
{
  "currentPage": 0,
  "pageSize": 50,
  "registered": false,
  "basicPropertiesFilter": {
    "nameLike": "%edge%"
  }
}
```

Relevant filter fields:

| Field | Purpose |
| --- | --- |
| `currentPage` | Zero-based page index. |
| `pageSize` | Number of rows to return. |
| `registered` | `false` returns pending records that have not been approved. `true` returns records already linked to a `Gateway`. |
| `gatewayIds` | Restricts the result to specific external gateway ids. This is for listing/filtering only; approval uses `pendingGatewayIds`. |
| `basicPropertiesFilter.nameLike` | Server-side name filter. Use `%value%` for contains matching. |

## Approval / registering pending gateways

Endpoint:

```http
POST /plugins/Gateway/approveGateways
Content-Type: application/json
```

Approval does **not** receive gateway data such as `gatewayId`, `publicKey`, `name`, or `noSignatureCapabilities`. Everything is already stored on the `PendingGateway`. The approval request only needs the internal pending gateway IDs and an optional target tenant.

Example request approving one pending gateway:

```json
{
  "pendingGatewayIds": ["d4b018f5-c6f5-4e42-a699-14a226c00ff8"]
}
```

Example request approving multiple pending gateways into a specific tenant:

```json
{
  "pendingGatewayIds": [
    "d4b018f5-c6f5-4e42-a699-14a226c00ff8",
    "66d97d44-2a93-4d5a-93a7-fc5cc33ed51e"
  ],
  "tenantId": "tenant-id-to-create-in"
}
```

### `ApproveGatewaysRequest` fields

| Field | Required | Purpose |
| --- | --- | --- |
| `pendingGatewayIds` | Yes | Internal IDs of the `PendingGateway` rows to approve. Each ID must exist and must not already have `registeredGateway` set. |
| `tenantId` | No | Tenant where the approved gateways, gateway users and map points are created. If empty or omitted, the current user's write-to tenant (`SecurityContext.tenantToCreateIn`) is used. |

For every requested `PendingGateway`, approval does the following:

1. Validates that the pending gateway exists and is not already registered.
2. Resolves `tenantId`; if omitted, uses the current user's write-to tenant.
3. Creates a dedicated `SecurityUser` named `<gatewayId>-User`.
4. Creates a `TenantToUser` link for that gateway user in the selected tenant.
5. Creates a `Gateway` with values copied from the pending record:
   - `remoteId = PendingGateway.gatewayId`
   - `publicKey = PendingGateway.publicKey`
   - `noSignatureCapabilities = PendingGateway.noSignatureCapabilities`
   - `reportedLat = PendingGateway.lat`
   - `reportedLon = PendingGateway.lon`
   - `name = PendingGateway.name`
   - `description = PendingGateway.description`
   - `approvingUser = current user`
   - `gatewayUser = created gateway user`
6. Reuses the pending `MappedPOI` if it exists, switches it from the pending-gateway icon to **Default Gateway Icon** (`externalId = default-gateway-icon`), and relinks it from `PendingGateway` to the created `Gateway`. If the default gateway map icon does not exist, the server creates a generic map icon first.
7. If no pending map point exists, creates a new `MappedPOI` for the gateway using the stored pending `lat`/`lon`.
8. Updates `PendingGateway.registeredGateway` to point to the created `Gateway`.
9. Invalidates the public-key cache for the gateway id.

After approval, the gateway is operational and can participate in the normal IoT protocol, subject to the `noSignatureCapabilities` flag.

## Practical notes

- Use a stable `gatewayId`; changing it creates a different onboarding identity.
- The UI approval action should call `/plugins/Gateway/approveGateways` with `pendingGatewayIds`, not `/plugins/PendingGateway/registerGateway`.
- Do not send gateway fields during approval. Approval intentionally reads them from the pending record to avoid stale or edited client data.
- Keep `gatewayId` unique. Registration uses it to detect existing gateways and pending records.
- For normal self-registration, do not send `registeredGatewayId`.
- For secure deployments, keep `noSignatureCapabilities = false` and supply a valid RSA public key.
- A gateway should retry registration until it receives a response indicating that the registration is pending or already approved, then stop retrying until restart or reconfiguration.
- Gateway onboarding coordinates should come from the gateway's Internet connection. Do not configure static gateway coordinates unless a different future registration method explicitly supports that.
