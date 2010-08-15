package com.raymondchen.godutch.activity;

import java.util.ArrayList;
import java.util.List;

import com.raymondchen.godutch.DataService;
import com.raymondchen.godutch.R;
import com.raymondchen.godutch.Trip;
import com.raymondchen.godutch.dao.DbUtil;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.MenuItem.OnMenuItemClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

public class HomePage extends Activity {
	
	private static int ABOUT_ITEM_ID = 0;
	private static int SETTING_ITEM_ID = 1;
	private static int USER_MANAGEMENT_ITEM_ID=2;
	

	private MenuItem aboutItem;
	private MenuItem settingItem;
	private MenuItem userManagementItem;
	private ListView listView;
	List<Trip> tripList;
	List<String> screenElementList;

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		int groupId = 0;
		aboutItem = menu.add(groupId, ABOUT_ITEM_ID, Menu.NONE,
				R.string.menuNameAbout);
		aboutItem.setOnMenuItemClickListener(new OnMenuItemClickListener() {
			public boolean onMenuItemClick(MenuItem _menuItem) {
				Intent intent = new Intent(getApplicationContext(),
						AboutActivity.class);
				startActivity(intent);
				return true;
			}
		});
		settingItem = menu.add(groupId, SETTING_ITEM_ID, Menu.NONE,
				R.string.menuNameSetting);
		settingItem.setOnMenuItemClickListener(new OnMenuItemClickListener() {
			public boolean onMenuItemClick(MenuItem _menuItem) {
				Intent intent = new Intent(getApplicationContext(),
						SettingsActivity.class);
				startActivity(intent);
				return true;
			}
		});
		userManagementItem=menu.add(groupId, USER_MANAGEMENT_ITEM_ID, Menu.NONE,
				R.string.menuNameUserManagement);
		userManagementItem.setOnMenuItemClickListener(new OnMenuItemClickListener() {
			public boolean onMenuItemClick(MenuItem _menuItem) {
				Intent intent = new Intent(getApplicationContext(),
						UserManagementActivity.class);
				startActivity(intent);
				return true;
			}
		});
		return true;
	}



	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.homepage);
		DbUtil.checkDbSchemaVersion(getApplicationContext());
		
		listView =(ListView)findViewById(R.id.taskListListView);
		
		initializeScreenElements();
	}

	private void initializeScreenElements() {
		loadActivityList();
		screenElementList = new ArrayList<String>();
		for (int i = 0; i < tripList.size(); i++) {
				screenElementList.add(tripList.get(i).getName());
		}
        listView.setAdapter(new ArrayAdapter<String>(this, R.layout.main,
				screenElementList));
		listView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
					Trip trip=tripList.get(position);
					Intent intent=new Intent(getApplicationContext(),NewExpenseActivity.class);
					intent.putExtra("tripId", trip.getTripId());
					startActivity(intent);
			}
		});
	}
	
	/**
	 * 从存储（数据库）中加载和刷新活动列表
	 */
	private void loadActivityList() {
		tripList = DataService.getAllTripList(getApplicationContext());
	}
}
