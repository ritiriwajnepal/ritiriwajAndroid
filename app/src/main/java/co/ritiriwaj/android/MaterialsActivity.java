package co.ritiriwaj.android;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
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
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import co.ritiriwaj.android.helper.MaterialsExpandableListItemAdapter;
import co.ritiriwaj.android.helper.MaterialsPagerAdapter;
import co.ritiriwaj.android.helper.ShowAlertDialog;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.nhaarman.listviewanimations.appearance.simple.AlphaInAnimationAdapter;
import com.nostra13.universalimageloader.core.ImageLoader;

public class MaterialsActivity extends AppCompatActivity {
	// ListView materialsListView;
	List<HashMap<String, String>> packageDetailsList;

	private static AsyncHttpClient client = new AsyncHttpClient();
	private static AsyncHttpClient materialClient = new AsyncHttpClient();
	private String GET_ALL_PACKAGES = MainActivity.IP
			+ "getPackagesByRitualCasteId/";
	private String GET_ALL_MATERIALS = MainActivity.IP
			+ "getMaterialsByPackageId/";
	String ritualCasteId;

	public static ProgressBar progressBarMaterials;

	@SuppressWarnings("unchecked")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.packages_layout);
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		ritualCasteId = getIntent().getExtras().getString("ritualCasteId");

		progressBarMaterials = (ProgressBar) findViewById(R.id.progressBarMaterials);
		progressBarMaterials.setVisibility(View.VISIBLE);

		if (savedInstanceState != null) {
			packageDetailsList = (List<HashMap<String, String>>) savedInstanceState
					.getSerializable("packageList");
			if (packageDetailsList != null)
				populatePackageDetails(packageDetailsList);
			else
				doShit();
		} else {
			doShit();
		}
	}

	@Override
	public void onDestroy() {
		client.cancelRequests(this, true);
		client.cancelAllRequests(true);
		super.onDestroy();
	}

	@Override
	public void onPause() {
		client.cancelRequests(this, true);
		client.cancelAllRequests(true);
		super.onPause();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putSerializable("packageList",
				(Serializable) packageDetailsList);
	}

	private void doShit() {
		client.get(this, GET_ALL_PACKAGES + ritualCasteId,
				new JsonHttpResponseHandler() {
					@Override
					public void onSuccess(int statusCode, Header[] headers,
							JSONArray response) {
						try {
							List<HashMap<String, String>> packageDetailsList = new ArrayList<HashMap<String, String>>();
							for (int i = 0; i < response.length(); i++) {
								JSONObject jsonObjectRow = response
										.getJSONObject(i);
								HashMap<String, String> packageMap = new HashMap<String, String>();
								packageMap.put("id",
										jsonObjectRow.getString("id"));
								packageMap.put("name",
										jsonObjectRow.getString("title"));
								packageMap.put("value",
										jsonObjectRow.getString("price"));
								packageMap.put("imageURL",
										jsonObjectRow.getString("image"));
								packageDetailsList.add(i, packageMap);
								Log.i("MaterialActivity", packageMap.toString());
							}
							populatePackageDetails(packageDetailsList);
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}

					@Override
					public void onFailure(int statusCode, Header[] headers,
							Throwable throwable, JSONArray errorResponse) {
						if (throwable.getCause() instanceof Exception) {
							showAlertDialog(MaterialsActivity.this,
									"Oops!!! so sorry, we encountered a server error");
						} else {
							showAlertDialog(MaterialsActivity.this,
									"Oops!!! There seems to be a network error.Please check network connection");
						}
					}
				});

	}

	private void populatePackageDetails(
			final List<HashMap<String, String>> packageDetailsList) {
		this.packageDetailsList = packageDetailsList;
		List<String> viewTitleList = new ArrayList<String>();
		ViewPager packageViewPager = (ViewPager) findViewById(R.id.packagesViewPager);
		ArrayList<View> views = new ArrayList<View>();
		LayoutInflater inflater = (LayoutInflater) getApplicationContext()
				.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

		for (int i = 0; i < packageDetailsList.size(); i++) {
			View packageView = inflater.inflate(
					R.layout.packages_view_layout,
					(ViewGroup) getWindow().getDecorView().findViewById(
							android.R.id.content), false);
			final ListView materialsListView = (ListView) packageView
					.findViewById(R.id.materialsListView);

			final String id = packageDetailsList.get(i).get("id");
			Button btnOrderNow = (Button) packageView
					.findViewById(R.id.btn_order_now);
			btnOrderNow.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					// viewOrderFormFragment(packageDetailsList.get(i));
					FragmentTransaction ft = getSupportFragmentManager()
							.beginTransaction();
					Bundle args = new Bundle();
					args.putString("packageId", id);
					DialogFragment orderFormFragment = OrderFormFragment
							.newInstance(args);
					orderFormFragment.show(ft, "orderFormFragment");

				}
			});

			ImageView packImageView = ((ImageView) packageView
					.findViewById(R.id.packageImageView));
			// ImageLoader.getInstance().init(SplashScreen.config);
			ImageLoader.getInstance().displayImage(
					MainActivity.PACKAGE_IMAGE_IP
							+ packageDetailsList.get(i).get("imageURL"),
					packImageView, SplashScreen.options);
			TextView packageTitle = (TextView) packageView
					.findViewById(R.id.packageTitleTxtView);
			packageTitle.setText(packageDetailsList.get(i).get("name"));
			TextView packageValue = (TextView) packageView
					.findViewById(R.id.packageValueTxtView);
			packageValue
					.setText("Rs." + packageDetailsList.get(i).get("value"));

			viewTitleList.add(i, packageDetailsList.get(i).get("name"));
			// to get material list
			materialClient.get(GET_ALL_MATERIALS
					+ packageDetailsList.get(i).get("id"),
					new JsonHttpResponseHandler() {
						@Override
						public void onFailure(int statusCode, Header[] headers,
								String responseString, Throwable throwable) {

							Log.e("materialList", responseString);
						}

						@Override
						public void onSuccess(int statusCode, Header[] headers,
								JSONArray response) {

							try {
								List<HashMap<String, String>> materialListHashMaps = new ArrayList<HashMap<String, String>>();
								for (int j = 0; j < response.length(); j++) {

									HashMap<String, String> materialHashMap = new HashMap<String, String>();
									JSONObject jsonObject = response
											.getJSONObject(j);
									// id name unit
									materialHashMap.put("materialId",
											jsonObject.getString("id"));
									materialHashMap.put("materialName",
											jsonObject.getString("name"));
									materialHashMap.put("materialImageURL",
											jsonObject.getString("image"));
									materialHashMap.put("materialDesc",
											jsonObject.getString("desc"));
									materialHashMap.put("materialUnit",
											jsonObject.getString("unit"));
									materialListHashMaps
											.add(j, materialHashMap);
									Log.i("materialsListHashMap", Arrays
											.toString(materialListHashMaps
													.toArray()));
								}
								populateMaterialsList(materialListHashMaps,
										materialsListView);

							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}

						}

						@Override
						public void onFailure(int statusCode, Header[] headers,
								Throwable throwable, JSONArray errorResponse) {
							if (throwable.getCause() instanceof Exception) {
								showAlertDialog(MaterialsActivity.this,
										"Oops!!! so sorry, we encountered a server error");
							} else {
								showAlertDialog(MaterialsActivity.this,
										"Oops!!! There seems to be a network error.Please check network connection");
							}
						}
					});

			views.add(i, packageView);

			progressBarMaterials.setVisibility(View.INVISIBLE);

		}

		packageViewPager.setAdapter(new MaterialsPagerAdapter(views,
				viewTitleList));
		// Bind the title indicator to the adapter
//		TitlePageIndicator titleIndicator = (TitlePageIndicator) findViewById(R.id.packagesPagerTitleIndicator);
//		titleIndicator.setViewPager(packageViewPager);

	}

	protected void viewOrderFormFragment(HashMap<String, String> packageData) {

		String packageId = packageData.get("id");
		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
		Bundle args = new Bundle();
		args.putString("packageId", packageId);
		DialogFragment orderFormFragment = OrderFormFragment.newInstance(args);
		orderFormFragment.show(ft, "orderFormFragment");

	}

	private void populateMaterialsList(
			List<HashMap<String, String>> materialsDetailsList,
			ListView materialsListView) {

		MaterialsExpandableListItemAdapter mExpandableListItemAdapter = new MaterialsExpandableListItemAdapter(
				MaterialsActivity.this, materialsDetailsList);
		materialsListView.setAdapter(mExpandableListItemAdapter);
		AlphaInAnimationAdapter alphaInAnimationAdapter = new AlphaInAnimationAdapter(
				mExpandableListItemAdapter);
		alphaInAnimationAdapter.setAbsListView(materialsListView);
		final ScrollView scrollview = ((ScrollView) findViewById(R.id.scrollView));
		scrollview.post(new Runnable() {
			@Override
			public void run() {
				scrollview.fullScroll(ScrollView.FOCUS_UP);
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
