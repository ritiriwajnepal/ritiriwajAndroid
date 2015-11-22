package co.ritiriwaj.android.helper;

import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import co.ritiriwaj.android.MainActivity;
import co.ritiriwaj.android.R;
import co.ritiriwaj.android.SplashScreen;

import com.nhaarman.listviewanimations.itemmanipulation.expandablelistitem.ExpandableListItemAdapter;
import com.nostra13.universalimageloader.core.ImageLoader;

public class MaterialsExpandableListItemAdapter extends
		ExpandableListItemAdapter<HashMap<String, String>> {
	Context context;
	List<HashMap<String, String>> cardDetailsList;

	public MaterialsExpandableListItemAdapter(Context context,
			List<HashMap<String, String>> cardDetailsList) {
		super(context, R.layout.materials_row_layout, R.id.titleContent,
				R.id.cardContent, cardDetailsList);
		this.context = context;
		this.cardDetailsList = cardDetailsList;
	}

	@Override
	public View getContentView(int position, View convertView,
			ViewGroup parent) {
		if (convertView == null) {
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.materials_row_layout,
					parent, false);
		}
		ImageView cardImage = (ImageView) convertView
				.findViewById(R.id.cardImage);
		 ImageLoader.getInstance().displayImage(
		 MainActivity.MATERIAL_IMAGE_IP
		 + cardDetailsList.get(position).get("materialImageURL"),
		 cardImage, SplashScreen.options);
		 ((TextView) convertView.findViewById(R.id.cardDescription))
		 .setText(cardDetailsList.get(position).get(
		 "materialDesc"));
		convertView.findViewById(R.id.titleContent).setVisibility(View.GONE);
		return convertView;
	}

	@Override
	public View getTitleView(int position, View convertView,
			ViewGroup parent) {
		if (convertView == null) {
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.materials_row_layout,
					parent, false);
		}
		((TextView) convertView.findViewById(R.id.materialNameTxtView))
				.setText(cardDetailsList.get(position).get("materialName"));
		
		Log.i("something", cardDetailsList.get(position).get("materialName")
				+ "\n" + cardDetailsList.get(position).get("materialUnit"));
		
		if(cardDetailsList.get(position).get("materialUnit") == ""){
			((TextView) convertView.findViewById(R.id.materialUnitTxtView)).setVisibility(convertView.INVISIBLE);
		} else {
			((TextView) convertView.findViewById(R.id.materialUnitTxtView))
			.setText(cardDetailsList.get(position).get("materialUnit"));
		}
		
		
		convertView.findViewById(R.id.cardContent).setVisibility(View.GONE);

		return convertView;
	}

}
