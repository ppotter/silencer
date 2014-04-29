package com.potter.silencer;

import java.util.Date;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.preference.PreferenceManager;

import com.potter.silencer.model.CalendarEventInstance;
import com.potter.silencer.receiver.AlarmSilencerBroadcastReceiver;
import com.potter.silencer.ui.preference.TimeDialogPreference;
import com.potter.silencer.ui.settings.SettingsFragment;

public class HandlerFactory {

	public static final String ACTION_START_EVENT_SILENCE = "com.potter.silencer.ACTION_START_EVENT_SILENCE";
	public static final String ACTION_START_TEMPORARY_SILENCE = "com.potter.silencer.ACTION_START_TEMPORARY_SILENCE";
	public static final String ACTION_END_EVENT_SILENCE = "com.potter.silencer.ACTION_END_EVENT_SILENCE";
	public static final String ACTION_END_TEMPORARY_SILENCE = "com.potter.silencer.ACTION_END_TEMPORARY_SILENCE";
	public static final String ACTION_END_SILENCE_ABSOLUTE = "com.potter.silencer.ACTION_END_SILENCE_ABSOLUTE";

	public static final String EXTRA_INSTANCE_ID = "com.potter.silencer.EXTRA_INSTANCE_ID";
	public static final String EXTRA_END_TIME = "com.potter.silencer.EXTRA_END_TIME";
	
	private final Context mContext;
	
	public HandlerFactory(final Context context){
		mContext = context;
	}
	
	public void createWakeUp(Cursor cursor){
		
	}
	
	public void createSilencer(Cursor cursor){
		
	}

	public PendingIntent prepareIntent(final String action, final CalendarEventInstance instance) {
		return prepareIntent(action, instance.getId(), instance.getEnd());
	}

	public PendingIntent prepareIntent(String action){
		return prepareIntent(action, -1, -1);
	}
	
	private PendingIntent prepareIntent(String action, final long eventId, final long alarmEndTime){
		Intent intent = new Intent(action, null, mContext, AlarmSilencerBroadcastReceiver.class);
		intent.putExtra(EXTRA_END_TIME, alarmEndTime);
		intent.putExtra(EXTRA_INSTANCE_ID, eventId);
		return PendingIntent.getBroadcast(mContext, 0, intent, 0);
	}
	
	private boolean shouldCreateAlarm(CalendarEventInstance instance){
		//Ignore if the event ended already.
		Date now = new Date();
		if(now.getTime() > instance.getEnd()){
			return false;
		}
		
		//Should all day events be ignored.
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(mContext);
		boolean shouldIgnoreAllDayEvents = preferences.getBoolean(SettingsFragment.KEY_PREF_SILENCE_ALL_DAY_EVENTS, SettingsFragment.DEFAULT_SILENCE_ALL_DAY_EVENTS_ENABLED);
		
		if(shouldIgnoreAllDayEvents && instance.isAllDay()){
			return false;
		}
		
		//Should long events be ignored.
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

}
