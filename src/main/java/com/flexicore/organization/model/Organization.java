package com.flexicore.organization.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flexicore.model.Baseclass;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Organization extends Baseclass {
	static Organization s_Singleton = new Organization();

	public static Organization s() {
		return s_Singleton;
	}

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
}
