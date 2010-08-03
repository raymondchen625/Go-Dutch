package com.raymondchen.godutch.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.raymondchen.godutch.DataService;
import com.raymondchen.godutch.R;
import com.raymondchen.godutch.User;

import android.app.ListActivity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.DialogPreference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceScreen;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;

public class SelectTripUsersActivity extends ListActivity   {

	List<CheckBoxPreference> userCheckboxList;
	List<User> currentUserList=new ArrayList<User>();
	List<Map<String,Object>> listContent=new ArrayList<Map<String,Object>>();
	PreferenceScreen selectTripUserActivity;
	DialogPreference addNewUser;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.select_trip_users);
		refreshUserList();
//		ListView listView = getListView();
//		listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
		SimpleAdapter adapter=new SimpleAdapter(getApplicationContext(), listContent, R.layout.select_user_item,new String[]{"name","email","selected"}, new int[]{R.id.nameSelectTextView,R.id.emailSelectTextView,R.id.selectUserItemCheckBox});
		adapter.setViewBinder(new SimpleAdapter.ViewBinder() {
			public boolean setViewValue(View view, Object data,
					String textRepresentation) {
				if (view instanceof CheckBox) {
				System.out.println("textRepresentation="+data.getClass());
				}
				return false;
			}
		});
		


		setListAdapter(adapter);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		refreshUserList();
		
	}
	
	private void refreshUserList() {
		List<User> userList=DataService.loadUserList(getApplicationContext());
		for (User user : userList) {
			if (! currentUserList.contains(user)) {
				currentUserList.add(user);
				Map<String,Object> map=new HashMap<String,Object>();
				map.put("name", user.getName());
				map.put("email",user.getEmail());
				map.put("selected", false);
				listContent.add(map);
			}
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		ListView listView = getListView();
		for (int position=0;position<listView.getChildCount();position++) {
		  LinearLayout v=(LinearLayout)listView.getChildAt(position);
		  System.out.println("v="+v.getChildAt(2));
		}
	}

	


}
