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

	public <T extends StateUpdate> T setId(String id) {
		this.id = id;
		return (T) this;
	}

	@JsonIgnore
	public State getState() {
		return State;
	}

	public <T extends StateUpdate> T setState(com.flexicore.model.territories.State state) {
		State = state;
		return (T) this;
	}
}