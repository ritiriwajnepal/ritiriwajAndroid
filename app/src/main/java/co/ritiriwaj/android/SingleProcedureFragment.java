package co.ritiriwaj.android;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.RelativeLayout.LayoutParams;
import co.ritiriwaj.android.model.SubProcedure;
import co.ritiriwaj.android.rowadapter.ProcedureRowAdapter;
import co.ritiriwaj.android.rowadapter.SubProcedureRowAdapter;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.nhaarman.listviewanimations.appearance.simple.SwingBottomInAnimationAdapter;

public class SingleProcedureFragment extends Fragment {
	private static String GET_ALL_SUB_PROCESS = MainActivity.IP
			+ "getAllSubProcessByProcessId/";
	private AsyncHttpClient client = new AsyncHttpClient();
	ListView listView;
	TextView title;
	String proNo, proId, proTitle;
	List<SubProcedure> subProcedureList;
	View view;
	RelativeLayout layout;
	ViewGroup container;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		setRetainInstance(true);
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.single_procedure_layout, container,
				false);
		proNo = getArguments().getString("proNo");
		proId = getArguments().getString("proId");
		proTitle = getArguments().getString("proTitle");
		view.setVisibility(View.INVISIBLE);
		layout = new RelativeLayout(getActivity());
		ProgressBar loadingProgressBar = new ProgressBar(getActivity(), null,
				android.R.attr.progressBarStyleLarge);
		loadingProgressBar.setIndeterminate(true);
		loadingProgressBar.setVisibility(View.VISIBLE);
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		params.addRule(RelativeLayout.CENTER_IN_PARENT);
		layout.addView(loadingProgressBar, params);
		container.addView(layout);
		this.container = container;
		return view;
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putSerializable("subProcedureList",
				(Serializable) subProcedureList);
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

	@SuppressWarnings("unchecked")
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		MainActivity.bottomToolBar.setVisibility(View.GONE);

		MainActivity.progressBarMain.setVisibility(View.VISIBLE);

		listView = (ListView) view.findViewById(R.id.singleProcedureList);
		title = (TextView) view.findViewById(R.id.procedureTitle);
		((TextView) view.findViewById(R.id.serial_no))
				.setText(ProcedureRowAdapter.convertToNepaleseNumber(proNo,
						getActivity()));
		// set the title of procedure
		title.setText(proTitle);
		if (savedInstanceState != null) {
			subProcedureList = (List<SubProcedure>) savedInstanceState
					.getSerializable("subProcedureList");
			if (subProcedureList != null) {
				populateSubProcedureList(subProcedureList);
				return;
			} else {
				doShit();
			}
		} else {
			if (subProcedureList != null) {
				populateSubProcedureList(subProcedureList);
				return;
			} else {
				doShit();
			}
		}

	}

	private void populateSubProcedureList(List<SubProcedure> list) {
		view.setVisibility(View.VISIBLE);
		MainActivity.progressBarMain.setVisibility(View.INVISIBLE);
		container.removeView(layout);

		subProcedureList = list;
		SubProcedureRowAdapter subProcedureRowAdapter = new SubProcedureRowAdapter(
				getActivity(), list);
		SwingBottomInAnimationAdapter animationAdapter = new SwingBottomInAnimationAdapter(
				subProcedureRowAdapter);
		animationAdapter.setAbsListView(listView);
		listView.setAdapter(animationAdapter);

	}

	private void doShit() {
		client.get(getActivity(), GET_ALL_SUB_PROCESS + proId,
				new JsonHttpResponseHandler() {
					@Override
					public void onSuccess(int statusCode, Header[] headers,
							JSONArray jsonArray) {
						List<SubProcedure> subProcedureList = new ArrayList<SubProcedure>();

						try {

							if (jsonArray != null) {
								for (int i = 0; i < jsonArray.length(); i++) {
									JSONObject jsonObject = jsonArray
											.getJSONObject(i);

									if (!jsonObject.has("reasonWhyId")) {
										SubProcedure subProcedure = new SubProcedure(
												jsonObject.getString("id"),
												jsonObject
														.getString("order_no"),
												jsonObject
														.getString("process_text"),
												jsonObject
														.getString("blockquote"),
												jsonObject.getString("images"),
												"");
										subProcedureList.add(i, subProcedure);
									} else {
										SubProcedure subProcedure = new SubProcedure(
												jsonObject.getString("id"),
												jsonObject
														.getString("order_no"),
												jsonObject
														.getString("process_text"),
												jsonObject
														.getString("blockquote"),
												jsonObject.getString("images"),
												jsonObject
														.getString("reasonWhyId"));
										subProcedureList.add(i, subProcedure);
									}
								}
							}
						} catch (IllegalStateException | JSONException e) {
							e.printStackTrace();
							Log.e("JSON error", e.getMessage());
						}

						populateSubProcedureList(subProcedureList);

					}

					@Override
					public void onFailure(int statusCode, Header[] headers,
							Throwable throwable, JSONArray errorResponse) {
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
