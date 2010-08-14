package com.raymondchen.godutch.dao;



import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DbUtil {
	
	public static final int CURRENT_DATABASE_VERSION=1;
	public static final String DATABASE_NAME="godutch.db";
	
	public static void checkDbSchemaVersion(Context context) {
		SQLiteOpenHelper helper=new SQLiteOpenHelper(context,DbUtil.DATABASE_NAME,null,DbUtil.CURRENT_DATABASE_VERSION){

			@Override
			public void onCreate(SQLiteDatabase db) {
				db.execSQL(ExpenseDbAdapter.DATABASE_CREATE);
				db.execSQL(TripDbAdapter.DATABASE_CREATE);
				db.execSQL(UserDbAdapter.DATABASE_CREATE);
			}
			@Override
			public void onUpgrade(SQLiteDatabase db, int oldVersion,
					int newVersion) {
				System.out.println("Upgrading from version " + oldVersion + " to "
						+ newVersion
						+ ", here are the alter table statements go: "
						);
				updateDbSchemaVersion(oldVersion, newVersion);
			}
			
		};
		SQLiteDatabase db=helper.getReadableDatabase();
		System.out.println("db version="+db.getVersion());
		db.close();
	}
	
	/**
	 * 将旧版本从旧到最新的每组DML SQL语句执行一次 
	 * @param oldVersion
	 * @param latestVersion
	 */
	private static void updateDbSchemaVersion(int oldVersion, int latestVersion) {
		if (oldVersion>=latestVersion) return;
		System.out.println("Upgrading DB schema from "+oldVersion +" to "+(oldVersion+1));
		oldVersion++;
		switch (oldVersion) {
			case 2:
				break;
			case 3:
				break;
		}	
		updateDbSchemaVersion(oldVersion,latestVersion);
	}


}
