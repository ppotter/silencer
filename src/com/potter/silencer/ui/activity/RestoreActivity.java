package com.potter.silencer.ui.activity;

import android.app.Activity;
import android.os.Bundle;

import com.potter.silencer.Audio;
import com.potter.silencer.ui.notification.SilencedNotificationFactory;

public class RestoreActivity extends Activity{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Audio.restore(this);
		SilencedNotificationFactory.cancelNotification(this);
		finish();
	}
	
}
