package co.ritiriwaj.android.helper;

import java.util.HashMap;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import co.ritiriwaj.android.DatePickerDialogFragment;
import co.ritiriwaj.android.MainActivity;
import co.ritiriwaj.android.PersonalRiwajListFragment;
import co.ritiriwaj.android.R;
import co.ritiriwaj.android.SingleRiwajFragment;
import co.ritiriwaj.android.db.dao.TblPersonalCalendarDao;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class AddEventDailogFragment extends DialogFragment {
	Context context;
	View view;
	String args;
	private static String URL_POST_EVENT = MainActivity.IP + "setPersonalEvent";
	private SharedPreferences sharedPref;
	private static AsyncHttpClient asyncHttpClient = new AsyncHttpClient();

	public AddEventDailogFragment(Context context, String riwajId) {
		this.context = context;
		this.args = riwajId;
	}

	@Override
	public void onActivityCreated(Bundle arg0) {
		sharedPref = PreferenceManager.getDefaultSharedPreferences(context);

		super.onActivityCreated(arg0);
	}

	@Override
	@NonNull
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);

		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

		view = inflater.inflate(R.layout.dialog_add_event, null);

		final EditText eventTitle = (EditText) view
				.findViewById(R.id.eventTitle);

		final TextView eventDate = (TextView) view.findViewById(R.id.eventDate);

		builder.setTitle("Add Your Event")
				.setView(view)
				.setCancelable(false)
				.setPositiveButton("Save", null)
				.setNegativeButton("Cancel",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int arg1) {
								dialog.dismiss();
							}
						});
		Log.i("builder", "dialog");
		final AlertDialog alertdialog = builder.create();

		ImageButton eventDatePickerButton = (ImageButton) view
				.findViewById(R.id.eventDatePicker);
		eventDatePickerButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				DatePickerDialogFragment datePicker = new DatePickerDialogFragment(
						eventDate);
				datePicker.show(((Activity) context).getFragmentManager(),
						"datePicker");

			}
		});

		alertdialog.setOnShowListener(new DialogInterface.OnShowListener() {

			@Override
			public void onShow(DialogInterface dialog) {

				Button saveEvent = ((AlertDialog) dialog)
						.getButton(AlertDialog.BUTTON_POSITIVE);

				saveEvent.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {

						if (eventTitle.getText().toString().isEmpty()) {
							eventTitle.setError("Please add a title.");
						}

						else if (eventDate.getText().toString().isEmpty()) {
							eventDate.setError("Please select a date.");
						} else {

							SimpleConnectionCheck connectionCheck = new SimpleConnectionCheck(getActivity());
							if (!connectionCheck.isNetworkConnected()){
								Toast.makeText(getActivity(), "Sorry! Please check internet.", Toast.LENGTH_SHORT).show();
								return;
							}
							
							RequestParams params = new RequestParams();
							params.put("ritualId", args);
							params.put("subscriberId",
									sharedPref.getString("subscriberId", ""));
							params.put("title", eventTitle.getText().toString());
							params.put("date", eventDate.getText().toString());

							Log.i("PARAMS", params.toString());

							asyncHttpClient.post(URL_POST_EVENT, params,
									new JsonHttpResponseHandler() {

										@Override
										public void onSuccess(int statusCode,
												Header[] headers,
												JSONObject response) {
											// Log.e("Failure", "success");
											Toast.makeText(
													context,
													"Event Added Successfully! View Events to confirm.",
													Toast.LENGTH_LONG).show();

											try {

												int eventId = response
														.getInt("eventId");
												TblPersonalCalendarDao tblPersonalCalendarDao = new TblPersonalCalendarDao(
														getActivity());

												HashMap<String, String> eventDetails = new HashMap<>();
												eventDetails.put("id",
														String.valueOf(eventId));
												eventDetails.put("ritual_id",
														args);
												eventDetails.put("title",
														eventTitle.getText()
																.toString());
												eventDetails.put("date",
														eventDate.getText()
																.toString());

												tblPersonalCalendarDao
														.insertPersonalEvent(eventDetails);

											} catch (JSONException e) {
												Log.e("RITIRIWAJ",
														e.getMessage());
											}

											alertdialog.dismiss();
											// get current fragment class
											Class<? extends Fragment> currentFragment = ((ActionBarActivity) context)
													.getSupportFragmentManager()
													.findFragmentById(
															R.id.fragment_placeholder)
													.getClass();
											/*
											 * if current fragment is
											 * PersonalRiwajListFragment
											 */
											if (currentFragment == PersonalRiwajListFragment.class) {
												// reload list
												((PersonalRiwajListFragment) ((ActionBarActivity) context)
														.getSupportFragmentManager()
														.findFragmentById(
																R.id.fragment_placeholder))
														.newDoShit();
											} else if (currentFragment == SingleRiwajFragment.class) {
												// reload list
												((SingleRiwajFragment) ((ActionBarActivity) context)
														.getSupportFragmentManager()
														.findFragmentById(
																R.id.fragment_placeholder))
														.doShit();
											}

										}

										@Override
										public void onFailure(int statusCode,
												Header[] headers,
												String responseString,
												Throwable throwable) {
											Log.e("Failure", responseString);
										}

										@Override
										public void onFailure(int statusCode,
												Header[] headers,
												Throwable throwable,
												JSONObject errorResponse) {

											super.onFailure(statusCode,
													headers, throwable,
													errorResponse);
										}

									});

						}
					}
				});

			}
		});

		return alertdialog;
	}
}
