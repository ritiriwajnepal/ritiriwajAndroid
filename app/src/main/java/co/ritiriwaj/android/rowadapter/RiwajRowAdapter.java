package co.ritiriwaj.android.rowadapter;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import co.ritiriwaj.android.MainActivity;
import co.ritiriwaj.android.R;
import co.ritiriwaj.android.SplashScreen;
import co.ritiriwaj.android.helper.AddEventDailogFragment;
import co.ritiriwaj.android.helper.EventListDialogFragment;
import co.ritiriwaj.android.model.Riwaj;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

public class RiwajRowAdapter extends ArrayAdapter<Riwaj> {

	Context context;
	View view;
	private SharedPreferences sharedPref;

	public RiwajRowAdapter(Context context, int resourceId, List<Riwaj> items) {
		super(context, resourceId, items);
		this.context = context;
	}

	/* private view holder class */
	private class ViewHolder {
		View imageView;
		ImageView image;
		TextView txtTitle;
		TextView txtDesc;
		TextView txtDate;
		TextView txtPersonalize;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = new ViewHolder();
		final Riwaj rowItem = getItem(position);

		LayoutInflater mInflater = (LayoutInflater) context
				.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.row_ritiriwaj, parent,
					false);

			holder.txtDesc = (TextView) convertView.findViewById(R.id.desc);
			holder.txtTitle = (TextView) convertView.findViewById(R.id.title);
			holder.txtDate = (TextView) convertView
					.findViewById(R.id.date_of_riwaj);
			holder.imageView = (View) convertView.findViewById(R.id.simpleImageView);
			holder.image = (ImageView) holder.imageView.findViewById(R.id.icon);
			holder.txtPersonalize = (TextView) convertView
					.findViewById(R.id.personaliseText);
			convertView.setTag(holder);
		} else
			holder = (ViewHolder) convertView.getTag();

		holder.txtTitle.setText(rowItem.getTitle());

		if (rowItem.getDate() != null) {
			// set date & remaining days in Calendar Event List

			holder.txtDate.setText(rowItem.getDate());
			holder.txtDesc.setText(rowItem.getDesc());

		} else { // Display Add to Calendar Button

			holder.txtDate.setVisibility(View.INVISIBLE);
			holder.txtDesc.setVisibility(View.INVISIBLE);

			if (rowItem.getEventList() != null) {

				holder.txtPersonalize.setText("View Events");

			}

			holder.txtPersonalize.setVisibility(View.VISIBLE);
			holder.txtPersonalize
					.setOnClickListener(new View.OnClickListener() {

						@Override
						public void onClick(View v) {

							sharedPref = PreferenceManager
									.getDefaultSharedPreferences(context);

							if (!sharedPref.getString("subscriberId", "")
									.equalsIgnoreCase("")) {

								if (rowItem.getEventList() != null) {
									// Display EventList Dialog
									EventListDialogFragment eventListDialog = new EventListDialogFragment(
											rowItem.getEventList(), String
													.valueOf(rowItem
															.getRiwajId()));
									eventListDialog.show(
											((ActionBarActivity) context)
													.getSupportFragmentManager(),
											"EVENT DIALOG");

								} else {

									// Display Add Event Dialog
									AddEventDailogFragment addEventDailogFragment = new AddEventDailogFragment(
											context, String.valueOf(rowItem
													.getRiwajId()));
									addEventDailogFragment.show(
											((ActionBarActivity) context)
													.getSupportFragmentManager(),
											"add event");

								}

							} else { // The user is offline
//								MainActivity.mySlidingMenu.toggle();
							}

						}
					});

		}

		if (!rowItem.getImageURL().isEmpty()) {
			final ProgressBar spinner = (ProgressBar) holder.imageView
					.findViewById(R.id.progressBar);
			
//			Drawable oldImage = holder.image.getDrawable();
			
			ImageLoader.getInstance().displayImage(
					MainActivity.RITUAL_IMAGE_IP + rowItem.getImageURL(),
					holder.image, SplashScreen.options,new SimpleImageLoadingListener() {
						@Override
						public void onLoadingStarted(String imageUri,
								View view) {
							spinner.setVisibility(View.VISIBLE);
						}

						@Override
						public void onLoadingFailed(String imageUri,
								View view, FailReason failReason) {
							String message = null;
							switch (failReason.getType()) {
							case IO_ERROR:
								message = "Input/Output error";
								break;
							case DECODING_ERROR:
								message = "Image can't be decoded";
								break;
							case NETWORK_DENIED:
								message = "Downloads are denied";
								break;
							case OUT_OF_MEMORY:
								message = "Out Of Memory error";
								break;
							case UNKNOWN:
								message = "Unknown error";
								break;
							}
//							Toast.makeText(getContext(), message,
//									Toast.LENGTH_SHORT).show();
							Log.e("IMAGE LOADING PROBLEM", message);
							spinner.setVisibility(View.GONE);
						}

						@Override
						public void onLoadingComplete(String imageUri,
								View view, Bitmap loadedImage) {
							spinner.setVisibility(View.GONE);
						}
					});
//			if(oldImage!=null){
//				((BitmapDrawable)oldImage).getBitmap().recycle();
//			}
		}
		return convertView;
	}

}