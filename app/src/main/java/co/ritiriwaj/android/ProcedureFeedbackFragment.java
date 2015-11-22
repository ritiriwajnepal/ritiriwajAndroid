package co.ritiriwaj.android;

import java.util.ArrayList;
import java.util.HashMap;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;
import co.ritiriwaj.android.helper.ShowAlertDialog;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class ProcedureFeedbackFragment extends DialogFragment {

	private static String GET_ETHNIC_GROUP_URL = MainActivity.IP
			+ "getAllEthnicGroup";
	private static String POST_PROCEDURE_FEEDBACK_URL = MainActivity.IP
			+ "postProcedureFeedback";
	View view;
	String ritualId;
	ArrayList<String> casteList;
	private static AsyncHttpClient asyncHttpClient = new AsyncHttpClient();

	public static ProcedureFeedbackFragment newInstance(Bundle args) {
		ProcedureFeedbackFragment pff = new ProcedureFeedbackFragment();
		pff.setArguments(args);
		return pff;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		int style = DialogFragment.STYLE_NORMAL, theme = android.R.style.Theme_Holo_Dialog_NoActionBar_MinWidth;
		setStyle(style, theme);
		setRetainInstance(true);
//		setCancelable(false);
		ritualId = getArguments().get("ritualId").toString();

	}

	@Override
	public void onSaveInstanceState(Bundle arg0) {
		super.onSaveInstanceState(arg0);
		arg0.putSerializable("casteList", this.casteList);
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		LayoutInflater i = getActivity().getLayoutInflater();
		view = i.inflate(R.layout.fragment_dialogue_process_feedback, null,
				false);

		AlertDialog.Builder b = new AlertDialog.Builder(getActivity())
				.setView(view)
				.setPositiveButton("Suggest", null)
				.setNegativeButton("Cancel",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton) {
								dialog.dismiss();
								getFragmentManager().beginTransaction().remove(ProcedureFeedbackFragment.this).commit();
							}
						});

		return b.create();

	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {

		super.onActivityCreated(savedInstanceState);
		if (savedInstanceState != null) {
			populateDialogData((ArrayList<String>) savedInstanceState
					.getSerializable("casteList"));
		} else {
			if (this.casteList != null) {
				populateDialogData(this.casteList);
			} else {
				final HashMap<String, String> ethnicGroupList = new HashMap<String, String>();

				asyncHttpClient.get(getActivity(),GET_ETHNIC_GROUP_URL,
						new JsonHttpResponseHandler() {

							@Override
							public void onSuccess(int statusCode,
									Header[] headers, JSONArray response) {

								try {
									ArrayList<String> casteList = new ArrayList<String>();
									casteList.add("-- Select --");

									for (int i = 0; i < response.length(); i++) {
										ethnicGroupList.put(
												((JSONObject) response.get(i))
														.getString("id"),
												((JSONObject) response.get(i))
														.getString("name"));
										casteList.add(i, ((JSONObject) response
												.get(i)).getString("name"));
									}

									populateDialogData(casteList);

								} catch (JSONException e) {
									e.printStackTrace();

								}
							}

						});

			}
		}

		getDialog().setOnShowListener(new DialogInterface.OnShowListener() {

			@Override
			public void onShow(DialogInterface dialog) {

				Button suggest = ((AlertDialog) dialog)
						.getButton(AlertDialog.BUTTON_POSITIVE);
				suggest.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						EditText textMessage = (EditText) view
								.findViewById(R.id.textMessage);
						Spinner casteListSpinner = (Spinner) view
								.findViewById(R.id.casteListSpinner);
						RadioGroup excitementLevelRadioGroup = (RadioGroup) view
								.findViewById(R.id.excitementLevel);

						if (textMessage.getText().toString().isEmpty()) {
							textMessage.setError("Please suggest something...");
						} else {

							String caste = casteListSpinner.getSelectedItem()
									.toString();
							String message = textMessage.getText().toString();

							String excitementLevel;

							switch (excitementLevelRadioGroup
									.getCheckedRadioButtonId()) {
							case R.id.low:
								excitementLevel = "low";
								break;
							case R.id.mid:
								excitementLevel = "mid";
								break;

							case R.id.high:
								excitementLevel = "high";
								break;

							default:
								excitementLevel = "low";
								break;
							}

							Log.i("Feedback", ritualId + "," + caste + ","
									+ message + "," + excitementLevel);

							sendProcedureFeedback(ritualId, caste, message,
									excitementLevel);

						}

					}

				});

			}
		});

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

	private void populateDialogData(final ArrayList<String> casteList) {
		this.casteList = casteList;
		final Spinner casteListSpinner = (Spinner) view
				.findViewById(R.id.casteListSpinner);

		ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
				R.layout.caste_textview, casteList);

		casteListSpinner.setAdapter(adapter);

		ArrayAdapter<String> myAdap = (ArrayAdapter<String>) casteListSpinner
				.getAdapter();
		int spinnerPosition = myAdap.getPosition("-- Select --");
		casteListSpinner.setSelection(spinnerPosition);

	}

	private void sendProcedureFeedback(String ritualId, String caste,
			String message, String excitementLevel) {

		HashMap<String, String> hashMap = new HashMap<>();
		hashMap.put("ritualId", ritualId);
		hashMap.put("caste", caste);
		hashMap.put("message", message);
		hashMap.put("excitementLevel", excitementLevel);

		RequestParams params = new RequestParams(hashMap);
		((AlertDialog) getDialog()).getButton(AlertDialog.BUTTON_POSITIVE)
				.setEnabled(false);

		// Toast.makeText(getActivity(), "Sending...",
		// Toast.LENGTH_SHORT).show();

		final ShowAlertDialog sendingAlert = new ShowAlertDialog(getActivity(),
				"Sending your feedback...");
		sendingAlert.show(getFragmentManager(), "AlertDialog");

		asyncHttpClient.post(POST_PROCEDURE_FEEDBACK_URL, params,
				new JsonHttpResponseHandler() {

					@Override
					public void onSuccess(int statusCode, Header[] headers,
							JSONObject response) {
						// TODO Auto-generated method stub
						// super.onSuccess(statusCode, headers, response);
						sendingAlert.dismiss();
						Toast.makeText(getActivity(),
								"Thank you for your feedback.",
								Toast.LENGTH_SHORT).show();

						getDialog().dismiss();
						getFragmentManager().beginTransaction().remove(ProcedureFeedbackFragment.this).commit();

					}

					@Override
					public void onFailure(int statusCode, Header[] headers,
							String responseString, Throwable throwable) {
						// TODO Auto-generated method stub
						// super.onFailure(statusCode, headers, responseString,
						// throwable);
						sendingAlert.dismiss();
						Toast.makeText(getActivity(),
								"Sorry! Something went Wrong.",
								Toast.LENGTH_SHORT).show();
						((AlertDialog) getDialog()).getButton(
								AlertDialog.BUTTON_POSITIVE).setEnabled(true);
					}

				});

	}
}
