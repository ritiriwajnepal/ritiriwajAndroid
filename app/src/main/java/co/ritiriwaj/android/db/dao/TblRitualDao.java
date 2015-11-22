package co.ritiriwaj.android.db.dao;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.http.Header;
import org.apache.http.conn.ConnectTimeoutException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;
import co.ritiriwaj.android.MainActivity;
import co.ritiriwaj.android.PersonalRiwajListFragment;
import co.ritiriwaj.android.R;
import co.ritiriwaj.android.RiwajListFragment;
import co.ritiriwaj.android.SingleRitualFragment;
import co.ritiriwaj.android.db.MySQLiteHelper;
import co.ritiriwaj.android.db.model.Ritual;
import co.ritiriwaj.android.db.tables.TblRitual;
import co.ritiriwaj.android.model.Riwaj;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class TblRitualDao {

	private SQLiteDatabase db;
	private MySQLiteHelper dbHelper;
	Cursor cursor;
	private AsyncHttpClient client = new AsyncHttpClient();
	Context context;

	private static String GET_ALL_RITUAL_LIST = MainActivity.IP
			+ "getAllRitualList";

	// private Ritual ritual;

	public TblRitualDao(Context context) {
		this.context = context;
		dbHelper = new MySQLiteHelper(context);

	}

	public void open() throws SQLException {
		db = dbHelper.getWritableDatabase();
	}

	public void close() {
		dbHelper.close();
	}

	public void insertAllRitualList() {

		HashMap<String, String> lastModified = new HashMap<String, String>();

		lastModified.put("last_modified", getLastModifiedRitual());

		RequestParams params = new RequestParams(lastModified);

		client.post(context, GET_ALL_RITUAL_LIST, params,
				new JsonHttpResponseHandler() {

					@Override
					public void onSuccess(int statusCode, Header[] headers,
							JSONObject response) {
						try {
							Log.i("jsonObject", response.toString());

							if (response.has("hasDeletedRecords")) {

								deleteRecords(response);

							}

							if (response.has("hasUpdate")) {
								// INSERT the retrieved data to SQLite and
								// populate riwajList
								if (response.getBoolean("hasUpdate")) {
									Log.i("has update",
											response.getBoolean("hasUpdate")
													+ "");
									InsertAndPopulateRiwajList insertAndPopulateRiwajList = new InsertAndPopulateRiwajList();
									insertAndPopulateRiwajList.execute(
											parseJSONforRitualList(response),
											response.getBoolean("hasUpdate"));
								}
							} else if (response.has("isNew")) {
								Log.i("is new", response.getBoolean("isNew")
										+ "");
								InsertAndPopulateRiwajList insertAndPopulateRiwajList = new InsertAndPopulateRiwajList();
								insertAndPopulateRiwajList
										.execute(parseJSONforRitualList(response));
							}
						} catch (JSONException e) {
							e.printStackTrace();
						}

					}

					@Override
					public void onFailure(int statusCode, Header[] headers,
							String responseString, Throwable throwable) {
						if (responseString.contentEquals("null")) {
							Toast.makeText(
									context,
									"Sorry! there are no events listed for this month",
									Toast.LENGTH_SHORT).show();
						}

						if (throwable.getCause() instanceof ConnectTimeoutException) {
							showAlertDialog(context, responseString);
						}
					}

					@Override
					public void onFailure(int statusCode, Header[] headers,
							Throwable throwable, JSONObject errorResponse) {
						if (throwable.getCause() instanceof Exception) {
							Log.e("RITIRIWAJ_EXCEPTION", throwable.getMessage());
							// showAlertDialog(context,
							// "Oops! Some technical errors occured.");
						} else {
							SharedPreferences sharedpref = PreferenceManager
									.getDefaultSharedPreferences(context);
							String dialogMessage;
							if (sharedpref.getBoolean("firstTimeFetch", false)) {
								dialogMessage = "You require Internet to view data for first time.";
								showAlertDialog(context, dialogMessage);
							} else {
								// dialogMessage =
								// "Oops!!! There seems to be a network error.Please check network connection";
							}

						}
					}

				});

	}

	/*
	 * Returns JSONArray of CalendarRituals
	 * 
	 * [{id : value, name : value, image : value, nep_date : value}]
	 */
	public List<Riwaj> getAllCalendarRituals() {
		this.open();
		List<Riwaj> riwajList = new ArrayList<Riwaj>();

		String[] columnNames = new String[] { TblRitual.ID, TblRitual.NAME,
				TblRitual.IMAGE, TblRitual.NEP_DATE, TblRitual.DATE,
				TblRitual.STATUS };
		String whereClause = TblRitual.TYPE + "= 'public' AND date >= date()";

		cursor = db.query(TblRitual.TABLE_NAME, columnNames, whereClause, null,
				null, null, null);

		if (cursor.moveToFirst())
			// if cursor is not empty
			while (!cursor.isAfterLast()) {

				Riwaj riwaj = new Riwaj(cursor.getString(2),
						cursor.getString(1), cursor.getString(3),
						calculateRemDays(cursor.getString(4)), // Calculated
																// Remaining
																// Days
						Integer.valueOf(cursor.getString(0)),
						Integer.valueOf(cursor.getString(5)));
				riwajList.add(riwaj);
				cursor.moveToNext();
			}

		// make sure to close the cursor
		cursor.close();
		this.close();
		// ritualList = cur2Json(cursor);

		// Log.i("RITIRIWAJ_SQLITE", ritualList.toString());
		return riwajList;

	}

	public List<Riwaj> getAllPersonalRituals() {
		this.open();
		List<Riwaj> riwajList = new ArrayList<Riwaj>();

		String[] columnNames = new String[] { TblRitual.ID, TblRitual.NAME,
				TblRitual.IMAGE, TblRitual.STATUS };
		String whereClause = TblRitual.TYPE + "= 'private'";

		cursor = db.query(TblRitual.TABLE_NAME, columnNames, whereClause, null,
				null, null, null);

		if (cursor.moveToFirst())
			// if cursor is not empty
			while (!cursor.isAfterLast()) {

				Riwaj riwaj = new Riwaj(cursor.getString(2),
						cursor.getString(1), Integer.valueOf(cursor
								.getString(0)), Integer.valueOf(cursor
								.getString(3)));
				riwajList.add(riwaj);
				cursor.moveToNext();
			}

		// make sure to close the cursor
		cursor.close();
		this.close();
		// ritualList = cur2Json(cursor);

		// Log.i("RITIRIWAJ_SQLITE", ritualList.toString());
		return riwajList;

	}

	private void deleteRecords(JSONObject response) throws JSONException {

		JSONArray deletedRecords = response.getJSONArray("deletedRecords");

		this.open();

		for (int i = 0; i < deletedRecords.length(); i++) {
			String deletedId = ((JSONObject) deletedRecords.get(i))
					.getString("deleted_id");
			db.delete(TblRitual.TABLE_NAME, TblRitual.ID + "=?",
					new String[] { deletedId });
			Log.i("RITUAL_DELETED", "id : " + deletedId);
		}

		this.close();

		((RiwajListFragment) ((ActionBarActivity) context)
				.getSupportFragmentManager().findFragmentById(
						R.id.fragment_placeholder))
				.populateRitiriwajList(getAllCalendarRituals());

	}

	private String getLastModifiedRitual() {
		this.open();
		Cursor cursor = db.rawQuery("SELECT " + TblRitual.LAST_MODIFIED
				+ " FROM " + TblRitual.TABLE_NAME + " ORDER BY "
				+ TblRitual.LAST_MODIFIED + " DESC LIMIT 1", null);

		String lastmod = "";
		if (cursor != null && cursor.moveToFirst() && cursor.getCount() > 0) {
			// Log.i("cursor", DatabaseUtils.dumpCursorToString(cursor));
			lastmod = cursor.getString(0);
		}
		cursor.close();
		this.close();
		Log.i("lastMod", lastmod);
		return lastmod;

	}

	public JSONArray cur2Json(Cursor cursor) {

		JSONArray resultSet = new JSONArray();
		cursor.moveToFirst();
		while (cursor.isAfterLast() == false) {
			int totalColumn = cursor.getColumnCount();
			JSONObject rowObject = new JSONObject();
			for (int i = 0; i < totalColumn; i++) {
				if (cursor.getColumnName(i) != null) {
					try {
						rowObject.put(cursor.getColumnName(i),
								cursor.getString(i));
					} catch (Exception e) {
						Log.d("RITIRIWAJ_SQLITE", e.getMessage());
					}
				}
			}
			resultSet.put(rowObject);
			cursor.moveToNext();
		}

		cursor.close();
		return resultSet;

	}

	private void showAlertDialog(final Context context, String message) {
		final AlertDialog.Builder builder = new AlertDialog.Builder(context);

		builder.setMessage(message)
				.setCancelable(false)
				.setPositiveButton("Reload",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface arg0, int arg1) {
								arg0.dismiss();
								TblRitualDao.this.insertAllRitualList();
							}
						})
				.setNegativeButton("Cancel",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface arg0, int arg1) {
								arg0.dismiss();
								((Activity) context).finish();
							}
						});
		Log.i("builder", "dialog");
		final AlertDialog alert = builder.create();
		alert.show();
	}

	private class InsertAndPopulateRiwajList extends
			AsyncTask<Object, Void, Void> {

		@SuppressWarnings("unchecked")
		@Override
		protected Void doInBackground(Object... params) {
			List<Ritual> ritualList = new ArrayList<Ritual>();
			boolean hasUpdate = false;

			ritualList = (List<Ritual>) params[0];
			if (params.length > 1)
				hasUpdate = (boolean) params[1];

			insertOrUpdateRitualData(ritualList, hasUpdate);

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {

			// Toast.makeText(context, "Data Updated! Please Reload",
			// Toast.LENGTH_LONG).show();

			// get current fragment class
			Class<? extends Fragment> currentFragment = ((AppCompatActivity) context)
					.getSupportFragmentManager()
					.findFragmentById(R.id.fragment_placeholder).getClass();

			if (currentFragment == RiwajListFragment.class) {
				((RiwajListFragment) ((AppCompatActivity) context)
						.getSupportFragmentManager().findFragmentById(
								R.id.fragment_placeholder))
						.populateRitiriwajList(getAllCalendarRituals());
			} else {
				((PersonalRiwajListFragment) ((AppCompatActivity) context)
						.getSupportFragmentManager().findFragmentById(
								R.id.fragment_placeholder))
						.populateRitiriwajList(getAllCalendarRituals());
			}
			
			super.onPostExecute(result);
		}

	}

	private void insertOrUpdateRitualData(List<Ritual> ritualList,
			boolean hasUpdate) {
		Log.i("insertdata", hasUpdate + "");
		TblRitualDao.this.open();
		db.beginTransactionNonExclusive();
		try {
			for (Ritual ritual : ritualList) {
				ContentValues values = new ContentValues();

				values.put(TblRitual.ID, Integer.valueOf(ritual.getId()));
				values.put(TblRitual.NAME, ritual.getName());
				values.put(TblRitual.IMAGE, ritual.getImage());
				values.put(TblRitual.DATE, ritual.getDate());
				values.put(TblRitual.NEP_DATE, ritual.getNepDate());
				values.put(TblRitual.TYPE, ritual.getType());
				values.put(TblRitual.LAST_MODIFIED, ritual.getLastModified());
				values.put(TblRitual.STATUS, ritual.getStatus());

				// Log.i("RITIRIWAJ", values.toString());
				if (!hasUpdate) {
					db.insert(TblRitual.TABLE_NAME, null, values);
					SharedPreferences sharedpref = PreferenceManager
							.getDefaultSharedPreferences(context);
					sharedpref.edit().putBoolean("firstTimeFetch", false);
				} else
					try {
						/*
						 * This will update the columns name, date,
						 * nep_date,last_modified. When ID=ritual.getId()
						 * exists, other columns will be unaffected. When
						 * ID=ritual.getId() does not exist, values of other
						 * columns will be set to ' ' instead of the default
						 * value.
						 */
						String sql = "INSERT OR REPLACE INTO "
								+ TblRitual.TABLE_NAME + " (" + TblRitual.ID
								+ "," + TblRitual.NAME + "," + TblRitual.IMAGE
								+ "," + TblRitual.DATE + ","
								+ TblRitual.NEP_DATE + "," + TblRitual.TYPE
								+ "," + TblRitual.LAST_MODIFIED + ","
								+ TblRitual.VIDEOURL + "," + TblRitual.WHAT
								+ "," + TblRitual.HISTORY + ","
								+ TblRitual.SIGNIFICANCE + ","
								+ TblRitual.INTERESTING_FACTS + ","
								+ TblRitual.SOURCE + "," + TblRitual.STATUS
								+ ") VALUES ("
								+ Integer.valueOf(ritual.getId()) + "," + "'"
								+ ritual.getName() + "'," + "'"
								+ ritual.getImage() + "'," + "'"
								+ ritual.getDate() + "'," + "'"
								+ ritual.getNepDate() + "'," + "'"
								+ ritual.getType() + "'," + "'"
								+ ritual.getLastModified() + "',"
								+ "COALESCE((SELECT " + TblRitual.VIDEOURL
								+ " FROM " + TblRitual.TABLE_NAME + " WHERE "
								+ TblRitual.ID + " = "
								+ Integer.valueOf(ritual.getId()) + "), ' ')"
								+ "," + "COALESCE((SELECT " + TblRitual.WHAT
								+ " FROM " + TblRitual.TABLE_NAME + " WHERE "
								+ TblRitual.ID + " = "
								+ Integer.valueOf(ritual.getId()) + "), ' ')"
								+ "," + "COALESCE((SELECT " + TblRitual.HISTORY
								+ " FROM " + TblRitual.TABLE_NAME + " WHERE "
								+ TblRitual.ID + " = "
								+ Integer.valueOf(ritual.getId()) + "), ' ')"
								+ "," + "COALESCE((SELECT "
								+ TblRitual.SIGNIFICANCE + " FROM "
								+ TblRitual.TABLE_NAME + " WHERE "
								+ TblRitual.ID + " = "
								+ Integer.valueOf(ritual.getId()) + "), ' ')"
								+ "," + "COALESCE((SELECT "
								+ TblRitual.INTERESTING_FACTS + " FROM "
								+ TblRitual.TABLE_NAME + " WHERE "
								+ TblRitual.ID + " = "
								+ Integer.valueOf(ritual.getId()) + "), ' ')"
								+ "," + "COALESCE((SELECT " + TblRitual.SOURCE
								+ " FROM " + TblRitual.TABLE_NAME + " WHERE "
								+ TblRitual.ID + " = "
								+ Integer.valueOf(ritual.getId()) + "), ' ')"
								+ ",'" + ritual.getStatus() + "')";
						db.execSQL(sql);
					} catch (SQLiteException exception) {
						Log.e("SQLITE EXCEPTION", exception.getMessage());
					}

			}
			Log.i("transaction", "transaction set successful");

			db.setTransactionSuccessful();

		} finally {
			Log.i("transaction", "end transaction");
			db.endTransaction();
			TblRitualDao.this.close();

		}
	}

	private void fetchSingleRitualData(
			final HashMap<String, String> ritualDetailsMap) {
		SharedPreferences preferences = PreferenceManager
				.getDefaultSharedPreferences(context);

		HashMap<String, String> hashMap = new HashMap<String, String>();

		hashMap.put("ritualId", ritualDetailsMap.get("ritualId"));
		hashMap.put("subscriberId", preferences.getString("subscriberId", ""));
		hashMap.put("subscriberFullName",
				preferences.getString("subscriberFullName", ""));
		hashMap.put("subscriberEmail",
				preferences.getString("subscriberEmail", ""));

		RequestParams params = new RequestParams(hashMap);

		client.post(context, SingleRitualFragment.GET_RITUAL_DETAILS, params,
				new JsonHttpResponseHandler() {
					@Override
					public void onSuccess(int statusCode, Header[] headers,
							JSONObject jsonObject) {
						Ritual ritual = null;
						try {

							if (jsonObject.has("eventList")) {
								ritualDetailsMap.put("eventList", jsonObject
										.getJSONArray("eventList").toString());
							}
							// Get Ritual object
							ritual = new Ritual(ritualDetailsMap
									.get("ritualId"), "", "", "", "", "", "",
									jsonObject.getString("what"), jsonObject
											.getString("history"), jsonObject
											.getString("significance"),
									jsonObject.getString("interesting_facts"),
									jsonObject.getString("source"), jsonObject
											.getString("videoURL"), "");

						} catch (IllegalStateException | JSONException e) {
							e.printStackTrace();
							Log.e("JSON error", e.getMessage());
						}
						InsertAndPopulateSingleRiwaj insertAndPopulateSingleRiwaj = new InsertAndPopulateSingleRiwaj();
						insertAndPopulateSingleRiwaj.execute(ritual, true,
								ritualDetailsMap);
					}

					@Override
					public void onFailure(int statusCode, Header[] headers,
							Throwable throwable, JSONObject errorResponse) {
						Log.i("Failure", throwable.getCause() + " JSONObject");
						if (throwable.getCause() instanceof Exception) {
							showAlertDialog(context,
									"Oops!!! so sorry, we encountered a server error");
						} else {
							showAlertDialog(context,
									"Oops!!! There seems to be a network error.Please check network connection");
						}
					}

					@Override
					public void onFailure(int statusCode, Header[] headers,
							String responseString, Throwable throwable) {
						if (throwable.getCause() instanceof Exception) {
							showAlertDialog(context, responseString);
						} else {
							showAlertDialog(context, responseString);
						}
					}
				});
	}

	private class InsertAndPopulateSingleRiwaj extends
			AsyncTask<Object, Void, Void> {
		HashMap<String, String> ritualDetailsMap = new HashMap<String, String>();
		boolean isFetch = false;

		@SuppressWarnings("unchecked")
		@Override
		protected Void doInBackground(Object... params) {

			Ritual ritual = (Ritual) params[0];
			isFetch = (boolean) params[1];
			ritualDetailsMap = (HashMap<String, String>) params[2];
			if (isFetch)
				insertOrUpdateRitualData(ritualDetailsMap, ritual);

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			HashMap<String, String> singleRitualDetails = getSingleRitualDetails(ritualDetailsMap);
			if (singleRitualDetails != null)
				((SingleRitualFragment) ((AppCompatActivity) context)
						.getSupportFragmentManager().findFragmentById(
								R.id.fragment_placeholder))
						.setRitualDetails(singleRitualDetails);
			super.onPostExecute(result);
		}
	}

	private void insertOrUpdateRitualData(
			HashMap<String, String> ritualInfoMap, Ritual ritual) {

		TblRitualDao.this.open();
		db.beginTransactionNonExclusive();
		try {
			try {
				String sql = "INSERT OR REPLACE INTO " + TblRitual.TABLE_NAME
						+ " (" + TblRitual.ID + "," + TblRitual.NAME + ","
						+ TblRitual.IMAGE + "," + TblRitual.DATE + ","
						+ TblRitual.NEP_DATE + "," + TblRitual.TYPE + ","
						+ TblRitual.LAST_MODIFIED + "," + TblRitual.VIDEOURL
						+ "," + TblRitual.WHAT + "," + TblRitual.HISTORY + ","
						+ TblRitual.SIGNIFICANCE + ","
						+ TblRitual.INTERESTING_FACTS + "," + TblRitual.SOURCE
						+ "," + TblRitual.STATUS + ") VALUES ("
						+ Integer.valueOf(ritual.getId()) + ","
						+ "COALESCE((SELECT " + TblRitual.NAME + " FROM "
						+ TblRitual.TABLE_NAME + " WHERE " + TblRitual.ID
						+ " = "
						+ Integer.valueOf(ritualInfoMap.get("ritualId"))
						+ "), ' ')" + "," + "COALESCE((SELECT "
						+ TblRitual.IMAGE + " FROM " + TblRitual.TABLE_NAME
						+ " WHERE " + TblRitual.ID + " = "
						+ Integer.valueOf(ritualInfoMap.get("ritualId"))
						+ "), ' ')" + "," + "COALESCE((SELECT "
						+ TblRitual.DATE + " FROM " + TblRitual.TABLE_NAME
						+ " WHERE " + TblRitual.ID + " = "
						+ Integer.valueOf(ritualInfoMap.get("ritualId"))
						+ "), ' ')" + "," + "COALESCE((SELECT "
						+ TblRitual.NEP_DATE + " FROM " + TblRitual.TABLE_NAME
						+ " WHERE " + TblRitual.ID + " = "
						+ Integer.valueOf(ritualInfoMap.get("ritualId"))
						+ "), ' ')" + "," + "COALESCE((SELECT "
						+ TblRitual.TYPE + " FROM " + TblRitual.TABLE_NAME
						+ " WHERE " + TblRitual.ID + " = "
						+ Integer.valueOf(ritualInfoMap.get("ritualId"))
						+ "), ' ')" + "," + "COALESCE((SELECT "
						+ TblRitual.LAST_MODIFIED + " FROM "
						+ TblRitual.TABLE_NAME + " WHERE " + TblRitual.ID
						+ " = "
						+ Integer.valueOf(ritualInfoMap.get("ritualId"))
						+ "), ' ')," + "'" + ritual.getVideoURL() + "'," + "'"
						+ ritual.getWhat() + "'," + "'" + ritual.getHistory()
						+ "'," + "'" + ritual.getSignificance() + "'," + "'"
						+ ritual.getInterestingFacts() + "'," + "'"
						+ ritual.getSource() + "'," + "COALESCE((SELECT "
						+ TblRitual.STATUS + " FROM " + TblRitual.TABLE_NAME
						+ " WHERE " + TblRitual.ID + " = "
						+ Integer.valueOf(ritualInfoMap.get("ritualId"))
						+ "), ' ')" + ")";
				Log.wtf("INSERT QUERY: ", sql);
				db.execSQL(sql);
			} catch (SQLiteException exception) {
				Log.e("SQLITE EXCEPTION", exception.getMessage());
			}

			Log.i("transaction", "transaction set successful");

			db.setTransactionSuccessful();

		} finally {
			Log.i("transaction", "end transaction");
			db.endTransaction();
			TblRitualDao.this.close();

		}
	}

	public HashMap<String, String> getSingleRitualDetails(
			HashMap<String, String> ritualDetailsMap) {
		this.open();

		String[] columnNames = new String[] { TblRitual.WHAT,
				TblRitual.HISTORY, TblRitual.SIGNIFICANCE,
				TblRitual.INTERESTING_FACTS, TblRitual.SOURCE,
				TblRitual.VIDEOURL };
		String whereClause = TblRitual.ID + "= '"
				+ ritualDetailsMap.get("ritualId") + "'";

		cursor = db.query(TblRitual.TABLE_NAME, columnNames, whereClause, null,
				null, null, null);
		Log.i("cursor_ritualDetails", DatabaseUtils.dumpCursorToString(cursor));
		if (cursor.moveToFirst())
		// if cursor is not empty
		{
			ritualDetailsMap.put("ritualWhat", cursor.getString(0));
			ritualDetailsMap.put("ritualHistory", cursor.getString(1));
			ritualDetailsMap.put("ritualSignificance", cursor.getString(2));
			ritualDetailsMap.put("ritualInteresting", cursor.getString(3));
			ritualDetailsMap.put("ritualSource", cursor.getString(4));
			ritualDetailsMap.put("ritualVideo", cursor.getString(5));
		}

		// make sure to close the cursor
		cursor.close();
		this.close();
		if (ritualDetailsMap.get("ritualWhat") == null) {
			fetchSingleRitualData(ritualDetailsMap);
			return null;
		} else {
			((SingleRitualFragment) ((AppCompatActivity) context)
					.getSupportFragmentManager().findFragmentById(
							R.id.fragment_placeholder))
					.setRitualDetails(ritualDetailsMap);
			return ritualDetailsMap;
		}
	}

	private List<Ritual> parseJSONforRitualList(JSONObject response) {

		List<Ritual> ritualList = new ArrayList<Ritual>();

		try {

			JSONArray jsonArray = response.getJSONArray("ritualList");

			for (int i = 0; i < jsonArray.length(); i++) {
				Ritual ritual = new Ritual(
						((JSONObject) jsonArray.get(i)).getString("id"),
						((JSONObject) jsonArray.get(i)).getString("name"),
						((JSONObject) jsonArray.get(i)).getString("image"),
						((JSONObject) jsonArray.get(i)).getString("date"),
						((JSONObject) jsonArray.get(i)).getString("nep_date"),
						((JSONObject) jsonArray.get(i)).getString("type"),
						((JSONObject) jsonArray.get(i))
								.getString("last_modified"), "", "", "", "",
						"", "",
						((JSONObject) jsonArray.get(i)).getString("status"));
				ritualList.add(i, ritual);

			}

		} catch (JSONException e) {
			Log.e("JSONException", e.getMessage());
		}
		return ritualList;
	}

	public List<Riwaj> getMonthlyRituals(String month) {
		this.open();
		List<Riwaj> riwajList = new ArrayList<Riwaj>();

		String[] columnNames = new String[] { TblRitual.ID, TblRitual.NAME,
				TblRitual.IMAGE, TblRitual.NEP_DATE, TblRitual.DATE,
				TblRitual.STATUS };
		String whereClause = TblRitual.TYPE + "= 'public' AND strftime('%m', `"
				+ TblRitual.NEP_DATE + "`) = '" + month + "'";
		Log.i("whereClause", whereClause);

		cursor = db.query(TblRitual.TABLE_NAME, columnNames, whereClause, null,
				null, null, null);
		Log.i("cursor_monthlyRituals", DatabaseUtils.dumpCursorToString(cursor));
		if (cursor.moveToFirst())
			// if cursor is not empty
			while (!cursor.isAfterLast()) {
				Riwaj riwaj = new Riwaj(cursor.getString(2),
						cursor.getString(1), cursor.getString(3),
						calculateRemDays(cursor.getString(4)),
						Integer.valueOf(cursor.getString(0)),
						Integer.valueOf(cursor.getString(5)));
				riwajList.add(riwaj);
				cursor.moveToNext();
			}

		// make sure to close the cursor
		cursor.close();
		this.close();
		// ritualList = cur2Json(cursor);

		// Log.i("RITIRIWAJ_SQLITE", ritualList.toString());
		return riwajList;
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
