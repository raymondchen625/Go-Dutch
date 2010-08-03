package com.raymondchen.godutch.activity;

import com.raymondchen.godutch.DataService;
import com.raymondchen.godutch.R;
import com.raymondchen.godutch.User;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class NewUserActivity extends Activity {

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		boolean result= super.onKeyDown(keyCode, event);
	    if (keyCode == KeyEvent.KEYCODE_BACK) {
	        finish ();
	    }
	    return result;
	}

	private EditText nameEditText;
	private EditText emailEditText;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.new_user);
		this.nameEditText=(EditText)findViewById(R.id.newUserNameEditText);
		this.emailEditText=(EditText)findViewById(R.id.newUserEmailEditText);
		Button newUserButton=(Button)findViewById(R.id.addUserButton);
		newUserButton.setOnClickListener(new OnClickListener(){
			public void onClick(View v) {
				String name=nameEditText.getText().toString();
				String email=emailEditText.getText().toString();
				if (name.length()==0 ) {
					Toast.makeText(getApplicationContext(), getResources().getText(R.string.inputnewuserinfo), Toast.LENGTH_SHORT).show();
				} else {
					User user=new User();
					user.setName(name);
					user.setEmail(email);
					DataService.addUser(getApplicationContext(), user);
					Toast.makeText(getApplicationContext(), getResources().getText(R.string.addusersucceeded), Toast.LENGTH_SHORT).show();
					resetForm();
				}
			}
		});
	}
	
	private void resetForm() {
		this.nameEditText.setText("");
		this.emailEditText.setText("");
	}

}
