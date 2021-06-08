package com.flexicore.ui.model;

import javax.persistence.Entity;

@Entity
public class MapPreset extends Preset {

	private double latCenter;
	private double lonCenter;

	public MapPreset() {
	}



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
