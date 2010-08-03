package com.raymondchen.godutch.activity;

import com.raymondchen.godutch.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class NewTripActivity extends Activity {
	private Button selectUsersButton;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.new_trip);
		selectUsersButton=(Button)findViewById(R.id.selectUsersButton);
		selectUsersButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				startActivity(new Intent(getApplicationContext(),SelectTripUsersActivity.class));
				
			}
		});
	}

}
