package com.potter.silencer.manager;

import java.util.Calendar;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.provider.CalendarContract.Instances;

import com.potter.silencer.AlarmFactory;
import com.potter.silencer.model.CalendarEventInstance;
import com.potter.silencer.ui.fragment.SettingsFragment;

public class CalendarManager {
	
	private final static long ONE_DAY = 60 * 60 * 24 * 1000;

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
			
			
			String selection = null;
			String[] selectionArgs = null;
			
			cursor = contentResolver.query(uri,
					CalendarEventInstance.EVENT_PROJECTION, selection,
					selectionArgs, null);
			
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
