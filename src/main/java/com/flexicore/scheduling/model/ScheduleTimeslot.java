package com.flexicore.scheduling.model;

import com.flexicore.model.Baseclass;
import com.flexicore.security.SecurityContext;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import java.time.OffsetDateTime;

@Entity
public class ScheduleTimeslot extends Baseclass {


	@Column(columnDefinition = "timestamp with time zone")
	private OffsetDateTime startTime; // the explicit time for TS start
	private String startTimeOffsetId;
	@Column(columnDefinition = "timestamp with time zone")
	private OffsetDateTime endTime; // the explicit time for TS to end
	private String endTimeOffsetId;

	private TimeOfTheDayName startTimeOfTheDayName; // alternate implicit start
													// time, for example sunRize
	private double timeOfTheDayNameStartLat; // location for implicit time
	private double timeOfTheDayNameStartLon;
	private long startMillisOffset; // offset from implicit time bame
	private long endMillisOffset; // offset from implicit time name

	@Column(columnDefinition = "timestamp with time zone")
	private OffsetDateTime lastExecution; // saved by the system

	private TimeOfTheDayName endTimeOfTheDayName;
	private double timeOfTheDayNameEndLat;
	private double timeOfTheDayNameEndLon;
	private long coolDownIntervalBeforeRepeat; // this is the recurring interval
												// for the timeslot.

	@ManyToOne(targetEntity = Schedule.class)
	private Schedule schedule; // parent schedule

	public ScheduleTimeslot() {
	}

	public ScheduleTimeslot(String name, SecurityContext securityContext) {
		super(name, securityContext);
	}

	public OffsetDateTime getStartTime() {
		return startTime;
	}

	public ScheduleTimeslot setStartTime(OffsetDateTime startTime) {
		this.startTime = startTime;
		return this;
	}

	public OffsetDateTime getEndTime() {
		return endTime;
	}

	public ScheduleTimeslot setEndTime(OffsetDateTime endTime) {
		this.endTime = endTime;
		return this;
	}
	@ManyToOne(targetEntity = Schedule.class)
	public Schedule getSchedule() {
		return schedule;
	}

	public ScheduleTimeslot setSchedule(Schedule schedule) {
		this.schedule = schedule;
		return this;
	}

	public TimeOfTheDayName getStartTimeOfTheDayName() {
		return startTimeOfTheDayName;
	}

	public ScheduleTimeslot setStartTimeOfTheDayName(
			TimeOfTheDayName startTimeOfTheDayName) {
		this.startTimeOfTheDayName = startTimeOfTheDayName;
		return this;
	}

	public double getTimeOfTheDayNameStartLat() {
		return timeOfTheDayNameStartLat;
	}

	public ScheduleTimeslot setTimeOfTheDayNameStartLat(
			double timeOfTheDayNameStartLat) {
		this.timeOfTheDayNameStartLat = timeOfTheDayNameStartLat;
		return this;
	}

	public double getTimeOfTheDayNameStartLon() {
		return timeOfTheDayNameStartLon;
	}

	public ScheduleTimeslot setTimeOfTheDayNameStartLon(
			double timeOfTheDayNameStartLon) {
		this.timeOfTheDayNameStartLon = timeOfTheDayNameStartLon;
		return this;
	}

	public long getCoolDownIntervalBeforeRepeat() {
		return coolDownIntervalBeforeRepeat;
	}

	public ScheduleTimeslot setCoolDownIntervalBeforeRepeat(
			long coolDownIntervalBeforeRepeat) {
		this.coolDownIntervalBeforeRepeat = coolDownIntervalBeforeRepeat;
		return this;
	}

	public long getStartMillisOffset() {
		return startMillisOffset;
	}

	public ScheduleTimeslot setStartMillisOffset(long startMillisOffset) {
		this.startMillisOffset = startMillisOffset;
		return this;
	}

	public TimeOfTheDayName getEndTimeOfTheDayName() {
		return endTimeOfTheDayName;
	}

	public ScheduleTimeslot setEndTimeOfTheDayName(
			TimeOfTheDayName endTimeOfTheDayName) {
		this.endTimeOfTheDayName = endTimeOfTheDayName;
		return this;
	}

	public double getTimeOfTheDayNameEndLat() {
		return timeOfTheDayNameEndLat;
	}

	public ScheduleTimeslot setTimeOfTheDayNameEndLat(
			double timeOfTheDayNameEndLat) {
		this.timeOfTheDayNameEndLat = timeOfTheDayNameEndLat;
		return this;
	}

	public double getTimeOfTheDayNameEndLon() {
		return timeOfTheDayNameEndLon;
	}

	public ScheduleTimeslot setTimeOfTheDayNameEndLon(
			double timeOfTheDayNameEndLon) {
		this.timeOfTheDayNameEndLon = timeOfTheDayNameEndLon;
		return this;
	}

	public OffsetDateTime getLastExecution() {
		return lastExecution;
	}

	public ScheduleTimeslot setLastExecution(OffsetDateTime lastExecution) {
		this.lastExecution = lastExecution;
		return this;
	}

	public long getEndMillisOffset() {
		return endMillisOffset;
	}

	public ScheduleTimeslot setEndMillisOffset(long endMillisOffset) {
		this.endMillisOffset = endMillisOffset;
		return this;
	}

	public String getStartTimeOffsetId() {
		return startTimeOffsetId;
	}

	public <T extends ScheduleTimeslot> T setStartTimeOffsetId(String startTimeOffsetId) {
		this.startTimeOffsetId = startTimeOffsetId;
		return (T) this;
	}

	public String getEndTimeOffsetId() {
		return endTimeOffsetId;
	}

	public <T extends ScheduleTimeslot> T setEndTimeOffsetId(String endTimeOffsetId) {
		this.endTimeOffsetId = endTimeOffsetId;
		return (T) this;
	}
}
