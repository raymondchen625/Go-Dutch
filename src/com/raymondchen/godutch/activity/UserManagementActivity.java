package com.raymondchen.godutch.activity;

import java.util.ArrayList;
import java.util.List;

import com.raymondchen.godutch.DataService;
import com.raymondchen.godutch.R;
import com.raymondchen.godutch.User;
import com.raymondchen.godutch.R.layout;
import com.raymondchen.godutch.R.string;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class UserManagementActivity extends ListActivity {
	List<User> userList;
	List<String> screenElementList;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		userList = DataService.loadUserList(getApplicationContext());
		initializeScreenElements();
	}
	
	private void initializeScreenElements() {
		screenElementList = new ArrayList<String>();
		screenElementList.add(getResources().getString(R.string.newUser));
		for (int i = 0; i < userList.size(); i++) {
				screenElementList.add(userList.get(i).getName() + " ("+userList.get(i).getEmail()+")");
		}
		setListAdapter(new ArrayAdapter<String>(this, R.layout.main,
				screenElementList));
		ListView listView = getListView();
		listView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if (position==0) { // 第一项选中（新建用户）
					Intent intent=new Intent(getApplicationContext(),NewUserActivity.class);
					startActivity(intent);
				}
			}
		});
	}

	@Override
	protected void onResume() {
		super.onResume();
		userList = DataService.loadUserList(getApplicationContext());
		initializeScreenElements();
		
	}
}
