package com.potter.silencer;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.preference.PreferenceManager;

import com.potter.silencer.model.CalendarEventInstance;
import com.potter.silencer.receiver.SilencerBroadcastReceiver;
import com.potter.silencer.ui.fragment.SettingsFragment;
import com.potter.silencer.ui.preference.TimeDialogPreference;

public class AlarmFactory {

	private static final long ALARM_BUFFER = 1000;
	
	public static final String ACTION_START_EVENT_SILENCE = "com.potter.silencer.ACTION_START_EVENT_SILENCE";
	public static final String ACTION_START_TEMPORARY_SILENCE = "com.potter.silencer.ACTION_START_TEMPORARY_SILENCE";
	public static final String ACTION_END_EVENT_SILENCE = "com.potter.silencer.ACTION_END_EVENT_SILENCE";
	public static final String ACTION_END_TEMPORARY_SILENCE = "com.potter.silencer.ACTION_END_TEMPORARY_SILENCE";

	public static final String EXTRA_INSTANCE_ID = "com.potter.silencer.EXTRA_INSTANCE_ID";

	private Context mContext;
	private AlarmManager mAlarmManager;
	
	private AlarmFactory(Context context){
		mContext = context;
	}
	
	public static AlarmFactory newInstance(Context context){
		return new AlarmFactory(context);
	}

	private AlarmManager getAlarmManager() {
		if (mAlarmManager == null)
			mAlarmManager = (AlarmManager) mContext
					.getSystemService(Context.ALARM_SERVICE);
		return mAlarmManager;
	}
	
	public AlarmFactory createAlarms(Cursor cursor){
		while(cursor.moveToNext()){
			createAlarm(cursor);
		}
		return this;
	}
	
	public AlarmFactory createAlarm(Cursor cursor) {
		return createAlarm(new CalendarEventInstance(cursor));
	}

	public AlarmFactory cancelAlarms(Cursor cursor) {
		while (cursor.moveToNext()) {
			cancelAlarm(cursor);
		}
		return this;
	}

	public AlarmFactory cancelAlarm(Cursor cursor) {
		return cancelAlarm(new CalendarEventInstance(cursor));
	}

	public PendingIntent prepareIntent(String action, final CalendarEventInstance instance) {
		return prepareIntent(action, instance.getId());
	}

	public PendingIntent prepareIntent(String action){
		return prepareIntent(action, -1);
	}
	
	private PendingIntent prepareIntent(String action, long instanceId){
		Intent intent = new Intent(action, null, mContext, SilencerBroadcastReceiver.class);
		intent.putExtra(EXTRA_INSTANCE_ID, instanceId);
		PendingIntent startAlarmIntent = PendingIntent.getBroadcast(mContext, 0, intent, 0);
		return startAlarmIntent;
	}

	public AlarmFactory createAlarm(final CalendarEventInstance instance) {
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(mContext);
		String time = preferences.getString(SettingsFragment.KEY_PREF_LONG_EVENTS_LENGTH, SettingsFragment.DEFAULT_PREF_LONG_EVENTS_LENGTH);
		
		long maxLength =  TimeDialogPreference.getMilliseconds(time);
		long eventLength = instance.getEnd() - instance.getBegin();
		
		if(eventLength < maxLength){
			getAlarmManager().set(AlarmManager.RTC_WAKEUP, instance.getBegin() - ALARM_BUFFER, prepareIntent(ACTION_START_EVENT_SILENCE, instance));
			getAlarmManager().set(AlarmManager.RTC_WAKEUP, instance.getEnd() + ALARM_BUFFER, prepareIntent(ACTION_END_EVENT_SILENCE, instance));
		}
		
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

}
