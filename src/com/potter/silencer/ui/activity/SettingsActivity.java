package com.potter.silencer.ui.activity;


import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;

import com.potter.silencer.manager.CalendarManager;
import com.potter.silencer.ui.fragment.SettingsFragment;

public class SettingsActivity extends Activity {

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
