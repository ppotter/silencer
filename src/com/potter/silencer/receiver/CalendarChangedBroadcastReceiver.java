package com.potter.silencer.receiver;

import com.potter.silencer.manager.CalendarManager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class CalendarChangedBroadcastReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
//		if(intent.getAction().equals("event inserted")){//TODO
//			
//		} else if(intent.getAction().equals("event changed")){//TODO
//			
//		} else if(intent.getAction().equals("event deleted")){//TODO
//			
//		} else {
//			
//		}
		new CalendarManager(context).cancelAllCurrentAlarms().createAllCurrentAlarms();
	}

}
