package co.ritiriwaj.android;

import java.util.Calendar;
import java.util.HashMap;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class DatePickerDialogFragment extends DialogFragment implements
		DatePickerDialog.OnDateSetListener {

	private TextView eventDate;

	public DatePickerDialogFragment() {

	}

	public DatePickerDialogFragment(TextView eventDate) {
		this.eventDate = eventDate;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {

		// Use the current date as the default date in the picker
		final Calendar c = Calendar.getInstance();
		int year = c.get(Calendar.YEAR);
		int month = c.get(Calendar.MONTH);
		int day = c.get(Calendar.DAY_OF_MONTH);

		// Create a new instance of DatePickerDialog and return it
		return new DatePickerDialog(getActivity(), this, year, month, day);
	}

	@Override
	public void onDateSet(DatePicker view, int year, int monthOfYear,
			int dayOfMonth) {

		eventDate.setText(year + "-" + (monthOfYear + 1) + "-" + dayOfMonth);
		getDialog().dismiss();

	}

}
