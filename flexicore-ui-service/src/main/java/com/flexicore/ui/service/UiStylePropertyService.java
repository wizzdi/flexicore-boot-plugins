package com.flexicore.ui.service;

import com.flexicore.ui.data.UiStylePropertyRepository;
import com.flexicore.ui.model.UiStyle;
import com.flexicore.ui.model.UiStyleProperty;
import com.flexicore.ui.request.UiStylePropertyCreate;
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
public class UiStylePropertyService implements Plugin {

    @Autowired
    private UiStylePropertyRepository uiStylePropertyRepository;

    public List<UiStyleProperty> listByUiStyles(Collection<UiStyle> uiStyles) {
        return uiStylePropertyRepository.listByUiStyles(uiStyles);
    }

    public void populateProperties(UiStyle uiStyle) {
        if (uiStyle != null) {
            populateProperties(Collections.singletonList(uiStyle));
        }
    }

    public void populateProperties(Collection<UiStyle> uiStyles) {
        if (uiStyles == null || uiStyles.isEmpty()) {
            return;
        }
        Map<String, UiStyle> stylesById = new LinkedHashMap<>();
        for (UiStyle uiStyle : uiStyles) {
            if (uiStyle != null && uiStyle.getId() != null) {
                uiStyle.setProperties(Collections.emptyList());
                stylesById.put(uiStyle.getId(), uiStyle);
            }
        }
        if (stylesById.isEmpty()) {
            return;
        }

        Map<String, List<UiStyleProperty>> propertiesByStyle = new LinkedHashMap<>();
        for (UiStyleProperty property : listByUiStyles(stylesById.values())) {
            UiStyle parent = property.getUiStyle();
            if (parent != null && parent.getId() != null && stylesById.containsKey(parent.getId())) {
                propertiesByStyle.computeIfAbsent(parent.getId(), ignored -> new ArrayList<>()).add(property);
            }
        }
        for (UiStyle uiStyle : stylesById.values()) {
            uiStyle.setProperties(propertiesByStyle.getOrDefault(uiStyle.getId(), Collections.emptyList()));
        }
    }

    public void synchronizePropertiesNoMerge(UiStyle uiStyle,
                                              List<UiStylePropertyCreate> definitions,
                                              List<Object> toMerge,
                                              SecurityContext securityContext) {
        List<UiStyleProperty> existing = uiStyle.getId() == null
                ? Collections.emptyList()
                : listByUiStyles(Collections.singletonList(uiStyle));
        if (definitions == null) {
            uiStyle.setProperties(existing);
            return;
        }

        Map<String, List<UiStyleProperty>> existingByKey = new LinkedHashMap<>();
        for (UiStyleProperty property : existing) {
            existingByKey.computeIfAbsent(property.getPropertyKey(), ignored -> new ArrayList<>()).add(property);
        }

        List<UiStyleProperty> result = new ArrayList<>();
        for (int priority = 0; priority < definitions.size(); priority++) {
            UiStylePropertyCreate definition = definitions.get(priority);
            List<UiStyleProperty> candidates = existingByKey.get(definition.getPropertyKey());
            boolean created = candidates == null || candidates.isEmpty();
            UiStyleProperty property = created
                    ? createPropertyNoMerge(uiStyle, definition, priority, securityContext)
                    : candidates.removeFirst();
            boolean changed = created;
            if (property.getUiStyle() == null || !Objects.equals(uiStyle.getId(), property.getUiStyle().getId())) {
                property.setUiStyle(uiStyle);
                changed = true;
            }
            if (!Objects.equals(property.getPropertyKey(), definition.getPropertyKey())) {
                property.setPropertyKey(definition.getPropertyKey());
                changed = true;
            }
            if (!Objects.equals(property.getStringValue(), definition.getStringValue())) {
                property.setStringValue(definition.getStringValue());
                changed = true;
            }
            if (!Objects.equals(property.getNumericValue(), definition.getNumericValue())) {
                property.setNumericValue(definition.getNumericValue());
                changed = true;
            }
            if (!Objects.equals(property.getBooleanValue(), definition.getBooleanValue())) {
                property.setBooleanValue(definition.getBooleanValue());
                changed = true;
            }
            if (property.getPriority() != priority) {
                property.setPriority(priority);
                changed = true;
            }
            if (property.isSoftDelete()) {
                property.setSoftDelete(false);
                changed = true;
            }
            if (changed) {
                toMerge.add(property);
            }
            result.add(property);
        }

        for (List<UiStyleProperty> unused : existingByKey.values()) {
            for (UiStyleProperty property : unused) {
                if (!property.isSoftDelete()) {
                    property.setSoftDelete(true);
                    toMerge.add(property);
                }
            }
        }
        uiStyle.setProperties(result);
    }

    private UiStyleProperty createPropertyNoMerge(UiStyle uiStyle,
                                                   UiStylePropertyCreate definition,
                                                   int priority,
                                                   SecurityContext securityContext) {
        UiStyleProperty property = new UiStyleProperty();
        property.setId(UUID.randomUUID().toString());
        property.setName(definition.getPropertyKey());
        property.setUiStyle(uiStyle);
        property.setPropertyKey(definition.getPropertyKey());
        property.setStringValue(definition.getStringValue());
        property.setNumericValue(definition.getNumericValue());
        property.setBooleanValue(definition.getBooleanValue());
        property.setPriority(priority);
        BaseclassService.createSecurityObjectNoMerge(property, securityContext);
        return property;
    }
}
