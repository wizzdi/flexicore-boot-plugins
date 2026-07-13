package com.flexicore.ui.service;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.flexicore.model.*;
import com.flexicore.ui.data.GridPresetRepository;
import com.flexicore.ui.model.GridPreset;
import com.flexicore.ui.model.GridPresetOperationField;
import com.flexicore.ui.model.GridPresetRuntimeFilter;
import com.flexicore.ui.model.GridPresetStyle;
import com.flexicore.ui.model.UiField;
import com.flexicore.ui.request.*;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import com.wizzdi.flexicore.boot.dynamic.invokers.model.DynamicExecution;
import com.wizzdi.flexicore.boot.dynamic.invokers.model.ServiceCanonicalName;
import com.wizzdi.flexicore.boot.dynamic.invokers.request.DynamicExecutionCreate;
import com.wizzdi.flexicore.boot.dynamic.invokers.request.DynamicExecutionFilter;
import com.wizzdi.flexicore.boot.dynamic.invokers.request.DynamicInvokerFilter;
import com.wizzdi.flexicore.boot.dynamic.invokers.request.DynamicInvokerMethodFilter;
import com.wizzdi.flexicore.boot.dynamic.invokers.request.ExecuteInvokerRequest;
import com.wizzdi.flexicore.boot.dynamic.invokers.request.ExecuteInvokersResponse;
import com.wizzdi.flexicore.boot.dynamic.invokers.response.InvokerInfo;
import com.wizzdi.flexicore.boot.dynamic.invokers.response.InvokerMethodInfo;
import com.wizzdi.flexicore.boot.dynamic.invokers.response.ParameterInfo;
import com.wizzdi.flexicore.boot.dynamic.invokers.service.DynamicExecutionService;
import com.wizzdi.flexicore.boot.dynamic.invokers.service.DynamicInvokerService;
import com.wizzdi.flexicore.security.configuration.SecurityContext;
import com.wizzdi.flexicore.security.events.BasicCreated;
import com.wizzdi.flexicore.security.request.BasicPropertiesFilter;
import com.wizzdi.flexicore.security.request.PermissionGroupToBaseclassCreate;
import com.wizzdi.flexicore.security.response.PaginationResponse;
import com.wizzdi.flexicore.security.service.BaseclassService;
import com.wizzdi.flexicore.security.service.PermissionGroupToBaseclassService;
import org.pf4j.Extension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.event.EventListener;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import jakarta.persistence.metamodel.SingularAttribute;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;


@Extension
@Component
public class GridPresetService implements Plugin {

    private static final Logger logger = LoggerFactory.getLogger(GridPresetService.class);
    private static final TypeReference<LinkedHashMap<String, Object>> FILTERING_MAP_TYPE = new TypeReference<>() {
    };

    @Autowired
    private PresetService presetService;
    @Autowired
    private GridPresetRepository gridPresetRepository;
    @Autowired
    private GridPresetOperationFieldService gridPresetOperationFieldService;
    @Autowired
    private GridPresetRuntimeFilterService gridPresetRuntimeFilterService;
    @Autowired
    private GridPresetStyleService gridPresetStyleService;
    @Autowired
    private UiFieldService uiFieldService;
    @Autowired
    private PermissionGroupToBaseclassService permissionGroupToBaseclassService;
    @Autowired
    private DynamicExecutionService dynamicExecutionService;
    @Autowired
    private DynamicInvokerService dynamicInvokerService;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    @Lazy
    private SecurityContext adminSecurityContext;
    @Autowired
    @Qualifier("gridPresetClazz")
    private Clazz gridPresetClazz;

    public PaginationResponse<GridPreset> getAllGridPresets(
            GridPresetFiltering gridPresetFiltering,
            SecurityContext securityContext) {
        List<GridPreset> list = listAllGridPresets(gridPresetFiltering,
                securityContext);
        long count = gridPresetRepository.countAllGridPresets(
                gridPresetFiltering, securityContext);
        return new PaginationResponse<>(list, gridPresetFiltering, count);
    }

    public List<GridPreset> listAllGridPresets(
            GridPresetFiltering gridPresetFiltering,
            SecurityContext securityContext) {
        List<GridPreset> presets = gridPresetRepository.listAllGridPresets(gridPresetFiltering,
                securityContext);
        gridPresetOperationFieldService.populateOperationFields(presets);
        gridPresetRuntimeFilterService.populateRuntimeFilters(presets);
        return presets;
    }

    public boolean updateGridPresetNoMerge(GridPresetCreate createPreset,
                                           GridPreset preset) {
        boolean update = presetService.updatePresetNoMerge(createPreset, preset);

        if (!Objects.equals(createPreset.getRelatedClassCanonicalName(), preset.getRelatedClassCanonicalName())) {
            preset.setRelatedClassCanonicalName(createPreset.getRelatedClassCanonicalName());
            update = true;
        }
        if (!Objects.equals(createPreset.getDynamicInvokerCanonicalName(), preset.getDynamicInvokerCanonicalName())) {
            preset.setDynamicInvokerCanonicalName(createPreset.getDynamicInvokerCanonicalName());
            update = true;
        }
        if (!Objects.equals(createPreset.getDynamicInvokerMethodName(), preset.getDynamicInvokerMethodName())) {
            preset.setDynamicInvokerMethodName(createPreset.getDynamicInvokerMethodName());
            update = true;
        }
        if (!Objects.equals(createPreset.getFiltering(), preset.getFiltering())) {
            preset.setFiltering(createPreset.getFiltering());
            update = true;
        }
        if (!Objects.equals(createPreset.getCreateOperationMethodName(), preset.getCreateOperationMethodName())) {
            preset.setCreateOperationMethodName(createPreset.getCreateOperationMethodName());
            update = true;
        }
        if (!Objects.equals(createPreset.getUpdateOperationMethodName(), preset.getUpdateOperationMethodName())) {
            preset.setUpdateOperationMethodName(createPreset.getUpdateOperationMethodName());
            update = true;
        }
        if (!Objects.equals(createPreset.getTitle(), preset.getTitle())) {
            preset.setTitle(createPreset.getTitle());
            update = true;
        }
        if (!sameBaseclass(createPreset.getGridPresetStyle(), preset.getGridPresetStyle())) {
            preset.setGridPresetStyle(createPreset.getGridPresetStyle());
            update = true;
        }

        if (createPreset.getLatMapping() != null && !Objects.equals(createPreset.getLatMapping(), preset.getLatMapping())) {
            preset.setLatMapping(createPreset.getLatMapping());
            update = true;
        }
        if (createPreset.getLonMapping() != null && !Objects.equals(createPreset.getLonMapping(), preset.getLonMapping())) {
            preset.setLonMapping(createPreset.getLonMapping());
            update = true;
        }
        if (createPreset.getDynamicExecution() != null &&
                (preset.getDynamicExecution() == null || !createPreset.getDynamicExecution().getId().equals(preset.getDynamicExecution().getId()))) {
            preset.setDynamicExecution(createPreset.getDynamicExecution());
            update = true;
        }

        return update;
    }

    @Transactional
    public GridPreset updateGridPreset(GridPresetUpdate updatePreset,
                                       SecurityContext securityContext) {
        ensureValidated(updatePreset, securityContext);
        List<Object> toMerge = new ArrayList<>();
        prepareDynamicExecutionNoMerge(updatePreset, toMerge, securityContext);
        if (updateGridPresetNoMerge(updatePreset, updatePreset.getPreset())) {
            toMerge.add(updatePreset.getPreset());
        }
        gridPresetOperationFieldService.synchronizeOperationFieldsNoMerge(
                updatePreset.getPreset(),
                updatePreset.getCreateOperationFields(),
                updatePreset.getUpdateOperationFields(),
                toMerge,
                securityContext);
        gridPresetRuntimeFilterService.synchronizeRuntimeFiltersNoMerge(
                updatePreset.getPreset(), updatePreset.getRuntimeFilterFields(), toMerge, securityContext);
        if (!toMerge.isEmpty()) {
            gridPresetRepository.massMerge(toMerge);
        }
        return updatePreset.getPreset();

    }

    @Transactional
    public GridPreset createGridPreset(GridPresetCreate createPreset,
                                       SecurityContext securityContext) {
        ensureValidated(createPreset, securityContext);
        List<Object> toMerge = new ArrayList<>();
        GridPreset preset = createGridPresetNoMerge(createPreset, securityContext, toMerge);
        toMerge.add(preset);
        gridPresetOperationFieldService.synchronizeOperationFieldsNoMerge(
                preset,
                createPreset.getCreateOperationFields(),
                createPreset.getUpdateOperationFields(),
                toMerge,
                securityContext);
        gridPresetRuntimeFilterService.synchronizeRuntimeFiltersNoMerge(
                preset, createPreset.getRuntimeFilterFields(), toMerge, securityContext);
        gridPresetRepository.massMerge(toMerge);
        return preset;

    }

    private GridPreset createGridPresetNoMerge(GridPresetCreate createPreset,
                                               SecurityContext securityContext,
                                               List<Object> toMerge) {
        prepareDynamicExecutionNoMerge(createPreset, toMerge, securityContext);
        GridPreset preset = new GridPreset();
        preset.setId(UUID.randomUUID().toString());
        updateGridPresetNoMerge(createPreset, preset);
        BaseclassService.createSecurityObjectNoMerge(preset, securityContext);
        return preset;
    }

    private void prepareDynamicExecutionNoMerge(GridPresetCreate createPreset,
                                                List<Object> toMerge,
                                                SecurityContext securityContext) {
        DynamicExecutionCreate dynamicExecutionCreate = createPreset.getDynamicExecutionCreate();
        if (dynamicExecutionCreate == null) {
            return;
        }

        DynamicExecution dynamicExecution = dynamicExecutionService.createDynamicExecutionNoMerge(
                dynamicExecutionCreate, toMerge, securityContext);
        createPreset.setDynamicExecution(dynamicExecution);
    }

    public void validateCopy(GridPresetCopy gridPresetCopy,
                             SecurityContext securityContext) {
        String gridPresetId = gridPresetCopy.getId();
        GridPreset gridPreset = gridPresetId != null ? getByIdOrNull(gridPresetId, GridPreset.class, securityContext) : null;
        if (gridPreset == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No Grid Preset With id " + gridPresetId);
        }
        gridPresetCopy.setPreset(gridPreset);
        validate(gridPresetCopy, securityContext);
    }

    public void validate(GridPresetCreate createGridPreset,
                         SecurityContext securityContext) {
        if (createGridPreset.isDefinitionResolved()) {
            return;
        }
        presetService.validate(createGridPreset, securityContext);

        GridPreset existing = getExistingPreset(createGridPreset);
        if (existing != null) {
            gridPresetOperationFieldService.populateOperationFields(existing);
            gridPresetRuntimeFilterService.populateRuntimeFilters(existing);
        }
        resolvePresentation(createGridPreset, existing, securityContext);
        String requestedInvoker = trimToNull(createGridPreset.getDynamicInvokerCanonicalName());
        String requestedMethod = trimToNull(createGridPreset.getDynamicInvokerMethodName());
        String invokerName = requestedInvoker != null ? requestedInvoker : existing != null ? existing.getDynamicInvokerCanonicalName() : null;
        String methodName = requestedMethod != null ? requestedMethod : existing != null ? existing.getDynamicInvokerMethodName() : null;
        if (invokerName == null || methodName == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "dynamicInvokerCanonicalName and dynamicInvokerMethodName must be provided");
        }

        boolean sourceChanged = existing == null ||
                !Objects.equals(invokerName, existing.getDynamicInvokerCanonicalName()) ||
                !Objects.equals(methodName, existing.getDynamicInvokerMethodName());

        InvokerInfo invokerInfo = getInvokerInfo(invokerName);
        InvokerMethodInfo sourceMethod = getInvokerMethod(invokerInfo, methodName, "grid source");

        Map<String, Object> filtering = resolveFiltering(createGridPreset.getFiltering(), existing, sourceChanged);
        Object executionParametersHolder = convertFiltering(invokerInfo, sourceMethod, filtering);
        DynamicExecutionCreate dynamicExecutionCreate = createDynamicExecutionCreate(createGridPreset, invokerName, methodName, executionParametersHolder);
        dynamicExecutionService.validateCreate(dynamicExecutionCreate, securityContext);

        createGridPreset.setDynamicInvokerCanonicalName(invokerName);
        createGridPreset.setDynamicInvokerMethodName(methodName);
        createGridPreset.setFiltering(filtering);
        createGridPreset.setRuntimeFilterFields(resolveRuntimeFilters(
                createGridPreset.getRuntimeFilterFields(),
                existing != null ? existing.getRuntimeFilterFields() : null,
                sourceChanged, sourceMethod, filtering));
        createGridPreset.setRelatedClassCanonicalName(invokerInfo.getHandlingType() != null
                ? invokerInfo.getHandlingType().getCanonicalName()
                : existing != null ? existing.getRelatedClassCanonicalName() : createGridPreset.getRelatedClassCanonicalName());

        OperationDefinition createOperation = resolveOperation(
                invokerInfo,
                "create",
                createGridPreset.getCreateOperationMethodName(),
                createGridPreset.getCreateOperationFields(),
                existing != null ? existing.getCreateOperationMethodName() : null,
                existing != null ? existing.getCreateOperationFields() : null,
                sourceChanged);
        createGridPreset.setCreateOperationMethodName(createOperation.methodName());
        createGridPreset.setCreateOperationFields(createOperation.fields());

        OperationDefinition updateOperation = resolveOperation(
                invokerInfo,
                "update",
                createGridPreset.getUpdateOperationMethodName(),
                createGridPreset.getUpdateOperationFields(),
                existing != null ? existing.getUpdateOperationMethodName() : null,
                existing != null ? existing.getUpdateOperationFields() : null,
                sourceChanged);
        createGridPreset.setUpdateOperationMethodName(updateOperation.methodName());
        createGridPreset.setUpdateOperationFields(updateOperation.fields());

        prepareDynamicExecutionPlan(createGridPreset, existing, sourceChanged, dynamicExecutionCreate, executionParametersHolder, securityContext);
        createGridPreset.setDefinitionResolved(true);
    }

    private void ensureValidated(GridPresetCreate createGridPreset, SecurityContext securityContext) {
        if (!createGridPreset.isDefinitionResolved()) {
            validate(createGridPreset, securityContext);
        }
    }

    private GridPreset getExistingPreset(GridPresetCreate request) {
        if (request instanceof GridPresetUpdate update) {
            return update.getPreset();
        }
        if (request instanceof GridPresetCopy copy) {
            return copy.getPreset();
        }
        return null;
    }

    private void resolvePresentation(GridPresetCreate request,
                                     GridPreset existing,
                                     SecurityContext securityContext) {
        if (request.getTitle() == null && existing != null) {
            request.setTitle(existing.getTitle());
        }

        String requestedStyleId = request.getGridPresetStyleId();
        if (requestedStyleId == null) {
            request.setGridPresetStyle(existing != null ? existing.getGridPresetStyle() : null);
            return;
        }

        String styleId = trimToNull(requestedStyleId);
        if (styleId == null) {
            request.setGridPresetStyle(null);
            return;
        }

        GridPresetStyle style = gridPresetStyleService.getByIdOrNull(styleId, GridPresetStyle.class, securityContext);
        if (style == null || style.isSoftDelete()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "No accessible GridPresetStyle with id " + styleId);
        }
        request.setGridPresetStyle(style);
    }

    private boolean sameBaseclass(Baseclass first, Baseclass second) {
        if (first == second) {
            return true;
        }
        if (first == null || second == null) {
            return false;
        }
        return Objects.equals(first.getId(), second.getId());
    }

    private InvokerInfo getInvokerInfo(String invokerName) {
        List<InvokerInfo> invokers = dynamicInvokerService.listAllDynamicInvokers(
                new DynamicInvokerFilter().setInvokerTypes(new HashSet<>(Collections.singleton(invokerName))), null);
        return invokers.stream()
                .filter(f -> f.getName() != null && invokerName.equals(f.getName().getCanonicalName()))
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "No Dynamic Invoker with canonical name " + invokerName));
    }

    private InvokerMethodInfo getInvokerMethod(InvokerInfo invokerInfo, String methodName, String purpose) {
        return invokerInfo.getMethods().stream()
                .filter(f -> methodName.equals(f.getName()))
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "No " + purpose + " method " + methodName + " on Dynamic Invoker " + invokerInfo.getName().getCanonicalName()));
    }

    private Map<String, Object> resolveFiltering(Map<String, Object> requested,
                                                 GridPreset existing,
                                                 boolean sourceChanged) {
        if (requested != null) {
            return deepCopyMap(requested);
        }
        if (existing != null && !sourceChanged) {
            return deepCopyMap(existing.getFiltering());
        }
        return new LinkedHashMap<>();
    }

    private Map<String, Object> deepCopyMap(Map<String, Object> source) {
        if (source == null || source.isEmpty()) {
            return new LinkedHashMap<>();
        }
        return objectMapper.convertValue(source, FILTERING_MAP_TYPE);
    }

    private Object convertFiltering(InvokerInfo invokerInfo,
                                    InvokerMethodInfo sourceMethod,
                                    Map<String, Object> filtering) {
        Class<?> parameterType = resolveParameterHolderType(invokerInfo, sourceMethod);
        ObjectMapper strictMapper = objectMapper.copy().enable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        try {
            return strictMapper.convertValue(filtering, parameterType);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Filtering is not valid for " + invokerInfo.getName().getCanonicalName() + "." + sourceMethod.getName() + ": " + rootMessage(e), e);
        }
    }

    private Class<?> resolveParameterHolderType(InvokerInfo invokerInfo, InvokerMethodInfo methodInfo) {
        String parameterHolderType = methodInfo.getParameterHolderType();
        if (parameterHolderType == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Dynamic Invoker method " + methodInfo.getName() + " has no request parameter holder");
        }
        for (Method method : invokerInfo.getName().getMethods()) {
            if (!methodInfo.getName().equals(method.getName())) {
                continue;
            }
            for (Class<?> parameterType : method.getParameterTypes()) {
                if (parameterHolderType.equals(parameterType.getCanonicalName())) {
                    return parameterType;
                }
            }
        }
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                "Could not resolve parameter holder " + parameterHolderType + " for " + invokerInfo.getName().getCanonicalName() + "." + methodInfo.getName());
    }

    private DynamicExecutionCreate createDynamicExecutionCreate(GridPresetCreate preset,
                                                                 String invokerName,
                                                                 String methodName,
                                                                 Object executionParametersHolder) {
        String displayName = trimToNull(preset.getName());
        String executionName = "GridPreset source " + simpleName(invokerName) + "." + methodName;
        if (displayName != null) {
            executionName += " - " + displayName;
        }
        DynamicExecutionCreate create = new DynamicExecutionCreate();
        create.setName(executionName);
        create.setDescription("Managed internally by GridPreset");
        create.setServiceCanonicalNames(new HashSet<>(Collections.singleton(invokerName)));
        create.setMethodName(methodName);
        create.setExecutionParametersHolder(executionParametersHolder);
        return create;
    }

    private void prepareDynamicExecutionPlan(GridPresetCreate request,
                                             GridPreset existing,
                                             boolean sourceChanged,
                                             DynamicExecutionCreate create,
                                             Object defaultExecutionParametersHolder,
                                             SecurityContext securityContext) {
        boolean filteringChanged = existing == null || !Objects.equals(request.getFiltering(), existing.getFiltering());
        boolean updatingExistingPreset = request instanceof GridPresetUpdate;
        if (updatingExistingPreset && !sourceChanged && !filteringChanged &&
                existing != null && existing.getDynamicExecution() != null) {
            request.setDynamicExecution(existing.getDynamicExecution());
            request.setDynamicExecutionCreate(null);
            return;
        }

        if (request.getFiltering().isEmpty()) {
            DynamicExecution reusable = findReusableUnfilteredExecution(
                    request.getDynamicInvokerCanonicalName(),
                    request.getDynamicInvokerMethodName(),
                    defaultExecutionParametersHolder,
                    securityContext);
            if (reusable != null) {
                request.setDynamicExecution(reusable);
                request.setDynamicExecutionCreate(null);
                return;
            }
        }

        request.setDynamicExecution(null);
        request.setDynamicExecutionCreate(create);
    }

    private DynamicExecution findReusableUnfilteredExecution(String invokerName,
                                                              String methodName,
                                                              Object defaultExecutionParametersHolder,
                                                              SecurityContext securityContext) {
        DynamicExecutionFilter filter = new DynamicExecutionFilter()
                .setDynamicInvokerMethodFilter(new DynamicInvokerMethodFilter()
                        .setBasicPropertiesFilter(new BasicPropertiesFilter().setNames(Collections.singleton(methodName)))
                        .setDynamicInvokerFilter(new DynamicInvokerFilter()
                                .setInvokerTypes(Collections.singleton(invokerName))));
        return dynamicExecutionService.listAllDynamicExecutions(filter, securityContext).stream()
                .filter(f -> hasOnlyInvoker(f, invokerName))
                .filter(f -> hasDefaultExecutionParameters(f, defaultExecutionParametersHolder))
                .findFirst()
                .orElse(null);
    }

    private boolean hasOnlyInvoker(DynamicExecution dynamicExecution, String invokerName) {
        Set<String> invokers = dynamicExecution.getServiceCanonicalNames().stream()
                .map(ServiceCanonicalName::getServiceCanonicalName)
                .collect(java.util.stream.Collectors.toSet());
        return invokers.equals(Collections.singleton(invokerName));
    }

    private boolean hasDefaultExecutionParameters(DynamicExecution dynamicExecution,
                                                  Object defaultExecutionParametersHolder) {
        try {
            return Objects.equals(
                    objectMapper.valueToTree(dynamicExecution.getExecutionParametersHolder()),
                    objectMapper.valueToTree(defaultExecutionParametersHolder));
        } catch (IllegalArgumentException e) {
            logger.debug("could not compare dynamic execution {} parameters for GridPreset reuse",
                    dynamicExecution.getId(), e);
            return false;
        }
    }

    private OperationDefinition resolveOperation(InvokerInfo invokerInfo,
                                                 String operationType,
                                                 String requestedMethodName,
                                                 List<GridPresetOperationField> requestedFields,
                                                 String existingMethodName,
                                                 List<GridPresetOperationField> existingFields,
                                                 boolean sourceChanged) {
        boolean methodProvided = requestedMethodName != null;
        String normalizedRequestedMethod = trimToNull(requestedMethodName);
        String methodName;
        if (methodProvided) {
            methodName = normalizedRequestedMethod;
        } else if (!sourceChanged && existingMethodName != null) {
            methodName = existingMethodName;
        } else {
            methodName = detectOperationMethod(invokerInfo, operationType);
        }

        boolean methodChanged = sourceChanged || !Objects.equals(methodName, existingMethodName);
        List<GridPresetOperationField> fields;
        if (requestedFields != null) {
            fields = copyOperationFields(requestedFields);
        } else if (!methodChanged && existingFields != null) {
            fields = copyOperationFields(existingFields);
        } else if (methodName != null) {
            fields = createDefaultOperationFields(
                    getInvokerMethod(invokerInfo, methodName, operationType + " operation").getParameters());
        } else {
            fields = new ArrayList<>();
        }

        if (methodName == null) {
            if (!fields.isEmpty()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        operationType + "OperationFields cannot be supplied without " + operationType + "OperationMethodName");
            }
            return new OperationDefinition(null, new ArrayList<>());
        }

        InvokerMethodInfo method = getInvokerMethod(invokerInfo, methodName, operationType + " operation");
        return new OperationDefinition(methodName, validateOperationFields(operationType, fields, method.getParameters()));
    }


    private List<GridPresetOperationField> createDefaultOperationFields(List<ParameterInfo> parameters) {
        List<GridPresetOperationField> fields = new ArrayList<>();
        for (int priority = 0; priority < parameters.size(); priority++) {
            fields.add(new GridPresetOperationField(parameters.get(priority).getName(), true)
                    .setPriority(priority));
        }
        return fields;
    }

    private List<GridPresetOperationField> copyOperationFields(List<GridPresetOperationField> fields) {
        List<GridPresetOperationField> copied = new ArrayList<>();
        if (fields != null) {
            for (GridPresetOperationField field : fields) {
                copied.add(field != null ? new GridPresetOperationField(field) : null);
            }
        }
        return copied;
    }

    private String detectOperationMethod(InvokerInfo invokerInfo, String operationType) {
        List<InvokerMethodInfo> candidates = invokerInfo.getMethods().stream()
                .filter(f -> f.getName() != null && f.getName().toLowerCase(Locale.ROOT).startsWith(operationType.toLowerCase(Locale.ROOT)))
                .toList();
        if (candidates.isEmpty()) {
            return null;
        }
        if (invokerInfo.getHandlingType() != null) {
            String expected = operationType + invokerInfo.getHandlingType().getSimpleName();
            Optional<String> exact = candidates.stream().map(InvokerMethodInfo::getName)
                    .filter(f -> expected.equalsIgnoreCase(f)).findFirst();
            if (exact.isPresent()) {
                return exact.get();
            }
        }
        return candidates.size() == 1 ? candidates.getFirst().getName() : null;
    }

    private List<GridPresetOperationField> validateOperationFields(String operationType,
                                                                   List<GridPresetOperationField> requestedFields,
                                                                   List<ParameterInfo> parameters) {
        LinkedHashSet<String> validPaths = new LinkedHashSet<>();
        for (ParameterInfo parameter : parameters) {
            collectParameterPaths(parameter, null, validPaths);
        }

        List<GridPresetOperationField> result = new ArrayList<>();
        Set<String> seen = new HashSet<>();
        for (int priority = 0; priority < requestedFields.size(); priority++) {
            GridPresetOperationField requestedField = requestedFields.get(priority);
            if (requestedField == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        operationType + " operation fields cannot contain null entries");
            }
            String fieldPath = trimToNull(requestedField.getFieldPath());
            if (fieldPath == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        operationType + " operation field paths cannot be blank");
            }
            if (!validPaths.contains(fieldPath)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Unknown " + operationType + " operation field '" + fieldPath + "'. Valid fields: " + validPaths);
            }
            if (!seen.add(fieldPath)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Duplicate " + operationType + " operation field '" + fieldPath + "'");
            }
            result.add(new GridPresetOperationField(fieldPath, requestedField.isVisible())
                    .setPriority(priority));
        }
        return result;
    }

    private void collectParameterPaths(ParameterInfo parameter, String parent, Set<String> paths) {
        collectParameterPaths(parameter, parent, paths,
                Collections.newSetFromMap(new IdentityHashMap<>()));
    }

    private void collectParameterPaths(ParameterInfo parameter,
                                       String parent,
                                       Set<String> paths,
                                       Set<ParameterInfo> activePath) {
        if (parameter == null || parameter.getName() == null) {
            return;
        }
        String path = parent == null ? parameter.getName() : parent + "." + parameter.getName();
        paths.add(path);
        if (!activePath.add(parameter)) {
            return;
        }
        try {
            if (parameter.getSubParameters() != null) {
                for (ParameterInfo subParameter : parameter.getSubParameters()) {
                    collectParameterPaths(subParameter, path, paths, activePath);
                }
            }
        } finally {
            activePath.remove(parameter);
        }
    }

    private List<GridPresetRuntimeFilter> resolveRuntimeFilters(List<GridPresetRuntimeFilter> requested,
                                                                    List<GridPresetRuntimeFilter> existing,
                                                                    boolean sourceChanged,
                                                                    InvokerMethodInfo sourceMethod,
                                                                    Map<String, Object> designFiltering) {
        List<GridPresetRuntimeFilter> definitions;
        if (requested != null) {
            definitions = requested;
        } else if (!sourceChanged && existing != null) {
            definitions = existing;
        } else {
            definitions = Collections.emptyList();
        }
        LinkedHashMap<String, ParameterInfo> leafParameters = new LinkedHashMap<>();
        for (ParameterInfo parameter : sourceMethod.getParameters()) {
            collectLeafParameters(parameter, null, leafParameters,
                    Collections.newSetFromMap(new IdentityHashMap<>()));
        }
        Set<String> fixedPaths = new LinkedHashSet<>();
        collectFilteringLeafPaths(designFiltering, null, fixedPaths);
        List<GridPresetRuntimeFilter> result = new ArrayList<>();
        Set<String> seen = new HashSet<>();
        for (int priority = 0; priority < definitions.size(); priority++) {
            GridPresetRuntimeFilter definition = definitions.get(priority);
            if (definition == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "runtimeFilterFields cannot contain null entries");
            }
            String path = trimToNull(definition.getFieldPath());
            ParameterInfo parameter = path != null ? leafParameters.get(path) : null;
            if (parameter == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Unknown runtime filter field '" + path + "'. Valid fields: " + leafParameters.keySet());
            }
            if (!seen.add(path)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Duplicate runtime filter field '" + path + "'");
            }
            for (String fixedPath : fixedPaths) {
                if (pathsConflict(path, fixedPath)) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                            "Runtime filter '" + path + "' conflicts with immutable design filter '" + fixedPath + "'");
                }
            }
            GridPresetRuntimeFilter normalized = new GridPresetRuntimeFilter();
            normalized.setFieldPath(path);
            normalized.setPriority(priority);
            normalized.setName(trimToNull(definition.getName()) != null ? definition.getName() :
                    trimToNull(parameter.getDisplayName()) != null ? parameter.getDisplayName() : path);
            normalized.setDescription(trimToNull(definition.getDescription()) != null ? definition.getDescription() : parameter.getDescription());
            result.add(normalized);
        }
        return result;
    }

    private void collectLeafParameters(ParameterInfo parameter, String parent,
                                       Map<String, ParameterInfo> result, Set<ParameterInfo> active) {
        if (parameter == null || trimToNull(parameter.getName()) == null || !active.add(parameter)) return;
        String path = parent == null ? parameter.getName() : parent + "." + parameter.getName();
        try {
            if (parameter.getSubParameters() == null || parameter.getSubParameters().isEmpty()) {
                result.put(path, parameter);
            } else {
                for (ParameterInfo child : parameter.getSubParameters()) collectLeafParameters(child, path, result, active);
            }
        } finally { active.remove(parameter); }
    }

    private void collectFilteringLeafPaths(Object value, String parent, Set<String> result) {
        if (value instanceof Map<?, ?> map) {
            for (Map.Entry<?, ?> entry : map.entrySet()) {
                String key = String.valueOf(entry.getKey());
                String path = parent == null ? key : parent + "." + key;
                if (entry.getValue() instanceof Map<?, ?> child && !child.isEmpty()) collectFilteringLeafPaths(child, path, result);
                else if (entry.getValue() != null) result.add(path);
            }
        }
    }

    private boolean pathsConflict(String a, String b) {
        return a.equals(b) || a.startsWith(b + ".") || b.startsWith(a + ".");
    }

    public ExecuteInvokersResponse executeGridPreset(GridPresetExecutionRequest request, SecurityContext securityContext) {
        String id = request != null ? trimToNull(request.getGridPresetId()) : null;
        GridPreset preset = id != null ? getByIdOrNull(id, GridPreset.class, securityContext) : null;
        if (preset == null) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No GridPreset with id " + id);
        gridPresetRuntimeFilterService.populateRuntimeFilters(preset);
        Map<String, GridPresetRuntimeFilter> allowed = new LinkedHashMap<>();
        for (GridPresetRuntimeFilter field : preset.getRuntimeFilterFields()) allowed.put(field.getFieldPath(), field);
        Map<String, Object> runtime = request.getRuntimeFiltering() != null ? request.getRuntimeFiltering() : Collections.emptyMap();
        Map<String, Object> flat = new LinkedHashMap<>();
        flattenRuntimeFiltering(runtime, null, flat);
        for (String path : flat.keySet()) {
            if (!allowed.containsKey(path)) throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Runtime filter '" + path + "' is not exposed by GridPreset " + preset.getName());
        }
        Set<String> fixedPaths = new LinkedHashSet<>();
        collectFilteringLeafPaths(preset.getFiltering(), null, fixedPaths);
        Map<String, Object> merged = deepCopyMap(preset.getFiltering());
        for (Map.Entry<String, Object> entry : flat.entrySet()) {
            for (String fixedPath : fixedPaths) {
                if (pathsConflict(entry.getKey(), fixedPath)) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                            "Runtime filter '" + entry.getKey() + "' conflicts with immutable design filter '" + fixedPath + "'");
                }
            }
            Object value = entry.getValue();
            if (value != null && (!(value instanceof String text) || !text.isBlank())) putPath(merged, entry.getKey(), value);
        }
        InvokerInfo invoker = getInvokerInfo(preset.getDynamicInvokerCanonicalName());
        InvokerMethodInfo method = getInvokerMethod(invoker, preset.getDynamicInvokerMethodName(), "grid source");
        Object holder = convertFiltering(invoker, method, merged);
        DynamicExecution storedExecution = preset.getDynamicExecution() != null
                ? dynamicExecutionService.getByIdOrNull(preset.getDynamicExecution().getId(), DynamicExecution.class, securityContext)
                : null;
        if (storedExecution == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "GridPreset has no accessible hidden DynamicExecution");
        }
        ExecuteInvokerRequest execute = dynamicExecutionService.getExecuteInvokerRequest(storedExecution, securityContext);
        execute.setExecutionParametersHolder(holder);
        return dynamicInvokerService.executeInvoker(execute, securityContext);
    }

    private void flattenRuntimeFiltering(Map<String, Object> source, String parent, Map<String, Object> target) {
        for (Map.Entry<String, Object> entry : source.entrySet()) {
            String path = parent == null ? entry.getKey() : parent + "." + entry.getKey();
            if (entry.getValue() instanceof Map<?, ?> child) {
                Map<String, Object> cast = new LinkedHashMap<>();
                child.forEach((k, v) -> cast.put(String.valueOf(k), v));
                flattenRuntimeFiltering(cast, path, target);
            } else target.put(path, entry.getValue());
        }
    }

    @SuppressWarnings("unchecked")
    private void putPath(Map<String, Object> target, String path, Object value) {
        String[] segments = path.split("\\.");
        Map<String, Object> current = target;
        for (int i = 0; i < segments.length - 1; i++) {
            Object next = current.get(segments[i]);
            if (!(next instanceof Map<?, ?>)) {
                Map<String, Object> created = new LinkedHashMap<>();
                current.put(segments[i], created);
                current = created;
            } else current = (Map<String, Object>) next;
        }
        current.put(segments[segments.length - 1], value);
    }

    public String validateColumnFieldPath(GridPreset gridPreset, String requestedFieldPath) {
        String fieldPath = trimToNull(requestedFieldPath);
        if (fieldPath == null) {
            return null;
        }
        if (gridPreset == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "A GridPreset is required to validate table column fieldPath");
        }

        Class<?> currentType = resolveGridRowType(gridPreset);
        String[] segments = fieldPath.split("\\.", -1);
        List<String> normalizedSegments = new ArrayList<>();
        for (int i = 0; i < segments.length; i++) {
            String segment = trimToNull(segments[i]);
            if (segment == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Invalid table column fieldPath '" + fieldPath + "'");
            }
            Class<?> propertyType = getPropertyType(currentType, segment);
            if (propertyType == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Unknown property '" + segment + "' on " + currentType.getCanonicalName() +
                                " while resolving table column fieldPath '" + fieldPath + "'");
            }
            if (i < segments.length - 1 && isTerminalPropertyType(propertyType)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Property '" + segment + "' on " + currentType.getCanonicalName() +
                                " cannot be traversed while resolving table column fieldPath '" + fieldPath + "'");
            }
            normalizedSegments.add(segment);
            currentType = propertyType;
        }
        return String.join(".", normalizedSegments);
    }

    private Class<?> resolveGridRowType(GridPreset gridPreset) {
        String invokerName = trimToNull(gridPreset.getDynamicInvokerCanonicalName());
        if (invokerName != null) {
            Class<?> handlingType = getInvokerInfo(invokerName).getHandlingType();
            if (handlingType != null) {
                return handlingType;
            }
        }
        String relatedClassCanonicalName = trimToNull(gridPreset.getRelatedClassCanonicalName());
        if (relatedClassCanonicalName == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "GridPreset has no related row type");
        }
        try {
            ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
            ClassLoader classLoader = contextClassLoader != null ? contextClassLoader : getClass().getClassLoader();
            return Class.forName(relatedClassCanonicalName, false, classLoader);
        } catch (ClassNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Could not resolve GridPreset row type " + relatedClassCanonicalName, e);
        }
    }

    private Class<?> getPropertyType(Class<?> type, String propertyName) {
        try {
            for (PropertyDescriptor descriptor : Introspector.getBeanInfo(type).getPropertyDescriptors()) {
                if (propertyName.equals(descriptor.getName()) && descriptor.getReadMethod() != null) {
                    return descriptor.getReadMethod().getReturnType();
                }
            }
        } catch (IntrospectionException e) {
            logger.debug("could not inspect {} while validating GridPreset column field path", type, e);
        }

        for (Class<?> current = type; current != null; current = current.getSuperclass()) {
            try {
                Field field = current.getDeclaredField(propertyName);
                return field.getType();
            } catch (NoSuchFieldException ignored) {
            }
        }
        return null;
    }

    private boolean isTerminalPropertyType(Class<?> type) {
        return type.isPrimitive() || type.isArray() || CharSequence.class.isAssignableFrom(type) ||
                Number.class.isAssignableFrom(type) || Boolean.class.equals(type) || Character.class.equals(type) ||
                Date.class.isAssignableFrom(type) || java.time.temporal.Temporal.class.isAssignableFrom(type) ||
                Enum.class.isAssignableFrom(type) || Collection.class.isAssignableFrom(type) ||
                Map.class.isAssignableFrom(type);
    }

    private String trimToNull(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private String simpleName(String canonicalName) {
        int lastDot = canonicalName.lastIndexOf('.');
        return lastDot > -1 ? canonicalName.substring(lastDot + 1) : canonicalName;
    }

    private String rootMessage(Throwable throwable) {
        Throwable current = throwable;
        while (current.getCause() != null) {
            current = current.getCause();
        }
        return current.getMessage() != null ? current.getMessage() : current.getClass().getSimpleName();
    }

    @Transactional
    public GridPreset copyGridPreset(GridPresetCopy gridPresetCopy,
                                     SecurityContext securityContext) {
        ensureValidated(gridPresetCopy, securityContext);
        GridPreset source = gridPresetCopy.getPreset();
        if (gridPresetCopy.getName() == null) {
            gridPresetCopy.setName(source.getName());
        }
        if (gridPresetCopy.getDescription() == null) {
            gridPresetCopy.setDescription(source.getDescription());
        }
        if (gridPresetCopy.getExternalId() == null) {
            gridPresetCopy.setExternalId(source.getExternalId());
        }
        if (!gridPresetCopy.isTitleSet()) {
            gridPresetCopy.setTitle(source.getTitle());
        }
        if (!gridPresetCopy.isUiStyleIdSet()) {
            gridPresetCopy.setUiStyleId(source.getUiStyleId());
            gridPresetCopy.setUiStyle(source.getUiStyle());
        }
        Map<String, Object> copiedJson = new LinkedHashMap<>(source.any());
        copiedJson.putAll(gridPresetCopy.any());
        gridPresetCopy.setJsonNode(copiedJson);

        List<Object> toMerge = new ArrayList<>();
        GridPreset gridPreset = createGridPresetNoMerge(gridPresetCopy, securityContext, toMerge);
        toMerge.add(gridPreset);
        gridPresetOperationFieldService.synchronizeOperationFieldsNoMerge(
                gridPreset,
                gridPresetCopy.getCreateOperationFields(),
                gridPresetCopy.getUpdateOperationFields(),
                toMerge,
                securityContext);
        gridPresetRuntimeFilterService.synchronizeRuntimeFiltersNoMerge(
                gridPreset, gridPresetCopy.getRuntimeFilterFields(), toMerge, securityContext);
        List<UiField> uiFields = uiFieldService.listAllUiFields(
                new UiFieldFiltering().setPresets(Collections.singletonList(source)), securityContext);
        for (UiField uiField : uiFields) {
            UiFieldCreate uiFieldCreate = uiFieldService.getUIFieldCreate(uiField);
            uiFieldCreate.setPreset(gridPreset);
            UiField uiFieldNoMerge = uiFieldService.createUiFieldNoMerge(uiFieldCreate, securityContext);
            toMerge.add(uiFieldNoMerge);
        }
        gridPresetRepository.massMerge(toMerge);
        return gridPreset;
    }

    @EventListener
    @Async
    public void handlePresetPermissionGroupCreated(BasicCreated<PermissionGroupToBaseclass> baseclassCreated) {
        PermissionGroupToBaseclass permissionGroupToBaseclass = baseclassCreated.getBaseclass();
        PermissionGroup permissionGroup = permissionGroupToBaseclass.getPermissionGroup();
        if (permissionGroupToBaseclass.getSecuredType().equals(gridPresetClazz.name())) {
            SecurityContext securityContext = adminSecurityContext;
            String baseclass = permissionGroupToBaseclass.getSecuredId();
            List<GridPreset> presets = listAllGridPresets(new GridPresetFiltering().setRelatedBaseclass(Collections.singleton(baseclass)), null);
            for (GridPreset preset : presets) {
                if (preset.getDynamicExecution() != null) {
                    logger.info("grid preset {}({}) was attached to permission group {}({}), will attach dynamic execution",
                            preset.getName(), preset.getId(), permissionGroup.getName(), permissionGroup.getId());
                    PermissionGroupToBaseclassCreate createPermissionGroupLinkRequest = new PermissionGroupToBaseclassCreate()
                            .setPermissionGroup(permissionGroup)
                            .setSecuredId(preset.getDynamicExecution().getSecurityId())
                            .setSecuredType(Clazz.ofClass(DynamicExecution.class));
                    permissionGroupToBaseclassService.createPermissionGroupToBaseclass(createPermissionGroupLinkRequest, securityContext);
                }
            }
        }
    }

    public <T extends Baseclass> List<T> listByIds(Class<T> c, Set<String> ids, SecurityContext securityContext) {
        return gridPresetRepository.listByIds(c, ids, securityContext);
    }

    public <T extends Baseclass> T getByIdOrNull(String id, Class<T> c, SecurityContext securityContext) {
        return gridPresetRepository.getByIdOrNull(id, c, securityContext);
    }

    public <D extends Basic, E extends Baseclass, T extends D> T getByIdOrNull(String id, Class<T> c, SingularAttribute<D, E> baseclassAttribute, SecurityContext securityContext) {
        return gridPresetRepository.getByIdOrNull(id, c, baseclassAttribute, securityContext);
    }

    public <D extends Basic, E extends Baseclass, T extends D> List<T> listByIds(Class<T> c, Set<String> ids, SingularAttribute<D, E> baseclassAttribute, SecurityContext securityContext) {
        return gridPresetRepository.listByIds(c, ids, baseclassAttribute, securityContext);
    }

    public <D extends Basic, T extends D> List<T> findByIds(Class<T> c, Set<String> ids, SingularAttribute<D, String> idAttribute) {
        return gridPresetRepository.findByIds(c, ids, idAttribute);
    }

    public <T extends Basic> List<T> findByIds(Class<T> c, Set<String> requested) {
        return gridPresetRepository.findByIds(c, requested);
    }

    public <T> T findByIdOrNull(Class<T> type, String id) {
        return gridPresetRepository.findByIdOrNull(type, id);
    }

    @Transactional
    public void merge(Object base) {
        gridPresetRepository.merge(base);
    }

    @Transactional
    public void massMerge(List<?> toMerge) {
        gridPresetRepository.massMerge(toMerge);
    }

    private record OperationDefinition(String methodName, List<GridPresetOperationField> fields) {
    }
}
