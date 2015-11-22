package co.ritiriwaj.android;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ImageView.ScaleType;
import android.widget.RelativeLayout;
import co.ritiriwaj.android.helper.SimpleConnectionCheck;
import co.ritiriwaj.android.helper.SimplePagerAdapterNoSide;


public class FragmentHome extends Fragment implements OnClickListener {

	View view;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.fragment_home, container, false);
		return view;
	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		Conv.density = getResources().getDisplayMetrics().density;

		Button genCalImageButton = (Button) view
				.findViewById(R.id.generalCalImageButton);
		genCalImageButton.setOnClickListener(this);

		Button personalCalButton = (Button) view
				.findViewById(R.id.personalCalImageButton);
		personalCalButton.setOnClickListener(this);

		Button gamesButton = (Button) view.findViewById(R.id.gamesImageButton);
		gamesButton.setOnClickListener(this);

		Button settingsButton = (Button) view
				.findViewById(R.id.eShopImageButton);
		settingsButton.setOnClickListener(this);

		// Set the pager with an adapter
//		ViewPager pager = (ViewPager) view.findViewById(R.id.splashPager);
//
//		ArrayList<View> views = new ArrayList<View>();
//		LayoutInflater inflater = (LayoutInflater) getActivity()
//				.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
//		for (int i = 0; i <= 4; i++) {
//			View view = inflater.inflate(R.layout.simple_image_view,
//					(ViewGroup) getActivity().getWindow().getDecorView()
//							.findViewById(android.R.id.content), false);
//			view.findViewById(R.id.progressBar).setVisibility(View.GONE);
//			ImageView tempImageView = ((ImageView) view
//					.findViewById(R.id.simpleImageView));
//			Drawable oldImage = tempImageView.getDrawable();
//			if (i == 0) {
//				tempImageView.setImageDrawable(getResources().getDrawable(
//						R.drawable.ritiriwaj_logo));
//
//				if (oldImage != null)
//					((BitmapDrawable) oldImage).getBitmap().recycle();
//
//				RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
//						(int) Conv.dpToPx(180), (int) Conv.dpToPx(180));
//				params.addRule(RelativeLayout.CENTER_IN_PARENT,
//						RelativeLayout.TRUE);
//				tempImageView.setLayoutParams(params);
//			} else {
//
//				switch (i) {
//				case 1:
//					tempImageView.setImageDrawable(getResources().getDrawable(
//							R.drawable.meme1));
//					if (oldImage != null)
//						((BitmapDrawable) oldImage).getBitmap().recycle();
//					break;
//				case 2:
//					tempImageView.setImageDrawable(getResources().getDrawable(
//							R.drawable.meme2));
//					if (oldImage != null)
//						((BitmapDrawable) oldImage).getBitmap().recycle();
//					break;
//				case 3:
//
//					tempImageView.setImageDrawable(getResources().getDrawable(
//							R.drawable.meme3));
//					if (oldImage != null)
//						((BitmapDrawable) oldImage).getBitmap().recycle();
//					break;
//				case 4:
//					tempImageView.setImageDrawable(getResources().getDrawable(
//							R.drawable.meme4));
//					if (oldImage != null)
//						((BitmapDrawable) oldImage).getBitmap().recycle();
//					break;
//				default:
//					break;
//				}
//				tempImageView.setScaleType(ScaleType.FIT_XY);
//			}
//
//			views.add(i, view);
//		}
//
//		pager.setAdapter(new SimplePagerAdapterNoSide(views));
//		// Bind the title indicator to the adapter
//		LinePageIndicator titleIndicator = (LinePageIndicator) view
//				.findViewById(R.id.titles);
//		titleIndicator.setViewPager(pager);
	}

	private void showAlertDialog(final Context context, String message) {
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
	public void onClick(View view) {
		Intent intent = new Intent(getActivity(),
				UnderConstructionActivity.class);
		Intent i = new Intent(getActivity(), MainActivity.class);
		SimpleConnectionCheck connectionCheck = new SimpleConnectionCheck(
				getActivity());
		switch (view.getId()) {

		case R.id.generalCalImageButton:
			
			i.putExtra("fragmentToStart", "RiwajListFragment");
			startActivity(i);
			
//			if (connectionCheck.isNetworkConnected()) {
//				i.putExtra("fragmentToStart", "RiwajListFragment");
//				startActivity(i);
//			} else {
//				showAlertDialog(getActivity(),
//						"RitiRiwaj requires internet connection");
//			}
			break;
		case R.id.personalCalImageButton:
			
			i.putExtra("fragmentToStart", "PersonalRiwajListFragment");
			startActivity(i);
			
//			if (connectionCheck.isNetworkConnected()) {
//				i.putExtra("fragmentToStart", "PersonalRiwajListFragment");
//				startActivity(i);
//			} else {
//				showAlertDialog(getActivity(),
//						"RitiRiwaj requires internet connection");
//			}
			break;
		case R.id.gamesImageButton:
			// Intent intentGames = new Intent();
			intent.putExtra("text", "Games");
			startActivity(intent);
			break;
		case R.id.eShopImageButton:
			intent.putExtra("text", "E-Market");
			startActivity(intent);
			break;
		default:
			break;
		}

	}

	public static class Conv {
		public static float density;

		public static float dpToPx(int dp) {
			return (float) dp * density;
		}
	}
}
