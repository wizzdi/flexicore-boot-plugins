package com.wizzdi.basic.iot.service.service;

import com.wizzdi.basic.iot.model.RemoteRoleDefinition;
import com.wizzdi.basic.iot.service.data.RemoteRoleDefinitionRepository;
import com.wizzdi.basic.iot.service.request.RemoteRoleDefinitionCreate;
import com.wizzdi.basic.iot.service.request.RemoteRoleDefinitionFilter;
import com.wizzdi.basic.iot.service.request.RemoteRoleDefinitionUpdate;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import com.wizzdi.flexicore.security.configuration.SecurityContext;
import com.wizzdi.flexicore.security.response.PaginationResponse;
import com.wizzdi.flexicore.security.service.BaseclassService;
import com.wizzdi.flexicore.security.service.BasicService;
import org.pf4j.Extension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Extension
@Component
public class RemoteRoleDefinitionService implements Plugin {
    @Autowired
    private RemoteRoleDefinitionRepository repository;
    @Autowired
    private BasicService basicService;

    public void validate(RemoteRoleDefinitionCreate create, SecurityContext securityContext) {
        basicService.validate(create, securityContext);
    }

    public void validate(RemoteRoleDefinitionUpdate update, SecurityContext securityContext) {
        validate((RemoteRoleDefinitionCreate) update, securityContext);
        RemoteRoleDefinition role = repository.getByIdOrNull(update.getId(), RemoteRoleDefinition.class, securityContext);
        if (role == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No accessible RemoteRoleDefinition with id " + update.getId());
        }
        update.setRemoteRoleDefinition(role);
    }

    public void validateFiltering(RemoteRoleDefinitionFilter filter, SecurityContext securityContext) {
        basicService.validate(filter, securityContext);
    }

    @Transactional
    public RemoteRoleDefinition create(RemoteRoleDefinitionCreate create, SecurityContext securityContext) {
        RemoteRoleDefinition role = new RemoteRoleDefinition();
        role.setId(UUID.randomUUID().toString());
        updateNoMerge(role, create);
        BaseclassService.createSecurityObjectNoMerge(role, securityContext);
        repository.merge(role);
        return role;
    }

    @Transactional
    public RemoteRoleDefinition update(RemoteRoleDefinitionUpdate update, SecurityContext securityContext) {
        RemoteRoleDefinition role = update.getRemoteRoleDefinition();
        if (updateNoMerge(role, update)) {
            repository.merge(role);
        }
        return role;
    }

    private boolean updateNoMerge(RemoteRoleDefinition role, RemoteRoleDefinitionCreate create) {
        boolean changed = basicService.updateBasicNoMerge(create, role);
        if (create.getExternalId() != null && !Objects.equals(create.getExternalId(), role.getExternalId())) {
            role.setExternalId(create.getExternalId());
            changed = true;
        }
        return changed;
    }

    public PaginationResponse<RemoteRoleDefinition> getAll(SecurityContext securityContext, RemoteRoleDefinitionFilter filter) {
        List<RemoteRoleDefinition> list = repository.list(securityContext, filter);
        return new PaginationResponse<>(list, filter, repository.count(securityContext, filter));
    }
}
