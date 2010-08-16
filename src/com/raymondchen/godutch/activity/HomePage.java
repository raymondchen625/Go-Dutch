package com.raymondchen.godutch.activity;

import java.util.ArrayList;
import java.util.List;

import com.raymondchen.godutch.DataService;
import com.raymondchen.godutch.Expense;
import com.raymondchen.godutch.R;
import com.raymondchen.godutch.Trip;
import com.raymondchen.godutch.User;
import com.raymondchen.godutch.dao.DbUtil;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

public class HomePage extends Activity {
	

	


	private ListView listView;
	List<Trip> tripList;
	List<String> screenElementList;
	private Button createTripButton;
	private Button userManagementButton;

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		int groupId = 0;
		int ABOUT_ITEM_ID = 0;
		int NEW_TRIP_ITEM_ID = 1;
		int USER_MANAGEMENT_ITEM_ID=2;
		MenuItem aboutItem = menu.add(groupId, ABOUT_ITEM_ID, Menu.NONE,
				R.string.menuNameAbout);
		aboutItem.setOnMenuItemClickListener(new OnMenuItemClickListener() {
			public boolean onMenuItemClick(MenuItem _menuItem) {
				Intent intent = new Intent(getApplicationContext(),
						AboutActivity.class);
				startActivity(intent);
				return true;
			}
		});
		
		MenuItem newTripItem=menu.add(groupId,NEW_TRIP_ITEM_ID,Menu.NONE,R.string.newTrip);
		newTripItem.setOnMenuItemClickListener(new OnMenuItemClickListener() {
			public boolean onMenuItemClick(MenuItem _menuItem) {
				Intent intent = new Intent(getApplicationContext(),
						NewTripActivity.class);
				startActivity(intent);
				return true;
			}
		});
		
		MenuItem userManagementItem=menu.add(groupId, USER_MANAGEMENT_ITEM_ID, Menu.NONE,
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
		createTripButton=(Button)findViewById(R.id.newTripButton1);
		userManagementButton=(Button)findViewById(R.id.userManagementButton1);
		listView =(ListView)findViewById(R.id.taskListListView);
		userManagementButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(getApplicationContext(),
						UserManagementActivity.class);
				startActivity(intent);
			}
		});
		createTripButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(getApplicationContext(),
						NewTripActivity.class);
				startActivity(intent);
			}
		});
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
	
	@Override
	public void onCreateContextMenu(ContextMenu contextMenu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(contextMenu, v, menuInfo);
		int i=0;
    	contextMenu.add(0,i++,0,getResources().getString(R.string.delete));
	}
	
	/**
	 * 从存储（数据库）中加载和刷新活动列表
	 */
	private void loadActivityList() {
		tripList = DataService.getAllTripList(getApplicationContext());
	}



	@Override
	protected void onResume() {
		super.onResume();
		initializeScreenElements();
	}
	
	
}
