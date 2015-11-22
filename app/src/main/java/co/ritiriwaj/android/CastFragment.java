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
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import co.ritiriwaj.android.helper.ExpandableListAdapter;
import co.ritiriwaj.android.model.EthnicGroupAndCaste;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

public class CastFragment extends DialogFragment {
	View view;
	ExpandableListAdapter listAdapter;
	ExpandableListView expListView;
	Button suggestionFormButton;
	List<String> listDataHeader;
	HashMap<String, List<String>> listDataChild;
	List<EthnicGroupAndCaste> ethnicGroupAndCastesList;
	private static String GET_GROUP_CASTE = MainActivity.IP
			+ "getAllGroupCasteByRitualId/";
	String riwajId;
	private AsyncHttpClient client = new AsyncHttpClient();

	// ShowAlertDialog alertDialog;

	public static CastFragment newInstance(Bundle args) {
		CastFragment castFragment = new CastFragment();
		castFragment.setArguments(args);
		return castFragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRetainInstance(true);
		riwajId = getArguments().get("riwajId").toString();
		int style = DialogFragment.STYLE_NORMAL, theme = android.R.style.Theme_Holo_Light_Dialog_MinWidth;
		setStyle(style, theme);

	}

	@Override
	@NonNull
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		LayoutInflater inflater = getActivity().getLayoutInflater();
		view = inflater.inflate(R.layout.caste_listview, null, false);
		AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
		alertDialog.setTitle(R.string.select_community);
		alertDialog.setView(view);
		return alertDialog.create();
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putSerializable("ethnicGroup",
				(Serializable) this.ethnicGroupAndCastesList);
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
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		if (savedInstanceState != null) {
			Log.i("CastFragment", "Restoring Fragment Data");
			ethnicGroupAndCastesList = (List<EthnicGroupAndCaste>) savedInstanceState
					.getSerializable("ethnicGroup");
			if (ethnicGroupAndCastesList != null)
				prepareListData(ethnicGroupAndCastesList);
			else
				doShit();
		} else {
			if (ethnicGroupAndCastesList != null) {
				Log.i("CastFragment", "Leaving as is");
				prepareListData(ethnicGroupAndCastesList);
			} else {
				Log.i("CastFragment", "Fetching Fragment Data");
				doShit();
			}
		}

	}

	private void prepareListData(
			final List<EthnicGroupAndCaste> ethnicGroupAndCasteList) {
		this.ethnicGroupAndCastesList = ethnicGroupAndCasteList;
		if (ethnicGroupAndCasteList.isEmpty()) {
			this.dismiss();
		} else {
			view.findViewById(R.id.progressBarCaste).setVisibility(View.GONE);

			listDataHeader = new ArrayList<String>();
			listDataChild = new HashMap<String, List<String>>();

			for (int i = 0; i < ethnicGroupAndCasteList.size(); i++) {
				listDataHeader.add(ethnicGroupAndCasteList.get(i)
						.getEthinicGroup().get("egName"));
				List<HashMap<String, String>> casteList = ethnicGroupAndCasteList
						.get(i).getCasteList();
				List<String> casteNameList = new ArrayList<String>();
				for (int j = 0; j < casteList.size(); j++) {
					casteNameList.add(j, casteList.get(j).get("cName"));
				}
				listDataChild.put(listDataHeader.get(i), casteNameList);
			}

			expListView = (ExpandableListView) view
					.findViewById(R.id.castListView);

			suggestionFormButton = (Button) view
					.findViewById(R.id.suggestionFormButton);

			listAdapter = new ExpandableListAdapter(getActivity(),
					listDataHeader, listDataChild);

			expListView.setAdapter(listAdapter);

			expListView.setOnChildClickListener(new OnChildClickListener() {

				@Override
				public boolean onChildClick(ExpandableListView parent, View v,
						int groupPosition, int childPosition, long id) {

					HashMap<String, String> clickedCasteMap = ethnicGroupAndCastesList
							.get(groupPosition).getCasteList()
							.get(childPosition);

					Intent data = new Intent();
					data.putExtra("egId",
							ethnicGroupAndCasteList.get(groupPosition)
									.getEthinicGroup().get("egId"));
					data.putExtra("egcId", clickedCasteMap.get("egcId"));
					data.putExtra("egName",
							ethnicGroupAndCasteList.get(groupPosition)
									.getEthinicGroup().get("egName"));

					getTargetFragment().onActivityResult(
							getTargetRequestCode(), 1, data);

					dismiss();
					getFragmentManager().beginTransaction().remove(CastFragment.this).commit();

					return false;
				}
			});

			suggestionFormButton.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					FragmentTransaction ft = getFragmentManager()
							.beginTransaction();

					Bundle args = new Bundle();
					args.putString("ritualId", riwajId);
					DialogFragment procedureFeedbackFragment = ProcedureFeedbackFragment
							.newInstance(args);
					procedureFeedbackFragment.show(ft,
							"Procedure Feedback Fragment");
				}
			});
		}
	}

	private void doShit() {
		client.get(getActivity(), GET_GROUP_CASTE + riwajId,
				new JsonHttpResponseHandler() {
					@Override
					public void onSuccess(int statusCode, Header[] headers,
							JSONArray response) {
						if (!response.isNull(0)) {
							Log.i("castFragment", response.toString());
							try {
								List<EthnicGroupAndCaste> ethnicGroupAndCasteList = new ArrayList<EthnicGroupAndCaste>();

								for (int i = 0; i < response.length(); i++) {
									JSONObject jsonObject = response
											.getJSONObject(i);
									String egId = jsonObject.get("egId")
											.toString();
									String egName = jsonObject
											.getString("egName");
									HashMap<String, String> ethnicMap = new HashMap<String, String>();
									ethnicMap.put("egId", egId);
									ethnicMap.put("egName", egName);
									List<HashMap<String, String>> casteList = new ArrayList<HashMap<String, String>>();
									JSONArray casteArray = jsonObject
											.getJSONArray("casteList");
									for (int j = 0; j < casteArray.length(); j++) {
										JSONObject casteObject = casteArray
												.getJSONObject(j);
										String cName = casteObject
												.getString("cName");
										String egcId = casteObject
												.getString("egcId");
										HashMap<String, String> casteMap = new HashMap<String, String>();
										casteMap.put("cName", cName);
										casteMap.put("egcId", egcId);
										casteList.add(j, casteMap);
									}
									EthnicGroupAndCaste groupAndCaste = new EthnicGroupAndCaste(
											ethnicMap, casteList);
									ethnicGroupAndCasteList.add(i,
											groupAndCaste);
								}
								prepareListData(ethnicGroupAndCasteList);

							} catch (IllegalStateException | JSONException e) {
								e.printStackTrace();
								Log.e("JSON error", e.getMessage());
							}
						} else {
							// show form to collect user response
							CastFragment.this.dismiss();
							getFragmentManager().beginTransaction().remove(CastFragment.this).commit();
							FragmentTransaction ft = getFragmentManager()
									.beginTransaction();

							Bundle args = new Bundle();
							args.putString("ritualId", riwajId);
							DialogFragment procedureFeedbackFragment = ProcedureFeedbackFragment
									.newInstance(args);
							procedureFeedbackFragment.show(ft,
									"Procedure Feedback Fragment");
						}
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
