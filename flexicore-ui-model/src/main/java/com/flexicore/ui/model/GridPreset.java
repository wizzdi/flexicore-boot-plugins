package com.flexicore.ui.model;

import com.wizzdi.flexicore.boot.dynamic.invokers.model.DynamicExecution;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;

import java.util.Optional;

@Entity
public class GridPreset extends Preset {

	private String relatedClassCanonicalName;

	@ManyToOne(targetEntity = DynamicExecution.class)
	private DynamicExecution dynamicExecution;

	private String latMapping;
	private String lonMapping;


	public GridPreset() {
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

	public String getTenantName(){
		return Optional.ofNullable(getSecurity()).map(f->f.getTenant()).map(f->f.getName()).orElse(null);
	}

	public String getCreatorName(){
		return Optional.ofNullable(getSecurity()).map(f->f.getCreator()).map(f->f.getName()).orElse(null);
	}
}
