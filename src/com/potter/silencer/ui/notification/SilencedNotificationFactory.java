package com.potter.silencer.ui.notification;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;

import com.potter.silencer.AlarmFactory;
import com.potter.silencer.R;
import com.potter.silencer.ui.fragment.SettingsFragment;

public class SilencedNotificationFactory {
	
	public static final int NOTIFICATION_ID = 15764239;

	public static Notification newInstance(Context context, int hour, int minute){
		NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
		.setSmallIcon(R.drawable.ic_launcher)
		.setContentText(context.getString(R.string.notification_click_to_restore))
		.setContentIntent(AlarmFactory.newInstance(context).prepareEndIntent());
		if(hour > 0 || minute > 0){
			String formattedMinute = String.valueOf(((minute < 10) ? "0" + minute : minute));
			if(isMilitaryPrefered(context)){
				builder.setContentTitle(context.getString(R.string.notification_silenced_until_military, hour, formattedMinute));
			} else {
				int formattedHour =  (hour % 12);
				String amPm = (hour < 12) ? context.getString(R.string.time_am) : context.getString(R.string.time_pm);
				builder.setContentTitle(context.getString(R.string.notification_silenced_until, formattedHour, formattedMinute, amPm));
			}
		} else {
			builder.setContentTitle(context.getString(R.string.notification_silenced_indefinitely));
		}
		Notification notification = builder.build();
		notification.flags = Notification.FLAG_AUTO_CANCEL;
		return builder.build();
	}
	
	private static boolean isMilitaryPrefered(final Context context){
		return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(SettingsFragment.KEY_PREF_MILITARY, false);
	}
	
	public static void cancelNotification(Context context){
		NotificationManager notifiationManager = (NotificationManager) context.getSystemService(Activity.NOTIFICATION_SERVICE);
		notifiationManager.cancel(NOTIFICATION_ID);
	}
}
