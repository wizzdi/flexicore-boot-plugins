package com.wizzdi.tenant.data.administration.data;

import com.flexicore.model.Baseclass;
import com.flexicore.model.OperationGroup;
import com.flexicore.model.OperationToGroup;
import com.flexicore.model.PermissionGroup;
import com.flexicore.model.PermissionGroupToBaseclass;
import com.flexicore.model.Role;
import com.flexicore.model.RoleToBaseclass;
import com.flexicore.model.RoleToUser;
import com.flexicore.model.SecurityEntity;
import com.flexicore.model.SecurityLink;
import com.flexicore.model.SecurityLinkGroup;
import com.flexicore.model.SecurityTenant;
import com.flexicore.model.SecurityUser;
import com.flexicore.model.TenantToUser;
import com.flexicore.model.security.SecurityPolicy;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import com.wizzdi.segmantix.model.Access;
import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Table;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.metamodel.Attribute;
import jakarta.persistence.metamodel.EntityType;
import jakarta.persistence.metamodel.IdentifiableType;
import jakarta.persistence.metamodel.SingularAttribute;
import org.pf4j.Extension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@Extension
public class TenantDataAdministrationRepository implements Plugin {

    private static final Logger logger = LoggerFactory.getLogger(TenantDataAdministrationRepository.class);
    private static final String MDC_OPERATION_ID = "tenantDataOperationId";
    private static final String SECURITY_WILDCARD = "SecurityWildcard";

    @PersistenceContext
    private EntityManager em;

    public SecurityTenant findTenant(String tenantId, boolean lockForDeletion) {
        logger.info(
                "TENANT_DATA_ADMIN_REPOSITORY operationId={} step=FIND_TENANT tenantId={} lockForDeletion={}",
                currentOperationId(),
                tenantId,
                lockForDeletion);
        SecurityTenant tenant = lockForDeletion
                ? em.find(SecurityTenant.class, tenantId, LockModeType.PESSIMISTIC_WRITE)
                : em.find(SecurityTenant.class, tenantId);
        logger.info(
                "TENANT_DATA_ADMIN_REPOSITORY operationId={} step=FIND_TENANT tenantId={} result={}",
                currentOperationId(),
                tenantId,
                tenant == null ? "NOT_FOUND" : "FOUND");
        return tenant;
    }

    public List<TenantNode> findTenantTree(SecurityTenant root, boolean lockForDeletion) {
        Map<String, TenantNode> result = new LinkedHashMap<>();
        SecurityTenant owner = root.getTenant();
        result.put(root.getId(), toNode(root, owner == null ? null : owner.getId(), 0));

        Set<String> visited = new HashSet<>();
        visited.add(root.getId());
        Set<String> frontier = Set.of(root.getId());
        int depth = 1;

        while (!frontier.isEmpty()) {
            TypedQuery<SecurityTenant> query = em.createQuery(
                    "select t from SecurityTenant t where t.tenant.id in :ownerIds order by t.id",
                    SecurityTenant.class);
            query.setParameter("ownerIds", frontier);
            if (lockForDeletion) {
                query.setLockMode(LockModeType.PESSIMISTIC_WRITE);
            }

            Set<String> next = new LinkedHashSet<>();
            for (SecurityTenant tenant : query.getResultList()) {
                if (!visited.add(tenant.getId())) {
                    continue;
                }
                SecurityTenant tenantOwner = tenant.getTenant();
                result.put(tenant.getId(), toNode(
                        tenant,
                        tenantOwner == null ? null : tenantOwner.getId(),
                        depth));
                next.add(tenant.getId());
            }
            frontier = next;
            depth++;
        }

        List<TenantNode> tenantTree = result.values().stream()
                .sorted(Comparator.comparingInt(TenantNode::depth)
                        .thenComparing(TenantNode::tenantId))
                .toList();
        logger.info(
                "TENANT_DATA_ADMIN_REPOSITORY operationId={} step=FIND_TENANT_TREE rootTenantId={} lockForDeletion={} tenantCount={} tenantIds={}",
                currentOperationId(),
                root.getId(),
                lockForDeletion,
                tenantTree.size(),
                tenantTree.stream().map(TenantNode::tenantId).toList());
        return tenantTree;
    }

    public Set<String> findTenantAndOwnerIds(SecurityTenant tenant) {
        Set<String> result = new LinkedHashSet<>();
        SecurityTenant current = tenant;
        while (current != null && result.add(current.getId())) {
            current = current.getTenant();
        }
        return result;
    }

    public boolean hasTenantAdministratorPermission(
            Set<String> roleIds,
            Set<String> tenantOrOwnerIds,
            String allOperationId) {
        if (roleIds.isEmpty() || tenantOrOwnerIds.isEmpty()) {
            logger.info(
                    "TENANT_DATA_ADMIN_REPOSITORY operationId={} step=CHECK_TENANT_ADMIN_PERMISSION result=DENIED roleIds={} tenantOrOwnerIds={} reason=EMPTY_INPUT",
                    currentOperationId(),
                    roleIds,
                    tenantOrOwnerIds);
            return false;
        }
        Long count = em.createQuery(
                        "select count(p) from RoleToBaseclass p "
                                + "where p.role.id in :roleIds "
                                + "and p.role.tenant.id in :tenantIds "
                                + "and p.softDelete = false "
                                + "and p.role.softDelete = false "
                                + "and p.securedType = :securedType "
                                + "and p.operationId = :operationId "
                                + "and p.access = :access",
                        Long.class)
                .setParameter("roleIds", roleIds)
                .setParameter("tenantIds", tenantOrOwnerIds)
                .setParameter("securedType", SECURITY_WILDCARD)
                .setParameter("operationId", allOperationId)
                .setParameter("access", Access.allow)
                .getSingleResult();
        boolean allowed = count != null && count > 0;
        logger.info(
                "TENANT_DATA_ADMIN_REPOSITORY operationId={} step=CHECK_TENANT_ADMIN_PERMISSION result={} matchingPermissions={} roleIds={} tenantOrOwnerIds={} operationIdRequired={}",
                currentOperationId(),
                allowed ? "ALLOWED" : "DENIED",
                count,
                roleIds,
                tenantOrOwnerIds,
                allOperationId);
        return allowed;
    }

    public InventorySnapshot inventory(Collection<String> tenantIds) {
        Set<String> requestedTenantIds = tenantIds.stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toCollection(LinkedHashSet::new));
        EntityModel entityModel = discoverEntityModel();
        logger.info(
                "TENANT_DATA_ADMIN_REPOSITORY operationId={} step=INVENTORY_START tenantIds={} discoveredRoots={}",
                currentOperationId(),
                requestedTenantIds,
                entityModel.roots().size());
        if (requestedTenantIds.isEmpty()) {
            return new InventorySnapshot(List.of(), entityModel.roots().size());
        }

        Map<String, MutableTypeCount> counts = new LinkedHashMap<>();
        for (RootDescriptor root : entityModel.roots()) {
            if (!hasEntityInheritance(root, entityModel)) {
                Long count = em.createQuery(
                                "select count(e) from " + root.entityName()
                                        + " e where " + tenantPredicate("e", root.tenantSelector(), true),
                                Long.class)
                        .setParameter("tenantIds", requestedTenantIds)
                        .getSingleResult();
                if (count != null && count > 0) {
                    addCount(counts, root.javaType(), root, count);
                }
                continue;
            }

            List<Object[]> rows = em.createQuery(
                            "select type(e), count(e) from " + root.entityName()
                                    + " e where " + tenantPredicate("e", root.tenantSelector(), true)
                                    + " group by type(e)",
                            Object[].class)
                    .setParameter("tenantIds", requestedTenantIds)
                    .getResultList();
            for (Object[] row : rows) {
                Class<?> concreteType = resolveConcreteType(row[0], root.javaType());
                long count = ((Number) row[1]).longValue();
                if (count > 0) {
                    addCount(counts, concreteType, root, count);
                }
            }
        }

        List<TypeCountSnapshot> snapshots = counts.values().stream()
                .map(MutableTypeCount::snapshot)
                .sorted(Comparator.comparing(TypeCountSnapshot::clazz))
                .toList();
        InventorySnapshot snapshot = new InventorySnapshot(snapshots, entityModel.roots().size());
        logger.info(
                "TENANT_DATA_ADMIN_REPOSITORY operationId={} step=INVENTORY_END tenantIds={} entityTypeCount={} totalRecords={} recordsByType={}",
                currentOperationId(),
                requestedTenantIds,
                snapshots.size(),
                snapshot.totalRecords(),
                snapshots.stream().collect(Collectors.toMap(
                        TypeCountSnapshot::clazz,
                        TypeCountSnapshot::count,
                        (first, second) -> first,
                        LinkedHashMap::new)));
        return snapshot;
    }

    public DeletionSnapshot deletionSnapshot(
            Collection<String> tenantIds,
            DeletePlan plan,
            boolean includeTenantRecords) {
        Set<String> requestedTenantIds = tenantIds.stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toCollection(LinkedHashSet::new));
        if (requestedTenantIds.isEmpty()) {
            return new DeletionSnapshot(List.of());
        }

        logger.info(
                "TENANT_DATA_ADMIN_REPOSITORY operationId={} step=DELETION_SNAPSHOT_START tenantIds={} includeTenantRecords={} plannedRootCount={}",
                currentOperationId(),
                requestedTenantIds,
                includeTenantRecords,
                plan.deletionOrder().size());
        EntityModel entityModel = discoverEntityModel();
        List<RootDescriptor> selectedRoots = new ArrayList<>(plan.deletionOrder());
        if (includeTenantRecords) {
            entityModel.roots().stream()
                    .filter(root -> SecurityTenant.class.equals(root.javaType()))
                    .findFirst()
                    .ifPresent(selectedRoots::add);
        }

        Map<String, MutableDeletionTypeRecords> grouped = new LinkedHashMap<>();
        for (RootDescriptor root : selectedRoots) {
            boolean entityInheritance = hasEntityInheritance(root, entityModel);
            String selection = entityInheritance
                    ? "select e, type(e), " + tenantExpression("e", root.tenantSelector())
                    : "select e, " + tenantExpression("e", root.tenantSelector());
            List<Object[]> rows = em.createQuery(
                            selection + " from " + root.entityName() + " e where "
                                    + tenantPredicate("e", root.tenantSelector(), true),
                            Object[].class)
                    .setParameter("tenantIds", requestedTenantIds)
                    .getResultList();
            for (Object[] row : rows) {
                Object entity = row[0];
                Class<?> concreteType = entityInheritance
                        ? resolveConcreteType(row[1], root.javaType())
                        : root.javaType();
                int tenantColumn = entityInheritance ? 2 : 1;
                String canonicalName = concreteType.getCanonicalName() == null
                        ? concreteType.getName()
                        : concreteType.getCanonicalName();
                MutableDeletionTypeRecords typeRecords = grouped.computeIfAbsent(
                        canonicalName,
                        ignored -> new MutableDeletionTypeRecords(
                                canonicalName,
                                concreteType.getSimpleName(),
                                root.tableName(),
                                isSecurityData(concreteType)));
                typeRecords.add(new DeletionRecordSnapshot(
                        identifier(entity),
                        Objects.toString(row[tenantColumn], null),
                        readStringProperty(entity, "getName"),
                        readStringProperty(entity, "getExternalId")));
            }
        }

        List<DeletionTypeRecordsSnapshot> recordsByType = grouped.values().stream()
                .map(MutableDeletionTypeRecords::snapshot)
                .sorted(Comparator.comparing(DeletionTypeRecordsSnapshot::clazz))
                .toList();
        DeletionSnapshot snapshot = new DeletionSnapshot(recordsByType);
        logger.info(
                "TENANT_DATA_ADMIN_REPOSITORY operationId={} step=DELETION_SNAPSHOT_END tenantIds={} typeCount={} totalRecords={} recordsByType={}",
                currentOperationId(),
                requestedTenantIds,
                recordsByType.size(),
                snapshot.totalRecords(),
                recordsByType.stream().collect(Collectors.toMap(
                        DeletionTypeRecordsSnapshot::clazz,
                        DeletionTypeRecordsSnapshot::count,
                        (first, second) -> first,
                        LinkedHashMap::new)));
        return snapshot;
    }

    public DeletePlan buildDeletePlan(boolean deleteSecurityObjects) {
        EntityModel model = discoverEntityModel();
        Map<Class<?>, RootDescriptor> selected = model.roots().stream()
                .filter(root -> !root.javaType().equals(SecurityTenant.class))
                .filter(root -> deleteSecurityObjects || !root.securityData())
                .collect(Collectors.toMap(
                        RootDescriptor::javaType,
                        Function.identity(),
                        (first, second) -> first,
                        LinkedHashMap::new));

        List<AssociationEdge> edges = new ArrayList<>(discoverAssociationEdges(model, selected));
        logger.info(
                "TENANT_DATA_ADMIN_REPOSITORY operationId={} step=BUILD_DELETE_PLAN_START deleteSecurityObjects={} discoveredRoots={} selectedRoots={} discoveredAssociationEdges={}",
                currentOperationId(),
                deleteSecurityObjects,
                model.roots().size(),
                selected.size(),
                edges.size());
        RootDescriptor securityUserRoot = selected.get(SecurityUser.class);
        if (securityUserRoot != null) {
            for (RootDescriptor root : selected.values()) {
                if (!root.equals(securityUserRoot)
                        && Baseclass.class.isAssignableFrom(root.javaType())) {
                    edges.add(new AssociationEdge(
                            root,
                            securityUserRoot,
                            root.entityName(),
                            root.javaType(),
                            "creator",
                            true));
                }
            }
        }
        List<AssociationEdge> activeEdges = new ArrayList<>(edges);
        List<AssociationToClear> associationsToClear = new ArrayList<>();

        while (true) {
            TopologicalResult result = topologicalOrder(selected.values(), activeEdges);
            if (result.order().size() == selected.size()) {
                DeletePlan plan = new DeletePlan(result.order(), associationsToClear);
                logger.info(
                        "TENANT_DATA_ADMIN_REPOSITORY operationId={} step=BUILD_DELETE_PLAN_END deletionOrder={} associationsToClear={}",
                        currentOperationId(),
                        plan.deletionOrder().stream()
                                .map(root -> root.javaType().getCanonicalName())
                                .toList(),
                        plan.associationsToClear().stream()
                                .map(association -> association.declaringJavaType().getCanonicalName()
                                        + "." + association.attributeName())
                                .toList());
                return plan;
            }

            Set<RootDescriptor> cyclic = result.remaining();
            Optional<AssociationEdge> removable = activeEdges.stream()
                    .filter(edge -> cyclic.contains(edge.source()) && cyclic.contains(edge.target()))
                    .filter(AssociationEdge::nullable)
                    .sorted(Comparator.comparing((AssociationEdge edge) -> edge.source().javaType().getCanonicalName())
                            .thenComparing(AssociationEdge::attributeName))
                    .findFirst();
            if (removable.isEmpty()) {
                String cycle = cyclic.stream()
                        .map(root -> root.javaType().getCanonicalName())
                        .sorted()
                        .collect(Collectors.joining(", "));
                logger.error(
                        "TENANT_DATA_ADMIN_REPOSITORY operationId={} step=BUILD_DELETE_PLAN_FAILED reason=NON_NULLABLE_CYCLE cycle={}",
                        currentOperationId(),
                        cycle);
                throw new IllegalStateException(
                        "Cannot determine a safe hard-delete order because non-nullable entity dependencies form a cycle: "
                                + cycle);
            }

            AssociationEdge edge = removable.get();
            logger.info(
                    "TENANT_DATA_ADMIN_REPOSITORY operationId={} step=BUILD_DELETE_PLAN_CLEAR_NULLABLE_ASSOCIATION source={} target={} attribute={}.{}",
                    currentOperationId(),
                    edge.source().javaType().getCanonicalName(),
                    edge.target().javaType().getCanonicalName(),
                    edge.declaringJavaType().getCanonicalName(),
                    edge.attributeName());
            activeEdges.remove(edge);
            associationsToClear.add(new AssociationToClear(
                    edge.declaringEntityName(),
                    edge.declaringJavaType(),
                    edge.attributeName(),
                    edge.source().tenantSelector()));
        }
    }

    public Map<String, Integer> deleteTenantData(String tenantId, DeletePlan plan) {
        logger.info(
                "TENANT_DATA_ADMIN_REPOSITORY operationId={} step=DELETE_TENANT_DATA_START tenantId={} deletionRootCount={} associationClearCount={}",
                currentOperationId(),
                tenantId,
                plan.deletionOrder().size(),
                plan.associationsToClear().size());
        em.flush();
        for (AssociationToClear association : plan.associationsToClear()) {
            int updated = em.createQuery("update " + association.entityName() + " e set e."
                            + association.attributeName() + " = null where "
                            + tenantPredicate("e", association.tenantSelector(), false))
                    .setParameter("tenantId", tenantId)
                    .executeUpdate();
            logger.info(
                    "TENANT_DATA_ADMIN_REPOSITORY operationId={} step=CLEAR_ASSOCIATION tenantId={} entity={} attribute={} updatedRows={}",
                    currentOperationId(),
                    tenantId,
                    association.declaringJavaType().getCanonicalName(),
                    association.attributeName(),
                    updated);
        }
        em.clear();

        Map<String, Integer> deletedByRoot = new LinkedHashMap<>();
        for (RootDescriptor root : plan.deletionOrder()) {
            int deleted = em.createQuery(
                            "delete from " + root.entityName() + " e where "
                                    + tenantPredicate("e", root.tenantSelector(), false))
                    .setParameter("tenantId", tenantId)
                    .executeUpdate();
            deletedByRoot.merge(root.javaType().getCanonicalName(), deleted, Integer::sum);
            if (deleted > 0) {
                logger.info(
                        "TENANT_DATA_ADMIN_REPOSITORY operationId={} step=DELETE_ENTITY_ROOT tenantId={} entity={} table={} deletedRows={}",
                        currentOperationId(),
                        tenantId,
                        root.javaType().getCanonicalName(),
                        root.tableName(),
                        deleted);
            }
            em.clear();
        }
        logger.info(
                "TENANT_DATA_ADMIN_REPOSITORY operationId={} step=DELETE_TENANT_DATA_END tenantId={} deletedByType={}",
                currentOperationId(),
                tenantId,
                deletedByRoot.entrySet().stream()
                        .filter(entry -> entry.getValue() != null && entry.getValue() > 0)
                        .collect(Collectors.toMap(
                                Map.Entry::getKey,
                                Map.Entry::getValue,
                                (first, second) -> first,
                                LinkedHashMap::new)));
        return deletedByRoot;
    }

    public int deleteTenantRecord(String tenantId) {
        int deleted = em.createQuery("delete from SecurityTenant t where t.id = :tenantId")
                .setParameter("tenantId", tenantId)
                .executeUpdate();
        em.clear();
        logger.info(
                "TENANT_DATA_ADMIN_REPOSITORY operationId={} step=DELETE_TENANT_RECORD tenantId={} deletedRows={}",
                currentOperationId(),
                tenantId,
                deleted);
        return deleted;
    }

    public void evictJpaCache() {
        em.clear();
        em.getEntityManagerFactory().getCache().evictAll();
    }

    private String currentOperationId() {
        return Objects.toString(MDC.get(MDC_OPERATION_ID), "unknown");
    }

    private EntityModel discoverEntityModel() {
        List<EntityType<?>> entityTypes = em.getMetamodel().getEntities().stream()
                .sorted(Comparator.comparing(entityType -> entityType.getJavaType().getCanonicalName()))
                .toList();

        Map<Class<?>, EntityType<?>> entityTypeByClass = entityTypes.stream()
                .collect(Collectors.toMap(entityType -> entityType.getJavaType(), Function.identity()));

        Map<Class<?>, RootDescriptor> rootByClass = new LinkedHashMap<>();
        Map<Class<?>, Class<?>> entityRootByClass = new HashMap<>();
        for (EntityType<?> entityType : entityTypes) {
            Class<?> rootClass = findEntityRoot(entityType);
            entityRootByClass.put(entityType.getJavaType(), rootClass);
            if (rootByClass.containsKey(rootClass)) {
                continue;
            }
            EntityType<?> rootEntityType = entityTypeByClass.get(rootClass);
            TenantSelector tenantSelector = tenantSelector(rootEntityType);
            if (tenantSelector == null) {
                continue;
            }
            rootByClass.put(rootClass, new RootDescriptor(
                    rootClass,
                    rootEntityType.getName(),
                    tableName(rootClass),
                    isSecurityData(rootClass),
                    tenantSelector));
        }

        Map<Class<?>, RootDescriptor> rootByEntityClass = new HashMap<>();
        for (EntityType<?> entityType : entityTypes) {
            Class<?> rootClass = entityRootByClass.get(entityType.getJavaType());
            RootDescriptor root = rootByClass.get(rootClass);
            if (root != null) {
                rootByEntityClass.put(entityType.getJavaType(), root);
            }
        }

        return new EntityModel(
                entityTypes,
                List.copyOf(rootByClass.values()),
                rootByEntityClass,
                entityTypeByClass);
    }

    private TenantSelector tenantSelector(EntityType<?> entityType) {
        if (SecurityTenant.class.equals(entityType.getJavaType())) {
            return TenantSelector.ENTITY_ID;
        }
        TenantSelector scalar = null;
        for (Attribute<?, ?> attribute : entityType.getAttributes()) {
            if ("tenant".equals(attribute.getName())
                    && SecurityTenant.class.isAssignableFrom(attribute.getJavaType())) {
                return TenantSelector.ASSOCIATION;
            }
            if ("tenantId".equalsIgnoreCase(attribute.getName())
                    && String.class.equals(attribute.getJavaType())) {
                scalar = TenantSelector.SCALAR_ID;
            }
        }
        return scalar;
    }

    private String tenantPredicate(String alias, TenantSelector selector, boolean collection) {
        String parameter = collection ? ":tenantIds" : ":tenantId";
        String operator = collection ? " in " : " = ";
        return switch (selector) {
            case ASSOCIATION -> alias + ".tenant.id" + operator + parameter;
            case SCALAR_ID -> alias + ".tenantId" + operator + parameter;
            case ENTITY_ID -> alias + ".id" + operator + parameter;
        };
    }

    private String tenantExpression(String alias, TenantSelector selector) {
        return switch (selector) {
            case ASSOCIATION -> alias + ".tenant.id";
            case SCALAR_ID -> alias + ".tenantId";
            case ENTITY_ID -> alias + ".id";
        };
    }

    private String identifier(Object entity) {
        Object identifier = em.getEntityManagerFactory()
                .getPersistenceUnitUtil()
                .getIdentifier(entity);
        return Objects.toString(identifier, null);
    }

    private String readStringProperty(Object entity, String getterName) {
        try {
            Method method = entity.getClass().getMethod(getterName);
            Object value = method.invoke(entity);
            return Objects.toString(value, null);
        } catch (ReflectiveOperationException ignored) {
            return null;
        }
    }

    private List<AssociationEdge> discoverAssociationEdges(
            EntityModel model,
            Map<Class<?>, RootDescriptor> selectedRoots) {
        Set<AssociationEdge> result = new LinkedHashSet<>();
        for (EntityType<?> entityType : model.entityTypes()) {
            RootDescriptor source = model.rootByEntityClass().get(entityType.getJavaType());
            if (source == null || !selectedRoots.containsKey(source.javaType())) {
                continue;
            }
            for (Attribute<?, ?> attribute : entityType.getAttributes()) {
                if (!(attribute instanceof SingularAttribute<?, ?> singularAttribute)) {
                    continue;
                }
                Attribute.PersistentAttributeType attributeType = attribute.getPersistentAttributeType();
                if (attributeType != Attribute.PersistentAttributeType.MANY_TO_ONE
                        && attributeType != Attribute.PersistentAttributeType.ONE_TO_ONE) {
                    continue;
                }
                if (attributeType == Attribute.PersistentAttributeType.ONE_TO_ONE
                        && isInverseOneToOne(attribute)) {
                    continue;
                }

                RootDescriptor target = model.rootByEntityClass().get(attribute.getJavaType());
                if (target == null
                        || source.equals(target)
                        || !selectedRoots.containsKey(target.javaType())) {
                    continue;
                }

                Class<?> declaringJavaType = attribute.getDeclaringType().getJavaType();
                EntityType<?> declaringEntityType = model.entityTypeByClass().get(declaringJavaType);
                if (declaringEntityType == null) {
                    declaringEntityType = entityType;
                    declaringJavaType = entityType.getJavaType();
                }
                boolean nullable = singularAttribute.isOptional();
                result.add(new AssociationEdge(
                        source,
                        target,
                        declaringEntityType.getName(),
                        declaringJavaType,
                        attribute.getName(),
                        nullable));
            }
        }
        return List.copyOf(result);
    }

    /**
     * An association declared with mappedBy is the inverse view of a foreign
     * key owned by the other entity. It must not contribute another delete
     * dependency edge, otherwise every bidirectional one-to-one mapping is
     * incorrectly interpreted as a non-nullable database cycle.
     */
    private boolean isInverseOneToOne(Attribute<?, ?> attribute) {
        Class<?> declaringType = attribute.getDeclaringType().getJavaType();
        String attributeName = attribute.getName();

        Field field = findField(declaringType, attributeName);
        if (field != null) {
            OneToOne annotation = field.getAnnotation(OneToOne.class);
            if (annotation != null && !annotation.mappedBy().isBlank()) {
                return true;
            }
        }

        Method getter = findGetter(declaringType, attributeName);
        if (getter != null) {
            OneToOne annotation = getter.getAnnotation(OneToOne.class);
            return annotation != null && !annotation.mappedBy().isBlank();
        }
        return false;
    }

    private Field findField(Class<?> type, String attributeName) {
        Class<?> current = type;
        while (current != null && !Object.class.equals(current)) {
            try {
                return current.getDeclaredField(attributeName);
            } catch (NoSuchFieldException ignored) {
                current = current.getSuperclass();
            }
        }
        return null;
    }

    private Method findGetter(Class<?> type, String attributeName) {
        if (attributeName == null || attributeName.isBlank()) {
            return null;
        }
        String suffix = Character.toUpperCase(attributeName.charAt(0))
                + attributeName.substring(1);
        for (String methodName : List.of("get" + suffix, "is" + suffix)) {
            Class<?> current = type;
            while (current != null && !Object.class.equals(current)) {
                try {
                    return current.getDeclaredMethod(methodName);
                } catch (NoSuchMethodException ignored) {
                    current = current.getSuperclass();
                }
            }
        }
        return null;
    }

    private TopologicalResult topologicalOrder(
            Collection<RootDescriptor> roots,
            List<AssociationEdge> edges) {
        Map<RootDescriptor, Integer> indegree = new LinkedHashMap<>();
        Map<RootDescriptor, Set<RootDescriptor>> outgoing = new LinkedHashMap<>();
        for (RootDescriptor root : roots) {
            indegree.put(root, 0);
            outgoing.put(root, new LinkedHashSet<>());
        }
        for (AssociationEdge edge : edges) {
            if (outgoing.get(edge.source()).add(edge.target())) {
                indegree.computeIfPresent(edge.target(), (ignored, current) -> current + 1);
            }
        }

        Deque<RootDescriptor> ready = indegree.entrySet().stream()
                .filter(entry -> entry.getValue() == 0)
                .map(Map.Entry::getKey)
                .sorted(Comparator.comparing(root -> root.javaType().getCanonicalName()))
                .collect(Collectors.toCollection(ArrayDeque::new));

        List<RootDescriptor> order = new ArrayList<>();
        while (!ready.isEmpty()) {
            RootDescriptor current = ready.removeFirst();
            order.add(current);
            List<RootDescriptor> targets = outgoing.getOrDefault(current, Set.of()).stream()
                    .sorted(Comparator.comparing(root -> root.javaType().getCanonicalName()))
                    .toList();
            for (RootDescriptor target : targets) {
                int next = indegree.computeIfPresent(target, (ignored, value) -> value - 1);
                if (next == 0) {
                    ready.addLast(target);
                }
            }
        }

        Set<RootDescriptor> remaining = new LinkedHashSet<>(roots);
        remaining.removeAll(order);
        return new TopologicalResult(order, remaining);
    }

    private void addCount(
            Map<String, MutableTypeCount> counts,
            Class<?> concreteType,
            RootDescriptor root,
            long count) {
        String canonicalName = concreteType.getCanonicalName() == null
                ? concreteType.getName()
                : concreteType.getCanonicalName();
        counts.computeIfAbsent(canonicalName, ignored -> new MutableTypeCount(
                        canonicalName,
                        concreteType.getSimpleName(),
                        root.tableName(),
                        isSecurityData(concreteType)))
                .add(count);
    }

    /**
     * TYPE(e) is valid only for a real JPA entity inheritance hierarchy.
     * Baseclass and SecurityEntity are mapped superclasses, so inheriting from
     * either one does not make an entity query polymorphic and does not imply a
     * discriminator column.
     */
    private boolean hasEntityInheritance(RootDescriptor root, EntityModel model) {
        return model.rootByEntityClass().entrySet().stream()
                .anyMatch(entry -> root.equals(entry.getValue())
                        && !root.javaType().equals(entry.getKey()));
    }

    private Class<?> resolveConcreteType(Object typeValue, Class<?> fallback) {
        if (typeValue instanceof Class<?> type) {
            return type;
        }
        if (typeValue != null) {
            String typeName = typeValue.toString();
            try {
                return Class.forName(typeName, false, fallback.getClassLoader());
            } catch (ClassNotFoundException ignored) {
                // EclipseLink normally returns Class<?> for TYPE(e); use the root if a provider returns a discriminator.
            }
        }
        return fallback;
    }

    private Class<?> findEntityRoot(EntityType<?> entityType) {
        Class<?> root = entityType.getJavaType();
        IdentifiableType<?> current = entityType;
        while (current.getSupertype() instanceof EntityType<?> entitySupertype) {
            root = entitySupertype.getJavaType();
            current = entitySupertype;
        }
        return root;
    }


    private String tableName(Class<?> rootClass) {
        Table table = rootClass.getAnnotation(Table.class);
        String name = table != null && !table.name().isBlank()
                ? table.name()
                : rootClass.getSimpleName().toLowerCase(Locale.ROOT);
        if (table != null && !table.schema().isBlank()) {
            return table.schema() + "." + name;
        }
        return name;
    }

    public static boolean isSecurityData(Class<?> type) {
        return SecurityEntity.class.isAssignableFrom(type)
                || SecurityLink.class.isAssignableFrom(type)
                || RoleToUser.class.isAssignableFrom(type)
                || TenantToUser.class.isAssignableFrom(type)
                || PermissionGroup.class.isAssignableFrom(type)
                || PermissionGroupToBaseclass.class.isAssignableFrom(type)
                || OperationGroup.class.isAssignableFrom(type)
                || OperationToGroup.class.isAssignableFrom(type)
                || SecurityLinkGroup.class.isAssignableFrom(type)
                || SecurityPolicy.class.isAssignableFrom(type)
                || Role.class.isAssignableFrom(type)
                || SecurityUser.class.isAssignableFrom(type)
                || SecurityTenant.class.isAssignableFrom(type);
    }

    private TenantNode toNode(SecurityTenant tenant, String ownerTenantId, int depth) {
        return new TenantNode(
                tenant.getId(),
                tenant.getExternalId(),
                tenant.getName(),
                ownerTenantId,
                depth);
    }

    public record TenantNode(
            String tenantId,
            String externalId,
            String name,
            String ownerTenantId,
            int depth) {
    }

    public record TypeCountSnapshot(
            String clazz,
            String simpleName,
            String tableName,
            long count,
            boolean securityData) {
    }

    public record InventorySnapshot(
            List<TypeCountSnapshot> recordsByType,
            int dynamicallyDiscoveredEntityRoots) {
        public long totalRecords() {
            return recordsByType.stream().mapToLong(TypeCountSnapshot::count).sum();
        }
    }

    public record DeletionRecordSnapshot(
            String id,
            String tenantId,
            String name,
            String externalId) {
    }

    public record DeletionTypeRecordsSnapshot(
            String clazz,
            String simpleName,
            String tableName,
            boolean securityData,
            List<DeletionRecordSnapshot> records) {
        public long count() {
            return records.size();
        }
    }

    public record DeletionSnapshot(
            List<DeletionTypeRecordsSnapshot> recordsByType) {
        public long totalRecords() {
            return recordsByType.stream()
                    .mapToLong(DeletionTypeRecordsSnapshot::count)
                    .sum();
        }
    }

    public record RootDescriptor(
            Class<?> javaType,
            String entityName,
            String tableName,
            boolean securityData,
            TenantSelector tenantSelector) {
    }

    public record AssociationToClear(
            String entityName,
            Class<?> declaringJavaType,
            String attributeName,
            TenantSelector tenantSelector) {
    }

    public enum TenantSelector {
        ASSOCIATION,
        SCALAR_ID,
        ENTITY_ID
    }

    public record DeletePlan(
            List<RootDescriptor> deletionOrder,
            List<AssociationToClear> associationsToClear) {
    }

    private record EntityModel(
            List<EntityType<?>> entityTypes,
            List<RootDescriptor> roots,
            Map<Class<?>, RootDescriptor> rootByEntityClass,
            Map<Class<?>, EntityType<?>> entityTypeByClass) {
    }

    private record AssociationEdge(
            RootDescriptor source,
            RootDescriptor target,
            String declaringEntityName,
            Class<?> declaringJavaType,
            String attributeName,
            boolean nullable) {
    }

    private record TopologicalResult(
            List<RootDescriptor> order,
            Set<RootDescriptor> remaining) {
    }

    private static class MutableDeletionTypeRecords {
        private final String clazz;
        private final String simpleName;
        private final String tableName;
        private final boolean securityData;
        private final List<DeletionRecordSnapshot> records = new ArrayList<>();

        private MutableDeletionTypeRecords(
                String clazz,
                String simpleName,
                String tableName,
                boolean securityData) {
            this.clazz = clazz;
            this.simpleName = simpleName;
            this.tableName = tableName;
            this.securityData = securityData;
        }

        private void add(DeletionRecordSnapshot record) {
            records.add(record);
        }

        private DeletionTypeRecordsSnapshot snapshot() {
            List<DeletionRecordSnapshot> sortedRecords = records.stream()
                    .sorted(Comparator.comparing(
                                    DeletionRecordSnapshot::tenantId,
                                    Comparator.nullsFirst(String::compareTo))
                            .thenComparing(
                                    DeletionRecordSnapshot::id,
                                    Comparator.nullsFirst(String::compareTo)))
                    .toList();
            return new DeletionTypeRecordsSnapshot(
                    clazz,
                    simpleName,
                    tableName,
                    securityData,
                    sortedRecords);
        }
    }

    private static class MutableTypeCount {
        private final String clazz;
        private final String simpleName;
        private final String tableName;
        private final boolean securityData;
        private long count;

        private MutableTypeCount(
                String clazz,
                String simpleName,
                String tableName,
                boolean securityData) {
            this.clazz = clazz;
            this.simpleName = simpleName;
            this.tableName = tableName;
            this.securityData = securityData;
        }

        private void add(long value) {
            count += value;
        }

        private TypeCountSnapshot snapshot() {
            return new TypeCountSnapshot(clazz, simpleName, tableName, count, securityData);
        }
    }
}
