package co.ritiriwaj.android.db.tables;

public class TblPersonalCalendar {

	public static String TABLE_NAME = "personal_calendar";

	public static String ID = "id";
	public static String RITUAL_ID = "ritual_id";
	public static String TITLE = "title";
	public static String DATE = "date";

	public static String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + "(" + ID
			+ " INTEGER PRIMARY KEY," + RITUAL_ID + " INTEGER," + TITLE
			+ " TEXT," + DATE + " TEXT" + ")";

}
