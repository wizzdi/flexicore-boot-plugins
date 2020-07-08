package com.flexicore.scheduling.containers.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flexicore.scheduling.model.Schedule;
import com.flexicore.scheduling.model.TimeOfTheDayName;

import java.time.OffsetDateTime;
import java.time.ZonedDateTime;

public class CreateTimeslot {
	private String scheduleId;
	@JsonIgnore
	private Schedule schedule;
	private String name;
	private String description;
	private TimeOfTheDayName startTimeOfTheDayName;
	private Double timeOfTheDayNameStartLat;
	private Double timeOfTheDayNameStartLon;

	private TimeOfTheDayName endTimeOfTheDayName;
	private Double timeOfTheDayNameEndLat;
	private Double timeOfTheDayNameEndLon;

	@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssXXX")
	private ZonedDateTime timeOfTheDayStart;

	@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssXXX")
	private ZonedDateTime timeOfTheDayEnd;
	private Long startMillisOffset;
	private Long endMillisOffset;

	private Long coolDownIntervalBeforeRepeat;

	public TimeOfTheDayName getStartTimeOfTheDayName() {
		return startTimeOfTheDayName;
	}

	public CreateTimeslot setStartTimeOfTheDayName(
			TimeOfTheDayName startTimeOfTheDayName) {
		this.startTimeOfTheDayName = startTimeOfTheDayName;
		return this;
	}

	public Double getTimeOfTheDayNameStartLat() {
		return timeOfTheDayNameStartLat;
	}

	public CreateTimeslot setTimeOfTheDayNameStartLat(
			Double timeOfTheDayNameStartLat) {
		this.timeOfTheDayNameStartLat = timeOfTheDayNameStartLat;
		return this;
	}

	public Double getTimeOfTheDayNameStartLon() {
		return timeOfTheDayNameStartLon;
	}

	public CreateTimeslot setTimeOfTheDayNameStartLon(
			Double timeOfTheDayNameStartLon) {
		this.timeOfTheDayNameStartLon = timeOfTheDayNameStartLon;
		return this;
	}

	public TimeOfTheDayName getEndTimeOfTheDayName() {
		return endTimeOfTheDayName;
	}

	public CreateTimeslot setEndTimeOfTheDayName(
			TimeOfTheDayName endTimeOfTheDayName) {
		this.endTimeOfTheDayName = endTimeOfTheDayName;
		return this;
	}

	public Double getTimeOfTheDayNameEndLat() {
		return timeOfTheDayNameEndLat;
	}

	public CreateTimeslot setTimeOfTheDayNameEndLat(
			Double timeOfTheDayNameEndLat) {
		this.timeOfTheDayNameEndLat = timeOfTheDayNameEndLat;
		return this;
	}

	public Double getTimeOfTheDayNameEndLon() {
		return timeOfTheDayNameEndLon;
	}

	public CreateTimeslot setTimeOfTheDayNameEndLon(
			Double timeOfTheDayNameEndLon) {
		this.timeOfTheDayNameEndLon = timeOfTheDayNameEndLon;
		return this;
	}

	public ZonedDateTime getTimeOfTheDayStart() {
		return timeOfTheDayStart;
	}

	public <T extends CreateTimeslot> T setTimeOfTheDayStart(
			ZonedDateTime timeOfTheDayStart) {
		this.timeOfTheDayStart = timeOfTheDayStart;
		return (T) this;
	}

	public ZonedDateTime getTimeOfTheDayEnd() {
		return timeOfTheDayEnd;
	}

	public <T extends CreateTimeslot> T setTimeOfTheDayEnd(
			ZonedDateTime timeOfTheDayEnd) {
		this.timeOfTheDayEnd = timeOfTheDayEnd;
		return (T) this;
	}

	public Long getStartMillisOffset() {
		return startMillisOffset;
	}

	public CreateTimeslot setStartMillisOffset(Long startMillisOffset) {
		this.startMillisOffset = startMillisOffset;
		return this;
	}

	public Long getCoolDownIntervalBeforeRepeat() {
		return coolDownIntervalBeforeRepeat;
	}

	public CreateTimeslot setCoolDownIntervalBeforeRepeat(
			Long coolDownIntervalBeforeRepeat) {
		this.coolDownIntervalBeforeRepeat = coolDownIntervalBeforeRepeat;
		return this;
	}

	public String getScheduleId() {
		return scheduleId;
	}

	public CreateTimeslot setScheduleId(String scheduleId) {
		this.scheduleId = scheduleId;
		return this;
	}

	@JsonIgnore
	public Schedule getSchedule() {
		return schedule;
	}

	public CreateTimeslot setSchedule(Schedule schedule) {
		this.schedule = schedule;
		return this;
	}

	public String getName() {
		return name;
	}

	public CreateTimeslot setName(String name) {
		this.name = name;
		return this;
	}

	public String getDescription() {
		return description;
	}

	public CreateTimeslot setDescription(String description) {
		this.description = description;
		return this;
	}

	public Long getEndMillisOffset() {
		return endMillisOffset;
	}

	public CreateTimeslot setEndMillisOffset(Long endMillisOffset) {
		this.endMillisOffset = endMillisOffset;
		return this;
	}
}
