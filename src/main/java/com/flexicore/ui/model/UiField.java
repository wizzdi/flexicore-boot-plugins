package com.flexicore.ui.model;

import com.flexicore.category.model.Category;
import com.flexicore.model.SecuredBasic;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Entity
public class UiField extends SecuredBasic {

	public UiField() {
	}

	private int priority;
	private boolean visible;
	private String displayName;
	@ManyToOne(targetEntity = Category.class)
	private Category category;
	@ManyToOne(targetEntity = Preset.class)
	private Preset preset;
	private boolean dynamicField;

	@ManyToOne(targetEntity = Preset.class)
	public Preset getPreset() {
		return preset;
	}

	public UiField setPreset(Preset preset) {
		this.preset = preset;
		return this;
	}

	public int getPriority() {
		return priority;
	}

	public UiField setPriority(int priority) {
		this.priority = priority;
		return this;
	}

	public boolean isVisible() {
		return visible;
	}

	public UiField setVisible(boolean visible) {
		this.visible = visible;
		return this;
	}

	public String getDisplayName() {
		return displayName;
	}

	public UiField setDisplayName(String displayName) {
		this.displayName = displayName;
		return this;
	}

	@ManyToOne(targetEntity = Category.class)
	public Category getCategory() {
		return category;
	}

	public UiField setCategory(Category category) {
		this.category = category;
		return this;
	}

	public boolean isDynamicField() {
		return dynamicField;
	}

	public <T extends UiField> T setDynamicField(boolean dynamicField) {
		this.dynamicField = dynamicField;
		return (T) this;
	}
}
