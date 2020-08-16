package com.flexicore.ui.model;

import com.flexicore.security.SecurityContext;

import javax.persistence.Entity;
import javax.persistence.Lob;

@Entity
public class MapPreset extends Preset {

	public MapPreset() {
	}

	public MapPreset(String name, SecurityContext securityContext) {
		super(name, securityContext);
	}

	private double latCenter;
	private double lonCenter;

	public double getLatCenter() {
		return latCenter;
	}

	public <T extends MapPreset> T setLatCenter(double latCenter) {
		this.latCenter = latCenter;
		return (T) this;
	}

	public double getLonCenter() {
		return lonCenter;
	}

	public <T extends MapPreset> T setLonCenter(double lonCenter) {
		this.lonCenter = lonCenter;
		return (T) this;
	}
}
