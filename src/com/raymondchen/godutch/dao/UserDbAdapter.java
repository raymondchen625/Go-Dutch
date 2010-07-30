package com.raymondchen.godutch.dao;

import java.util.ArrayList;
import java.util.List;

import com.raymondchen.godutch.DefaultSetting;
import com.raymondchen.godutch.User;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class UserDbAdapter {
	private static final String DATABASE_TABLE = "user";
	private static final int DATABASE_VERSION = 1;
	// The index (key) column name for use in where clauses.
	public static final String KEY_ID = "userId";
	// 定义各个其它字段以及它们的序号
	public static final String KEY_NAME = "name";
	public static final int NAME_COLUMN = 1;
	public static final String KEY_EMAIL = "email";
	public static final int EMAIL_COLUMN = 2;
	// 建表语句
	private static final String DATABASE_CREATE = "create table "
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
		dbHelper = new myDbHelper(context, DefaultSetting.DATABASE_NAME, null,
				DATABASE_VERSION);
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
		return db.delete(DATABASE_TABLE, KEY_ID + "=" + userId, null) > 0;
	}

	public List<User> getAllEntries() {
		System.out.println("db="+db);
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
		Cursor cursor=db.query(DATABASE_TABLE, new String[]{KEY_ID, KEY_NAME,KEY_EMAIL}, "where userId=?", new String[]{userId+""}, null, null, null,"1");
		if (cursor.getCount()==0) {
			return null;
		}
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
			_db.execSQL(DATABASE_CREATE);
		}

		// Called when there is a database version mismatch meaning that the
		// version
		// of the database on disk needs to be upgraded to the current version.
		@Override
		public void onUpgrade(SQLiteDatabase _db, int _oldVersion,
				int _newVersion) {
			// Log the version upgrade.
			System.out.println("Upgrading from version " + _oldVersion + " to "
					+ _newVersion + ", which will destroy all old data");
			// Upgrade the existing database to conform to the new version.
			// Multiple
			// previous versions can be handled by comparing _oldVersion and
			// _newVersion
			// values.
			// The simplest case is to drop the old table and create a new one.
			_db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE);
			// Create a new one.
			onCreate(_db);
		}
	}
}
