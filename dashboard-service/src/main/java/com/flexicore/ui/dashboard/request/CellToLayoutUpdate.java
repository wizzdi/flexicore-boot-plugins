package com.flexicore.ui.dashboard.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flexicore.ui.dashboard.model.CellToLayout;

public class CellToLayoutUpdate extends CellToLayoutCreate {

	private String id;
	@JsonIgnore
	private CellToLayout cellToLayout;

	public String getId() {
		return id;
	}

	public CellToLayoutUpdate setId(String id) {
		this.id = id;
		return this;
	}

	@JsonIgnore
	public CellToLayout getCellToLayout() {
		return cellToLayout;
	}

	public CellToLayoutUpdate setCellToLayout(CellToLayout cellToLayout) {
		this.cellToLayout = cellToLayout;
		return this;
	}
}
