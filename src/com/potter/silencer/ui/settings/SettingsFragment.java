package com.potter.silencer.ui.settings;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;

import com.potter.silencer.CalendarSyncAsyncTask;
import com.potter.silencer.R;
import com.potter.silencer.ui.preference.VolumeNumberPickerPreference;

public class SettingsFragment extends PreferenceFragment implements OnSharedPreferenceChangeListener{

	
	public static final String KEY_PREF_LONG_EVENTS_ENABLED = "pref_key_long_events_enabled";
	public static final String KEY_PREF_SILENCE_ALL_DAY_EVENTS = "pref_key_silence_all_day_events";
	public static final String KEY_PREF_SILENCE_CALENDAR_EVENT = "pref_key_silence_calendar_event";
	public static final String KEY_PREF_LONG_EVENTS_LENGTH = "pref_key_long_events_length";
	public static final String KEY_PREF_VIBRATE = "pref_key_vibrate";
	public static final String KEY_PREF_NOTIFICATIONS = "pref_key_notifications";
	public static final String KEY_PREF_MEDIA = "pref_key_media";
	public static final String KEY_PREF_RINGER = "pref_key_ringer";
	public static final String KEY_PREF_VOLUME_RESTORE_LEVEL = "pref_key_volume_restore_level";
	public static final String KEY_PREF_MILITARY = "pref_key_military";
	
	public static final String DEFAULT_PREF_LONG_EVENTS_LENGTH = "23:00";
	public static final Integer DEFAULT_VOLUME_RESTORE_LEVEL = 8;
	public static final Boolean DEFAULT_LONG_EVENTS_ENABLED = true;
	public static final Boolean DEFAULT_SILENCE_ALL_DAY_EVENTS_ENABLED = true;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		addPreferencesFromResource(R.xml.preferences);
		
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
		setVolumeRestoreLevelSummary(sharedPreferences.getInt(KEY_PREF_VOLUME_RESTORE_LEVEL, DEFAULT_VOLUME_RESTORE_LEVEL));
		setPreferenceSummary(KEY_PREF_LONG_EVENTS_LENGTH, sharedPreferences.getString(KEY_PREF_LONG_EVENTS_LENGTH, DEFAULT_PREF_LONG_EVENTS_LENGTH));
		setPreferenceEnabled(sharedPreferences, KEY_PREF_LONG_EVENTS_LENGTH, KEY_PREF_LONG_EVENTS_ENABLED);
		setPreferenceEnabled(sharedPreferences, KEY_PREF_SILENCE_ALL_DAY_EVENTS, KEY_PREF_SILENCE_CALENDAR_EVENT);
		setPreferenceEnabled(sharedPreferences, KEY_PREF_LONG_EVENTS_ENABLED, KEY_PREF_SILENCE_CALENDAR_EVENT);
		setPreferenceEnabled(sharedPreferences, KEY_PREF_LONG_EVENTS_LENGTH, KEY_PREF_SILENCE_CALENDAR_EVENT);
	}

	
	@Override
	public void onResume() {
	    super.onResume();
		PreferenceManager.getDefaultSharedPreferences(getActivity())
	            .registerOnSharedPreferenceChangeListener(this);
	}

	@Override
	public void onPause() {
	    super.onPause();
		PreferenceManager.getDefaultSharedPreferences(getActivity())
	            .unregisterOnSharedPreferenceChangeListener(this);
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
		if(key.equals(KEY_PREF_LONG_EVENTS_ENABLED) || key.equals(KEY_PREF_LONG_EVENTS_LENGTH)) {
			setPreferenceEnabled(sharedPreferences, KEY_PREF_LONG_EVENTS_LENGTH, KEY_PREF_LONG_EVENTS_ENABLED);
			setPreferenceSummary(KEY_PREF_LONG_EVENTS_LENGTH, sharedPreferences.getString(KEY_PREF_LONG_EVENTS_LENGTH, DEFAULT_PREF_LONG_EVENTS_LENGTH));
		} else if(key.equals(KEY_PREF_VOLUME_RESTORE_LEVEL)) {
			setVolumeRestoreLevelSummary(sharedPreferences.getInt(KEY_PREF_VOLUME_RESTORE_LEVEL, 8));
		} else if(key.equals(KEY_PREF_SILENCE_CALENDAR_EVENT)) {
			setPreferenceEnabled(sharedPreferences, KEY_PREF_SILENCE_ALL_DAY_EVENTS, KEY_PREF_SILENCE_CALENDAR_EVENT);
			setPreferenceEnabled(sharedPreferences, KEY_PREF_LONG_EVENTS_ENABLED, KEY_PREF_SILENCE_CALENDAR_EVENT);
			setPreferenceEnabled(sharedPreferences, KEY_PREF_LONG_EVENTS_LENGTH, KEY_PREF_SILENCE_CALENDAR_EVENT);
			if(sharedPreferences.getBoolean(KEY_PREF_SILENCE_CALENDAR_EVENT, false)){
				new CalendarSyncAsyncTask(getActivity()).execute(CalendarSyncAsyncTask.CANCEL_CREATE_ALARMS);
			} else {
				new CalendarSyncAsyncTask(getActivity()).execute(CalendarSyncAsyncTask.CANCEL_ALARMS);
			}
		} else if(key.equals(KEY_PREF_SILENCE_ALL_DAY_EVENTS)) {
			new CalendarSyncAsyncTask(getActivity()).execute(CalendarSyncAsyncTask.CANCEL_CREATE_ALARMS);
		}
		
	}

	protected void setVolumeRestoreLevelSummary(int value){
		findPreference(KEY_PREF_VOLUME_RESTORE_LEVEL)
			.setSummary(VolumeNumberPickerPreference.getValue(String.valueOf(value)) + "%");
	}
	
	protected void setPreferenceSummary(String preference, String value){
		Preference longEventsLength = findPreference(preference);
		longEventsLength.setSummary(value);
	}
	
	protected void setPreferenceEnabled(SharedPreferences sharedPreferences, String preferenceKey, String preferenceDependency){
		Preference preference = findPreference(preferenceKey);
		preference.setEnabled(sharedPreferences.getBoolean(preferenceDependency, true));
	}
	
}
