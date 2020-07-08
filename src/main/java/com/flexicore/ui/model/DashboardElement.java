package com.flexicore.ui.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flexicore.model.Baseclass;
import com.flexicore.security.SecurityContext;

import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

@Entity
public class DashboardElement extends Baseclass {

	public DashboardElement() {
	}

	public DashboardElement(String name, SecurityContext securityContext) {
		super(name, securityContext);
	}

	@Lob
	private String contextString;

	@JsonIgnore
	@OneToMany(targetEntity = DashboardExecution.class, mappedBy = "dashboardElement")
	private List<DashboardExecution> invokers = new ArrayList<>();

	@ManyToOne(targetEntity = Dashboard.class)
	private Dashboard dashboard;

	@ManyToOne(targetEntity = Dashboard.class)
	public Dashboard getDashboard() {
		return dashboard;
	}

	public <T extends DashboardElement> T setDashboard(Dashboard dashboard) {
		this.dashboard = dashboard;
		return (T) this;
	}

	@JsonIgnore
	@OneToMany(targetEntity = DashboardExecution.class, mappedBy = "dashboardElement")
	public List<DashboardExecution> getInvokers() {
		return invokers;
	}

	public <T extends DashboardElement> T setInvokers(
			List<DashboardExecution> invokers) {
		this.invokers = invokers;
		return (T) this;
	}

	@Lob
	public String getContextString() {
		return contextString;
	}

	public <T extends DashboardElement> T setContextString(String contextString) {
		this.contextString = contextString;
		return (T) this;
	}

}
