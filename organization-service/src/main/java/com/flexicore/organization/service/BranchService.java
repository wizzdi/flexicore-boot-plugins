package com.flexicore.organization.service;


import com.flexicore.model.Basic;

import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import com.wizzdi.flexicore.security.response.PaginationResponse;
import com.flexicore.model.Baseclass;
import com.flexicore.organization.data.BranchRepository;
import com.flexicore.organization.model.Branch;
import com.flexicore.organization.model.Organization;
import com.flexicore.organization.request.BranchCreate;
import com.flexicore.organization.request.BranchFiltering;
import com.flexicore.organization.request.BranchUpdate;
import com.wizzdi.flexicore.security.configuration.SecurityContext;


import java.util.*;
import java.util.stream.Collectors;

import com.wizzdi.flexicore.security.service.BaseclassService;
import org.pf4j.Extension;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import jakarta.persistence.metamodel.SingularAttribute;


@Extension
@Component

public class BranchService implements Plugin {


	@Autowired
	private BranchRepository repository;


	@Autowired
	private SiteService siteService;



	public void validateFiltering(BranchFiltering filtering,
			SecurityContext securityContext) {
		siteService.validateFiltering(filtering, securityContext);
		Set<String> organizationIds = filtering.getOrganizationIds();
		Map<String, Organization> organizations = organizationIds.isEmpty() ? new HashMap<>() : listByIds(Organization.class, organizationIds,securityContext).parallelStream().collect(Collectors.toMap(f -> f.getId(), f -> f));
		organizationIds.removeAll(organizations.keySet());
		if (!organizationIds.isEmpty()) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"No Organization with ids " + organizationIds);
		}
		filtering.setOrganizations(new ArrayList<>(organizations.values()));
	}

	public PaginationResponse<Branch> getAllBranches(
			SecurityContext securityContext, BranchFiltering filtering) {
		List<Branch> list = repository.getAllBranches(securityContext,
				filtering);
		long count = repository.countAllBranches(securityContext, filtering);
		return new PaginationResponse<>(list, filtering, count);
	}

	public Branch createBranch(BranchCreate creationContainer,
			SecurityContext securityContext) {
		Branch branch = createBranchNoMerge(creationContainer, securityContext);
		repository.merge(branch);
		return branch;
	}

	private Branch createBranchNoMerge(BranchCreate creationContainer,
			SecurityContext securityContext) {
		Branch branch = new Branch();
		branch.setId(UUID.randomUUID().toString());
		updateBranchNoMerge(branch, creationContainer);
		BaseclassService.createSecurityObjectNoMerge(branch, securityContext);
		return branch;
	}

	private boolean updateBranchNoMerge(Branch branch,
			BranchCreate creationContainer) {
		boolean update = siteService.updateSiteNoMerge(branch,
				creationContainer);

		if (creationContainer.getOrganization() != null && (branch.getOrganization() == null || !creationContainer.getOrganization().getId().equals(branch.getOrganization().getId()))) {
			branch.setOrganization(creationContainer.getOrganization());
			update = true;
		}
		return update;
	}

	public Branch updateBranch(BranchUpdate updateContainer,
			SecurityContext securityContext) {
		Branch branch = updateContainer.getBranch();
		if (updateBranchNoMerge(branch, updateContainer)) {
			repository.merge(branch);
		}
		return branch;
	}

	public void validate(BranchCreate creationContainer,
			SecurityContext securityContext) {
		siteService.validate(creationContainer, securityContext);
		String organizationId = creationContainer.getOrganizationId();
		Organization organization = organizationId == null ? null : getByIdOrNull(organizationId, Organization.class,securityContext);
		if (organization == null && organizationId != null) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"No Organization with id " + organizationId);
		}
		creationContainer.setOrganization(organization);
	}


	public long countAllBranches(SecurityContext securityContext, BranchFiltering filtering) {
		return repository.countAllBranches(securityContext, filtering);
	}


	public <T extends Baseclass> T getByIdOrNull(String id, Class<T> c, SecurityContext securityContext) {
		return repository.getByIdOrNull(id, c, securityContext);
	}

	public <T extends Baseclass> List<T> listByIds(Class<T> c, Set<String> ids, SecurityContext securityContext) {
		return repository.listByIds(c, ids, securityContext);
	}


	public <D extends Basic, E extends Baseclass, T extends D> List<T> listByIds(Class<T> c, Set<String> ids, SingularAttribute<D, E> baseclassAttribute, SecurityContext securityContext) {
		return repository.listByIds(c, ids, baseclassAttribute, securityContext);
	}

	public <D extends Basic, T extends D> List<T> findByIds(Class<T> c, Set<String> ids, SingularAttribute<D, String> idAttribute) {
		return repository.findByIds(c, ids, idAttribute);
	}

	public <T extends Basic> List<T> findByIds(Class<T> c, Set<String> requested) {
		return repository.findByIds(c, requested);
	}

	public <T> T findByIdOrNull(Class<T> type, String id) {
		return repository.findByIdOrNull(type, id);
	}

	@Transactional
	public void merge(Object base) {
		repository.merge(base);
	}

	@Transactional
	public void massMerge(List<?> toMerge) {
		repository.massMerge(toMerge);
	}
}
