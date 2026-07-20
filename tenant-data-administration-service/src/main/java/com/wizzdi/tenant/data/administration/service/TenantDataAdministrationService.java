package com.wizzdi.tenant.data.administration.service;

import com.flexicore.annotations.rest.All;
import com.flexicore.model.Role;
import com.flexicore.model.SecurityTenant;
import com.flexicore.model.SecurityUser;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import com.wizzdi.flexicore.security.configuration.SecurityContext;
import com.wizzdi.flexicore.security.service.SecurityOperationService;
import com.wizzdi.tenant.data.administration.data.TenantDataAdministrationRepository;
import com.wizzdi.tenant.data.administration.data.TenantDataAdministrationRepository.DeletePlan;
import com.wizzdi.tenant.data.administration.data.TenantDataAdministrationRepository.DeletionRecordSnapshot;
import com.wizzdi.tenant.data.administration.data.TenantDataAdministrationRepository.DeletionSnapshot;
import com.wizzdi.tenant.data.administration.data.TenantDataAdministrationRepository.DeletionTypeRecordsSnapshot;
import com.wizzdi.tenant.data.administration.data.TenantDataAdministrationRepository.InventorySnapshot;
import com.wizzdi.tenant.data.administration.data.TenantDataAdministrationRepository.TenantNode;
import com.wizzdi.tenant.data.administration.data.TenantDataAdministrationRepository.TypeCountSnapshot;
import com.wizzdi.tenant.data.administration.request.TenantDataDeleteRequest;
import com.wizzdi.tenant.data.administration.request.TenantDataRequest;
import com.wizzdi.tenant.data.administration.response.TenantDataDeletionResponse;
import com.wizzdi.tenant.data.administration.response.TenantDataInventoryResponse;
import com.wizzdi.tenant.data.administration.response.TenantDeletionRecord;
import com.wizzdi.tenant.data.administration.response.TenantDeletionTypeRecords;
import com.wizzdi.tenant.data.administration.response.TenantTreeEntry;
import com.wizzdi.tenant.data.administration.response.TenantTypeCount;
import jakarta.persistence.PersistenceException;
import org.pf4j.Extension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

@Component
@Extension
public class TenantDataAdministrationService implements Plugin {

    private static final Logger logger = LoggerFactory.getLogger(TenantDataAdministrationService.class);
    private static final String MDC_OPERATION_ID = "tenantDataOperationId";
    private static final String ALL_OPERATION_ID =
            SecurityOperationService.getStandardAccessId(All.class);

    @Autowired
    private TenantDataAdministrationRepository repository;

    @Autowired
    private PlatformTransactionManager transactionManager;

    @Transactional(readOnly = true)
    public TenantDataInventoryResponse getTenantData(
            TenantDataRequest request,
            SecurityContext securityContext) {
        OperationLogContext operation = beginOperation(
                "INVENTORY",
                request,
                securityContext,
                null,
                null);
        try {
            List<String> requestedTenantIds = validateTenantIds(request);
            logger.info(
                    "TENANT_DATA_ADMIN_STEP operationId={} action=INVENTORY step=TENANT_IDS_VALIDATED tenantIds={}",
                    operation.operationId(),
                    requestedTenantIds);

            List<SecurityTenant> requestedTenants = requireTenants(requestedTenantIds, false);
            logger.info(
                    "TENANT_DATA_ADMIN_STEP operationId={} action=INVENTORY step=TENANTS_RESOLVED tenants={}",
                    operation.operationId(),
                    tenantSummary(requestedTenants));
            requestedTenants.forEach(tenant -> validateAdministrator(tenant, securityContext));

            List<TenantNode> tenantTree = mergeTenantTrees(requestedTenants, false);
            logger.info(
                    "TENANT_DATA_ADMIN_STEP operationId={} action=INVENTORY step=TENANT_TREE_RESOLVED tenantCount={} tree={}",
                    operation.operationId(),
                    tenantTree.size(),
                    tenantTreeSummary(tenantTree));

            InventorySnapshot inventory = repository.inventory(tenantIds(tenantTree));
            TenantDataInventoryResponse response = inventoryResponse(requestedTenants, tenantTree, inventory);
            logger.info(
                    "TENANT_DATA_ADMIN_REQUEST_END operationId={} action=INVENTORY status=SUCCESS tenantCount={} entityTypeCount={} totalRecords={} durationMs={}",
                    operation.operationId(),
                    tenantTree.size(),
                    inventory.recordsByType().size(),
                    inventory.totalRecords(),
                    elapsedMillis(operation.startedNanos()));
            return response;
        } catch (ResponseStatusException exception) {
            logValidationFailure(operation, exception);
            throw exception;
        } catch (RuntimeException exception) {
            logUnexpectedFailure(operation, exception);
            throw exception;
        } finally {
            endOperation(operation);
        }
    }

    public TenantDataDeletionResponse deleteTenantData(
            TenantDataDeleteRequest request,
            SecurityContext securityContext) {
        boolean dry = request != null && request.isDry();
        boolean deleteSecurityObjects = request != null && request.isDeleteSecurityObjects();
        OperationLogContext operation = beginOperation(
                "DELETE",
                request,
                securityContext,
                dry,
                deleteSecurityObjects);
        try {
            TenantDataDeletionResponse response;
            if (dry) {
                response = deleteTenantDataInternal(request, securityContext, true);
            } else {
                logger.info(
                        "TENANT_DATA_ADMIN_TRANSACTION operationId={} action=DELETE status=BEGIN",
                        operation.operationId());
                TransactionTemplate transactionTemplate = new TransactionTemplate(transactionManager);
                response = transactionTemplate.execute(status ->
                        deleteTenantDataInternal(request, securityContext, false));
                logger.info(
                        "TENANT_DATA_ADMIN_TRANSACTION operationId={} action=DELETE status=COMMITTED",
                        operation.operationId());
            }

            logger.info(
                    "TENANT_DATA_ADMIN_REQUEST_END operationId={} action=DELETE status=SUCCESS dry={} deleteSecurityObjects={} requestedTenantCount={} selectedRecords={} deletedRecords={} durationMs={}",
                    operation.operationId(),
                    dry,
                    deleteSecurityObjects,
                    response == null ? 0 : response.requestedTenantIds().size(),
                    response == null ? 0 : response.totalSelectedRecords(),
                    response == null ? 0 : response.totalDeletedRecords(),
                    elapsedMillis(operation.startedNanos()));
            return response;
        } catch (ResponseStatusException exception) {
            logValidationFailure(operation, exception);
            throw exception;
        } catch (RuntimeException exception) {
            logUnexpectedFailure(operation, exception);
            throw exception;
        } finally {
            endOperation(operation);
        }
    }

    private TenantDataDeletionResponse deleteTenantDataInternal(
            TenantDataDeleteRequest request,
            SecurityContext securityContext,
            boolean dry) {
        List<String> requestedTenantIds = validateTenantIds(request);
        boolean deleteSecurityObjects = request != null && request.isDeleteSecurityObjects();
        logger.info(
                "TENANT_DATA_ADMIN_STEP operationId={} action=DELETE step=TENANT_IDS_VALIDATED dry={} deleteSecurityObjects={} tenantIds={}",
                currentOperationId(),
                dry,
                deleteSecurityObjects,
                requestedTenantIds);

        List<SecurityTenant> requestedTenants = requireTenants(requestedTenantIds, !dry);
        logger.info(
                "TENANT_DATA_ADMIN_STEP operationId={} action=DELETE step=TENANTS_RESOLVED lockForDeletion={} tenants={}",
                currentOperationId(),
                !dry,
                tenantSummary(requestedTenants));
        requestedTenants.forEach(tenant -> validateAdministrator(tenant, securityContext));

        List<TenantNode> tenantTree = mergeTenantTrees(requestedTenants, !dry);
        Set<String> tenantIds = tenantIds(tenantTree);
        logger.info(
                "TENANT_DATA_ADMIN_STEP operationId={} action=DELETE step=TENANT_TREE_RESOLVED tenantCount={} tree={}",
                currentOperationId(),
                tenantTree.size(),
                tenantTreeSummary(tenantTree));

        DeletePlan deletePlan = repository.buildDeletePlan(deleteSecurityObjects);
        logger.info(
                "TENANT_DATA_ADMIN_STEP operationId={} action=DELETE step=DELETE_PLAN_READY entityRootCount={} associationClearCount={} deletionOrder={} associationsToClear={}",
                currentOperationId(),
                deletePlan.deletionOrder().size(),
                deletePlan.associationsToClear().size(),
                deletePlan.deletionOrder().stream()
                        .map(root -> root.javaType().getCanonicalName())
                        .toList(),
                deletePlan.associationsToClear().stream()
                        .map(association -> association.declaringJavaType().getCanonicalName()
                                + "." + association.attributeName())
                        .toList());

        DeletionSnapshot selectedRecords = repository.deletionSnapshot(
                tenantIds,
                deletePlan,
                deleteSecurityObjects);
        logger.info(
                "TENANT_DATA_ADMIN_STEP operationId={} action=DELETE step=RECORD_SNAPSHOT_READY selectedRecords={} recordsByType={}",
                currentOperationId(),
                selectedRecords.totalRecords(),
                deletionTypeCountSummary(selectedRecords));

        boolean requestingUserSelectedForDeletion = deleteSecurityObjects
                && requestingUserBelongsToDeletedTenant(securityContext, tenantIds);
        long expectedDeleted = selectedRecords.totalRecords();
        long actualDeleted = 0;

        List<TenantNode> deepestFirst = tenantTree.stream()
                .sorted(Comparator.comparingInt(TenantNode::depth).reversed()
                        .thenComparing(TenantNode::tenantId))
                .toList();
        if (!dry) {
            try {
                for (TenantNode node : deepestFirst) {
                    logger.info(
                            "TENANT_DATA_ADMIN_TENANT_DELETE_START operationId={} tenantId={} externalId={} depth={} deleteSecurityObjects={}",
                            currentOperationId(),
                            node.tenantId(),
                            node.externalId(),
                            node.depth(),
                            deleteSecurityObjects);
                    Map<String, Integer> deletedByType = repository.deleteTenantData(
                            node.tenantId(),
                            deletePlan);
                    long tenantDeleted = deletedByType.values().stream()
                            .mapToLong(Integer::longValue)
                            .sum();
                    int tenantRecordDeleted = 0;
                    if (deleteSecurityObjects) {
                        tenantRecordDeleted = repository.deleteTenantRecord(node.tenantId());
                        tenantDeleted += tenantRecordDeleted;
                    }
                    actualDeleted += tenantDeleted;
                    logger.info(
                            "TENANT_DATA_ADMIN_TENANT_DELETE_END operationId={} tenantId={} deletedRecords={} tenantRecordDeleted={} deletedByType={}",
                            currentOperationId(),
                            node.tenantId(),
                            tenantDeleted,
                            tenantRecordDeleted,
                            nonZeroCounts(deletedByType));
                }
            } catch (PersistenceException exception) {
                logger.error(
                        "TENANT_DATA_ADMIN_DELETE_BLOCKED operationId={} tenantIds={} rootCause={}",
                        currentOperationId(),
                        tenantIds,
                        rootMessage(exception),
                        exception);
                throw new ResponseStatusException(
                        HttpStatus.CONFLICT,
                        "Hard deletion was blocked by a database relationship outside the selected tenant trees; "
                                + "no deletion was committed. " + rootMessage(exception),
                        exception);
            }

            DeletionSnapshot remaining = repository.deletionSnapshot(
                    tenantIds,
                    deletePlan,
                    deleteSecurityObjects);
            logger.info(
                    "TENANT_DATA_ADMIN_STEP operationId={} action=DELETE step=POST_DELETE_VERIFICATION expectedDeleted={} actualDeleted={} remainingRecords={} remainingByType={}",
                    currentOperationId(),
                    expectedDeleted,
                    actualDeleted,
                    remaining.totalRecords(),
                    deletionTypeCountSummary(remaining));
            if (remaining.totalRecords() > 0) {
                throw new IllegalStateException(
                        "Tenant hard deletion verification failed; remaining records: "
                                + remaining.recordsByType());
            }
            if (actualDeleted != expectedDeleted) {
                throw new IllegalStateException(
                        "Tenant hard deletion count mismatch; expected " + expectedDeleted
                                + " records but deleted " + actualDeleted);
            }
            repository.evictJpaCache();
        }

        List<String> tenantDeletionOrder = deepestFirst.stream()
                .map(TenantNode::tenantId)
                .toList();
        List<String> selectedTenantIds = deleteSecurityObjects
                ? tenantDeletionOrder
                : List.of();
        List<String> deletedTenantIds = !dry && deleteSecurityObjects
                ? selectedTenantIds
                : List.of();
        List<String> retainedTenantIds = dry || !deleteSecurityObjects
                ? tenantTree.stream().map(TenantNode::tenantId).toList()
                : List.of();
        List<String> deletionOrder = new ArrayList<>(deletePlan.deletionOrder().stream()
                .map(root -> root.javaType().getCanonicalName())
                .toList());
        if (deleteSecurityObjects) {
            deletionOrder.add(SecurityTenant.class.getCanonicalName());
        }

        return new TenantDataDeletionResponse(
                singleTenantId(requestedTenants),
                singleTenantExternalId(requestedTenants),
                requestedTenants.stream().map(SecurityTenant::getId).toList(),
                requestedTenants.stream().map(SecurityTenant::getExternalId).toList(),
                dry,
                !dry,
                deleteSecurityObjects,
                requestingUserSelectedForDeletion,
                !dry && requestingUserSelectedForDeletion,
                selectedTenantIds,
                deletedTenantIds,
                retainedTenantIds,
                tenantDeletionOrder,
                deletionOrder,
                selectedRecords.recordsByType().stream()
                        .map(this::toDeletionResponse)
                        .toList(),
                expectedDeleted,
                actualDeleted);
    }

    private TenantDeletionTypeRecords toDeletionResponse(DeletionTypeRecordsSnapshot type) {
        return new TenantDeletionTypeRecords(
                type.clazz(),
                type.simpleName(),
                type.tableName(),
                type.securityData(),
                type.count(),
                type.records().stream()
                        .map(this::toDeletionResponse)
                        .toList());
    }

    private TenantDeletionRecord toDeletionResponse(DeletionRecordSnapshot record) {
        return new TenantDeletionRecord(
                record.id(),
                record.tenantId(),
                record.name(),
                record.externalId());
    }

    private TenantDataInventoryResponse inventoryResponse(
            List<SecurityTenant> requestedTenants,
            List<TenantNode> tenantTree,
            InventorySnapshot inventory) {
        List<TenantTreeEntry> tree = tenantTree.stream()
                .map(node -> new TenantTreeEntry(
                        node.tenantId(),
                        node.externalId(),
                        node.name(),
                        node.ownerTenantId(),
                        node.depth()))
                .toList();
        List<TenantTypeCount> counts = inventory.recordsByType().stream()
                .map(this::toResponse)
                .toList();
        return new TenantDataInventoryResponse(
                singleTenantId(requestedTenants),
                singleTenantExternalId(requestedTenants),
                requestedTenants.stream().map(SecurityTenant::getId).toList(),
                requestedTenants.stream().map(SecurityTenant::getExternalId).toList(),
                tree,
                counts,
                inventory.dynamicallyDiscoveredEntityRoots(),
                inventory.totalRecords());
    }

    private TenantTypeCount toResponse(TypeCountSnapshot type) {
        return new TenantTypeCount(
                type.clazz(),
                type.simpleName(),
                type.tableName(),
                type.count(),
                type.securityData());
    }

    private List<SecurityTenant> requireTenants(
            List<String> requestedTenantIds,
            boolean lockForDeletion) {
        Map<String, SecurityTenant> byId = new LinkedHashMap<>();
        requestedTenantIds.stream()
                .sorted()
                .forEach(tenantId -> byId.put(
                        tenantId,
                        requireTenant(tenantId, lockForDeletion)));
        return requestedTenantIds.stream()
                .map(byId::get)
                .toList();
    }

    private SecurityTenant requireTenant(String tenantId, boolean lockForDeletion) {
        logger.info(
                "TENANT_DATA_ADMIN_TENANT_LOOKUP operationId={} tenantId={} lockForDeletion={}",
                currentOperationId(),
                tenantId,
                lockForDeletion);
        SecurityTenant tenant = repository.findTenant(tenantId, lockForDeletion);
        if (tenant == null) {
            logger.warn(
                    "TENANT_DATA_ADMIN_TENANT_LOOKUP operationId={} tenantId={} lockForDeletion={} result=NOT_FOUND",
                    currentOperationId(),
                    tenantId,
                    lockForDeletion);
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "No SecurityTenant with id " + tenantId);
        }
        logger.info(
                "TENANT_DATA_ADMIN_TENANT_LOOKUP operationId={} tenantId={} lockForDeletion={} result=FOUND externalId={} ownerTenantId={}",
                currentOperationId(),
                tenantId,
                lockForDeletion,
                tenant.getExternalId(),
                tenant.getTenant() == null ? null : tenant.getTenant().getId());
        return tenant;
    }

    private List<TenantNode> mergeTenantTrees(
            List<SecurityTenant> requestedTenants,
            boolean lockForDeletion) {
        Map<String, TenantNode> merged = new LinkedHashMap<>();
        requestedTenants.stream()
                .sorted(Comparator.comparing(SecurityTenant::getId))
                .forEach(tenant -> repository.findTenantTree(tenant, lockForDeletion)
                        .forEach(node -> merged.putIfAbsent(node.tenantId(), node)));

        Map<String, Integer> depths = new LinkedHashMap<>();
        for (String tenantId : merged.keySet()) {
            tenantDepth(tenantId, merged, depths, new LinkedHashSet<>());
        }

        return merged.values().stream()
                .map(node -> new TenantNode(
                        node.tenantId(),
                        node.externalId(),
                        node.name(),
                        node.ownerTenantId(),
                        depths.get(node.tenantId())))
                .sorted(Comparator.comparingInt(TenantNode::depth)
                        .thenComparing(TenantNode::tenantId))
                .toList();
    }

    private int tenantDepth(
            String tenantId,
            Map<String, TenantNode> tenants,
            Map<String, Integer> memo,
            Set<String> visiting) {
        Integer existing = memo.get(tenantId);
        if (existing != null) {
            return existing;
        }
        if (!visiting.add(tenantId)) {
            logger.warn(
                    "TENANT_DATA_ADMIN_TENANT_TREE_CYCLE operationId={} tenantId={} visiting={}",
                    currentOperationId(),
                    tenantId,
                    visiting);
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Cannot determine tenant deletion order because the tenant ownership graph contains a cycle at "
                            + tenantId);
        }

        TenantNode node = tenants.get(tenantId);
        int depth = node != null
                && node.ownerTenantId() != null
                && tenants.containsKey(node.ownerTenantId())
                ? tenantDepth(node.ownerTenantId(), tenants, memo, visiting) + 1
                : 0;
        visiting.remove(tenantId);
        memo.put(tenantId, depth);
        return depth;
    }

    private void validateAdministrator(SecurityTenant tenant, SecurityContext securityContext) {
        if (securityContext == null || securityContext.getUser() == null) {
            logger.warn(
                    "TENANT_DATA_ADMIN_AUTH operationId={} tenantId={} result=DENIED reason=NO_AUTHENTICATED_USER",
                    currentOperationId(),
                    tenant == null ? null : tenant.getId());
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "An authenticated tenant administrator or super administrator is required");
        }

        List<Role> roles = effectiveRoles(securityContext);
        String userId = securityContext.getUser().getId();
        List<String> roleSummary = roles.stream()
                .map(role -> role.getId() + ":" + role.getName()
                        + ":superAdmin=" + role.isSuperAdmin()
                        + ":tenant=" + (role.getTenant() == null ? null : role.getTenant().getId()))
                .toList();
        if (roles.stream().anyMatch(Role::isSuperAdmin)) {
            logger.info(
                    "TENANT_DATA_ADMIN_AUTH operationId={} tenantId={} userId={} result=ALLOWED mode=SUPER_ADMIN effectiveRoles={}",
                    currentOperationId(),
                    tenant.getId(),
                    userId,
                    roleSummary);
            return;
        }

        Set<String> roleIds = roles.stream()
                .map(Role::getId)
                .filter(Objects::nonNull)
                .collect(java.util.stream.Collectors.toCollection(LinkedHashSet::new));
        Set<String> tenantAndOwnerIds = repository.findTenantAndOwnerIds(tenant);
        boolean tenantAdministrator = repository.hasTenantAdministratorPermission(
                roleIds,
                tenantAndOwnerIds,
                ALL_OPERATION_ID);
        logger.info(
                "TENANT_DATA_ADMIN_AUTH operationId={} tenantId={} userId={} result={} mode=TENANT_ADMIN roleIds={} tenantAndOwnerIds={} operationIdRequired={} effectiveRoles={}",
                currentOperationId(),
                tenant.getId(),
                userId,
                tenantAdministrator ? "ALLOWED" : "DENIED",
                roleIds,
                tenantAndOwnerIds,
                ALL_OPERATION_ID,
                roleSummary);
        if (tenantAdministrator) {
            return;
        }

        throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "The authenticated user is not a tenant administrator for tenant "
                        + tenant.getId() + " and is not a super administrator");
    }

    private List<Role> effectiveRoles(SecurityContext securityContext) {
        Map<String, Role> roles = new LinkedHashMap<>();
        if (securityContext.getAllRoles() != null) {
            for (Role role : securityContext.getAllRoles()) {
                if (role != null && role.getId() != null) {
                    roles.put(role.getId(), role);
                }
            }
        }
        if (securityContext.getRoleMap() != null) {
            securityContext.getRoleMap().values().stream()
                    .filter(Objects::nonNull)
                    .flatMap(List::stream)
                    .filter(Objects::nonNull)
                    .filter(role -> role.getId() != null)
                    .forEach(role -> roles.put(role.getId(), role));
        }
        return List.copyOf(roles.values());
    }

    private boolean requestingUserBelongsToDeletedTenant(
            SecurityContext securityContext,
            Set<String> tenantIds) {
        SecurityUser user = securityContext.getUser();
        return user != null
                && user.getTenant() != null
                && tenantIds.contains(user.getTenant().getId());
    }

    private Set<String> tenantIds(List<TenantNode> tenantTree) {
        return tenantTree.stream()
                .map(TenantNode::tenantId)
                .collect(java.util.stream.Collectors.toCollection(LinkedHashSet::new));
    }

    private List<String> validateTenantIds(TenantDataRequest request) {
        Set<String> tenantIds = new LinkedHashSet<>();
        if (request != null && request.getTenantIds() != null) {
            request.getTenantIds().stream()
                    .filter(Objects::nonNull)
                    .map(String::trim)
                    .filter(value -> !value.isBlank())
                    .forEach(tenantIds::add);
        }
        if (request != null && request.getTenantId() != null
                && !request.getTenantId().isBlank()) {
            tenantIds.add(request.getTenantId().trim());
        }
        if (tenantIds.isEmpty()) {
            logger.warn(
                    "TENANT_DATA_ADMIN_VALIDATION operationId={} result=DENIED reason=EMPTY_TENANT_IDS singularTenantId={} tenantIds={}",
                    currentOperationId(),
                    request == null ? null : request.getTenantId(),
                    request == null ? null : request.getTenantIds());
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "tenantIds must contain at least one tenant id");
        }
        return List.copyOf(tenantIds);
    }

    private String singleTenantId(List<SecurityTenant> requestedTenants) {
        return requestedTenants.size() == 1 ? requestedTenants.get(0).getId() : null;
    }

    private String singleTenantExternalId(List<SecurityTenant> requestedTenants) {
        return requestedTenants.size() == 1 ? requestedTenants.get(0).getExternalId() : null;
    }

    private OperationLogContext beginOperation(
            String action,
            TenantDataRequest request,
            SecurityContext securityContext,
            Boolean dry,
            Boolean deleteSecurityObjects) {
        String previousOperationId = MDC.get(MDC_OPERATION_ID);
        String operationId = UUID.randomUUID().toString();
        MDC.put(MDC_OPERATION_ID, operationId);
        String userId = securityContext == null || securityContext.getUser() == null
                ? null
                : securityContext.getUser().getId();
        logger.info(
                "TENANT_DATA_ADMIN_REQUEST_START operationId={} action={} userId={} singularTenantId={} tenantIds={} dry={} deleteSecurityObjects={}",
                operationId,
                action,
                userId,
                request == null ? null : request.getTenantId(),
                request == null ? null : request.getTenantIds(),
                dry,
                deleteSecurityObjects);
        return new OperationLogContext(
                operationId,
                previousOperationId,
                action,
                userId,
                System.nanoTime());
    }

    private void logValidationFailure(
            OperationLogContext operation,
            ResponseStatusException exception) {
        logger.warn(
                "TENANT_DATA_ADMIN_REQUEST_END operationId={} action={} status=FAILED httpStatus={} reason={} userId={} durationMs={}",
                operation.operationId(),
                operation.action(),
                exception.getStatusCode(),
                exception.getReason(),
                operation.userId(),
                elapsedMillis(operation.startedNanos()),
                exception);
    }

    private void logUnexpectedFailure(
            OperationLogContext operation,
            RuntimeException exception) {
        logger.error(
                "TENANT_DATA_ADMIN_REQUEST_END operationId={} action={} status=FAILED exceptionType={} rootCause={} userId={} durationMs={}",
                operation.operationId(),
                operation.action(),
                exception.getClass().getCanonicalName(),
                rootMessage(exception),
                operation.userId(),
                elapsedMillis(operation.startedNanos()),
                exception);
    }

    private void endOperation(OperationLogContext operation) {
        if (operation.previousOperationId() == null) {
            MDC.remove(MDC_OPERATION_ID);
        } else {
            MDC.put(MDC_OPERATION_ID, operation.previousOperationId());
        }
    }

    private String currentOperationId() {
        return Objects.toString(MDC.get(MDC_OPERATION_ID), "unknown");
    }

    private long elapsedMillis(long startedNanos) {
        return (System.nanoTime() - startedNanos) / 1_000_000L;
    }

    private List<String> tenantSummary(List<SecurityTenant> tenants) {
        return tenants.stream()
                .map(tenant -> tenant.getId()
                        + "(externalId=" + tenant.getExternalId()
                        + ",owner=" + (tenant.getTenant() == null
                        ? null
                        : tenant.getTenant().getId()) + ")")
                .toList();
    }

    private List<String> tenantTreeSummary(List<TenantNode> tenantTree) {
        return tenantTree.stream()
                .map(node -> node.tenantId()
                        + "(externalId=" + node.externalId()
                        + ",owner=" + node.ownerTenantId()
                        + ",depth=" + node.depth() + ")")
                .toList();
    }

    private Map<String, Long> deletionTypeCountSummary(DeletionSnapshot snapshot) {
        Map<String, Long> result = new LinkedHashMap<>();
        snapshot.recordsByType().forEach(type -> result.put(type.clazz(), type.count()));
        return result;
    }

    private Map<String, Integer> nonZeroCounts(Map<String, Integer> counts) {
        Map<String, Integer> result = new LinkedHashMap<>();
        counts.forEach((type, count) -> {
            if (count != null && count > 0) {
                result.put(type, count);
            }
        });
        return result;
    }

    private record OperationLogContext(
            String operationId,
            String previousOperationId,
            String action,
            String userId,
            long startedNanos) {
    }

    private String rootMessage(Throwable throwable) {
        Throwable current = throwable;
        while (current.getCause() != null && current.getCause() != current) {
            current = current.getCause();
        }
        return current.getMessage() == null
                ? current.getClass().getSimpleName()
                : current.getMessage();
    }
}
