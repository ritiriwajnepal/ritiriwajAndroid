package co.ritiriwaj.android;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;
import co.ritiriwaj.android.db.dao.TblPersonalCalendarDao;
import co.ritiriwaj.android.db.dao.TblRitualDao;
import co.ritiriwaj.android.db.tables.TblPersonalCalendar;
import co.ritiriwaj.android.helper.SimpleConnectionCheck;
import co.ritiriwaj.android.model.Riwaj;
import co.ritiriwaj.android.rowadapter.RiwajRowAdapter;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.nhaarman.listviewanimations.appearance.simple.SwingBottomInAnimationAdapter;

public class PersonalRiwajListFragment extends ListFragment {
	private static String GET_RITUAL_URL = MainActivity.IP
			+ "getAllPersonalRitual";
	List<Riwaj> rowItems;
	private static AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
	private TblRitualDao tblRitualDao;

	// ShowAlertDialog alertDialog;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRetainInstance(true);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if (MainActivity.bottomToolBar != null) {
			MainActivity.bottomToolBar.getMenu().clear();
			MainActivity.bottomToolBar.setVisibility(View.GONE);
		}
		MainActivity.toolbar.setTitle(getString(R.string.app_name) + " - "
				+ "Personal Events");
		return super.onCreateView(inflater, container, savedInstanceState);
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putSerializable("riwajList", (Serializable) rowItems);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		MainActivity.progressBarMain.setVisibility(View.VISIBLE);
		getListView().setDivider(
				getResources().getDrawable(android.R.color.transparent));
		if (savedInstanceState != null) {
			Log.i("RiwajListFragment", "Restoring Fragment Data");
			rowItems = (List<Riwaj>) savedInstanceState
					.getSerializable("riwajList");
			if (rowItems != null)
				populateRitiriwajList(rowItems); // inject the event list in row
													// adapter
			else
				doShit(); // download the personal event list from server
		} else {
			if (rowItems != null) {
				Log.i("RiwajListFragment", "Leaving everything as is");
				MainActivity.progressBarMain.setVisibility(View.INVISIBLE);
				return;
			} else {
				Log.i("RiwajListFragment", "Fetching Fragment Data");

				// doShit();
				newDoShit();
			}
		}

	}

	public void newDoShit() {
		tblRitualDao = new TblRitualDao(getActivity());

		SharedPreferences preferences = PreferenceManager
				.getDefaultSharedPreferences(getActivity());
		List<Riwaj> riwajList;
		if (preferences.getString("subscriberId", "") != "") {
			riwajList = tblRitualDao.getAllPersonalRituals();

			// get event list also
			TblPersonalCalendarDao tblPersonalCalendarDao = new TblPersonalCalendarDao(
					getActivity());
			List<HashMap<String, String>> eventList = new ArrayList<>();

			for (Riwaj riwaj : riwajList) {
				int subscriberId = Integer.valueOf(preferences.getString(
						"subscriberId", ""));

				eventList = tblPersonalCalendarDao.getPersonalEvents(
						subscriberId, riwaj.getRiwajId());

				if (!eventList.isEmpty())
					riwaj.setEventList(eventList);
			}

			Log.i("RiwajListwithEvents", riwajList.toString());

		} else {
			riwajList = tblRitualDao.getAllPersonalRituals();
		}

		SimpleConnectionCheck check = new SimpleConnectionCheck(getActivity());

		if (!check.isNetworkConnected()) {
			// If exists, fetch & populate Local Data
			if (!riwajList.isEmpty()) {
				Log.i("RITIRIWAJ_SQLITE",
						"Ritual List Exists in Local Database");
				populateRitiriwajList(riwajList);
			} else {

				SharedPreferences sharedpref = PreferenceManager
						.getDefaultSharedPreferences(getActivity());
				sharedpref.edit().putBoolean("firstTimeFetch", true).commit();

				tblRitualDao.insertAllRitualList();
				populateRitiriwajList(riwajList);
			}

		} else {
			SharedPreferences sharedpref = PreferenceManager
					.getDefaultSharedPreferences(getActivity());
			sharedpref.edit().putBoolean("firstTimeFetch", true);

			tblRitualDao.insertAllRitualList();
			populateRitiriwajList(riwajList);
		}
	}

	@Override
	public void onDestroy() {
		asyncHttpClient.cancelRequests(getActivity(), true);
		asyncHttpClient.cancelAllRequests(true);
		super.onDestroy();
	}

	@Override
	public void onPause() {
		asyncHttpClient.cancelRequests(getActivity(), true);
		asyncHttpClient.cancelAllRequests(true);
		super.onPause();
	}

	public void populateRitiriwajList(List<Riwaj> listMaps) {
		rowItems = listMaps;

		MainActivity.progressBarMain.setVisibility(View.INVISIBLE);

		RiwajRowAdapter adapter = new RiwajRowAdapter(getActivity(),
				R.layout.row_ritiriwaj, listMaps);
		SwingBottomInAnimationAdapter swingBottomInAnimationAdapter = new SwingBottomInAnimationAdapter(
				adapter);
		swingBottomInAnimationAdapter.setAbsListView(getListView());
		setListAdapter(swingBottomInAnimationAdapter);
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		if (rowItems.get(position).getStatus() != 0) {
			// initialise singleRiwajFragment and pass some data to it

			SingleRitualFragment singleRitualFragment = new SingleRitualFragment();

			Bundle args = new Bundle();

			args.putString("riwajTitle", rowItems.get(position).getTitle());
			args.putInt("riwajId", rowItems.get(position).getRiwajId());
			args.putString("imageURL", rowItems.get(position).getImageURL());
			args.putString("riwajDesc", rowItems.get(position).getDesc());
			args.putString("riwajDate", rowItems.get(position).getDate());
			// singleRiwajFragment.setArguments(args);
			singleRitualFragment.setArguments(args);

			// begin fragment transaction
			FragmentManager fm = getFragmentManager();
			FragmentTransaction ft = fm.beginTransaction();

			// ft.setCustomAnimations(R.anim.slide_in_right,
			// R.anim.slide_out_left);
			// ft.setCustomAnimations(R.anim.enter_from_right,
			// R.anim.exit_to_left);

			// ft.replace(R.id.fragment_placeholder, singleRiwajFragment);
			ft.replace(R.id.fragment_placeholder, singleRitualFragment);
			ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
			ft.addToBackStack(null);
			ft.commit();
		} else {
			Toast.makeText(getActivity(), "No data for this event",
					Toast.LENGTH_LONG).show();
		}
	}

	public void doShit() {

		SharedPreferences preferences = PreferenceManager
				.getDefaultSharedPreferences(getActivity());

		HashMap<String, String> hashMap = new HashMap<String, String>();
		hashMap.put("subscriberId", preferences.getString("subscriberId", ""));
		hashMap.put("subscriberFullName",
				preferences.getString("subscriberFullName", ""));
		hashMap.put("subscriberEmail",
				preferences.getString("subscriberEmail", ""));

		RequestParams params = new RequestParams(hashMap);
		asyncHttpClient.post(getActivity(), GET_RITUAL_URL, params,
				new JsonHttpResponseHandler() {
					@Override
					public void onSuccess(int statusCode, Header[] headers,
							JSONArray response) {
						List<Riwaj> listMaps = new ArrayList<Riwaj>();
						try {

							Riwaj riwaj;
							for (int i = 0; i < response.length(); i++) {

								if (((JSONObject) response.get(i))
										.has("eventList")) {
									JSONArray eventArray = ((JSONObject) response
											.get(i)).getJSONArray("eventList");
									// Log.i("EVENT", eventArray.toString());

									List<HashMap<String, String>> eventList = new ArrayList<>();

									for (int n = 0; n < eventArray.length(); n++) {
										HashMap<String, String> event = new HashMap<>();
										event.put("id",
												eventArray.getJSONObject(n)
														.getString("id"));
										event.put("title",
												eventArray.getJSONObject(n)
														.getString("title"));
										event.put("date",
												eventArray.getJSONObject(n)
														.getString("date"));
										event.put("remDay",
												eventArray.getJSONObject(n)
														.getString("remDay"));
										eventList.add(n, event);
									}

									riwaj = new Riwaj(((JSONObject) response
											.get(i)).getString("image"),
											((JSONObject) response.get(i))
													.getString("name"),
											((JSONObject) response.get(i))
													.getInt("id"), eventList);

								} else {
									riwaj = new Riwaj(((JSONObject) response
											.get(i)).getString("image"),
											((JSONObject) response.get(i))
													.getString("name"),
											((JSONObject) response.get(i))
													.getInt("id"));
								}

								listMaps.add(i, riwaj);
							}
						} catch (JSONException e) {
							e.printStackTrace();
						}
						populateRitiriwajList(listMaps);
					}

					@Override
					public void onFailure(int statusCode, Header[] headers,
							String responseString, Throwable throwable) {
						showAlertDialog(getActivity(), responseString);
					}

					@Override
					public void onFailure(int statusCode, Header[] headers,
							Throwable throwable, JSONObject errorResponse) {
						if (throwable.getCause() instanceof Exception) {
							showAlertDialog(getActivity(),
									"Oops!!! so sorry, we encountered a server error");
						} else {
							showAlertDialog(getActivity(),
									"Oops!!! There seems to be a network error.Please check network connection");
						}
					}
				});

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
								doShit();
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
}
