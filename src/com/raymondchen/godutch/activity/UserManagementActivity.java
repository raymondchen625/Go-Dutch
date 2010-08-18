package com.raymondchen.godutch.activity;

import java.util.ArrayList;
import java.util.List;

import com.raymondchen.godutch.DataService;
import com.raymondchen.godutch.R;
import com.raymondchen.godutch.User;
import com.raymondchen.godutch.R.layout;
import com.raymondchen.godutch.R.string;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnClickListener;
import android.view.View.OnCreateContextMenuListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class UserManagementActivity extends Activity {
	private List<User> userList;
	private List<String> screenElementList;
	private ListView listView;
	private Button addUserButton;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.user_admin);
		listView=(ListView)findViewById(R.id.currentUsersListView);
		addUserButton=(Button)findViewById(R.id.addUserButton01);
		addUserButton.setOnClickListener(new OnClickListener() {
			public void onClick(View view) {
				Intent intent=new Intent(getApplicationContext(),NewUserActivity.class);
				startActivity(intent);
			}
		});
		initializeScreenElements();
	}
	
	private void initializeScreenElements() {
		userList = DataService.loadUserList(getApplicationContext());
		screenElementList = new ArrayList<String>();
		for (int i = 0; i < userList.size(); i++) {
			String email="";
			if (userList.get(i).getEmail()!=null && !userList.get(i).getEmail().trim().equals("")){
				email=" ("+userList.get(i).getEmail()+")";
			}
				screenElementList.add(userList.get(i).getName() + email);
		}
		ArrayAdapter<String> listAdapter=new ArrayAdapter<String>(this, R.layout.main,
				screenElementList);
		listView.setAdapter(listAdapter);
		final UserManagementActivity parentObj=this;
		listView.setOnCreateContextMenuListener(new OnCreateContextMenuListener() {
			public void onCreateContextMenu(ContextMenu contextMenu, View v,
					ContextMenuInfo info) {
				MenuItem menuItem=contextMenu.add(getResources().getString(R.string.delete));
		    	menuItem.setOnMenuItemClickListener(new OnMenuItemClickListener() {
					public boolean onMenuItemClick(MenuItem item) {
						AdapterView.AdapterContextMenuInfo menuInfo=(AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
						final int position=menuInfo.position;
						AlertDialog.Builder builder=new AlertDialog.Builder(parentObj);
						builder.setPositiveButton(R.string.delete,new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int which) {
								DataService.deleteUserById(getApplicationContext(), userList.get(position).getUserId());
								initializeScreenElements();
							}
						});
						builder.setCancelable(true);
						builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener(){
							public void onClick(DialogInterface dialog, int which) {
							}
							
						});
						AlertDialog dialog=builder.create();
						dialog.setTitle(getResources().getString(R.string.confirmDeleteUser));
						dialog.setMessage(getResources().getString(R.string.deleteUserWarning));
						dialog.show();
						return true;
					}
		    	});
				
			}
			
		});
	}

	@Override
	protected void onResume() {
		super.onResume();
		initializeScreenElements();
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
