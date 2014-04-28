package com.example.appreader;

public class Constants {
	public static final String DATABASE_NAME = "TopGrossingApps.db";
	public static final int DATABASE_VERSION = 1;
	public static final String TABLE_NAME = "favorites";
	public static final String KEY_NAME = "name";
	public static final String KEY_FAVORITE = "Favorite";
	public static final String KEY_ID = "id integer primary key autoincrement";
	public static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS favorites (" + Constants.KEY_ID + "," 
		+ Constants.KEY_NAME + " text," +  Constants.KEY_FAVORITE + " integer);";
}
