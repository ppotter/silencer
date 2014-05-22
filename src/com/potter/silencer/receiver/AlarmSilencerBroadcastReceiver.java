package com.potter.silencer.receiver;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.potter.silencer.Audio;
import com.potter.silencer.ui.notification.SilencedNotificationFactory;

public class AlarmSilencerBroadcastReceiver extends BroadcastReceiver{
	
	
	public static final String ACTION_START_EVENT_SILENCE = AlarmSilencerBroadcastReceiver.class.getPackage() + ".ACTION_START_EVENT_SILENCE";
	public static final String ACTION_START_TEMPORARY_SILENCE = AlarmSilencerBroadcastReceiver.class.getPackage() + ".ACTION_START_TEMPORARY_SILENCE";
	public static final String ACTION_END_EVENT_SILENCE = AlarmSilencerBroadcastReceiver.class.getPackage() + ".ACTION_END_EVENT_SILENCE";
	public static final String ACTION_END_TEMPORARY_SILENCE = AlarmSilencerBroadcastReceiver.class.getPackage() + ".ACTION_END_TEMPORARY_SILENCE";
	public static final String ACTION_END_SILENCE_ABSOLUTE = AlarmSilencerBroadcastReceiver.class.getPackage() + ".ACTION_END_SILENCE_ABSOLUTE";

	public static final String EXTRA_INSTANCE_ID = AlarmSilencerBroadcastReceiver.class.getPackage() + ".EXTRA_INSTANCE_ID";
	public static final String EXTRA_END_TIME = AlarmSilencerBroadcastReceiver.class.getPackage() + ".EXTRA_END_TIME";

	
	/**
	 * Counter used to determine if volume should be restored, 
	 * if multiple events have called to silence volume, 
	 * volume wont be restored until all events have called to restore it.
	 */
	public static int silenceCount = 0;

	@Override
	public void onReceive(Context context, Intent intent) {
		Log.i(this.getClass().getCanonicalName(), "Received Volume changed event");
		
		if(intent.getAction().equals(AlarmSilencerBroadcastReceiver.ACTION_START_EVENT_SILENCE) || intent.getAction().equals(AlarmSilencerBroadcastReceiver.ACTION_START_TEMPORARY_SILENCE)){
			Log.i(this.getClass().getCanonicalName(), "Received start silence event");
			silenceCount++;
			NotificationManager notifiationManager = (NotificationManager) context.getSystemService(Activity.NOTIFICATION_SERVICE);
			notifiationManager.notify(SilencedNotificationFactory.NOTIFICATION_ID, SilencedNotificationFactory.getInstance().get(context, intent.getLongExtra(AlarmSilencerBroadcastReceiver.EXTRA_END_TIME, SilencedNotificationFactory.INDEFINITE_END_TIME)));
			if(!Audio.isVolumnSilenced(context)){
				Audio.mute(context);
			}
		} else if (intent.getAction().equals(AlarmSilencerBroadcastReceiver.ACTION_END_EVENT_SILENCE) || intent.getAction().equals(AlarmSilencerBroadcastReceiver.ACTION_END_TEMPORARY_SILENCE)){
			Log.i(this.getClass().getCanonicalName(), "Received end silence event");
			silenceCount--;
			SilencedNotificationFactory.getInstance().cancelNotification(context);
			if(Audio.isVolumnSilenced(context) && silenceCount < 1){
				Audio.restore(context);
			}
		} else {
			Log.i(this.getClass().getCanonicalName(), String.format("Unknown intent action: %", intent.getAction()));
		}
		
	}

}
