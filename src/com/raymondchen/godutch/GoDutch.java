package com.raymondchen.godutch;

import java.util.ArrayList;
import java.util.List;

import com.raymondchen.godutch.activity.SettingsActivity;

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

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		int groupId = 0;
		aboutItem = menu.add(groupId, ABOUT_ITEM_ID, Menu.NONE,
				R.string.menuNameAbout);
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
		return true;
	}

	private MenuItem aboutItem;
	private MenuItem settingItem;
	List<GoDutchActivity> activityList;
	List<User> userList;
	List<String> screenElementList;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		loadUserList();
		loadActivityList();
		initializeScreenElements();
		setListAdapter(new ArrayAdapter<String>(this, R.layout.main,
				screenElementList));
		ListView listView = getListView();
		listView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// When clicked, show a toast with the TextView text
				Toast.makeText(getApplicationContext(),
						((TextView) view).getText() + "-" + position,
						Toast.LENGTH_SHORT).show();
			}
		});

	}

	/**
	 * �Ӵ洢�����ݿ⣩�м��غ�ˢ�»�б�
	 */
	private void loadActivityList() {
		activityList = new ArrayList<GoDutchActivity>();
		GoDutchActivity activity = new GoDutchActivity();
		activity.setName("������ɽ");
		activityList.add(activity);
		activity = new GoDutchActivity();
		activity.setName("��Ӿ");
		activityList.add(activity);
		activity = new GoDutchActivity();
		activity.setName("ȥ������Աߵĺ�ɽ����û����ʧ������");
		activityList.add(activity);
		activity = new GoDutchActivity();
		activity.setName("Last activity is always about food");
		activityList.add(activity);

	}

	private void loadUserList() {
		userList = new ArrayList<User>();
		User user = new User();
		user.setEmail("raymond@126.com");
		user.setName("Raymond126");
		user.setUserId(1L);
		userList.add(user);
		user = new User();
		user.setEmail("raymond@raymondchen.com");
		user.setName("Raymond Chen");
		user.setUserId(2L);
		userList.add(user);
		user = new User();
		user.setEmail("raymondchen625@gmail.com");
		user.setName("Raymond Chen 625");
		user.setUserId(3L);
		userList.add(user);
	}

	private void initializeScreenElements() {
		screenElementList = new ArrayList<String>();
		screenElementList.add(getResources().getString(R.string.newActivity));
		// ������������б������������ձ�ǩ����һ����ȫ�������ǩ��������һ���������������
		for (int i = 0; i < DefaultSetting.ACTIVITIES_NUMBER_ON_MAIN_SCREEN; i++) {
			if (activityList.size() > i) {
				screenElementList.add(activityList.get(i).getName());
			} else {
				screenElementList.add("");
			}
		}
		screenElementList.add("... ȫ���");
		screenElementList.add("�����������");
	}

}