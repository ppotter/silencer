package com.potter.silencer.activity;


import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;

import com.potter.silencer.fragment.SettingsFragment;
import com.potter.silencer.manager.CalendarManager;

public class SettingsActivity extends Activity {
	
	public static final String KEY_PREF_LONG_EVENTS = "pref_key_long_events";
	public static final String KEY_PREF_VIBRATE = "pref_key_vibrate";
	public static final String KEY_PREF_NOTIFICATIONS = "pref_key_notifications";
	public static final String KEY_PREF_MEDIA = "pref_key_media";
	public static final String KEY_PREF_RINGER = "pref_key_ringer";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		getFragmentManager().beginTransaction()
			.replace(android.R.id.content, new SettingsFragment())
			.commit();
		new CalendarLoaderTask(this).execute();

	}
	
	private class CalendarLoaderTask extends AsyncTask<Void, Integer, Void>{

		private Context context;
		
		public CalendarLoaderTask(Context context){
			this.context = context;
		}
		
		@Override
		protected Void doInBackground(Void... params) {
			CalendarManager cm = new CalendarManager(context);
			cm.cancelAllCurrentAlarms().createAllCurrentAlarms();
			return null;
		}
	}

}
