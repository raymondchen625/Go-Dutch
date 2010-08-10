package com.raymondchen.godutch;

import java.util.List;

import com.raymondchen.godutch.dao.TripDbAdapter;
import com.raymondchen.godutch.dao.UserDbAdapter;

import android.content.Context;

public class DataService {
	public static List<User> loadUserList(Context context) {
		UserDbAdapter adapter=new UserDbAdapter(context);
		return adapter.getAllEntries();
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
	
	public static List<Trip> getAllTripList(Context context) {
		TripDbAdapter adapter=new TripDbAdapter(context);
		return adapter.getAllEntries();
	}
}
