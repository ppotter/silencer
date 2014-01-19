package com.potter.silencer.ui.preference;

import android.content.Context;
import android.content.res.TypedArray;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.View;
import android.widget.NumberPicker;

import com.potter.silencer.R;

public abstract class NumberPickerPreference extends DialogPreference {

	private Integer number = null;
	private NumberPicker numberPicker = null;
	
	protected abstract NumberPicker getNumberPicker();
	protected abstract Integer getDefaultValue();
	
	public NumberPickerPreference(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		setPositiveButtonText(R.string.preference_dialog_positive);
		setNegativeButtonText(R.string.preference_dialog_negative);
	}
	

	@Override
	protected View onCreateDialogView() {
		numberPicker = getNumberPicker();
		return numberPicker;
	}

	@Override
	protected void onBindDialogView(View v) {
		super.onBindDialogView(v);

		if(number != null) numberPicker.setValue(number);
	}

	@Override
	protected void onDialogClosed(boolean positiveResult) {
		super.onDialogClosed(positiveResult);

		if (positiveResult) {
			number = numberPicker.getValue();

			if (callChangeListener(number)) {
				persistInt(number);
			}
		}
	}
	
	@Override
	protected Object onGetDefaultValue(TypedArray a, int index) {
		return a.getInt(index,  getDefaultValue());
	}
	
	@Override
	protected void onSetInitialValue(boolean restoreValue, Object defaultValue) {
		int def = ( defaultValue instanceof Number ) ? (Integer)defaultValue
				: ( defaultValue != null ) ? Integer.parseInt(defaultValue.toString()) : 1;
		if (restoreValue) {
			number = getPersistedInt(def);
		} else {
			number = (Integer)defaultValue;
		}
	}
}
