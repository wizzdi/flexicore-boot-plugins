# Tenant Data Administration Service

This plugin provides multi-tenant inventory and transactional hard deletion.

## Authorization

Both APIs require the authenticated user to be either:

- associated with a `Role` whose `superAdmin` flag is enabled; or
- a tenant administrator for every explicitly requested tenant.

A missing tenant, empty `tenantIds` array, or unauthorized requested tenant returns HTTP 400. Authorization is validated independently for each explicit tenant ID before inventory or deletion starts.

## Tenant selection and nesting

Both requests accept an array:

```json
{
  "tenantIds": [
    "parent-tenant-uuid",
    "child-tenant-uuid",
    "independent-tenant-uuid"
  ]
}
```

The legacy `tenantId` field remains accepted and is merged with `tenantIds`. Blank values and duplicate IDs are removed.

For each requested tenant, the plugin includes every recursively owned tenant. Overlapping trees are merged by tenant ID. When a parent and one of its children are both requested, the child is included only once.

The plugin recalculates depth from the persisted `SecurityTenant.tenant` ownership links across the merged set. Tenant deletion order is therefore independent of request-array order and always places descendants before ancestors. An ownership cycle returns HTTP 409.

Responses include:

- `requestedTenantIds` and `requestedTenantExternalIds`;
- the merged `tenantTree` with owner and calculated depth;
- `tenantDeletionOrder`, ordered deepest-first;
- the existing entity `deletionOrder`, ordered from dependent entity type to dependency.

For backward compatibility, response fields `tenantId` and `tenantExternalId` are populated only when exactly one tenant was explicitly requested; for multi-tenant requests they are `null`.

## Inventory

`POST /plugins/TenantDataAdministration/getTenantData`

```json
{
  "tenantIds": [
    "tenant-a-uuid",
    "tenant-b-uuid"
  ]
}
```

The response dynamically inspects the active JPA metamodel. It returns concrete entity classes and aggregated counts across the merged requested tenant trees. Actual JPA entity inheritance is grouped by concrete type without assuming that every `Baseclass` table uses a discriminator.

## Hard deletion

`POST /plugins/TenantDataAdministration/deleteTenantData`

```json
{
  "tenantIds": [
    "tenant-a-uuid",
    "tenant-b-uuid"
  ],
  "deleteSecurityObjects": false,
  "dry": true
}
```

The complete real deletion runs in one transaction. When `dry` is `false`, requested tenants and their owned tenant trees are locked, merged, and processed according to the ownership-derived `tenantDeletionOrder`. Loaded entity roots are ordered from dependents to dependencies by their owning JPA associations.

When `dry` is `true`, no records are changed. The response contains the exact records that would be deleted, grouped by dynamically inferred concrete entity type. Every record entry includes its identifier, tenant identifier, and optional `name` and `externalId` values when those properties exist.

The same `recordsByType` structure is returned after a real deletion. `totalSelectedRecords` is the planned record count, while `totalDeletedRecords` is zero for a dry run and the committed deletion count for a real run.

When `deleteSecurityObjects` is `false`, tenant content is hard-deleted while users, roles, permissions, membership links and tenant records remain.

When `deleteSecurityObjects` is `true`, those security records and all selected tenant records are also hard-deleted. The authenticated user may therefore be deleted by the same transaction.

The service verifies that every selected record is gone before commit. A foreign-key reference from outside the merged selected tenant trees returns HTTP 409 and rolls back the entire operation rather than deleting data from another tenant.

The inventory also discovers tenant-scoped entities that do not inherit `Baseclass`, such as the audit log's scalar `tenantId`. After a successful deletion, the deletion request's own audit entry is deliberately persisted without a tenant association so auditing cannot recreate data in a deleted tenant.
