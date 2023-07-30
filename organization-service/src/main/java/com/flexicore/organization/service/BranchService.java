package com.flexicore.organization.service;


import com.flexicore.model.Basic;
import com.flexicore.model.SecuredBasic_;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import com.wizzdi.flexicore.security.response.PaginationResponse;
import com.flexicore.model.Baseclass;
import com.flexicore.organization.data.BranchRepository;
import com.flexicore.organization.model.Branch;
import com.flexicore.organization.model.Organization;
import com.flexicore.organization.request.BranchCreate;
import com.flexicore.organization.request.BranchFiltering;
import com.flexicore.organization.request.BranchUpdate;
import com.flexicore.security.SecurityContextBase;


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
			SecurityContextBase securityContext) {
		siteService.validateFiltering(filtering, securityContext);
		Set<String> organizationIds = filtering.getOrganizationIds();
		Map<String, Organization> organizations = organizationIds.isEmpty() ? new HashMap<>() : listByIds(Organization.class, organizationIds, SecuredBasic_.security, securityContext).parallelStream().collect(Collectors.toMap(f -> f.getId(), f -> f));
		organizationIds.removeAll(organizations.keySet());
		if (!organizationIds.isEmpty()) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"No Organization with ids " + organizationIds);
		}
		filtering.setOrganizations(new ArrayList<>(organizations.values()));
	}

	public PaginationResponse<Branch> getAllBranches(
			SecurityContextBase securityContext, BranchFiltering filtering) {
		List<Branch> list = repository.getAllBranches(securityContext,
				filtering);
		long count = repository.countAllBranches(securityContext, filtering);
		return new PaginationResponse<>(list, filtering, count);
	}

	public Branch createBranch(BranchCreate creationContainer,
			SecurityContextBase securityContext) {
		Branch branch = createBranchNoMerge(creationContainer, securityContext);
		repository.merge(branch);
		return branch;
	}

	private Branch createBranchNoMerge(BranchCreate creationContainer,
			SecurityContextBase securityContext) {
		Branch branch = new Branch();
		branch.setId(Baseclass.getBase64ID());
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
			SecurityContextBase securityContext) {
		Branch branch = updateContainer.getBranch();
		if (updateBranchNoMerge(branch, updateContainer)) {
			repository.merge(branch);
		}
		return branch;
	}

	public void validate(BranchCreate creationContainer,
			SecurityContextBase securityContext) {
		siteService.validate(creationContainer, securityContext);
		String organizationId = creationContainer.getOrganizationId();
		Organization organization = organizationId == null ? null : getByIdOrNull(organizationId, Organization.class, SecuredBasic_.security, securityContext);
		if (organization == null && organizationId != null) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"No Organization with id " + organizationId);
		}
		creationContainer.setOrganization(organization);
	}


	public long countAllBranches(SecurityContextBase securityContext, BranchFiltering filtering) {
		return repository.countAllBranches(securityContext, filtering);
	}

	public <T extends Baseclass> List<T> listByIds(Class<T> c, Set<String> ids, SecurityContextBase securityContext) {
		return repository.listByIds(c, ids, securityContext);
	}

	public <T extends Baseclass> T getByIdOrNull(String id, Class<T> c, SecurityContextBase securityContext) {
		return repository.getByIdOrNull(id, c, securityContext);
	}

	public <D extends Basic, E extends Baseclass, T extends D> T getByIdOrNull(String id, Class<T> c, SingularAttribute<D, E> baseclassAttribute, SecurityContextBase securityContext) {
		return repository.getByIdOrNull(id, c, baseclassAttribute, securityContext);
	}

	public <D extends Basic, E extends Baseclass, T extends D> List<T> listByIds(Class<T> c, Set<String> ids, SingularAttribute<D, E> baseclassAttribute, SecurityContextBase securityContext) {
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