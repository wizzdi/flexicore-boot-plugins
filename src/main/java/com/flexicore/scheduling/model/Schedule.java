package com.flexicore.scheduling.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flexicore.model.Baseclass;
import com.flexicore.security.SecurityContext;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Schedule extends Baseclass {

	@JsonIgnore
	@OneToMany(targetEntity = ScheduleToAction.class, mappedBy = "leftside", cascade = {
			CascadeType.PERSIST, CascadeType.MERGE})
	private List<ScheduleToAction> scheduleToActions = new ArrayList<>();

	@JsonIgnore
	@OneToMany(targetEntity = ScheduleTimeslot.class, mappedBy = "schedule", cascade = {
			CascadeType.PERSIST, CascadeType.MERGE})
	private List<ScheduleTimeslot> timeslots = new ArrayList<>();

	@Column(columnDefinition = "timestamp with time zone")
	private OffsetDateTime timeFrameStart;
	@Column(columnDefinition = "timestamp with time zone")
	private OffsetDateTime timeFrameEnd;

	private boolean sunday;
	private boolean monday;
	private boolean tuesday;
	private boolean wednesday;
	private boolean thursday;
	private boolean friday;
	private boolean saturday;
	private boolean holiday;
	private boolean enabled;

	public Schedule() {
	}

	public Schedule(String name, SecurityContext securityContext) {
		super(name, securityContext);
	}

	public OffsetDateTime getTimeFrameStart() {
		return timeFrameStart;
	}

	public Schedule setTimeFrameStart(OffsetDateTime timeFrameStart) {
		this.timeFrameStart = timeFrameStart;
		return this;
	}

	public OffsetDateTime getTimeFrameEnd() {
		return timeFrameEnd;
	}

	public Schedule setTimeFrameEnd(OffsetDateTime timeFrameEnd) {
		this.timeFrameEnd = timeFrameEnd;
		return this;
	}

	public boolean isSunday() {
		return sunday;
	}

	public Schedule setSunday(boolean sunday) {
		this.sunday = sunday;
		return this;
	}

	public boolean isMonday() {
		return monday;
	}

	public Schedule setMonday(boolean monday) {
		this.monday = monday;
		return this;
	}

	public boolean isTuesday() {
		return tuesday;
	}

	public Schedule setTuesday(boolean tuesday) {
		this.tuesday = tuesday;
		return this;
	}

	public boolean isWednesday() {
		return wednesday;
	}

	public Schedule setWednesday(boolean wednesday) {
		this.wednesday = wednesday;
		return this;
	}

	public boolean isThursday() {
		return thursday;
	}

	public Schedule setThursday(boolean thursday) {
		this.thursday = thursday;
		return this;
	}

	public boolean isFriday() {
		return friday;
	}

	public Schedule setFriday(boolean friday) {
		this.friday = friday;
		return this;
	}

	public boolean isSaturday() {
		return saturday;
	}

	public Schedule setSaturday(boolean saturday) {
		this.saturday = saturday;
		return this;
	}

	@JsonIgnore
	@OneToMany(targetEntity = ScheduleToAction.class, mappedBy = "leftside", cascade = {
			CascadeType.PERSIST, CascadeType.MERGE})
	public List<ScheduleToAction> getScheduleToActions() {
		return scheduleToActions;
	}

	public Schedule setScheduleToActions(
			List<ScheduleToAction> scheduleToActions) {
		this.scheduleToActions = scheduleToActions;
		return this;
	}

	public boolean isHoliday() {
		return holiday;
	}

	public Schedule setHoliday(boolean holiday) {
		this.holiday = holiday;
		return this;
	}

	@JsonIgnore
	@OneToMany(targetEntity = ScheduleTimeslot.class, mappedBy = "schedule", cascade = {
			CascadeType.PERSIST, CascadeType.MERGE})
	public List<ScheduleTimeslot> getTimeslots() {
		return timeslots;
	}

	public Schedule setTimeslots(List<ScheduleTimeslot> timeslots) {
		this.timeslots = timeslots;
		return this;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public Schedule setEnabled(boolean enabled) {
		this.enabled = enabled;
		return this;
	}
}
