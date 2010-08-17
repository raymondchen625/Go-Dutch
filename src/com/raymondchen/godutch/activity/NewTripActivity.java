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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class NewTripActivity extends Activity {
	private static final int SELECT_USER = 1;
	private Button selectUsersButton;
	private TextView selectedUserListTextView;
	private Button addTripButton;
	private EditText newTripEditText;
	private List<User> selectedUserList = new ArrayList<User>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.new_trip);
		selectUsersButton = (Button) findViewById(R.id.selectUsersButton);
		selectedUserListTextView = (TextView) findViewById(R.id.selectedUserListTextView);
		newTripEditText = (EditText) findViewById(R.id.newTripEditText);
		addTripButton = (Button) findViewById(R.id.addTripButton);
		selectUsersButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				startActivityForResult(new Intent(getApplicationContext(),
						SelectTripUsersActivity.class), SELECT_USER);
			}
		});
		addTripButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if (selectedUserList == null || selectedUserList.size() <= 1) {
					Toast.makeText(getApplicationContext(),
							getResources().getText(R.string.selectUsersPlease),
							Toast.LENGTH_SHORT).show();
					return;
				} else if (newTripEditText.getText().toString().equals("")) {
					Toast.makeText(
							getApplicationContext(),
							getResources().getText(
									R.string.specifyTripNamePlease),
							Toast.LENGTH_SHORT).show();
					return;
				}
				Trip trip = new Trip();
				trip.setName(newTripEditText.getText().toString());
				trip.setMembers(selectedUserList);
				DataService.addTrip(getApplicationContext(), trip);
				Toast.makeText(getApplicationContext(),
						getResources().getText(R.string.addTripSucceeded),
						Toast.LENGTH_LONG).show();
				finish();
			}

		});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == Activity.RESULT_OK) {
			Bundle bundle = data.getBundleExtra(getPackageName());
			long[] userIdArray = bundle.getLongArray(getPackageName());
			refreshSelectedUserList(userIdArray);
		}
	}

	private void refreshSelectedUserList(long[] userIdArray) {
		this.selectedUserList = new ArrayList<User>();
		for (long userId : userIdArray) {
			selectedUserList.add(DataService.getUserById(
					getApplicationContext(), userId));
		}
		String userListText = "";
		for (int i = 0; i < selectedUserList.size(); i++) {
			userListText += selectedUserList.get(i).getName();
			if (i < selectedUserList.size() - 1) {
				userListText += ", ";
			}
		}
		selectedUserListTextView.setText(userListText);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		int groupId = 0;
		int returnItemId = 0;
		MenuItem backItem = menu.add(groupId, returnItemId, Menu.NONE,
				R.string.back);
		backItem.setOnMenuItemClickListener(new OnMenuItemClickListener() {
			public boolean onMenuItemClick(MenuItem _menuItem) {
				finish();
				return true;
			}
		});
		return true;
	}

}
