package co.ritiriwaj.android;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;
import co.ritiriwaj.android.helper.AddEventDailogFragment;
import co.ritiriwaj.android.helper.EventListDialogFragment;
import co.ritiriwaj.android.helper.SimplePagerAdapterNoSide;

import com.google.android.youtube.player.YouTubeApiServiceUtil;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubeStandalonePlayer;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.nostra13.universalimageloader.core.ImageLoader;


@SuppressLint("InflateParams")
public class SingleRiwajFragment extends Fragment implements OnClickListener {

	Button btnViewProcedure;
	Button watchVideo;
	RelativeLayout layout;
	ViewGroup container;
	private SharedPreferences sharedPref;

	TextView riwajTitle;
	TextView riwajWhatTextView;
	TextView riwajDate;
	TextView riwajRemDays;
	TextView txtPersonalize;

	View view;
	private int ritualId;
	private String imageURL;
	private String ritualTitle, ritualDate, ritualRemDay;
	private String personalEvents;
	private String videoURL;


	ViewPager contentViewPager;

	List<HashMap<String, String>> eventList;

	private AsyncHttpClient client = new AsyncHttpClient();
	/*
	 * "ritual" is used when indicating hashMap "riwaj" is used when indicating
	 * args
	 */

	private static String GET_RITUAL_DETAILS = MainActivity.IP
			+ "getRitualDetails";

	HashMap<String, String> ritualDetails = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRetainInstance(true);
		// setHasOptionsMenu(true);
				sharedPref = PreferenceManager
				.getDefaultSharedPreferences(getActivity());

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		this.container = container;
		view = inflater.inflate(R.layout.fragment_riwaj_single, container,
				false);

		// Set values from previous card (Ritual ie Clicked)

		ritualId = (int) getArguments().get("riwajId");
		imageURL = getArguments().get("imageURL").toString();
		ritualTitle = getArguments().get("riwajTitle").toString();

		if (getArguments().get("riwajDate") != null)
			ritualDate = getArguments().get("riwajDate").toString();
		if (getArguments().get("riwajDesc") != null)
			ritualRemDay = getArguments().get("riwajDesc").toString();

		MainActivity.toolbar.setTitle(getString(R.string.app_name) + " - "
				+ ritualTitle);
		// LoginButton authButton = (LoginButton) MainActivity.mySlidingMenu
		// .findViewById(R.id.authButton);
		// authButton.setFragment(this);

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
		return view;

	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putBoolean(PENDING_PUBLISH_KEY, pendingPublishReauthorization);
		outState.putSerializable("ritualDetails", ritualDetails);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		MainActivity.progressBarMain.setVisibility(View.VISIBLE);
		LayoutInflater inflater = getActivity().getLayoutInflater();
		View bottomToolbarView = inflater.inflate(
				R.layout.single_riwaj_bottom_toolbar, null, false);
		FrameLayout bottomToolbarPlaceHolder = (FrameLayout) MainActivity.bottomToolBar
				.findViewById(R.id.toolbar_placeholder);
		bottomToolbarPlaceHolder.removeAllViews();
		bottomToolbarPlaceHolder.addView(bottomToolbarView);
		bottomToolbarView.findViewById(R.id.btn_watch_history)
				.setOnClickListener(this);
		bottomToolbarView.findViewById(R.id.btn_view_procedure)
				.setOnClickListener(this);

		riwajTitle = (TextView) view.findViewById(R.id.txt_riwaj_title);
		riwajDate = (TextView) view.findViewById(R.id.txt_riwaj_date);
		riwajRemDays = (TextView) view.findViewById(R.id.txt_riwaj_rem_days);
		txtPersonalize = (TextView) view.findViewById(R.id.personaliseText);
		txtPersonalize.setOnClickListener(this);
		riwajWhatTextView = (TextView) view.findViewById(R.id.whatTextView);

		contentViewPager = (ViewPager) view.findViewById(R.id.tabsViewPager);

		if (savedInstanceState != null) {
			Log.i("SingleRiwajFragment", "Restoring Fragment Data");
			pendingPublishReauthorization = savedInstanceState.getBoolean(
					PENDING_PUBLISH_KEY, false);
			ritualDetails = (HashMap<String, String>) savedInstanceState
					.getSerializable("ritualDetails");
			if (ritualDetails != null)
				setRitualDetails(ritualDetails);
			else
				doShit();
		} else {
			if (ritualDetails != null) {
				Log.i("SingleRiwajFragment", "Leaving everthing as is");
				setRitualDetails(ritualDetails);
				return;
			} else {
				Log.i("SingleRiwajFragment", "Fetching Fragment Data");

				doShit();

			}
		}

	}

	public void doShit() {

		final HashMap<String, String> ritualDetailsMap = new HashMap<String, String>();

		/*
		 * adding data not in JSON but obtained from earlier fragment, code in
		 * very very early stage of development
		 */

		ritualDetailsMap.put("ritualId", String.valueOf(ritualId));
		ritualDetailsMap.put("ritualName", ritualTitle);
		ritualDetailsMap.put("ritualImageId", imageURL + "");

		if (ritualDate != null)
			ritualDetailsMap.put("ritualDate", ritualDate);

		if (ritualRemDay != null)
			ritualDetailsMap.put("ritualRemDays", ritualRemDay);

		SharedPreferences preferences = PreferenceManager
				.getDefaultSharedPreferences(getActivity());

		HashMap<String, String> hashMap = new HashMap<String, String>();

		hashMap.put("ritualId", String.valueOf(ritualId));

		hashMap.put("subscriberId", preferences.getString("subscriberId", ""));
		hashMap.put("subscriberFullName",
				preferences.getString("subscriberFullName", ""));
		hashMap.put("subscriberEmail",
				preferences.getString("subscriberEmail", ""));

		RequestParams params = new RequestParams(hashMap);

		client.post(getActivity(), GET_RITUAL_DETAILS, params,
				new JsonHttpResponseHandler() {
					@Override
					public void onSuccess(int statusCode, Header[] headers,
							JSONObject jsonObject) {

						try {

							if (jsonObject.has("eventList")) {
								ritualDetailsMap.put("eventList", jsonObject
										.getJSONArray("eventList").toString());
							}
							// Get Extra Information of Ritual

							ritualDetailsMap.put("ritualWhat",
									jsonObject.getString("what"));
							ritualDetailsMap.put("ritualHistory",
									jsonObject.getString("history"));
							ritualDetailsMap.put("ritualInteresting",
									jsonObject.getString("interesting_facts"));
							ritualDetailsMap.put("ritualSignificance",
									jsonObject.getString("significance"));
							ritualDetailsMap.put("ritualSource",
									jsonObject.getString("source"));
							ritualDetailsMap.put("ritualVideo",
									jsonObject.getString("videoURL"));

						} catch (IllegalStateException | JSONException e) {
							e.printStackTrace();
							Log.e("JSON error", e.getMessage());
						}

						setRitualDetails(ritualDetailsMap);

					}

					@Override
					public void onFailure(int statusCode, Header[] headers,
							Throwable throwable, JSONObject errorResponse) {
						Log.i("Failure", throwable.getCause() + " JSONObject");
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
							String responseString, Throwable throwable) {
						if (throwable.getCause() instanceof Exception) {
							showAlertDialog(getActivity(), responseString);
						} else {
							showAlertDialog(getActivity(), responseString);
						}
					}
				});
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

	protected void viewCastFragment() {

		FragmentTransaction ft = getFragmentManager().beginTransaction();
		Bundle args = new Bundle();
		args.putString("riwajTitle", ritualTitle);
		args.putInt("riwajId", ritualId);
		DialogFragment castFragment = CastFragment.newInstance(args);
		castFragment.setTargetFragment(this, 0);
		castFragment.show(ft, "castFragment");

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_view_procedure:
			viewCastFragment();
			break;

		case R.id.btn_watch_history:
			// showShareDialog(getActivity());
			watchVideo(ritualDetails.get("ritualVideo"));
			break;
		case R.id.personaliseText:
			if (!sharedPref.getString("subscriberId", "").equalsIgnoreCase("")) {
				if (ritualDetails.get("eventList") != null) {

					// View Event List Dialog
					EventListDialogFragment eventListDialog = new EventListDialogFragment(
							eventList, String.valueOf(ritualId));
					eventListDialog.show(getActivity()
							.getSupportFragmentManager(), "EVENT DIALOG");
				} else {
					// Display Add Event Dialog
					AddEventDailogFragment addEventDailogFragment = new AddEventDailogFragment(
							getActivity(), String.valueOf(ritualId));
					addEventDailogFragment.show(getActivity()
							.getSupportFragmentManager(), "add event");

				}
			} else {
//				MainActivity.mySlidingMenu.toggle();
			}
			break;
		default:
			break;
		}

	}

	private void setRitualDetails(final HashMap<String, String> details) {

		MainActivity.progressBarMain.setVisibility(View.INVISIBLE);

		ritualDetails = details;

		riwajTitle.setText(details.get("ritualName"));
		riwajWhatTextView.setText(details.get("ritualWhat"));

		ImageView riwajImage = (ImageView) view.findViewById(R.id.icon);
		Drawable oldImage = riwajImage.getDrawable();

		if (!details.get("ritualImageId").isEmpty()) {

			ImageLoader.getInstance()
					.displayImage(
							MainActivity.RITUAL_IMAGE_IP
									+ details.get("ritualImageId"), riwajImage,
							SplashScreen.options);
		}
		if (oldImage != null) {
			((BitmapDrawable) oldImage).getBitmap().recycle();

		}

		if (details.get("ritualDate") != null) {
			riwajDate.setVisibility(View.VISIBLE);
			riwajRemDays.setVisibility(View.VISIBLE);

			riwajDate.setText(details.get("ritualDate"));
			riwajRemDays.setText(details.get("ritualRemDays"));

		} else {
			riwajDate.setVisibility(View.INVISIBLE);
			riwajRemDays.setVisibility(View.INVISIBLE);

			txtPersonalize.setVisibility(View.VISIBLE);

			if (details.get("eventList") != null) {
				txtPersonalize.setText("View Events");

				try {
					JSONArray jsonEventList = new JSONArray(
							details.get("eventList"));
					eventList = new ArrayList<>();

					for (int i = 0; i < jsonEventList.length(); i++) {
						JSONObject eventObject = jsonEventList.getJSONObject(i);
						HashMap<String, String> eventMap = new HashMap<>();
						eventMap.put("id", eventObject.getString("id"));
						eventMap.put("title", eventObject.getString("title"));
						eventMap.put("date", eventObject.getString("date"));
						eventMap.put("remDay", eventObject.getString("remDay"));
						eventList.add(i, eventMap);
					}

				} catch (JSONException e) {
					e.printStackTrace();
				}

			}

		}

		ArrayList<View> views = new ArrayList<View>();
		for (int i = 0; i < 4; i++) {
			LayoutInflater inflater = (LayoutInflater) getActivity()
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View tempView = inflater.inflate(
					R.layout.view_layout_for_single_riwaj, null, false);

			TextView riwajPagerTitle = (TextView) tempView
					.findViewById(R.id.riwajInfoTitleTextView);
			TextView riwajPagerDesc = (TextView) tempView
					.findViewById(R.id.riwajInfoDescTextView);

			switch (i) {
			case 0:
				riwajPagerTitle.setText("History");
				riwajPagerDesc.setText(details.get("ritualHistory"));
				break;

			case 1:
				riwajPagerTitle.setText("Significance");
				riwajPagerDesc.setText(details.get("ritualSignificance"));
				break;

			case 2:
				riwajPagerTitle.setText("Interesting Facts");
				riwajPagerDesc.setText(details.get("ritualInteresting"));
				break;

			case 3:
				riwajPagerTitle.setText("Information Source");
				riwajPagerDesc.setText(details.get("ritualSource"));
				break;

			default:
				break;
			}

			views.add(tempView);
		}
		SimplePagerAdapterNoSide adapterNoSide = new SimplePagerAdapterNoSide(
				views);

		contentViewPager.setAdapter(adapterNoSide);

//		PagerSlidingTabStrip tabs = (PagerSlidingTabStrip) view
//				.findViewById(R.id.tabs);
//		tabs.setViewPager(contentViewPager);

		container.removeView(layout);
		view.setVisibility(View.VISIBLE);
		MainActivity.bottomToolBar.setVisibility(View.VISIBLE);

	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		Log.i("single riwaj", "on activity result");
		/* use this when onActivityResult issue is solved for facebook */
		// uiHelper.onActivityResult(requestCode, resultCode, data,
		// new FacebookDialog.Callback() {
		// @Override
		// public void onError(FacebookDialog.PendingCall pendingCall,
		// Exception error, Bundle data) {
		// Log.e("Activity",
		// String.format("Error: %s", error.toString()));
		// }
		//
		// @Override
		// public void onComplete(
		// FacebookDialog.PendingCall pendingCall, Bundle data) {
		// Log.i("Activity", "Success!");
		// // Nepal 4K - GGOksApHzeo
		// Intent intent = YouTubeStandalonePlayer
		// .createVideoIntent(
		// getActivity(),
		// "AIzaSyAaUfCB6EYlpFbwcRj-Q6M7qdKxG2tmwDw",
		// "0zx6PnQsImA", 0, false, false);
		// startActivity(intent);
		//
		// }
		// });

		if (requestCode == 0 && resultCode == 1) {

			ProcedureListFragment procedureListFragment = new ProcedureListFragment();
			Bundle args = new Bundle();
			args.putString("egId", data.getExtras().getString("egId"));
			args.putString("egcId", data.getExtras().getString("egcId"));
			args.putString("egName", data.getExtras().getString("egName"));
			procedureListFragment.setArguments(args);
			getFragmentManager().beginTransaction().addToBackStack(null)
					.replace(R.id.fragment_placeholder, procedureListFragment)
					.commit();

		}
	}

	public void showShareDialog(Context context) {

		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setMessage("Please share to watch the video")
				.setPositiveButton("Share",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
//								if (!sharedPref.getString("subscriberId", "")
//										.equalsIgnoreCase(""))
//									publishStory();
//								else {
//									MainActivity.mySlidingMenu.toggle();
//								}
							}
						})
				.setNegativeButton("Cancel",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								dialog.dismiss();
							}
						});

		final AlertDialog alertDialog = builder.create();

		alertDialog.show();

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

	private static final List<String> PERMISSIONS = Arrays
			.asList("publish_actions");
	private static final String PENDING_PUBLISH_KEY = "pendingPublishReauthorization";
	private boolean pendingPublishReauthorization = false;

	private boolean isSubsetOf(Collection<String> subset,
			Collection<String> superset) {
		for (String string : subset) {
			if (!superset.contains(string)) {
				return false;
			}
		}
		return true;
	}

//	private void publishStory() {
//		Session session = Session.getActiveSession();
//
//		if (session != null) {
//
//			// Check for publish permissions
//			List<String> permissions = session.getPermissions();
//			if (!isSubsetOf(PERMISSIONS, permissions)) {
//				pendingPublishReauthorization = true;
//				Session.NewPermissionsRequest newPermissionsRequest = new Session.NewPermissionsRequest(
//						this, PERMISSIONS);
//				session.requestNewPublishPermissions(newPermissionsRequest);
//				return;
//			}
//
//			/*
//			 * shareDialog calls mainActivity's onActivityResult so right now
//			 * shareDialog is useless
//			 */
//
//			// if (FacebookDialog.canPresentShareDialog(getActivity(),
//			// FacebookDialog.ShareDialogFeature.SHARE_DIALOG)) {
//			// // Publish the post using the Share Dialog
//			// FacebookDialog shareDialog = new
//			// FacebookDialog.ShareDialogBuilder(
//			// getActivity()).setLink(
//			// "https://developers.facebook.com/android").build();
//			// uiHelper.trackPendingDialogCall(shareDialog.present());
//			//
//			// } else {
//			// Fallback. For example, publish the post using the Feed Dialog
//			publishFeedDialog();
//			// }
//
//		}
//
//	}
//
//	private void publishFeedDialog() {
//		Bundle params = new Bundle();
//		params.putString("name", "Facebook SDK for Android");
//		params.putString("caption",
//				"Build great social apps and get more installs.");
//		params.putString(
//				"description",
//				"The Facebook SDK for Android makes it easier and faster to develop Facebook integrated Android apps.");
//		params.putString("link", "https://developers.facebook.com/android");
//		params.putString("picture",
//				"https://raw.github.com/fbsamples/ios-3.x-howtos/master/Images/iossdk_logo.png");
//
//		WebDialog feedDialog = (new WebDialog.FeedDialogBuilder(getActivity(),
//				Session.getActiveSession(), params)).setOnCompleteListener(
//				new OnCompleteListener() {
//
//					@Override
//					public void onComplete(Bundle values,
//							FacebookException error) {
//						if (error == null) {
//							// When the story is posted, echo the success
//							// and the post Id.
//							final String postId = values.getString("post_id");
//							if (postId != null) {
//								watchVideo(ritualDetails.get("ritualVideo"));
//
//								Toast.makeText(
//										getActivity().getApplicationContext(),
//										postId, Toast.LENGTH_LONG).show();
//
//								Toast.makeText(getActivity(),
//										"Posted story, id: " + postId,
//										Toast.LENGTH_SHORT).show();
//							} else {
//								// User clicked the Cancel button
//								Toast.makeText(
//										getActivity().getApplicationContext(),
//										"Publish cancelled", Toast.LENGTH_SHORT)
//										.show();
//							}
//						} else if (error instanceof FacebookOperationCanceledException) {
//							// User clicked the "x" button
//							Toast.makeText(
//									getActivity().getApplicationContext(),
//									"Publish cancelled", Toast.LENGTH_SHORT)
//									.show();
//						} else {
//							// Generic, ex: network error
//							Toast.makeText(
//									getActivity().getApplicationContext(),
//									"Error posting story", Toast.LENGTH_SHORT)
//									.show();
//						}
//					}
//
//				}).build();
//		feedDialog.show();
//	}

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
		} else
			showSorryDialog(getActivity(),
					"We don't have the requested video at the moment. But, stay tuned!");
	}
}
