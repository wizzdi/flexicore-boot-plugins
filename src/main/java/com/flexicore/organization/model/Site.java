package com.flexicore.organization.model;

import com.flexicore.model.Baseclass;

import javax.persistence.Entity;

@Entity
public class Site extends Baseclass {
	static private Site s_Singleton = new Site();
	static public Site s() {
		return s_Singleton;
	}


}