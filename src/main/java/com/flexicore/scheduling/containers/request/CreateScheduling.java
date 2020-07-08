package com.flexicore.scheduling.containers.request;

import com.flexicore.scheduling.model.TimeOfTheDayName;

import java.time.OffsetDateTime;

public class CreateScheduling {

	private String name;
	private String description;
	private OffsetDateTime timeFrameStart;
	private OffsetDateTime timeFrameEnd;
	private Boolean sunday;
	private Boolean monday;
	private Boolean tuesday;
	private Boolean wednesday;
	private Boolean thursday;
	private Boolean friday;
	private Boolean saturday;
	private Boolean holiday;
	private Boolean enabled;

	public String getName() {
		return name;
	}

	public CreateScheduling setName(String name) {
		this.name = name;
		return this;
	}

	public OffsetDateTime getTimeFrameStart() {
		return timeFrameStart;
	}

	public CreateScheduling setTimeFrameStart(OffsetDateTime timeFrameStart) {
		this.timeFrameStart = timeFrameStart;
		return this;
	}

	public OffsetDateTime getTimeFrameEnd() {
		return timeFrameEnd;
	}

	public CreateScheduling setTimeFrameEnd(OffsetDateTime timeFrameEnd) {
		this.timeFrameEnd = timeFrameEnd;
		return this;
	}

	public String getDescription() {
		return description;
	}

	public CreateScheduling setDescription(String description) {
		this.description = description;
		return this;
	}

	public Boolean getSunday() {
		return sunday;
	}

	public CreateScheduling setSunday(Boolean sunday) {
		this.sunday = sunday;
		return this;
	}

	public Boolean getMonday() {
		return monday;
	}

	public CreateScheduling setMonday(Boolean monday) {
		this.monday = monday;
		return this;
	}

	public Boolean getTuesday() {
		return tuesday;
	}

	public CreateScheduling setTuesday(Boolean tuesday) {
		this.tuesday = tuesday;
		return this;
	}

	public Boolean getWednesday() {
		return wednesday;
	}

	public CreateScheduling setWednesday(Boolean wednesday) {
		this.wednesday = wednesday;
		return this;
	}

	public Boolean getThursday() {
		return thursday;
	}

	public CreateScheduling setThursday(Boolean thursday) {
		this.thursday = thursday;
		return this;
	}

	public Boolean getFriday() {
		return friday;
	}

	public CreateScheduling setFriday(Boolean friday) {
		this.friday = friday;
		return this;
	}

	public Boolean getSaturday() {
		return saturday;
	}

	public CreateScheduling setSaturday(Boolean saturday) {
		this.saturday = saturday;
		return this;
	}

	public Boolean getHoliday() {
		return holiday;
	}

	public CreateScheduling setHoliday(Boolean holiday) {
		this.holiday = holiday;
		return this;
	}

	public Boolean getEnabled() {
		return enabled;
	}

	public CreateScheduling setEnabled(Boolean enabled) {
		this.enabled = enabled;
		return this;
	}
}
