package com.potter.silencer.ui.fragment;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;

import com.potter.silencer.R;

public class SettingsFragment extends PreferenceFragment implements OnSharedPreferenceChangeListener{

	
	public static final String KEY_PREF_LONG_EVENTS_ENABLED = "pref_key_long_events_enabled";
	public static final String KEY_PREF_LONG_EVENTS_CATEGORY = "pref_key_long_events_category";
	public static final String KEY_PREF_LONG_EVENTS_LENGTH = "pref_key_long_events_length";
	public static final String KEY_PREF_VIBRATE = "pref_key_vibrate";
	public static final String KEY_PREF_NOTIFICATIONS = "pref_key_notifications";
	public static final String KEY_PREF_MEDIA = "pref_key_media";
	public static final String KEY_PREF_RINGER = "pref_key_ringer";
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		addPreferencesFromResource(R.xml.preferences);
		
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
		Preference longEventsLength = findPreference(KEY_PREF_LONG_EVENTS_LENGTH);
		longEventsLength.setSummary(preferences.getString(KEY_PREF_LONG_EVENTS_LENGTH, ""));
		longEventsLength.setEnabled(preferences.getBoolean(KEY_PREF_LONG_EVENTS_ENABLED, true));
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
		if(key.equals(KEY_PREF_LONG_EVENTS_ENABLED)) {
			findPreference(KEY_PREF_LONG_EVENTS_LENGTH)
				.setEnabled(sharedPreferences.getBoolean(KEY_PREF_LONG_EVENTS_ENABLED, true));
		} else if(key.equals(KEY_PREF_LONG_EVENTS_LENGTH)) {
			findPreference(KEY_PREF_LONG_EVENTS_LENGTH)
				.setSummary(sharedPreferences.getString(KEY_PREF_LONG_EVENTS_LENGTH, ""));
		}
		
	}

	
}
