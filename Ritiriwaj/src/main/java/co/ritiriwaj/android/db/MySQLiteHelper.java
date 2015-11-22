package co.ritiriwaj.android.db;

import co.ritiriwaj.android.db.tables.TblPersonalCalendar;
import co.ritiriwaj.android.db.tables.TblRitual;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MySQLiteHelper extends SQLiteOpenHelper {

	private static final String DB_NAME = "db_ritiriwaj.db";
	private static final int DB_VERSION = 1; 
	
	public MySQLiteHelper(Context context) {
		super(context, DB_NAME, null, DB_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// Tables to be created
		db.execSQL(TblRitual.CREATE_TABLE);
		db.execSQL(TblPersonalCalendar.CREATE_TABLE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// Only if database structure needs to be modified
	}

}
