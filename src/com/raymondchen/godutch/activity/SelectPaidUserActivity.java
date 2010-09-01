package com.raymondchen.godutch.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.raymondchen.godutch.DataService;
import com.raymondchen.godutch.R;
import com.raymondchen.godutch.Trip;
import com.raymondchen.godutch.User;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Interpolator.Result;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.DialogPreference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceScreen;
import android.util.AttributeSet;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;

public class SelectPaidUserActivity extends ListActivity   {

	List<CheckBoxPreference> userCheckboxList;
	List<User> currentUserList=new ArrayList<User>();
	List<User> selectedUserList=new ArrayList<User>();
	List<Map<String,Object>> listContent=new ArrayList<Map<String,Object>>();
	PreferenceScreen selectTripUserActivity;
	DialogPreference addNewUser;
	Trip trip;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		long tripId=getIntent().getExtras().getLong("tripId");
		trip=DataService.getTripById(getApplicationContext(), tripId);
		refreshUserList();
		
	}
	
	private void refreshUserList() {
		List<User> userList=trip.getMembers();
		for (User user : userList) {
			if (! currentUserList.contains(user)) {
				currentUserList.add(user);
				String email="";
				Bitmap avatarBitmap=null;
				if (user.getEmail()!=null && !user.getEmail().trim().equals("")){
					email=" ("+user.getEmail()+")";
				}
				if (user.getAvatar()!=null) {
					avatarBitmap=BitmapFactory.decodeByteArray(user.getAvatar(), 0, user.getAvatar().length);
				}
				Map<String,Object> map=new HashMap<String,Object>();
				map.put("userId", user.getUserId());
				map.put("name", user.getName());
				map.put("email",email);
				map.put("avatar", avatarBitmap);
				listContent.add(map);
			}
		}
		SimpleAdapter adapter=new SimpleAdapter(getApplicationContext(), listContent, R.layout.select_paid_user,new String[]{"name","email","avatar"}, new int[]{R.id.paidUserNameSelectTextView,R.id.paidUserEmailSelectTextView,R.id.paidUserAvatarSelectImageView});
		adapter.setViewBinder(new SimpleAdapter.ViewBinder() {
			public boolean setViewValue(View view, Object obj,
					String textRepresentation) {
				if (view instanceof ImageView) {
					ImageView image=(ImageView)view;
					if (obj!=null) {
					image.setImageBitmap((Bitmap)obj);
					} else {
						image.setImageResource(R.drawable.default_avatar);
					}
					return true;
				}
				return false;
			}
		});
		setListAdapter(adapter);
		final List<User> memberList=userList;
		getListView().setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> adapterView, View view, int position,
					long id) {
				Bundle bundle=new Bundle();
				User user=memberList.get(position);
				bundle.putLong("userId",user.getUserId());;
				Intent result=getIntent();
				result.putExtra(getPackageName(),bundle);
				setResult(Activity.RESULT_OK,result);
				finish();
			}

		});
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
