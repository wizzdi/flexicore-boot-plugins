package com.flexicore.ui.model;

import com.flexicore.model.dynamic.DynamicExecution;
import com.flexicore.model.dynamic.DynamicInvoker;
import com.flexicore.security.SecurityContext;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Entity
public class Form extends Preset {
	static Form s_Singleton = new Form();
	public static Form s() {
		return s_Singleton;
	}

	public Form() {
	}

	public Form(String name, SecurityContext securityContext) {
		super(name, securityContext);
	}

	@ManyToOne(targetEntity = DynamicExecution.class)
	private DynamicExecution dynamicExecution;

	@ManyToOne(targetEntity = DynamicExecution.class)
	public DynamicExecution getDynamicExecution() {
		return dynamicExecution;
	}

	public <T extends Form> T setDynamicExecution(
			DynamicExecution dynamicExecution) {
		this.dynamicExecution = dynamicExecution;
		return (T) this;
	}
}
