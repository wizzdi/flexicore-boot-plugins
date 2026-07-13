package com.wizzdi.basic.iot.service.service;

import com.wizzdi.basic.iot.model.FleetHealthPolicy;
import com.wizzdi.basic.iot.model.RemoteGroup;
import com.wizzdi.basic.iot.service.data.RemoteGroupRepository;
import com.wizzdi.basic.iot.service.request.RemoteGroupCreate;
import com.wizzdi.basic.iot.service.request.RemoteGroupFilter;
import com.wizzdi.basic.iot.service.request.RemoteGroupUpdate;
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
public class RemoteGroupService implements Plugin {
    @Autowired
    private RemoteGroupRepository repository;
    @Autowired
    private BasicService basicService;

    public void validate(RemoteGroupCreate create, SecurityContext securityContext) {
        basicService.validate(create, securityContext);
        String policyId = create.getFleetHealthPolicyId();
        if (policyId != null && !policyId.isBlank()) {
            FleetHealthPolicy policy = repository.getByIdOrNull(policyId, FleetHealthPolicy.class, securityContext);
            if (policy == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No accessible FleetHealthPolicy with id " + policyId);
            }
            create.setFleetHealthPolicy(policy);
        }
    }

    public void validate(RemoteGroupUpdate update, SecurityContext securityContext) {
        validate((RemoteGroupCreate) update, securityContext);
        RemoteGroup group = repository.getByIdOrNull(update.getId(), RemoteGroup.class, securityContext);
        if (group == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No accessible RemoteGroup with id " + update.getId());
        }
        update.setRemoteGroup(group);
    }

    public void validateFiltering(RemoteGroupFilter filter, SecurityContext securityContext) {
        basicService.validate(filter, securityContext);
    }

    @Transactional
    public RemoteGroup create(RemoteGroupCreate create, SecurityContext securityContext) {
        RemoteGroup group = new RemoteGroup();
        group.setId(UUID.randomUUID().toString());
        updateNoMerge(group, create, true);
        BaseclassService.createSecurityObjectNoMerge(group, securityContext);
        repository.merge(group);
        return group;
    }

    @Transactional
    public RemoteGroup update(RemoteGroupUpdate update, SecurityContext securityContext) {
        RemoteGroup group = update.getRemoteGroup();
        if (updateNoMerge(group, update, false)) {
            repository.merge(group);
        }
        return group;
    }

    private boolean updateNoMerge(RemoteGroup group, RemoteGroupCreate create, boolean creating) {
        boolean changed = basicService.updateBasicNoMerge(create, group);
        if (create.getExternalId() != null && !Objects.equals(create.getExternalId(), group.getExternalId())) {
            group.setExternalId(create.getExternalId());
            changed = true;
        }
        if (create.getHealthEnabled() != null && create.getHealthEnabled() != group.isHealthEnabled()) {
            group.setHealthEnabled(create.getHealthEnabled());
            changed = true;
        }
        if (create.getFleetHealthPolicyId() != null) {
            FleetHealthPolicy requested = create.getFleetHealthPolicyId().isBlank() ? null : create.getFleetHealthPolicy();
            if (!sameEntity(group.getFleetHealthPolicy(), requested)) {
                group.setFleetHealthPolicy(requested);
                changed = true;
            }
        } else if (creating && create.getFleetHealthPolicy() != null) {
            group.setFleetHealthPolicy(create.getFleetHealthPolicy());
            changed = true;
        }
        return changed;
    }

    private boolean sameEntity(FleetHealthPolicy left, FleetHealthPolicy right) {
        return left == null ? right == null : right != null && Objects.equals(left.getId(), right.getId());
    }

    public PaginationResponse<RemoteGroup> getAll(SecurityContext securityContext, RemoteGroupFilter filter) {
        List<RemoteGroup> list = repository.listGroups(securityContext, filter);
        return new PaginationResponse<>(list, filter, repository.countGroups(securityContext, filter));
    }
}
