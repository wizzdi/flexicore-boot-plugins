package com.flexicore.ui.dashboard.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flexicore.ui.dashboard.model.GridLayout;

public class GridLayoutUpdate extends GridLayoutCreate {

	private String id;
	@JsonIgnore
	private GridLayout gridLayout;

	public String getId() {
		return id;
	}

	public GridLayoutUpdate setId(String id) {
		this.id = id;
		return this;
	}

	@JsonIgnore
	public GridLayout getGridLayout() {
		return gridLayout;
	}

	public GridLayoutUpdate setGridLayout(GridLayout gridLayout) {
		this.gridLayout = gridLayout;
		return this;
	}
}
