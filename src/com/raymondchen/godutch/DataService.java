package com.raymondchen.godutch;

import java.util.ArrayList;
import java.util.List;
import com.raymondchen.godutch.dao.ExpenseDbAdapter;
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
	
	public static Trip getTripById(Context context,long tripId) {
		TripDbAdapter adapter=new TripDbAdapter(context);
		return adapter.getEntry(tripId);
	}
	
	public static void addExpense(Context context, Expense expense) {
		ExpenseDbAdapter adapter=new ExpenseDbAdapter(context);
		adapter.insertEntry(expense);
	}
	
	public static List<Expense> getExpenseListByTripId(Context context, long tripId) {
		ExpenseDbAdapter adapter=new ExpenseDbAdapter(context);
		return adapter.getExpenseListByTripId(tripId);
	}
	
	/**
	 * also delete related expenses
	 * @param tripId
	 */
	public static void deleteTripById(Context context,long tripId) {
		ExpenseDbAdapter expenseAdapter=new ExpenseDbAdapter(context);
		expenseAdapter.removeEntriesByTripId(tripId);
		TripDbAdapter tripAdapter=new TripDbAdapter(context);
		tripAdapter.removeEntry(tripId);
	}
	
	public static void deleteExpenseById(Context context,long expenseId) {
		ExpenseDbAdapter expenseAdapter=new ExpenseDbAdapter(context);
		expenseAdapter.removeEntry(expenseId);
	}
	
	/**
	 * ɾ���û���ͬʱɾ���漰���û�������expense��trip
	 * @param context
	 * @param userId
	 */
	public static void deleteUserById(Context context, long userId) {
		ExpenseDbAdapter expenseAdapter=new ExpenseDbAdapter(context);
		expenseAdapter.removeEntriesByPaidUserId(userId);
		TripDbAdapter tripAdapter=new TripDbAdapter(context);
		tripAdapter.removeEntriesJoinedByUser(userId);
		UserDbAdapter userAdapter=new UserDbAdapter(context);
		userAdapter.removeEntry(userId);
	}
	

}
