package com.potter.silencer.ui.activity;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

import android.app.Activity;
import android.app.NotificationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NotificationCompat;
import android.view.KeyEvent;
import android.widget.Toast;

import com.potter.silencer.AlarmFactory;
import com.potter.silencer.R;
import com.potter.silencer.ui.notification.SilencedNotificationFactory;
import com.sleepbot.datetimepicker.time.RadialPickerLayout;
import com.sleepbot.datetimepicker.time.TimePickerDialog;
import com.sleepbot.datetimepicker.time.TimePickerDialog.OnTimeSetListener;

public class NotifySilenceActivity extends FragmentActivity implements OnTimeSetListener {

	private static final String TIMEPICKER_TAG = "TIMEPICKER_TAG";
	
	public static final String KEY_PREF_DURATION = "com.potter.silencer.ui.activity.KEY_PREF_DURATION";
	private static final long DEFAULT_DURATION = 1000 * 60 * 60;
	
	private TimePickerDialog timePickerDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		final Calendar calendar = Calendar.getInstance();

		long duration = PreferenceManager.getDefaultSharedPreferences(this).getLong(KEY_PREF_DURATION, DEFAULT_DURATION);
		Calendar time = Calendar.getInstance();
		time.add(Calendar.HOUR_OF_DAY, (int) TimeUnit.MILLISECONDS.toHours(duration));
		time.add(Calendar.MINUTE, (int) TimeUnit.MILLISECONDS.toMinutes(duration));
		
		timePickerDialog = TimePickerDialog.newInstance(this, calendar.get(Calendar.HOUR_OF_DAY) ,calendar.get(Calendar.MINUTE), false, false);
		timePickerDialog.setOnTimeSetListener(this);
		timePickerDialog.setStartTime( time.get(Calendar.HOUR_OF_DAY), time.get(Calendar.MINUTE));
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
		Toast.makeText(this, "new time:" + hourOfDay + "-" + minute, Toast.LENGTH_LONG).show();
		Calendar current = Calendar.getInstance(), timeSet = Calendar.getInstance();
		timeSet.set(Calendar.HOUR_OF_DAY, hourOfDay);
		timeSet.set(Calendar.MINUTE, minute);
		if(timeSet.getTimeInMillis() < current.getTimeInMillis())
			timeSet.add(Calendar.DAY_OF_YEAR, 1);
		long duration = AlarmFactory.timeToMilliseconds(hourOfDay, minute);
//		long duration = timeSet.getTimeInMillis() - current.getTimeInMillis();
		PreferenceManager.getDefaultSharedPreferences(this).edit().putLong(KEY_PREF_DURATION, duration).commit();
		AlarmFactory.newInstance(this).createEndAlarm(System.currentTimeMillis() + duration);

		NotificationManager notificationManager = (NotificationManager) getSystemService(Activity.NOTIFICATION_SERVICE);
		notificationManager.notify(SilencedNotificationFactory.NOTIFICATION_ID, SilencedNotificationFactory.newInstance(this, hourOfDay, minute));
		finish();

	}
}
