package com.raymondchen.godutch.activity;

import com.raymondchen.godutch.DefaultSetting;
import com.raymondchen.godutch.R;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

public class SettingsActivity extends Activity {

	private int defaultActivityNumber = DefaultSetting.ACTIVITIES_NUMBER_ON_MAIN_SCREEN;
	private boolean displayTaxRate = DefaultSetting.DISPLAY_TAX_RATE;
	private EditText defaultActivitiesEditText;
	private CheckBox displayTaxRateCheckBox;
	private SharedPreferences sharedPreferences;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		sharedPreferences=getSharedPreferences(getPackageName(), MODE_PRIVATE);
		setContentView(R.layout.settings);
		this.defaultActivitiesEditText = (EditText) findViewById(R.id.activityNumberEditText);
		this.displayTaxRateCheckBox = (CheckBox) findViewById(R.id.displayTaxRateCheckBox);
	}

	@Override
	protected void onPause() {
		super.onPause();
		
		Integer v = new Integer(defaultActivitiesEditText.getText()
				.toString());
		if (v != null) {
			defaultActivityNumber = v;
		}
		displayTaxRate = displayTaxRateCheckBox.isChecked();
		savePreferences();

	}

	@Override
	protected void onResume() {
		super.onResume();
		this.defaultActivityNumber=sharedPreferences.getInt("defaultActivityNumber", DefaultSetting.ACTIVITIES_NUMBER_ON_MAIN_SCREEN);
		this.displayTaxRate=sharedPreferences.getBoolean("displayTaxRate", DefaultSetting.DISPLAY_TAX_RATE);
		defaultActivitiesEditText.setText(defaultActivityNumber + "");
		displayTaxRateCheckBox.setChecked(displayTaxRate);
	}
	
	private void savePreferences() {
		SharedPreferences.Editor editor = sharedPreferences.edit();
		editor.putBoolean("displayTaxRate", displayTaxRate);
		editor.putInt("defaultActivityNumber", defaultActivityNumber);
		editor.commit();
	}

}
