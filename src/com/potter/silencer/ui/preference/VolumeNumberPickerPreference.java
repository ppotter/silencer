package com.potter.silencer.ui.preference;

import com.potter.silencer.ui.fragment.SettingsFragment;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.NumberPicker;

public class VolumeNumberPickerPreference extends NumberPickerPreference {

	private static final String[] values = {"0","10","20","30","40","50","60","70","80","90","100"};
	
	public VolumeNumberPickerPreference(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	@Override
	protected NumberPicker getNumberPicker() {
		NumberPicker numberPicker = new NumberPicker(getContext());
		numberPicker.setMinValue(0);
		numberPicker.setMaxValue(values.length-1);
		numberPicker.setDisplayedValues(values);
		numberPicker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
		return numberPicker;
	}
	
	@Override
	protected Integer getDefaultValue() {
		return Integer.valueOf(80);
	}

	public static Integer getValue(String n){
		return Integer.valueOf(values[Integer.valueOf(n)]);
	}
	
	public static float getFloatValue(int n){
		Integer value = Integer.valueOf(values[n]);
		float result = ((float)value)/100;
		return result;
	}
}
