package com.potter.silencer.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.potter.silencer.CalendarSyncAsyncTask;
import com.potter.silencer.service.SilenceHandlerService;

public class CalendarChangedBroadcastReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
//		if(intent.getAction().equals("event inserted")){//TODO
//			
//		} else if(intent.getAction().equals("event changed")){//TODO
//			
//		} else if(intent.getAction().equals("event deleted")){//TODO
//			
//		} else {//TODO
//			
//		}
//		new CalendarSyncAsyncTask(context).execute(CalendarSyncAsyncTask.CANCEL_CREATE_ALARMS);
		context.startService(new Intent(SilenceHandlerService.ACTION_CANCEL_CREATE_ALARMS, null, context, SilenceHandlerService.class));
	}

}
