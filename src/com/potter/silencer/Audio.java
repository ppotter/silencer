package com.potter.silencer;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.widget.Toast;

import com.potter.silencer.ui.preference.VolumeNumberPickerPreference;
import com.potter.silencer.ui.settings.SettingsFragment;

public class Audio {

	public static volatile boolean recentlySilencedByApp = false;
	
	private static int DELAY = 2000;
	private static Handler sHandler = new Handler();
	
	
	public static boolean wasRecentlySilencedByApp(){
		return recentlySilencedByApp;
	}
	
	private static void resetFlag(){
		sHandler.postDelayed(new Runnable() {
			
			@Override
			public void run() {
				recentlySilencedByApp = false;
			}
		}, DELAY);
	}
	
	public static void mute(final Context context)
	{
		recentlySilencedByApp = true;
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
		AudioManager audioManager = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
		if(preferences.getBoolean(SettingsFragment.KEY_PREF_MEDIA, true)) audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 0, 0);
		if(preferences.getBoolean(SettingsFragment.KEY_PREF_NOTIFICATIONS, true)) audioManager.setStreamVolume(AudioManager.STREAM_NOTIFICATION, 0, 0);
		if(preferences.getBoolean(SettingsFragment.KEY_PREF_RINGER, true)) audioManager.setStreamVolume(AudioManager.STREAM_RING, 0, 0);
		if(preferences.getBoolean(SettingsFragment.KEY_PREF_VIBRATE, false)) audioManager.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
		Toast.makeText(context, R.string.silencing_ringer, Toast.LENGTH_SHORT).show();
		resetFlag();
	}
	
	public static void restore(final Context context){
		float value = VolumeNumberPickerPreference.getFloatValue(
				PreferenceManager.getDefaultSharedPreferences(context)
					.getInt(SettingsFragment.KEY_PREF_VOLUME_RESTORE_LEVEL, 
							SettingsFragment.DEFAULT_VOLUME_RESTORE_LEVEL));
		Audio.restore(context, value);
	}
	
	public static void restore(final Context context, final float percentage)
	{
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
		AudioManager audioManager = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
		if(preferences.getBoolean(SettingsFragment.KEY_PREF_MEDIA, true)) audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, (int)(audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC) * percentage), 0);
		if(preferences.getBoolean(SettingsFragment.KEY_PREF_NOTIFICATIONS, true)) audioManager.setStreamVolume(AudioManager.STREAM_NOTIFICATION, (int)(audioManager.getStreamMaxVolume(AudioManager.STREAM_NOTIFICATION) * percentage), 0);
		if(preferences.getBoolean(SettingsFragment.KEY_PREF_RINGER, true)) audioManager.setStreamVolume(AudioManager.STREAM_RING, (int)(audioManager.getStreamMaxVolume(AudioManager.STREAM_RING) * percentage), 0);
		audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
		Toast.makeText(context, R.string.restoring_ringer, Toast.LENGTH_SHORT).show();
	}
	
	public static boolean isVolumnSilenced(final Context context){
		AudioManager audioManager = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
		return AudioManager.RINGER_MODE_NORMAL != audioManager.getRingerMode();
	}
}
