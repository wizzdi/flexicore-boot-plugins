package com.flexicore.ui.service;

import com.flexicore.ui.data.GridPresetRuntimeFilterRepository;
import com.flexicore.ui.model.GridPreset;
import com.flexicore.ui.model.GridPresetRuntimeFilter;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import com.wizzdi.flexicore.security.configuration.SecurityContext;
import com.wizzdi.flexicore.security.service.BaseclassService;
import org.pf4j.Extension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@Extension
@Component
public class GridPresetRuntimeFilterService implements Plugin {

    @Autowired
    private GridPresetRuntimeFilterRepository repository;

    public List<GridPresetRuntimeFilter> listByGridPresets(Collection<GridPreset> presets) {
        return repository.listByGridPresets(presets);
    }

    public void populateRuntimeFilters(GridPreset preset) {
        if (preset != null) {
            populateRuntimeFilters(Collections.singletonList(preset));
        }
    }

    public void populateRuntimeFilters(Collection<GridPreset> presets) {
        if (presets == null || presets.isEmpty()) return;
        Map<String, GridPreset> byId = new LinkedHashMap<>();
        for (GridPreset preset : presets) {
            if (preset != null && preset.getId() != null) {
                preset.setRuntimeFilterFields(Collections.emptyList());
                byId.put(preset.getId(), preset);
            }
        }
        Map<String, List<GridPresetRuntimeFilter>> grouped = new LinkedHashMap<>();
        for (GridPresetRuntimeFilter field : listByGridPresets(byId.values())) {
            if (field.getGridPreset() != null && byId.containsKey(field.getGridPreset().getId())) {
                grouped.computeIfAbsent(field.getGridPreset().getId(), ignored -> new ArrayList<>()).add(field);
            }
        }
        for (GridPreset preset : byId.values()) {
            preset.setRuntimeFilterFields(grouped.getOrDefault(preset.getId(), Collections.emptyList()));
        }
    }

    public void synchronizeRuntimeFiltersNoMerge(GridPreset preset,
                                                  List<GridPresetRuntimeFilter> definitions,
                                                  List<Object> toMerge,
                                                  SecurityContext securityContext) {
        List<GridPresetRuntimeFilter> existing = preset.getId() == null ? Collections.emptyList()
                : listByGridPresets(Collections.singletonList(preset));
        Map<String, List<GridPresetRuntimeFilter>> byPath = new LinkedHashMap<>();
        for (GridPresetRuntimeFilter field : existing) {
            byPath.computeIfAbsent(field.getFieldPath(), ignored -> new ArrayList<>()).add(field);
        }
        List<GridPresetRuntimeFilter> result = new ArrayList<>();
        List<GridPresetRuntimeFilter> requested = definitions != null ? definitions : Collections.emptyList();
        for (int priority = 0; priority < requested.size(); priority++) {
            GridPresetRuntimeFilter definition = requested.get(priority);
            List<GridPresetRuntimeFilter> candidates = byPath.get(definition.getFieldPath());
            boolean created = candidates == null || candidates.isEmpty();
            GridPresetRuntimeFilter field = created ? createNoMerge(preset, definition, priority, securityContext) : candidates.removeFirst();
            boolean changed = created;
            if (field.getGridPreset() == null || !Objects.equals(preset.getId(), field.getGridPreset().getId())) { field.setGridPreset(preset); changed = true; }
            if (!Objects.equals(field.getFieldPath(), definition.getFieldPath())) { field.setFieldPath(definition.getFieldPath()); changed = true; }
            if (!Objects.equals(field.getName(), definition.getName())) { field.setName(definition.getName()); changed = true; }
            if (!Objects.equals(field.getDescription(), definition.getDescription())) { field.setDescription(definition.getDescription()); changed = true; }
            if (field.getPriority() != priority) { field.setPriority(priority); changed = true; }
            if (field.isSoftDelete()) { field.setSoftDelete(false); changed = true; }
            if (changed) toMerge.add(field);
            result.add(field);
        }
        for (List<GridPresetRuntimeFilter> unused : byPath.values()) {
            for (GridPresetRuntimeFilter field : unused) {
                if (!field.isSoftDelete()) { field.setSoftDelete(true); toMerge.add(field); }
            }
        }
        preset.setRuntimeFilterFields(result);
    }

    private GridPresetRuntimeFilter createNoMerge(GridPreset preset, GridPresetRuntimeFilter definition,
                                                   int priority, SecurityContext securityContext) {
        GridPresetRuntimeFilter field = new GridPresetRuntimeFilter();
        field.setId(UUID.randomUUID().toString());
        field.setName(definition.getName());
        field.setDescription(definition.getDescription());
        field.setGridPreset(preset);
        field.setFieldPath(definition.getFieldPath());
        field.setPriority(priority);
        BaseclassService.createSecurityObjectNoMerge(field, securityContext);
        return field;
    }
}
