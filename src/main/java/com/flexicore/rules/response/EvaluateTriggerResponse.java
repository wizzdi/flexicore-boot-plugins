package com.flexicore.rules.response;

public class EvaluateTriggerResponse {
	private boolean active;

	public boolean isActive() {
		return active;
	}

	public <T extends EvaluateTriggerResponse> T setActive(boolean active) {
		this.active = active;
		return (T) this;
	}
}
