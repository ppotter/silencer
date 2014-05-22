package com.potter.silencer.manager;

import java.util.ArrayList;
import java.util.Calendar;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.CalendarContract.Instances;

import com.potter.silencer.AlarmFactory;
import com.potter.silencer.model.CalendarEventInstance;
import com.potter.silencer.service.RestoreRunnable;
import com.potter.silencer.service.SilenceRunnable;
import com.potter.silencer.ui.preference.TimeDialogPreference;
import com.potter.silencer.ui.settings.SettingsFragment;

public class CalendarManager {
	
	private Context mContext;
	private AlarmFactory mAlarmFactory;

	public CalendarManager(final Context context) {
		this.mContext = context;
	}

	private AlarmFactory getAlarmFactory() {
		if (mAlarmFactory == null)
			mAlarmFactory = AlarmFactory.newInstance(mContext);
		return mAlarmFactory;
	}
	
	public CalendarEventInstance getCalendarEventInstance(int id){
		Cursor cursor = null;
		ContentResolver contentResolver = mContext.getContentResolver();
		Uri uri = Instances.CONTENT_URI;
		
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DATE, -1);
		uri = ContentUris.withAppendedId(uri, calendar.getTimeInMillis());//start
		
		calendar = Calendar.getInstance();
		calendar.add(Calendar.YEAR, 1);
		uri = ContentUris.withAppendedId(uri, calendar.getTimeInMillis());//end
		
		String selection = Instances._ID + " = ?";
		String[] selectionArgs = {String.valueOf(id)};
		String sortBy = Instances.END + " ASC";
		cursor = contentResolver.query(uri,
				CalendarEventInstance.EVENT_PROJECTION, selection,
				selectionArgs, sortBy);
		return new CalendarEventInstance(cursor);
	}

	public CalendarManager createAllCurrentAlarms() {
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(mContext);

		if(preferences.getBoolean(SettingsFragment.KEY_PREF_SILENCE_CALENDAR_EVENT, false)){
			Cursor cursor = null;
			ContentResolver contentResolver = mContext.getContentResolver();
			Uri uri = Instances.CONTENT_URI;
			
			Calendar calendar = Calendar.getInstance();
			calendar.add(Calendar.DATE, -1);
			uri = ContentUris.withAppendedId(uri, calendar.getTimeInMillis());//start
			
			calendar = Calendar.getInstance();
			calendar.add(Calendar.YEAR, 1);
			uri = ContentUris.withAppendedId(uri, calendar.getTimeInMillis());//end
			
			String selection = getSelection();
			String[] selectionArgs = getSelectionArgs();
			String sortBy = Instances.END + " ASC";
			cursor = contentResolver.query(uri,
					CalendarEventInstance.EVENT_PROJECTION, selection,
					selectionArgs, sortBy);
			
			getAlarmFactory().createAlarms(cursor);
		}
		return this;
	}
	
	public CalendarManager cancelAllCurrentAlarms(){
		Cursor cursor = null;
		ContentResolver contentResolver = mContext.getContentResolver();
		Uri uri = Instances.CONTENT_URI;
		uri = ContentUris.withAppendedId(uri, System.currentTimeMillis());//start
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.MONTH, 8);
		uri = ContentUris.withAppendedId(uri, calendar.getTimeInMillis());//end
		String selection = null;
		String[] selectionArgs = null;

		cursor = contentResolver.query(uri,
				CalendarEventInstance.EVENT_PROJECTION, selection,
				selectionArgs, null);

		getAlarmFactory().cancelAlarms(cursor);
		return this;
	}
	
	
	public CalendarManager createAllCurrentPostDelays(final Handler handler) {
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(mContext);
		
		if(preferences.getBoolean(SettingsFragment.KEY_PREF_SILENCE_CALENDAR_EVENT, false)){
			Cursor cursor = null;
			ContentResolver contentResolver = mContext.getContentResolver();
			Uri uri = Instances.CONTENT_URI;
			
			Calendar calendar = Calendar.getInstance();
			calendar.add(Calendar.DATE, -1);
			uri = ContentUris.withAppendedId(uri, calendar.getTimeInMillis());//start
			
			calendar = Calendar.getInstance();
			calendar.add(Calendar.YEAR, 1);
			uri = ContentUris.withAppendedId(uri, calendar.getTimeInMillis());//end
			
			String selection = getSelection();
			String[] selectionArgs = getSelectionArgs();
			String sortBy = Instances.END + " ASC";
			cursor = contentResolver.query(uri,
					CalendarEventInstance.EVENT_PROJECTION, selection,
					selectionArgs, sortBy);
			
			createAlarmPostDelays(handler, cursor);
			}
		return this;
	}
	
	public void createRestoreAlarmPostDelay(final Handler handler, final long endTime){
		handler.postDelayed(new RestoreRunnable(mContext), endTime - System.currentTimeMillis());
	}

	private void createAlarmPostDelays(final Handler handler, final Cursor cursor){
		while(cursor.moveToNext()){
			createAlarmPostDelay(handler, new CalendarEventInstance(cursor));
		}
	}
	
	private void createAlarmPostDelay(final Handler handler, final CalendarEventInstance calendarEventInstance){
		if(shouldCreateAlarm(calendarEventInstance)){
			handler.postDelayed(new SilenceRunnable(mContext, calendarEventInstance.getEnd()), calendarEventInstance.getBegin() - System.currentTimeMillis());
			handler.postDelayed(new RestoreRunnable(mContext), calendarEventInstance.getEnd() - System.currentTimeMillis());
		}
	}
	
	public CalendarManager cancelAllCurrentPostDelays(final Handler handler){
		handler.removeCallbacks(new SilenceRunnable(mContext));
		handler.removeCallbacks(new RestoreRunnable(mContext));
		return this;
	}
	
	private boolean shouldCreateAlarm(CalendarEventInstance instance){
		//Should long events be ignored.
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(mContext);
		String time = preferences.getString(SettingsFragment.KEY_PREF_LONG_EVENTS_LENGTH, SettingsFragment.DEFAULT_PREF_LONG_EVENTS_LENGTH);
		boolean shouldIgnoreLongEvents = preferences.getBoolean(SettingsFragment.KEY_PREF_LONG_EVENTS_ENABLED, SettingsFragment.DEFAULT_LONG_EVENTS_ENABLED);
		if(shouldIgnoreLongEvents){
			long maxLength =  TimeDialogPreference.getMilliseconds(time);
			long eventLength = instance.getEnd() - instance.getBegin();
			
			if(eventLength > maxLength){
				//The event is too long and should be ignored per the user preference.
				return false;
			}
		}

		return true;
	}

	
	private String getSelection(){
		StringBuilder result = new StringBuilder();
		
		result.append(Instances.END + " > ?");
		
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(mContext);
		if(isAllDayEventsIgnored(preferences)){
			result.append(" AND ");
			result.append(Instances.ALL_DAY + " = ?");
		}
		
		return result.toString();
	}
	
	private String[] getSelectionArgs(){
		ArrayList<String> result = new ArrayList<String>();
		result.add(String.valueOf(System.currentTimeMillis()));
		
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(mContext);
		if(isAllDayEventsIgnored(preferences)){
			result.add(String.valueOf(0));
		}
		
		String[] returnableResult = new String[result.size()];
		return result.toArray(returnableResult);
	}
	
	private boolean isAllDayEventsIgnored(SharedPreferences preferences){
		return preferences.getBoolean(SettingsFragment.KEY_PREF_SILENCE_ALL_DAY_EVENTS, SettingsFragment.DEFAULT_SILENCE_ALL_DAY_EVENTS_ENABLED);
	}
	
	private boolean isLongEventsIgnored(SharedPreferences preferences){
		return preferences.getBoolean(SettingsFragment.KEY_PREF_LONG_EVENTS_ENABLED, SettingsFragment.DEFAULT_LONG_EVENTS_ENABLED);
	}
	
	private String getLongEventsLimit(SharedPreferences preferences){
		return preferences.getString(SettingsFragment.KEY_PREF_LONG_EVENTS_LENGTH, SettingsFragment.DEFAULT_PREF_LONG_EVENTS_LENGTH);
	}
}
