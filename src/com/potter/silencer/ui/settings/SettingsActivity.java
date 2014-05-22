package com.potter.silencer.ui.settings;


import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;

import com.potter.silencer.CalendarSyncAsyncTask;
import com.potter.silencer.service.SilenceHandlerService;

public class SettingsActivity extends Activity {
	
	private static final String PREF_KEY_INITIALIZED_ALARMS = SettingsActivity.class.getPackage() + ".PREF_KEY_INITIALIZED_ALARMS";
	private static final boolean DEFAULT_INITIALIZED_ALARMS = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		getFragmentManager().beginTransaction()
			.replace(android.R.id.content, new SettingsFragment())
			.commit();
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(SettingsActivity.this);
		if(!preferences.getBoolean(PREF_KEY_INITIALIZED_ALARMS, DEFAULT_INITIALIZED_ALARMS)){
//			new CalendarSyncAsyncTask(this).execute(CalendarSyncAsyncTask.CANCEL_CREATE_ALARMS);
			SettingsActivity.this.startService(new Intent(SilenceHandlerService.ACTION_CANCEL_CREATE_ALARMS, null, SettingsActivity.this, SilenceHandlerService.class));
			preferences.edit().putBoolean(PREF_KEY_INITIALIZED_ALARMS, true).commit();
		}
	}

}
