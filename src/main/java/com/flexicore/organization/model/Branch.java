package com.flexicore.organization.model;

import com.flexicore.model.Baseclass;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Entity
public class Branch extends Baseclass {
	static private Branch s_Singleton = new Branch();
	static public Branch s() { return s_Singleton; }

}