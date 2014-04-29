package com.potter.silencer.model;

import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.CalendarContract.Instances;

public class CalendarEventInstance implements Parcelable {
	public static final String[] EVENT_PROJECTION = new String[] {
		Instances._ID,                           // 0
		Instances.BEGIN,
		Instances.END,
		Instances.END_DAY,
		Instances.END_MINUTE,
		Instances.START_DAY,
		Instances.START_MINUTE,
		Instances.EVENT_ID,
		Instances.ALL_DAY
	};
	  
	// The indices for the projection array above.
	private static final int PROJECTION_ID_INDEX = 0;
	private static final int PROJECTION_BEGIN_INDEX = 1;
	private static final int PROJECTION_END_INDEX = 2;
	private static final int PROJECTION_END_DAY_INDEX = 3;
	private static final int PROJECTION_END_MINUTE_INDEX = 4;
	private static final int PROJECTION_START_DAY_INDEX = 5;
	private static final int PROJECTION_START_MINUTE_INDEX = 6;
	private static final int PROJECTION_EVENT_ID_INDEX = 7;
	private static final int PROJECTION_ALL_DAY_INDEX = 8;
	
	private long id;
	private long begin;
	private long end;
	private int endDay;
	private int endMinute;
	private int startDay;
	private int startMinute;
	private int eventId;
	private String duration;
	private int allDay;
	
	public CalendarEventInstance(){}

	public CalendarEventInstance(Cursor cursor){
	    // Get the field values
	    id = cursor.getLong(PROJECTION_ID_INDEX);
	    begin = cursor.getLong(PROJECTION_BEGIN_INDEX);
	    end = cursor.getLong(PROJECTION_END_INDEX);
	    endDay = cursor.getInt(PROJECTION_END_DAY_INDEX);
	    endMinute = cursor.getInt(PROJECTION_END_MINUTE_INDEX);
	    startDay = cursor.getInt(PROJECTION_START_DAY_INDEX);
	    startMinute = cursor.getInt(PROJECTION_START_MINUTE_INDEX);
	    eventId = cursor.getInt(PROJECTION_EVENT_ID_INDEX);
	    allDay = cursor.getInt(PROJECTION_ALL_DAY_INDEX);
	}
	
	public CalendarEventInstance(Parcel in){
		id = in.readLong();
		begin = in.readLong();
		end = in.readLong();
		endDay = in.readInt();
		endMinute = in.readInt();
		startDay = in.readInt();
		startMinute = in.readInt();
		eventId = in.readInt();
		allDay = in.readInt();
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeLong(id);
		dest.writeLong(begin);
		dest.writeLong(end);
		dest.writeInt(endDay);
		dest.writeInt(endMinute);
		dest.writeInt(startDay);
		dest.writeInt(startMinute);
		dest.writeInt(eventId);
		dest.writeString(duration);
		dest.writeInt(allDay);
	}
	
	public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public CalendarEventInstance createFromParcel(Parcel in) {
            return new CalendarEventInstance(in); 
        }

        public CalendarEventInstance[] newArray(int size) {
            return new CalendarEventInstance[size];
        }
    };

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getBegin() {
		return begin;
	}

	public void setBegin(long begin) {
		this.begin = begin;
	}

	public long getEnd() {
		return end;
	}

	public void setEnd(long end) {
		this.end = end;
	}

	public int getEndDay() {
		return endDay;
	}

	public void setEndDay(int endDay) {
		this.endDay = endDay;
	}

	public int getEndMinute() {
		return endMinute;
	}

	public void setEndMinute(int endMinute) {
		this.endMinute = endMinute;
	}

	public int getStartDay() {
		return startDay;
	}

	public void setStartDay(int startDay) {
		this.startDay = startDay;
	}

	public int getStartMinute() {
		return startMinute;
	}

	public void setStartMinute(int startMinute) {
		this.startMinute = startMinute;
	}

	public int getEventId() {
		return eventId;
	}

	public void setEventId(int eventId) {
		this.eventId = eventId;
	}
	
	public boolean isAllDay(){
		return 1 == allDay;
	}
	
	public int getAllDay(){
		return allDay;
	}
	
	public void setAllDay(int allDay){
		this.allDay = allDay;
	}
}
