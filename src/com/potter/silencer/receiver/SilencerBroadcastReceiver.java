package com.potter.silencer.receiver;

import com.potter.silencer.Audio;
import com.potter.silencer.manager.CalendarManager;
import com.potter.silencer.ui.fragment.SettingsFragment;
import com.potter.silencer.ui.preference.VolumeNumberPickerPreference;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

public class SilencerBroadcastReceiver extends BroadcastReceiver{

	@Override
	public void onReceive(Context context, Intent intent) {
		Log.i(this.getClass().getCanonicalName(), "Received calendar event");
		if(intent.getAction().equals(CalendarManager.ACTION_START_SILENCE)){
			Audio.mute(context);
		} else if ( intent.getAction().equals(CalendarManager.ACTION_END_SILENCE)){
			float value = VolumeNumberPickerPreference.getFloatValue(
					PreferenceManager.getDefaultSharedPreferences(context)
						.getInt(SettingsFragment.KEY_PREF_VOLUME_RESTORE_LEVEL, 
								SettingsFragment.DEFAULT_VOLUME_RESTORE_LEVEL));
			Audio.restore(context, value);
		} else {
			Log.i(this.getClass().getCanonicalName(), "Unknown intent action: " + intent.getAction());
		}
		
	}

}
