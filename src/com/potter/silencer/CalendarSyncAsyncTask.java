package com.potter.silencer;

import com.potter.silencer.manager.CalendarManager;

import android.content.Context;
import android.os.AsyncTask;

public class CalendarSyncAsyncTask extends AsyncTask<String, Void, Void> {

	public static final String CREATE_ALARMS = "com.potter.silencer.manager.CREATE_ALARMS";
	public static final String CANCEL_ALARMS = "com.potter.silencer.manager.CANCEL_ALARMS";
	public static final String CANCEL_CREATE_ALARMS = "com.potter.silencer.manager.CANCEL_CREATE_ALARMS";
	
	
	private Context mContext;
	private CalendarManager mCalendarManager;
	
	public CalendarSyncAsyncTask(Context context){
		mContext = context;
		mCalendarManager = new CalendarManager(mContext);
	}
	
	@Override
	protected Void doInBackground(String... action) {
		if(CANCEL_ALARMS.equals(action) || CANCEL_CREATE_ALARMS.equals(action)){
			mCalendarManager.cancelAllCurrentAlarms();
		}
		
		if(CREATE_ALARMS.equals(action) || CANCEL_CREATE_ALARMS.equals(action)){
			mCalendarManager.createAllCurrentAlarms();
		}
		return null;
	}

}