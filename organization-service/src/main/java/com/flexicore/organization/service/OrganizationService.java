package com.flexicore.organization.service;


import com.flexicore.model.Baseclass;
import com.flexicore.model.Basic;
import com.flexicore.model.SecuredBasic_;
import com.flexicore.model.territories.Address;
import com.flexicore.organization.data.OrganizationRepository;
import com.flexicore.organization.model.Organization;
import com.flexicore.organization.request.OrganizationFiltering;
import com.flexicore.organization.request.OrganizationUpdate;
import com.flexicore.organization.request.OrganizationCreate;
import com.flexicore.security.SecurityContextBase;
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
import java.util.List;
import java.util.Set;


@Extension
@Component

public class OrganizationService implements Plugin {


	@Autowired
	private OrganizationRepository repository;

	@Autowired
	private BasicService basicService;




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

	public Organization createOrganization(
			OrganizationCreate creationContainer,
			SecurityContextBase securityContext) {

		Organization employee = createOrganizationNoMerge(creationContainer,
				securityContext);
		repository.merge(employee);
		return employee;
	}

	
	public Organization createOrganizationNoMerge(
			OrganizationCreate creationContainer,
			SecurityContextBase securityContext) {
		Organization organization = new Organization();
		organization.setId(Baseclass.getBase64ID());

		updateOrganizationNoMerge(organization, creationContainer);
		BaseclassService.createSecurityObjectNoMerge(organization, securityContext);
		return organization;

	}

	
	public boolean updateOrganizationNoMerge(Organization organization,
			OrganizationCreate organizationCreate) {
		boolean update = basicService.updateBasicNoMerge(organizationCreate,organization);
		if(organizationCreate.getMainAddress()!=null&& (organization.getMainAddress()==null||!organizationCreate.getMainAddress().getId().equals(organization.getMainAddress().getId()))){
			organization.setMainAddress(organizationCreate.getMainAddress());
			update=true;
		}
		return update;
	}

	public void validate(OrganizationCreate organizationCreate,SecurityContextBase securityContext){
		basicService.validate(organizationCreate,securityContext);
		String addressId=organizationCreate.getMainAddressId();
		Address address=addressId!=null?getByIdOrNull(addressId,Address.class, SecuredBasic_.security,securityContext):null;
		if(addressId!=null&&address==null){
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"no address with id "+addressId);
		}
		organizationCreate.setMainAddress(address);
	}

	
	public void validateFiltering(OrganizationFiltering filtering,
			SecurityContextBase securityContext) {
		basicService.validate(filtering,securityContext);
	}

	public PaginationResponse<Organization> getAllOrganizations(SecurityContextBase securityContext, OrganizationFiltering filtering) {
		List<Organization> list = repository.getAllOrganizations(securityContext, filtering);
		long count = repository.countAllOrganizations(securityContext, filtering);
		return new PaginationResponse<>(list, filtering, count);
	}

	public Organization updateOrganization(OrganizationUpdate updateContainer,
								   SecurityContextBase securityContext) {
		Organization organization = updateContainer.getOrganization();
		if (updateOrganizationNoMerge(organization, updateContainer)) {
			repository.merge(organization);
		}
		return organization;
	}



}