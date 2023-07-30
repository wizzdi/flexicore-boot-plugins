package com.flexicore.ui.dashboard.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flexicore.ui.dashboard.model.CellContent;

public class CellContentUpdate extends CellContentCreate {

	private String id;
	@JsonIgnore
	private CellContent cellContent;

	public String getId() {
		return id;
	}

	public CellContentUpdate setId(String id) {
		this.id = id;
		return this;
	}

	@JsonIgnore
	public CellContent getCellContent() {
		return cellContent;
	}

	public CellContentUpdate setCellContent(CellContent cellContent) {
		this.cellContent = cellContent;
		return this;
	}
}
