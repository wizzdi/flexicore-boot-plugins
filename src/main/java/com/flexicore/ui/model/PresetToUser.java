package com.flexicore.ui.model;

import com.flexicore.model.Baseclass;
import com.flexicore.model.Baselink;
import com.flexicore.model.Clazz;
import com.flexicore.model.User;
import com.flexicore.security.SecurityContext;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Entity
public class PresetToUser extends PresetToEntity {
	static PresetToUser s_Singleton = new PresetToUser();
	public static PresetToUser s() {
		return s_Singleton;
	}

	public PresetToUser() {
	}

	public PresetToUser(String name, SecurityContext securityContext) {
		super(name, securityContext);
	}

	@Override
	@ManyToOne(targetEntity = Preset.class, cascade = {CascadeType.MERGE,
			CascadeType.PERSIST})
	public Preset getLeftside() {
		return (Preset) super.getLeftside();
	}

	public void setLeftside(Preset leftside) {
		super.setLeftside(leftside);
	}

	@Override
	public void setLeftside(Baseclass leftside) {
		super.setLeftside(leftside);
	}

	@Override
	@ManyToOne(targetEntity = User.class, cascade = {CascadeType.MERGE,
			CascadeType.PERSIST})
	public User getRightside() {
		return (User) super.getRightside();
	}

	public void setRightside(User rightside) {
		super.setRightside(rightside);
	}

	@Override
	public void setRightside(Baseclass rightside) {
		super.setRightside(rightside);
	}

}
