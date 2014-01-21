package com.potter.silencer.ui.activity;

import com.potter.silencer.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

public class NotifySilenceActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		//		showAlert();
	}
	
	private void showAlert(){
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		LayoutInflater inflater = this.getLayoutInflater();
		
		LinearLayout v = (LinearLayout)inflater.inflate(R.layout.silence_length_dialog, null);
//		v.addView(child)
		builder.setView(v);
		builder.create().show();
	}
}
