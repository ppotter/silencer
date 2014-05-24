package com.potter.silencer.receiver;

import com.potter.silencer.CalendarSyncAsyncTask;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class OnBootBroadcastReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		new CalendarSyncAsyncTask(context).execute(CalendarSyncAsyncTask.CANCEL_CREATE_ALARMS);
	}

}
