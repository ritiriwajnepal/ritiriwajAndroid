package co.ritiriwaj.android;

import java.util.ArrayList;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import co.ritiriwaj.android.helper.ParallaxViewPager;
import co.ritiriwaj.android.helper.SimplePagerAdapterNoSide;

public class FragmentAppTour extends Fragment implements OnClickListener {

	View view;
	ParallaxViewPager parallaxViewPager;
	ImageButton nextButton;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.fragment_app_demo, container, false);
		return view;
	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		parallaxViewPager = (ParallaxViewPager) view
				.findViewById(R.id.appDemoPager);
		parallaxViewPager.setOverlapPercentage(0.99f).setScaleType(
				ParallaxViewPager.FIT_HEIGHT);

		ArrayList<View> views = new ArrayList<View>();

		LayoutInflater inflater = (LayoutInflater) getActivity()
				.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

		for (int i = 0; i <= 2; i++) {

			View view = inflater.inflate(R.layout.appdemo_view1,
					(ViewGroup) getActivity().getWindow().getDecorView()
							.findViewById(android.R.id.content), false);

			TextView demoTitle = (TextView) view.findViewById(R.id.demoTitle);
			TextView demoDescBelowTitle = (TextView) view
					.findViewById(R.id.demoDescBelowTitle);

			if (i == 0) {

				demoTitle.setText(R.string.apptour_what);
				demoDescBelowTitle.setText(R.string.apptour_what_desc);
				// demoDescBelowTitle.setText("/Lltl/jfh dgfpg' k5fl8sf] O{ltxf; / To;sf] dxTj");

			} else if (i == 1) {

				demoTitle.setText(R.string.apptour_why);
				demoDescBelowTitle.setText(R.string.apptour_why_desc);
				// demoDescBelowTitle.setText("x/]s lalw leq n's]sf] wfld{s ljZjf; tyf a}1flgs /x:o");

			} else {

				demoTitle.setText(R.string.apptour_how);
				demoDescBelowTitle.setText(R.string.apptour_how_desc);
				// demoDescBelowTitle.setText("k|fdfl0fs lalw tyf cfjZos ;fdu|Lsf] ;'rL tyf O{ ahf/");

			}

			// Typeface typeface = Typeface.createFromAsset(getActivity()
			// .getAssets(), "fonts/Ananda1Hv.TTF");
			// demoTitle.setTypeface(typeface);
			// demoDescBelowTitle.setTypeface(typeface);

			// view.findViewById(R.id.progressBar).setVisibility(View.GONE);

			// ImageView tempImageView = ((ImageView) view
			// .findViewById(R.id.simpleImageView));
			//
			// tempImageView.setImageDrawable(getResources().getDrawable(
			// R.drawable.ritiriwaj_splash_logo));

			views.add(i, view);
		}

		parallaxViewPager.setAdapter(new SimplePagerAdapterNoSide(views));

		Button skipButton = (Button) view.findViewById(R.id.skipButton);
		nextButton = (ImageButton) view.findViewById(R.id.nextButton);

		skipButton.setOnClickListener(this);
		nextButton.setOnClickListener(this);

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.skipButton:
			SharedPreferences preferences = PreferenceManager
					.getDefaultSharedPreferences(getActivity());
			preferences.edit().putBoolean("skip", true).commit();
			getFragmentManager().beginTransaction()
					.replace(R.id.fragment_placeholder, new FragmentHome())
					.commit();
			break;
		case R.id.nextButton:
			int child = parallaxViewPager.getCurrentItem();
			switch (child) {
			case 0:
				parallaxViewPager.setCurrentItem(1);
				break;
			case 1:
				parallaxViewPager.setCurrentItem(2);
				break;
			case 2:

				getFragmentManager().beginTransaction()
						.replace(R.id.fragment_placeholder, new FragmentHome())
						.commit();
				break;
			default:
				break;
			}
			break;
		default:
			break;
		}

	}

}
