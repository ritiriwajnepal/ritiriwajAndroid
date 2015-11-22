package co.ritiriwaj.android.rowadapter;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.http.util.ByteArrayBuffer;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v4.view.ViewPager;
import android.text.style.ParagraphStyle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import co.ritiriwaj.android.MainActivity;
import co.ritiriwaj.android.R;
import co.ritiriwaj.android.ReasonsAndCommentsActivity;
import co.ritiriwaj.android.SplashScreen;
import co.ritiriwaj.android.helper.SimplePagerAdapter;
import co.ritiriwaj.android.model.SubProcedure;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.imageaware.ImageAware;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

public class SubProcedureRowAdapter extends ArrayAdapter<SubProcedure> {
	Context context;
	List<SubProcedure> list;

	private static String IMAGE_BASE_URL = MainActivity.IMAGES_IP;

	public SubProcedureRowAdapter(Context context, List<SubProcedure> objects) {
		super(context, R.layout.row_layout, objects);
		this.context = context;
		this.list = objects;

	}

	private class ViewHolder {
		View paragraphView;
		View blockQuoteView;
		ViewPager imagePagerView;
		TextView paragraphTextView;
		TextView blockQuoteTextView;
		ImageView reasonWhyInfoIcon;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder holder = new ViewHolder();

		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		if (convertView == null) {
			convertView = inflater.inflate(R.layout.row_layout, parent, false);

			holder.paragraphView = convertView
					.findViewById(R.id.paragraph_placehoder);
			holder.paragraphTextView = (TextView) holder.paragraphView
					.findViewById(R.id.paragraphText);

			holder.imagePagerView = (ViewPager) convertView
					.findViewById(R.id.imagePager);

			holder.blockQuoteView = convertView
					.findViewById(R.id.blockquote_placeholder);
			holder.blockQuoteTextView = (TextView) holder.blockQuoteView
					.findViewById(R.id.blockQuoteTextView);
			holder.reasonWhyInfoIcon = (ImageView) convertView
					.findViewById(R.id.reasonWhyInfoIcon);
			convertView.setTag(holder);

		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		// paragraph layout
		if (!list.get(position).getParaText().isEmpty()) {
			holder.paragraphView.setVisibility(View.VISIBLE);
			holder.paragraphTextView.setText(list.get(position).getParaText());

			if (!list.get(position).getReasonWhyId().isEmpty()) {
				holder.reasonWhyInfoIcon.setVisibility(View.VISIBLE);
				holder.paragraphView
						.setOnClickListener(new View.OnClickListener() {

							@Override
							public void onClick(View arg0) {
								Intent intent = new Intent(context,
										ReasonsAndCommentsActivity.class);
								intent.putExtra("subProId", list.get(position)
										.getId());
								context.startActivity(intent);

							}
						});
			} else {
				holder.reasonWhyInfoIcon.setVisibility(View.GONE);
				holder.paragraphView.setOnClickListener(null);
			}
		} else {
			holder.paragraphView.setVisibility(View.GONE);
		}

		// imageView layout
		if (!list.get(position).getImageListCSV().isEmpty()) {
			holder.imagePagerView.setVisibility(View.VISIBLE);
			List<String> imageURL = extractImageURL(list.get(position)
					.getImageListCSV());

			final ArrayList<View> imageViews = new ArrayList<View>();

			for (int k = 0; k < imageURL.size(); k++) {
				View imageView1 = inflater.inflate(R.layout.simple_image_view,
						null);

				ImageView image = (ImageView) imageView1
						.findViewById(R.id.simpleImageView);
				final ProgressBar spinner = (ProgressBar) imageView1
						.findViewById(R.id.progressBar);
				ImageLoader.getInstance().displayImage(
						IMAGE_BASE_URL + imageURL.get(k), image,
						SplashScreen.options, new SimpleImageLoadingListener() {
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
								Toast.makeText(getContext(), message,
										Toast.LENGTH_SHORT).show();
								spinner.setVisibility(View.GONE);
							}

							@Override
							public void onLoadingComplete(String imageUri,
									View view, Bitmap loadedImage) {
								spinner.setVisibility(View.GONE);
							}
						});

				imageView1
						.setBackgroundResource(R.drawable.transparent_button_selector);
				image.setScaleType(ScaleType.CENTER_CROP);
				imageViews.add(k, imageView1);
			}

			SimplePagerAdapter pagerAdapter = new SimplePagerAdapter(imageViews);
			holder.imagePagerView.setAdapter(pagerAdapter);
			holder.imagePagerView.setClipToPadding(false);
			holder.imagePagerView.setPageMargin(12);
		} else {
			holder.imagePagerView.setVisibility(View.GONE);
		}

		// block quote layout
		if (!list.get(position).getBlockQuote().isEmpty()) {
			holder.blockQuoteView.setVisibility(View.VISIBLE);
			holder.blockQuoteTextView.setText(list.get(position)
					.getBlockQuote());
		} else {
			holder.blockQuoteView.setVisibility(View.GONE);
		}

		return convertView;
	}

	private List<String> extractImageURL(String csvString) {
		List<String> imageURLlist = new ArrayList<String>();
		String[] imageArray = csvString.split(",");
		for (int i = 0; i < imageArray.length; i++) {
			imageURLlist.add(imageArray[i]);
		}
		Log.i("list", Arrays.toString(imageURLlist.toArray()));
		return imageURLlist;
	}

}
