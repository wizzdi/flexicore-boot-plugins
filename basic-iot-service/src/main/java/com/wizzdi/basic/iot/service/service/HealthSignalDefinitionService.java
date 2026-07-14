package com.wizzdi.basic.iot.service.service;

import com.wizzdi.basic.iot.model.HealthSignalDefinition;
import com.wizzdi.basic.iot.service.events.HealthSignalDefinitionChangedEvent;
import com.wizzdi.basic.iot.service.data.HealthSignalDefinitionRepository;
import com.wizzdi.basic.iot.service.request.HealthSignalDefinitionCreate;
import com.wizzdi.basic.iot.service.request.HealthSignalDefinitionFilter;
import com.wizzdi.basic.iot.service.request.HealthSignalDefinitionUpdate;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import com.wizzdi.flexicore.security.configuration.SecurityContext;
import com.wizzdi.flexicore.security.response.PaginationResponse;
import com.wizzdi.flexicore.security.service.BaseclassService;
import com.wizzdi.flexicore.security.service.BasicService;
import org.pf4j.Extension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Extension
@Component
public class HealthSignalDefinitionService implements Plugin {
    @Autowired
    private HealthSignalDefinitionRepository repository;
    @Autowired
    private BasicService basicService;
    @Autowired
    private ApplicationEventPublisher eventPublisher;

    public void validateFiltering(HealthSignalDefinitionFilter filter, SecurityContext securityContext) {
        basicService.validate(filter, securityContext);
    }

    public void validate(HealthSignalDefinitionCreate create, SecurityContext securityContext) {
        basicService.validate(create, securityContext);
        boolean updating = create instanceof HealthSignalDefinitionUpdate;
        if (updating) {
            HealthSignalDefinitionUpdate update = (HealthSignalDefinitionUpdate) create;
            HealthSignalDefinition existing = repository.getByIdOrNull(update.getId(), HealthSignalDefinition.class, securityContext);
            if (existing == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No accessible HealthSignalDefinition with id " + update.getId());
            }
            update.setHealthSignalDefinition(existing);
        }
        if (!updating && (create.getExternalId() == null || create.getExternalId().isBlank())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "externalId is required");
        }
        if (!updating && create.getValueType() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "valueType is required");
        }
    }

    public PaginationResponse<HealthSignalDefinition> getAll(SecurityContext securityContext, HealthSignalDefinitionFilter filter) {
        List<HealthSignalDefinition> list = repository.list(securityContext, filter);
        return new PaginationResponse<>(list, filter, repository.count(securityContext, filter));
    }

    public HealthSignalDefinition create(HealthSignalDefinitionCreate create, SecurityContext securityContext) {
        HealthSignalDefinition entity = new HealthSignalDefinition();
        entity.setId(UUID.randomUUID().toString());
        updateNoMerge(entity, create);
        BaseclassService.createSecurityObjectNoMerge(entity, securityContext);
        repository.merge(entity);
        eventPublisher.publishEvent(new HealthSignalDefinitionChangedEvent(entity.getId(), OffsetDateTime.now()));
        return entity;
    }

    public HealthSignalDefinition update(HealthSignalDefinitionUpdate update, SecurityContext securityContext) {
        HealthSignalDefinition entity = update.getHealthSignalDefinition();
        boolean changed = updateNoMerge(entity, update);
        if (changed) {
            repository.merge(entity);
            eventPublisher.publishEvent(new HealthSignalDefinitionChangedEvent(entity.getId(), OffsetDateTime.now()));
        }
        return entity;
    }

    private boolean updateNoMerge(HealthSignalDefinition entity, HealthSignalDefinitionCreate create) {
        boolean changed = basicService.updateBasicNoMerge(create, entity);
        if (create.getExternalId() != null && !Objects.equals(entity.getExternalId(), create.getExternalId())) {
            entity.setExternalId(create.getExternalId());
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
        if (create.getBuiltIn() != null && entity.isBuiltIn() != create.getBuiltIn()) {
            entity.setBuiltIn(create.getBuiltIn());
            changed = true;
        }
        if (create.getAggregatable() != null && entity.isAggregatable() != create.getAggregatable()) {
            entity.setAggregatable(create.getAggregatable());
            changed = true;
        }
        return changed;
    }
}
