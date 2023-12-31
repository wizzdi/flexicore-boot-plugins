package com.flexicore.ui.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flexicore.ui.model.Form;

public class FormUpdate extends FormCreate {

	private String id;
	@JsonIgnore
	private Form form;

	public String getId() {
		return id;
	}

	public FormUpdate setId(String id) {
		this.id = id;
		return this;
	}

	@JsonIgnore
	public Form getForm() {
		return form;
	}

	public FormUpdate setForm(Form form) {
		this.form = form;
		return this;
	}
}
