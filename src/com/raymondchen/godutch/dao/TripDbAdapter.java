package com.raymondchen.godutch.dao;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import com.raymondchen.godutch.DataService;
import com.raymondchen.godutch.Trip;
import com.raymondchen.godutch.User;

public class TripDbAdapter {
	public static final String DATABASE_TABLE = "trip";

	// The index (key) column name for use in where clauses.
	public static final String KEY_ID = "tripId";
	// 定义各个其它字段以及它们的序号
	public static final String KEY_NAME = "name";
	public static final int NAME_COLUMN = 1;
	public static final String KEY_MEMBER_IDS = "memberIds";
	public static final int MEMBER_IDS_COLUMN = 2;

	// 建表语句
	public static final String DATABASE_CREATE = "create table "
			+ DATABASE_TABLE + " (" + KEY_ID
			+ " integer primary key autoincrement, " + KEY_NAME
			+ " text not null, " + KEY_MEMBER_IDS + " text);";

	// Variable to hold the database instance
	private SQLiteDatabase db;
	// Context of the application using the database.
	private final Context context;
	// Database open/upgrade helper
	private myDbHelper dbHelper;

	public TripDbAdapter(Context _context) {
		context = _context;
		dbHelper = new myDbHelper(context, DbUtil.DATABASE_NAME, null,
				DbUtil.CURRENT_DATABASE_VERSION);
	}

	public TripDbAdapter open() {
		db = dbHelper.getWritableDatabase();
		return this;
	}

	public void close() {
		db.close();
	}

	public long insertEntry(Trip trip) {
		if (trip == null) {
			throw new IllegalArgumentException("user argument must not be null");
		}
		open();
		ContentValues newValues = new ContentValues();
		newValues.put(KEY_NAME, trip.getName());
		newValues.put(KEY_MEMBER_IDS, trip.getMemberIds());
		Long index = db.insert(DATABASE_TABLE, null, newValues);
		close();
		return index;
	}

	public boolean removeEntry(long tripId) {
		open();
		boolean result = db.delete(DATABASE_TABLE, KEY_ID + "=" + tripId, null) > 0;
		close();
		return result;
	}

	public List<Trip> getAllEntries() {
		open();
		Cursor cursor = db.query(DATABASE_TABLE, new String[] { KEY_ID,
				KEY_NAME, KEY_MEMBER_IDS }, null, null, null, null, null);
		List<Trip> list = new ArrayList<Trip>();
		if (cursor.moveToFirst()) {
			do {
				Trip trip = new Trip();
				trip.setName(cursor.getString(NAME_COLUMN));
				trip.setTripId(cursor.getLong(0));
				String memberIdList = cursor.getString(MEMBER_IDS_COLUMN);
				fillTripMembers(memberIdList, trip);
				list.add(trip);
			} while (cursor.moveToNext());
		}
		cursor.close();
		close();
		return list;
	}

	public Trip getEntry(long tripId) {
		open();
		Cursor cursor = db.query(DATABASE_TABLE, new String[] { KEY_ID,
				KEY_NAME, KEY_MEMBER_IDS }, KEY_ID + " = ?",
				new String[] { tripId + "" }, null, null, null);
		if (cursor.getCount() == 0) {
			cursor.close();
			close();
			return null;
		}
		cursor.moveToFirst();
		Trip trip = new Trip();
		trip.setName(cursor.getString(NAME_COLUMN));
		trip.setTripId(cursor.getLong(0));
		String memberIdList = cursor.getString(MEMBER_IDS_COLUMN);
		fillTripMembers(memberIdList, trip);
		cursor.close();
		close();
		return trip;
	}

	public boolean updateEntry(Trip trip) {
		if (trip == null) {
			throw new IllegalArgumentException("trip argument must not be null");
		}
		open();
		ContentValues updatedValues = new ContentValues();
		updatedValues.put(KEY_NAME, trip.getName());
		updatedValues.put(KEY_MEMBER_IDS, trip.getMemberIds());
		String where = "tripId = ?";
		db.update(DATABASE_TABLE, updatedValues, where,
				new String[] { trip.getTripId() + "" });
		close();
		return true;
	}

	private static class myDbHelper extends SQLiteOpenHelper {
		public myDbHelper(Context context, String name, CursorFactory factory,
				int version) {
			super(context, name, factory, version);
		}

		// Called when no database exists in disk and the helper class needs
		// to create a new one.
		@Override
		public void onCreate(SQLiteDatabase _db) {
			// do nothing
		}
		// Called when there is a database version mismatch meaning that the
		// version
		// of the database on disk needs to be upgraded to the current version.
		@Override
		public void onUpgrade(SQLiteDatabase _db, int _oldVersion,
				int _newVersion) {
		}
	}

	private void fillTripMembers(String memberIdList, Trip trip) {
		if (memberIdList == null) {
			memberIdList = "";
		}
		List<User> memberList = new ArrayList<User>();
		String[] idList = memberIdList.split(",");
		for (String id : idList) {
			User user = DataService.getUserById(context, new Long(id));
			if (user != null) {
				memberList.add(user);
			}
		}
		trip.setMembers(memberList);
	}
	
	public boolean removeEntriesJoinedByUser(long userId) {
		open();
		boolean result = db.delete(DATABASE_TABLE, KEY_MEMBER_IDS + " like ? or "+KEY_MEMBER_IDS +" like ? or "+KEY_MEMBER_IDS +" like ?" , new String[]{userId+",%","%,"+userId,"%,"+userId+",%"}) > 0;
		close();
		return result;
	}
}
