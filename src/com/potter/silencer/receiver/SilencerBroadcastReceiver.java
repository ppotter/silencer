package com.potter.silencer.receiver;

import com.potter.silencer.Audio;
import com.potter.silencer.manager.CalendarManager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class SilencerBroadcastReceiver extends BroadcastReceiver{

	@Override
	public void onReceive(Context context, Intent intent) {
		Log.i(this.getClass().getCanonicalName(), "Received calendar event");
		if(intent.getAction().equals(CalendarManager.ACTION_START_SILENCE)){
			Audio.mute(context);
		} else if ( intent.getAction().equals(CalendarManager.ACTION_END_SILENCE)){
			Audio.restore(context, 0.8f);
		} else {
			Log.i(this.getClass().getCanonicalName(), "Unknown intent action: " + intent.getAction());
		}
		
	}

}
