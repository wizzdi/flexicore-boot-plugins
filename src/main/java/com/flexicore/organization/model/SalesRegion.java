package com.flexicore.organization.model;

import com.flexicore.model.Baseclass;

import javax.persistence.Entity;

@Entity
public class SalesRegion extends Baseclass {
	static SalesRegion s_Singleton = new SalesRegion();

	public static SalesRegion s() {
		return s_Singleton;
	}

}
