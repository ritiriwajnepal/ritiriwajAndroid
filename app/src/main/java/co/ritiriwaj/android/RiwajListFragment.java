package co.ritiriwaj.android;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.Header;
import org.apache.http.conn.ConnectTimeoutException;
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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;
import co.ritiriwaj.android.db.dao.TblRitualDao;
import co.ritiriwaj.android.helper.SimpleConnectionCheck;
import co.ritiriwaj.android.model.Riwaj;
import co.ritiriwaj.android.rowadapter.RiwajRowAdapter;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.nhaarman.listviewanimations.appearance.simple.SwingBottomInAnimationAdapter;

public class RiwajListFragment extends ListFragment {
	private static String GET_RITUAL_URL = MainActivity.IP
			+ "getAllCalendarRitual";
	// private String GET_CALENDAR_RITUAL_BY_MONTH = MainActivity.IP
	// + "getCalendarRitualsByNepaliMonth/";
	List<Riwaj> rowItems;
	private AsyncHttpClient client = new AsyncHttpClient();

	private TblRitualDao tblRitualDao;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
		setRetainInstance(true);
		MainActivity.toolbar.setTitle(getString(R.string.app_name) + " - "
				+ "Calendar Events");
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if (MainActivity.bottomToolBar != null) {
			MainActivity.bottomToolBar.getMenu().clear();
			MainActivity.bottomToolBar.setVisibility(View.GONE);
		}
		
		return super.onCreateView(inflater, container, savedInstanceState);
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.test, menu);
		super.onCreateOptionsMenu(menu, inflater);
	}

	@Override
	public void onDestroy() {
		client.cancelRequests(getActivity(), true);
		client.cancelAllRequests(true);
		super.onDestroy();
	}

	@Override
	public void onPause() {
		client.cancelRequests(getActivity(), true);
		client.cancelAllRequests(true);
		super.onPause();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.baishak:
			// Toast.makeText(getActivity(), "Loading...", Toast.LENGTH_SHORT)
			// .show();
			// doShit(GET_CALENDAR_RITUAL_BY_MONTH + "1");
			this.populateMonthlyRitiriwajList("01");
			break;
		case R.id.jestha:
			// Toast.makeText(getActivity(), "Loading...", Toast.LENGTH_SHORT)
			// .show();
			// doShit(GET_CALENDAR_RITUAL_BY_MONTH + "2");
			this.populateMonthlyRitiriwajList("02");
			break;
		case R.id.ashadh:
			// Toast.makeText(getActivity(), "Loading...", Toast.LENGTH_SHORT)
			// .show();
			// doShit(GET_CALENDAR_RITUAL_BY_MONTH + "3");
			this.populateMonthlyRitiriwajList("03");
			break;
		case R.id.shrawan:
			// Toast.makeText(getActivity(), "Loading...", Toast.LENGTH_SHORT)
			// .show();
			// doShit(GET_CALENDAR_RITUAL_BY_MONTH + "4");
			this.populateMonthlyRitiriwajList("04");
			break;
		case R.id.bhadra:
			// Toast.makeText(getActivity(), "Loading...", Toast.LENGTH_SHORT)
			// .show();
			// doShit(GET_CALENDAR_RITUAL_BY_MONTH + "5");
			this.populateMonthlyRitiriwajList("05");
			break;
		case R.id.ashwin:
			// Toast.makeText(getActivity(), "Loading...", Toast.LENGTH_SHORT)
			// .show();
			// doShit(GET_CALENDAR_RITUAL_BY_MONTH + "6");
			this.populateMonthlyRitiriwajList("06");
			break;
		case R.id.kartik:
			// Toast.makeText(getActivity(), "Loading...", Toast.LENGTH_SHORT)
			// .show();
			// doShit(GET_CALENDAR_RITUAL_BY_MONTH + "7");
			this.populateMonthlyRitiriwajList("07");
			break;
		case R.id.mangsir:
			// Toast.makeText(getActivity(), "Loading...", Toast.LENGTH_SHORT)
			// .show();
			// doShit(GET_CALENDAR_RITUAL_BY_MONTH + "8");
			this.populateMonthlyRitiriwajList("08");
			break;
		case R.id.poush:
			// Toast.makeText(getActivity(), "Loading...", Toast.LENGTH_SHORT)
			// .show();
			// doShit(GET_CALENDAR_RITUAL_BY_MONTH + "9");
			this.populateMonthlyRitiriwajList("09");
			break;
		case R.id.magh:
			// Toast.makeText(getActivity(), "Loading...", Toast.LENGTH_SHORT)
			// .show();
			// doShit(GET_CALENDAR_RITUAL_BY_MONTH + "10");
			this.populateMonthlyRitiriwajList("10");
			break;
		case R.id.falgun:
			// Toast.makeText(getActivity(), "Loading...", Toast.LENGTH_SHORT)
			// .show();
			// doShit(GET_CALENDAR_RITUAL_BY_MONTH + "11");
			this.populateMonthlyRitiriwajList("11");
			break;
		case R.id.chaitra:
			// Toast.makeText(getActivity(), "Loading...", Toast.LENGTH_SHORT)
			// .show();
			// doShit(GET_CALENDAR_RITUAL_BY_MONTH + "12");
			this.populateMonthlyRitiriwajList("12");
			break;
		// case R.id.search:
		// break;

		default:
			break;
		}
		return super.onOptionsItemSelected(item);
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
				populateRitiriwajList(rowItems);
			else
				doShit(GET_RITUAL_URL);
		} else {
			if (rowItems != null) {
				Log.i("RiwajListFragment", "Leaving everything as is");
				MainActivity.progressBarMain.setVisibility(View.INVISIBLE);
				return;
			} else {
				Log.i("RiwajListFragment", "Fetching Fragment Data");

				// Check if local data exists
				tblRitualDao = new TblRitualDao(getActivity());

				// from offline db (might be empty first time)
				List<Riwaj> riwajList = tblRitualDao.getAllCalendarRituals();

				SimpleConnectionCheck check = new SimpleConnectionCheck(
						getActivity());

				if (!check.isNetworkConnected()) {
					// If exists, fetch & populate Local Data
					if (!riwajList.isEmpty()) {
						Log.i("RITIRIWAJ_SQLITE",
								"Ritual List Exists in Local Database");
						populateRitiriwajList(riwajList);
					} else {

						SharedPreferences sharedpref = PreferenceManager
								.getDefaultSharedPreferences(getActivity());
						sharedpref.edit().putBoolean("firstTimeFetch", true)
								.commit();

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
		}
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

			// SingleRiwajFragment singleRiwajFragment = new
			// SingleRiwajFragment();
			SingleRitualFragment singleRitualFragment = new SingleRitualFragment();
			Bundle args = new Bundle();
			args.putString("riwajTitle", rowItems.get(position).getTitle());
			args.putInt("riwajId", rowItems.get(position).getRiwajId());
			args.putString("imageURL", rowItems.get(position).getImageURL());
			args.putString("riwajDesc", rowItems.get(position).getDesc());
			args.putString("riwajDate", rowItems.get(position).getDate());
			args.putSerializable("riwajEventList",
					(Serializable) rowItems.get(position).getEventList());
			// singleRiwajFragment.setArguments(args);
			singleRitualFragment.setArguments(args);

			// begin fragment transaction
			FragmentManager fm = getFragmentManager();
			FragmentTransaction ft = fm.beginTransaction();

			// ft.setCustomAnimations(R.anim.slide_in_right,
			// R.anim.slide_out_left,
			// R.anim.enter_from_right, R.anim.exit_to_left);
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

	private void showAlertDialog(final Context context, String message) {
		final AlertDialog.Builder builder = new AlertDialog.Builder(context);

		builder.setMessage(message)
				.setCancelable(false)
				.setPositiveButton("Reload",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface arg0, int arg1) {
								arg0.dismiss();
								doShit(GET_RITUAL_URL);
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

	private void populateMonthlyRitiriwajList(String month) {
		// Check if local data exists
		tblRitualDao = new TblRitualDao(getActivity());

		// from offline db (might be empty first time)
		List<Riwaj> riwajList = tblRitualDao.getMonthlyRituals(month);

		SimpleConnectionCheck check = new SimpleConnectionCheck(getActivity());

		if (!check.isNetworkConnected()) {
			// If exists, fetch & populate Local Data
			if (!riwajList.isEmpty()) {
				Log.i("RITIRIWAJ_SQLITE",
						"Ritual List Exists in Local Database");
				populateRitiriwajList(riwajList);
			} else {
				Toast.makeText(getActivity(),
						"Sorry! there are no events listed for this month",
						Toast.LENGTH_SHORT).show();
			}

		} else {
			tblRitualDao.insertAllRitualList();
			populateRitiriwajList(riwajList);
		}
	}

	public void doShit(final String url) {
		client.get(getActivity(), url, new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers,
					JSONArray response) {
				List<Riwaj> listMaps = new ArrayList<Riwaj>();

				try {
					for (int i = 0; i < response.length(); i++) {
						Riwaj riwaj = new Riwaj(((JSONObject) response.get(i))
								.getString("image"), ((JSONObject) response
								.get(i)).getString("name"),
								((JSONObject) response.get(i))
										.getString("date"),
								((JSONObject) response.get(i))
										.getString("remDay"),
								((JSONObject) response.get(i)).getInt("id"),
								((JSONObject) response.get(i)).getInt("status"));

						listMaps.add(i, riwaj);
					}

				} catch (JSONException e) {
					Log.e("JSONException", e.getMessage());
				}

				if (!listMaps.isEmpty()) {
					populateRitiriwajList(listMaps);
				}

			}

			@Override
			public void onFailure(int statusCode, Header[] headers,
					String responseString, Throwable throwable) {
				if (responseString.contentEquals("null")) {
					Toast.makeText(getActivity(),
							"Sorry! there are no events listed for this month",
							Toast.LENGTH_SHORT).show();
				}

				if (throwable.getCause() instanceof ConnectTimeoutException) {
					showAlertDialog(getActivity(), responseString);
				}
			}

			@Override
			public void onFailure(int statusCode, Header[] headers,
					Throwable throwable, JSONObject errorResponse) {
				if (throwable.getCause() instanceof Exception) {
					Log.e("RITIRIWAJ_EXCEPTION", throwable.getMessage());
					showAlertDialog(getActivity(),
							"Oops! Some technical errors occured.");
				} else {
					showAlertDialog(getActivity(),
							"Oops!!! There seems to be a network error.Please check network connection");
				}
			}
		});
	}

}
