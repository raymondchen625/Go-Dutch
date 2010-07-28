package com.raymondchen.godutch.activity;

import com.raymondchen.godutch.DefaultSetting;
import com.raymondchen.godutch.R;

import android.app.Activity;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

public class SettingsActivity extends Activity {

	private int defaultActivityNumber = DefaultSetting.ACTIVITIES_NUMBER_ON_MAIN_SCREEN;
	private boolean displayTaxRate = DefaultSetting.DISPLAY_TAX_RATE;
	private EditText defaultActivitiesEditText;
	private CheckBox displayTaxRateCheckBox;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.defaultActivitiesEditText = (EditText) findViewById(R.id.activityNumberEditText);
		this.displayTaxRateCheckBox = (CheckBox) findViewById(R.id.displayTaxRateCheckBox);
		setContentView(R.layout.settings);
	}

	@Override
	protected void onPause() {
		super.onPause();

//		Integer v = Integer.getInteger(defaultActivitiesEditText.getText()
//				.toString());
//		if (v != null) {
//			defaultActivityNumber = v;
//		}
//		displayTaxRate = displayTaxRateCheckBox.isChecked();

	}

	@Override
	protected void onResume() {
		super.onResume();
//		defaultActivitiesEditText.setText(defaultActivityNumber);
//		displayTaxRateCheckBox.setChecked(displayTaxRate);
	}

}
