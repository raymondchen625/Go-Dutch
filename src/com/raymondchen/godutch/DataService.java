package com.raymondchen.godutch;

import java.util.ArrayList;
import java.util.List;

import com.raymondchen.godutch.dao.TripDbAdapter;
import com.raymondchen.godutch.dao.UserDbAdapter;

import android.content.Context;

public class DataService {
	public static List<User> loadUserList(Context context) {
		UserDbAdapter adapter=new UserDbAdapter(context);
		return adapter.getAllEntries();
		
		/**
		List<User> userList = new ArrayList<User>();
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
		return userList;
		*/
	}
	
	public static void addUser(Context context,User user) {
		UserDbAdapter adapter=new UserDbAdapter(context);
		adapter.insertEntry(user);
	}
	
	public static User getUserById(Context context, long userId) {
		UserDbAdapter adapter=new UserDbAdapter(context);
		return adapter.getEntry(userId);
	}
	
	public static void addTrip(Context context,Trip trip) {
		TripDbAdapter adapter=new TripDbAdapter(context);
		adapter.insertEntry(trip);
	}
}
