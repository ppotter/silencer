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

import com.potter.silencer.AlarmFactory;
import com.potter.silencer.model.CalendarEventInstance;
import com.potter.silencer.receiver.SilencerBroadcastReceiver;
import com.potter.silencer.ui.fragment.SettingsFragment;
import com.potter.silencer.ui.preference.TimeDialogPreference;

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

		getAlarmFactory().createAlarms(cursor);
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

		getAlarmFactory().cancelAlarms(cursor);
		return this;
	}
}
