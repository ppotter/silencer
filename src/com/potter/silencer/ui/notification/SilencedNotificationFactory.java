package com.potter.silencer.ui.notification;

import java.util.Calendar;
import java.util.Date;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;

import com.potter.silencer.AlarmFactory;
import com.potter.silencer.R;
import com.potter.silencer.receiver.AlarmSilencerBroadcastReceiver;
import com.potter.silencer.ui.settings.SettingsFragment;


/**
 * Singleton factory for creating silenced notifications.
 */
public class SilencedNotificationFactory {
	
	public static final int NOTIFICATION_ID = 15764239;
	
	public static final long INDEFINITE_END_TIME = -1;
	public static final long NOT_SET = 0;
	
	private static long mCurrentEndTime = NOT_SET;
	
	private static SilencedNotificationFactory instance;
	
	public static SilencedNotificationFactory getInstance(){
		if(instance == null){
			instance = new SilencedNotificationFactory();
		}
		return instance;
	}
	
	/**
	 * returns a notification populated based on the endTime provided.
	 * @param context
	 * @param endTime
	 * @return
	 */
	public Notification get(Context context, long endTime){
		if(endTime != INDEFINITE_END_TIME){
			if(endTime > mCurrentEndTime){
				return getNotificationWithEndTime(context, endTime);
			}
			return getNotificationWithEndTime(context, mCurrentEndTime);
		}
		return get(context, R.string.notification_click_to_restore);
	}
	
	private Notification getNotificationWithEndTime(Context context, long endTime){
		mCurrentEndTime = endTime;
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date(endTime));
		boolean isMilitary = isMilitaryPrefered(context);
		int hour = (isMilitary) ? calendar.get(Calendar.HOUR_OF_DAY) : calendar.get(Calendar.HOUR);
		int minute = calendar.get(Calendar.MINUTE);
		int amPm = calendar.get(Calendar.AM_PM);
		int stringId = (isMilitary) ? R.string.notification_silenced_until_military : R.string.notification_silenced_until;
		return get(context, hour, minute, amPm, stringId);
	}

	/**
	 * returns a notification populated with the end time.
	 * @param context
	 * @param hour
	 * @param minute
	 * @param amPm
	 * @param stringId
	 * @return
	 */
	private Notification get(Context context, int hour, int minute, int amPm, int stringId){
		String formattedMinute = String.valueOf(((minute < 10) ? "0" + minute : minute));
		String formattedAmPm = context.getResources().getString((amPm == Calendar.AM) ? R.string.time_am : R.string.time_pm);
		NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
		.setSmallIcon(R.drawable.ic_launcher_lite)
		.setContentIntent(AlarmFactory.newInstance(context).prepareIntent(AlarmSilencerBroadcastReceiver.ACTION_END_TEMPORARY_SILENCE))
		.setContentText(context.getString(R.string.notification_click_to_restore))
		.setContentTitle(context.getString(stringId, hour, formattedMinute, formattedAmPm));
		Notification notification = builder.build();
		notification.flags = Notification.FLAG_AUTO_CANCEL;
		return builder.build();
	}
	
	/**
	 * returns indefinite or unknown end time notification.
	 * @param context
	 * @param stringId the String resource id to use.
	 * @return
	 */
	private Notification get(Context context, int stringId) {
		mCurrentEndTime = INDEFINITE_END_TIME;
		NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
		.setSmallIcon(R.drawable.ic_launcher_lite)
		.setContentIntent(AlarmFactory.newInstance(context)
		.prepareIntent(AlarmSilencerBroadcastReceiver.ACTION_END_TEMPORARY_SILENCE))
		.setContentTitle(context.getString(stringId));
		Notification notification = builder.build();
		notification.flags = Notification.FLAG_AUTO_CANCEL;
		return builder.build();
	}
	
	private boolean isMilitaryPrefered(final Context context){
		return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(SettingsFragment.KEY_PREF_MILITARY, false);
	}
	
	public void cancelNotification(Context context){
		mCurrentEndTime = NOT_SET;
		NotificationManager notifiationManager = (NotificationManager) context.getSystemService(Activity.NOTIFICATION_SERVICE);
		notifiationManager.cancel(NOTIFICATION_ID);
	}
}
