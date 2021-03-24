package com.flexicore.organization.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flexicore.model.Baseclass;
import com.flexicore.model.SecuredBasic;
import com.flexicore.model.territories.Address;
import com.flexicore.security.SecurityContextBase;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import java.lang.annotation.Target;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Organization extends SecuredBasic {


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
}
