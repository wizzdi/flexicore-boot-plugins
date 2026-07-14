package com.wizzdi.basic.iot.service.service;

import com.wizzdi.basic.iot.model.StatePropertyDefinition;
import com.wizzdi.basic.iot.model.StateSchema;
import com.wizzdi.basic.iot.service.data.StatePropertyDefinitionRepository;
import com.wizzdi.basic.iot.service.request.StatePropertyDefinitionCreate;
import com.wizzdi.basic.iot.service.request.StatePropertyDefinitionFilter;
import com.wizzdi.basic.iot.service.request.StatePropertyDefinitionUpdate;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import com.wizzdi.flexicore.security.configuration.SecurityContext;
import com.wizzdi.flexicore.security.response.PaginationResponse;
import com.wizzdi.flexicore.security.service.BaseclassService;
import com.wizzdi.flexicore.security.service.BasicService;
import org.pf4j.Extension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Extension
@Component
public class StatePropertyDefinitionService implements Plugin {
    @Autowired
    private StatePropertyDefinitionRepository repository;
    @Autowired
    private BasicService basicService;

    public void validateFiltering(StatePropertyDefinitionFilter filter, SecurityContext securityContext) {
        basicService.validate(filter, securityContext);
    }

    public void validate(StatePropertyDefinitionCreate create, SecurityContext securityContext) {
        basicService.validate(create, securityContext);
        boolean updating = create instanceof StatePropertyDefinitionUpdate;
        if (updating) {
            StatePropertyDefinitionUpdate update = (StatePropertyDefinitionUpdate) create;
            StatePropertyDefinition existing = repository.getByIdOrNull(update.getId(), StatePropertyDefinition.class, securityContext);
            if (existing == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No accessible StatePropertyDefinition with id " + update.getId());
            }
            update.setStatePropertyDefinition(existing);
        }
        if (create.getStateSchemaId() != null) {
            StateSchema schema = repository.getByIdOrNull(create.getStateSchemaId(), StateSchema.class, securityContext);
            if (schema == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No accessible StateSchema with id " + create.getStateSchemaId());
            }
            create.setStateSchema(schema);
        } else if (!updating) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "stateSchemaId is required");
        }
        if (!updating && (create.getPropertyPath() == null || create.getPropertyPath().isBlank())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "propertyPath is required");
        }
        if (!updating && create.getValueType() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "valueType is required");
        }
    }

    public PaginationResponse<StatePropertyDefinition> getAll(SecurityContext securityContext, StatePropertyDefinitionFilter filter) {
        List<StatePropertyDefinition> list = repository.list(securityContext, filter);
        return new PaginationResponse<>(list, filter, repository.count(securityContext, filter));
    }

    public StatePropertyDefinition create(StatePropertyDefinitionCreate create, SecurityContext securityContext) {
        StatePropertyDefinition entity = new StatePropertyDefinition();
        entity.setId(UUID.randomUUID().toString());
        updateNoMerge(entity, create);
        BaseclassService.createSecurityObjectNoMerge(entity, securityContext);
        repository.merge(entity);
        return entity;
    }

    public StatePropertyDefinition update(StatePropertyDefinitionUpdate update, SecurityContext securityContext) {
        StatePropertyDefinition entity = update.getStatePropertyDefinition();
        if (updateNoMerge(entity, update)) {
            repository.merge(entity);
        }
        return entity;
    }

    private boolean updateNoMerge(StatePropertyDefinition entity, StatePropertyDefinitionCreate create) {
        boolean changed = basicService.updateBasicNoMerge(create, entity);
        if (create.getStateSchema() != null && (entity.getStateSchema() == null || !Objects.equals(entity.getStateSchema().getId(), create.getStateSchema().getId()))) {
            entity.setStateSchema(create.getStateSchema());
            changed = true;
        }
        if (create.getExternalId() != null && !Objects.equals(entity.getExternalId(), create.getExternalId())) {
            entity.setExternalId(create.getExternalId());
            changed = true;
        }
        if (create.getPropertyPath() != null && !Objects.equals(entity.getPropertyPath(), create.getPropertyPath())) {
            entity.setPropertyPath(create.getPropertyPath());
            changed = true;
        }
        if (create.getValueType() != null && entity.getValueType() != create.getValueType()) {
            entity.setValueType(create.getValueType());
            changed = true;
        }
        if (create.getUnit() != null && !Objects.equals(entity.getUnit(), create.getUnit())) {
            entity.setUnit(create.getUnit());
            changed = true;
        }
        return changed;
    }
}
