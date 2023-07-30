package com.flexicore.organization.service;


import com.flexicore.model.Baseclass;
import com.flexicore.model.Basic;
import com.flexicore.model.SecuredBasic_;
import com.flexicore.organization.data.EmployeeRepository;
import com.flexicore.organization.model.Employee;
import com.flexicore.organization.model.Organization;
import com.flexicore.organization.request.EmployeeCreate;
import com.flexicore.organization.request.EmployeeFiltering;
import com.flexicore.organization.request.EmployeeUpdate;
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
import java.util.*;
import java.util.stream.Collectors;


@Extension
@Component

public class EmployeeService implements Plugin {


	@Autowired
	private EmployeeRepository repository;

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

	
	public PaginationResponse<Employee> listAllEmployees(
			SecurityContextBase securityContext, EmployeeFiltering filtering) {

		List<Employee> endpoints = repository.listAllEmployees(securityContext,
				filtering);
		long count = repository.countAllEmployees(securityContext, filtering);
		return new PaginationResponse<>(endpoints, filtering, count);
	}

	public Employee createEmployee(EmployeeCreate creationContainer,
			SecurityContextBase securityContext) {

		Employee employee = createEmployeeNoMerge(creationContainer,
				securityContext);

		repository.merge(employee);
		return employee;
	}

	
	public Employee createEmployeeNoMerge(EmployeeCreate creationContainer,
			SecurityContextBase securityContext) {
		Employee employee = new Employee();
		employee.setId(Baseclass.getBase64ID());

		updateEmployeeNoMerge(employee, creationContainer);
		BaseclassService.createSecurityObjectNoMerge(employee, securityContext);
		return employee;

	}

	public Employee updateEmployee(EmployeeUpdate creationContainer,
			SecurityContextBase securityContext) {
		Employee employee = creationContainer.getEmployee();
		if (updateEmployeeNoMerge(employee, creationContainer)) {
			repository.merge(employee);
		}
		return employee;
	}

	
	public boolean updateEmployeeNoMerge(Employee employee,
			EmployeeCreate employeeCreate) {
		boolean update = basicService.updateBasicNoMerge(employeeCreate, employee);
		if(employeeCreate.getOrganization()!=null&&(employee.getOrganization()==null||!employeeCreate.getOrganization().getId().equals(employee.getOrganization().getId()))){
			employee.setOrganization(employeeCreate.getOrganization());
			update=true;
		}
		if(employeeCreate.getExternalId()!=null&&(employee.getExternalId()==null||!employeeCreate.getExternalId().equals(employee.getExternalId()))){
			employee.setExternalId(employeeCreate.getExternalId());
			update=true;
		}
		return update;
	}

	public void validateFiltering(EmployeeFiltering filtering,
			SecurityContextBase securityContext) {
		basicService.validate(filtering,securityContext);
		Set<String> organizationIds=filtering.getOrganizationsIds();
		Map<String, Organization> map=organizationIds.isEmpty()?new HashMap<>():listByIds(Organization.class,organizationIds, SecuredBasic_.security,securityContext).stream().collect(Collectors.toMap(f->f.getId(),f->f));
		organizationIds.remove(map.keySet());
		if(!organizationIds.isEmpty()){
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"no organizations with ids "+organizationIds);
		}
		filtering.setOrganizations(new ArrayList<>(map.values()));


	}

	public void validate(EmployeeCreate creationContainer, SecurityContextBase securityContext) {
		String organizationId = creationContainer.getOrganizationId();
		Organization organization = organizationId == null ? null : getByIdOrNull(organizationId, Organization.class, SecuredBasic_.security, securityContext);
		if (organization == null && organizationId != null) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"No Organization with id " + organizationId);
		}
		creationContainer.setOrganization(organization);

	}
}
