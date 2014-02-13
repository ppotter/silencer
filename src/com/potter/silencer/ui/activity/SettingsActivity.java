package com.potter.silencer.ui.activity;


import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;

import com.potter.silencer.CalendarSyncAsyncTask;
import com.potter.silencer.manager.CalendarManager;
import com.potter.silencer.ui.fragment.SettingsFragment;

public class SettingsActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		getFragmentManager().beginTransaction()
			.replace(android.R.id.content, new SettingsFragment())
			.commit();
		new CalendarSyncAsyncTask(this).execute(CalendarSyncAsyncTask.CANCEL_CREATE_ALARMS);
	}

}
