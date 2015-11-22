package co.ritiriwaj.android;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.Signature;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentManager.OnBackStackChangedListener;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.loopj.android.http.AsyncHttpClient;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MainActivity extends AppCompatActivity implements
        OnBackStackChangedListener {

    // public static String DOMAIN = "http://27.34.91.77/RitiRiwaj/";
    public static String DOMAIN = "http://www.ritiriwajnepal.com.np/webapp/";
    //	 public static String DOMAIN = "http://10.0.0.1/RitiRiwaj/"; // loopback
//	public static String DOMAIN = "http://27.34.98.54/RitiRiwaj/"; 
    // for 127.0.0.1 if ip is 192.168.2.1


    public static String IP = DOMAIN + "webservice/";
    public static String IMAGES_IP = DOMAIN
            + "assets/uploads/subProcessImages/";
    public static String RITUAL_IMAGE_IP = DOMAIN
            + "assets/uploads/ritualImages/";
    public static String PACKAGE_IMAGE_IP = DOMAIN
            + "assets/uploads/packageImages/";
    public static String MATERIAL_IMAGE_IP = DOMAIN
            + "assets/uploads/materialImages/";
    public static String GET_SUBSCRIBER_ID = IP + "getSubscriberId";

    public static ProgressBar progressBarMain;

    private static AsyncHttpClient client = new AsyncHttpClient();

    public static Toolbar bottomToolBar;
    public static Toolbar toolbar;

    public static SharedPreferences sharedPreferences;
    View loginButtonsHolder, userProfileHolder;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.hideOverflowMenu();
        setSupportActionBar(toolbar);

        bottomToolBar = (Toolbar) findViewById(R.id.bottomToolbar);
        // Listen for changes in the back stack
        getSupportFragmentManager().addOnBackStackChangedListener(this);
        // Handle when activity is recreated like on orientation Change
        shouldDisplayHomeUp();


        progressBarMain = (ProgressBar) findViewById(R.id.progressBarMain);

        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    "co.ritiriwaj.android", PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:",
                        Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (NameNotFoundException e) {

        } catch (NoSuchAlgorithmException e) {

        }

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        if (findViewById(R.id.fragment_placeholder) != null) {
            if (savedInstanceState != null) {
                Log.i("MainActivity", "Restoring Current Fragment");
                return;

            } else {
                if (getIntent().getExtras().getString("fragmentToStart")
                        .contentEquals("RiwajListFragment")) {
                    RiwajListFragment riwajListFragment = new RiwajListFragment();
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.fragment_placeholder,
                                    riwajListFragment).commit();
                } else {
                    PersonalRiwajListFragment personalRiwajListFragment = new PersonalRiwajListFragment();
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.fragment_placeholder,
                                    personalRiwajListFragment).commit();
                }

            }
        }

    }

    @Override
    public void onBackStackChanged() {
        shouldDisplayHomeUp();
    }

    public void shouldDisplayHomeUp() {
        // Enable Up button only if there are entries in the back stack
        boolean canback = getSupportFragmentManager().getBackStackEntryCount() > 0;
        getSupportActionBar().setDisplayHomeAsUpEnabled(canback);
    }

    @Override
    public boolean onSupportNavigateUp() {
        // This method is called when the up button is pressed. Just the pop
        // back stack.
        getSupportFragmentManager().popBackStack();
        return true;
    }

//
//	// facebook SDK integration
//	private void onSessionStateChange(Session session, SessionState state,
//			Exception exception) {
//
//		if (state.isOpened()) {
//			SimpleConnectionCheck connectionCheck = new SimpleConnectionCheck(
//					MainActivity.this);
//			if (connectionCheck.isNetworkConnected()) {
//				// make request to the /me API
//				Request request = Request.newMeRequest(session,
//						new Request.GraphUserCallback() {
//							// callback after Graph API response with user
//							// object
//							@Override
//							public void onCompleted(GraphUser user,
//									Response response) {
//
//								Editor editor = sharedPreferences.edit();
//								editor.putString("userId", user.getId())
//										.putString("subscriberFullName",
//												user.getName())
//										.putString(
//												"subscriberEmail",
//												user.asMap().get("email")
//														.toString()).commit();
//								HashMap<String, String> tempHashMap = new HashMap<String, String>();
//								tempHashMap.put("subscriberFullName",
//										user.getName());
//								tempHashMap.put("subscriberEmail", user.asMap()
//										.get("email").toString());
//								RequestParams params = new RequestParams(
//										tempHashMap);
//								client.post(GET_SUBSCRIBER_ID, params,
//										new JsonHttpResponseHandler() {
//											@Override
//											public void onSuccess(
//													int statusCode,
//													Header[] headers,
//													JSONObject response) {
//												try {
//													Log.i("SUBSCRIBER ID",
//															response.get(
//																	"subscriberId")
//																	.toString());
//													Editor tempEdit = sharedPreferences
//															.edit();
//													tempEdit.putString(
//															"subscriberId",
//															response.get(
//																	"subscriberId")
//																	.toString())
//															.commit();
//
//													// Download all of the personal events also
//													TblPersonalCalendarDao personalCalendarDao = new TblPersonalCalendarDao(getApplicationContext());
//													personalCalendarDao.insertAllPersonalEvents(response.getInt("subscriberId"));
//
//												} catch (JSONException e) {
//													e.printStackTrace();
//												}
//											}
//										});
//								showUserProfile();
//							}
//						});
//				if (sharedPreferences.getString("subscriberId", "").isEmpty()) {
//					Request.executeBatchAsync(request);
//				} else {
//					showUserProfile();
//				}
//				Log.i("MainActivity", "Logged in...");
//			}
//
//		} else if (state.isClosed()) {
//			mySlidingMenu.findViewById(R.id.userProfileHolder).setVisibility(
//					View.GONE);
//
//			sharedPreferences.edit().clear().commit();
//			sharedPreferences.edit().remove("subscriberId").commit();
//
//			TblPersonalCalendarDao tblPersonalCalendarDao = new TblPersonalCalendarDao(getApplicationContext());
//			tblPersonalCalendarDao.deleteAllPersonalEvents();
//
//			Log.i("MainActivity", "removed subscriberId");
//
//			Log.i("MainActivity", "Logged out...");
//		}
//	}

//	private Session.StatusCallback callback = new Session.StatusCallback() {
//		@Override
//		public void call(Session session, SessionState state,
//				Exception exception) {
//			onSessionStateChange(session, state, exception);
//		}
//	};

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.i("main Activity", "onActivityResult");
    }


//	private void showUserProfile() {
//		ImageView userImage = (ImageView) mySlidingMenu
//				.findViewById(R.id.userImage);
//		Drawable oldImage = userImage.getBackground();
//
//		((TextView) mySlidingMenu.findViewById(R.id.userName))
//		// .setText(user.getName());
//				.setText(sharedPreferences.getString("subscriberFullName", ""));
//		if (!ImageLoader.getInstance().isInited())
//			ImageLoader.getInstance().init(SplashScreen.config);
//		ImageLoader.getInstance().displayImage(
//				"http://graph.facebook.com/"
//						// + user.getId()
//						+ sharedPreferences.getString("userId", "")
//						+ "/picture?type=normal", userImage,
//				SplashScreen.options);
//		if (oldImage != null)
//			((BitmapDrawable) oldImage).getBitmap().recycle();
//		// mySlidingMenu.findViewById(R.id.userProfileHolder).setVisibility(
//		// View.VISIBLE);
//	}
}
