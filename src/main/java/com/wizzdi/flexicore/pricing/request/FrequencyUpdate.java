package com.wizzdi.flexicore.pricing.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.wizzdi.flexicore.pricing.model.price.Frequency;

public class FrequencyUpdate extends FrequencyCreate {
	private String id;
	@JsonIgnore
	private Frequency frequency;

	public String getId() {
		return id;
	}

	public FrequencyUpdate setId(String id) {
		this.id = id;
		return this;
	}

	@JsonIgnore
	public Frequency getFrequency() {
		return frequency;
	}

	public FrequencyUpdate setFrequency(Frequency frequency) {
		this.frequency = frequency;
		return this;
	}
}
