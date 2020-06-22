package com.flexicore.territories.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flexicore.model.territories.State;

public class StateUpdate extends StateCreate {

	private String id;
	@JsonIgnore
	private State State;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	@JsonIgnore
	public State getState() {
		return State;
	}

	public void setState(State State) {
		this.State = State;
	}

}