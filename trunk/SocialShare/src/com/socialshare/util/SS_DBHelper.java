package com.socialshare.util;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabaseCorruptException;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

import com.socialshare.datatypes.DT_MyPosts;

public class SS_DBHelper extends SQLiteOpenHelper {
    
	private static final String DATABASE_NAME = "SS_Database.db";
	private static final int DATABASE_VERSION = 1;

	public static final String TB_MY_POSTS 	= "myposts";

	private static final String EXE_TBL_MY_POSTS = "" +
			" CREATE TABLE IF NOT EXISTS '" + TB_MY_POSTS + "' (" +
	/* 0 */	" 'id' 				INTEGER PRIMARY KEY  AUTOINCREMENT  NOT NULL  UNIQUE," +
	/* 1 */	" 'content'			TEXT," +
	/* 2 */	" 'facebook'		TEXT," +
	/* 3 */	" 'twiter' 			TEXT," +
	/* 4 */	" 'google' 			TEXT" +
			" )";
	
	public SS_DBHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(EXE_TBL_MY_POSTS);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	}

	public SQLiteDatabase init() {
		SQLiteDatabase mDB = null;
		try {
			mDB = getWritableDatabase();
		} catch (SQLiteDatabaseCorruptException e) {
			SS_Util.showMessage("Error!", "DB corrupted!", "OK");
			SS_Log.printStackTrace(e);
		} catch (SQLiteException e) {
			SS_Util.showMessage("Error!", "DB operations have some problems!", "OK");
			SS_Log.printStackTrace(e);
		} catch (Exception e) {
			SS_Util.showMessage("Error!", "DB operations have some problems!", "OK");
			SS_Log.printStackTrace(e);
		}
		return mDB;
		
	}

	public long addPost(DT_MyPosts myPosts) {
		SQLiteDatabase mDB = init();
		if (null == mDB) return 0;
		try {
			ContentValues values = new ContentValues();
			values.put("content", myPosts.content);
			values.put("facebook", myPosts.facebook);
			values.put("twiter", myPosts.twiter);
			values.put("google", myPosts.google);
			return mDB.insert(TB_MY_POSTS, "NULL", values);
		} catch (SQLiteDatabaseCorruptException e) {
			SS_Util.showMessage("Error!", "DB corrupted!", "OK");
			SS_Log.printStackTrace(e);
		} catch (SQLiteException e) {
			SS_Util.showMessage("Error!", "DB operations have some problems!", "OK");
			SS_Log.printStackTrace(e);
		} catch (Exception e) {
			SS_Util.showMessage("Error!", "DB operations have some problems!", "OK");
			SS_Log.printStackTrace(e);
		} finally {
			close();
		}
		return 0;
	}
	
	public ArrayList<DT_MyPosts> selectMyPosts() {
		ArrayList<DT_MyPosts> symbolList = new ArrayList<DT_MyPosts>();
		SQLiteDatabase mDB = init();
		if (null != mDB) {
			Cursor cursor = null;
			try {
				String sSql = "SELECT * FROM " + TB_MY_POSTS;
				cursor = mDB.rawQuery(sSql, null);
				if (cursor.moveToFirst()) {
					do {
						DT_MyPosts symbolItem 	= new DT_MyPosts();
						symbolItem.content  	= cursor.getString(1);
						symbolItem.facebook    	= cursor.getString(2);
						symbolItem.twiter     	= cursor.getString(3);
						symbolItem.google   	= cursor.getString(4);
						symbolList.add(symbolItem);
					} while (cursor.moveToNext());
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				cursor.close();
				close();
			}
		}
		return symbolList;
	}

	public boolean updateSocialMediaStatus(long postID, String socialMedia, String status) {
		SQLiteDatabase mDB = init();
		if (null == mDB)
			return false;
		try {
			String columnName = socialMedia.equalsIgnoreCase("facebook") ? "facebook" 
								: socialMedia.equalsIgnoreCase("twiter") ? "twiter"
								: "google";
			mDB.execSQL("UPDATE " + TB_MY_POSTS + " SET " + columnName + " = '" + status + "' WHERE id = " + postID + "");
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			close();
		}
		return false;
	}
}
