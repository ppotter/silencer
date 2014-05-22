package com.potter.silencer.service;

import com.potter.silencer.receiver.AlarmSilencerBroadcastReceiver;

import android.content.Context;
import android.content.Intent;

public class SilenceRunnable implements Runnable {

	private final Context mContext;
	private final long mEndTime;
	
	public SilenceRunnable(final Context context){
		this(context, -1);
	}
	
	public SilenceRunnable(final Context context, final long endTime){
		mContext = context;
		mEndTime = endTime;
	}
	
	@Override
	public void run() {
		Intent intent = new Intent(AlarmSilencerBroadcastReceiver.ACTION_START_EVENT_SILENCE, null, mContext, AlarmSilencerBroadcastReceiver.class);
		intent.putExtra(AlarmSilencerBroadcastReceiver.EXTRA_END_TIME, mEndTime);
		mContext.sendBroadcast(intent);
	}
}
