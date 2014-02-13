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
	
	public static final String ACTION_START_SILENCE = "com.potter.silencer.ACTION_START_SILENCE";
	public static final String ACTION_END_SILENCE = "com.potter.silencer.ACTION_END_SILENCE";

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

	public PendingIntent prepareStartIntent(final CalendarEventInstance instance) {
		Intent startIntent = new Intent(ACTION_START_SILENCE, null, mContext, SilencerBroadcastReceiver.class);
		startIntent.putExtra(EXTRA_INSTANCE_ID, instance.getId());
		PendingIntent startAlarmIntent = PendingIntent.getBroadcast(mContext, 0, startIntent, 0);
		return startAlarmIntent;
	}

	public PendingIntent prepareEndIntent(final CalendarEventInstance instance) {
		Intent endIntent = new Intent(ACTION_END_SILENCE, null, mContext, SilencerBroadcastReceiver.class);
		endIntent.putExtra(EXTRA_INSTANCE_ID, instance.getId());
		PendingIntent endAlarmIntent = PendingIntent.getBroadcast(mContext, 0, endIntent, 0);
		return endAlarmIntent;
	}

	public PendingIntent prepareStartIntent() {
		Intent startIntent = new Intent(ACTION_START_SILENCE, null, mContext, SilencerBroadcastReceiver.class);
		startIntent.putExtra(EXTRA_INSTANCE_ID, -1);
		PendingIntent startAlarmIntent = PendingIntent.getBroadcast(mContext, 0, startIntent, 0);
		return startAlarmIntent;
	}
	
	public PendingIntent prepareEndIntent(){
		Intent endIntent = new Intent(ACTION_END_SILENCE, null, mContext, SilencerBroadcastReceiver.class);
		endIntent.putExtra(EXTRA_INSTANCE_ID, -1);
		PendingIntent endAlarmIntent = PendingIntent.getBroadcast(mContext, 0, endIntent, 0);
		return endAlarmIntent;
	}

	public AlarmFactory createAlarm(final CalendarEventInstance instance) {
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(mContext);
		String time = preferences.getString(SettingsFragment.KEY_PREF_LONG_EVENTS_LENGTH, SettingsFragment.DEFAULT_PREF_LONG_EVENTS_LENGTH);
		
		long maxLength =  TimeDialogPreference.getMilliseconds(time);
		long eventLength = instance.getEnd() - instance.getBegin();
		
		if(eventLength < maxLength){
			getAlarmManager().set(AlarmManager.RTC_WAKEUP, instance.getBegin() - ALARM_BUFFER, prepareStartIntent(instance));
			getAlarmManager().set(AlarmManager.RTC_WAKEUP, instance.getEnd() + ALARM_BUFFER, prepareEndIntent(instance));
		}
		
		return this;
	}
	
	public AlarmFactory createBeginAlarm(long milliseconds){
		getAlarmManager().set(AlarmManager.RTC_WAKEUP, milliseconds - ALARM_BUFFER, prepareStartIntent());
		return this;
	}
	
	public AlarmFactory createEndAlarm(long milliseconds){
		getAlarmManager().set(AlarmManager.RTC_WAKEUP, milliseconds + ALARM_BUFFER, prepareEndIntent());
		return this;
	}
	
	public static long timeToMilliseconds(int hours, int minutes){
		return ((hours * 60) + minutes) * 60 * 1000;
	}
	
	public AlarmFactory cancelAlarm(final CalendarEventInstance instance) {
		getAlarmManager().cancel(prepareStartIntent(instance));
		getAlarmManager().cancel(prepareEndIntent(instance));
		return this;
	}

}
