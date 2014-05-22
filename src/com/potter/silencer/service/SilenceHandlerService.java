package com.potter.silencer.service;

import com.potter.silencer.manager.CalendarManager;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;

public class SilenceHandlerService extends Service {
	
	public static final String ACTION_CREATE_ALARM = SilenceHandlerService.class.getPackage() + ".CREATE_ALARM";
	public static final String ACTION_CREATE_ALARMS = SilenceHandlerService.class.getPackage() + ".CREATE_ALARMS";
	public static final String ACTION_CANCEL_ALARMS = SilenceHandlerService.class.getPackage() + ".CANCEL_ALARMS";
	public static final String ACTION_CANCEL_CREATE_ALARMS = SilenceHandlerService.class.getPackage() + ".CANCEL_CREATE_ALARMS";

	public static final String EXTRA_END_TIME = SilenceHandlerService.class.getPackage() + ".END_TIME";
	
	private Handler mHandler;
	private CalendarManager mCalendarManager;

	@Override
	public void onCreate() {
		super.onCreate();
		mHandler = new Handler();
		mCalendarManager = new CalendarManager(getApplicationContext());
	}
	
	@Override
	public void onStart(Intent intent, int startId) {
	    handleCommand(intent);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		handleCommand(intent);
		return START_STICKY;
	}
	
	private void handleCommand(Intent intent){
		if(ACTION_CREATE_ALARM.equals(intent.getAction())){
			mCalendarManager.createRestoreAlarmPostDelay(mHandler, intent.getLongExtra(EXTRA_END_TIME, -1));
		} else if (ACTION_CANCEL_ALARMS.equals(intent.getAction())){
			mCalendarManager.cancelAllCurrentPostDelays(mHandler);
		} else if (ACTION_CREATE_ALARMS.equals(intent.getAction())){
			mCalendarManager.createAllCurrentPostDelays(mHandler);
		} else if (ACTION_CANCEL_CREATE_ALARMS.equals(intent.getAction())){
			mCalendarManager.cancelAllCurrentPostDelays(mHandler);
			mCalendarManager.createAllCurrentPostDelays(mHandler);
		}
	}

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

}
