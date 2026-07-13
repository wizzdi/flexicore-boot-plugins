package com.flexicore.ui.service;

import com.flexicore.model.Baseclass;
import com.flexicore.model.Basic;
import com.flexicore.ui.data.UiStyleRepository;
import com.flexicore.ui.model.UiStyle;
import com.flexicore.ui.model.UiStyleValueType;
import com.flexicore.ui.request.UiStyleCreate;
import com.flexicore.ui.request.UiStyleFiltering;
import com.flexicore.ui.request.UiStylePropertyCreate;
import com.flexicore.ui.request.UiStyleUpdate;
import com.flexicore.ui.response.UiStyleAllowedValue;
import com.flexicore.ui.response.UiStylePropertyDefinition;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import com.wizzdi.flexicore.security.configuration.SecurityContext;
import com.wizzdi.flexicore.security.response.PaginationResponse;
import com.wizzdi.flexicore.security.service.BaseclassService;
import com.wizzdi.flexicore.security.service.BasicService;
import jakarta.persistence.metamodel.SingularAttribute;
import org.pf4j.Extension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Pattern;

@Extension
@Component
public class UiStyleService implements Plugin {

    private static final Pattern HEX_COLOR = Pattern.compile("#(?:[0-9a-fA-F]{3}|[0-9a-fA-F]{4}|[0-9a-fA-F]{6}|[0-9a-fA-F]{8})");
    @Autowired
    private UiStyleRepository uiStyleRepository;
    @Autowired
    private UiStylePropertyService uiStylePropertyService;
    @Autowired
    private UiStyleDefinitionService uiStyleDefinitionService;
    @Autowired
    private BasicService basicService;

    public PaginationResponse<UiStyle> getAllUiStyles(UiStyleFiltering filtering,
                                                       SecurityContext securityContext) {
        validateFiltering(filtering, securityContext);
        List<UiStyle> list = listAllUiStyles(filtering, securityContext);
        long count = uiStyleRepository.countAllUiStyles(filtering, securityContext);
        return new PaginationResponse<>(list, filtering, count);
    }

    public List<UiStyle> listAllUiStyles(UiStyleFiltering filtering,
                                         SecurityContext securityContext) {
        List<UiStyle> styles = uiStyleRepository.listAllUiStyles(filtering, securityContext);
        uiStylePropertyService.populateProperties(styles);
        return styles;
    }

    @Transactional
    public UiStyle createUiStyle(UiStyleCreate create, SecurityContext securityContext) {
        validate(create, securityContext);
        List<Object> toMerge = new ArrayList<>();
        UiStyle style = new UiStyle();
        style.setId(UUID.randomUUID().toString());
        updateUiStyleNoMerge(create, style);
        BaseclassService.createSecurityObjectNoMerge(style, securityContext);
        toMerge.add(style);
        uiStylePropertyService.synchronizePropertiesNoMerge(
                style, create.getProperties() != null ? create.getProperties() : List.of(),
                toMerge, securityContext);
        uiStyleRepository.massMerge(toMerge);
        return style;
    }

    @Transactional
    public UiStyle updateUiStyle(UiStyleUpdate update, SecurityContext securityContext) {
        validate(update, securityContext);
        UiStyle style = update.getUiStyle();
        List<Object> toMerge = new ArrayList<>();
        if (updateUiStyleNoMerge(update, style)) {
            toMerge.add(style);
        }
        uiStylePropertyService.synchronizePropertiesNoMerge(
                style, update.getProperties(), toMerge, securityContext);
        if (!toMerge.isEmpty()) {
            uiStyleRepository.massMerge(toMerge);
        }
        return style;
    }

    public boolean updateUiStyleNoMerge(UiStyleCreate create, UiStyle style) {
        return basicService.updateBasicNoMerge(create, style);
    }

    public void validate(UiStyleCreate create, SecurityContext securityContext) {
        basicService.validate(create, securityContext);
        String effectiveName = create.getName();
        if ((effectiveName == null || effectiveName.isBlank())
                && create instanceof UiStyleUpdate update && update.getUiStyle() != null) {
            effectiveName = update.getUiStyle().getName();
        }
        if (effectiveName == null || effectiveName.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "UI style name must be provided");
        }

        if (create.getProperties() == null) {
            return;
        }
        Set<String> keys = new HashSet<>();
        for (int index = 0; index < create.getProperties().size(); index++) {
            UiStylePropertyCreate property = create.getProperties().get(index);
            if (property == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "UI style property at index " + index + " is null");
            }
            UiStylePropertyDefinition definition = uiStyleDefinitionService.getRequiredDefinition(property.getPropertyKey());
            property.setPropertyKey(definition.getKey());
            if (!keys.add(definition.getKey())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "UI style property " + definition.getKey() + " appears more than once");
            }
            validateValue(property, definition);
        }
    }

    public void validateFiltering(UiStyleFiltering filtering, SecurityContext securityContext) {
        basicService.validate(filtering, securityContext);
        if (filtering.getBasicPropertiesFilter() != null) {
            basicService.validate(filtering.getBasicPropertiesFilter(), securityContext);
        }
        boolean constrained = (filtering.getTargetTypes() != null && !filtering.getTargetTypes().isEmpty())
                || (filtering.getPropertyKeys() != null && !filtering.getPropertyKeys().isEmpty());
        filtering.setPropertyConstraint(constrained);
        filtering.setResolvedPropertyKeys(uiStyleDefinitionService.resolvePropertyKeys(
                filtering.getTargetTypes(), filtering.getPropertyKeys()));
    }

    private void validateValue(UiStylePropertyCreate property, UiStylePropertyDefinition definition) {
        UiStyleValueType valueType = definition.getValueType();
        if (valueType == null) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "UI style property " + definition.getKey() + " has no value type");
        }
        switch (valueType) {
            case BOOLEAN -> {
                require(property.getBooleanValue() != null, definition, "booleanValue");
                reject(property.getStringValue() != null || property.getNumericValue() != null, definition);
            }
            case NUMBER -> {
                require(property.getNumericValue() != null && Double.isFinite(property.getNumericValue()),
                        definition, "numericValue");
                reject(property.getStringValue() != null || property.getBooleanValue() != null, definition);
                if (definition.getMinimum() != null && property.getNumericValue() < definition.getMinimum()) {
                    throw valueError(definition, "must be at least " + definition.getMinimum());
                }
                if (definition.getMaximum() != null && property.getNumericValue() > definition.getMaximum()) {
                    throw valueError(definition, "must be at most " + definition.getMaximum());
                }
            }
            case ENUM -> {
                require(property.getStringValue() != null && !property.getStringValue().isBlank(),
                        definition, "stringValue");
                reject(property.getNumericValue() != null || property.getBooleanValue() != null, definition);
                String normalized = property.getStringValue().trim().toUpperCase(Locale.ROOT);
                boolean allowed = definition.getAllowedValues().stream()
                        .map(UiStyleAllowedValue::getValue)
                        .filter(Objects::nonNull)
                        .anyMatch(value -> value.equalsIgnoreCase(normalized));
                if (!allowed) {
                    throw valueError(definition, "is not one of the allowed values");
                }
                property.setStringValue(normalized);
            }
            case COLOR -> {
                require(property.getStringValue() != null && !property.getStringValue().isBlank(),
                        definition, "stringValue");
                reject(property.getNumericValue() != null || property.getBooleanValue() != null, definition);
                String value = property.getStringValue().trim();
                String semantic = value.toUpperCase(Locale.ROOT);
                boolean allowedToken = definition.getAllowedValues().stream()
                        .map(UiStyleAllowedValue::getValue)
                        .filter(Objects::nonNull)
                        .anyMatch(token -> token.equalsIgnoreCase(semantic));
                if (!HEX_COLOR.matcher(value).matches() && !allowedToken) {
                    throw valueError(definition,
                            "must be a hexadecimal color or a supported theme color token");
                }
                property.setStringValue(value.startsWith("#") ? value : semantic);
            }
            case TEXT -> {
                require(property.getStringValue() != null, definition, "stringValue");
                reject(property.getNumericValue() != null || property.getBooleanValue() != null, definition);
            }
        }
    }

    private void require(boolean valid, UiStylePropertyDefinition definition, String fieldName) {
        if (!valid) {
            throw valueError(definition, "requires " + fieldName);
        }
    }

    private void reject(boolean invalid, UiStylePropertyDefinition definition) {
        if (invalid) {
            throw valueError(definition, "contains a value in a field that does not match its value type");
        }
    }

    private ResponseStatusException valueError(UiStylePropertyDefinition definition, String message) {
        return new ResponseStatusException(HttpStatus.BAD_REQUEST,
                "UI style property " + definition.getKey() + " " + message);
    }

    public <T extends Baseclass> List<T> listByIds(Class<T> type, Set<String> ids,
                                                    SecurityContext securityContext) {
        return uiStyleRepository.listByIds(type, ids, securityContext);
    }

    public <T extends Baseclass> T getByIdOrNull(String id, Class<T> type,
                                                 SecurityContext securityContext) {
        return uiStyleRepository.getByIdOrNull(id, type, securityContext);
    }

    public <D extends Basic, E extends Baseclass, T extends D> T getByIdOrNull(
            String id, Class<T> type, SingularAttribute<D, E> baseclassAttribute,
            SecurityContext securityContext) {
        return uiStyleRepository.getByIdOrNull(id, type, baseclassAttribute, securityContext);
    }

    public <D extends Basic, E extends Baseclass, T extends D> List<T> listByIds(
            Class<T> type, Set<String> ids, SingularAttribute<D, E> baseclassAttribute,
            SecurityContext securityContext) {
        return uiStyleRepository.listByIds(type, ids, baseclassAttribute, securityContext);
    }

    public <D extends Basic, T extends D> List<T> findByIds(
            Class<T> type, Set<String> ids, SingularAttribute<D, String> idAttribute) {
        return uiStyleRepository.findByIds(type, ids, idAttribute);
    }

    public <T extends Basic> List<T> findByIds(Class<T> type, Set<String> ids) {
        return uiStyleRepository.findByIds(type, ids);
    }

    public <T> T findByIdOrNull(Class<T> type, String id) {
        return uiStyleRepository.findByIdOrNull(type, id);
    }
}
