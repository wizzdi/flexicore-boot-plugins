package com.flexicore.ui.model;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flexicore.model.Baseclass;
import com.wizzdi.dynamic.properties.converter.JsonConverter;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Entity
public class Preset extends Baseclass {

	private String externalId;

	@Column(columnDefinition = "jsonb")
	@Convert(converter = JsonConverter.class)
	private Map<String, Object> jsonNode=new HashMap<>();

	public Preset() {
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

	@JsonAnySetter
	public void add(String key, Object value) {
		jsonNode.put(key, value);
	}


	public <T extends Preset> T setJsonNode(Map<String, Object> jsonNode) {
		this.jsonNode = jsonNode;
		return (T) this;
	}
}
