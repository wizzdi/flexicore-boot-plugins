package com.flexicore.organization.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flexicore.model.PermissionGroup;
import com.flexicore.model.SecuredBasic;
import com.flexicore.model.territories.Address;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
@Table(name = "organization", indexes = {@Index(name = "externalid_ix", columnList = "externalid", unique = false)})
@Entity
public class Organization extends SecuredBasic {

	private String externalId;
	@ManyToOne(targetEntity = Address.class)
	private Address mainAddress;

	@OneToMany(targetEntity = Branch.class, mappedBy = "organization")
	@JsonIgnore
	private List<Branch> branches = new ArrayList<>();

	@OneToMany(targetEntity = Employee.class, mappedBy = "organization")
	@JsonIgnore
	private List<Employee> employees = new ArrayList<>();

	@OneToMany(targetEntity = Branch.class, mappedBy = "organization")
	@JsonIgnore
	public List<Branch> getBranches() {
		return branches;
	}

	public Organization setBranches(List<Branch> branches) {
		this.branches = branches;
		return this;
	}
	@OneToOne(targetEntity = PermissionGroup.class)
	private PermissionGroup permissionGroup;
	@OneToMany(targetEntity = Employee.class, mappedBy = "organization")
	@JsonIgnore
	public List<Employee> getEmployees() {
		return employees;
	}

	public Organization setEmployees(List<Employee> employees) {
		this.employees = employees;
		return this;
	}

	@ManyToOne(targetEntity = Address.class)
	public Address getMainAddress() {
		return mainAddress;
	}

	public <T extends Organization> T setMainAddress(Address mainAddress) {
		this.mainAddress = mainAddress;
		return (T) this;
	}

	public String getExternalId() {
		return externalId;
	}

	public Organization setExternalId(String externalId) {
		this.externalId = externalId;
		return this;
	}

	public PermissionGroup getPermissionGroup() {
		return permissionGroup;
	}

	public Organization setPermissionGroup(PermissionGroup permissionGroup) {
		this.permissionGroup = permissionGroup;
		return this;
	}
}
