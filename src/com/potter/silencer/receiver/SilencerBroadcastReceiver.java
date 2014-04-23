package com.potter.silencer.receiver;

import java.util.Calendar;
import java.util.Date;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.potter.silencer.AlarmFactory;
import com.potter.silencer.Audio;
import com.potter.silencer.ui.notification.SilencedNotificationFactory;

public class SilencerBroadcastReceiver extends BroadcastReceiver{

	@Override
	public void onReceive(Context context, Intent intent) {
		Log.i(this.getClass().getCanonicalName(), "Received silencer event");
		
		if(intent.getAction().equals(AlarmFactory.ACTION_START_EVENT_SILENCE) || intent.getAction().equals(AlarmFactory.ACTION_START_TEMPORARY_SILENCE)){
			Log.i(this.getClass().getCanonicalName(), "Received start silence event");
			Audio.mute(context);
			NotificationManager notifiationManager = (NotificationManager) context.getSystemService(Activity.NOTIFICATION_SERVICE);
			if(intent.hasExtra(AlarmFactory.EXTRA_END_TIME)){
				Calendar calendar = Calendar.getInstance();
				calendar.setTime(new Date(intent.getLongExtra(AlarmFactory.EXTRA_END_TIME, new Date().getTime())));
				notifiationManager.notify(SilencedNotificationFactory.NOTIFICATION_ID, SilencedNotificationFactory.newInstance(context, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE)));
			} else {
				//TODO Better handling for potential misses on end time.
				notifiationManager.notify(SilencedNotificationFactory.NOTIFICATION_ID, SilencedNotificationFactory.newInstance(context, -1, -1));
			}
		} else if (intent.getAction().equals(AlarmFactory.ACTION_END_EVENT_SILENCE) || intent.getAction().equals(AlarmFactory.ACTION_END_TEMPORARY_SILENCE)){
			Log.i(this.getClass().getCanonicalName(), "Received end silence event");
			Audio.restore(context);
			SilencedNotificationFactory.cancelNotification(context);
		} else {
			Log.i(this.getClass().getCanonicalName(), String.format("Unknown intent action: %", intent.getAction()));
		}
		
	}

}
