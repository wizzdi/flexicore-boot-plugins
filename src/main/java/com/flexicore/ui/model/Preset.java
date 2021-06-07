package com.flexicore.ui.model;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flexicore.converters.JsonConverter;
import com.flexicore.model.Baseclass;
import com.flexicore.security.SecurityContext;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Entity
public class Preset extends Baseclass {

	private String externalId;

	@Column(columnDefinition = "jsonb")
	@Convert(converter = JsonConverter.class)
	private Map<String, Object> jsonNode;

	public Preset() {
	}

	public Preset(String name, SecurityContext securityContext) {
		super(name, securityContext);
	}

	@OneToMany(targetEntity = UiField.class, mappedBy = "preset", cascade = {
			CascadeType.MERGE, CascadeType.PERSIST})
	@JsonIgnore
	private List<UiField> uiFields = new ArrayList<>();

	@OneToMany(targetEntity = UiField.class, mappedBy = "preset", cascade = {
			CascadeType.MERGE, CascadeType.PERSIST})
	@JsonIgnore
	public List<UiField> getUiFields() {
		return uiFields;
	}

	public <T extends Preset> T setUiFields(List<UiField> uiFields) {
		this.uiFields = uiFields;
		return (T) this;
	}

	public String getExternalId() {
		return externalId;
	}

	public <T extends Preset> T setExternalId(String externalId) {
		this.externalId = externalId;
		return (T) this;
	}

	@JsonIgnore
	@Column(columnDefinition = "jsonb")
	@Convert(converter = JsonConverter.class)
	public Map<String, Object> getJsonNode() {
		return jsonNode;
	}

	@JsonAnyGetter
	public Map<String, Object> any() {
		return jsonNode;
	}

	public <T extends Baseclass> T setJsonNode(Map<String, Object> jsonNode) {
		this.jsonNode = jsonNode;
		return (T) this;
	}
}
