package com.raymondchen.godutch.activity;

import java.util.ArrayList;
import java.util.List;

import com.raymondchen.godutch.R;
import com.raymondchen.godutch.User;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class NewTripActivity extends Activity {
	private static final int SELECT_USER=1;
	private Button selectUsersButton;
	private List<User> selectedUserList=new ArrayList<User>();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.new_trip);
		selectUsersButton=(Button)findViewById(R.id.selectUsersButton);
		selectUsersButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				startActivityForResult(new Intent(getApplicationContext(),SelectTripUsersActivity.class),SELECT_USER);
			}
		});
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		System.out.println("requestCode="+requestCode);
		System.out.println("resultCode="+resultCode);
		Bundle bundle=data.getBundleExtra(getPackageName());
		long[] userIdArray=bundle.getLongArray(getPackageName());
		System.out.println("userIdArray size="+userIdArray.length);
	}
	
	public void setSelectedUserList(List<User> selectedUserList) {
		this.selectedUserList=selectedUserList;
		System.out.println("set done");
	}
	

}
