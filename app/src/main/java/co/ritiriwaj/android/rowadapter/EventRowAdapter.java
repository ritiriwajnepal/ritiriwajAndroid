package co.ritiriwaj.android.rowadapter;

import java.util.HashMap;
import java.util.List;

import org.apache.http.Header;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import co.ritiriwaj.android.MainActivity;
import co.ritiriwaj.android.PersonalRiwajListFragment;
import co.ritiriwaj.android.R;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

public class EventRowAdapter extends ArrayAdapter<HashMap<String, String>> {

	Context context;
	List<HashMap<String, String>> eventList;
	private AsyncHttpClient client = new AsyncHttpClient();
	private static String URL_DELETE_EVENT = MainActivity.IP
			+ "deletePersonalCalendarEvent/";

	public EventRowAdapter(Context context, int resource,
			List<HashMap<String, String>> objects) {

		super(context, R.layout.row_event, objects);
		this.context = context;
		this.eventList = objects;

	}

	private class ViewHolder {
		TextView eventTitle;
		TextView eventDate;
		TextView eventRemDay;
		ImageButton eventDelete;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {

		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

		ViewHolder holder = new ViewHolder();
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.row_event, parent, false);
			holder.eventTitle = (TextView) convertView
					.findViewById(R.id.eventTitle);
			holder.eventDate = (TextView) convertView
					.findViewById(R.id.eventDate);
			holder.eventRemDay = (TextView) convertView
					.findViewById(R.id.eventRemDay);
			holder.eventDelete = (ImageButton) convertView
					.findViewById(R.id.eventDeleteButton);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		// Log.i("EVENTLIST", eventList.get(position).toString());

		holder.eventTitle.setText(eventList.get(position).get("title"));
		holder.eventDate.setText(eventList.get(position).get("date"));
		holder.eventRemDay.setText(eventList.get(position).get("remDay"));

		holder.eventDelete.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				showAlertDeleteDialog(context, position);

			}
		});

		return convertView;
	}

	private void showAlertDeleteDialog(final Context context, final int position) {
		final AlertDialog.Builder builder = new AlertDialog.Builder(context);

		builder.setMessage("Confirm Delete!")
				.setCancelable(false)
				.setPositiveButton("Delete",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int arg1) {

								String eventId = eventList.get(position).get(
										"id");
								deleteEvent(eventId, position);
								dialog.dismiss();

							}
						})
				.setNegativeButton("Cancel",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int arg1) {
								dialog.dismiss();
							}
						});
		Log.i("builder", "dialog");
		final AlertDialog alert = builder.create();
		alert.show();
	}

	private void deleteEvent(String eventId, final int position) {
		client.get(context, URL_DELETE_EVENT + eventId,
				new JsonHttpResponseHandler() {

					@Override
					public void onSuccess(int statusCode, Header[] headers,
							JSONObject response) {

						eventList.remove(position);
						notifyDataSetChanged();
						// get current fragment class
						Class<? extends Fragment> currentFragment = ((ActionBarActivity) context)
								.getSupportFragmentManager()
								.findFragmentById(R.id.fragment_placeholder)
								.getClass();
						/*
						 * if current fragment is PersonalRiwajListFragment
						 */
						if (currentFragment == PersonalRiwajListFragment.class) {
							// reload list
							((PersonalRiwajListFragment) ((ActionBarActivity) context)
									.getSupportFragmentManager()
									.findFragmentById(R.id.fragment_placeholder))
									.doShit();
						}
					}

					@Override
					public void onFailure(int statusCode, Header[] headers,
							String responseString, Throwable throwable) {

						Toast.makeText(context, "Sorry! Event Delete Failed.",
								Toast.LENGTH_SHORT).show();

					}

				});
	}

}
