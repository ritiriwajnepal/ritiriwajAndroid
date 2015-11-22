package co.ritiriwaj.android;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.View;
import co.ritiriwaj.android.helper.MaterialsExpandableListItemAdapter;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.nhaarman.listviewanimations.appearance.simple.AlphaInAnimationAdapter;

public class MaterialsListFragment extends ListFragment {

	View view;
	private static AsyncHttpClient materialClient = new AsyncHttpClient();
	private String GET_ALL_MATERIALS = MainActivity.IP
			+ "getMaterialsByRitualCasteId/";
	private String ritualCasteId;
//	private String GET_ALL_MATERIALS = MainActivity.IP
//			+ "getMaterialsByPackageId/";

	public MaterialsListFragment(String ritualCasteId) {
		this.ritualCasteId = ritualCasteId; 
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {

		super.onActivityCreated(savedInstanceState);
		MainActivity.bottomToolBar.setVisibility(View.GONE);

		materialClient.get(
				GET_ALL_MATERIALS + ritualCasteId,
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
										"");
								
								materialListHashMaps.add(j, materialHashMap);
								Log.i("materialsListHashMap", Arrays
										.toString(materialListHashMaps
												.toArray()));
							}
							
							MaterialsExpandableListItemAdapter mExpandableListItemAdapter = new MaterialsExpandableListItemAdapter(
									getActivity(), materialListHashMaps);
							setListAdapter(mExpandableListItemAdapter);
							
							AlphaInAnimationAdapter alphaInAnimationAdapter = new AlphaInAnimationAdapter(
									mExpandableListItemAdapter);
							alphaInAnimationAdapter.setAbsListView(getListView());

						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

					}

//					@Override
//					public void onFailure(int statusCode, Header[] headers,
//							Throwable throwable, JSONArray errorResponse) {
//						if (throwable.getCause() instanceof Exception) {
//							showAlertDialog(MaterialsActivity.this,
//									"Oops!!! so sorry, we encountered a server error");
//						} else {
//							showAlertDialog(MaterialsActivity.this,
//									"Oops!!! There seems to be a network error.Please check network connection");
//						}
//					}
				});

		

	}

}
