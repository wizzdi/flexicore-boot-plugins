package com.flexicore.ui.service;

import com.flexicore.ui.data.GridPresetOperationFieldRepository;
import com.flexicore.ui.model.GridPreset;
import com.flexicore.ui.model.GridPresetOperationField;
import com.flexicore.ui.model.GridPresetOperationType;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import com.wizzdi.flexicore.security.configuration.SecurityContext;
import com.wizzdi.flexicore.security.service.BaseclassService;
import org.pf4j.Extension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Extension
@Component
public class GridPresetOperationFieldService implements Plugin {

    @Autowired
    private GridPresetOperationFieldRepository gridPresetOperationFieldRepository;

    public List<GridPresetOperationField> listByGridPresets(Collection<GridPreset> gridPresets) {
        return gridPresetOperationFieldRepository.listByGridPresets(gridPresets);
    }

    public void populateOperationFields(GridPreset gridPreset) {
        if (gridPreset != null) {
            populateOperationFields(Collections.singletonList(gridPreset));
        }
    }

    public void populateOperationFields(Collection<GridPreset> gridPresets) {
        if (gridPresets == null || gridPresets.isEmpty()) {
            return;
        }
        Map<String, GridPreset> presetsById = new LinkedHashMap<>();
        for (GridPreset gridPreset : gridPresets) {
            if (gridPreset != null && gridPreset.getId() != null) {
                gridPreset.setCreateOperationFields(Collections.emptyList());
                gridPreset.setUpdateOperationFields(Collections.emptyList());
                presetsById.put(gridPreset.getId(), gridPreset);
            }
        }
        if (presetsById.isEmpty()) {
            return;
        }

        Map<String, List<GridPresetOperationField>> createFields = new LinkedHashMap<>();
        Map<String, List<GridPresetOperationField>> updateFields = new LinkedHashMap<>();
        for (GridPresetOperationField field : listByGridPresets(presetsById.values())) {
            GridPreset parent = field.getGridPreset();
            if (parent == null || parent.getId() == null || !presetsById.containsKey(parent.getId())) {
                continue;
            }
            Map<String, List<GridPresetOperationField>> target = field.getOperationType() == GridPresetOperationType.CREATE
                    ? createFields
                    : updateFields;
            target.computeIfAbsent(parent.getId(), ignored -> new ArrayList<>()).add(field);
        }

        for (GridPreset gridPreset : presetsById.values()) {
            gridPreset.setCreateOperationFields(createFields.getOrDefault(gridPreset.getId(), Collections.emptyList()));
            gridPreset.setUpdateOperationFields(updateFields.getOrDefault(gridPreset.getId(), Collections.emptyList()));
        }
    }

    public void synchronizeOperationFieldsNoMerge(GridPreset gridPreset,
                                                   List<GridPresetOperationField> createDefinitions,
                                                   List<GridPresetOperationField> updateDefinitions,
                                                   List<Object> toMerge,
                                                   SecurityContext securityContext) {
        List<GridPresetOperationField> existing = gridPreset.getId() == null
                ? Collections.emptyList()
                : listByGridPresets(Collections.singletonList(gridPreset));
        Map<GridPresetOperationType, List<GridPresetOperationField>> existingByType = new EnumMap<>(GridPresetOperationType.class);
        for (GridPresetOperationField field : existing) {
            existingByType.computeIfAbsent(field.getOperationType(), ignored -> new ArrayList<>()).add(field);
        }

        List<GridPresetOperationField> createFields = synchronizeOperationNoMerge(
                gridPreset,
                GridPresetOperationType.CREATE,
                createDefinitions,
                existingByType.getOrDefault(GridPresetOperationType.CREATE, Collections.emptyList()),
                toMerge,
                securityContext);
        List<GridPresetOperationField> updateFields = synchronizeOperationNoMerge(
                gridPreset,
                GridPresetOperationType.UPDATE,
                updateDefinitions,
                existingByType.getOrDefault(GridPresetOperationType.UPDATE, Collections.emptyList()),
                toMerge,
                securityContext);
        gridPreset.setCreateOperationFields(createFields);
        gridPreset.setUpdateOperationFields(updateFields);
    }

    private List<GridPresetOperationField> synchronizeOperationNoMerge(GridPreset gridPreset,
                                                                       GridPresetOperationType operationType,
                                                                       List<GridPresetOperationField> definitions,
                                                                       List<GridPresetOperationField> existing,
                                                                       List<Object> toMerge,
                                                                       SecurityContext securityContext) {
        Map<String, List<GridPresetOperationField>> existingByPath = new LinkedHashMap<>();
        for (GridPresetOperationField field : existing) {
            existingByPath.computeIfAbsent(field.getFieldPath(), ignored -> new ArrayList<>()).add(field);
        }

        List<GridPresetOperationField> result = new ArrayList<>();
        List<GridPresetOperationField> requested = definitions != null ? definitions : Collections.emptyList();
        for (int priority = 0; priority < requested.size(); priority++) {
            GridPresetOperationField definition = requested.get(priority);
            List<GridPresetOperationField> candidates = existingByPath.get(definition.getFieldPath());
            boolean created = candidates == null || candidates.isEmpty();
            GridPresetOperationField field = created
                    ? createOperationFieldNoMerge(gridPreset, operationType, definition, priority, securityContext)
                    : candidates.removeFirst();
            boolean changed = created;
            if (field.getGridPreset() == null || !gridPreset.getId().equals(field.getGridPreset().getId())) {
                field.setGridPreset(gridPreset);
                changed = true;
            }
            if (field.getOperationType() != operationType) {
                field.setOperationType(operationType);
                changed = true;
            }
            if (!java.util.Objects.equals(field.getFieldPath(), definition.getFieldPath())) {
                field.setFieldPath(definition.getFieldPath());
                changed = true;
            }
            if (field.isVisible() != definition.isVisible()) {
                field.setVisible(definition.isVisible());
                changed = true;
            }
            if (field.getPriority() != priority) {
                field.setPriority(priority);
                changed = true;
            }
            if (field.isSoftDelete()) {
                field.setSoftDelete(false);
                changed = true;
            }
            if (changed) {
                toMerge.add(field);
            }
            result.add(field);
        }

        for (List<GridPresetOperationField> unused : existingByPath.values()) {
            for (GridPresetOperationField field : unused) {
                if (!field.isSoftDelete()) {
                    field.setSoftDelete(true);
                    toMerge.add(field);
                }
            }
        }
        return result;
    }

    private GridPresetOperationField createOperationFieldNoMerge(GridPreset gridPreset,
                                                                  GridPresetOperationType operationType,
                                                                  GridPresetOperationField definition,
                                                                  int priority,
                                                                  SecurityContext securityContext) {
        GridPresetOperationField field = new GridPresetOperationField();
        field.setId(UUID.randomUUID().toString());
        field.setName(operationType.name().toLowerCase() + "." + definition.getFieldPath());
        field.setGridPreset(gridPreset);
        field.setOperationType(operationType);
        field.setFieldPath(definition.getFieldPath());
        field.setVisible(definition.isVisible());
        field.setPriority(priority);
        BaseclassService.createSecurityObjectNoMerge(field, securityContext);
        return field;
    }
}
