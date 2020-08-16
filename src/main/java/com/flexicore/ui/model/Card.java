package com.flexicore.ui.model;

import com.flexicore.model.Baseclass;
import com.flexicore.security.SecurityContext;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Entity
public class Card extends Baseclass {

	public Card() {
	}

	public Card(String name, SecurityContext securityContext) {
		super(name, securityContext);
	}


	@ManyToOne(targetEntity = CardGroup.class)
	private CardGroup cardGroup;

	@ManyToOne(targetEntity = CardGroup.class)
	public CardGroup getCardGroup() {
		return cardGroup;
	}

	public <T extends Card> T setCardGroup(
			CardGroup dashboardElement) {
		this.cardGroup = dashboardElement;
		return (T) this;
	}



}
