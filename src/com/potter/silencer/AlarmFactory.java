package com.potter.silencer;

import java.util.Date;

import android.app.AlarmManager;
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

public class AlarmFactory {

	private static final long ALARM_BUFFER = 5000;
	
	public static final String ACTION_START_EVENT_SILENCE = "com.potter.silencer.ACTION_START_EVENT_SILENCE";
	public static final String ACTION_START_TEMPORARY_SILENCE = "com.potter.silencer.ACTION_START_TEMPORARY_SILENCE";
	public static final String ACTION_END_EVENT_SILENCE = "com.potter.silencer.ACTION_END_EVENT_SILENCE";
	public static final String ACTION_END_TEMPORARY_SILENCE = "com.potter.silencer.ACTION_END_TEMPORARY_SILENCE";

	public static final String EXTRA_INSTANCE_ID = "com.potter.silencer.EXTRA_INSTANCE_ID";
	public static final String EXTRA_END_TIME = "com.potter.silencer.EXTRA_END_TIME";

	private final Context mContext;
	private AlarmManager mAlarmManager;
	
	private AlarmFactory(final Context context){
		mContext = context;
	}
	
	public static AlarmFactory newInstance(final Context context){
		return new AlarmFactory(context);
	}

	private AlarmManager getAlarmManager() {
		if (mAlarmManager == null)
			mAlarmManager = (AlarmManager) mContext
					.getSystemService(Context.ALARM_SERVICE);
		return mAlarmManager;
	}
	
	public AlarmFactory createAlarms(final Cursor cursor){
		while(cursor.moveToNext()){
			createAlarm(cursor);
		}
		return this;
	}
	
	public AlarmFactory createAlarm(final Cursor cursor) {
		return createAlarm(new CalendarEventInstance(cursor));
	}

	public AlarmFactory cancelAlarms(final Cursor cursor) {
		while (cursor.moveToNext()) {
			cancelAlarm(cursor);
		}
		return this;
	}

	public AlarmFactory cancelAlarm(final Cursor cursor) {
		return cancelAlarm(new CalendarEventInstance(cursor));
	}

	public PendingIntent prepareIntent(final String action, final CalendarEventInstance instance) {
		return prepareIntent(action, instance.getEnd());
	}

	public PendingIntent prepareIntent(String action){
		return prepareIntent(action, -1);
	}
	
	private PendingIntent prepareIntent(String action, final long alarmEndTime){
		Intent intent = new Intent(action, null, mContext, AlarmSilencerBroadcastReceiver.class);
		intent.putExtra(EXTRA_END_TIME, alarmEndTime);
		return PendingIntent.getBroadcast(mContext, 0, intent, 0);
	}

	public AlarmFactory createAlarm(final CalendarEventInstance instance) {
		Date now = new Date();
		if(now.getTime() > instance.getEnd()){
			return this;
		}
		
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(mContext);
		String time = preferences.getString(SettingsFragment.KEY_PREF_LONG_EVENTS_LENGTH, SettingsFragment.DEFAULT_PREF_LONG_EVENTS_LENGTH);
		boolean shouldIgnoreLongEvents = preferences.getBoolean(SettingsFragment.KEY_PREF_LONG_EVENTS_ENABLED, SettingsFragment.DEFAULT_LONG_EVENTS_ENABLED);
		
		if(shouldIgnoreLongEvents){
			long maxLength =  TimeDialogPreference.getMilliseconds(time);
			long eventLength = instance.getEnd() - instance.getBegin();
			
			if(eventLength > maxLength){
				//The event is too long and should be ignored per the user preference.
				return this;
			}
		}
		
		createAlarms(instance);
		return this;
	}
	
	public AlarmFactory createBeginAlarm(long milliseconds){
		getAlarmManager().set(AlarmManager.RTC_WAKEUP, milliseconds - ALARM_BUFFER, prepareIntent(ACTION_START_TEMPORARY_SILENCE));
		return this;
	}
	
	public AlarmFactory createEndAlarm(long milliseconds){
		getAlarmManager().set(AlarmManager.RTC_WAKEUP, milliseconds + ALARM_BUFFER, prepareIntent(ACTION_END_TEMPORARY_SILENCE));
		return this;
	}
	
	public static long timeToMilliseconds(int hours, int minutes){
		return ((hours * 60) + minutes) * 60 * 1000;
	}
	
	public AlarmFactory cancelAlarm(final CalendarEventInstance instance) {
		getAlarmManager().cancel(prepareIntent(ACTION_START_EVENT_SILENCE, instance));
		getAlarmManager().cancel(prepareIntent(ACTION_END_EVENT_SILENCE, instance));
		return this;
	}
	
	private void createAlarms(final CalendarEventInstance instance){
		getAlarmManager().set(AlarmManager.RTC_WAKEUP, instance.getBegin() - ALARM_BUFFER, prepareIntent(ACTION_START_EVENT_SILENCE, instance));
		getAlarmManager().set(AlarmManager.RTC_WAKEUP, instance.getEnd() + ALARM_BUFFER, prepareIntent(ACTION_END_EVENT_SILENCE, instance));
	}

}
