package com.raymondchen.godutch.dao;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase.CursorFactory;

import com.raymondchen.godutch.Expense;
import com.raymondchen.godutch.Util;

public class ExpenseDbAdapter {
	private static final String DATABASE_TABLE = "expense";
	// The index (key) column name for use in where clauses.
	public static final String KEY_ID = "expenseId";
	// 定义各个其它字段以及它们的序号
	public static final String KEY_TRIP_ID = "tripId";
	public static final int TRIP_ID_COLUMN = 1;
	public static final String KEY_NAME = "name";
	public static final int NAME_COLUMN = 2;
	public static final String KEY_AMOUNT = "amount";
	public static final int AMOUNT_COLUMN = 3;
	public static final String KEY_SHARED_USER_IDS = "sharedUserIds";
	public static final int SHARED_USER_IDS_COLUMN = 4;
	public static final String KEY_TIME = "time";
	public static final int TIME_COLUMN = 5;
	public static final String KEY_PAID_USER_ID = "paidUserId";
	public static final int PAID_USER_ID_COLUMN = 6;

	// 建表语句
	public static final String DATABASE_CREATE = "create table "
			+ DATABASE_TABLE + " (" + KEY_ID
			+ " integer primary key autoincrement, " + KEY_TRIP_ID
			+ " integer, " + KEY_NAME + " text not null, " + KEY_AMOUNT
			+ " real, " + KEY_SHARED_USER_IDS + " text, " + KEY_TIME
			+ " text default (datetime('now')), " + KEY_PAID_USER_ID
			+ " integer);";
	// Variable to hold the database instance
	private SQLiteDatabase db;
	// Context of the application using the database.
	private final Context context;
	// Database open/upgrade helper
	private myDbHelper dbHelper;

	public ExpenseDbAdapter(Context _context) {
		context = _context;
		dbHelper = new myDbHelper(context, DbUtil.DATABASE_NAME, null,
				DbUtil.CURRENT_DATABASE_VERSION);
	}

	public ExpenseDbAdapter open() {
		db = dbHelper.getWritableDatabase();
		return this;
	}

	public void close() {
		db.close();
	}

	public long insertEntry(Expense expense) {
		if (expense == null) {
			throw new IllegalArgumentException("user argument must not be null");
		}
		open();
		ContentValues newValues = new ContentValues();
		newValues.put(KEY_TRIP_ID, expense.getTripId());
		newValues.put(KEY_NAME, expense.getName());
		newValues.put(KEY_AMOUNT, expense.getAmount());
		newValues.put(KEY_SHARED_USER_IDS, expense.getSharedUserIds());
		newValues.put(KEY_PAID_USER_ID, expense.getPaidUserId());
		Long index = db.insert(DATABASE_TABLE, null, newValues);
		close();
		return index;
	}

	public boolean removeEntry(long expenseId) {
		open();
		boolean result = db.delete(DATABASE_TABLE, KEY_ID + "=" + expenseId,
				null) > 0;
		close();
		return result;
	}
	
	public boolean removeEntriesByTripId(long tripId) {
		open();
		boolean result = db.delete(DATABASE_TABLE, KEY_TRIP_ID + "=" + tripId,
				null) > 0;
		close();
		return result;
	}

	public List<Expense> getAllEntries() {
		open();
		Cursor cursor = db.query(DATABASE_TABLE, new String[] { KEY_ID,
				KEY_TRIP_ID, KEY_NAME, KEY_AMOUNT, KEY_SHARED_USER_IDS,
				KEY_TIME, KEY_PAID_USER_ID }, null, null, null, null, null);
		List<Expense> list = new ArrayList<Expense>();
		if (cursor.moveToFirst()) {
			do {
				Expense expense = new Expense();
				expense.setExpenseId(cursor.getLong(0));
				expense.setTripId(cursor.getLong(TRIP_ID_COLUMN));
				expense.setName(cursor.getString(NAME_COLUMN));
				expense.setAmount(cursor.getDouble(AMOUNT_COLUMN));
				expense.setSharedUserIds(cursor
						.getString(SHARED_USER_IDS_COLUMN));
				expense.setPaidUserId(cursor.getLong(PAID_USER_ID_COLUMN));
				try {
					expense.setTime(Util.getDateFromString(cursor
							.getString(TIME_COLUMN)));
				} catch (ParseException e) {
					expense.setTime(new Date()); // 出错时设置为现在
				}
				list.add(expense);
			} while (cursor.moveToNext());
		}
		cursor.close();
		close();
		return list;
	}

	public List<Expense> getExpenseListByTripId(long tripId) {
		open();
		Cursor cursor = db.query(DATABASE_TABLE, new String[] { KEY_ID,
				KEY_TRIP_ID, KEY_NAME, KEY_AMOUNT, KEY_SHARED_USER_IDS,
				KEY_TIME, KEY_PAID_USER_ID }, KEY_TRIP_ID + " = ?",
				new String[] { tripId + "" }, null, null, null);
		List<Expense> list = new ArrayList<Expense>();
		if (cursor.moveToFirst()) {
			do {
				Expense expense = new Expense();
				expense.setExpenseId(cursor.getLong(0));
				expense.setTripId(cursor.getLong(TRIP_ID_COLUMN));
				expense.setName(cursor.getString(NAME_COLUMN));
				expense.setAmount(cursor.getDouble(AMOUNT_COLUMN));
				expense.setSharedUserIds(cursor
						.getString(SHARED_USER_IDS_COLUMN));
				expense.setPaidUserId(cursor.getLong(PAID_USER_ID_COLUMN));
				try {
					expense.setTime(Util.getDateFromString(cursor
							.getString(TIME_COLUMN)));
				} catch (ParseException e) {
					expense.setTime(new Date()); // 出错时设置为现在
				}
				list.add(expense);
			} while (cursor.moveToNext());
		}
		cursor.close();
		close();
		return list;
	}

	public Expense getEntry(long expenseId) {
		open();
		Cursor cursor = db.query(DATABASE_TABLE, new String[] { KEY_ID,
				KEY_TRIP_ID, KEY_NAME, KEY_AMOUNT, KEY_SHARED_USER_IDS,
				KEY_TIME, KEY_PAID_USER_ID }, KEY_ID + " = ?",
				new String[] { expenseId + "" }, null, null, null);
		if (cursor.getCount() == 0) {
			cursor.close();
			close();
			return null;
		}
		cursor.moveToFirst();
		Expense expense = new Expense();
		expense.setExpenseId(cursor.getLong(0));
		expense.setTripId(cursor.getLong(TRIP_ID_COLUMN));
		expense.setName(cursor.getString(NAME_COLUMN));
		expense.setAmount(cursor.getDouble(AMOUNT_COLUMN));
		expense.setSharedUserIds(cursor.getString(SHARED_USER_IDS_COLUMN));
		expense.setPaidUserId(cursor.getLong(PAID_USER_ID_COLUMN));
		try {
			expense.setTime(Util.getDateFromString(cursor
					.getString(TIME_COLUMN)));
		} catch (ParseException e) {
			expense.setTime(new Date()); // 出错时设置为现在
		}
		cursor.close();
		close();
		return expense;
	}

	public boolean updateEntry(Expense expense) {
		if (expense == null) {
			throw new IllegalArgumentException(
					"expense argument must not be null");
		}
		open();
		ContentValues updatedValues = new ContentValues();
		updatedValues.put(KEY_TRIP_ID, expense.getTripId());
		updatedValues.put(KEY_NAME, expense.getName());
		updatedValues.put(KEY_AMOUNT, expense.getAmount());
		updatedValues.put(KEY_SHARED_USER_IDS, expense.getSharedUserIds());
		updatedValues.put(KEY_PAID_USER_ID, expense.getPaidUserId());
		String where = "expenseId = ?";
		db.update(DATABASE_TABLE, updatedValues, where,
				new String[] { expense.getExpenseId() + "" });
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
			System.out.println("do nothing ");
		}

		// Called when there is a database version mismatch meaning that the
		// version
		// of the database on disk needs to be upgraded to the current version.
		@Override
		public void onUpgrade(SQLiteDatabase _db, int _oldVersion,
				int _newVersion) {
			System.out.println("Per-table upgrade is disabled");
		}
	}

}
