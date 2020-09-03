package com.flexicore.ui.dashboard.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flexicore.ui.dashboard.model.CellContentElement;

public class CellContentElementUpdate extends CellContentElementCreate {

	private String id;
	@JsonIgnore
	private CellContentElement cellContentElement;

	public String getId() {
		return id;
	}

	public CellContentElementUpdate setId(String id) {
		this.id = id;
		return this;
	}

	@JsonIgnore
	public CellContentElement getCellContentElement() {
		return cellContentElement;
	}

	public CellContentElementUpdate setCellContentElement(CellContentElement cellContentElement) {
		this.cellContentElement = cellContentElement;
		return this;
	}
}
