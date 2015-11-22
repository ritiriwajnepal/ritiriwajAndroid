package co.ritiriwaj.android;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;
import co.ritiriwaj.android.helper.ShowAlertDialog;
import co.ritiriwaj.android.rowadapter.CommentRowAdapter;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class ReasonsAndCommentsActivity extends AppCompatActivity implements
		OnClickListener {
	int subProId;
	private static String REASON_URL = MainActivity.IP
			+ "getReasonsBySubProcessId/";
	TextView religiousReason, scientificReason;
	EditText commentEditText;
	ImageButton commentButton;
	ListView commentListView;
	// SocialAuthAdapter socialAuthAdapter;
	String reasonWhyId;
	SharedPreferences sharedPreferences;
	List<HashMap<String, String>> currentCommentList;
	HashMap<String, String> reasonMap;
	ShowAlertDialog alertDialog;
	SaveOutstateData saveOutstateData;
	private static AsyncHttpClient client = new AsyncHttpClient();
	private static AsyncHttpClient clientReason = new AsyncHttpClient();
	private static AsyncHttpClient clientComment = new AsyncHttpClient();
	private static AsyncHttpClient clientPostComment = new AsyncHttpClient();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_reasons_and_comment);

		religiousReason = (TextView) findViewById(R.id.religiousReasonTextView);
		scientificReason = (TextView) findViewById(R.id.scientificReasonTextView);
		commentEditText = (EditText) findViewById(R.id.commentEditText);
		commentButton = (ImageButton) findViewById(R.id.commentButton);
		commentButton.setOnClickListener(this);
		commentListView = (ListView) findViewById(R.id.commentsListView);
		alertDialog = new ShowAlertDialog(ReasonsAndCommentsActivity.this,
				"Loading...");
		subProId = Integer.valueOf(getIntent().getExtras()
				.getString("subProId"));

		sharedPreferences = MainActivity.sharedPreferences;

		// socialAuthAdapter = new SocialAuthAdapter(new ResponseListener());

		if (savedInstanceState != null) {
			SaveOutstateData temp = (SaveOutstateData) savedInstanceState
					.get("saveData");
			setReasonText(temp.getReasonMap());
			setComments(temp.getCommentList());
		} else {
			if (reasonMap != null && currentCommentList != null) {
				saveOutstateData = new SaveOutstateData(currentCommentList,
						reasonMap);
				setReasonText(reasonMap);
				setComments(currentCommentList);
			} else {

				alertDialog.show(getSupportFragmentManager(), "alertDialog");

				// GetAllReasonsAsync allReasonsAsync = new
				// GetAllReasonsAsync();
				// allReasonsAsync.executeOnExecutor(
				// AsyncTask.THREAD_POOL_EXECUTOR, REASON_URL + subProId);
				doShit();
			}
		}

	}

	@Override
	protected void onStop() {
		client.cancelRequests(this, true);
		clientComment.cancelRequests(this, true);
		clientPostComment.cancelRequests(this, true);
		clientReason.cancelRequests(this, true);
		super.onStop();
	}

	private void setReasonText(HashMap<String, String> reasonMap) {
		this.reasonMap = reasonMap;
		religiousReason.setText(reasonMap.get("religious_reason"));
		scientificReason.setText(reasonMap.get("scientific_reason"));
	}

	public void setComments(List<HashMap<String, String>> commentList) {
		this.currentCommentList = commentList;
		CommentRowAdapter commentRowAdapter = new CommentRowAdapter(this,
				commentList);

		commentListView.setAdapter(commentRowAdapter);

		setListViewHeightBasedOnChildren(commentListView);

	}

	private void postUserData(RequestParams params) {
		clientPostComment.post(MainActivity.IP + "postReasonComment", params,
				new JsonHttpResponseHandler() {
					@Override
					public void onSuccess(int statusCode, Header[] headers,
							JSONObject jsonObject) {
						HashMap<String, String> hashMap = new HashMap<String, String>();
						try {

							if (jsonObject.getString("response")
									.equalsIgnoreCase("success")) {
								Editor editor = sharedPreferences.edit();
								editor.putString("subscriberId",
										jsonObject.getString("subscriberId"))
										.commit();
								hashMap.put("full_name", sharedPreferences
										.getString("subscriberFullName", ""));
								hashMap.put("commentId",
										jsonObject.getString("commentId"));
								hashMap.put("text", commentEditText.getText()
										.toString());

								currentCommentList.add(0, hashMap);

								setComments(currentCommentList);

								commentEditText.setText("");
							}

							alertDialog.dismiss();

						} catch (IllegalStateException | JSONException e) {
							e.printStackTrace();
							Log.e("JSON error", e.getMessage());
						}
					}

					@Override
					public void onFailure(int statusCode, Header[] headers,
							Throwable throwable, JSONObject errorResponse) {

					}
				});
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		saveOutstateData = new SaveOutstateData(currentCommentList, reasonMap);
		outState.putParcelable("saveData", saveOutstateData);

	}

	private void doShit() {
		clientReason.get(REASON_URL + subProId, new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers,
					JSONObject response) {
				HashMap<String, String> hashMap = new HashMap<String, String>();
				try {
					reasonWhyId = response.getString("id");
					hashMap.put("id", response.getString("id"));
					hashMap.put("religious_reason",
							response.getString("religious_reason"));
					hashMap.put("scientific_reason",
							response.getString("sci_reason"));
				} catch (JSONException e) {
				}

				setReasonText(hashMap);
				clientComment.get(MainActivity.IP
						+ "getAllCommentsByReasonWhyId/" + reasonWhyId,
						new JsonHttpResponseHandler() {
							@Override
							public void onSuccess(int statusCode,
									Header[] headers, JSONArray dataArray) {
								JSONObject jsonObject;
								List<HashMap<String, String>> listMaps = new ArrayList<HashMap<String, String>>();
								try {
									for (int i = 0; i < dataArray.length(); i++) {
										jsonObject = dataArray.getJSONObject(i);
										HashMap<String, String> tempHashMap = new HashMap<String, String>();
										tempHashMap.put("commentId",
												jsonObject.getString("id"));
										tempHashMap.put("text",
												jsonObject.getString("text"));
										tempHashMap.put("full_name", jsonObject
												.getString("full_name"));
										listMaps.add(i, tempHashMap);

									}
								} catch (JSONException e) {
								}
								setComments(listMaps);
								alertDialog.dismiss();
							}

							@Override
							public void onFailure(int statusCode,
									Header[] headers, Throwable throwable,
									JSONArray errorResponse) {
								if (throwable.getCause() instanceof Exception) {
									showAlertDialog(
											ReasonsAndCommentsActivity.this,
											"Oops!!! so sorry, we encountered a server error");
								} else {
									showAlertDialog(
											ReasonsAndCommentsActivity.this,
											"Oops!!! There seems to be a network error.Please check network connection");
								}
							}
						});

			}

			@Override
			public void onSuccess(int statusCode, Header[] headers,
					JSONArray response) {
				showAlertDialog(ReasonsAndCommentsActivity.this,
						response.toString());
			}
		});
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.commentButton:
			if (!commentEditText.getText().toString().isEmpty()) {
				alertDialog = new ShowAlertDialog(
						ReasonsAndCommentsActivity.this, "Posting comment...");

				alertDialog.show(getSupportFragmentManager(), "alertDailog");

				if (sharedPreferences.getString("subscriberId", "").isEmpty()) {
					// socialAuthAdapter.authorize(this, Provider.FACEBOOK);
//					Session.openActiveSession(ReasonsAndCommentsActivity.this,
//							true, callback);
				} else {
					HashMap<String, String> nameValuePairs = new HashMap<String, String>();
					nameValuePairs.put("subscriberId",
							sharedPreferences.getString("subscriberId", ""));
					nameValuePairs.put("subscriberEmail",
							sharedPreferences.getString("subscriberEmail", ""));
					nameValuePairs.put("subscriberFullName", sharedPreferences
							.getString("subscriberFullName", ""));
					nameValuePairs.put("commentText", commentEditText.getText()
							.toString());
					nameValuePairs.put("reasonWhyId", reasonWhyId);
					RequestParams params = new RequestParams(nameValuePairs);
					postUserData(params);

					// PostUserDataAsync postUserDataAsync = new
					// PostUserDataAsync();
					// postUserDataAsync.execute(nameValuePairs);

				}
			} else {
				Toast.makeText(ReasonsAndCommentsActivity.this,
						"Comment must not be blank", Toast.LENGTH_LONG).show();
			}
			break;

		default:
			break;
		}
	}

	//

	private class SaveOutstateData implements Parcelable {

		List<HashMap<String, String>> commentList;
		HashMap<String, String> reasonMap;

		public SaveOutstateData(Parcel in) {
			readFromParcel(in);
		}

		public SaveOutstateData(List<HashMap<String, String>> commentList,
				HashMap<String, String> reasonMap) {
			setCommentList(commentList);
			setReasonMap(reasonMap);
		}

		public List<HashMap<String, String>> getCommentList() {
			return this.commentList;
		}

		public void setCommentList(List<HashMap<String, String>> commentList) {
			this.commentList = commentList;
		}

		public HashMap<String, String> getReasonMap() {
			return this.reasonMap;
		}

		public void setReasonMap(HashMap<String, String> reasonMap) {
			this.reasonMap = reasonMap;
		}

		@Override
		public int describeContents() {
			return 0;
		}

		@Override
		public void writeToParcel(Parcel dest, int flags) {
			dest.writeList(commentList);
			dest.writeMap(reasonMap);
		}

		private void readFromParcel(Parcel in) {
			in.readList(commentList, null);
			reasonMap = in.readHashMap(null);
		}

		public final Parcelable.Creator<Object> CREATOR = new Parcelable.Creator<Object>() {
			public SaveOutstateData createFromParcel(Parcel in) {
				return new SaveOutstateData(in);
			}

			public SaveOutstateData[] newArray(int size) {
				return new SaveOutstateData[size];
			}
		};

	}

	/****
	 * Method for Setting the Height of the ListView dynamically. Hack to fix
	 * the issue of not showing all the items of the ListView when placed inside
	 * a ScrollView
	 ****/
	public static void setListViewHeightBasedOnChildren(ListView listView) {
		ListAdapter listAdapter = listView.getAdapter();
		if (listAdapter == null)
			return;

		int desiredWidth = MeasureSpec.makeMeasureSpec(listView.getWidth(),
				MeasureSpec.UNSPECIFIED);
		int totalHeight = 0;
		View view = null;
		for (int i = 0; i < listAdapter.getCount(); i++) {
			view = listAdapter.getView(i, view, listView);
			if (i == 0)
				view.setLayoutParams(new ViewGroup.LayoutParams(desiredWidth,
						LayoutParams.WRAP_CONTENT));

			view.measure(desiredWidth, MeasureSpec.UNSPECIFIED);
			totalHeight += view.getMeasuredHeight();
		}
		ViewGroup.LayoutParams params = listView.getLayoutParams();
		params.height = totalHeight
				+ (listView.getDividerHeight() * (listAdapter.getCount() - 1));
		listView.setLayoutParams(params);
		listView.requestLayout();

	}

//	private void onSessionStateChange(Session session, SessionState state,
//			Exception exception) {
//
//		if (state.isOpened()) {
//			// make request to the /me API
//			Request request = Request.newMeRequest(session,
//					new Request.GraphUserCallback() {
//						// callback after Graph API response with user object
//						@Override
//						public void onCompleted(GraphUser user,
//								Response response) {
//							Editor editor = sharedPreferences.edit();
//							editor.putString("userId", user.getId())
//									.putString("subscriberFullName",
//											user.getName())
//									.putString(
//											"subscriberEmail",
//											user.asMap().get("email")
//													.toString()).commit();
//							HashMap<String, String> tempHashMap = new HashMap<String, String>();
//							tempHashMap.put("subscriberFullName",
//									user.getName());
//							tempHashMap.put("subscriberEmail", user.asMap()
//									.get("email").toString());
//							RequestParams params = new RequestParams(
//									tempHashMap);
//							client.post(MainActivity.GET_SUBSCRIBER_ID, params,
//									new JsonHttpResponseHandler() {
//										@Override
//										public void onSuccess(int statusCode,
//												Header[] headers,
//												JSONObject response) {
//											try {
//												Log.i("SUBSCRIBER ID", response
//														.get("subscriberId")
//														.toString());
//												Editor tempEdit = sharedPreferences
//														.edit();
//												tempEdit.putString(
//														"subscriberId",
//														response.get(
//																"subscriberId")
//																.toString())
//														.commit();
//											} catch (JSONException e) {
//												e.printStackTrace();
//											}
//										}
//									});
//
//							HashMap<String, String> nameValuePairs = new HashMap<String, String>();
//							nameValuePairs.put("subscriberId",
//									sharedPreferences.getString("subscriberId",
//											""));
//							nameValuePairs.put("subscriberEmail",
//									sharedPreferences.getString(
//											"subscriberEmail", ""));
//							nameValuePairs.put("subscriberFullName",
//									sharedPreferences.getString(
//											"subscriberFullName", ""));
//							nameValuePairs.put("commentText", commentEditText
//									.getText().toString());
//							nameValuePairs.put("reasonWhyId", reasonWhyId);
//							RequestParams paramsReq = new RequestParams(
//									nameValuePairs);
//							postUserData(paramsReq);
//						}
//					});
//			Request.executeBatchAsync(request);
//
//			Log.i("ReasonsAndComment", "Logged in...");
//		} else if (state.isClosed()) {
//			Log.i("ReasonsAndComment", "Logged out...");
//		}
//	}

//	private Session.StatusCallback callback = new Session.StatusCallback() {
//		@Override
//		public void call(Session session, SessionState state,
//				Exception exception) {
//			onSessionStateChange(session, state, exception);
//		}
//	};
//
//	@Override
//	public void onResume() {
//		super.onResume();
//		uiHelper.onResume();
//	}
//
//	@Override
//	public void onActivityResult(int requestCode, int resultCode, Intent data) {
//		super.onActivityResult(requestCode, resultCode, data);
//		uiHelper.onActivityResult(requestCode, resultCode, data);
//	}
//
//	@Override
//	public void onPause() {
//		super.onPause();
//		uiHelper.onPause();
//	}
//
//	@Override
//	public void onDestroy() {
//		super.onDestroy();
//		uiHelper.onDestroy();
//	}

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