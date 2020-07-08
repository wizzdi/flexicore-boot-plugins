package com.flexicore.ui.model;

import com.flexicore.security.SecurityContext;

import javax.persistence.Entity;

@Entity
public class FormField extends UiField {
	static FormField s_Singleton = new FormField();
	public static FormField s() {
		return s_Singleton;
	}

	public FormField() {
	}

	public FormField(String name, SecurityContext securityContext) {
		super(name, securityContext);
	}

	private boolean editable;
	private boolean creatable;
	private double anchorLeft;
	private double anchorRight;
	private double anchorTop;
	private double anchorBottom;

	public boolean isEditable() {
		return editable;
	}

	public <T extends FormField> T setEditable(boolean editable) {
		this.editable = editable;
		return (T) this;
	}

	public double getAnchorLeft() {
		return anchorLeft;
	}

	public <T extends FormField> T setAnchorLeft(double anchorLeft) {
		this.anchorLeft = anchorLeft;
		return (T) this;
	}

	public double getAnchorRight() {
		return anchorRight;
	}

	public <T extends FormField> T setAnchorRight(double anchorRight) {
		this.anchorRight = anchorRight;
		return (T) this;
	}

	public double getAnchorTop() {
		return anchorTop;
	}

	public <T extends FormField> T setAnchorTop(double anchorTop) {
		this.anchorTop = anchorTop;
		return (T) this;
	}

	public double getAnchorBottom() {
		return anchorBottom;
	}

	public <T extends FormField> T setAnchorBottom(double anchorBottom) {
		this.anchorBottom = anchorBottom;
		return (T) this;
	}

	public boolean isCreatable() {
		return creatable;
	}

	public <T extends FormField> T setCreatable(boolean creatable) {
		this.creatable = creatable;
		return (T) this;
	}

	@Override
	public Form getPreset() {
		return (Form) super.getPreset();
	}

}
