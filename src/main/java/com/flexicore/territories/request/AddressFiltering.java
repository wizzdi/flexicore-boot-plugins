package com.flexicore.territories.request;

import com.flexicore.model.FilteringInformationHolder;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flexicore.model.territories.Street;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AddressFiltering extends FilteringInformationHolder {

	private Set<Integer> floors;
	private Set<String> streetsIds=new HashSet<>();
	@JsonIgnore
	private List<Street> streets;
	private Set<Integer> numbers;
	private Set<String> zipCodes;

	public Set<Integer> getFloors() {
		return floors;
	}

	public <T extends AddressFiltering> T setFloors(Set<Integer> floors) {
		this.floors = floors;
		return (T) this;
	}

	public Set<String> getStreetsIds() {
		return streetsIds;
	}

	public <T extends AddressFiltering> T setStreetsIds(Set<String> streetsIds) {
		this.streetsIds = streetsIds;
		return (T) this;
	}

	@JsonIgnore
	public List<Street> getStreets() {
		return streets;
	}

	public <T extends AddressFiltering> T setStreets(List<Street> streets) {
		this.streets = streets;
		return (T) this;
	}

	public Set<Integer> getNumbers() {
		return numbers;
	}

	public <T extends AddressFiltering> T setNumbers(Set<Integer> numbers) {
		this.numbers = numbers;
		return (T) this;
	}

	public Set<String> getZipCodes() {
		return zipCodes;
	}

	public <T extends AddressFiltering> T setZipCodes(Set<String> zipCodes) {
		this.zipCodes = zipCodes;
		return (T) this;
	}
}