package com.flexicore.ui.dashboard.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flexicore.ui.dashboard.model.GridLayoutCell;

public class GridLayoutCellUpdate extends GridLayoutCellCreate {

	private String id;
	@JsonIgnore
	private GridLayoutCell gridLayoutCell;

	public String getId() {
		return id;
	}

	public GridLayoutCellUpdate setId(String id) {
		this.id = id;
		return this;
	}

	@JsonIgnore
	public GridLayoutCell getGridLayoutCell() {
		return gridLayoutCell;
	}

	public GridLayoutCellUpdate setGridLayoutCell(GridLayoutCell gridLayoutCell) {
		this.gridLayoutCell = gridLayoutCell;
		return this;
	}
}
