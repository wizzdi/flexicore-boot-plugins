package com.flexicore.ui.model;

import com.flexicore.security.SecurityContext;
import com.wizzdi.flexicore.boot.dynamic.invokers.model.DynamicExecution;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Entity
public class GridPreset extends Preset {

	private String relatedClassCanonicalName;

	@ManyToOne(targetEntity = DynamicExecution.class)
	private DynamicExecution dynamicExecution;

	private String latMapping;
	private String lonMapping;


	public GridPreset() {
	}

	public GridPreset(String name, SecurityContext securityContext) {
		super(name, securityContext);
	}


	public String getRelatedClassCanonicalName() {
		return relatedClassCanonicalName;
	}


	@ManyToOne(targetEntity = DynamicExecution.class)
	public DynamicExecution getDynamicExecution() {
		return dynamicExecution;
	}


	public <T extends GridPreset> T setRelatedClassCanonicalName(String relatedClassCanonicalName) {
		this.relatedClassCanonicalName = relatedClassCanonicalName;
		return (T) this;
	}

	public <T extends GridPreset> T setDynamicExecution(DynamicExecution dynamicExecution) {
		this.dynamicExecution = dynamicExecution;
		return (T) this;
	}

	public String getLatMapping() {
		return latMapping;
	}

	public <T extends GridPreset> T setLatMapping(String latMapping) {
		this.latMapping = latMapping;
		return (T) this;
	}

	public String getLonMapping() {
		return lonMapping;
	}

	public <T extends GridPreset> T setLonMapping(String lonMapping) {
		this.lonMapping = lonMapping;
		return (T) this;
	}
}
