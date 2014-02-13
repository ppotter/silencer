package com.potter.silencer.ui.notification;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import com.potter.silencer.AlarmFactory;
import com.potter.silencer.R;
import com.potter.silencer.ui.activity.RestoreActivity;

public class SilencedNotificationFactory {
	
	public static final int NOTIFICATION_ID = 15764239;

	public static Notification newInstance(Context context, int hour, int minute){
		
		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
		.setSmallIcon(R.drawable.ic_launcher)
		.setContentTitle(context.getString(R.string.notification_silenced_until, hour + ":" + minute))
		.setContentText(context.getString(R.string.notification_click_to_restore))
		.setContentIntent(PendingIntent.getActivity(context, 0, new Intent(context, RestoreActivity.class), 0));
		Notification notification = mBuilder.build();
		notification.flags = Notification.FLAG_AUTO_CANCEL;
		return mBuilder.build();
		
	}
	
	public static void cancelNotification(Context context){
		NotificationManager notifiationManager = (NotificationManager) context.getSystemService(Activity.NOTIFICATION_SERVICE);
		notifiationManager.cancel(NOTIFICATION_ID);
	}
}
