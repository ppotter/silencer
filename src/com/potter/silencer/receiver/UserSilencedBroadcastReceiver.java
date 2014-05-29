package com.potter.silencer.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;

import com.potter.silencer.Audio;
import com.potter.silencer.ui.notification.SilencedNotificationFactory;
import com.potter.silencer.ui.picker.SilenceTimePickerActivity;

public class UserSilencedBroadcastReceiver extends BroadcastReceiver {
	
	@Override
	public void onReceive(Context context, Intent intent) {
		AudioManager audioManager = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
		if((audioManager.getRingerMode() == AudioManager.RINGER_MODE_SILENT || audioManager.getRingerMode() == AudioManager.RINGER_MODE_VIBRATE) && !Audio.wasRecentlySilencedByApp()){
			Intent i = new Intent(context, SilenceTimePickerActivity.class);
			i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(i);
			AlarmSilencerBroadcastReceiver.incrementSilenceCount(context);
		} else if(audioManager.getRingerMode() == AudioManager.RINGER_MODE_NORMAL){
			SilencedNotificationFactory.getInstance().cancelNotification(context);
			AlarmSilencerBroadcastReceiver.clearSilenceCount(context);
		}
	}
}
