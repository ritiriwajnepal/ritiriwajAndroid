package co.ritiriwaj.android;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.Toast;
import co.ritiriwaj.android.model.Procedure;
import co.ritiriwaj.android.rowadapter.ProcedureRowAdapter;

import com.google.android.youtube.player.YouTubeApiServiceUtil;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubeStandalonePlayer;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.nhaarman.listviewanimations.appearance.simple.SwingBottomInAnimationAdapter;

public class ProcedureListFragment extends Fragment implements OnClickListener,
		OnLongClickListener {
	View view;
	RelativeLayout layout;
	String egId;
	String egcId;
	String egName;
	ListView listView;
	List<Procedure> procedureRowItems;
	private static String GET_ALL_PROCESS = MainActivity.IP
			+ "getAllProcessByRitualCasteId/";
	private AsyncHttpClient client = new AsyncHttpClient();
	private ViewGroup container;
	boolean hasPackages = false;

	// ShowAlertDialog alertDialog;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		setRetainInstance(true);
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.fragment_procedure_list, container,
				false);
		egId = getArguments().getString("egId");
		egcId = getArguments().getString("egcId");
		egName = getArguments().getString("egName");
		// getActivity().getActionBar().setTitle(egName +
		// " समुदायको प्रक्रिया");
		setHasOptionsMenu(true);
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
		MainActivity.bottomToolBar.setVisibility(View.GONE);
		container.addView(layout);
		this.container = container;
		return view;
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putSerializable("procedureList",
				(Serializable) procedureRowItems);
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
		listView = (ListView) view.findViewById(R.id.procedure_list);
		listView.setDivider(getResources().getDrawable(
				android.R.color.transparent));

		MainActivity.progressBarMain.setVisibility(View.VISIBLE);
		LayoutInflater inflater = getActivity().getLayoutInflater();
		View bottomToolbarView = inflater.inflate(
				R.layout.procedure_list_bottom_toolbar, null, false);
		FrameLayout bottomToolbarPlaceHolder = (FrameLayout) MainActivity.bottomToolBar
				.findViewById(R.id.toolbar_placeholder);
		bottomToolbarPlaceHolder.removeAllViews();
		bottomToolbarPlaceHolder.addView(bottomToolbarView);
		bottomToolbarView.findViewById(R.id.btn_buy_package)
				.setOnClickListener(this);
		bottomToolbarView.findViewById(R.id.btn_buy_package)
				.setOnLongClickListener(this);
		bottomToolbarView.findViewById(R.id.btn_watch_procedure_video)
				.setOnClickListener(this);
		bottomToolbarView.findViewById(R.id.btn_watch_procedure_video)
				.setOnLongClickListener(this);
		bottomToolbarView.findViewById(R.id.btn_view_materials)
				.setOnClickListener(this);
		bottomToolbarView.findViewById(R.id.btn_view_materials)
				.setOnLongClickListener(this);
		if (savedInstanceState != null) {
			procedureRowItems = (List<Procedure>) savedInstanceState
					.getSerializable("procedureList");

			if (procedureRowItems != null) {
				populateProcedureList(procedureRowItems);
				return;
			} else {
				doShit();
			}
		} else {
			if (procedureRowItems != null) {
				populateProcedureList(procedureRowItems);
				return;
			} else {
				doShit();
			}
		}

	}

	private void populateProcedureList(List<Procedure> procedureList) {
		container.removeView(layout);
		view.setVisibility(View.VISIBLE);
		MainActivity.progressBarMain.setVisibility(View.INVISIBLE);

		procedureRowItems = procedureList;

		ProcedureRowAdapter adapter = new ProcedureRowAdapter(getActivity(),
				R.layout.row_procedure, procedureList);
		SwingBottomInAnimationAdapter animationAdapter = new SwingBottomInAnimationAdapter(
				adapter);
		animationAdapter.setAbsListView(listView);
		listView.setAdapter(animationAdapter);

		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				SingleProcedureFragment singleProcedureFragment = new SingleProcedureFragment();
				Bundle args = new Bundle();
				args.putString("proNo", procedureRowItems.get(position)
						.getOrderNo());
				args.putString("proId", procedureRowItems.get(position).getId());
				args.putString("proTitle", procedureRowItems.get(position)
						.getTitle());
				singleProcedureFragment.setArguments(args);
				getFragmentManager()
						.beginTransaction()
						.replace(R.id.fragment_placeholder,
								singleProcedureFragment).addToBackStack(null)
						.commit();

			}
		});
		MainActivity.bottomToolBar.setVisibility(View.VISIBLE);

	}

	private void doShit() {
		client.get(getActivity(), GET_ALL_PROCESS + egcId,
				new JsonHttpResponseHandler() {
					@Override
					public void onSuccess(int statusCode, Header[] headers,
							JSONObject response) {
						try {
							JSONArray jsonArray = response
									.getJSONArray("processList");
							List<Procedure> procedureList = new ArrayList<Procedure>();
							for (int i = 0; i < jsonArray.length(); i++) {
								JSONObject jsonObject = jsonArray
										.getJSONObject(i);
								Procedure procedure = new Procedure(jsonObject
										.getString("title"), jsonObject
										.getString("id"), jsonObject
										.getString("order_no"));
								procedureList.add(i, procedure);
							}
							if (response.getString("hasPackages")
									.equalsIgnoreCase("true")) {
								hasPackages = true;
							} else {
								hasPackages = false;
							}
							populateProcedureList(procedureList);
						} catch (JSONException exception) {

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

	private void showSorryDialog(final Context context, String message) {
		final AlertDialog.Builder builder = new AlertDialog.Builder(context);

		builder.setMessage(message)
				.setCancelable(false)
				.setPositiveButton("Okay",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface arg0, int arg1) {
								arg0.dismiss();

							}
						});
		Log.i("builder", "dialog");
		final AlertDialog alert = builder.create();
		alert.show();
	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.btn_buy_package:
			if (hasPackages) {
				Intent intent = new Intent(getActivity(),
						MaterialsActivity.class);
				intent.putExtra("ritualCasteId", egcId);
				startActivity(intent);
			} else {
				showSorryDialog(getActivity(), "Sorry, we are out of stock");
			}
			break;
		case R.id.btn_watch_procedure_video:
			watchVideo("");
			break;
		case R.id.btn_view_materials:
			getFragmentManager()
					.beginTransaction()
					.addToBackStack(null)
					.replace(R.id.fragment_placeholder,
							new MaterialsListFragment(egcId)).commit();
			break;
		default:
			break;
		}

	}

	@Override
	public boolean onLongClick(View v) {
		switch (v.getId()) {
		case R.id.btn_buy_package:
			Toast.makeText(getActivity(), R.string.buy_packages,
					Toast.LENGTH_SHORT).show();
			break;

		case R.id.btn_watch_procedure_video:
			Toast.makeText(getActivity(), R.string.watch_video,
					Toast.LENGTH_SHORT).show();
			break;

		case R.id.btn_view_materials:
			Toast.makeText(getActivity(), R.string.materials_list,
					Toast.LENGTH_SHORT).show();
			break;

		default:
			break;
		}
		return true;
	}

	private void watchVideo(String videoURL) {
		if (!videoURL.isEmpty()) {
			if (YouTubeApiServiceUtil.isYouTubeApiServiceAvailable(
					getActivity()).equals(YouTubeInitializationResult.SUCCESS)) {
				Intent intent = YouTubeStandalonePlayer.createVideoIntent(
						getActivity(),
						"AIzaSyAaUfCB6EYlpFbwcRj-Q6M7qdKxG2tmwDw", videoURL, 0,
						false, false);
				startActivity(intent);
			} else {
				startActivity(new Intent(Intent.ACTION_VIEW,
						Uri.parse("http://www.youtube.com/watch?v=0zx6PnQsImA")));
			}
		} else {
			showSorryDialog(getActivity(),
					"We don't have the requested video at the moment. But, stay tuned!");
		}
	}

}
