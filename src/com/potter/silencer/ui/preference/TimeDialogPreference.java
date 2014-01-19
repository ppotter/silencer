package com.potter.silencer.ui.preference;

import com.potter.silencer.R;

import android.content.Context;
import android.preference.EditTextPreference;
import android.util.AttributeSet;

import android.content.Context;
import android.content.res.TypedArray;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TimePicker;

public class TimeDialogPreference extends DialogPreference {
	private int lastHour = 0;
	private int lastMinute = 0;
	private TimePicker timePicker = null;

	public static long getMilliseconds(String time){
		int hour = getHour(time), 
				minutes = getMinute(time);
		return (hour * 1000 * 60 * 60) + (minutes * 1000 * 60);
	}
	
	public static int getHour(String time) {
		String[] pieces = time.split(":");

		return (Integer.parseInt(pieces[0]));
	}

	public static int getMinute(String time) {
		String[] pieces = time.split(":");

		return (Integer.parseInt(pieces[1]));
	}

	public TimeDialogPreference(Context ctxt, AttributeSet attrs) {
		super(ctxt, attrs);

		setPositiveButtonText(R.string.time_preference_dialog_positive);
		setNegativeButtonText(R.string.time_preference_dialog_negative);
	}

	@Override
	protected View onCreateDialogView() {
		timePicker = new TimePicker(getContext());
		timePicker.setIs24HourView(true);
		return (timePicker);
	}

	@Override
	protected void onBindDialogView(View v) {
		super.onBindDialogView(v);

		timePicker.setCurrentHour(lastHour);
		timePicker.setCurrentMinute(lastMinute);
	}

	@Override
	protected void onDialogClosed(boolean positiveResult) {
		super.onDialogClosed(positiveResult);

		if (positiveResult) {
			lastHour = timePicker.getCurrentHour();
			lastMinute = timePicker.getCurrentMinute();

			String time = formatTime();

			if (callChangeListener(time)) {
				persistString(time);
			}
		}
	}
	
	protected String formatTime(){
		String hour = String.valueOf(lastHour);
		String minute = String.valueOf(lastMinute);
		String time = ((lastHour < 10) ? "0" + hour : hour) 
				+ ":" + 
				((lastMinute < 10) ? "0" + minute : minute);
		
		return time;
	}

	@Override
	protected Object onGetDefaultValue(TypedArray a, int index) {
		return (a.getString(index));
	}

	@Override
	protected void onSetInitialValue(boolean restoreValue, Object defaultValue) {
		String time = null;

		if (restoreValue) {
			if (defaultValue == null) {
				time = getPersistedString("00:00");
			} else {
				time = getPersistedString(defaultValue.toString());
			}
		} else {
			time = defaultValue.toString();
		}

		lastHour = getHour(time);
		lastMinute = getMinute(time);
	}
}