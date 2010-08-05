package com.raymondchen.godutch.activity;

import java.util.ArrayList;
import java.util.List;

import com.raymondchen.godutch.DataService;
import com.raymondchen.godutch.R;
import com.raymondchen.godutch.Trip;
import com.raymondchen.godutch.User;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class NewTripActivity extends Activity {
	private static final int SELECT_USER=1;
	private Button selectUsersButton;
	private TextView selectedUserListTextView;
	private Button addTripButton;
	private EditText newTripEditText;
	private List<User> selectedUserList=new ArrayList<User>();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.new_trip);
		selectUsersButton=(Button)findViewById(R.id.selectUsersButton);
		selectedUserListTextView=(TextView)findViewById(R.id.selectedUserListTextView);
		newTripEditText=(EditText)findViewById(R.id.newTripEditText);
		addTripButton=(Button)findViewById(R.id.addTripButton);
		selectUsersButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				startActivityForResult(new Intent(getApplicationContext(),SelectTripUsersActivity.class),SELECT_USER);
			}
		});
		addTripButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if (selectedUserList==null || selectedUserList.size()==0) {
					Toast.makeText(getApplicationContext(), getResources().getText(R.string.selectUsersPlease), Toast.LENGTH_SHORT).show();
					return ;
				} else if (newTripEditText.getText().toString().equals("")) {
					Toast.makeText(getApplicationContext(), getResources().getText(R.string.specifyTripNamePlease), Toast.LENGTH_SHORT).show();
					return ;
				}
				Trip trip=new Trip();
				trip.setName(newTripEditText.getText().toString());
				trip.setMembers(selectedUserList);
				DataService.addTrip(getApplicationContext(), trip);
				Toast.makeText(getApplicationContext(), getResources().getText(R.string.addTripSucceeded), Toast.LENGTH_LONG).show();
				finish();
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
		refreshSelectedUserList(userIdArray);
	}
	
	private void refreshSelectedUserList(long[] userIdArray) {
		this.selectedUserList=new ArrayList<User>();
		for (long userId : userIdArray) {
			selectedUserList.add(DataService.getUserById(getApplicationContext(), userId));
		}
		String userListText="";
		for (int i=0;i<selectedUserList.size();i++) {
			userListText+=selectedUserList.get(i).getName();
			if (i<selectedUserList.size()-1) {
				userListText+=", ";
			}
		}
		selectedUserListTextView.setText(userListText);
	}

}
