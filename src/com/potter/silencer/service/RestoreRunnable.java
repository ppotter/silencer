package com.potter.silencer.service;

import com.potter.silencer.receiver.AlarmSilencerBroadcastReceiver;

import android.content.Context;
import android.content.Intent;

public class RestoreRunnable implements Runnable {

	private final Context mContext;
	
	public RestoreRunnable(final Context context){
		mContext = context;
	}
	
	@Override
	public void run() {
		mContext.sendBroadcast(new Intent(AlarmSilencerBroadcastReceiver.ACTION_END_EVENT_SILENCE, null, mContext, AlarmSilencerBroadcastReceiver.class));
	}
}
