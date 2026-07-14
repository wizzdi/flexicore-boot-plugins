package com.wizzdi.basic.iot.service.service;

import com.wizzdi.basic.iot.model.Remote;
import com.wizzdi.basic.iot.model.RemoteGroup;
import com.wizzdi.basic.iot.model.RemoteGroupMembershipAction;
import com.wizzdi.basic.iot.model.RemoteGroupToRemote;
import com.wizzdi.basic.iot.model.RemoteRoleDefinition;
import com.wizzdi.basic.iot.service.data.RemoteGroupRepository;
import com.wizzdi.basic.iot.service.events.RemoteGroupMembershipChangedEvent;
import com.wizzdi.basic.iot.service.request.RemoteGroupToRemoteCreate;
import com.wizzdi.basic.iot.service.request.RemoteGroupToRemoteFilter;
import com.wizzdi.basic.iot.service.request.RemoteGroupToRemoteUpdate;
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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.OffsetDateTime;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.Objects;
import java.util.UUID;

@Extension
@Component
public class RemoteGroupToRemoteService implements Plugin {
    @Autowired
    private RemoteGroupRepository repository;
    @Autowired
    private BasicService basicService;
    @Autowired
    private ApplicationEventPublisher eventPublisher;

    public void validate(RemoteGroupToRemoteCreate create, SecurityContext securityContext) {
        basicService.validate(create, securityContext);
        if (create.getWeight() != null && create.getWeight() <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "weight must be greater than zero");
        }
        if (create.getActiveFrom() != null && create.getActiveUntil() != null && !create.getActiveUntil().isAfter(create.getActiveFrom())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "activeUntil must be after activeFrom");
        }
        if (create.getRemoteGroupId() != null && !create.getRemoteGroupId().isBlank()) {
            RemoteGroup group = repository.getByIdOrNull(create.getRemoteGroupId(), RemoteGroup.class, securityContext);
            if (group == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No accessible RemoteGroup with id " + create.getRemoteGroupId());
            }
            create.setRemoteGroup(group);
        }
        if (create.getRemoteId() != null && !create.getRemoteId().isBlank()) {
            Remote remote = repository.getByIdOrNull(create.getRemoteId(), Remote.class, securityContext);
            if (remote == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No accessible Remote with id " + create.getRemoteId());
            }
            create.setRemote(remote);
        }
        if (create.getRoleId() != null && !create.getRoleId().isBlank()) {
            RemoteRoleDefinition role = repository.getByIdOrNull(create.getRoleId(), RemoteRoleDefinition.class, securityContext);
            if (role == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No accessible RemoteRoleDefinition with id " + create.getRoleId());
            }
            create.setRole(role);
        }
    }

    public void validateForCreate(RemoteGroupToRemoteCreate create, SecurityContext securityContext) {
        validate(create, securityContext);
        if (create.getRemoteGroup() == null || create.getRemote() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "remoteGroupId and remoteId are required");
        }
    }

    public void validate(RemoteGroupToRemoteUpdate update, SecurityContext securityContext) {
        validate((RemoteGroupToRemoteCreate) update, securityContext);
        RemoteGroupToRemote membership = repository.getByIdOrNull(update.getId(), RemoteGroupToRemote.class, securityContext);
        if (membership == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No accessible RemoteGroupToRemote with id " + update.getId());
        }
        update.setRemoteGroupToRemote(membership);
    }

    public void validateFiltering(RemoteGroupToRemoteFilter filter, SecurityContext securityContext) {
        basicService.validate(filter, securityContext);
    }

    @Transactional
    public RemoteGroupToRemote create(RemoteGroupToRemoteCreate create, SecurityContext securityContext) {
        RemoteGroupToRemote membership = new RemoteGroupToRemote();
        membership.setId(UUID.randomUUID().toString());
        updateNoMerge(membership, create, true);
        BaseclassService.createSecurityObjectNoMerge(membership, securityContext);
        repository.merge(membership);
        incrementGroupInputVersions(Set.of(membership.getRemoteGroup()));
        publishMembershipChanged(Set.of(), Set.of(), membership);
        return membership;
    }

    @Transactional
    public RemoteGroupToRemote update(RemoteGroupToRemoteUpdate update, SecurityContext securityContext) {
        RemoteGroupToRemote membership = update.getRemoteGroupToRemote();
        RemoteGroup previousGroup = membership.getRemoteGroup();
        Set<String> previousGroupIds = previousGroup == null ? Set.of() : Set.of(previousGroup.getId());
        Set<String> previousRemoteIds = membership.getRemote() == null ? Set.of() : Set.of(membership.getRemote().getId());
        if (updateNoMerge(membership, update, false)) {
            repository.merge(membership);
            Set<RemoteGroup> affectedGroups = new LinkedHashSet<>();
            if (previousGroup != null) {
                affectedGroups.add(previousGroup);
            }
            if (membership.getRemoteGroup() != null) {
                affectedGroups.add(membership.getRemoteGroup());
            }
            incrementGroupInputVersions(affectedGroups);
            publishMembershipChanged(previousGroupIds, previousRemoteIds, membership);
        }
        return membership;
    }

    private boolean updateNoMerge(RemoteGroupToRemote membership, RemoteGroupToRemoteCreate create, boolean creating) {
        boolean changed = basicService.updateBasicNoMerge(create, membership);
        if (create.getRemoteGroupId() != null && !sameEntity(membership.getRemoteGroup(), create.getRemoteGroup())) {
            membership.setRemoteGroup(create.getRemoteGroup());
            changed = true;
        } else if (creating && create.getRemoteGroup() != null) {
            membership.setRemoteGroup(create.getRemoteGroup());
            changed = true;
        }
        if (create.getRemoteId() != null && !sameEntity(membership.getRemote(), create.getRemote())) {
            membership.setRemote(create.getRemote());
            changed = true;
        } else if (creating && create.getRemote() != null) {
            membership.setRemote(create.getRemote());
            changed = true;
        }
        if (create.getRoleId() != null) {
            RemoteRoleDefinition requested = create.getRoleId().isBlank() ? null : create.getRole();
            if (!sameEntity(membership.getRole(), requested)) {
                membership.setRole(requested);
                changed = true;
            }
        }
        if (create.getMembershipAction() != null && create.getMembershipAction() != membership.getMembershipAction()) {
            membership.setMembershipAction(create.getMembershipAction());
            changed = true;
        } else if (creating && membership.getMembershipAction() == null) {
            membership.setMembershipAction(RemoteGroupMembershipAction.INCLUDE);
            changed = true;
        }
        if (create.getRequiredMember() != null && create.getRequiredMember() != membership.isRequiredMember()) {
            membership.setRequiredMember(create.getRequiredMember());
            changed = true;
        }
        if (create.getWeight() != null && !Objects.equals(create.getWeight(), membership.getWeight())) {
            membership.setWeight(create.getWeight());
            changed = true;
        }
        if (create.getActiveFrom() != null && !Objects.equals(create.getActiveFrom(), membership.getActiveFrom())) {
            membership.setActiveFrom(create.getActiveFrom());
            changed = true;
        }
        if (create.getActiveUntil() != null && !Objects.equals(create.getActiveUntil(), membership.getActiveUntil())) {
            membership.setActiveUntil(create.getActiveUntil());
            changed = true;
        }
        return changed;
    }

    private void incrementGroupInputVersions(Set<RemoteGroup> groups) {
        for (RemoteGroup group : groups) {
            if (group == null) {
                continue;
            }
            group.setHealthInputVersion(Math.max(1, group.getHealthInputVersion() + 1));
            repository.merge(group);
        }
    }

    private void publishMembershipChanged(Set<String> previousGroupIds,
                                          Set<String> previousRemoteIds,
                                          RemoteGroupToRemote membership) {
        Set<String> groupIds = new LinkedHashSet<>(previousGroupIds);
        Set<String> remoteIds = new LinkedHashSet<>(previousRemoteIds);
        if (membership.getRemoteGroup() != null) {
            groupIds.add(membership.getRemoteGroup().getId());
        }
        if (membership.getRemote() != null) {
            remoteIds.add(membership.getRemote().getId());
        }
        eventPublisher.publishEvent(new RemoteGroupMembershipChangedEvent(
                Set.copyOf(groupIds),
                Set.copyOf(remoteIds),
                OffsetDateTime.now()));
    }

    private boolean sameEntity(com.flexicore.model.Baseclass left, com.flexicore.model.Baseclass right) {
        return left == null ? right == null : right != null && Objects.equals(left.getId(), right.getId());
    }

    public PaginationResponse<RemoteGroupToRemote> getAll(SecurityContext securityContext, RemoteGroupToRemoteFilter filter) {
        List<RemoteGroupToRemote> list = repository.listMemberships(securityContext, filter);
        return new PaginationResponse<>(list, filter, repository.countMemberships(securityContext, filter));
    }
}
