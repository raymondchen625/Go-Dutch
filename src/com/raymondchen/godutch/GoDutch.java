package com.raymondchen.godutch;

import java.util.ArrayList;
import java.util.List;

import com.raymondchen.godutch.activity.AboutActivity;
import com.raymondchen.godutch.activity.NewExpenseActivity;
import com.raymondchen.godutch.activity.NewTripActivity;
import com.raymondchen.godutch.activity.SettingsActivity;
import com.raymondchen.godutch.activity.UserManagementActivity;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class GoDutch extends ListActivity {
	private static int ABOUT_ITEM_ID = 0;
	private static int SETTING_ITEM_ID = 1;
	private static int USER_MANAGEMENT_ITEM_ID=2;

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

	private MenuItem aboutItem;
	private MenuItem settingItem;
	private MenuItem userManagementItem;
	List<Trip> tripList;
	List<String> screenElementList;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		loadActivityList();
		initializeScreenElements();
		setListAdapter(new ArrayAdapter<String>(this, R.layout.main,
				screenElementList));
		ListView listView = getListView();
		listView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if (position>0 && position<screenElementList.size()-2) {
					Trip trip=tripList.get(position-1);
					Toast.makeText(getApplicationContext(),
							 "tripId - " + trip.getTripId(),
							Toast.LENGTH_SHORT).show();
					Intent intent=new Intent(getApplicationContext(),NewExpenseActivity.class);
					intent.putExtra("tripId", trip.getTripId());
					startActivity(intent);
				} else {
				// When clicked, show a toast with the TextView text
				Toast.makeText(getApplicationContext(),
						((TextView) view).getText() + "-" + position,
						Toast.LENGTH_SHORT).show();
				if (position==0) {
					startActivity(new Intent(getApplicationContext(),NewTripActivity.class));
				}
				}
			}
		});

	}

	/**
	 * 从存储（数据库）中加载和刷新活动列表
	 */
	private void loadActivityList() {
		tripList = DataService.getAllTripList(getApplicationContext());
	}

	private void initializeScreenElements() {
		screenElementList = new ArrayList<String>();
		screenElementList.add(getResources().getString(R.string.newTrip));
		// 加入最近三项活动列表（不足三项加入空标签），一个“全部活动”标签，最后加入一个“快速添加帐务”
		for (int i = 0; i < tripList.size(); i++) {
				screenElementList.add(tripList.get(i).getName());

		}
		screenElementList.add("... 全部活动");
		screenElementList.add("快速添加帐务");
	}

}