package co.ritiriwaj.android.db.dao;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import co.ritiriwaj.android.MainActivity;
import co.ritiriwaj.android.db.MySQLiteHelper;
import co.ritiriwaj.android.db.tables.TblPersonalCalendar;
import co.ritiriwaj.android.db.tables.TblRitual;
import co.ritiriwaj.android.model.Riwaj;

public class TblPersonalCalendarDao {

	private SQLiteDatabase db;
	private MySQLiteHelper dbHelper;
	private AsyncHttpClient client = new AsyncHttpClient();
	Cursor cursor;
	Context context;

	private static String GET_PERSONAL_EVENTS = MainActivity.IP
			+ "getPersonalEvents";

	public TblPersonalCalendarDao(Context context) {
		this.context = context;
		dbHelper = new MySQLiteHelper(context);

	}

	public void open() throws SQLException {
		db = dbHelper.getWritableDatabase();
	}

	public void close() {
		dbHelper.close();
	}

	public void insertPersonalEvent(HashMap<String, String> eventDetails) {
		
		TblPersonalCalendarDao.this.open();
		
		ContentValues values = new ContentValues();
		values.put(TblPersonalCalendar.ID,
				Integer.valueOf(eventDetails.get("id").toString()));
		values.put(TblPersonalCalendar.RITUAL_ID,
				Integer.valueOf(eventDetails.get("ritual_id").toString()));
		values.put(TblPersonalCalendar.TITLE, eventDetails.get("title")
				.toString());
		values.put(TblPersonalCalendar.DATE, eventDetails.get("date")
				.toString());

		db.insert(TblPersonalCalendar.TABLE_NAME, null, values);
		TblPersonalCalendarDao.this.close();

	}

	public void insertAllPersonalEvents(int subscriberId) {

		HashMap<String, String> hashmaps = new HashMap<>();
		hashmaps.put("subscriberId", String.valueOf(subscriberId));
		RequestParams params = new RequestParams(hashmaps);

		client.post(context, GET_PERSONAL_EVENTS, params,
				new JsonHttpResponseHandler() {

					@Override
					public void onSuccess(int statusCode, Header[] headers,
							JSONArray response) {

						if (response.toString() != "[]") {

							TblPersonalCalendarDao.this.open();
							for (int i = 0; i < response.length(); i++) {
								ContentValues values = new ContentValues();
								try {

									values.put(TblPersonalCalendar.ID, Integer
											.valueOf(response.getJSONObject(i)
													.getString("id")));
									values.put(TblPersonalCalendar.RITUAL_ID,
											Integer.valueOf(response
													.getJSONObject(i)
													.getString("ritual_id")));
									values.put(TblPersonalCalendar.TITLE,
											response.getJSONObject(i)
													.getString("title"));
									values.put(TblPersonalCalendar.DATE,
											response.getJSONObject(i)
													.getString("date"));

								} catch (Exception e) {
									Log.e("insertAllPersonalEvents()",
											e.getMessage());
								}

								db.insert(TblPersonalCalendar.TABLE_NAME, null,
										values);

							}

							TblPersonalCalendarDao.this.close();
						}

					}

				});
	}

	public void deleteAllPersonalEvents(){
		TblPersonalCalendarDao.this.open();
		db.execSQL("DELETE FROM "+ TblPersonalCalendar.TABLE_NAME);
		TblPersonalCalendarDao.this.close();
	}
	
	public List<HashMap<String, String>> getPersonalEvents(int subscriberId,
			int ritualId) {

		this.open();
		List<HashMap<String, String>> eventList = new ArrayList<>();

		String[] columnNames = new String[] { TblPersonalCalendar.ID,
				TblPersonalCalendar.TITLE, TblPersonalCalendar.DATE };
		String whereClause = TblPersonalCalendar.RITUAL_ID + "=" + ritualId;

		cursor = db.query(TblPersonalCalendar.TABLE_NAME, columnNames,
				whereClause, null, null, null, null);

		if (cursor.moveToFirst())
			// if cursor is not empty
			while (!cursor.isAfterLast()) {

				HashMap<String, String> event = new HashMap<>();

				event.put("id", cursor.getString(0));
				event.put("title", cursor.getString(1));
				event.put("date", cursor.getString(2));
				event.put("remDay", calculateRemDays(cursor.getString(2)));

				eventList.add(event);

				cursor.moveToNext();
			}

		// make sure to close the cursor
		cursor.close();
		this.close();

		return eventList;

	}

	private String calculateRemDays(String actual) {
		String remTime = "";
		Calendar cal = Calendar.getInstance();
		String[] trimmed = actual.split("-");
		cal.set(Calendar.YEAR, Integer.valueOf(trimmed[0]));
		cal.set(Calendar.MONTH, Integer.valueOf(trimmed[1]) - 1);
		cal.set(Calendar.DAY_OF_MONTH, Integer.valueOf(trimmed[2]));
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		Date today = new Date();

		long togo = (cal.getTimeInMillis() - today.getTime()) / 1000;

		// togo in seconds
		// remTime = (togo % 60)+"seconds";
		togo = togo / 60;
		// togo in minutes
		String mins = (togo % 60) + " m";
		togo = togo / 60;
		// togo in hours
		String hours = (togo % 24) + " h";
		togo = togo / 24;
		// togo in days
		if (togo % 7 < 0)
			return "Passed this year";
		String days = (togo % 7) + " D";
		togo = togo / 7;
		if (togo < 0)
			return "Passed this year";
		// togo in weeks
		String weeks = (togo) + " W";

		if (!weeks.contentEquals("0 W"))
			if (Integer.valueOf(weeks.replace(" W", "")) > 1)
				remTime = weeks.replace("W", "Weeks");
			else
				remTime = weeks.replace("W", "Week");

		if (!days.contentEquals("0 D"))
			if (Integer.valueOf(days.replace(" D", "")) > 1)
				remTime = remTime + " " + days.replace("D", "Days");
			else
				remTime = remTime + " " + days.replace("D", "Day");

		if (!hours.contentEquals("0 h"))
			remTime = remTime + " " + hours;
		// if (!mins.contentEquals("0 m"))
		// remTime = remTime + " " + mins;

		// if (remTime.contentEquals(" " + hours + " " + mins)) {
		if (remTime.contentEquals(" " + hours)) {
			if (Integer.valueOf(hours.replace(" h", "")) > 0)
				return "Tomorrow";
			else
				return "Today";
		}
		return "in " + remTime;
	}

}
