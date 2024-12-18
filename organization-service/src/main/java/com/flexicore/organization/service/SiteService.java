package com.flexicore.organization.service;


import com.flexicore.model.Baseclass;
import com.flexicore.model.Basic;
import com.flexicore.model.territories.Address;
import com.flexicore.model.territories.Address_;
import com.flexicore.organization.data.SiteRepository;
import com.flexicore.organization.model.Site;
import com.flexicore.organization.request.SiteCreate;
import com.flexicore.organization.request.SiteFiltering;
import com.flexicore.organization.request.SiteUpdate;
import com.wizzdi.flexicore.security.configuration.SecurityContext;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import com.wizzdi.flexicore.security.response.PaginationResponse;
import com.wizzdi.flexicore.security.service.BaseclassService;
import com.wizzdi.flexicore.security.service.BasicService;
import org.pf4j.Extension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import jakarta.persistence.metamodel.SingularAttribute;
import java.util.*;
import java.util.stream.Collectors;


@Extension
@Component

public class SiteService implements Plugin {


	@Autowired
	private SiteRepository repository;
	@Autowired
	private BasicService basicService;




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


	public void validateFiltering(SiteFiltering filtering,
								  SecurityContext securityContext) {
		basicService.validate(filtering, securityContext);
		Set<String> addressIds = filtering.getAddressIds();
		Map<String, Address> address = addressIds.isEmpty() ? new HashMap<>() : listByIds(Address.class, addressIds,securityContext).parallelStream().collect(Collectors.toMap(f -> f.getId(), f -> f));
		addressIds.removeAll(address.keySet());
		if (!addressIds.isEmpty()) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No Address with ids " + addressIds);
		}
		filtering.setAddresses(new ArrayList<>(address.values()));
	}


	public PaginationResponse<Site> getAllSites(
			SecurityContext securityContext, SiteFiltering filtering) {
		List<Site> list = listAllSites(securityContext, filtering);
		long count = repository.countAllSites(securityContext, filtering);
		return new PaginationResponse<>(list, filtering, count);
	}


	public List<Site> listAllSites(SecurityContext securityContext,
								   SiteFiltering filtering) {
		return repository.getAllSites(securityContext, filtering);
	}

	public Site createSite(SiteCreate creationContainer,
						   SecurityContext securityContext) {
		Site site = createSiteNoMerge(creationContainer, securityContext);
		repository.merge(site);
		return site;
	}


	public Site createSiteNoMerge(SiteCreate creationContainer,
								  SecurityContext securityContext) {
		Site site = new Site();
		site.setId(UUID.randomUUID().toString());
		updateSiteNoMerge(site, creationContainer);
		BaseclassService.createSecurityObjectNoMerge(site, securityContext);

		return site;
	}


	public boolean updateSiteNoMerge(Site site, SiteCreate creationContainer) {
		boolean update = basicService.updateBasicNoMerge(creationContainer, site);

		if (creationContainer.getAddress() != null && (site.getAddress() == null || !creationContainer.getAddress().getId().equals(site.getAddress().getId()))) {
			site.setAddress(creationContainer.getAddress());
			update = true;
		}

		if (creationContainer.getExternalId() != null && !creationContainer.getExternalId().equals(site.getExternalId())) {
			site.setExternalId(creationContainer.getExternalId());
			update = true;
		}
		return update;
	}

	public Site updateSite(SiteUpdate updateContainer, SecurityContext securityContext) {
		Site site = updateContainer.getSite();
		if (updateSiteNoMerge(site, updateContainer)) {
			repository.merge(site);
		}
		return site;
	}


	public void validate(SiteCreate creationContainer, SecurityContext securityContext) {
		basicService.validate(creationContainer,securityContext);
		String addressId = creationContainer.getAddressId();
		Address address = addressId == null ? null : getByIdOrNull(addressId,
				Address.class,  securityContext);
		if (address == null && addressId != null) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No Address with id " + addressId);
		}
		creationContainer.setAddress(address);
	}


}
