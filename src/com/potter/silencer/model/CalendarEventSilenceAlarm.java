package com.potter.silencer.model;

public class CalendarEventSilenceAlarm {

	private int id;
	private int startRequestCode;
	private int endRequestCode;
	private int startTime;
	private int endTime;
	
	
	public CalendarEventSilenceAlarm(int id, int startRequestCode,
			int endRequestCode, int startTime, int endTime) {
		super();
		this.id = id;
		this.startRequestCode = startRequestCode;
		this.endRequestCode = endRequestCode;
		this.startTime = startTime;
		this.endTime = endTime;
	}
	
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getStartRequestCode() {
		return startRequestCode;
	}
	public void setStartRequestCode(int startRequestCode) {
		this.startRequestCode = startRequestCode;
	}
	public int getEndRequestCode() {
		return endRequestCode;
	}
	public void setEndRequestCode(int endRequestCode) {
		this.endRequestCode = endRequestCode;
	}
	public int getStartTime() {
		return startTime;
	}
	public void setStartTime(int startTime) {
		this.startTime = startTime;
	}
	public int getEndTime() {
		return endTime;
	}
	public void setEndTime(int endTime) {
		this.endTime = endTime;
	}
	
}
