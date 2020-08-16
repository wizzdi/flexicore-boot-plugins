package com.flexicore.ui.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flexicore.security.SecurityContext;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Dashboard extends Preset {

	public Dashboard() {
	}

	public Dashboard(String name, SecurityContext securityContext) {
		super(name, securityContext);
	}


	@OneToMany(targetEntity = CardGroup.class, mappedBy = "dashboard", cascade = {CascadeType.MERGE, CascadeType.PERSIST})
	@JsonIgnore
	private List<CardGroup> cardGroups = new ArrayList<>();

	@OneToMany(targetEntity = CardGroup.class, mappedBy = "dashboard", cascade = {CascadeType.MERGE, CascadeType.PERSIST})
	@JsonIgnore
	public List<CardGroup> getCardGroups() {
		return cardGroups;
	}

	public <T extends Dashboard> T setCardGroups(List<CardGroup> dashboardElements) {
		this.cardGroups = dashboardElements;
		return (T) this;
	}

}
