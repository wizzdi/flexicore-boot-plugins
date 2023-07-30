package com.flexicore.organization.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flexicore.organization.model.Employee;

public class EmployeeUpdate extends EmployeeCreate {

	private String id;
	@JsonIgnore
	private Employee employee;

	public String getId() {
		return id;
	}

	public EmployeeUpdate setId(String id) {
		this.id = id;
		return this;
	}

	@JsonIgnore
	public Employee getEmployee() {
		return employee;
	}

	public EmployeeUpdate setEmployee(Employee employee) {
		this.employee = employee;
		return this;
	}
}
