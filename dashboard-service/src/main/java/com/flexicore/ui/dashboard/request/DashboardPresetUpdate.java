package com.flexicore.ui.dashboard.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flexicore.ui.dashboard.model.DashboardPreset;

public class DashboardPresetUpdate extends DashboardPresetCreate {

	private String id;
	@JsonIgnore
	private DashboardPreset dashboardPreset;

	public String getId() {
		return id;
	}

	public DashboardPresetUpdate setId(String id) {
		this.id = id;
		return this;
	}

	@JsonIgnore
	public DashboardPreset getDashboardPreset() {
		return dashboardPreset;
	}

	public DashboardPresetUpdate setDashboardPreset(DashboardPreset dashboardPreset) {
		this.dashboardPreset = dashboardPreset;
		return this;
	}
}
