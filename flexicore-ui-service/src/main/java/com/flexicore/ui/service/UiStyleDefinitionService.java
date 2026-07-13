package com.flexicore.ui.service;

import com.flexicore.ui.model.UiStyleTargets;
import com.flexicore.ui.request.UiStylePropertyDefinitionFilter;
import com.flexicore.ui.response.UiStylePropertyDefinition;
import com.flexicore.ui.style.UiStylePropertyDefinitionProvider;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import org.pf4j.Extension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Extension
@Component
public class UiStyleDefinitionService implements Plugin {

    @Autowired
    private List<UiStylePropertyDefinitionProvider> providers;

    public List<UiStylePropertyDefinition> getDefinitions(UiStylePropertyDefinitionFilter filter) {
        Set<String> requestedTargets = normalize(filter != null ? filter.getTargetTypes() : null);
        Map<String, UiStylePropertyDefinition> definitions = getDefinitionMap();
        validateTargets(requestedTargets, definitions.values());
        return definitions.values().stream()
                .filter(definition -> requestedTargets.isEmpty()
                        || UiStyleTargets.COMMON.equals(definition.getTargetType())
                        || requestedTargets.contains(definition.getTargetType()))
                .sorted(definitionComparator())
                .toList();
    }

    public Map<String, UiStylePropertyDefinition> getDefinitionMap() {
        Map<String, UiStylePropertyDefinition> definitions = new LinkedHashMap<>();
        for (UiStylePropertyDefinitionProvider provider : providers) {
            Collection<UiStylePropertyDefinition> provided = provider.getDefinitions();
            if (provided == null) {
                continue;
            }
            for (UiStylePropertyDefinition definition : provided) {
                if (definition == null || definition.getKey() == null || definition.getKey().isBlank()) {
                    throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                            "A UI style property provider returned a definition without a key");
                }
                String key = normalize(definition.getKey());
                String targetType = normalize(definition.getTargetType());
                String section = normalize(definition.getSection());
                if (targetType == null || definition.getValueType() == null) {
                    throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                            "UI style property " + key + " must define targetType and valueType");
                }
                definition.setKey(key);
                definition.setTargetType(targetType);
                definition.setSection(section != null ? section : "GENERAL");
                if (definition.getDisplayName() == null || definition.getDisplayName().isBlank()) {
                    definition.setDisplayName(key);
                }
                UiStylePropertyDefinition previous = definitions.putIfAbsent(key, definition);
                if (previous != null) {
                    throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                            "Duplicate UI style property definition " + key);
                }
            }
        }
        return definitions;
    }

    public UiStylePropertyDefinition getRequiredDefinition(String propertyKey) {
        String normalized = normalize(propertyKey);
        UiStylePropertyDefinition definition = getDefinitionMap().get(normalized);
        if (definition == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Unsupported UI style property " + propertyKey);
        }
        return definition;
    }

    public Set<String> resolvePropertyKeys(Set<String> targetTypes, Set<String> requestedPropertyKeys) {
        Map<String, UiStylePropertyDefinition> definitions = getDefinitionMap();
        Set<String> normalizedTargets = normalize(targetTypes);
        Set<String> normalizedKeys = normalize(requestedPropertyKeys);
        validateTargets(normalizedTargets, definitions.values());

        Set<String> targetKeys = definitions.values().stream()
                .filter(definition -> normalizedTargets.isEmpty()
                        || UiStyleTargets.COMMON.equals(definition.getTargetType())
                        || normalizedTargets.contains(definition.getTargetType()))
                .map(UiStylePropertyDefinition::getKey)
                .collect(Collectors.toCollection(LinkedHashSet::new));

        if (normalizedKeys.isEmpty()) {
            return targetKeys;
        }
        for (String key : normalizedKeys) {
            if (!definitions.containsKey(key)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Unsupported UI style property " + key);
            }
        }
        if (normalizedTargets.isEmpty()) {
            return normalizedKeys;
        }
        targetKeys.retainAll(normalizedKeys);
        return targetKeys;
    }


    private void validateTargets(Set<String> requestedTargets,
                                 Collection<UiStylePropertyDefinition> definitions) {
        if (requestedTargets.isEmpty()) {
            return;
        }
        Set<String> knownTargets = definitions.stream()
                .map(UiStylePropertyDefinition::getTargetType)
                .filter(java.util.Objects::nonNull)
                .collect(Collectors.toSet());
        Set<String> unknownTargets = new LinkedHashSet<>(requestedTargets);
        unknownTargets.removeAll(knownTargets);
        if (!unknownTargets.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Unsupported UI style target types " + unknownTargets);
        }
    }

    private Comparator<UiStylePropertyDefinition> definitionComparator() {
        return Comparator.comparing(UiStylePropertyDefinition::getTargetType,
                        Comparator.nullsFirst(String::compareTo))
                .thenComparingInt(UiStylePropertyDefinition::getPriority)
                .thenComparing(UiStylePropertyDefinition::getSection,
                        Comparator.nullsFirst(String::compareTo))
                .thenComparing(UiStylePropertyDefinition::getKey);
    }

    private Set<String> normalize(Collection<String> values) {
        if (values == null || values.isEmpty()) {
            return new LinkedHashSet<>();
        }
        Set<String> normalized = new LinkedHashSet<>();
        for (String value : values) {
            String current = normalize(value);
            if (current != null) {
                normalized.add(current);
            }
        }
        return normalized;
    }

    private String normalize(String value) {
        return value != null && !value.isBlank() ? value.trim().toUpperCase(Locale.ROOT) : null;
    }
}
