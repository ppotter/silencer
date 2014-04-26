package com.potter.silencer.ui.picker;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

import android.app.Activity;
import android.app.NotificationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.view.KeyEvent;

import com.potter.silencer.AlarmFactory;
import com.potter.silencer.Audio;
import com.potter.silencer.ui.notification.SilencedNotificationFactory;
import com.sleepbot.datetimepicker.time.RadialPickerLayout;
import com.sleepbot.datetimepicker.time.TimePickerDialog;
import com.sleepbot.datetimepicker.time.TimePickerDialog.OnTimeSetListener;

public class SilenceTimePickerActivity extends FragmentActivity implements OnTimeSetListener {

	private static final String TIMEPICKER_TAG = "TIMEPICKER_TAG";
	
	public static final String KEY_PREF_DURATION = "com.potter.silencer.ui.activity.KEY_PREF_DURATION";
	private static final long DEFAULT_DURATION = 1000 * 60 * 60;
	
	private TimePickerDialog timePickerDialog;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	@Override
	protected void onResume() {
		super.onResume();

		long duration = PreferenceManager.getDefaultSharedPreferences(this).getLong(KEY_PREF_DURATION, DEFAULT_DURATION);
		createDialog(duration);
	}
	
	private void createDialog(final long duration){
		final Calendar calendar = Calendar.getInstance();
		
		Calendar time = Calendar.getInstance();
		time.add(Calendar.HOUR_OF_DAY, (int) TimeUnit.MILLISECONDS.toHours(duration));
		time.add(Calendar.MINUTE, (int) TimeUnit.MILLISECONDS.toMinutes(duration) % 60);
		
		
		timePickerDialog = TimePickerDialog.newInstance(this, calendar.get(Calendar.HOUR_OF_DAY) ,calendar.get(Calendar.MINUTE), false, false);
		timePickerDialog.setOnTimeSetListener(this);
		timePickerDialog.setStartTime(time.get(Calendar.HOUR_OF_DAY), time.get(Calendar.MINUTE));
		timePickerDialog.setRetainInstance(true);
		timePickerDialog.show(getSupportFragmentManager(), TIMEPICKER_TAG);
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		
		if(keyCode == KeyEvent.KEYCODE_BACK)
			finish();
		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onPause() {
		super.onPause();
		timePickerDialog.dismiss();
	}

	@Override
	public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute) {
		NotificationManager notificationManager = (NotificationManager) getSystemService(Activity.NOTIFICATION_SERVICE);
		if(hourOfDay > 0 && minute > 0) {
			
			//if an actual value as selected and the done button pressed.
			Calendar current = Calendar.getInstance(), timeSet = Calendar.getInstance();
			timeSet.set(Calendar.HOUR_OF_DAY, hourOfDay);
			timeSet.set(Calendar.MINUTE, minute);
			if(timeSet.getTimeInMillis() < current.getTimeInMillis())
				timeSet.add(Calendar.DAY_OF_YEAR, 1);

			AlarmFactory.newInstance(this).createEndAlarm(timeSet.getTimeInMillis());
			long duration = timeSet.getTimeInMillis() - current.getTimeInMillis();
			PreferenceManager.getDefaultSharedPreferences(this).edit().putLong(KEY_PREF_DURATION, duration).commit();
			notificationManager.notify(SilencedNotificationFactory.NOTIFICATION_ID, SilencedNotificationFactory.getInstance().get(SilenceTimePickerActivity.this, timeSet.getTimeInMillis()));
		} else if(!Audio.isVolumnSilenced(this)){
			Audio.mute(this);
			notificationManager.notify(SilencedNotificationFactory.NOTIFICATION_ID, SilencedNotificationFactory.getInstance().get(SilenceTimePickerActivity.this, SilencedNotificationFactory.INDEFINITE_END_TIME));
		}

		finish();
	}
}
