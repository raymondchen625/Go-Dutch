package com.raymondchen.godutch.dao;

import java.util.ArrayList;
import java.util.List;

import com.raymondchen.godutch.User;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class UserDbAdapter {
	private static final String DATABASE_TABLE = "user";
	// The index (key) column name for use in where clauses.
	public static final String KEY_ID = "userId";
	// 定义各个其它字段以及它们的序号
	public static final String KEY_NAME = "name";
	public static final int NAME_COLUMN = 1;
	public static final String KEY_EMAIL = "email";
	public static final int EMAIL_COLUMN = 2;
	// 建表语句
	public static final String DATABASE_CREATE = "create table "
			+ DATABASE_TABLE + " (" + KEY_ID
			+ " integer primary key autoincrement, " + KEY_NAME
			+ " text not null, " + KEY_EMAIL + " text);";
	// Variable to hold the database instance
	private SQLiteDatabase db;
	// Context of the application using the database.
	private final Context context;
	// Database open/upgrade helper
	private myDbHelper dbHelper;

	public UserDbAdapter(Context _context) {
		context = _context;
		dbHelper = new myDbHelper(context, DbUtil.DATABASE_NAME, null,
				DbUtil.CURRENT_DATABASE_VERSION);
	}

	public UserDbAdapter open() {
		db = dbHelper.getWritableDatabase();
		return this;
	}

	public void close() {
		db.close();
	}

	public long insertEntry(User user) {
		if (user==null) {
			throw new IllegalArgumentException("user argument must not be null");
		}
		open();
		ContentValues newValues=new ContentValues();
		newValues.put(KEY_NAME, user.getName());
		newValues.put(KEY_EMAIL, user.getEmail());
		Long index=db.insert(DATABASE_TABLE, null, newValues);
		close();
		return index;
	}

	public boolean removeEntry(long userId) {
		open();
		boolean result=db.delete(DATABASE_TABLE, KEY_ID + "=" + userId, null) > 0;
		close();
		return result;
	}

	public List<User> getAllEntries() {
		open();
		Cursor cursor= db.query(DATABASE_TABLE, new String[] { KEY_ID, KEY_NAME,KEY_EMAIL },
				null, null, null, null, null);
		List<User> list=new ArrayList<User>();
		if (cursor.moveToFirst()) {
			do {
				User user=new User();
				user.setEmail(cursor.getString(EMAIL_COLUMN));
				user.setName(cursor.getString(NAME_COLUMN));
				user.setUserId(cursor.getLong(0));
				list.add(user);
			} while (cursor.moveToNext());
		}
		cursor.close();
		close();
		return list;
	}

	public User getEntry(long userId) {
		open();
		Cursor cursor=db.query(DATABASE_TABLE, new String[]{KEY_ID, KEY_NAME,KEY_EMAIL}, KEY_ID+" = ?", new String[]{userId+""}, null, null, null);
		if (cursor.getCount()==0) {
			return null;
		}
		cursor.moveToFirst();
		User user=new User();
		user.setEmail(cursor.getString(EMAIL_COLUMN));
		user.setName(cursor.getString(NAME_COLUMN));
		user.setUserId(cursor.getLong(0));
		cursor.close();
		close();
		return user;
	}

	public boolean updateEntry(User user) {
		if (user==null) {
			throw new IllegalArgumentException("user argument must not be null");
		}
		ContentValues updatedValues=new ContentValues();
		updatedValues.put(KEY_NAME, user.getName());
		updatedValues.put(KEY_EMAIL, user.getEmail());
		String where="userId=?";
		db.update(DATABASE_TABLE, updatedValues, where, new String[]{user.getUserId()+""});
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
		}

		// Called when there is a database version mismatch meaning that the
		// version
		// of the database on disk needs to be upgraded to the current version.
		@Override
		public void onUpgrade(SQLiteDatabase _db, int _oldVersion,
				int _newVersion) {
		}
	}
}
