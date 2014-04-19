package com.potter.silencer.receiver;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

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
		Log.i(this.getClass().getCanonicalName(), "Received calendar event");
		if(intent.getAction().equals(AlarmFactory.ACTION_START_SILENCE)){
			Audio.mute(context);
			NotificationManager notifiationManager = (NotificationManager) context.getSystemService(Activity.NOTIFICATION_SERVICE);
			Calendar calendar = GregorianCalendar.getInstance(); 
			calendar.setTime(new Date());  
			notifiationManager.notify(SilencedNotificationFactory.NOTIFICATION_ID, SilencedNotificationFactory.newInstance(context, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.HOUR)));
		} else if (intent.getAction().equals(AlarmFactory.ACTION_END_SILENCE)){
			Audio.restore(context);
			SilencedNotificationFactory.cancelNotification(context);
		} else {
			Log.i(this.getClass().getCanonicalName(), "Unknown intent action: " + intent.getAction());
		}
		
	}

}
