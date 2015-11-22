package co.ritiriwaj.android;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;

import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

public class SplashScreen extends AppCompatActivity {

	// Splash screen timer
	// private static int SPLASH_TIME_OUT = 3000;
	public static DisplayImageOptions options;
	public static ImageLoaderConfiguration config;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
				.setDefaultFontPath("fonts/Roboto-Regular.ttf")
				.setFontAttrId(R.attr.fontPath).build());

		setContentView(R.layout.activity_splash);

		SharedPreferences preferences = PreferenceManager
				.getDefaultSharedPreferences(this);
		// preferences.edit().putBoolean("skip", false).commit();

		if (!preferences.getBoolean("skip", false)) {

			FragmentAppTour appTourFragment = new FragmentAppTour();
			FragmentManager fm = getSupportFragmentManager();
			FragmentTransaction ft = fm.beginTransaction();

			ft.replace(R.id.fragment_placeholder, appTourFragment);
			ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
			// ft.addToBackStack(null);
			ft.commit();

		} else {
			getSupportFragmentManager().beginTransaction()
					// .addToBackStack(null)
					.replace(R.id.fragment_placeholder, new FragmentHome())
					.commit();
		}
		// Create global configuration and initialize ImageLoader with this
		// config
		config = new ImageLoaderConfiguration.Builder(this)
				.threadPriority(Thread.MAX_PRIORITY)
				.denyCacheImageMultipleSizesInMemory().threadPoolSize(3)
				.diskCacheFileNameGenerator(new Md5FileNameGenerator())
				.diskCacheSize(20 * 1024 * 1024).diskCacheFileCount(100)
				.tasksProcessingOrder(QueueProcessingType.LIFO)
				.diskCacheExtraOptions(480, 320, null)
				// .memoryCache(new WeakMemoryCache())
				.writeDebugLogs().build();

		ImageLoader.getInstance().init(config);

		options = new DisplayImageOptions.Builder()
				.showImageForEmptyUri(R.drawable.banner)
				.showImageOnFail(R.drawable.banner)
				.resetViewBeforeLoading(true).cacheOnDisk(true)
				.cacheInMemory(false).imageScaleType(ImageScaleType.EXACTLY)
				.bitmapConfig(Bitmap.Config.RGB_565).considerExifParams(true)
				.displayer(new FadeInBitmapDisplayer(300)).build();

	}

	@Override
	protected void attachBaseContext(Context newBase) {
		super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
	}
}