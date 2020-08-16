package com.flexicore.ui.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flexicore.model.Baseclass;
import com.flexicore.security.SecurityContext;

import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

@Entity
public class CardGroup extends Baseclass {

	public CardGroup() {
	}

	public CardGroup(String name, SecurityContext securityContext) {
		super(name, securityContext);
	}


	@JsonIgnore
	@OneToMany(targetEntity = Card.class, mappedBy = "cardGroup")
	private List<Card> cards = new ArrayList<>();

	@ManyToOne(targetEntity = Dashboard.class)
	private Dashboard dashboard;

	@ManyToOne(targetEntity = Dashboard.class)
	public Dashboard getDashboard() {
		return dashboard;
	}

	public <T extends CardGroup> T setDashboard(Dashboard dashboard) {
		this.dashboard = dashboard;
		return (T) this;
	}

	@JsonIgnore
	@OneToMany(targetEntity = Card.class, mappedBy = "cardGroup")
	public List<Card> getCards() {
		return cards;
	}

	public <T extends CardGroup> T setCards(
			List<Card> invokers) {
		this.cards = invokers;
		return (T) this;
	}


}
