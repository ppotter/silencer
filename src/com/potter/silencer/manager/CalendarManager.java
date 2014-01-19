package com.potter.silencer.manager;

import java.util.Calendar;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.provider.CalendarContract;
import android.provider.CalendarContract.Instances;

import com.potter.silencer.model.CalendarEventInstance;
import com.potter.silencer.receiver.SilencerBroadcastReceiver;
import com.potter.silencer.ui.fragment.SettingsFragment;
import com.potter.silencer.ui.preference.TimeDialogPreference;

public class CalendarManager {

	public static final String ACTION_START_SILENCE = "com.potter.silencer.ACTION_START_SILENCE";
	public static final String ACTION_END_SILENCE = "com.potter.silencer.ACTION_END_SILENCE";

	public static final String EXTRA_INSTANCE_ID = "com.potter.silencer.EXTRA_INSTANCE_ID";

	private Context mContext;
	private AlarmManager mAlarmManager;

	public CalendarManager(final Context context) {
		this.mContext = context;
	}

	private AlarmManager getAlarmManager() {
		if (mAlarmManager == null)
			mAlarmManager = (AlarmManager) mContext
					.getSystemService(Context.ALARM_SERVICE);
		return mAlarmManager;
	}

	public CalendarManager createAllCurrentAlarms() {
		Cursor cursor = null;
		ContentResolver contentResolver = mContext.getContentResolver();
//		Uri uri = Uri.parse("content://com.android.calendar/instances");
		Uri uri = Instances.CONTENT_URI;
		uri = ContentUris.withAppendedId(uri, System.currentTimeMillis());//start
		
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.YEAR, 1);
		uri = ContentUris.withAppendedId(uri, calendar.getTimeInMillis());//end
		

		String selection = null;
		String[] selectionArgs = null;

		cursor = contentResolver.query(uri,
				CalendarEventInstance.EVENT_PROJECTION, selection,
				selectionArgs, null);

		createAlarms(cursor);
		return this;
	}
	
	public CalendarManager cancelAllCurrentAlarms(){
		Cursor cursor = null;
		ContentResolver contentResolver = mContext.getContentResolver();
//		Uri uri = Uri.parse("content://com.android.calendar/instances");
		Uri uri = Instances.CONTENT_URI;
		uri = ContentUris.withAppendedId(uri, System.currentTimeMillis());//start
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.YEAR, 1);
		uri = ContentUris.withAppendedId(uri, calendar.getTimeInMillis());//end
		String selection = null;
		String[] selectionArgs = null;

		cursor = contentResolver.query(uri,
				CalendarEventInstance.EVENT_PROJECTION, selection,
				selectionArgs, null);

		cancelAlarms(cursor);
		return this;
	}

	public CalendarManager createAlarms(Cursor cursor){
		while(cursor.moveToNext()){
			createAlarm(cursor);
		}
		return this;
	}
	
	public CalendarManager createAlarm(Cursor cursor) {
		return createAlarm(new CalendarEventInstance(cursor));
	}

	public CalendarManager cancelAlarms(Cursor cursor) {
		while (cursor.moveToNext()) {
			cancelAlarm(cursor);
		}
		return this;
	}

	public CalendarManager cancelAlarm(Cursor cursor) {
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

	public CalendarManager createAlarm(final CalendarEventInstance instance) {
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(mContext);
		String time = preferences.getString(SettingsFragment.KEY_PREF_LONG_EVENTS_LENGTH, SettingsFragment.DEFAULT_PREF_LONG_EVENTS_LENGTH);
		
		long maxLength =  TimeDialogPreference.getMilliseconds(time);
		long eventLength = instance.getEnd() - instance.getBegin();
		
		if(eventLength < maxLength){
			getAlarmManager().set(AlarmManager.RTC_WAKEUP, instance.getBegin(),	prepareStartIntent(instance));
			getAlarmManager().set(AlarmManager.RTC_WAKEUP, instance.getEnd(), prepareEndIntent(instance));
		}
		
		return this;
	}

	public CalendarManager cancelAlarm(final CalendarEventInstance instance) {
		getAlarmManager().cancel(prepareStartIntent(instance));
		getAlarmManager().cancel(prepareEndIntent(instance));
		return this;
	}
}
